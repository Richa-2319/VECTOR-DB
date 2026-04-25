package com.your_own_ai;

import java.util.*;

public class HNSW {
    static class Node {
        VectorItem item;
        int maxLyr;
        List<List<Integer>> nbrs;

        Node(VectorItem item, int maxLyr, List<List<Integer>> nbrs) {
            this.item = item;
            this.maxLyr = maxLyr;
            this.nbrs = nbrs;
        }
    }

    public static class GraphInfo {
        public int topLayer, nodeCount;
        public List<Integer> nodesPerLayer = new ArrayList<>();
        public List<Integer> edgesPerLayer = new ArrayList<>();

        public static class NV {
            public int id; public String metadata, category; public int maxLyr;
            public NV(int id, String m, String c, int mx) { this.id=id; this.metadata=m; this.category=c; this.maxLyr=mx; }
        }
        public static class EV {
            public int src, dst, lyr;
            public EV(int s, int d, int l) { this.src=s; this.dst=d; this.lyr=l; }
        }

        public List<NV> nodes = new ArrayList<>();
        public List<EV> edges = new ArrayList<>();
    }

    private final Map<Integer, Node> G = new HashMap<>();
    private final int M, M0, ef_build;
    private final float mL;
    private int topLayer = -1;
    private int entryPt = -1;
    private final Random rng = new Random(42);

    public HNSW(int m, int efBuild) {
        this.M = m;
        this.M0 = 2 * m;
        this.ef_build = efBuild;
        this.mL = (float) (1.0f / Math.log(m));
    }

    private int randLevel() {
        return (int) Math.floor(-Math.log(rng.nextDouble()) * mL);
    }

    private List<BruteForce.Pair> searchLayer(float[] q, int ep, int ef, int lyr, Distance.DistFn dist) {
        Set<Integer> vis = new HashSet<>();
        PriorityQueue<BruteForce.Pair> cands = new PriorityQueue<>((a, b) -> Float.compare(a.first, b.first)); // min-heap
        PriorityQueue<BruteForce.Pair> found = new PriorityQueue<>((a, b) -> -Float.compare(a.first, b.first)); // max-heap

        float d0 = dist.calculate(q, G.get(ep).item.emb);
        vis.add(ep);
        cands.offer(new BruteForce.Pair(d0, ep));
        found.offer(new BruteForce.Pair(d0, ep));

        while (!cands.isEmpty()) {
            BruteForce.Pair c = cands.poll();
            if (found.size() >= ef && c.first > found.peek().first) break;
            int cid = c.second;
            if (lyr >= G.get(cid).nbrs.size()) continue;

            for (int nid : G.get(cid).nbrs.get(lyr)) {
                if (vis.contains(nid) || !G.containsKey(nid)) continue;
                vis.add(nid);
                float nd = dist.calculate(q, G.get(nid).item.emb);
                if (found.size() < ef || nd < found.peek().first) {
                    cands.offer(new BruteForce.Pair(nd, nid));
                    found.offer(new BruteForce.Pair(nd, nid));
                    if (found.size() > ef) found.poll();
                }
            }
        }

        List<BruteForce.Pair> res = new ArrayList<>();
        while (!found.isEmpty()) res.add(found.poll());
        Collections.reverse(res);
        return res;
    }

    private List<Integer> selectNbrs(List<BruteForce.Pair> cands, int maxM) {
        List<Integer> r = new ArrayList<>();
        for (int i = 0; i < Math.min(cands.size(), maxM); i++) {
            r.add(cands.get(i).second);
        }
        return r;
    }

    public void insert(VectorItem item, Distance.DistFn dist) {
        int id = item.id;
        int lvl = randLevel();
        List<List<Integer>> nbrs = new ArrayList<>();
        for (int i = 0; i <= lvl; i++) nbrs.add(new ArrayList<>());
        G.put(id, new Node(item, lvl, nbrs));

        if (entryPt == -1) {
            entryPt = id;
            topLayer = lvl;
            return;
        }

        int ep = entryPt;
        for (int lc = topLayer; lc > lvl; lc--) {
            if (lc < G.get(ep).nbrs.size()) {
                List<BruteForce.Pair> W = searchLayer(item.emb, ep, 1, lc, dist);
                if (!W.isEmpty()) ep = W.get(0).second;
            }
        }

        for (int lc = Math.min(topLayer, lvl); lc >= 0; lc--) {
            List<BruteForce.Pair> W = searchLayer(item.emb, ep, ef_build, lc, dist);
            int maxM = (lc == 0) ? M0 : M;
            List<Integer> sel = selectNbrs(W, maxM);
            G.get(id).nbrs.set(lc, sel);

            for (int nid : sel) {
                if (!G.containsKey(nid)) continue;
                List<List<Integer>> nNbrs = G.get(nid).nbrs;
                while (nNbrs.size() <= lc) nNbrs.add(new ArrayList<>());
                List<Integer> conn = nNbrs.get(lc);
                conn.add(id);

                if (conn.size() > maxM) {
                    List<BruteForce.Pair> ds = new ArrayList<>();
                    for (int c : conn) {
                        if (G.containsKey(c)) {
                            ds.add(new BruteForce.Pair(dist.calculate(G.get(nid).item.emb, G.get(c).item.emb), c));
                        }
                    }
                    Collections.sort(ds);
                    conn.clear();
                    for (int i = 0; i < maxM && i < ds.size(); i++) {
                        conn.add(ds.get(i).second);
                    }
                }
            }
            if (!W.isEmpty()) ep = W.get(0).second;
        }

        if (lvl > topLayer) {
            topLayer = lvl;
            entryPt = id;
        }
    }

    public List<BruteForce.Pair> knn(float[] q, int k, int ef, Distance.DistFn dist) {
        if (entryPt == -1) return new ArrayList<>();
        int ep = entryPt;
        for (int lc = topLayer; lc > 0; lc--) {
            if (lc < G.get(ep).nbrs.size()) {
                List<BruteForce.Pair> W = searchLayer(q, ep, 1, lc, dist);
                if (!W.isEmpty()) ep = W.get(0).second;
            }
        }
        List<BruteForce.Pair> W = searchLayer(q, ep, Math.max(ef, k), 0, dist);
        if (W.size() > k) W = W.subList(0, k);
        return W;
    }

    public void remove(int id) {
        if (!G.containsKey(id)) return;
        for (Map.Entry<Integer, Node> e : G.entrySet()) {
            for (List<Integer> layer : e.getValue().nbrs) {
                layer.remove((Integer) id);
            }
        }
        if (entryPt == id) {
            entryPt = -1;
            for (int nid : G.keySet()) {
                if (nid != id) {
                    entryPt = nid;
                    break;
                }
            }
        }
        G.remove(id);
    }

    public GraphInfo getInfo() {
        GraphInfo gi = new GraphInfo();
        gi.topLayer = topLayer;
        gi.nodeCount = G.size();
        int maxL = Math.max(topLayer + 1, 1);
        for (int i = 0; i < maxL; i++) {
            gi.nodesPerLayer.add(0);
            gi.edgesPerLayer.add(0);
        }
        for (Map.Entry<Integer, Node> e : G.entrySet()) {
            int id = e.getKey();
            Node nd = e.getValue();
            gi.nodes.add(new GraphInfo.NV(id, nd.item.metadata, nd.item.category, nd.maxLyr));
            for (int lc = 0; lc <= nd.maxLyr && lc < maxL; lc++) {
                gi.nodesPerLayer.set(lc, gi.nodesPerLayer.get(lc) + 1);
                if (lc < nd.nbrs.size()) {
                    for (int nid : nd.nbrs.get(lc)) {
                        if (id < nid) {
                            gi.edgesPerLayer.set(lc, gi.edgesPerLayer.get(lc) + 1);
                            gi.edges.add(new GraphInfo.EV(id, nid, lc));
                        }
                    }
                }
            }
        }
        return gi;
    }

    public int size() {
        return G.size();
    }
}

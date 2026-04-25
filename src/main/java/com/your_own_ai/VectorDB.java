package com.your_own_ai;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class VectorDB {
    private final Map<Integer, VectorItem> store = new HashMap<>();
    private final BruteForce bf = new BruteForce();
    private final KDTree kdt;
    private final HNSW hnsw;
    private final ReentrantLock mu = new ReentrantLock();
    private int nextId = 1;

    public final int dims;

    public VectorDB(int d) {
        this.dims = d;
        this.kdt = new KDTree(d);
        this.hnsw = new HNSW(16, 200);
    }

    public int insert(String meta, String cat, float[] emb, Distance.DistFn dist) {
        mu.lock();
        try {
            VectorItem v = new VectorItem(nextId++, meta, cat, emb);
            store.put(v.id, v);
            bf.insert(v);
            kdt.insert(v);
            hnsw.insert(v, dist);
            return v.id;
        } finally {
            mu.unlock();
        }
    }

    public boolean remove(int id) {
        mu.lock();
        try {
            if (!store.containsKey(id)) return false;
            store.remove(id);
            bf.remove(id);
            hnsw.remove(id);
            kdt.rebuild(new ArrayList<>(store.values()));
            return true;
        } finally {
            mu.unlock();
        }
    }

    public static class Hit {
        public int id;
        public String meta, cat;
        public float[] emb;
        public float dist;
        public Hit(int i, String m, String c, float[] e, float d) { id=i; meta=m; cat=c; emb=e; dist=d; }
    }

    public static class SearchOut {
        public List<Hit> hits = new ArrayList<>();
        public long us;
        public String algo, metric;
    }

    public SearchOut search(float[] q, int k, String metric, String algo) {
        mu.lock();
        try {
            Distance.DistFn dfn = Distance.getDistFn(metric);
            long t0 = System.nanoTime();

            List<BruteForce.Pair> raw;
            if ("bruteforce".equals(algo)) raw = bf.knn(q, k, dfn);
            else if ("kdtree".equals(algo)) raw = kdt.knn(q, k, dfn);
            else raw = hnsw.knn(q, k, 50, dfn);

            long us = (System.nanoTime() - t0) / 1000;

            SearchOut out = new SearchOut();
            out.us = us;
            out.algo = algo;
            out.metric = metric;
            for (BruteForce.Pair p : raw) {
                if (store.containsKey(p.second)) {
                    VectorItem v = store.get(p.second);
                    out.hits.add(new Hit(p.second, v.metadata, v.category, v.emb, p.first));
                }
            }
            return out;
        } finally {
            mu.unlock();
        }
    }

    public static class BenchOut {
        public long bfUs, kdUs, hnswUs;
        public int n;
        public BenchOut(long b, long k, long h, int n) { bfUs=b; kdUs=k; hnswUs=h; this.n=n; }
    }

    public BenchOut benchmark(float[] q, int k, String metric) {
        mu.lock();
        try {
            Distance.DistFn dfn = Distance.getDistFn(metric);
            
            long t1 = System.nanoTime();
            bf.knn(q, k, dfn);
            long bfUs = (System.nanoTime() - t1) / 1000;

            long t2 = System.nanoTime();
            kdt.knn(q, k, dfn);
            long kdUs = (System.nanoTime() - t2) / 1000;

            long t3 = System.nanoTime();
            hnsw.knn(q, k, 50, dfn);
            long hnswUs = (System.nanoTime() - t3) / 1000;

            return new BenchOut(bfUs, kdUs, hnswUs, store.size());
        } finally {
            mu.unlock();
        }
    }

    public List<VectorItem> all() {
        mu.lock();
        try {
            return new ArrayList<>(store.values());
        } finally {
            mu.unlock();
        }
    }

    public HNSW.GraphInfo hnswInfo() {
        mu.lock();
        try {
            return hnsw.getInfo();
        } finally {
            mu.unlock();
        }
    }

    public int size() {
        mu.lock();
        try {
            return store.size();
        } finally {
            mu.unlock();
        }
    }
}

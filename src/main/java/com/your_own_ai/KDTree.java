package com.your_own_ai;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class KDTree {
    static class KDNode {
        VectorItem item;
        KDNode left = null;
        KDNode right = null;
        KDNode(VectorItem v) { item = v; }
    }

    private KDNode root = null;
    private final int dims;

    public KDTree(int d) { this.dims = d; }

    private KDNode insert(KDNode n, VectorItem v, int d) {
        if (n == null) return new KDNode(v);
        int ax = d % dims;
        if (v.emb[ax] < n.item.emb[ax]) n.left = insert(n.left, v, d + 1);
        else n.right = insert(n.right, v, d + 1);
        return n;
    }

    public void insert(VectorItem v) { root = insert(root, v, 0); }

    private void knn(KDNode n, float[] q, int k, int d, Distance.DistFn dist, PriorityQueue<BruteForce.Pair> heap) {
        if (n == null) return;
        float dn = dist.calculate(q, n.item.emb);
        if (heap.size() < k || dn < heap.peek().first) {
            heap.offer(new BruteForce.Pair(dn, n.item.id));
            if (heap.size() > k) heap.poll();
        }
        int ax = d % dims;
        float diff = q[ax] - n.item.emb[ax];
        KDNode closer = diff < 0 ? n.left : n.right;
        KDNode farther = diff < 0 ? n.right : n.left;
        knn(closer, q, k, d+1, dist, heap);
        if (heap.size() < k || Math.abs(diff) < heap.peek().first) {
            knn(farther, q, k, d+1, dist, heap);
        }
    }

    public List<BruteForce.Pair> knn(float[] q, int k, Distance.DistFn dist) {
        PriorityQueue<BruteForce.Pair> heap = new PriorityQueue<>((a, b) -> -Float.compare(a.first, b.first)); // Max-heap
        knn(root, q, k, 0, dist, heap);
        List<BruteForce.Pair> r = new ArrayList<>();
        while (!heap.isEmpty()) { r.add(0, heap.poll()); }
        return r;
    }

    public void rebuild(List<VectorItem> items) {
        root = null;
        for (VectorItem v : items) insert(v);
    }
}

package com.your_own_ai;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class DocumentDB {
    private final Map<Integer, DocItem> store = new HashMap<>();
    private final HNSW hnsw = new HNSW(16, 200);
    private final BruteForce bf = new BruteForce();
    private final ReentrantLock mu = new ReentrantLock();
    private int nextId = 1;
    private int dims = 0;

    public int insert(String title, String text, float[] emb) {
        mu.lock();
        try {
            if (dims == 0) dims = emb.length;
            DocItem item = new DocItem(nextId++, title, text, emb);
            store.put(item.id, item);
            VectorItem vi = new VectorItem(item.id, title, "doc", emb);
            hnsw.insert(vi, Distance::cosine);
            bf.insert(vi);
            return item.id;
        } finally {
            mu.unlock();
        }
    }

    public static class DocHit {
        public float dist;
        public DocItem item;
        public DocHit(float d, DocItem i) { dist=d; item=i; }
    }

    public List<DocHit> search(float[] q, int k, float maxDist) {
        mu.lock();
        try {
            if (store.isEmpty()) return new ArrayList<>();
            List<BruteForce.Pair> raw;
            if (store.size() < 10) {
                raw = bf.knn(q, k, Distance::cosine);
            } else {
                raw = hnsw.knn(q, k, 50, Distance::cosine);
            }
            List<DocHit> out = new ArrayList<>();
            for (BruteForce.Pair p : raw) {
                if (store.containsKey(p.second) && p.first <= maxDist) {
                    out.add(new DocHit(p.first, store.get(p.second)));
                }
            }
            return out;
        } finally {
            mu.unlock();
        }
    }

    public boolean remove(int id) {
        mu.lock();
        try {
            if (!store.containsKey(id)) return false;
            store.remove(id);
            hnsw.remove(id);
            bf.remove(id);
            return true;
        } finally {
            mu.unlock();
        }
    }

    public List<DocItem> all() {
        mu.lock();
        try {
            return new ArrayList<>(store.values());
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

    public int getDims() {
        return dims;
    }
}

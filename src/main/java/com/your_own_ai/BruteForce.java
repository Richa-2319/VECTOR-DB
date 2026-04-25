package com.your_own_ai;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class BruteForce {
    public List<VectorItem> items = new ArrayList<>();

    public static class Pair implements Comparable<Pair> {
        public float first;
        public int second;

        public Pair(float first, int second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public int compareTo(Pair o) {
            if (this.first != o.first) {
                return Float.compare(this.first, o.first);
            }
            return Integer.compare(this.second, o.second);
        }
    }

    public void insert(VectorItem v) {
        items.add(v);
    }

    public List<Pair> knn(float[] q, int k, Distance.DistFn dist) {
        PriorityQueue<Pair> pq = new PriorityQueue<>((a, b) -> -Float.compare(a.first, b.first)); // Max-heap
        for (VectorItem v : items) {
            float d = dist.calculate(q, v.emb);
            if (pq.size() < k || d < pq.peek().first) {
                pq.offer(new Pair(d, v.id));
                if (pq.size() > k) {
                    pq.poll();
                }
            }
        }
        List<Pair> result = new ArrayList<>();
        while (!pq.isEmpty()) {
            result.add(0, pq.poll()); // Add to front for sorted order
        }
        return result;
    }

    public void remove(int id) {
        items.removeIf(v -> v.id == id);
    }
}

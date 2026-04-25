package com.your_own_ai;

import java.util.Arrays;
import com.google.gson.annotations.SerializedName;

public class VectorItem {
    public int id;
    public String metadata;
    public String category;
    @SerializedName("embedding")
    public float[] emb;

    public VectorItem(int id, String metadata, String category, float[] emb) {
        this.id = id;
        this.metadata = metadata;
        this.category = category;
        this.emb = emb;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        return id == ((VectorItem) obj).id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}

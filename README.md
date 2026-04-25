# VectorDB — Build a Vector Database from Scratch (Java + RAG + Ollama)

A fully working **Vector Database** built from scratch using **Java (backend)** with a web UI.
Implements **HNSW**, **KD-Tree**, and **Brute Force** search algorithms, plus a **RAG pipeline** powered by a local LLM via Ollama.

> Built as an educational project to demonstrate how production vector databases like Pinecone, Weaviate, and Chroma work internally.

---

## 🚀 What This Project Does

| Feature                  | Description                                     |
| ------------------------ | ----------------------------------------------- |
| **3 Search Algorithms**  | HNSW, KD-Tree, Brute Force                      |
| **3 Distance Metrics**   | Cosine, Euclidean, Manhattan                    |
| **16D Demo Vectors**     | 20 semantic vectors (CS, Math, Food, Sports)    |
| **2D PCA Visualization** | Live scatter plot showing clustering            |
| **Real Embeddings**      | Uses Ollama (`nomic-embed-text`) → 768D vectors |
| **RAG Pipeline**         | Retrieve + generate answers using local LLM     |
| **REST API**             | Insert, delete, search, benchmark, ask AI       |

---

## 🧠 How It Works

```
Your Text
    │
    ▼
Ollama (nomic-embed-text)     ← converts text → vector (768D)
    │
    ▼
HNSW Index (Java backend)     ← stores and indexes vectors
    │
    ▼
Semantic Search               ← finds nearest vectors
    │
    ▼
Ollama (llama3.2)             ← generates answer
    │
    ▼
Final Response
```

---

## 📦 Prerequisites

You need:

* JDK (Java Compiler)
* Maven (Build Tool)
* Git
* Ollama (Local LLM runtime)

---

## 🛠️ Step-by-Step Setup (Windows)

---

### 🔹 Step 1 — Install JDK

1. Download JDK from: https://www.oracle.com/java/technologies/downloads/
2. Install it
3. Set environment variables:

```
JAVA_HOME = C:\Program Files\Java\jdk-XX
```

Add to PATH:

```
%JAVA_HOME%\bin
```

Verify:

```
java -version
javac -version
```

---

### 🔹 Step 2 — Install Maven

1. Download from: https://maven.apache.org/download.cgi
2. Extract to `C:\maven`
3. Add to PATH:

```
C:\maven\bin
```

Verify:

```
mvn -version
```

---

### 🔹 Step 3 — Install Git

Download from: https://git-scm.com/download/win

Verify:

```
git --version
```

---

### 🔹 Step 4 — Install Ollama

1. Download from: https://ollama.com
2. Install and run

Pull models:

```
ollama pull nomic-embed-text
ollama pull llama3.2:1b
```

Verify:

```
ollama list
```

---

### 🔹 Step 5 — Clone Repository

```
git clone https://github.com/YOUR_USERNAME/VectorDB.git
cd VectorDB
```

---

### 🔹 Step 6 — Run Java Server

```
mvn clean compile exec:java
```

---

### 🔹 Step 7 — Run Ollama (if needed)

```
ollama serve
```

*(Skip if already running in background)*

---

### 🔹 Step 8 — Open Application

```
http://localhost:8080
```

---

## ✅ Expected Output

```
=== VectorDB Engine (Java) ===
http://localhost:8080
20 demo vectors | 16 dims | HNSW+KD-Tree+BruteForce
Ollama: ONLINE
embed model: nomic-embed-text
gen model: llama3.2:1b
```

---

## 🧪 Using the Application

### 🔍 Tab 1: Search

* Enter query (e.g., `binary tree`, `pizza`)
* Choose algorithm & metric
* Click **Search**

---

### 📄 Tab 2: Documents

* Enter title
* Paste text
* Click **Embed & Insert**

---

### 🤖 Tab 3: Ask AI (RAG)

* Insert documents first
* Ask a question
* Click **Ask AI**

Pipeline:

```
Question → embedding → HNSW search → context → LLM → answer
```

---

## 🌐 REST API

Base URL:

```
http://localhost:8080
```

### Example: Ask AI

```
curl -X POST http://localhost:8080/doc/ask ^
  -H "Content-Type: application/json" ^
  -d "{\"question\":\"What is dynamic programming?\",\"k\":3}"
```

---

## 🏗️ Project Structure

```
VectorDB/
├── src/main/java/com/your_own_ai/
│   ├── Main.java
│   ├── VectorDB.java
│   ├── HNSW.java
│   ├── KDTree.java
│   ├── BruteForce.java
│   ├── DocumentDB.java
│   └── OllamaClient.java
├── index.html
├── pom.xml
└── README.md
```

---

## 🧠 Algorithms

* **HNSW** → Fast approximate search (O(log N))
* **KD-Tree** → Efficient for low dimensions
* **Brute Force** → Exact baseline

---

## ⚠️ Common Issues

| Problem            | Fix                      |
| ------------------ | ------------------------ |
| Ollama unavailable | Ensure Ollama is running |
| Slow response      | Use `llama3.2:1b`        |
| Port 8080 busy     | Kill process             |
| No AI response     | Check Ollama API         |

---

## 🚀 Summary

This project demonstrates:

* Vector Database internals
* Semantic Search
* RAG pipeline
* Local AI system

All running locally without cloud.

---


import java.io.*;
import java.util.*;

public class Graph {

    public int nodes;
    public String[] nodeNames;
    private int[][] adj;          // adjacency matrix; -1 = no edge / blocked
    int[][] baseAdj;      // original weights for restore

    public Graph(int n) {
        this.nodes = n;
        this.nodeNames = new String[n];
        this.adj = new int[n][n];
        this.baseAdj = new int[n][n];
        for (int[] row : adj) Arrays.fill(row, -1);
        for (int[] row : baseAdj) Arrays.fill(row, -1);
    }

    public void addEdge(int u, int v, int w) {
        adj[u][v] = w;
        adj[v][u] = w;
        baseAdj[u][v] = w;
        baseAdj[v][u] = w;
    }

    public boolean blockEdge(int u, int v) {
        if (baseAdj[u][v] == -1) return false;
        adj[u][v] = -1;
        adj[v][u] = -1;
        return true;
    }

    public boolean unblockEdge(int u, int v) {
        if (baseAdj[u][v] == -1) return false;
        adj[u][v] = baseAdj[u][v];
        adj[v][u] = baseAdj[v][u];
        return true;
    }

    public boolean edgeExists(int u, int v) {
        return baseAdj[u][v] != -1;
    }

    public boolean isBlocked(int u, int v) {
        return baseAdj[u][v] != -1 && adj[u][v] == -1;
    }

    public int[] dijkstra(int src) {
        int[] dist = new int[nodes];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[src] = 0;

        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        pq.offer(new int[]{src, 0});

        while (!pq.isEmpty()) {
            int[] cur = pq.poll();
            int u = cur[0], d = cur[1];
            if (d > dist[u]) continue;
            for (int v = 0; v < nodes; v++) {
                if (adj[u][v] != -1 && dist[u] + adj[u][v] < dist[v]) {
                    dist[v] = dist[u] + adj[u][v];
                    pq.offer(new int[]{v, dist[v]});
                }
            }
        }
        return dist;
    }

    public List<Integer> bfsNearby(int src, int maxHops) {
        List<Integer> result = new ArrayList<>();
        boolean[] visited = new boolean[nodes];
        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[]{src, 0});
        visited[src] = true;

        while (!queue.isEmpty()) {
            int[] cur = queue.poll();
            int node = cur[0], hops = cur[1];
            if (node != src) result.add(node);
            if (hops >= maxHops) continue;
            for (int v = 0; v < nodes; v++) {
                if (adj[node][v] != -1 && !visited[v]) {
                    visited[v] = true;
                    queue.offer(new int[]{v, hops + 1});
                }
            }
        }
        return result;
    }

    public Set<String> getPathEdges(int src, int dst) {
        int[] prev = new int[nodes];
        Arrays.fill(prev, -1);

        int[] dist = new int[nodes];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[src] = 0;

        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        pq.offer(new int[]{src, 0});

        while (!pq.isEmpty()) {
            int[] cur = pq.poll();
            int u = cur[0];
            if (cur[1] > dist[u]) continue;
            for (int v = 0; v < nodes; v++) {
                if (adj[u][v] != -1 && dist[u] + adj[u][v] < dist[v]) {
                    dist[v] = dist[u] + adj[u][v];
                    prev[v] = u;
                    pq.offer(new int[]{v, dist[v]});
                }
            }
        }

        Set<String> edges = new HashSet<>();
        int cur = dst;
        while (prev[cur] != -1) {
            int p = prev[cur];
            int mn = Math.min(p, cur), mx = Math.max(p, cur);
            edges.add(mn + "-" + mx);
            cur = p;
        }
        return edges;
    }

    public void printMap() {
        System.out.println("\n=== City Map ===");
        for (int u = 0; u < nodes; u++) {
            for (int v = u + 1; v < nodes; v++) {
                if (baseAdj[u][v] != -1) {
                    String status = (adj[u][v] == -1) ? " [BLOCKED]" : "";
                    System.out.printf("  %s -- %s  (dist: %d)%s%n",
                            nodeNames[u], nodeNames[v], baseAdj[u][v], status);
                }
            }
        }
    }

    public static Graph loadFromFile(String path) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(path));
        String line;
        Graph g = null;

        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\s+");
            if (parts[0].equals("NODES")) {
                g = new Graph(Integer.parseInt(parts[1]));
            } else if (parts[0].equals("NODE_NAME") && g != null) {
                g.nodeNames[Integer.parseInt(parts[1])] = parts[2];
            } else if (parts[0].equals("EDGE") && g != null) {
                g.addEdge(Integer.parseInt(parts[1]),
                        Integer.parseInt(parts[2]),
                        Integer.parseInt(parts[3]));
            }
        }
        br.close();
        return g;
    }
}
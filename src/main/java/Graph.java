import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Graph {
    private Map<String, Set<String>> adjList;

    public Graph() {
        adjList = new HashMap<>();
    }

    public void addEdge(String node1, String node2) {
        Set<String> node1List = adjList.computeIfAbsent(node1, k -> new HashSet<>());
        node1List.add(node2);
        Set<String> node2List = adjList.computeIfAbsent(node2, k -> new HashSet<>());
        node2List.add(node1);
    }

    public int shortestPath(String node1, String node2) {
        Map<String, Integer> dist = new HashMap<>();
        Map<String, Boolean> sptSet = new HashMap<>();

        adjList.forEach((v, e) -> {
            dist.put(v, Integer.MAX_VALUE);
            sptSet.put(v, false);
        });

        dist.put(node1, 0);

        for (int i=0 ; i<adjList.size() ; i++) {
            String u = minDistance(dist, sptSet);
            sptSet.put(u, true);

            for (String v : adjList.keySet()) {
                if (!sptSet.get(v) && adjList.get(u).contains(v) && dist.get(u) != Integer.MAX_VALUE &&
                        dist.get(u) + 1 < dist.get(v)) {
                    dist.put(v, dist.get(u) + 1);
                }
            }
        }

        if (dist.get(node2) == Integer.MAX_VALUE) return -1;
        return dist.get(node2);
    }

    private static String minDistance(Map<String, Integer> dist, Map<String, Boolean> sptSet) {
        int min = Integer.MAX_VALUE;
        String min_index = "";

        for (String v : dist.keySet()) {
            if (!sptSet.get(v) && dist.get(v) <= min) {
                min = dist.get(v);
                min_index = v;
            }
        }

        return min_index;
    }

    public String toString() {
        StringBuilder responseBuilder = new StringBuilder();

        for (String v : adjList.keySet()) {
            responseBuilder.append(v).append(" ---- ");
            adjList.get(v).forEach(u -> responseBuilder.append(u).append(", "));
            responseBuilder.append("\n");
        }

        System.out.println(responseBuilder.toString());
        return responseBuilder.toString();
    }

//    public static void main(String[] args) {
//        Graph g = new Graph();
//        g.addEdge("1", "2");
//        g.printGraph();
//        g.addEdge("2", "3");
//        g.printGraph();
//        g.addEdge("1", "3");
//        g.printGraph();
//        g.addEdge("3", "4");
//        g.printGraph();
//        g.addEdge("5", "6");
//        g.printGraph();
//
//        System.out.println(g.shortestPath("1", "4"));
//        System.out.println(g.shortestPath("1", "6"));
//    }
}

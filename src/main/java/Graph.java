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

        return -1;
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
}

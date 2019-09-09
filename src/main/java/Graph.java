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

        
    }
}

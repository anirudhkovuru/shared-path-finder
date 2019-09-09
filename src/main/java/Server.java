import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm.SingleSourcePaths;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Server implements Executor {
    private static DefaultUndirectedGraph<String, DefaultEdge> graph = new DefaultUndirectedGraph<>(DefaultEdge.class);

    private static final String ADD_EDGE_COMMAND = "add_edge";
    private static final String SHORTEST_DISTANCE_COMMAND = "shortest_distance";
    private static final String GET_GRAPH_COMMAND = "get_graph";

    private Server() {
        super();
    }

    private String addEdgeTask(List<String> nodes) {
        if (nodes.size() != 2) {
            log("ERROR: 2 vertices required");
            return "ERROR: 2 vertices required";
        }

        String node1 = nodes.get(0);
        String node2 = nodes.get(1);

        graph.addVertex(node1);
        graph.addVertex(node2);
        graph.addEdge(node1, node2);

        log(String.format("OK: Edge added between %s and %s", node1, node2));
        return String.format("OK: Edge added between %s and %s", node1, node2);
    }

    private String shortestDistanceTask(List<String> nodes) {
        if (nodes.size() != 2) {
            log("ERROR: 2 vertices required");
            return "ERROR: 2 vertices required";
        }

        String node1 = nodes.get(0);
        String node2 = nodes.get(1);

        DijkstraShortestPath<String, DefaultEdge> dijkstraAlg = new DijkstraShortestPath<>(graph);
        SingleSourcePaths<String, DefaultEdge> node1Paths = dijkstraAlg.getPaths(node1);
        GraphPath<String, DefaultEdge> path = node1Paths.getPath(node2);

        if (path == null) {
            log(String.format("OK: Sent the path length between %s and %s", node1, node2));
            return "-1";
        }

        log(String.format("OK: Sent the path length between %s and %s", node1, node2));
        return String.valueOf(path.getLength());
    }

    private String getGraphTask() {
        StringBuilder responseBuilder = new StringBuilder();

        graph.edgeSet().forEach(e -> {
            responseBuilder.append(graph.getEdgeSource(e)).append(" ");
            responseBuilder.append("----- ");
            responseBuilder.append(graph.getEdgeTarget(e)).append("\n");
        });


        String graphString = responseBuilder.toString();
        log("OK: Sent graph");
        return graphString.substring(0, graphString.length()-1);
    }

    @Override
    public String execute(String line) {
        if (line.trim().isEmpty()) {
            return "";
        }

        Parser.Command command = Parser.parse(line);
        String response;
        switch(command.getName()) {
            case ADD_EDGE_COMMAND:
                response = addEdgeTask(command.getArgs());
                break;
            case SHORTEST_DISTANCE_COMMAND:
                response = shortestDistanceTask(command.getArgs());
                break;
            case GET_GRAPH_COMMAND:
                response = getGraphTask();
                break;
            default:
                response = "ERROR: No such command.";
                break;
        }

        return response;
    }


    public static void main(String[] args) {
        System.setProperty("java.security.policy","file:./server.policy");
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }


        try {
            String name = "server-graph";
            Executor executor = new Server();
            Executor stub = (Executor) UnicastRemoteObject.exportObject(executor, 0);

            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(name, stub);

            System.out.println("server bound with name: " + name);

        } catch (Exception e) {
            System.err.println("Server exception:");
            e.printStackTrace();
        }
    }

    private static void log(String s) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        System.out.println("[" + dtf.format(now) + "] " + s);
    }
}

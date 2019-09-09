import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Server implements Executor {

    private Graph graph = new Graph();

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

        if (graph.doesNotContainVertex(node1) || graph.doesNotContainVertex(node2)) {
            log("ERROR: one of the vertices does not exist");
            return "ERROR: one of the vertices does not exist";
        }

        log(String.format("OK: Sent the path length between %s and %s", node1, node2));
        return String.valueOf(graph.shortestPath(node1, node2));
    }

    private String getGraphTask() {
        String graphString = graph.toString();
        log("OK: Sent graph");
        if (graphString.isEmpty()) return graphString;
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

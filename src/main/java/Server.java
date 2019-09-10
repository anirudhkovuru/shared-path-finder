import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server implements Executor {

    private Map<String, Graph> graphs = new ConcurrentHashMap<>();

    private static final String ADD_EDGE_COMMAND = "add_edge";
    private static final String SHORTEST_DISTANCE_COMMAND = "shortest_distance";
    private static final String GET_GRAPH_COMMAND = "get_graph";
    private static final String ADD_GRAPH_COMMAND = "add_graph";
    private static final String LIST_GRAPH_COMMAND = "list_graphs";

    private Server() {
        super();
    }

    private String addEdgeTask(List<String> nodes) {
        if (nodes.size() != 3) {
            log("ERROR: 3 arguments required");
            return "ERROR: 3 arguments required";
        }

        String graphName = nodes.get(0);
        if (!graphs.containsKey(graphName)) {
            log(String.format("ERROR: %s does not exist", graphName));
            return String.format("ERROR: %s does not exist", graphName);
        }

        String node1 = nodes.get(1);
        String node2 = nodes.get(2);
        graphs.get(graphName).addEdge(node1, node2);

        log(String.format("OK: Edge added between %s and %s to %s", node1, node2, graphName));
        return String.format("OK: Edge added between %s and %s to %s", node1, node2, graphName);
    }

    private String shortestDistanceTask(List<String> nodes) {
        if (nodes.size() != 3) {
            log("ERROR: 3 arguments required");
            return "ERROR: 3 arguments required";
        }

        String graphName = nodes.get(0);
        if (!graphs.containsKey(graphName)) {
            log(String.format("ERROR: %s does not exist", graphName));
            return String.format("ERROR: %s does not exist", graphName);
        }

        String node1 = nodes.get(1);
        String node2 = nodes.get(2);
        Graph graph = graphs.get(graphName);

        if (graph.doesNotContainVertex(node1) || graph.doesNotContainVertex(node2)) {
            log("ERROR: one of the vertices does not exist");
            return "ERROR: one of the vertices does not exist";
        }

        log(String.format("OK: Sent the path length between %s and %s in %s", node1, node2, graphName));
        return String.valueOf(graph.shortestPath(node1, node2));
    }

    private String getGraphTask(List<String> nodes) {
        if (nodes.size() != 1) {
            log("ERROR: 1 argument required");
            return "ERROR: 1 argument required";
        }

        String graphName = nodes.get(0);
        if (!graphs.containsKey(graphName)) {
            log(String.format("ERROR: %s does not exist", graphName));
            return String.format("ERROR: %s does not exist", graphName);
        }

        String graphString = graphs.get(graphName).toString();
        log("OK: Sent graph");
        if (graphString.isEmpty()) return graphString;
        return graphString.substring(0, graphString.length()-1);
    }

    private String addGraphTask(List<String> names) {
        if (names.size() != 1) {
            log("ERROR: name required");
            return "ERROR: name required";
        }

        String name = names.get(0);
        graphs.put(name, new Graph());
        log(String.format("OK: %s graph added", name));
        return String.format("OK: %s graph added", name);
    }

    private String listGraphsTask() {
        StringBuilder responseBuilder = new StringBuilder();

        graphs.keySet().forEach(g -> {
            responseBuilder.append(g);
            responseBuilder.append("\n");
        });

        String graphNames = responseBuilder.toString();

        log("OK: Sent graph names");
        if (graphNames.isEmpty()) return graphNames;
        return graphNames.substring(0, graphNames.length()-1);
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
                response = getGraphTask(command.getArgs());
                break;
            case ADD_GRAPH_COMMAND:
                response = addGraphTask(command.getArgs());
                break;
            case LIST_GRAPH_COMMAND:
                response = listGraphsTask();
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

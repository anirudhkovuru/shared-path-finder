import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Client {
    private static final String DEFAULT_RMI_NAME = "server-graph";

    private static String rmiName;

    public static void main(String[] args) {
        if (args == null || args.length == 0) {
            rmiName = DEFAULT_RMI_NAME;
        } else if (args.length == 1) {
            rmiName = args[0];
        }

        startCommandLine();
        System.exit(0);
    }

    private static void startCommandLine() {
        System.setProperty("java.security.policy","file:./client.policy");
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            Registry registry = LocateRegistry.getRegistry();
            Executor executor = (Executor) registry.lookup(rmiName);

            Scanner input = new Scanner(System.in);
            while (true) {
                System.out.print(System.lineSeparator() + "> ");
                String line = input.nextLine();
                parseResponse(executor.execute(line));
            }
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
    }

    private static void parseResponse(String response) {
        if (!(response == null || response.isEmpty())) {
            if (!response.startsWith("OK")) {
                System.out.println(response);
            }
        }
    }
}

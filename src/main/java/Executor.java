import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Executor extends Remote {
    String execute(String line) throws RemoteException;
}

package server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.Queue;

/**
 * @author lulu
 */
public interface MessageBroker extends Remote {

    void sendMessage(IPlayer iPlayer, String message) throws RemoteException;

    Queue<Map<IPlayer, String>> getMessageQueue() throws RemoteException;


}

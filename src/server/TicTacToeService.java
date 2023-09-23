package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TicTacToeService extends Remote {
    String sendMessage(String clientMessage) throws RemoteException;
}

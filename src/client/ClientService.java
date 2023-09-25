package client;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientService extends Remote {
    void play() throws RemoteException;
    String getUsername() throws RemoteException;
    void startGame(char symbol) throws RemoteException;

}

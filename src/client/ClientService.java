package client;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientService extends Remote {
    public void registerPlayer() throws RemoteException;
    String getUsername() throws RemoteException;
    void startGame(char symbol, boolean isFirst) throws RemoteException;

    void addOnBoard(char symbolToAdd, int row, int col) throws RemoteException;

    void getResult(Result result) throws RemoteException;
    char getSymbol() throws RemoteException;

    void play() throws RemoteException;

}

package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author lulu
 */
public interface IPlayer extends Remote {
    public char getSymbol() throws RemoteException;


    void setSymbol(char symbol) throws RemoteException;

    String getUsername() throws RemoteException;

    int getRank() throws RemoteException;

    void setRank(int rank) throws RemoteException;
}

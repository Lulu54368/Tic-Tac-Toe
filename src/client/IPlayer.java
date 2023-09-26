package client;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IPlayer extends Remote {
    public char getSymbol() throws RemoteException;


    void setSymbol(char symbol) throws RemoteException;

    void setRank(int rank) throws RemoteException;

    String getUsername() throws RemoteException;

}

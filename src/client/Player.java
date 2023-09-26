package client;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Player implements IPlayer, Serializable {
    int rank;
    String username;
    char symbol;

    public Player(String username) throws RemoteException {
        this.username = username;
        UnicastRemoteObject
                .exportObject( this, 0);
    }
    @Override
    public char getSymbol() throws RemoteException {
        return symbol;
    }
    @Override
    public void setSymbol(char symbol) throws RemoteException{
        this.symbol = symbol;
    }

    public int getRank() {
        return rank;
    }
    @Override
    public void setRank(int rank) throws RemoteException{
        this.rank = rank;
    }
    @Override
    public String getUsername() throws RemoteException{
        return username;
    }


}

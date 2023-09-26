package client;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientService extends Remote {
    public void registerPlayer() throws RemoteException;
    void startGame(boolean isFirst) throws RemoteException;

    void addOnBoard(char symbolToAdd, int row, int col) throws RemoteException;

    void getResult(Result result) throws RemoteException;

    void play() throws RemoteException;
    IPlayer getCurrentPlayer() throws RemoteException;


    void setCompetitor(IPlayer competitor) throws RemoteException;

    void setTurn(IPlayer currentPlayer) throws RemoteException;
}

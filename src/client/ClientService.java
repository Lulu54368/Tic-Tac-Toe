package client;

import server.IPlayer;
import server.MessageBroker;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientService extends Remote {
    public void registerPlayer() throws RemoteException;
    void startGame(boolean isFirst) throws RemoteException;

    void addOnBoard(char symbolToAdd, int row, int col) throws RemoteException;

    void getResult(Result result) throws RemoteException;

    void play() throws RemoteException;
    IPlayer getCurrentPlayer() throws RemoteException;


    void setTurn(IPlayer currentPlayer) throws RemoteException;

    void setMessageBroker(MessageBroker messageBroker) throws RemoteException;

    void updateMessage() throws RemoteException;

    void sendTime(int time) throws RemoteException;

    void sendMessage(String message) throws RemoteException;
}

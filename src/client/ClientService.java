package client;

import server.IPlayer;
import server.MessageBroker;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author lulu
 */
public interface ClientService extends Remote {
    void registerPlayer() throws RemoteException;

    void startGame(IPlayer currentPlayer, boolean isFirst) throws RemoteException;

    void play(int row, int col) throws RemoteException;

    void addOnBoard(char symbolToAdd, int row, int col) throws RemoteException;

    void getResult(Result result) throws RemoteException;

    void getResult(Result result, String username) throws RemoteException;

    IPlayer getCurrentPlayer() throws RemoteException;


    void setTurn(IPlayer currentPlayer) throws RemoteException;

    void updateMessage(String message, IPlayer player) throws RemoteException;

    void sendTime(int time) throws RemoteException;

    void sendMessage(String message) throws RemoteException;


    void play() throws RemoteException;

    void quit() throws RemoteException;

    void showHomePage() throws RemoteException;

    void setMessageBroker(MessageBroker messageBroker) throws RemoteException;


    void unRegisterPlayer() throws RemoteException;

    String pong() throws RemoteException;

    void pause() throws RemoteException;

    void resume(char[][] board, String currentPlayer, int time) throws RemoteException;

}

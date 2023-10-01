package client;

import server.IPlayer;
import server.ITicTacToeGame;
import server.MessageBroker;
import server.TicTacToeGame;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientService extends Remote {
    public void registerPlayer() throws RemoteException;
    void startGame(boolean isFirst) throws RemoteException;

    void play(int row, int col) throws RemoteException;

    void addOnBoard(char symbolToAdd, int row, int col) throws RemoteException;

    void getResult(Result result) throws RemoteException;

    void getResult(Result result, String username) throws RemoteException;

    IPlayer getCurrentPlayer() throws RemoteException;


    void setTurn(IPlayer currentPlayer) throws RemoteException;

    void updateMessage() throws RemoteException;

    void sendTime(int time) throws RemoteException;

    void sendMessage(String message) throws RemoteException;

    void setGame(ITicTacToeGame game) throws RemoteException;

    ITicTacToeGame getGame() throws RemoteException;

    void play() throws RemoteException;

    void quit() throws RemoteException;

    void showHomePage() throws RemoteException;
}

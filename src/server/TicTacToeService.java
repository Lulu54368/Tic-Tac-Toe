package server;

import client.ClientService;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TicTacToeService extends Remote {
    void addOnBoard(ClientService clientService, int row, int col) throws RemoteException;

    void switchTurn(ClientService player) throws RemoteException;

    void registerPlayer(ClientService clientService) throws RemoteException;

    String pong() throws RemoteException;

    void endGame(TicTacToeGame game) throws RemoteException;

    void sendMessage(ClientService player, String message, IPlayer currentPlayer) throws RemoteException;

    void lose(ClientService losePlayer) throws RemoteException;

    int[] playInRandomPosition(ClientService currentPlayer) throws RemoteException;
}

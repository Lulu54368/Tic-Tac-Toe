package server;

import client.ClientService;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TicTacToeService extends Remote{
    boolean isValidMove(int row, int col)  throws RemoteException;
    boolean checkWin( int row, int col) throws RemoteException;
    boolean isBoardFull() throws RemoteException;
    void addOnBoard(char currentPlayer, int row, int col) throws RemoteException;

    void registerPlayer(ClientService clientService) throws RemoteException;
}

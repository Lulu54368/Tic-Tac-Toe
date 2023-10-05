package server;

import client.ClientService;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ITicTacToeGame extends Remote {

    void quitGame();

    boolean isValidMove(int row, int col);

    int[] getPosition(ClientService currentPlayer) throws RemoteException;
}

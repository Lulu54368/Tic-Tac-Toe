package server;

import client.ClientService;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.Queue;

public interface ITicTacToeGame extends Remote {

    void quitGame();

    void updateMessage(IPlayer iPlayer, String message) throws RemoteException;

    Queue<Map<IPlayer, String>> getMessage() throws RemoteException;
}

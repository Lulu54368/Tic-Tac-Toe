package server;

import java.rmi.Remote;

public interface ITicTacToeGame extends Remote {

    void quitGame();

    boolean isValidMove(int row, int col);
}

package server;

import client.ClientService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class TicTacToeServiceImpl  extends UnicastRemoteObject implements TicTacToeService{
    private static char[][] board = new char[3][3];
    char currentPlayer = 'X';
    LinkedBlockingQueue<ClientService> waitingPlayers = new LinkedBlockingQueue();
    ExecutorService executorService = Executors.newFixedThreadPool(10);
    List<TicTacToeGame> activeGames = new LinkedList<>();

    static {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = ' ';
            }
        }
    }

    protected TicTacToeServiceImpl() throws RemoteException {
    }

    @Override
    public boolean isValidMove( int row, int col)  throws RemoteException {
        System.out.println("it's "+ board[row][col]);
        if (row < 0 || row >= 3 || col < 0 || col >= 3 || board[row][col] != ' ') {
            return false;
        }

        return true;
    }
    @Override
    public boolean checkWin( int row, int col)  throws RemoteException{
        // Check row
        if (board[row][0] == currentPlayer && board[row][1] == currentPlayer && board[row][2] == currentPlayer) {
            return true;
        }
        // Check column
        if (board[0][col] == currentPlayer && board[1][col] == currentPlayer && board[2][col] == currentPlayer) {
            return true;
        }
        // Check diagonals
        if ((row == col || row + col == 2) &&
                ((board[0][0] == currentPlayer && board[1][1] == currentPlayer && board[2][2] == currentPlayer) ||
                        (board[0][2] == currentPlayer && board[1][1] == currentPlayer && board[2][0] == currentPlayer))) {
            return true;
        }
        return false;
    }
    @Override
    public boolean isBoardFull()  throws RemoteException{
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == ' ') {
                    return false;
                }
            }
        }
        return true;
    }
    @Override
    public void addOnBoard(char currentPlayer, int row, int col)  throws RemoteException{
        board[row][col] = currentPlayer;
        currentPlayer = currentPlayer;
    }

    @Override
    public void registerPlayer(ClientService clientService) throws RemoteException {
        //throw an exception when there are duplicate username
        waitingPlayers.offer(clientService);
        System.out.println("Player registered.");
        tryMatchPlayers();
    }

    private void tryMatchPlayers() {
        executorService.submit(()->{
            while (waitingPlayers.size() >= 2) {
                ClientService player1 = waitingPlayers.poll();
                ClientService player2 = waitingPlayers.poll();
                TicTacToeGame game = new TicTacToeGame(player1, player2);
                activeGames.add(game);
                try {
                    game.start();
                } catch (RemoteException e) {
                    //TODO: handle exception
                    throw new RuntimeException(e);
                }
            }
        });

    }
}

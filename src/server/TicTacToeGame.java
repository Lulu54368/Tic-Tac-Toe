package server;

import client.ClientService;

import java.rmi.RemoteException;

public class TicTacToeGame {
    private static int nextGameId = 1;

    private int gameId;
    private ClientService player1;
    private ClientService player2;
    private char[][] board;
    private int currentPlayerIndex;
    private boolean gameFinished;

    public TicTacToeGame(ClientService player1, ClientService player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.gameId = nextGameId++;
        this.board = new char[3][3];
        this.currentPlayerIndex = 0;
    }

    public int getGameId() {
        return gameId;
    }

    public void start() throws RemoteException {
        System.out.println("play1 "+player1.getUsername()+ " and play2 "+player2.getUsername()+" start");
        try {
            //TODO: choosing symbol randomly
            player1.startGame('X');
            player2.startGame('O');
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public synchronized void makeMove(int row, int col, char symbol) {
        if (!gameFinished && currentPlayerIndex < 2 && symbol == getPlayerSymbol(currentPlayerIndex)) {
            if (isValidMove(row, col)) {
                board[row][col] = symbol;
                notifyPlayers("Move made by Player " + (currentPlayerIndex + 1));
                if (isWin(symbol)) {
                    notifyPlayers("Player " + (currentPlayerIndex + 1) + " wins!");
                    gameFinished = true;
                } else if (isBoardFull()) {
                    notifyPlayers("It's a draw!");
                    gameFinished = true;
                } else {
                    currentPlayerIndex = (currentPlayerIndex + 1) % 2;
                    notifyPlayers("Player " + (currentPlayerIndex + 1) + "'s turn.");
                }
            }
        }
    }

    public synchronized void quitGame(ClientService client) {
        try {
            if (!gameFinished) {
                //client.notify("Player quit the game.");
                gameFinished = true;
            }
            System.out.println("end game");
            /*player1.endGame();
            player2.endGame();*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private char getPlayerSymbol(int index) {
        return (index == 0) ? 'X' : 'O';
    }

    private boolean isValidMove(int row, int col) {
        return row >= 0 && row < 3 && col >= 0 && col < 3 && board[row][col] == 0;
    }

    private boolean isWin(char symbol) {
        // Implement win condition logic here
        // You need to check rows, columns, and diagonals
        return false;
    }

    private boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private void notifyPlayers(String message) {
        System.out.println("notify client "+ message);
        /*try {
            player1.notify(message);
            player2.notify(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }*/
    }

    public boolean isGameFinished() {
        return gameFinished;
    }
}


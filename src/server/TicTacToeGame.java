package server;

import client.ClientService;
import client.Result;

import java.io.Serializable;
import java.rmi.RemoteException;

public class TicTacToeGame implements Serializable {
    private ClientService player1;
    private ClientService player2;
    private char[][] board;
    private boolean gameFinished;




    public TicTacToeGame(ClientService player1, ClientService player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.board = new char[3][3];
    }

    public void start() throws RemoteException {
        System.out.println("play1 "+player1.getCurrentPlayer().getUsername()+
                " and play2 "+player2.getCurrentPlayer().getUsername()+" start");
        //TODO: choosing symbol randomly
        player1.getCurrentPlayer().setSymbol('X');
        player2.getCurrentPlayer().setSymbol('O');
        MessageBroker messageBroker = new MessageBrokerImpl(this);
        player1.setMessageBroker(messageBroker);
        player2.setMessageBroker(messageBroker);
        gameFinished = false;
        new Thread(()-> {
            try {
                new Counter(player1).count();
                player1.startGame(true);
            } catch (RemoteException e) {
                //TODO: handle exception
                throw new RuntimeException(e);
            }
        }).start();
        new Thread(()->{
            try {
                player2.startGame(false);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }).start();



    }

    public Result  makeMove(int row, int col, char symbol) {
        if (!gameFinished ) {
            if (isValidMove(row, col)) {
                board[row][col] = symbol;
                if (isWin(symbol, row, col)) {
                    gameFinished = true;
                    return Result.WIN;
                } else if (isBoardFull()) {
                    gameFinished = true;
                    return Result.DRAW;
                } else {
                    return Result.CONTINUE;
                }
            }
            else{
                return Result.RETRY;
            }
        }
        return Result.END;
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


    private boolean isValidMove(int row, int col) {
        return row >= 0 && row < 3 && col >= 0 && col < 3 && board[row][col] == 0;

    }

    private boolean isWin(char symbol, int row, int col) {
        // Check row
        if (board[row][0] == symbol && board[row][1] == symbol && board[row][2] == symbol) {
            return true;
        }
        // Check column
        if (board[0][col] == symbol && board[1][col] == symbol && board[2][col] == symbol) {
            return true;
        }
        // Check diagonals
        if ((row == col || row + col == 2) &&
                ((board[0][0] == symbol && board[1][1] == symbol && board[2][2] == symbol) ||
                        (board[0][2] == symbol && board[1][1] == symbol && board[2][0] == symbol))) {
            return true;
        }
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

    public boolean isGameFinished() {
        return gameFinished;
    }

    public void updateMessage() throws RemoteException {
        player1.updateMessage();
        player2.updateMessage();
    }
}


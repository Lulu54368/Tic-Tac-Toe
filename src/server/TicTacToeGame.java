package server;

import client.ClientService;
import client.Result;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author lulu
 */
public class TicTacToeGame implements Serializable, ITicTacToeGame {
    List<int[]> list = new LinkedList<>();
    private ClientService player1;
    private ClientService player2;
    private char[][] board;
    private boolean gameFinished;

    public TicTacToeGame(ClientService player1, ClientService player2) throws RemoteException {
        this.player1 = player1;
        this.player2 = player2;
        this.board = new char[3][3];
        player1.setGame(this);
        player2.setGame(this);
        initiateBoard();
    }


    public void start() throws RemoteException {
        List<Character> symbols = Arrays.asList('X', 'O');
        Collections.shuffle(symbols);
        player1.getCurrentPlayer().setSymbol(symbols.get(0));
        player2.getCurrentPlayer().setSymbol(symbols.get(1));
        gameFinished = false;
        MessageBroker messageBroker = new MessageBrokerImpl();
        player1.setMessageBroker(messageBroker);
        player2.setMessageBroker(messageBroker);
        new Thread(() -> {
            try {
                player1.startGame(player1.getCurrentPlayer(), true);
            } catch (RemoteException e) {
                System.err.println("Unable to start the game");
                throw new RuntimeException(e);
            }
        }).start();
        new Thread(() -> {
            try {
                player2.startGame(player1.getCurrentPlayer(), false);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public Result makeMove(int row, int col, char symbol) {
        if (!gameFinished) {
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
            } else {
                return Result.RETRY;
            }
        }
        return Result.END;
    }

    @Override
    public void quitGame() {
        if (!gameFinished) {
            gameFinished = true;
        }

    }

    @Override
    public boolean isValidMove(int row, int col) {
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

    private void initiateBoard() {
        List<Integer> rowList = Arrays.asList(0, 1, 2);
        List<Integer> colList = Arrays.asList(0, 1, 2);
        for (int row : rowList) {
            for (int col : colList) {
                list.add(new int[]{row, col});
            }
        }
        Collections.shuffle(list);
    }

    @Override
    public int[] getPosition(ClientService currentPlayer) throws RemoteException {
        int i = 0;
        int[] component = list.get(i);
        while (!this.isValidMove(component[0], component[1])) {
            i++;
            list.remove(component);
            component = list.get(i);
        }
        return component;
    }

}


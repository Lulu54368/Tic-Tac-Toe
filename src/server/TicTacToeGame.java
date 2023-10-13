package server;

import client.ClientService;
import client.Result;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.*;

/**
 * @author lulu
 */
public class TicTacToeGame implements Serializable, ITicTacToeGame {
    private final String user1;
    private final String user2;
    private final char[][] board;
    private final HashMap<Character, String> symbolUsername = new HashMap<>();
    List<int[]> list = new LinkedList<>();
    private boolean gameFinished;
    private String currentPlayer;
    private int time;

    public TicTacToeGame(String user1, String user2) throws RemoteException {
        this.user1 = user1;
        this.user2 = user2;
        this.board = new char[3][3];
        initiateBoard();
    }


    public void start() throws RemoteException {
        ClientService player1 = PlayerGames.getClientByUsername(user1);
        ClientService player2 = PlayerGames.getClientByUsername(user2);
        List<Character> symbols = Arrays.asList('X', 'O');
        Collections.shuffle(symbols);
        player1.getCurrentPlayer().setSymbol(symbols.get(0));
        player2.getCurrentPlayer().setSymbol(symbols.get(1));
        gameFinished = false;
        MessageBroker messageBroker = new MessageBrokerImpl();
        player1.setMessageBroker(messageBroker);
        player2.setMessageBroker(messageBroker);
        symbolUsername.put(symbols.get(0), player1.getCurrentPlayer().getUsername());
        symbolUsername.put(symbols.get(1), player1.getCurrentPlayer().getUsername());
        new Thread(() -> {
            try {
                currentPlayer = player1.getCurrentPlayer().getUsername();
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
                    currentPlayer = symbolUsername.get(symbol == 'O' ? 'X' : 'O');
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
        return (row == col || row + col == 2) &&
                ((board[0][0] == symbol && board[1][1] == symbol && board[2][2] == symbol) ||
                        (board[0][2] == symbol && board[1][1] == symbol && board[2][0] == symbol));
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

    public void pause() {
        if (!gameFinished) {
            ClientService player1 = PlayerGames.getClientByUsername(user1);
            ClientService player2 = PlayerGames.getClientByUsername(user2);
            try {
                player1.pause();
            } catch (RemoteException e) {
                System.out.println(user1 + " lost connection");
            }
            try {
                player2.pause();
            } catch (RemoteException e) {
                System.out.println(user2 + " lost connection");
            }
        }

    }

    public void resume() {
        if (!gameFinished) {
            ClientService player1 = PlayerGames.getClientByUsername(user1);
            ClientService player2 = PlayerGames.getClientByUsername(user2);
            try {
                player1.resume(board, currentPlayer, time);
                player2.resume(board, currentPlayer, time);
            } catch (RemoteException e) {
                System.err.println("Unable to resume the client");
            }
        }


    }

    public void updateTime(int time) {
        this.time = time;
    }

}


package client;

import server.TicTacToeService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class ClientServiceImpl implements ClientService{
    private static char[][] board = new char[3][3];
    private static char currentPlayer = 'X';
    private TicTacToeService server;
    private String username;

    public ClientServiceImpl(TicTacToeService server, String username) throws RemoteException {
        super();
        this.server = server;
        this.username = username;
        UnicastRemoteObject
                .exportObject( this, 0);
    }
    @Override
    public void play() throws RemoteException {
        /*initializeBoard();
        boolean gameOver = false;

        while (!gameOver) {
            displayBoard();
            int[] move = getPlayerMove();
            int row = move[0];
            int col = move[1];
            System.out.println("row is "+ row+" col is "+ col);
            if (server.isValidMove( row, col)) {
                server.addOnBoard(currentPlayer, row, col);
                board[row][col] = currentPlayer;
                if (server.checkWin( row, col)) {
                    displayBoard();
                    System.out.println("Player " + currentPlayer + " wins!");
                    gameOver = true;
                } else if (server.isBoardFull()) {
                    displayBoard();
                    System.out.println("It's a draw!");
                    gameOver = true;
                } else {
                    currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
                }
            } else {
                System.out.println("Invalid move. Try again.");
            }
        }*/

        System.out.println("start playing..");
        server.registerPlayer(this);
    }
    private static void initializeBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = ' ';
            }
        }
    }
    private static void displayBoard() {
        System.out.println("-------------");
        for (int i = 0; i < 3; i++) {
            System.out.print("| ");
            for (int j = 0; j < 3; j++) {
                System.out.print(board[i][j] + " | ");
            }
            System.out.println("\n-------------");
        }
    }
    private static int[] getPlayerMove() {
        Scanner scanner = new Scanner(System.in);
        int[] move = new int[2];

        System.out.print("Player " + currentPlayer + ", enter your move (row and column): ");
        move[0] = scanner.nextInt();
        move[1] = scanner.nextInt();

        return move;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public void startGame(char symbol) {
        System.out.println(username+ " start with "+ symbol);
    }
}

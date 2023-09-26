package client;

import server.TicTacToeService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class ClientServiceImpl implements ClientService{
    private static char[][] board = new char[3][3];
    private TicTacToeService server;
    private String username;
    private static char symbol;

    public ClientServiceImpl(TicTacToeService server, String username) throws RemoteException {
        super();
        this.server = server;
        this.username = username;
        UnicastRemoteObject
                .exportObject( this, 0);
    }
    @Override
    public void registerPlayer() throws RemoteException {
        System.out.println("register player "+ username);
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

        System.out.print("Player " + symbol + ", enter your move (row and column): ");
        move[0] = scanner.nextInt();
        move[1] = scanner.nextInt();

        return move;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public void startGame(char symbol, boolean isFirst) throws RemoteException {
        this.symbol=symbol;
        System.out.println(username+ " start with "+ symbol);
        initializeBoard();
        if(isFirst) play();

    }
    @Override
    public void play() throws RemoteException {
        displayBoard();
        int[] move = getPlayerMove();
        int row = move[0];
        int col = move[1];
        System.out.println("row is "+ row+" col is "+ col);
        if (server.isValidMove( row, col)) {
            //TODO: create a new thread
            server.addOnBoard(this, row, col);

        }
        else {
            System.out.println("Invalid move. Try again.");
        }
    }

    @Override
    public void addOnBoard(char symbolToAdd, int row, int col) throws RemoteException {
        board[row][col] = symbolToAdd;
    }

    @Override
    public void getResult(Result result) throws RemoteException{
        if(result == Result.DRAW || result == Result.WIN || result == Result.FAIL){
            displayBoard();
            System.out.println("Player " + username + " "+ result.getResult());
        }else if(result == Result.RETRY){
            System.out.println("Player "+ username+" "+ ", please input a valid value!");
            play();
        }

    }
    @Override
    public char getSymbol() throws RemoteException{
        return symbol;
    }
}
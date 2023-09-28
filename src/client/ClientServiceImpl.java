package client;

import server.IPlayer;
import server.MessageBroker;
import server.Player;
import server.TicTacToeService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class ClientServiceImpl implements ClientService{
    private static char[][] board = new char[3][3];
    private TicTacToeService server;
    Player currentPlayer;
    MessageBroker messageBroker;

    public ClientServiceImpl(TicTacToeService server, String username) throws RemoteException {
        super();
        this.server = server;
        this.currentPlayer = new Player(username);
        UnicastRemoteObject
                .exportObject( this, 0);
    }
    @Override
    public void registerPlayer() throws RemoteException {
        System.out.println("register player "+ currentPlayer.getUsername());
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
    private int[] getPlayerMove() throws RemoteException {
        Scanner scanner = new Scanner(System.in);
        int[] move = new int[2];
        System.out.print("Player " + currentPlayer.getUsername() + ", enter your move (row and column): ");
        move[0] = scanner.nextInt();
        move[1] = scanner.nextInt();
        return move;
    }


    @Override
    public void startGame(boolean isFirst) throws RemoteException {
        System.out.println(currentPlayer.getUsername()+ " start with "+ currentPlayer.getSymbol());
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
        //TODO: create a new thread
        server.addOnBoard(this, row, col);


    }

    @Override
    public void addOnBoard(char symbolToAdd, int row, int col) throws RemoteException {
        board[row][col] = symbolToAdd;
    }

    @Override
    public void getResult(Result result) throws RemoteException{
        if(result == Result.DRAW || result == Result.WIN || result == Result.FAIL){
            displayBoard();
            System.out.println("Player " + currentPlayer.getUsername() + " "+ result.getResult());
        }else if(result == Result.RETRY){
            System.out.println("Player "+ currentPlayer.getUsername()+" "+ ", please input a valid value!");
            play();
        }
        else if(result == Result.END){
            System.out.println("The play end!");
        }

    }
    @Override
    public IPlayer getCurrentPlayer() throws RemoteException {
        return currentPlayer;
    }

    @Override
    public void setTurn(IPlayer currentPlayer) throws RemoteException {
        //set competitor's turn
        System.out.println("Its "+ currentPlayer.getUsername()+" turn!");
        System.out.println("ranking "+ currentPlayer.getRank());
    }
    @Override
    public void setMessageBroker(MessageBroker messageBroker) throws RemoteException{
        this.messageBroker = messageBroker;
    }

    @Override
    public void updateMessage() throws RemoteException {
        System.out.println(messageBroker.getMessageQueue());
    }

    @Override
    public void sendTime(int time) throws RemoteException {
        System.out.println("timer is "+ time);
    }
}
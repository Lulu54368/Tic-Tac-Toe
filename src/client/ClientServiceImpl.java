package client;

import server.*;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import static client.ClientGui.getClientGUI;
import static server.PlayerGames.getAnotherPlayer;

public class ClientServiceImpl implements ClientService{
    private static char[][] board = new char[3][3];
    private TicTacToeService server;
    Player currentPlayer;
    ITicTacToeGame game;
    Counter counter;
    class HeartBeat extends Thread{
        @Override
        public void run(){
            try{
                while(true){
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                System.exit(0);
            }
        }
    }

    public ClientServiceImpl(TicTacToeService server, String username) throws RemoteException {
        super();
        this.server = server;
        this.currentPlayer = new Player(username);
        new HeartBeat().start();
        UnicastRemoteObject
                .exportObject( this, 0);

    }
    @Override
    public void registerPlayer() throws RemoteException {
        System.out.println("register player "+ currentPlayer.getUsername());
        server.registerPlayer(this);
    }

    @Override
    public void startGame(boolean isFirst) throws RemoteException {
        System.out.println(currentPlayer.getUsername()+ " start with "+ currentPlayer.getSymbol());
        if(isFirst){
            play();
        }

    }
    @Override
    public void play(int row, int col) throws RemoteException {
        server.addOnBoard(this, row, col);
    }

    @Override
    public void addOnBoard(char symbolToAdd, int row, int col) throws RemoteException {
        int index = row *3+col;
        getClientGUI(this).addOnBoard(symbolToAdd, index);
    }

    @Override
    public void getResult(Result result) throws RemoteException{
        if(result == Result.DRAW){
            getClientGUI(this).showResult("It is a draw!");
        }else if(result == Result.RETRY){
            System.out.println("Player "+ currentPlayer.getUsername()+" "+ ", please input a valid value!");
            play();
        }
        else if(result == Result.END){
            System.out.println("The play end!");
        }

    }
    @Override
    public void getResult(Result result, String username) throws RemoteException{
        if(result == Result.WIN){
            getClientGUI(this).showResult(username);
        }

    }
    @Override
    public IPlayer getCurrentPlayer() throws RemoteException {
        return currentPlayer;
    }

    @Override
    public void setTurn(IPlayer currentPlayer) throws RemoteException {
        //set competitor's turn
        counter.cancel();
        getClientGUI(this).erase();
        getClientGUI(this).disableButton();
        System.out.println("Its "+ currentPlayer.getUsername()+" turn!");
        System.out.println("ranking "+ currentPlayer.getRank());
    }


    @Override
    public synchronized void updateMessage() throws RemoteException {
        System.out.println(game);
        getClientGUI(this).showMessage(game.getMessage());
    }

    @Override
    public void sendTime(int time) throws RemoteException {
        getClientGUI(this).showTime(time);
    }
    @Override
    public void sendMessage(String message) throws RemoteException {
        //TODO: should refer to the same game object
        ClientService anotherPlayer = getAnotherPlayer((TicTacToeGame) game, this);
        anotherPlayer.getGame().updateMessage(currentPlayer, message);
        game.updateMessage(currentPlayer, message);
    }

    @Override
    public void setGame(ITicTacToeGame game) throws RemoteException{
        this.game = game;
    }
    @Override
    public ITicTacToeGame getGame() throws RemoteException{
        return game;
    }

    @Override
    public void play() throws RemoteException {
        counter = new Counter(this, server);
        counter.count();
        getClientGUI(this).play();
    }
    @Override
    public void quit() throws RemoteException {
        server.endGame((TicTacToeGame) game);

    }

    @Override
    public void showHomePage() throws RemoteException {
        if(counter != null) counter.cancel();
        getClientGUI(this).showHomePage();
    }
}
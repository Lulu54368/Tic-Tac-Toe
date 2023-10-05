package client;

import server.*;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import static client.ClientGui.getClientGUI;
import static client.StartGUI.getStartGUI;

public class ClientServiceImpl implements ClientService {
    private static char[][] board = new char[3][3];
    Player currentPlayer;
    ITicTacToeGame game;
    Counter counter;
    MessageBroker messageBroker;
    private TicTacToeService server;

    private boolean isFinished = true;

    public ClientServiceImpl(TicTacToeService server, String username) throws RemoteException {
        super();
        this.server = server;
        this.currentPlayer = new Player(username);
        UnicastRemoteObject
                .exportObject(this, 0);
        getStartGUI(this);
        isFinished = true;

    }

    @Override
    public void registerPlayer() throws RemoteException {
        server.registerPlayer(this);
    }

    @Override
    public void startGame(IPlayer currentPlayer, boolean isFirst) throws RemoteException {
        getStartGUI(this).startGame();
        isFinished = false;
        if (isFirst) {
            play();
        } else {
            setTurn(currentPlayer);
        }
    }

    @Override
    public void play(int row, int col) throws RemoteException {
        server.addOnBoard(this, row, col);
    }

    @Override
    public void addOnBoard(char symbolToAdd, int row, int col) throws RemoteException {
        int index = row * 3 + col;
        getClientGUI(this).addOnBoard(symbolToAdd, index);
    }

    @Override
    public void getResult(Result result) throws RemoteException {
        if (result == Result.DRAW) {
            isFinished = true;
            getClientGUI(this).showResult("It is a draw!");
        } else if (result == Result.RETRY) {
            getClientGUI(this).play();
        } else if (result == Result.END) {
            System.out.println("The play end!");
        }

    }

    @Override
    public void getResult(Result result, String username) throws RemoteException {
        if (result == Result.WIN) {
            isFinished = true;
            getClientGUI(this).showResult(username + " win!");
        }


    }

    @Override
    public IPlayer getCurrentPlayer() throws RemoteException {
        return currentPlayer;
    }

    @Override
    public void setTurn(IPlayer currentPlayer) throws RemoteException {
        //set competitor's turn
        if (counter != null) counter.cancel();
        getClientGUI(this).erase();
        getClientGUI(this).disableButton();
        getClientGUI(this).showBanner(currentPlayer);
    }

    @Override
    public void updateMessage(String message, IPlayer player) throws RemoteException {
        messageBroker.sendMessage(player, message);
        getClientGUI(this).showMessage(messageBroker.getMessageQueue());

    }

    @Override
    public void sendTime(int time) throws RemoteException {
        getClientGUI(this).showTime(time);
    }

    @Override
    public void sendMessage(String message) throws RemoteException {
        //TODO: should refer to the same game object
        server.sendMessage(this, message, currentPlayer);
    }

    @Override
    public ITicTacToeGame getGame() throws RemoteException {
        return game;
    }

    @Override
    public void setGame(ITicTacToeGame game) throws RemoteException {
        this.game = game;
    }


    @Override
    public void play() throws RemoteException {
        counter = new Counter(this, server);
        counter.count();
        getClientGUI(this).play();
        getClientGUI(this).showBanner(currentPlayer);
    }

    @Override
    public void quit() throws RemoteException {
        server.lose(this);
        isFinished = true;
    }

    @Override
    public void showHomePage() throws RemoteException {
        if (counter != null) counter.cancel();
        getClientGUI(this).clear();
        getStartGUI(this).showHomePage();
    }
    @Override
    public void setMessageBroker(MessageBroker messageBroker) throws RemoteException {
        this.messageBroker = messageBroker;
    }

    @Override
    public boolean isFinished() {
        return isFinished;
    }
}
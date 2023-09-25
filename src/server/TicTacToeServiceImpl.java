package server;

import client.ClientService;
import client.Result;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class TicTacToeServiceImpl  extends UnicastRemoteObject implements TicTacToeService{
    private static char[][] board = new char[3][3];
    LinkedBlockingQueue<ClientService> waitingPlayers = new LinkedBlockingQueue();
    ExecutorService executorService = Executors.newFixedThreadPool(10);
    List<TicTacToeGame> activeGames = new LinkedList<>();
    HashMap<ClientService, TicTacToeGame> playerGame = new HashMap<>();

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
    public void addOnBoard(ClientService clientService, int row, int col)  throws RemoteException{
        TicTacToeGame game = playerGame.get(clientService);
        ClientService competitor = getAnotherPlayer(game, clientService);
        Result result = game.makeMove(row, col, clientService.getSymbol());
        if(result == Result.WIN){
            clientService.getResult(Result.WIN);
            competitor.getResult(Result.FAIL);
        }
        else if(result == Result.DRAW ){
            clientService.getResult(Result.DRAW);
            competitor.getResult(Result.DRAW);
        }
        else if(result == Result.FAIL){
            clientService.getResult(Result.FAIL);
            clientService.getResult(Result.WIN);
        }
        else if(result == Result.CONTINUE){
            competitor.addOnBoard(clientService.getSymbol(), row, col);
            competitor.play();
        }else if(result == Result.RETRY){
            clientService.getResult(Result.RETRY);
        }
    }
    private ClientService getAnotherPlayer(TicTacToeGame ticTacToeGame, ClientService player){
        return playerGame.entrySet()
                .stream()
                .filter(e->e.getValue().equals(ticTacToeGame))
                .map(e->e.getKey())
                .filter(p-> !p.equals(player))
                .findFirst()
                .get();

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
                playerGame.put(player1, game);
                playerGame.put(player2, game);
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

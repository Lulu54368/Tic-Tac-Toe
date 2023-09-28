package server;

import client.ClientService;
import client.Result;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TicTacToeServiceImpl  extends UnicastRemoteObject implements TicTacToeService{
    private static char[][] board = new char[3][3];
    private static Queue<ClientService> waitingPlayers = new LinkedList<>();
    private static ExecutorService executorService = Executors.newFixedThreadPool(10);
    private static List<TicTacToeGame> activeGames = new LinkedList<>();
    private static HashMap<ClientService, TicTacToeGame> playerGame = new HashMap<>();

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
    public void addOnBoard(ClientService clientService, int row, int col)  throws RemoteException{
        TicTacToeGame game = playerGame.get(clientService);
        ClientService competitor = getAnotherPlayer(game, clientService);
        Result result = game.makeMove(row, col, clientService.getCurrentPlayer().getSymbol());
        if(result != Result.RETRY && result != Result.END){
            clientService.addOnBoard(clientService.getCurrentPlayer().getSymbol(), row, col);
            competitor.addOnBoard(clientService.getCurrentPlayer().getSymbol(), row, col);
        }
        if(result == Result.WIN){
            Score.win(clientService.getCurrentPlayer().getUsername());
            clientService.getResult(Result.WIN);
            Score.lose(competitor.getCurrentPlayer().getUsername());
            competitor.getResult(Result.FAIL);
        }
        else if(result == Result.DRAW ){
            Score.draw(clientService.getCurrentPlayer().getUsername());
            clientService.getResult(Result.DRAW);
            Score.draw(competitor.getCurrentPlayer().getUsername());
            competitor.getResult(Result.DRAW);
        }
        else if(result == Result.FAIL){
            Score.lose(clientService.getCurrentPlayer().getUsername());
            clientService.getResult(Result.FAIL);
            Score.win(competitor.getCurrentPlayer().getUsername());
            clientService.getResult(Result.WIN);
        }
        else if(result == Result.CONTINUE){
            clientService.setTurn(competitor.getCurrentPlayer());
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

    private static void removeClientServiceByGame(TicTacToeGame game){
        playerGame.entrySet()
                .stream()
                .filter(e->e.getValue().equals(game))
                .map(e->playerGame.remove(e.getKey(), game));
    }

    @Override
    public void registerPlayer(ClientService clientService) throws RemoteException {
        //throw an exception when there are duplicate username
        Score.createNew(clientService.getCurrentPlayer().getUsername());
        int rank = Score.getRank(clientService.getCurrentPlayer().getUsername());
        clientService.getCurrentPlayer().setRank(rank);
        waitingPlayers.offer(clientService);
        System.out.println("Player "+clientService.getCurrentPlayer().getUsername()+" registered.");
        tryMatchPlayers();
    }

    private void tryMatchPlayers() {
        executorService.submit(()->{
            //TODO: assign player randomly
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                //TODO: handle exception
                throw new RuntimeException(e);
            }
            Collections.shuffle((LinkedList)waitingPlayers);
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

    public static void endGame(TicTacToeGame game){
        activeGames.remove(game);
        removeClientServiceByGame(game);
    }

}

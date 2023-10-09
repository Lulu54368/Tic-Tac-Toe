package server;

import client.ClientService;
import client.Result;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static server.PlayerGames.*;

public class TicTacToeServiceImpl extends UnicastRemoteObject implements TicTacToeService {
    private Queue<ClientService> waitingPlayers = new LinkedList<>();
    private ExecutorService executorService = Executors.newFixedThreadPool(10);
    private List<TicTacToeGame> activeGames = new LinkedList<>();


    public TicTacToeServiceImpl() throws RemoteException {
        super();
    }


    @Override
    public void addOnBoard(ClientService clientService, int row, int col) throws RemoteException {
        TicTacToeGame game = getGameByPlayer(clientService);
        ClientService competitor = getAnotherPlayer(game, clientService);
        Result result = game.makeMove(row, col, clientService.getCurrentPlayer().getSymbol());
        if (result != Result.RETRY && result != Result.END) {
            clientService.addOnBoard(clientService.getCurrentPlayer().getSymbol(), row, col);
            competitor.addOnBoard(clientService.getCurrentPlayer().getSymbol(), row, col);
        }
        if (result == Result.WIN) {
            lose(competitor);
        } else if (result == Result.DRAW) {
            Score.draw(clientService.getCurrentPlayer().getUsername());
            Score.draw(competitor.getCurrentPlayer().getUsername());
            new Thread(() -> {
                try {
                    clientService.getResult(Result.DRAW);
                    clientService.showHomePage();
                } catch (RemoteException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }).start();
            Score.draw(competitor.getCurrentPlayer().getUsername());
            new Thread(() -> {
                try {
                    competitor.getResult(Result.DRAW);
                    competitor.showHomePage();
                } catch (RemoteException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }).start();
            endGame(game);
        } else if (result == Result.CONTINUE) {
            switchTurn(clientService);
        } else if (result == Result.RETRY) {
            clientService.getResult(Result.RETRY);
        }
    }

    @Override
    public void switchTurn(ClientService player) throws RemoteException {
        TicTacToeGame ticTacToeGame = getGameByPlayer(player);
        ClientService competitor = getAnotherPlayer(ticTacToeGame, player);
        player.setTurn(competitor.getCurrentPlayer());
        competitor.play();
    }

    @Override
    public void registerPlayer(ClientService clientService) throws RemoteException {
        //throw an exception when there are duplicate username
        Score.createNew(clientService.getCurrentPlayer().getUsername());
        int rank = Score.getRank(clientService.getCurrentPlayer().getUsername());
        clientService.getCurrentPlayer().setRank(rank);
        waitingPlayers.offer(clientService);
        System.out.println("Player " + clientService.getCurrentPlayer().getUsername() + " registered.");
        tryMatchPlayers();
    }

    private void tryMatchPlayers() {
        executorService.submit(() -> {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            Collections.shuffle((LinkedList) waitingPlayers);
            while (waitingPlayers.size() >= 2) {
                ClientService player1 = waitingPlayers.poll();
                ClientService player2 = waitingPlayers.poll();
                TicTacToeGame game = null;

                try {
                    game = new TicTacToeGame(player1, player2);
                    putClientGameEntry(game, player1);
                    putClientGameEntry(game, player2);
                } catch (RemoteException e) {
                    //TODO: handle exception
                    throw new RuntimeException(e);
                }
                activeGames.add(game);
                try {
                    game.start();
                } catch (RemoteException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        });

    }

    @Override
    public String pong() throws RemoteException {
        return "OK";
    }

    @Override
    public void endGame(TicTacToeGame game) throws RemoteException {
        activeGames.remove(game);
        removeClientServiceByGame(game);
        game.quitGame();
    }

    @Override
    public void sendMessage(ClientService player, String message, IPlayer currentPlayer) throws RemoteException {
        player.updateMessage(message, currentPlayer);
        getAnotherPlayer(getGameByPlayer(player), player).updateMessage(message, currentPlayer);
    }

    @Override
    public void lose(ClientService losePlayer) throws RemoteException {
        TicTacToeGame game = PlayerGames.getGameByPlayer(losePlayer);
        ClientService winnerPlayer = getAnotherPlayer(game, losePlayer);
        Score.win(winnerPlayer.getCurrentPlayer().getUsername());
        Score.lose(losePlayer.getCurrentPlayer().getUsername());
        Thread winThread = new Thread(() -> {
            try {
                winnerPlayer.getResult(Result.WIN, winnerPlayer.getCurrentPlayer().getUsername());
                winnerPlayer.showHomePage();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
        Thread loseThread = new Thread(() -> {
            try {
                losePlayer.getResult(Result.WIN, winnerPlayer.getCurrentPlayer().getUsername());
                losePlayer.showHomePage();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
        winThread.start();
        loseThread.start();
        endGame(game);
    }


    @Override
    public int[] playInRandomPosition(ClientService currentPlayer) throws RemoteException {
        TicTacToeGame game = getGameByPlayer(currentPlayer);
        int[] component = game.getPosition(currentPlayer);
        currentPlayer.play(component[0], component[1]);
        return component;
    }

    @Override
    public void unRegisterPlayer(ClientService clientService) throws RemoteException {
        //should remove the player from the waiting list
        TicTacToeGame game = getGameByPlayer(clientService);
        if (game != null) {
            ClientService anotherPlayer = getAnotherPlayer(game, clientService);
            anotherPlayer.showHomePage();
            endGame(game);
        } else
            waitingPlayers.remove(clientService);
    }


}

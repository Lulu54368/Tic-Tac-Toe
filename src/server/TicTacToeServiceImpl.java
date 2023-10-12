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
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static client.StartGUI.getStartGUI;
import static server.PlayerGames.*;

/**
 * @author lulu
 */
public class TicTacToeServiceImpl extends UnicastRemoteObject implements TicTacToeService {
    private final Queue<ClientService> waitingPlayers = new LinkedList<>();
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    private final List<TicTacToeGame> activeGames = new LinkedList<>();


    public TicTacToeServiceImpl() throws RemoteException {
        super();
        checkStatus();
    }


    @Override
    public void addOnBoard(ClientService clientService, int row, int col) throws RemoteException {
        TicTacToeGame game = getGameByPlayer(clientService.getCurrentPlayer().getUsername());
        ClientService competitor = getAnotherPlayer(game, clientService);
        Result result = game.makeMove(row, col, clientService.getCurrentPlayer().getSymbol());
        if (result != Result.RETRY && result != Result.END) {
            clientService.addOnBoard(clientService.getCurrentPlayer().getSymbol(), row, col);
            competitor.addOnBoard(clientService.getCurrentPlayer().getSymbol(), row, col);
        }
        if (result == Result.WIN) {
            lose(competitor);
        } else if (result == Result.DRAW) {
            draw(game);
        } else if (result == Result.CONTINUE) {
            switchTurn(clientService);
        } else if (result == Result.RETRY) {
            clientService.getResult(Result.RETRY);
        }
    }

    @Override
    public void switchTurn(ClientService player) throws RemoteException {
        TicTacToeGame ticTacToeGame = getGameByPlayer(player.getCurrentPlayer().getUsername());
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
        if (!PlayerGames.isActivePlayer(clientService)) {
            waitingPlayers.offer(clientService);
            System.out.println("Player " + clientService.getCurrentPlayer().getUsername() + " registered.");
            tryMatchPlayers();
        } else {
            getStartGUI(clientService).setVisible(false);
            clientService.play();
        }

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
                    game = new TicTacToeGame(player1.getCurrentPlayer().getUsername(),
                            player2.getCurrentPlayer().getUsername());
                    putClientGameEntry(game, player1);
                    putClientGameEntry(game, player2);
                } catch (RemoteException e) {
                    System.err.println("Unable to create a new game");
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
        getAnotherPlayer(getGameByPlayer(player.getCurrentPlayer().getUsername()), player).updateMessage(message, currentPlayer);
    }

    @Override
    public void lose(ClientService losePlayer) throws RemoteException {
        TicTacToeGame game = PlayerGames.getGameByPlayer(losePlayer.getCurrentPlayer().getUsername());
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

    public void draw(TicTacToeGame game) {
        getPlayersByGame(game)
                .stream()
                .map(user -> {
                    Score.draw(user);
                    return getClientByUsername(user);
                })
                .forEach(player -> new Thread(() -> {
                    try {
                        player.getResult(Result.DRAW);
                        player.showHomePage();
                    } catch (RemoteException e) {
                    }
                }).start());

        try {
            endGame(game);
        } catch (RemoteException e) {
            System.out.println("issues occurred ending game");
        }
    }


    @Override
    public int[] playInRandomPosition(ClientService currentPlayer) throws RemoteException {
        TicTacToeGame game = getGameByPlayer(currentPlayer.getCurrentPlayer().getUsername());
        int[] component = game.getPosition(currentPlayer);
        currentPlayer.play(component[0], component[1]);
        return component;
    }

    @Override
    public void unRegisterPlayer(ClientService clientService) throws RemoteException {
        //should remove the player from the waiting list
        TicTacToeGame game = getGameByPlayer(clientService.getCurrentPlayer().getUsername());
        if (game != null) {
            ClientService anotherPlayer = getAnotherPlayer(game, clientService);
            anotherPlayer.showHomePage();
            endGame(game);
        } else
            waitingPlayers.remove(clientService);
    }

    private void checkStatus() {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        Runnable checkServerStatus = () -> {
            PlayerGames.getActivePlayer()
                    .entrySet()
                    .parallelStream()
                    .forEach(e -> {
                        try {
                            e.getValue().pong();
                        } catch (RemoteException ex) {
                            TicTacToeGame game;
                            //pause the game for 30 seconds
                            try {
                                game = PlayerGames.getGameByPlayer(e.getKey());
                                game.pause();
                            } catch (RemoteException exc) {
                                exc.printStackTrace();
                                throw new RuntimeException(exc);
                            }
                            for (int i = 0; i < 30; i++) {
                                ClientService newClientService = PlayerGames.getClientByUsername(e.getKey());
                                try {
                                    Thread.sleep(1000);
                                    newClientService.pong();
                                    e.setValue(newClientService);
                                    game.resume();
                                    return;
                                } catch (InterruptedException exception) {
                                    throw new RuntimeException(exception);
                                } catch (RemoteException exception) {
                                    continue;
                                }
                            }
                            draw(game);
                        }
                    });
        };
        executorService.scheduleAtFixedRate(checkServerStatus, 0, 1000, TimeUnit.MILLISECONDS);

    }


}

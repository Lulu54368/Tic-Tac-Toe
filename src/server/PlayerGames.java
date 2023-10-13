package server;

import client.ClientService;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * @author lulu
 */
public class PlayerGames {
    private static final HashMap<TicTacToeGame, List<String>> playerGame = new HashMap<>();
    private static final HashMap<String, ClientService> activePlayer = new HashMap<>();

    public static TicTacToeGame getGameByPlayer(String username) throws RemoteException {
        if (playerGame.isEmpty() || playerGame == null)
            return null;
        return playerGame.entrySet()
                .stream()
                .filter(e -> e.getValue().contains(username))
                .findFirst()
                .get()
                .getKey();
    }

    public static ClientService getAnotherPlayer(TicTacToeGame ticTacToeGame, ClientService player) throws RemoteException {
        for (String username : playerGame.get(ticTacToeGame)) {
            if (!username.equals(player.getCurrentPlayer().getUsername())) {
                return activePlayer.get(username);
            }
        }
        return null;
    }

    public static void removeClientServiceByGame(TicTacToeGame game) {
        List<String> players = playerGame.get(game);
        players.stream().forEach(player -> activePlayer.remove(player));
        playerGame.remove(game);
    }

    public static void putClientGameEntry(TicTacToeGame ticTacToeGame, ClientService clientService) throws RemoteException {
        if (!playerGame.containsKey(ticTacToeGame)) {
            playerGame.put(ticTacToeGame, new LinkedList<>());
        }
        List<String> players = playerGame.get(ticTacToeGame);
        if (!players.contains(clientService.getCurrentPlayer().getUsername())) {
            players.add(clientService.getCurrentPlayer().getUsername());
            playerGame.put(ticTacToeGame, players);
        }
        activePlayer.put(clientService.getCurrentPlayer().getUsername(), clientService);
    }

    public static boolean isActivePlayer(ClientService clientService) throws RemoteException {
        String username = clientService.getCurrentPlayer().getUsername();
        if (activePlayer.containsKey(username)) {
            activePlayer.remove(username);
            activePlayer.put(username, clientService);
            return true;
        }
        return false;
    }

    public static HashMap<String, ClientService> getActivePlayer() {
        return activePlayer;
    }


    public static ClientService getClientByUsername(String username) {
        return activePlayer.get(username);
    }

    public static List<String> getPlayersByGame(TicTacToeGame game) {
        return playerGame.get(game);
    }
}

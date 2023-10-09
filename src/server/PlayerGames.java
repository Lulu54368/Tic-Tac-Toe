package server;

import client.ClientService;

import java.rmi.RemoteException;
import java.util.HashMap;

public class PlayerGames {
    private static HashMap<ClientService, TicTacToeGame> playerGame = new HashMap<>();

    public static TicTacToeGame getGameByPlayer(ClientService clientService) {
        return playerGame.get(clientService);
    }

    public static ClientService getAnotherPlayer(TicTacToeGame ticTacToeGame, ClientService player) {
        return playerGame.entrySet()
                .stream()
                .filter(e -> e.getValue().equals(ticTacToeGame))
                .map(e -> e.getKey())
                .filter(p -> !p.equals(player))
                .findFirst()
                .get();
    }

    public static void removeClientServiceByGame(TicTacToeGame game) {
        playerGame.entrySet()
                .stream()
                .filter(e -> e.getValue().equals(game))
                .map(e -> playerGame.remove(e.getKey(), game));
    }

    public static void putClientGameEntry(TicTacToeGame ticTacToeGame, ClientService clientService) throws RemoteException {
        playerGame.put(clientService, ticTacToeGame);
    }


}

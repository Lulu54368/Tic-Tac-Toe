package client;

import server.TicTacToeService;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static client.ClientGui.getClientGUI;

/**
 * @author lulu
 */
public class ClientServer {
    private static Integer portNumber;
    private static String ip;
    private static String username;
    private static TicTacToeService server;
    private static ClientServiceImpl clientService;

    public static void main(String[] args) {
        if (args.length < 3) {
            //logger.error("Please enter proper input!");
            System.exit(1);
        }
        portNumber = Integer.parseInt(args[2]);
        ip = args[1];
        username = args[0];
        try {
            connectServer();
            clientService.registerPlayer();
            getClientGUI(clientService).startGame(username, clientService.currentPlayer.getRank());
        } catch (Exception e) {
            System.out.println("Unable to register the player");
            System.exit(-1);
        }
        checkStatus();

    }

    private static void connectServer() throws NotBoundException, RemoteException {
        Registry registry = LocateRegistry.getRegistry(ip, portNumber);
        server = (TicTacToeService) registry
                .lookup("TicTacToeService");
        clientService = new ClientServiceImpl(server, username);
    }

    private static void checkStatus() {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        Runnable checkServerStatus = () -> {
            try {
                // check the server status every 1 sec
                server.pong();
            } catch (Exception e) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                System.exit(0);
            }
        };
        executorService.scheduleAtFixedRate(checkServerStatus, 0, 1000, TimeUnit.MILLISECONDS);


    }


}

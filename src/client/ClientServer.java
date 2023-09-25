package client;

import server.TicTacToeService;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientServer {
    private static Integer portNumber;
    private static String ip;
    private static String username;
    public static void main(String[] args) throws RemoteException, NotBoundException {
        if (args.length < 3) {
            //logger.error("Please enter proper input!");
            System.exit(1);
        }
        portNumber = Integer.parseInt(args[2]);
        ip = args[1];
        username = args[0];
        Registry registry = LocateRegistry.getRegistry(ip, portNumber);
        TicTacToeService server = (TicTacToeService) registry
                .lookup("TicTacToeService");
        ClientServiceImpl clientService = new ClientServiceImpl(server, username);
        clientService.play();
    }

}

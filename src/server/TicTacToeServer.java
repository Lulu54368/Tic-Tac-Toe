package server;


import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class TicTacToeServer {
     private static int portNumber;
     private static String ip;
     public static void main(String[] args) throws RemoteException {
         if (args.length < 2) {
            //logger.error("Please enter proper input!");
            System.exit(1);
         }
         portNumber = Integer.parseInt(args[1]);
         ip = args[0];
         TicTacToeService server = new TicTacToeServiceImpl();
         TicTacToeService stub = (TicTacToeService) UnicastRemoteObject
                 .exportObject((TicTacToeService) server, 0);
         Registry registry = LocateRegistry.createRegistry(portNumber);
         registry.rebind("TicTacToeService", stub);



    }
}

package server;

import java.rmi.RemoteException;

public class TicTacToeServiceImpl implements TicTacToeService{
    @Override
    public String sendMessage(String clientMessage) throws RemoteException {
        return "server message";
    }
}

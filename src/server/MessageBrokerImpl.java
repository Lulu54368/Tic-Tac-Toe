package server;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class MessageBrokerImpl implements MessageBroker, Serializable {
    private Queue<Map<IPlayer, String>> messageQueue = new LinkedList<>();

    private TicTacToeGame game;

    public MessageBrokerImpl(TicTacToeGame game) {
        this.game = game;
    }

    @Override
    public void sendMessage(IPlayer iPlayer, String message) throws RemoteException {
        if(messageQueue.size() >=10){
            messageQueue.poll();
        }
        Map<IPlayer, String> messageToAdd = new HashMap<>();
        messageToAdd.put(iPlayer, message);
        messageQueue.offer(messageToAdd);
        game.updateMessage();
    }
    @Override
    public Queue<Map<IPlayer, String>> getMessageQueue() throws RemoteException{
        return messageQueue;
    }
}

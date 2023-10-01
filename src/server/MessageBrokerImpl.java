package server;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class MessageBrokerImpl implements MessageBroker, Serializable {
    private Queue<Map<IPlayer, String>> messageQueue = new LinkedList<>();


    @Override
    public synchronized void sendMessage(IPlayer iPlayer, String message) throws RemoteException {
        if(messageQueue.size() >=10){
            messageQueue.poll();
        }
        Map<IPlayer, String> messageToAdd = new HashMap<>();
        messageToAdd.put(iPlayer, message);
        messageQueue.offer(messageToAdd);
    }

    @Override
    public synchronized Queue<Map<IPlayer, String>> getMessageQueue() throws RemoteException{
        return messageQueue;
    }
}

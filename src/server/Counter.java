package server;

import client.ClientService;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Timer;
import java.util.TimerTask;

import static server.TicTacToeServiceImpl.switchTurn;

public class Counter extends TimerTask implements  Serializable {

    int time = 20;
    ClientService currentPlayer;

    public Counter(ClientService currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    @Override
    public void run() {
        if(time == 0 ){
            try {
                System.out.println("time out!");
                switchTurn(currentPlayer);
                cancel();
                return;
            } catch (RemoteException e) {
                //TODO: handle exception
                throw new RuntimeException(e);
            }
        }
        if(time > 0){
            try {
                currentPlayer.sendTime(time);
            } catch (RemoteException e) {
                //TODO: handle exception
                throw new RuntimeException(e);
            }
            time--;
        }


    }

    public void count() {
        TimerTask timerTask = this;
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, 0, 1000L);
    }
}

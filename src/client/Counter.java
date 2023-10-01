package client;

import server.TicTacToeService;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Timer;
import java.util.TimerTask;

public class Counter extends TimerTask implements  Serializable {

    int time = 20;
    ClientService currentPlayer;
    TicTacToeService ticTacToeService;
    Timer timer = new Timer();

    public Counter(ClientService currentPlayer, TicTacToeService ticTacToeService) {
        this.currentPlayer = currentPlayer;
        this.ticTacToeService = ticTacToeService;
    }

    @Override
    public void run() {
        if(time == 0 ){
            System.out.println("time out!");
            try {
                ticTacToeService.switchTurn(currentPlayer);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
            cancel();
            return;
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
        timer.scheduleAtFixedRate(timerTask, 0, 1000L);
    }
    public boolean cancel(){
        timer.cancel();
        return true;
    }
}

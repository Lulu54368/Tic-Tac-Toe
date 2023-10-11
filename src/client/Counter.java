package client;

import server.TicTacToeService;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Timer;
import java.util.TimerTask;

import static client.ClientGui.getClientGUI;

public class Counter extends TimerTask implements Serializable {

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
        if (time == 0) {
            try {
                currentPlayer.sendTime(time);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
            try {
                int[] component = ticTacToeService.playInRandomPosition(currentPlayer);
                getClientGUI(currentPlayer)
                        .notify("Time out! Place row " + component[0] + " col " + component[1] + " for you!");
                cancel();
                return;
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
        if (time > 0) {
            try {
                currentPlayer.sendTime(time);
            } catch (RemoteException e) {
                System.err.println("Unable to send time");
                throw new RuntimeException(e);
            }
            time--;
        }


    }

    public void count() {
        TimerTask timerTask = this;
        timer.scheduleAtFixedRate(timerTask, 0, 1000L);
    }

    public boolean cancel() {
        timer.cancel();
        return true;
    }
}

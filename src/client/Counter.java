package client;

import server.TicTacToeService;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.*;

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
                List<Integer> rowList = Arrays.asList(0, 1, 2);
                List<Integer> colList = Arrays.asList(0, 1,2);
                Collections.shuffle(rowList);
                Collections.shuffle(colList);
                for(int row: rowList){
                    for(int col:colList){
                        if(currentPlayer.getGame().isValidMove(row, col)){
                            currentPlayer.play(row, col);
                            getClientGUI(currentPlayer).notify("Time out! Place row "+ row+" col "+ col+" for you!");
                            return;
                        }


                    }
                    break;
                }

            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
            /*try {
                ticTacToeService.switchTurn(currentPlayer);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }*/
            cancel();
            return;
        }
        if (time > 0) {
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

    public boolean cancel() {
        timer.cancel();
        return true;
    }
}

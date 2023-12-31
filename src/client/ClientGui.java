package client;

import server.IPlayer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

import static client.StartGUI.getStartGUI;

/**
 * @author lulu
 */
public class ClientGui extends JFrame {
    private static ClientGui clientGui;
    private final ClientService clientService;
    private JTextArea timer;
    private JTextPane playerchat;
    private JButton button1;
    private JButton button2;
    private JButton button4;
    private JButton button5;
    private JButton button3;
    private JButton button6;
    private JButton button7;
    private JButton button8;
    private JButton button9;
    private final List<JButton> jButtons = Arrays.asList(button1, button2, button3, button4, button5, button6, button7, button8, button9);
    private JTextArea playerContent;
    private JButton quit;
    private JPanel clientPanel;
    private JTextField username;
    private JButton sendButton;

    private ClientGui(ClientService clientService) {
        initialiseGUI();
        disableButton();
        this.clientService = clientService;
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Thread submitMessage = new Thread(() ->
                    {
                        try {
                            clientService.sendMessage(playerContent.getText());
                        } catch (RemoteException ex) {
                            throw new RuntimeException(ex);
                        }
                    });
                    submitMessage.start();
                    submitMessage.join();
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        jButtons.stream().forEach(jButton -> {
            jButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int row = jButtons.indexOf(jButton) / 3;
                    int col = jButtons.indexOf(jButton) % 3;
                    try {
                        disableButton();
                        clientService.play(row, col);
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
        });
        quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int response = JOptionPane.showConfirmDialog(null, "Do you want to exit?");
                if (response == JOptionPane.YES_OPTION) {
                    try {
                        clientService.quit();
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int response = JOptionPane.showConfirmDialog(null, "Do you want to exit?");
                if (response == JOptionPane.YES_OPTION) {
                    try {
                        clientService.quit();
                        System.exit(0);
                    } catch (RemoteException ex) {
                        ex.printStackTrace();
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

    }

    public static ClientGui getClientGUI(ClientService clientService) {
        if (clientGui == null) {
            clientGui = new ClientGui(clientService);
        }
        return clientGui;
    }

    private void initialiseGUI() {
        setContentPane(clientPanel);
        setTitle("Tic-Tac-Toe Client GUI");
        setSize(640, 200);
        setLocationRelativeTo(null);
        setVisible(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

    }

    public void startGame(String username, int rank) {
        this.username.setText("#" + rank + " " + username);

    }


    public void disableButton() {
        jButtons.stream().forEach(jButton -> {
            jButton.setEnabled(false);
        });
    }

    public void play() throws RemoteException {
        enableButton();
    }

    public void showBanner(IPlayer iPlayer) throws RemoteException {
        String banner = "#" + iPlayer.getRank() + " " + iPlayer.getUsername() + "(" + iPlayer.getSymbol() + ")";
        username.setText(banner);
    }

    private void enableButton() {
        jButtons.stream().forEach(jButton -> {
            jButton.setEnabled(true);
        });
    }


    public void showTime(int time) {
        timer.setText("Timer\n" + time);
    }

    public void showMessage(Queue<Map<IPlayer, String>> messageQueue) {
        String chat = messageQueue.stream()
                .map(q -> q
                        .entrySet()
                        .stream()
                        .map(m -> {
                            try {
                                String name = "#" + m.getKey().getRank() + " " + m.getKey().getUsername();
                                return name + " " + m.getValue() + "\n";
                            } catch (RemoteException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .findFirst()
                        .get())
                .collect(Collectors.joining());
        playerchat.setText(chat);
    }

    public void addOnBoard(char symbol, int index) {
        jButtons.get(index)
                .setText(String.valueOf(symbol));
    }

    public void erase() {
        timer.setText("");
    }


    public synchronized void showResult(String result) {
        JOptionPane.showMessageDialog(null, result, "result", JOptionPane.INFORMATION_MESSAGE);

    }

    public void clear() {
        erase();
        jButtons.stream().forEach(jButton -> {
            jButton.setText("");
        });
        playerContent.setText("");
        playerchat.setText("");
        setVisible(false);
    }

    public void resume(char[][] board, boolean flag) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int ind = i * 3 + j;
                jButtons.get(ind).setText(String.valueOf(board[i][j]));
            }
        }
        setVisible(true);
        getStartGUI(clientService).setVisible(false);
        if (flag) {
            enableButton();
        } else {
            disableButton();
        }
    }

    public synchronized void notify(String s) {
        JOptionPane.showMessageDialog(ClientGui.this, s);
    }
}

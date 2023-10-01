package client;

import server.IPlayer;
import server.MessageBroker;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

public class ClientGui extends JFrame{
    private JTextArea timer;
    private JTextPane playerchat;
    private JButton button1;
    private JButton button2;
    private JButton button3;
    private JButton button4;
    private JButton button5;
    private JButton button6;
    private JButton button7;
    private JButton button8;
    private JButton button9;
    private JTextArea playerContent;
    private JButton quit;
    private JPanel clientPanel;
    private JTextField username;
    private JButton sendButton;
    private static ClientGui clientGui;
    private ClientService clientService;

    private ClientGui(ClientService clientService){
        initialiseGUI();
        this.clientService = clientService;
    }
    public static ClientGui getClientGUI(ClientService clientService){
        if(clientGui == null){
            clientGui = new ClientGui(clientService);
        }
        return clientGui;
    }
    private void initialiseGUI() {
        setContentPane(clientPanel);
        setTitle("Dictionary Client GUI");
        setSize(640, 200);
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);


    }
    public void startGame(String username, int rank) {
        this.username.setText("#" + rank+" " +username);

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Thread submitMessage = new Thread(()->
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
    }

    public void showTime(int time) {
        timer.setText("Timer\n"+ time);
    }

    public void showMessage(Queue<Map<IPlayer, String>> messageQueue) {
        String chat = messageQueue.stream()
                .map(q->q
                        .entrySet()
                        .stream()
                        .map(m-> {
                            try {
                                String name = "#"+ m.getKey().getRank()+ " "+ m.getKey().getUsername();
                                return name + " "+ m.getValue()+ "\n";
                            } catch (RemoteException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .findFirst()
                        .get())
                .collect(Collectors.joining());
        playerchat.setText(chat);
    }




}

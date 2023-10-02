package client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

import static client.ClientGui.getClientGUI;

public class StartGUI extends JFrame{
    private JPanel homePagePanel;
    private JTextField message;
    private JButton quitButton;
    private JButton startButton;
    private static StartGUI startGUI;
    private ClientService clientService;
    private final String WAITING_MATCHING="Please wait for a match...";
    private final String ENTER_INPUT="Please make a choice...";

    private StartGUI(ClientService clientService) {
        initialiseGUI();
        this.clientService = clientService;
        waitingForMatch();
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    clientService.registerPlayer();
                } catch (RemoteException ex) {
                    //TODO: handle exception
                    throw new RuntimeException(ex);
                }
                waitingForMatch();

            }
        });
        quitButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(1);
            }
        });
    }

    public static StartGUI getStartGUI(ClientService clientService) {
        if (startGUI == null) {
            startGUI = new StartGUI(clientService);
        }
        return startGUI;
    }

    private void initialiseGUI() {
        setContentPane(homePagePanel);
        setTitle("Tic-Tac-Toe Start GUI");
        setSize(640, 200);
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);


    }

    public void startGame() {
        this.setVisible(false);
        getClientGUI(clientService).setVisible(true);
    }

    public void showHomePage() {
        this.setVisible(true);
        startButton.setVisible(true);
        quitButton.setVisible(true);
        message.setText(ENTER_INPUT);
    }
    private void waitingForMatch(){
        startButton.setVisible(false);
        quitButton.setVisible(false);
        message.setText(WAITING_MATCHING);
    }
}

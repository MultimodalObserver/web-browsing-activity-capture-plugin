package mo.capture.webActivity.server.view;

import mo.capture.webActivity.server.controller.ServerController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ServerConfigurationView {
    private JFrame mainFrame;
    private JButton saveButton;
    private JTextField serverIpTextField;
    private JTextField serverPortTextField;
    private JLabel serverIpErrorLabel;
    private JLabel serverPortErrorLabel;
    private JLabel connectionStatusLabel;
    private ServerController controller;

    public ServerConfigurationView(ServerController controller){
        this.controller = controller;
        this.mainFrame = new JFrame("Server Configuration");
        this.mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.serverIpTextField = new JTextField();
        this.serverPortTextField = new JTextField();
        this.serverIpErrorLabel = new JLabel();
        this.serverPortErrorLabel = new JLabel();
        this.connectionStatusLabel = new JLabel();
        this.saveButton = new JButton("Connect");
        this.mainFrame.getContentPane().setLayout(new BoxLayout(this.mainFrame.getContentPane(), BoxLayout.Y_AXIS));
        this.serverIpTextField.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.serverIpErrorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.serverPortTextField.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.serverPortErrorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.connectionStatusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.serverIpErrorLabel.setVisible(false);
        this.serverPortErrorLabel.setVisible(false);
        this.mainFrame.getContentPane().add(this.serverIpTextField);
        this.mainFrame.getContentPane().add(this.serverIpErrorLabel);
        this.mainFrame.getContentPane().add(this.serverPortTextField);
        this.mainFrame.getContentPane().add(this.serverPortErrorLabel);
        this.mainFrame.getContentPane().add(this.connectionStatusLabel);
        this.mainFrame.getContentPane().add(saveButton);
        this.saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ServerConfigurationView.this.serverIpErrorLabel.setText("");
                ServerConfigurationView.this.serverIpErrorLabel.setVisible(false);
                ServerConfigurationView.this.serverPortErrorLabel.setText("");
                ServerConfigurationView.this.serverPortErrorLabel.setVisible(false);
                String ip = ServerConfigurationView.this.serverIpTextField.getText();
                String port = ServerConfigurationView.this.serverPortTextField.getText();
                if(ip.isEmpty() || port.isEmpty()){
                    if(ip.isEmpty()){
                        ServerConfigurationView.this.serverIpErrorLabel.setText("Server IP cannot be empty");
                        ServerConfigurationView.this.serverIpErrorLabel.setVisible(true);
                    }
                    if(port.isEmpty()){
                        ServerConfigurationView.this.serverPortErrorLabel.setText("Server port cannot be empty");
                        ServerConfigurationView.this.serverPortErrorLabel.setVisible(true);
                    }
                    return;
                }
                int connectionResult = controller.startServer(ip, port);
                if(connectionResult == ServerController.UNKNOWN_HOST ||
                        connectionResult == ServerController.PORT_NOT_AVAILABLE){
                    if(connectionResult == ServerController.UNKNOWN_HOST){
                        serverIpErrorLabel.setText("Server IP "+ ip + " unknown");
                        serverIpErrorLabel.setVisible(true);
                    }
                    else{
                        serverPortErrorLabel.setText("Server port "+ port+" not available");
                        serverPortErrorLabel.setVisible(true);
                    }
                    return;
                }
                connectionStatusLabel.setText("Server mounted on "+ip+":"+port);
            }
        });
    }

    public void show(){
        this.mainFrame.pack();
        this.mainFrame.setVisible(true);
    }

}

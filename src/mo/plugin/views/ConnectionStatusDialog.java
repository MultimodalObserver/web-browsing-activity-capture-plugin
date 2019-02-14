package mo.plugin.views;

import mo.server.controllers.ServerController;
import mo.core.I18n;
import mo.core.ui.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConnectionStatusDialog extends JDialog {

    private JLabel connectionStatusLabel;
    private int connectionStatus;
    private JButton acceptButton;
    private I18n i18n;

    public ConnectionStatusDialog(int connectionStatus, String serverHost, String serverPort){
        super(null,"", Dialog.ModalityType.APPLICATION_MODAL);
        this.connectionStatus = connectionStatus;
        this.connectionStatusLabel = new JLabel("");
        this.acceptButton = new JButton("Accept");
        this.i18n = new I18n(ConnectionStatusDialog.class);
        if(this.connectionStatus == ServerController.UNKNOWN_HOST){
            this.connectionStatusLabel.setText("Server Host "+ serverHost + " unknown");
        }
        else if(this.connectionStatus == ServerController.PORT_NOT_AVAILABLE){
            this.connectionStatusLabel.setText("Server port "+ serverPort+" not available");
        }
        else if(this.connectionStatus == ServerController.CONNECTION_ESTABLISHED){
            this.connectionStatusLabel.setText("Server mounted on "+serverHost+":"+serverPort);
        }
        this.connectionStatusLabel.setVisible(true);
        this.centerComponents();
        this.addComponents();
        this.addListeners();
    }

    private void centerComponents(){
        this.connectionStatusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.acceptButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    private void addComponents() {
        Container contentPane = this.getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.add(this.connectionStatusLabel);
        contentPane.add(this.acceptButton);
    }

    private void addListeners(){
        this.acceptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ConnectionStatusDialog.this.setVisible(false);
                ConnectionStatusDialog.this.dispose();
            }
        });
    }

    public void showDialog(){
        setMinimumSize(new Dimension(400, 150));
        setPreferredSize(new Dimension(400, 300));
        pack();
        Utils.centerOnScreen(this);
        this.setVisible(true);
    }



}

package mo.capture.webActivity.plugin.views;

import mo.core.I18n;
import mo.core.ui.Utils;

import javax.swing.*;
import java.awt.*;

public class ConnectionSuccessDialog extends JDialog {

    private JLabel messageLabel;
    private JButton acceptButton;
    private I18n i18n;

    public ConnectionSuccessDialog(){
        super(null,"", Dialog.ModalityType.APPLICATION_MODAL);
        this.i18n = new I18n(ConnectionSuccessDialog.class);
        this.initComponents();
        this.centerComponents();
        this.addComponents();
        this.addListeners();
    }

    private void initComponents() {
        this.messageLabel = new JLabel();
        this.acceptButton = new JButton(this.i18n.s("acceptButtonText"));
        /* Agregar componentes para mostrar el estado inicial de todo el servidor
        *
        * rutas y todo eso!!!
        * */
    }

    private void centerComponents(){
        this.messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.acceptButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    private void addComponents() {
        Container contentPane = this.getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.add(this.messageLabel);
        contentPane.add(this.acceptButton);
    }

    private void addListeners(){
        this.acceptButton.addActionListener(e -> {
            this.setVisible(false);
            this.dispose();
        });
    }

    public void showDialog(){
        setMinimumSize(new Dimension(400, 150));
        setPreferredSize(new Dimension(400, 300));
        pack();
        Utils.centerOnScreen(this);
        this.setVisible(true);
    }

    public void setConnectionStatusLabelMessage(String message){
        this.messageLabel.setText(message);
    }
}

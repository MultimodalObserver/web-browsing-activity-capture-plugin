package mo.capture.webactivity.plugin.views;

import javax.swing.*;
import mo.capture.webactivity.plugin.models.CaptureConfiguration;
import mo.capture.webactivity.server.controllers.ServerController;
import mo.core.I18n;
import mo.core.ui.Utils;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConfigurationDialog  extends JDialog {

    private CaptureConfiguration temporalConfig;
    private boolean accepted;
    private JLabel configurationNameLabel;
    private JTextField configurationNameTextField;
    private JLabel configurationNameErrorLabel;
    private JButton saveConfigButton;
    private JLabel serverIpLabel;
    private JLabel serverPortLabel;
    private JTextField serverIpTextField;
    private JTextField serverPortTextField;
    private JLabel serverIpErrorLabel;
    private JLabel serverPortErrorLabel;
    private I18n i18n;


    public ConfigurationDialog(){
        super(null,"", Dialog.ModalityType.APPLICATION_MODAL);
        this.temporalConfig = null;
        this.accepted = false;
        this.i18n = new I18n(ConfigurationDialog.class);
        this.setTitle(this.i18n.s("configurationFrameTitleText"));
        /* COnfiguration Name*/
        this.configurationNameLabel = new JLabel(this.i18n.s("configurationNameLabelText"));
        this.configurationNameTextField = new JTextField();
        this.configurationNameErrorLabel = new JLabel(this.i18n.s("configurationNameErrorLabelText"));
        this.configurationNameErrorLabel.setVisible(false);
        /* Server Host */
        this.serverIpLabel = new JLabel("Server Host");
        this.serverIpTextField = new JTextField();
        this.serverIpErrorLabel = new JLabel("");
        this.serverIpErrorLabel.setVisible(false);
        /* Server Port */
        this.serverPortLabel = new JLabel("Server Port");
        this.serverPortTextField = new JTextField();
        this.serverPortErrorLabel = new JLabel();
        this.serverPortErrorLabel.setVisible(false);
        /* Save Button*/
        this.saveConfigButton = new JButton(this.i18n.s("saveConfigButtonText"));
        this.centerComponents();
        this.addComponents();
        this.addActionListeners();
    }

    private void centerComponents(){
        /* Config name*/
        this.configurationNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.configurationNameTextField.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.configurationNameErrorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        /* Server host*/
        this.serverIpLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.serverIpTextField.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.serverIpErrorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        /* Server Port*/
        this.serverPortLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.serverPortTextField.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.serverPortErrorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        /* Save Button*/
        this.saveConfigButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    private void addComponents(){
        Container contentPane = this.getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        /* COnfiguration Name*/
        contentPane.add(this.configurationNameLabel);
        contentPane.add(this.configurationNameTextField);
        contentPane.add(this.configurationNameErrorLabel);
        /* Server Host*/
        contentPane.add(this.serverIpLabel);
        contentPane.add(this.serverIpTextField);
        contentPane.add(this.serverIpErrorLabel);
        /* Server POrt*/
        contentPane.add(this.serverPortLabel);
        contentPane.add(this.serverPortTextField);
        contentPane.add(this.serverPortErrorLabel);
        /* Save BUtton*/
        contentPane.add(this.saveConfigButton);
    }

    public void showDialog(){
        setMinimumSize(new Dimension(400, 150));
        setPreferredSize(new Dimension(400, 300));
        pack();
        Utils.centerOnScreen(this);
        this.setVisible(true);
    }

    private void addActionListeners(){
        this.saveConfigButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ConfigurationDialog.this.configurationNameErrorLabel.setVisible(false);
                String configurationName = ConfigurationDialog.this.configurationNameTextField.getText();
                String serverIp = ConfigurationDialog.this.serverIpTextField.getText();
                String serverPort = ConfigurationDialog.this.serverPortTextField.getText();
                if(configurationName.isEmpty()){
                    ConfigurationDialog.this.configurationNameErrorLabel.setVisible(true);
                    return;
                }
                if(serverIp.isEmpty()){
                    ConfigurationDialog.this.serverIpErrorLabel.setVisible(true);
                    return;
                }
                if(serverPort.isEmpty()){
                    ConfigurationDialog.this.serverPortErrorLabel.setVisible(true);
                    return;
                }
                ConfigurationDialog.this.setVisible(false);
                ConfigurationDialog.this.dispose();
                ConfigurationDialog.this.temporalConfig = new CaptureConfiguration(configurationName, serverIp, serverPort);
                ConfigurationDialog.this.accepted = true;
                ConfigurationDialog.this.setVisible(false);
                ConfigurationDialog.this.dispose();
            }
        });
    }

    public CaptureConfiguration getTemporalConfig() {
        return temporalConfig;
    }

    public boolean isAccepted() {
        return accepted;
    }

}

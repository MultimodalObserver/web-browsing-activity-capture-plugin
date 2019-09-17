package mo.capture.webActivity.plugin.view;

import javax.swing.*;
import mo.capture.webActivity.plugin.model.CaptureConfiguration;
import mo.capture.webActivity.server.controller.ServerController;
import mo.core.I18n;
import mo.core.ui.Utils;

import java.awt.*;

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
    private JCheckBox checkConnectionCheckBox;
    private JLabel testServerLabel;
    private JCheckBox exportToCsvCheckBox;
    private I18n i18n;


    public ConfigurationDialog(){
        super(null,"", Dialog.ModalityType.APPLICATION_MODAL);
        this.temporalConfig = null;
        this.accepted = false;
        this.i18n = new I18n(ConfigurationDialog.class);
        this.setTitle(this.i18n.s("configurationFrameTitleText"));
        this.initComponents();
        this.addComponents();
        this.addActionListeners();
    }

    private void initComponents() {
        /* COnfiguration Name Label*/
        this.configurationNameLabel = new JLabel(this.i18n.s("configurationNameLabelText"));

        /* Configuration name TextField*/
        this.configurationNameTextField = new JTextField();


        /*Configuration Name Error label*/
        this.configurationNameErrorLabel = new JLabel();
        this.configurationNameErrorLabel.setVisible(false);
        this.configurationNameErrorLabel.setForeground(Color.RED);


        /* Server Host Label*/
        this.serverIpLabel = new JLabel(this.i18n.s("serverHostLabelText"));

        /* Server Host Text Field*/
        this.serverIpTextField = new JTextField();

        /* Server IP Error Label*/
        this.serverIpErrorLabel = new JLabel();
        this.serverIpErrorLabel.setVisible(false);
        this.serverIpErrorLabel.setForeground(Color.RED);

        /* Server Port Label*/
        this.serverPortLabel = new JLabel(this.i18n.s("serverPortLabelText"));

        /* Server Port Text Field*/
        this.serverPortTextField = new JTextField();

        /* Server port error label*/
        this.serverPortErrorLabel = new JLabel();
        this.serverPortErrorLabel.setVisible(false);
        this.serverPortErrorLabel.setForeground(Color.RED);

        /*Export to CSV CheckBox */
        this.exportToCsvCheckBox = new JCheckBox(this.i18n.s("exportToCsvCheckBoxText"));

        /* Check connection CheckBox */
        this.checkConnectionCheckBox = new JCheckBox(this.i18n.s("checkConnectionCheckBoxText"));

        /* connection status label*/
        this.testServerLabel = new JLabel();
        this.testServerLabel.setVisible(false);
        this.testServerLabel.setForeground(Color.GREEN);


        /* Save Button*/
        this.saveConfigButton = new JButton(this.i18n.s("saveConfigButtonText"));
    }

    private void addComponents(){
        Container contentPane = this.getContentPane();
        contentPane.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        /* COnfiguration Name label*/
        constraints.gridx = 0;
        constraints.gridy = 0;
        this.setConstraintsForLeftSide(constraints);
        constraints.insets = new Insets(10,10,5,5);
        contentPane.add(this.configurationNameLabel, constraints);

        /* Configuration name text field*/
        constraints = new GridBagConstraints();
        constraints.gridx=1;
        constraints.gridy=0;
        this.setConstraintsForRightSide(constraints, false);
        constraints.insets = new Insets(10,5,5,10);
        contentPane.add(this.configurationNameTextField, constraints);

        /* Configuration name error label*/
        constraints = new GridBagConstraints();
        constraints.gridx=1;
        constraints.gridy=1;
        this.setConstraintsForRightSide(constraints, true);
        contentPane.add(this.configurationNameErrorLabel, constraints);

        /* Server Host Label*/
        constraints = new GridBagConstraints();
        constraints.gridx=0;
        constraints.gridy=2;
        this.setConstraintsForLeftSide(constraints);
        contentPane.add(this.serverIpLabel, constraints);

        /* Server host text field*/
        constraints = new GridBagConstraints();
        constraints.gridx=1;
        constraints.gridy=2;
        this.setConstraintsForRightSide(constraints, false);
        contentPane.add(this.serverIpTextField, constraints);

        /* Server host error label*/
        constraints = new GridBagConstraints();
        constraints.gridx=1;
        constraints.gridy=3;
        this.setConstraintsForRightSide(constraints, true);
        contentPane.add(this.serverIpErrorLabel, constraints);

        /* Server Port label*/
        constraints = new GridBagConstraints();
        constraints.gridx=0;
        constraints.gridy=4;
        this.setConstraintsForLeftSide(constraints);
        contentPane.add(this.serverPortLabel, constraints);

        /* Server port text field*/
        constraints = new GridBagConstraints();
        constraints.gridx=1;
        constraints.gridy=4;
        this.setConstraintsForRightSide(constraints, false);
        contentPane.add(this.serverPortTextField, constraints);

        /* Server port error label*/
        constraints = new GridBagConstraints();
        constraints.gridx=1;
        constraints.gridy=5;
        this.setConstraintsForRightSide(constraints, true);
        contentPane.add(this.serverPortErrorLabel,constraints);

        /* Export to CSv CheckBox*/
        constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 6;
        constraints.gridheight=1;
        constraints.gridwidth=3;
        constraints.weightx=0.0;
        constraints.weighty=0.0;
        constraints.fill=GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(0,5,0,10);
        contentPane.add(this.exportToCsvCheckBox, constraints);

        /* Check connection Checkbox*/
        constraints.gridx=0;
        constraints.gridy=7;
        contentPane.add(this.checkConnectionCheckBox, constraints);

        /*Test result label*/
        constraints.gridy=8;
        constraints.insets= new Insets(5,15,5,10);
        contentPane.add(this.testServerLabel, constraints);

        /* Save Button*/
        constraints.gridy=9;
        constraints.insets= new Insets(5,10,10,10);
        contentPane.add(this.saveConfigButton, constraints);
    }

    private void setConstraintsForLeftSide(GridBagConstraints constraints){
        constraints.gridwidth=1;
        constraints.gridheight=2;
        constraints.weighty=1.0;
        constraints.insets= new Insets(5,10,5,5);
        constraints.anchor=GridBagConstraints.FIRST_LINE_START;
    }

    private void setConstraintsForRightSide(GridBagConstraints constraints, boolean errorLabel){
        constraints.gridheight=1;
        constraints.gridwidth=GridBagConstraints.REMAINDER;
        constraints.weightx=1.0;
        constraints.fill=GridBagConstraints.HORIZONTAL;
        int topInset = errorLabel ? 0 : 5;
        int bottomInset = errorLabel ? 5 : 0;
        constraints.insets= new Insets(topInset,5,bottomInset,10);
        constraints.anchor = GridBagConstraints.FIRST_LINE_START;
    }

    public void showDialog(){
        this.setMinimumSize(new Dimension(500, 300));
        this.setPreferredSize(new Dimension(500, 300));
        this.pack();
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        Utils.centerOnScreen(this);
        this.setVisible(true);
    }

    private void addActionListeners(){
        this.checkConnectionCheckBox.addActionListener(e -> {
            this.resetTestMessage();
            this.resetErrors();
            if(this.checkConnectionCheckBox.isSelected()){
                if(this.invalidData(true)){
                    this.checkConnectionCheckBox.setSelected(false);
                    return;
                }
                String serverHost = this.serverIpTextField.getText();
                String serverPort = this.serverPortTextField.getText();
                int serverStatus = ServerController.getInstance().startServer(serverHost, serverPort);
                if(serverStatus == ServerController.CONNECTION_ESTABLISHED){
                    this.testServerLabel.setText(this.i18n.s("successTestMessage"));
                    this.testServerLabel.setVisible(true);
                    ServerController.getInstance().stopTestServer();
                }
                else if(serverStatus == ServerController.UNKNOWN_HOST){
                    this.serverIpErrorLabel.setText(this.i18n.s("unknownHostErrorMessage"));
                    this.serverIpErrorLabel.setVisible(true);
                    this.checkConnectionCheckBox.setSelected(false);
                }
                else if(serverStatus == ServerController.INVALID_PORT){
                    this.serverPortErrorLabel.setText(this.i18n.s("invalidPortErrorMessage"));
                    this.serverPortErrorLabel.setVisible(true);
                    this.checkConnectionCheckBox.setSelected(false);
                }
                else if(serverStatus == ServerController.PORT_NOT_AVAILABLE){
                    this.serverPortErrorLabel.setText(this.i18n.s("portNotAvailableErrorMessage"));
                    this.serverPortErrorLabel.setVisible(true);
                    this.checkConnectionCheckBox.setSelected(false);
                }
            }
        });
        this.saveConfigButton.addActionListener(e -> {
            this.resetErrors();
            if(this.invalidData(false)){
             return;
            }
            String configurationName = this.configurationNameTextField.getText();
            String serverHost = this.serverIpTextField.getText();
            String serverPort = this.serverPortTextField.getText();
            boolean exportToCsv = this.exportToCsvCheckBox.isSelected();
            this.temporalConfig = new CaptureConfiguration(configurationName, serverHost, serverPort, exportToCsv);
            this.accepted = true;
            this.setVisible(false);
            this.dispose();
        });
    }

    public CaptureConfiguration getTemporalConfig() {
        return temporalConfig;
    }

    public boolean isAccepted() {
        return accepted;
    }

    private void resetTestMessage(){
        this.testServerLabel.setText("");
        this.testServerLabel.setVisible(false);
    }

    private void resetErrors(){
        this.configurationNameErrorLabel.setText("");
        this.configurationNameErrorLabel.setVisible(false);
        this.serverIpErrorLabel.setText("");
        this.serverIpErrorLabel.setVisible(false);
        this.serverPortErrorLabel.setText("");
        this.serverPortErrorLabel.setVisible(false);
    }

    private boolean invalidData(boolean testConnection){
        String configurationName = this.configurationNameTextField.getText();
        String serverIp = this.serverIpTextField.getText();
        String serverPort = this.serverPortTextField.getText();
        boolean consideringName = testConnection && configurationName.isEmpty();
        if(consideringName || serverIp.isEmpty() || serverPort.isEmpty()){
            if(consideringName){
                this.configurationNameErrorLabel.setText(this.i18n.s("emptyConfigName"));
                this.configurationNameErrorLabel.setVisible(true);
            }
            if(serverIp.isEmpty()){
                this.serverIpErrorLabel.setText(this.i18n.s("emptyServerHost"));
                this.serverIpErrorLabel.setVisible(true);
            }
            if(serverPort.isEmpty()){
                this.serverPortErrorLabel.setText(this.i18n.s("emptyServerPort"));
                this.serverPortErrorLabel.setVisible(true);
            }
            return true;
        }
        return false;
    }

}

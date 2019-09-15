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
    private JLabel formatLabel;
    private JComboBox<String> formatComboBox;
    private I18n i18n;


    public ConfigurationDialog(){
        super(null,"", Dialog.ModalityType.APPLICATION_MODAL);
        this.temporalConfig = null;
        this.accepted = false;
        this.i18n = new I18n(ConfigurationDialog.class);
        this.setTitle(this.i18n.s("configurationFrameTitleText"));
        this.initComponents();
        //this.centerComponents();
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


        /*Output file format label*/
        this.formatLabel = new JLabel(this.i18n.s("outputFormatLabelText"));

        /*Output format combo box */
        this.formatComboBox = new JComboBox<>();
        this.formatComboBox.addItem(ServerController.JSON_FORMAT);
        this.formatComboBox.addItem(ServerController.CSV_FORMAT);

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

        /* Output Format Label*/
        constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 6;
        this.setConstraintsForLeftSide(constraints);
        constraints.gridheight =1;
        contentPane.add(this.formatLabel, constraints);

        /* Output combo box*/
        constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.gridy = 6;
        this.setConstraintsForRightSide(constraints, false);
        contentPane.add(this.formatComboBox, constraints);

        /* Check connection Checkbox*/
        constraints = new GridBagConstraints();
        constraints.gridx=0;
        constraints.gridy=7;
        constraints.gridheight=1;
        constraints.gridwidth=3;
        constraints.weightx=0.0;
        constraints.weighty=0.0;
        constraints.fill=GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(0,5,0,10);
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
                //System.out.println("ME SELECCIONARON");
                if(!this.validateData()){
                    //System.out.println("NO PASE VALIDACION");
                    this.checkConnectionCheckBox.setSelected(false);
                    return;
                }
                //System.out.println("PASE VALIDACION");
                String serverHost = this.serverIpTextField.getText();
                String serverPort = this.serverPortTextField.getText();
                int serverStatus = ServerController.getInstance().startServer(serverHost, serverPort);
                if(serverStatus == ServerController.CONNECTION_ESTABLISHED){
                    //System.out.println("SERVIDOR INICIADO");
                    /* Mensaje de confirmacion
                    *
                    * Levantar nuevo dialogo que liste todo el estado inicial del server
                    *
                    * /* Esto cambiarlo!!!
                    *
                    * se debe abrir el success luego de finalizar este dialog.
                    *
                    * PONER LABEL DE TEST DE RESULTADO DE CONEXION
                    *
                    * CAMBIAR LABEL POR DIALOG CORTITO. DA MAS INTERACCION!!!
                    * */
                    this.testServerLabel.setText(this.i18n.s("successTestMessage"));
                    this.testServerLabel.setVisible(true);
                    ServerController.getInstance().stopTestServer();
                }
                else if(serverStatus == ServerController.UNKNOWN_HOST){
                    //System.out.println("HOST DESCONOCIDO");
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
                    //System.out.println("PUERTO NO DISPONIBLE");
                    this.serverPortErrorLabel.setText(this.i18n.s("portNotAvailableErrorMessage"));
                    this.serverPortErrorLabel.setVisible(true);
                    this.checkConnectionCheckBox.setSelected(false);
                }
            }
        });
        this.saveConfigButton.addActionListener(e -> {
            this.resetErrors();
            if(!this.validateData()){
             return;
            }
            String configurationName = this.configurationNameTextField.getText();
            String serverHost = this.serverIpTextField.getText();
            String serverPort = this.serverPortTextField.getText();
            String outputFormat = (String) this.formatComboBox.getSelectedItem();
            this.temporalConfig = new CaptureConfiguration(configurationName, serverHost, serverPort, outputFormat);
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

    public void setAccepted(boolean accepted){
        this.accepted = accepted;
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

    private  boolean validateData(){
        String configurationName = this.configurationNameTextField.getText();
        String serverIp = this.serverIpTextField.getText();
        String serverPort = this.serverPortTextField.getText();
        if(configurationName.isEmpty() || serverIp.isEmpty() || serverPort.isEmpty()){
            if(configurationName.isEmpty()){
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
            return false;
        }
        return true;
    }

}

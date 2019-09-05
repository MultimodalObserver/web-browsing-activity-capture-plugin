package mo.capture.webActivity.plugin.view;

import mo.capture.webActivity.server.controller.ServerController;
import mo.capture.webActivity.server.router.RouteHandlerInfo;
import mo.core.I18n;
import mo.core.ui.Utils;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class ConnectionSuccessDialog extends JDialog {

    private JLabel messageLabel;
    private JButton acceptButton;
    private JLabel routesLabel;
    private I18n i18n;

    public ConnectionSuccessDialog(){
        super(null,"", Dialog.ModalityType.APPLICATION_MODAL);
        this.i18n = new I18n(ConnectionSuccessDialog.class);
        this.setTitle(this.i18n.s("connectionSuccessDialogTitle"));
        this.initComponents();
        this.addComponents();
        this.addListeners();
    }

    private void initComponents() {
        this.messageLabel = new JLabel();
        this.routesLabel = new JLabel(this.i18n.s("routesDisplayedName"));
        this.acceptButton = new JButton(this.i18n.s("acceptButtonText"));
        /* Agregar componentes para mostrar el estado inicial de todo el servidor
        *
        * rutas y todo eso!!!
        * */
    }

    private void addComponents() {
        this.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy= 0;
        constraints.gridwidth=2;
        constraints.gridheight=1;
        constraints.weighty=1.0;
        constraints.weightx=1.0;
        constraints.fill=GridBagConstraints.HORIZONTAL;
        constraints.insets= new Insets(10,10,10,10);
        this.getContentPane().add(this.messageLabel, constraints);
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

    public void setSuccessStatus(String message){
        this.messageLabel.setText(message);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx=0;
        constraints.gridy=1;
        constraints.gridheight = 1;
        constraints.gridwidth=2;
        constraints.weightx=1.0;
        constraints.weighty=1.0;
        constraints.fill=GridBagConstraints.HORIZONTAL;
        constraints.anchor=GridBagConstraints.PAGE_START;
        constraints.insets= new Insets(10,10,10,10);
        Map<String, Map<String, RouteHandlerInfo>> routes = ServerController.getInstance().getRouter().getRoutes();
        String protocol = "http://";
        String serverHost = ServerController.getInstance().getInetSocketAddress().getHostName();
        String serverPort = String.valueOf(ServerController.getInstance().getInetSocketAddress().getPort());
        int row = 2;
        Container contentPane = this.getContentPane();
        for(Object key : routes.keySet()){
            String route = key.toString();
            if(route.equals("/start") || route.equals("/stop")){
                continue;
            }
            String endpoint = protocol + serverHost + ":" + serverPort + route;
            String dataType = route.replace("/", "");
            JLabel dataTypeLabel = new JLabel(this.i18n.s(dataType + "HelpMessageText"));
            constraints = new GridBagConstraints();
            constraints.gridx=0;
            constraints.gridy=row;
            constraints.gridheight=1;
            constraints.gridwidth=1;
            constraints.weightx=0.0;
            constraints.weighty=1.0;
            constraints.fill=GridBagConstraints.NONE;
            constraints.anchor=GridBagConstraints.FIRST_LINE_START;
            constraints.insets = new Insets(5,10,5,5);
            contentPane.add(dataTypeLabel, constraints);
            JLabel endpointLabel = new JLabel(endpoint);
            constraints = new GridBagConstraints();
            constraints.gridx=1;
            constraints.gridy=row;
            constraints.gridwidth=2;
            constraints.gridheight=1;
            constraints.weightx=1.0;
            constraints.weighty=0.0;
            constraints.fill=GridBagConstraints.HORIZONTAL;
            constraints.anchor=GridBagConstraints.FIRST_LINE_START;
            constraints.insets = new Insets(5,5,5,10);
            contentPane.add(endpointLabel, constraints);
            row++;
        }
        constraints = new GridBagConstraints();
        constraints.gridx=0;
        constraints.gridy=row;
        constraints.gridheight=1;
        constraints.gridwidth=2;
        constraints.weighty=0.0;
        constraints.weightx=0.0;
        constraints.fill=GridBagConstraints.HORIZONTAL;
        constraints.insets=new Insets(5,10,10,10);
        contentPane.add(this.acceptButton, constraints);
    }
}

/*
 * COPYRIGHT © Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig 2011
 * LoginScreen.java
 *
 * Login screen for the IM-client part
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * @author miniwolf
 */
class LoginScreen extends JFrame {
    private JButton jLoginBtn;
    private JLabel jMessageLabel;
    private JTextField jUsername;
    private JPasswordField jPassword, jPasswordRepeat;
    private Communication communication;
    private boolean connected = false, creatingUser = false;

    /**
     * Creates new form LoginScreen
     */
    public LoginScreen() {
        initComponents();
        initCommunication();
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     */
    private void initComponents() {
        jUsername = new JTextField();
        jPassword = new JPasswordField();
        jPasswordRepeat = new JPasswordField();
        jMessageLabel = new JLabel();
        jLoginBtn = new JButton();
        JMenuBar jMenuBar1 = new JMenuBar();
        JMenu jMenu1 = new JMenu();
        JMenuItem jMenuCreateUser = new JMenuItem();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setName("Fagi"); // NOI18N
        setResizable(false);

        Container container = getContentPane();
        container.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints;

        jUsername.setText("Username...");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 181;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(6, 10, 0, 10);
        container.add(jUsername, gridBagConstraints);

        jPassword.setText("Password...");
        jPassword.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                handleUserRequest();
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 181;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(6, 10, 0, 10);
        container.add(jPassword, gridBagConstraints);

        jPasswordRepeat.setText("Password...");
        jPasswordRepeat.setVisible(false);
        jPasswordRepeat.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                handleUserRequest();
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 181;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(6, 10, 0, 10);
        container.add(jPasswordRepeat, gridBagConstraints);

        jLoginBtn.setText("Login");
        jLoginBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                handleUserRequest();
            }
        });

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 18;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(6, 106, 0, 10);
        container.add(jLoginBtn, gridBagConstraints);

        jMessageLabel.setText("test");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 66;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(7, 10, 11, 0);
        container.add(jMessageLabel, gridBagConstraints);

        jMenuCreateUser.setText("Create User");
        jMenuCreateUser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jMenuCreateUserActionPerformed();
            }
        });
        jMenu1.setText("User");
        jMenu1.add(jMenuCreateUser);
        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);
        pack();
    }

    private void initCommunication() {
        try {
            communication = new Communication();
            LoginManager.setCommunication(communication);
            jMessageLabel.setText("Connected to server");
            connected = true;
        } catch (IOException ioe) {
            jMessageLabel.setText("Connection refused");
        }
        pack();
    }

    private void jMenuCreateUserActionPerformed() {
        if ( !connected ) {
            jMessageLabel.setText("Cannot create user, no connection");
            return;
        }

        jPasswordRepeat.setVisible(true);
        jLoginBtn.setText("Create User");
        creatingUser = true;
        pack();
    }

    private void handleUserRequest() {
        if ( !connected ) {
            jMessageLabel.setText("Cannot handle user requests, no connection");
            return;
        }

        if ( creatingUser ) {
            LoginManager.handleCreateUser(jUsername.getText(), new String(jPassword.getPassword()), new String(jPasswordRepeat.getPassword()), jMessageLabel);
            jPasswordRepeat.setVisible(false);
            jLoginBtn.setText("Login");
            creatingUser = false;
            pack();
        } else {
            String username = jUsername.getText();
            Login login = new Login(username, new String(jPassword.getPassword()));
            LoginManager.handleLogin(login, username, jMessageLabel, this);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if ( args.length != 0 )
            System.out.println("Usage: java LoginScreen");

        /* Set the Nimbus look and feel */
        try {
            for ( UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels() ) {
                if ( "Nimbus".equals(info.getName()) ) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(LoginScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        /* Create and display the form */
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LoginScreen().setVisible(true);
            }
        });
    }
}

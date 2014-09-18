/*
 * COPYRIGHT © Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig 2011
 * MainScreen.java
 *
 * UserInterface, containing chat window and contact list
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * TODO: Write description
 */
class MainScreen extends JFrame implements KeyListener {
    private final String username;
    private final Communication communication;
    private final ArrayList<Conversation> conversations;
    private final MessageListener messageListener;
    private Thread messageThread;
    private JActionList<Object> jContactList;
    private JScrollPane jScrollPane2;
    private JTextArea jMessage;
    private JLabel chatname;

    /**
     * Creates new form ContactScreen
     *
     * @param username      which is used all around the class for knowing who the user is
     * @param communication granted by the LoginScreen class
     */
    public MainScreen(String username, Communication communication) {
        this.username = username;
        this.communication = communication;
        conversations = new ArrayList<Conversation>();
        createGUI();
        initComponents();
        messageListener = new MessageListener(communication, jContactList, this);
        messageListener.update(conversations);
        messageThread = new Thread(messageListener);
        messageThread.start();
        jContactList.setCellRenderer(new MyListCellRenderer<Object>(messageListener, this));
        setVisible(true);
    }

    /**
     * Callback from constructor.
     * Used to initialize the form.
     */
    private void initComponents() {
        JScrollPane jScrollPane1 = new JScrollPane();
        jScrollPane2 = new JScrollPane();
        JScrollPane jScrollPane3 = new JScrollPane();
        final JScrollPane jScrollPane4 = new JScrollPane();
        JTextArea jConversation = new Chat("nobody, that's sad.");
        jMessage = new JTextArea();
        JTabbedPane jTabbedPane1 = new JTabbedPane();
        jContactList = new JActionList<Object>();
        JActionList<Object> jList2 = new JActionList<Object>();
        chatname = new JLabel();
        JButton jSendBtn = new JButton();
        JMenuBar jMenuBar1 = new JMenuBar();
        JMenu jMenu1 = new JMenu();
        JMenuItem jMenuFriendRequest = new JMenuItem();
        JMenuItem jLogout = new JMenuItem();

        Container content = getContentPane();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(600, 400));
        content.setLayout(new GridBagLayout());

        jMessage.setText("Enter your message...");
        jMessage.setLineWrap(true);
        jMessage.setWrapStyleWord(true);
        jMessage.addKeyListener(this);

        jScrollPane4.setViewportView(jMessage);
        jScrollPane4.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                jMessage.select(getHeight() + 1000, 0);
            }
        });

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 242;
        gridBagConstraints.ipady = 20;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(6, 6, 11, 0);
        content.add(jScrollPane4, gridBagConstraints);

        jScrollPane2.setViewportView(jConversation);
        jScrollPane2.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent e) {
                Chat chat = (Chat) jScrollPane2.getViewport().getView();
                for ( Conversation conversation : conversations ) if ( conversation.getConversation() == chat )
                    conversation.getConversation().select(getHeight() + 1000, 0);
            }
        });

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 294;
        gridBagConstraints.ipady = 262;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(6, 6, 0, 0);
        content.add(jScrollPane2, gridBagConstraints);

        jContactList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jContactList.setSelectionBackground(new Color(255, 153, 51));
                jContactListClickPerformed();
            }
        });

        jScrollPane1.setViewportView(jContactList);
        jTabbedPane1.addTab("Online", jScrollPane1);

        jScrollPane3.setViewportView(jList2);
        jTabbedPane1.addTab("Requests", jScrollPane3);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.ipadx = 62;
        gridBagConstraints.ipady = 289;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(11, 10, 11, 0);
        content.add(jTabbedPane1, gridBagConstraints);

        chatname.setText("ChatName"); // TODO: Remove after debugging purpose
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 21;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(11, 4, 0, 0);
        content.add(chatname, gridBagConstraints);

        jSendBtn.setText("Send...");
        jSendBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleMessage();
            }
        });

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(23, 6, 11, 9);
        content.add(jSendBtn, gridBagConstraints);

        jMenu1.setText("File");

        jMenuFriendRequest.setText("FriendRequest");
        jMenuFriendRequest.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jMenuFriendRequestClickPerformed();
            }
        });
        jMenu1.add(jMenuFriendRequest);

        jLogout.setText("Logout");
        jLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jLogOutClickPerformed();
            }
        });
        jMenu1.add(jLogout);
        jMenuBar1.add(jMenu1);
        setJMenuBar(jMenuBar1);

        pack();
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        e.consume();
        if ( e.getSource() != jMessage || e.getKeyCode() != KeyEvent.VK_ENTER )
            return;
        if ( e.isControlDown() ) jMessage.append("\n");
        else handleMessage();
    }

    private void handleMessage() {
        if ( jMessage.getText().equals("") ) return;

        Chat chat = (Chat) jScrollPane2.getViewport().getView();
        for ( Conversation conversation : conversations )
            if ( conversation.getConversation() == chat ) {
                communication.sendObject(new Message(username, jMessage.getText(), conversation.getChatBuddy()));
                Object object = communication.handleObjects();
                if ( object instanceof UserOnlineException )
                    conversation.getConversation().append("User went offline");
                break;
            }

        chat.append(username + ": " + jMessage.getText() + "\n");
        jMessage.setText("");
        jMessage.requestFocusInWindow();
    }

    private void jContactListClickPerformed() {
        String chatBuddy = (String) jContactList.getSelectedValue();
        if ( chatBuddy == null ) return;

        boolean exists = false;
        for ( Conversation conversation : conversations ) {
            if ( conversation.getChatBuddy().equals(chatBuddy) ) {
                jScrollPane2.setViewportView(conversation.getConversation());
                chatname.setText(chatBuddy);
                if ( messageListener.unread.indexOf(chatBuddy) != -1 ) {
                    messageListener.unread.remove(chatBuddy);
                    jContactList.repaint();
                }
                exists = true;
                break;
            }
        }

        if ( !exists ) {
            updateConversations(chatBuddy);
            return;
        }
        jMessage.requestFocusInWindow();
    }

    public void updateConversations(String chatBuddy) {
        Conversation conversation = new Conversation(chatBuddy);
        conversations.add(conversation);
        messageListener.update(conversations);
    }

    private void jLogOutClickPerformed() {
        messageListener.close();
        messageThread.interrupt();
        /* Have to wait, else the listener will try asking
           request using the closed socket causing a SocketException. */
        while ( !messageThread.isInterrupted() ) {}
        Logout logout = new Logout(username);
        LoginManager.handleLogout(logout, this);
    }

    private void jMenuFriendRequestClickPerformed() {
        String friend = JOptionPane.showInputDialog("Type username you want to add.");
        if ( null == friend ) return;

        FriendRequest friendRequest = new FriendRequest(username, friend);
        LoginManager.handleFriendRequest(friendRequest);
    }

    public JScrollPane getConversationWindow() {
        return jScrollPane2;
    }

    private static void createGUI() {
        // Set the Nimbus look and feel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(MainScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }
}

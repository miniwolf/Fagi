/*
 * COPYRIGHT © Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig 2011
 * MessageListener.java
 *
 * For receiving messages and printing these to the conversation
 */

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Add descriptions, add friend requests
 */
class MessageListener extends Thread {
    private final Communication communication;
    public ArrayList<Conversation> conversations;
    private final JActionList<Object> jContactList;
    private final MainScreen mainScreen;
    public final ArrayList<Object> unread = new ArrayList<Object>();

    public MessageListener(Communication communication, JActionList<Object> jContactList, MainScreen mainScreen) {
        this.communication = communication;
        this.jContactList = jContactList;
        this.mainScreen = mainScreen;
    }

    /**
     * Updates the current conversation array.
     *
     * @param conversations new friend list to insert instead of previous one.
     */
    public void update(ArrayList<Conversation> conversations) {
        this.conversations = conversations;
    }

    /**
     * Thread method, sending the text to the JTextArea
     * and updating our friend list.
     */
    public void run() {
        while ( true ) {
            Message message = null;
            while ( message == null ) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    System.err.println("t: " + e.toString());
                    return;
                }
                List<String> friends = communication.getFriends();
                updateFriends(friends);

                message = communication.receiveMessage();
            }
            updateConversation(message);
        }
    }

    private void updateConversation(Message message) {
        String chatBuddy = message.getSender();
        for ( Conversation conversation : conversations ) {
            if ( !conversation.getChatBuddy().equals(chatBuddy) ) continue;

            conversation.getConversation().append(conversation.getChatBuddy() + ": " + message.getMessage() + "\n");
            if ( mainScreen.getConversationWindow().getViewport().getView().equals(conversation) )
                return;

            unread.add(chatBuddy);
            jContactList.repaint();
            return;
        }
        mainScreen.updateConversations(chatBuddy);
        updateConversation(message);
    }

    private void updateFriends(List<String> friends) {
        if ( friends.isEmpty() ) { // TODO: Cheaper to not set if already empty?
            String[] object = {""};
            jContactList.setListData(object);
            return;
        }

        ListModel listModel = jContactList.getModel();
        int modelSize = listModel.getSize();
        if ( friends.size() != modelSize ) {
            jContactList.setListData(friends.toArray());
            return;
        }

        for ( int i = 0; i < modelSize; i++ )
            if ( friends.get(i) != listModel.getElementAt(i) ) {
                jContactList.setListData(friends.toArray());
                return;
            }
    }
}

/**
 * TODO: Missing highlight from current chat.
 */
class MyListCellRenderer<E> extends JLabel implements ListCellRenderer<E> {
    private final MessageListener listener;
    private final MainScreen screen;

    public MyListCellRenderer(MessageListener listener, MainScreen screen) {
        setOpaque(true);
        this.listener = listener;
        this.screen = screen;
    }

    public Component getListCellRendererComponent(JList paramlist, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if ( value == null) return null;

        setText(value.toString());
        for ( Conversation conversation : listener.conversations )
            if ( screen.getConversationWindow().getViewport().getView().equals(conversation) &&
                 value.toString().equals(conversation.getChatBuddy()) ) {
                setBackground(Color.WHITE);
                return this;
            }

        setBackground(listener.unread.indexOf(value.toString()) != -1 ? Color.BLUE : Color.WHITE);
        return this;
    }
}
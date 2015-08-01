package com.fagi.model;/*
 * Copyright (c) 2011. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * JActionList.java
 *
 * Extension for JList to handle mouse and keyboard actions.
 */


import java.awt.event.*;

/**
 * Extending JList with an actionListener
 * to be able to detect mouseActions and Enter
 */
public class JActionList<E> extends javax.swing.JList<E> {
    private ActionListener action;

    public JActionList() {
        /*
         * Can be extended to support several selected values
         * by extending object to an array and writing
         * object[0].toString() instead.
         */
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                if ( action == null ) return;

                Object object = getSelectedValue();
                if ( me.getClickCount() == 2 ) {
                    if ( object != null )
                        action.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, object.toString()));
                    me.consume();
                }
            }
        });

        /*
         * Can be extended to support several selected values
         * by extending object to an array and writing
         * object[0].toString() instead.
         */
        addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent ke) {
                if ( action == null ) return;

                Object object = getSelectedValue();
                if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
                    if ( object != null )
                        action.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, object.toString()));
                    ke.consume();
                }
            }
        });
        this.setSelectedIndex(0);
    }

    public void addActionListener(ActionListener action) {
        this.action = action;
    }
}
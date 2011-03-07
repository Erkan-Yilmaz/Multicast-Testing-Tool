/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ShowSenderDialog.java
 *
 * Created on Mar 3, 2011, 7:48:01 PM
 */

package com.spam.mctool.view.dialogs;

import com.spam.mctool.model.Sender;

/**
 *
 * @author Tobias Stöckel (Tobias.Stoeckel@de.ibm.com)
 */
public class ShowSenderDialog extends javax.swing.JDialog {

    private static final long serialVersionUID = 1L;
    private Sender sender;

	/** Creates new form ShowSenderDialog */
    public ShowSenderDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        initData();
    }

    public ShowSenderDialog(java.awt.Frame parent, boolean modal, Sender sender) {
        this(parent, modal);
        this.sender = sender;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ShowSenderDialog dialog = new ShowSenderDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    private void initData() {
        // TODO load data from sender to dialog components
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}
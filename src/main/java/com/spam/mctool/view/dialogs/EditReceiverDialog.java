/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * EditReceiverDialog.java
 *
 * Created on Mar 3, 2011, 7:47:08 PM
 */

package com.spam.mctool.view.dialogs;

import com.spam.mctool.model.Receiver;

/**
 *
 * @author Tobias Stöckel (Tobias.Stoeckel@de.ibm.com)
 */
public class EditReceiverDialog extends javax.swing.JDialog {

    private static final long serialVersionUID = 1L;
    private Receiver receiver;
    private int test;

	/** Creates new form EditReceiverDialog */
    public EditReceiverDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    public EditReceiverDialog(java.awt.Frame parent, boolean modal, Receiver receiver) {
        this(parent, modal);
        this.receiver = receiver;
        loadData();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        GroupField = new javax.swing.JTextField();
        GroupLabel = new javax.swing.JLabel();
        PortLabel = new javax.swing.JLabel();
        PortField = new javax.swing.JTextField();
        InterfaceLabel = new javax.swing.JLabel();
        InterfaceCombo = new javax.swing.JComboBox();
        ActivateBox = new javax.swing.JCheckBox();
        OKButton = new javax.swing.JButton();
        CancelButton = new javax.swing.JButton();
        PacketStyleLabel = new javax.swing.JLabel();
        PacketStyleCombo = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/spam/mctool/view/dialogs/Bundle"); // NOI18N
        GroupField.setText(bundle.getString("EditReceiverDialog.GroupField.text")); // NOI18N

        GroupLabel.setText(bundle.getString("EditReceiverDialog.GroupLabel.text")); // NOI18N

        PortLabel.setText(bundle.getString("EditReceiverDialog.PortLabel.text")); // NOI18N

        PortField.setText(bundle.getString("EditReceiverDialog.PortField.text")); // NOI18N

        InterfaceLabel.setText(bundle.getString("EditReceiverDialog.InterfaceLabel.text")); // NOI18N

        InterfaceCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        ActivateBox.setText(bundle.getString("EditReceiverDialog.ActivateBox.text")); // NOI18N
        ActivateBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ActivateBoxActionPerformed(evt);
            }
        });

        OKButton.setText(bundle.getString("EditReceiverDialog.OKButton.text")); // NOI18N
        OKButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OKButtonActionPerformed(evt);
            }
        });

        CancelButton.setText(bundle.getString("EditReceiverDialog.CancelButton.text")); // NOI18N

        PacketStyleLabel.setText(bundle.getString("EditReceiverDialog.PacketStyleLabel.text")); // NOI18N

        PacketStyleCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(GroupField, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                    .addComponent(GroupLabel)
                    .addComponent(PortLabel)
                    .addComponent(PortField, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                    .addComponent(InterfaceLabel)
                    .addComponent(InterfaceCombo, 0, 250, Short.MAX_VALUE)
                    .addComponent(PacketStyleLabel)
                    .addComponent(PacketStyleCombo, 0, 250, Short.MAX_VALUE)
                    .addComponent(ActivateBox, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(43, 43, 43)
                        .addComponent(OKButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(CancelButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(GroupLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(GroupField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(PortLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PortField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(InterfaceLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(InterfaceCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(PacketStyleLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PacketStyleCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(ActivateBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(OKButton)
                    .addComponent(CancelButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void ActivateBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ActivateBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ActivateBoxActionPerformed

    private void OKButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OKButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_OKButtonActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                EditReceiverDialog dialog = new EditReceiverDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    private void loadData(){
        this.GroupField.setText(this.receiver.getGroup().toString());
        this.GroupField.setEnabled(false);
        this.PortField.setText(String.valueOf(this.receiver.getPort()));
        this.PortField.setEnabled(false);
        this.InterfaceCombo.setEnabled(false);
        this.PacketStyleCombo.setEnabled(false);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox ActivateBox;
    private javax.swing.JButton CancelButton;
    private javax.swing.JTextField GroupField;
    private javax.swing.JLabel GroupLabel;
    private javax.swing.JComboBox InterfaceCombo;
    private javax.swing.JLabel InterfaceLabel;
    private javax.swing.JButton OKButton;
    private javax.swing.JComboBox PacketStyleCombo;
    private javax.swing.JLabel PacketStyleLabel;
    private javax.swing.JTextField PortField;
    private javax.swing.JLabel PortLabel;
    // End of variables declaration//GEN-END:variables

}

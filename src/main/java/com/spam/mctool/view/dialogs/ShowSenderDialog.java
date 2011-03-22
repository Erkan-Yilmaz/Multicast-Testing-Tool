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
import com.spam.mctool.view.main.MainFrame;
import javax.swing.JFrame;

/**
 *
 * @author Tobias Schoknecht (Tobias.Schoknecht@de.ibm.com)
 */
public class ShowSenderDialog extends javax.swing.JDialog {

    private static final long serialVersionUID = 1L;
    private Sender sender;
    private MainFrame parent;

	/** Creates new form ShowSenderDialog */
    public ShowSenderDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    public ShowSenderDialog(MainFrame parent, boolean modal) {
        this((JFrame)parent, modal);
        this.parent = parent;
    }

    public ShowSenderDialog(MainFrame parent, boolean modal, Sender sender) {
        this(parent, modal);
        this.sender = sender;
        initData();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        SenderIDLabel = new javax.swing.JLabel();
        SenderIDData = new javax.swing.JLabel();
        InterfaceLabel = new javax.swing.JLabel();
        InterfaceData = new javax.swing.JLabel();
        GroupLabel = new javax.swing.JLabel();
        GroupData = new javax.swing.JLabel();
        PortLabel = new javax.swing.JLabel();
        PortData = new javax.swing.JLabel();
        PacketStyleLabel = new javax.swing.JLabel();
        PacketStyleData = new javax.swing.JLabel();
        ActivationTimeLabel = new javax.swing.JLabel();
        ActivationTimeData = new javax.swing.JLabel();
        PacketSizeLabel = new javax.swing.JLabel();
        PacketSizeData = new javax.swing.JLabel();
        DataLabel = new javax.swing.JLabel();
        DataData = new javax.swing.JLabel();
        ConfPPSLabel = new javax.swing.JLabel();
        ConfPPSData = new javax.swing.JLabel();
        SentPPSLabel = new javax.swing.JLabel();
        SentPPSData = new javax.swing.JLabel();
        CloseButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("internationlization/Bundle"); // NOI18N
        SenderIDLabel.setText(bundle.getString("ShowSenderDialog.SenderIDLabel.text_1")); // NOI18N

        SenderIDData.setText(bundle.getString("ShowSenderDialog.SenderIDData.text_1")); // NOI18N

        InterfaceLabel.setText(bundle.getString("ShowSenderDialog.InterfaceLabel.text_1")); // NOI18N

        InterfaceData.setText(bundle.getString("ShowSenderDialog.InterfaceData.text_1")); // NOI18N

        GroupLabel.setText(bundle.getString("ShowSenderDialog.GroupLabel.text_1")); // NOI18N

        GroupData.setText(bundle.getString("ShowSenderDialog.GroupData.text_1")); // NOI18N

        PortLabel.setText(bundle.getString("ShowSenderDialog.PortLabel.text_1")); // NOI18N

        PortData.setText(bundle.getString("ShowSenderDialog.PortData.text_1")); // NOI18N

        PacketStyleLabel.setText(bundle.getString("ShowSenderDialog.PacketStyleLabel.text_1")); // NOI18N

        PacketStyleData.setText(bundle.getString("ShowSenderDialog.PacketStyleData.text_1")); // NOI18N

        ActivationTimeLabel.setText(bundle.getString("ShowSenderDialog.ActivationTimeLabel.text_1")); // NOI18N

        ActivationTimeData.setText(bundle.getString("ShowSenderDialog.ActivationTimeData.text_1")); // NOI18N

        PacketSizeLabel.setText(bundle.getString("ShowSenderDialog.PacketSizeLabel.text_1")); // NOI18N

        PacketSizeData.setText(bundle.getString("ShowSenderDialog.PacketSizeData.text_1")); // NOI18N

        DataLabel.setText(bundle.getString("ShowSenderDialog.DataLabel.text_1")); // NOI18N

        DataData.setText(bundle.getString("ShowSenderDialog.DataData.text_1")); // NOI18N

        ConfPPSLabel.setText(bundle.getString("ShowSenderDialog.ConfPPSLabel.text_1")); // NOI18N

        ConfPPSData.setText(bundle.getString("ShowSenderDialog.ConfPPSData.text_1")); // NOI18N

        SentPPSLabel.setText(bundle.getString("ShowSenderDialog.SentPPSLabel.text_1")); // NOI18N

        SentPPSData.setText(bundle.getString("ShowSenderDialog.SentPPSData.text_1")); // NOI18N

        CloseButton.setText(bundle.getString("ShowSenderDialog.CloseButton.text")); // NOI18N
        CloseButton.setActionCommand(bundle.getString("ShowSenderDialog.CloseButton.actionCommand")); // NOI18N
        CloseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CloseButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(SenderIDLabel)
                            .addComponent(InterfaceLabel)
                            .addComponent(GroupLabel)
                            .addComponent(PortLabel)
                            .addComponent(PacketStyleLabel)
                            .addComponent(ActivationTimeLabel)
                            .addComponent(PacketSizeLabel)
                            .addComponent(DataLabel)
                            .addComponent(ConfPPSLabel)
                            .addComponent(SentPPSLabel))
                        .addGap(79, 79, 79)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(SentPPSData)
                            .addComponent(ConfPPSData)
                            .addComponent(DataData)
                            .addComponent(PacketSizeData)
                            .addComponent(ActivationTimeData)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(PacketStyleData)
                                .addComponent(PortData)
                                .addComponent(GroupData, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(InterfaceData, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(SenderIDData))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(105, 105, 105)
                        .addComponent(CloseButton)))
                .addContainerGap(80, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SenderIDLabel)
                    .addComponent(SenderIDData))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(InterfaceLabel)
                    .addComponent(InterfaceData))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(GroupLabel)
                    .addComponent(GroupData))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(PortLabel)
                    .addComponent(PortData))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(PacketStyleLabel)
                    .addComponent(PacketStyleData))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ActivationTimeLabel)
                    .addComponent(ActivationTimeData))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(PacketSizeLabel)
                    .addComponent(PacketSizeData))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DataLabel)
                    .addComponent(DataData))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ConfPPSLabel)
                    .addComponent(ConfPPSData))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SentPPSLabel)
                    .addComponent(SentPPSData))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(CloseButton)
                .addContainerGap(32, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void CloseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CloseButtonActionPerformed
        this.dispose();
    }//GEN-LAST:event_CloseButtonActionPerformed

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
        this.SenderIDData.setText(String.valueOf(this.sender.getSenderId()));
        //interface
        this.GroupData.setText(this.sender.getGroup().getHostAddress());
        this.PortData.setText(String.valueOf(this.sender.getPort()));
        this.PacketStyleData.setText(this.sender.getpType().toString());
        //Active since
        this.PacketSizeData.setText(String.valueOf(this.sender.getPacketSize()));
        this.DataData.setText(this.sender.getPayloadAsString());
        this.ConfPPSData.setText(String.valueOf(this.sender.getSenderConfiguredPacketRate()));
        this.SentPPSData.setText(String.valueOf(this.sender.getAvgPPS()));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel ActivationTimeData;
    private javax.swing.JLabel ActivationTimeLabel;
    private javax.swing.JButton CloseButton;
    private javax.swing.JLabel ConfPPSData;
    private javax.swing.JLabel ConfPPSLabel;
    private javax.swing.JLabel DataData;
    private javax.swing.JLabel DataLabel;
    private javax.swing.JLabel GroupData;
    private javax.swing.JLabel GroupLabel;
    private javax.swing.JLabel InterfaceData;
    private javax.swing.JLabel InterfaceLabel;
    private javax.swing.JLabel PacketSizeData;
    private javax.swing.JLabel PacketSizeLabel;
    private javax.swing.JLabel PacketStyleData;
    private javax.swing.JLabel PacketStyleLabel;
    private javax.swing.JLabel PortData;
    private javax.swing.JLabel PortLabel;
    private javax.swing.JLabel SenderIDData;
    private javax.swing.JLabel SenderIDLabel;
    private javax.swing.JLabel SentPPSData;
    private javax.swing.JLabel SentPPSLabel;
    // End of variables declaration//GEN-END:variables

}

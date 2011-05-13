/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ShowReceiverDialog.java
 *
 * Created on Mar 3, 2011, 7:47:50 PM
 */

package com.spam.mctool.view.dialogs;

import com.spam.mctool.intermediates.ReceiverDataChangedEvent;
import com.spam.mctool.model.Receiver;
import com.spam.mctool.model.ReceiverDataChangeListener;
import com.spam.mctool.model.ReceiverGroup;
import com.spam.mctool.view.main.MainFrame;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import javax.swing.JFrame;

/**
 * Dialog to show receiver statistics
 *
 * @author Tobias Schoknecht (tobias.schoknecht@gmail.com)
 */
public class ShowReceiverDialog extends javax.swing.JDialog implements ReceiverDataChangeListener {

    private static final long serialVersionUID = 1L;
    private Receiver receiver;
    private ReceiverGroup receivergroup;
    private MainFrame parent;

    /**
     * Main constructor
     *
     * @param parent Reference to the parent window
     * @param modal Currently not used
     */
    private ShowReceiverDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    /**
     * Chained constructor to assign parent reference and to typecast parent to jframe
     *
     * @param parent Reference to the parent MainFrame
     * @param modal Currently not used
     */
    private ShowReceiverDialog(MainFrame parent, boolean modal) {
        this((JFrame)parent, modal);
        this.parent = parent;
    }

    /**
     * Constructor to be called
     *
     * @param parent Reference to the parent MainFrame
     * @param modal Currently not used
     * @param receiver Reference to the receivergroup for which statistics are to be shown
     * @param receivergroup Reference to the receivergroup to which the receiver belongs
     */
    public ShowReceiverDialog(MainFrame parent, boolean modal, Receiver receiver, ReceiverGroup receivergroup) {
        this(parent, modal);
        this.receiver = receiver;
        this.receivergroup = receivergroup;
        loadData();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        senderIDLabel = new javax.swing.JLabel();
        senderIDData = new javax.swing.JLabel();
        interfaceLabel = new javax.swing.JLabel();
        interfaceData = new javax.swing.JLabel();
        groupLabel = new javax.swing.JLabel();
        groupData = new javax.swing.JLabel();
        packetStyleLabel = new javax.swing.JLabel();
        packetStyleData = new javax.swing.JLabel();
        portLabel = new javax.swing.JLabel();
        portData = new javax.swing.JLabel();
        dataData = new javax.swing.JLabel();
        dataLabel = new javax.swing.JLabel();
        packetSizeData = new javax.swing.JLabel();
        packetSizeLabel = new javax.swing.JLabel();
        confPPSData = new javax.swing.JLabel();
        senderPPSLabel = new javax.swing.JLabel();
        senderPPSData = new javax.swing.JLabel();
        confPPSLabel = new javax.swing.JLabel();
        receiverPPSLabel = new javax.swing.JLabel();
        receiverPPSData = new javax.swing.JLabel();
        lostPacketsLabel = new javax.swing.JLabel();
        lostPacketsData = new javax.swing.JLabel();
        maxDelayLabel = new javax.swing.JLabel();
        maxDelayData = new javax.swing.JLabel();
        avgTraversalLabel = new javax.swing.JLabel();
        avgTraversalData = new javax.swing.JLabel();
        closeButton = new javax.swing.JButton();
        jGraph = new JPanelGraph();
        diagramNullLabel = new javax.swing.JLabel();
        diagramMaxVal = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("internationalization/Bundle"); // NOI18N
        setTitle(bundle.getString("ShowReceiverDialog.title")); // NOI18N

        senderIDLabel.setText(bundle.getString("ShowReceiverDialog.senderIDLabel.text")); // NOI18N

        senderIDData.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        senderIDData.setText(bundle.getString("ShowReceiverDialog.senderIDData.text")); // NOI18N

        interfaceLabel.setText(bundle.getString("ShowReceiverDialog.interfaceLabel.text")); // NOI18N

        interfaceData.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        interfaceData.setText(bundle.getString("ShowReceiverDialog.interfaceData.text")); // NOI18N

        groupLabel.setText(bundle.getString("ShowReceiverDialog.groupLabel.text")); // NOI18N

        groupData.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        groupData.setText(bundle.getString("ShowReceiverDialog.groupData.text")); // NOI18N

        packetStyleLabel.setText(bundle.getString("ShowReceiverDialog.packetStyleLabel.text")); // NOI18N

        packetStyleData.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        packetStyleData.setText(bundle.getString("ShowReceiverDialog.packetStyleData.text")); // NOI18N

        portLabel.setText(bundle.getString("ShowReceiverDialog.portLabel.text")); // NOI18N

        portData.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        portData.setText(bundle.getString("ShowReceiverDialog.portData.text")); // NOI18N

        dataData.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        dataData.setText(bundle.getString("ShowReceiverDialog.dataData.text")); // NOI18N

        dataLabel.setText(bundle.getString("ShowReceiverDialog.dataLabel.text")); // NOI18N

        packetSizeData.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        packetSizeData.setText(bundle.getString("ShowReceiverDialog.packetSizeData.text")); // NOI18N

        packetSizeLabel.setText(bundle.getString("ShowReceiverDialog.packetSizeLabel.text")); // NOI18N

        confPPSData.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        confPPSData.setText(bundle.getString("ShowReceiverDialog.confPPSData.text")); // NOI18N

        senderPPSLabel.setText(bundle.getString("ShowReceiverDialog.senderPPSLabel.text")); // NOI18N

        senderPPSData.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        senderPPSData.setText(bundle.getString("ShowReceiverDialog.senderPPSData.text")); // NOI18N

        confPPSLabel.setText(bundle.getString("ShowReceiverDialog.confPPSLabel.text")); // NOI18N

        receiverPPSLabel.setText(bundle.getString("ShowReceiverDialog.receiverPPSLabel.text")); // NOI18N

        receiverPPSData.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        receiverPPSData.setText(bundle.getString("ShowReceiverDialog.receiverPPSData.text")); // NOI18N

        lostPacketsLabel.setText(bundle.getString("ShowReceiverDialog.lostPacketsLabel.text")); // NOI18N

        lostPacketsData.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lostPacketsData.setText(bundle.getString("ShowReceiverDialog.lostPacketsData.text")); // NOI18N

        maxDelayLabel.setText(bundle.getString("ShowReceiverDialog.maxDelayLabel.text")); // NOI18N

        maxDelayData.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        maxDelayData.setText(bundle.getString("ShowReceiverDialog.maxDelayData.text")); // NOI18N

        avgTraversalLabel.setText(bundle.getString("ShowReceiverDialog.avgTraversalLabel.text")); // NOI18N

        avgTraversalData.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        avgTraversalData.setText(bundle.getString("ShowReceiverDialog.avgTraversalData.text")); // NOI18N

        closeButton.setText(bundle.getString("ShowReceiverDialog.closeButton.text")); // NOI18N
        closeButton.setActionCommand(bundle.getString("ShowReceiverDialog.closeButton.actionCommand")); // NOI18N
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        jGraph.setBackground(new java.awt.Color(0, 0, 0));

        javax.swing.GroupLayout jGraphLayout = new javax.swing.GroupLayout(jGraph);
        jGraph.setLayout(jGraphLayout);
        jGraphLayout.setHorizontalGroup(
            jGraphLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 341, Short.MAX_VALUE)
        );
        jGraphLayout.setVerticalGroup(
            jGraphLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 132, Short.MAX_VALUE)
        );

        diagramNullLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        diagramNullLabel.setText(bundle.getString("ShowReceiverDialog.diagramNullLabel.text")); // NOI18N

        diagramMaxVal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        diagramMaxVal.setText(bundle.getString("ShowReceiverDialog.diagramMaxVal.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                            .addGap(64, 64, 64)
                                            .addComponent(diagramNullLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(diagramMaxVal, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jGraph, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(senderIDLabel)
                                    .addGap(18, 18, 18)
                                    .addComponent(senderIDData, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addContainerGap())
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(interfaceData, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 436, Short.MAX_VALUE)
                                .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 436, Short.MAX_VALUE))
                            .addContainerGap())
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jSeparator2, javax.swing.GroupLayout.DEFAULT_SIZE, 436, Short.MAX_VALUE)
                            .addContainerGap())
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(packetSizeLabel)
                                        .addComponent(dataLabel)
                                        .addComponent(confPPSLabel)
                                        .addComponent(senderPPSLabel)
                                        .addComponent(receiverPPSLabel)
                                        .addComponent(lostPacketsLabel)
                                        .addComponent(maxDelayLabel)
                                        .addComponent(avgTraversalLabel))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(packetSizeData, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE)
                                        .addComponent(dataData, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE)
                                        .addComponent(confPPSData, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE)
                                        .addComponent(senderPPSData, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE)
                                        .addComponent(receiverPPSData, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE)
                                        .addComponent(lostPacketsData, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE)
                                        .addComponent(maxDelayData, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE)
                                        .addComponent(avgTraversalData, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(packetStyleLabel)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(packetStyleData, javax.swing.GroupLayout.DEFAULT_SIZE, 329, Short.MAX_VALUE)))
                            .addContainerGap())
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(groupLabel)
                                        .addComponent(groupData, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(42, 42, 42)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(portLabel)
                                        .addComponent(portData, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addComponent(interfaceLabel, javax.swing.GroupLayout.Alignment.LEADING))
                            .addContainerGap(115, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(closeButton)
                        .addGap(120, 120, 120))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jGraph, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(diagramMaxVal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(diagramNullLabel)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(senderIDLabel)
                    .addComponent(senderIDData))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(groupLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(groupData))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(portLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(portData)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(interfaceLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(interfaceData)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(packetStyleLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(packetSizeLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dataLabel))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(packetStyleData)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(packetSizeData)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dataData)))
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(confPPSLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(senderPPSLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(receiverPPSLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lostPacketsLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(maxDelayLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(avgTraversalLabel))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(confPPSData)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(senderPPSData)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(receiverPPSData)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lostPacketsData)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(maxDelayData)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(avgTraversalData)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(closeButton)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    /**
     * Button to handle close event
     *
     * @param evt Click-event
     */
    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        this.dispose();
    }//GEN-LAST:event_closeButtonActionPerformed

    /**
     * Loads and updates data in the UI Dialog
     */
    private void loadData(){

        //load address and interface name and display them on UI
        for (InterfaceAddress interfaceAddress : this.receivergroup.getNetworkInterface().getInterfaceAddresses()) {
            InetAddress address = interfaceAddress.getAddress();
            String ip = null;

            if(this.receivergroup.getGroup() instanceof Inet4Address){
                if(address instanceof Inet4Address){
                    ip = address.getHostAddress();
                }
            }
            else if(this.receivergroup.getGroup() instanceof Inet6Address){
                if(address instanceof Inet6Address){
                    ip = address.getHostAddress();
                }
            }

            this.interfaceData.setText(this.receivergroup.getNetworkInterface().getDisplayName() + " - " + ip);
        }

        //load all data from corresponding receiver/receivergroup and insert them to UI dialog
        this.senderIDData.setText(String.valueOf(this.receiver.getSenderId()));
        this.packetStyleData.setText(this.receiver.getPacketType().getDisplayName());
        this.packetSizeData.setText(String.valueOf(this.receiver.getPacketSize()));
        this.dataData.setText(this.receiver.getPayloadAsString());
        this.confPPSData.setText(String.valueOf(this.receiver.getSenderConfiguredPPS()));
        this.senderPPSData.setText(String.valueOf(this.receiver.getSenderMeasuredPPS()));
        this.receiverPPSData.setText(String.valueOf(this.receiver.getAvgPPS()));
        this.lostPacketsData.setText(String.valueOf(this.receiver.getLostPackets()));
        this.maxDelayData.setText(String.valueOf(this.receiver.getMaxDelay()) + " ms");
        if(this.receiver.getPacketType().toString().equals("SPAM")){
            this.avgTraversalData.setText(String.valueOf(this.receiver.getAvgTraversal()) + " ms");
        }
        else{
            this.avgTraversalData.setText("-");
        }
        this.groupData.setText(this.receivergroup.getGroup().getHostAddress());
        this.portData.setText(String.valueOf(this.receivergroup.getPort()));
        this.diagramMaxVal.setText(String.valueOf(this.receiver.getSenderConfiguredPPS()));

        //initialize graph packetRate and insert new measured value
        this.jGraph.setMaxPacketRate((int)this.receiver.getSenderConfiguredPPS());
        this.jGraph.newVal((int)this.receiver.getAvgPPS());
        
    }

   /**
    * Function to be executed on change event
    *
    * @param e ReceiverDataChangedEvent thrown on every update of the senderData
    */
    public void dataChanged(ReceiverDataChangedEvent e) {
        loadData();
    }
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel avgTraversalData;
    private javax.swing.JLabel avgTraversalLabel;
    private javax.swing.JButton closeButton;
    private javax.swing.JLabel confPPSData;
    private javax.swing.JLabel confPPSLabel;
    private javax.swing.JLabel dataData;
    private javax.swing.JLabel dataLabel;
    private javax.swing.JLabel diagramMaxVal;
    private javax.swing.JLabel diagramNullLabel;
    private javax.swing.JLabel groupData;
    private javax.swing.JLabel groupLabel;
    private javax.swing.JLabel interfaceData;
    private javax.swing.JLabel interfaceLabel;
    private com.spam.mctool.view.dialogs.JPanelGraph jGraph;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lostPacketsData;
    private javax.swing.JLabel lostPacketsLabel;
    private javax.swing.JLabel maxDelayData;
    private javax.swing.JLabel maxDelayLabel;
    private javax.swing.JLabel packetSizeData;
    private javax.swing.JLabel packetSizeLabel;
    private javax.swing.JLabel packetStyleData;
    private javax.swing.JLabel packetStyleLabel;
    private javax.swing.JLabel portData;
    private javax.swing.JLabel portLabel;
    private javax.swing.JLabel receiverPPSData;
    private javax.swing.JLabel receiverPPSLabel;
    private javax.swing.JLabel senderIDData;
    private javax.swing.JLabel senderIDLabel;
    private javax.swing.JLabel senderPPSData;
    private javax.swing.JLabel senderPPSLabel;
    // End of variables declaration//GEN-END:variables

}

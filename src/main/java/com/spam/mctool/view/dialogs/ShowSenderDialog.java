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

import com.spam.mctool.intermediates.SenderDataChangedEvent;
import com.spam.mctool.model.Sender;
import com.spam.mctool.model.SenderDataChangeListener;
import com.spam.mctool.view.main.MainFrame;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import javax.swing.JFrame;

/**
 * Dialog to show sender statistics
 *
 * @author Tobias Schoknecht (tobias.schoknecht@gmail.com)
 */
public class ShowSenderDialog extends javax.swing.JDialog implements SenderDataChangeListener{

    private static final long serialVersionUID = 1L;
    private Sender sender;
    private MainFrame parent;

    /**
     * Main constructor
     *
     * @param parent Reference to the parent window
     * @param modal Currently not used
     */
    public ShowSenderDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    /**
     * Chained constructor to assign parent reference and to typecast parent to jframe
     *
     * @param parent Reference to the parent MainFrame
     * @param modal Currently not used
     */
    private ShowSenderDialog(MainFrame parent, boolean modal) {
        this((JFrame)parent, modal);
        this.parent = parent;
    }

    /**
     * Constructor to be called
     *
     * @param parent Reference to the parent MainFrame
     * @param modal Currently not used
     * @param sender Reference to the sender for which statistics are to be shown
     */
    public ShowSenderDialog(MainFrame parent, boolean modal, Sender sender) {
        this(parent, modal);
        this.sender = sender;
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
        portLabel = new javax.swing.JLabel();
        portData = new javax.swing.JLabel();
        packetStyleLabel = new javax.swing.JLabel();
        packetStyleData = new javax.swing.JLabel();
        packetSizeLabel = new javax.swing.JLabel();
        packetSizeData = new javax.swing.JLabel();
        dataLabel = new javax.swing.JLabel();
        dataData = new javax.swing.JLabel();
        confPPSLabel = new javax.swing.JLabel();
        confPPSData = new javax.swing.JLabel();
        sentPPSLabel = new javax.swing.JLabel();
        sentPPSData = new javax.swing.JLabel();
        closeButton = new javax.swing.JButton();
        jGraph = new JPanelGraph();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        diagramNullLabel = new javax.swing.JLabel();
        diagramMaxVal = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("internationalization/Bundle"); // NOI18N
        setTitle(bundle.getString("ShowSenderDialog.title")); // NOI18N
        setName("Form"); // NOI18N

        senderIDLabel.setText(bundle.getString("ShowSenderDialog.senderIDLabel.text")); // NOI18N
        senderIDLabel.setName("senderIDLabel"); // NOI18N

        senderIDData.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        senderIDData.setText(bundle.getString("ShowSenderDialog.senderIDData.text")); // NOI18N
        senderIDData.setName("senderIDData"); // NOI18N

        interfaceLabel.setText(bundle.getString("ShowSenderDialog.interfaceLabel.text")); // NOI18N
        interfaceLabel.setName("interfaceLabel"); // NOI18N

        interfaceData.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        interfaceData.setText(bundle.getString("ShowSenderDialog.interfaceData.text")); // NOI18N
        interfaceData.setName("interfaceData"); // NOI18N

        groupLabel.setText(bundle.getString("ShowSenderDialog.groupLabel.text")); // NOI18N
        groupLabel.setName("groupLabel"); // NOI18N

        groupData.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        groupData.setText(bundle.getString("ShowSenderDialog.groupData.text")); // NOI18N
        groupData.setName("groupData"); // NOI18N

        portLabel.setText(bundle.getString("ShowSenderDialog.portLabel.text")); // NOI18N
        portLabel.setName("portLabel"); // NOI18N

        portData.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        portData.setText(bundle.getString("ShowSenderDialog.portData.text")); // NOI18N
        portData.setName("portData"); // NOI18N

        packetStyleLabel.setText(bundle.getString("ShowSenderDialog.packetStyleLabel.text")); // NOI18N
        packetStyleLabel.setName("packetStyleLabel"); // NOI18N

        packetStyleData.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        packetStyleData.setText(bundle.getString("ShowSenderDialog.packetStyleData.text")); // NOI18N
        packetStyleData.setName("packetStyleData"); // NOI18N

        packetSizeLabel.setText(bundle.getString("ShowSenderDialog.packetSizeLabel.text")); // NOI18N
        packetSizeLabel.setName("packetSizeLabel"); // NOI18N

        packetSizeData.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        packetSizeData.setText(bundle.getString("ShowSenderDialog.packetSizeData.text")); // NOI18N
        packetSizeData.setName("packetSizeData"); // NOI18N

        dataLabel.setText(bundle.getString("ShowSenderDialog.dataLabel.text")); // NOI18N
        dataLabel.setName("dataLabel"); // NOI18N

        dataData.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        dataData.setText(bundle.getString("ShowSenderDialog.dataData.text")); // NOI18N
        dataData.setName("dataData"); // NOI18N

        confPPSLabel.setText(bundle.getString("ShowSenderDialog.confPPSLabel.text")); // NOI18N
        confPPSLabel.setName("confPPSLabel"); // NOI18N

        confPPSData.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        confPPSData.setText(bundle.getString("ShowSenderDialog.confPPSData.text")); // NOI18N
        confPPSData.setName("confPPSData"); // NOI18N

        sentPPSLabel.setText(bundle.getString("ShowSenderDialog.sentPPSLabel.text")); // NOI18N
        sentPPSLabel.setName("sentPPSLabel"); // NOI18N

        sentPPSData.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        sentPPSData.setText(bundle.getString("ShowSenderDialog.sentPPSData.text")); // NOI18N
        sentPPSData.setName("sentPPSData"); // NOI18N

        closeButton.setText(bundle.getString("ShowSenderDialog.closeButton.text")); // NOI18N
        closeButton.setActionCommand(bundle.getString("ShowSenderDialog.closeButton.actionCommand")); // NOI18N
        closeButton.setName("closeButton"); // NOI18N
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        jGraph.setBackground(new java.awt.Color(0, 0, 0));
        jGraph.setName("jGraph"); // NOI18N

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

        jSeparator1.setName("jSeparator1"); // NOI18N

        jSeparator2.setName("jSeparator2"); // NOI18N

        diagramNullLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        diagramNullLabel.setText(bundle.getString("ShowSenderDialog.diagramNullLabel.text")); // NOI18N
        diagramNullLabel.setName("diagramNullLabel"); // NOI18N

        diagramMaxVal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        diagramMaxVal.setText(bundle.getString("ShowSenderDialog.diagramMaxVal.text")); // NOI18N
        diagramMaxVal.setName("diagramMaxVal"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(senderIDLabel)
                                .addGap(18, 18, 18)
                                .addComponent(senderIDData, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(groupLabel)
                                        .addGap(115, 115, 115))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(groupData, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGap(18, 18, 18)))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(portData, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
                                    .addComponent(portLabel))
                                .addGap(156, 156, 156))
                            .addComponent(interfaceLabel)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(confPPSLabel)
                                    .addComponent(packetSizeLabel)
                                    .addComponent(dataLabel)
                                    .addComponent(packetStyleLabel)
                                    .addComponent(sentPPSLabel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(packetSizeData)
                                    .addComponent(dataData, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(confPPSData, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(sentPPSData, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(packetStyleData, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(interfaceData, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                            .addGap(64, 64, 64)
                                            .addComponent(diagramNullLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(diagramMaxVal, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jGraph, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.LEADING)))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(closeButton)
                        .addGap(161, 161, 161))))
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
                        .addComponent(groupData)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(interfaceLabel))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(portLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(portData)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(interfaceData)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.DEFAULT_SIZE, 10, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(packetStyleLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(packetSizeLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dataLabel)
                        .addGap(18, 18, 18)
                        .addComponent(confPPSLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sentPPSLabel))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(packetStyleData)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(packetSizeData)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(sentPPSData)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(dataData)
                                .addGap(18, 18, 18)
                                .addComponent(confPPSData)
                                .addGap(24, 24, 24)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
    private void loadData() {

        //set the ip address and the NetworkInterface in the corresponding UI field
        for (InterfaceAddress interfaceAddress : this.sender.getNetworkInterface().getInterfaceAddresses()) {
            InetAddress address = interfaceAddress.getAddress();
            String ip = null;

            if(this.sender.getGroup() instanceof Inet4Address){
                if(address instanceof Inet4Address){
                    ip = address.getHostAddress();
                }
            }
            else if(this.sender.getGroup() instanceof Inet6Address){
                if(address instanceof Inet6Address){
                    ip = address.getHostAddress();
                }
            }

            this.interfaceData.setText(this.sender.getNetworkInterface().getDisplayName() + " - " + ip);
        }

        //load all data from sender and insert them to the UI dialog
        this.senderIDData.setText(String.valueOf(this.sender.getSenderId()));
        this.groupData.setText(this.sender.getGroup().getHostAddress());
        this.portData.setText(String.valueOf(this.sender.getPort()));
        this.packetStyleData.setText(this.sender.getpType().getDisplayName());
        this.packetSizeData.setText(String.valueOf(this.sender.getPacketSize())+ " Byte");
        this.dataData.setText(this.sender.getPayloadAsString());
        this.confPPSData.setText(String.valueOf(this.sender.getSenderConfiguredPacketRate()));
        this.sentPPSData.setText(String.valueOf(this.sender.getAvgPPS()));
        this.diagramMaxVal.setText(String.valueOf(this.sender.getSenderConfiguredPacketRate()));
        
        //initialize graph packetRate and insert new measured value on update
        this.jGraph.setMaxPacketRate(this.sender.getSenderConfiguredPacketRate());
        this.jGraph.newVal((int)this.sender.getAvgPPS());

    }

   /**
    * Function to be executed on change event
    *
    * @param e SenderDataChangedEvent thrown on every update of the senderData
    */
    public void dataChanged(SenderDataChangedEvent e) {
        loadData();
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
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
    private javax.swing.JLabel packetSizeData;
    private javax.swing.JLabel packetSizeLabel;
    private javax.swing.JLabel packetStyleData;
    private javax.swing.JLabel packetStyleLabel;
    private javax.swing.JLabel portData;
    private javax.swing.JLabel portLabel;
    private javax.swing.JLabel senderIDData;
    private javax.swing.JLabel senderIDLabel;
    private javax.swing.JLabel sentPPSData;
    private javax.swing.JLabel sentPPSLabel;
    // End of variables declaration//GEN-END:variables

}

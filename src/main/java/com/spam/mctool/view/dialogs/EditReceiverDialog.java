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

import com.spam.mctool.model.ReceiverGroup;
import com.spam.mctool.view.main.MainFrame;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

/**
 *
 * @author Tobias Schoknecht (Tobias.Schoknecht@de.ibm.com)
 */
public class EditReceiverDialog extends javax.swing.JDialog {

    private static final long serialVersionUID = 1L;
    private ReceiverGroup receiverGroup = null;
    private Map<String,String> interfaceMap = new HashMap<String, String>();
    private Map<String,String> analyzingBehaviourMap = new HashMap<String, String>();
    private MainFrame parent;

	/** Creates new form EditReceiverDialog */
    private EditReceiverDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        loadNetInterfaces();
        initComboBoxes();
    }
    
    public EditReceiverDialog(MainFrame parent, boolean modal) {
        this((JFrame)parent, modal);
        this.parent = parent;
    }

    public EditReceiverDialog(MainFrame parent, boolean modal, ReceiverGroup receiverGroup, boolean create) {
        this(parent, modal);
        this.receiverGroup = receiverGroup;
        loadData(create);

        if(create){
            this.receiverGroup = null;
        }
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
        InterfaceLabel = new javax.swing.JLabel();
        InterfaceCombo = new javax.swing.JComboBox();
        ActivateBox = new javax.swing.JCheckBox();
        OKButton = new javax.swing.JButton();
        CancelButton = new javax.swing.JButton();
        PortField = new javax.swing.JSpinner();
        AnalyzingBehaviourLabel = new javax.swing.JLabel();
        AnalyzingBehaviourCombo = new javax.swing.JComboBox();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("internationalization/Bundle"); // NOI18N
        GroupLabel.setText(bundle.getString("EditReceiverDialog.GroupLabel.text")); // NOI18N

        PortLabel.setText(bundle.getString("EditReceiverDialog.PortLabel.text")); // NOI18N

        InterfaceLabel.setText(bundle.getString("EditReceiverDialog.InterfaceLabel.text")); // NOI18N

        InterfaceCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        ActivateBox.setText(bundle.getString("EditReceiverDialog.ActivateBox.text")); // NOI18N

        OKButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/check.png"))); // NOI18N
        OKButton.setText(bundle.getString("EditReceiverDialog.OKButton.text")); // NOI18N
        OKButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OKButtonActionPerformed(evt);
            }
        });

        CancelButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/delete.png"))); // NOI18N
        CancelButton.setText(bundle.getString("EditReceiverDialog.CancelButton.text")); // NOI18N
        CancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CancelButtonActionPerformed(evt);
            }
        });

        AnalyzingBehaviourLabel.setText(bundle.getString("EditReceiverDialog.AnalyzingBehaviourLabel.text")); // NOI18N

        AnalyzingBehaviourCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 520, Short.MAX_VALUE)
                            .addContainerGap())
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(GroupField, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(GroupLabel))
                                    .addGap(18, 18, 18)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(PortLabel)
                                        .addComponent(PortField, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)))
                                .addComponent(InterfaceLabel))
                            .addContainerGap())
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(InterfaceCombo, 0, 252, Short.MAX_VALUE)
                            .addGap(280, 280, 280))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(AnalyzingBehaviourLabel)
                            .addContainerGap(393, Short.MAX_VALUE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(AnalyzingBehaviourCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addContainerGap(402, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addComponent(jSeparator2, javax.swing.GroupLayout.DEFAULT_SIZE, 520, Short.MAX_VALUE)
                            .addContainerGap())
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addComponent(ActivateBox, javax.swing.GroupLayout.DEFAULT_SIZE, 520, Short.MAX_VALUE)
                            .addContainerGap()))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(OKButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(CancelButton)
                        .addGap(229, 229, 229))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(GroupLabel)
                    .addComponent(PortLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(GroupField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(PortField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(InterfaceLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(InterfaceCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(AnalyzingBehaviourLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(AnalyzingBehaviourCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ActivateBox)
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(OKButton)
                    .addComponent(CancelButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void OKButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OKButtonActionPerformed
        Map<String,String> receiverMap = new HashMap<String, String>();

        if(this.receiverGroup == null){
            receiverMap.put("group", this.GroupField.getText());
            receiverMap.put("port", this.PortField.getValue().toString());
            receiverMap.put("ninf",this.interfaceMap.get(this.InterfaceCombo.getSelectedItem().toString()));
            receiverMap.put("abeh",this.analyzingBehaviourMap.get(this.AnalyzingBehaviourCombo.getSelectedItem().toString()));
            parent.addReceiverGroup(receiverMap, ActivateBox.isSelected());
        }
        else{
            if(this.ActivateBox.isSelected()){
                this.receiverGroup.activate();
            }
        }
        this.dispose();
    }//GEN-LAST:event_OKButtonActionPerformed

    private void CancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CancelButtonActionPerformed
        this.dispose();
    }//GEN-LAST:event_CancelButtonActionPerformed

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

    private void loadData(boolean create){
        this.GroupField.setText(this.receiverGroup.getGroup().toString());
        this.PortField.setValue(this.receiverGroup.getPort());

        for (InterfaceAddress interfaceAddress : this.receiverGroup.getNetworkInterface().getInterfaceAddresses()) {
            InetAddress address = interfaceAddress.getAddress();
            String ip = null;

            if(this.receiverGroup.getGroup() instanceof Inet4Address){
                if(address instanceof Inet4Address){
                    ip = address.getHostAddress();
                }
            }
            else if(this.receiverGroup.getGroup() instanceof Inet6Address){
                if(address instanceof Inet6Address){
                    ip = address.getHostAddress();
                }
            }

            this.InterfaceCombo.setSelectedItem(this.receiverGroup.getNetworkInterface().getDisplayName() + " - " + ip);
        }

        Iterator it = analyzingBehaviourMap.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry entry = (Map.Entry)it.next();

            if(entry.getValue().equals(this.receiverGroup.getAnalyzingBehaviour().getIdentifier())){
                this.AnalyzingBehaviourCombo.setSelectedItem(entry.getKey());
            }
        }

        if(!create){
            this.GroupField.setEnabled(false);
            this.PortField.setEnabled(false);
            this.InterfaceCombo.setEnabled(false);
        }
    }

    private void loadNetInterfaces(){
        this.InterfaceCombo.removeAllItems();
	Enumeration<NetworkInterface> interfaces = null;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException ex) {
            Logger.getLogger(EditSenderDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();

            for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                InetAddress address = interfaceAddress.getAddress();

                this.InterfaceCombo.addItem(networkInterface.getDisplayName() + " - " + address.getHostAddress());
                this.interfaceMap.put(networkInterface.getDisplayName() + " - " + address.getHostAddress(), address.getHostAddress());
            }
        }
    }

    private void initComboBoxes(){
        this.analyzingBehaviourMap.put("Default","default");
        this.analyzingBehaviourMap.put("Lazy","lazy");
        this.analyzingBehaviourMap.put("Eager","eager");
        this.AnalyzingBehaviourCombo.removeAllItems();
        this.AnalyzingBehaviourCombo.addItem("Default");
        this.AnalyzingBehaviourCombo.addItem("Lazy");
        this.AnalyzingBehaviourCombo.addItem("Eager");
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox ActivateBox;
    private javax.swing.JComboBox AnalyzingBehaviourCombo;
    private javax.swing.JLabel AnalyzingBehaviourLabel;
    private javax.swing.JButton CancelButton;
    private javax.swing.JTextField GroupField;
    private javax.swing.JLabel GroupLabel;
    private javax.swing.JComboBox InterfaceCombo;
    private javax.swing.JLabel InterfaceLabel;
    private javax.swing.JButton OKButton;
    private javax.swing.JSpinner PortField;
    private javax.swing.JLabel PortLabel;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    // End of variables declaration//GEN-END:variables

}

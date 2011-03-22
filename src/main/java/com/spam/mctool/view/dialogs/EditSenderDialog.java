/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * EditSenderDialog.java
 *
 * Created on Mar 3, 2011, 7:47:26 PM
 */

package com.spam.mctool.view.dialogs;

import com.spam.mctool.model.Sender;
import com.spam.mctool.view.main.MainFrame;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFrame;

/**
 *
 * @author Tobias Schoknecht (Tobias.Schoknecht@de.ibm.com)
 */
public class EditSenderDialog extends javax.swing.JDialog {

    private static final long serialVersionUID = 1L;
    private Sender sender = null;
    private Map<String,String> interfaceMap = new HashMap<String, String>();
    private Map<String,String> packageMap = new HashMap<String, String>();
    private Map<String,String> analyzingBehaviourMap = new HashMap<String, String>();
    private MainFrame parent;
    //TODO loglevel?

	/** Creates new form EditSenderDialog */
    private EditSenderDialog(JFrame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        loadNetInterfaces();
        initComboBoxes();
    }

    public EditSenderDialog(MainFrame parent, boolean modal) {
        this((JFrame)parent, modal);
        this.parent = parent;
    }

    public EditSenderDialog(MainFrame parent, boolean modal, Sender sender) {
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

        jSpinner1 = new javax.swing.JSpinner();
        InterfaceCombo = new javax.swing.JComboBox();
        OKButton = new javax.swing.JButton();
        CancelButton = new javax.swing.JButton();
        InterfaceLabel = new javax.swing.JLabel();
        PortLabel = new javax.swing.JLabel();
        GroupLabel = new javax.swing.JLabel();
        ActivateBox = new javax.swing.JCheckBox();
        DataLabel = new javax.swing.JLabel();
        DataField = new javax.swing.JTextField();
        PacketRateLabel = new javax.swing.JLabel();
        PacketSizeLabel = new javax.swing.JLabel();
        TTLLabel = new javax.swing.JLabel();
        PacketStyleLabel = new javax.swing.JLabel();
        PacketStyleCombo = new javax.swing.JComboBox();
        GroupField = new javax.swing.JFormattedTextField();
        PortField = new javax.swing.JSpinner();
        PacketRateField = new javax.swing.JSpinner();
        PacketSizeField = new javax.swing.JSpinner();
        TTLField = new javax.swing.JSpinner();
        AnalyzingBehaviourLabel = new javax.swing.JLabel();
        AnalyzingBehaviourCombo = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        OKButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/check.png"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("internationalization/Bundle"); // NOI18N
        OKButton.setText(bundle.getString("EditSenderDialog.OKButton.text")); // NOI18N
        OKButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OKButtonActionPerformed(evt);
            }
        });

        CancelButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/delete.png"))); // NOI18N
        CancelButton.setText(bundle.getString("EditSenderDialog.CancelButton.text")); // NOI18N
        CancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CancelButtonActionPerformed(evt);
            }
        });

        InterfaceLabel.setText(bundle.getString("EditSenderDialog.InterfaceLabel.text")); // NOI18N

        PortLabel.setText(bundle.getString("EditSenderDialog.PortLabel.text")); // NOI18N

        GroupLabel.setText(bundle.getString("EditSenderDialog.GroupLabel.text")); // NOI18N

        ActivateBox.setText(bundle.getString("EditSenderDialog.ActivateBox.text")); // NOI18N

        DataLabel.setText(bundle.getString("EditSenderDialog.DataLabel.text")); // NOI18N

        PacketRateLabel.setText(bundle.getString("EditSenderDialog.PacketRateLabel.text")); // NOI18N

        PacketSizeLabel.setText(bundle.getString("EditSenderDialog.PacketSizeLabel.text")); // NOI18N

        TTLLabel.setText(bundle.getString("EditSenderDialog.TTLLabel.text")); // NOI18N

        PacketStyleLabel.setText(bundle.getString("EditSenderDialog.PacketStyleLabel.text")); // NOI18N

        AnalyzingBehaviourLabel.setText(bundle.getString("EditSenderDialog.AnalyzingBehaviourLabel.text")); // NOI18N

        AnalyzingBehaviourCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(GroupLabel)
                    .addComponent(PortLabel)
                    .addComponent(GroupField, javax.swing.GroupLayout.DEFAULT_SIZE, 501, Short.MAX_VALUE)
                    .addComponent(PortField, javax.swing.GroupLayout.DEFAULT_SIZE, 501, Short.MAX_VALUE)
                    .addComponent(InterfaceLabel)
                    .addComponent(InterfaceCombo, 0, 501, Short.MAX_VALUE)
                    .addComponent(DataField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 501, Short.MAX_VALUE)
                    .addComponent(DataLabel)
                    .addComponent(PacketStyleLabel)
                    .addComponent(PacketStyleCombo, 0, 501, Short.MAX_VALUE)
                    .addComponent(PacketRateLabel)
                    .addComponent(PacketRateField, javax.swing.GroupLayout.DEFAULT_SIZE, 501, Short.MAX_VALUE)
                    .addComponent(PacketSizeLabel)
                    .addComponent(PacketSizeField, javax.swing.GroupLayout.DEFAULT_SIZE, 501, Short.MAX_VALUE)
                    .addComponent(TTLLabel)
                    .addComponent(TTLField, javax.swing.GroupLayout.DEFAULT_SIZE, 501, Short.MAX_VALUE)
                    .addComponent(AnalyzingBehaviourLabel)
                    .addComponent(AnalyzingBehaviourCombo, 0, 501, Short.MAX_VALUE)
                    .addComponent(ActivateBox, javax.swing.GroupLayout.DEFAULT_SIZE, 501, Short.MAX_VALUE)
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PortLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PortField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(InterfaceLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(InterfaceCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(DataLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(DataField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(PacketStyleLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PacketStyleCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(PacketRateLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PacketRateField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(PacketSizeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PacketSizeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(TTLLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(TTLField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(AnalyzingBehaviourLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(AnalyzingBehaviourCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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

    private void OKButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OKButtonActionPerformed
        Map<String,String> senderMap = new HashMap<String, String>();

        if(this.sender == null){
            senderMap.put("group", this.GroupField.getText());
            senderMap.put("port", this.PortField.getValue().toString());
            senderMap.put("pps", this.PacketRateField.getValue().toString());
            senderMap.put("psize", this.PacketSizeField.getValue().toString());
            senderMap.put("ttl", this.TTLField.getValue().toString());
            senderMap.put("payload", this.DataField.getText());
            senderMap.put("ptype", this.packageMap.get(this.PacketStyleCombo.getSelectedItem().toString()));
            senderMap.put("ninf",this.interfaceMap.get(this.InterfaceCombo.getSelectedItem().toString()));
            senderMap.put("abeh",this.analyzingBehaviourMap.get(this.AnalyzingBehaviourCombo.getSelectedItem().toString()));
            parent.addSender(senderMap, ActivateBox.isSelected());
        }
        else{
            this.sender.setSenderConfiguredPacketRate(Integer.parseInt(this.PacketRateField.getValue().toString()));
            this.sender.setPacketSize(Integer.parseInt(this.PacketSizeField.getValue().toString()));
            this.sender.setTtl(Byte.parseByte(this.TTLField.getValue().toString()));
            if(this.ActivateBox.isSelected()){
                this.sender.activate();
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
                EditSenderDialog dialog = new EditSenderDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    private void loadData(){
        this.GroupField.setText(this.sender.getGroup().getHostAddress());
        this.GroupField.setEnabled(false);
        this.PortField.setValue(this.sender.getPort());
        this.PortField.setEnabled(false);
        this.DataField.setText(this.sender.getPayloadAsString());
        this.DataField.setEnabled(false);
        this.PacketRateField.setValue(this.sender.getSenderConfiguredPacketRate());
        this.PacketSizeField.setValue(this.sender.getPacketSize());
        this.InterfaceCombo.setEnabled(false);
        this.TTLField.setValue(this.sender.getTtl());
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

                Pattern p = Pattern.compile("(([1-9][0-9]{0,2}|0)\\.([1-9][0-9]{0,2}|0)\\.([1-9][0-9]{0,2}|0)\\.([1-9][0-9]{0,2}|0))");
		Matcher m = p.matcher(address.getHostAddress());

                if(m.matches()){
                    this.InterfaceCombo.addItem(networkInterface.getDisplayName() + " - " + address.getHostAddress());
                    this.interfaceMap.put(networkInterface.getDisplayName() + " - " + address.getHostAddress(),address.getHostAddress());
                }
            }
        }       
    }

    private void initComboBoxes(){
        this.packageMap.put("SPAM","spam");
        this.packageMap.put("Hirschmann","hmann");
        this.PacketStyleCombo.removeAllItems();
        this.PacketStyleCombo.addItem("SPAM");
        this.PacketStyleCombo.addItem("Hirschmann");

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
    private javax.swing.JTextField DataField;
    private javax.swing.JLabel DataLabel;
    private javax.swing.JFormattedTextField GroupField;
    private javax.swing.JLabel GroupLabel;
    private javax.swing.JComboBox InterfaceCombo;
    private javax.swing.JLabel InterfaceLabel;
    private javax.swing.JButton OKButton;
    private javax.swing.JSpinner PacketRateField;
    private javax.swing.JLabel PacketRateLabel;
    private javax.swing.JSpinner PacketSizeField;
    private javax.swing.JLabel PacketSizeLabel;
    private javax.swing.JComboBox PacketStyleCombo;
    private javax.swing.JLabel PacketStyleLabel;
    private javax.swing.JSpinner PortField;
    private javax.swing.JLabel PortLabel;
    private javax.swing.JSpinner TTLField;
    private javax.swing.JLabel TTLLabel;
    private javax.swing.JSpinner jSpinner1;
    // End of variables declaration//GEN-END:variables

}

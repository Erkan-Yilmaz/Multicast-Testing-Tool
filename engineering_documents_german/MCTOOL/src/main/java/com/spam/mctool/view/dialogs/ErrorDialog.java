/*
 * ErrorDialog.java
 *
 * Created on 13.05.2011, 09:20:39
 */

package com.spam.mctool.view.dialogs;

import com.spam.mctool.controller.ErrorEvent;
import com.spam.mctool.controller.ErrorEventManager;



/**
 * Dialog displaying one or multiple error messages. Once made visible new messages
 * can be appended to the list via the <code>dataChanged</code> method.
 * @author Tobias
 */
public class ErrorDialog extends javax.swing.JDialog {

    
    
    /** Create and initialize the dialog */
    public ErrorDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }



    /**
     * Overrides JDialog's <code>setVisible</code> method in order to center the
     * dialog relative to its parent (usually the main frame).
     * @param visible true: show and center the dialog. false: hide the dialog
     */
    @Override
    public void setVisible(boolean visible) {
        com.spam.mctool.view.main.MainFrame parent = (com.spam.mctool.view.main.MainFrame)getParent();
        java.awt.Dimension dim = parent.getSize();
        java.awt.Point     loc = parent.getLocationOnScreen();

        java.awt.Dimension size = getSize();

        loc.x += (dim.width  - size.width)/2;
        loc.y += (dim.height - size.height)/2;

        if (loc.x < 0) loc.x = 0;
        if (loc.y < 0) loc.y = 0;

        java.awt.Dimension screen = getToolkit().getScreenSize();

        if (size.width  > screen.width)
          size.width  = screen.width;
        if (size.height > screen.height)
          size.height = screen.height;

        if (loc.x + size.width > screen.width)
          loc.x = screen.width - size.width;

        if (loc.y + size.height > screen.height)
          loc.y = screen.height - size.height;

        setBounds(loc.x, loc.y, size.width, size.height);
        super.setVisible(visible);
    }



    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        textArea = new javax.swing.JTextArea();
        okPanel = new javax.swing.JPanel();
        buOK = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("internationalization/Bundle"); // NOI18N
        setTitle(bundle.getString("ErrorDialog.title")); // NOI18N
        setMinimumSize(new java.awt.Dimension(717, 304));
        setName("Form"); // NOI18N
        getContentPane().setLayout(new java.awt.BorderLayout(10, 0));

        mainPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setLayout(new java.awt.BorderLayout(10, 10));

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        textArea.setColumns(20);
        textArea.setEditable(false);
        textArea.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        textArea.setRows(5);
        textArea.setName("textArea"); // NOI18N
        jScrollPane1.setViewportView(textArea);

        mainPanel.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        okPanel.setName("okPanel"); // NOI18N
        okPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        buOK.setText(bundle.getString("ErrorDialog.buOK.text")); // NOI18N
        buOK.setName("buOK"); // NOI18N
        buOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buOKActionPerformed(evt);
            }
        });
        okPanel.add(buOK);

        mainPanel.add(okPanel, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents



    /**
     * Hide the dialog and clear its message area for the next use.
     * @param evt
     */
    private void buOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buOKActionPerformed
        this.setVisible(false);
        textArea.setText(null);
    }//GEN-LAST:event_buOKActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buOK;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel okPanel;
    private javax.swing.JTextArea textArea;
    // End of variables declaration//GEN-END:variables



    /**
     * Inform about a new error event. The new error's message will be appended
     * to the dialog's log.
     * @param e The error event that has bee reported to the manager.
     */
    public void dataChanged(ErrorEvent e) {
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("internationalization/Bundle");

        String newMessage = "";

        switch(e.getErrorLevel()) {
            case ErrorEventManager.DEBUG:
                newMessage = bundle.getString("View.Error.debug.title") + ": ";
                break;
            case ErrorEventManager.WARNING:
                newMessage = bundle.getString("View.Error.warning.title") + ": ";
                break;
            case ErrorEventManager.SEVERE:
                newMessage = bundle.getString("View.Error.severe.title") + ": ";
                break;
            case ErrorEventManager.ERROR:
                newMessage = bundle.getString("View.Error.error.title") + ": ";
                break;
            case ErrorEventManager.CRITICAL:
                newMessage = bundle.getString("View.Error.critical.title") + ": ";
                break;
            case ErrorEventManager.FATAL:
                newMessage = bundle.getString("View.Error.fatal.title") + ": ";
                break;
            default:
                newMessage = bundle.getString("View.Error.unknown.title") + ": ";
                break;
        }

        newMessage += e.getCompleteMessage() + "\n";

        textArea.append(newMessage);
    }

}

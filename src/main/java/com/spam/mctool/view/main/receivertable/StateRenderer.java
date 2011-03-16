/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spam.mctool.view.main.receivertable;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Tobias Stöckel
 */
class StateRenderer extends JPanel implements TableCellRenderer {

    private JLabel laIcon = new JLabel();

    public StateRenderer() {
        this.setLayout(new BorderLayout());
        this.add(laIcon, BorderLayout.WEST);
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        boolean active = (Boolean)value;
        if(active) laIcon.setText(">");
        else             laIcon.setText("II");
        return this;
    }

}

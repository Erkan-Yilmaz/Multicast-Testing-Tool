/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spam.mctool.view.main.receivertable;

import com.spam.mctool.model.ReceiverGroup;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Tobias Stöckel
 */
class GroupRowRenderer extends JPanel implements TableCellRenderer {

    JLabel jLabel1 = new JLabel();
    private JTable table;
    private int column;

    public GroupRowRenderer() {
        this.setLayout(new BorderLayout());
        this.add(jLabel1, BorderLayout.WEST);
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        this.table = table;
        this.column = column;
        ReceiverGroupRow groupRow = (ReceiverGroupRow)value;
        ReceiverGroup group = (ReceiverGroup)groupRow.getReceiverGroup();
        if(groupRow.isExpanded()) {
            jLabel1.setText("v Group: " + group.getGroup());
        } else {
            jLabel1.setText("> Group: " + group.getGroup());
        }
        return this;
    }

    @Override
    public void setBounds(int x, int y, int w, int h) {
        super.setBounds(0, y, table.getWidth(), h);
    }

    @Override
    public void paint(Graphics g) {
        int columnWidths = 0;
        for(int i=0; i<column; i++) {
            columnWidths += table.getColumnModel().getColumn(i).getWidth();
        }
        g.translate(-columnWidths, 0);
        super.paint(g);
    }

}


package com.spam.mctool.view.main.receivertable;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;



/**
 * TableCellRenderer that displays a green arrow for a running receiver and an orange pause
 * symbol for an interrupted receiver
 * @author Tobias Stöckel
 */
class ReceiverStateRenderer extends JPanel implements TableCellRenderer {

    /**
     * the label containing the icon
     */
    private JLabel laIcon = new JLabel();

    /**
     * the green arrow
     */
    private ImageIcon alive = new ImageIcon(getClass().getResource("/images/play_green.png"));

    /**
     * the orange pause symbol
     */
    private ImageIcon dead  = new ImageIcon(getClass().getResource("/images/pause_orange.png"));

    
    
    /**
     * Creates and assembles the renderer
     */
    public ReceiverStateRenderer() {
        this.setLayout(new BorderLayout());
        this.add(laIcon, BorderLayout.WEST);
    }



    /**
     * Initializes the renderer to the passed values and returns it.
     */
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        boolean active = (Boolean)value;
        if(active) laIcon.setIcon(alive);
        else       laIcon.setIcon(dead);
        if (isSelected) {
            this.setBackground(table.getSelectionBackground());
            this.setForeground(table.getSelectionForeground());
        } else {
            this.setForeground(table.getForeground());
            if (row % 2 == 0) {
                this.setBackground(table.getBackground());
            } else {
                this.setBackground(table.getGridColor());
            }
        }
        return this;
    }

}

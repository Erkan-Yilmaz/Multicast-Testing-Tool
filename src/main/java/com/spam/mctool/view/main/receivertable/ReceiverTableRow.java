
package com.spam.mctool.view.main.receivertable;

/**
 * Abstract type for rows contained in a <code>ReceiverTableModel</code>.
 * @author Tobias Stöckel
 */
abstract class ReceiverTableRow {

    /**
     * Marks the row as visible in the view
     */
    private boolean visible = false;



    /**
     * Return whether the row is visible
     */
    public boolean isVisible() {
        return visible;
    }



    /**
     * Mark the row as visible or invisible
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

}

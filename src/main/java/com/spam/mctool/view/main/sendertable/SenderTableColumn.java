package com.spam.mctool.view.main.sendertable;

import java.util.ResourceBundle;

/**
 * Defines the columns, their captions and their tooltips as displayed by the
 * JReceiverTable.
 * @author Tobias
 */
public enum SenderTableColumn {

    /**
     * Column displaying a sender's status
     */
    STATUS        ("MainFrame.SenderTable.status",        "MainFrame.SenderTable.status.tooltip"        ),

    /**
     * Column displaying a sender's id
     */
    SENDER_ID     ("MainFrame.SenderTable.sender_id",     "MainFrame.SenderTable.sender_id.tooltip"     ),

    /**
     * Column displaying a sender's destination port
     */
    PORT          ("MainFrame.SenderTable.port",          "MainFrame.SenderTable.port.tooltip"          ),

    /**
     * Column displaying a sender's destination group address
     */
    GROUP_ADDRESS ("MainFrame.SenderTable.group_address", "MainFrame.SenderTable.group_address.tooltip" ),

    /**
     * Column displaying a sender's configured packet rate
     */
    CONF_PPS      ("MainFrame.SenderTable.conf_pps",      "MainFrame.SenderTable.conf_pps.tooltip"      ),
    AVG_PPS       ("MainFrame.SenderTable.avg_pps",       "MainFrame.SenderTable.avg_pps.tooltip"       ),
    MIN_PPS       ("MainFrame.SenderTable.min_pps",       "MainFrame.SenderTable.min_pps.tooltip"       ),
    MAX_PPS       ("MainFrame.SenderTable.max_pps",       "MainFrame.SenderTable.max_pps.tooltip"       );

    /**
     * the identifier for the internationalized caption
     */
    private String captionIdentifier;

    /**
     * the identifier for the internationalized tooltip
     */
    private String tooltipIdentifier;



    /**
     * create a column with the specified caption and tooltip
     */
    SenderTableColumn(String captionIdentifier, String tooltipIdentifier) {
        this.captionIdentifier = captionIdentifier;
        this.tooltipIdentifier = tooltipIdentifier;
    }



    /**
     * get the internationalzed caption of this column
     */
    String getCaption() {
        return ResourceBundle.getBundle("internationalization/Bundle").getString(captionIdentifier);
    }



    /**
     * get the internationalized tooltip of this column
     * @return
     */
    String getToolTip() {
        return ResourceBundle.getBundle("internationalization/Bundle").getString(tooltipIdentifier);
    }
}

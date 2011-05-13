/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spam.mctool.view.main.sendertable;

import java.util.ResourceBundle;

/**
 *
 * @author Tobias
 */
public enum SenderTableColumn {
    STATUS        ("MainFrame.SenderTable.status",        "MainFrame.SenderTable.status.tooltip"        ),
    SENDER_ID     ("MainFrame.SenderTable.sender_id",     "MainFrame.SenderTable.sender_id.tooltip"     ),
    PORT          ("MainFrame.SenderTable.port",          "MainFrame.SenderTable.port.tooltip"          ),
    GROUP_ADDRESS ("MainFrame.SenderTable.group_address", "MainFrame.SenderTable.group_address.tooltip" ),
    CONF_PPS      ("MainFrame.SenderTable.conf_pps",      "MainFrame.SenderTable.conf_pps.tooltip"      ),
    AVG_PPS       ("MainFrame.SenderTable.avg_pps",       "MainFrame.SenderTable.avg_pps.tooltip"       ),
    MIN_PPS       ("MainFrame.SenderTable.min_pps",       "MainFrame.SenderTable.min_pps.tooltip"       ),
    MAX_PPS       ("MainFrame.SenderTable.max_pps",       "MainFrame.SenderTable.max_pps.tooltip"       );

    private String captionIdentifier;
    private String tooltipIdentifier;

    SenderTableColumn(String captionIdentifier, String tooltipIdentifier) {
        this.captionIdentifier = captionIdentifier;
        this.tooltipIdentifier = tooltipIdentifier;
    }

    String getCaption() {
        return ResourceBundle.getBundle("internationalization/Bundle").getString(captionIdentifier);
    }

    String getToolTip() {
        return ResourceBundle.getBundle("internationalization/Bundle").getString(tooltipIdentifier);
    }
}

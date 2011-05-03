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
    STATUS        ("MainFrame.SenderTable.status"),
    SENDER_ID     ("MainFrame.SenderTable.sender_id"),
    PORT          ("MainFrame.SenderTable.port"),
    GROUP_ADDRESS ("MainFrame.SenderTable.group_address"),
    CONF_PPS      ("MainFrame.SenderTable.conf_pps"),
    AVG_PPS       ("MainFrame.SenderTable.avg_pps"),
    MIN_PPS       ("MainFrame.SenderTable.min_pps"),
    MAX_PPS       ("MainFrame.SenderTable.max_pps");

    private String captionIdentifier;

    SenderTableColumn(String captionIdentifier) {
        this.captionIdentifier = captionIdentifier;
    }

    String getCaption() {
        return ResourceBundle.getBundle("internationalization/Bundle").getString(captionIdentifier);
    }
}

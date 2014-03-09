/*
 * Copyright (C) 2013 Dabo Ross <http://www.daboross.net/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.daboross.outputtablesclient.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;
import net.daboross.outputtablesclient.main.Application;

public class RootInterface {

    private final Application application;
    private final JFrame rootFrame;
    private final JTabbedPane tabbedPane;
    private final JTextArea loggingTextArea;
    private final JPanel mainPanel;
    private final JPanel inputOutputPanel;
    private final JLabel statusLabel;

    public RootInterface(final Application application) {
        this.application = application;
        // rootFrame
        rootFrame = new JFrame();
        rootFrame.setMinimumSize(new Dimension(640, 480));
        rootFrame.setPreferredSize(new Dimension(640, 480));
        rootFrame.setLayout(new BorderLayout());
        rootFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        rootFrame.setExtendedState(rootFrame.getExtendedState() | JFrame.MAXIMIZED_HORIZ);
        rootFrame.setTitle(String.format("NullPointerException Interface %s: %s",
                application.getClientAddress(), OutputInterface.class.getPackage().getImplementationVersion()));

        // tabbedPane
        tabbedPane = new JTabbedPane();
        rootFrame.add(tabbedPane, BorderLayout.CENTER);

        // loggingTextArea
        loggingTextArea = new JTextArea();
        ((DefaultCaret) loggingTextArea.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane loggingPane = new JScrollPane(loggingTextArea);
        tabbedPane.addTab("Log", loggingPane);

        // mainPanel
        mainPanel = new JPanel(new BorderLayout());
        tabbedPane.add(mainPanel, "Main");
        tabbedPane.setSelectedComponent(mainPanel);


        // statusLabel
        statusLabel = new JLabel();
        statusLabel.setFont(statusLabel.getFont().deriveFont(25f).deriveFont(Font.BOLD));
        statusLabel.setText("Not connected");
        statusLabel.setBorder(new EmptyBorder(30, 5, 30, 5));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(statusLabel, BorderLayout.NORTH);
        // inputOutputPanel
        inputOutputPanel = new JPanel(new GridLayout(1, 2));
        mainPanel.add(inputOutputPanel, BorderLayout.CENTER);
    }

    public void show() {
        rootFrame.setVisible(true);
    }

    public JFrame getRootFrame() {
        return rootFrame;
    }

    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    public JTextArea getLoggingTextArea() {
        return loggingTextArea;
    }

    public JPanel getInputOutputPanel() {
        return inputOutputPanel;
    }

    public JLabel getStatusLabel() {
        return statusLabel;
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }
}
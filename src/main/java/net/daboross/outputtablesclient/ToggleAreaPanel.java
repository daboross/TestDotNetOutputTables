/*
 * Copyright (C) 2014 Dabo Ross <http://www.daboross.net/>
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
package net.daboross.outputtablesclient;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

public class ToggleAreaPanel extends JPanel {

    private Container toggleOn;
    private final HashMap<String, ToggleThing> thingsToToggle = new HashMap<>();
    private GridBagConstraints constraints = new GridBagConstraints();

    public ToggleAreaPanel() {
    }

    public void setToggleOn(Container toggleOn) {
        this.toggleOn = toggleOn;
    }

    public void addToToggle(final String name, Component toToggle, Object toggleConstraints) {
        final ToggleThing thing = new ToggleThing(toToggle, toggleConstraints);
        final JToggleButton button = new JToggleButton();
        button.setText(name);
        thingsToToggle.put(name, thing);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (button.isEnabled()) {
                    StaticLog.log("EsnureOn " + name);
                    thing.ensureOn();
                } else {
                    StaticLog.log("EsnureOff " + name);
                    thing.ensureOff();
                }
            }
        });

        constraints.gridy++;
        add(button, constraints);
    }

    public class ToggleThing {

        private final Component toToggle;
        private final Object toggleConstraints;
        private boolean toggled;

        public ToggleThing(Component toToggle, Object toggleConstraints) {
            this.toToggle = toToggle;
            this.toggleConstraints = toggleConstraints;
        }

        public void toggle() {
            if (toggled) {
                toggleOn.remove(toToggle);
            } else {
                toggleOn.add(toToggle, toggleConstraints);
            }
            toggled = !toggled;
        }

        public void ensureOn() {
            if (!toggled) {
                toggled = !toggled;
                toggleOn.add(toToggle, toggleConstraints);
            }
        }

        public void ensureOff() {
            if (toggled) {
                toggleOn.remove(toToggle);
                toggled = !toggled;
            }
        }
    }
}

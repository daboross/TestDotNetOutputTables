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
package net.daboross.outputtablesclient.main;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import net.daboross.outputtablesclient.api.InputListener;
import net.daboross.outputtablesclient.output.Output;
import net.daboross.outputtablesclient.persist.JSONHomeStorage;
import org.ingrahamrobotics.dotnettables.DotNetTable;
import org.ingrahamrobotics.dotnettables.DotNetTables;
import org.json.JSONObject;

public class InputTablesMain implements DotNetTable.DotNetTableEvents {

    private static final String FEEDBACK_KEY = "_DRIVER_FEEDBACK_KEY";
    private final InputListenerForward l = new InputListenerForward();
    private final Map<String, String> values = new HashMap<>();
    private final DotNetTable defaultSettingsTable;
    private final DotNetTable settingsTable;
    private final Timer timer = new Timer();
    private boolean stale = true;
    private long currentFeedback;
    private final JSONHomeStorage storage;
    private final JSONObject storageObj;

    public InputTablesMain() {
        defaultSettingsTable = DotNetTables.subscribe("robot-input-default");
        settingsTable = DotNetTables.publish("robot-input");
        storage = new JSONHomeStorage();
        JSONObject tempObject = storage.obj().optJSONObject("input-save");
        if (tempObject == null) {
            tempObject = new JSONObject();
            storage.obj().put("input-save", tempObject);
        }
        storageObj = tempObject;
    }

    public void subscribe() {
        for (String key : (Set<String>) storageObj.keySet()) {
            String value = storageObj.getString(key);
            settingsTable.setValue(key, value);
            values.put(key, value);
            l.onCreateDefaultKey(key, value);
        }
        defaultSettingsTable.onChange(this);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateStale();
            }
        }, 300, 300);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendSettings();
            }
        }, 1000, 1000);
    }

    public void addListener(InputListener listener) {
        l.addListener(listener);
    }

    public void removeListener(InputListener listener) {
        l.removeListener(listener);
    }

    public void updateKey(String key, String newValue) {
        settingsTable.setValue(key, newValue);
        storageObj.put(key, newValue);
        storage.save();
        sendSettings();
    }

    @Override
    public synchronized void changed(DotNetTable dnt) {
        if (!dnt.name().equals("robot-input-default")) {
            Output.logI("Non-input table '%s' ignored", dnt.name());
            return;
        }
        updateStale();
        Output.logI("Table changed");
        for (Enumeration<String> e = dnt.keys(); e.hasMoreElements(); ) {
            String key = e.nextElement();
            if (key.startsWith("_")) {
                continue;
            }
            String value = dnt.getValue(key);
            if (!values.containsKey(key)) {
                values.put(key, value);
                l.onCreateDefaultKey(key, value);
            } else if (!values.get(key).equals(value)) {
                values.put(key, value);
                l.onUpdateDefaultKey(key, value);
            }
        }
        for (String key : new ArrayList<>(values.keySet())) {
            if (!dnt.exists(key)) {
                values.remove(key);
                l.onDeleteKey(key);
            }
        }
    }

    @Override
    public synchronized void stale(DotNetTable dnt) {
        l.onStale();
    }

    private void updateStale() {
        if (defaultSettingsTable.exists(FEEDBACK_KEY)) {
            String feedbackStr = defaultSettingsTable.getValue(FEEDBACK_KEY);
            double feedback = -1;
            try {
                feedback = Double.parseDouble(feedbackStr);
            } catch (NumberFormatException ex) {
                Output.logI("Non-number feedback '%s'.", feedbackStr);
            }
            if (stale) {
                if (currentFeedback < feedback + 2) {
                    stale = false;
                    l.onNotStale();
                }
            } else {
                if (currentFeedback > feedback + 2) {
                    stale = true;
                    l.onStale();
                }
            }
        }
    }

    private void sendSettings() {
        currentFeedback++;
        settingsTable.setValue(FEEDBACK_KEY, currentFeedback);
        settingsTable.send();
    }

    public JSONHomeStorage getStorage() {
        return storage;
    }
}

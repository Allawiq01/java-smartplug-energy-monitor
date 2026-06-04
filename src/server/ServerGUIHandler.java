package server;

import se.mau.DA343A.VT25.projekt.LiveXYSeries;
import se.mau.DA343A.VT25.projekt.ServerGUI;

import javax.swing.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Hanterar serverns grafiska gränssnitt (GUI) och uppdaterar data i realtid.
 */
public class ServerGUIHandler {
    private static final int MAX_VISIBLE_POINTS = 300;

    private final ServerGUI serverGUI;
    private final Map<String, LiveXYSeries<Double>> seriesMap = new HashMap<>();
    private final Map<String, Integer> applianceConsumption = new HashMap<>();
    private final Timer guiUpdateTimer = new Timer();

    /**
     * Skapar en GUI-hanterare för servern och initierar gränssnittet.
     */
    public ServerGUIHandler() {
        serverGUI = new ServerGUI("Smart Plug Server");

        // Starta GUI:t på rätt tråd
        SwingUtilities.invokeLater(serverGUI::createAndShowUI);
        startGUIUpdateTimer();
    }

    /**
     * Uppdaterar effektförbrukningen för en given apparat och uppdaterar GUI:t.
     *
     * @param applianceName Namn på apparaten som uppdateras.
     * @param power         Nuvarande effektförbrukning i watt.
     */
    public synchronized void updateConsumption(String applianceName, int power) {
        applianceConsumption.put(applianceName, power);
        SwingUtilities.invokeLater(() -> ensureSeriesExists(applianceName));
    }


    /**
     * Loggar ett meddelande i serverns GUI.
     *
     * @param message Meddelandet att logga.
     */
    public synchronized void logMessage(String message) {
        SwingUtilities.invokeLater(() -> serverGUI.addLogMessage(message));
    }

    private void startGUIUpdateTimer() {
        guiUpdateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                synchronized (ServerGUIHandler.this) {
                    double total = applianceConsumption.values()
                            .stream()
                            .mapToDouble(Integer::doubleValue)
                            .sum();

                    SwingUtilities.invokeLater(() -> {
                        serverGUI.setTotalConsumption(total);
                        applianceConsumption.forEach((applianceName, power) -> {
                            ensureSeriesExists(applianceName);
                            seriesMap.get(applianceName).addValue(
                                    (double) Instant.now().getEpochSecond(),
                                    (double) power
                            );
                            serverGUI.addLogMessage(applianceName + " - " + power + "W");
                        });
                    });
                }
            }
        }, 1000, 1000);
    }

    private void ensureSeriesExists(String applianceName) {
        if (!seriesMap.containsKey(applianceName)) {
            LiveXYSeries<Double> newSeries = new LiveXYSeries<>(applianceName, MAX_VISIBLE_POINTS);
            seriesMap.put(applianceName, newSeries);
            serverGUI.addSeries(newSeries);
        }
    }
}

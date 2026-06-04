package Client;

import config.SmartPlugConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Grafiskt gränssnitt för en smart plug-klient.
 */
public class ClientGUI {
    private final EnergyClient client;
    private final JSlider slider;
    private final JLabel powerLabel;

    /**
     * Skapar GUI för en klient som representerar en elektrisk apparat.
     *
     * @param applianceName Namn på apparaten.
     * @param serverIP      Serverns IP-adress.
     * @param serverPort    Serverns portnummer.
     * @param teamName      Teamets namn för autentisering.
     */
    public ClientGUI(String applianceName, String serverIP, int serverPort, String teamName) {
        client = new EnergyClient(applianceName, serverIP, serverPort, teamName);

        JFrame frame = new JFrame("Smart Plug - " + applianceName);
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                client.closeConnection();
            }
        });

        powerLabel = new JLabel("Nuvarande förbrukning: 0W", SwingConstants.CENTER);
        frame.add(powerLabel, BorderLayout.NORTH);

        slider = new JSlider(0, SmartPlugConfig.getMaxPowerConsumption(applianceName), 0);
        slider.setMajorTickSpacing(100);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.addChangeListener(e -> {
            int power = slider.getValue();
            powerLabel.setText("Nuvarande förbrukning: " + power + "W");
            client.sendPowerConsumption(power);
        });

        if (!client.isConnected()) {
            slider.setEnabled(false);
            powerLabel.setText("Kunde inte ansluta till servern");
        }

        frame.add(slider, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Smart Plug System");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JComboBox<String> applianceDropdown = new JComboBox<>(SmartPlugConfig.APPLIANCES);
        frame.add(applianceDropdown, BorderLayout.CENTER);

        JButton startClientButton = new JButton("Starta Klient");
        startClientButton.addActionListener(e -> {
            String selectedAppliance = (String) applianceDropdown.getSelectedItem();
            new ClientGUI(
                    selectedAppliance,
                    SmartPlugConfig.SERVER_HOST,
                    SmartPlugConfig.SERVER_PORT,
                    SmartPlugConfig.TEAM_NAME
            );
        });
        frame.add(startClientButton, BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

import config.SmartPlugConfig;
import server.EnergyServer;
import Client.ClientGUI;

import javax.swing.*;
import java.awt.*;

public class Main {
    private static EnergyServer server;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Smart Plug System");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JButton startServerButton = new JButton("Starta Server");
        startServerButton.addActionListener(e -> startServer());
        frame.add(startServerButton, BorderLayout.NORTH);

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

    private static void startServer() {
        if (server == null) {
            server = new EnergyServer(SmartPlugConfig.SERVER_PORT);
            Thread serverThread = new Thread(server, "SmartPlug-Server");
            serverThread.start();
            JOptionPane.showMessageDialog(null, "Servern startad!", "Info", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Servern kör redan!", "Info", JOptionPane.WARNING_MESSAGE);
        }
    }
}

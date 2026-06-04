package Client;

import se.mau.DA343A.VT25.projekt.Buffer;
import se.mau.DA343A.VT25.projekt.net.SecurityTokens;

import javax.swing.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Objects;

/**
 * Klientklass som hanterar kommunikationen med servern för energiförbrukning.
 */
public class EnergyClient {
    private final String applianceName;
    private final Buffer<Integer> powerBuffer;

    private Socket socket;
    private DataOutputStream out;
    private Thread senderThread;
    private volatile boolean running;

    /**
     * Skapar en klientanslutning till servern.
     *
     * @param applianceName Namn på apparaten.
     * @param serverIP      Serverns IP-adress.
     * @param serverPort    Serverns portnummer.
     * @param teamName      Teamets namn för autentisering.
     */
    public EnergyClient(String applianceName, String serverIP, int serverPort, String teamName) {
        this.applianceName = Objects.requireNonNull(applianceName, "applianceName");
        this.powerBuffer = new Buffer<>();

        try {
            socket = new Socket(serverIP, serverPort);
            out = new DataOutputStream(socket.getOutputStream());
            running = true;
            sendInitialData(teamName);

            senderThread = new Thread(this::sendFromBuffer, "SmartPlug-Client-" + applianceName);
            senderThread.start();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Kunde inte ansluta till servern!", "Fel", JOptionPane.ERROR_MESSAGE);
            closeResources();
        }
    }

    /**
     * Skickar initiala data, inklusive autentiseringstoken, till servern.
     *
     * @throws IOException Om data inte kan skickas.
     */
    private void sendInitialData(String teamName) throws IOException {
        String token = new SecurityTokens(teamName).generateToken();
        out.writeUTF(token);
        out.writeUTF(applianceName);
        out.writeInt(0); // Startvärde för effektförbrukning
        out.flush();
    }

    /**
     * Lägger till ett nytt värde i bufferten istället för att skicka direkt.
     *
     * @param power Ny effektförbrukning i watt.
     */
    public void sendPowerConsumption(int power) {
        if (!isConnected()) {
            return;
        }
        powerBuffer.put(power);
    }

    /**
     * Läser kontinuerligt från bufferten och skickar data till servern.
     */
    private void sendFromBuffer() {
        while (running) {
            try {
                Integer power = powerBuffer.get(); // Väntar tills ett värde finns i bufferten
                if (out != null) {
                    out.writeInt(power); // Skickar det senaste värdet till servern
                    out.flush();
                }
                Thread.sleep(1000); // Skickar data varje sekund
            } catch (IOException e) {
                System.out.println(applianceName + " - Koppling bruten.");
                running = false;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
            }
        }
    }

    public boolean isConnected() {
        return running && socket != null && socket.isConnected() && !socket.isClosed() && out != null;
    }

    /**
     * Stänger klientens anslutning till servern.
     */
    public void closeConnection() {
        running = false;
        closeResources();

        if (senderThread != null) {
            senderThread.interrupt();
        }
    }

    private void closeResources() {
        try {
            if (out != null) {
                out.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Kunde inte stänga klientanslutningen: " + e.getMessage());
        }
    }

}

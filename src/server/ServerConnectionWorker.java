package server;

import config.SmartPlugConfig;
import se.mau.DA343A.VT25.projekt.net.ListeningSocketConnectionWorker;
import se.mau.DA343A.VT25.projekt.net.SecurityTokens;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.net.SocketAddress;

/**
 * Hanterar en enskild klientanslutning till servern.
 */
public class ServerConnectionWorker implements ListeningSocketConnectionWorker {
    private final ServerGUIHandler guiHandler;
    private final SecurityTokens securityTokens = new SecurityTokens(SmartPlugConfig.TEAM_NAME);

    /**
     * Skapar en ny anslutningshanterare.
     *
     * @param guiHandler Referens till GUI-hanteraren.
     */
    public ServerConnectionWorker(ServerGUIHandler guiHandler) {
        this.guiHandler = guiHandler;
    }

    @Override
    public void newConnection(SocketAddress address, DataInput in, DataOutput out) {
        try {
            String token = in.readUTF();
            if (!securityTokens.verifyToken(token)) {
                guiHandler.logMessage("Ogiltig säkerhetstoken från " + address + ". Anslutning avvisad.");
                return;
            }

            String applianceName = in.readUTF();
            if (applianceName == null || applianceName.isBlank()) {
                guiHandler.logMessage("Klient från " + address + " saknar apparatnamn. Anslutning avvisad.");
                return;
            }

            int powerConsumption = in.readInt();
            guiHandler.logMessage("Ny klient ansluten: " + applianceName + " från " + address);

            guiHandler.updateConsumption(applianceName, powerConsumption);

            while (!Thread.currentThread().isInterrupted()) {
                powerConsumption = in.readInt();
                guiHandler.updateConsumption(applianceName, powerConsumption);
            }
        } catch (IOException e) {
            guiHandler.logMessage("Klient frånkopplad.");
        }
    }
}

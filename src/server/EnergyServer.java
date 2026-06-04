package server;

import config.SmartPlugConfig;
import se.mau.DA343A.VT25.projekt.net.ListeningSocket;
import se.mau.DA343A.VT25.projekt.net.ListeningSocketConnectionWorker;

/**
 * Serverklass som hanterar klientanslutningar och energidata.
 */
public class EnergyServer extends ListeningSocket {
    private final ServerGUIHandler guiHandler;

    /**
     * Skapar en server som lyssnar på en given port.
     *
     * @param listeningPort Porten som servern ska lyssna på.
     */
    public EnergyServer(int listeningPort) {
        super(listeningPort);
        guiHandler = new ServerGUIHandler();
    }

    @Override
    public ListeningSocketConnectionWorker createNewConnectionWorker() {
        return new ServerConnectionWorker(guiHandler);
    }

    /**
     * Startar servern.
     *
     * @param args Kommandoradsargument (ej använda).
     */
    public static void main(String[] args) {
        EnergyServer server = new EnergyServer(SmartPlugConfig.SERVER_PORT);
        new Thread(server, "SmartPlug-Server").start();
    }
}

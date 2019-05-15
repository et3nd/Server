package realizeserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;

public class StartupServer {
    private ServerSocket serverSocket;
    static LinkedList<Server> listOfConnections = new LinkedList<>();
    static ArrayList<String> listOfLogins = new ArrayList<>();

    private void startup() throws IOException {
        try {
            serverSocket = new ServerSocket(29598);
            while (true) {
                Socket socket = serverSocket.accept();
                Server server = new Server(socket);
                listOfConnections.add(server);
                server.threadInitialization();
            }
        } catch (ConcurrentModificationException ignored) { }
        finally {
            serverSocket.close();
        }
    }


    public void loader() {
        try {
            startup();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

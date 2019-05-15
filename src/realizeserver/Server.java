package realizeserver;

import java.io.*;
import java.net.Socket;
import java.util.ConcurrentModificationException;

public class Server extends Thread {
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;

    Server(Socket socket) throws IOException {
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            readLogin();
            while (true) {
                String outcommingMessage = in.readLine();
                System.out.println(outcommingMessage);
                if (outcommingMessage.equals("Exit")) {
                    closeConnection();
                    break;
                }
                for (Server server : StartupServer.listOfConnections) {
                    if (!server.equals(this)) {
                        server.writeMessageToClients(outcommingMessage);
                    }
                }
            }
        } catch (IOException e) {
            closeConnection();
        } catch (ConcurrentModificationException | NullPointerException ignored) { }
    }

    private void readLogin() throws IOException {
        String loginMessage = in.readLine();
        for (String log : StartupServer.listOfLogins) {
            if (loginMessage.equals(log)) {
                out.write("Отказ");
                out.flush();
                closeConnection();
                break;
            }
        }
        StartupServer.listOfLogins.add(loginMessage);
        out.write("Хелло, " + loginMessage + "\n");
        out.flush();
    }

    private void closeConnection() {
        try {
            if (!socket.isClosed()) {
                socket.close();
                in.close();
                out.close();
                for (Server server : StartupServer.listOfConnections) {
                    if (server.equals(this)) {
                        server.interrupt();
                    }
                    StartupServer.listOfConnections.remove(this);
                }
            }
        } catch (ConcurrentModificationException ignored) { }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeMessageToClients(String message) {
        try {
            out.write(message + "\n");
            out.flush();
        } catch (IOException e) {
            closeConnection();
        }
    }

    void threadInitialization() {
        start();
    }

}

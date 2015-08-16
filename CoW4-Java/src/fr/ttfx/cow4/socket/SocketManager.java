package fr.ttfx.cow4.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;

/**
 * Created by Naël MSKINE on 08/08/15.
 *
 * Gère la réception et l\"envoi de message vers le serveur via le socket
 */
public class SocketManager {
    private Consumer<String> handleFunc = null;
    private Socket socket = null;
    private PrintWriter output;
    private BufferedReader input;

    public boolean connectToServer(String host, int port, String iaName, String iaImgUrl, Consumer<String> handleFunc) {
        this.handleFunc = handleFunc;

        try {
            socket = new Socket(host, port);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream());

            output.write("{" +
                    "    \"type\":\"authenticate\"," +
                    "    \"name\":\""+iaName+"\"," +
                    "    \"avatar\":\""+iaImgUrl+"\"" +
                    "}" +
                    "#end#");
            output.flush();

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    char buffer[] = new char[64 * 1024]; // 64ko buffer
                    StringBuffer sb = new StringBuffer();
                    while (true) {
                        try {
                            readSocketMessage(buffer, sb);
                        } catch (IOException e) {
                            e.printStackTrace();
                            break;
                        }
                    }
                }
            });

            t.start();

        } catch (IOException e) {
            e.printStackTrace();
            closeConnection();
            return false;
        }
        return true;
    }

    boolean firstMessage = true;
    private boolean readSocketMessage(char[] buffer, StringBuffer sb) throws IOException {
        int read = input.read(buffer);
        sb.append(read);
        int messageEnd = sb.indexOf("#end#");
        if (messageEnd > 0) {
            String message = sb.substring(0, messageEnd);
            if (firstMessage) {
                // server has sent IA id or an error
                firstMessage = false;
            } else {

            }
        }
        return firstMessage;
    }

    public void closeConnection() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

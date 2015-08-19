package fr.ttfx.cow4.socket;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.ttfx.cow4.world.GameWorld;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;

/**
 * Created by Naël MSKINE on 08/08/15.
 *
 * en: It manages input and output messages to the server using a socket
 * fr: Gère la réception et l\"envoi de messages vers le serveur via le socket
 */
public class SocketManager {
    private GameWorld gameWorld;
    private Consumer<GameWorld> handleFunc = null;
    private Socket socket = null;
    private PrintWriter output;
    private BufferedReader input;

    public boolean connectToServer(String host, int port, String iaName, String iaImgUrl, Consumer<GameWorld> handleFunc, GameWorld gameWorld) {
        this.handleFunc = handleFunc;
        this.gameWorld = gameWorld;

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
                    StringBuilder sb = new StringBuilder();
                    while (true) {
                        try {
                            int read = input.read(buffer);
                            sb.append(buffer);
                            int messageEnd = sb.indexOf("#end#");
                            System.out.println("buffer : " + sb.toString());
                            System.out.println("end : " + messageEnd);
                            if (messageEnd > 0) {
                                System.out.println("found end");
                                String message = sb.substring(0, messageEnd);
                                parseMessage(message);
                                String otherPart = sb.substring(messageEnd + "#end#".length(), sb.length());
                                sb = new StringBuilder();
                                // System.out.println("other" + );
                                // sb.append(otherPart);
                            }
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

    /**
     * en: The JSON parser should be instanciated less time as possible to improve performances
     * fr: Le parser doit être instancié le moins de fois possible pour améliorer les performances
     */
    static JsonParser parser = new JsonParser();

    /**
     * Parse and extracts informations available in the message
     * received by the Code Of War 4 Server
     * @param message A message read from the socket
     */
    private void parseMessage(String message) {
        System.out.println("message = " + message);
        JsonObject jsonMessage = parser.parse(message).getAsJsonObject();

        if (jsonMessage.get("type").getAsString().equals("id")) {
            // Receiving our IA id
            // This is the connection process and
            // the first answer from the server
            System.out.println("Connexion OK");
            Long id = jsonMessage.get("id").getAsLong();
            gameWorld.getMyIA().setId(id);
        } else {
            System.out.println("Turn");
            // Receiving a message. The turn starts.
            JsonObject dataPart = jsonMessage.get("data").getAsJsonObject();


            /////////////////////////////////////////
            //              Turn number            //
            /////////////////////////////////////////
            gameWorld.setGameTurn(dataPart.get("currentTurn").getAsInt());


            /////////////////////////////////////////
            //              IAs Infos               //
            /////////////////////////////////////////
            JsonArray iaList = dataPart.get("iaList").getAsJsonArray();
            gameWorld.parseIaInfos(iaList);


            /////////////////////////////////////////
            //        Cells & IAs Positions        //
            /////////////////////////////////////////
            JsonArray labyrinth = dataPart.get("cells").getAsJsonArray();
            gameWorld.initNbLines(labyrinth.size());
            for (int i = 0; i < labyrinth.size(); i++){
                JsonArray labyrinthLine = labyrinth.get(i).getAsJsonArray();
                gameWorld.initNbCellsInLine(i, labyrinthLine.size());
                for (int j = 0; j < labyrinthLine.size(); j++) {
                    JsonObject cell = labyrinthLine.get(j).getAsJsonObject();
                    gameWorld.parseCell(cell, i, j);
                }
            }
            handleFunc.accept(gameWorld);
        }
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

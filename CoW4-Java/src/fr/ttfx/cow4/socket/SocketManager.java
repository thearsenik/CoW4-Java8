package fr.ttfx.cow4.socket;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.ttfx.cow4.actions.Order;
import fr.ttfx.cow4.world.GameWorld;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by Naël MSKINE on 08/08/15.
 * <p>
 * en: It manages input and output messages to the server using a socket
 * fr: Gère la réception et l\"envoi de messages vers le serveur via le socket
 */
public class SocketManager {
    private GameWorld gameWorld;
    private Function<GameWorld, List<Order>> handleFunc = null;
    private Runnable initFunc = null;
    private Socket socket = null;
    private PrintWriter output;
    private BufferedReader input;

    public boolean connectToServer(String host, int port, String aiName, String aiImgUrl, CharacterSkin charType, Function<GameWorld, List<Order>> handleFunc, GameWorld gameWorld) {
        return connectToServer(host, port, aiName, aiImgUrl, charType, () -> { }, handleFunc, gameWorld);
    }

    public boolean connectToServer(String host, int port, String aiName, String aiImgUrl, CharacterSkin charType, Runnable initFunc, Function<GameWorld, List<Order>> handleFunc, GameWorld gameWorld) {
        this.handleFunc = handleFunc;
        this.initFunc = initFunc;
        this.gameWorld = gameWorld;

        try {
            socket = new Socket(host, port);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream());

            output.write("{" +
                    "    \"type\":\"authenticate\"," +
                    "    \"name\":\"" + aiName + "\"," +
                    "    \"avatar\":\"" + aiImgUrl + "\"," +
                    "    \"token\":\"" + "tokendemo" + "\"," +
                    "    \"profil\":" + charType.getId() + "" +
                    "}" +
                    "#end#");
            output.flush();

            char buffer[] = new char[64 * 1024]; // 64ko buffer
            StringBuilder sb = new StringBuilder();
            while (true) {
                try {
                    int read = input.read(buffer);
                    sb.append(buffer, 0, read);
                    int messageEnd = sb.indexOf("#end#");
                    if (messageEnd > 0) {
                        String message = sb.substring(0, messageEnd);

                        /////////////////////////////////////////
                        //          Reset Items Data           //
                        /////////////////////////////////////////
                        gameWorld.getCellsWithItems().clear();

                        /////////////////////////////////////////
                        //          Parse Input Data           //
                        /////////////////////////////////////////
                        boolean shouldAnswer = parseMessage(message);

                        if (shouldAnswer) {
                            /////////////////////////////////////////
                            //              Call AI                //
                            /////////////////////////////////////////
                            List<Order> orders = handleFunc.apply(gameWorld);

                            /////////////////////////////////////////
                            //      Send response to server        //
                            /////////////////////////////////////////
                            Response response = new Response();
                            response.setActions(orders);
                            String responseStr = gson.toJson(response);
                            output.write(responseStr);
                            output.write("#end#\n");
                            output.flush();
                        }
                        String nextMessage = sb.substring(messageEnd + "#end#".length());
                        sb = new StringBuilder(nextMessage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            closeConnection();
            return false;
        }
        return true;
    }

    /**
     * en: The JSON parser should be instancaited less time as possible to improve performances
     * fr: Le parser doit être instancié le moins de fois possible pour améliorer les performances
     */
    static JsonParser parser = new JsonParser();
    static Gson gson = new Gson();

    /**
     * Parse and extracts informations available in the message
     * received by the Code Of War 4 Server
     *
     * @param message A message read from the socket
     */
    private boolean parseMessage(String message) {
        JsonObject jsonMessage = parser.parse(message).getAsJsonObject();
//        JsonObject jsonMessage = null;
//        try {
//            jsonMessage = parser.parse(message).getAsJsonObject();
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }

        if (jsonMessage.get("type").getAsString().equals("id")) {
            // Receiving our AI id
            // This is the connection process and
            // the first answer from the server
            System.out.println("Connexion OK");
            Long id = jsonMessage.get("id").getAsLong();
            gameWorld.getMyAI().setId(id);
            return false;
        } else {
            // Receiving a message. The turn starts.
            JsonObject dataPart = jsonMessage.get("data").getAsJsonObject();


            /////////////////////////////////////////
            //              Turn number            //
            /////////////////////////////////////////
            gameWorld.setGameTurn(dataPart.get("currentTurn").getAsInt());

            if (gameWorld.getGameTurn() == 0) {
                initFunc.run();
            }

            /////////////////////////////////////////
            //              AIs Infos               //
            /////////////////////////////////////////
            JsonArray aiList = dataPart.get("aiList").getAsJsonArray();
            gameWorld.parseAiInfos(aiList);


            /////////////////////////////////////////
            //        Cells & AIs Positions        //
            /////////////////////////////////////////
            JsonArray labyrinth = dataPart.get("cells").getAsJsonArray();
            gameWorld.initNbLines(labyrinth.size());
            for (int i = 0; i < labyrinth.size(); i++) {
                JsonArray labyrinthLine = labyrinth.get(i).getAsJsonArray();
                gameWorld.initNbCellsInLine(i, labyrinthLine.size());
                for (int j = 0; j < labyrinthLine.size(); j++) {
                    JsonObject cell = labyrinthLine.get(j).getAsJsonObject();
                    gameWorld.parseCell(cell, i, j);
                }
            }
            return true;
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

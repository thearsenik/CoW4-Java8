package tests;

import fr.ttfx.cow4.actions.Order;
import fr.ttfx.cow4.socket.CharacterSkin;
import fr.ttfx.cow4.socket.Response;
import fr.ttfx.cow4.socket.SocketManager;
import fr.ttfx.cow4.world.GameWorld;
import fr.ttfx.cow4.world.StaticGameWorld;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Arsenik on 09/09/15.
 */
public class ReplayRunner extends SocketManager {
    protected List<String> replayLines;


    public static void main(String[] args) {
        ReplayRunner replayRunner1 = new ReplayRunner();
        ReplayRunner replayRunner2 = new ReplayRunner();
        try {
            replayRunner1.loadReplayFile(args[0]);
            replayRunner2.loadReplayFile(args[1]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Thread t1 = new Thread() {
            @Override
            public void run() {
                replayRunner1.start(args[0]);
            }
        };
        t1.start();

        Thread t2 = new Thread() {
            @Override
            public void run() {
                replayRunner2.start(args[1]);
            }
        };
        t2.start();
    }

    protected void start(String fileName) {
        connectToServer("localhost", 8127, fileName, "", CharacterSkin.BARBARIAN, () -> {}, (world) -> null, new StaticGameWorld(), false);
    }

    protected void loadReplayFile(String file) throws IOException {
        replayLines = Files.lines(Paths.get(file)).collect(Collectors.toList());
    }

    @Override
    public boolean connectToServer(String host, int port, String aiName, String aiImgUrl, CharacterSkin charType, Runnable initFunc, Function<GameWorld, List<Order>> handleFunc, GameWorld gameWorld, boolean record) {
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

                            output.write(replayLines.get(gameWorld.getGameTurn()));
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
}

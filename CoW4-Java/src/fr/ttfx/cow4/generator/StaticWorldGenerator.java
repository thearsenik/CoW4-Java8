package fr.ttfx.cow4.generator;

/**
 * Created by Arsenik on 18/08/15.
 */

import com.google.gson.JsonObject;
import fr.ttfx.cow4.socket.SocketManager;
import fr.ttfx.cow4.world.Cell;
import fr.ttfx.cow4.world.GameWorld;

/**
 * en: This class generates the labyrinth data structure for StaticGameWorld class
 * fr: Cette classe génère la structure du labyrinthe pour la classe StaticGameWorld
 */
public class StaticWorldGenerator {

    public static class StaticDataRecorder extends GameWorld{
        @Override
        public void parseCell(JsonObject cellData, int line, int column) {
            Cell cell = new Cell();
            cell.setLeft(cellData.has("left"));
            cell.setRight(cellData.has("right"));
            cell.setTop(cellData.has("top"));
            cell.setBottom(cellData.has("bottom"));
            cell.setLine(line);
            cell.setColumn(column);
            labyrinth[line][column] = cell;
        }

        @Override
        public void initNbLines(int nb) {
            labyrinth = new Cell[nb][];
        }

        @Override
        public void initNbCellsInLine(int lineNumber, int nb) {
            labyrinth[lineNumber] = new Cell[nb];
        }
    }

    public static void main(String[] args) {
        new SocketManager().connectToServer(
                args[0],
                Integer.parseInt(args[1]),
                "staticWorlGenerator",
                "",
                (world) -> {
                    printWorld(world);
                    System.exit(0);
                },
                new StaticDataRecorder());
    }

    private static void printWorld(GameWorld world) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (int i = 0; i < world.getLabyrinth().length; i++) {
            sb.append("{");
            for (int j = 0; j < world.getLabyrinth()[i].length; j++) {
                sb.append("new Cell(");
                sb.append(world.getLabyrinth()[i][j].canLeft());
                sb.append(",");
                sb.append(world.getLabyrinth()[i][j].canRight());
                sb.append(",");
                sb.append(world.getLabyrinth()[i][j].canTop());
                sb.append(",");
                sb.append(world.getLabyrinth()[i][j].canBottom());
                sb.append(",");
                sb.append(world.getLabyrinth()[i][j].getLine());
                sb.append(",");
                sb.append(world.getLabyrinth()[i][j].getColumn());
                sb.append(")");
                if (j < (world.getLabyrinth()[i].length - 1)) {
                    sb.append(",");
                }
                sb.append("\n");
            }
            sb.append("}");
            if (i < (world.getLabyrinth().length - 1))  {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append("}");
        System.out.println(sb.toString());
    }
}

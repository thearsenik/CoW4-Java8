package fr.ttfx.cow4.world;

import com.google.gson.JsonObject;

/**
 * Created by Arsenik on 18/08/15.
 */
public class DynamicGameWorld extends GameWorld {
    @Override
    public void parseCell(JsonObject cellData, int line, int column) {
        Cell cell = new Cell();
        cell.setId(cellData.get("id").getAsLong());
        cell.setLeft(cellData.has("left"));
        cell.setRight(cellData.has("right"));
        cell.setTop(cellData.has("top"));
        cell.setBottom(cellData.has("bottom"));
        cell.setLine(line);
        cell.setColumn(column);
        labyrinth[line][column] = cell;

        super.parseCell(cellData, line, column);
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

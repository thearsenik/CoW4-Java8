package fr.ttfx.cow4.world;

import com.google.gson.JsonObject;

/**
 * Created by Arsenik on 18/08/15.
 */
public class DynamicGameWorld extends GameWorld {

    @Override
    public void parseCell(JsonObject cell, int line, int column) {

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

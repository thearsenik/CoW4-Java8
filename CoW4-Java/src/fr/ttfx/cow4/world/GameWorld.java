package fr.ttfx.cow4.world;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Created by TheArsenik on 16/08/15.
 */
public abstract class GameWorld {
    protected IA myIA = new IA();
    protected IA ennemyIA = new IA();
    protected IA chicken = new IA();
    protected Cell[][] labyrinth;
    protected int gameTurn;

    public IA getMyIA() {
        return myIA;
    }

    public IA getEnnemyIA() {
        return ennemyIA;
    }

    public Cell[][] getLabyrinth() {
        return labyrinth;
    }

    public IA getChicken() {
        return chicken; // Goto KFC
    }

    public int getGameTurn() {
        return gameTurn;
    }

    public void setGameTurn(int gameTurn) {
        this.gameTurn = gameTurn;
    }

    public void parseIaInfos(JsonArray iaList) {
        for (int i = 0; i < iaList.size(); i++) {
            JsonObject ia = iaList.get(i).getAsJsonObject();

            if (ia.get("id").getAsLong() == getMyIA().getId()) {
                // it's my IA
                fillIaInfo(getMyIA(), ia);
            } else if ("SheepIA".equals(ia.get("name").getAsString())) {
                // it's the chicken
                fillIaInfo(getChicken(), ia);
            } else {
                // it's the ennemy
                fillIaInfo(getEnnemyIA(), ia);
            }
        }
    }

    public IA getIaById(Long id) {
        if (getMyIA().getId() == id) {
            return getMyIA();
        } else if (getEnnemyIA().getId() == id) {
            return getEnnemyIA();
        } else if (getChicken().getId() == id) {
            return getChicken();
        }
        return null;
    }

    public abstract void parseCell(JsonObject cell, int line, int column);

    public abstract void initNbLines(int nb);

    public abstract void initNbCellsInLine(int lineNumber, int nb);

    private void fillIaInfo(IA ia, JsonObject iaData) {
        ia.setId(iaData.get("id").getAsLong());
        ia.setInvisibilityDuration(iaData.get("invisibilityDuration").getAsInt());
        ia.setMouvementPoints(iaData.get("pm").getAsInt());
        ia.setName(iaData.get("name").getAsString());
        //ia.getOwnedItems()
        //iaData.get("items").getAsJsonArray();
    }
}

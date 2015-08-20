package fr.ttfx.cow4.world;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Created by TheArsenik on 16/08/15.
 */

/**
 * Contains all informations send by the server about the game
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

    /**
     * Copies informations from the JSON Array to IA objects
     * @param iaList An array that contains all IA informations
     */
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

    /**
     * Returns IA object corresponding to the id
     * @param id The id of the IA to return
     * @return returns an IA if matching with an id else null.
     */
    public IA getIaById(Long id) {
        if (getMyIA().getId().equals(id)) {
            return getMyIA();
        } else if (getEnnemyIA().getId().equals(id)) {
            return getEnnemyIA();
        } else if (getChicken().getId().equals(id)) {
            return getChicken();
        }
        return null;
    }

    public abstract void parseCell(JsonObject cell, int line, int column);

    public abstract void initNbLines(int nb);

    public abstract void initNbCellsInLine(int lineNumber, int nb);

    /**
     * Copies informations from the JSON Object to IA object
     * @param ia IA Object
     * @param iaData JSON Object that contains informations
     */
    private void fillIaInfo(IA ia, JsonObject iaData) {
        ia.setId(iaData.get("id").getAsLong());
        ia.setInvisibilityDuration(iaData.get("invisibilityDuration").getAsInt());
        ia.setMouvementPoints(iaData.get("pm").getAsInt());
        ia.setName(iaData.get("name").getAsString());
        ia.getItems().clear();
        JsonArray itemJsonArray = iaData.get("items").getAsJsonArray();
        for (int i = 0; i < itemJsonArray.size(); i++) {
            String itemStr = itemJsonArray.get(i).getAsJsonObject().get("type").getAsString();
            ItemType itemType = ItemType.Unknown;
            if ("trap".equals(itemStr)) {
                itemType = ItemType.Trap;
            } else if ("potion".equals(itemStr)) {
                itemType = ItemType.InvisibilityPotion;
            } else if ("parfum".equals(itemStr)) {
                itemType = ItemType.PulletPerfume;
            }
            if (itemType == ItemType.Unknown) {
                ia.getItems().add(new Item(itemType));
            } else {
                System.out.println("WARNING: Unrecognized item: " + itemStr);
            }
        }
        System.out.println("MyIA: " + getMyIA().getId() + ", EnnemyIA: " + getEnnemyIA().getId() + ", Chicken: " + getChicken().getId());
    }
}

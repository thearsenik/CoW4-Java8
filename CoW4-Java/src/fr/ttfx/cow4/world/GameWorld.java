package fr.ttfx.cow4.world;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    protected List<Cell> cellsWithItems = new ArrayList<>();

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

    public List<Cell> getCellsWithItems() {
        return cellsWithItems;
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

    public void parseCell(JsonObject cell, int line, int column) {
        if (cell.has("occupant")) {
            JsonElement occupant = cell.get("occupant");
            if (!(occupant instanceof JsonNull)) {
                Long occupantId = occupant.getAsJsonObject().get("id").getAsLong();
                getIaById(occupantId).setCell(getCell(line, column));
            }
        }

        if (cell.has("item") && !(cell.get("item") instanceof JsonNull)) {
            String itemTypeStr = cell.get("item").getAsJsonObject().get("type").getAsString();
            ItemType itemType = null;
            if (ItemType.Trap.getLabel().equals(itemTypeStr)) {
                itemType = ItemType.Trap;
            } else if (ItemType.InvisibilityPotion.getLabel().equals(itemTypeStr)) {
                itemType = ItemType.InvisibilityPotion;
            } else if (ItemType.PulletPerfume.getLabel().equals(itemTypeStr)) {
                itemType = ItemType.PulletPerfume;
            } else {
                itemType = ItemType.Unknown;
            }
            getCell(line, column).setItem(new Item(itemType));
            cellsWithItems.add(getCell(line, column));
        } else {
            getCell(line, column).setItem(null);
        }
    }

    /**
     * Stores the shortest path length to avoid unnecessary computing
     */
    protected List<Cell> currentShortestPath;

    /**
     * This method search for the shortest path between two cells by
     * trying all paths.
     * @param from Cell from where the path starts
     * @param to The destination Cell
     * @return A List of Cells indicating the path between the two Cells.
     */
    public List<Cell> getShortestPath(Cell from, Cell to) {
        currentShortestPath = new ArrayList<>(300);
        List<Cell> path = new ArrayList<>(300);
        getShortestPathAux(path, from, to);
        // remove "from" from path
        currentShortestPath.remove(0);
        return currentShortestPath;
    }

    public Cell getCell(int line, int column) {
        return labyrinth[line][column];
    }

    public abstract void initNbLines(int nb);

    public abstract void initNbCellsInLine(int lineNumber, int nb);

    protected void getShortestPathAux(List<Cell> path, Cell current, Cell dest) {
        if (currentShortestPath.size() > 0 &&  path.size() > currentShortestPath.size()) {
            // This path won't be the shortest;
            return;
        }

        if (path.contains(current)) {
            // We made a loop. This path is leading to nothing good.
            return;
        }

        Cell previous = path.size() > 0 ? path.get(path.size() - 1) : null;

        path.add(current);

        if (current == dest) {
            // We found a path to destination
            if (currentShortestPath.size() == 0 || currentShortestPath.size() > path.size()) {
                // We register the shortest path size to avoid unnecessary computing
                currentShortestPath.clear();
                currentShortestPath.addAll(path);
            }
            return;
        }

        // Determine which directions should be prioritized
        int horizontalDiff = current.getColumn() - dest.getColumn();
        int verticalDiff = current.getLine() - dest.getLine();
        Direction priotizedDirections[] = new Direction[Direction.values().length];
        boolean diffLeft = horizontalDiff > 0;
        boolean diffTop = verticalDiff > 0;
        if (Math.abs(horizontalDiff) > Math.abs(verticalDiff)) {
            if (diffLeft) {
                priotizedDirections[0] = Direction.LEFT;
                priotizedDirections[3] = Direction.RIGHT;
            } else {
                priotizedDirections[0] = Direction.RIGHT;
                priotizedDirections[3] = Direction.LEFT;
            }

            if (diffTop) {
                priotizedDirections[1] = Direction.TOP;
                priotizedDirections[2] = Direction.BOTTOM;
            } else {
                priotizedDirections[1] = Direction.BOTTOM;
                priotizedDirections[2] = Direction.TOP;
            }
        } else {
            if (diffLeft) {
                priotizedDirections[1] = Direction.LEFT;
                priotizedDirections[2] = Direction.RIGHT;
            } else {
                priotizedDirections[1] = Direction.RIGHT;
                priotizedDirections[2] = Direction.LEFT;
            }

            if (diffTop) {
                priotizedDirections[0] = Direction.TOP;
                priotizedDirections[3] = Direction.BOTTOM;
            } else {
                priotizedDirections[0] = Direction.BOTTOM;
                priotizedDirections[3] = Direction.TOP;
            }
        }

        for (Direction direction : priotizedDirections) {
            if (direction == Direction.LEFT && (current.canLeft() && previous != getCell(current.getLine(), current.getColumn() - 1))) {
                getShortestPathAux(path, getCell(current.getLine(), current.getColumn() - 1), dest);
            } else if (direction == Direction.RIGHT && (current.canRight() && previous != getCell(current.getLine(), current.getColumn() + 1))) {
                getShortestPathAux(path, getCell(current.getLine(), current.getColumn() + 1), dest);
            } else if (direction == Direction.TOP && (current.canTop() && previous != getCell(current.getLine() - 1, current.getColumn()))) {
                getShortestPathAux(path, getCell(current.getLine() - 1, current.getColumn()), dest);
            } else if (direction == Direction.BOTTOM && (current.canBottom() && previous != getCell(current.getLine()  + 1, current.getColumn()))) {
                getShortestPathAux(path, getCell(current.getLine() + 1, current.getColumn()), dest);
            }
        }

        path.remove(path.size() - 1);
    }

    /**
     * Copies informations from the JSON Object to IA object
     * @param ia IA Object
     * @param iaData JSON Object that contains informations
     */
    protected void fillIaInfo(IA ia, JsonObject iaData) {
        ia.setId(iaData.get("id").getAsLong());
        ia.setInvisibilityDuration(iaData.get("invisibilityDuration").getAsInt());
        ia.setMouvementPoints(iaData.get("pm").getAsInt());
        ia.setName(iaData.get("name").getAsString());
        ia.getItems().clear();
        JsonArray itemJsonArray = iaData.get("items").getAsJsonArray();
        for (int i = 0; i < itemJsonArray.size(); i++) {
            String itemStr = itemJsonArray.get(i).getAsJsonObject().get("type").getAsString();
            ItemType itemType = ItemType.Unknown;
            if (ItemType.Trap.getLabel().equals(itemStr)) {
                itemType = ItemType.Trap;
            } else if (ItemType.InvisibilityPotion.getLabel().equals(itemStr)) {
                itemType = ItemType.InvisibilityPotion;
            } else if (ItemType.PulletPerfume.getLabel().equals(itemStr)) {
                itemType = ItemType.PulletPerfume;
            }
            if (itemType != ItemType.Unknown) {
                ia.getItems().add(new Item(itemType));
            } else {
                System.out.println("WARNING: Unrecognized item: " + itemStr);
            }
        }
    }
}

package fr.ttfx.cow4.world;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import fr.ttfx.cow4.socket.CharacterSkin;

import java.util.*;

/**
 * Created by TheArsenik on 16/08/15.
 */

/**
 * Contains all informations send by the server about the game
 */
public abstract class GameWorld {
    private static final int LABYRINTH_SIZE = 25;

    protected AI myAI = new AI();
    protected AI ennemyAI = new AI();
    protected AI chicken = new AI();
    protected Cell[][] labyrinth;
    protected int gameTurn;
    protected List<Cell> cellsWithItems = new ArrayList<>();

    /*************************
     * A-Star data structures
     *************************/
    private Node nodes[][] = null;
    private List<Node> openList = new SortedList<>((x,y) -> x.f - y.f);
    private Node current;
    private List<Cell> path = new LinkedList<>();

    public AI getMyAI() {
        return myAI;
    }

    public AI getEnnemyAI() {
        return ennemyAI;
    }

    public Cell[][] getLabyrinth() {
        return labyrinth;
    }

    public AI getChicken() {
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
     * Copies informations from the JSON Array to AI objects
     * @param aiList An array that contains all AI informations
     */
    public void parseAiInfos(JsonArray aiList) {
        for (int i = 0; i < aiList.size(); i++) {
            JsonObject ai = aiList.get(i).getAsJsonObject();

            if (ai.get("id").getAsLong() == getMyAI().getId()) {
                // it's my AI
                fillAiInfo(getMyAI(), ai);
            } else if ("SheepIA".equals(ai.get("name").getAsString())) {
                // it's the chicken
                fillAiInfo(getChicken(), ai);
            } else {
                // it's the ennemy
                fillAiInfo(getEnnemyAI(), ai);
            }
        }
    }

    /**
     * Returns AI object corresponding to the id
     * @param id The id of the AI to return
     * @return returns an AI if matching with an id else null.
     */
    public AI getAiById(Long id) {
        if (getMyAI().getId().equals(id)) {
            return getMyAI();
        } else if (getEnnemyAI().getId().equals(id)) {
            return getEnnemyAI();
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
                getAiById(occupantId).setCell(getCell(line, column));
            }
        }

        if (cell.has("item") && !(cell.get("item") instanceof JsonNull)) {
            String itemTypeStr = cell.get("item").getAsJsonObject().get("type").getAsString();
            ItemType itemType;
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
     * This method searches for the shortest path between two cells by
     * trying all paths.
     *
     * Ennemy AI and traps are considered as blocking elements
     *
     * @param from Cell from where the path starts
     * @param to The destination Cell
     * @return A List of Cells indicating the path between the two Cells.
     */
    public List<Cell> getShortestPath(Cell from, Cell to) {
        return getShortestPath(from, to, true);
    }

    /**
     * This method searches for the shortest path between two cells by
     * trying all paths.
     *
     * Ennemy AI and traps are ignored if considerOnlyWalls is set to true
     *
     * @param from Cell from where the path starts
     * @param to The destination Cell$
     * @param considerOnlyWalls  If true ennemy AI and traps are ignored so only walls
     *                           would be considered as blocking elements
     * @return A List of Cells indicating the path between the two Cells.
     */
    public List<Cell> getShortestPath(Cell from, Cell to, boolean considerOnlyWalls) {
        if (nodes == null) {
            nodes = new Node[LABYRINTH_SIZE][LABYRINTH_SIZE];
            for (int i = 0; i < LABYRINTH_SIZE; i++) {
                for (int j = 0; j < LABYRINTH_SIZE; j++) {
                    nodes[i][j] = new Node(i, j, labyrinth[i][j]);
                }
            }
        }

        if (from == null || to == null) {
            return null;
        }

        path.clear();
        openList.clear();
        for (int i = 0; i < LABYRINTH_SIZE; i++) {
            for (int j = 0; j < LABYRINTH_SIZE; j++) {
                Node node = nodes[i][j];
                node.isInClosedList = false;
                node.isInOpenList = false;
                node.parent = null;
            }
        }
        Node toNode = nodes[to.getLine()][to.getColumn()];

        openList.add(nodes[from.getLine()][from.getColumn()]);
        openList.get(0).isInOpenList = true;

        do {
            current = openList.get(0);
            openList.remove(0);
            current.isInOpenList = false;
            current.isInClosedList = true;
            if (toNode.isInClosedList) {
                Node n = toNode;
                while(n.parent != null) {
                    path.add(0, n.cell);
                    n = n.parent;
                }
                return path;
            }
            addAdjacentNodesToOpenList(to, considerOnlyWalls);
        } while(!openList.isEmpty());
        return null;
    }

    private void addAdjacentNodesToOpenList(Cell to, boolean considerOnlyWalls) {
        if (current.cell.canTop()) {
            Node node = nodes[current.cell.getLine() - 1][current.cell.getColumn()];
            if (considerOnlyWalls || (getEnnemyAI().getCell() != node.cell
                    && !getCellsWithItems().stream().filter(x -> x.getItem().getType() == ItemType.Trap).anyMatch(x -> node.cell == x))) {
                computeNode(to, node);
            }
        }
        if (current.cell.canBottom()) {
            Node node = nodes[current.cell.getLine() + 1][current.cell.getColumn()];
            if (considerOnlyWalls || (getEnnemyAI().getCell() != node.cell
                    && !getCellsWithItems().stream().filter(x -> x.getItem().getType() == ItemType.Trap).anyMatch(x -> node.cell == x))) {
                computeNode(to, node);
            }
        }
        if (current.cell.canLeft()) {
            Node node = nodes[current.cell.getLine()][current.cell.getColumn() - 1];
            if (considerOnlyWalls || (getEnnemyAI().getCell() != node.cell
                    && !getCellsWithItems().stream().filter(x -> x.getItem().getType() == ItemType.Trap).anyMatch(x -> node.cell == x))) {
                computeNode(to, node);
            }
        }
        if (current.cell.canRight()) {
            Node node = nodes[current.cell.getLine()][current.cell.getColumn() + 1];
            if (considerOnlyWalls || (getEnnemyAI().getCell() != node.cell
                    && !getCellsWithItems().stream().filter(x -> x.getItem().getType() == ItemType.Trap).anyMatch(x -> node.cell == x))) {
                computeNode(to, node);
            }
        }
    }

    private void computeNode(Cell to, Node node) {
        if (!node.isInClosedList) {
            if (node.isInOpenList) {
                int newF = node.h + current.g + 1;
                if (newF < node.f) {
                    node.g = current.g + 1;
                    node.f = newF;
                    node.parent = current;
                }
            } else {
                node.g = current.g + 1;
                node.parent = current;
                node.h = Math.abs(Math.abs(node.cell.getLine() - to.getLine()) + Math.abs(node.cell.getColumn() - to.getColumn()));
                node.f = node.g + node.h;
                openList.add(node);
                node.isInOpenList = true;
            }
        }
    }

    public class Node {
        public int line;
        public int column;
        public Cell cell;
        public int g;
        public int h;
        public int f;
        public Node parent;
        public boolean isInClosedList;
        public boolean isInOpenList;

        public Node (int line, int column, Cell cell){
            this.line = line;
            this.column = column;
            this.cell = cell;
        }
    }

    /**
     * From StackOverflow: http://stackoverflow.com/questions/2661065/a-good-sorted-list-for-java
     * @param <T>
     */
    public class SortedList<T> extends LinkedList<T> {
        /**
         * Needed for serialization.
         */
        private static final long serialVersionUID = 1L;
        /**
         * Comparator used to sort the list.
         */
        private Comparator<? super T> comparator = null;
        /**
         * Construct a new instance with the list elements sorted in their
         * {@link java.lang.Comparable} natural ordering.
         */
        public SortedList() {
        }
        /**
         * Construct a new instance using the given comparator.
         *
         * @param comparator
         */
        public SortedList(Comparator<? super T> comparator) {
            this.comparator = comparator;
        }
        /**
         * Add a new entry to the list. The insertion point is calculated using the
         * comparator.
         *
         * @param paramT
         */
        @Override
        public boolean add(T paramT) {
            int insertionPoint = Collections.binarySearch(this, paramT, comparator);
            super.add((insertionPoint > -1) ? insertionPoint : (-insertionPoint) - 1, paramT);
            return true;
        }
        /**
         * Adds all elements in the specified collection to the list. Each element
         * will be inserted at the correct position to keep the list sorted.
         *
         * @param paramCollection
         */
        @Override
        public boolean addAll(Collection<? extends T> paramCollection) {
            boolean result = false;
            if (paramCollection.size() > 4) {
                result = super.addAll(paramCollection);
                Collections.sort(this, comparator);
            }
            else {
                for (T paramT:paramCollection) {
                    result |= add(paramT);
                }
            }
            return result;
        }
        /**
         * Check, if this list contains the given Element. This is faster than the
         * {@link #contains(Object)} method, since it is based on binary search.
         *
         * @param paramT
         * @return <code>true</code>, if the element is contained in this list;
         * <code>false</code>, otherwise.
         */
        public boolean containsElement(T paramT) {
            return (Collections.binarySearch(this, paramT, comparator) > -1);
        }
    }

    public Cell getCell(int line, int column) {
        return labyrinth[line][column];
    }

    public abstract void initNbLines(int nb);

    public abstract void initNbCellsInLine(int lineNumber, int nb);

    /**
     * Copies informations from the JSON Object to AI object
     * @param ai AI Object
     * @param aiData JSON Object that contains informations
     */
    protected void fillAiInfo(AI ai, JsonObject aiData) {
        ai.setId(aiData.get("id").getAsLong());
        ai.setInvisibilityDuration(aiData.get("invisibilityDuration").getAsInt());
        ai.setMouvementPoints(aiData.get("pm").getAsInt());
        ai.setName(aiData.get("name").getAsString());
        CharacterSkin skin = null;
        int profil = aiData.get("profil").getAsInt();
        if (profil == CharacterSkin.DWARF.getId()) {
            skin = CharacterSkin.DWARF;
        } else if (profil == CharacterSkin.WIZARD.getId()) {
            skin = CharacterSkin.WIZARD;
        } else if (profil == CharacterSkin.ELF.getId()) {
            skin = CharacterSkin.ELF;
        } else if (profil == CharacterSkin.PULLET.getId()) {
            skin = CharacterSkin.PULLET;
        } else {
            System.err.println("Unknow skin with id=" + profil);
        }
        ai.setProfil(skin);
        ai.getItems().clear();
        JsonArray itemJsonArray = aiData.get("items").getAsJsonArray();

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
                ai.getItems().add(new Item(itemType));
            } else {
                System.out.println("WARNING: Unrecognized item: " + itemStr);
            }
        }


    }
}

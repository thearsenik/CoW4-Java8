package fr.ttfx.cow4.world;

import java.util.List;

/**
 * Created by TheArsenik on 16/08/15.
 */
public class IA {
    private Long id;
    private int invisibilityDuration;
    private List<Item> ownedItems;
    private String name;
    private int mouvementPoints;
    private Cell cell;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getInvisibilityDuration() {
        return invisibilityDuration;
    }

    public void setInvisibilityDuration(int invisibilityDuration) {
        this.invisibilityDuration = invisibilityDuration;
    }

    public boolean isVisible() {
        return invisibilityDuration > 0;
    }

    public List<Item> getOwnedItems() {
        return ownedItems;
    }

    public void setOwnedItems(List<Item> ownedItems) {
        this.ownedItems = ownedItems;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMouvementPoints() {
        return mouvementPoints;
    }

    public void setMouvementPoints(int mouvementPoints) {
        this.mouvementPoints = mouvementPoints;
    }

    public void setCell(Cell cell) {
        this.cell = cell;
    }

    public Cell getCell() {
        return cell;
    }
}

package fr.ttfx.cow4.world;

import fr.ttfx.cow4.socket.CharacterSkin;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TheArsenik on 16/08/15.
 */
public class AI {
    private Long id;
    private int invisibilityDuration;
    private List<Item> items = new ArrayList<>();
    private String name;
    private int mouvementPoints;
    private Cell cell;
    private CharacterSkin profil;

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
        return invisibilityDuration <= 0;
    }

    public List<Item> getItems() { return items; }

    public void setItems(List<Item> items) {
        this.items = items;
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

    public void setProfil(CharacterSkin profil) {
        this.profil = profil;
    }

    public CharacterSkin getProfil() {
        return profil;
    }
}

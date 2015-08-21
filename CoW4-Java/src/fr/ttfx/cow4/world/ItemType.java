package fr.ttfx.cow4.world;

/**
 * Created by Arsenik on 18/08/15.
 */
public enum ItemType {
    Unknown ("unkown"),
    InvisibilityPotion ("potion"),
    Trap ("trap"),
    PulletPerfume ("parfum");

    private String label;

    ItemType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return getLabel();
    }
}

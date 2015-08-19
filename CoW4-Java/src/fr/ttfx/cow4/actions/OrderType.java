package fr.ttfx.cow4.actions;

/**
 * Created by Arsenik on 19/08/15.
 */
public enum OrderType {
    MOVE_ORDER("move"),
    USE_ITEM_ORDER("useItem"),
    PICK_UP_ITEM_ORDER("getItem");

    OrderType(String label) {
        this.label = label;
    }

    private String label;

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

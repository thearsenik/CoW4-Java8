package fr.ttfx.cow4.world;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Arsenik on 18/08/15.
 */
public enum ItemType {
    @SerializedName("unknown")
    Unknown ("unknown"),
    @SerializedName("potion")
    InvisibilityPotion ("potion"),
    @SerializedName("trap")
    Trap ("trap"),
    @SerializedName("parfum")
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

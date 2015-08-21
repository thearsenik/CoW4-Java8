package fr.ttfx.cow4.world;

/**
 * Created by Arsenik on 18/08/15.
 */
public class Item {
    private ItemType type;

    public Item(ItemType type) {
        this.type = type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }

    public ItemType getType() {
        return type;
    }
}

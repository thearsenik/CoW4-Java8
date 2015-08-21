package fr.ttfx.cow4.actions;

import fr.ttfx.cow4.world.Item;

/**
 * Created by Arsenik on 19/08/15.
 */
public class UseItemOrder extends Order {
    private Item item;

    public UseItemOrder (Item item) {
        type = OrderType.USE_ITEM_ORDER.getLabel();
        this.item = item;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}

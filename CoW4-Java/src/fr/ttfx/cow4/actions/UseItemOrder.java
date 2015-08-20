package fr.ttfx.cow4.actions;

/**
 * Created by Arsenik on 19/08/15.
 */
public class UseItemOrder extends Order {
    public UseItemOrder () {
        type = OrderType.USE_ITEM_ORDER.getLabel();
    }
}

package fr.ttfx.cow4.actions;

/**
 * Created by Arsenik on 19/08/15.
 */
public class UseItemOrder implements Order {
    @Override
    public OrderType getType() {
        return OrderType.USE_ITEM_ORDER;
    }
}

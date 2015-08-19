package fr.ttfx.cow4.actions;

/**
 * Created by Arsenik on 19/08/15.
 */
public class PickUpOrder implements Order {

    @Override
    public OrderType getType() {
        return OrderType.PICK_UP_ITEM_ORDER;
    }
}

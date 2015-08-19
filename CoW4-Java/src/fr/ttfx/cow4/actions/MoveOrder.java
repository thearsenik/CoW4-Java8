package fr.ttfx.cow4.actions;

/**
 * Created by Arsenik on 19/08/15.
 */
public class MoveOrder implements Order {
    private Long target;

    @Override
    public OrderType getType() {
        return OrderType.MOVE_ORDER;
    }

    public Long getTarget() {
        return target;
    }

    public void setTarget(Long target) {
        this.target = target;
    }
}

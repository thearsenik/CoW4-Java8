package fr.ttfx.cow4.actions;

/**
 * Created by Arsenik on 19/08/15.
 */
public class MoveOrder extends Order {
    private Long target;

    public MoveOrder(Long targetCellId) {
        this.target = targetCellId;
        type = OrderType.MOVE_ORDER.getLabel();
    }

    public Long getTarget() {
        return target;
    }

    public void setTarget(Long target) {
        this.target = target;
    }
}

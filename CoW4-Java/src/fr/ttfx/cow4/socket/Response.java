package fr.ttfx.cow4.socket;

import fr.ttfx.cow4.actions.Order;
import fr.ttfx.cow4.world.AI;

import java.util.List;

/**
 * Created by Arsenik on 19/08/15.
 */
public class Response {
    private String type = "turnResult";
    private List<Order> actions;

    public String getType() {
        return type;
    }

    public List<Order> getActions() {
        return actions;
    }

    public void setActions(List<Order> actions) {
        this.actions = actions;
    }
}

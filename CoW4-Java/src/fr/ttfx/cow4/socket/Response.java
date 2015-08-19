package fr.ttfx.cow4.socket;

import fr.ttfx.cow4.actions.Order;
import fr.ttfx.cow4.world.IA;

/**
 * Created by Arsenik on 19/08/15.
 */
public class Response {
    private String type = "turnResult";
    private IA ia;
    private Order[] actions;

    public String getType() {
        return type;
    }

    public IA getIa() {
        return ia;
    }

    public void setIa(IA ia) {
        this.ia = ia;
    }

    public Order[] getActions() {
        return actions;
    }

    public void setActions(Order[] actions) {
        this.actions = actions;
    }
}

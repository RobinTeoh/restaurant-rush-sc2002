package restaurant.customer;

import java.util.List;

import restaurant.menu.MenuItem;

public class CriticCustomer extends Customer {
    private static final int LOSS_PER_TURN = 12;

    public CriticCustomer(int id) {
        super(id);
    }

    @Override
    public String getTypeName() {
        return "Critic";
    }

    @Override
    protected int satisfactionLossPerTurn() {
        return LOSS_PER_TURN;
    }

    // critic always orders the most expensive dish (D7), first one wins a tie
    @Override
    protected MenuItem pickDish(List<MenuItem> items) {
        MenuItem best = items.get(0);
        for (MenuItem item : items) {
            if (item.getPriceCents() > best.getPriceCents()) {
                best = item;
            }
        }
        return best;
    }
}

package restaurant.pricing;

import java.util.List;

import restaurant.customer.Customer;

// can only be switched on once per game, lasts the turn it starts and the next one
public class HappyHour {
    public static final int DURATION = 2;
    public static final int PERCENT_OFF = 20;
    public static final int RECOVERY = 15;

    private boolean used;
    private int turnsLeft;

    public boolean canActivate() {
        return !used;
    }

    public void activate() {
        if (used) {
            throw new IllegalStateException("Happy Hour can only be used once per game");
        }
        used = true;
        turnsLeft = DURATION;
    }

    public boolean isActive() {
        return turnsLeft > 0;
    }

    public int getTurnsLeft() {
        return turnsLeft;
    }

    // call at the end of every turn
    public void endTurn() {
        if (turnsLeft > 0) {
            turnsLeft--;
        }
    }

    // takes 20% off a price that already has the VIP discount
    public int applyTo(int price) {
        if (isActive()) {
            return Money.percentOff(price, PERCENT_OFF);
        }
        return price;
    }

    // call at the start of each happy hour turn, recover() only helps customers still waiting
    public void recoverWaiting(List<Customer> customers) {
        if (!isActive()) {
            return;
        }
        for (Customer c : customers) {
            c.recover(RECOVERY);
        }
    }
}

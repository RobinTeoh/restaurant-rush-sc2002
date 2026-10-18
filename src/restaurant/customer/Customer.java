package restaurant.customer;

import java.util.List;
import java.util.Random;

import restaurant.menu.Menu;
import restaurant.menu.MenuItem;

/**
 * A customer in the restaurant (Stage 1).
 * Keeps its own satisfaction and state. The state only changes through the
 * event methods below, and each one checks the current state first.
 */
public class Customer {
    private static final int LOSS_PER_TURN = 8;
    private static final int MAX_SATISFACTION = 100;
    private static final long SEED = 32; // used by chooseFood; to confirm with the team (DECISIONS D6)

    private final int id;
    private int satisfaction;
    private CustomerState state;

    public Customer(int id) {
        this.id = id;
        this.satisfaction = MAX_SATISFACTION;
        this.state = CustomerState.WAITING;
    }

    /** The Waiter seats this customer: WAITING -> SEATED. */
    public void seat() {
        requireState(CustomerState.WAITING); //customer need be waiting in order to seat
        state = CustomerState.SEATED;
    }

    /**
     * The Waiter takes the order: SEATED -> ORDERED. The customer picks its own dish
     * (DECISIONS D5): customer n always uses new Random(SEED + n), so every run is the same.
     */
    public MenuItem chooseFood(Menu menu) {
        if (menu == null) {
            throw new IllegalArgumentException("menu must not be null");
        }
        requireState(CustomerState.SEATED);
        List<MenuItem> items = menu.getItems();
        MenuItem dish = items.get(new Random(SEED + id).nextInt(items.size()));
        state = CustomerState.ORDERED;
        return dish;
    }

    /** The Waiter serves the food: ORDERED -> EATING. */
    public void serve() {
        requireState(CustomerState.ORDERED);
        state = CustomerState.EATING;
    }

    /** One full turn of eating is over: EATING -> READY_TO_PAY. */
    public void finishEating() {
        requireState(CustomerState.EATING);
        state = CustomerState.READY_TO_PAY;
    }

    /** The Cashier collects payment: READY_TO_PAY -> PAID. The satisfaction is now final. */
    public void pay() {
        requireState(CustomerState.READY_TO_PAY);
        state = CustomerState.PAID;
    }

    /**
     * Called on every customer at turn end. Only customers still waiting for a table
     * or for their food lose satisfaction; at 0 the customer leaves.
     * Does nothing in any other state.
     */
    public void loseSatisfaction() {
        if (!state.losesSatisfaction()) {
            return;
        }
        satisfaction = Math.max(0, satisfaction - LOSS_PER_TURN);
        if (satisfaction == 0) {
            state = CustomerState.LEFT;
        }
    }

    public boolean hasLeft() {
        return state == CustomerState.LEFT;
    }

    public CustomerState getState() {
        return state;
    }

    /**
     * The satisfaction recorded at payment, used for the average of paid customers.
     * Satisfaction cannot change after the customer is served, so this is the current value.
     */
    public int getRecordedSatisfaction() {
        requireState(CustomerState.PAID);
        return satisfaction;
    }

    public int getId() {
        return id;
    }

    public int getSatisfaction() {
        return satisfaction;
    }

    private void requireState(CustomerState expected) {
        if (state != expected) {
            throw new IllegalStateException(
                    "Customer " + id + " is " + state + " but must be " + expected);
        }
    }
}

package restaurant.customer;

import java.util.List;
import java.util.Random;

import restaurant.menu.Menu;
import restaurant.menu.MenuItem;

// parent of every customer type, the state rules live here only
// the event methods are final so a subclass cannot break the state flow,
// subclasses only change their numbers (loss, discount) and how they pick food
public abstract class Customer {
    private static final int MAX_SATISFACTION = 100;
    private static final long SEED = 32; // fixed so every run orders the same food (D5/D6)

    private final int id;
    private int satisfaction;
    private CustomerState state;

    protected Customer(int id) {
        this.id = id;
        this.satisfaction = MAX_SATISFACTION;
        this.state = CustomerState.WAITING;
    }

    public final void seat() {
        requireState(CustomerState.WAITING); //customer need be waiting in order to seat
        state = CustomerState.SEATED;
    }

    // waiter takes the order, customer picks its own dish
    public final MenuItem chooseFood(Menu menu) {
        if (menu == null) {
            throw new IllegalArgumentException("menu must not be null");
        }
        requireState(CustomerState.SEATED);
        MenuItem dish = pickDish(menu.getItems());
        state = CustomerState.ORDERED;
        return dish;
    }

    public final void serve() {
        requireState(CustomerState.ORDERED);
        state = CustomerState.EATING;
    }

    public final void finishEating() {
        requireState(CustomerState.EATING);
        state = CustomerState.READY_TO_PAY;
    }

    public final void pay() {
        requireState(CustomerState.READY_TO_PAY);
        state = CustomerState.PAID;
    }

    // called on every customer at turn end, only the ones still waiting lose satisfaction
    public final void loseSatisfaction() {
        if (!state.losesSatisfaction()) {
            return;
        }
        satisfaction = Math.max(0, satisfaction - satisfactionLossPerTurn());
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

    // satisfaction cannot change after serving, so at payment the current value is the recorded one
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

    // percent off the bill, only VIP gets one
    public int getDiscountPercent() {
        return 0;
    }

    public abstract String getTypeName();

    protected abstract int satisfactionLossPerTurn();

    // default food choice, same seed rule as stage 1, critic overrides this
    protected MenuItem pickDish(List<MenuItem> items) {
        return items.get(new Random(SEED + id).nextInt(items.size()));
    }

    private void requireState(CustomerState expected) {
        if (state != expected) {
            throw new IllegalStateException(
                    "Customer " + id + " is " + state + " but must be " + expected);
        }
    }
}

package restaurant.order;

import restaurant.customer.Customer;
import restaurant.menu.MenuItem;
import restaurant.pricing.Money;

/**
 * One customer's order for one dish.
 * Keeps its own preparation progress and status. The status only changes through
 * the event methods below, and each one checks the current status first.
 */
public class Order {
    private final Customer customer;
    private final MenuItem item;
    private final int turnPlaced;
    private int unitsDone;
    private OrderStatus status;

    private Order(Customer customer, MenuItem item, int turnPlaced) {
        this.customer = customer;
        this.item = item;
        this.turnPlaced = turnPlaced;
        this.unitsDone = 0;
        this.status = OrderStatus.PLACED;
    }

    /** The Waiter takes an order (CONTRACT.md C -> B). */
    public static Order createOrder(Customer customer, MenuItem item, int turn) {
        if (customer == null || item == null) {
            throw new IllegalArgumentException("customer and item must not be null");
        }
        if (turn < 1) {
            throw new IllegalArgumentException("turn must be at least 1");
        }
        return new Order(customer, item, turn);
    }

    /**
     * Adds one prep unit. On the last unit the order becomes READY.
     * Returns false and changes nothing if the order is not PLACED, so the
     * game can ask the manager again without using up the Chef's task.
     */
    public boolean prepareOneUnit() {
        if (status != OrderStatus.PLACED) {
            return false;
        }
        unitsDone++;
        if (unitsDone == item.getPrepUnits()) {
            status = OrderStatus.READY;
        }
        return true;
    }

    public boolean isReady() {
        return status == OrderStatus.READY;
    }

    /** The Waiter serves the dish: READY -> SERVED. */
    public void markServed() {
        requireStatus(OrderStatus.READY);
        status = OrderStatus.SERVED;
    }

    /** The Cashier collects payment: SERVED -> PAID. */
    public void markPaid() {
        requireStatus(OrderStatus.SERVED);
        status = OrderStatus.PAID;
    }

    /** The customer left before being served: PLACED or READY -> CANCELLED (DECISIONS D3). */
    public void cancel() {
        if (!status.canBeCancelled()) {
            throw new IllegalStateException(
                    "Order for customer " + customer.getId() + " is " + status + " and cannot be cancelled");
        }
        status = OrderStatus.CANCELLED;
    }

    /**
     * The amount to pay, in cents: the item price less the customer's own discount
     * (VIP 10%, everyone else 0). Neither the item nor the customer type changes,
     * so the price is fixed from the moment the order is taken.
     */
    public int getBill() {
        if (status == OrderStatus.CANCELLED) {
            throw new IllegalStateException("a cancelled order has no bill (DECISIONS D3)");
        }
        return Money.percentOff(item.getPriceCents(), customer.getDiscountPercent());
    }

    public Customer getCustomer() {
        return customer;
    }

    public MenuItem getItem() {
        return item;
    }

    public int getTurnPlaced() {
        return turnPlaced;
    }

    public int getUnitsDone() {
        return unitsDone;
    }

    public OrderStatus getStatus() {
        return status;
    }

    private void requireStatus(OrderStatus expected) {
        if (status != expected) {
            throw new IllegalStateException(
                    "Order for customer " + customer.getId() + " is " + status + " but must be " + expected);
        }
    }
}

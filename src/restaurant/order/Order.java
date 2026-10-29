package restaurant.order;

import restaurant.customer.Customer;
import restaurant.menu.MenuItem;
import restaurant.pricing.HappyHour;
import restaurant.pricing.Money;

public class Order {
    private final Customer customer;
    private final MenuItem item;
    private final int turnPlaced;
    private final int price;
    private int unitsDone;
    private OrderStatus status;

    private Order(Customer customer, MenuItem item, int turnPlaced, int price) {
        this.customer = customer;
        this.item = item;
        this.turnPlaced = turnPlaced;
        this.price = price;
        this.unitsDone = 0;
        this.status = OrderStatus.PLACED;
    }

    public static Order createOrder(Customer customer, MenuItem item, int turn) {
        return createOrder(customer, item, turn, null);
    }

    // price is worked out here and kept, so happy hour ending later does not change it
    public static Order createOrder(Customer customer, MenuItem item, int turn, HappyHour happyHour) {
        if (customer == null || item == null) {
            throw new IllegalArgumentException("customer and item must not be null");
        }
        if (turn < 1) {
            throw new IllegalArgumentException("turn must be at least 1");
        }
        int price = Money.percentOff(item.getPriceCents(), customer.getDiscountPercent());
        if (happyHour != null) {
            price = happyHour.applyTo(price);
        }
        return new Order(customer, item, turn, price);
    }

    // returns false if the order is not waiting to be cooked, so the chef's task is not used up
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

    public void markServed() {
        requireStatus(OrderStatus.READY);
        status = OrderStatus.SERVED;
    }

    public void markPaid() {
        requireStatus(OrderStatus.SERVED);
        status = OrderStatus.PAID;
    }

    // customer left before being served (D3)
    public void cancel() {
        if (!status.canBeCancelled()) {
            throw new IllegalStateException(
                    "Order for customer " + customer.getId() + " is " + status + " and cannot be cancelled");
        }
        status = OrderStatus.CANCELLED;
    }

    public int getBill() {
        if (status == OrderStatus.CANCELLED) {
            throw new IllegalStateException("a cancelled order has no bill (D3)");
        }
        return price;
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

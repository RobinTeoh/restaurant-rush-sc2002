package restaurant.staff;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

import restaurant.control.GameStats;
import restaurant.customer.Customer;
import restaurant.order.Order;
import restaurant.place.Table;

public class Cashier {

    private final Deque<Order> paymentLine = new ArrayDeque<>();

    public void addToPaymentLine(Order order) {
        paymentLine.addLast(order);
    }

    public Optional<Order> collect(List<Table> tables, GameStats stats) {
        Order order = paymentLine.pollFirst();
        if (order == null) {
            return Optional.empty();
        }
        Customer customer = order.getCustomer();
        int bill = order.getBill();
        customer.pay();
        int satisfaction = customer.getRecordedSatisfaction();
        order.markPaid();
        for (Table table : tables) {
            if (table.getOccupant() == customer) {
                table.release();
            }
        }
        stats.recordPayment(bill, satisfaction);
        return Optional.of(order);
    }

    public boolean hasCustomersWaitingToPay() {
        return !paymentLine.isEmpty();
    }
    
}

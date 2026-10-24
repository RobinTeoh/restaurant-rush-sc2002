package restaurant.staff;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import restaurant.control.GameStats;
import restaurant.customer.Customer;
import restaurant.customer.RegularCustomer;
import restaurant.customer.CustomerState;
import restaurant.menu.Menu;
import restaurant.order.Order;
import restaurant.order.OrderStatus;
import restaurant.place.Table;

class CashierTest {

    /** Takes a customer all the way to READY_TO_PAY at the given table, using the real classes. */
    private Order readyToPay(int id, Table table) {
        Customer c = new RegularCustomer(id);
        table.assign(c);
        c.seat();
        Order order = Order.createOrder(c, c.chooseFood(Menu.standard()), 1);
        Chef chef = new Chef();
        while (!order.isReady()) {
            chef.work(order);
        }
        order.markServed();
        c.serve();
        c.finishEating();
        return order;
    }

    @Test
    void nobodyToPayMeansNoPayment() {
        GameStats stats = new GameStats();
        assertTrue(new Cashier().collect(List.of(), stats).isEmpty());
        assertEquals(0, stats.getPaidCount());
    }

    @Test
    void onePaymentPerTurnOldestFirst() {
        Table t1 = new Table(1);
        Table t2 = new Table(2);
        Order first = readyToPay(1, t1);  // Salad $9.00
        Order second = readyToPay(2, t2); // Burger $15.00
        Cashier cashier = new Cashier();
        GameStats stats = new GameStats();
        cashier.addToPaymentLine(first);
        cashier.addToPaymentLine(second);

        assertEquals(first, cashier.collect(List.of(t1, t2), stats).orElseThrow());
        assertEquals(1, stats.getPaidCount());
        assertEquals(CustomerState.READY_TO_PAY, second.getCustomer().getState(), "only one payment per turn");

        assertEquals(second, cashier.collect(List.of(t1, t2), stats).orElseThrow());
        assertEquals(2400, stats.getRevenueCents());
    }

    @Test
    void paymentMarksPaidFreesTableAndRecordsSatisfaction() {
        Table table = new Table(1);
        Order order = readyToPay(1, table);
        Cashier cashier = new Cashier();
        GameStats stats = new GameStats();
        cashier.addToPaymentLine(order);
        cashier.collect(List.of(table), stats);

        assertTrue(table.isFree());
        assertEquals(CustomerState.PAID, order.getCustomer().getState());
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(100.0, stats.averageSatisfaction().getAsDouble());
    }
}
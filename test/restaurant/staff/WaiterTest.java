package restaurant.staff;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import restaurant.control.ActionResult;
import restaurant.customer.Customer;
import restaurant.customer.RegularCustomer;
import restaurant.customer.CustomerState;
import restaurant.menu.Menu;
import restaurant.order.Order;
import restaurant.place.Table;
import restaurant.place.WaitingQueue;

class WaiterTest {

    private final Waiter waiter = new Waiter();
    private final Menu menu = Menu.standard();
    private final List<Order> orders = new ArrayList<>();

    private Customer seatedCustomer(int id) {
        Customer c = new RegularCustomer(id);
        WaitingQueue queue = new WaitingQueue();
        queue.add(c);
        waiter.seat(c, new Table(1), queue);
        return c;
    }

    @Test
    void seatFailsWhenNobodyIsWaiting() {
        ActionResult r = waiter.seat(null, new Table(1), new WaitingQueue());
        assertFalse(r.ok());
        assertEquals("Nobody is waiting in the queue.", r.message());
    }

    @Test
    void seatFailsWhenNoTableIsFree() {
        ActionResult r = waiter.seat(new RegularCustomer(1), null, new WaitingQueue());
        assertFalse(r.ok());
        assertEquals("There is no free table.", r.message());
    }

    @Test
    void seatMovesCustomerFromQueueToTable() {
        Customer c = new RegularCustomer(1);
        WaitingQueue queue = new WaitingQueue();
        queue.add(c);
        Table table = new Table(1);

        assertTrue(waiter.seat(c, table, queue).ok());
        assertTrue(queue.isEmpty());
        assertEquals(c, table.getOccupant());
        assertEquals(CustomerState.SEATED, c.getState());
    }

    @Test
    void takeOrderFailsWithNobodySeated() {
        ActionResult r = waiter.takeOrder(null, menu, 1, orders);
        assertFalse(r.ok());
        assertEquals("No customer is seated.", r.message());
    }

    @Test
    void takeOrderWorksOnceThenFails() {
        Customer c = seatedCustomer(1);
        assertTrue(waiter.takeOrder(c, menu, 1, orders).ok());
        assertEquals(1, orders.size());
        assertEquals(CustomerState.ORDERED, c.getState());

        ActionResult again = waiter.takeOrder(c, menu, 1, orders);
        assertFalse(again.ok());
        assertEquals("Customer 1 has already ordered.", again.message());
        assertEquals(1, orders.size(), "a failed action changes nothing");
    }

    @Test
    void serveFailsBeforeOrdering() {
        Customer c = seatedCustomer(1);
        ActionResult r = waiter.serve(c, null);
        assertFalse(r.ok());
        assertEquals("Customer 1 has not ordered yet.", r.message());
    }

    @Test
    void serveFailsUntilTheDishIsReady() {
        Customer c = seatedCustomer(3); // customer 3 orders Pasta (2 units) with the agreed seed
        waiter.takeOrder(c, menu, 1, orders);
        Order order = orders.get(0);
        assertFalse(waiter.serve(c, order).ok());
        new Chef().work(order);
        assertFalse(waiter.serve(c, order).ok(), "Pasta needs 2 units");
        assertEquals(CustomerState.ORDERED, c.getState());
    }

    @Test
    void serveWorksOnceReady() {
        Customer c = seatedCustomer(1);
        waiter.takeOrder(c, menu, 1, orders);
        Order order = orders.get(0);
        Chef chef = new Chef();
        while (!order.isReady()) {
            chef.work(order);
        }
        assertTrue(waiter.serve(c, order).ok());
        assertEquals(CustomerState.EATING, c.getState());
    }
    @Test
    void serveFailsWhenTheDishWasAlreadyServed() {
        Customer c = seatedCustomer(1);
        waiter.takeOrder(c, menu, 1, orders);
        Order order = orders.get(0);
        Chef chef = new Chef();
        while (!order.isReady()) {
            chef.work(order);
        }
        waiter.serve(c, order);

        ActionResult again = waiter.serve(c, order);
        assertFalse(again.ok());
        assertEquals("Customer 1's Salad has already been served.", again.message());
        assertEquals(CustomerState.EATING, c.getState(), "a failed action changes nothing");
    }
}
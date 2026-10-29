package restaurant.staff;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import restaurant.control.ActionResult;
import restaurant.customer.Customer;
import restaurant.customer.CustomerState;
import restaurant.customer.RegularCustomer;
import restaurant.place.Table;
import restaurant.place.WaitingQueue;

class HostTest {

    private final Host host = new Host();

    @Test
    void nobodyQueuingMeansNothingHappens() {
        Table table = new Table(1);
        ActionResult r = host.seatFirstInQueue(new WaitingQueue(), List.of(table));
        assertFalse(r.ok());
        assertTrue(table.isFree());
    }

    @Test
    void noFreeTableMeansNothingHappens() {
        Table table = new Table(1);
        table.assign(new RegularCustomer(9));
        WaitingQueue queue = new WaitingQueue();
        Customer c = new RegularCustomer(1);
        queue.add(c);

        assertFalse(host.seatFirstInQueue(queue, List.of(table)).ok());
        assertEquals(CustomerState.WAITING, c.getState());
        assertEquals(1, queue.size());
    }

    @Test
    void seatsTheFirstQueuedCustomerAtTheFirstFreeTable() {
        Table t1 = new Table(1);
        Table t2 = new Table(2);
        t1.assign(new RegularCustomer(9));
        WaitingQueue queue = new WaitingQueue();
        Customer first = new RegularCustomer(1);
        Customer second = new RegularCustomer(2);
        queue.add(first);
        queue.add(second);

        ActionResult r = host.seatFirstInQueue(queue, List.of(t1, t2));
        assertTrue(r.ok());
        assertEquals("Host seated customer 1 at table 2.", r.message());
        assertEquals(first, t2.getOccupant());
        assertEquals(CustomerState.SEATED, first.getState());
        assertEquals(List.of(second), queue.list(), "only one customer per turn");
    }
}
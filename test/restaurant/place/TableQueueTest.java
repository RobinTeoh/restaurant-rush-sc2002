package restaurant.place;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import restaurant.customer.Customer;
import restaurant.customer.RegularCustomer;

class TableQueueTest {

    @Test
    void newTableIsFree() {
        Table table = new Table(1);
        assertTrue(table.isFree());
        assertNull(table.getOccupant());
    }

    @Test
    void tableCanBeAssignedAndReleased() {
        Table table = new Table(1);
        Customer c = new RegularCustomer(1);
        table.assign(c);
        assertFalse(table.isFree());
        assertEquals(c, table.getOccupant());
        table.release();
        assertTrue(table.isFree());
    }

    @Test
    void occupiedTableRefusesASecondCustomer() {
        Table table = new Table(1);
        table.assign(new RegularCustomer(1));
        assertThrows(IllegalStateException.class, () -> table.assign(new RegularCustomer(2)));
    }

    @Test
    void queueKeepsArrivalOrder() {
        WaitingQueue queue = new WaitingQueue();
        Customer c1 = new RegularCustomer(1);
        Customer c2 = new RegularCustomer(2);
        queue.add(c1);
        queue.add(c2);
        assertEquals(List.of(c1, c2), queue.list());
    }

    @Test
    void anyCustomerCanBeRemovedFromTheMiddle() {
        WaitingQueue queue = new WaitingQueue();
        Customer c1 = new RegularCustomer(1);
        Customer c2 = new RegularCustomer(2);
        Customer c3 = new RegularCustomer(3);
        queue.add(c1);
        queue.add(c2);
        queue.add(c3);
        assertTrue(queue.remove(c2));
        assertEquals(List.of(c1, c3), queue.list());
    }

    @Test
    void sameCustomerCannotBeQueuedTwice() {
        WaitingQueue queue = new WaitingQueue();
        Customer c = new RegularCustomer(1);
        queue.add(c);
        assertThrows(IllegalStateException.class, () -> queue.add(c));
    }

    @Test
    void listCannotBeChangedFromOutside() {
        WaitingQueue queue = new WaitingQueue();
        queue.add(new RegularCustomer(1));
        assertThrows(UnsupportedOperationException.class, () -> queue.list().clear());
    }
}
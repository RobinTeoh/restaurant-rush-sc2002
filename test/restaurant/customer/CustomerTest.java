package restaurant.customer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CustomerTest {

    @Test
    void newCustomerIsWaitingWithFullSatisfaction() {
        Customer c = new Customer(1);
        assertEquals(1, c.getId());
        assertEquals(CustomerState.WAITING, c.getState());
        assertEquals(100, c.getSatisfaction());
        assertFalse(c.hasLeft());
    }

    @Test
    void seatMovesWaitingToSeated() {
        Customer c = new Customer(1);
        c.seat();
        assertEquals(CustomerState.SEATED, c.getState());
    }

    @Test
    void waitingCustomerLoses8PerTurn() {
        Customer c = new Customer(1);
        c.loseSatisfaction();
        assertEquals(92, c.getSatisfaction());
    }

    @Test
    void seatedCustomerAlsoLosesSatisfaction() {
        // Brief §3.3 turn 1: seated, not served -> 92 at turn end.
        Customer c = new Customer(1);
        c.seat();
        c.loseSatisfaction();
        assertEquals(92, c.getSatisfaction());
    }

    @Test
    void customerLeftAloneForThirteenTurnsLeaves() {
        Customer c = new Customer(1);
        for (int turn = 1; turn <= 12; turn++) {
            c.loseSatisfaction();
        }
        assertEquals(4, c.getSatisfaction());
        assertFalse(c.hasLeft());

        c.loseSatisfaction();
        assertEquals(0, c.getSatisfaction());
        assertTrue(c.hasLeft());
        assertEquals(CustomerState.LEFT, c.getState());
    }

    @Test
    void customerWhoLeftNoLongerChanges() {
        Customer c = leaveAfterThirteenTurns();
        c.loseSatisfaction();
        assertEquals(0, c.getSatisfaction());
        assertEquals(CustomerState.LEFT, c.getState());
    }

    @Test
    void cannotSeatACustomerTwice() {
        Customer c = new Customer(1);
        c.seat();
        assertThrows(IllegalStateException.class, c::seat);
    }

    @Test
    void cannotSeatACustomerWhoLeft() {
        Customer c = leaveAfterThirteenTurns();
        assertThrows(IllegalStateException.class, c::seat);
    }

    @Test
    void laterStepsCannotBeSkipped() {
        Customer c = new Customer(1);
        assertThrows(IllegalStateException.class, c::serve);
        assertThrows(IllegalStateException.class, c::finishEating);
        assertThrows(IllegalStateException.class, c::pay);
        assertEquals(CustomerState.WAITING, c.getState());
    }

    @Test
    void recordedSatisfactionIsOnlyAvailableAfterPayment() {
        Customer c = new Customer(1);
        assertThrows(IllegalStateException.class, c::getRecordedSatisfaction);
    }

    private static Customer leaveAfterThirteenTurns() {
        Customer c = new Customer(1);
        for (int turn = 1; turn <= 13; turn++) {
            c.loseSatisfaction();
        }
        return c;
    }

    // To add once chooseFood(menu) exists (needs Ian's Menu and MenuItem):
    // - the full §3.3 Pasta example: 92, 84, 76, 76, 76, paid with 76 recorded
    // - an EATING or READY_TO_PAY customer does not lose satisfaction
    // - seed check: customers 1-6 give at most 2 Pasta, any 4 of the first 5 >= $45
}

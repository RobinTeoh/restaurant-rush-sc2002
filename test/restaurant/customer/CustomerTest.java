package restaurant.customer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import restaurant.menu.Menu;
import restaurant.menu.MenuItem;

class CustomerTest {

    @Test
    void newCustomerIsWaitingWithFullSatisfaction() {
        Customer c = new RegularCustomer(1);
        assertEquals(1, c.getId());
        assertEquals(CustomerState.WAITING, c.getState());
        assertEquals(100, c.getSatisfaction());
        assertFalse(c.hasLeft());
    }

    @Test
    void seatMovesWaitingToSeated() {
        Customer c = new RegularCustomer(1);
        c.seat();
        assertEquals(CustomerState.SEATED, c.getState());
    }

    @Test
    void waitingCustomerLoses8PerTurn() {
        Customer c = new RegularCustomer(1);
        c.loseSatisfaction();
        assertEquals(92, c.getSatisfaction());
    }

    @Test
    void seatedCustomerAlsoLosesSatisfaction() {
        // Brief §3.3 turn 1: seated, not served -> 92 at turn end.
        Customer c = new RegularCustomer(1);
        c.seat();
        c.loseSatisfaction();
        assertEquals(92, c.getSatisfaction());
    }

    @Test
    void customerLeftAloneForThirteenTurnsLeaves() {
        Customer c = new RegularCustomer(1);
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
        Customer c = new RegularCustomer(1);
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
        Customer c = new RegularCustomer(1);
        assertThrows(IllegalStateException.class, c::serve);
        assertThrows(IllegalStateException.class, c::finishEating);
        assertThrows(IllegalStateException.class, c::pay);
        assertEquals(CustomerState.WAITING, c.getState());
    }

    @Test
    void recordedSatisfactionIsOnlyAvailableAfterPayment() {
        Customer c = new RegularCustomer(1);
        assertThrows(IllegalStateException.class, c::getRecordedSatisfaction);
    }

    @Test
    void chooseFoodMovesSeatedToOrderedAndReturnsADishFromTheMenu() {
        Menu menu = Menu.standard();
        Customer c = new RegularCustomer(1);
        c.seat();
        MenuItem dish = c.chooseFood(menu);
        assertTrue(menu.getItems().contains(dish));
        assertEquals(CustomerState.ORDERED, c.getState());
    }

    @Test
    void chooseFoodNeedsASeatedCustomer() {
        Menu menu = Menu.standard();
        Customer waiting = new RegularCustomer(1);
        assertThrows(IllegalStateException.class, () -> waiting.chooseFood(menu));

        Customer ordered = new RegularCustomer(2);
        ordered.seat();
        ordered.chooseFood(menu);
        assertThrows(IllegalStateException.class, () -> ordered.chooseFood(menu));
    }

    @Test
    void chooseFoodRejectsAMissingMenu() {
        Customer c = new RegularCustomer(1);
        c.seat();
        assertThrows(IllegalArgumentException.class, () -> c.chooseFood(null));
        assertEquals(CustomerState.SEATED, c.getState());
    }

    @Test
    void pastaExampleFromTheBrief() {
        // Brief §3.3. With SEED = 32, customer 3 orders Pasta (see FoodChoiceTest).
        Menu menu = Menu.standard();
        Customer c = new RegularCustomer(3);

        // Turn 1: arrives, seated.
        c.seat();
        c.loseSatisfaction();
        assertEquals(92, c.getSatisfaction());

        // Turn 2: order taken (Chef does unit 1 of 2).
        assertEquals("Pasta", c.chooseFood(menu).getName());
        c.loseSatisfaction();
        assertEquals(84, c.getSatisfaction());

        // Turn 3: Pasta becomes READY but cannot be served yet; the customer is still waiting.
        c.loseSatisfaction();
        assertEquals(76, c.getSatisfaction());

        // Turn 4: served, no further loss.
        c.serve();
        c.loseSatisfaction();
        assertEquals(76, c.getSatisfaction());
        assertEquals(CustomerState.EATING, c.getState());

        // Turn 5: finishes eating, still no loss.
        c.finishEating();
        c.loseSatisfaction();
        assertEquals(76, c.getSatisfaction());
        assertEquals(CustomerState.READY_TO_PAY, c.getState());

        // Turn 6: Cashier collects; satisfaction 76 is recorded.
        c.pay();
        assertEquals(CustomerState.PAID, c.getState());
        assertEquals(76, c.getRecordedSatisfaction());
    }

    private static Customer leaveAfterThirteenTurns() {
        Customer c = new RegularCustomer(1);
        for (int turn = 1; turn <= 13; turn++) {
            c.loseSatisfaction();
        }
        return c;
    }
}

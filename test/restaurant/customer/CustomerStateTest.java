package restaurant.customer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CustomerStateTest {

    @Test
    void customersWaitingForATableOrFoodLoseSatisfaction() {
        assertTrue(CustomerState.WAITING.losesSatisfaction());
        assertTrue(CustomerState.SEATED.losesSatisfaction());
        assertTrue(CustomerState.ORDERED.losesSatisfaction());
    }

    @Test
    void servedPaidOrGoneCustomersDoNotLoseSatisfaction() {
        assertFalse(CustomerState.EATING.losesSatisfaction());
        assertFalse(CustomerState.READY_TO_PAY.losesSatisfaction());
        assertFalse(CustomerState.PAID.losesSatisfaction());
        assertFalse(CustomerState.LEFT.losesSatisfaction());
    }
}

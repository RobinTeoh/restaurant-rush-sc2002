package restaurant.customer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.function.IntFunction;

import org.junit.jupiter.api.Test;

import restaurant.menu.Menu;

// any customer type can be used wherever a Customer is expected (Liskov),
// so the same steps run on all 3 types through the parent type only
class CustomerSubstitutionTest {
    private static final List<IntFunction<Customer>> TYPES =
            List.of(RegularCustomer::new, VIPCustomer::new, CriticCustomer::new);

    @Test
    void everyTypeGoesThroughTheSameFullVisit() {
        Menu menu = Menu.standard();
        for (IntFunction<Customer> type : TYPES) {
            Customer c = type.apply(1);
            assertEquals(CustomerState.WAITING, c.getState());
            assertEquals(100, c.getSatisfaction());

            c.seat();
            c.loseSatisfaction();
            assertTrue(menu.getItems().contains(c.chooseFood(menu)));
            c.loseSatisfaction();
            int satisfactionWhenServed = c.getSatisfaction();
            c.serve();
            c.loseSatisfaction();
            c.finishEating();
            c.loseSatisfaction();
            c.pay();

            assertEquals(CustomerState.PAID, c.getState());
            assertEquals(satisfactionWhenServed, c.getRecordedSatisfaction(), c.getTypeName());
            assertFalse(c.hasLeft());
        }
    }

    @Test
    void everyTypeRefusesTheSameIllegalSteps() {
        for (IntFunction<Customer> type : TYPES) {
            Customer c = type.apply(1);
            assertThrows(IllegalStateException.class, c::serve, c.getTypeName());
            assertThrows(IllegalStateException.class, c::finishEating, c.getTypeName());
            assertThrows(IllegalStateException.class, c::pay, c.getTypeName());
            assertThrows(IllegalStateException.class, c::getRecordedSatisfaction, c.getTypeName());
            c.seat();
            assertThrows(IllegalStateException.class, c::seat, c.getTypeName());
        }
    }

    @Test
    void everyTypeLeavesAtZeroAndThenStopsChanging() {
        for (IntFunction<Customer> type : TYPES) {
            Customer c = type.apply(1);
            for (int turn = 1; turn <= 25; turn++) {
                c.loseSatisfaction();
            }
            assertEquals(0, c.getSatisfaction(), c.getTypeName());
            assertTrue(c.hasLeft(), c.getTypeName());
            assertThrows(IllegalStateException.class, c::seat, c.getTypeName());
        }
    }
}

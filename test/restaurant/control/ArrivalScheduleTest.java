package restaurant.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import restaurant.customer.Customer;
import restaurant.customer.RegularCustomer;
import restaurant.customer.VIPCustomer;

class ArrivalScheduleTest {

    @Test
    void turnsOnlyMeansEveryoneIsRegularWithIdsInArrivalOrder() {
        ArrivalSchedule schedule = new ArrivalSchedule(GameConfig.ARRIVAL_TURNS);
        int expectedId = 1;
        for (int turn = 1; turn <= GameConfig.TOTAL_TURNS; turn++) {
            for (Customer c : schedule.arrivalsOn(turn)) {
                assertEquals(expectedId++, c.getId());
                assertEquals("Regular", c.getTypeName());
            }
        }
        assertEquals(7, expectedId);
        assertEquals(6, schedule.totalArrivals());
    }

    @Test
    void eachCustomerArrivesOnItsOwnTurn() {
        Customer first = new RegularCustomer(1);
        Customer second = new VIPCustomer(2);
        ArrivalSchedule schedule = new ArrivalSchedule(List.of(1, 4), List.of(first, second));
        assertEquals(List.of(first), schedule.arrivalsOn(1));
        assertEquals(List.of(second), schedule.arrivalsOn(4));
        assertTrue(schedule.arrivalsOn(2).isEmpty());
    }

    @Test
    void twoCustomersCanArriveOnTheSameTurn() {
        Customer a = new RegularCustomer(1);
        Customer b = new RegularCustomer(2);
        ArrivalSchedule schedule = new ArrivalSchedule(List.of(3, 3), List.of(a, b));
        assertEquals(List.of(a, b), schedule.arrivalsOn(3));
    }

    @Test
    void badInputIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new ArrivalSchedule(null));
        assertThrows(IllegalArgumentException.class, () -> new ArrivalSchedule(List.of(1), null));
        assertThrows(IllegalArgumentException.class,
                () -> new ArrivalSchedule(List.of(1, 4), List.of(new RegularCustomer(1))));
    }
}

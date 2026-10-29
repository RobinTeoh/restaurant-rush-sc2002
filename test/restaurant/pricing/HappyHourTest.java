package restaurant.pricing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import restaurant.customer.Customer;
import restaurant.customer.RegularCustomer;
import restaurant.menu.Menu;

class HappyHourTest {

    @Test
    void offAtStart() {
        HappyHour hh = new HappyHour();
        assertFalse(hh.isActive());
        assertTrue(hh.canActivate());
        assertEquals(0, hh.getTurnsLeft());
    }

    @Test
    void lastsTwoTurns() {
        HappyHour hh = new HappyHour();
        hh.activate();
        assertEquals(2, hh.getTurnsLeft());
        hh.endTurn();
        assertTrue(hh.isActive());
        assertEquals(1, hh.getTurnsLeft());
        hh.endTurn();
        assertFalse(hh.isActive());
    }

    @Test
    void onlyOncePerGame() {
        HappyHour hh = new HappyHour();
        hh.activate();
        assertThrows(IllegalStateException.class, hh::activate);
        hh.endTurn();
        hh.endTurn();
        assertFalse(hh.canActivate());
        assertThrows(IllegalStateException.class, hh::activate);
    }

    @Test
    void twentyPercentOff() {
        HappyHour hh = new HappyHour();
        hh.activate();
        assertEquals(1080, hh.applyTo(1350)); // vip burger
        assertEquals(1440, hh.applyTo(1800)); // pasta
    }

    @Test
    void waitingCustomersRecover15() {
        Customer waiting = new RegularCustomer(1);
        Customer eating = new RegularCustomer(2);
        for (int i = 0; i < 3; i++) {
            waiting.loseSatisfaction();
            eating.loseSatisfaction();
        }
        eating.seat();
        eating.chooseFood(Menu.standard());
        eating.serve();

        HappyHour hh = new HappyHour();
        hh.activate();
        hh.recoverWaiting(List.of(waiting, eating));
        assertEquals(91, waiting.getSatisfaction()); // 76 + 15
        assertEquals(76, eating.getSatisfaction());
    }

    @Test
    void recoveryCappedAt100() {
        Customer c = new RegularCustomer(1);
        c.loseSatisfaction();
        HappyHour hh = new HappyHour();
        hh.activate();
        hh.recoverWaiting(List.of(c));
        assertEquals(100, c.getSatisfaction());
    }

    @Test
    void noRecoveryWhenOff() {
        Customer c = new RegularCustomer(1);
        c.loseSatisfaction();
        new HappyHour().recoverWaiting(List.of(c));
        assertEquals(92, c.getSatisfaction());
    }

    @Test
    void noDiscountWhenOff() {
        HappyHour hh = new HappyHour();
        assertEquals(1800, hh.applyTo(1800));
        hh.endTurn();
        assertEquals(0, hh.getTurnsLeft());
    }
}

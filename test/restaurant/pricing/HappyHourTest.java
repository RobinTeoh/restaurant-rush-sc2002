package restaurant.pricing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

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
    void noDiscountWhenOff() {
        HappyHour hh = new HappyHour();
        assertEquals(1800, hh.applyTo(1800));
        hh.endTurn();
        assertEquals(0, hh.getTurnsLeft());
    }
}

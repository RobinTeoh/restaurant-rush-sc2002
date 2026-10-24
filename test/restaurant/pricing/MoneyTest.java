package restaurant.pricing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class MoneyTest {

    @Test
    void formatsCentsAsDollarsWithTwoDecimals() {
        assertEquals("$18.00", Money.format(1800));
        assertEquals("$9.00", Money.format(900));
        assertEquals("$0.05", Money.format(5));
        assertEquals("$0.00", Money.format(0));
        assertEquals("$24.30", Money.format(2430));
    }

    @Test
    void negativeAmountIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> Money.format(-1));
    }

    @Test
    void percentOffMatchesTheBriefsVipExamples() {
        assertEquals(1350, Money.percentOff(1500, 10)); // VIP Burger $13.50
        assertEquals(810, Money.percentOff(900, 10));   // VIP Salad $8.10
        assertEquals(1620, Money.percentOff(1800, 10)); // VIP Pasta $16.20
        assertEquals(1080, Money.percentOff(1350, 20)); // VIP Burger in Happy Hour (stage 3), D1 order
    }

    @Test
    void percentOffRoundsHalfUpToTheCent() {
        assertEquals(95, Money.percentOff(105, 10));    // 94.5 -> 95
        assertEquals(94, Money.percentOff(104, 10));    // 93.6 -> 94
        assertEquals(1500, Money.percentOff(1500, 0));
        assertEquals(0, Money.percentOff(1500, 100));
    }

    @Test
    void percentOffRejectsBadInput() {
        assertThrows(IllegalArgumentException.class, () -> Money.percentOff(-1, 10));
        assertThrows(IllegalArgumentException.class, () -> Money.percentOff(100, -1));
        assertThrows(IllegalArgumentException.class, () -> Money.percentOff(100, 101));
    }
}

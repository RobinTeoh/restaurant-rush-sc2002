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
}

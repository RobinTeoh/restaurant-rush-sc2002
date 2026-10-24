package restaurant.pricing;

/**
 * Money helpers. All amounts in the game are integer cents; this is the one
 * place that turns cents into the dollar text the player sees.
 */
public final class Money {

    private Money() {
        // only static helpers
    }

    /** 1800 -> "$18.00", 5 -> "$0.05". */
    public static String format(int cents) {
        if (cents < 0) {
            throw new IllegalArgumentException("cents must not be negative");
        }
        return String.format("$%d.%02d", cents / 100, cents % 100);
    }

    /** Takes percent off, rounded half-up to the nearest cent (DECISIONS D1). 1500, 10 -> 1350. */
    public static int percentOff(int cents, int percent) {
        if (cents < 0) {
            throw new IllegalArgumentException("cents must not be negative");
        }
        if (percent < 0 || percent > 100) {
            throw new IllegalArgumentException("percent must be 0 to 100");
        }
        return (cents * (100 - percent) + 50) / 100;
    }
}

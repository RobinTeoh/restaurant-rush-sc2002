package restaurant.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class GameStatsTest {

    /** Records payments given as pairs: bill in cents, then satisfaction. */
    private GameStats paid(int... billAndSatisfaction) {
        GameStats stats = new GameStats();
        for (int i = 0; i < billAndSatisfaction.length; i += 2) {
            stats.recordPayment(billAndSatisfaction[i], billAndSatisfaction[i + 1]);
        }
        return stats;
    }

    @Test
    void averageIsEmptyWhenNobodyPaid() {
        GameStats stats = new GameStats();
        assertTrue(stats.averageSatisfaction().isEmpty());
        assertFalse(stats.isVictory());
    }

    @Test
    void victoryWhenAllThreeTargetsAreMetExactly() {
        GameStats stats = paid(900, 60, 1500, 60, 1800, 60, 300, 60);
        assertEquals(4, stats.getPaidCount());
        assertEquals(4500, stats.getRevenueCents());
        assertTrue(stats.isVictory());
    }

    @Test
    void defeatWhenOnlyThreePaid() {
        assertFalse(paid(1800, 100, 1800, 100, 1800, 100).isVictory());
    }

    @Test
    void defeatWhenRevenueIsOneCentShort() {
        assertFalse(paid(900, 90, 1500, 90, 1800, 90, 299, 90).isVictory());
    }

    @Test
    void defeatWhenAverageIsJustBelowTarget() {
        assertFalse(paid(1800, 59, 1800, 59, 1800, 59, 1800, 59).isVictory());
    }

    @Test
    void departuresAreCountedButNeverAddRevenue() {
        GameStats stats = new GameStats();
        stats.recordDeparture();
        assertEquals(1, stats.getUnhappyDepartures());
        assertEquals(0, stats.getRevenueCents());
        assertEquals(0, stats.getPaidCount());
    }

    @Test
    void negativeBillIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new GameStats().recordPayment(-1, 50));
    }
}
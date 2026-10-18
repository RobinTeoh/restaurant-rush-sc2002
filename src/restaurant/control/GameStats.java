package restaurant.control;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

public class GameStats {
    private int revenueCents;
    private int unhappyDepartures;
    private final List<Integer> recordedSatisfactions = new ArrayList<>();


    public void recordPayment(int billCents, int satisfactionAtPayment) {
        if (billCents < 0) {
            throw new IllegalArgumentException("bill cannot be negative: " + billCents);
        }
        revenueCents += billCents;
        recordedSatisfactions.add(satisfactionAtPayment);
    }

    public void recordDeparture() {
        unhappyDepartures++;
    }

    public int getRevenueCents() {
        return revenueCents;
    }

    public int getPaidCount() {
        return recordedSatisfactions.size();
    }

    public int getUnhappyDepartures() {
        return unhappyDepartures;
    }



    public OptionalDouble averageSatisfaction() {
        return recordedSatisfactions.stream().mapToInt(Integer::intValue).average();

    }

    public boolean isVictory() {
        OptionalDouble average = averageSatisfaction();
        return getPaidCount() >= GameConfig.TARGET_PAID
            && revenueCents >= GameConfig.TARGET_REVENUE_CENTS
            && average.isPresent()
            && average.getAsDouble() >= GameConfig.TARGET_AVERAGE_SATISFACTION;
    }
    
}

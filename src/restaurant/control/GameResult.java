package restaurant.control;

import java.util.OptionalDouble;

public record GameResult (
    int customersPaid,
    int unhappyDepartures,
    int revenueCents,
    OptionalDouble averageSatisfaction,
    int turnsUsed,
    boolean victory) {
    
}

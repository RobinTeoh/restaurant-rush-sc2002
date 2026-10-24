package restaurant.control;

import java.util.List;
import java.util.OptionalDouble;

public record TurnSnapshot (
        int turn,
        int totalTurns,
        int revenueCents,
        OptionalDouble averageSatisfaction,
        List<Integer> queuedCustomerIds,
        List<TableLine> tables,
        List<CustomerLine> customers,
        List<String>staffTasks) {
    
    public record TableLine(int tableId, Integer customerId) { }

    public record CustomerLine(int id, String type, int satisfaction, String state, String order) { }

    
}

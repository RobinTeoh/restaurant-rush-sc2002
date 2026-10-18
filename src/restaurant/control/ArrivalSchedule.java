package restaurant.control;

import java.util.List;

public class ArrivalSchedule {
    private final List<Integer> arrivalTurns;

    public ArrivalSchedule(List<Integer> arrivalTurns) {
        if (arrivalTurns == null) {
            throw new IllegalArgumentException("arrivalTurns must not be null");
        }
        this.arrivalTurns = List.copyOf(arrivalTurns);
    }

    public int arrivalsOn(int turn) {
        int count = 0;
        for (int t : arrivalTurns) {
            if (t == turn) {
                count++;
            }
        }
        return count;
    }

    public int totalArrivals() {
        return arrivalTurns.size();
    }
}


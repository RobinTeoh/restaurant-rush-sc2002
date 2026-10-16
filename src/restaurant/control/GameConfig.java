package restaurant.control;

import java.util.List;

public final class GameConfig {
    private GameConfig() { }

    public static final int TOTAL_TURNS = 18;
    public static final int TABLE_COUNT = 2;
    public static final List<Integer> ARRIVAL_TURNS = List.of(1,4,7,10,13,16);

    public static final int TARGET_PAID = 4;
    public static final int TARGET_REVENUE_CENTS = 4500;
    public static final int TARGET_AVERAGE_SATISFACTION = 60;

}

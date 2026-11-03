package restaurant.control;

import java.util.List;
import java.util.Optional;

import restaurant.customer.CriticCustomer;
import restaurant.customer.Customer;
import restaurant.customer.RegularCustomer;
import restaurant.customer.VIPCustomer;
import restaurant.menu.Menu;

public final class GameConfig {
    private GameConfig() { }

    public static final int TOTAL_TURNS = 18;
    public static final int TABLE_COUNT = 2;
    public static final List<Integer> ARRIVAL_TURNS = List.of(1,4,7,10,13,16);

    public static final int TARGET_PAID = 4;
    public static final int TARGET_REVENUE_CENTS = 4500;
    public static final int TARGET_AVERAGE_SATISFACTION = 60;

    public static List<Customer> baseCustomers() {
        return List.of(new RegularCustomer(1), new VIPCustomer(2), new RegularCustomer(3),
                new CriticCustomer(4), new RegularCustomer(5), new RegularCustomer(6));
    }

    /** Scenario names the player can pass when starting the game, e.g. "sh run.sh stage1". */
    public static final String STAGE_1 = "stage1";
    public static final String STAGE_2 = "stage2";
    public static final String STAGE_3 = "stage3";
    public static final String DEFAULT_SCENARIO = STAGE_3;


    public static Optional<ArrivalSchedule> scheduleFor(String scenario) {
        if (STAGE_1.equals(scenario)) {
            return Optional.of(new ArrivalSchedule(ARRIVAL_TURNS));
        }
        if (STAGE_2.equals(scenario) || STAGE_3.equals(scenario)) {
            return Optional.of(new ArrivalSchedule(ARRIVAL_TURNS, baseCustomers()));
        }
        return Optional.empty();
    }

    /** The Host only works in Stage 3, so Stage 1 and 2 games play exactly as before. */
    public static boolean hostEnabled(String scenario) {
        return STAGE_3.equals(scenario);
    }

    public static boolean happyHourEnabled(String scenario) {
        return STAGE_3.equals(scenario);
    }

    public static Menu menuFor(String scenario) {
        return STAGE_3.equals(scenario) ? Menu.withCombos() : Menu.standard();
    }

}
package restaurant.control;

import java.util.List;

import restaurant.customer.CriticCustomer;
import restaurant.customer.Customer;
import restaurant.customer.RegularCustomer;
import restaurant.customer.VIPCustomer;

public final class GameConfig {
    private GameConfig() { }

    public static final int TOTAL_TURNS = 18;
    public static final int TABLE_COUNT = 2;
    public static final List<Integer> ARRIVAL_TURNS = List.of(1,4,7,10,13,16);

    public static final int TARGET_PAID = 4;
    public static final int TARGET_REVENUE_CENTS = 4500;
    public static final int TARGET_AVERAGE_SATISFACTION = 60;

    // base setting customers, VIP 2nd and Critic 4th (D6b), new objects every call so each game starts fresh
    public static List<Customer> baseCustomers() {
        return List.of(new RegularCustomer(1), new VIPCustomer(2), new RegularCustomer(3),
                new CriticCustomer(4), new RegularCustomer(5), new RegularCustomer(6));
    }

}

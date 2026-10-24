package restaurant.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import restaurant.customer.Customer;
import restaurant.menu.Menu;
import restaurant.menu.MenuItem;
import restaurant.order.Order;

// locks in the stage 2 base setting (D6b, D7), if anyone changes it these fail
class BaseSettingTest {
    private static final int PAYABLE_CUSTOMERS = 5; // the turn-16 customer can never pay

    @Test
    void vipIsSecondAndCriticIsFourth() {
        List<String> types = new ArrayList<>();
        for (Customer c : GameConfig.baseCustomers()) {
            types.add(c.getTypeName());
        }
        assertEquals(List.of("Regular", "VIP", "Regular", "Critic", "Regular", "Regular"), types);
    }

    @Test
    void idsAreOneToSixInArrivalOrder() {
        List<Customer> customers = GameConfig.baseCustomers();
        for (int i = 0; i < customers.size(); i++) {
            assertEquals(i + 1, customers.get(i).getId());
        }
        assertEquals(GameConfig.ARRIVAL_TURNS.size(), customers.size());
    }

    @Test
    void everyGameGetsFreshCustomers() {
        assertNotSame(GameConfig.baseCustomers().get(0), GameConfig.baseCustomers().get(0));
    }

    @Test
    void baseSettingDishesAndBills() {
        // the critic (4th) would get a Burger from the seed, but always orders the most expensive dish
        List<String> dishes = new ArrayList<>();
        List<Integer> bills = new ArrayList<>();
        for (Order o : ordersForEveryone()) {
            dishes.add(o.getItem().getName());
            bills.add(o.getBill());
        }
        assertEquals(List.of("Salad", "Burger", "Pasta", "Pasta", "Pasta", "Salad"), dishes);
        assertEquals(List.of(900, 1350, 1800, 1800, 1800, 900), bills);
    }

    @Test
    void anyFourOfTheFirstFiveStillReachTheRevenueTarget() {
        List<Order> orders = ordersForEveryone();
        int total = 0;
        int mostExpensive = 0;
        for (int i = 0; i < PAYABLE_CUSTOMERS; i++) {
            total += orders.get(i).getBill();
            mostExpensive = Math.max(mostExpensive, orders.get(i).getBill());
        }
        assertTrue(total - mostExpensive >= GameConfig.TARGET_REVENUE_CENTS);
    }

    private static List<Order> ordersForEveryone() {
        Menu menu = Menu.standard();
        List<Order> orders = new ArrayList<>();
        for (Customer c : GameConfig.baseCustomers()) {
            c.seat();
            MenuItem dish = c.chooseFood(menu);
            orders.add(Order.createOrder(c, dish, 1));
        }
        return orders;
    }
}

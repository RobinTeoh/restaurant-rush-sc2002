package restaurant.customer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import restaurant.menu.Menu;
import restaurant.menu.MenuItem;

/**
 * Locks in the base-setting food choices (DECISIONS D5/D6). If the seed or the menu
 * order changes, these tests fail and a new seed must be chosen and documented.
 */
class FoodChoiceTest {
    private static final int CUSTOMERS = 6;
    private static final int PAYABLE_CUSTOMERS = 5; // the turn-16 customer cannot pay (brief §3.4)
    private static final int REVENUE_TARGET_CENTS = 4500;

    @Test
    void baseSettingCustomersOrderTheseDishes() {
        assertEquals(List.of("Salad", "Burger", "Pasta", "Burger", "Pasta", "Salad"), dishNames());
    }

    @Test
    void sameCustomerAlwaysChoosesTheSameDish() {
        assertEquals(dishNames(), dishNames());
    }

    @Test
    void atMostTwoPastaPerGame() {
        long pasta = dishNames().stream().filter("Pasta"::equals).count();
        assertTrue(pasta <= 2, "D5 allows at most 2 Pasta, got " + pasta);
    }

    @Test
    void anyFourOfTheFirstFiveCustomersReachTheRevenueTarget() {
        List<MenuItem> dishes = dishes();
        int total = 0;
        int mostExpensive = 0;
        for (int i = 0; i < PAYABLE_CUSTOMERS; i++) {
            int price = dishes.get(i).getPriceCents();
            total += price;
            mostExpensive = Math.max(mostExpensive, price);
        }
        int worstFour = total - mostExpensive;
        assertTrue(worstFour >= REVENUE_TARGET_CENTS,
                "the cheapest 4 of the first 5 customers pay only " + worstFour + " cents");
    }

    private static List<MenuItem> dishes() {
        Menu menu = Menu.standard();
        List<MenuItem> dishes = new ArrayList<>();
        for (int id = 1; id <= CUSTOMERS; id++) {
            Customer c = new Customer(id);
            c.seat();
            dishes.add(c.chooseFood(menu));
        }
        return dishes;
    }

    private static List<String> dishNames() {
        List<String> names = new ArrayList<>();
        for (MenuItem dish : dishes()) {
            names.add(dish.getName());
        }
        return names;
    }
}

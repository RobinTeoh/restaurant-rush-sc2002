package restaurant.menu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import restaurant.customer.RegularCustomer;
import restaurant.order.Order;
import restaurant.staff.Chef;

class ComboMealTest {
    private static final MenuItem SALAD = new MenuItem("Salad", 900, 1);
    private static final MenuItem BURGER = new MenuItem("Burger", 1500, 1);
    private static final MenuItem PASTA = new MenuItem("Pasta", 1800, 2);

    @Test
    void saladAndPasta() {
        ComboMeal combo = new ComboMeal(SALAD, PASTA);
        assertEquals("Salad + Pasta", combo.getName());
        assertEquals(2430, combo.getPriceCents());
        assertEquals(3, combo.getPrepUnits());
        assertEquals("Salad + Pasta $24.30 (3 units)", combo.toString());
    }

    @Test
    void otherCombos() {
        assertEquals(2160, new ComboMeal(SALAD, BURGER).getPriceCents());
        assertEquals(2, new ComboMeal(SALAD, BURGER).getPrepUnits());
        assertEquals(2970, new ComboMeal(BURGER, PASTA).getPriceCents());
        assertEquals(3, new ComboMeal(BURGER, PASTA).getPrepUnits());
    }

    @Test
    void keepsBothItems() {
        assertEquals(List.of(SALAD, PASTA), new ComboMeal(SALAD, PASTA).getItems());
    }

    @Test
    void chefCooksComboLikeNormalDish() {
        Order order = Order.createOrder(new RegularCustomer(1), new ComboMeal(SALAD, PASTA), 1);
        Chef chef = new Chef();
        chef.work(order);
        chef.work(order);
        chef.work(order);
        assertTrue(order.isReady());
        assertEquals(2430, order.getBill());
    }
}

package restaurant.staff;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import restaurant.customer.Customer;
import restaurant.customer.RegularCustomer;
import restaurant.menu.Menu;
import restaurant.menu.MenuItem;
import restaurant.order.Order;
import restaurant.pricing.Money;

class ChefTest {
    private static final MenuItem PASTA = Menu.standard().getItems().get(2);

    @Test
    void pastaNeedsExactlyTwoChefTurnsAndBills1800() {
        // Development plan, Week 1 B: Pasta needs exactly 2 chef turns; bill $18.00.
        Chef chef = new Chef();
        Order o = Order.createOrder(new RegularCustomer(1), PASTA, 1);

        assertTrue(chef.work(o));
        assertFalse(o.isReady());

        assertTrue(chef.work(o));
        assertTrue(o.isReady());

        assertEquals(1800, o.getBill());
        assertEquals("$18.00", Money.format(o.getBill()));
    }

    @Test
    void workOnReadyOrderReturnsFalseWithNoChange() {
        Chef chef = new Chef();
        Order o = Order.createOrder(new RegularCustomer(1), PASTA, 1);
        chef.work(o);
        chef.work(o);
        assertFalse(chef.work(o));
        assertEquals(2, o.getUnitsDone());
    }

    @Test
    void nullOrderIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Chef().work(null));
    }
}

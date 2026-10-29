package restaurant.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import restaurant.customer.CriticCustomer;
import restaurant.customer.Customer;
import restaurant.customer.RegularCustomer;
import restaurant.customer.VIPCustomer;
import restaurant.menu.ComboMeal;
import restaurant.menu.MenuItem;
import restaurant.pricing.HappyHour;

class OrderTest {
    private static final MenuItem BURGER = new MenuItem("Burger", 1500, 1);
    private static final MenuItem PASTA = new MenuItem("Pasta", 1800, 2);

    @Test
    void newOrderIsPlacedWithNoWorkDone() {
        Customer c = new RegularCustomer(1);
        Order o = Order.createOrder(c, PASTA, 3);
        assertEquals(OrderStatus.PLACED, o.getStatus());
        assertEquals(0, o.getUnitsDone());
        assertEquals(3, o.getTurnPlaced());
        assertEquals(c, o.getCustomer());
        assertEquals(PASTA, o.getItem());
        assertFalse(o.isReady());
    }

    @Test
    void oneUnitDishIsReadyAfterOneUnit() {
        Order o = Order.createOrder(new RegularCustomer(1), BURGER, 1);
        assertTrue(o.prepareOneUnit());
        assertTrue(o.isReady());
    }

    @Test
    void readyOrderTakesNoMoreWork() {
        Order o = Order.createOrder(new RegularCustomer(1), BURGER, 1);
        o.prepareOneUnit();
        assertFalse(o.prepareOneUnit());
        assertEquals(1, o.getUnitsDone());
    }

    @Test
    void fullLifecyclePlacedReadyServedPaid() {
        Order o = Order.createOrder(new RegularCustomer(1), BURGER, 1);
        o.prepareOneUnit();
        o.markServed();
        assertEquals(OrderStatus.SERVED, o.getStatus());
        o.markPaid();
        assertEquals(OrderStatus.PAID, o.getStatus());
        assertEquals(1500, o.getBill());
    }

    @Test
    void cannotServeBeforeReady() {
        Order o = Order.createOrder(new RegularCustomer(1), PASTA, 1);
        o.prepareOneUnit();
        assertThrows(IllegalStateException.class, o::markServed);
    }

    @Test
    void cannotPayBeforeServed() {
        Order o = Order.createOrder(new RegularCustomer(1), BURGER, 1);
        o.prepareOneUnit();
        assertThrows(IllegalStateException.class, o::markPaid);
    }

    @Test
    void readyOrderIsCancelledWhenCustomerLeaves() {
        // DECISIONS D3: never served, never paid, no revenue.
        Order o = Order.createOrder(new RegularCustomer(1), BURGER, 1);
        o.prepareOneUnit();
        o.cancel();
        assertEquals(OrderStatus.CANCELLED, o.getStatus());
        assertFalse(o.prepareOneUnit());
        assertThrows(IllegalStateException.class, o::markServed);
        assertThrows(IllegalStateException.class, o::getBill);
    }

    @Test
    void placedOrderCanBeCancelled() {
        Order o = Order.createOrder(new RegularCustomer(1), PASTA, 1);
        o.cancel();
        assertEquals(OrderStatus.CANCELLED, o.getStatus());
    }

    @Test
    void servedOrderCannotBeCancelled() {
        Order o = Order.createOrder(new RegularCustomer(1), BURGER, 1);
        o.prepareOneUnit();
        o.markServed();
        assertThrows(IllegalStateException.class, o::cancel);
    }

    @Test
    void invalidArgumentsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> Order.createOrder(null, BURGER, 1));
        assertThrows(IllegalArgumentException.class, () -> Order.createOrder(new RegularCustomer(1), null, 1));
        assertThrows(IllegalArgumentException.class, () -> Order.createOrder(new RegularCustomer(1), BURGER, 0));
    }

    @Test
    void billUsesEachCustomersOwnDiscount() {
        assertEquals(1500, Order.createOrder(new RegularCustomer(1), BURGER, 1).getBill());
        assertEquals(1350, Order.createOrder(new VIPCustomer(2), BURGER, 1).getBill()); // brief: VIP 10% off
        assertEquals(1800, Order.createOrder(new CriticCustomer(3), PASTA, 1).getBill()); // critic pays full price
    }

    @Test
    void happyHourAfterVipDiscount() {
        HappyHour hh = new HappyHour();
        hh.activate();
        assertEquals(1080, Order.createOrder(new VIPCustomer(1), BURGER, 1, hh).getBill());
        assertEquals(1440, Order.createOrder(new RegularCustomer(2), PASTA, 1, hh).getBill());
    }

    @Test
    void happyHourOffIsNormalPrice() {
        HappyHour hh = new HappyHour();
        assertEquals(1350, Order.createOrder(new VIPCustomer(1), BURGER, 1, hh).getBill());
    }

    @Test
    void priceLockedAfterHappyHourEnds() {
        HappyHour hh = new HappyHour();
        hh.activate();
        Order o = Order.createOrder(new RegularCustomer(1), PASTA, 1, hh);
        hh.endTurn();
        hh.endTurn();
        assertEquals(1440, o.getBill());
    }

    @Test
    void oldOrderNotChangedByHappyHour() {
        HappyHour hh = new HappyHour();
        Order o = Order.createOrder(new RegularCustomer(1), PASTA, 1, hh);
        hh.activate();
        assertEquals(1800, o.getBill());
    }

    @Test
    void vipComboInHappyHour() {
        // 2430 -> 2187 -> 1750
        HappyHour hh = new HappyHour();
        hh.activate();
        ComboMeal combo = new ComboMeal(new MenuItem("Salad", 900, 1), PASTA);
        assertEquals(1750, Order.createOrder(new VIPCustomer(1), combo, 1, hh).getBill());
    }
}

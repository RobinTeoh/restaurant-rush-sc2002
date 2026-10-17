package restaurant.menu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class MenuTest {

    @Test
    void standardMenuMatchesTheBrief() {
        List<MenuItem> items = Menu.standard().getItems();
        assertEquals(3, items.size());
        assertItem(items.get(0), "Salad", 900, 1);
        assertItem(items.get(1), "Burger", 1500, 1);
        assertItem(items.get(2), "Pasta", 1800, 2);
    }

    @Test
    void itemsCannotBeChangedFromOutside() {
        Menu menu = Menu.standard();
        assertThrows(UnsupportedOperationException.class,
                () -> menu.getItems().add(new MenuItem("Soup", 500, 1)));
    }

    @Test
    void changingTheCallersListDoesNotChangeTheMenu() {
        List<MenuItem> list = new ArrayList<>();
        list.add(new MenuItem("Salad", 900, 1));
        Menu menu = new Menu(list);
        list.add(new MenuItem("Soup", 500, 1));
        assertEquals(1, menu.getItems().size());
    }

    @Test
    void emptyMenuIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Menu(List.of()));
    }

    @Test
    void invalidItemsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new MenuItem(" ", 900, 1));
        assertThrows(IllegalArgumentException.class, () -> new MenuItem("Salad", -1, 1));
        assertThrows(IllegalArgumentException.class, () -> new MenuItem("Salad", 900, 0));
    }

    @Test
    void itemShowsPriceInDollars() {
        assertEquals("Pasta $18.00 (2 units)", new MenuItem("Pasta", 1800, 2).toString());
        assertEquals("Salad $9.00 (1 unit)", new MenuItem("Salad", 900, 1).toString());
    }

    private static void assertItem(MenuItem item, String name, int priceCents, int prepUnits) {
        assertEquals(name, item.getName());
        assertEquals(priceCents, item.getPriceCents());
        assertEquals(prepUnits, item.getPrepUnits());
    }
}

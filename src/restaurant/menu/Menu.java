package restaurant.menu;

import java.util.List;

/**
 * The dishes the restaurant serves (Stage 1).
 * The list cannot be changed after the menu is created.
 */
public class Menu {
    private final List<MenuItem> items;

    public Menu(List<MenuItem> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("a menu needs at least one item");
        }
        this.items = List.copyOf(items); // copy, so the caller's list cannot change our menu
    }

    /**
     * The brief's menu. The order Salad, Burger, Pasta matters: Customer.chooseFood
     * picks by index with a fixed seed (DECISIONS D5/D6).
     */
    public static Menu standard() {
        return new Menu(List.of(
                new MenuItem("Salad", 900, 1),
                new MenuItem("Burger", 1500, 1),
                new MenuItem("Pasta", 1800, 2)));
    }

    /** The items in menu order. The list is read-only. */
    public List<MenuItem> getItems() {
        return items;
    }
}

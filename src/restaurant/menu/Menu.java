package restaurant.menu;

import java.util.List;

public class Menu {
    private final List<MenuItem> items;

    public Menu(List<MenuItem> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("a menu needs at least one item");
        }
        this.items = List.copyOf(items);
    }

    // order matters, customers pick by index with the seed (D5/D6)
    public static Menu standard() {
        return new Menu(List.of(
                new MenuItem("Salad", 900, 1),
                new MenuItem("Burger", 1500, 1),
                new MenuItem("Pasta", 1800, 2)));
    }

    // stage 3 menu: the three dishes then the three combos (D8)
    public static Menu withCombos() {
        MenuItem salad = new MenuItem("Salad", 900, 1);
        MenuItem burger = new MenuItem("Burger", 1500, 1);
        MenuItem pasta = new MenuItem("Pasta", 1800, 2);
        return new Menu(List.of(salad, burger, pasta,
                new ComboMeal(salad, burger),
                new ComboMeal(salad, pasta),
                new ComboMeal(burger, pasta)));
    }

    public List<MenuItem> getItems() {
        return items;
    }
}

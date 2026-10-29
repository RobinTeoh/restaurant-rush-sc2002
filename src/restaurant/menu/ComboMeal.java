package restaurant.menu;

import java.util.List;

import restaurant.pricing.Money;

// two menu items sold together, units added up and 10% off the total
public class ComboMeal extends MenuItem {
    private static final int DISCOUNT_PERCENT = 10;

    private final MenuItem first;
    private final MenuItem second;

    public ComboMeal(MenuItem first, MenuItem second) {
        super(first.getName() + " + " + second.getName(),
                Money.percentOff(first.getPriceCents() + second.getPriceCents(), DISCOUNT_PERCENT),
                first.getPrepUnits() + second.getPrepUnits());
        this.first = first;
        this.second = second;
    }

    public List<MenuItem> getItems() {
        return List.of(first, second);
    }
}

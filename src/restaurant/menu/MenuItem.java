package restaurant.menu;

import restaurant.pricing.Money;

/**
 * One dish on the menu (Stage 1).
 * Price is kept in integer cents so money never has floating-point errors.
 * A MenuItem never changes after it is created.
 */
public class MenuItem {
    private final String name;
    private final int priceCents;
    private final int prepUnits;

    public MenuItem(String name, int priceCents, int prepUnits) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        if (priceCents < 0) {
            throw new IllegalArgumentException("priceCents must not be negative");
        }
        if (prepUnits < 1) {
            throw new IllegalArgumentException("prepUnits must be at least 1");
        }
        this.name = name;
        this.priceCents = priceCents;
        this.prepUnits = prepUnits;
    }

    public String getName() {
        return name;
    }

    public int getPriceCents() {
        return priceCents;
    }

    /** How many Chef turns this dish needs before it is READY. */
    public int getPrepUnits() {
        return prepUnits;
    }

    /** e.g. "Pasta $18.00 (2 units)" */
    @Override
    public String toString() {
        return name + " " + Money.format(priceCents) + " (" + prepUnits
                + (prepUnits == 1 ? " unit)" : " units)");
    }
}

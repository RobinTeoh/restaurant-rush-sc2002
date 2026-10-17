package restaurant.place;

import restaurant.customer.Customer;

public class Table {
    private final int id;
    private Customer occupant;

    public Table(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public boolean isFree() {
        return occupant == null;
    }

    public Customer getOccupant() {
        return occupant;
    }

    public void assign(Customer customer) {

        if (customer == null) {
            throw new IllegalArgumentException("customer must not be null");

        }

        if (!isFree()) {
            throw new IllegalStateException("Table " + id + " is already occupied");
        }
        occupant = customer;
    }

    public void release() {
        occupant = null;
    }
}

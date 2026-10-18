package restaurant.place;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import restaurant.customer.Customer;

public class WaitingQueue {

    private final List<Customer> customers = new ArrayList<>();

    public void add(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("customer must not be null");
        }
        if (customers.contains(customer)) {
            throw new IllegalStateException("Customer " + customer.getId() + " is already in the queue");
        }
        customers.add(customer);
    }

    public boolean remove(Customer customer) {
        return customers.remove(customer);
    }

    public List<Customer> list() {
        return Collections.unmodifiableList(customers);

    }

    public boolean isEmpty() {
        return customers.isEmpty();
    }

    public int size() {
        return customers.size();
    }
    
}

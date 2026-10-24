package restaurant.control;

import java.util.ArrayList;
import java.util.List;

import restaurant.customer.Customer;
import restaurant.customer.RegularCustomer;

public class ArrivalSchedule {
    private final List<Integer> arrivalTurns;
    private final List<Customer> customers; // customers.get(i) arrives on arrivalTurns.get(i)

    // stage 1 setting, everyone is a regular customer with ids 1, 2, 3... in arrival order
    public ArrivalSchedule(List<Integer> arrivalTurns) {
        this(arrivalTurns, regularCustomers(arrivalTurns));
    }

    public ArrivalSchedule(List<Integer> arrivalTurns, List<Customer> customers) {
        if (arrivalTurns == null || customers == null) {
            throw new IllegalArgumentException("arrivalTurns and customers must not be null");
        }
        if (arrivalTurns.size() != customers.size()) {
            throw new IllegalArgumentException("need exactly one customer per arrival turn");
        }
        this.arrivalTurns = List.copyOf(arrivalTurns);
        this.customers = List.copyOf(customers);
    }

    public List<Customer> arrivalsOn(int turn) {
        List<Customer> arriving = new ArrayList<>();
        for (int i = 0; i < arrivalTurns.size(); i++) {
            if (arrivalTurns.get(i) == turn) {
                arriving.add(customers.get(i));
            }
        }
        return arriving;
    }

    public int totalArrivals() {
        return arrivalTurns.size();
    }

    private static List<Customer> regularCustomers(List<Integer> arrivalTurns) {
        if (arrivalTurns == null) {
            throw new IllegalArgumentException("arrivalTurns must not be null");
        }
        List<Customer> regulars = new ArrayList<>();
        for (int id = 1; id <= arrivalTurns.size(); id++) {
            regulars.add(new RegularCustomer(id));
        }
        return regulars;
    }
}

package restaurant.staff;

import java.util.List;

import restaurant.control.ActionResult;
import restaurant.customer.Customer;
import restaurant.customer.CustomerState;
import restaurant.menu.Menu;
import restaurant.menu.MenuItem;
import restaurant.order.Order;
import restaurant.place.Table;
import restaurant.place.WaitingQueue;

public class Waiter {
    

    public ActionResult seat(Customer customer, Table table, WaitingQueue queue) {
        if (customer == null) {
            return ActionResult.failure("Nobody is waiting in the queue.");
        }
        if (table == null || table.isFree()) {
            return ActionResult.failure("There is no free table.");

        }
        if (customer.getState() != CustomerState.WAITING) {
            return ActionResult.failure("Customer " + customer.getId() + " is not waiting for a table.");
        }
        queue.remove(customer);
        table.assign(customer);
        customer.seat();
        return ActionResult.success("Waiter seated customer " + customer.getId()
            + " at table " + table.getId() + ".");
    }

    public ActionResult takeOrder(Customer customer, Menu menu, int turn, List<Order> orders) {
        if (customer == null) {
            return ActionResult.failure("No customer is seated.");
        }
        if (customer.getState() != CustomerState.SEATED) {
            return ActionResult.failure("Customer " + customer.getId() + " has already ordered.");
        }
        MenuItem dish = customer.chooseFood(menu);
        orders.add(Order.createOrder(customer, dish, turn));
        return ActionResult.success("Customer " + customer.getId() + " ordered " + dish + ".");
    }

    public ActionResult serve(Customer customer, Order order) {
        if (customer == null) {
            return ActionResult.failure("No customer is seated.");
        }
        if (order == null) {
            return ActionResult.failure("Customer " + customer.getId() + " has not ordered yet.");
        }
        if (!order.isReady()) {
            return ActionResult.failure("Customer " + customer.getId() + "'s " + order.getItem().getName()
            + " is not READY yet (" + order.getUnitsDone() + "/"
            + order.getItem().getPrepUnits() + " units cooked).");   
        }
        order.markServed();
        customer.serve();
        return ActionResult.success("Waiter served " + order.getItem().getName()
            + " to customer " + customer.getId() + ".");
    }
}

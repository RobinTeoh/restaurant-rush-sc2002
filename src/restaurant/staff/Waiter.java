package restaurant.staff;

import java.util.List;

import restaurant.control.ActionResult;
import restaurant.customer.Customer;
import restaurant.customer.CustomerState;
import restaurant.menu.Menu;
import restaurant.menu.MenuItem;
import restaurant.order.Order;
import restaurant.order.OrderStatus;
import restaurant.place.Table;
import restaurant.place.WaitingQueue;
import restaurant.pricing.HappyHour;
import restaurant.pricing.Money;

public class Waiter {
    

    public ActionResult seat(Customer customer, Table table, WaitingQueue queue) {
        if (customer == null) {
            return ActionResult.failure("Nobody is waiting in the queue.");
        }
        if (table == null || !table.isFree()) {
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
        return takeOrder(customer, menu, turn, orders, null);
    }

    public ActionResult takeOrder(Customer customer, Menu menu, int turn, List<Order> orders,
                HappyHour happyHour) {
        if (customer == null) {
            return ActionResult.failure("No customer is seated.");
        }
        if (customer.getState() != CustomerState.SEATED) {
            return ActionResult.failure("Customer " + customer.getId() + " has already ordered.");
        }
        MenuItem dish = customer.chooseFood(menu);
        Order order = Order.createOrder(customer, dish, turn, happyHour);
        orders.add(order);
        String message = "Customer " + customer.getId() + " ordered " + dish + ".";
        if (happyHour != null && happyHour.isActive()) {
            message += " Happy Hour price locked at " + Money.format(order.getBill()) + ".";
        }
        return ActionResult.success(message);
    }
    public ActionResult serve(Customer customer, Order order) {
        if (customer == null) {
            return ActionResult.failure("No customer is seated.");
        }
        if (order == null) {
            return ActionResult.failure("Customer " + customer.getId() + " has not ordered yet.");
        }
        if (order.getStatus() == OrderStatus.SERVED || order.getStatus() == OrderStatus.PAID) {
            return ActionResult.failure("Customer " + customer.getId() + "'s "
                    + order.getItem().getName() + " has already been served.");  
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

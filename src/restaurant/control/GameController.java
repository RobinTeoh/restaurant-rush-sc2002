package restaurant.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import restaurant.customer.Customer;
import restaurant.customer.CustomerState;
import restaurant.menu.Menu;
import restaurant.order.Order;
import restaurant.order.OrderStatus;
import restaurant.place.Table;
import restaurant.place.WaitingQueue;
import restaurant.pricing.Money;
import restaurant.staff.Cashier;
import restaurant.staff.Chef;
import restaurant.staff.Waiter;

public class GameController {
    private static final List<String> WAITER_ACTIONS =
        List.of("Seat a customer", "Take an order", "Serve a dish", "Wait");
    private static final List<String> STAFF_TASKS = 
        List.of("Waiter: seat a customer / take an order / serve a READY dish / wait",
            "Chef: cook one order by one unit / wait",
            "Cashier: automatic, one payment per turn (oldest first)");
    
    private final GameView view;
    private final ArrivalSchedule schedule;
    private final Menu menu;
    private final List<Table> tables = new ArrayList<>();
    private final WaitingQueue queue = new WaitingQueue();
    private final Waiter waiter = new Waiter();
    private final Chef chef = new Chef();
    private final Cashier cashier = new Cashier();
    private final GameStats stats = new GameStats();

    private final List<Customer> customers = new ArrayList<>();
    private final List<Order> orders = new ArrayList<>();
    private final Map<Customer, Integer> servedOnTurn = new HashMap<>();

    private int turn;
    private int nextCustomerId = 1;

    public GameController(GameView view, ArrivalSchedule schedule, Menu menu, int tableCount) {
        this.view = view;
        this.schedule = schedule;
        this.menu = menu;
        for (int i = 1; i <= tableCount; i++) {
            tables.add(new Table(i));
        }
    }


    public GameResult play() {
        while (turn < GameConfig.TOTAL_TURNS) {
            playNextTurn();
        }
        GameResult result = currentResult();
        view.showResults(result, true);
        return result;
    }

    public void playNextTurn() {
        if (turn >= GameConfig.TOTAL_TURNS) {
            throw new IllegalStateException("The game has already finished");
        }
        turn++;
        arrivals();
        view.showTurnStart(snapshot());
        waiterPhase();
        chefPhase();
        paymentAndEating();
        satisfactionUpdate();
        view.showResults(currentResult(), false);
    }

    // Arrivals
    private void arrivals() {
        for (int i = 0; i < schedule.arrivalsOn(turn); i++) {
            Customer customer = new Customer(nextCustomerId++);
            customers.add(customer);
            queue.add(customer);
            view.showMessage("Customer " + customer.getId() + " arrives and joins the queue.");
        }
    }

    // Waiter
    private void waiterPhase() {
        while (true) {
            int choice = view.choose("What should the Waiter do?", WAITER_ACTIONS);
            ActionResult result = switch (choice) {
                case 0 -> seatAction();
                case 1 -> takeOrderAction();
                case 2 -> serveAction();
                default -> ActionResult.success("The Waiter waits.");
            };
            view.showMessage(result.message());
            if (result.ok()) {
                return;
            }
        }
    }

    private ActionResult seatAction() {
        Table table = firstFreeTable();
        List<Customer> waiting = queue.list();
        if (waiting.isEmpty() || table == null) {
            return waiter.seat(waiting.isEmpty() ? null : waiting.get(0), table, queue);
        }
        int pick = view.choose("Seat which customer?", labels(waiting));
        if (pick < 0) {
            return ActionResult.success("The Waiter waits.");
        }
        return waiter.seat(waiting.get(pick), table, queue);
    }

    private ActionResult takeOrderAction() {
        List<Customer> seated = seatedCustomers();
        if (seated.isEmpty()) {
            return waiter.takeOrder(null, menu, turn, orders);
        }
        int pick = view.choose("Take whose order?", labels(seated));
        if (pick < 0) {
            return ActionResult.success("The Waiter waits.");
        }
        return waiter.takeOrder(seated.get(pick), menu, turn, orders);
    }

    private ActionResult serveAction() {
        List<Customer> seated = seatedCustomers();
        if (seated.isEmpty()) {
            return waiter.serve(null, null);
        }
        int pick = view.choose("Serve which customer?", labels(seated));
        if (pick < 0) {
            return ActionResult.success("The Waiter waits.");
        }
        Customer customer = seated.get(pick);
        ActionResult result = waiter.serve(customer, orderOf(customer));
        if (result.ok()) {
            servedOnTurn.put(customer, turn);
        }
        return result;
    }

    // Chef
    private void chefPhase() {
        while (true) {
            List<Order> kitchen = ordersInKitchen();
            List<String> options = new ArrayList<>();
            for (Order o : kitchen) {
                options.add(orderLabel(o));
            }
            options.add("Wait");
            int choice = view.choose("Which order should the Chef cook?", options);
            if (choice < 0 || choice == kitchen.size()) {
                view.showMessage("The Chef waits.");
                return;
            }
            Order order = kitchen.get(choice);
            if (chef.work(order)) {
                view.showMessage("Chef cooked " + order.getItem().getName() + " for customer "
                + order.getCustomer().getId()
                + (order.isReady() ? ". It is READY" : " (" + order.getUnitsDone() + "/"
                + order.getItem().getPrepUnits() + " units)."));
                return; 
            }
            view.showMessage("The " + order.getItem().getName() + " for customer "
                    + order.getCustomer().getId() + " is already " + order.getStatus() + ". Choose another.");
        }
    }


    //Payment and eating
    private void paymentAndEating() {
        cashier.collect(tables, stats).ifPresent(o -> view.showMessage("Customer "
            + o.getCustomer().getId() + " paid " + Money.format(o.getBill()) + "."));
        for (Customer c : customers) {
            Integer served = servedOnTurn.get(c);
            if (c.getState() == CustomerState.EATING && served != null && served < turn) {
                c.finishEating();
                cashier.addToPaymentLine(orderOf(c));
                view.showMessage("Customer " + c.getId() + " finished eating and is ready to pay");
            }
        }
    }

    // satisfaction and departures

    private void satisfactionUpdate() {
        for (Customer c : customers) {
            if (c.hasLeft()) {
                continue;
            }
            c.loseSatisfaction();
            if (c.hasLeft()) {
                handleDeparture(c);
            }
        }
    }

    private void handleDeparture(Customer c) {
        queue.remove(c);
        for (Table t : tables) {
            if (t.getOccupant() == c) {
                t.release();
            }
        }
        Order order = orderOf(c);
        if (order != null && order.getStatus().canBeCancelled()) {
            order.cancel();
        }
        stats.recordDeparture();
        view.showMessage("Customer " + c.getId() + " reached 0 satisfaction and left unhappy.");
    }

    //helpers
    private Table firstFreeTable() {
        for (Table t : tables) {
            if (t.isFree()) {
                return t;
            }
        }
        return null;
    }

    private List<Customer> seatedCustomers() {
        List<Customer> seated = new ArrayList<>();
        for (Table t : tables) {
            if (!t.isFree()) {
                seated.add(t.getOccupant());
            }
        }
        return seated;
    }

    private Order orderOf(Customer c) {
        for (Order o : orders) {
            if (o.getCustomer() == c) {
                return o;
            }
        }
        return null;
    }

    private List<Order> ordersInKitchen() {
        List<Order> kitchen = new ArrayList<>();
        for (Order o : orders) {
            if (o.getStatus() == OrderStatus.PLACED || o.getStatus() == OrderStatus.READY) {
                kitchen.add(o);
            }
        }
        return kitchen;
    }

    private static List<String> labels(List<Customer> list) {
        List<String> labels = new ArrayList<>();
        for (Customer c : list) {
            labels.add("Customer " + c.getId() + " (satisfaction " + c.getSatisfaction() + ", " + c.getState() + ")");
        }
        return labels;
    }

    private static String orderLabel(Order o) {
        return "Customer " + o.getCustomer().getId() + ": "  + o.getItem().getName() + " ("
        + o.getStatus() + ", " + o.getUnitsDone() + "/" + o.getItem().getPrepUnits() + " units)";
    }

    private TurnSnapshot snapshot() {
        List<Integer> queued = new ArrayList<>();
        for (Customer c : queue.list()) {
            queued.add(c.getId());
        }
        List <TurnSnapshot.TableLine> tableLines = new ArrayList<>();
        for (Table t : tables) {
            tableLines.add(new TurnSnapshot.TableLine(t.getId(), t.isFree() ? null : t.getOccupant().getId()));
        }
        List<TurnSnapshot.CustomerLine> lines = new ArrayList<>();
        for (Customer c : customers) {
            if (c.hasLeft() || c.getState() == CustomerState.PAID) {
                continue;
            }
            Order o = orderOf(c);
            String order = o == null ? "none" : o.getItem().getName() + " (" + o.getStatus() + ", "
                + o.getUnitsDone() + "/" + o.getItem().getPrepUnits() + " units)";
            lines.add(new TurnSnapshot.CustomerLine(c.getId(), c.getSatisfaction(), c.getState().toString(), order));
        }
        return new TurnSnapshot(turn, GameConfig.TOTAL_TURNS, stats.getRevenueCents(), 
                stats.averageSatisfaction(), queued, tableLines, lines, STAFF_TASKS);
    }
    private GameResult currentResult() {
        return new GameResult(stats.getPaidCount(), stats.getUnhappyDepartures(),
            stats.getRevenueCents(), stats.averageSatisfaction(), turn, stats.isVictory());
    }
    //read only access for tests

    public int getTurn() {
        return turn;
    }

    public GameStats getStats() {
        return stats;
    }

    public List<Customer> getCustomers() {
        return List.copyOf(customers);
    }

    public List<Table> getTables() {
        return List.copyOf(tables);
    }






}

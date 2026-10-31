package restaurant.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import restaurant.customer.CriticCustomer;
import restaurant.customer.Customer;
import restaurant.customer.CustomerState;
import restaurant.customer.VIPCustomer;
import restaurant.customer.RegularCustomer;
import restaurant.menu.Menu;

/**
 * Plays real turns through the controller with scripted answers.
 * Waiter menu: 0 = seat, 1 = take order, 2 = serve, 3 = wait.
 * Chef menu: one option per order in the kitchen, then Wait last.
 */
class GameControllerTest {

    private GameController newGame(ScriptedView view) {
        return new GameController(view, new ArrivalSchedule(GameConfig.ARRIVAL_TURNS),
                Menu.standard(), GameConfig.TABLE_COUNT);
    }

    @Test
    void alwaysPlaysAllEighteenTurns() {
        ScriptedView view = new ScriptedView(); // no input at all
        GameResult result = newGame(view).play();

        assertEquals(18, result.turnsUsed());
        assertEquals(18, view.turnStarts);
        assertEquals(19, view.results.size(), "results after every turn, plus the final one");
        assertEquals(0, result.customersPaid());
        assertTrue(result.averageSatisfaction().isEmpty(), "N/A when nobody paid");
        assertFalse(result.victory());
    }

    @Test
    void ignoredCustomersLeaveAtZero() {
        // Turn-1 and turn-4 arrivals reach 0 at the end of turns 13 and 16; the rest run out of turns.
        GameResult result = newGame(new ScriptedView()).play();
        assertEquals(2, result.unhappyDepartures());
    }

    @Test
    void invalidChoiceIsExplainedAndDoesNotUseUpTheTask() {
        ScriptedView view = new ScriptedView(
                1,      // take order: nobody seated, so invalid
                0, 0,   // asked again: seat customer 1
                0);     // Chef: Wait (the only option)
        GameController game = newGame(view);
        game.playNextTurn();

        assertTrue(view.messages.contains("No customer is seated."));
        assertEquals(CustomerState.SEATED, game.getCustomers().get(0).getState());
    }

    @Test
    void saladCustomerPaysTwoTurnsAfterBeingServed() {
        ScriptedView view = new ScriptedView(
                0, 0, 0,  // turn 1: seat customer 1; Chef waits
                1, 0, 0,  // turn 2: take order (Salad); Chef cooks it, READY
                2, 0, 0); // turn 3: serve; Chef waits
        GameController game = newGame(view);

        game.playNextTurn();
        Customer c = game.getCustomers().get(0);
        assertEquals(92, c.getSatisfaction());
        game.playNextTurn();
        assertEquals(84, c.getSatisfaction(), "a READY dish that isn't served still costs satisfaction");
        game.playNextTurn();
        assertEquals(CustomerState.EATING, c.getState());
        game.playNextTurn();
        assertEquals(CustomerState.READY_TO_PAY, c.getState());
        assertEquals(0, game.getStats().getPaidCount(), "can't pay on the turn eating finished");
        game.playNextTurn();
        assertEquals(CustomerState.PAID, c.getState());
        assertEquals(900, game.getStats().getRevenueCents());
        assertTrue(game.getTables().get(0).isFree());
    }

    /** Brief §3.3: a Pasta customer goes 92, 84, 76, 76, 76 and pays on its 6th turn. */
    @Test
    void pastaExampleFromTheBrief() {
        // Customer 3 (arrives on turn 7) orders Pasta with the agreed seed.
        ScriptedView view = new ScriptedView(
                3, 0, 3, 0, 3, 0, 3, 0, 3, 0, 3, 0, // turns 1-6: Waiter waits, Chef waits
                0, 2, 0,  // turn 7: seat the 3rd customer in the queue (customer 3); Chef waits
                1, 0, 0,  // turn 8: take order (Pasta); Chef cooks unit 1
                3, 0,     // turn 9: Waiter waits; Chef cooks unit 2, READY
                2, 0, 0,  // turn 10: serve; Chef waits
                3, 0,     // turn 11: wait (customer becomes READY_TO_PAY)
                3, 0);    // turn 12: wait (Cashier collects)
        GameController game = newGame(view);
        for (int t = 1; t <= 6; t++) {
            game.playNextTurn();
        }
        int[] expected = {92, 84, 76, 76, 76};
        for (int satisfaction : expected) {
            game.playNextTurn();
            assertEquals(satisfaction, game.getCustomers().get(2).getSatisfaction());
        }
        game.playNextTurn(); // turn 12, the customer's 6th turn
        Customer pasta = game.getCustomers().get(2);
        assertEquals(CustomerState.PAID, pasta.getState());
        assertEquals(1800, game.getStats().getRevenueCents());
        assertEquals(76.0, game.getStats().averageSatisfaction().getAsDouble());
    }

    @Test
    void leavingCustomerFreesTableAndCountsAsUnhappy() {
        ScriptedView view = new ScriptedView(0, 0, 0); // turn 1: seat customer 1, then never serve
        GameController game = newGame(view);
        for (int t = 1; t <= 13; t++) {
            game.playNextTurn();
        }
        assertTrue(game.getCustomers().get(0).hasLeft());
        assertTrue(game.getTables().get(0).isFree());
        assertEquals(1, game.getStats().getUnhappyDepartures());
    }

    @Test
    void turnSixteenCustomerCannotPayEvenWhenServedAsFastAsPossible() {
        List<Integer> answers = new ArrayList<>();
        for (int t = 1; t <= 15; t++) {
            answers.addAll(List.of(3, 0)); // waiter waits, chef waits
        }
        answers.addAll(List.of(0, 4, 0)); // turn 16: seat customer 6 (last of 5 in the queue)
        answers.addAll(List.of(1, 0, 0)); // turn 17: take the order, chef makes the Salad READY
        answers.addAll(List.of(2, 0, 0)); // turn 18: serve it
        GameController game = newGame(new ScriptedView(answers.stream().mapToInt(Integer::intValue).toArray()));
        game.play();

        Customer sixth = game.getCustomers().get(5);
        assertEquals(CustomerState.EATING, sixth.getState(), "served on turn 18, would only pay on turn 20");
        assertEquals(0, game.getStats().getPaidCount());
    }

    // stage 2 base setting

    private GameController baseGame(ScriptedView view) {
        return new GameController(view, new ArrivalSchedule(GameConfig.ARRIVAL_TURNS, GameConfig.baseCustomers()),
                Menu.standard(), GameConfig.TABLE_COUNT);
    }

    @Test
    void baseGameSeatsVipSecondAndCriticFourth() {
        GameController game = baseGame(new ScriptedView());
        game.play();
        List<String> types = new ArrayList<>();
        for (Customer c : game.getCustomers()) {
            types.add(c.getTypeName());
        }
        assertEquals(List.of("Regular", "VIP", "Regular", "Critic", "Regular", "Regular"), types);
        assertTrue(game.getCustomers().get(3).hasLeft(), "ignored critic (turn 10) loses 12 a turn and is gone by turn 18");
        assertTrue(game.getCustomers().get(0).hasLeft(), "ignored regular from turn 1 leaves on turn 13");
        assertFalse(game.getCustomers().get(1).hasLeft(), "ignored vip only loses 5 a turn");
        assertEquals(2, game.getStats().getUnhappyDepartures());
    }

    @Test
    void vipPaysTheDiscountedBill() {
        // one VIP on turn 1, same steps as the salad test above
        ScriptedView view = new ScriptedView(0, 0, 0, 1, 0, 0, 2, 0, 0);
        GameController game = new GameController(view,
                new ArrivalSchedule(List.of(1), List.of(new VIPCustomer(1))), Menu.standard(), GameConfig.TABLE_COUNT);
        for (int t = 1; t <= 5; t++) {
            game.playNextTurn();
        }
        assertEquals(810, game.getStats().getRevenueCents(), "Salad $9.00 less 10%");
        assertEquals(90.0, game.getStats().averageSatisfaction().getAsDouble(), "vip lost 5 on two waiting turns");
    }

    @Test
    void criticOrdersPastaInTheGame() {
        ScriptedView view = new ScriptedView(0, 0, 0, 1, 0, 0); // seat, then take the order
        GameController game = new GameController(view,
                new ArrivalSchedule(List.of(1), List.of(new CriticCustomer(1))), Menu.standard(), GameConfig.TABLE_COUNT);
        game.playNextTurn();
        game.playNextTurn();
        assertTrue(view.messages.contains("Customer 1 ordered Pasta $18.00 (2 units)."));
        assertEquals(76, game.getCustomers().get(0).getSatisfaction(), "critic lost 12 on two waiting turns");
    }
        @Test
    void hostSeatsBeforeTheWaiterSoTheWaiterCanTakeTheOrderSameTurn() {
        ScriptedView view = new ScriptedView(
                1, 0,   // Waiter: take order from the customer the Host just seated
                0);     // Chef: cook it
        GameController game = new GameController(view, new ArrivalSchedule(GameConfig.ARRIVAL_TURNS),
                Menu.standard(), GameConfig.TABLE_COUNT, true);
        game.playNextTurn();

        assertTrue(view.messages.contains("Host seated customer 1 at table 1."));
        assertEquals(CustomerState.ORDERED, game.getCustomers().get(0).getState());
    }

    @Test
    void withoutTheHostNobodyIsSeatedAutomatically() {
        GameController game = newGame(new ScriptedView());
        game.playNextTurn();
        assertEquals(CustomerState.WAITING, game.getCustomers().get(0).getState());
    }

    // ---- Stage 3: Host (to-do 6) ----

    @Test
    void waiterCanStillSeatACustomerAfterTheHost() {
        ScriptedView view = new ScriptedView(
                0, 0,   // Waiter: seat the customer still queuing
                0);     // Chef: Wait
        GameController game = new GameController(view,
                new ArrivalSchedule(List.of(1, 1), List.of(new RegularCustomer(1), new RegularCustomer(2))),
                Menu.standard(), GameConfig.TABLE_COUNT, true);
        game.playNextTurn();

        assertEquals(CustomerState.SEATED, game.getCustomers().get(0).getState(), "seated by the Host");
        assertEquals(CustomerState.SEATED, game.getCustomers().get(1).getState(), "seated by the Waiter");
    }

    @Test
    void hostSeatsAtMostOneCustomerPerTurn() {
        ScriptedView view = new ScriptedView(); // Waiter and Chef wait
        GameController game = new GameController(view,
                new ArrivalSchedule(List.of(1, 1), List.of(new RegularCustomer(1), new RegularCustomer(2))),
                Menu.standard(), GameConfig.TABLE_COUNT, true);
        game.playNextTurn();

        assertEquals(CustomerState.SEATED, game.getCustomers().get(0).getState());
        assertEquals(CustomerState.WAITING, game.getCustomers().get(1).getState(),
                "a second table is free, but the Host seats only one per turn");
    }

    // ---- Stage 3: Happy Hour (to-do 2-5) ----
    // With Happy Hour on, every turn first asks: 0 = Continue, 1 = Start Happy Hour.

    private GameController happyHourGame(ScriptedView view, Customer customer) {
        return new GameController(view, new ArrivalSchedule(List.of(1), List.of(customer)),
                Menu.standard(), GameConfig.TABLE_COUNT, false, true);
    }

    @Test
    void happyHourOnlyOncePerGameLastsTwoTurnsAndRecoversWaitingCustomers() {
        ScriptedView view = new ScriptedView(
                1, 3, 0,      // turn 1: start Happy Hour; Waiter waits; Chef waits
                1, 0, 3, 0,   // turn 2: try again -> refused and asked again -> Continue; waits
                0, 3, 0);     // turn 3: Continue; waits
        GameController game = happyHourGame(view, new RegularCustomer(1));
        Customer c;

        game.playNextTurn();
        c = game.getCustomers().get(0);
        assertTrue(view.messages.contains("Happy Hour started: 20% off orders taken this turn and next."));
        assertEquals(92, c.getSatisfaction());

        game.playNextTurn();
        assertTrue(view.messages.contains("Happy Hour has already been used this game."));
        assertEquals(92, c.getSatisfaction(), "recovered 92 -> 100 (capped), then lost 8");

        game.playNextTurn();
        assertEquals(84, c.getSatisfaction(), "Happy Hour is over, so no recovery on turn 3");
    }

    @Test
    void remainingHappyHourTurnsAreShownAtTurnStart() {
        ScriptedView view = new ScriptedView(1, 3, 0, 0, 3, 0, 0, 3, 0);
        GameController game = happyHourGame(view, new RegularCustomer(1));
        game.playNextTurn();
        game.playNextTurn();
        game.playNextTurn();

        assertEquals("Happy Hour: available (once per game)", view.snapshots.get(0).happyHourStatus());
        assertEquals("Happy Hour: ON, 1 turn(s) left", view.snapshots.get(1).happyHourStatus());
        assertEquals("Happy Hour: used", view.snapshots.get(2).happyHourStatus());
    }

    @Test
    void vipBurgerOrderedDuringHappyHourIsLockedAt1080() {
        ScriptedView view = new ScriptedView(
                1, 0, 0, 0,   // turn 1: start Happy Hour; seat the VIP; Chef waits
                0, 1, 0, 0);  // turn 2 (still Happy Hour): take order (Burger); Chef cooks
        GameController game = happyHourGame(view, new VIPCustomer(2)); // customer 2 orders Burger (D5)
        game.playNextTurn();
        game.playNextTurn();

        assertTrue(view.messages.contains(
                "Customer 2 ordered Burger $15.00 (1 unit). Happy Hour price locked at $10.80."));
    }

    @Test
    void orderTakenAfterHappyHourPaysTheNormalPrice() {
        ScriptedView view = new ScriptedView(
                1, 0, 0, 0,   // turn 1: start Happy Hour; seat; Chef waits
                0, 3, 0,      // turn 2: Continue; Waiter waits; Chef waits
                0, 1, 0, 0);  // turn 3 (Happy Hour over): take order; Chef cooks
        GameController game = happyHourGame(view, new VIPCustomer(2));
        game.playNextTurn();
        game.playNextTurn();
        game.playNextTurn();

        assertTrue(view.messages.contains("Customer 2 ordered Burger $15.00 (1 unit)."),
                "no Happy Hour price after it ends");
    }

    @Test
    void stage1And2GamesNeverAskAboutHappyHour() {
        ScriptedView view = new ScriptedView();
        newGame(view).playNextTurn();
        assertEquals("", view.snapshots.get(0).happyHourStatus());
    }
}
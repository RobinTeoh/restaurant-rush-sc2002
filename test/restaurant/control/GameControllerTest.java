package restaurant.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import restaurant.customer.Customer;
import restaurant.customer.CustomerState;
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
}
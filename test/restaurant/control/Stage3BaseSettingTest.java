package restaurant.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import restaurant.customer.Customer;
import restaurant.menu.Menu;
import restaurant.menu.MenuItem;
import restaurant.order.Order;

// locks in the stage 3 base setting (D8): what the six customers order, and that the game is still winnable
class Stage3BaseSettingTest {
    private static final int PAYABLE_CUSTOMERS = 5; // the turn-16 customer can never pay

    @Test
    void customersOrderDishesAndCombosByTheSeededRule() {
        assertEquals(List.of("Salad + Burger", "Salad + Pasta", "Pasta", "Burger + Pasta", "Burger + Pasta", "Salad"),
                dishNames());
    }

    // D5's "at most 2 Pasta" only holds for the Stage 1 menu: with combos, 4 of the 6 orders contain Pasta
    @Test
    void fourOfTheSixOrdersContainPastaOnTheCombosMenu() {
        int withPasta = 0;
        for (String name : dishNames()) {
            if (name.contains("Pasta")) {
                withPasta++;
            }
        }
        assertEquals(4, withPasta);
    }

    @Test
    void anyFourOfTheFirstFiveStillReachTheRevenueTarget() {
        List<Order> orders = ordersForEveryone();
        int total = 0;
        int mostExpensive = 0;
        for (int i = 0; i < PAYABLE_CUSTOMERS; i++) {
            total += orders.get(i).getBill();
            mostExpensive = Math.max(mostExpensive, orders.get(i).getBill());
        }
        assertTrue(total - mostExpensive >= GameConfig.TARGET_REVENUE_CENTS);
    }

    @Test
    void theStage3VictoryRunWinsTheGame() throws IOException {
        GameResult result = replay("runs/stage3-victory.txt");
        assertTrue(result.victory());
        assertTrue(result.customersPaid() >= GameConfig.TARGET_PAID);
        assertTrue(result.revenueCents() >= GameConfig.TARGET_REVENUE_CENTS);
    }

    @Test
    void theStage3DefeatRunLosesTheGame() throws IOException {
        GameResult result = replay("runs/stage3-defeat.txt");
        assertFalse(result.victory());
        assertEquals(0, result.customersPaid());
    }

    private static GameResult replay(String runFile) throws IOException {
        List<Integer> answers = new ArrayList<>();
        for (String line : Files.readAllLines(Path.of(runFile))) {
            if (!line.isBlank()) {
                answers.add(Integer.parseInt(line.trim()) - 1); // run files use the console numbers (1, 2, ...), the view wants indexes
            }
        }
        int[] script = answers.stream().mapToInt(Integer::intValue).toArray();
        GameController game = new GameController(
                new ScriptedView(script),
                GameConfig.scheduleFor(GameConfig.STAGE_3).orElseThrow(),
                GameConfig.menuFor(GameConfig.STAGE_3),
                GameConfig.TABLE_COUNT,
                GameConfig.hostEnabled(GameConfig.STAGE_3),
                GameConfig.happyHourEnabled(GameConfig.STAGE_3));
        return game.play();
    }

    private static List<String> dishNames() {
        List<String> names = new ArrayList<>();
        for (Order o : ordersForEveryone()) {
            names.add(o.getItem().getName());
        }
        return names;
    }

    private static List<Order> ordersForEveryone() {
        Menu menu = GameConfig.menuFor(GameConfig.STAGE_3);
        List<Order> orders = new ArrayList<>();
        for (Customer c : GameConfig.baseCustomers()) {
            c.seat();
            MenuItem dish = c.chooseFood(menu);
            orders.add(Order.createOrder(c, dish, 1));
        }
        return orders;
    }
}

package restaurant.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import restaurant.customer.Customer;

class GameConfigTest {

    /** Everyone who arrives over the whole game, in arrival order. */
    private List<Customer> allArrivals(ArrivalSchedule schedule) {
        List<Customer> all = new ArrayList<>();
        for (int turn = 1; turn <= GameConfig.TOTAL_TURNS; turn++) {
            all.addAll(schedule.arrivalsOn(turn));
        }
        return all;
    }

    @Test
    void stage1ScenarioHasOnlyRegularCustomers() {
        List<Customer> arrivals = allArrivals(GameConfig.scheduleFor("stage1").orElseThrow());
        assertEquals(6, arrivals.size());
        for (Customer c : arrivals) {
            assertEquals("Regular", c.getTypeName());
        }
    }

    @Test
    void stage2ScenarioHasAVipAndACritic() {
        List<Customer> arrivals = allArrivals(GameConfig.scheduleFor("stage2").orElseThrow());
        assertEquals(6, arrivals.size());
        assertEquals("VIP", arrivals.get(1).getTypeName());
        assertEquals("Critic", arrivals.get(3).getTypeName());
    }

    @Test
    void stage3ScenarioUsesTheSameCustomersAsStage2() {
        List<Customer> arrivals = allArrivals(GameConfig.scheduleFor("stage3").orElseThrow());
        assertEquals(6, arrivals.size());
        assertEquals("VIP", arrivals.get(1).getTypeName());
        assertEquals("Critic", arrivals.get(3).getTypeName());
    }

    @Test
    void onlyStage3HasTheHost() {
        assertFalse(GameConfig.hostEnabled("stage1"));
        assertFalse(GameConfig.hostEnabled("stage2"));
        assertTrue(GameConfig.hostEnabled("stage3"));
    }

    @Test
    void unknownScenarioIsRejected() {
        assertTrue(GameConfig.scheduleFor("stage9").isEmpty());
    }
    @Test
    void onlyStage3HasHappyHour() {
        assertFalse(GameConfig.happyHourEnabled("stage1"));
        assertFalse(GameConfig.happyHourEnabled("stage2"));
        assertTrue(GameConfig.happyHourEnabled("stage3"));
    }

    @Test
    void onlyStage3MenuHasTheCombos() {
        assertEquals(3, GameConfig.menuFor("stage1").getItems().size());
        assertEquals(3, GameConfig.menuFor("stage2").getItems().size());
        assertEquals(6, GameConfig.menuFor("stage3").getItems().size());
    }
}
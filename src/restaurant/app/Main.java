package restaurant.app;

import java.util.Optional;

import restaurant.boundary.ConsoleUI;
import restaurant.control.ArrivalSchedule;
import restaurant.control.GameConfig;
import restaurant.control.GameController;


public class Main {
    public static void main(String[] args) {
        String scenario = args.length > 0 ? args[0] : GameConfig.DEFAULT_SCENARIO;
        Optional<ArrivalSchedule> schedule = GameConfig.scheduleFor(scenario);
        if (schedule.isEmpty()) {
            System.out.println("Unknown scenario '" + scenario + "'. Use "
                    + GameConfig.STAGE_1 + ", " + GameConfig.STAGE_2 + " or " + GameConfig.STAGE_3 + ".");
            return;
        }
        GameController game = new GameController(
            new ConsoleUI(System.in, System.out),
            schedule.get(),
            GameConfig.menuFor(scenario),
            GameConfig.TABLE_COUNT,
            GameConfig.hostEnabled(scenario),
            GameConfig.happyHourEnabled(scenario));
        game.play();
    }
}
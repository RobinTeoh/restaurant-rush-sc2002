package restaurant.app;

import restaurant.boundary.ConsoleUI;
import restaurant.control.ArrivalSchedule;
import restaurant.control.GameConfig;
import restaurant.control.GameController;
import restaurant.menu.Menu;

public class Main {
    public static void main(String[] args) {
        GameController game = new GameController(
            new ConsoleUI(System.in, System.out),
            new ArrivalSchedule(GameConfig.ARRIVAL_TURNS, GameConfig.baseCustomers()),
            Menu.standard(),
            GameConfig.TABLE_COUNT);
        game.play();
    }
}

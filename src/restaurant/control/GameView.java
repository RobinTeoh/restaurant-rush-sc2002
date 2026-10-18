package restaurant.control;

import java.util.List;

public interface GameView {
    
    void showTurnStart(TurnSnapshot snapshot);

    void showMessage(String message);

    void showResults(GameResult result, boolean finalResult);

    int choose(String prompt, List<String> options);
}

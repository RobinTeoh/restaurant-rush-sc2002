package restaurant.control;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * A GameView that plays from a fixed list of answers instead of the keyboard,
 * so tests can drive whole turns. Once the answers run out it returns -1 (input ended),
 * which the game treats as Wait.
 */
class ScriptedView implements GameView {

    private final Deque<Integer> answers = new ArrayDeque<>();
    final List<String> messages = new ArrayList<>();
    final List<GameResult> results = new ArrayList<>();
    int turnStarts;

    ScriptedView(int... answers) {
        for (int a : answers) {
            this.answers.add(a);
        }
    }

    @Override
    public void showTurnStart(TurnSnapshot snapshot) {
        turnStarts++;
    }

    @Override
    public void showMessage(String message) {
        messages.add(message);
    }

    @Override
    public void showResults(GameResult result, boolean finalResult) {
        results.add(result);
    }

    @Override
    public int choose(String prompt, List<String> options) {
        Integer next = answers.pollFirst();
        return next == null ? -1 : next;
    }
}
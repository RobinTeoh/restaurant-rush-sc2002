package restaurant.boundary;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.OptionalDouble;
import java.util.Scanner;

import restaurant.control.GameConfig;
import restaurant.control.GameResult;
import restaurant.control.GameView;
import restaurant.control.TurnSnapshot;

public class ConsoleUI implements GameView {

    private final Scanner in;
    private final PrintStream out;
    private boolean inputEnded;

    public ConsoleUI(InputStream in, PrintStream out) {
        this.in = new Scanner(in);
        this.out = out;
    }

    @Override 
    public void showTurnStart(TurnSnapshot s) {
        out.println();
        out.println("======== TURN " + s.turn() + " / " + s.totalTurns() + " ========");
        out.println("Revenue: " + formatMoney(s.revenueCents()) + "  Average Satisfaction: "
            + formatAverage(s.averageSatisfaction()));
        out.println("Waiting queue: " + (s.queuedCustomerIds().isEmpty() ? "empty" : s.queuedCustomerIds()));
        for (TurnSnapshot.TableLine t : s.tables()) {
            out.println("Table " + t.tableId() + ": "
                + (t.customerId() == null ? "free" : "customer " + t.customerId()));
        }
        out.println("Customers:");
        if (s.customers().isEmpty()) {
            out.println(". (none)");
        }
        for (TurnSnapshot.CustomerLine c : s.customers()) {
            out.printf("  Customer %d %-9s satisfaction %3d %-13s order: %s%n",
                c.id(), "(" + c.type() + ")", c.satisfaction(), c.state(), c.order());
        }
        out.println("Staff tasks:");
        for (String task : s.staffTasks()) {
            out.println("  " + task);
        }
        
    }
    
    @Override 
    public void showMessage(String message) {
        out.println("> " + message);
    }

    @Override 
    public void showResults(GameResult r, boolean finalResult) {
        out.println(finalResult ? "-------- FINAL RESULTS --------"
            : "-- End of turn " + r.turnsUsed() + " --");
        out.println("Customers served and paid: " + r.customersPaid());
        out.println("Unhappy departures:        " + r.unhappyDepartures());
        out.println("Revenue:                   " + formatMoney(r.revenueCents()));
        out.println("Average satisfaction:      " + formatAverage(r.averageSatisfaction()));
        out.println("Turns used:                " + r.turnsUsed() + " / " + GameConfig.TOTAL_TURNS);
        if (finalResult) {
            out.println("Targets: at least " + GameConfig.TARGET_PAID + " paid, "
                + formatMoney(GameConfig.TARGET_REVENUE_CENTS) + " revenue, average "
                + GameConfig.TARGET_AVERAGE_SATISFACTION);
            out.println(r.victory() ? "VICTORY" : "DEFEAT");
            
        }
    }

    @Override 
    public int choose(String prompt, List<String> options) {
        if (inputEnded) {
            return -1;
        }
        out.println(prompt);
        for (int i = 0; i < options.size(); i++) {
            out.println("  " + (i + 1) + ". " + options.get(i));
        }
        while (true) {
            out.print("Enter 1-" + options.size() + ": ");
            if (!in.hasNextLine()) {
                inputEnded = true;
                out.println();
                out.println("Input ended. Every remaining choice is Wait.");
                return -1;
            }
            String line = in.nextLine().trim();
            if (line.isEmpty()) {
                out.println("Please type a number.");
                continue;
            }
            int number;
            try {
                number = Integer.parseInt(line);
            } catch (NumberFormatException e) {
                out.println("'" + line + "' is not a number.");
                continue;
            }
            if (number < 1 || number > options.size()) {
                out.println(number + " is not an option.");
                continue;
            }
            return number - 1;

        }
    }
    
    private static String formatMoney(int cents) {
        return String.format("$%d.%02d", cents / 100, cents % 100);
    }

    private static String formatAverage(OptionalDouble average) {
        return average.isPresent() ? String.format("%.1f", average.getAsDouble()) : "N/A";
    }

}
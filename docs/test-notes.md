# Test notes

Reproducible runs and edge-case checks. A run file holds the player's answers, one per line.
Replay one with `run.bat < runs\<file>` (Windows) or `sh run.sh < runs/<file>` (macOS/Linux/Git Bash), after building.
CI replays every `runs/stage<N>-victory*.txt` and `runs/stage<N>-defeat*.txt` and checks for VICTORY / DEFEAT.

Base setting for every run: 18 turns, 2 tables, 1 Waiter / Chef / Cashier, arrivals on turns 1, 4, 7, 10, 13, 16,
food chosen by `new Random(32 + customerId)` (DECISIONS D5/D6).
Targets: at least 4 paid, revenue at least $45.00, average satisfaction of paid customers at least 60.

* **Stage 1** (all Regular): customers 1 to 6 order Salad, Burger, Pasta, Burger, Pasta, Salad ($9, $15, $18, $15, $18, $9).
* **Stage 2** (Regular, **VIP**, Regular, **Critic**, Regular, Regular): Salad, Burger, Pasta, **Pasta**, Pasta, Salad;
  bills $9.00, **$13.50** (VIP 10% off), $18.00, $18.00 (the Critic always orders the most expensive dish), $18.00, $9.00.

Replay each stage's run files with its scenario: `sh run.sh stage1 < runs/stage1-...`, `sh run.sh stage2 < runs/stage2-...`. CI replays every stage so far.
Stage 1 behaviour on the Stage 2 code is proven by the Stage 1 unit tests, which still pass unchanged (they use the all-Regular setting).

## Stage 1

### Reproducible runs

| Run file | What the manager does | Result | Paid | Revenue | Avg satisfaction | Unhappy departures |
|---|---|---|---|---|---|---|
| `runs/stage1-victory.txt` | Waiter: serve a READY dish first, otherwise take an order, otherwise seat the first queued customer. Chef: cook the oldest order still being prepared. | **VICTORY** | 4 | $57.00 | 80.0 | 0 |
| `runs/stage1-victory-5paid.txt` | Same, except on turns 13 and 14 the Waiter seats customer 5 and takes the order *before* serving customer 4 (Burger, already READY). | **VICTORY** | 5 | $75.00 | 76.0 | 0 |
| `runs/stage1-defeat.txt` | Same Waiter as the first run, but the Chef is never given an order to cook ("forgot the kitchen"). | **DEFEAT** | 0 | $0.00 | N/A | 2 |
| `runs/stage1-edge-invalid-input.txt` | Invalid inputs on turn 1 (see below), then the input ends and every remaining choice is Wait. | **DEFEAT** | 0 | $0.00 | N/A | 2 |

Each file was replayed twice through the real console game (`run.bat < file`) and printed identical output both times.

### How the player's choices change the result (for the report)

* **Kitchen priority matters.** The only strategy among 30 tried (6 Waiter priority orders x 5 Chef rules) that loses is the one where the Chef never cooks. Every strategy that cooks wins with 4 paid and $57.00; the average satisfaction is 76 to 80 depending on how the Waiter orders its tasks.
* **Waiter priority decides whether a fifth customer pays.** On turn 13 customer 4's Burger is READY and customer 5 arrives, and the single Waiter can only do one. Serving customer 4 first gives 4 paid ($57.00); seating customer 5 first, taking the order on turn 14 and serving customer 4 on turn 15 gives 5 paid ($75.00). Customer 6 (turn 16) can never pay (brief section 3.4).
* With seed 32 the base setting is forgiving: the targets are met by almost any sensible play, and the choices change *how well* the game goes more than whether it is won.

### Edge cases

| # | Case (brief section) | Evidence |
|---|---|---|
| 1 | Letters, blank line, out-of-range number: explained and asked again (3.4) | `runs/stage1-edge-invalid-input.txt` (output: `'abc' is not a number.`, `9 is not an option.`, `Please type a number.`) |
| 2 | Impossible Waiter action explained, task not used up (3.4) | Same run file (`No customer is seated.` then the Waiter is asked again); `GameControllerTest.invalidChoiceIsExplainedAndDoesNotUseUpTheTask` |
| 3 | No free table / nobody waiting | `WaiterTest.seatFailsWhenNoTableIsFree`, `seatFailsWhenNobodyIsWaiting` |
| 4 | Order taken twice | `WaiterTest.takeOrderWorksOnceThenFails` |
| 5 | Serve before ordering / before READY / already served | `WaiterTest.serveFailsBeforeOrdering`, `serveFailsUntilTheDishIsReady`, `serveFailsWhenTheDishWasAlreadyServed` |
| 6 | A dish READY this turn is served next turn at the earliest (3.2 step 3) | `GameController.playNextTurn` runs the Waiter before the Chef; `GameControllerTest.pastaExampleFromTheBrief` (Pasta READY on turn 9, served on turn 10); `WaiterTest.serveFailsUntilTheDishIsReady` |
| 7 | Brief section 3.3 example: satisfaction 92, 84, 76, 76, 76, paid on turn 6 | `GameControllerTest.pastaExampleFromTheBrief`, `CustomerTest.pastaExampleFromTheBrief` |
| 8 | One payment per turn, oldest first, not on the turn the customer became ready | `CashierTest.onePaymentPerTurnOldestFirst`, `GameControllerTest.saladCustomerPaysTwoTurnsAfterBeingServed` |
| 9 | Customer at 0 satisfaction leaves: table freed, unfinished order cancelled, counted as unhappy (3.2 step 5) | `GameControllerTest.ignoredCustomersLeaveAtZero`, `leavingCustomerFreesTableAndCountsAsUnhappy`, `OrderTest.readyOrderIsCancelledWhenCustomerLeaves`; `runs/stage1-defeat.txt` (customers 1 and 2 leave) |
| 10 | Served, eating and ready-to-pay customers lose nothing (3.2 step 5) | `CustomerTest.pastaExampleFromTheBrief` |
| 11 | Always 18 turns, no early ending (3.2 step 6) | `GameControllerTest.alwaysPlaysAllEighteenTurns` |
| 12 | Average shows N/A before anyone has paid (3.5) | `GameStatsTest.averageIsEmptyWhenNobodyPaid`; defeat runs show `N/A` |
| 13 | Victory needs all three targets; each target one step short is a defeat | `GameStatsTest.victoryWhenAllThreeTargetsAreMetExactly`, `defeatWhenOnlyThreePaid`, `defeatWhenRevenueIsOneCentShort`, `defeatWhenAverageIsJustBelowTarget` |
| 14 | Abandoned orders never add revenue (3.4) | `GameStatsTest.departuresAreCountedButNeverAddRevenue`, `Order.getBill` refuses a cancelled order |
| 15 | Input ends early (piped file too short): remaining choices are Wait, no crash | `runs/stage1-edge-invalid-input.txt` (ends after 8 answers and still plays all 18 turns) |
| 16 | Food is chosen by a documented, deterministic rule; at most 2 Pasta; four customers can reach $45 | `FoodChoiceTest` (4 tests) |
| 17 | The turn-16 customer never pays; at most 5 customers pay (3.4) | `GameControllerTest.turnSixteenCustomerCannotPayEvenWhenServedAsFastAsPossible` (added in Stage 2); random-play check below |

### Random-play check (50,000 games)

A throw-away program played 50,000 games on the real `GameController` with random valid answers (every 7th game also had its input cut short),
and checked the brief's rules after every turn: no customer at two tables, no left or paid customer holding a table, satisfaction always 0 to 100
and LEFT exactly at 0, arrivals match the schedule, revenue equals the dishes of the paid customers, the average is right (N/A when nobody paid),
the victory flag is right, at most 5 paid, customer 6 never paid, and always exactly 18 turns.
**Result: 0 failures, no crashes** (67 games were won by chance; the most customers paid in any game was 4).

### Not yet covered by an automated test

* ~~The turn-16 customer never paying (edge case 17)~~ **Added in Stage 2:** `GameControllerTest.turnSixteenCustomerCannotPayEvenWhenServedAsFastAsPossible`.

### Known behaviours to document (not bugs)

* Arrivals happen before the start-of-turn display, so a customer who arrives this turn already appears in it (brief section 3.2 lists the display first).
* "Customers served and paid" in the results counts customers who have **paid** (brief section 3.5 says "served count").
* The Chef's list also shows READY orders; choosing one is refused with a message and the Chef is asked again.

## Stage 2

Base setting: Regular, **VIP** (arrives turn 4), Regular, **Critic** (arrives turn 10), Regular, Regular.

### Reproducible runs

| Run file | What the manager does | Result | Paid | Revenue | Avg satisfaction | Unhappy departures |
|---|---|---|---|---|---|---|
| `runs/stage2-victory.txt` | Waiter: serve a READY dish first, otherwise take an order, otherwise seat the first queued customer. Chef: cook the oldest order still being prepared. | **VICTORY** | 4 | $58.50 | 75.5 | 0 |
| `runs/stage2-victory-5paid.txt` | Waiter: take an order first, then serve, then seat. Same Chef. | **VICTORY** | 5 | $76.50 | 73.2 | 0 |
| `runs/stage2-defeat.txt` | Same Waiter as the first run, but the Chef is never given an order. | **DEFEAT** | 0 | $0.00 | N/A | 2 |

Each file was replayed twice through the real console game (`run.bat < file`) with identical output, and CI's replay loop passes.
$58.50 = $9.00 + **$13.50** (VIP Burger) + $18.00 + **$18.00** (Critic Pasta); the fifth customer adds another $18.00.

### How the player's choices change the result (for the report)

* Same 30 strategies as Stage 1: every strategy that cooks wins and only "the Chef never cooks" loses.
  Average satisfaction now ranges from **70.5 to 75.5** (Stage 1: 76 to 80), because the Critic loses 12 per waiting turn.
* **Waiter priority now decides whether a fifth customer pays**, not only an exceptional plan: taking orders before serving
  gets 5 paid ($76.50) with no special turns; serving first gets 4 ($58.50).
* **Ignoring the Critic costs more than ignoring anyone else:** an ignored Critic is gone after 9 waiting turns
  (Regular 13, VIP 20). With no input at all, customers 1 (Regular) and 4 (Critic) leave; the VIP who arrived on turn 4 is still there at the end.

### Edge cases (Stage 2)

| # | Case (brief section) | Evidence |
|---|---|---|
| 1 | VIP loses 5, Critic 12, Regular 8 per waiting turn (4.2) | `CustomerTypesTest.eachTypeLosesItsOwnAmountPerWaitingTurn`, `criticLeavesFirstWhenIgnored`, `vipNeedsTwentyTurnsToLeave` |
| 2 | VIP pays 10% less, rounded half-up to the cent; Regular and Critic pay full price (4.2, D1) | `MoneyTest.percentOffMatchesTheBriefsVipExamples`, `percentOffRoundsHalfUpToTheCent`, `OrderTest.billUsesEachCustomersOwnDiscount`, `GameControllerTest.vipPaysTheDiscountedBill` (Salad $8.10 in a real game) |
| 3 | Critic always orders the most expensive dish (D7) | `CustomerTypesTest.criticAlwaysOrdersTheMostExpensiveDish`, `GameControllerTest.criticOrdersPastaInTheGame` |
| 4 | Every type follows the same order and service flow (4.2) | `CustomerSubstitutionTest` (same full visit, same illegal steps refused, same leaving at 0, all through a `Customer` reference) |
| 5 | Base run has at least one VIP and one Critic, still six arrivals (4.2) | `BaseSettingTest.vipIsSecondAndCriticIsFourth`, `idsAreOneToSixInArrivalOrder`, `GameControllerTest.baseGameSeatsVipSecondAndCriticFourth` |
| 6 | The base setting can still reach $45 | `BaseSettingTest.anyFourOfTheFirstFiveStillReachTheRevenueTarget` |
| 7 | Behaviour is dispatched through the customer objects, not type checks in the controller (4.2) | No `instanceof` or `getClass()` anywhere in `src/` (searched); `GameController` names no customer class |
| 8 | Stage 1 behaviour still works (4.4) | All 74 Stage 1 tests pass unchanged apart from `new Customer(...)` becoming `new RegularCustomer(...)` |

### Mutation check (do the tests catch mistakes?)

The code was broken on purpose eight ways in a scratch copy; the tests failed every time:
VIP discount 10 to 0 (4 tests fail), Critic loss 12 to 8 (4), VIP loss 5 to 8 (4), Critic picks the cheapest dish (3),
bill ignores the discount (3), rounding truncates instead of half-up (1), VIP and Critic swapped in the base setting (3),
arrivals ignore the turn (9).

### Random-play check (50,000 games, Stage 2 base setting)

Same rule checks as Stage 1, with revenue now checked against each paid customer's own dish and discount.
**Result: 0 failures, no crashes.** Average unhappy departures per random game rose from 0.64 (Stage 1) to 1.19, because the Critic leaves sooner.

## Stage 3

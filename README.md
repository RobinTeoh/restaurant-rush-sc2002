# Restaurant Rush

SC2002 group assignment: a turn-based restaurant management game in Java (command line).

**Repository:** https://github.com/RobinTeoh/restaurant-rush-sc2002
**Team:** A: Robin (Customers) · B: Ian (Kitchen, Menu & Pricing) · C: Caleb (Game loop, Staff & CLI)

## Team docs
| File | What it is |
|---|---|
| [docs/Team-Manual.pdf](docs/Team-Manual.pdf) | Easy-read manual: roles, timeline, rules, rubric, checklists |
| [docs/development-plan.md](docs/development-plan.md) | Week-by-week plan we follow |
| [docs/team-roles.md](docs/team-roles.md) | Who owns which folders, diagrams, report sections and video segments |
| [DECISIONS.md](DECISIONS.md) | Rules the brief leaves open, and what we chose |
| [CONTRACT.md](CONTRACT.md) | Agreed method signatures between roles |
| [docs/test-notes.md](docs/test-notes.md) | Reproducible runs and edge-case checks |
| [docs/design-log.md](docs/design-log.md) | Design choices, alternatives rejected, and why |

## Requirements
- JDK 17 or newer (compiled with `--release 17`)
- No network access needed; JUnit is bundled in `lib/`

## Build, run, test
| | Windows (cmd) | macOS / Linux / Git Bash |
|---|---|---|
| Build | `build.bat` | `sh build.sh` |
| Run | `run.bat` | `sh run.sh` |
| Test | `test.bat` | `sh test.sh` |
| Replay a run | `run.bat < runs\stage1-victory.txt` | `sh run.sh < runs/stage1-victory.txt` |

Build before running (`run` uses the last build). Type a number and press Enter to answer each prompt.

## Base setting and reproducible runs
18 turns, 2 tables, one Waiter, Chef and Cashier, customers arriving on turns 1, 4, 7, 10, 13 and 16.
Victory needs at least 4 paid customers, at least $45.00 revenue and an average satisfaction of at least 60.

**Customers (Stage 2):** Regular, **VIP**, Regular, **Critic**, Regular, Regular, in arrival order.
VIP loses 5 satisfaction per waiting turn and gets 10% off; Critic loses 12 and always orders the most expensive dish; Regular loses 8.

**How customers choose food:** customer *n* (1 to 6, in arrival order) picks a dish with `new Random(32 + n)`, so every run is the same.
The customers order Salad, Burger, Pasta, Pasta (the Critic), Pasta, Salad. Details: [DECISIONS.md](DECISIONS.md) D5, D6, D6b and D7.

| Run file (replay at tag) | Result |
|---|---|
| `runs/stage2-victory.txt` (`stage-2`) | VICTORY: 4 paid, $58.50, average 75.5 |
| `runs/stage2-victory-5paid.txt` (`stage-2`) | VICTORY: 5 paid, $76.50, average 73.2 (the most that can pay) |
| `runs/stage2-defeat.txt` (`stage-2`) | DEFEAT: the Chef is never given an order, so nobody is served |
| `runs/stage1-victory.txt` (`stage-1`) | VICTORY: 4 paid, $57.00, average 80.0 |
| `runs/stage1-victory-5paid.txt` (`stage-1`) | VICTORY: 5 paid, $75.00, average 76.0 |
| `runs/stage1-defeat.txt` (`stage-1`) | DEFEAT |
| `runs/stage1-edge-invalid-input.txt` (`stage-1`) | Invalid inputs are explained and asked again, then the input ends and the game plays on |

A run file belongs to its stage, because later stages change the customers. To replay a Stage 1 run: `git checkout stage-1`, build, then run.

What each run does, the edge cases and the test evidence: [docs/test-notes.md](docs/test-notes.md).

## Stages
| Stage | Tag | Adds |
|---|---|---|
| 1 | `stage-1` | Regular customers, menu, Waiter, Chef, Cashier, full turn sequence, both endings |
| 2 | `stage-2` | VIP and Critic customers |
| 3 | `stage-3` | Host, ComboMeal, Happy Hour |

### Stage 1 → Stage 2
Compare: https://github.com/RobinTeoh/restaurant-rush-sc2002/compare/stage-1...stage-2

**New files**

| File | Why |
|---|---|
| `customer/RegularCustomer.java` | Stage 1's customer behaviour as its own type (loses 8) |
| `customer/VIPCustomer.java` | Loses 5 per waiting turn, 10% off the bill (brief 4.2) |
| `customer/CriticCustomer.java` | Loses 12 per waiting turn, always orders the most expensive dish (D7) |
| `test/.../customer/CustomerTypesTest.java`, `CustomerSubstitutionTest.java` | What differs per type; every type follows the same flow through a `Customer` reference |
| `test/.../control/ArrivalScheduleTest.java`, `BaseSettingTest.java` | Who arrives when; the Stage 2 base setting (types, dishes, bills, $45 still reachable) |
| `runs/stage2-*.txt` | Reproducible Stage 2 victory and defeat runs |

**Changed files, and why**

| File | Change | Why |
|---|---|---|
| `customer/Customer.java` | Now **abstract**. The constant loss of 8 became `satisfactionLossPerTurn()`, overridden by each type. The dish choice moved from `chooseFood` into `pickDish()`, which the Critic overrides. Added `getDiscountPercent()` (0 by default) and `getTypeName()`. The event methods are now `final`. | Stage 1 had one kind of customer, so it was one class (designed from Stage 1 only). With three types, the shared state rules stay in one parent and each type only supplies its own numbers and food choice. `final` stops a type from breaking the visit flow. |
| `control/ArrivalSchedule.java` | Holds the customer for each arrival; `arrivalsOn(turn)` returns those customers instead of a count. The turns-only constructor still means all Regular. | Decides which type arrives without the controller knowing any customer class (no type checks, brief 4.2). Keeping the turns-only form lets the Stage 1 tests run unchanged. |
| `control/GameController.java` | `arrivals()` adds whoever the schedule says arrives (3 lines); the unused id counter is gone. Labels and the display show each customer's type. | `Customer` can no longer be created directly. Showing the type lets the player make VIP and Critic choices. |
| `control/GameConfig.java`, `app/Main.java` | `GameConfig.baseCustomers()` returns the Stage 2 base setting; `Main` uses it. | One place for the base setting (D6b). New objects every call, so each game starts fresh. |
| `control/TurnSnapshot.java`, `boundary/ConsoleUI.java` | Customer lines carry and print the type. | Display only. |
| `pricing/Money.java` | Added `percentOff(cents, percent)` with D1's half-up rounding. | The only place a percentage is applied; Stage 3's Happy Hour reuses it. |
| `order/Order.java` | `getBill()` takes off the customer's own discount. | VIP 10% off. No type check: each customer reports its own discount. |
| Existing tests | `new Customer(...)` became `new RegularCustomer(...)` (42 places in 7 test files); nothing else changed. | `Customer` is abstract. All 74 Stage 1 tests still pass, which shows Stage 1 behaviour is unchanged. |
| `.github/workflows/ci.yml` | `STAGE` set to 2. | CI replays the Stage 2 run files. |

**Deleted:** nothing.

### Stage 2 → Stage 3
Compare: https://github.com/RobinTeoh/restaurant-rush-sc2002/compare/stage-2...stage-3
<!-- new files; changed or deleted files with reasons -->

## Video
<!-- link + timestamps for segments 1-7 -->

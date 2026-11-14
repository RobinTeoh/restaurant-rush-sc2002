# Restaurant Rush

SC2002 group assignment (SCEE GRP 5): a turn-based restaurant management game in Java, played in the console.
You are the manager. Staff time is limited, so you decide which customers and orders come first. The game was built in three tagged, independently runnable stages.

<p align="center"><img src="docs/images/restaurant-rush-banner.png" alt="Restaurant Rush: a Java-based simulation" width="720"></p>

**Repository:** https://github.com/RobinTeoh/restaurant-rush-sc2002

## Contents
[Quick start](#quick-start) · [Submission index](#submission-index) · [Base setting and reproducible runs](#base-setting-and-reproducible-runs) · [Stages and tags](#stages-and-tags) · [Diagrams](#diagrams) · [Video](#video) · [AI history](#ai-history) · [Team](#team) · [Documentation map](#documentation-map)

## Quick start
- JDK 17 or newer (compiled with `--release 17`)
- No network access needed; JUnit is bundled in `lib/`

| | Windows (cmd) | macOS / Linux / Git Bash |
|---|---|---|
| Build | `build.bat` | `sh build.sh` |
| Run | `run.bat` | `sh run.sh` |
| Test | `test.bat` | `sh test.sh` |
| Replay a run | `run.bat stage1 < runs\stage1-victory.txt` | `sh run.sh stage1 < runs/stage1-victory.txt` |

Build before running (`run` uses the last build). Type a number and press Enter to answer each prompt.
Tests: 74 at `stage-1`, 102 at `stage-2`, 151 at `stage-3`; all pass at each tag, and CI replays every run file on each push.

## Submission index
Where to find each item the brief asks for.

| Item | Where |
|---|---|
| Source code, build and run instructions | [`src/`](src/), [`test/`](test/), [Quick start](#quick-start) |
| README (repository URL, stage links, per-transition file lists, video timestamps) | this file: [Stages and tags](#stages-and-tags) (Stage 1 → 2 and 2 → 3), [Video](#video) |
| PDF report | [`docs/report/SC2002-Restaurant-Rush-Report.pdf`](docs/report/SC2002-Restaurant-Rush-Report.pdf)  |
| Recorded video (about 15 minutes, with segment timestamps) | [Video](#video): https://youtu.be/XInMKYZXef4 |
| Declarations (Use of GAI) | [AI history](#ai-history) |
| Class diagrams (one per stage) | [Diagrams](#diagrams) |
| Sequence diagram (one scenario: VIP, Happy Hour, Combo at turn 4) | [Diagrams](#diagrams) |
| Design discussion (decisions, principles, alternatives) | [`docs/design/`](docs/design/) |
| Stage tags and comparisons | [Stages and tags](#stages-and-tags) |
| Tests and reproducible runs | [`test/`](test/), [`runs/`](runs/), [`docs/testing/test-notes.md`](docs/testing/test-notes.md) |
| Process evidence (CI, PR template, design log, roles) | [`.github/`](.github/), [`docs/design/design-log.md`](docs/design/design-log.md), [`docs/process/team-roles.md`](docs/process/team-roles.md), the pull requests on GitHub |
| AI history | each member's own branch: [AI history](#ai-history) |

## Base setting and reproducible runs
18 turns, 2 tables, one Waiter, Chef and Cashier, customers arriving on turns 1, 4, 7, 10, 13 and 16.
Victory needs at least 4 paid customers, at least $45.00 revenue and an average satisfaction of at least 60.

**Customers (Stage 2):** Regular, **VIP**, Regular, **Critic**, Regular, Regular, in arrival order.
VIP loses 5 satisfaction per waiting turn and gets 10% off; Critic loses 12 and always orders the most expensive dish; Regular loses 8.

**How customers choose food:** customer *n* (1 to 6, in arrival order) picks a dish with `new Random(32 + n)`, so every run is the same.
The Stage 2 customers order Salad, Burger, Pasta, Pasta (the Critic), Pasta, Salad. In Stage 3 the menu also has three combos, and the six customers order Salad + Burger, Salad + Pasta, Pasta, Burger + Pasta (the Critic), Burger + Pasta, Salad (D8). Details: [DECISIONS.md](docs/design/DECISIONS.md) D5, D6, D6b and D7.

| Run file (replay at tag) | Result |
|---|---|
| `runs/stage3-victory.txt` (`stage-3`) | VICTORY: 4 paid, $87.57, average 80.0 (Happy Hour switched on in turn 7) |
| `runs/stage3-victory-happyhour-t4.txt` (`main`) | VICTORY: 4 paid, $86.80, average 75.5 (the report's Section 3 scenario: Happy Hour started at turn 4) |
| `runs/stage3-victory-continue-t4.txt` (`main`) | VICTORY: 4 paid, $91.17, average 74.3 (the same scenario, continuing at turn 4 without Happy Hour) |
| `runs/stage3-defeat.txt` (`stage-3`) | DEFEAT: the Waiter always waits, so no order is taken and nobody is served |
| `runs/stage2-victory.txt` (`stage-2`) | VICTORY: 4 paid, $58.50, average 75.5 |
| `runs/stage2-victory-5paid.txt` (`stage-2`) | VICTORY: 5 paid, $76.50, average 73.2 (the most that can pay) |
| `runs/stage2-defeat.txt` (`stage-2`) | DEFEAT: the Chef is never given an order, so nobody is served |
| `runs/stage1-victory.txt` (`stage-1`) | VICTORY: 4 paid, $57.00, average 80.0 |
| `runs/stage1-victory-5paid.txt` (`stage-1`) | VICTORY: 5 paid, $75.00, average 76.0 |
| `runs/stage1-defeat.txt` (`stage-1`) | DEFEAT |
| `runs/stage1-edge-invalid-input.txt` (`stage-1`) | Invalid inputs are explained and asked again, then the input ends and the game plays on |

A run file belongs to its stage, because later stages change the customers and the menu. To replay one: `git checkout stage-N`, build, then `sh run.sh stageN < runs/stageN-...txt` (`run.bat stageN < ...` on Windows). Without the `stageN` argument the game starts the latest stage (Stage 3 at the `stage-3` tag; each tag defaults to its own stage).

The two `-t4` run files were added after the `stage-3` tag (for the report's Section 3 diagram), so replay them on `main`. The Stage 3 code is identical at the tag and on `main`.

What each run does, the edge cases and the test evidence: [docs/testing/test-notes.md](docs/testing/test-notes.md).

## Stages and tags
| Stage | Tag (browse the code) | Commit | Adds | Compare |
|---|---|---|---|---|
| 1 | [`stage-1`](https://github.com/RobinTeoh/restaurant-rush-sc2002/tree/stage-1) | `14f368c` | Regular customers, menu, Waiter, Chef, Cashier, full turn sequence, both endings | first stage |
| 2 | [`stage-2`](https://github.com/RobinTeoh/restaurant-rush-sc2002/tree/stage-2) | `68d2336` | VIP and Critic customers | [stage-1...stage-2](https://github.com/RobinTeoh/restaurant-rush-sc2002/compare/stage-1...stage-2) |
| 3 | [`stage-3`](https://github.com/RobinTeoh/restaurant-rush-sc2002/tree/stage-3) | `e6ccf01` | Host, ComboMeal, Happy Hour | [stage-2...stage-3](https://github.com/RobinTeoh/restaurant-rush-sc2002/compare/stage-2...stage-3) |

Each tag builds and runs on its own: `git checkout stage-N`, build, then run.

### Stage 1 → Stage 2
Compare: [stage-1...stage-2](https://github.com/RobinTeoh/restaurant-rush-sc2002/compare/stage-1...stage-2) (36 files, no file deleted). The design effect, with the reasoning, is in the report, Section 5.2.

| File | New / changed | Reason |
|---|---|---|
| `customer/RegularCustomer.java` | New | Keeps the Stage 1 behaviour (loses 8 per waiting turn) as its own type |
| `customer/VIPCustomer.java` | New | Loses 5 per waiting turn, 10% off the bill (brief 4.2) |
| `customer/CriticCustomer.java` | New | Loses 12 per waiting turn, always orders the most expensive dish (D7) |
| `customer/Customer.java` | Changed | Now abstract: final event methods keep the state rules in one place, and subtypes override `satisfactionLossPerTurn()`, `getDiscountPercent()` and `pickDish()` |
| `control/ArrivalSchedule.java` | Changed | The schedule decides which customer type arrives, so the controller needs no customer class; `arrivalsOn(turn)` now returns customers |
| `control/GameController.java` | Changed | Adds whoever the schedule returns, with no type checks (D15); labels show the type |
| `order/Order.java`, `pricing/Money.java` | Changed | The bill uses the customer's own discount through the new `Money.percentOff` (half-up rounding, D1) |
| `control/GameConfig.java`, `app/Main.java`, `control/TurnSnapshot.java`, `boundary/ConsoleUI.java` | Changed | Stage 2 base setting in one place (D6b); the customer type is shown on screen |
| `runs/stage2-*.txt` (3 files) | New | One Stage 2 victory, a 5-paid victory and a defeat |
| `.github/workflows/ci.yml` | Changed | CI replays the Stage 2 run files too |
| `docs/uml/stage-1-class-diagram.drawio`, `stage-2-class-diagram.drawio` | New | Class diagram sources for Stages 1 and 2 |
| `CONTRACT.md`, `DECISIONS.md`, `README.md`, `docs/design-log.md`, `docs/test-notes.md` | Changed | Documentation for Stage 2 (these files moved into `docs/` after the tag) |
| `test/` (13 files added or changed, e.g. `CustomerTypesTest`, `CustomerSubstitutionTest`, `BaseSettingTest`) | New / changed | Customer is abstract, so tests create `RegularCustomer`; each new rule has tests (74 → 102) |

What still works: all Stage 1 rules are unchanged; the Stage 1 run files still replay at `stage-1`, and CI replays them on each push.

### Stage 2 → Stage 3
Compare: [stage-2...stage-3](https://github.com/RobinTeoh/restaurant-rush-sc2002/compare/stage-2...stage-3) (38 files, no file deleted). The design effect, with the reasoning, is in the report, Section 5.3.

| File | New / changed | Reason |
|---|---|---|
| `staff/Host.java` | New | Seats the first queued customer at a free table, at most once per turn, before the Waiter (D4, brief 4.3) |
| `menu/ComboMeal.java` | New | Two dishes sold together: prep units added, 90% of the pair, rounded half-up (extends `MenuItem`) |
| `pricing/HappyHour.java` | New | Once per game, two turns, 20% off orders taken, +15 recovery for waiting customers |
| `menu/Menu.java` | Changed | `withCombos()` lists the three dishes, then the three combos (D8) |
| `order/Order.java` | Changed | `createOrder(..., happyHour)` locks the bill when the order is taken: VIP discount first, then Happy Hour; the old `createOrder` is kept |
| `customer/Customer.java` | Changed | Adds `recover(points)`: only waiting customers gain, capped at 100 |
| `staff/Waiter.java` | Changed | `takeOrder` passes Happy Hour on to `Order.createOrder` |
| `control/GameController.java` | Changed | Each turn: Happy Hour prompt and recovery, then Host, then Waiter; Happy Hour ends at turn end |
| `control/GameConfig.java`, `app/Main.java` | Changed | The scenario (`stage1`, `stage2`, `stage3`) picks arrivals, menu, Host and Happy Hour; the default is now Stage 3 |
| `control/TurnSnapshot.java`, `boundary/ConsoleUI.java` | Changed | Show the Happy Hour status and turns left (display only) |
| `run.sh`, `run.bat`, `.github/workflows/ci.yml` | Changed | Accept the scenario name; CI replays the run files of every stage |
| `runs/stage3-victory.txt`, `runs/stage3-defeat.txt` | New | Stage 3 victory and defeat |
| `docs/uml/stage-3-class-diagram.drawio`, `stage-3.png`, `stage-2.png` | New | Stage 3 class diagram, and the Stage 2 diagram redrawn in lecture notation |
| `docs/design-principles.md`, `docs/decision-summary.md` | New | SOLID evidence checked against the code, and the decisions by stage |
| `CONTRACT.md`, `DECISIONS.md`, `README.md`, `docs/design-log.md`, `docs/test-notes.md` | Changed | Documentation for Stage 3 |
| `test/` (`HostTest`, `ComboMealTest`, `HappyHourTest`, `GameConfigTest`, `Stage3BaseSettingTest`, and additions to existing tests) | New / changed | Each Stage 3 rule has tests; 49 tests added (102 → 151), none removed |

What still works: all 102 Stage 2 tests pass unchanged; the Stage 1 and Stage 2 run files replay at their own scenario with the same results; with Host and Happy Hour switched off the turn is exactly Stage 2.

The full explanation of each change (what it did to the design) is in the report: Section 5 (Stage evolution).

## Diagrams
All sources and images are in [`docs/uml/`](docs/uml/).

| Diagram | Files |
|---|---|
| Class diagram, Stage 1 | [`stage-1.png`](docs/uml/stage-1.png), [`stage-1-class-diagram.drawio`](docs/uml/stage-1-class-diagram.drawio) |
| Class diagram, Stage 2 | [`stage-2.png`](docs/uml/stage-2.png), [`stage-2-class-diagram.drawio`](docs/uml/stage-2-class-diagram.drawio) |
| Class diagram, Stage 3 (final) | [`stage-3.png`](docs/uml/stage-3.png), [`stage-3-class-diagram.drawio`](docs/uml/stage-3-class-diagram.drawio) |
| Sequence diagram (Figure 3-1, four parts) | [Part 1, messages 1-19](<docs/uml/Sequence Diagram-Part 1_ messages 1-19.png>), [Part 2, messages 20-51](<docs/uml/Sequence Diagram-Part 2_ messages 20-51.png>), [Part 3, messages 52-72](<docs/uml/Sequence Diagram-Part 3_ messages 52-72.png>), [Part 4, messages 73-89](<docs/uml/Sequence Diagram-Part 4_ messages 73-89.png>) |

The sequence diagram replays the scenario in [`runs/stage3-victory-happyhour-t4.txt`](runs/stage3-victory-happyhour-t4.txt); its alternative branch is [`runs/stage3-victory-continue-t4.txt`](runs/stage3-victory-continue-t4.txt).

## Video
**Video link:** https://youtu.be/XInMKYZXef4

Each start time below opens the video at that segment.

| # | Segment | Speaker | Start |
|---|---|---|---|
| 1 | Game launch from the submitted project, and an overview of the three stages | Caleb | [0:10](https://youtu.be/XInMKYZXef4?t=10) |
| 2 | Meaningful manager choices, including a decision where staff are constrained | Caleb | [1:03](https://youtu.be/XInMKYZXef4?t=63) |
| 3 | A dish going READY, then SERVED, then paid | Robin | [1:43](https://youtu.be/XInMKYZXef4?t=103) |
| 4 | A VIP or Critic behaviour | Robin | [3:01](https://youtu.be/XInMKYZXef4?t=181) |
| 5 | A Stage 3 feature | Ian | [4:32](https://youtu.be/XInMKYZXef4?t=272) |
| 6 | One victory and one defeat (separate reproducible runs) | Ian | [5:26](https://youtu.be/XInMKYZXef4?t=326) |
| 7 | Stage tag comparisons and the class diagrams | Takshak | [6:17](https://youtu.be/XInMKYZXef4?t=377) |
| 8 | Use case and sequence diagram | Sibghat | [9:11](https://youtu.be/XInMKYZXef4?t=551) |
| 9 | Individual contributions and key learning | each member | [12:38](https://youtu.be/XInMKYZXef4?t=758) |

Segment 9 in detail: Caleb [12:40](https://youtu.be/XInMKYZXef4?t=760), Robin [13:01](https://youtu.be/XInMKYZXef4?t=781), Ian [13:24](https://youtu.be/XInMKYZXef4?t=804), Takshak [13:44](https://youtu.be/XInMKYZXef4?t=824), Sibghat [14:13](https://youtu.be/XInMKYZXef4?t=853). The video starts with a 10-second intro and ends with a thank-you card at 14:40.

## AI history
Each member keeps their full AI conversation, in order and not summarised, on their own branch.

| Member | Branch |
|---|---|
| Ian Tan | [`ian/ai-history`](https://github.com/RobinTeoh/restaurant-rush-sc2002/tree/ian/ai-history/ai-history) |
| Robin Teoh | [`robin/ai-history`](https://github.com/RobinTeoh/restaurant-rush-sc2002/tree/robin/ai-history/ai-history) |
| Caleb Soon | [`caleb/ai-history`](https://github.com/RobinTeoh/restaurant-rush-sc2002/tree/caleb/ai-history/ai-history) |
| Takshak | [`Takshak`](https://github.com/RobinTeoh/restaurant-rush-sc2002/tree/Takshak) |
| Sibghat Ullah | [`Sibghat-Ullah`](https://github.com/RobinTeoh/restaurant-rush-sc2002/tree/Sibghat-Ullah/ai-chat%20history) |

## Team
| Member | Role |
|---|---|
| Robin Teoh | Customers (`customer/`); customer state diagram |
| Ian Tan | Kitchen, menu and pricing (`menu/`, `order/`, `pricing/`, Chef), including ComboMeal and Happy Hour |
| Caleb Soon | Game loop, staff and CLI (`app/`, `boundary/`, `control/`, `place/`, Waiter, Cashier, Host), CI and tags |
| Takshak | Class diagrams, one per stage |
| Sibghat Ullah | Sequence diagram |

Who owns which folder, diagram, report section and video segment: [`docs/process/team-roles.md`](docs/process/team-roles.md).

## Documentation map
| Folder / file | What it holds |
|---|---|
| [`docs/design/DECISIONS.md`](docs/design/DECISIONS.md) | Rules the brief leaves open, and what we chose (D1-D15) |
| [`docs/design/CONTRACT.md`](docs/design/CONTRACT.md) | Method signatures between roles, checked against the code |
| [`docs/design/design-log.md`](docs/design/design-log.md) | Design choices, alternatives rejected, and why |
| [`docs/design/design-principles.md`](docs/design/design-principles.md) | Where SOLID genuinely shows in the code, and where it does not |
| [`docs/design/decision-summary.md`](docs/design/decision-summary.md) | One-page summary of every decision by stage |
| [`docs/testing/test-notes.md`](docs/testing/test-notes.md) | Reproducible runs, edge cases and test evidence per stage |
| [`docs/uml/`](docs/uml/) | The submitted class diagrams (one per stage) and the sequence diagram |
| [`docs/design/customer-drafts/`](docs/design/customer-drafts/) | Early Customer class and state drafts, kept as design history |
| [`docs/report/`](docs/report/) | The final group report (PDF), the same file as in the submitted archive |
| [`docs/process/`](docs/process/) | Team roles, the original development plan, and the team manual (PDF and HTML) |
| [`runs/`](runs/) | Recorded player inputs for every stage, replayed by the tests and CI |

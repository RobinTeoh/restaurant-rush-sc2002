# Restaurant Rush: Team Lab Manual

**SC2002 group assignment · 3 members · 4 weeks · target: A+**

This manual says, for each person and each week, **what to build, which part of the brief it covers, what A+ quality looks like, and what to push.** If everyone finishes their checklist each week, the result at the end of Week 4 is a complete submission.

> Assumed start: **Monday 12 Oct 2026**, submission ready **Friday 6 Nov 2026**, with a weekend of buffer. When the deadline appears on NTULearn, shift the dates but **keep the order**: the tags must happen in sequence.

---

## 0. How to use this manual

1. Find your letter in Section 1 and write your name next to it.
2. Every **Monday**, read "Everyone" for that week, then your own section.
3. Copy your week's checklist into a **GitHub Issue** titled `Week N · A · <your name>`. Assign it to yourself, add it to the milestone, and tick boxes as you go. These issues count as evidence for the "development process" marks.
4. **If it isn't pushed to GitHub, it isn't done.** Every task ends in a commit or pull request (PR).
5. Before opening any PR, check your work against the **A+ check** list and the PR template in Appendix I.
6. For rule questions, look in `docs/DECISIONS.md`. For method names and parameters, look in `docs/CONTRACT.md`. **Tell the other two in chat before changing either file.**

Expected effort: about **8–10 hours per person per week**.

---

## 1. Roles

| | **A: Customers & Front of House** | **B: Kitchen, Menu & Money** | **C: Game Loop, Waiter & CLI** (tags & CI lead) |
|---|---|---|---|
| Name | ____________ | ____________ | ____________ |
| Code folders | `customer/`, `place/`, `staff/Host` | `menu/`, `order/`, `pricing/`, `staff/Chef`, `staff/Cashier` | `app/`, `boundary/`, `control/`, `staff/Waiter` |
| Stage 1 | Customer, satisfaction, abandonment, food choice, Table, WaitingQueue | Menu, Order, Chef, Cashier, money in cents, revenue | Turn loop, Waiter, CLI, input handling, display, victory/defeat |
| Stage 2 | **VIPCustomer, CriticCustomer** | Pricing review, acceptance tests | Scenario selection, Stage 2 tag, README 1→2 |
| Stage 3 | **Host**, combo-choice rule, Happy Hour satisfaction recovery | **ComboMeal**, **Happy Hour** pricing and duration | Stage 3 turn flow (Happy Hour prompt, Host step), README 2→3 |
| QA | Reproducible victory/defeat runs, `test-notes.md` | Acceptance tests (§3.3 Pasta example, payment timing) | CI, tag ceremonies, fresh-clone checks |
| Report lead | **Class diagram** (25 marks), key abstractions table, functional evidence | **Sequence diagram** (20 marks) | **Design discussion** (15 marks), README, PDF assembly |
| Video | Segments 4, 5 (Host), 6, 7 (class diagram) | Segments 3, 5 (Combo + Happy Hour), 7 (sequence diagram) | Segments 1, 2, 7 (tags); video editing |

**Everyone** writes JUnit tests for their own code, reviews the others' PRs, writes their own reflection, signs the declarations and speaks in the video.

**Repo owner: A.** A creates the GitHub repo in Week 0 and holds the admin rights (settings, invites, branch protection, grader access). **C** still leads the tag ceremonies and CI, and any member can push branches and tags.

---

## 2. Timeline at a glance

| Week | Dates | Goal | Tags | Meetings (all three) |
|---|---|---|---|---|
| 0 | 10–11 Oct (weekend) | A sets up the repo; everyone does personal setup | none | none |
| 1 | 12–18 Oct | Kickoff, walking skeleton, all Stage 1 parts | none | **Mon** kickoff (2 h) · Thu chat check-in · **Sun** integration session (1.5 h) |
| 2 | 19–25 Oct | Stage 1 complete, then Stage 2; Stage 3 starts on branches | `stage-1` **Wed**, `stage-2` **Sun** | Wed tag (30 min) · Sun tag (30 min) |
| 3 | 26 Oct–1 Nov | Stage 3 complete, code freeze, diagrams v1 | `stage-3` **Fri** | Wed integration (1 h) · Fri tag (30 min) |
| 4 | 2–8 Nov | Final diagrams, report, video, submission | none | **Mon** diagram review (1 h) · **Thu** video recording · **Fri** submit |

### Hard rules (all four weeks)
1. `main` always compiles and all tests pass. CI checks this.
2. **No Stage N+1 code is merged into `main` before the `stage-N` tag exists.** Work ahead only on branches.
3. Every change goes through a PR that **one other member reviews**. Merge with **"Create a merge commit"**, never squash, so each person's commits stay visible.
4. Only edit your own folders. If you need a change in someone else's, ask them or open an issue.
5. Commit small and often. Messages say *what and why*, plus the section of the brief, e.g. `Add satisfaction floor at 0 so customers can't go negative (§3.2 step 5)`.

---

## 3. Requirement coverage map

Every requirement in the brief has an owner, a week and a proof.

| Brief § | Requirement | Owner | Week | Proof |
|---|---|---|---|---|
| 2.1 | 18 turns, 2 tables, 1 Waiter/Chef/Cashier, arrivals on turns 1,4,7,10,13,16 | C (+A tables) | 1 | `ScenarioTest` |
| 2.1 | Menu: Salad $9/1 unit, Burger $15/1, Pasta $18/2 | B | 1 | `MenuTest` |
| 2.1 | Victory = ≥4 paid AND ≥$45 revenue AND average satisfaction ≥60 | C | 1 | `GameResultTest` |
| 2.2 | Customer chooses own food by a documented, deterministic rule; choice shown | A (rule), C (display) | 1 | `FoodChoiceTest`, DECISIONS D1 |
| 2.2 | No rules in the UI | C, everyone in review | all | PR checklist |
| 2.2 | Money in integer cents, shown with 2 decimals | B | 1 | `MoneyTest` |
| 2.2 | Works offline | C | 1 | Only bundled JUnit jar; no downloads |
| 3.1 | Start-of-turn display (all 8 items) | C | 1 | Video seg. 2, screenshot |
| 3.2-1 | Arrival joins the queue with satisfaction 100 | C + A | 1 | `ScenarioTest` |
| 3.2-2 | Waiter: seat / take order / serve READY / wait; Chef: advance one order / wait | C (Waiter, prompts), B (Chef) | 1 | `WaiterValidationTest`, `ChefTest` |
| 3.2-3 | Waiter acts before Chef; a dish that becomes READY this turn can't be served until next turn | C + B | 1–2 | `PastaExampleTest` |
| 3.2-4 | Eating → READY_TO_PAY; Cashier pays one customer per turn, oldest first, not in the same turn they became READY_TO_PAY | B + A | 1–2 | `PaymentTimingTest` |
| 3.2-5 | Lose 8 points while waiting or seated-not-served; at 0 the customer leaves, order cancelled, table freed | A | 1 | `SatisfactionTest` |
| 3.2-6 | Always 18 turns, then Victory or Defeat | C | 1 | `GameResultTest` |
| 3.3 | Pasta example: 92, 84, 76, 76, 76, paid on turn 6 | B (test), all | 2 | `PastaExampleTest` (acceptance) |
| 3.4 | Invalid choice: explain why, ask again, task **not** used up | C | 1 | `WaiterValidationTest`, run file |
| 3.4 | Revenue never includes unpaid or abandoned orders | B | 1–2 | `LedgerTest` |
| 3.4 | Turn-16 customer can't pay (intended) | B | 2 | `TurnSixteenTest` |
| 3.5 | Results: served, unhappy departures, revenue, average (N/A if none), turns used | C | 1 | Screenshot |
| 4.1 | `stage-1` tag; one victory run + one defeat run in test notes | C (tag), A (runs) | 2 | `runs/stage1-*.txt`, `test-notes.md` |
| 4.2 | VIP (−5, 10% off), Critic (−12, documented special behaviour), no type checks | A | 2 | `VipCustomerTest`, `CriticCustomerTest`, grep for `instanceof` |
| 4.2 | Reproducible run with ≥1 VIP and ≥1 Critic, still 6 arrivals | A + C | 2 | `runs/stage2-*.txt` |
| 4.3 | Host seats ≤1 queued customer per turn, in addition to the Waiter | A (+C turn flow) | 3 | `HostTest` |
| 4.3 | ComboMeal: units = sum; price = 90% rounded to the cent; documented choice rule | B (+A rule) | 3 | `ComboMealTest` |
| 4.3 | Happy Hour: once per game, this turn + next, 20% off orders taken, VIP discount first, price locked, +15 recovery capped at 100, duration displayed | B (+A recovery, C prompt/display) | 3 | `HappyHourTest` |
| 4.4 | Explain every change to an earlier class | Everyone (PR text), C (README) | 2–3 | README transitions |
| 5 | Class diagram (matches Stage 3 code, annotated) | A | 3–4 | `docs/uml/` |
| 5 | Sequence diagram (manager choice changes outcome) | B | 3–4 | `docs/uml/` |
| 5 | Design discussion | C | 3–4 | Report §5 |
| 5 | Tags + README transitions with compare links | C | 2–3 | GitHub |
| 6.1 | Zip: source, README, PDF, video, declarations | C (assembles), all | 4 | Appendix H |
| 6.2 | ~15-min video, 7 segments, timestamps in README | All | 4 | Appendix G |

---

## 4. What A+ looks like (rubric → standard)

| Criterion | Marks | A+ standard | Lead |
|---|---|---|---|
| **UML class diagram** | 25 | Every Stage 3 class appears **with the exact code name**. Visibility on every attribute and operation (`-` `+` `#`). Enums shown. Abstract classes marked. Inheritance arrows. Composition diamonds (e.g. ComboMeal ◆— 2 MenuItem). Associations with multiplicities (e.g. Table 0..1 — Customer). «boundary» / «control» / «entity» stereotypes. **At least 3 callout notes**: encapsulation, polymorphism, composition. Readable on one PDF page. | A |
| **UML sequence diagram** | 20 | One scenario where **a manager choice changes the outcome**, shown with an `alt` fragment. Lifelines for the CLI, controller, staff and domain objects. `loop` over turns, guards like `[order READY]`. Covers arrival → seat → order → Chef → READY → SERVED → eating → payment → result check, plus VIP pricing or Happy Hour. **Message names are real method names.** | B |
| **Design consideration** | 15 | Responsibilities table for every key class. SOLID **only with a concrete class/method as proof** (skip any principle that isn't really in the code). At least two real trade-offs. One alternative design compared seriously. "How choices change the result" backed by **numbers from our recorded runs**. | C |
| **Implementation** | 20 | Every rule in §3 is correct and has a test. No `instanceof` or type switch in the controller. No rules in the UI. Input handling can't crash. Each tag compiles on its own from a fresh clone. Runs are reproducible from files. Clean code: private fields, small methods, named constants instead of magic numbers. | All |
| **Thinking & development process** | 20 | Three tags on runnable commits. A **UML snapshot per stage**. README transitions listing new and changed files with reasons and compare links. Weekly issues. Every PR reviewed with a real comment. **All three members commit every week.** A design log. Honest reflections. A 15-minute video where everyone speaks, with timestamps. | C (+all) |

---

## 5. Week 1: Kickoff, setup, Stage 1 parts (12–18 Oct)

### A · Week 0 (weekend before kickoff): repo setup

**Covers:** §5 GitHub evidence (the repo itself), Implementation (offline build, reproducibility)
**Expect:** about 1 hour. The starter files (Appendix D) are already prepared in the `restaurant-rush` folder.

- [ ] JDK on your PATH: `java -version` and `javac -version` both work in a new terminal
- [ ] `git config user.email` is an email **on your GitHub account**, so your commits are credited to you
- [ ] Locally: `build`, `run` and `test` all pass
- [ ] First commit on `main`: `Set up project skeleton, build scripts and team docs`
- [ ] Create a **private** GitHub repo `restaurant-rush` (empty: no README, no .gitignore, no licence), then push `main`
- [ ] Invite B and C as collaborators (Settings → Collaborators)
- [ ] Settings → General → Pull Requests: allow **merge commits only** (turn off squash and rebase); tick "Automatically delete head branches"
- [ ] Branch protection on `main`: require a PR with 1 approval and passing status checks. This needs GitHub Pro (free with the Student Developer Pack); without it, follow the rule by hand.
- [ ] Milestones: `Stage 1`, `Stage 2`, `Stage 3`, `Submission`. Labels: `A`, `B`, `C`, `bug`, `docs`
- [ ] Check that the **Actions** tab shows the first CI run passing
- [ ] Post the repo link in the group chat

**Push:** the first commit, directly to `main`. This is the only direct push to `main`; everything after it goes through PRs.
**Done when:** B and C can clone the repo and run `build` and `test`.

---

### Everyone: Week 1

**Before Monday:** read the brief fully, especially §2–3.

**Monday kickoff meeting (2 h)**
1. Trace the **§3.3 Pasta example** together on paper or a whiteboard, turn by turn.
2. Draw the **customer** and **order** state machines (Appendix A). Photograph them into `docs/design-log.md`.
3. Go through **Appendix B (decisions)** and accept or change each default. **A** types the result into `docs/DECISIONS.md`.
4. Go through **Appendix C (contract)** and agree the method names and parameters. **B** types the result into `docs/CONTRACT.md`.
5. Agree the tools: **Java 17**, draw.io for UML, the chat channel and meeting times.
6. Write your names into Section 1.

**Personal setup (before Tuesday)**
- [ ] Install JDK 17; `java -version` shows 17
- [ ] Install Git and an IDE (IntelliJ, VS Code or Eclipse; anything is fine, since we build with scripts)
- [ ] `git config --global user.name "Your Name"` and `user.email` **matching your GitHub account**, so your commits count as yours
- [ ] Accept the repo invite, clone, and run `build` and `test` successfully
- [ ] Create your `Week 1 · X · name` issue

**Sunday integration session (1.5 h):** merge all open PRs, play one full game together, and turn every bug into an issue.

---

### C · Week 1: Walking skeleton, turn loop, Waiter, CLI

**Covers:** §2.1 (base setting, victory check), §2.2 (no rules in UI, offline), §3.1, §3.2 steps 1–3 and 6, §3.4 (invalid choices), §3.5
**Expect:** the walking skeleton on Wednesday unblocks A and B, so it comes first once CONTRACT.md is merged.

**Mon–Tue: get familiar with the repo (set up by A in Week 0)**
- [ ] Clone, `build`, `test`; read the CI file `.github/workflows/ci.yml`, since you own it from now on
- [ ] Push a test branch and open a test PR to check the workflow; close it without merging

**Wed: walking skeleton (once CONTRACT.md is merged)**
- [ ] Create **every class and method in CONTRACT.md** as a stub returning a default value, so A's and B's packages exist
- [ ] `Main` runs an 18-turn loop that prints the turn number
- [ ] PR → merge. From now on, everyone **fills in their own stubs** instead of building separately against fakes, so integration happens daily.

**Thu–Sun: Stage 1 features**
- [ ] `ArrivalSchedule`: arrival turns stored **as data** (a list), not as if-statements
- [ ] `GameController`: one method per brief step, called in order: `arrivals()`, `waiterPhase()`, `chefPhase()`, `eatingAndPayment()`, `endOfTurn()`, then `checkResult()` after turn 18. The Waiter prompt is asked and executed **before** the Chef prompt (DECISIONS D7).
- [ ] `Waiter`: `seat`, `takeOrder`, `serve`, or wait. Each returns an `ActionResult(ok, reason)`. Reasons include: no free table, nobody in the queue, customer has no table, already ordered, order not READY.
- [ ] Invalid choice → show the reason → ask again; **the task is not used up**
- [ ] `GameView` interface + `ConsoleUI` implementation. The controller only talks to `GameView`, so tests can use a scripted view (a genuine DIP example for the report).
- [ ] Robust input: letters, blank lines, out-of-range numbers, and end of input (finish cleanly with a message, no stack trace)
- [ ] Start-of-turn display with all §3.1 items: turn, revenue, average satisfaction, queue, occupied tables, each customer's satisfaction, order status, available staff tasks
- [ ] End-of-turn results (§3.5) after **every** turn and at the end: served, unhappy departures, revenue, average (N/A), turns used
- [ ] Result: Victory if and only if paid ≥ 4 && revenue ≥ 4500 cents && average ≥ 60
- [ ] Tests: `ScenarioTest`, `WaiterValidationTest`, `GameResultTest`

**A+ check before each PR**
- `ConsoleUI` contains **zero** game rules: no satisfaction or price arithmetic, no validity checks. It only prints and reads.
- `GameController` reads like brief §3.2.
- Constants (18, 2, 4, 4500, 60, arrival turns) live in one place, not scattered.

**Push this week:** `c/walking-skeleton` · `c/turn-loop` · `c/waiter` · `c/console-ui` (each with tests)
**Done when:** `run` plays 18 turns with real menus, and no input can crash it.

---

### A · Week 1: Customers, satisfaction, tables, queue

**Covers:** §2.2 (customer chooses food), §3.2 step 1 (starts at 100), step 4 (state changes), step 5 (loss and abandonment), §3.4 (table stays occupied; can't eat before served)
**Expect:** Mon–Tue goes on DECISIONS.md; real coding starts once the skeleton lands on Wednesday. Write your tests first while waiting.

**Tasks**
- [ ] `docs/DECISIONS.md` typed from the kickoff and merged (**Tuesday latest**)
- [ ] `CustomerState` enum: `WAITING, SEATED, ORDERED, EATING, READY_TO_PAY, PAID, LEFT`
- [ ] Abstract `Customer` + `RegularCustomer` (DECISIONS D15). Fields: id, arrival number, satisfaction = 100, state, table, order, recorded satisfaction.
- [ ] `protected abstract int satisfactionLossPerTurn()`, where Regular returns 8. This makes Stage 2 a matter of new files only.
- [ ] `endOfTurn()`: loses satisfaction **only** in `WAITING`, `SEATED`, `ORDERED`; never goes below 0; at 0 → `LEFT`
- [ ] Guarded state changes: `seatAt(table)`, `placeOrder(order)`, `serve(turn)`, `finishEatingIfDue(turn)`, `pay(turn)` (records satisfaction), `leave()`. Calling one in the wrong state throws `IllegalStateException`.
- [ ] `chooseFood(Menu)` using DECISIONS D1. Print the choice when the order is placed (C displays the returned value).
- [ ] `Table` (id, occupant, `isFree`, `assign`, `release`) and `WaitingQueue` (keeps arrival order; any queued customer can be removed when seated)
- [ ] Tests: `SatisfactionTest` (alone for 13 turns → 0 → leaves; eating customers lose nothing; floor at 0), `CustomerStateTest` (illegal transitions throw), `FoodChoiceTest`, `TableTest`

**A+ check before each PR**
- All fields `private`. Satisfaction only changes through methods that clamp it to 0–100. This becomes your **encapsulation callout** on the class diagram.
- Illegal states can't be reached: you can't eat before being served or pay before READY_TO_PAY (§3.4).
- No per-type `if` anywhere. Values that vary by type come from overridable methods.

**Push this week:** `a/decisions` · `a/customer-core` · `a/table-queue` (each with tests)
**Done when:** a Customer correctly goes through the full Pasta-example life cycle in a unit test.

---

### B · Week 1: Menu, Order, Chef, Cashier, money

**Covers:** §2.1 (menu), §2.2 (money in cents), §3.2 steps 3–4 (Chef, READY, payment), §3.4 (revenue rule)
**Expect:** CONTRACT.md is the most urgent file of the week, because C's skeleton depends on it. Aim for **Tuesday**.

**Tasks**
- [ ] `docs/CONTRACT.md` typed from the kickoff and merged (**Tuesday latest**)
- [ ] `Money`: all amounts are `int` cents; `format(cents)` → `"$18.00"`; `percentOff(cents, pct)` using the rounding in DECISIONS D3. **This is the only place rounding happens.**
- [ ] `MenuItem` (name, priceCents, prepUnits) and `Menu` containing Salad, Burger and Pasta
- [ ] `OrderStatus` enum: `PLACED, PREPARING, READY, SERVED, PAID, CANCELLED`
- [ ] `Order`: customer, item, unitsDone, status, **lockedPriceCents (set once when the order is created)**, turnTaken. Methods: `advance()` (READY on the last unit), `markServed()` (only if READY), `cancel()` (only before SERVED), `markPaid()`
- [ ] `PriceCalculator.priceFor(customer, item)` = `customer.applyDiscount(item price)`. In Stage 1, Regular returns the price unchanged.
- [ ] `Chef.work(order)` → `ActionResult`; refuses READY, SERVED or CANCELLED orders
- [ ] `Ledger`: revenue, paid count, recorded satisfactions, `averageSatisfaction()` returning an empty result when nobody has paid (C shows N/A)
- [ ] `Cashier.collect(turn, customers, ledger)`: picks **at most one** customer who was READY_TO_PAY **before this turn**, oldest first (D4); charges the locked price; marks customer and order PAID; frees the table; records satisfaction
- [ ] Tests: `MenuTest`, `MoneyTest`, `OrderTest` (Pasta needs exactly 2 Chef turns), `ChefTest`, `CashierTest` (one per turn, oldest first, not in the same turn), `LedgerTest` (abandoned orders never add revenue)

**A+ check before each PR**
- No `double` or `float` holding money anywhere.
- No public `setStatus()`. An Order changes its own status through named methods.
- The price is locked at order time, so Stage 3 only adds one step to `PriceCalculator`.

**Push this week:** `b/contract` · `b/menu-order` · `b/chef` · `b/cashier-ledger` (each with tests)
**Done when:** a unit test takes a Pasta order from PLACED → READY in 2 Chef calls → SERVED → PAID with $18.00 in the Ledger.

---

## 6. Week 2: `stage-1` (Wed) and `stage-2` (Sun) (19–25 Oct)

### Everyone: Week 2
- **Mon–Tue:** fix the bugs from Sunday's issues.
- **Tue evening:** `PastaExampleTest` must pass. This is the gate for tagging Stage 1.
- **Wed:** **Stage 1 tag ceremony** (Appendix E), led by C, with all three present.
- **Wed–Sun:** A builds Stage 2; B and C start Stage 3 **on branches only**.
- **Sun:** **Stage 2 tag ceremony**.

---

### A · Week 2: Stage 1 test runs, then VIP & Critic

**Covers:** §4.1 (reproducible victory and defeat), §4.2 (all of it), Process (stage snapshot)

**Mon–Wed: Stage 1 QA (before the tag)**
- [ ] Find a **victory run**: `runs/stage1-victory.txt` (first line selects the scenario, then one input per line). Record the expected final numbers in `docs/test-notes.md`.
- [ ] Find a **defeat run**: `runs/stage1-defeat.txt` (e.g. a manager who neglects the kitchen)
- [ ] Edge-case runs, recorded in `test-notes.md`: no free table, serving an order that isn't READY, typing letters, blank input, abandonment at 0 (order cancelled, table freed, counted as unhappy), turn-16 customer never pays, N/A before the first payment
- [ ] **UML snapshot** `docs/uml/stage-1.drawio` + `.png`. It can be rough, but it must use the real classes.

**Wed (after the tag) – Sun: Stage 2**, on branch `a/stage2-customers`
- [ ] `VIPCustomer`: `satisfactionLossPerTurn()` = 5; `applyDiscount()` = 10% off via `Money.percentOff`
- [ ] `CriticCustomer`: loss 12, full price, plus the **special behaviour** (DECISIONS D2), implemented **only inside CriticCustomer**
- [ ] With C: a `mixed` scenario of six arrivals including ≥1 VIP and ≥1 Critic. Keep the `regular` scenario selectable.
- [ ] Confirm victory is still reachable in `mixed`
- [ ] `grep -rn "instanceof" src/` returns **nothing** in `control/` (ideally nothing at all)
- [ ] Tests: VIP loses 5 and Burger bill = $13.50; Critic loses 12 and has its behaviour; one test runs the **same** flow through a `Customer` reference for all three types (LSP evidence)
- [ ] `runs/stage2-victory.txt` and `runs/stage2-defeat.txt` (both include a VIP and a Critic)
- [ ] **UML snapshot** `docs/uml/stage-2.png`
- [ ] PR description lists **new files** and **changed files with the reason**. C copies this into the README.

**A+ check:** the Stage 1→2 diff in `control/` should be **registration only** (the scenario list). Anything more needs a written reason in the PR.

**Push this week:** `a/stage1-runs` · `a/uml-stage1` · `a/stage2-customers` (merge **after** `stage-1`) · `a/stage2-runs`
**Done when:** `stage-2` is tagged with your runs and snapshot in it.

---

### B · Week 2: Acceptance tests, then ComboMeal on a branch

**Covers:** §3.3 (acceptance), §3.2 step 4 timing, §3.4 (turn 16, revenue), start of §4.3 ComboMeal

**Mon–Tue: Stage 1 acceptance tests (before the tag)**
- [ ] `PastaExampleTest`: drive `GameController` **through its methods, not console text** (so the test keeps working when Stage 3 adds prompts). Assert satisfaction 92, 84, 76, 76, 76, payment on turn 6 and revenue $18.00.
- [ ] `PaymentTimingTest`: served on turn t → READY_TO_PAY on t+1 → paid on t+2 at the earliest; at most one payment per turn
- [ ] `TurnSixteenTest`: the turn-16 arrival never pays, even when served as fast as possible
- [ ] `RevenueTest`: an abandoned customer's order never reaches revenue

**Wed–Sun**
- [ ] Review A's Stage 2 PR, focusing on VIP pricing through `PriceCalculator`
- [ ] Branch `b/combo-meal` (**don't merge before `stage-2`**):
  - [ ] Introduce a common abstraction so an `Order` can hold either a single dish or a combo (e.g. abstract `MenuItem` with `Dish` and `ComboMeal`). This **changes a Stage 1 class**, so write the reason in the PR for the README.
  - [ ] `ComboMeal` **holds two** MenuItems (composition); units = sum; price = `Money.percentOff(sum, 10)`
  - [ ] Register the three combos in the Stage 3 menu
  - [ ] Tests: Salad+Burger = $21.60 / 2 units, Salad+Pasta = $24.30 / 3 units, Burger+Pasta = $29.70 / 3 units
- [ ] With C: **choose the sequence-diagram scenario now** (one where a manager choice changes the outcome), so A can record it as a run

**Push this week:** `b/acceptance-tests` (before Wed) · `b/combo-meal` (draft PR)
**Done when:** the acceptance tests are green on `main` and the combo PR is open as a draft.

---

### C · Week 2: Harden, CI, README, both tags

**Covers:** §4.1 tag, §4.2 tag, §5 GitHub evidence, Implementation (reproducibility)

**Mon–Wed**
- [ ] Fix integration bugs; help B with the scripted `GameView` for tests
- [ ] `README.md`: requirements (JDK 17), build, run, test, and **how to replay a run file** (`run < runs/stage1-victory.txt`)
- [ ] Test the README instructions on **A's and B's laptops**
- [ ] CI (`.github/workflows/ci.yml`, Appendix D): build, tests, replay the current stage's run files and check for VICTORY or DEFEAT
- [ ] **Lead the `stage-1` tag ceremony** (Appendix E)

**Wed–Sun**
- [ ] Scenario selection at start-up (`regular` / `mixed`); the base setting is always offered
- [ ] **Lead the `stage-2` tag ceremony**
- [ ] README section **"Stage 1 → Stage 2"**: new files; changed or deleted files **with reasons**; link `https://github.com/<org>/restaurant-rush/compare/stage-1...stage-2`
- [ ] Branch `c/stage3-turn` (**don't merge before `stage-2`**): Happy Hour activation prompt at turn start (D9), Host step in the turn (calls A's Host), display of Happy Hour remaining turns

**Push this week:** `c/integration-fixes` · `c/readme-run` · `c/ci` · `c/scenarios` · README 1→2 · `c/stage3-turn` (draft PR)
**Done when:** `stage-1` and `stage-2` are on GitHub, CI is green, and the README links work.

---

## 7. Week 3: `stage-3` (Fri), code freeze, diagrams v1 (26 Oct–1 Nov)

### Everyone: Week 3
- **Mon–Tue:** merge the Stage 3 branches in this order: `b/combo-meal` → `a/host` → `b/happy-hour` → `a/stage3-customers` → `c/stage3-turn`
- **Wed:** integration session (1 h): play Stage 3 together and log bugs
- **Fri:** **Stage 3 tag ceremony**, then **CODE FREEZE**. After this, only bug fixes go in, by PR, and the fresh-clone check is repeated.
- **Weekend:** diagrams v1 and design-discussion draft done

---

### A · Week 3: Host, combo choice, recovery, class diagram v1

**Covers:** §4.3 (Host, combo choice rule, Happy Hour recovery), §4.4 (earlier behaviour still works), §5 class diagram

- [ ] `Host` (in `staff/`): seats **at most one** queued customer per turn, **in addition to** the Waiter's task; the Waiter can still seat customers (D11)
- [ ] Combo-choice rule in `Customer` (D12), documented and deterministic; the Critic overrides it if your D2 behaviour says so
- [ ] `recover(15)`, capped at 100, applied only to customers waiting for a table or food (`WAITING`, `SEATED`, `ORDERED`), never to those eating or gone
- [ ] Tests: Host and Waiter both seat in the same turn; Host can't seat when there's no free table; recovery cap; recovery skips EATING / READY_TO_PAY / LEFT
- [ ] `runs/stage3-victory.txt` and `runs/stage3-defeat.txt` (using Host, a combo and Happy Hour)
- [ ] Confirm the Stage 1 and 2 unit and acceptance tests **still pass on Stage 3 code**, and note it in `test-notes.md`. This is the §4.4 evidence. (Old run files only replay at their own tag, because Stage 3 adds prompts.)
- [ ] **Class diagram v1** from the **real** Stage 3 code (draw.io): all packages, visibility, multiplicities, enums, stereotypes, 3+ callouts. B and C check their own packages.

**Push this week:** `a/host` · `a/stage3-customers` · `a/stage3-runs` · `a/class-diagram-v1`
**Done when:** `stage-3` is tagged and the class diagram v1 PNG is in `docs/uml/`.

---

### B · Week 3: Happy Hour, sequence diagram v1

**Covers:** §4.3 Happy Hour pricing and duration, §5 sequence diagram

- [ ] `HappyHour` policy: `activate(turn)` works **once per game** and only at turn start; active on that turn and the next; `remainingTurns(turn)` for the display
- [ ] `PriceCalculator`: base price → **customer discount first** → Happy Hour 20% if active → **lock** into the Order. Existing orders never change.
- [ ] Tests: VIP Burger during Happy Hour = **$10.80**; Regular Pasta during Happy Hour = $14.40; combo during Happy Hour; an order taken before activation is unchanged; second activation is refused; lasts exactly 2 turns
- [ ] **Sequence diagram v1**, for the scenario agreed with C:
  - lifelines `:ConsoleUI`, `:GameController`, `:Waiter`, `:Chef`, `:Cashier`, `v:VIPCustomer`, `o:Order`, `:PriceCalculator` / `:HappyHour`, `:Ledger`
  - `loop [turn 1..18]`, `alt [manager serves VIP first] / [else]`, `opt [order READY before Waiter acts]`
  - full path: arrival → seat → order (VIP price, Happy Hour) → Chef → READY → SERVED → eating → payment → result check
  - **message names copied from the code**
- [ ] Review A's class diagram for `menu/`, `order/`, `pricing/`

**Push this week:** `b/happy-hour` · `b/sequence-diagram-v1`
**Done when:** all pricing tests are green and the sequence diagram v1 PNG is in `docs/uml/`.

---

### C · Week 3: Stage 3 turn flow, tag, README 2→3, design discussion draft

**Covers:** §4.3 integration, §5 GitHub evidence, §5 design discussion

- [ ] Merge and integrate `c/stage3-turn`: Happy Hour prompt → recovery → display → six steps (D9); Host step before the Waiter (D11)
- [ ] CI green with the Stage 3 run files
- [ ] **Lead the `stage-3` tag ceremony**, then announce the code freeze
- [ ] README section **"Stage 2 → Stage 3"** (new / changed / deleted files, reasons, compare link `stage-2...stage-3`)
- [ ] **Design discussion draft** (report §5):
  - responsibilities table (each person sends rows for their classes)
  - SOLID with a class/method as proof (Appendix F has ideas; skip any that isn't genuine)
  - trade-offs (at least 2) and one alternative design
  - "how the player's choices change a result", using **numbers from A's runs**

**Push this week:** `c/stage3-turn` · README 2→3 · `docs/report/design-discussion.md`
**Done when:** `stage-3` is on GitHub and the design discussion draft is pushed.

---

## 8. Week 4: Report, video, submission (2–8 Nov)

### Everyone: Week 4
- **Mon: diagram review (1 h).** Put the class diagram next to the code and tick off **every class, attribute and operation**. Do the same for the sequence diagram's message names.
- **Tue–Wed:** finish report sections (Appendix F) and write your **own reflection** (about ½ page: what you built, what went wrong, what you'd change).
- **Thu:** record video segments (Appendix G). Everyone records their own; C edits them together.
- **Fri:** C assembles the zip; **all three** check it on a different laptop (Appendix H); submit.
- Each person signs the **Declaration of Original Work** and their **own GAI declaration**. Declare AI help honestly, including help with planning or code.

### A · Week 4
- [ ] **Class diagram v2 (final)**: fixes from Monday, callouts finished, PNG + PDF page
- [ ] Key abstractions table for the report (merge everyone's rows)
- [ ] Functional evidence section: tests summary, run results, edge cases (from `test-notes.md`)
- [ ] Stage evolution figure: `stage-1.png`, `stage-2.png`, `stage-3.png` side by side, with 2–3 lines each on what changed
- [ ] Reflection · declarations · record **segments 4, 5 (Host), 6, 7 (class diagram)**

**Push:** `a/class-diagram-final` · `a/report-evidence` · `a/reflection`

### B · Week 4
- [ ] **Sequence diagram v2 (final)**, with a short walkthrough paragraph explaining where the manager's choice changes the outcome
- [ ] Review the SOLID section for `menu/`, `order/`, `pricing/` examples
- [ ] Reflection · declarations · record **segments 3, 5 (Combo + Happy Hour), 7 (sequence diagram)**

**Push:** `b/sequence-diagram-final` · `b/reflection`

### C · Week 4
- [ ] Final design discussion
- [ ] Assemble the **PDF report** (Appendix F order) and push the PDF to `docs/report/`
- [ ] Final README: repo URL, build/run/test, scenario and run-file usage, both stage transitions, **video timestamps**
- [ ] Record **segments 1, 2, 7 (tags)**; edit the video; upload it; put the link and timestamps in the README
- [ ] Confirm with A that graders have repo access; build the zip `LAB_Grp.zip` (e.g. `FCS1_Grp1.zip`); submit
- [ ] Reflection · declarations

**Push:** `c/design-discussion-final` · `c/readme-final` · report PDF

**Done when:** everything in Appendix H is ticked and the submission confirmation is screenshotted to the chat.

---

# Appendices

## Appendix A: Rules cheat sheet (most commonly gotten wrong)

**Customer states**
```
WAITING ──seat──▶ SEATED ──take order──▶ ORDERED ──serve──▶ EATING ──(next turn)──▶ READY_TO_PAY ──(a later turn, Cashier)──▶ PAID
   │                 │                      │
   └──── satisfaction hits 0 at turn end ───┴──▶ LEFT   (order cancelled, table freed, counted as unhappy)
```
**Order states:** `PLACED → PREPARING → READY → SERVED → PAID`, or `CANCELLED` if the customer leaves before SERVED.

| Rule | Brief |
|---|---|
| Satisfaction loss applies to WAITING, SEATED **and ORDERED** (including while the food is READY but not served). EATING and READY_TO_PAY lose nothing. | §3.2-5 |
| Waiter acts before Chef → a dish that becomes READY this turn is served **next turn** at the earliest | §3.2-3 |
| Chef may cook an order the Waiter took **this** turn | §3.2-2 |
| Served on turn t → READY_TO_PAY on t+1 → pays on t+2 at the earliest | §3.2-4 |
| Cashier: at most one per turn, oldest first, automatic | §3.2-4 |
| Average satisfaction = value **recorded at payment**; N/A if nobody has paid | §3.5 |
| Invalid choice: explain, ask again, **don't use up the task** | §3.4 |
| Always 18 turns; the turn-16 customer can never pay (max 5 paid) | §3.2-6, §3.4 |
| Money in int cents; display `$x.xx` | §2.2 |
| VIP: −5 per turn, 10% off · Critic: −12 per turn, full price + special behaviour | §4.2 |
| Combo: units = sum; price = 90% of the sum, rounded to the cent | §4.3 |
| Happy Hour: once per game, at turn start, lasts this turn + next; 20% off orders **taken** while active; VIP discount first; price locked; +15 recovery (max 100) for customers waiting for a table or food; show remaining turns | §4.3 |
| Check numbers: Pasta example 92/84/76/76/76 · VIP Burger $13.50 · VIP Burger + Happy Hour $10.80 · Salad+Pasta combo $24.30 | §3.3, §4.3 |

## Appendix B: Starter decisions (`docs/DECISIONS.md`)

These are recommended defaults. Accept or change each one at kickoff, then **document the final choice and the reason** (this feeds the trade-offs section).

| # | Question | Recommended default | Why |
|---|---|---|---|
| D1 | Food-choice rule | Customer *n* (arrival order) orders `[Salad, Burger, Pasta][(n−1) mod 3]` | Deterministic and easy to reproduce. Any 4 of the first 5 customers total ≥ $48, so $45 is reachable. |
| D2 | Critic special behaviour | **Order preference:** the Critic always orders the dish with the most prep units (Pasta; in Stage 3 the most expensive combo) | Implemented as one overridden method. Combined with the 12-point loss, it forces a real prioritisation decision. |
| D3 | Rounding | Apply each percentage in turn, rounding half-up to the cent: `(cents * (100 − pct) + 50) / 100` | Gives $10.80 and $24.30 exactly; integer-only arithmetic |
| D4 | Cashier "oldest first" | Earliest turn of becoming READY_TO_PAY; tie → earliest arrival | Matches "oldest" in the payment queue |
| D5 | Order inside step 4 | Cashier pays first (only customers who were READY_TO_PAY before this step), then eaters become READY_TO_PAY | Guarantees "pays next turn at the earliest" |
| D6 | Customer leaves with a READY order | Cancel every order not yet SERVED (including READY) | The brief says to cancel unfinished orders, and nobody is there to serve |
| D7 | Waiter/Chef sequence | Ask and execute the Waiter first, then ask and execute the Chef | Satisfies all three §3.2-3 rules with no special cases |
| D8 | Which queued customer to seat | The manager picks any queued customer | Gives the player a real choice |
| D9 | Turn start (Stage 3) | Happy Hour prompt (if unused) → +15 recovery if active → start-of-turn display → steps 1–6 | New arrivals join at 100 in step 1, after recovery |
| D10 | Happy Hour duration | Active on activation turn T and T+1; display "2 turns left" then "1 turn left" | Matches "that turn and the next" |
| D11 | Host | The manager chooses which queued customer the Host seats (or Wait); the Host acts **before** the Waiter in step 2 | Keeps it a decision; the Waiter can then take that customer's order |
| D12 | Combo choice (Stage 3) | Combos are only on the Stage 3 menu. Customers with an even arrival number order a combo (rotating through the three); odd numbers use D1. | Documented and deterministic; Stage 1/2 scenarios are unaffected |
| D13 | End of input | Print "Input ended" and finish the game safely | Piped run files must never crash |
| D14 | Results display | §3.5 summary after every turn and at the end | The brief calls it "end-of-turn results" |
| D15 | Customer hierarchy in Stage 1 | Abstract `Customer` + `RegularCustomer` already in Stage 1 | Stage 2 then only adds files. Justify this in the report as a deliberate extension point, not a Stage 2 feature. |

## Appendix C: Starter contract (`docs/CONTRACT.md`)

This is a sketch to agree on and rename at kickoff. Once merged, **tell the others before changing any signature.**

```java
// ---- customer/ (A) ----
public enum CustomerState { WAITING, SEATED, ORDERED, EATING, READY_TO_PAY, PAID, LEFT }
public abstract class Customer {
    public int getId();  public int getSatisfaction();  public CustomerState getState();
    public Table getTable();  public Order getOrder();
    public MenuItem chooseFood(Menu menu);            // D1, overridable (Critic, combos)
    protected abstract int satisfactionLossPerTurn(); // Regular 8, VIP 5, Critic 12
    public int applyDiscount(int priceCents);         // Regular: unchanged; VIP: 10% off
    public void seatAt(Table t);  public void placeOrder(Order o);
    public void serve(int turn);  public boolean finishEatingIfDue(int turn);
    public void pay(int turn);    public int getRecordedSatisfaction();
    public void endOfTurn();      // loses satisfaction if waiting; may become LEFT
    public void recover(int pts); // Stage 3
}
// ---- place/ (A) ----
public class Table        { int getId(); boolean isFree(); void assign(Customer c); void release(); Customer getOccupant(); }
public class WaitingQueue { void add(Customer c); boolean remove(Customer c); List<Customer> list(); boolean isEmpty(); }

// ---- menu/, order/, pricing/ (B) ----
public class MenuItem { String getName(); int getPriceCents(); int getPrepUnits(); }
public class Menu     { List<MenuItem> getItems(); }
public enum OrderStatus { PLACED, PREPARING, READY, SERVED, PAID, CANCELLED }
public class Order {
    Order(Customer c, MenuItem item, int lockedPriceCents, int turnTaken);
    boolean advance();  // true if it just became READY
    OrderStatus getStatus();  boolean isReady();  void markServed();  void cancel();  void markPaid();
    int getPriceCents();
}
public class PriceCalculator { int priceFor(Customer c, MenuItem item, int turn); }
public final class Money     { static String format(int cents); static int percentOff(int cents, int pct); }
public class Ledger          { void record(Customer c, int cents); int getRevenueCents(); int getPaidCount(); OptionalDouble averageSatisfaction(); }

// ---- staff/ ----
public class Chef    { ActionResult work(Order o); }                                   // B
public class Cashier { Optional<Customer> collect(int turn, List<Customer> all, Ledger l); } // B
public class Waiter  { ActionResult seat(Customer c, Table t); ActionResult takeOrder(Customer c, int turn); ActionResult serve(Order o); } // C
public class Host    { ActionResult seat(Customer c, Table t); }                       // A, Stage 3

// ---- control/, boundary/ (C) ----
public record ActionResult(boolean ok, String message) {}
public interface GameView { void showTurnStart(/* snapshot */); int choose(String prompt, List<String> options); void showMessage(String m); }
public class ConsoleUI implements GameView { ... }
public class GameController { void play(); /* one private method per brief step */ }
```

## Appendix D: Repo structure, scripts, git, CI

```
restaurant-rush/
├── .github/
│   ├── workflows/ci.yml
│   └── pull_request_template.md
├── docs/
│   ├── LAB-MANUAL.md   DECISIONS.md   CONTRACT.md
│   ├── design-log.md   test-notes.md
│   ├── uml/            stage-1.png, stage-2.png, class-diagram.drawio/.png, sequence.drawio/.png
│   └── report/
├── lib/junit-platform-console-standalone-1.10.2.jar   ← bundled so tests run offline
├── runs/               stage1-victory.txt, stage1-defeat.txt, stage2-*, stage3-*
├── src/restaurant/
│   ├── app/            Main                                  (C)
│   ├── boundary/       GameView, ConsoleUI, InputReader      (C)
│   ├── control/        GameController, ArrivalSchedule, Scenario, ActionResult (C)
│   ├── staff/          Waiter (C) · Chef, Cashier (B) · Host (A)
│   ├── place/          Table, WaitingQueue                   (A)
│   ├── customer/       Customer + subclasses, CustomerState  (A)
│   ├── menu/           MenuItem, Menu, ComboMeal             (B)
│   ├── order/          Order, OrderStatus                    (B)
│   └── pricing/        Money, PriceCalculator, Ledger, HappyHour (B)
├── test/restaurant/    mirrors src/
├── build.sh  build.bat  run.sh  run.bat  test.sh  test.bat
├── .gitattributes      ← keeps .sh files LF so CI on Linux can run them
├── .gitignore
└── README.md
```

**`.gitignore`**
```
out/
out-test/
*.class
sources.txt
.idea/
*.iml
.vscode/
.DS_Store
Thumbs.db
```

**`build.sh` / `run.sh` / `test.sh`** (the `.bat` versions use `dir /s /b src\*.java > sources.txt` and `;` as the classpath separator)
```sh
# build.sh
set -e; rm -rf out; mkdir -p out
javac --release 17 -d out $(find src -name "*.java")

# run.sh
java -cp out restaurant.app.Main "$@"

# test.sh  (classpath separator is ':' on macOS/Linux and ';' on Windows; the real script picks the right one)
set -e; rm -rf out-test; mkdir -p out-test
javac --release 17 -d out-test -cp out:lib/junit-platform-console-standalone-1.10.2.jar $(find test -name "*.java")
java -jar lib/junit-platform-console-standalone-1.10.2.jar execute -cp out:out-test --scan-class-path
```

**Opening the project in IntelliJ IDEA** (one-time setup per person, about 2 minutes; `.idea/` is not committed)
1. **File → Open** → select the `restaurant-rush` folder (not `src`).
2. **File → Project Structure → Project**: SDK = **17**, language level = 17.
3. **Modules → Sources tab**: mark `src` as **Sources** (blue) and `test` as **Tests** (green).
4. **Libraries → + → Java** → choose `lib/junit-platform-console-standalone-1.10.2.jar` → set its scope to **Test** in Modules → Dependencies.
5. Run `restaurant.app.Main` with the green ▶. To replay a run file: **Run → Edit Configurations → Modify options → Redirect input from** → `runs/stage1-victory.txt`.
6. Right-click `test` → **Run 'All Tests'**.

Don't let IntelliJ convert the project to Maven or Gradle, and don't use its "Add JUnit to classpath" quick-fix, which downloads its own copy. Everyone uses the bundled jar so IntelliJ, VS Code and the build scripts all behave the same.

**Daily git routine**
```sh
git switch main && git pull                      # start from the latest main
git switch -c a/customer-core                    # one branch per task: <letter>/<topic>
# ...work, build, test...
git add <files>
git commit -m "Add satisfaction floor at 0 (§3.2 step 5)"
git push -u origin a/customer-core               # then open a PR on GitHub, request a reviewer
git switch a/customer-core && git merge main     # if main moved while you worked
git switch main && git pull && git branch -d a/customer-core   # after the PR is merged
```

**`.github/workflows/ci.yml`** (C updates `STAGE` after each tag)
```yaml
name: ci
on: [push, pull_request]
env:
  STAGE: "1"
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with: { distribution: temurin, java-version: "17" }
      - run: sh build.sh
      - run: sh test.sh
      - name: Replay run files for current stage
        run: |
          for f in runs/stage${STAGE}-victory*.txt runs/stage${STAGE}-defeat*.txt; do
            [ -e "$f" ] || continue
            case "$f" in *victory*) want=VICTORY ;; *) want=DEFEAT ;; esac
            out=$(sh run.sh < "$f")
            echo "$out" | grep -q "$want" || { echo "FAILED: $f (expected $want)"; exit 1; }
            echo "OK: $f -> $want"
          done
```

## Appendix E: Tag ceremony (for each stage)

Led by C, with all three present (a call is fine).

- [ ] All of this stage's PRs are merged; its issues are closed or moved to the next milestone
- [ ] CI is green on `main`
- [ ] **Fresh clone** into a new folder → `build` → `test`: everything passes
- [ ] Replay this stage's run files → VICTORY / DEFEAT and final numbers match `test-notes.md`
- [ ] **No future-stage code:** search `src/` for `VIP|Critic|Host|Combo|HappyHour` (Stage 1) or `Host|Combo|HappyHour` (Stage 2) → no hits
- [ ] Tag and push: `git switch main && git pull` → `git tag -a stage-1 -m "Stage 1: basic restaurant operation"` → `git push origin stage-1`
- [ ] Check that the tag appears on GitHub (optionally create a Release with 3 lines of notes)
- [ ] UML snapshot `docs/uml/stage-N.png` committed (A)
- [ ] README transition section written (C, for stages 2 and 3)
- [ ] Update `STAGE` in `ci.yml`, then post "stage-N tagged ✅" in chat

> Never move a pushed tag. If a bug in an old stage is found later, fix it on `main` and mention it in the README. Move the tag only if the tagged commit doesn't build, and say so in the README.

## Appendix F: Report outline (PDF)

| § | Section | Owner |
|---|---|---|
| 1 | Cover: group, members, repo URL | C |
| 2 | Overview and how to build/run/replay | C |
| 3 | **Class diagram** (full page) + annotations for encapsulation, polymorphism, composition | A |
| 4 | **Sequence diagram** + walkthrough (where the manager's choice changes the outcome) | B |
| 5.1 | Key abstractions and responsibilities table | A (everyone sends rows) |
| 5.2 | SOLID, with proof from the code | C (everyone sends examples) |
| 5.3 | Trade-offs and one alternative design | C + B |
| 5.4 | How the player's choices change a result (numbers from the runs) | C |
| 6 | Stage evolution: UML snapshots 1/2/3, what changed and why, compare links | A (snapshots) + C (text) |
| 7 | Functional evidence: test summary, run results, edge cases | A |
| 8 | Reflections (½ page each) | Everyone |
| 9 | Appendix: DECISIONS summary | A |

**SOLID ideas (use only what is really in the code)**
- **SRP:** `ConsoleUI` only does I/O; `Ledger` only does totals; `Order` only tracks its own progress.
- **OCP:** Stage 2 added customer types without touching `GameController` (the 1→2 diff proves it); Happy Hour was added as one step in `PriceCalculator`.
- **LSP:** every `Customer` subtype passes the same flow test.
- **DIP:** `GameController` depends on the `GameView` interface, and tests swap in a scripted view.
- **ISP:** include only if you really have small, client-specific interfaces.

**Trade-off and alternative ideas**
- Inheritance for customer types vs. composition (a `Customer` holding a `CustomerProfile` strategy)
- Locking the price in `Order` vs. recalculating it at payment
- The manager choosing the Host's action vs. an automatic Host
- Executing each staff choice immediately vs. collecting all choices first

## Appendix G: Video plan (about 15 minutes)

| # | Segment (brief §6.2) | Speaker | ~min | What to show |
|---|---|---|---|---|
| 1 | Game launching from the submitted project | C | 1 | Unzip → build → run |
| 2 | Manager choices with constrained staff | C | 2.5 | Two customers need the Waiter in one turn; an invalid choice being refused and asked again |
| 3 | READY → SERVED → payment | B | 2 | Including "READY this turn, can't serve until next turn" |
| 4 | VIP or Critic behaviour | A | 2 | VIP −5 and 10% bill; the Critic's behaviour; 20 s of the overriding code |
| 5 | Stage 3 feature | A (Host) 1, B (Combo + Happy Hour) 2 | 3 | Host + Waiter seating in the same turn; combo price; Happy Hour $10.80 and countdown |
| 6 | One victory and one defeat | A | 2 | Replayed quickly with `run < runs/stage3-victory.txt` |
| 7 | Tag comparisons and diagrams | C (tags) 1, A (class) 1, B (sequence) 1 | 3 | GitHub compare pages; walk through both diagrams |

Recording tips: terminal font ≥ 18 pt, 1080p, test your microphone first. Each person records their own part and C stitches them together. Upload to OneDrive or Google Drive, **open the link in a logged-out browser to check access**, and keep it available until grades are released. Put the timestamps in the README.

## Appendix H: Final submission checklist

- [ ] Zip named `<LAB>_<Grp>.zip` (e.g. `FCS1_Grp1.zip`)
- [ ] Source code + build/run instructions
- [ ] README: repo URL · Stage 1→2 and 2→3 (new files, changed or deleted files with reasons, compare links) · video timestamps
- [ ] PDF report (Appendix F, all sections present)
- [ ] Video file, or a link that opens without logging in
- [ ] Signed Declaration of Original Work
- [ ] **Each member's** GAI declaration
- [ ] Graders can open the repo
- [ ] Tags `stage-1`, `stage-2`, `stage-3` visible on GitHub; each builds from a fresh clone
- [ ] Class diagram cross-checked against the code (every class, attribute and operation)
- [ ] **Unzip on a different laptop with Wi-Fi off** → build → test → play → run files work
- [ ] Submitted before the deadline; confirmation screenshot posted in the group chat

## Appendix I: PR template (`.github/pull_request_template.md`)

```markdown
## What
<!-- one or two lines -->

## Why (brief section)
<!-- e.g. §3.2 step 5 -->

## Files
- New:
- Changed (and why):     <!-- C copies this into the README stage comparison -->
- Deleted (and why):

## How I tested
<!-- tests added, run files replayed -->

## Checklist
- [ ] Builds and all tests pass locally
- [ ] No game rules in ConsoleUI
- [ ] No instanceof / type switch for behaviour
- [ ] Fields private; no public setters for state
- [ ] Money in int cents; rounding only in Money
- [ ] No next-stage features merged early
- [ ] DECISIONS.md / CONTRACT.md updated if behaviour or a signature changed
```

**Reviewer rule:** review within 24 hours, and leave at least one real comment, or write "Checked X, Y, Z", before approving.

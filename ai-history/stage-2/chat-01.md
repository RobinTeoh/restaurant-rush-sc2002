# Stage 2 - AI history, chat 01

Source session record: 8e495f31-2782-45d1-ac24-428e34e6031c.jsonl
Period (Singapore time): 2026-10-23 01:32 to 2026-10-25 00:54
Assistant: Claude Code (Anthropic). Prompts and replies are in original order and are not summarised.
Left out: hidden reasoning, raw tool output, and harness-injected system reminders. Tool calls appear as one-line markers.

---

### 1. Claude - 2026-10-23 01:32

_[tool call: Write - C:\Users\robin\Documents\SC2002\report-notes-LOCAL.md]_

### 2. Claude - 2026-10-23 01:32

_[tool call: Write - C:\Users\robin\.claude\projects\c--Users-robin-Documents-SC2002\memory\solid-per-stage-rule.md]_

### 3. Claude - 2026-10-23 01:32

_[tool call: Write - C:\Users\robin\.claude\projects\c--Users-robin-Documents-SC2002\memory\MEMORY.md]_

### 4. Claude - 2026-10-23 01:32

Yes, I understand. From Stage 2 onward, every change should apply design principles, and the finished design needs at least one real example of each of S, O, L, I and D.

## Where the notes are
[report-notes-LOCAL.md](../report-notes-LOCAL.md) is saved in `C:\Users\robin\Documents\SC2002\`, **one level above the repo**. It's outside git, so it can never be committed by accident. I'll add new trade-offs and evidence there, not in the repo.

It contains:
- a SOLID tracker with the plan for each principle
- the Stage 1 to Stage 2 change story
- 9 trade-offs and alternatives collected so far (State pattern vs enum, fixed seed, no static state, and so on)
- the evidence you already have (74 tests, the 50,000-game check, the strategy comparison)
- your report to-do list and what to put in your GAI declaration

## The SOLID plan, honestly

| Principle | Status | Planned highlight |
|---|---|---|
| **S** | Partly there (`CustomerState.losesSatisfaction()`, `Order`, `GameStats`) | **Split `GameController`.** It's 319 lines doing the loop, menus, labels and display building. Do this before Stage 3 piles Host and Happy Hour into it. |
| **O** | Set up for Stage 2 | VIP and Critic added **without editing the controller**. Proof: the diff of `control/` between the tags is empty or registration only. |
| **L** | No example yet | `Customer` becomes the parent. One test runs the **same full flow** through all three types. |
| **I** | Weak so far (`GameView` is small, but both implementers use all of it) | Needs a real case. The best candidate is the Stage 3 **Host and Waiter both seating customers**, through one small role. If nothing genuine comes up, don't force it. |
| **D** | Already true (`GameController` depends on `GameView`; tests swap in `ScriptedView`) | Stage 3: a **pricing abstraction** that Happy Hour plugs into. |

**The risk:** **I and L have no real example today.** I'll steer Stage 2 and 3 so they appear on purpose instead of being forced at the end. Each time we make a change, I'll say which principle it shows, and we'll log it in `docs/design-log.md`.

I've also saved this as a standing rule, so it carries into later sessions.

Before Stage 2, the Stage 1 items are still open: push the `a/stage1-runs` branch, merge the PR, and get Caleb to tag.

### 5. Robin - 2026-10-24 23:19

lets check the stage 1 all ready, then we will begin our stage 2

### 6. Claude - 2026-10-24 23:19

_[tool call: Bash - Fetch and show tags, branches, main history and unpushed commits]_

### 7. Claude - 2026-10-24 23:19

`stage-1` now exists and points at the merge of your PR #13. I'll verify it the way a grader would: fresh clone, check out the tag, build, test, and replay the runs.

### 8. Claude - 2026-10-24 23:19

_[tool call: Bash - Fresh-clone the repo, check out stage-1 and scan for later-stage code and evidence files]_

### 9. Claude - 2026-10-24 23:20

_[tool call: PowerShell - Build, test and replay all run files on the tagged fresh clone]_

### 10. Claude - 2026-10-24 23:20

_[tool call: Bash - Check the tag is on main, the CI stage setting, open decisions and README links at the tag]_

### 11. Claude - 2026-10-24 23:20

**Stage 1 is ready.** I checked the tag the way a grader would: a fresh clone of GitHub, checked out at `stage-1`.

## Verification of the `stage-1` tag

| Check | Result |
|---|---|
| Tag exists and is on `main` | ✅ Annotated tag at `14f368c`, the merge of your PR #13 |
| Builds from a fresh clone | ✅ `Build OK` |
| Tests | ✅ 74 of 74 pass |
| Replay of all 4 run files | ✅ victory 4 paid $57.00, victory 5 paid $75.00, defeat, and invalid-input all give the expected results |
| No Stage 2/3 code | ✅ the only hit is the comment "Stage 1 has no discounts" in `Order.java` |
| Docs at the tag | ✅ test notes, decisions (D5–D13 closed), contract and README are all in it |
| README compare links | ✅ present (`stage-1...stage-2`) |

## One gap
The **whole-system class diagram is not in the tag**. It's still local and uncommitted on a branch GitHub has already deleted: `stage-1-class-diagram.drawio` and the design-log edit, and the PNG isn't exported yet. I wouldn't move the tag, since the code is complete and correct. Instead, add the diagram to `main` now, and say in the README "Stage 1 snapshot added after the tag (docs only, no code changed)". Do it before starting Stage 2 so the history stays clean:

```
git switch main
git pull
git switch -c a/stage1-diagram
```
Your uncommitted files carry over. Export `docs/uml/stage-1.png` from draw.io, then:
```
git add docs/uml/stage-1-class-diagram.drawio docs/uml/stage-1.png docs/design-log.md
git commit -m "Add Stage 1 whole-system class diagram (docs only, after the stage-1 tag)"
git push -u origin a/stage1-diagram
```
Open a PR for a teammate to review.

## Starting Stage 2
**Your task:** `VIPCustomer` (loses 5, 10% off), `CriticCustomer` (loses 12, plus a special behaviour), a mixed arrival list with at least one VIP and one Critic (still 6 arrivals), no type checks in the controller, and new victory and defeat runs.

**Stage 1 → 2 design plan, with the SOLID points we need:**

| Change | Principle it shows |
|---|---|
| `Customer` becomes the parent class; each type gives its own loss per turn | **O**: new types without editing `GameController` |
| One test runs the same full flow through all three types | **L**: substitutability |
| The Critic's special behaviour lives only inside `CriticCustomer` | polymorphism, no type checks |
| `getDiscountPercent()` added (default 0, VIP 10). Ian's pricing must read it. | **O** / contract change to agree with Ian |
| The controller creates customers from a scenario (a list of types), not hardcoded `new Customer` | needs Caleb: a way to choose the type per arrival |

**Decisions to make first** (before any code):
1. **D7, the Critic's special behaviour.** The brief wants "a distinct order preference or reaction to service". Options: (a) always orders the most expensive dish, (b) loses extra satisfaction if served a dish that was READY but waited, (c) pays nothing extra but leaves earlier. Which fits best with the seed, so the base run stays winnable?
2. **D6b, the mixed arrival list.** Which two arrivals become VIP and Critic?
3. **How the controller creates different types.** This touches Caleb's `GameController`, so agree it with him first.

Want to start with **D7 and D6b**? I'll bring the numbers for each option, like I did for the seed, so we pick one that keeps the game winnable.

### 12. Robin - 2026-10-24 23:49

i think it should be fine, the requirement for stage should be code only, class diagram is submitted in report, we will proceed to stage 2

1) It may just leave wihtout paying? is this okay?
2) this must be fixed right? then VIP 2nd, crtic 4th
3) it should be polymorphism, use upcasting to call the customer, and their action, its just critics got special behaviour to trigger

### 13. Claude - 2026-10-24 23:50

I'll start with the class diagram, then take your three points in order.

**Class diagram:** that's your call, and nothing blocks Stage 2. One thing to know: brief §5 says the diagrams go in the PDF report **and** the repository, and "stage snapshots" are part of the 20 process marks. So at least commit the `.drawio` file. Skipping the PNG export is fine.

## 1. Critic leaves without paying: allowed, but it has a cost

The brief allows it: Critic needs "a distinct order preference **or reaction to service** that you define and document", and leaving unpaid is a reaction to service. But in the current code it would force changes to Caleb's controller:
- `GameController` calls `finishEating()` and adds the customer to the payment line without checking `hasLeft()`. A Critic who left would reach `Cashier.collect()`, and `pay()` would throw.
- A SERVED order can't be cancelled (`canBeCancelled` is PLACED or READY only), and the controller would need to free the table and count the departure.
- Brief §3.2 step 5 says served, eating and ready-to-pay customers lose nothing, and §4.2 says all types follow the same service flow. A mid-flow exit pushes against both.

| Option | Where it lives | Controller change | Example of polymorphism |
|---|---|---|---|
| **A. Order preference** (my recommendation): the Critic always orders the most expensive dish | `CriticCustomer` overrides `chooseFood()` | none | Same call `customer.chooseFood(menu)`, a different dish |
| **B. Leaves unpaid** | `CriticCustomer` plus the controller, `Order` and `Cashier` | yes, at least three places | Works, but it's not "just add files" |

If you want B, we need an exact trigger first (for example "served when satisfaction is below 50"), and Caleb has to agree to the controller change. I'd go with A, because it keeps the Stage 2 diff to new files only, which is your Open/Closed evidence.

## 2. Arrival list: yes, it must be fixed

The brief says the base run must include at least one VIP and one Critic, still six arrivals, and be reproducible. So a **fixed list, VIP 2nd and Critic 4th**, is right. A rough estimate with seed 32 (I'll verify by simulation once it's coded):
- customers: Regular, **VIP**, Regular, **Critic**, Regular, Regular
- dishes (id-based seed): Salad, Burger, Pasta, Burger, Pasta, Salad
- revenue from the first five, with VIP at 10% off: about $73.50. Even the worst four are about $55.50, above the $45 target.
- a Critic served as fast as the Stage 1 winning run would end around 64 satisfaction (3 waiting turns at 12). So the average stays comfortably above 60.

One caution: if the Critic always orders Pasta (option A with "most expensive"), the game has 3 Pasta, which goes against D5's aim of at most 2. Choose a preference that doesn't clash, or re-check the number.

## 3. Polymorphism: you're right

- The controller already holds `List<Customer>` and calls `customer.loseSatisfaction()`, `chooseFood(menu)` and so on. Creating `new VIPCustomer(2)` and storing it as a `Customer` is the upcast, and Java picks the right version at runtime. The controller never asks "which type are you?".
- Only the Critic needs a **hook** where its special behaviour fires. With option A that hook is `chooseFood`, which already exists. VIP and Critic differ in the loss amount and the discount through overridden methods.
- **The one controller change:** line `new Customer(nextCustomerId++)` creates the type. The clean fix is to give the controller a list of creators, one per arrival (a `Supplier`-style function per arrival, set up in `Main`), so the controller names no concrete classes. That's also your **Dependency Inversion** evidence.

## The design for your review
- `Customer` becomes **abstract**, with `RegularCustomer`, `VIPCustomer` and `CriticCustomer`. The loss per turn becomes an abstract method. `getDiscountPercent()` is added (default 0, VIP 10).
- This changes Stage 1 tests and one controller line. Both go in the README's "Stage 1 → 2" section with their reasons.

## Your decisions
1. **Critic behaviour: A (order preference) or B (leave unpaid)?**
2. If A, which preference? "Always orders the dish with the most prep units" gives Pasta. A softer option such as "never orders Salad" avoids the Pasta count problem.
3. **OK to ask Caleb for the one-line creator change in the controller?**

Tell me and I'll update `DECISIONS.md` (D6b, D7) and the design log, so you can start Stage 2 on a clean branch.

### 14. Robin - 2026-10-24 23:57

[image attached]
okay, do with the easiest one with minimal code change, choose the most expensive food, but will it affect the game, that the max pasta thingy, and attached is my draft class diagram, u see is it aligned, and also, stage 2 is fully by me, so can edit

### 15. Claude - 2026-10-24 23:58

Your draft is close, and I'll review it below. First I'll answer "will the Pasta rule affect the game?" with a test instead of a guess. I'll build a throwaway Stage 2 copy in my scratch folder, not your repo, and run the same 30 strategies on it.

### 16. Claude - 2026-10-24 23:58

_[tool call: Bash - Build a throwaway Stage 2 copy with VIP 2nd and Critic 4th, and run 30 strategies on it]_

### 17. Claude - 2026-10-24 23:58

_[tool call: Grep - C:\Users\robin\Documents\SC2002\restaurant-rush]_

### 18. Claude - 2026-10-24 23:58

Choosing "most expensive food" works and barely affects the game. I tested it, and your draft diagram needs several fixes to match the real code.

## Does "most expensive" affect the game and the Pasta limit?

I built a throwaway Stage 2 copy (not your repo): VIP 2nd, Critic 4th, and the Critic orders the most expensive dish.

| Item | Result |
|---|---|
| Dishes | Salad, Burger (VIP), Pasta, **Pasta** (Critic, was Burger), Pasta, Salad |
| Pasta count | **3**, one more than D5's "at most 2" aim |
| Winnable? | ✅ Yes. 24 of 30 strategies win. The 6 losers are the "Chef never cooks" ones, as in Stage 1. |
| Typical win | 4 paid, $58.50, average about 74 to 75 |
| Best win (seat customer 5 before serving customer 4) | 5 paid, $76.50, average 73.2 |
| Kitchen pressure | No problem: the Pasta customers arrive 3 turns apart and the Chef only needs 2 units |

The "max 2 Pasta" line was our team's own aim (D5), not the brief's, and the code no longer counts Pasta anyway. So:
- Accept 3 Pasta in the Stage 2 base run, and note it in `DECISIONS.md` as "D5 aim applies to the Stage 1 regular setting. In Stage 2 the Critic's preference deliberately adds one Pasta."
- `FoodChoiceTest.atMostTwoPastaPerGame` still passes, because it builds regular customers. Add a separate test for the mixed setting that locks in the Critic's dish.
- Bonus: because customer 4 would otherwise order a Burger, the Critic's preference is **visibly different** in the video, which is what a grader wants to see.

## Your draft diagram against the code

The structure is right (abstract `Customer`, three children, enum, inheritance arrows pointing at `Customer`). But it still has the **old Stage 1 v1 design** in it, so it doesn't match the code:

| Draft | Real code / design | Fix |
|---|---|---|
| `tableNo`, `chosenFood`, `recordedSatisfaction`, `random {static}`, `pastaCount {static}` | None of these exist (dropped on purpose) | Remove them |
| `seat(table: int)` | `seat()` | Fix |
| `recordSatisfactionAtPayment()`, `getChosenItem()`, `getTable()` | Not in the code | Remove |
| `getDiscountPercent(): double` | Money is integers: the percent is an `int` (0, 10) | Change to `int` |
| Children have `discountRate: double` and `satisfactionLossPerTurn` as static fields | A **static field can't be overridden**, so `Customer` can't ask a child for its value | Make it a method: `# satisfactionLossPerTurn(): int {abstract}` in `Customer`, overridden in each child |
| `CriticCustomer` has `specialBehaviour` | That isn't a real operation | Replace with `# pickDish(menu: Menu): MenuItem` (picks the most expensive) |
| Regular and Critic have `discountRate = 0` | Redundant | `Customer.getDiscountPercent()` returns 0 by default; only `VIPCustomer` overrides it (10) |
| `# Customer(id)` | Correct for an abstract class | Keep |
| `<<abstract>>` `Customer` | Correct | Keep, with the name in italics |

**The smallest-change design (what the sim used):**
- `Customer.chooseFood(menu)` stays public, does the state check and `SEATED → ORDERED`, and calls a new protected `pickDish(menu)`.
- `Customer.pickDish` uses the seed (Stage 1 behaviour). `CriticCustomer` overrides **only** `pickDish`. The state rules are written once, so the Critic can't break them (**Liskov**), and nothing in the controller changes for it (**Open/Closed**).

## The code changes needed (since Stage 2 is yours to edit)
- **New:** `RegularCustomer`, `VIPCustomer`, `CriticCustomer`.
- **`Customer`:** abstract, `satisfactionLossPerTurn()` replaces the constant, plus `pickDish` and `getDiscountPercent()`.
- **`GameController`:** one line, the `new Customer(...)` for arrivals. Choose the type per arrival (VIP 2nd, Critic 4th) without type checks, for example a list of creator functions set up in `Main`.
- **`Order.getBill()` (Ian):** apply `customer.getDiscountPercent()` with D1's half-up rounding.
- **Tests:** 43 uses of `new Customer(` in 8 test and source files must become `new RegularCustomer(`. Mechanical, but they must go in the README's "Stage 1 → 2" section with the reason.
- Give Caleb and Ian a heads-up in chat before you edit their folders.

Ready when you are: shall I update `DECISIONS.md` (D6b: VIP 2nd, Critic 4th; D7: most expensive; D5 note) and the design log first, so you can start on a clean `a/stage2-customers` branch? Then redraw the diagram with the fixes above and send it to me to check.

### 19. Robin - 2026-10-25 00:10

okay confirm one more time of stage 2 plan, then u can proceed build, make sure we hit the design principle for report also, and update me what u have changes, so i can send to the team, then finally update the class diagram (new drawio as stage 2), and also for satisfaction are we using static final instead like the code now (sorry i used old diagrma just now) , give me the detailed plan u going to do before u proceed

### 20. Claude - 2026-10-25 00:10

Before writing the plan, I'll check the repo's current state so the plan matches reality.

### 21. Claude - 2026-10-25 00:10

_[tool call: Bash - Fetch and show branch, uncommitted files and latest main]_

### 22. Claude - 2026-10-25 00:10

_[tool call: Grep - C:\Users\robin\Documents\SC2002\restaurant-rush\src]_

### 23. Claude - 2026-10-25 00:11

Here is the full plan for your approval. I won't change anything until you say go.

## First, your satisfaction question: `static final` or not?

Keep `static final`, but **move it into each subclass**. A `static final` in `Customer` can't vary by type, and Java can't override a field, so `Customer` would always use 8. The fix:

```java
// Customer (abstract)
protected abstract int satisfactionLossPerTurn();   // loseSatisfaction() calls this

// VIPCustomer
private static final int LOSS_PER_TURN = 5;
private static final int DISCOUNT_PERCENT = 10;
@Override protected int satisfactionLossPerTurn() { return LOSS_PER_TURN; }
@Override public int getDiscountPercent() { return DISCOUNT_PERCENT; }
```

The constant keeps the number readable, and the overridden method makes it polymorphic. This is the **Template Method** pattern: `loseSatisfaction()` holds the shared rules (which states, floor at 0, LEFT at 0) once, and asks the subclass only for the number.

## Stage 2 plan

### Step 0: Set up the branch
- Update your local `main` (it's 2 commits behind), then create `a/stage2-customers`.
- Your uncommitted Stage 1 diagram (`stage-1-class-diagram.drawio`) and the design-log edit come along. I'll keep them as a separate docs commit, so Stage 2 code and Stage 1 docs aren't mixed.
- I make the changes; **you commit and push**. I'll give you the commit groups at the end, so the history shows clear steps.

### Step 1: Customer types (`customer/`, your folder)
| File | Change |
|---|---|
| `Customer` | **abstract**. `LOSS_PER_TURN` removed. Adds `protected abstract int satisfactionLossPerTurn()`, `public int getDiscountPercent()` (returns 0), `protected MenuItem pickDish(Menu)` (the seed choice from Stage 1), and `public abstract String getTypeName()` for the display. `chooseFood()` becomes **`final`**: it keeps the state check and `SEATED → ORDERED` and calls `pickDish()`, so no subclass can break the state rules. |
| `RegularCustomer` (new) | loss 8, name "Regular" |
| `VIPCustomer` (new) | loss 5, discount 10, name "VIP" |
| `CriticCustomer` (new) | loss 12, name "Critic", **overrides only `pickDish()`**: the most expensive dish on the menu (D7) |

### Step 2: Arrival types without type checks (`control/` and `app/`, Caleb's folders)
- `ArrivalSchedule` also holds **one creator per arrival** (Java's built-in `IntFunction<Customer>`, so no new interface just for show), with `createCustomer(int id)`.
- `GameController` arrival line: `new Customer(...)` becomes `schedule.createCustomer(...)`. That's the **only** controller logic change, and the controller no longer names any customer class.
- The base setting list lives in `GameConfig`: Regular, **VIP**, Regular, **Critic**, Regular, Regular (D6b). `Main` uses it.
- `ArrivalSchedule(turns)` with only turns keeps meaning "all Regular", so the Stage 1 controller tests stay unchanged. That's your proof that earlier behaviour still works (brief §4.4).

### Step 3: VIP discount (`pricing/`, `order/`, Ian's folders)
- `Money.percentOff(cents, pct)`: the **one place** D1's half-up rounding lives. Stage 3's Happy Hour will reuse it.
- `Order.getBill()` returns `Money.percentOff(item price, customer.getDiscountPercent())`. VIP Burger is $13.50.

### Step 4: Show customer types to the player (Caleb's display)
Without the type showing, the manager can't make meaningful VIP/Critic choices, and the video can't show segment 4.
- `TurnSnapshot.CustomerLine` gets a `type` field, and `ConsoleUI` prints it, e.g. `Customer 2 (VIP)`.
- Arrival and "seat/serve which customer" labels show the type too.

### Step 5: Tests
| Test | Proves |
|---|---|
| Update existing tests: 43 uses of `new Customer(` become `new RegularCustomer(` (abstract classes can't be created) | Stage 1 behaviour unchanged |
| `VIPCustomerTest` | loses 5; 10% off; VIP Burger bill 1350 |
| `CriticCustomerTest` | loses 12; always orders Pasta; still needs to be SEATED |
| **`CustomerSubstitutionTest`** | the **same full flow** works for Regular, VIP and Critic through a `Customer` reference (**Liskov**) |
| `MoneyTest` additions | 1500 at 10% is 1350; half-up rounding |
| `OrderTest` addition | VIP bill is discounted; Regular bill isn't |
| `ArrivalScheduleTest` | VIP 2nd, Critic 4th; turns-only means all Regular |
| `FoodChoiceTest` addition | Stage 2 base dishes are Salad, Burger, Pasta, Pasta, Pasta, Salad; any 4 of the first 5 still reach $45 |
| `GameControllerTest` addition | the base game creates VIP 2nd and Critic 4th |

### Step 6: Verify
- Build plus all tests, then search `src/` for `instanceof` (expect none).
- The 50,000-game random check, re-run with VIP discount and Critic checks added.
- Record `runs/stage2-victory.txt`, `stage2-victory-5paid.txt` and `stage2-defeat.txt`, and replay each twice through `run.bat`.
- CI: set `STAGE: "2"` so GitHub replays the Stage 2 runs.

### Step 7: Docs
- `DECISIONS.md`: D6b (VIP 2nd, Critic 4th), D7 (most expensive dish), D5 note (the Stage 2 setting has 3 Pasta on purpose).
- `CONTRACT.md`: B → A `getDiscountPercent()` settled; `Customer` abstract plus the three types; `ArrivalSchedule.createCustomer`.
- `docs/test-notes.md`: Stage 2 runs and edge cases.
- `README.md`: the **Stage 1 → 2** section with new files, changed files with a reason each, and the compare link.
- `docs/design-log.md`: one row per decision.
- `report-notes-LOCAL.md` (outside git): updated SOLID tracker with real evidence.

### Step 8: Class diagram
A **new** `docs/uml/stage-2-class-diagram.drawio`, the whole system, same tidy style (straight lines, no crossings, checked). The inheritance tree under `Customer` is the new focus, with notes for polymorphism, Template Method and LSP.

### Step 9: The team message
A short summary for Ian and Caleb: what changed in their folders, why, and what to review.

## SOLID evidence this gives you (honest)

| Principle | Stage 2 evidence | Strength |
|---|---|---|
| **S** | Rounding moved into `Money.percentOff`. "Who arrives when" moved from the controller into `ArrivalSchedule`. The Critic only decides its dish. | Good |
| **O** | VIP and Critic added as new classes. The controller's logic doesn't change; a future type is one new class plus one list entry. | **Strong** |
| **L** | `CustomerSubstitutionTest`, plus `final chooseFood()` so subclasses can't break the state machine | **Strong** |
| **I** | **None in Stage 2.** I won't invent one. The plan is still Stage 3: Host and Waiter both seating customers. | Gap, stays planned |
| **D** | The controller depends on customer creators supplied from configuration, not on concrete classes, plus the existing `GameView` | Good |

## Please confirm three things
1. **Show the customer type in the display** (Step 4)? It touches Caleb's `TurnSnapshot` and `ConsoleUI`, but without it the player can't see who is VIP or Critic. I recommend yes.
2. **`Money.percentOff` in Ian's `pricing/` folder**, rather than rounding inside `Order`? I recommend yes.
3. **Keep the turns-only `ArrivalSchedule` constructor** so Stage 1 tests stay as proof of earlier behaviour? I recommend yes.

Say **go** (and answer 1–3, or just "all yes"), and I'll start with Step 0.

### 24. Robin - 2026-10-25 00:21

yes, this is my thinking for static final also, but why suddenly use pickDish(), not using back the choseFood from stage 1, only overwrite for Critic customer of this chooseFood method

I dont get what is Step 2 doing, isnt we just create a list to append all Customer type, then we just run the list by polymorphism (for each c in CustomerList), customerList store all customer type? or isit caleb already use this in stage 1, my plan is minimal change to their code, just slight edit to add in the customer type (because thats how a good design princlple should looks like)

### 25. Claude - 2026-10-25 00:22

Both are good questions. For the first, overriding `chooseFood` doesn't actually work in our code. For the second, your list idea is better than mine, and I'll use it.

## 1. Why `pickDish()`, and why not just override `chooseFood()` for the Critic?

`chooseFood()` does **two jobs** in Stage 1:
1. **State rules:** the menu isn't null, the customer must be SEATED, then it becomes ORDERED.
2. **Picking the dish:** the seeded random choice.

The Critic only differs in job 2. If `CriticCustomer` overrides the whole of `chooseFood()`, it must redo job 1 itself, and **it can't**: `state` and `requireState()` are `private` in `Customer`, so the Critic can't set ORDERED. To make it work you'd have to:
- make `state` protected or add a setter, which breaks the encapsulation we designed, or
- copy the state rules into the Critic. If someone forgets one, the Critic could order while still WAITING. That breaks the parent's rules, a **Liskov** violation.

There is a hacky alternative: call `super.chooseFood(menu)` for the state change, ignore the random dish it returns, and return Pasta. It works, but it picks a dish only to throw it away, which reviewers notice.

**With `pickDish()`,** `chooseFood()` stays exactly as in Stage 1 (same name, same contract, Caleb's code unchanged), and its one line of dish-picking moves into a small `protected pickDish()`. The Critic overrides only that. Job 1 is written once and the Critic can't break it. It's a small refactor of your own Stage 1 code, and it's the same pattern you agreed to for the loss per turn (Template Method).

## 2. Step 2: your idea is better

You're right that the polymorphism is **already there**. Caleb's controller keeps `List<Customer> customers` and does `for (Customer c : customers) c.loseSatisfaction()`, so nothing in those loops changes.

The only line that breaks is the one that **creates** customers:
```java
Customer customer = new Customer(nextCustomerId++);   // GameController, arrivals()
```
`Customer` becomes abstract, so this no longer compiles, and something must say "the 2nd arrival is a VIP". If the controller decided (`if (id == 2) new VIPCustomer...`), that's a type chain in the turn controller, which brief §4.2 forbids.

**Your approach:** build the list of customers up front, and the controller just takes the next one when someone arrives:
```java
// GameConfig (or Main): the Stage 2 base setting, ids fixed in arrival order
List.of(new RegularCustomer(1), new VIPCustomer(2), new RegularCustomer(3),
        new CriticCustomer(4), new RegularCustomer(5), new RegularCustomer(6))
```
It's simpler than my "creator per arrival" idea. Concretely:
- **`ArrivalSchedule`** (one small edit): it gets the arrival turns **and** this list, and `arrivalsOn(turn)` returns the customers arriving that turn instead of a count. The turns-only constructor stays and builds all `RegularCustomer`s, so the Stage 1 tests still prove earlier behaviour.
- **`GameController`** (3 lines in `arrivals()`): loop over `schedule.arrivalsOn(turn)` instead of `new Customer(nextCustomerId++)`. The unused `nextCustomerId` field goes.
- **`Main`**: passes the Stage 2 list.

The controller never names a concrete customer class, which is your **Open/Closed** and **Dependency Inversion** evidence. Adding a new type later means one new class plus one list entry.

One thing to know: the customer objects exist before the game starts (all WAITING at 100), and a list can only be played once. That's fine: `Main` builds one game, and each test builds a fresh list.

## Revised Steps 1–2 (everything else unchanged)
1. **`customer/`:** `Customer` abstract; `chooseFood()` stays, now `final`, and calls `pickDish()`; `satisfactionLossPerTurn()` abstract; `getDiscountPercent()` (0); `getTypeName()`. New `RegularCustomer`, `VIPCustomer`, `CriticCustomer` each have their own `static final` constants, and the Critic overrides only `pickDish()`.
2. **Arrivals:** your pre-built list in `ArrivalSchedule`, a 3-line change in `GameController`, and `Main` passes the Stage 2 list.

Say **go** and I'll start.


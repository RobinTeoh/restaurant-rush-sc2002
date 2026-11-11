# Restaurant Rush: development plan

> **Historical document.** This is the plan we wrote at the start of the project. Paths below are as planned on day one: `DECISIONS.md`, `CONTRACT.md`, the design log, the principles notes and the test notes now live under [docs/design/](../design/) and [docs/testing/](../testing/). What was actually built and tested is recorded in the [README](../../README.md) and the report.

Roles (from [team-roles.md](team-roles.md)):
**A = Customers (Robin)**, **B = Kitchen, Menu & Pricing, incl. all of Happy Hour (Ian)**, **C = Game loop, Staff & CLI (Caleb)**.
Diagrams: **Takshak = class diagrams**, **Sibghat = sequence diagram**. The two work together and review each other's diagram (see Week 3, step 5).

The plan assumes **4 weeks**. If your deadline is shorter, squeeze each week proportionally, but keep the order: the tags must happen in sequence.

> **Status:** first meeting held 12 Oct 2026. Decisions recorded in [DECISIONS.md](../design/DECISIONS.md).
> Lines marked **➕ Addition** were added after the meeting to protect rubric marks; everything else is the plan as agreed.

---

## Week 0 (first 1–2 days): kickoff, everyone together

### Meeting agenda (about 2 hours)
1. Everyone reads the brief beforehand, then go through sections 2–3 together.
2. **Trace the section 3.3 Pasta example on a whiteboard.** That table becomes your first acceptance test.
3. Draw the customer state machine and the order status machine.
4. Decide and write down the open questions in `DECISIONS.md`:
   - rounding (once at the end, or after each discount)
   - Cashier "oldest first" (arrival order or READY_TO_PAY order)
   - what happens to a READY order when its customer leaves
   - Host: automatic or manager-chosen, and before or after the Waiter
   - **food-choice rule** (check that 4 paying customers can reach $45)
   - Critic's special behaviour
5. Agree the **connection points** and write them in `CONTRACT.md`, with the exact method name, parameters and return type:
   - C → A: lose satisfaction, check if left, choose food, get state, record satisfaction at payment
   - C → B: create order, chef works on order, is ready, mark served, get bill
   - B → A: get discount for this customer
6. Pick a seed or arrival list for the base run.
   - ➕ **Addition:** with the agreed seeded food choice (D5), check the chosen seed lets 4 paying customers reach $45. A seed giving mostly Salad makes victory impossible.

### Repo setup (C does this right after the meeting)
> ➕ **Status:** already done. Robin created `RobinTeoh/restaurant-rush-sc2002` with build/run/test scripts, CI and a PR template. Caleb owns CI from here.

```
restaurant-rush-sc2002/
├── README.md
├── DECISIONS.md
├── CONTRACT.md
├── docs/            diagrams (uml/), report drafts (report/), test notes, design log
└── src/restaurant/
    ├── app/         Main                                    (C)
    ├── boundary/    ConsoleUI                               (C)
    ├── control/     GameController, ArrivalSchedule         (C)
    ├── staff/       Waiter, Cashier, Host (C) · Chef (B)
    ├── place/       Table, WaitingQueue                     (C)
    ├── customer/    Customer + subclasses, CustomerState    (A)
    ├── menu/        MenuItem, Menu, ComboMeal               (B)
    ├── order/       Order, OrderStatus                      (B)
    └── pricing/     money helpers, HappyHour                (B)
```
Class names are suggestions; rename freely. Each person **only edits their own folders**. If you need a change in someone else's folder, ask them in the group chat or open an issue.

Git rules:
- `main` must always compile.
- Work on branches named `a/customer-basics`, `b/chef`, `c/turn-loop` and so on.
- Merge by pull request. **One other member reviews** every PR (this is also your "development process" evidence).
- Commit small and often, with messages that say *why*.

**Done when:** the repo exists, everyone has pushed one commit, and DECISIONS.md and CONTRACT.md are merged.

---

## Week 1: build Stage 1 parts, each person alone

Everyone writes **fake versions** of the other people's classes (in their own test code, not in `main`) so their part runs alone.

### A: Customers
- [ ] Customer with id, satisfaction (starts 100), state, assigned table/order, satisfaction recorded at payment
- [ ] Loses 8 per waiting turn, minimum 0; leaves at 0
- [ ] Only loses while waiting or seated-not-served (not while eating or ready to pay)
- [ ] Food choice using your documented rule
- [ ] Discount method returns 0% for a Regular
- [ ] Small test `main`: a customer left alone for 13 turns hits 0 and leaves

### B: Kitchen & Menu
- [ ] Menu items with price in **cents** and prep units
- [ ] Order: items, units done, status (placed → READY → SERVED → PAID, or CANCELLED)
- [ ] Chef advances one order by one unit; it becomes READY on its last unit
- [ ] Bill = price × customer's discount; dollar display with two decimals
- [ ] Small test `main`: Pasta needs exactly 2 chef turns to be READY; bill $18.00

### C: Game loop, Staff & CLI
- [ ] Table (2) and waiting queue
- [ ] Turn loop running the six steps in order, 18 turns, no early ending
- [ ] Waiter: seat / take order / serve (READY before the Waiter acts) / wait
- [ ] Chef choice collected from the manager and passed to B's Chef
- [ ] Cashier: one READY_TO_PAY customer per turn, oldest first, frees the table
- [ ] Start-of-turn display and end-of-game results (N/A average if nobody paid)
- [ ] Invalid input explained and asked again **without using up the task**
- [ ] Victory/defeat check against the three targets

**Done when:** each person's part works against fakes and has been merged to `main` through a reviewed PR.

---

## Week 2 (first half): integrate and tag Stage 1

1. Swap the fakes for the real classes. Fix mismatches together (in one sitting if possible).
2. **Acceptance test:** replay the section 3.3 Pasta example and check that every satisfaction value matches (92, 84, 76, 76, 76, paid on turn 6).
3. Edge-case checks:
   - [ ] invalid choices (no free table, order not READY, typing letters)
   - [ ] a customer abandoning at 0, order cancelled, table freed
   - [ ] the turn-16 customer never pays
   - [ ] average shows N/A before the first payment
4. Find **one victory run and one defeat run** and write down every input, so anyone can reproduce them. Save them in `docs/test-notes.md`.
5. Check that there is no VIP, Critic, Host, combo or Happy Hour code anywhere.
6. Tag it:
   ```
   git tag -a stage-1 -m "Stage 1: basic restaurant operation"
   git push origin stage-1
   ```
7. **Clone the tag fresh into a new folder and run it** to prove it compiles on its own.
8. ➕ **Addition (Takshak, with Robin's help on Customer classes):** commit a Stage 1 class diagram snapshot, `docs/uml/stage-1.png`, drawn from the real Stage 1 code. The rubric's Process criterion marks "stage snapshots", and the report's stage-evolution section shows 1 → 2 → 3 side by side.

---

## Week 2 (second half): Stage 2, plus Stage 3 started on branches

### A: Stage 2 (merges into `main`)
- [ ] VIPCustomer: loses 5, 10% discount
- [ ] CriticCustomer: loses 12, plus your documented special behaviour
- [ ] Arrival list with at least one VIP and one Critic, still six arrivals
- [ ] **No type checks in the controller.** Search the code for `instanceof` before merging.
- [ ] New victory and defeat runs for Stage 2 in the test notes

Then: integrate, test, tag `stage-2`, and fresh-clone check as in Week 2.

➕ **Addition (Takshak):** `docs/uml/stage-2.png` snapshot, updated from the Stage 1 diagram. PR descriptions list new and changed files with reasons, so C can copy them into the README.

### B and C: start Stage 3 on branches (do not merge before the stage-2 tag)
- **B:** ComboMeal (units summed, 90% price rounded to the cent, documented combo-choice rule) and Happy Hour (once per game, two turns, 20% off, VIP discount first, price locked at order time, +15 recovery for waiting customers, remaining duration shown).
- **C:** Host seats at most one queued customer per turn in addition to the Waiter; menu option to switch on Happy Hour; calls B's Happy Hour hook at turn start.

### C: README section "Stage 1 → 2"
New files; changed or deleted files with reasons; compare link (`.../compare/stage-1...stage-2`).

---

## Week 3: Stage 3 tag and diagrams

1. Merge B's and C's Stage 3 branches, integrate, test.
2. Checks:
   - [ ] Salad + Pasta combo = $24.30, 3 units
   - [ ] VIP Burger during Happy Hour = $10.80
   - [ ] Happy Hour can't be switched on twice; it doesn't change old orders
   - [ ] Stage 1 and 2 behaviour still works (rerun the earlier victory/defeat inputs)
     - ➕ **Note:** the Stage 1/2 input files will not replay unchanged on Stage 3 code, because Stage 3 adds the Happy Hour prompt. Prove old behaviour with (a) the Stage 1/2 unit and acceptance tests still passing, and/or (b) copies of the old runs with the extra answers added (e.g. `runs/stage3-replay-stage1-victory.txt`). Record which in `docs/test-notes.md`.
3. Tag `stage-3`, then the fresh-clone check.
4. C writes the README section "Stage 2 → 3".
5. **Diagrams** (draw from the real Stage 3 code, not from memory):
   - ➕ The Stage 3 class diagram doubles as the `stage-3` snapshot.
   - **Takshak (class diagram):** one diagram per stage, with visibility, multiplicities, enums, inheritance, and annotations for encapsulation, polymorphism and composition. Starts from Robin's Stage 2 and 3 drafts in `docs/uml/` and the Stage 1 redraw, and checks every class name against the code at each tag.
   - **Sibghat (sequence diagram):** one full scenario: arrival → seat → order → chef → READY → SERVED → eating → payment → result check, including one VIP or Happy Hour behaviour, with guards and loops. Caleb confirms the turn order; Robin and Ian answer questions about Customer and Order/Pricing.
   - **Together:** Takshak and Sibghat cross-check. Every object and message in the sequence diagram must be a class and method that exists in the class diagram, and the class diagram's relationships must match what the sequence diagram calls. Each reviews the other's diagram before it goes into the report.
   - Use the lecture notation (Chapter 6 class diagram, Chapter 7 sequence diagram): only boundary / control / entity, `<<interface>>` and `<<enumeration>>` stereotypes.

---

## Week 4: report, video, submission

### Report (shared doc, each section has an owner)
| Section | Owner |
|---|---|
| Class diagrams (one per stage) | Takshak, reviewed by Sibghat |
| Sequence diagram | Sibghat, reviewed by Takshak |
| Key abstractions and responsibilities table | everyone fills in their own classes |
| SOLID principles (only where the code really shows them) | B |
| Trade-offs and one alternative design | A |
| How the player's choices change a result | C |
| Functional evidence (screenshots of runs) | C |
| Reflections | each person writes their own |

### Video (about 15 minutes, everyone speaks)
| Segment | Speaker |
|---|---|
| 1 Game launching | C |
| 2 Manager choices with constrained staff | C |
| 3 READY → SERVED → payment | B |
| 4 VIP or Critic behaviour | A |
| 5 Stage 3 feature (combo, Happy Hour, Host) | B, with C on the Host |
| 6 One victory and one defeat | A |
| 7 Tag comparisons and diagrams | C (tags), Takshak (class diagrams), Sibghat (sequence diagram) |

Put the timestamps in the README.

### Final checklist
- [ ] Class diagram matches the Stage 3 code (check every class name)
- [ ] All three tags exist on runnable commits
- [ ] README has the repo URL, both stage comparisons and the video timestamps
- [ ] Build and run instructions tested on someone else's laptop
- [ ] Declaration of Original Work signed; each member's GAI declaration done
- [ ] Zip named like `FCS1_Grp1.zip`, submitted

---

## Weekly rhythm
- A short check-in message every couple of days: what I finished, what I'm stuck on.
- **If you need to change CONTRACT.md, tell the other two before you push.** That file is the only thing keeping your parts independent.

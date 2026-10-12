# Team roles

Who owns what, taken from the [development plan](development-plan.md).
**Each person only edits their own folders.** If you need a change in someone else's folder, ask them in the group chat or open an issue.

| | **A: Customers** | **B: Kitchen, Menu & Pricing** | **C: Game loop, Staff & CLI** |
|---|---|---|---|
| Name | **Robin** | **Ian** | **Caleb** |
| Branch prefix | `a/…` | `b/…` | `c/…` |
| Folders | `customer/` | `menu/`, `order/`, `pricing/`, `staff/Chef` | `app/`, `boundary/`, `control/`, `place/`, `staff/Waiter`, `staff/Cashier`, `staff/Host` |
| Stage 1 | Customer, satisfaction loss and leaving, food choice, discount method (Regular = 0%) | MenuItem, Menu, Order, OrderStatus, Chef, bill in cents, money display | Table, WaitingQueue, turn loop, Waiter, Cashier, Chef prompt, displays, input handling, victory/defeat |
| Stage 2 | **VIPCustomer, CriticCustomer**, mixed arrival list, Stage 2 runs | Stage 3 on a branch | Stage 3 on a branch, README "Stage 1 → 2" |
| Stage 3 | | **ComboMeal, Happy Hour** (all of it: once per game, 2 turns, 20% off, VIP first, price lock, +15 recovery, duration) | **Host**, Happy Hour on/off menu option, calling B's Happy Hour hook, README "Stage 2 → 3" |
| Diagram | **Class diagram** | | **Sequence diagram** |
| Report | Trade-offs and one alternative design | SOLID principles | How the player's choices change a result; functional evidence |
| Video | 4 VIP/Critic · 6 Victory and defeat · 7 Diagrams | 3 READY → SERVED → payment · 5 Combo and Happy Hour | 1 Launching · 2 Manager choices · 5 Host · 7 Tag comparisons |

**Everyone:** writes tests for their own code, reviews the others' PRs, fills in their own classes in the key-abstractions table, writes their own reflection, signs the declarations and speaks in the video.

## Connection points between roles
These are written in detail in [CONTRACT.md](../CONTRACT.md).

| From → to | What is needed |
|---|---|
| C → A | lose satisfaction, check if left, choose food, get state, record satisfaction at payment |
| C → B | create order, chef works on order, is ready, mark served, get bill |
| B → A | get discount for this customer |

## To confirm (not assigned in the plan)
| Gap | Suggestion |
|---|---|
| Who tracks revenue, paid count and recorded satisfactions (a `Ledger`) | **C**, alongside the Cashier, since C does the victory check |
| Happy Hour's +15 recovery is B's, but it changes a Customer's satisfaction (A's folder) | **A** provides `recover(points)` on Customer, capped at 100; **B** decides when and to whom it applies. Add it to CONTRACT.md as **B → A**. |
| Per-stage class diagram snapshots (➕ addition) | **A**, as class-diagram owner |
| CI and tag steps | **C**, as game-loop owner |

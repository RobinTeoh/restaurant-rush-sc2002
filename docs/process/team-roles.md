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
| Diagram | Supports Takshak with the Customer classes | Supports Sibghat with Order/Pricing questions | Confirms the turn order for the sequence diagram |
| Report | Evidence and process (P4), framing and editing (P5), `customer/` key-abstractions rows | SOLID evidence for `menu/`, `order/`, `pricing/` | How the player's choices change a result; functional evidence |
| Video | 3 READY → SERVED → payment · 4 VIP/Critic | 5 Stage 3 feature (Combo, Happy Hour) · 6 Victory and defeat | 1 Launching · 2 Manager choices |

### Diagram owners
| | **Takshak** | **Sibghat** |
|---|---|---|
| Owns | **Class diagrams**, one per stage (`docs/uml/`), including the Stage 1 redraw | **Sequence diagram**, one full scenario with `alt`, `loop` and guards |
| Report | Class design (section 2), trade-offs, alternative design | Dynamic design (section 3), how the player's choices change the result |
| Video | 7 Tag comparisons and class diagrams | 8 Use case and sequence diagram |
| Works with | Sibghat: every object and message in the sequence diagram must be a class and method in the class diagram | Takshak: same cross-check; each reviews the other's diagram before it goes in the report |

**Everyone:** writes tests for their own code, reviews the others' PRs, fills in their own classes in the key-abstractions table, writes their own reflection, signs the declarations and speaks in the video.

## Connection points between roles
These are written in detail in [CONTRACT.md](../design/CONTRACT.md).

| From → to | What is needed |
|---|---|
| C → A | lose satisfaction, check if left, choose food, get state, record satisfaction at payment |
| C → B | create order, chef works on order, is ready, mark served, get bill |
| B → A | get discount for this customer |

## Gaps that were not assigned in the plan, and how they were settled
| Gap | Resolution |
|---|---|
| Who tracks revenue, paid count and recorded satisfactions | **C**, in `GameStats` (`control/`), next to the Cashier, since C does the victory check |
| Happy Hour's +15 recovery is B's, but it changes a Customer's satisfaction (A's folder) | **A** provides `recover(points)` on `Customer`, capped at 100; **B**'s `HappyHour` decides when and to whom it applies. Listed in [CONTRACT.md](../design/CONTRACT.md) as **B → A** |
| Per-stage class diagram snapshots | **Takshak**, as class-diagram owner; sources are in [docs/uml/](../uml/) |
| CI and tag steps | **C**, as game-loop owner (`.github/workflows/ci.yml`) |

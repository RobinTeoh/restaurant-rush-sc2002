# Contract: connection points between roles

The exact method name, parameters and return type for every call that crosses from one person's folders into another's.
**This file is the only thing keeping our parts independent. If you need to change it, tell the other two before you push.**

Roles: A = Robin (Customers) · B = Ian (Kitchen, Menu & Pricing) · C = Caleb (Game loop, Staff & CLI). See [docs/team-roles.md](docs/team-roles.md).

**Method names are agreed (12 Oct 2026).** Types marked *(proposed)* were not stated at the meeting; confirm or change them before coding against them.

## C → A (game loop calls Customer)

Implemented in Stage 1 (`Customer.java`). A call made in the wrong state throws `IllegalStateException`; the Waiter and controller check `getState()` first and show the player a reason (brief §3.4).
**Stage 2:** `Customer` is abstract with three types. The event methods (`seat`, `chooseFood`, `serve`, `finishEating`, `pay`, `loseSatisfaction`) are `final`, so every type follows the same flow; the types differ only through `satisfactionLossPerTurn()`, `getDiscountPercent()` and `pickDish()`.

| Need | Class | Method | Signature | Notes |
|---|---|---|---|---|
| Create a customer | `RegularCustomer`, `VIPCustomer`, `CriticCustomer` | constructor | e.g. `public VIPCustomer(int id)` | **Stage 2:** `Customer` is abstract, so create one of the three types. Satisfaction 100, WAITING. Ids are 1, 2, 3... in arrival order (the food seed uses the id). The game gets its customers from `ArrivalSchedule`; only setup code (`GameConfig`, tests) names the types. |
| Lose satisfaction | `Customer` | `loseSatisfaction()` | `public final void loseSatisfaction()` | Called on **every** customer at turn end. Loses the customer type's amount (Regular 8, VIP 5, Critic 12) only in WAITING, SEATED or ORDERED, never below 0; at 0 the customer becomes LEFT. Does nothing in any other state (no exception). |
| *Stage 2:* Type name | `Customer` | `getTypeName()` | `public String getTypeName()` | "Regular", "VIP" or "Critic", for the display only. Never used for decisions. |
| Check if left | `Customer` | `hasLeft()` | `public boolean hasLeft()` | True once satisfaction hit 0 |
| Choose food | `Customer` | `chooseFood(menu)` | `public final MenuItem chooseFood(Menu menu)` | SEATED → ORDERED, so taking the order is one call. Regular and VIP pick with `new Random(SEED + id)` (D5/D6); the Critic always picks the most expensive dish (D7). |
| Get state | `Customer` | `getState()` | `public CustomerState getState()` | |
| *Added:* Seat | `Customer` | `seat()` | `public void seat()` | WAITING → SEATED. The customer does not store its table (`Table` knows its occupant). |
| *Added:* Serve | `Customer` | `serve()` | `public void serve()` | ORDERED → EATING |
| *Added:* Finish eating | `Customer` | `finishEating()` | `public void finishEating()` | EATING → READY_TO_PAY. The **controller** decides when one full turn has passed. |
| *Changed:* Pay | `Customer` | `pay()` | `public void pay()` | READY_TO_PAY → PAID. **Replaces** the agreed `recordSatisfactionAtPayment()`: one call cannot be done halfway. Satisfaction cannot change after SERVED, so the recorded value is the current one. |
| Read recorded satisfaction | `Customer` | `getRecordedSatisfaction()` | `public int getRecordedSatisfaction()` | Only valid once PAID (throws before). Used for the average. |
| *Added:* Display | `Customer` | `getId()`, `getSatisfaction()` | `public int getId()`, `public int getSatisfaction()` | For the start-of-turn display |

## C → B (game loop calls kitchen and orders)
| Need | Class | Agreed method | Full signature *(proposed types)* | Notes |
|---|---|---|---|---|
| Create order | `Order` (static factory) | `createOrder(customer, item, turn)` | `public static Order createOrder(Customer customer, MenuItem item, int turn)` | Price locked when the order is taken. Caleb calls `customer.chooseFood(menu)` first, then passes the dish in |
| Chef works on order | `Chef` | `chef.work(order)` | `public boolean work(Order order)` | Advances one prep unit. Returns `false` (no change) if the order is READY, SERVED, PAID or CANCELLED, so the game can ask again without using up the task |
| Is ready | `Order` | `isReady()` | `public boolean isReady()` | |
| Mark served | `Order` | `markServed()` | `public void markServed()` | Only allowed when READY |
| Get bill | `Order` | `getBill()` | `public int getBill()` | Integer **cents** (e.g. 1800 = $18.00). **Stage 2:** item price less the customer's discount, via `Money.percentOff` (VIP Burger 1350). Throws for a CANCELLED order (D3) |
| *Stage 2:* Percent off | `Money` | `Money.percentOff(cents, percent)` | `public static int percentOff(int cents, int percent)` | D1 half-up rounding; the only place a percentage is applied |
| *Stage 2:* Who arrives | `ArrivalSchedule` | `arrivalsOn(turn)` | `public List<Customer> arrivalsOn(int turn)` | **Changed** from a count to the customers themselves. New constructor `ArrivalSchedule(List<Integer> turns, List<Customer> customers)`; the turns-only constructor means all Regular. |
| *Proposed by B:* Mark paid | `Order` | `markPaid()` | `public void markPaid()` | Cashier, after `customer.pay()`. Only allowed when SERVED |
| *Proposed by B:* Cancel order | `Order` | `cancel()` | `public void cancel()` | Controller, when the customer has left (D3). Only allowed when PLACED or READY |
| *Proposed by B:* Whose order | `Order` | `getCustomer()` | `public Customer getCustomer()` | Also `getItem()`, `getStatus()`, `getUnitsDone()`, `getTurnPlaced()` for the display |
| *Proposed by B:* Show money | `Money` | `Money.format(cents)` | `public static String format(int cents)` | 1800 → `"$18.00"`. The only place cents become dollar text |
| *Proposed by B:* The menu | `Menu` | `Menu.standard()` | `public static Menu standard()` | Salad $9.00 (1 unit), Burger $15.00 (1), Pasta $18.00 (2), in that order (Robin's seed relies on it) |
| *Stage 3, proposed by B:* Create order with Happy Hour | `Order` | `createOrder(customer, item, turn, happyHour)` | `public static Order createOrder(Customer customer, MenuItem item, int turn, HappyHour happyHour)` | Price is saved when the order is taken: customer discount, then 20% if Happy Hour is on. `happyHour` can be `null`. The 3-argument one still works |
| *Stage 3, proposed by B:* Combo menu | `Menu` | `Menu.withCombos()` | `public static Menu withCombos()` | Use for `stage3` instead of `Menu.standard()` (D8). In `Main`: `GameConfig.STAGE_3.equals(scenario) ? Menu.withCombos() : Menu.standard()` |
| *Stage 3, proposed by B:* Happy Hour | `HappyHour` | `canActivate()`, `activate()`, `isActive()`, `getTurnsLeft()`, `endTurn()` | | One per game. Only show the option while `canActivate()`. `getTurnsLeft()` is 2, 1, then 0. Call `endTurn()` at the end of every turn |
| *Stage 3, proposed by B:* Happy Hour recovery | `HappyHour` | `recoverWaiting(customers)` | `public void recoverWaiting(List<Customer> customers)` | Start of each Happy Hour turn (D9). Calls `Customer.recover(15)` on each customer; only waiting ones gain |

## B → A (pricing calls Customer)
| Need | Class | Agreed method | Full signature *(proposed types)* | Notes |
|---|---|---|---|---|
| Get discount for this customer | `Customer` | `getDiscountPercent()` | `public int getDiscountPercent()` | **Stage 2.** 0 by default, `VIPCustomer` returns 10. Read by `Order.getBill()`. Rounding per D1. |
| Happy Hour recovery | `Customer` | `recover(int points)` | `public final void recover(int points)` | **Stage 3, agreed.** Adds `points` (capped at 100), only while WAITING, SEATED or ORDERED; ignored for EATING, READY_TO_PAY, PAID and LEFT (a customer who left never returns). Negative points throw. B calls it with 15 (D9). |

## Gaps (status)
| Gap | Status |
|---|---|
| Nothing reads the recorded satisfaction back | **Settled:** `Customer.getRecordedSatisfaction()` (see C → A) |
| B → A discount method not named | **Settled in Stage 2:** `getDiscountPercent()` |
| ~~Owner class of `createOrder`~~ | **Settled:** `Order` (B's folder) |

## Shared types
| Type | Owner | Values / members |
|---|---|---|
| `CustomerState` enum | A | WAITING, SEATED, ORDERED, EATING, READY_TO_PAY, PAID, LEFT. `losesSatisfaction()` is true for WAITING, SEATED and ORDERED. |
| `OrderStatus` enum | B | PLACED, READY, SERVED, PAID, CANCELLED. Progress while PLACED is `getUnitsDone()` out of `getItem().getPrepUnits()` |
| `MenuItem`, `Menu` | B | `MenuItem`: `getName()`, `getPriceCents()`, `getPrepUnits()`, `toString()` e.g. `"Pasta $18.00 (2 units)"`. `Menu`: `getItems()` (read-only list, menu order) |
| `ComboMeal` *(Stage 3)* | B | `extends MenuItem`. `new ComboMeal(first, second)`, e.g. "Salad + Pasta", 2430, 3 units. `getItems()` gives the two dishes |

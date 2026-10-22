# Contract: connection points between roles

The exact method name, parameters and return type for every call that crosses from one person's folders into another's.
**This file is the only thing keeping our parts independent. If you need to change it, tell the other two before you push.**

Roles: A = Robin (Customers) · B = Ian (Kitchen, Menu & Pricing) · C = Caleb (Game loop, Staff & CLI). See [docs/team-roles.md](docs/team-roles.md).

**Method names are agreed (12 Oct 2026).** Types marked *(proposed)* were not stated at the meeting; confirm or change them before coding against them.

## C → A (game loop calls Customer)

Implemented in Stage 1 (`Customer.java`). A call made in the wrong state throws `IllegalStateException`; the Waiter and controller check `getState()` first and show the player a reason (brief §3.4).

| Need | Class | Method | Signature | Notes |
|---|---|---|---|---|
| Create a customer | `Customer` | constructor | `public Customer(int id)` | Satisfaction 100, WAITING. Ids are 1, 2, 3... in arrival order (the food seed uses the id). |
| Lose satisfaction | `Customer` | `loseSatisfaction()` | `public void loseSatisfaction()` | Called on **every** customer at turn end. Loses 8 only in WAITING, SEATED or ORDERED, never below 0; at 0 the customer becomes LEFT. Does nothing in any other state (no exception). |
| Check if left | `Customer` | `hasLeft()` | `public boolean hasLeft()` | True once satisfaction hit 0 |
| Choose food | `Customer` | `chooseFood(menu)` | `public MenuItem chooseFood(Menu menu)` | SEATED → ORDERED, so taking the order is one call. Picks with `new Random(SEED + id)` (DECISIONS D5/D6). |
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
| Get bill | `Order` | `getBill()` | `public int getBill()` | Integer **cents** (e.g. 1800 = $18.00). Stage 1: the item price (no discounts). Throws for a CANCELLED order (D3) |
| *Proposed by B:* Mark paid | `Order` | `markPaid()` | `public void markPaid()` | Cashier, after `customer.pay()`. Only allowed when SERVED |
| *Proposed by B:* Cancel order | `Order` | `cancel()` | `public void cancel()` | Controller, when the customer has left (D3). Only allowed when PLACED or READY |
| *Proposed by B:* Whose order | `Order` | `getCustomer()` | `public Customer getCustomer()` | Also `getItem()`, `getStatus()`, `getUnitsDone()`, `getTurnPlaced()` for the display |
| *Proposed by B:* Show money | `Money` | `Money.format(cents)` | `public static String format(int cents)` | 1800 → `"$18.00"`. The only place cents become dollar text |
| *Proposed by B:* The menu | `Menu` | `Menu.standard()` | `public static Menu standard()` | Salad $9.00 (1 unit), Burger $15.00 (1), Pasta $18.00 (2), in that order (Robin's seed relies on it) |

## B → A (pricing calls Customer)
| Need | Class | Agreed method | Full signature *(proposed types)* | Notes |
|---|---|---|---|---|
| Get discount for this customer | `Customer` | **Stage 2** | e.g. `public int getDiscountPercent()` | **Not in Stage 1:** the bill is the item price. Added in Stage 2 with `VIPCustomer` (VIP 10). Rounding per D1. |
| *To confirm:* Happy Hour recovery | `Customer` | **Stage 3** | e.g. `public void recover(int points)` | +15, capped at 100 (Stage 3) |

## Gaps (status)
| Gap | Status |
|---|---|
| Nothing reads the recorded satisfaction back | **Settled:** `Customer.getRecordedSatisfaction()` (see C → A) |
| B → A discount method not named | **Moved to Stage 2** (see above) |
| ~~Owner class of `createOrder`~~ | **Settled:** `Order` (B's folder) |

## Shared types
| Type | Owner | Values / members |
|---|---|---|
| `CustomerState` enum | A | WAITING, SEATED, ORDERED, EATING, READY_TO_PAY, PAID, LEFT. `losesSatisfaction()` is true for WAITING, SEATED and ORDERED. |
| `OrderStatus` enum | B | PLACED, READY, SERVED, PAID, CANCELLED. Progress while PLACED is `getUnitsDone()` out of `getItem().getPrepUnits()` |
| `MenuItem`, `Menu` | B | `MenuItem`: `getName()`, `getPriceCents()`, `getPrepUnits()`, `toString()` e.g. `"Pasta $18.00 (2 units)"`. `Menu`: `getItems()` (read-only list, menu order) |

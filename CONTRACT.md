# Contract: connection points between roles

The exact method name, parameters and return type for every call that crosses from one person's folders into another's.
**This file is the only thing keeping our parts independent. If you need to change it, tell the other two before you push.**

Roles: A = Robin (Customers) · B = Ian (Kitchen, Menu & Pricing) · C = Caleb (Game loop, Staff & CLI). See [docs/team-roles.md](docs/team-roles.md).

**Method names are agreed (12 Oct 2026).** Types marked *(proposed)* were not stated at the meeting; confirm or change them before coding against them.

## C → A (game loop calls Customer)
| Need | Class | Agreed method | Full signature *(proposed types)* | Notes |
|---|---|---|---|---|
| Lose satisfaction | `Customer` | `loseSatisfaction()` | `public void loseSatisfaction()` | Called at turn end. The customer applies its own loss (Regular 8, later VIP 5, Critic 12), minimum 0, and only while waiting or seated-not-served. At 0 the customer becomes LEFT. |
| Check if left | `Customer` | `hasLeft()` | `public boolean hasLeft()` | True once satisfaction hit 0 |
| Choose food | `Customer` | `chooseFood(menu)` | `public MenuItem chooseFood(Menu menu)` | Uses DECISIONS D5 (seeded random, max 2 Pasta) |
| Get state | `Customer` | `getState()` | `public CustomerState getState()` | |
| Record satisfaction at payment | `Customer` | `recordSatisfactionAtPayment()` | `public void recordSatisfactionAtPayment()` | Stores the current satisfaction for the average |

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
| Get discount for this customer | `Customer` | ***not agreed yet*** | e.g. `public int getDiscountPercent()` | Regular 0, VIP 10 (Stage 2). Rounding per D1 |
| *To confirm:* Happy Hour recovery | `Customer` | ***not agreed yet*** | e.g. `public void recover(int points)` | +15, capped at 100 (Stage 3) |

## Gaps to settle (found while filling this in)
| Gap | Why it matters | Suggestion |
|---|---|---|
| Nothing reads the recorded satisfaction back | C needs it to compute the average satisfaction of paid customers | Add `public int getRecordedSatisfaction()` on `Customer` (C → A) |
| B → A discount method not named | B can't compute the VIP bill without it | Agree a name before Stage 1 ends (Regular returns 0) |
| ~~Owner class of `createOrder`~~ | Decides which folder it lives in | **Settled:** `Order` (B's folder) |

## Shared types
| Type | Owner | Values / members |
|---|---|---|
| `CustomerState` enum | A | *to fill, e.g.* WAITING, SEATED, ORDERED, EATING, READY_TO_PAY, PAID, LEFT |
| `OrderStatus` enum | B | PLACED, READY, SERVED, PAID, CANCELLED. Progress while PLACED is `getUnitsDone()` out of `getItem().getPrepUnits()` |
| `MenuItem`, `Menu` | B | `MenuItem`: `getName()`, `getPriceCents()`, `getPrepUnits()`, `toString()` e.g. `"Pasta $18.00 (2 units)"`. `Menu`: `getItems()` (read-only list, menu order) |

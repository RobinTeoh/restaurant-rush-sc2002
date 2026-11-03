# Team decision summary (Stages 1 to 3)

One page for the report and the video: every rule the brief leaves open, what the team chose, and what that changed in the game or the code.
Full wording and alternatives: [DECISIONS.md](../DECISIONS.md) (rules) and [design-log.md](design-log.md) (design choices).

## Stage 1: basic operation

| # | Decision | Effect / outcome |
|---|---|---|
| D1 | Money is integer cents; every discount rounds half-up, after each discount. | Both brief examples come out exactly ($24.30 combo, $10.80 VIP Burger in Happy Hour). No floating-point drift in the $45 check. |
| D2, D11 | Cashier pays the customer who became READY_TO_PAY earliest (first-in-first-out line). The Cashier acts before customers finish eating that turn. | A customer pays two turns after being served at the earliest, never on the turn they became ready (brief 3.2 step 4). |
| D3 | A customer who leaves while the order is READY: order is CANCELLED, table freed, no revenue. | Abandoned orders can never add revenue (brief 3.4). Tested. |
| D5, D6 | Food is a seeded random pick: customer *n* uses `new Random(32 + n)` over the menu. Seed 32 gives Salad, Burger, Pasta, Burger, Pasta, Salad. | Every run is reproducible. At most 2 Pasta, and any 4 of the first 5 customers earn at least $57 (target $45). No shared or static state, so tests cannot affect each other. |
| D10 | The manager picks which queued customer the Waiter seats. | A real choice: seating order changes who waits, leaves, or pays. |
| D12 | When input ends, remaining choices are Wait; the game plays on. | Piped run files and a closed console never crash. |
| D13 | Stage 1 `Customer` is one ordinary class with `LOSS_PER_TURN = 8`; no subclasses, no discount method. | Stage 1 is designed from Stage 1 only. Stage 2 has a real refactor to explain in the README. |
| log | `CustomerState` is an enum, not one class per state (State pattern rejected). | Fewer classes for 7 states; compiler-checked. This is the report's **alternative design**. |
| log | Customer flags `hasLeft()`; the controller cancels the order and frees the table. | The customer never reaches into `Table` or the kitchen. Dependencies point one way. |

**Outcome:** 4 paid / $57.00 / average 80 on the best simple play. The only losing strategy among 30 is "the Chef never cooks", so the game rewards kitchen priority. Waiter priority decides whether a fifth customer pays.

## Stage 2: customer behaviour

| # | Decision | Effect / outcome |
|---|---|---|
| D6b, D15 | The arrival list holds ready-made customers (Regular, VIP, Regular, Critic, Regular, Regular). The controller adds whoever arrives. | No customer class is named in the controller and there are no `instanceof` checks (brief 4.2). A new type is one class plus one list entry. |
| log | `Customer` becomes abstract. Event methods (`seat`, `serve`, `pay`, ...) are `final` and call hooks `satisfactionLossPerTurn()` and `pickDish()`. | Every type follows the same flow (Liskov), proven by `CustomerSubstitutionTest`. Types only supply numbers and a food choice. |
| D7 | Critic's special behaviour: always orders the most expensive dish. | The base run gets 3 Pasta instead of 2 (D5 note) and stays winnable. All of it lives in `CriticCustomer.pickDish()`. An ignored Critic leaves after 9 turns (Regular 13, VIP 20). |
| D14 | VIP discount is `Money.percentOff(price, customer.getDiscountPercent())`. | VIP Burger $13.50. `Order` asks the customer for its discount instead of checking its type. `Money.percentOff` is the only place a percentage is applied. |

**Outcome:** best play is 5 paid / $76.50. Average satisfaction ranges 70.5 to 75.5 depending on Waiter priority. Stage 1 behaviour is unchanged: all 74 Stage 1 tests pass (only `new Customer` became `new RegularCustomer`).

## Stage 3: new role and menu policy

| # | Decision | Effect / outcome |
|---|---|---|
| D4 | Host is automatic and acts before the Waiter: seats the first queued customer if a table is free. The Waiter can still seat afterwards. | At most 1 Host seating per turn, in addition to the Waiter's task (brief 4.3). The Waiter is freed for orders and serving. The manager does not choose the Host's customer, which is a deliberate trade-off. |
| D8 | Combo rule: the Stage 3 menu is the 3 dishes then 3 combos (Salad + Burger, Salad + Pasta, Burger + Pasta). Customers choose as before (seeded pick; the Critic takes the most expensive). With seed 32: Salad + Burger, Salad + Pasta, Pasta, Burger + Pasta (Critic), Burger + Pasta, Salad. | **Kept as Ian wrote it** (the "every 2nd customer" alternative was rejected). No change to any customer class, which is the Open/Closed evidence. Four of six customers order a combo, so combos are visible in the video. Best play: 4 paid, $91.17, average 74.25. |
| log | `ComboMeal extends MenuItem`; units are the sum, price is 90% of the pair, half-up. | `Order`, `Chef` and `Waiter` did not change. An `Orderable` interface was rejected (more change, no extra benefit). |
| log | `Order` stores its price when the order is taken: VIP discount first, then Happy Hour. | Happy Hour ending later cannot change a placed bill. VIP Burger in Happy Hour is $10.80. Old `createOrder` still works for Stages 1 and 2. |
| D9 (closed) | Happy Hour timing: arrivals, then the start-of-turn display, then the Happy Hour prompt (once per game), then +15 recovery, then Host, Waiter, Chef, payment and satisfaction. `endTurn()` runs at turn end. | Lasts the starting turn and the next. The screen shows "turns left" after it is switched on. Only WAITING, SEATED and ORDERED customers recover (capped at 100). |
| log | `HappyHour` is its own class; `Customer.recover(points)` keeps the state rule in the customer. | The controller only switches it on and calls `endTurn()`. Pricing and recovery rules live in one place. |

**Outcome:** the Stage 3 base run is still winnable (151 tests, including `Stage3BaseSettingTest`). Happy Hour is the manager's new timing choice: switching it on when many customers wait both saves them from leaving and discounts orders.

## Open items for the team
* Confirm the D9 order with Caleb: the start-of-turn display is drawn before the Happy Hour prompt, so the +15 appears in the messages, not in that first display.
* Record Stage 3 evidence in `docs/test-notes.md` and the Stage 2 to 3 README section (still empty).

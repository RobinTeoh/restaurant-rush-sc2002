# Design decisions

Rules the brief leaves open, the choice we made and why.
**Tell the other two in chat before changing an Agreed decision.**

## Agreed at the first meeting (12 Oct 2026)

| # | Question | Decision | Why |
|---|---|---|---|
| D1 | Rounding | Round **after each discount**, half-up to the nearest cent: `(cents * (100 − pct) + 50) / 100`. Example: VIP Burger in Happy Hour = 1500 → 1350 → 1080 = **$10.80**. Combo Salad + Pasta = 2700 → **$24.30**. | Matches both examples in the brief; integer cents only |
| D2 | Cashier "oldest first" | The customer who became **READY_TO_PAY earliest** pays first. *Tie (both became READY_TO_PAY on the same turn): earlier arrival pays first. (Tie-break proposed, to confirm.)* | "Oldest" in the payment queue, not oldest in the restaurant |
| D3 | Customer leaves while their order is READY | The order is **discarded**: marked CANCELLED, never served, never paid, no revenue. The table is freed. | Nobody is there to serve; the brief says revenue never includes abandoned orders |
| D4 | Host (Stage 3) | **Automatic**, acting **before the Waiter** in step 2: if a table is free and the queue is not empty, the Host seats the **first customer in the queue**. The Waiter may still seat a customer afterwards. | Frees the Waiter for orders and serving; keeps the turn short |
| D5 | Food choice | **Seeded random** pick, using a fixed seed so every run is the same. The choice is shown when the order is taken. *Changed after the meeting (Robin, PR #10; team to confirm):* customer *n* picks with `new Random(SEED + n)` from the menu in menu order (Salad, Burger, Pasta), so there is **no Pasta counter in the code**. The "at most 2 Pasta" aim is met by the choice of seed (D6), and `FoodChoiceTest` fails if the seed or the menu order ever breaks it. | Reproducible, but not a fixed pattern. Each customer's choice depends only on itself, so there is no shared or static state and tests cannot affect each other. A runtime "re-roll until winnable" check was rejected: the seed is a constant, so it would give the same answer on every run. |

## Implemented in Stage 1 (decided while coding; team to confirm)

| # | Question | Decision | Where |
|---|---|---|---|
| D6 | Base-run seed and arrival list | **Seed 32.** Customers 1 to 6 order Salad, Burger, Pasta, Burger, Pasta, Salad ($9, $15, $18, $15, $18, $9): 2 Pasta, and any 4 of the first 5 customers pay at least $57 (target $45). Arrivals on turns 1, 4, 7, 10, 13, 16. Customer ids are 1, 2, 3... in arrival order (the seed depends on it). | `Customer.SEED`, `GameConfig.ARRIVAL_TURNS`, `FoodChoiceTest` |
| D10 | Which queued customer the Waiter seats | The **manager picks any queued customer** ("Seat which customer?"). | `GameController.seatAction` |
| D11 | Order of actions inside step 4 | The **Cashier pays first**, then customers served on an earlier turn finish eating and join the back of the payment line. So a customer pays at the earliest two turns after being served. The Cashier's first-in-first-out line gives D2's "oldest first". | `GameController.paymentAndEating`, `Cashier` |
| D12 | End of input | Print "Input ended. Every remaining choice is Wait." and play on; no crash. | `ConsoleUI.choose` |
| D13 | Customer class hierarchy in Stage 1 | **One ordinary `Customer` class** with a constant `LOSS_PER_TURN = 8`; no subclasses and no discount method in Stage 1 (designed from the Stage 1 requirements only). Stage 2 turns `Customer` into the parent of `VIPCustomer` and `CriticCustomer`; that refactor is explained in the README's Stage 1 → 2 section. | `Customer`, `docs/design-log.md` |

## Implemented in Stage 2 (Robin; team to confirm)

| # | Question | Decision | Where |
|---|---|---|---|
| D6b | Stage 2 arrival list | Still six arrivals on turns 1, 4, 7, 10, 13, 16: **Regular, VIP, Regular, Critic, Regular, Regular** (VIP 2nd, Critic 4th). The base setting always uses this list. The turns-only `ArrivalSchedule` still means "all Regular", which keeps the Stage 1 tests running as proof that earlier behaviour still works. | `GameConfig.baseCustomers()`, `ArrivalSchedule`, `BaseSettingTest` |
| D7 | Critic's special behaviour | **Order preference: the Critic always orders the most expensive dish** (Pasta, $18.00). Its other numbers come from the brief: loses 12 per waiting turn, pays full price. Only `CriticCustomer.pickDish()` changes; the ordering rules stay in `Customer`. | `CriticCustomer`, `CustomerTypesTest` |
| D5 note | Pasta count in Stage 2 | Customer 4 would get a Burger from the seed, but as the Critic it orders Pasta, so the Stage 2 base run has **3 Pasta** (customers 3, 4, 5). Accepted on purpose: it makes the Critic's preference visible, and the run stays winnable (only strategies where the Chef never cooks lose). The "at most 2 Pasta" aim of D5 applies to the Stage 1 all-Regular setting. | `BaseSettingTest` |
| D14 | VIP discount and rounding | `Order.getBill()` = `Money.percentOff(price, customer.getDiscountPercent())`, D1 half-up rounding. VIP Burger $13.50, VIP Salad $8.10. `Money.percentOff` is the only place a percentage is applied, so Stage 3's Happy Hour reuses it. | `Money`, `Order`, `MoneyTest`, `OrderTest` |
| D15 | Which customer type arrives | Decided **outside** the controller: the arrival list holds ready-made customers of each type, and `GameController` only adds whoever `ArrivalSchedule` says arrives this turn. The controller never names a customer class and has no type checks (brief §4.2). | `ArrivalSchedule`, `GameController.arrivals`, `Main` |

## Stage 3 (Ian)

| # | Question | Decision | Where |
|---|---|---|---|
| D8 | Combo-choice rule | In stage 3 the menu has the 3 dishes then the 3 combos (Salad + Burger, Salad + Pasta, Burger + Pasta). Customers choose the same way as before (seeded pick, Critic takes the most expensive). With seed 32: Salad + Burger, Salad + Pasta, Pasta, Burger + Pasta (Critic), Burger + Pasta, Salad. Still winnable: a simple play (serve, then take orders, then seat; Chef cooks oldest order) gives 4 paid, $91.17, average 74.25. | `Menu.withCombos()`, `MenuTest` |

## Still open (decide before the stage that needs them)

| # | Question | Needed by | Options / notes |
|---|---|---|---|
| D9 | Happy Hour timing | Stage 3 | Brief: switched on at turn start, lasts that turn and the next. Decide the prompt order: Happy Hour prompt → +15 recovery → display → steps 1–6? |

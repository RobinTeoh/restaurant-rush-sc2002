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
| D5 | Food choice | **Seeded random** pick from Burger, Salad and Pasta, using a fixed seed so every run is the same. **At most 2 Pasta orders per game**: if a pick would be a 3rd Pasta, pick again. The choice is shown when the order is taken. | Reproducible, but not a fixed pattern; capping Pasta stops the kitchen from being flooded with 2-unit dishes |

> ⚠ **Check for D5 (before Stage 1 tag):** a seed that gives mostly Salad can make the $45 target impossible (4 × $9 = $36). Choose the seed (D6) and confirm that 4 of the first 5 customers can reach ≥ $45.

## Still open (decide before the stage that needs them)

| # | Question | Needed by | Options / notes |
|---|---|---|---|
| D6 | Base-run seed and arrival list | Stage 1 | The seed value for D5; Stage 2 arrival list with ≥1 VIP and ≥1 Critic, still 6 arrivals |
| D7 | Critic's special behaviour | Stage 2 | Must be a distinct order preference or reaction to service, implemented inside `CriticCustomer` only (no type checks in the controller). E.g. always orders the dish with the most prep units; or loses extra satisfaction if served a dish that waited READY |
| D8 | Combo-choice rule | Stage 3 | Which customers order a combo, and which combo. Must be documented and deterministic (or seeded) |
| D9 | Happy Hour timing | Stage 3 | Brief: switched on at turn start, lasts that turn and the next. Decide the prompt order: Happy Hour prompt → +15 recovery → display → steps 1–6? |
| D10 | Which queued customer the **Waiter** seats | Stage 1 | Manager picks any queued customer, or first in queue only |
| D11 | Order of actions inside step 4 | Stage 1 | Suggest: Cashier pays first (only customers already READY_TO_PAY before this turn), then eaters become READY_TO_PAY. Guarantees "pays next turn at the earliest" |
| D12 | End of input (piped run files) | Stage 1 | Suggest: print "Input ended" and finish safely, no crash |
| D13 | Customer class hierarchy in Stage 1 | Stage 1 | Suggest: abstract `Customer` + `RegularCustomer` from Stage 1, so Stage 2 only adds files (a deliberate extension point) |

# Design decisions

Owner: A. Rules the brief leaves open, plus the choice we made and why.
Status is **Proposed** until the team confirms it at the Week 1 kickoff.
**Tell the other two in chat before changing an agreed decision.**

| # | Question | Decision | Why | Status |
|---|---|---|---|---|
| D1 | Food-choice rule | Customer *n* (arrival order) orders `[Salad, Burger, Pasta][(n−1) mod 3]` | Deterministic and easy to reproduce. Any 4 of the first 5 customers total ≥ $48, so $45 is reachable. | Proposed |
| D2 | Critic special behaviour | The Critic always orders the dish with the most prep units (Pasta; in Stage 3 the most expensive combo) | One overridden method. With the 12-point loss it forces a real prioritisation decision. | Proposed |
| D3 | Rounding | Apply each percentage in turn, rounding half-up to the cent: `(cents * (100 − pct) + 50) / 100` | Gives $10.80 and $24.30 exactly; integer-only arithmetic | Proposed |
| D4 | Cashier "oldest first" | Earliest turn of becoming READY_TO_PAY; tie → earliest arrival | Matches "oldest" in the payment queue | Proposed |
| D5 | Order inside step 4 | Cashier pays first (only customers who were READY_TO_PAY before this step), then eaters become READY_TO_PAY | Guarantees "pays next turn at the earliest" | Proposed |
| D6 | Customer leaves with a READY order | Cancel every order not yet SERVED (including READY) | The brief says to cancel unfinished orders, and nobody is there to serve | Proposed |
| D7 | Waiter/Chef sequence | Ask and execute the Waiter first, then ask and execute the Chef | Satisfies all three §3.2-3 rules with no special cases | Proposed |
| D8 | Which queued customer to seat | The manager picks any queued customer | Gives the player a real choice | Proposed |
| D9 | Turn start (Stage 3) | Happy Hour prompt (if unused) → +15 recovery if active → start-of-turn display → steps 1–6 | New arrivals join at 100 in step 1, after recovery | Proposed |
| D10 | Happy Hour duration | Active on activation turn T and T+1; display "2 turns left" then "1 turn left" | Matches "that turn and the next" | Proposed |
| D11 | Host | The manager chooses which queued customer the Host seats (or Wait); the Host acts before the Waiter in step 2 | Keeps it a decision; the Waiter can then take that customer's order | Proposed |
| D12 | Combo choice (Stage 3) | Combos are only on the Stage 3 menu. Customers with an even arrival number order a combo (rotating through the three); odd numbers use D1. | Documented and deterministic; Stage 1/2 scenarios are unaffected | Proposed |
| D13 | End of input | Print "Input ended" and finish the game safely | Piped run files must never crash | Proposed |
| D14 | Results display | §3.5 summary after every turn and at the end | The brief calls it "end-of-turn results" | Proposed |
| D15 | Customer hierarchy in Stage 1 | Abstract `Customer` + `RegularCustomer` already in Stage 1 | Stage 2 then only adds files; a deliberate extension point, not a Stage 2 feature | Proposed |

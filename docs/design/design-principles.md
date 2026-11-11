# Design principles in our code

Reference for the team and the report (brief section 5: "SRP, OCP, LSP, ISP and DIP, only where genuinely reflected in your code").
Everything below was checked against the code at tag `stage-3` (identical on `main`). If you change a class named here, update this file.
Method used in the lecture (Chapter 9): start simple, add one new requirement, see which classes had to change, explain why.

## Summary

| Principle | Genuine? | Strongest example |
|---|---|---|
| **S**ingle responsibility | Yes | `HappyHour`, `Money.percentOff`, `CustomerState.losesSatisfaction`, `Host` |
| **O**pen / closed | Yes, with a limit | VIP and Critic (Stage 2); `ComboMeal` (Stage 3) |
| **L**iskov substitution | Yes | `Customer` and its three subtypes |
| **I**nterface segregation | **Not applied (stated honestly)** | `GameView` is small, but nothing here is a fat interface that we split |
| **D**ependency inversion | Yes | `GameController` depends on `GameView` and the abstract `Customer` |

## S: Single responsibility (one reason to change)

| Class / method | The one reason it changes | Stage |
|---|---|---|
| `Money.percentOff(cents, percent)` | How a percentage is rounded (D1). It is the only place a percentage is applied: `Order.getBill`, `HappyHour.applyTo` and `ComboMeal` all call it. | 2, reused in 3 |
| `CustomerState.losesSatisfaction()` | Which states count as "still waiting". Used by `Customer.loseSatisfaction` and `Customer.recover`. | 1, reused in 3 |
| `HappyHour` | The Happy Hour rules (once per game, 2 turns, 20%, +15). The controller only switches it on and calls `endTurn()`. | 3 |
| `Host` | Seating the first queued customer. Separate from `Waiter` because the brief makes it a separate role. | 3 |
| `ArrivalSchedule` + `GameConfig.baseCustomers()` | Who arrives when and as which type. Moved out of `GameController`. | 2 |
| `GameStats`, `ConsoleUI` | Totals and the victory rule; console input and output only. | 1 |

Limit: `GameController` is still large (the turn loop, the menus, the labels and the snapshot are all in one class). We did not split it. It is the weakest SRP spot, so mention it as a known trade-off, not a strength.

## O: Open / closed (extend, do not edit stable code)

* **Stage 2:** VIP and Critic are new subclasses. `GameController` did not change except `arrivals()` reading customers from the schedule. A new type is one class plus one list entry in `GameConfig`.
* **Stage 3, combos:** `ComboMeal extends MenuItem`, so `Chef` and `MenuItem` did not change at all (`git diff stage-2 HEAD` shows no change in either file) and `Order` needed no combo-specific code.
* **Limit (be honest):** Stage 3 did edit `Order` and `Waiter`. That was for Happy Hour (the price is locked when the order is taken, so `createOrder` gained a `HappyHour` parameter), not for combos. The old `createOrder` still works, so Stage 1 and 2 code did not break. The README Stage 2 to 3 table explains each change.
* **No `instanceof` or type checks anywhere in `src/`** (the lecture's counter-example is an `instanceof` chain in `AreaCalculator`).

## L: Liskov substitution (a subtype can replace its parent)

* `Customer`'s event methods (`seat`, `chooseFood`, `serve`, `finishEating`, `pay`, `loseSatisfaction`, `recover`) are `final`. Subtypes only override the hooks `satisfactionLossPerTurn()`, `getDiscountPercent()` and `pickDish()`. So no subtype can break the visit flow.
* Evidence: `CustomerSubstitutionTest` runs the same full visit, the same refused illegal steps and the same leaving at 0 on Regular, VIP and Critic through a `Customer` reference.
* Pattern name for the report: Template Method (the final methods are the template, the hooks are the steps).

## I: Interface segregation (no client forced to use methods it does not need)

* The only interface is `GameView` with four methods (`showTurnStart`, `showMessage`, `showResults`, `choose`). `ConsoleUI` and the test `ScriptedView` both use all four, and so does `GameController`.
* So ISP is **not violated, but we did not apply it to fix anything**. We did not add a `Seater` interface for `Host` and `Waiter` (different signatures, nothing treats them alike) and we did not split `GameView`. The brief says "do not create interfaces just to satisfy a checklist"; see the design-log row of 2026-11-03.
* In the report: say this plainly instead of inventing an example.

## D: Dependency inversion (depend on abstractions)

* `GameController` depends on the `GameView` interface, not on `ConsoleUI`. Tests swap in `ScriptedView`, which is how `GameControllerTest` and `Stage3BaseSettingTest` play whole games.
* `GameController` depends only on the abstract `Customer`. It imports no customer subclass (check its import list); the concrete types are chosen in setup (`GameConfig.baseCustomers()`, then `Main`).
* Limit: `GameController` creates `Waiter`, `Chef`, `Cashier`, `Host` and `HappyHour` itself (composition), so it depends on those concrete classes. That is deliberate: they are its own parts, not things we need to swap.

## Other design points worth naming

| Point | Where |
|---|---|
| Encapsulation | Private fields, no setters; `Customer` and `Order` change state only through methods that check the current state first (`requireState` / `requireStatus`). `MenuItem` and `Menu` are immutable. |
| Composition | `GameController` owns its staff and statistics; `Menu` owns its `MenuItem`s; `ComboMeal` holds two `MenuItem`s (aggregation). |
| Polymorphism | `pickDish()` (Critic orders the most expensive dish), `getDiscountPercent()` (VIP), `satisfactionLossPerTurn()`; Java picks the override at run time. |
| Alternative design (for the report) | State pattern (one class per customer state) instead of the `CustomerState` enum; rejected, see [design-log.md](design-log.md) 2026-10-14. |

## Where the rest of this lives

| What | File |
|---|---|
| Every decision and why | [DECISIONS.md](DECISIONS.md), [design-log.md](design-log.md), [decision-summary.md](decision-summary.md) |
| What changed between stages | [README.md](../../README.md) (Stage 1 to 2, Stage 2 to 3) |
| Tests that prove behaviour | [test-notes.md](../testing/test-notes.md) |
| Class diagrams | [docs/uml/](../uml/) |

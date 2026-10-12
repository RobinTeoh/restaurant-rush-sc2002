# Contract: connection points between roles

The exact method name, parameters and return type for every call that crosses from one person's folders into another's.
**This file is the only thing keeping our parts independent. If you need to change it, tell the other two before you push.**

Roles: A = Robin (Customers) · B = Ian (Kitchen, Menu & Pricing) · C = Caleb (Game loop, Staff & CLI). See [docs/team-roles.md](docs/team-roles.md).

Fill in the **Signature** column at the kickoff. Class names are suggestions; rename freely.

## C → A (game loop calls Customer)
| Need | Class | Signature | Notes |
|---|---|---|---|
| Lose satisfaction | `Customer` | | Regular −8 per turn, minimum 0; only while waiting or seated-not-served |
| Check if left | `Customer` | | True once satisfaction hits 0 |
| Choose food | `Customer` | | Uses DECISIONS D5 |
| Get state | `Customer` | | Returns the customer state enum |
| Record satisfaction at payment | `Customer` | | Value used for the average |

## C → B (game loop calls kitchen and orders)
| Need | Class | Signature | Notes |
|---|---|---|---|
| Create order | `Order` | | Price locked when the order is taken |
| Chef works on order | `Chef` | | Advances one preparation unit |
| Is ready | `Order` | | |
| Mark served | `Order` | | Only allowed when READY |
| Get bill | `Order` | | Integer cents |

## B → A (pricing calls Customer)
| Need | Class | Signature | Notes |
|---|---|---|---|
| Get discount for this customer | `Customer` | | Regular 0%, VIP 10% (Stage 2) |
| *To confirm:* Happy Hour recovery | `Customer` | | +15, capped at 100 (Stage 3). See team-roles "To confirm" |

## Shared types
| Type | Owner | Values / members |
|---|---|---|
| Customer state enum | A | |
| Order status enum | B | |

# Restaurant Rush

SC2002 group assignment: a turn-based restaurant management game in Java (command line).

**Repository:** https://github.com/RobinTeoh/restaurant-rush-sc2002
**Team:** A: Robin (Customers) · B: Ian (Kitchen, Menu & Pricing) · C: Caleb (Game loop, Staff & CLI)

## Team docs
| File | What it is |
|---|---|
| [docs/Team-Manual.pdf](docs/Team-Manual.pdf) | Easy-read manual: roles, timeline, rules, rubric, checklists |
| [docs/development-plan.md](docs/development-plan.md) | Week-by-week plan we follow |
| [docs/team-roles.md](docs/team-roles.md) | Who owns which folders, diagrams, report sections and video segments |
| [DECISIONS.md](DECISIONS.md) | Rules the brief leaves open, and what we chose |
| [CONTRACT.md](CONTRACT.md) | Agreed method signatures between roles |
| [docs/test-notes.md](docs/test-notes.md) | Reproducible runs and edge-case checks |
| [docs/design-log.md](docs/design-log.md) | Design choices, alternatives rejected, and why |

## Requirements
- JDK 17 or newer (compiled with `--release 17`)
- No network access needed; JUnit is bundled in `lib/`

## Build, run, test
| | Windows (cmd) | macOS / Linux / Git Bash |
|---|---|---|
| Build | `build.bat` | `sh build.sh` |
| Run | `run.bat` | `sh run.sh` |
| Test | `test.bat` | `sh test.sh` |
| Replay a run | `run.bat < runs\stage1-victory.txt` | `sh run.sh < runs/stage1-victory.txt` |

## Stages
| Stage | Tag | Adds |
|---|---|---|
| 1 | `stage-1` | Regular customers, menu, Waiter, Chef, Cashier, full turn sequence, both endings |
| 2 | `stage-2` | VIP and Critic customers |
| 3 | `stage-3` | Host, ComboMeal, Happy Hour |

### Stage 1 → Stage 2
Compare: https://github.com/RobinTeoh/restaurant-rush-sc2002/compare/stage-1...stage-2
<!-- new files; changed or deleted files with reasons -->

### Stage 2 → Stage 3
Compare: https://github.com/RobinTeoh/restaurant-rush-sc2002/compare/stage-2...stage-3
<!-- new files; changed or deleted files with reasons -->

## Video
<!-- link + timestamps for segments 1-7 -->

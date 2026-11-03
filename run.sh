#!/bin/sh
# Run the game. Optional scenario: stage 1, stage 2 or stage 3 (default: stage 3, the latest).
#Replay a run file with: sh run.sh < runs/stage1-victory.txt
cd "$(dirname "$0")"
java -cp out restaurant.app.Main "$@"

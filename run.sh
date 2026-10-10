#!/bin/sh
# Run the game. Replay a run file with: sh run.sh < runs/stage1-victory.txt
cd "$(dirname "$0")"
java -cp out restaurant.app.Main "$@"

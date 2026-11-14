@echo off
rem Run the game. Optional scenario: stage 1, stage 2 or stage 3 (default: stage 3, the latest).
rem Replay a run file with: run.bat < runs\stage1-victory.txt
cd /d "%~dp0"
java -cp out restaurant.app.Main %*



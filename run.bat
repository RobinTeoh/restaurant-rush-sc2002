@echo off
rem Run the game. Replay a run file with: run.bat < runs\stage1-victory.txt
cd /d "%~dp0"
java -cp out restaurant.app.Main %*

@echo off
rem Compile all sources in src\ into out\ (Java 17).
setlocal enabledelayedexpansion
cd /d "%~dp0"
if exist out rmdir /s /q out
mkdir out
set "ROOT=%CD%\"
(for /r src %%f in (*.java) do (set "p=%%f" & set "p=!p:%ROOT%=!" & echo "!p:\=/!")) > sources.txt
javac --release 17 -d out @sources.txt
if errorlevel 1 exit /b 1
echo Build OK -^> out\

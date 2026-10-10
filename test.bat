@echo off
rem Compile tests in test\ and run them with the bundled JUnit (run build.bat first).
setlocal enabledelayedexpansion
cd /d "%~dp0"
set JUNIT=lib\junit-platform-console-standalone-1.10.2.jar
if exist out-test rmdir /s /q out-test
mkdir out-test
set "ROOT=%CD%\"
(for /r test %%f in (*.java) do (set "p=%%f" & set "p=!p:%ROOT%=!" & echo "!p:\=/!")) > test-sources.txt
javac --release 17 -d out-test -cp "out;%JUNIT%" @test-sources.txt
if errorlevel 1 exit /b 1
java -jar %JUNIT% execute -cp "out;out-test" --scan-class-path

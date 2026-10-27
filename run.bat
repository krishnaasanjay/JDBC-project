@echo off
cd /d "%~dp0"
if not exist out mkdir out
javac -cp "lib\*" -d out Main.java DBConnection.java EmployeeHandler.java
if errorlevel 1 (
    echo Compilation failed. Read the errors above.
    pause
    exit /b 1
)
java -cp "out;lib\*" Main
pause

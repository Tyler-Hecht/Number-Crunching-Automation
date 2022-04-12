@echo off
cd orders
py trialtypes_automation.py
if %errorlevel% neq 0 (
	pause 
	exit)
py orders_automation.py
if %errorlevel% neq 0 (
	pause
	exit)
cd..
cd input
py xlsx_to_txt.py
if %errorlevel% neq 0 (
	pause
	exit)
py participants_automation.py
if %errorlevel% neq 0 (
	pause
	exit)
cd..
javac Cruncher.java
java Cruncher
echo NUMBER CRUNCHING COMPLETE!
pause
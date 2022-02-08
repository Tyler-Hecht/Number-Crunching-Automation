#!/bin/bash
python3 ./trialtypes_automation.py 
python3 ./orders_automation.py 
python3 xlsx_to_txt.py 
python3 participants_automation.py 
javac Cruncher.java 
java Cruncher 
echo NUMBER CRUNCHING COMPLETE!

#!/bin/bash
cd orders 
python3 ./trialtypes_automation.py 
python3 ./orders_automation.py 
cd .. 
cd input 
python3 xlsx_to_txt.py 
python3 participants_automation.py 
cd .. 
javac Cruncher.java 
java Cruncher 
echo NUMBER CRUNCHING COMPLETE!

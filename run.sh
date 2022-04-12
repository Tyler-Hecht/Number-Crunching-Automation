#!/bin/bash
cd orders 
python3 trialtypes_automation_mac.py 
if [ $? -ne 0 ]; then exit 0; fi 
python3 orders_automation_mac.py 
if [ $? -ne 0 ]; then exit 0; fi 
cd .. 
cd input 
python3 xlsx_to_txt_mac.py 
if [ $? -ne 0 ]; then exit 0; fi 
python3 participants_automation_mac.py 
if [ $? -ne 0 ]; then exit 0; fi 
cd .. 
javac Cruncher.java 
java Cruncher 
echo NUMBER CRUNCHING COMPLETE! 

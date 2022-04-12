#!/bin/bash
python3 clearer.py
if [ $? -ne 0 ]; then exit 0; fi 
python3 input_mover.py
if [ $? -ne 0 ]; then exit 0; fi 
cd DatavyuToSupercoder
java -jar DatavyuToSupercoder.jar
cd ..
python3 copier.py
if [ $? -ne 0 ]; then exit 0; fi 
cd Facetalk
python3 catcher.py
if [ $? -ne 0 ]; then exit 0; fi 
python3 facetalk.py
if [ $? -ne 0 ]; then exit 0; fi 
cd ..
python3 output_mover.py
if [ $? -ne 0 ]; then exit 0; fi 
pause
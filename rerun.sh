@echo off
python3 rerunner.py True
if [ $? -ne 0 ]; then exit 0; fi 
cd Facetalk
python3 catcher.py
if [ $? -ne 0 ]; then exit 0; fi 
python3 facetalk.py
if [ $? -ne 0 ]; then exit 0; fi 
cd ..
python3 rerunner.py False
if [ $? -ne 0 ]; then exit 0; fi 
pause
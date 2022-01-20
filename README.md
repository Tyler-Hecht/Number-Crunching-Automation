# orders_automation
Just place this file in a folder along with the .xlsx file and the trialtypes file ("trialtypes.txt")\n
There should be only one .xlsx file in the folder!\n
The sheets of the xlsx file should be named something like "Order 1"\n
To run, just run the python file ("py orders_automation.py") in the command prompt under the folder's directory\n
This requires pandas and openpyxl to be installed

Things that aren't implemented but can be done:
-Allow for sheets with order numbers in the double digits (e.g. "Order 10")
-Automatically create a trialtypes file given configurations of fps and fpt (and maybe windows)
-Other stuff that I haven't thought of yet

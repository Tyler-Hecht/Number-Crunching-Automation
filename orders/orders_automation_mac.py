from pandas import *
import openpyxl
import os
import glob

#open the xlsx file
path = os.getcwd()
file = glob.glob(os.path.join(path, "*.xlsx"))
# handles errors with xlsx file
if len(file) == 0:
    print("ERROR: No .xlsx files found")
    exit()
if len(file) > 1:
    # allows the .xlsx file to be open while running the program
    if not (len(file) == 2 and (file[0].split("/")[-1] == file[1].split("/")[-1][2:])):
        print("ERROR: Multiple .xlsx files in folder")
        exit(1)
# if no errors
file_name = file[0].split("/")[-1]
xls = ExcelFile(file_name)

# finds trialtypes.txt
try:
    with open("trialtypes.txt") as f:
        txt = f.readlines()
# if there is no trialtypes.txt
except:
    print("ERROR: trialtypes.txt not found. Make sure the file is properly named.")
    exit(1)
# gets the important information from trialtypes.txt
trial_types = {}
for line in txt:
    # ignores the lines about frames
    if line[0] != "F":
       trial_types[line.split()[1]] = line.split()[0]

# replaces "R" with "L" and vice versa
def mirror(str: str) -> str:
    new_str = ""
    for char in str:
        if char == "R":
            new_str += "L"
        elif char == "L":
            new_str += "R"
        else:
            new_str += char
    return new_str

# iterates over every sheet (order)
for sheet in xls.sheet_names:
    # makes sure the sheet is named correctly
    if not "order" in sheet.lower():
        print("ERROR: sheet in .xlsx file named incorrectly. Make sure all the sheets are named \"Order i\" such that i is a single digit.")
        exit(1)
    # converts the sheet to a dataframe
    df = xls.parse(sheet)

    # detects errors in the column names
    try:
        df["Trial Type"]
        df["Participant Looking Location"]
    except:
        print("Columns in .xlsx file named incorrectly. Make sure to call the second and third columns \"Trial Type\" and \"Participant Looking Location\", respectively.")
        exit(1)

    # builds the txt file
    text = ""
    i = 0
    for trial in df["Trial Type"]:
        text += mirror(df["Participant Looking Location"][i].upper()) + " "
        i += 1
        # finds errors in column names
        try:
            text += str(trial_types[trial]) + "\n"
        except:
            print("ERROR: Trial type found in .xlsx file which is not recognized by trialtypes.txt")
            exit(1)
    
    # creates a txt file
    name = "order" + sheet[-1] + ".txt"
    with open(name, 'w') as f:
        f.write(text[:-1])
        
print("Order files created")

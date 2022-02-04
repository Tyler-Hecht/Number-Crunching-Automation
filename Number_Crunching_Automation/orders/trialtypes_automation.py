from pandas import *
import openpyxl
import os
import glob

# open the xlsx file
path = os.getcwd()
file = glob.glob(os.path.join(path, "*.xlsx"))
# handles errors with xlsx file
if len(file) == 0:
    print("ERROR: No .xlsx files found")
    exit(1)
if len(file) > 1:
    # allows the .xlsx file to be open while running the program
    if not (len(file) == 2 and (file[0].split("\\")[-1] == file[1].split("\\")[-1][2:])):
        print("ERROR: Multiple .xlsx files in folder")
        exit(1)
# if no errors
file_name = file[0].split("\\")[-1]
xls = ExcelFile(file_name)

# create dictionary of trial types file
tt_dict = {}
i = 0
for sheet in xls.sheet_names:
    df = xls.parse(sheet)
    for row in df["Trial Type"]:
        if not row in tt_dict:
            tt_dict[row] = i
            i += 1

# asks for user input
fps = input("Frames per second?")
fpt = input("Frames per trial?")
windows = input("""If you would like to choose the window for each trial type, type 0.
If you would like to choose a window that will apply to all trial types, type 1.
If you don't want to add any windows, type 2.""")

# gets the windows
if windows == "0":
    window_dict = {}
    for key in tt_dict:
        window = input("Window for " + key + "? If you don't want a window for this trial, type 0.")
        if window == "0":
            window_dict[key] = ""
        else:
            # detect error in window formatting
            if not ":" in window:
                print("ERROR: Window not formatted correctly")
                exit(1)
            else:
                window_dict[key] = window
if windows == "1":
    window_dict = {}
    window = input("What window would you like for all the trial types?")
    # detect error in window formatting
    if not ":" in window:
        print("ERROR: Window not formatted correctly")
        exit(1)
    for key in tt_dict:
        window_dict[key] = window

# build txt file
txt = "Frames per second = " + fps + "\nFrames per trial = " + fpt + "\n"
for key in tt_dict:
    txt += str(tt_dict[key]) + " " + key
    if windows in ["0", "1"]:
        txt += " " + window_dict[key]
    txt += "\n"
    
# writes the .txt file
with open("trialtypes.txt", 'w') as f:
    f.write(txt[:-1])

print("trialtypes file created")
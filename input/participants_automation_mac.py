from pandas import *
import openpyxl
import os
import glob

# get the participants orders sheet
path = os.getcwd()
xlsx_files = sorted(glob.glob(os.path.join(path, "*.xlsx")))
order_sheets = []
try:
    for file in xlsx_files:
        # this will probably ensure that the order sheet is obtained
        if "order" in file.lower() and "sheet" in file.lower():
            order_sheets.append(file)
# if there aren't any .xlsx files
except:
    print("ERROR: No .xlsx files found")
    exit()
# makes sure there's only one order sheet
if len(order_sheets) == 0:
    print("ERROR: No order sheet found. Make sure it is an .xlsx containing the words \"order\" and \"sheet\".")
    exit()
if len(order_sheets) > 1:
    # allows the .xlsx file to be open while running the program
    if not (len(order_sheets) == 2 and (order_sheets[0].split("/")[-1] == order_sheets[1].split("/")[-1][2:])):
        print("ERROR: Multiple order sheets found")
        exit()
order_sheet = order_sheets[0].split("/")[-1]
# actually goes into the order sheet (skipping the first row because it's not a column header)
df = read_excel(order_sheet, skiprows=[0])
# creates a dictionary of the participant ids and their order number
dict = {}
i = 0
for id in df["Participant #"]:
    dict[id] = df["Order"][i]
    i += 1

# get all the participant .txt files
txt_files = sorted(glob.glob(os.path.join(path, "*.txt")))
# if there aren't any
if len(txt_files) == 0:
    print("ERROR: No .txt files found")
    exit()
    
# builds the .txt file
txt = ""
for file in txt_files:
    # gets the participant id based on the file name
    file_name = file.split("/")[-1]
    if file_name != "Participants.txt":
        participant_id = file_name.split("_")[0]
        # determines the appropriate order file (such as "order2.txt")
        order_file = "order" + str(dict[participant_id]) + ".txt"
        # adds to the text
        txt += participant_id + " " + file_name + " " + order_file + "\n"

# writes the .txt file
with open("Participants.txt", 'w') as f:
    f.write(txt[:-1])
    
print("Participants.txt created")

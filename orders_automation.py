from pandas import *
import os
import glob

#open the xlsx file
path = os.getcwd()
file = glob.glob(os.path.join(path, "*.xlsx"))
file_name = file[0].split("\\")[-1]
xls = ExcelFile(file_name)

#handle the trialtypes file
with open("trialtypes.txt") as f:
    txt = f.readlines()
trial_types = {}
for line in txt:
    if line[0] != "F":
       trial_types[line.split()[1]] = line.split()[0]

# the ol' switcheroo
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

# does the stuff
for sheet in xls.sheet_names:
    df = xls.parse(sheet)
    bad_dict = (df.to_dict())

    # there's probably a better way to do this but it works
    trial_type = []
    participant_looking_location = []
    for key in bad_dict["Trial Type"]:
        trial_type.append(bad_dict["Trial Type"][key])
    for key in bad_dict["Participant Looking Location"]:
        participant_looking_location.append(bad_dict["Participant Looking Location"][key])

    text = ""
    i = 0
    # important stuff
    for trial in trial_type:
        text += mirror(participant_looking_location[i]) + " " + str(trial_types[trial]) + "\n"
        i += 1
    
    # creates a txt file
    name = "order" + sheet[-1] + ".txt"
    with open(name, 'w') as f:
        f.write(text[:-1])
        
print("Complete")
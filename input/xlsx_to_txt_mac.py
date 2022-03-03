from pandas import *
import openpyxl
import os
import glob

def mass_convert_xlsx():
    path = os.getcwd()
    files = []
    # handles the user input by finding specific files
    for file in glob.glob(os.path.join(path, "*.xlsx")):
        # makes sure the order sheet file (used in participants_automation) isn't included
        if not ("order" in file.lower() and "sheet" in file.lower()):
            files.append(file)
    # if there aren't any .xlsx files in the folder
    if not files:
        print("ERROR: No .xlsx files detected in folder")
        exit(1)
    # iterates over every file again, this time actually doing the conversion
    for file in files:
        # gets the file name
        name = file.split("/")[-1]
        name_components = name.split("_")
        # create the new file name (based on whether or not it has a version)
        if len(name_components) > 2 and name_components[2] in ["V1", "V2"]:
            new_name = name_components[0] + "_" + name_components[1] + "_" + name_components[2] + ".txt"
        else:
            print(name_components)
            new_name = name_components[0] + "_" + name_components[1] + ".txt"
            
        # creates a dataframe (df)
        xls = ExcelFile(name)
        # gets the first sheet in the xlsx file
        df = xls.parse(0)
        
        # gets the columns of the df and how many rows there are
        columns = [df.columns[0], df.columns[1], df.columns[2]]
        rows_amount = len(df["B"].to_list())
        
        # builds the text for the txt file
        text = ""
        # python thinks that the cells in the first row are column names, so this handles that
        for column in columns:
            # the df names an empty cell something like "Unnamed 2", so this reverses that
            if "Unnamed" in str(column):
                text += "" + "\t"
            elif type(column) == float:
                text += round(column) + "\t"
            else:
                text += str(column) + "\t"
        # cuts off the last tab
        text = text[:-1]
        # handles the rest of the rows (i is the row number, excluding the first row)
        for i in range(0, rows_amount):
            text += "\n"
            for column in columns:
                # finds what's in the row's cell in each column
                value = str(df[column].to_list()[i])
                # again, reverses empty cells being named strangely
                if value == "nan":
                    text += "" + "\t"
                else:
                    text += value.split(".")[0] + "\t"
            # cuts off the last tab
            text = text[:-1]
        
        # writes the txt file
        with open(new_name, 'w') as f:
            f.write(text)

# runs the program
mass_convert_xlsx()

print(".xlsx files converted to .txt")


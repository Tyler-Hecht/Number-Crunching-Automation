from pandas import *
import openpyxl
import os
import glob

# finds the files
path = os.getcwd()
files = glob.glob(os.path.join(path, "*.xlsx"))
# if there aren't any .xlsx files in the folder
if not files:
    print("ERROR: No .xlsx files detected in folder")
    exit()

def mass_convert_xlsx(version: str):
    # iterates over every file
    for file in files:
        # gets the file name
        name = file.split("\\")[-1]
        name_components = name.split("_")
        # this will only work for a name like "18TH_AWL_V1_completed.xlsx"
        new_name = name_components[0] + "_" + name_components[1] + "_" + name_components[2] + ".txt"
        # ignores the other version
        if version == "V1":
            if name_components[2] == "V2":
                continue
        if version == "V2":
            if name_components[2] == "V1":
                continue
            
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
mass_convert_xlsx(input("V1, V2, or both?").upper())

print("complete")
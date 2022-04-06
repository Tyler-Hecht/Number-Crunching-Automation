import os
import glob
import pandas

path = os.getcwd()
input_path = path + "/DatavyuToSupercoder/Output"
output_path = path + "/Facetalk/Input"
files = glob.glob(os.path.join(input_path, "*.xls"))

# gets rid of that weird OUTPUT.DS_S file
real_files = []
for file in files:
    file_name = file.split("\\")[-1]
    if file_name != "OUTPUT_.DS_S.xls":
        real_files.append(file_name)

# gets the relevant parts of the data
os.chdir(input_path)
dataframes = []
for file in real_files:
    df = pandas.read_excel(file, skiprows = [0], na_filter = True)
    df = df[["Code", "Onset", "Offset"]]
    dataframes.append(df)



# creates the .xlsx files
os.chdir(output_path)
with pandas.ExcelWriter('output.xlsx') as writer:  
    dataframes[0].to_excel(writer, sheet_name='Coder 1', index = False, header = False)
    dataframes[1].to_excel(writer, sheet_name='Coder 2', index = False, header = False)
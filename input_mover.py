import os
import shutil
import glob

path = os.getcwd()
input_path = path + "/INPUT/"
output_path = path + "/DatavyuToSupercoder/Input/"
files = glob.glob(os.path.join(input_path, "*.csv"))

# moves the files
os.chdir(input_path)
for file in files:
    file_name = file.split("\\")[-1]
    shutil.copyfile(file_name, output_path + file_name)
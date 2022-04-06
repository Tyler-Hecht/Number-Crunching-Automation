import os
import shutil

path = os.getcwd()
input_path = path + "/Facetalk/Input/"
output_path = path + "/OUTPUT/"

os.chdir(input_path)
shutil.copyfile("output.xlsx", output_path + "Output.xlsx")
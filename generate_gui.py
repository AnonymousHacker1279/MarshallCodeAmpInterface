import os
import subprocess
import fileinput

# Generate the UI file
cwd = os.getcwd()
generate_ui_command = f"pyside6-uic {cwd}\\designer\\main_window.ui > {cwd}\\package\\ui\\main_window_ui.py"
subprocess.run(generate_ui_command, shell=True)

# Generate the resources file
generate_resources_command = f"pyside6-rcc {cwd}\\resources.qrc -o {cwd}\\package\\ui\\resources_rc.py"
subprocess.run(generate_resources_command, shell=True)

# Replace the import statement in the generated UI file to respect package location
with fileinput.FileInput(f"{cwd}\\package\\ui\\main_window_ui.py", inplace=True) as file:
	for line in file:
		print(line.replace("import resources_rc", "import package.ui.resources_rc"), end='')

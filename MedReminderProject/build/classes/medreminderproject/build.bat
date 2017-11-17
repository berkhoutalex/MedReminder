@echo off

set CLASS_PATH=../../build/classes

if [%1]==[] (
	set JAVA_FILE=MedReminderProject.java
) else (
	set JAVA_FILE=%1
)

copy UILayout.fxml "%CLASS_PATH%/medreminderproject/UILayout.fxml"
javac %JAVA_FILE% -classpath %CLASS_PATH% -d %CLASS_PATH%
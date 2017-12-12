@echo off
cd ../..
set CLASS_PATH="build/classes"
java -cp %CLASS_PATH% -enableassertions medreminderproject.MedReminderProject %1
cd src/medreminderproject
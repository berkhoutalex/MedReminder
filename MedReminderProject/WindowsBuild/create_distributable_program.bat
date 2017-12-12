@echo off

IF EXIST MedReminder ( rmdir MedReminder /S /Q )
mkdir MedReminder
copy "MedReminder.exe" "MedReminder/MedReminder.exe"
xcopy "../build/classes" "MedReminder/classes" /E /Y /Q /I
cd ..
copy "ding.wav" "WindowsBuild/MedReminder/ding.wav"
copy "medication_log.bin" "WindowsBuild/MedReminder/medication_log.bin"
copy "reminders.bin" "WindowsBuild/MedReminder/reminders.bin"
cd WindowsBuild
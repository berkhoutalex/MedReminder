@echo off

IF EXIST MedReminder ( rmdir MedReminder /S /Q )
mkdir MedReminder
copy "MedReminder.exe" "MedReminder/MedReminder.exe"
xcopy "../build/classes" "MedReminder/classes" /E /Y /Q /I
cd ..
copy "ding.wav" "WindowsBuild/MedReminder/ding.wav"
cd WindowsBuild
@echo off
echo RUNNING SAGAN ONE: %date% %time%
java -version 
java -cp ./Sagan-1.jar -Xms256m -Xmx756m pro.carl.edu.sagan1.gui.Sagan1
echo ============================
pause


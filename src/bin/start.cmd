@echo off
setlocal EnableDelayedExpansion
:check
for /f "delims= " %%i in ('jps -l ^| find /i "lg-alarm"') do (
    set pid=%%i
    echo Alarm application has been started, and ran in the background; pid=!pid!
    goto end
)
echo Alarm application startup.....
cd ..
start javaw -Xmx1024m -Xms512m -server -jar lg-alarm-1.0.0.jar
echo Use JAVA_HOME=%JAVA_HOME%
goto check
:end
pause
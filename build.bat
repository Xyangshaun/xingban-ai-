@echo off
set JAVA_HOME=C:\Program Files\Microsoft\jdk-17.0.19.10-hotspot
set PATH=%JAVA_HOME%\bin;%PATH%
echo Java Home: %JAVA_HOME%
echo Java Version:
java -version
echo.
echo Building project...
call gradlew build --no-daemon
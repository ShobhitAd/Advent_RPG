@echo off
echo "Start"

if %1==compile (
	echo "Compiling"
	javac *.java
)
if %1==jar (
	echo "Creating jar"
	jar cvfe Advent.jar clsGame *.class
)

if %1==run (
	echo "Running"
	java clsGame
)
if %1==clean (
	echo "Cleaning"
	del *.class
)
echo "End"
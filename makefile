# Variables
JAVA_FILES = *.java
EXE = clsGame

#Rules
compile:
	javac $(JAVA_FILES)

run:
	java $(EXE)

clean:
	rm *.class

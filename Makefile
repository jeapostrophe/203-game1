all: build test run

build: Game1.java
	# dist contains the jar files linked on the course site
	# On Windows this would be .;dist/libjcsi.jar (the : has become a ;)
	javac -cp .:dist/libjcsi.jar Game1.java

test:
	java -cp .:dist/libjcsi.jar Game1Test

run:
	java -cp .:dist/libjcsi.jar Game1Run

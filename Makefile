all: build test run

WHICH=Game1
WHICH=TwenFort

build: $(WHICH).java
	# dist contains the jar files linked on the course site
	# On Windows this would be .;dist/libjcsi.jar (the : has become a ;)
	javac -cp .:dist/libjcsi.jar $^

test:
	java -cp .:dist/libjcsi.jar $(WHICH)Test

run:
	java -cp .:dist/libjcsi.jar $(WHICH)Run

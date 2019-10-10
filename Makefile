default: main

%: %.java
	javac $@.java
	java -ea $@

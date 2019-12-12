JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	Client1/Client.java \
	Client2/Client.java \
	Client3/Client.java \
	Server/Server.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class

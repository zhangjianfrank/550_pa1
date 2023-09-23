
default: all

all:
	mkdir -p classes
	javac *.java -d ./classes

clean:
	$(RM) *.class

run_server:
	java IndexServer

run_peer:
	java PeerServer

run_test:
	java TestClient

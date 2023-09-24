default: all

all:
	javac -d  ../out/production/550_pa1/  *.java

clean:
	$(RM)  ../out/production/550_pa1/*.class

run_server:
	 java -classpath ../out/production/550_pa1 IndexServer

run_peer:
	java -classpath ../out/production/550_pa1 PeerServer

run_test:
	java -classpath ../out/production/550_pa1 TestClient
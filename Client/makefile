CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system -lboost_thread -pthread

all: BGRSclient
	g++ -o bin/BGRSclient bin/connectionHandler.o bin/ReadFromClient.o bin/ReadFromServer.o bin/BGRSclient.o $(LDFLAGS)

BGRSclient: bin/connectionHandler.o bin/ReadFromClient.o bin/ReadFromServer.o bin/BGRSclient.o

bin/BGRSclient.o: src/BGRSclient.cpp
	g++ $(CFLAGS) -o bin/BGRSclient.o src/BGRSclient.cpp

bin/connectionHandler.o: src/connectionHandler.cpp
	g++ $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp

bin/ReadFromClient.o: src/ReadFromClient.cpp
	g++ $(CFLAGS) -o bin/ReadFromClient.o src/ReadFromClient.cpp

bin/ReadFromServer.o: src/ReadFromServer.cpp
	g++ $(CFLAGS) -o bin/ReadFromServer.o src/ReadFromServer.cpp

.PHONY: clean
clean:
	rm -f bin/*

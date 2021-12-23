#include <stdlib.h>
#include "../include/connectionHandler.h"
#include "../include/ReadFromClient.h"
#include "../include/ReadFromServer.h"
#include <mutex>
#include <thread>
#include <string>
#include <iostream>


int main (int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        cout << 7 << endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);

    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }
    std::cout << "connected" << std::endl;

    std::mutex mutex;
    ReadFromClient readerClient(1, mutex, connectionHandler);
    ReadFromServer readerServer(2, mutex, connectionHandler);
    std::thread th1(&ReadFromClient::run, &readerClient);
    std::thread th2(&ReadFromServer::run, &readerServer);
    th1.join();
    th2.join();
    return 0;
}



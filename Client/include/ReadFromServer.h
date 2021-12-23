

#ifndef READFROMSERVER_H
#define READFROMSERVER_H

#endif //BOOST_ECHO_CLIENT_2_READFROMSERVER_H
#include <iostream>
#include <mutex>
#include <thread>
#include "../include/connectionHandler.h"


using namespace std;

class ReadFromServer{
private:
    int _id;
    std::mutex & _mutex;
    ConnectionHandler& connectionHandler;

public:
    ReadFromServer (int id, std::mutex& mutex,ConnectionHandler& connectionHandler1);
    void run();
    bool decode(std::string& frame, char delimiter);

};
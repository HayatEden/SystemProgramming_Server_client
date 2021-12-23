
#ifndef READ_FROM_CLIENT__
#define READ_FROM_CLIENT__

#endif //BOOST_ECHO_CLIENT_2_THREAD_H
#include <iostream>
#include <mutex>
#include <thread>
#include "../include/connectionHandler.h"


using namespace std;

class ReadFromClient{
private:
    int _id;
    std::mutex & _mutex;
    ConnectionHandler& connectionHandler;

public:
    ReadFromClient (int id, std::mutex& mutex,ConnectionHandler& connectionHandler1);
    void run();
    bool encode(string& str);
    short opcode(string str);


};


#include "../include/ReadFromServer.h"
#include <boost/algorithm/string.hpp>
#include <string>
#include <iostream>

ReadFromServer::ReadFromServer(int id, std::mutex& mutex,ConnectionHandler& connectionHandler1) : _id(id), _mutex(mutex),connectionHandler(connectionHandler1){}

void ReadFromServer::run() {
    std::string answer;
    while (1) {
        if (!decode(answer, '\0')) {
            break;
        }
        std::cout<<answer<<std::endl;

        if (answer == "ACK 4") {
            _mutex.unlock();
            break;
        }

        answer = "";
    }
}


bool ReadFromServer::decode(std::string& frame, char delimiter){
    char ch;
    try {
        if (!connectionHandler.getBytes(&ch, 2)) {
            return false;
        }
        // decode opcode
        short opcode = connectionHandler.bytesToShort(&ch);
        if (opcode == 12) {
            frame.append("ACK ");
        } else {
            frame.append("ERROR ");
        }
        // decode message number
        if (!connectionHandler.getBytes(&ch, 2)) {
            return false;
        }
        short messageOpcode = connectionHandler.bytesToShort(&ch);
        frame = frame + to_string(messageOpcode);
        // if it is ack - decode optional part of the message
        if (frame[0] == 'A') {
            do {
                if (!connectionHandler.getBytes(&ch, 1)) {
                    return false;
                }
                if (ch != '\0')
                    frame.append(1, ch);
            } while (delimiter != ch);
        }
    } catch (std::exception &e) {
        return false;
    }
    return true;
}


//
// Created by eden hayat on 05/01/2021.
//
#include "../include/ReadFromClient.h"
#include "../include/connectionHandler.h"
#include <boost/algorithm/string.hpp>
#include <string>
#include <iostream>


ReadFromClient::ReadFromClient (int id, std::mutex& mutex,ConnectionHandler& connectionHandler1) : _id(id), _mutex(mutex),connectionHandler(connectionHandler1) {}

void ReadFromClient::run() {
    while (1) {
        const short bufSize = 1024;
        char buf[bufSize];
        std::cin.getline(buf, bufSize);
        std::string line(buf);
        if (!encode(line)) {
            break;
        }
        if(line=="LOGOUT"){
            while(!_mutex.try_lock()){}
                break;
        }
    }
}
bool ReadFromClient::encode(string& str){
    vector<string> result;
    split(result, str, boost::is_any_of(" "));
    vector<char> toSend(0);
    short op = opcode(result[0]);
    connectionHandler.shortToBytes(op,toSend);
    if(op<=3 || op == 8){
        string userName = result[1];
        for (int i = 0; i < userName.size(); ++i) {
            toSend.push_back(userName[i]);
        }
        toSend.push_back('\000');
        if (op != 8) {
            string pass = result[2];
            for (int i = 0; i < pass.size(); ++i) {
                toSend.push_back(pass[i]);
            }
            toSend.push_back('\000');
        }
    }
    else if (op == 5 || op==6 || op==7 || op==9|| op==10){ // for opcode 5-7,9-10
        string courseNum = result[1];
        int courseNumInt = stoi(courseNum);
        short courseNumShort = (short)courseNumInt;
        connectionHandler.shortToBytes(courseNumShort,toSend);
    }
    //if op is 4 or 11 - just send
    connectionHandler.sendBytes(&toSend[0],toSend.size());

    return true;
}

short ReadFromClient::opcode(string str){
    if(str=="ADMINREG"){
        return 1;
    }
    if(str=="STUDENTREG"){
        return 2;
    }
    if(str=="LOGIN"){
        return 3;
    }
    if(str=="LOGOUT"){
        return 4;
    }
    if(str=="COURSEREG"){
        return 5;
    }
    if(str=="KDAMCHECK"){
        return 6;
    }
    if(str=="COURSESTAT"){
        return 7;
    }
    if(str=="STUDENTSTAT"){
        return 8;
    }
    if(str=="ISREGISTERED"){
        return 9;
    }
    if(str=="UNREGISTER"){
        return 10;
    }
    if(str=="MYCOURSES"){
        return 11;
    }
    return -1;
}



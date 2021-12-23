#include "../include/connectionHandler.h"
#include <boost/algorithm/string.hpp>
#include <string>
#include <iostream>
#include <mutex>
#include <thread>



using boost::asio::ip::tcp;

using std::cin;
using std::cout;
using std::cerr;
using std::endl;
using std::string;
 
ConnectionHandler::ConnectionHandler(string host, short port): host_(std::move(host)), port_(port), io_service_(), socket_(io_service_){}
    
ConnectionHandler::~ConnectionHandler() {
    close();
}
 
bool ConnectionHandler::connect() {
    std::cout << "Starting connect to " 
        << host_ << ":" << port_ << std::endl;
    try {
		tcp::endpoint endpoint(boost::asio::ip::address::from_string(host_), port_); // the server endpoint
		boost::system::error_code error;
		socket_.connect(endpoint, error);
		if (error)
			throw boost::system::system_error(error);
    }
    catch (std::exception& e) {
        std::cerr << "Connection failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}
 
bool ConnectionHandler::getBytes(char bytes[], unsigned int bytesToRead) {
    std::lock_guard<std::mutex> lock(mutex);
    size_t tmp = 0;
	boost::system::error_code error;
    try {
        while (!error && bytesToRead > tmp ) {
			tmp += socket_.read_some(boost::asio::buffer(bytes+tmp, bytesToRead-tmp), error);			
        }
		if(error)
			throw boost::system::system_error(error);
    } catch (std::exception& e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

bool ConnectionHandler::sendBytes(const char bytes[], int bytesToWrite) {
    std::lock_guard<std::mutex> lock(mutex);
    int tmp = 0;
	boost::system::error_code error;
    try {
        while (!error && bytesToWrite > tmp ) {
			tmp += socket_.write_some(boost::asio::buffer(bytes + tmp, bytesToWrite - tmp), error);
        }
		if(error)
			throw boost::system::system_error(error);
    } catch (std::exception& e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}
 
bool ConnectionHandler::getLine(std::string& line) {
    return getFrameAscii(line, '\0');
}

bool ConnectionHandler::sendLine(std::string& line) {
    return sendImp(line);
}
 

bool ConnectionHandler::getFrameAscii(std::string& frame, char delimiter) {
    char ch;
    // Stop when we encounter the null character.
    // Notice that the null character is not appended to the frame string.
    try {
        if (!getBytes(&ch, 2)) {
            return false;
        }
        // decode opcode
        short opcode = bytesToShort(&ch);
        if (opcode == 12) {
            frame.append("ACK ");
        } else {
            frame.append("ERR ");
        }
        // decode message number
        if (!getBytes(&ch, 2)) {
            return false;
        }
        short messageOpcode = bytesToShort(&ch);
        frame = frame + to_string(messageOpcode);

        // if it is ack - decode optional part of the message
        if (frame[0] == 'A') {
            try {
                do {
                    if (!getBytes(&ch, 1)) {
                        return false;
                    }
                    if (ch != '\0')
                        frame.append(1, ch);
                } while (delimiter != ch);
            } catch (std::exception &e) {
                std::cerr << "recv failed2 (Error: " << e.what() << ')' << std::endl;
                return false;
            }
        }
    } catch (std::exception &e) {
        std::cerr << "recv failed2 (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;

}


short ConnectionHandler:: bytesToShort(char* bytesArr){
    short result = (short)((bytesArr[0] & 0xff) << 8);
    result += (short)(bytesArr[1] & 0xff);
    return result;
}
void ConnectionHandler:: shortToBytes(short num, vector<char>& bytesArr){
    bytesArr.push_back((num >> 8) & 0xFF);
    bytesArr.push_back(num & 0xFF);
}

 
 
bool ConnectionHandler::sendFrameAscii(const std::string& frame, char delimiter) {
	bool result=sendBytes(frame.c_str(),frame.length());
	if(!result) return false;
	return sendBytes(&delimiter,1);
}

bool ConnectionHandler::sendImp(string& str){
    vector<string> result;
    split(result, str, boost::is_any_of(" "));
    vector<char> toSend(0);
    short op = opcode(result[0]);
    shortToBytes(op,toSend);
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
        shortToBytes(courseNumShort,toSend);
//        short course = courseNum;
    }
    //if op is 4 or 11 - just send
    sendBytes(&toSend[0],toSend.size());

    return true;
}
 
// Close down the connection properly.
void ConnectionHandler::close() {
    try {
        socket_.close();
    } catch (...) {
        std::cout << "closing failed: connection already closed" << std::endl;
    }

}


short ConnectionHandler::opcode(string str){
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

//string ConnectionHandler::decode(string str ){
//    char ch;
//    string decodeAns;
//    try{
//        while(getBytes(&ch,1)){
//            decodeAns=decodeAns+ch;
//        }
//    }catch(std::exception& e) {}
//    if(decodeAns.length()<4){ // message is not eligible
//        return nullptr;
//    }
//    string toPrint = "";
//    if (decodeAns[1] == '3'){
//        toPrint = toPrint + "ERR ";
//    } else{
//        toPrint = toPrint + "ACK ";
//    }
//
//
//    return decodeAns;
//    int tmp = stoi(&str[1]);
//    if (tmp == 3{
//    decodeAns=decodeAns + "ERR "+str[2]+str[3];
//    }else
//        decodeAns=decodeAns + "ACK " + ;

//}


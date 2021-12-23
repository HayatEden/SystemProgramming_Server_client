//
// Created by eden hayat on 04/01/2021.
//
#ifndef BOOST_ECHO_CLIENT_2_THREAD_H
#define BOOST_ECHO_CLIENT_2_THREAD_H

#endif //BOOST_ECHO_CLIENT_2_THREAD_H
#include <iostream>
#include <mutex>
#include <thread>

using namespace std;

class Thread{
private:
    int _id;
    std::mutex & _mutex;
public:
    Thread (int id, std::mutex& mutex) : _id(id), _mutex(mutex) {}

    void run(){
        for (int i= 0; i < 100; i++){
            std::lock_guard<std::mutex> lock(_mutex); // constructor locks the mutex while destructor (out of scope) unlocks it
            std::cout << i << ") Task " << _id << " is working" << std::endl;
        }
    }
};
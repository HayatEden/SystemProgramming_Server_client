package bgu.spl.net.api;


import bgu.spl.net.impl.rci.*;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class MessageEncoderDecoderImpl implements MessageEncoderDecoder<CommandImpl> {

    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private CommandImpl command;
    private int bytesLen = 0;
    private int opcodeWasRead = 0;
    private short opcode = -1;
    private int numOfZeroBytes = 0;
    private int courseNumWasRead = 0;

    @Override
    public CommandImpl decodeNextByte(byte nextByte) {

        if (opcode != -1) { // msg already identified by opcode
            boolean finishedDecode;
            if (opcode <= 3 || opcode == 8) { // it is ADMINREG/STUDENTREG/LOGIN/STUDENTSTAT message
                finishedDecode = userAndPass(nextByte);
            } else { // it is COURSEREG/KDAMCHECK/COURSESTAT/ISREGISTER/UNREGISTER
                finishedDecode = courseNumber(nextByte);
            }
            if (finishedDecode){
                return command;
            }
        }
        else { // msg wasn't identified yet
            opcodeWasRead++;
            pushByte(nextByte);
            if (opcodeWasRead == 2) { // finished reading opcode
                opcode = bytesToShort(bytes);
                createCommand();
                bytesLen = 0;
                command.setOpcode(opcode);
            }
            if (opcode == 4 || opcode == 11){ // it is LOGOUT/MYCOURSES and therefore contains only opcode
                resetEncDec();
                return command;
            }
        }
        return null;
    }


    public void resetEncDec(){
        bytesLen = 0;
        opcodeWasRead = 0;
        opcode = -1;
        numOfZeroBytes = 0;
        courseNumWasRead = 0;
    }


    public boolean courseNumber(byte nextbyte){
        if (courseNumWasRead == 1){
            pushByte(nextbyte);
            int courseNum = bytesToShort(bytes);
            command.setCourseNum(courseNum);
            resetEncDec();
            return true;
        }
        courseNumWasRead++;
        pushByte(nextbyte);
        return false;
    }

    public boolean userAndPass(byte nextByte) {
        if (nextByte == '\0'){
            numOfZeroBytes++;
            if (numOfZeroBytes == 1){ // finished reading username
                command.setUserName(popString());
                if (opcode == 8){ // it is studentStat msg and there is only a username
                    resetEncDec();
                    return true;
                }
                bytesLen = 0;
            }
            if (numOfZeroBytes == 2){ // finished reading username and password
                command.setPassword(popString());
                resetEncDec();
                return true;
            }
        }
        pushByte(nextByte);
        return false;
    }

    public boolean thereIsMoreToSend(CommandImpl message){
        if (opcode >= 6 && opcode <= 11 && opcode != 10){
            return true;
        }
        return false;
    }

    @Override
    public byte[] encode(CommandImpl message) {
        byte[] fullMessage;
        String str = "";
        short Opcode;
        short currOpcode = message.getOpcode();
        if (message.isERR()){
            Opcode = 13;
            fullMessage = new byte[4];
        }else { //isACK
            Opcode = 12;
            if (currOpcode == 6) {
                str = ((commandCheckKdam6) message).getKdamCoursesList();
            }
            else if (currOpcode == 7) {
                str = '\n' + "Course: (" + ((commandCourseStatus7) message).getCourseNum().toString() + ")";
                str = str + " " + ((commandCourseStatus7)message).getCourseName();
                str = str + '\n' + "Seats Available: " + ((commandCourseStatus7) message).getSeatsAvailable() + "/" + ((commandCourseStatus7) message).getNumOfMaxStudents();
                str = str + '\n' + "Students Registered: [";
                LinkedList<String> registeredStudents = ((commandCourseStatus7) message).getRegisteredStudents();
                for (int i = 0; i < registeredStudents.size(); i++) {
                    str = str + registeredStudents.get(i) + ",";
                }
                if (registeredStudents.size() == 0){
                    str = str + "]";
                }
                else {
                    str = str.substring(0, str.length() - 1) + "]";
                }

            }
            else if (currOpcode == 8) {
                str = str + '\n' + "Student: " + message.getUserName();
                str = str + '\n' + "Courses: " + ((commandStudentStatus8)message).getCourseList();
            }
            else if (currOpcode == 9) {
                str = str + '\n';
                if (((commandCheckIfReg9) message).isRegistered()) {
                    str = str + "REGISTERED";
                } else
                    str = str + "NOT REGISTERED";
            } else if (currOpcode == 11) {
                str = str + '\n' + ((commandMyCurrCourse11)message).getCourseList();
            }
            str = str + '\0';
            fullMessage = new byte[4+str.length()];
        }
        byte[] ACKorERR = shortToBytes(Opcode);
        short MessageOpcode = message.getOpcode();
        byte[] messageOpcode = shortToBytes(MessageOpcode);
        byte[] info = str.getBytes();
        ByteBuffer buffer = ByteBuffer.wrap(fullMessage);
        buffer.put(ACKorERR);
        buffer.put(messageOpcode);
        buffer.put(info);
        return buffer.array();
    }

    public short bytesToShort(byte[] byteArr){
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }

    public byte[] shortToBytes(short num){
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }


    private void pushByte(byte nextByte) {
        if (bytesLen >= bytes.length) {
            bytes = Arrays.copyOf(bytes, bytesLen * 2);
        }
        bytes[bytesLen++] = nextByte;
    }

    private String popString() {
        //notice that we explicitly requesting that the string will be decoded from UTF-8
        //this is not actually required as it is the default encoding in java.
        String result = new String(bytes, 0, bytesLen, StandardCharsets.UTF_8);
        bytesLen = 0;
        return result;
    }

    public void createCommand(){
        if (opcode <= 3){
            command = new commandUserAndPass1to3();
        }
        else if (opcode == 4){
            command = new commandLogOut4();
        }
        else if (opcode == 5){
            command = new commandCourseReg5();
        }
        else if (opcode == 6){
            command = new commandCheckKdam6();
        }
        else if (opcode == 7){
            command = new commandCourseStatus7();
        }
        else if (opcode == 8){
            command = new commandStudentStatus8();
        }
        else if (opcode == 9){
            command = new commandCheckIfReg9();
        }
        else if (opcode == 10){
            command = new commandUnregister10();
        }
        else { // opcode == 11
            command = new commandMyCurrCourse11();
        }
    }

}

package uk.co.olbois.facecraft.networking;

import java.security.MessageDigest;
import java.util.Date;

import uk.co.olbois.facecraft.model.SampleUser;
import uk.co.olbois.facecraft.model.message.Message;

/**
 * Class for communicating with the springio server
 */
public class DatabaseController {

    private static Date messageDate;
    private static Date eventDate;

    /**
     * get all messages from the database after the current last time retreived
     */
    public void getMessages() {

        if (messageDate == null)
            messageDate = new Date();

        //do stuff


        messageDate = new Date();
    }

    /**
     * send a message to the database
     */
    public void sendMessage(Message message){

    }

    /**
     * get any events since the last poll
     */
    public void getEvents(){

        if (eventDate == null)
            eventDate = new Date();

        //do stuff


        eventDate = new Date();
    }

    public void sendEvent() {

    }

    /**
     * ??????? do we need this
     */
    public void login(SampleUser sampleUser) {

    }

    /**
     * again ?????????? do we need
     */
    public void addServer() {

    }

    public void connectToServer() {

    }

    public void getInvites() {

    }

    public void sendInvite() {

    }

}

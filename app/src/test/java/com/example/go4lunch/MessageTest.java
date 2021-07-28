package com.example.go4lunch;


import com.example.go4lunch.models.Message;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;

public class MessageTest {
    private Message messageTest;
    private Message messagePicture;


    @Before
    public void setUp() throws Exception {
        messageTest = new Message("test","user1","picture1","user2", Calendar.getInstance().getTime());
        messagePicture = new Message("test","restaurantPicture","user1","picture1", Calendar.getInstance().getTime());
    }

    @Test
    public void getMessageInfo(){
        assertEquals("test", messageTest.getMessage());
        assertEquals("user1", messageTest.getIdUserSenderIdReceiver());
        assertEquals("picture1", messageTest.getUrlImageUserSender());
    }

    @Test
    public void setMessageInfo() {
        messageTest.setMessage("1111");
        messageTest.setUrlImage("url_picture");

        assertEquals("1111", messageTest.getMessage());
        assertEquals("url_picture", messageTest.getUrlImage());
    }

}

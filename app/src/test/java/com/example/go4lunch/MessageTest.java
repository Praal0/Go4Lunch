package com.example.go4lunch;


import com.example.go4lunch.models.Message;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;

public class MessageTest {
    private Message messageTest;



    @Before
    public void setUp() throws Exception {
        messageTest = new Message("test","fdf","user1/user2","picture1", Calendar.getInstance().getTime());
    }

    @Test
    public void getMessageInfo(){
        assertEquals("test", messageTest.getMessage());
        assertEquals("fdf",messageTest.getIdUserSender());
        assertEquals("user1/user2", messageTest.getIdUserSenderIdReceiver());
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

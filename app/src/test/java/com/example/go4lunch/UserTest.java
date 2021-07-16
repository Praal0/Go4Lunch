package com.example.go4lunch;


import com.example.go4lunch.models.User;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserTest {
    private User usertest;

    @Before
    public void setUp() throws Exception {
        usertest = new User("1234","username",null,false);
    }

    @Test
    public void getUserInfo() {
        assertEquals("1234", usertest.getUid());
        assertEquals("username", usertest.getUsername());
        assertEquals(null, usertest.getUrlPicture());
        assertEquals(false, usertest.getNotification());
    }

    @Test
    public void setUserInfo() {
        usertest.setUid("1111");
        usertest.setUsername("test_username");
        usertest.setUrlPicture("url_picture");
        usertest.setNotification(true);

        assertEquals("1111", usertest.getUid());
        assertEquals("test_username", usertest.getUsername());
        assertEquals("url_picture", usertest.getUrlPicture());
        assertEquals(true, usertest.getNotification());
    }
}

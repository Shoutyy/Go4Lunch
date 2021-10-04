package com.example.go4lunch;

import com.example.go4lunch.models.User;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;


public class UserTest {
    private User userTest;

    @Before
    public void setUp() throws Exception {
        userTest = new User("1234","username",null,"1234567",null,1444);
    }

    @Test
    public void getUserInfo() {
        assertEquals("1234", userTest.getUid());
        assertEquals("username", userTest.getUsername());
        assertEquals(null, userTest.getUrlPicture());
        assertEquals("1234567", userTest.getPlaceId());
        assertEquals(null, userTest.getLike());
        assertEquals(1444, userTest.getCurrentTime());
    }

    @Test
    public void setUserInfo() {
        userTest.setUid("1235");
        userTest.setUsername("usernameTest");
        userTest.setUrlPicture("url_picture");
        userTest.setPlaceId("1234568");
        userTest.setLike(null);

        assertEquals("1235", userTest.getUid());
        assertEquals("usernameTest", userTest.getUsername());
        assertEquals("url_picture", userTest.getUrlPicture());
        assertEquals("1234568", userTest.getPlaceId());
        assertEquals(null, userTest.getLike());
    }
}

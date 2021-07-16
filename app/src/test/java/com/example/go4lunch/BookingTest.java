package com.example.go4lunch;

import com.example.go4lunch.models.Booking;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class BookingTest {
    private Booking booking;

    @Before
    public void setUp() throws Exception {
        booking = new Booking("24/09/2021","1234", "5678","Test_Name");
    }

    @Test
    public void getBookingInfo(){
        assertEquals("24/09/2021", booking.getBookingDate());
        assertEquals("1234", booking.getUserId());
        assertEquals("5678", booking.getRestaurantId());
        assertEquals("Test_Name", booking.getRestaurantName());
    }

    @Test
    public void setBookingInfo() {
        booking.setBookingDate("25/10/2019");
        booking.setUserId("1111");
        booking.setRestaurantId("9999");
        booking.setRestaurantName("RestaurantName");

        assertEquals("25/10/2019", booking.getBookingDate());
        assertEquals("1111", booking.getUserId());
        assertEquals("9999", booking.getRestaurantId());
        assertEquals("RestaurantName", booking.getRestaurantName());
    }
}
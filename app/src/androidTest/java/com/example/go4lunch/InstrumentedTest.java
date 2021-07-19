package com.example.go4lunch;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.go4lunch.models.PlacesInfo.MapPlacesInfo;
import com.example.go4lunch.models.PlacesInfo.PlacesDetails.PlaceDetailsInfo;
import com.example.go4lunch.utils.PlacesStreams;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class InstrumentedTest {
    private String placeId;
    private String api_KEY;
    private String location;
    private int radius;
    private String type = "restaurant";

    @Before
    public void setUp() throws Exception {
        placeId = "ChIJ4V5WgPjtwkcRPCAt0H7xGF0";
        api_KEY = BuildConfig.API_KEY;
        location = "50.3663983,3.55778";
        radius = 1000;
    }

    @Test
    public void checkSimplePlaceInfo() throws Exception {
        io.reactivex.Observable<PlaceDetailsInfo> detailsInfoObservable = PlacesStreams.streamSimpleFetchPlaceInfo(placeId, api_KEY);
        TestObserver<PlaceDetailsInfo> testObserver = new TestObserver<>();
        detailsInfoObservable.subscribeWith(testObserver)
                .assertNoErrors()
                .assertNoTimeout()
                .awaitTerminalEvent();
        PlaceDetailsInfo placeDetailsInfo = testObserver.values().get(0);
        assertEquals("Dolce Pizza", placeDetailsInfo.getResult().getName());
    }

    @Test
    public void checkNearbyPlace() {
        Observable<MapPlacesInfo> detailsInfoObservable = PlacesStreams.streamFetchNearbyPlaces(location,radius,type, api_KEY);
        TestObserver<MapPlacesInfo> testObserver = new TestObserver<>();
        detailsInfoObservable.subscribeWith(testObserver)
                .assertNoErrors()
                .assertNoTimeout()
                .awaitTerminalEvent();
        MapPlacesInfo mapPlacesInfo = testObserver.values().get(0);
        assertEquals(true, mapPlacesInfo.getResults().size() > 0);
    }
}
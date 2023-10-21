package uk.ac.ed.inf;

import junit.framework.TestCase;
import org.junit.jupiter.api.Test;
import uk.ac.ed.inf.ilp.data.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RESTAccessTest extends TestCase {
    @Test
    public void testIsAliveNoURL() {
        RESTAccess access = new RESTAccess();

        boolean isIt = false;
        try {
            isIt = access.isAlive(null);
        } catch (IOException | InterruptedException e1) {
            fail("An exception " + e1 + " was thrown");
        }

        assertEquals(isIt, false);
    }

    @Test
    public void testIsAliveMalformedURL() {
        RESTAccess access = new RESTAccess();

        boolean isIt = false;
        try {
            isIt = access.isAlive("https://www.google.com");
        } catch (IOException | InterruptedException e1) {
            fail("An exception " + e1 + " was thrown");
        }

        assertEquals(isIt, false);
    }

    @Test
    public void testIsAlive() {
        RESTAccess access = new RESTAccess();

        boolean isIt = false;
        try {
            isIt = access.isAlive("https://ilp-rest.azurewebsites.net");
        } catch (IOException | InterruptedException e1) {
            fail("An exception " + e1 + " was thrown");
        }

        assertEquals(isIt, true);
    }

    @Test
    public void testGetRestaurants() {
        RESTAccess access = new RESTAccess();

        Restaurant[] restaurants = new Restaurant[] {};
        try {
            restaurants = access.getRestaurants("https://ilp-rest.azurewebsites.net");
        } catch (IOException | InterruptedException e1) {
            fail("An exception " + e1 + " was thrown");
        }

        List<Restaurant> restaurantsArray = new ArrayList<>();

        boolean isEqual = restaurantsArray.containsAll(List.of(TestConstants.RESTAURANTS));

        assertEquals(true, isEqual);
    }
}
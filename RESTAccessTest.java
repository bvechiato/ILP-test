package uk.ac.ed.inf;

import junit.framework.TestCase;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.DayOfWeek;

public class RESTAccessTest extends TestCase {
    Restaurant[] RESTAURANTS = new Restaurant[] {
            new Restaurant("Civerinos Slice",
                    new LngLat(-3.1912869215011597, 55.945535152517735),
                    new DayOfWeek[] {DayOfWeek.MONDAY, DayOfWeek.SUNDAY},
                    new Pizza[] {
                            new Pizza("Margarita", 1000),
                            new Pizza("Calzone", 1400)
                    }
            ),
            new Restaurant("Sora Lella Vegan Restaurant",
                    new LngLat(-3.202541470527649, 55.943284737579376),
                    new DayOfWeek[] {DayOfWeek.MONDAY, DayOfWeek.FRIDAY},
                    new Pizza[] {
                            new Pizza("Meat Lover", 1400),
                            new Pizza("Vegan Delight", 1100)
                    }
            ),
            new Restaurant("Domino's Pizza - Edinburgh - Southside",
                    new LngLat(-3.1838572025299072, 55.94449876875712),
                    new DayOfWeek[] {DayOfWeek.WEDNESDAY, DayOfWeek.SUNDAY},
                    new Pizza[] {
                            new Pizza("Super Cheese", 1400),
                            new Pizza("All Shrooms", 900)
                    }
            ),
            new Restaurant("Sodeberg Pavillion",
                    new LngLat(-3.1940174102783203, 55.94390696616939),
                    new DayOfWeek[] {DayOfWeek.TUESDAY, DayOfWeek.SUNDAY},
                    new Pizza[] {
                            new Pizza("Proper Pizza", 1400),
                            new Pizza("Pineapple & Ham & Cheese", 900)
                    }
            )
    };

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
}

package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Pizza;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.time.DayOfWeek;

public final class TestConstants {
    public static final String ENDPOINT = "https://ilp-rest.azurewebsites.net/";

    public final static LngLat APPLETON_TOWER = new LngLat(-3.186874, 55.944494);

    public static final NamedRegion CENTRAL_AREA = new NamedRegion("Central Area", new LngLat[] {
            new LngLat(-3.190578818321228,55.94402412577528),
            new LngLat(-3.1899887323379517,55.94284650540911),
            new LngLat(-3.187097311019897,55.94328811724263),
            new LngLat(-3.187682032585144, 55.944477740393744),
            new LngLat(-3.190578818321228,55.94402412577528)
    }
    );

    public static final Restaurant[] RESTAURANTS = new Restaurant[] {
            new Restaurant("Civerinos Slice",
                    new LngLat(-3.1912869215011597, 55.945535152517735),
                    new DayOfWeek[] {DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY},
                    new Pizza[] {
                            new Pizza("Margarita", 1000),
                            new Pizza("Calzone", 1400)
                    }
            ),
            new Restaurant("Sora Lella Vegan Restaurant",
                    new LngLat(-3.202541470527649, 55.943284737579376),
                    new DayOfWeek[] {DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY},
                    new Pizza[] {
                            new Pizza("Meat Lover", 1400),
                            new Pizza("Vegan Delight", 1100)
                    }
            ),
            new Restaurant("Domino's Pizza - Edinburgh - Southside",
                    new LngLat(-3.1838572025299072, 55.94449876875712),
                    new DayOfWeek[] {DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY},
                    new Pizza[] {
                            new Pizza("Super Cheese", 1400),
                            new Pizza("All Shrooms", 900)
                    }
            ),
            new Restaurant("Sodeberg Pavillion",
                    new LngLat(-3.1940174102783203, 55.94390696616939),
                    new DayOfWeek[] {DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY},
                    new Pizza[] {
                            new Pizza("Proper Pizza", 1400),
                            new Pizza("Pineapple & Ham & Cheese", 900)
                    }
            )
    };
    public TestConstants() {
    }
}
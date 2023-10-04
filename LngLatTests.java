package uk.ac.ed.inf;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ed.ac.info.LngLatHandler;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for simple App.
 */
public class LngLatTests {
    /** distanceTo */
    @Test
    void distanceCorrect() {
        LngLatHandler handler = new LngLatHandler();

        // Coordinates of the points (also the campus boundaries but shhh)
        double x1 = -3.192473; double y1 = 55.946233;
        double x2 = -3.184319; double y2 = 55.942617;

        LngLat pointA = new LngLat(x1, y1); LngLat pointB = new LngLat(x2, y2);

        assertTrue(handler.distanceTo(pointA, pointB) == Math.hypot(Math.abs(x1 - x2), Math.abs(y1 - y2)));
    }

    /** isCloseTo */
    @RepeatedTest(100)
    void isClose() {
        LngLatHandler handler = new LngLatHandler();

        // Coordinates of the point
        double x1 = -3.192473; double y1 = 55.946233;
        LngLat pointA = new LngLat(x1, y1);

        // For each valid movement direction
        for (double angle = 0; angle < 360; angle += 22.5) {
            // Generate a random radius such that radius <= 0.00014
            double randomR = new Random().nextDouble() * 0.00014;

            // Apply trigonometry to find the point that distance along that angle
            LngLat pointB = new LngLat(x1 + randomR * Math.cos(angle * Math.PI/180), y1 + randomR * Math.sin(angle * Math.PI/180));

            // Because the distance is less than 0.00015, the point should always be close
            assertTrue(handler.isCloseTo(pointA, pointB));
        }
    }

    @Test
    void isNotClose() {
        LngLatHandler handler = new LngLatHandler();

        // Coordinates of the points (also the campus boundaries but shhh)
        double x1 = -3.192473; double y1 = 55.946233;
        double x2 = -3.184319; double y2 = 55.942617;

        // These two points are evidently not close
        LngLat pointA = new LngLat(x1, y1); LngLat pointB = new LngLat(x2, y2);

        assertFalse(handler.isCloseTo(pointA, pointB));
    }

    /** isInRegion */
    @RepeatedTest(100)
    void regularRectInside() {
        // The boundaries of central campus
        double x1 = -3.192473; double y1 = 55.946233;
        double x2 = -3.184319; double y2 = 55.942617;

        // The coordinates in the counter-clockwise order expected
        LngLat[] coordinates = {
                new LngLat(x1, y1),
                new LngLat(x1, y2),
                new LngLat(x2, y2),
                new LngLat(x2, y1)
        };

        NamedRegion region = new NamedRegion("central", coordinates);
        LngLatHandler handler = new LngLatHandler();

        // Generate a random point inside the bounds
        double randomX = x2 + new Random().nextDouble() * (x1 - x2);
        double randomY = y2 + new Random().nextDouble() * (y1 - y2);

        LngLat point = new LngLat(randomX, randomY);

        // The random point should register as inside the boundaries
        assertTrue(handler.isInRegion(point, region));
    }

    @RepeatedTest(100)
    void regularRectOutside() {
        // The boundaries of central campus
        double x1 = -3.192473; double y1 = 55.946233;
        double x2 = -3.184319; double y2 = 55.942617;

        // The coordinates in the counter-clockwise order expected
        LngLat[] coordinates = {
                new LngLat(x1, y1),
                new LngLat(x1, y2),
                new LngLat(x2, y2),
                new LngLat(x2, y1)
        };

        NamedRegion region = new NamedRegion("central", coordinates);
        LngLatHandler handler = new LngLatHandler();

        // Generate a random point inside the bounds
        double randomX = x2 + new Random().nextDouble() * (x1 - x2);
        double randomY = y2 + new Random().nextDouble() * (y1 - y2);

        // Adding 1.1x the x length to an x coordinate within guaranteed generates a point outside etc.
        // Do this in each direction for rigor
        LngLat point1 = new LngLat(randomX + 1.1 * Math.abs(x1 - x2), randomY + 1.1 * Math.abs(x1 - x2));
        LngLat point2 = new LngLat(randomX + 1.1 * Math.abs(x1 - x2), randomY - 1.1 * Math.abs(x1 - x2));
        LngLat point3 = new LngLat(randomX - 1.1 * Math.abs(x1 - x2), randomY + 1.1 * Math.abs(x1 - x2));
        LngLat point4 = new LngLat(randomX - 1.1 * Math.abs(x1 - x2), randomY - 1.1 * Math.abs(x1 - x2));

        assertFalse(handler.isInRegion(point1, region));
        assertFalse(handler.isInRegion(point2, region));
        assertFalse(handler.isInRegion(point3, region));
        assertFalse(handler.isInRegion(point4, region));
    }

    private LngLat[] regularPolygonCreator(int numSides) {
        // Create a list to store the vertices of the polygon
        LngLat[] polygon = new LngLat[numSides];

        // Regular polygons can be constructed using equidistant points on a circle
        for (int n = 1; n <= numSides; n++) {
            double angle = (2 * Math.PI) / n;
            polygon[n-1] = (new LngLat(Math.cos(angle), Math.sin(angle)));
        }

        return polygon;
    }

    @Test
    void regularPolygonInside() {
        LngLatHandler handler = new LngLatHandler();

        // For each n-gon such that 3 <= n <= 10
        for (int n = 3; n <= 100; n++) {
            // Create the regular n-gon centred at (0,0) and determine if (0, 0) is recognised as a valid point
            NamedRegion region = new NamedRegion(String.format("%d-gon", n), regularPolygonCreator(n));
            boolean passed = handler.isInRegion(new LngLat(0, 0), region);

            // Write a console log and assert whether the test passes
            if (passed) {
                System.out.println(region.name() + ": Passed");
                assertTrue(true);
            } else {
                System.err.println(region.name() + ": Failed");
                fail();
            }
        }
    }

    /** nextPosition */
    @RepeatedTest(100)
    void hovering() {
        LngLatHandler handler = new LngLatHandler();

        double randomLng = -180 + new Random().nextDouble() * 360;
        double randomLat = -90 + new Random().nextDouble() * 180;

        LngLat oldPos = new LngLat(randomLng, randomLat);
        LngLat nextPos = handler.nextPosition(oldPos, 999);

        assertTrue(nextPos.equals(oldPos));
    }

    @RepeatedTest(100)
    void flying() {
        LngLatHandler handler = new LngLatHandler();

        double randomLng = -180 + new Random().nextDouble() * 360;
        double randomLat = -90 + new Random().nextDouble() * 180;
        double randomAngle = 22.5 * new Random().nextInt(16);

        LngLat oldPos = new LngLat(randomLng, randomLat);
        LngLat nextPos = handler.nextPosition(oldPos, randomAngle);

        // Need to convert to radians here
        // NGL the only way to check this one is just to put the implementation in
        double dLng = 0.00015 * Math.sin((randomAngle - 90) * Math.PI / 180);
        double dLat = 0.00015 * Math.cos((randomAngle - 90) * Math.PI / 180);

        assertTrue(nextPos.equals(new LngLat(oldPos.lng() + dLng, oldPos.lat() + dLat)));
    }
}

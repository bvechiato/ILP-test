package uk.ac.ed.inf;

import junit.framework.TestCase;
import org.junit.jupiter.api.Test;

import java.io.IOException;

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
}

package uk.ac.ed.inf;

import junit.framework.TestCase;
import org.junit.jupiter.api.RepeatedTest;
import uk.ac.ed.inf.ilp.data.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;


/*
 * ASSUMPTIONS
 *
 */
public class FlightPathTest extends TestCase {
    public static Random RANDOM = new Random();
    public static LngLatHandler HANDLER = new LngLatHandler();
    public static RESTAccess REST_ACCESS = new RESTAccess();

    /**
     * Generates random points until it's in a region
     *
     * @param region given region
     * @return random point in that region
     */
    public LngLat createLngLatInRegion(NamedRegion region) {
        LngLat[] vertices = region.vertices();
        Arrays.sort(vertices, Comparator
                .comparing(LngLat::lng)
                .thenComparing(LngLat::lat)
        );
        double lng;
        double lat;
        LngLat pos = new LngLat(0, 0);

        while (!HANDLER.isInRegion(pos, region)) {
            lng = RANDOM.nextDouble(vertices[0].lng(), vertices[3].lng());
            lat = RANDOM.nextDouble(vertices[0].lat(), vertices[3].lat());

            pos = new LngLat(lng, lat);
        }

        return pos;
    }

    @RepeatedTest(100)
    public void testPathFromRandomPositionInNoFlyZone() throws IOException, InterruptedException {
        NamedRegion[] noFlyZones = REST_ACCESS.getNoFlyZones(uk.ac.ed.inf.TestConstants.ENDPOINT);
        FlightPathCalculator pathCalculator = new FlightPathCalculator(uk.ac.ed.inf.TestConstants.CENTRAL_AREA, noFlyZones);

        LngLat pos = createLngLatInRegion(noFlyZones[RANDOM.nextInt(0, noFlyZones.length - 1)]);
        List<LngLat> path = pathCalculator.findPath(pos, uk.ac.ed.inf.TestConstants.APPLETON_TOWER);


        System.err.println("From [" + pos.lng() + ", " + pos.lat() + "]");
        if (!path.isEmpty()) {
            System.out.println("\tPath found: ");
            for (LngLat point : path) {
                System.out.println("[" + point.lng() + ", " + point.lat() + "],");
            }
        }

        assertTrue(path.isEmpty());
    }

    @RepeatedTest(100)
    public void testPathFromNullPosition() throws IOException, InterruptedException {
        NamedRegion[] noFlyZones = REST_ACCESS.getNoFlyZones(uk.ac.ed.inf.TestConstants.ENDPOINT);
        FlightPathCalculator pathCalculator = new FlightPathCalculator(uk.ac.ed.inf.TestConstants.CENTRAL_AREA, noFlyZones);

        List<LngLat> path = pathCalculator.findPath(null, uk.ac.ed.inf.TestConstants.APPLETON_TOWER);

        if (!path.isEmpty()) {
            System.out.println("\tPath found: ");
            for (LngLat point : path) {
                System.out.println("[" + point.lng() + ", " + point.lat() + "],");
            }
        }

        assertTrue(path.isEmpty());
    }

    public void createDroneGeojson(ArrayList<Flightpath> flightpaths, LocalDate date) throws JsonProcessingException {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // Adds the FeatureCollection to the geogson
        JsonObject geoJson = new JsonObject();
        geoJson.addProperty("type", "FeatureCollection");

        JsonArray features = new JsonArray();

        // Creates the feature
        JsonObject feature = new JsonObject();
        feature.addProperty("type", "Feature");

        // Creates the LineString
        JsonObject geometry = new JsonObject();
        geometry.addProperty("type", "LineString");

        JsonArray coordsArray = new JsonArray();

        // Adds each point in flightpath to a JsonArray
        for (Flightpath flightpath : flightpaths) {

            JsonArray point = new JsonArray();
            point.add(flightpath.fromLongitude());
            point.add(flightpath.fromLatitude());
            coordsArray.add(point);
        }

        // Adds the JsonArray geometry, adding it to the feature
        geometry.add("coordinates", coordsArray);
        feature.add("geometry", geometry);
        feature.add("properties", new JsonObject());

        // Adds the feature to the list of features, then adding that to the geogson
        features.add(feature);
        geoJson.add("features", features);

        writer("drone", gson.toJson(geoJson), date);
    }
}
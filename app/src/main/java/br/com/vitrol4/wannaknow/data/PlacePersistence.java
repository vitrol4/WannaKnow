package br.com.vitrol4.wannaknow.data;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by vitrol4 on 14/11/15.
 */
public class PlacePersistence extends LinkedList<Place> {

    public static List<Place> places = new LinkedList<>();

    public static void generateData() {
        PlacePersistence.places.add(new Place("Place 1", "Description Place 1", new Date()));
        PlacePersistence.places.add(new Place("Place 2", "Description Place 2", new Date()));
        PlacePersistence.places.add(new Place("Place 3", "Description Place 3", new Date()));
        PlacePersistence.places.add(new Place("Place 4", "Description Place 4", new Date()));
        PlacePersistence.places.add(new Place("Place 5", "Description Place 5", new Date()));
    }

}

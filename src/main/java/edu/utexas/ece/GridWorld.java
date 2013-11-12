package edu.utexas.ece;

import java.util.HashMap;

public class GridWorld {

    // Height and width of the grid
    private Integer height;
    private Integer width;

    // Components of grid
    private HashMap<Coordinate, IntersectionServer> intersectionsMap;
    private VehicleClient[] vehicles;

    // Constructor
    public GridWorld(Integer height, Integer width, Integer nVehicles) {
        // Set height and width of grid world
        this.height = height;
        this.width = width;

        // Initialize intersection map
        this.intersectionsMap = new HashMap<Coordinate, IntersectionServer>();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Coordinate coordinate = new Coordinate(i, j);
                this.intersectionsMap.put(coordinate, new IntersectionServer(coordinate));
            }
        }

        // Initialize vehicle clients
        this.vehicles = new VehicleClient[nVehicles];
        for (int i = 0; i < nVehicles; i++) {
            this.vehicles[i] = new VehicleClient(height, width);
            this.vehicles[i].generateRoute();
        }
    }
    
    public IntersectionServer getServer(Coordinate coordinate) {
        return intersectionsMap.get(coordinate);
    }

    public static void main(String[] args) {
        System.out.println("Hello World!" + IntersectionServer.DURATION_STRAIGHT);
        
        GridWorld gridWorld = new GridWorld(8, 8, 16);
        
    }
}

package edu.utexas.ece;

import java.util.HashMap;

public class GridWorld {
    // Height and width of the grid
    private Integer height;
    private Integer width;
    
    private GridFrame	frame;
    // Components of grid
    private HashMap<Coordinate, IntersectionServer> intersectionsMap;
    private VehicleClient[] vehicles;

    public GridWorld(Integer height, Integer width, Integer nVehicles) {
        // Set height and width of grid world
        this.height = height;
        this.width = width;
        
        // Create frame
        frame = new GridFrame(this.width, this.height);

        // Initialize intersection map
        
        this.intersectionsMap = new HashMap<Coordinate, IntersectionServer>();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Coordinate coordinate = new Coordinate(i, j);
                IntersectionServer intersection = new IntersectionServer(coordinate, this);
                this.intersectionsMap.put(coordinate, intersection);
                new Thread(intersection).start();
                
            }
        }

        // Initialize vehicle clients
        this.vehicles = new VehicleClient[nVehicles];
        for (int i = 0; i < nVehicles; i++) {
            this.vehicles[i] = new VehicleClient(this);
            this.vehicles[i].generateRoute(height, width);
            System.out.println(this.vehicles[i]);
            setVehicle(this.vehicles[i]);
        }
    }
    
    public synchronized void setVehicle(VehicleClient vehicle) {
        this.frame.setVehicle(vehicle);
    }
    
    public synchronized void setIntersection(IntersectionServer intersection) {
        this.frame.setIntersection(intersection);
    }
    
    public IntersectionServer getServer(Coordinate coordinate) {
        return intersectionsMap.get(coordinate);
    }

    public static void main(String[] args) {
        new GridWorld(2, 2, 16);
    }
}

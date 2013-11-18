package edu.utexas.ece;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class GridWorld {
    // Height and width of the grid
    private Integer height;
    private Integer width;
    
    private GridFrame	frame;
    // Components of grid
    private HashMap<String, IntersectionServer> intersectionsMap;
    private VehicleClient[] vehicles;
    
    // Number of vehicles on grid
    private int		nVehicles;
    
    private BufferedWriter	statisticsStream;
    
    private GridWorldMode mode;

    public Integer getWidth() {
        return width;
    }
    
    public Integer getHeight() {
        return height;
    }
    
    public GridWorld(Integer height, Integer width, Integer nVehicles, GridWorldMode mode) {
        // Set height and width of grid world
        this.height = height;
        this.width = width;
        this.nVehicles = nVehicles;
        this.mode = mode;
        
        // Open output file stream
        try {
			this.statisticsStream = new BufferedWriter(new FileWriter("statistic.csv"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        // Create frame
        frame = new GridFrame(this.width, this.height);

        // Initialize intersection map
        
        intersectionsMap = new HashMap<String, IntersectionServer>();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Coordinate coordinate = new Coordinate(i, j);
                IntersectionServer intersection;
                if (mode.equals(GridWorldMode.LA)) {
                    intersection = new IntersectionServerLA(coordinate, this);
                } else {
                    intersection = new IntersectionServer(coordinate, this);
                }
                
                intersectionsMap.put(coordinate.toString(), intersection);
                new Thread(intersection).start();
            }
        }

        // Initialize vehicle clients
        this.vehicles = new VehicleClient[nVehicles];
        for (int i = 0; i < nVehicles; i++) {
            VehicleClient vehicle = new VehicleClient(this);
            this.vehicles[i] = vehicle;
            if (mode.equals(GridWorldMode.LA)) {
                
            } else {
                vehicle.generateRoute();
            }
            System.out.println(this.vehicles[i]);
            setVehicle(this.vehicles[i]);
            new Thread(vehicle).start();
        }
    }
    
    public synchronized void removeVehicle(VehicleClient vehicle){
    	// Decrement vehicle count
    	this.nVehicles--;
    	// If all vehicles have reached their destination
    	if(this.nVehicles == 0){
    		
    		// Print out all information
    		try {
				this.statisticsStream.write("Vehicle,Velocity\n");
	    		
	    		for(int i = 0; i< vehicles.length; i++){
	    			this.statisticsStream.write(i + "," + vehicles[i].getVelocity() + "\n");
	    		}
	    		this.statisticsStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		System.exit(0);
    	}
    	this.frame.removeVehicle(vehicle);
    }
    
    public synchronized void setVehicle(VehicleClient vehicle) {
        this.frame.setVehicle(vehicle);
    }
    
    public synchronized void setIntersection(IntersectionServer intersection) {
        this.frame.setIntersection(intersection);
    }
    
    public IntersectionServer getServer(Coordinate coordinate) {
        return intersectionsMap.get(coordinate.toString());
    }

    public static void main(String[] args) {
        new GridWorld(2, 2, 50, GridWorldMode.LA);
    }
}

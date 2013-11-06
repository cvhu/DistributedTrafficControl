package edu.utexas.ece;

import java.util.HashMap;

public class GridWorld {

	// Height and width of the grid
	private Integer	height;
	private Integer	width;
	
	// Components of grid
	private HashMap<String, IntersectionServer>	map;
	private VehicleClient[]						vehicles;
	
	// Constructor
	public GridWorld(Integer height, Integer width, Integer nVehicles){
		// Set height and width of grid world
		this.height = height;
		this.width = width;
		
		// Initialize intersection map
		this.map = new HashMap<String, IntersectionServer>();
		for(int i = 0; i < width; i++){
			for(int j = 0; j < height; j++){
				String c = "(" + i + "," + j + ")";
				this.map.put(c, new IntersectionServer());
			}
		}
		
		// Initialize vehicle clients
		this.vehicles = new VehicleClient[nVehicles];
		for(int i = 0; i < nVehicles; i++){
			this.vehicles[i] = new VehicleClient(height, width);
		}
	}
	
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
    }
}

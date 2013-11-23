package edu.utexas.ece;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;

/**
 * @author cvhu
 *
 */
public class GridWorld {
    // Height and width of the grid
    private Integer height;
    private Integer width;
    
    private GridFrame	frame;
    // Components of grid
    private HashMap<String, IntersectionServer> intersectionsMap;
    private VehicleClient[] vehicles;
    
    private long start;
    
    private String title;
    
    // Number of vehicles on grid
    private int		nVehicles;
    
    private GridWorldMode mode;

    public Integer getWidth() {
        return width;
    }
    
    public Integer getHeight() {
        return height;
    }
    
    public GridWorld(Integer height, Integer width, Integer nVehicles, GridWorldMode mode) {
        title = String.format("stats_%dx%d_%d_%s.csv", width, height, nVehicles, mode);
        // Set height and width of grid world
        this.height = height;
        this.width = width;
        this.nVehicles = nVehicles;
        this.mode = mode;
        
        // Create frame
        frame = new GridFrame(this.width, this.height);
        frame.setTitle(title);
        
        start = System.currentTimeMillis();

        // Initialize intersection map
        
        intersectionsMap = new HashMap<String, IntersectionServer>();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Coordinate coordinate = new Coordinate(i, j);
                IntersectionServer intersection;
                switch (mode) {
                    case LA:
                        intersection = new IntersectionServerLA(coordinate, this);
                        break;
                    case WQS:
                        intersection = new IntersectionServerWQS(coordinate, this);
                        break;
                    case LAWQS:
                        intersection = new IntersectionServerLAWQS(coordinate, this);
                        break;
                    case DUMMY:
                    default:
                        intersection = new IntersectionServer(coordinate, this);
                        break;
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
            new Thread(vehicle).start();
        }
    }
    
    public GridWorldMode getMode() {
        return this.mode;
    }
    
    public synchronized void removeVehicle(VehicleClient vehicle) throws IOException{
    	// Decrement vehicle count
    	this.nVehicles--;
    	this.frame.removeVehicle(vehicle);
    	// If all vehicles have reached their destination
    	if(this.nVehicles == 0){
    	 // Open
    	    BufferedWriter statisticsWriter;
    	    BufferedWriter serverLoadWriter;
            try {
                File file = new File(title);
                File sfile = new File("server" + title);
                if (!file.exists()) {
                    file.createNewFile();
                }
                if (!sfile.exists()) {
                    sfile.createNewFile();
                }
                statisticsWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
                serverLoadWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(sfile)));
                statisticsWriter.write("Vehicle, Moves, Counter, Time, Velocity, CVelocity\n");
                serverLoadWriter.write("Server, Load Count\n");
                for (int i = 0; i < vehicles.length; i++) {
                    //System.out.println(i + "," + vehicles[i].printStats());
                    statisticsWriter.write(i + "," + vehicles[i].printStats() + "\n");
                }
                for (IntersectionServer intersection : intersectionsMap.values()) {
                    serverLoadWriter.write(String.format("'%s', %d\n", intersection.getCoordinate(), intersection.getLoadCount()));
                }
                System.out.printf("The %s algorithm took %d milliseconds\n", mode, System.currentTimeMillis() - start);
                statisticsWriter.write(String.format("The %s algorithm took %d milliseconds\n", mode, System.currentTimeMillis() - start));
                statisticsWriter.close();
                serverLoadWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                stopAll();
            }
    	}
    }
    
    public void stopAll() {
        for (IntersectionServer intersection : intersectionsMap.values()) {
            intersection.terminate();
        }
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
        for (final GridWorldMode mode : GridWorldMode.values()) {
            new Thread(new Runnable() {
                
                @Override
                public void run() {
                    new GridWorld(2, 2, 10, mode);
                    
                }
            }).start();
        }
    }
}

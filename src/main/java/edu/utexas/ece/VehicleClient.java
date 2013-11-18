package edu.utexas.ece;

import java.awt.Color;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class VehicleClient implements Runnable{

	private Color				color;					// Unique color
    private GridWorld 			gridWorld;				// World of vehicle
    
    private Boolean             destinationReached;     // Destination reached?
    private Direction           currentDirection;       // Current orientation
    private Coordinate          currentDestination;     // Current Destination
    private Coordinate          currentIntersection;    // Current Intersection
    private Coordinate          startPosition;
    private Coordinate 			finalDestination;		// End destination
    private Queue<Coordinate>   destinationQueue;       // Queue of destinations
    private VehicleAction 		pendingAction;
    private boolean sent = false;
    
    // Performance data
    private int		moves;
    private long	startTime;
    private long	stopTime;
    private double	velocity;

    // Constructor
    public VehicleClient(GridWorld gridWorld){
    	Random rand = new Random();
    	this.color = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
        destinationReached = false;
        this.gridWorld = gridWorld;
        this.destinationQueue = new LinkedList<Coordinate>();
        this.moves = 0;
        this.startTime = System.nanoTime();
        setStart();
        setDestination();
    }

    // Getters
	public Color getColor() {
		return color;
	}
    
    public Direction getCurrentDirection(){
        return currentDirection;
    }
    
    public Coordinate getCurrentIntersection(){
        return currentIntersection;
    }
    
    public Boolean getDestinationReached() {
        return destinationReached;
    }

    public Coordinate getCurrentDestination() {
        return currentDestination;
    }
    
    public Orientation getCurrentOrientation(){
    	return new Orientation(currentDirection, currentIntersection);
    }
    
    public int getMoves(){
    	return this.moves;
    }
    
    public double getVelocity(){
    	return this.velocity;
    }

    public void setStart() {
        this.startPosition = new Coordinate(randInt(0, gridWorld.getWidth() - 1), randInt(0, gridWorld.getHeight() - 1));
    }
    
    public void setDestination() {
        Coordinate pos = new Coordinate(randInt(0, gridWorld.getWidth() - 1), randInt(0, gridWorld.getHeight() - 1));
        while (pos.equals(this.startPosition)) {
            pos = new Coordinate(randInt(0, gridWorld.getWidth() - 1), randInt(0, gridWorld.getHeight() - 1));
        }
        this.finalDestination = pos;
    }

    public void generateRoute() {
        
        Coordinate destination = finalDestination;

        // Generate a random orientation
        Integer randOrientation = randInt(0, 3);
        switch (randOrientation) {
        // Face north
        case 0:
            // If we actually have to go south, then take a turn-around
            if ((currentIntersection.getY() > destination.getY())
                    && (currentIntersection.getX() == destination.getX())) {
                currentIntersection.setY(currentIntersection.getY() - 1);
                currentDirection = Direction.SOUTH;
            } else
                currentDirection = Direction.NORTH;
            break;
        // Face east
        case 1:
            // If we actually have to go west, then take a turn-around
            if ((currentIntersection.getX() > destination.getX())
                    && (currentIntersection.getY() == destination.getY())) {
                currentIntersection.setX(currentIntersection.getX() - 1);
                currentDirection = Direction.WEST;
            } else
                currentDirection = Direction.EAST;
            break;
        // Face west
        case 2:
            // If we actually have to go east, then take a turn-around
            if ((currentIntersection.getX() < destination.getX())
                    && (currentIntersection.getY() == destination.getY())) {
                currentIntersection.setX(currentIntersection.getX() + 1);
                currentDirection = Direction.EAST;
            } else
                currentDirection = Direction.WEST;
            break;
        // Face south
        case 3:
            // If we actually have to go north, then take a turn-around
            if ((currentIntersection.getY() < destination.getY())
                    && (currentIntersection.getX() == destination.getX())) {
                currentIntersection.setY(currentIntersection.getY() + 1);
                currentDirection = Direction.NORTH;
            } else
                currentDirection = Direction.SOUTH;
            break;
        // Should not happen
        default:
            System.err.println("ERROR: Invalid Orientation\n");
            System.exit(1);
        }

        // Generate a random walk towards the destination
        Coordinate c = new Coordinate(currentIntersection.getX(), currentIntersection.getY());

        while ((c.getX() != destination.getX())
                || (c.getY() != destination.getY())) {

            // Flip a coin
            Integer coin = randInt(0, 1);

            // 4 cases...

            // Perform a horizontal drive
            if (coin == 0) {
                // We are not already vertical to the destination
                if (c.getX() != destination.getX()) {
                    if (destination.getX() > c.getX())
                        c.setX(c.getX() + 1);
                    else
                        c.setX(c.getX() - 1);
                }
                // We are already vertical to the destination...
                else {
                    if (destination.getY() > c.getY())
                        c.setY(c.getY() + 1);
                    else
                        c.setY(c.getY() - 1);
                }
            }
            // Perform a vertical drive
            else {
                // We are not already horizontal to the destination
                if (c.getY() != destination.getY()) {
                    if (destination.getY() > c.getY())
                        c.setY(c.getY() + 1);
                    else
                        c.setY(c.getY() - 1);
                }
                // We are already horizontal to the destination...
                else {
                    if (destination.getX() > c.getX())
                        c.setX(c.getX() + 1);
                    else
                        c.setX(c.getX() - 1);
                }
            }

            // Add walk to destination queue
            this.destinationQueue.add(new Coordinate(c.getX(), c.getY()));
        }

        // Set our next destination
        if (this.destinationQueue.size() != 0)
            this.currentDestination = this.destinationQueue.peek();

        // If we have no future destination, then we've made it
        if (this.destinationQueue.size() == 0)
            this.destinationReached = true;
        
        System.out.printf("Destinations: %s\n", Arrays.asList(destinationQueue).toString());
    }
    
    
    public synchronized VehicleAction getAction() {
        pendingAction = currentIntersection.getAction(currentDestination, currentDirection);
        return pendingAction;
    }

    // Move vehicle to next destination
    public synchronized void handleRequestOkay() {
        System.out.println("request okay");
        this.moves++;
        
        // If we have no future destination, then we've made it
        if (this.destinationQueue.size() == 0){
            this.destinationReached = true;
            return;
        }
        
        //System.out.print(this.currentIntersection + " " + this.currentDirection.name() + " -> ");
        
        // Figure which way we're moving
        if(this.currentIntersection.getX() == this.currentDestination.getX()-1)
            this.currentDirection = Direction.EAST;
        else if(this.currentIntersection.getX() == this.currentDestination.getX()+1)
            this.currentDirection = Direction.WEST;
        else if(this.currentIntersection.getY() == this.currentDestination.getY()-1)
            this.currentDirection = Direction.NORTH;
        else if(this.currentIntersection.getY() == this.currentDestination.getY()+1)
            this.currentDirection = Direction.SOUTH;
        // Move to next destination
        this.currentIntersection = this.destinationQueue.remove();
        this.currentDestination = this.destinationQueue.peek();
        
        gridWorld.setVehicle(this);
        sent = false;
        
        if ((currentDestination == null) || finalDestination.equals(currentIntersection)) {
        	this.stopTime = System.nanoTime();
        	double timeSpent = (double)this.stopTime - (double)this.startTime;
        	this.velocity = ((double)this.moves)/(timeSpent/1000000000.0);
            gridWorld.removeVehicle(this);
        }
    }

    // Move vehicle based on action
    public synchronized void handleRequestOkayWithAction(Coordinate c){
    	this.moves++;
    	
    	// Figure out our current direction
    	if(c.getX() > this.currentIntersection.getX())
    		this.currentDirection = Direction.EAST;
    	else if(c.getX() < this.currentIntersection.getX())
    		this.currentDirection = Direction.WEST;
    	else if(c.getY() > this.currentIntersection.getY())
    		this.currentDirection = Direction.NORTH;
    	else if(c.getY() < this.currentIntersection.getY())
    		this.currentDirection = Direction.SOUTH;
    	this.currentIntersection  = new Coordinate(c.getX(), c.getY());
    	
    	// Display changes to vehicle
    	gridWorld.setVehicle(this);
        sent = false;
    	
    	// Check if we reached our destination
    	if(this.currentIntersection.equals(this.currentDestination)){
    		this.stopTime = System.nanoTime();
    		double timeSpent = (double)this.stopTime - (double)this.startTime;
        	this.velocity = ((double)this.moves)/(timeSpent/1000000000.0);
    		this.destinationReached = true;
    		gridWorld.removeVehicle(this);
    	}
    }
    
    // Random number generator
    private Integer randInt(Integer min, Integer max) {
        Random rand = new Random();
        Integer randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    public String toString(){
        //return String.format("%s\n%s\n%s\n%s\n", currentIntersection, currentDirection, currentDestination, destinationQueue);
        return currentIntersection.toString();
    }
    
    public static void main(String[] args) {
        VehicleClient vehicle = new VehicleClient(new GridWorld(2, 2, 10, GridWorldMode.DUMMY));
        vehicle.generateRoute();
        new Thread(vehicle).start();
    }

    @Override
    public void run() {
        while ((currentDestination != null) && !finalDestination.equals(currentIntersection)) {
            if (!sent) {
                //System.out.println(currentIntersection);
                IntersectionServer intersection = gridWorld.getServer(currentIntersection);
                try {
                    Thread.sleep(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                intersection.sendRequest(this);
                sent = true;
            } {
                System.out.println("sent" + this);
            }
        }
        
        // reached destination.
        gridWorld.removeVehicle(this);
    }

}

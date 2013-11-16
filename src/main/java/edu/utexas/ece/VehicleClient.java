package edu.utexas.ece;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class VehicleClient implements Runnable{

    private GridWorld gridWorld;
    private Boolean             destinationReached;     // Destination reached?
    private Direction           currentDirection;       // Current orientation
    private Coordinate          currentDestination;     // Current Destination
    private Coordinate          currentIntersection;    // Current Intersection
    private Coordinate finalDestination;
    private Queue<Coordinate>   destinationQueue;       // Queue of destinations
    private VehicleAction pendingAction;
    private boolean sent = false;

    // Constructor
    public VehicleClient(GridWorld gridWorld){
        destinationReached = false;
        this.gridWorld = gridWorld;
        this.destinationQueue = new LinkedList<Coordinate>();
    }

    // Getters
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

    // Initialize
    public void generateRoute(Integer height, Integer width) {

        // Generate random origin
        this.currentIntersection = new Coordinate(randInt(0, width - 1),
                randInt(0, height - 1));

        // Generate a random destination
//        Coordinate destination = new Coordinate(randInt(0, width - 1),
//                randInt(0, height - 1));
        finalDestination = new Coordinate(0, 0);
        
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
        VehicleClient vehicle = new VehicleClient(new GridWorld(2, 2, 10));
        vehicle.generateRoute(2, 2);
        new Thread(vehicle).start();
    }

    @Override
    public void run() {
        while (!finalDestination.equals(currentIntersection)) {
            if (!sent) {
                //System.out.println(currentIntersection);
                IntersectionServer intersection = gridWorld.getServer(currentIntersection);
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                intersection.sendRequest(this);
                sent = true;
            }
        }
        
        // reached destination.
        gridWorld.removeVehicle(this);
    }

}

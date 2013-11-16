package edu.utexas.ece;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class VehicleClient implements Runnable{

    private GridWorld gridWorld;
    private Boolean             destinationReached;     // Destination reached?
    private Direction           currentDirection;       // Current orientation
    private Coordinate          currentDestination;     // Current Destination
    private Coordinate          currentIntersection;    // Current Intersection
    private Queue<Coordinate>   destinationQueue;       // Queue of destinations

    // Constructor
    public VehicleClient(GridWorld gridWorld){
        destinationReached = false;
        this.gridWorld = gridWorld;
        this.destinationQueue = new LinkedList<Coordinate>();
    }

    // Getters
    public Direction getCurrentDirection(){
        return this.currentDirection;
    }
    
    public Coordinate getCurrentIntersection(){
        return this.currentIntersection;
    }
    
    public Boolean getDestinationReached() {
        return this.destinationReached;
    }

    public Coordinate getCurrentDestination() {
        return this.currentDestination;
    }
    
    public Orientation getCurrentOrientation(){
    	return new Orientation(this.currentDirection, this.currentIntersection);
    }

    // Initialize
    public void generateRoute(Integer height, Integer width) {

        // Generate random origin
        this.currentIntersection = new Coordinate(randInt(0, width - 1),
                randInt(0, height - 1));

        // Generate a random destination
        Coordinate destination = new Coordinate(randInt(0, width - 1),
                randInt(0, height - 1));

        // Generate a random orientation
        Integer randOrientation = randInt(0, 3);
        switch (randOrientation) {
        // Face north
        case 0:
            // If we actually have to go south, then take a turn-around
            if ((this.currentIntersection.getY() > destination.getY())
                    && (this.currentIntersection.getX() == destination.getX())) {
                this.currentIntersection.setY(this.currentIntersection.getY() - 1);
                this.currentDirection = Direction.SOUTH;
            } else
                this.currentDirection = Direction.NORTH;
            break;
        // Face east
        case 1:
            // If we actually have to go west, then take a turn-around
            if ((this.currentIntersection.getX() > destination.getX())
                    && (this.currentIntersection.getY() == destination.getY())) {
                this.currentIntersection.setX(this.currentIntersection.getX() - 1);
                this.currentDirection = Direction.WEST;
            } else
                this.currentDirection = Direction.EAST;
            break;
        // Face west
        case 2:
            // If we actually have to go east, then take a turn-around
            if ((this.currentIntersection.getX() < destination.getX())
                    && (this.currentIntersection.getY() == destination.getY())) {
                this.currentIntersection.setX(this.currentIntersection.getX() + 1);
                this.currentDirection = Direction.EAST;
            } else
                this.currentDirection = Direction.WEST;
            break;
        // Face south
        case 3:
            // If we actually have to go north, then take a turn-around
            if ((this.currentIntersection.getY() < destination.getY())
                    && (this.currentIntersection.getX() == destination.getX())) {
                this.currentIntersection.setY(this.currentIntersection.getY() + 1);
                this.currentDirection = Direction.NORTH;
            } else
                this.currentDirection = Direction.SOUTH;
            break;
        // Should not happen
        default:
            System.err.println("ERROR: Invalid Orientation\n");
            System.exit(1);
        }

        // Generate a random walk towards the destination
        Coordinate c = new Coordinate(this.currentIntersection);

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
            this.destinationQueue.add(new Coordinate(c));
        }

        // Set our next destination
        if (this.destinationQueue.size() != 0)
            this.currentDestination = this.destinationQueue.peek();

        // If we have no future destination, then we've made it
        if (this.destinationQueue.size() == 0)
            this.destinationReached = true;
    }

    // Move vehicle to next destination
    public void handleRequestOkay() {
        System.out.println("request okay");
        
        // If we have no future destination, then we've made it
        if (this.destinationQueue.size() == 0){
            this.destinationReached = true;
            return;
        }
        
        System.out.print(this.currentIntersection + " " + this.currentDirection.name() + " -> ");
        
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

//        System.out.println(this.currentIntersection + " " + this.currentDirection.name());

        // If we have no future destination, then we've made it
        if (this.destinationQueue.size() == 0)
            this.destinationReached = true;
        
        gridWorld.setVehicle(this);
    }

    // Random number generator
    private Integer randInt(Integer min, Integer max) {
        Random rand = new Random();
        Integer randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    public String toString(){
        return String.format("%s\n%s\n%s\n%s\n", currentIntersection, currentDirection, currentDestination, destinationQueue);
    }
    
    public static void main(String[] args) {
        VehicleClient vehicle = new VehicleClient(new GridWorld(2, 2, 10));
        vehicle.generateRoute(2, 2);
        new Thread(vehicle).start();
    }

    @Override
    public void run() {
        while (!destinationQueue.isEmpty()) {
//            System.out.println(currentIntersection);
            IntersectionServer intersection = gridWorld.getServer(currentIntersection);
            intersection.sendRequest(this);
        }
    }

}

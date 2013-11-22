package edu.utexas.ece;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class VehicleClient implements Runnable{

	private Color				color;					// Unique color
    private GridWorld 			gridWorld;				// World of vehicle
   
    private Direction           currentDirection;       // Current orientation
    private Coordinate          currentDestination;     // Current Destination
    private Coordinate          currentIntersection;    // Current Intersection
    private Coordinate          startPosition;
    private Coordinate 			finalDestination;		// End destination
    private ArrayList<Coordinate>   destinationQueue;       // Queue of destinations
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
        this.gridWorld = gridWorld;
        this.destinationQueue = new ArrayList<Coordinate>();
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
    
    public void generatePath() {
        Coordinate currentPos = startPosition;
        while (!currentPos.equals(finalDestination)) {
            currentPos = currentPos.moveOneStepTowards(finalDestination);
            destinationQueue.add(currentPos);
        }
        System.out.printf("Generated Path: %s\n Start: %s\n End: %s\n", Arrays.asList(destinationQueue).toString(), startPosition, finalDestination);
    }
    
    public void generateDestination() {
        destinationQueue.add(finalDestination);
    }
    
    public synchronized VehicleAction getAction() {
        pendingAction = currentIntersection.getAction(currentDestination, currentDirection);
        return pendingAction;
    }

    // Move vehicle based on the recommended coordinate.
    public synchronized void handleRequestOkay(Coordinate c){
    	this.moves++;
    	this.currentDirection = currentIntersection.getDirectionTo(c);
    	this.currentIntersection  = c;
    	gridWorld.setVehicle(this);
    	if (!destinationQueue.isEmpty()) {
    	    this.destinationQueue.remove(0);
    	}
    	if (!destinationQueue.isEmpty()) {
    	    this.currentDestination = destinationQueue.get(0);
    	}
        sent = false;
    }
    
    // Random number generator
    private Integer randInt(Integer min, Integer max) {
        Random rand = new Random();
        Integer randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    public String toString(){
        return String.format("Vehicle: %s-%s -> %s\n Destinations: %s\n", currentIntersection, currentDirection, currentDestination, Arrays.asList(destinationQueue));
    }
    
    public static void main(String[] args) {
        VehicleClient vehicle = new VehicleClient(new GridWorld(2, 2, 10, GridWorldMode.DUMMY));
        vehicle.generatePath();
        new Thread(vehicle).start();
    }

    @Override
    public void run() {
        currentIntersection = startPosition;
        currentDestination = destinationQueue.get(0);
        currentDirection = currentIntersection.getDirectionTo(currentDestination);
        gridWorld.setVehicle(this);
        while (!destinationQueue.isEmpty()) {
            if (!sent && !currentIntersection.equals(currentDestination)) {
                IntersectionServer intersection = gridWorld.getServer(currentIntersection);
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println(this);
                intersection.sendRequest(this);
                sent = true;
            } else {
//                System.out.println("sent " + this);
            }
        }
        this.stopTime = System.nanoTime();
        double timeSpent = (double)this.stopTime - (double)this.startTime;
        this.velocity = ((double)this.moves)/(timeSpent/1000000000.0);
        gridWorld.removeVehicle(this);
    }

}

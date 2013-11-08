package edu.utexas.ece;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class VehicleClient {

	// Knowledge of grid
	private Integer height;
	private Integer width;

	// Destination reached?
	private Boolean destinationReached;
	// Current Destination
	private Coordinate currentDestination;
	// Current Intersection
	private Coordinate currentIntersection;
	// Queue of destinations
	private Queue<Coordinate> destinationQueue;

	// Constructor
	public VehicleClient(Integer height, Integer width) {
		this.height = height;
		this.width = width;
		this.destinationQueue = new LinkedList<Coordinate>();
	}

	// Getters
	public Boolean getDestinationReached() {
		return this.destinationReached;
	}

	public Coordinate getCurrentDestination() {
		return this.currentDestination;
	}

	// Initialize
	public void generateRoute() {

		// Generate random origin
		this.currentIntersection = new Coordinate(randInt(0, this.width - 1),
				randInt(0, this.height - 1));

		// Generate a random destination
		Coordinate destination = new Coordinate(randInt(0, this.width - 1),
				randInt(0, this.height - 1));

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
	public void move() {
		// If we have no future destination, then we've made it
		if (this.destinationQueue.size() == 0)
			this.destinationReached = true;

		// Move to next destination
		this.currentDestination = this.destinationQueue.remove();

		// If we have no future destination, then we've made it
		if (this.destinationQueue.size() == 0)
			this.destinationReached = true;
	}

	// Random number generator
	private Integer randInt(Integer min, Integer max) {
		Random rand = new Random();
		Integer randomNum = rand.nextInt((max - min) + 1) + min;
		return randomNum;
	}

	public static void main(String[] args) {
		VehicleClient vehicle = new VehicleClient(8, 8);
		vehicle.generateRoute();
	}

}

package edu.utexas.ece;

import java.util.ArrayList;
import java.util.HashMap;

public class IntersectionServer implements Runnable{
    
    // The duration of the corresponding states in milliseconds.
    public static final int DURATION_STRAIGHT = 4000;
    public static final int DURATION_LEFT = 2000;
    
    private Coordinate coordinate;
    private HashMap<Direction, ArrayList<VehicleClient>> requestsMap;
    private HashMap<IntersectionState, Integer> durationsMap;
    private IntersectionState currentState;
    private GridWorld gridWorld;
    
    public IntersectionServer(Coordinate coordinate, GridWorld gridWorld) {
        this.gridWorld = gridWorld;
        this.coordinate = coordinate;
        requestsMap = new HashMap<Direction, ArrayList<VehicleClient>>();
        durationsMap = new HashMap<IntersectionState, Integer>();
        init();
    }
    
    public void poll() {
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                while (true) {
                    loopStates();
                }
            }
        }).run();
    }
    
    public void loopStates() {
        for (IntersectionState state : IntersectionState.values()) {
            currentState = state;
            gridWorld.setIntersection(this);
            System.out.printf("Processing state: %s\n", currentState);
            long start = System.currentTimeMillis();
            long end = start + durationsMap.get(state);
            while (System.currentTimeMillis() < end) {
                processQueue();
            }
        }
    }
    
    public IntersectionState getState() {
        return currentState;
    }
    
    public Coordinate getCoordinate() {
        return coordinate;
    }
    
    public void processQueue() {
        switch (currentState) {
            case VERTICAL_STRAIGHT:
                popRequests(Direction.NORTH, true);
                popRequests(Direction.SOUTH, true);
            case VERTICAL_LEFT:
                popRequests(Direction.NORTH, false);
                popRequests(Direction.SOUTH, false);
            case HORIZONTAL_STRAIGHT:
                popRequests(Direction.EAST, true);
                popRequests(Direction.WEST, true);
            case HORIZONTAL_LEFT:
                popRequests(Direction.EAST, false);
                popRequests(Direction.WEST, false);
            default:
                //System.out.println("Invalid server state");
        }
    }
    
    public synchronized void popRequests(Direction direction, boolean straight) {
        ArrayList<VehicleClient> requests = requestsMap.get(direction);
        
        if (!requests.isEmpty()) {
            VehicleClient vehicle = requests.get(0);
            boolean valid;
            if (straight) {
                valid = coordinate.isStraight(vehicle.getCurrentDestination(), vehicle.getCurrentDirection());
            } else {
                valid = coordinate.isLeft(vehicle.getCurrentDestination(), vehicle.getCurrentDirection());
            }
            if (valid) {
                vehicle.handleRequestOkay();
                requests.remove(0);
                requestsMap.put(direction, requests);
            }
        }
    }
    
    public void init() {
        for (Direction direction : Direction.values()) {
            requestsMap.put(direction, new ArrayList<VehicleClient>());
        }
        for (IntersectionState state : IntersectionState.values()) {
            switch (state) {
                case HORIZONTAL_LEFT:
                case VERTICAL_LEFT:
                    durationsMap.put(state, DURATION_LEFT);
                case HORIZONTAL_STRAIGHT:
                case VERTICAL_STRAIGHT:
                    durationsMap.put(state, DURATION_STRAIGHT);
                default:
            }
        }
    }
    
    public synchronized void sendRequest(VehicleClient client) {
        Direction direction = client.getCurrentDirection();
        ArrayList<VehicleClient> requests = requestsMap.get(direction);
        requests.add(client);
        requestsMap.put(direction, requests);
    }
    
    public static void main(String[] argv) {
        new IntersectionServer(new Coordinate(2, 4), new GridWorld(2, 2, 4));
    }

    @Override
    public void run() {
        poll();
    }
}

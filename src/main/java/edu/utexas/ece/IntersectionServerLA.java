package edu.utexas.ece;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class IntersectionServerLA extends IntersectionServer{
    
    public IntersectionServerLA(Coordinate coordinate, GridWorld gridWorld) {
        super(coordinate, gridWorld);
    }
    
    public synchronized void popRequests(Direction direction, boolean straight) {
        ArrayList<VehicleClient> requests = requestsMap.get(direction);
        if (requests.size() > 0) {
            System.out.println("pop: " + Arrays.asList(requests));
        }
        if (!requests.isEmpty()) {
            VehicleClient vehicle = requests.get(0);
            VehicleAction action = vehicle.getAction();
            if ((vehicle.getCurrentDestination() == null) || (action == null)) {
                System.out.println("Before pop" + Arrays.asList(requests));
                vehicle.handleRequestOkay();
                requests.remove(0);
                System.out.println("After pop" + Arrays.asList(requests));
                requestsMap.put(direction, requests);
                return;
            }
            System.out.println("popping queue: " + vehicle.toString());
//            vehicle.handleRequestOkay();
            boolean valid;
            if (straight) {
                valid = (action == VehicleAction.GO_STRAIGHT) || (action == VehicleAction.TURN_RIGHT);
            } else {
                valid = (action == VehicleAction.TURN_LEFT);
            }
            if (valid) {
                System.out.println("Before pop" + Arrays.asList(requests));
                vehicle.handleRequestOkay();
                requests.remove(0);
                System.out.println("After pop" + Arrays.asList(requests));
                requestsMap.put(direction, requests);
            } else {
                System.out.println("Invalid request");
            }
        } else {    
//            System.out.println("empty queue");
        }
    }
}

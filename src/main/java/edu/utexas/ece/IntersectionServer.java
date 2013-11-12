package edu.utexas.ece;

import java.util.ArrayList;

public class IntersectionServer {
    
    public static final int DURATION_STRAIGHT = 4;
    public static final int DURATION_LEFT = 2;
    
    private Coordinate coordinate;
    private ArrayList<VehicleClient> requestsQueue;
    private int availableSlots = 0;
    private IntersectionState currentState;
    
    public IntersectionServer(Coordinate coordinate) {
        this.coordinate = coordinate;
        requestsQueue = new ArrayList<VehicleClient>();
        availableSlots = DURATION_STRAIGHT;
        currentState = IntersectionState.VERTICAL_STRAIGHT;
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                
            }
        }).run();
    }
    
    public void sendRequest(VehicleClient client) {
        switch (currentState) {
            case VERTICAL_STRAIGHT:
                if (availableSlots > 0) {
                    if (coordinate.isStraight(client.getCurrentDestination(), client.getCurrentDirection())) {
                        availableSlots--;
                        client.handleRequestOkay();
                    } else {
                        requestsQueue.add(client);
                    }
                } else {
                    currentState = IntersectionState.VERTICAL_LEFT;
                    availableSlots = DURATION_LEFT;
                    requestsQueue.add(client);
                }
            case VERTICAL_LEFT:
                if (availableSlots > 0) {
                    if (coordinate.isLeft(client.getCurrentDestination(), client.getCurrentDirection())) {
                        availableSlots--;
                        client.handleRequestOkay();
                    } else {
                        requestsQueue.add(client);
                    }
                } else {
                    currentState = IntersectionState.HORIZONTAL_STRAIGHT;
                    availableSlots = DURATION_STRAIGHT;
                    requestsQueue.add(client);
                }
            case HORIZONTAL_STRAIGHT:
                if (availableSlots > 0) {
                    if (coordinate.isStraight(client.getCurrentDestination(), client.getCurrentDirection())) {
                        availableSlots--;
                        client.handleRequestOkay();
                    } else {
                        requestsQueue.add(client);
                    }
                } else {
                    currentState = IntersectionState.HORIZONTAL_LEFT;
                    availableSlots = DURATION_LEFT;
                    requestsQueue.add(client);
                }
            case HORIZONTAL_LEFT:
                if (availableSlots > 0) {
                    if (coordinate.isLeft(client.getCurrentDestination(), client.getCurrentDirection())) {
                        availableSlots--;
                        client.handleRequestOkay();
                    } else {
                        requestsQueue.add(client);
                    }
                } else {
                    currentState = IntersectionState.VERTICAL_STRAIGHT;
                    availableSlots = DURATION_STRAIGHT;
                    requestsQueue.add(client);
                }
            default:
                System.out.println("Invalid server state");
        }
    }
}

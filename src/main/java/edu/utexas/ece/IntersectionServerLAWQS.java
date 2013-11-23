package edu.utexas.ece;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

public class IntersectionServerLAWQS extends IntersectionServer{
    
    public IntersectionServerLAWQS(Coordinate coordinate, GridWorld gridWorld) {
        super(coordinate, gridWorld);
        currentState = IntersectionState.VERTICAL_STRAIGHT;
    }
    
    @Override
    public void loopStates() {
        currentState = getWQSState();
        gridWorld.setIntersection(this);
        long start = System.currentTimeMillis();
        long end = start + durationsMap.get(currentState);
        while (System.currentTimeMillis() < end) {
            processQueue();
            incrementQWeight();
        }
    }
    
    public IntersectionState getWQSState() {
        switch (currentState) {
            case VERTICAL_STRAIGHT:
                return IntersectionState.VERTICAL_LEFT;
            case HORIZONTAL_STRAIGHT:
                return IntersectionState.HORIZONTAL_LEFT;
            default:
                break;
        }
        List<IntersectionState> candidates = new ArrayList<IntersectionState>();
        int maxWeight = Integer.MIN_VALUE;
        for (IntersectionState state : IntersectionState.values()) {
            int weight = 0;
            switch (state) {
                case VERTICAL_LEFT:
                case VERTICAL_STRAIGHT:
                    weight += getQWeight(Direction.NORTH);
                    weight += getQWeight(Direction.SOUTH);
                    break;
                case HORIZONTAL_LEFT:
                case HORIZONTAL_STRAIGHT:
                    weight += getQWeight(Direction.EAST);
                    weight += getQWeight(Direction.WEST);
                    break;
                default:
                    break;
            }
            if (weight > maxWeight) {
                candidates.clear();
                candidates.add(state);
                maxWeight = weight;
            } else if (weight == maxWeight) {
                candidates.add(state);
            }
        }
        Random rand = new Random();
        return candidates.get(rand.nextInt(candidates.size()));
    }
    
    public synchronized int getQWeight(Direction direction) {
        int weight = 0;
        for (VehicleClient vehicle : requestsMap.get(direction)) {
            weight += vehicle.getRoundsWaited();
        }
        return weight;
    }
    
    public synchronized void incrementQWeight() {
        for (Entry<Direction, ArrayList<VehicleClient>> entry : requestsMap.entrySet()) {
            for (VehicleClient vehicle : entry.getValue()) {
                vehicle.incrementRoundsWaited();
            }
        }
    }
    
    @Override
    public synchronized void processVehicle(VehicleClient vehicle) {
        
        List<Coordinate> candidates = new ArrayList<Coordinate>();
        List<Coordinate> actions = new ArrayList<Coordinate>();
        Coordinate destination = vehicle.getCurrentDestination();
        Integer minSize = Integer.MAX_VALUE;
        
        if (destination.getX() > coordinate.getX()) {
            candidates.add(coordinate.getRight());
        } else if (destination.getX() < coordinate.getX()) {
            candidates.add(coordinate.getLeft());
        } else {
            candidates.add(coordinate.getLeft());
            candidates.add(coordinate.getRight());
        }
        
        if (destination.getY() > coordinate.getY()) {
            candidates.add(coordinate.getTop());
        } else if (destination.getY() < coordinate.getY()) {
            candidates.add(coordinate.getBottom());
        } else {
            candidates.add(coordinate.getTop());
            candidates.add(coordinate.getBottom());
        }
        
        for (Coordinate candidate : candidates) {
            IntersectionServer intersection = gridWorld.getServer(candidate);
            if (intersection != null) {
                Integer candidateSize = intersection.getRequestsSize(coordinate.getDirectionTo(candidate));
                if (candidateSize < minSize) {
                    minSize = candidateSize;
                    actions.clear();
                    actions.add(candidate);
                } else if (candidateSize == minSize) {
                    actions.add(candidate);
                }
            }
        }
        vehicle.handleRequestOkay(actions.get(0));
    }


}

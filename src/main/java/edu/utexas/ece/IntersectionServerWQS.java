package edu.utexas.ece;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

public class IntersectionServerWQS extends IntersectionServer{
    
    public IntersectionServerWQS(Coordinate coordinate, GridWorld gridWorld) {
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

}

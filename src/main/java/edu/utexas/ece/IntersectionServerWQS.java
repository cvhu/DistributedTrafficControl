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
    
    

}

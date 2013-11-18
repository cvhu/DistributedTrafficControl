package edu.utexas.ece;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IntersectionServerLA extends IntersectionServer{
    
    public IntersectionServerLA(Coordinate coordinate, GridWorld gridWorld) {
        super(coordinate, gridWorld);
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

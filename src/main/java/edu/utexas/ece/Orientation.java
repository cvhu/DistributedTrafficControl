package edu.utexas.ece;

public class Orientation {
    
    private Direction   direction;
    private Coordinate  coordinate;
    
    // Constructor
    public Orientation(Direction direction, Coordinate coordinate){
        this.direction = direction;
        this.coordinate = new Coordinate(coordinate);
    }
    
    public Direction getDirection(){
        return this.direction;
    }
    
    public Coordinate getCoordinate(){
        return this.coordinate;
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}

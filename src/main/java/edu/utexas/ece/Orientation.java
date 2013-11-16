package edu.utexas.ece;

public class Orientation {
    
    private Direction   direction;
    private Coordinate  coordinate;
    
    // Constructor
    public Orientation(Direction direction, Coordinate coordinate){
        this.direction = direction;
        this.coordinate = new Coordinate(coordinate);
    }
    
    // Copy constructor
    public Orientation(Orientation o){
        this.direction = o.getDirection();
        this.coordinate = new Coordinate(o.getCoordinate());
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

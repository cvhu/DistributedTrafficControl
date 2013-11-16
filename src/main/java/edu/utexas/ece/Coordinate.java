package edu.utexas.ece;

public class Coordinate {

    private Integer x;
    private Integer y;

    public Coordinate(Integer x, Integer y) {
        this.x = x;
        this.y = y;
    }

    public Integer getX() {
        return this.x;
    }

    public Integer getY() {
        return this.y;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public String toString() {
        return "(" + x.toString() + "," + y.toString() + ")";

    }
    
    public VehicleAction getAction(Coordinate coordinate, Direction direction) {
        if (isStraight(coordinate, direction)) {
            return VehicleAction.GO_STRAIGHT;
        }
        if (isLeft(coordinate, direction)) {
            return VehicleAction.TURN_LEFT;
        }
        if (isRight(coordinate, direction)) {
            return VehicleAction.TURN_RIGHT;
        }
        return null;
    }
    
    public boolean isVertical(Coordinate coordinate) {
        boolean valid = false;
        try {
            valid = (x == coordinate.getX()) && (Math.abs(y - coordinate.getY()) == 1);
        } catch (NullPointerException e) {
            System.err.printf("intersection: %s destination: %s\n", this, coordinate);
        }
        return valid; 
    }
    
    public boolean equals(Coordinate coordinate) {
        return (x == coordinate.getX()) && (y == coordinate.getY());
    }
    
    public boolean isHorizontal(Coordinate coordinate) {
        boolean valid = false;
        try {
            valid = (y == coordinate.getY()) && (Math.abs(x - coordinate.getX()) == 1);
        } catch (NullPointerException e) {
            System.err.printf("intersection: %s destination: %s\n", this, coordinate);
        }
        return valid;
    }
    
    public boolean isStraight(Coordinate coordinate, Direction direction) {
        boolean directionVertical = (direction == Direction.NORTH) || (direction == Direction.SOUTH);
        boolean directionHorizontal = (direction == Direction.EAST) || (direction == Direction.WEST);
        System.out.printf("isStraight: %s %s %s %s %s %s %s\n", isVertical(coordinate), directionVertical, isHorizontal(coordinate), directionHorizontal, this, direction, coordinate);
        return (isVertical(coordinate) && directionVertical) || (isHorizontal(coordinate) && directionHorizontal);
    }
    
    public boolean isLeft(Coordinate coordinate, Direction direction) {
        boolean northLeft = (direction == Direction.NORTH) && (isHorizontal(coordinate) && (coordinate.getX() == (x - 1)));
        boolean southLeft = (direction == Direction.SOUTH) && (isHorizontal(coordinate) && (coordinate.getX() == (x + 1)));
        boolean eastLeft = (direction == Direction.EAST) && (isVertical(coordinate) && (coordinate.getY() == (y + 1)));
        boolean westLeft = (direction == Direction.WEST) && (isVertical(coordinate) && (coordinate.getY() == (y - 1)));
        System.out.printf("isLeft: %s %s %s %s %s %s %s\n", northLeft, southLeft, eastLeft, westLeft, this, direction, coordinate);
        return northLeft || southLeft || eastLeft || westLeft;
    }
    
    public boolean isRight(Coordinate coordinate, Direction direction) {
        boolean northRight = (direction == Direction.NORTH) && (isHorizontal(coordinate) && (coordinate.getX() == (x + 1)));
        boolean southRight = (direction == Direction.SOUTH) && (isHorizontal(coordinate) && (coordinate.getX() == (x - 1)));
        boolean eastRight = (direction == Direction.EAST) && (isVertical(coordinate) && (coordinate.getY() == (y - 1)));
        boolean westRight = (direction == Direction.WEST) && (isVertical(coordinate) && (coordinate.getY() == (y + 1)));
        System.out.printf("isRight: %s %s %s %s %s %s %s\n", northRight, southRight, eastRight, westRight, this, direction, coordinate);
        return northRight || southRight || eastRight || westRight;
    }
}

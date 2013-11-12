package edu.utexas.ece;

public class Coordinate {

    private Integer x;
    private Integer y;

    public Coordinate(Integer x, Integer y) {
        this.x = x;
        this.y = y;
    }

    public Coordinate(Coordinate c) {
        this.x = c.getX();
        this.y = c.getY();
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
    
    public boolean isVertical(Coordinate coordinate) {
        return (this.x == coordinate.getX()) && (Math.abs(this.y - coordinate.getY()) == 1);
    }
    
    public boolean isHorizontal(Coordinate coordinate) {
        return (this.y == coordinate.getY()) && (Math.abs(this.x - coordinate.getX()) == 1);
    }
    
    public boolean isStraight(Coordinate coordinate, Direction direction) {
        boolean directionVertical = (direction == Direction.NORTH) || (direction == Direction.SOUTH);
        boolean directionHorizontal = (direction == Direction.EAST) || (direction == Direction.WEST);
        return (isVertical(coordinate) && directionVertical) || (isHorizontal(coordinate) && directionHorizontal);
    }
    
    public boolean isLeft(Coordinate coordinate, Direction direction) {
        boolean northLeft = (direction == Direction.NORTH) && (isHorizontal(coordinate) && (coordinate.getX() == (x - 1)));
        boolean southLeft = (direction == Direction.SOUTH) && (isHorizontal(coordinate) && (coordinate.getX() == (x + 1)));
        boolean eastLeft = (direction == Direction.EAST) && (isVertical(coordinate) && (coordinate.getY() == (y + 1)));
        boolean westLeft = (direction == Direction.WEST) && (isVertical(coordinate) && (coordinate.getY() == (y - 1)));
        return northLeft || southLeft || eastLeft || westLeft;
    }
}

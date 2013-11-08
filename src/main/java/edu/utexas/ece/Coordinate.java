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
}

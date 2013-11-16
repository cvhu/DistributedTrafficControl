package edu.utexas.ece;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import javax.swing.*;

public class GridPanel extends JPanel {
	
    // Grid dimensions
    int width;
    int height;
    
    // Required information
    HashMap<VehicleClient, Orientation> vehicles;
    IntersectionServer[][]              intersections;
    
    // Intersection light images
    private BufferedImage vertical_left;
    private BufferedImage vertical_straight;
    private BufferedImage horizontal_left;
    private BufferedImage horizontal_straight;

    public GridPanel(Integer width, Integer height) {
        // Set width and height
        this.width = width;
        this.height = height;
        
        this.vehicles = new HashMap<VehicleClient, Orientation>();
        this.intersections = new IntersectionServer[this.width][this.height];
        
        // Open intersection light images
        try {
			this.vertical_left = ImageIO.read(new File("VERTICAL_LEFT.png"));
			this.vertical_straight = ImageIO.read(new File("VERTICAL_STRAIGHT.png"));
			this.horizontal_left = ImageIO.read(new File("HORIZONTAL_LEFT.png"));
			this.horizontal_straight = ImageIO.read(new File("HORIZONTAL_STRAIGHT.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void paint(Graphics g) {

        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;

        // Set rendering hints
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHints(rh);

        // Get dimensions
        Dimension size = getSize();
        double w = size.getWidth();
        double h = size.getHeight();

        

        // Draw the roads
        int ytop;
        int ybottom;
        int xleft;
        int xright;
        
        final float dash1[] = {10.0f};
        final  BasicStroke dashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
                                                    BasicStroke.JOIN_MITER,
                                                    10.0f, dash1, 0.0f);

        // Draw roads going north and south
        g2d.setColor(Color.black);
        ytop = (int) (h / (this.height + 1)) - 64;
        ybottom = (int) ((h / (this.height + 1)) * (this.height)) + 64;
        for (int i = 0; i < this.width; i++) {
            xleft = (int) ((w / (this.width + 1)) * (i + 1) - 12);
            xright = (int) ((w / (this.width + 1)) * (i + 1) + 12);
            
            // Use 1 pixel stroke
            g2d.setStroke(new BasicStroke(1));
            g2d.drawLine(xleft, ytop, xleft, ybottom);
            g2d.drawLine(xright, ytop, xright, ybottom);
            
            // Draw dashed lane separator
            g2d.setStroke(dashed);
            g2d.drawLine((xleft+xright)/2, ytop, (xleft+xright)/2, ybottom);
        }

        // Draw roads going east and west
        xleft = (int) (w / (this.width + 1)) - 64;
        xright = (int) ((w / (this.width + 1)) * (this.width)) + 64;
        for (int i = 0; i < this.height; i++) {
            ytop = (int) ((h / (this.height + 1)) * (i + 1) - 12);
            ybottom = (int) ((h / (this.height + 1)) * (i + 1) + 12);
            
            // Use 1 pixel stroke
            g2d.setStroke(new BasicStroke(1));
            g2d.drawLine(xleft, ytop, xright, ytop);
            g2d.drawLine(xleft, ybottom, xright, ybottom);
            
            // Draw dashed lane separator
            g2d.setStroke(dashed);
            g2d.drawLine(xleft, (ytop+ybottom)/2, xright, (ytop+ybottom)/2);
        }
        // Revert stroke
        g2d.setStroke(new BasicStroke(1));
        
        
        // Draw each intersection
        for (int i = 0; i < this.width; i++) {
            for (int j = 0; j < this.height; j++) {                
                
                // Calculate center location of intersection
                double x = (w / (this.width + 1)) * (i + 1);
                double y = (h / (this.height + 1)) * (this.height - j);
                
                // If we have information on the intersection...
                if(intersections[i][j] != null){
                    // Get the state of the intersection
                    IntersectionState	state = intersections[i][j].getState();
                    switch(state){
	                    case VERTICAL_LEFT:
	                    	g2d.drawImage(this.vertical_left, (int)(x-12), (int)(y-12), null);
	                    	break;
	                    case VERTICAL_STRAIGHT:
	                    	g2d.drawImage(this.vertical_straight, (int)(x-12), (int)(y-12), null);
	                    	break;
	                    case HORIZONTAL_LEFT:
	                    	g2d.drawImage(this.horizontal_left, (int)(x-12), (int)(y-12), null);
	                    	break;
	                    case HORIZONTAL_STRAIGHT:
	                    	g2d.drawImage(this.horizontal_straight, (int)(x-12), (int)(y-12), null);
	                    	break;
	                    default:
	                    	System.err.println("ERROR: Paint invalid server state");
	                    	System.exit(1);
                    }
                }
            }
        }
        
        
        // Draw each vehicle
        g2d.setColor(Color.blue);
        int[][][] numVehicles = new int[this.width][this.height][4];
        for(VehicleClient v : this.vehicles.keySet()) {
           
            // Get x, y coordinates and direction
            Integer xCoordinate = v.getCurrentIntersection().getX();
            Integer yCoordinate = v.getCurrentIntersection().getY();
            int directionInt = 0;
            if(v.getCurrentDirection() == Direction.NORTH)
                directionInt = 0;
            else if(v.getCurrentDirection() == Direction.EAST)
                directionInt = 1;
            else if(v.getCurrentDirection() == Direction.SOUTH)
                directionInt = 2;
            else if(v.getCurrentDirection() == Direction.WEST)
                directionInt = 3;
            else{
                System.err.println("ERROR: Paint invalid direction");
                System.exit(1);
            }
            
            // Calculate center location of intersection
            double x = (w / (this.width + 1)) * (xCoordinate + 1);
            double y = (h / (this.height + 1)) * (this.height - yCoordinate);
            
            switch(directionInt){
            	// Going north
            	case 0:
            		// Move to right lane
            		x += 2;
            		// Move vehicle into position
            		y += 16 + numVehicles[xCoordinate][yCoordinate][directionInt]*10;
            		break;
            	// Going east
            	case 1:
            		// Move vehicle into position
            		x -= 22 + numVehicles[xCoordinate][yCoordinate][directionInt]*10;;
            		// Move to right lane
            		y += 2;
            		break;
            	// Going south
            	case 2:
            		// Move to right lane
            		x -= 10;
            		// Move vehicle into position
            		y -= 22 + numVehicles[xCoordinate][yCoordinate][directionInt]*10;
            		break;
            	// Going west
            	case 3:
            		// Move vehicle into position
            		x += 16 + numVehicles[xCoordinate][yCoordinate][directionInt]*10;;
            		// Move to right lane
            		y -= 10;
            		break;
            }
            // Draw vehicle
            g2d.fillOval((int)x, (int)y, 8, 8);
            
            // Add vehicle to total number
            numVehicles[xCoordinate][yCoordinate][directionInt]++;
        }
        
    }

    // Set vehicle
    public void setVehicle(VehicleClient v){
        this.vehicles.put(v, new Orientation(v.getCurrentOrientation()));
    }
    
    public void setIntersection(IntersectionServer intersection){
        // TODO
        // Get x and y coordinates and assign in matrix
    	Integer x = intersection.getCoordinate().getX();
    	Integer y = intersection.getCoordinate().getY();
    	this.intersections[x][y] = intersection;
    }
    
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}

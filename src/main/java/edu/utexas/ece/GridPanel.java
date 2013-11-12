package edu.utexas.ece;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

public class GridPanel extends JPanel {
	
    // Grid dimensions
    int width;
    int height;
    
    // Intersection light images
    private BufferedImage vertical_left;
    private BufferedImage vertical_straight;
    private BufferedImage horizontal_left;
    private BufferedImage horizontal_straight;

    public GridPanel(Integer width, Integer height) {
        // Set width and height
        this.width = width;
        this.height = height;
        
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

        System.out.println(w + " " + h);

        g2d.setStroke(new BasicStroke(1));

        // Draw each intersection
        for (int i = 0; i < this.width; i++) {
            for (int j = 0; j < this.height; j++) {
            	
            	// Calculate center location of intersection
                double x = (w / (this.width + 1)) * (i + 1);
                double y = (h / (this.height + 1)) * (j + 1);
                
	            g2d.drawImage(this.horizontal_left, (int)(x-12), (int)(y-12), null);  
            }
        }

        // Draw the roads
        int ytop;
        int ybottom;
        int xleft;
        int xright;

        // Draw roads going north and south
        g2d.setColor(Color.black);
        ytop = (int) (h / (this.height + 1)) - 48;
        ybottom = (int) ((h / (this.height + 1)) * (this.height)) + 48;
        for (int i = 0; i < this.width; i++) {
            xleft = (int) ((w / (this.width + 1)) * (i + 1) - 12);
            xright = (int) ((w / (this.width + 1)) * (i + 1) + 12);
            g2d.drawLine(xleft, ytop, xleft, ybottom);
            g2d.drawLine(xright, ytop, xright, ybottom);
        }

        // Draw roads going east and west
        xleft = (int) (w / (this.width + 1)) - 48;
        xright = (int) ((w / (this.width + 1)) * (this.width)) + 48;
        for (int i = 0; i < this.height; i++) {
            ytop = (int) ((h / (this.height + 1)) * (i + 1) - 12);
            ybottom = (int) ((h / (this.height + 1)) * (i + 1) + 12);
            g2d.drawLine(xleft, ytop, xright, ytop);
            g2d.drawLine(xleft, ybottom, xright, ybottom);
        }

    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}

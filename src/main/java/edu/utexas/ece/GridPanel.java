package edu.utexas.ece;

import java.awt.*;
import java.awt.geom.Ellipse2D;

import javax.swing.*;

public class GridPanel extends JPanel {

	// Grid dimensions
	int		width;
	int		height;

	public GridPanel(Integer width, Integer height){

		// Set width and height
		this.width = width;
		this.height = height;
	}

	public void paint(Graphics g){
		
		super.paint(g);
		Graphics2D g2d = (Graphics2D)g;

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
		for(int i = 0; i < this.width; i++){
			for(int j = 0; j < this.height; j++){
				g2d.setColor(Color.black);
				double x = (w/(this.width + 1)) * (i+1);
				double y = (h/(this.height + 1)) * (j+1);
				Ellipse2D.Double circle = new Ellipse2D.Double(x, y, 16, 16);
				g2d.fill(circle);
			}
		}
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

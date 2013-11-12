package edu.utexas.ece;

import java.awt.Color;

import javax.swing.*;

public class GridFrame extends JFrame {

    private Integer 	width;
    private Integer 	height;
    
    private GridPanel 	gridPanel;

    public GridFrame(Integer width, Integer height) {

        // Set width and height
        this.width = width;
        this.height = height;
        // Create new panel
        this.gridPanel = new GridPanel(this.width, this.height);

        // Set panel attributes
        this.add(this.gridPanel);
        this.setTitle("Grid World");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(this.width * 128, this.height * 128);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setResizable(false);
    }
    

    public static void main(String[] args) {
    	GridFrame frame = new GridFrame(8,8);
    	
    	frame.repaint();

    }

}

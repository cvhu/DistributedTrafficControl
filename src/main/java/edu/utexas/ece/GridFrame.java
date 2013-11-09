package edu.utexas.ece;

import javax.swing.*;

public class GridFrame extends JFrame {

    private Integer width;
    private Integer height;

    public GridFrame(Integer width, Integer height) {

        // Set width and height
        this.width = width;
        this.height = height;

        // Set panel attributes
        this.add(new GridPanel(this.width, this.height));
        this.setTitle("Grid World");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(this.width * 128, this.height * 128);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setResizable(false);
    }

    public static void main(String[] args) {
        new GridFrame(8, 8);

    }

}

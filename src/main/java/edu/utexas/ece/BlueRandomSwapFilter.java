package edu.utexas.ece;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.RGBImageFilter;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

public class BlueRandomSwapFilter extends RGBImageFilter{

	// Color
	private Color c;
	
	public BlueRandomSwapFilter(Color c){
		this.c = c;
	}
	
	@Override
	public int filterRGB(int x, int y, int rgb) {
		int newColor = 0xff000000 ^ (c.getRed() << 16) ^ (c.getGreen() << 8) ^ (c.getBlue());
		return ~newColor | rgb;
	}
	
	
	public static void main(String[] args) {

	}



}

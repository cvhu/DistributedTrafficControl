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
		//if(rgb == 0x0000ff00){
		int newColor = (c.getRed() << 24) | (c.getGreen() << 16) | (c.getBlue() << 8);
			return (rgb & 0xffff00ff) | newColor;
		//}
		//else
			//return rgb;
	}
	
	
	public static void main(String[] args) {

	}



}

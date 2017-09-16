package core;

import java.util.HashMap;
import java.util.Map;

import lib_duke.AugmentedPixel;
import lib_duke.ImageResource;
import lib_duke.Pixel;

public class CannyEdgeDetection {

	private ImageResource toProcess;
	private Map<Pixel, AugmentedPixel> toAugmented = new HashMap<Pixel, AugmentedPixel>();
	
	public CannyEdgeDetection (ImageResource toProcess){
		this.toProcess = toProcess;
	}
	
	static class Sobel{
		private static final int [][] verKernel = new int [][]{
			{1,0,-1},
			{2,0,-2},
			{1,0,-1}
		};
		private static final int [][] horKernel = new int [][]{
			{1,2,1},
			{0,0,0},
			{-1,-2,-1}
		};
	}
}

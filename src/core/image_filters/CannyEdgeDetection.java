package core.image_filters;

import java.util.HashMap;
import java.util.Map;

import ifaces.IImageProcesor;
import lib_duke.AugmentedPixel;
import lib_duke.ImageResource;
import lib_duke.Pixel;

public class CannyEdgeDetection implements IImageProcesor {

	private ImageResource toProcess;
	private Map<Pixel, AugmentedPixel> toAugmented = new HashMap<Pixel, AugmentedPixel>();
	
	public CannyEdgeDetection (ImageResource in, ImageResource out){
		this.toProcess = in;
	}
	
	static class Sobel{
		private static final int [][] VER_KERNEL = new int [][]{
			{1,0,-1},
			{2,0,-2},
			{1,0,-1}
		};
		private static final int [][] HOR_KERNEL = new int [][]{
			{1,2,1},
			{0,0,0},
			{-1,-2,-1}
		};
		private static final byte BORDER_SOBEL = 1;
	}
	
	static class Gaussian{
		private static final int [][] GAUS_KERNEL_5 = new int[][]{
			{2,4,5,4,2},
			{4,9,12,9,4},
			{5,12,15,12,5},
			{4,9,12,9,4},
			{2,4,5,4,2}
		};
		private static final double NORMALIZE_GAUS_5 = 1/159;
		private static final byte BORDER_GAUS = 2;
		
		
	}

	@Override
	public void proces(ImageResource in, ImageResource out) {
		// TODO Auto-generated method stub
		
	}
}

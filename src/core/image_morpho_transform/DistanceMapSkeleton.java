package core.image_morpho_transform;

import core.image_filters.BaseFilter;
import core.image_morpho_transform.Skeleton.SkeletonUtils;
import ifaces.I_ColorScheme;
import lib_duke.ImageResource;
import lib_duke.Pixel;

public class DistanceMapSkeleton extends BaseFilter implements I_ColorScheme {

	public DistanceMapSkeleton(ImageResource in, int border, boolean w, boolean d, int... intArgs) {
		super(in.getWidth(), in.getHeight(), border, w, d, 5, intArgs);
		this.in = in;
		//TODO not very nice...
		Skeleton sk = new Skeleton(in, border, w, d, intArgs);
		utils = sk.new SkeletonUtils(threshold);
	}

	private SkeletonUtils utils;
	private ImageResource in;
	private int threshold = redScheme[0];
	private final int [] xIncr = new int[] {0,1,1,1,0,-1,-1,-1};
	private final int [] yIncr = new int[] {1,1,0,-1,-1,-1,0,1};
	private int [] distances = new int[8];

	@Override
	public void doYourThing() {
		skeletonize();
	}

	/**
	 * 
	 * @param utils
	 */
	private void skeletonize() {
		int clipped = 0;
		int over30 = 0;
		int green;
		for(Pixel p : in.pixels()) {
			if(utils.isForeground(p)) {
				//find closest edge pixel and store distance to it to green channel of p
				for(int i = 0; i < 8; i++) {
					distances[i] = getDistToEdge(p, xIncr[i], yIncr[i]);
				}
				green = minDist(distances);
				if(green > 30) { over30 ++; }
				if(green > 255) { green = 255; clipped ++; }
				
				//testPrintPixelDistances(distances, green);
				
				p.setGreen(green);
			}
		}
		System.err.println("SKELETONIZE: over 30: " + over30);
		System.err.println("SKELETONIZE: clipped: " + clipped);
		assert(clipped == 0);
		
		// PUT VECTORS IN BLUE CHANNEL
		
		// vector representation:
		// 0 : 0
		// + : 100
		// - : 200
		// >>>> scanline direction
		// , iterate from ind 1
		// ++++---0
		// 12343211
		
		//horizontal
		Pixel curr;
		Pixel last;
		for (int y = 0; y < in.getHeight(); y++) {
			for (int x = 1; x < in.getWidth(); x++) {
				curr = in.getPixel(x, y);
				last = in.getPixel(x - 1, y);
				int currDist = curr.getGreen();
				int lastDist = last.getGreen();
				if((currDist == lastDist))continue;
				if (currDist > lastDist) curr.setBlue(100); //+
				if (currDist < lastDist) curr.setBlue(200); //-
			}
		}
		for (int x = 0; x < in.getWidth(); x++) {
			for (int y = 1; y < in.getHeight(); y++) {
				curr = in.getPixel(x, y);
				last = in.getPixel(x, y - 1);
				int currDist = curr.getGreen();
				int lastDist = last.getGreen();
				if((currDist == lastDist))continue;
				if (currDist > lastDist) curr.setBlue(100); //+
				if (currDist < lastDist) curr.setBlue(200); //-
			}
		}
		// PUT VECTORS IN BLUE CHANNEL
	}
	
	private int getDistToEdge(Pixel from, int incrX, int incrY) {
		Pixel current;
		int x = from.getX();
		int y = from.getY();
		int dist = 0;
		while(utils.isWithinImageBounds(x, y)) {
			current = in.getPixel(x, y);
			if(utils.isBackground(current)) break;
			dist ++;
			x += incrX; y += incrY;
		}
		return dist;
	}
	
	private int minDist(int [] distances) {
		int min = Integer.MAX_VALUE;
		for (int dist : distances) {
			if(dist < min) min = dist;
		}
		return min;
	}
	
	@SuppressWarnings("unused")
	private void testPrintPixelDistances(int [] dist, int min) {
		System.out.println("pixel_________________________________________________\n");
		for(int i : dist) System.out.print(", " + i);
		System.out.println("\n " + min);
		System.out.println("______________________________________________________");		
	}
}

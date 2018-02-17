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
		int green;
		for(Pixel p : in.pixels()) {
			if(utils.isForeground(p)) {
				//find closest edge pixel and store distance to it to green channel of p
				for(int i = 0; i < 8; i++) {
					distances[i] = getDistToEdge(p, xIncr[i], yIncr[i]);
				}
				green = minDist(distances);
				p.setGreen(green);
			}
		}
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
}

package core.image_morpho_transform;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import core.image_filters.BaseFilter;
import core.image_morpho_transform.Skeleton.SkeletonUtils;
import ifaces.I_ColorScheme;
import lib_duke.ImageResource;
import lib_duke.Pixel;

public class DistanceMapSkeleton extends BaseFilter implements I_ColorScheme {

	public DistanceMapSkeleton(ImageResource in, int border, boolean w, boolean d, int... intArgs) {
		super(in.getWidth(), in.getHeight(), border, w, d, 5, intArgs);
		this.in = in;
		Skeleton sk = new Skeleton(in, border, w, d, intArgs);
		utils = sk.new SkeletonUtils(threshold);
	}

	private SkeletonUtils utils;
	private ImageResource in;
	private int threshold = redScheme[0];
	private int maxDistComputed = -1;
	
	@Override
	public void doYourThing() {
		computeDistanceMap();
		//distance map is now in in - green channel
		//maxDistComputed is set
		skeletonize();
	}

	private void computeDistanceMap() {
		
		blackenG(in);
		blackenB(in);
		
		int clipped = 0;
		int over30 = 0;
		int green;
		for(Pixel pivot : in.pixels()) {
			if(utils.isForeground(pivot)) {
				//find closest edge pixel and store distance to it to green channel of pivot
				green = getDistToClosestFeaturePixel(pivot);
				if(green > 30) { over30 ++; }		
				if(green > 255) { green = 255; clipped ++; }
				if(green > maxDistComputed) maxDistComputed = green;
				pivot.setGreen(green);
			}
		}
		System.err.println("SKELETONIZE: over 30: " + over30);
		System.err.println("SKELETONIZE: clipped: " + clipped);
		assert(clipped == 0);
	}
	
	
	
	
	private boolean nDefined = false;
	private List <Integer> distances = new LinkedList<Integer>();
	private static final int DIST_TO_FEATURE_PIX_SEARCH_BOUNDARY = 1000;
	int searchSpan;
	
	private int getDistToClosestFeaturePixel(Pixel pivot) {
		
		assert(utils.isForeground(pivot));
		
		nDefined = false;
		distances.clear();
		searchSpan = DIST_TO_FEATURE_PIX_SEARCH_BOUNDARY;
		
		int i = 1;
		Pixel c1 = null;
		Pixel c2 = null;
		Pixel c3 = null;
		Pixel c4 = null;
		Pixel iterated;
		int halfI;
				
		while (i < searchSpan){
			
			halfI = i / 2;
			c1 = in.getPixel(
					utils.clipXToImageBounds(pivot.getX() - halfI), 
					utils.clipYToImageBounds(pivot.getY() - halfI));
			c2 = in.getPixel(
					utils.clipXToImageBounds(pivot.getX() + halfI),
					utils.clipYToImageBounds(pivot.getY() - halfI));
			c3 = in.getPixel(
					utils.clipXToImageBounds(pivot.getX() + halfI),
					utils.clipYToImageBounds(pivot.getY() + halfI));
			c4 = in.getPixel(
					utils.clipXToImageBounds(pivot.getX() - halfI),
					utils.clipYToImageBounds(pivot.getY() + halfI));
			
			for(int f1 = c1.getX(); f1 < c2.getX(); f1 ++){
				iterated = in.getPixel(f1, c1.getY());
				addDistanceIfPixBackground(pivot, iterated);
			}
			for(int f2 = c2.getY(); f2 < c3.getY(); f2 ++){
				iterated = in.getPixel(c2.getX(), f2);
				addDistanceIfPixBackground(pivot, iterated);
			}
			for(int f3 = c3.getX(); f3 > c4.getX(); f3 --){
				iterated = in.getPixel(f3, c3.getY());
				addDistanceIfPixBackground(pivot, iterated);
			}
			for(int f4 = c4.getY(); f4 > c1.getY(); f4 --){
				iterated = in.getPixel(c4.getX(), f4);
				addDistanceIfPixBackground(pivot, iterated);
			}	
			i += 2;
		}
		Integer minCartesianDist = Integer.MAX_VALUE;
		for(Integer dist : distances) if(dist < minCartesianDist) minCartesianDist = dist;
		return (int) minCartesianDist;
	}
	private void addDistanceIfPixBackground(Pixel pivot, Pixel iterated){
		if(utils.isBackground(iterated)){
			if(nDefined == false){
				setBoundary(pivot, iterated);
			}
			distances.add(utils.cartesianDistPixels(pivot, iterated));
		}
	}
	private void setBoundary (Pixel pivot, Pixel background){
			searchSpan = 3 * (2 * (utils.cartesianDistPixels(pivot, background)));
			nDefined = true;
	}

	private void skeletonize(){

		int dist = 1;
		Set<Pixel> remove = new HashSet<Pixel>();
		Set<Pixel> removed = new HashSet<Pixel>();
		while(dist <= maxDistComputed){//maxDistComputed
			remove.clear();
			for(Pixel p : in.pixels()){
				if(p.getGreen() > 0 && p.getGreen() <= dist){
					remove.add(p);
				}
			}
			for(int background = 7; background > 0; background --){
				removed.clear();
				for(Pixel p : remove){
					if(utils.getNmbOfBackgroundPix(p) >= background && utils.isRemovable(p)){
						p.setRed(lightGreenScheme[0]);
						p.setGreen(lightGreenScheme[1]);
						p.setBlue(lightGreenScheme[2]);
						removed.add(p);
					}
				}
				for(Pixel p : removed){
					remove.remove(p);
				}
			}
		dist ++;
		}
	}

	@SuppressWarnings("unused")
	private boolean blackenR(ImageResource resource){
		boolean nonZeroExisted = false;
		for(Pixel p : resource.pixels()){
			if(p.getRed() != 0) nonZeroExisted = true;
			p.setRed(0);
		}
		return nonZeroExisted;
	}
	private boolean blackenG(ImageResource resource){
		boolean nonZeroExisted = false;
		for(Pixel p : resource.pixels()){
			if(p.getGreen() != 0) nonZeroExisted = true;
			p.setGreen(0);
		}
		return nonZeroExisted;
	}
	private boolean blackenB(ImageResource resource){
		boolean nonZeroExisted = false;
		for(Pixel p : resource.pixels()){
			if(p.getBlue() != 0) nonZeroExisted = true;
			p.setBlue(0);
		}
		return nonZeroExisted;
	}
}

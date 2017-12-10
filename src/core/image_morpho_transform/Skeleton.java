package core.image_morpho_transform;

import java.util.ArrayList;
import java.util.List;

import core.image_filters.BaseFilter;
import core.utils.RoundIteratorOfPixels;
import ifaces.I_ColorScheme;
import lib_duke.ImageResource;
import lib_duke.Pixel;

// research
// http://homepages.inf.ed.ac.uk/rbf/HIPR2/thin.htm
// https://dsp.stackexchange.com/questions/2523/connecting-edges-detected-by-an-edge-detector
// implementation
// http://cgm.cs.mcgill.ca/~godfried/teaching/projects97/azar/skeleton.html -- Hilditch's Algorithm

public class Skeleton extends BaseFilter implements I_ColorScheme {

	public Skeleton(ImageResource in, int border, boolean w, boolean d, int... intArgs) {
		super(in.getWidth(), in.getHeight(), border, w, d, 5, intArgs);
		this.in = in;
	}

	private ImageResource in;
	private int threshold = redScheme[0];
	
	@Override
	public void doYourThing() {
		SkeletonUtils utils = new SkeletonUtils(threshold); // threshold
		skeletonize(utils);
	}

	/**
	 * 
	 * @param utils
	 */
	private void skeletonize(SkeletonUtils utils) {

		if (debug) System.out.println("NEW TASK " + Thread.currentThread().toString());

		Pixel current;
		List<Pixel> applicants = new ArrayList<Pixel>();
		int removed = 0;

		int count = 0;
	
		int[] fore = new int[] { 3 };//3 hilditch settings 1
		int[] back = new int[] { 1 };//1 hilditch settings 1
		int[] foreMax = new int[] { 7000 };//1000 hilditch settings 7
		int[] backMax = new int[] { 1000 };

		for (int i = 0; i < 1; i++) { // stages ??

			utils.setForeThresh(fore[i]);
			utils.setBackThresh(back[i]);
			utils.setForeMax(foreMax[i]);
			utils.setBackMax(backMax[i]);

			while (count < 300) {// precaution...
				for (int absX = widthFrom; absX < widthTo; absX++) {
					for (int absY = heightFrom; absY < heightTo; absY++) {

						current = in.getPixel(absX, absY);

						if (current.getRed() == redScheme[0] &&
							utils.isApplicable(current)){
							applicants.add(current);
						}
					}
				}
				
				for (Pixel p : applicants) {
					if(utils.isRemovable(p)){
						p.setRed(lightGreenScheme[0]);
						p.setGreen(lightGreenScheme[1]);
						p.setBlue(lightGreenScheme[2]);
						removed++;
					}
				}

				if (removed == 0)
					break;
				else if (debug)
					System.out.println("toRemove = applicable: " + applicants.size() + "\nremoved: " + removed
							+ "\n___________________________________________");
				applicants.clear();
				count++;
				removed = 0;
			} // while
		}
	}
	
	public int getThresholdForeBack(){return threshold;}

	/**
	 * 
	 */
	public class SkeletonUtils {

		private final RoundIteratorOfPixels riop;
		private int foregroundColorThreshold; // value of red channel
		private byte fGround,bGround;
		private boolean removable;
		private Pixel pivot = null;

		private int backThresh = 100;// 0,2
		private int foreThresh = 100;// 1,3
		private int backMax = 100;
		private int foreMax = 100;

		public SkeletonUtils(int foregroundColor) {
			riop = new RoundIteratorOfPixels(in);
			foregroundColorThreshold = foregroundColor;
		}

		boolean isForeground(Pixel p) {
			return (p.getRed() >= foregroundColorThreshold);
		}

		boolean isBackground(Pixel p) {
			return (p.getRed() < foregroundColorThreshold);
		}

		/**
		 * 
		 * @param pivot
		 * @return
		 */
		public boolean isRemovable(Pixel pivot) {
			checkMyPivot(pivot);
			return removable;
		}

		/**
		 * 
		 * @param pivot
		 */
		private void checkMyPivot(Pixel pivot) {
			if (this.pivot != pivot) {
				this.pivot = pivot;
				computeRemovable();
			}
		}

		/**
		 * 
		 * @param pivot
		 * @return
		 */
		public boolean isApplicable(Pixel pivot) {
			//Neighbors test
			byte background = 0,foreground = 0;
			riop.setPixelToCheckAround(pivot);
			for (Pixel aroundPivot : riop) {
				if (isForeground(aroundPivot)) {
					foreground++;
				} else if (isBackground(aroundPivot))
					background++;
			}
			
			boolean neighbors = ((int) background > backThresh && (int) foreground > foreThresh && (int) background < backMax
					&& (int) foreground < foreMax);
			
			//hilditch algo specific stuff
			
			//____  p2  ____
			// p8  pivot p4 
			//____  p6  ____
			
			//Pixel p2 = in.getPixel(pivot.getX(), pivot.getY() - 1);
			//Pixel p8 = in.getPixel(pivot.getX() - 1, pivot.getY());
			//Pixel p4 = in.getPixel(pivot.getX() + 1, pivot.getY());
			//Pixel p6 = in.getPixel(pivot.getX(), pivot.getY() + 1);
			
			//ensures that 2-pixel wide vertical lines do not get completely eroded by the algorithm.
			boolean vert = true; //isBackground(p2) || isBackground(p4) || isBackground(p8);
			//ensures that 2-pixel wide horizontal lines do not get completely eroded by the algorithm.
			boolean hori = true; //isBackground(p2) || isBackground(p4) || isBackground(p6);
			
			return neighbors && vert && hori;
		}
		
		/**
		 * 
		 * @return
		 */
		ImageResource getImageResource(){
			return in;
		}

		LocalyDisconnectTest ldt = new LocalyDisconnectTest(this);
		
		/**
		 * 
		 */
		private void computeRemovable() {
			removable = (false == ldt.locallyDisconnects(pivot));
		}

		/**
		 * 
		 * @param pIn
		 * @param pivot
		 * @return
		 */
		boolean isWithinEnvelope(Pixel pIn, Pixel pivot) {
			boolean x = (pIn.getX() > pivot.getX() - 2 && pIn.getX() < pivot.getX() + 2);
			boolean y = (pIn.getY() > pivot.getY() - 2 && pIn.getY() < pivot.getY() + 2);
			return x && y;
		}
		
		/**
		 * 
		 */
		@SuppressWarnings("unused")
		private void printValues() {
			System.out.println("________________________");
			System.out.println("fGround " + fGround);
			System.out.println("bGround " + bGround);
			System.out.println("________________________");
		}

		private void setBackThresh(int backThresh) {
			this.backThresh = backThresh;
		}

		private void setForeThresh(int foreThresh) {
			this.foreThresh = foreThresh;
		}

		private void setBackMax(int backMax) {
			this.backMax = backMax;
		}

		private void setForeMax(int foreMax) {
			this.foreMax = foreMax;
		}

	}//SkeletonUtils
}

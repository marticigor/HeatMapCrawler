package core.image_filters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import core.utils.RoundIteratorOfPixels;
import ifaces.I_ColorScheme;
import lib_duke.ImageResource;
import lib_duke.Pixel;

// http://homepages.inf.ed.ac.uk/rbf/HIPR2/thin.htm
// https://dsp.stackexchange.com/questions/2523/connecting-edges-detected-by-an-edge-detector

public class Skeleton extends BaseFilter implements I_ColorScheme {

    public Skeleton(ImageResource in ,int border, boolean w, boolean d, int...intArgs) {
        super( in .getWidth(), in .getHeight(), border, w, d, 5, intArgs);
        this.in = in ;
    }

    private ImageResource in;
    
    @Override
    public void doYourThing() {
        int threshold = redScheme[0];
        SkeletonUtils utils = new SkeletonUtils(threshold); //threshold
        skeletonize(utils);
    }

    private void skeletonize(SkeletonUtils utils) {

        //Consider all pixels on the boundaries of foreground regions (i.e. foreground points that have
        //at least one background neighbor). Delete any such point that has more
        //than one foreground neighbor, as long as doing so does not locally disconnect
        //(i.e. split into two) the region containing that pixel. Iterate until convergence.
        
    	if(debug) System.out.println("NEW TASK");
    	
        Pixel current;
        List < Pixel > toRemove = new ArrayList < Pixel > ();

        int count = 0;
        int removed = 0;
        
        int[] fore = new int []{3};//3
        int[] back = new int []{1};//2

		for (int i = 0; i < 1; i++) {

			utils.setForeThresh(fore[i]);
			utils.setBackThresh(back[i]);
			
			while (count < 200) {// precaution

				for (int absX = widthFrom; absX < widthTo; absX++) {
					for (int absY = heightFrom; absY < heightTo; absY++) {

						current = in.getPixel(absX, absY);

						if (utils.isApplicable(current)) {
							toRemove.add(current);
						}
					}
				}

				for (Pixel p : toRemove) {
					if (utils.isRemovable(p) && p.getRed() != 0) {
						p.setRed(lightGreenScheme[0]);
						p.setGreen(lightGreenScheme[1]);
						p.setBlue(lightGreenScheme[2]);
						removed++;
					}
				}

				if (removed == 0)
					break;
				else if (debug)
					System.out.println("toRemove = applicable: " + toRemove.size() + "\nremoved: " + removed
							+ "\n___________________________________________");

				toRemove.clear();

				removed = 0;
				count++;
			} // while

		}
    }

    /**
     * 
     */
    private class SkeletonUtils {

        private final RoundIteratorOfPixels riop;
        private int foregroundColorThreshold; //value of red channel
        private byte foreground, background, up, down;
        private boolean removable;
        private boolean applicable;
        private boolean fullySurr;
        private Pixel pivot = null;
        Map <Pixel,Set<Pixel>> pixelToDisjointSet;
        Set <Pixel> disjointSet;
        List <Pixel> foregroundPixels;
        
        private int backThresh = 100;//0,2
        private int foreThresh = 100;//1,3

        public SkeletonUtils(int foregroundColor) {
            riop = new RoundIteratorOfPixels( in );
            this.foregroundColorThreshold = foregroundColor;
        }
        
		private void countValues() {

            riop.setPixelToCheckAround(pivot);
            foreground = 0;
            background = 0;
            up = 0;
            down = 0;
            
            removable = false;
            applicable = false;
            fullySurr = false;

            pixelToDisjointSet = new HashMap<Pixel,Set<Pixel>>();
            foregroundPixels = new LinkedList<Pixel>();
            
            for (Pixel aroundPivot : riop) {
                if (isForeground(aroundPivot)) {
                	foreground++;
                    disjointSet = new HashSet<Pixel>();
                    disjointSet.add(aroundPivot);
                    pixelToDisjointSet.put(aroundPivot, disjointSet);
                    foregroundPixels.add(aroundPivot);//keep order
                }
                else if (isBackground(aroundPivot)) background++;
            }
                        
            for (Pixel p : foregroundPixels){
            	
            	riop.setPixelToCheckAround(p);
            	
            	Set<Pixel> pSet = pixelToDisjointSet.get(p);
            	
            	//System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
            	for (Pixel pIn : riop){
            		if( !isWithinEnvelope(pIn, pivot) ) {
            			//System.out.println("NOT WITHIN ENVELOPE");
            			continue;
            			}
            		
            		if( isForeground(pIn ) &&
            			pIn != pivot
            		  ) {
            			//join disjoint sets (all pInSet members into pSet)
            			Set<Pixel> pInSet = pixelToDisjointSet.get(pIn);
            			for (Object iter : pInSet) pSet.add((Pixel)iter);
            		}
            	}
            }

            int maxSize = 0;
            //System.out.println("_______________________________________________________________");
            //System.out.println("foregroundPixels size: " + foregroundPixels.size());
            for(Pixel p : foregroundPixels){
            	Set<Pixel> s = pixelToDisjointSet.get(p);
            	if(s.size() > maxSize) maxSize = s.size();
            	//System.out.println("size set: " + s.size());
            }
            
            removable = (foreground == maxSize);
            applicable = (background > backThresh && foreground > foreThresh);
            fullySurr = (background == 0 && foreground == 8); // mutually exclusive to applicable? Possibly
            //printValues();
        }
        
        /**
         * 
         * @param pIn
         * @param pivot
         * @return
         */
        private boolean isWithinEnvelope(Pixel pIn, Pixel pivot){
        	boolean x = ( pIn.getX() > pivot.getX() - 2 && pIn.getX() < pivot.getX() + 2 );
        	boolean y = ( pIn.getY() > pivot.getY() - 2 && pIn.getY() < pivot.getY() + 2 );
        	return x && y;
        }
        
        private boolean isForeground(Pixel p) {
            return (p.getRed() >= foregroundColorThreshold);
        }
        private boolean isBackground(Pixel p) {
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
         * @return
         */
        @SuppressWarnings("unused")
        public boolean isFullySurrounded(Pixel pivot) {
            checkMyPivot(pivot);
            return fullySurr;
        }
        /**
         * 
         * @param pivot
         * @return
         */
        public boolean isApplicable(Pixel pivot) {
            checkMyPivot(pivot);
            return applicable;
        }
        /**
         * 
         * @param pivot
         */
        private void checkMyPivot(Pixel pivot) {
            if (this.pivot != pivot) {
                this.pivot = pivot;
                countValues();
            }
        }
        /**
         * 
         */
        @SuppressWarnings("unused")
        private void printValues() {
            System.out.println("foreground " + foreground);
            System.out.println("background " + background);
            System.out.println("up " + up);
            System.out.println("down " + down);
            System.out.println("will be connected " + removable);
        }

		private void setBackThresh(int backThresh) {
			this.backThresh = backThresh;
		}

		private void setForeThresh(int foreThresh) {
			this.foreThresh = foreThresh;
		}

		//tests
        @SuppressWarnings("unused")
        private final byte[][] pattern = new byte[][] {
            {
                0,
                0,
                0
            }, {
                0,
                0,
                0
            }, {
                1,
                0,
                1
            }
        };
    }
}
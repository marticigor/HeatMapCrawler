package core.image_filters;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import core.RoundIteratorOfPixels;
import ifaces.I_ColorScheme;
import lib_duke.ImageResource;
import lib_duke.Pixel;

// http://homepages.inf.ed.ac.uk/rbf/HIPR2/thin.htm
// https://dsp.stackexchange.com/questions/2523/connecting-edges-detected-by-an-edge-detector

public class Skeleton extends BaseFilter implements I_ColorScheme {

    public Skeleton(ImageResource in , boolean w, boolean d, int...intArgs) {
        super( in .getWidth(), in .getHeight(), 2, w, d, 5, intArgs);
        this.in = in ;
    }

    private ImageResource in;
    
    @Override
    public void doYourThing() {
        int threshold = redScheme[0];
        SkeletonUtils utils = new SkeletonUtils(threshold); //threshold
        utils.setbackThresh(2);
        utils.setForeThresh(3);
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
        
        while (count < 100) {//precaution

            for (int absX = widthFrom; absX < widthTo; absX++) {
                for (int absY = heightFrom; absY < heightTo; absY++) {
                	
                    current = in .getPixel(absX, absY);

                    if (utils.isApplicable(current)) {
                        toRemove.add(current);
                    }
                }
            }

            for (Pixel p: toRemove) {
                if (utils.isRemovable(p) &&
                    p.getRed() != 0) {
                        p.setRed(lightGreenScheme[0]);
                        p.setBlue(lightGreenScheme[1]);
                        p.setGreen(lightGreenScheme[2]);
                        removed++;
                }
            }

            if (removed == 0) break;
            else if (debug) System.out.println("toRemove = applicable: " + toRemove.size() +
            		"\nremoved: " + removed + 
            		 "\n___________________________________________");

            toRemove.clear();

            removed = 0;
            count++;
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
        List <Pixel> foregroundPixels;

        public SkeletonUtils(int foregroundColor) {
            riop = new RoundIteratorOfPixels( in );
            this.foregroundColorThreshold = foregroundColor;
        }

        private void countValues() {

            // tests Pixel pivot = im.getPixel(1,1);

            riop.setPixelToCheckAround(pivot);
            riop.resetCount();
            foreground = 0;
            background = 0;
            up = 0;
            down = 0;
            
            boolean eachPHasNei = true;
            removable = false;
            applicable = false;
            fullySurr = false;

            foregroundPixels = new LinkedList<Pixel>();
            
            for (Pixel p: riop) {
                //foreground included, background excluded
                if (isForeground(p)) {foreground++; foregroundPixels.add(p);}
                else if (isBackground(p)) background++;
            }
            
            int countFore = 0;
            
            for (Pixel p : foregroundPixels){
            	
            	riop.setPixelToCheckAround(p);
            	riop.resetCount();
            	
            	countFore = 0;
            	for (Pixel pIn : riop){
            		if( !isWithinEnvelope(pIn, pivot) ) continue;
            		
            		if( isForeground(pIn ) &&
            			pIn != pivot
            		  ) countFore ++;
            	}
            	if(countFore == 0){ eachPHasNei = false; }// break;
            }

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
        
        private int backThresh = 0;//0
        private int foreThresh = 1;//1

        public void setForeThresh(int i) {
            this.foreThresh = i;
        }
		public void setbackThresh(int i) {
            this.backThresh = i;
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
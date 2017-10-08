package core.image_filters;

import java.util.ArrayList;
import java.util.List;

import core.RoundIteratorOfPixels;
import ifaces.IColorScheme;
import lib_duke.ImageResource;
import lib_duke.Pixel;

//http://homepages.inf.ed.ac.uk/rbf/HIPR2/thin.htm
public class Skeleton extends BaseFilter implements IColorScheme {

    public Skeleton(ImageResource in , boolean w, boolean d, int...intArgs) {
        super( in .getWidth(), in .getHeight(), 2, w, d, 5, intArgs);
        this.in = in ;
    }

    @SuppressWarnings("unused")
    private ImageResource in , out;
    private static final int REMOVAL_MARK = 21;
    @SuppressWarnings("unused")
    private static final int REMOVAL_UNMARK = 100;
    @SuppressWarnings("unused")
    private static final int BLUE_VISUAL = 255;

    public Skeleton(ImageResource in , ImageResource out, boolean w, boolean d, int...intArgs) {
        super( in .getWidth(), in .getHeight(), 2, w, d, 5, intArgs);
        this.in = in ;

    }

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
        
    	if(debug)System.out.println("NEW TASK");
    	
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
                	
                    p.setRed(0);
                    p.setBlue(80);
                    p.setGreen(80);
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
     *
     *
     */
    private class SkeletonUtils {

        private final RoundIteratorOfPixels riop;
        private int foregroundColorThreshold; //value of red channel
        private byte foreground, background, up, down;
        private boolean connected;
        private boolean applicable;
        private boolean fullySurr;
        private Pixel pivot = null;

        public SkeletonUtils(int foregroundColor) {
            riop = new RoundIteratorOfPixels( in );
            this.foregroundColorThreshold = foregroundColor;
        }

        private void countValues() {

            // tests Pixel pivot = im.getPixel(1,1);
            Pixel pBack = null;
            Pixel pLast = null;

            riop.setPixelToCheckAround(pivot);
            riop.resetCount();
            foreground = 0;
            background = 0;
            up = 0;
            down = 0;
            connected = false;
            applicable = false;
            fullySurr = false;

            int counter = 0;

            for (Pixel p: riop) {
                //foreground included, background excluded
                if (isForeground(p)) foreground++;
                else if (isBackground(p)) background++;
                if (counter > 0) {
                    detectEdges(pBack, p);
                }
                pBack = p;
                counter++;
            }

            pLast = in .getPixel(pivot.getX() - 1, pivot.getY() - 1);
            pBack = in .getPixel(pivot.getX() - 1, pivot.getY());
            detectEdges(pBack, pLast);

            connected = ((up == upThresh && down == downThresh));

            applicable = (background > backThresh && foreground > foreThresh);

            fullySurr = (background == 0 && foreground == 8); // mutually exclusive to applicable? Possibly

            //printValues();
        }

        private int upThresh = 1;
        private int downThresh = 1;
        private int backThresh = 0;
        private int foreThresh = 1;


        public void setForeThresh(int i) {
            this.foreThresh = i;
        }

		public void setbackThresh(int i) {
            this.backThresh = i;
        }

        private void detectEdges(Pixel pBack, Pixel p) {
            if (isForeground(pBack) && isBackground(p)) down++;
            else if (isBackground(pBack) && isForeground(p)) up++;
        }

        private boolean isForeground(Pixel p) {
            return (p.getRed() >= foregroundColorThreshold);
        }

        private boolean isBackground(Pixel p) {
            return (p.getRed() < foregroundColorThreshold);
        }

        @SuppressWarnings("unused")
        private boolean isMarkedForRemoval(Pixel p) {
            return (p.getGreen() == REMOVAL_MARK);
        }

        Pixel onLeft;
        int leftX;
        @SuppressWarnings("unused")
        private boolean isNextToMarked(Pixel p) {

            leftX = (p.getX() > 0) ? p.getX() - 1 : 0;
            onLeft = in .getPixel(leftX, p.getY());
            return (onLeft.getGreen() == REMOVAL_MARK);

        }
        /**
         * 
         * @param pivot
         * @return
         */
        public boolean isRemovable(Pixel pivot) {
            checkMyPivot(pivot);
            return connected;
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

        private void checkMyPivot(Pixel pivot) {
            if (this.pivot != pivot) {
                this.pivot = pivot;
                countValues();
            }
        }

        @SuppressWarnings("unused")
        private void printValues() {
            System.out.println("foreground " + foreground);
            System.out.println("background " + background);
            System.out.println("up " + up);
            System.out.println("down " + down);
            System.out.println("will be connected " + connected);
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
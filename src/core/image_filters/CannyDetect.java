package core.image_filters;

import java.util.HashMap;
import java.util.Map;

import core.image_filters.filter_utils.ChunksNotMessedAssertion;
import core.image_filters.filter_utils.ChunksOrWhole;
import ifaces.IImageProcesor;
import lib_duke.AugmentedPixel;
import lib_duke.ImageResource;
import lib_duke.Pixel;

public class CannyDetect implements IImageProcesor {

    private Map < Pixel, AugmentedPixel > toAugmented = new HashMap < Pixel, AugmentedPixel > ();

    private int borderSharpenStage;
    private boolean wholeImage;
    private boolean debug;
    private int [] args;//first 4 always chunk, maybe dummy

    public CannyDetect(boolean w, boolean d, int...intArgs) {
        this.wholeImage = w;
        this.debug = d;
        if (intArgs.length != 5) throw new RuntimeException("Arguments length"); 
        this.args = intArgs;
        borderSharpenStage = intArgs[4];

        if (debug) for (int i: intArgs) System.out.println("Args in Sharpen: " + i);
    }

    static class Sobel {
        private static final int[][] VER_KERNEL = new int[][] {
            {
                1,0,-1
            }, {
                2,0,-2
            }, {
                1,0,-1
            }
        };
        private static final int[][] HOR_KERNEL = new int[][] {
            {
                1,2,1
            }, {
                0,0,0
            }, {
            	-1,-2,-1
            }
        };
        private static final byte BORDER_SOBEL = 1;
    }

    @Override
    public void doYourThing(ImageResource in , ImageResource out) {

        int [] values = ChunksOrWhole.decide(args, wholeImage, in.getWidth(), in.getHeight());
    	
        final int xSize = in .getWidth();
        final int ySize = in .getHeight();
        boolean halt = ChunksNotMessedAssertion.assertOK(xSize, ySize, values, borderSharpenStage);
        if (halt) throw new RuntimeException("chunks messed");
    }
}

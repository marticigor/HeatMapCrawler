package core.image_filters;

import java.util.HashMap;
import java.util.Map;

import ifaces.IImageProcesor;
import lib_duke.AugmentedPixel;
import lib_duke.ImageResource;
import lib_duke.Pixel;

public class CannyEdgeDetection implements IImageProcesor {

    private Map < Pixel, AugmentedPixel > toAugmented = new HashMap < Pixel, AugmentedPixel > ();

    public CannyEdgeDetection() {}

    static class Sobel {
        private static final int[][] VER_KERNEL = new int[][] {
            {
                1,
                0,
                -1
            }, {
                2,
                0,
                -2
            }, {
                1,
                0,
                -1
            }
        };
        private static final int[][] HOR_KERNEL = new int[][] {
            {
                1,
                2,
                1
            }, {
                0,
                0,
                0
            }, {-1,
                -2,
                -1
            }
        };
        private static final byte BORDER_SOBEL = 1;
    }

    @Override
    public void doYourThing(ImageResource in , ImageResource out) {

    }
}

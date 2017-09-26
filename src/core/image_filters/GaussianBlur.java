package core.image_filters;

import core.image_filters.filter_utils.BorderWatch;
import core.image_filters.filter_utils.ChunksNotMessedAssertion;
import core.image_filters.filter_utils.ChunksOrWhole;
import ifaces.IColorScheme;
import ifaces.IImageProcesor;
import lib_duke.ImageResource;
import lib_duke.Pixel;

public class GaussianBlur implements IImageProcesor, IColorScheme {

    private int borderSharpenStage;
    private boolean wholeImage;
    private boolean debug;
    private int[] args; //first 4 always chunk, maybe dummy

    public GaussianBlur(boolean w, boolean d, int...intArgs) {
        this.wholeImage = w;
        this.debug = d;
        args = intArgs;
        if (intArgs.length != 5) throw new RuntimeException("Arguments length");
        borderSharpenStage = intArgs[4];
    }

    @Override
    public void doYourThing(ImageResource in , ImageResource out) {

        int borderG = Gaussian.BORDER_GAUS_3;

        int[] values = ChunksOrWhole.decide(args, wholeImage, in .getWidth(), in .getHeight());
        final int xSize = in .getWidth();
        final int ySize = in .getHeight();
        final boolean halt = ChunksNotMessedAssertion.assertOK(xSize, ySize, values, borderSharpenStage);
        if (halt) throw new RuntimeException("chunks messed");

        BorderWatch border = new BorderWatch(values, borderG, xSize, ySize);
        
        int widthFrom = border.getWidthFrom();
        int widthTo = border.getWidthTo();
        int heightFrom = border.getHeightFrom();
        int heightTo = border.getHeightTo();

        Pixel pixelOut, matrixPixel;

        int r, g, b;
        int matrixX, matrixY;
        int cumulR, cumulG, cumulB;

        if (borderG > borderSharpenStage) throw new RuntimeException("borders");

        if (debug) {
            System.out.println(" GaussianBlur var: xSize " + xSize);
            System.out.println(" GaussianBlur var: ySize " + ySize);
        }

        //matrix counters
        int countX;
        int countY;

        final int MAX = 255;
        
        for (int absX = widthFrom; absX < widthTo; absX++) {
            for (int absY = heightFrom; absY < heightTo; absY++) {
          	
                pixelOut = out.getPixel(absX, absY);

                matrixX = 0;
                matrixY = 0;
                cumulR = 0;
                cumulG = 0;
                cumulB = 0;

                /*
                 * IF TO SLOW ...
                A Gaussian blur effect is typically generated by convolving an image with a kernel
                of Gaussian values. In practice, it is best to take advantage of the Gaussian blur’s
                separable property by dividing the process into two passes. In the first pass,
                a one-dimensional kernel is used to blur the image in only the horizontal
                or vertical direction. In the second pass, the same one-dimensional kernel is used
                to blur in the remaining direction. The resulting effect is the same as convolving
                with a two-dimensional kernel in a single pass, but requires fewer calculations.
				
                https://en.wikipedia.org/wiki/Gaussian_blur
                */

                //iterate kernel

                countX = 0;
                countY = 0;
                for (int absKernelX = absX - borderG; absKernelX < absX + borderG + 1; absKernelX++) {
                    for (int absKernelY = absY - borderG; absKernelY < absY + borderG + 1; absKernelY++) {

                        matrixX = countX;
                        matrixY = countY;

                        matrixPixel = in .getPixel(absKernelX, absKernelY);
                        cumulR += (matrixPixel.getRed() * Gaussian.GAUS_KERNEL_3[matrixX][matrixY]);
                        cumulG += (matrixPixel.getGreen() * Gaussian.GAUS_KERNEL_3[matrixX][matrixY]);
                        cumulB += (matrixPixel.getBlue() * Gaussian.GAUS_KERNEL_3[matrixX][matrixY]);
                        countY++;
                    }
                    countY = 0;
                    countX++;
                }

                r = (int)(((double) cumulR) * Gaussian.NORMALIZE_GAUS_3);
                g = (int)(((double) cumulG) * Gaussian.NORMALIZE_GAUS_3);
                b = (int)(((double) cumulB) * Gaussian.NORMALIZE_GAUS_3);

                if (r > MAX || g > MAX || b > MAX) {
                    System.out.println("CULPRIT R " + r);
                    System.out.println("CULPRIT G " + g);
                    System.out.println("CULPRIT B " + b);
                    System.out.println("CULPRIT MAX " + MAX);
                    throw new RuntimeException("MAX_VALUES_IN_GB");
                }
                
                pixelOut.setRed(r);
                pixelOut.setGreen(g);
                pixelOut.setBlue(b);
                  
            }
        }
    }
    
    static class Gaussian {
        private static final int[][] GAUS_KERNEL_3 = new int[][] {
        {
            2,2,2
        }, {
            2,6,2
        }, {
            2,2,2
        }
    };
    private static final double NORMALIZE_GAUS_3 = 1 / 22d;
    private static final byte BORDER_GAUS_3 = 1;
    }
}

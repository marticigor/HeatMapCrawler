package core.image_filters;

import core.image_filters.filter_utils.ChunksNotMessedAssertion;
import core.image_filters.filter_utils.ChunksOrWhole;
import ifaces.IColorScheme;
import ifaces.IImageProcesor;
import lib_duke.ImageResource;
import lib_duke.Pixel;

public class Sharpen implements IImageProcesor, IColorScheme {

    private int borderSharpenStage;
    private int devToMakeItValidRoutable;
    private boolean wholeImage;
    private boolean debug;
    private int [] args;//first 4 always chunk, maybe dummy

    public Sharpen(boolean w, boolean d, int...intArgs) {
        this.wholeImage = w;
        this.debug = d;
        if (intArgs.length != 6) throw new RuntimeException("Arguments length"); 
        this.args = intArgs;
        borderSharpenStage = intArgs[4];
        devToMakeItValidRoutable = intArgs[5];

        if (debug) for (int i: intArgs) System.out.println("Args in Sharpen: " + i);
    }
    /**
     * 
     */
    @Override
    public void doYourThing(ImageResource in , ImageResource out) {

        int [] values = ChunksOrWhole.decide(args, wholeImage, in.getWidth(), in.getHeight());
        
        final int xSize = in .getWidth();
        final int ySize = in .getHeight();
        boolean halt = ChunksNotMessedAssertion.assertOK(xSize, ySize, values, borderSharpenStage);
        if (halt) throw new RuntimeException("chunks messed");
        
        int widthFrom = values[0];
        int widthTo = values[1];
        int heightFrom = values[2];
        int heightTo = values[3];    	
        
        Pixel outP;
        Pixel inP;
        int valueOfGray;

        for (int y = heightFrom; y < heightTo; y++) {
            for (int x = widthFrom; x < widthTo; x++) {

                inP = in .getPixel(x, y);
                outP = out.getPixel(inP.getX(), inP.getY());

                if (inP.getRed() == inP.getGreen() && inP.getGreen() == inP.getBlue()) {

                    valueOfGray = inP.getRed();
                    outP.setRed(valueOfGray);
                    outP.setGreen(valueOfGray);
                    outP.setBlue(valueOfGray);


                } else if (isRoutable(inP) && (inP.getX() > borderSharpenStage &&
                        inP.getX() < in .getWidth() - borderSharpenStage) &&
                    (inP.getY() > borderSharpenStage && inP.getY() < in .getHeight() - borderSharpenStage)) {
                    outP.setRed(redScheme[0]);
                    outP.setGreen(redScheme[1]);
                    outP.setBlue(redScheme[2]);
                } else {
                    outP.setRed(lightGreenScheme[0]);
                    outP.setGreen(lightGreenScheme[1]);
                    outP.setBlue(lightGreenScheme[2]);
                }
            }
        }
        if(debug)System.out.println("CHUNK PROCESSED " + System.currentTimeMillis());
    }

    private int avg;
    private int dev;

    /**
     *
     */
    private boolean isRoutable(Pixel p) {

        avg = (p.getRed() + p.getGreen() + p.getBlue()) / 3;
        dev = 0;
        dev += Math.abs(p.getRed() - avg);
        dev += Math.abs(p.getGreen() - avg);
        dev += Math.abs(p.getBlue() - avg);

        if (dev > devToMakeItValidRoutable) return true;
        else return false;
    }

}
package core.image_filters;

import java.util.Map;

import core.image_filters.filter_utils.BorderWatch;
import core.image_filters.filter_utils.ChunksNotMessedAssertion;
import core.image_filters.filter_utils.ChunksOrWhole;
import ifaces.IColorScheme;
import ifaces.IImageProcesor;
import lib_duke.AugmentedPixel;
import lib_duke.ImageResource;
import lib_duke.Pixel;

//INVARIANT: create with HashMap from Canny Detect
public class EdgeHighlight implements IImageProcesor, IColorScheme {

    private final Map < Pixel, AugmentedPixel > toAugmented;
    private int borderSharpenStage;
    private boolean wholeImage;
    private boolean debug;
    private int[] args; //first 4 always chunk, maybe dummy

    //TODO lot of code duplicity in filters - make base class holding chops values e.t.c

    public EdgeHighlight(Map < Pixel, AugmentedPixel > toAugmented, boolean w, boolean d, int...intArgs) {
        this.toAugmented = toAugmented;
        this.wholeImage = w;
        this.debug = d;
        if (intArgs.length != 5) throw new RuntimeException("Arguments length");
        this.args = intArgs;
        borderSharpenStage = intArgs[4];
        if (debug)
            for (int i: intArgs) System.out.println("Args in Highlight: " + i);
    }

    public void doYourThing(ImageResource in , ImageResource out) {

        int[] values = ChunksOrWhole.decide(args, wholeImage, in .getWidth(), in .getHeight());

        final int xSize = in .getWidth();
        final int ySize = in .getHeight();
        final boolean halt = ChunksNotMessedAssertion.assertOK(xSize, ySize, values, borderSharpenStage);
        if (halt) throw new RuntimeException("chunks messed");

        BorderWatch border = new BorderWatch(values, borderSharpenStage, xSize, ySize);

        int widthFrom = border.getWidthFrom();
        int widthTo = border.getWidthTo();
        int heightFrom = border.getHeightFrom();
        int heightTo = border.getHeightTo();

        Pixel inP, outP;
        Pixel inPlus, inMinus;
        int xPlus, xMinus, yPlus, yMinus;
        double gradCurr, gradPlus, gradMinus;
        AugmentedPixel augCurr, augPlus, augMinus;

        for (int absX = widthFrom; absX < widthTo; absX++) {
            for (int absY = heightFrom; absY < heightTo; absY++) {

                inP = in .getPixel(absX, absY);

                if (!toAugmented.keySet().contains(inP)) throw new RuntimeException("HashMap mess 1");

                augCurr = toAugmented.get(inP);
                gradCurr = augCurr.getGradientComputed();
                xPlus = augCurr.getDirEnum().getxMove();
                yPlus = augCurr.getDirEnum().getyMove();
                xMinus = -xPlus;
                yMinus = -yPlus;
                inPlus = in .getPixel(inP.getX() + xPlus, inP.getY() + yPlus);
                inMinus = in .getPixel(inP.getX() + xMinus, inP.getY() + yMinus);
                if (toAugmented.keySet().contains(inPlus)) augPlus = toAugmented.get(inPlus);
                else continue;

                gradPlus = augPlus.getGradientComputed();
                if (toAugmented.keySet().contains(inMinus)) augMinus = toAugmented.get(inMinus);
                else continue;

                gradMinus = augMinus.getGradientComputed();
                outP = out.getPixel(absX, absY);
                if (gradCurr > gradPlus && gradCurr > gradMinus) {
                    outP.setRed(redScheme[0]);
                    outP.setGreen(redScheme[1]);
                    outP.setBlue(redScheme[2]);
                } else {
                    outP.setRed(0);
                    outP.setGreen(25);
                    outP.setBlue(0);
                }

            }
        }
    }
}
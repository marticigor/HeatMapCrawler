package core.image_filters;

import java.util.Map;

import ifaces.IColorScheme;
import ifaces.IImageProcesor;
import lib_duke.AugmentedPixel;
import lib_duke.ImageResource;
import lib_duke.Pixel;

//INVARIANT: create with HashMap from Canny Detect
public class EdgeHighlight extends BaseFilter implements IImageProcesor, IColorScheme {

    private final Map < Pixel, AugmentedPixel > toAugmented;
    private ImageResource in,out;

    public EdgeHighlight(ImageResource in, ImageResource out,
    		Map < Pixel, AugmentedPixel > toAugmented, boolean w, boolean d, int...intArgs) {
    	super(in.getWidth(), in.getHeight(),2 ,w ,d ,5 ,intArgs);//hardcoded mock border
        this.toAugmented = toAugmented;
        this.in = in;
        this.out = out;
    }

    public void doYourThing() {

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
package core.image_filters;

import ifaces.IColorScheme;
import ifaces.IImageProcesor;
import lib_duke.ImageResource;
import lib_duke.Pixel;

public class Sharpen implements IImageProcesor, IColorScheme {

    private int widthFrom;
    private int widthTo;
    private int heightFrom;
    private int heightTo;
    private int borderSharpenStage;
    private int devToMakeItValidRoutable;
    private boolean wholePicture;

    public Sharpen(boolean boolArg, int...intArgs) {
        this.wholePicture = boolArg;
        if (intArgs.length != 6) throw new RuntimeException("Arguments length");
        widthFrom = intArgs[0];
        widthTo = intArgs[1];
        heightFrom = intArgs[2];
        heightTo = intArgs[3];
        borderSharpenStage = intArgs[4];
        devToMakeItValidRoutable = intArgs[5];

        for (int i: intArgs) System.out.println("Args in Sharpen: " + i);

    }

    @Override
    public void proces(ImageResource in , ImageResource out) {

        widthFrom = (wholePicture) ? 0 : widthFrom;
        widthTo = (wholePicture) ? in .getWidth() : widthTo;

        heightFrom = (wholePicture) ? 0 : heightFrom;
        heightTo = (wholePicture) ? in .getHeight() : heightTo;

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
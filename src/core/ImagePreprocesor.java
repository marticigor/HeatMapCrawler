package core;

import core.image_filters.Sharpen;
import ifaces.IColorScheme;
import ifaces.IImageProcesor;
import lib_duke.ImageResource;

public class ImagePreprocesor implements IColorScheme {

    private final ImageResource inputImageResource;
    private final ImageResource procesedImageResourceStage1,
    procesedImageResourceStage2,
    procesedImageResourceStage3;

    private final int devToMakeItValidRoutable; //80;
    private final int borderAtSharpenStage;

    public ImagePreprocesor(int deviation, int border) {
        this.devToMakeItValidRoutable = deviation;
        this.borderAtSharpenStage = border;

        //initMap
        inputImageResource = new ImageResource(); //opens dialog
        //drawMap
        inputImageResource.draw();
        //initSharpenedMap
        int w = inputImageResource.getWidth();
        int h = inputImageResource.getHeight();
        procesedImageResourceStage1 = new ImageResource(w, h);
        procesedImageResourceStage2 = new ImageResource(w, h);
        procesedImageResourceStage3 = new ImageResource(w, h);
    }

    public int getX() {
        return inputImageResource.getWidth();
    }
    public int getY() {
        return inputImageResource.getHeight();
    }

    /**
     *
     */
    public void proces(int widthFrom,
        int widthTo,
        int heightFrom,
        int heightTo,
        boolean wholePicture) {

        IImageProcesor sharpen = new Sharpen(
            wholePicture,
            widthFrom,
            widthTo,
            heightFrom,
            heightTo,
            borderAtSharpenStage,
            devToMakeItValidRoutable
        );
        sharpen.proces(inputImageResource, procesedImageResourceStage1);
    }

    /**
     *
     */
    public ImageResource getProcesed() {
        return procesedImageResourceStage1;
    }
}
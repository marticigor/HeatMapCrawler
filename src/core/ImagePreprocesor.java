package core;

import core.image_filters.GaussianBlur;
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

    private boolean visual, debug;
    
    public ImagePreprocesor(int deviation, int border, boolean visual, boolean debug) {
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
        
        this.visual = visual;
        this.debug = debug;
    }

    public int getX() {
        return inputImageResource.getWidth();
    }
    public int getY() {
        return inputImageResource.getHeight();
    }

    /**
     * 
     * @param widthFrom
     * @param widthTo
     * @param heightFrom
     * @param heightTo
     * @param wholePicture - so no params needed, only dummies
     */
    public void procesSharpen(
    	int widthFrom,
        int widthTo,
        int heightFrom,
        int heightTo,
        boolean wholePicture) {

        IImageProcesor sharpen = new Sharpen(
            wholePicture,
            debug,
            widthFrom,
            widthTo,
            heightFrom,
            heightTo,
            borderAtSharpenStage,
            devToMakeItValidRoutable
        );
        if(debug | visual) System.out.println(this.getClass().toString() + " call " + "sharpen.proces");
        sharpen.doYourThing(inputImageResource, procesedImageResourceStage1);
    }

    /**
     * 
     * @param widthFrom
     * @param widthTo
     * @param heightFrom
     * @param heightTo
     * @param wholePicture
     */
    public void procesGaussian(
    		int widthFrom,
            int widthTo,
            int heightFrom,
            int heightTo,
            boolean wholePicture){

    	IImageProcesor gaussian = new GaussianBlur(
    			wholePicture,
    			debug,
                widthFrom,
                widthTo,
                heightFrom,
                heightTo,
                borderAtSharpenStage
    			);
    	
    	gaussian.doYourThing(procesedImageResourceStage1, procesedImageResourceStage2);
    }
    
    /**
     *
     */
    public ImageResource getProcesed() {
        return procesedImageResourceStage2;
    }
}
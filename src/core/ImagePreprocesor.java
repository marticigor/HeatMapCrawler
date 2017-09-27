package core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import core.image_filters.CannyDetect;
import core.image_filters.EdgeHighlight;
import core.image_filters.GaussianBlur;
import core.image_filters.Sharpen;
import core.image_filters.filter_utils.MapMerge;
import ifaces.IColorScheme;
import ifaces.IImageProcesor;
import lib_duke.AugmentedPixel;
import lib_duke.ImageResource;
import lib_duke.Pixel;

public class ImagePreprocesor implements IColorScheme {

    private final ImageResource inputImageResource;
    @SuppressWarnings("unused")
	private final ImageResource procesedImageResourceStage1,
    procesedImageResourceStage2,
    procesedImageResourceStage3,//once no visualization kept is needed, make a cyclic queue
    procesedImageResourceStage4,
    procesedImageResourceStage5;

    private final int devToMakeItValidRoutable; //80;
    private final int borderAtSharpenStage;

    private boolean visual, debug;
    private final List<Map<Pixel, AugmentedPixel>> chopsToAugmentedList = new ArrayList<Map<Pixel, AugmentedPixel>>();
    
    
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
        procesedImageResourceStage2 = new ImageResource(w, h);//once no visualization kept is needed, make a cyclic queue
        procesedImageResourceStage3 = new ImageResource(w, h);
        procesedImageResourceStage4 = new ImageResource(w, h);
        procesedImageResourceStage5 = new ImageResource(w, h);
        
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
        	inputImageResource,
        	procesedImageResourceStage1,
            wholePicture,
            debug,
            widthFrom,
            widthTo,
            heightFrom,
            heightTo,
            borderAtSharpenStage,
            devToMakeItValidRoutable
        );
        
        debugPrint("procesSharpen");
        sharpen.doYourThing();
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
    			procesedImageResourceStage1,
    			procesedImageResourceStage2,
    			wholePicture,
    			debug,
                widthFrom,
                widthTo,
                heightFrom,
                heightTo,
                borderAtSharpenStage
    			);
    	debugPrint("procesGaussian");
    	gaussian.doYourThing();
    }
    
	CannyDetect canny; //implements IImageProcesor
    
    /**
     * 
     * @param widthFrom
     * @param widthTo
     * @param heightFrom
     * @param heightTo
     * @param wholePicture
     */
	public void procesCanny(
    		int widthFrom,
            int widthTo,
            int heightFrom,
            int heightTo,
            boolean wholePicture
			) {
			
		canny = new CannyDetect(
				procesedImageResourceStage2,
				this,
				wholePicture,
				debug, 
                widthFrom,
                widthTo,
                heightFrom,
                heightTo,
                borderAtSharpenStage
				);
		debugPrint("procesCanny");
		canny.doYourThing();
	}
	
	Map<Pixel, AugmentedPixel> toAugmented;
	/**
	 * 
	 * @param widthFrom
	 * @param widthTo
	 * @param heightFrom
	 * @param heightTo
	 * @param wholePicture
	 */
	public void procesHighlight(
    		int widthFrom,
            int widthTo,
            int heightFrom,
            int heightTo,
            boolean wholePicture
			) {
			
		if(canny == null) throw new RuntimeException("INVARIANT 1");
		if(toAugmented == null) toAugmented = this.getToAugmented();
		if(toAugmented == null || toAugmented.size() == 0) throw new RuntimeException("INVARIANT 2");
		
		IImageProcesor highlight = new EdgeHighlight(
				procesedImageResourceStage2,
				procesedImageResourceStage3,
				toAugmented,
				wholePicture,
				debug, 
                widthFrom,
                widthTo,
                heightFrom,
                heightTo,
                borderAtSharpenStage
				);
		debugPrint("procesHighlight");
		highlight.doYourThing();
	}
   public void addMap(Map <Pixel, AugmentedPixel> chop){
	   chopsToAugmentedList.add(chop);
   }
   private Map<Pixel, AugmentedPixel> getToAugmented(){
	   MapMerge<Pixel, AugmentedPixel> merge = new MapMerge<Pixel, AugmentedPixel>(chopsToAugmentedList);
	   return merge.getMerged();
   }
    /**
    *
    */
   public ImageResource getProcesedStage() {
   	//
   	// test save
   	// procesedImageResourceStage3.draw();
   	// procesedImageResourceStage2.saveAs();
   	//
   	//
       return procesedImageResourceStage2;
   }
    /**
     *
     */
    public ImageResource getProcesed() {
    	//
    	// test save
    	// procesedImageResourceStage3.draw();
    	// procesedImageResourceStage2.saveAs();
    	//
    	//
        return procesedImageResourceStage3;
    }
    private void debugPrint(String job){
    	if(debug || visual) System.out.println(this.getClass().toString() + " call " + job);
    }
}
package core.image_filters;

import core.RoundIteratorOfPixels;
import ifaces.IColorScheme;
import lib_duke.ImageResource;
import lib_duke.Pixel;

//http://homepages.inf.ed.ac.uk/rbf/HIPR2/thin.htm
public class Skeleton extends BaseFilter implements IColorScheme {

	public Skeleton(ImageResource in, boolean w, boolean d, int...intArgs){
                super(in.getWidth(), in.getHeight(),2, w, d, 5, intArgs);
                this.in = in;
	}
	
    @SuppressWarnings("unused")
	private ImageResource in,out;

	public Skeleton(ImageResource in, ImageResource out, boolean w, boolean d, int...intArgs){
		super(in.getWidth(), in.getHeight(),2, w, d, 5, intArgs);
        this.in = in;
        
	}
	
	/*
	 *Consider all pixels on the boundaries of foreground regions (i.e. foreground points
	 *that have at least one background neighbor). Delete any such point that has
	 *more than one foreground neighbor, as long as doing so does not locally disconnect
	 *(i.e. split into two) the region containing that pixel. Iterate until convergence. 
	 */
	
	@Override
	public void doYourThing() {

	    //Consider all pixels on the boundaries of foreground regions (i.e. foreground points that have
	    //at least one background neighbor). Delete any such point that has more
	    //than one foreground neighbor, as long as doing so does not locally disconnect
	    //(i.e. split into two) the region containing that pixel. Iterate until convergence.
		
		int threshold = redScheme[0];
			
		SkeletonUtils  utils = new SkeletonUtils(threshold);//threshold
		Pixel current;
		//List<Pixel> toRemove = new ArrayList<Pixel>();
		
		for(int i = 0; i < 1; i++){
		
		    for (int absX = widthFrom; absX < widthTo; absX++) {
                for (int absY = heightFrom; absY < heightTo; absY++) {
            
            	    current = in.getPixel(absX, absY);
            	    if(utils.isApplicable(current) && utils.isRemovable(current)) {
            	    	current.setRed(50);
            	    	current.setBlue(50);
            	    	current.setGreen(50);
            	        //toRemove.add(current);
            	    }
                }
            }
		    
		    //for(Pixel p : toRemove) {p.setRed(0);p.setBlue(0);p.setGreen(0);}
		    //toRemove.clear();
		    
		}
	}
	
	/**
	 * 
	 *
	 *
	 */
	private class SkeletonUtils{
		
	    private final RoundIteratorOfPixels riop;
	    private int foregroundColorThreshold;//value of red channel
	    private byte foreground, background, up, down;
	    private boolean connected;
	    private boolean applicable;
	    private Pixel pivot = null;

	    public SkeletonUtils(int foregroundColor){
	        riop = new RoundIteratorOfPixels(in);
	        this.foregroundColorThreshold = foregroundColor;
	    }

	    private void countValues(){
	    
	        // tests Pixel pivot = im.getPixel(1,1);
	        Pixel pBack = null ;
	        Pixel pLast = null;

	        riop.setPixelToCheckAround(pivot);
	        riop.resetCount();
	        foreground = 0;
	        background = 0;
	        up = 0;
	        down = 0;
	        connected = false;
	        applicable = false;
	        
	        int counter = 0;
	        
	        for(Pixel p : riop){
	        	//foreground included, background excluded
	            if(isForeground(p)) foreground ++; else background ++;
	            if(counter > 0){ 
	                detectEdges(pBack,p);
	            }
	            pBack = p;
	            counter ++;
	        }
	        
	        pLast = in.getPixel(pivot.getX() - 1, pivot.getY() - 1);
	        pBack = in.getPixel(pivot.getX() - 1, pivot.getY());
	        detectEdges(pBack, pLast);
	        
	        connected = ( (up == 1 && down == 1) );
	        
	        applicable = (background > 0 && foreground > 1); 
	        
	        //printValues();
	    }
	        
	    private void detectEdges(Pixel pBack, Pixel p){
	        if(isForeground(pBack) && isBackground(p)) down ++;
	        else if(isBackground(pBack) && isForeground(p)) up ++;
	    }
	    
	    private boolean isForeground(Pixel p){
	    	return (p.getRed() >= foregroundColorThreshold);
	    }
	    
	    private boolean isBackground(Pixel p){
	    	return (p.getRed() < foregroundColorThreshold);
	    }
	    /**
	     * 
	     * @param pivot
	     * @return
	     */
	    public boolean isRemovable(Pixel pivot){
	        if(this.pivot != pivot) {this.pivot = pivot; countValues();}
	        return connected;
	    }
	    /**
	     * 
	     * @param pivot
	     * @return
	     */
	    public boolean isApplicable(Pixel pivot){
	        if(this.pivot != pivot) {this.pivot = pivot; countValues();}
	        return applicable;    
	    }
	    
	    @SuppressWarnings("unused")
		private void printValues(){
	       System.out.println("foreground " + foreground);
	       System.out.println("background " + background);
	       System.out.println("up " + up);
	       System.out.println("down " + down);
	       System.out.println("will be connected " + connected);
	    }
	    
	    //tests
	    @SuppressWarnings("unused")
		private final byte [] [] pattern = new byte[][] {
	    {0,0,0},
	    {0,0,0},
	    {1,0,1}};
	}
	
}
package core.image_filters;

import core.image_filters.filter_utils.BorderWatch;
import core.image_filters.filter_utils.ChunksNotMessedAssertion;
import core.image_filters.filter_utils.ChunksOrWhole;
import ifaces.I_ImageProcesor;

public class BaseFilter implements I_ImageProcesor{
	
    protected int borderSharpenStage;
    protected boolean wholeImage;
    protected boolean debug;
    protected int [] args;//first 4 always chunk, maybe dummy
    
    private int [] values;
    
    protected int widthFrom;
    protected int widthTo;
    protected int heightFrom;
    protected int heightTo;
    
    protected int xSize, ySize;
    
    protected BaseFilter(
    		
    		int x, int y, int border,
    		boolean wholeImage, boolean debug, int expectedArgsLength, int [] args){
    	
        this.wholeImage = wholeImage;
        this.debug = debug;
        if (args.length != expectedArgsLength) throw new RuntimeException("Arguments length in BaseFilter"); 
        this.args = args;
        borderSharpenStage = args[4];
        if (debug) for (int i: args) System.out.println("args in BaseFilter: " + i);
        
        values = ChunksOrWhole.decide(args, wholeImage, x, y);
        final boolean halt = ChunksNotMessedAssertion.assertOK(x, y, values, borderSharpenStage);
        if (halt) throw new RuntimeException("chunks messed");

        BorderWatch borderWatch = new BorderWatch(values, border, x, y);
        
        this.widthFrom = borderWatch.getWidthFrom();
        this.widthTo = borderWatch.getWidthTo();
        this.heightFrom = borderWatch.getHeightFrom();
        this.heightTo = borderWatch.getHeightTo();
        
        this.xSize = x;
        this.ySize = y;
        
        if(borderSharpenStage < border) throw new RuntimeException("kernel mess BaseFilter");
    }

	@Override
	public void doYourThing() {
		// TODO Auto-generated method stub
		
	}
}

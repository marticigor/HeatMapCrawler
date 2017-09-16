package core;

import lib_duke.ImageResource;
import lib_duke.Pixel;

public class IteratorTester {
    
    public void testIterator(){
    
        ImageResource ir = new ImageResource();
        RoundIteratorOfPixels ip = new RoundIteratorOfPixels();
        ip.setImageResource(ir);
        ip.setPixelToCheckAround(ir.getPixel(100,100));
        ip.reset();
        
        for (Pixel iterated : ip){
        
            System.out.println("iterovany pixel x y "+ iterated.getX()+" "+iterated.getY());
        
        }        
    }
}

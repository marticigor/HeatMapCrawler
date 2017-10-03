package core;

import java.util.Iterator;
import java.util.NoSuchElementException;

import lib_duke.ImageResource;
import lib_duke.Pixel;

public class RoundIteratorOfPixels implements Iterable < Pixel > , Iterator < Pixel > {

	public RoundIteratorOfPixels (ImageResource ir){
		this.ir = ir;
	}
	
    public RoundIteratorOfPixels() {
	}

	private ImageResource ir = null;
    //private Pixel home = null;
    private int homeX,
    homeY;
    private byte[] roundHomeX = {
    	-1,
        0,
        +1,
        +1,
        +1,
        0,
        -1,
        -1
    };
    private byte[] roundHomeY = {
    	-1,
        -1,
        -1,
        0,
        +1,
        +1,
        +1,
        0
    };

    public byte[] getRoundY(){
    	return roundHomeY;
    }
    
    public byte[] getRoundX(){
    	return roundHomeX;
    }
    
    private byte count = 0;
    public boolean print = false;

    /**
     *  
     */
    public void setImageResource(ImageResource ir) {
        this.ir = ir;
    }

    /**
     *  
     */
    public void setPixelToCheckAround(Pixel home) {

        //this.home = home;
        this.homeX = home.getX();
        this.homeY = home.getY();
    }

    /**
     *  
     */
    public void resetCount() {
        count = 0;
    }

    /**
     *  
     */
    public boolean hasNext() {

        if (count < 8) return true;
        else return false;
    }

    /**
     *  
     */
    public Pixel next() {

        if (print) System.out.println("now DEBUG from iterator count " + count + " " + this.toString() + " X Y " + (homeX + roundHomeX[count]) + " " + (homeY + roundHomeY[count]));
        if (count > 7) throw new NoSuchElementException();
        Pixel returning = ir.getPixel((homeX + roundHomeX[count]), (homeY + roundHomeY[count]));

        count++;
        return returning;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    public Iterator < Pixel > iterator() {
        return this;
    }

}
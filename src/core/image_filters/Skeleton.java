package core.image_filters;

import java.util.ArrayList;
import java.util.List;

import core.RoundIteratorOfPixels;
import ifaces.IColorScheme;
import lib_duke.ImageResource;
import lib_duke.Pixel;

//http://homepages.inf.ed.ac.uk/rbf/HIPR2/thin.htm
public class Skeleton extends BaseFilter implements IColorScheme {

        private ImageResource in;

	public Skeleton(ImageResource in, boolean w, boolean d, int...intArgs){
                super(in.getWidth(), in.getHeight(),2, w, d, 5, intArgs);
                this.in = in;
	}
	
	@Override
	public void doYourThing() {
	    //Consider all pixels on the boundaries of foreground regions (i.e. foreground points that have
	    //at least one background neighbor). Delete any such point that has more
	    //than one foreground neighbor, as long as doing so does not locally disconnect
	    //(i.e. split into two) the region containing that pixel. Iterate until convergence.
		

	}

}

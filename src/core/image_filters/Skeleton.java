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
		
		List<int[]> strElems = new ArrayList<int[]>();
		
		strElems.add(StructElements.l1);
		strElems.add(StructElements.r1);
		strElems.add(StructElements.l2);
		strElems.add(StructElements.r2);
		strElems.add(StructElements.l3);
		strElems.add(StructElements.r3);
		strElems.add(StructElements.l4);
		strElems.add(StructElements.r4);
		
		Pixel p;
		RoundIteratorOfPixels riop = new RoundIteratorOfPixels(in);
		int indexRoundIter = 0;
		int compareFromPi = -1;
		int compareFromElem = -1;
		boolean passed = true;


	}
	
	private static class StructElements{
		
		static final int [] l1 = new int []{0,0,0,-1,1,1,1,-1};
		static final int [] r1 = new int []{-1,0,0,0,-1,1,-1,1};
		static final int [] l2 = new int []{1,-1,0,0,0,-1,1,1};
		static final int [] r2 = new int []{-1,1,-1,0,0,0,-1,1};
		static final int [] l3 = new int []{1,1,1,-1,0,0,0,-1};
		static final int [] r3 = new int []{-1,1,-1,1,-1,0,0,0};
		static final int [] l4 = new int []{0,-1,1,1,1,-1,0,0};
		static final int [] r4 = new int []{0,0,-1,1,-1,1,-1,0};

	}
}

package core.image_filters.filter_utils;

public class ChunksNotMessedAssertion {

	public static boolean assertOK(int xSize, int ySize, int [] values, int border){
		boolean halt = false;
        halt = (values[0] != 0 && values[0] < border) ? true : false;
        if(halt) return true;
        halt = (values[1] != xSize && values[1] > xSize - border) ? true : false;
        if(halt) return true;
        halt = (values[2] != 0 && values[2] < border) ? true : false;
        if(halt) return true;
        halt = (values[3] != xSize && values[3] > xSize - border) ? true : false;
        if(halt) return true;else return false;
	}
	
}

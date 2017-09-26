package core.image_filters.filter_utils;

public class ChunksNotMessedAssertion {

	public static boolean assertOK(int xSize, int ySize, int [] values, int border){
		boolean halt = false;
		
        halt = (values[0] != 0 && values[0] < border) ? true : false;
        if(halt) {dumpMoreInfo(0, xSize, ySize, values, border); return true;}
        
        halt = (values[1] != xSize && values[1] > xSize - border) ? true : false;
        if(halt) {dumpMoreInfo(1, xSize, ySize, values, border); return true;}
        
        halt = (values[2] != 0 && values[2] < border) ? true : false;
        if(halt) {dumpMoreInfo(2, xSize, ySize, values, border); return true;}
        
        halt = (values[3] != ySize && values[3] > ySize - border) ? true : false;
        if(halt) {dumpMoreInfo(3, xSize, ySize, values, border); return true;} else return false;
	}
	
	private static void dumpMoreInfo(int whichCondition, int xSize, int ySize, int [] values, int border){
		System.out.println("\nChunksNotMessedAssertion");
		System.out.println("xSize " + xSize);
		System.out.println("ySize " + ySize);
		System.out.println("border " + border);
		System.out.println("\nwhichCondition " + whichCondition);
		System.out.println("\nvalues");
		for (int i : values) System.out.println(i);
	}
}

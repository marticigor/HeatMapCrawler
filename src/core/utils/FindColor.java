package core.utils;

public class FindColor {

	// public static RGB findColor(int myValue, int limit){
	//
	// if (myValue == 0) myValue = 1;
	// if (myValue>limit) myValue = limit-1;
	// int half = limit/2;
	// double coef = (double)255/half;
	// int red; int green;
	// if(myValue>=half) red = 255; else{
	// red = (int)(myValue*coef);
	// }
	// if(myValue<=half) green = 255;else{
	// green = (int)(255-((myValue-half)*coef));
	// }
	// return new RGB(red,green,0);
	// }

	public static RGB findColorII(int myValue, int limit) {
		if (myValue == 0)
			myValue = 1;
		if (myValue >= limit)
			myValue = limit - 1;
		double coef = (double) 255 / limit;
		int red = (int) Math.floor(myValue * coef);
		// int green = (int) Math.floor((limit - myValue) * coef);
		return new RGB(red, 0, 0);
	}

	public static class RGB {
		public RGB(int r, int g, int b) {
			this.red = r;
			this.green = g;
			this.blue = b;
		}

		public int red = 0;
		public int green = 0;
		public int blue = 0;
	}
}

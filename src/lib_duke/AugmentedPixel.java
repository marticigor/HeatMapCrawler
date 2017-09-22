package lib_duke;

public class AugmentedPixel{

	private int verGradient;
	private int horGradient;
	private double gradientComputed = Double.MIN_VALUE;
	private double direction;

	public AugmentedPixel(int v, int h) {
		this.verGradient = v;
		this.horGradient = h;
	}
	
	public int getVerGr(){return verGradient;}
	public int getHorGr(){return horGradient;}
	public double getGradientComputed(){return gradientComputed;}
	public double getDirection(){
	    return direction;
	}

	public void compute(){
		//hypot function
		gradientComputed = Math.sqrt((verGradient * verGradient) + (horGradient * horGradient));
	    //atan2 function
		direction = Math.atan2((double)(verGradient), (double)(horGradient));//19 widening primitive conversions
	}
}


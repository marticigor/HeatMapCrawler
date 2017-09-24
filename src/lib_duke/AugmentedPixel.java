package lib_duke;

import core.image_filters.filter_utils.DirectionEnumeration;
import core.image_filters.filter_utils.DirectionEnumeration.Direction;

public class AugmentedPixel{

	private int verGradient;
	private int horGradient;
	private double gradientComputed = Double.MIN_VALUE;
	private double dirDeg;
	private Direction dirEnum = Direction.ERROR;

	public AugmentedPixel(int v, int h) {
		this.verGradient = v;
		this.horGradient = h;
	}
	
	public int getVerGr(){return verGradient;}
	public int getHorGr(){return horGradient;}
	public double getGradientComputed(){return gradientComputed;}
	public double getDirection(){
		if(dirEnum == Direction.ERROR)throw new RuntimeException("enum still ERROR");
	    return dirDeg;
	}
	public Direction getDirEnum(){
		if(dirEnum == Direction.ERROR)throw new RuntimeException("enum still ERROR");
		return dirEnum;
	}

	public void compute(){
		//hypot function
		gradientComputed = Math.sqrt((verGradient * verGradient) + (horGradient * horGradient));
	    //atan2 function
		dirDeg = 180 + Math.toDegrees(Math.atan2((double)(verGradient), (double)(horGradient)));//19 widening primitive conversions
		dirEnum = DirectionEnumeration.getDirection(dirDeg);
	}
}


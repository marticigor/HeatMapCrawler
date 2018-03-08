package core.salient_areas_detectors.utils;

import ifaces.I_ColorScheme;
import lib_duke.ImageResource;
import lib_duke.Pixel;

public class UtilMethods implements I_ColorScheme {

	public UtilMethods(int surfaceConstant1_1, int surfaceConstant1_2, int surfaceConstant2_1, int surfaceConstant2_2,
			int neighbourghsConstant, int borderInSharpenStage, int lookAheadAndBack, int width, int height,
			ImageResource workBench, ImageResource projectWorkInto, ImageResource testAgainst) {
		// super();
		this.surfaceConstant1_1 = surfaceConstant1_1;
		this.surfaceConstant1_2 = surfaceConstant1_2;
		this.surfaceConstant2_1 = surfaceConstant2_1;
		this.surfaceConstant2_2 = surfaceConstant2_2;
		this.neighbourghsConstant = neighbourghsConstant;
		this.borderInSharpenStage = borderInSharpenStage;
		this.lookAheadAndBack = lookAheadAndBack;
		this.width = width;
		this.height = height;
		this.testAgainst = testAgainst;
	}

	private int surfaceConstant1_1, surfaceConstant1_2, surfaceConstant2_1, surfaceConstant2_2;
	private int neighbourghsConstant;
	private int borderInSharpenStage, lookAheadAndBack;
	private int width;
	private int height;
	private ImageResource testAgainst;

	/**
	 * 
	 * @param surface
	 * @param routableNeighbours
	 * @return
	 */
	public boolean evaluateAgainstConstants(int surface, int routableNeighbours) {
		boolean firstInterval = (surface > surfaceConstant1_1 && surface <= surfaceConstant1_2);
		boolean secondInterval = (surface > surfaceConstant2_1 && surface <= surfaceConstant2_2);
		boolean neighbours = routableNeighbours > neighbourghsConstant;
		return (firstInterval || secondInterval) && neighbours;
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isOkBorders(int x, int y) {
		boolean okX = (x >= borderInSharpenStage + lookAheadAndBack)
				&& (x < width - borderInSharpenStage - lookAheadAndBack);
		boolean okY = (y >= borderInSharpenStage + lookAheadAndBack)
				&& (y < height - borderInSharpenStage - lookAheadAndBack);
		return okX && okY;
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isSalientPixInAnother(int x, int y) {
		Pixel salientInTh = testAgainst.getPixel(x, y);
		if (isSetToClusterAround(salientInTh))
			return true;
		else
			return false;
	}

	/**
	 * 
	 * @param pIn
	 * @return
	 */
	public boolean isForeground(Pixel pIn) {
		return pIn.getRed() == redScheme[0] && pIn.getGreen() == redScheme[1] && pIn.getBlue() == redScheme[2];
	}

	/**
	 * 
	 * @param pIn
	 * @return
	 */
	public boolean isBackground(Pixel pIn) {
		return !isForeground(pIn);
	}

	/**
	 * 
	 * @param p
	 * @return
	 */
	public boolean isSetToClusterAround(Pixel p) {
		if (p.getRed() == whiteScheme[0] && p.getGreen() == whiteScheme[1] && p.getBlue() == whiteScheme[2])
			return true;
		return false;
	}

	/**
	 * 
	 * @param p
	 */
	public void setWhite(Pixel p) {
		p.setRed(whiteScheme[0]);
		p.setGreen(whiteScheme[1]);
		p.setBlue(whiteScheme[2]);
	}

	/**
	 * 
	 * @param p
	 */
	public void setRed(Pixel p) {
		p.setRed(redScheme[0]);
		p.setGreen(redScheme[1]);
		p.setBlue(redScheme[2]);
	}
}

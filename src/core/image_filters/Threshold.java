package core.image_filters;

import ifaces.I_ColorScheme;
import lib_duke.ImageResource;
import lib_duke.Pixel;

public class Threshold extends BaseFilter implements I_ColorScheme {

	private final int devToMakeItValidRoutable;
	private final ImageResource in, out;

	public Threshold(ImageResource in, ImageResource out, int border, boolean w, boolean d, int... intArgs) {
		super(in.getWidth(), in.getHeight(), border, w, d, 6, intArgs);// hardcoded
																		// border!
		devToMakeItValidRoutable = intArgs[5];
		this.in = in;
		this.out = out;
	}

	/**
	 * 
	 */
	@Override
	public void doYourThing() {

		Pixel outP;
		Pixel inP;
		for (int y = heightFrom; y < heightTo; y++) {
			for (int x = widthFrom; x < widthTo; x++) {

				inP = in.getPixel(x, y);
				outP = out.getPixel(inP.getX(), inP.getY());

				if (  (inP.getRed() == inP.getGreen() && inP.getGreen() == inP.getBlue()) ||
						isWatermarkInSomeCases(inP)
						) {

					// valueOfGray = inP.getRed();
					// outP.setRed(valueOfGray);
					// outP.setGreen(valueOfGray);
					// outP.setBlue(valueOfGray);

					outP.setRed(0);
					outP.setGreen(0);
					outP.setBlue(0);

				} else if (isRoutable(inP)
						&& (inP.getX() >= borderSharpenStage && inP.getX() < in.getWidth() - borderSharpenStage)
						&& (inP.getY() >= borderSharpenStage && inP.getY() < in.getHeight() - borderSharpenStage)) {
					outP.setRed(redScheme[0]);
					outP.setGreen(redScheme[1]);
					outP.setBlue(redScheme[2]);
				} else {
					// outP.setRed(lightGreenScheme[0]);
					// outP.setGreen(lightGreenScheme[1]);
					// outP.setBlue(lightGreenScheme[2]);
				}

			}
		}
		if (debug)
			System.out.println("CHUNK PROCESSED " + System.currentTimeMillis());
	}

	private int avg;
	private int dev;

	/**
	 *
	 */
	private boolean isRoutable(Pixel p) {

		avg = (p.getRed() + p.getGreen() + p.getBlue()) / 3;
		dev = 0;
		dev += Math.abs(p.getRed() - avg);
		dev += Math.abs(p.getGreen() - avg);
		dev += Math.abs(p.getBlue() - avg);

		if (dev > devToMakeItValidRoutable)
			return true;
		else
			return false;
	}

	/*
	 * switch this feature on in the rare case of a watermark in right bottom
	 */
	private boolean isWatermarkInSomeCases(Pixel p) {
		
		return false;
		//COMMENT THIS OUT WHEN NO WATERMARK
		//if (p.getX() > 856 && p.getY() > 1006)
			//return true;
		//else
			//return false;
	}

}
package core.image_filters;

import lib_duke.ImageResource;
import lib_duke.Pixel;

public class JustCopy extends BaseFilter {

	private ImageResource in, out;

	public JustCopy(ImageResource in, ImageResource out, int border, boolean w, boolean d, int... intArgs) {
		super(in.getWidth(), in.getHeight(), border, w, d, 5, intArgs);
		this.in = in;
		this.out = out;
	}

	@Override
	public void doYourThing() {

		Pixel pIn, pOut;
		for (int absX = widthFrom; absX < widthTo; absX++) {
			for (int absY = heightFrom; absY < heightTo; absY++) {
				pIn = in.getPixel(absX, absY);
				pOut = out.getPixel(absX, absY);
				pOut.setRed(pIn.getRed());
				pOut.setGreen(pIn.getGreen());
				pOut.setBlue(pIn.getBlue());
			}
		}
	}
}

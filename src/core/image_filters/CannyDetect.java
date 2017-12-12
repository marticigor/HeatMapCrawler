package core.image_filters;

import java.util.HashMap;
import java.util.Map;

import core.ImagePreprocesor;
import ifaces.I_ColorScheme;
import lib_duke.AugmentedPixel;
import lib_duke.ImageResource;
import lib_duke.Pixel;

public class CannyDetect extends BaseFilter implements I_ColorScheme {

	private Map<Pixel, AugmentedPixel> toAugmented = new HashMap<Pixel, AugmentedPixel>();
	private ImagePreprocesor ip;
	private ImageResource in;

	public CannyDetect(ImageResource in, ImagePreprocesor ip, boolean w, boolean d, int... intArgs) {
		super(in.getWidth(), in.getHeight(), Sobel.KERNEL_BORDER, w, d, 5, intArgs);
		this.ip = ip;
		this.in = in;
	}

	static class Sobel {
		private static final int KERNEL_BORDER = 1;
		private static final int[][] HOR_KERNEL = new int[][] { { 1, 0, -1 }, { 2, 0, -2 }, { 1, 0, -1 } };
		private static final int[][] VER_KERNEL = new int[][] { { 1, 2, 1 }, { 0, 0, 0 }, { -1, -2, -1 } };
	}

	@Override
	public void doYourThing() {

		int borderS = Sobel.KERNEL_BORDER;

		int countX, countY;
		int verGrad, horGrad;

		Pixel inPix = null;

		for (int absX = widthFrom; absX < widthTo; absX++) {
			for (int absY = heightFrom; absY < heightTo; absY++) {

				// SobelOperator
				// iterate kernel
				countX = 0;
				countY = 0;

				horGrad = 0;
				verGrad = 0;

				for (int absKernelX = absX - borderS; absKernelX < absX + borderS + 1; absKernelX++) {
					for (int absKernelY = absY - borderS; absKernelY < absY + borderS + 1; absKernelY++) {

						inPix = in.getPixel(absKernelX, absKernelY);
						horGrad += inPix.getRed() * Sobel.HOR_KERNEL[countX][countY];
						verGrad += inPix.getRed() * Sobel.VER_KERNEL[countX][countY];

						countY++;
					}
					countY = 0;
					countX++;
				}

				AugmentedPixel augPix = new AugmentedPixel(verGrad, horGrad);
				inPix = in.getPixel(absX, absY);
				toAugmented.put(inPix, augPix);

			}
		}
		ip.addMap(toAugmented);
		if (debug)
			System.out.println("toAugmented.size() in CannyDetect " + toAugmented.size());
	}

	public Map<Pixel, AugmentedPixel> getToAugmented() {
		return toAugmented;
	}
}

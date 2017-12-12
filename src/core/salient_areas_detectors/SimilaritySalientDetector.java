package core.salient_areas_detectors;

import java.util.LinkedList;
import java.util.List;

import core.salient_areas_detectors.utils.UtilMethods;
import lib_duke.ImageResource;
import lib_duke.Pixel;
import ifaces.I_ColorScheme;
import ifaces.I_SalientDetector;

public class SimilaritySalientDetector implements I_SalientDetector, I_ColorScheme {

	public SimilaritySalientDetector(ImageResource workBench, ImageResource noded, ImageResource testAgainst,
			int borderInSharpenStage, int lookAheadAndBack, int surfaceConstant1_1, int surfaceConstant1_2,
			int surfaceConstant2_1, int surfaceConstant2_2, int neighbourghsConstant, boolean visual, boolean debug) {

		this.workBench = workBench;
		this.noded = noded;
		this.testAgainst = testAgainst;
		this.borderInSharpenStage = borderInSharpenStage;
		this.lookAheadAndBack = lookAheadAndBack;
		this.width = workBench.getWidth();
		this.height = workBench.getHeight();
		this.surfaceConstant1_1 = surfaceConstant1_1;
		this.surfaceConstant1_2 = surfaceConstant1_2;
		this.surfaceConstant2_1 = surfaceConstant2_1;
		this.surfaceConstant2_2 = surfaceConstant2_2;
		this.neighbourghsConstant = neighbourghsConstant;
		this.visual = visual;
		this.debug = debug;

		this.utils = new UtilMethods(surfaceConstant1_1, surfaceConstant1_2, surfaceConstant2_1, surfaceConstant2_2,
				neighbourghsConstant, borderInSharpenStage, KERNEL_BORDERS, width, height, workBench, noded,
				testAgainst);
	}

	@SuppressWarnings("unused")
	private ImageResource workBench, noded, testAgainst;
	@SuppressWarnings("unused")
	private int borderInSharpenStage, lookAheadAndBack;
	private int width, height;
	@SuppressWarnings("unused")
	private int surfaceConstant1_1, surfaceConstant1_2, surfaceConstant2_1, surfaceConstant2_2, neighbourghsConstant;
	@SuppressWarnings("unused")
	private boolean visual, debug;
	private UtilMethods utils;

	@Override
	public void detectSalientAreas(boolean testAgainstAnotherImageResource) {

		int match;
		List<int[][]> kernels = new LinkedList<int[][]>();
		kernels.add(KERNEL_VER);
		kernels.add(KERNEL_HOR);
		kernels.add(KERNEL_DIAG_BACKWARD);
		kernels.add(KERNEL_DIAG_FORWARD);

		for (int x = borderInSharpenStage + KERNEL_BORDERS; x < width - (borderInSharpenStage + KERNEL_BORDERS); x++) {
			for (int y = borderInSharpenStage + KERNEL_BORDERS; y < height
					- (borderInSharpenStage + KERNEL_BORDERS); y++) {
				Pixel isItRed = workBench.getPixel(x, y);
				if (isItRed.getRed() != redScheme[0])
					continue;
				// ----------------------------------------------------------------
				if (utils.isOkBorders(x, y)) {
					// do stuff
					utils.setWhite(noded.getPixel(x, y));
					Pixel skeleton = null;
					// iterate kernels
					for (int[][] kernel : kernels) {
						match = 0;
						int xKer = -1;
						int yKer;
						for (int xWorkBench = x - KERNEL_BORDERS; xWorkBench < x + KERNEL_BORDERS + 1; xWorkBench++) {
							xKer++;
							yKer = -1;
							for (int yWorkBench = y - KERNEL_BORDERS; yWorkBench < y + KERNEL_BORDERS
									+ 1; yWorkBench++) {
								yKer++;
								// count matches
								skeleton = workBench.getPixel(xWorkBench, yWorkBench);
								int valueSkeleton = (skeleton.getRed() == redScheme[0]) ? 1 : 0;
								int valueKernel = kernel[xKer][yKer];
								if (valueSkeleton == valueKernel)
									match++;
								else
									match--;
							}
						}
						if (debug)
							System.out.println("SIMILARITY_SALIENT_DETECTOR match " + match);
						if (match > MATCH_THRESHOLD) {
							utils.setRed(noded.getPixel(x, y));
							break;
						}
					} // iterate kernels
				} else {
					// do not take any action based on incomplete kernel
					// convolution
				}
				// ----------------------------------------------------------------
			}
		}
	}

	// -------------------------------------------------
	private static final int MATCH_THRESHOLD = 5;// 5
	// -------------------------------------------------
	private static final int KERNEL_BORDERS = 1;

	private static final int[][] KERNEL_VER = new int[][] { { 0, 1, 0 }, { 0, 1, 0 }, { 0, 1, 0 } };

	private static final int[][] KERNEL_HOR = new int[][] { { 0, 0, 0 }, { 1, 1, 1 }, { 0, 0, 0 } };

	private static final int[][] KERNEL_DIAG_FORWARD = new int[][] { { 0, 0, 1 }, { 0, 1, 0 }, { 1, 0, 0 } };
	private static final int[][] KERNEL_DIAG_BACKWARD = new int[][] { { 1, 0, 0 }, { 0, 1, 0 }, { 0, 0, 1 } };

	/*
	 * private static final int MATCH_THRESHOLD = 17; private static final int
	 * KERNEL_BORDERS = 2;
	 * 
	 * private static final int[][] KERNEL_VER = new int[][] { { 0,0,1,0,0 }, {
	 * 0,0,1,0,0 }, { 0,0,1,0,0 }, { 0,0,1,0,0 }, { 0,0,1,0,0 } };
	 * 
	 * private static final int[][] KERNEL_HOR = new int[][] { { 0,0,0,0,0 }, {
	 * 0,0,0,0,0 }, { 1,1,1,1,1 }, { 0,0,0,0,0 }, { 0,0,0,0,0 } };
	 * 
	 * private static final int[][] KERNEL_DIAG_FORWARD = new int[][] { {
	 * 0,0,0,0,1 }, { 0,0,0,1,0 }, { 0,0,1,0,0 }, { 0,1,0,0,0 }, { 1,0,0,0,0 }
	 * }; private static final int[][] KERNEL_DIAG_BACKWARD = new int[][] { {
	 * 1,0,0,0,0 }, { 0,1,0,0,0 }, { 0,0,1,0,0 }, { 0,0,0,1,0 }, { 0,0,0,0,1 }
	 * };
	 */
}
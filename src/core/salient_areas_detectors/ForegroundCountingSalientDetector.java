package core.salient_areas_detectors;

import java.util.HashSet;
import java.util.Set;

import core.salient_areas_detectors.utils.UtilMethods;
import core.utils.RoundIteratorOfPixels;
import lib_duke.ImageResource;
import lib_duke.Pixel;
import ifaces.I_ColorScheme;
import ifaces.I_SalientDetector;

public class ForegroundCountingSalientDetector implements I_SalientDetector, I_ColorScheme {

	public ForegroundCountingSalientDetector(ImageResource workBench, ImageResource noded, ImageResource testAgainst,
			int borderInSharpenStage, int lookAheadAndBack, int surfaceConstant1_1, int surfaceConstant1_2,
			int surfaceConstant2_1, int surfaceConstant2_2, int neighbourghsConstant, boolean visual, boolean debug) {

		this.workBench = workBench;
		this.noded = noded;
		this.borderInSharpenStage = borderInSharpenStage;
		this.lookAheadAndBack = lookAheadAndBack;
		this.width = workBench.getWidth();
		this.height = workBench.getHeight();
		this.visual = visual;
		this.debug = debug;

		/*
		 * int surfaceConstant1_1, int surfaceConstant1_2, int surfaceConstant2_1, int
		 * surfaceConstant2_2, int neighbourghsConstant, int borderInSharpenStage, int
		 * lookAheadAndBack, int width, int height, ImageResource workBench,
		 * ImageResource projectWorkInto, ImageResource testAgainst
		 */

		this.utils = new UtilMethods(surfaceConstant1_1, surfaceConstant1_2, surfaceConstant2_1, surfaceConstant2_2,
				neighbourghsConstant, borderInSharpenStage, lookAheadAndBack, width, height, workBench, noded,
				testAgainst);
		System.out.println(LOG_TAG + utils.toString());
	}

	private ImageResource workBench, noded;
	private int borderInSharpenStage, lookAheadAndBack;
	private int width, height;
	@SuppressWarnings("unused")
	private boolean visual, debug;
	private UtilMethods utils;
	private final static String LOG_TAG = "ForegroundCountingSalientDetector: ";

	@Override
	public void detectSalientAreas(boolean testAgainstAnotherImageResource) {

		System.out.println(LOG_TAG + "border sharpen stage " + borderInSharpenStage);

		RoundIteratorOfPixels iteratorRound = new RoundIteratorOfPixels();
		iteratorRound.setImageResource(workBench);
		this.width = workBench.getWidth();
		this.height = workBench.getHeight();
		Set<Pixel> toBeWhite = new HashSet<Pixel>();

		// case
		// 000
		// 0XX
		// 000
		for (int x = lookAheadAndBack; x < width - lookAheadAndBack; x++) {
			for (int y = lookAheadAndBack; y < height - lookAheadAndBack; y++) {

				Pixel p = workBench.getPixel(x, y);
				Pixel toRed = null;
				if (utils.isBackground(p))
					continue;
				else {
					// TODO COHESION!
					toRed = noded.getPixel(p.getX(), p.getY());
					utils.setRed(toRed);
				} // TODO skeletonize does not use red scheme?

				iteratorRound.setPixelToCheckAround(p);

				int count = 0;
				for (Pixel pIt : iteratorRound) {
					if (utils.isForeground(pIt))
						count++;
				}

				// do not do anything based on heuristics affected by borders

				if (count == 1 && utils.isOkBorders(x, y))
					toBeWhite.add(noded.getPixel(x, y));
			}
		}

		Pixel p = null;
		Pixel pIn = null;

		int surfaceArea = 0;
		int routableNeighbours = 0;

		int minSurface = Integer.MAX_VALUE;
		int maxSurface = Integer.MIN_VALUE;
		//
		// finds salient pixels that probably belong to a region of
		// interest in terms of future node.
		//
		for (int x = lookAheadAndBack; x < width - lookAheadAndBack; x++) {
			for (int y = lookAheadAndBack; y < height - lookAheadAndBack; y++) {

				// INTERSECTION
				if (testAgainstAnotherImageResource && !utils.isSalientPixInAnother(x, y))
					continue;

				p = workBench.getPixel(x, y);

				if (utils.isForeground(p)) {

					for (int xIn = x - lookAheadAndBack; xIn < x + lookAheadAndBack + 1; xIn++) {
						for (int yIn = y - lookAheadAndBack; yIn < y + lookAheadAndBack + 1; yIn++) {

							pIn = workBench.getPixel(xIn, yIn);

							if (utils.isForeground(pIn)) {

								routableNeighbours++;
								iteratorRound.setPixelToCheckAround(pIn);

								for (Pixel p1 : iteratorRound) {
									if (utils.isBackground(p1))
										surfaceArea++;
								}
							}
						}
					}

					if (utils.evaluateAgainstConstants(surfaceArea, routableNeighbours)) {

						// do not do anything based on heuristics affected by
						// borders

						if (utils.isOkBorders(x, y)) {
							toBeWhite.add(noded.getPixel(x, y));
						}

					}

					if (surfaceArea > maxSurface)
						maxSurface = surfaceArea;
					if (surfaceArea < minSurface)
						minSurface = surfaceArea;

					surfaceArea = 0;
					routableNeighbours = 0;
				}
			}
		}

		for (Pixel px : toBeWhite) {
			utils.setWhite(px);
		}

		System.out.println(LOG_TAG + "Surface min max " + minSurface + " " + maxSurface);
	}
}

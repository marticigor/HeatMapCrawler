package core.salient_areas_detectors;

import java.util.HashSet;
import java.util.Set;

import core.utils.RoundIteratorOfPixels;
import lib_duke.ImageResource;
import lib_duke.Pixel;
import ifaces.I_ColorScheme;
import ifaces.I_SalientDetector;

public class ForegroundCountingSalientDetector implements I_SalientDetector,
		I_ColorScheme {

	public ForegroundCountingSalientDetector(ImageResource workBench,
			ImageResource noded, ImageResource testAgainst,
			int borderInSharpenStage, int lookAheadAndBack,
			int surfaceConstant1_1, int surfaceConstant1_2,
			int surfaceConstant2_1, int surfaceConstant2_2,
			int neighbourghsConstant, boolean visual, boolean debug) {

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
	}

	private ImageResource workBench, noded, testAgainst;
	private int borderInSharpenStage, lookAheadAndBack;
	private int width, height;
	private int surfaceConstant1_1, surfaceConstant1_2, surfaceConstant2_1,
			surfaceConstant2_2, neighbourghsConstant;
	private boolean visual, debug;

	@Override
	public void detectSalientAreas(boolean testAgainstAnotherImageResource) {

		System.out.println("border sharpen stage " + borderInSharpenStage);

		RoundIteratorOfPixels iteratorRound = new RoundIteratorOfPixels();
		iteratorRound.setImageResource(workBench);
		this.width = workBench.getWidth();
		this.height = workBench.getHeight();
		Set<Pixel> toBeWhite = new HashSet<Pixel>();

		for (int x = lookAheadAndBack; x < width - lookAheadAndBack; x++) {
			for (int y = lookAheadAndBack; y < height - lookAheadAndBack; y++) {

				Pixel p = workBench.getPixel(x, y);
				if (!isForeground(p))
					continue;

				iteratorRound.setPixelToCheckAround(p);

				int count = 0;
				for (Pixel pIt : iteratorRound) {
					if (isForeground(pIt))
						count++;
				}

				// do nothing based on heuristics affected by borders

				if (count == 1 && isOkBorders(x, y))
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

				if (testAgainstAnotherImageResource
						&& !isSalientPixInAnother(x, y))
					continue;

				p = workBench.getPixel(x, y);

				if (isForeground(p)) {

					for (int xIn = x - lookAheadAndBack; xIn < x
							+ lookAheadAndBack + 1; xIn++) {
						for (int yIn = y - lookAheadAndBack; yIn < y
								+ lookAheadAndBack + 1; yIn++) {

							pIn = workBench.getPixel(xIn, yIn);

							if (isForeground(pIn)) {

								routableNeighbours++;
								iteratorRound.setPixelToCheckAround(pIn);

								for (Pixel p1 : iteratorRound) {
									if (isBackground(p1))
										surfaceArea++;
								}
							}
						}
					}

					if (evaluateAgainstConstants(surfaceArea,
							routableNeighbours)) {

						// do nothing based on heuristics affected by borders

						if (isOkBorders(x, y)) {
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
			setWhite(px);
		}

		if (visual || debug)
			System.out.println("Surface min max " + minSurface + " "
					+ maxSurface);
	}

	/**
	 * 
	 * @param surface
	 * @param routableNeighbours
	 * @return
	 */
	private boolean evaluateAgainstConstants(int surface, int routableNeighbours) {
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
	private boolean isOkBorders(int x, int y) {
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
	private boolean isSalientPixInAnother(int x, int y) {
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
	private boolean isForeground(Pixel pIn) {
		return pIn.getRed() == redScheme[0] && pIn.getGreen() == redScheme[1]
				&& pIn.getBlue() == redScheme[2];
	}

	/**
	 * 
	 * @param pIn
	 * @return
	 */
	private boolean isBackground(Pixel pIn) {
		return !isForeground(pIn);
	}

	/**
	 * 
	 * @param p
	 * @return
	 */
	private boolean isSetToClusterAround(Pixel p) {
		if (p.getRed() == whiteScheme[0] && p.getGreen() == whiteScheme[1]
				&& p.getBlue() == whiteScheme[2])
			return true;
		return false;
	}

	/**
	 * 
	 * @param p
	 */
	private void setWhite(Pixel p) {
		p.setRed(whiteScheme[0]);
		p.setGreen(whiteScheme[1]);
		p.setBlue(whiteScheme[2]);
	}

}

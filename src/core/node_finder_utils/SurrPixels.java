package core.node_finder_utils;

import core.utils.RoundIteratorOfPixels;
import ifacec.node_finder.I_PixelExam;
import lib_duke.ImageResource;
import lib_duke.Pixel;

public class SurrPixels implements I_PixelExam {

	private int[] colorScheme;
	private RoundIteratorOfPixels iteratorRound;

	public SurrPixels(ImageResource ir, int[] colorScheme) {
		this.colorScheme = colorScheme;
		iteratorRound = new RoundIteratorOfPixels(ir);
		iteratorRound.setImageResource(ir);
		assert (colorScheme.length == 3);
	}

	@Override
	public int exam(Pixel p) {
		int neighbours = 0;
		iteratorRound.setPixelToCheckAround(p);
		for (Pixel myPx : iteratorRound) {
			if (myPx.getRed() == colorScheme[0] && myPx.getGreen() == colorScheme[1]
					&& myPx.getBlue() == colorScheme[2])
				neighbours++;
		}
		return neighbours;
	}
}

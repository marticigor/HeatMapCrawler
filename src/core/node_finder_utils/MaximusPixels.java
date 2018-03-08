package core.node_finder_utils;

import java.util.*;

import ifaces.node_finder.I_PixelExam;
import ifaces.node_finder.I_PixelSelector;
import lib_duke.Pixel;

public class MaximusPixels implements I_PixelSelector {

	private Set<Pixel> outputSet = new HashSet<Pixel>();

	@Override
	public void proces(Set<Pixel> inputSet, I_PixelExam getSurr, int... args) {
		assert (args.length == 1);
		assert (args[0] > -1 && args[0] < 9);
		assert (inputSet.size() > 0);

		int max = args[0];
		int surr;
		for (Pixel p : inputSet) {
			surr = getSurr.exam(p);
			if (surr == max)
				outputSet.add(p);
		}
	}

	@Override
	public Set<Pixel> getSet() {
		return outputSet;
	}
}

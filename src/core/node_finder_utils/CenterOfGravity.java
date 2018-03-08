package core.node_finder_utils;

import java.util.*;

import ifaces.node_finder.I_PixelExam;
import ifaces.node_finder.I_PixelSelector;
import lib_duke.Pixel;

public class CenterOfGravity implements I_PixelSelector {

	private double centerGravityX, centerGravityY;
	private Set<Pixel> outputSet = new HashSet<Pixel>();

	@Override
	public void proces(Set<Pixel> inputSet, I_PixelExam exam, int... args) {
		assert (exam == null);
		assert (args.length == 1 && args[0] == 0);
		assert (inputSet.size() > 0);

		computeCenterOfGravity(inputSet);
		double minDist = Double.MAX_VALUE;
		double currDist;
		for (Pixel p : inputSet) {
			currDist = distToCenter(p);
			if (currDist < minDist)
				minDist = currDist;
		}
		for (Pixel p : inputSet) {
			if (distToCenter(p) == minDist)
				outputSet.add(p);
		}
	}

	private void computeCenterOfGravity(Set<Pixel> cluster) {
		assert (cluster.size() > 0);
		int sumX = 0;
		int sumY = 0;
		for (Pixel p : cluster) {
			sumX += p.getX();
			sumY += p.getY();
		}
		centerGravityX = (double) sumX / (double) cluster.size();
		centerGravityY = (double) sumY / (double) cluster.size();
	}

	private double distToCenter(Pixel p) {
		double deltaX = Math.abs((double) p.getX() - centerGravityX);
		double deltaY = Math.abs((double) p.getY() - centerGravityY);
		return Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
	}

	@Override
	public Set<Pixel> getSet() {
		return outputSet;
	}

}

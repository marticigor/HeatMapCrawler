package core;

import java.util.*;

import core.utils.RoundIteratorOfPixels;
import ifaces.I_ColorScheme;
import lib_duke.ImageResource;
import lib_duke.Pixel;

public class RecursiveClusterFinder {

	private HashSet<Pixel> allClusterAroundNode;
	private RoundIteratorOfPixels iteratorRound;
	private ImageResource ir;
	private int red, green, blue;
	private boolean redClusterSearch = false;

	private boolean visualize;
	private Visualizer vis;

	/**
	 * called by NodeFinder
	 */
	public RecursiveClusterFinder(ImageResource ir, int red, int green, int blue) {

		this.ir = ir;
		this.red = red;
		this.green = green;
		this.blue = blue;

		iteratorRound = new RoundIteratorOfPixels();
		iteratorRound.setImageResource(ir);

		this.redClusterSearch = false;

		allClusterAroundNode = new HashSet<Pixel>();

	}

	/**
	 * called by AdjacencyFinder
	 */
	public RecursiveClusterFinder(ImageResource ir, int red, int green,
			int blue, boolean visualize) {

		this.ir = ir;
		this.red = red;
		this.green = green;
		this.blue = blue;

		this.visualize = visualize;

		if (visualize)
			vis = new Visualizer();

		this.redClusterSearch = true;

		iteratorRound = new RoundIteratorOfPixels();
		iteratorRound.setImageResource(ir);

		allClusterAroundNode = new HashSet<Pixel>();
	}

	HashSet<Pixel> neighbours;

	/**
	 * loads recursively set of (colorScheme) pixels into neighbors and then
	 * allClusterAroundNode maybe called from outside a few times (thus building
	 * allClusterAroundNode gradually)
	 */
	public void buildPartialCluster(Pixel p) {

		if (allClusterAroundNode.size() > 4000) {
			throw new RuntimeException(
					"RecursiveClusterFinder, setCluster() size");
		}

		if (!redClusterSearch)
			allClusterAroundNode.add(p);

		if (redClusterSearch && p.getRed() == red && p.getGreen() == green
				&& p.getBlue() == blue)
			allClusterAroundNode.add(p);

		if (visualize && allClusterAroundNode.size() == 1)
			vis.displayInitial();
		if (visualize && allClusterAroundNode.size() % 50 == 0)
			vis.displayProgres();

		iteratorRound.setPixelToCheckAround(p);

		neighbours = new HashSet<Pixel>();

		for (Pixel px : iteratorRound) {
			if (px.getRed() == red && px.getGreen() == green
					&& px.getBlue() == blue) {
				neighbours.add(px);
			}
		}

		for (Pixel pCopied : neighbours) {
			if (allClusterAroundNode.contains(pCopied))
				continue; // to skip recursion in many cases
			buildPartialCluster(pCopied); // now immerse into recursion
		}
	}

	/**
      * 
      */
	public HashSet<Pixel> getAllCluster() {
		return allClusterAroundNode;
	}

	/**
      *  
      */
	public void resetAllCluster() {
		allClusterAroundNode = new HashSet<Pixel>();
	}

	/**
      *  
      */
	class Visualizer implements I_ColorScheme {

		ImageResource visIr;
		RoundIteratorOfPixels riop;

		Visualizer() {
			visIr = new ImageResource(ir.getWidth(), ir.getHeight());
			riop = new RoundIteratorOfPixels();
			riop.setImageResource(visIr);
		}

		/**
          *  
          */
		public void displayInitial() {
			for (Pixel p : visIr.pixels()) {
				p.setRed(0);
				p.setGreen(0);
				p.setBlue(0);
			}

			for (Pixel p : allClusterAroundNode) {

				Pixel pVis = visIr.getPixel(p.getX(), p.getY());

				riop.setPixelToCheckAround(pVis);

				for (Pixel pHighlight : riop) {

					pHighlight.setRed(whiteScheme[0]);
					pHighlight.setGreen(whiteScheme[1]);
					pHighlight.setBlue(whiteScheme[2]);

				}
			}
		}

		/**
          *  
          */
		public void displayProgres() {

			for (Pixel p : allClusterAroundNode) {

				Pixel pVis = visIr.getPixel(p.getX(), p.getY());

				pVis.setRed(blueischScheme[0]);
				pVis.setGreen(blueischScheme[1]);
				pVis.setBlue(blueischScheme[2]);
			}
		}
	}
}

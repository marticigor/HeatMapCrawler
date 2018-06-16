package core;

import java.util.*;

import core.utils.RoundIteratorOfPixels;
import ifaces.I_ColorScheme;
import lib_duke.ImageResource;
import lib_duke.Pixel;

public class RecursiveClusterFinder implements I_ColorScheme {

	private HashSet<Pixel> allClusterAroundNode;
	private RoundIteratorOfPixels iteratorRound;
	private HashSet<Pixel> neighbours;
	private ImageResource ir;
	private boolean redClusterSearch = false;

	private boolean visualize;
	private Visualizer vis;

	public enum PixColors {
		WHITE, RED, YELLOW
	};

	private List<PixColors> colorsToCheck;
	private int maxClusterSize = Integer.MAX_VALUE;

	/**
	 * called by NodeFinder
	 */
	public RecursiveClusterFinder(ImageResource ir, int maxClusterSize) {

		this.ir = ir;
		this.maxClusterSize = maxClusterSize;

		iteratorRound = new RoundIteratorOfPixels();
		iteratorRound.setImageResource(ir);

		this.redClusterSearch = false;

		allClusterAroundNode = new HashSet<Pixel>();
		colorsToCheck = new LinkedList<PixColors>();
		colorsToCheck.add(PixColors.WHITE);

	}

	/**
	 * called by AdjacencyFinder
	 */
	public RecursiveClusterFinder(

			ImageResource ir, boolean visualize

	) {

		this.ir = ir;
		this.visualize = visualize;

		if (visualize)
			vis = new Visualizer();

		this.redClusterSearch = true;

		iteratorRound = new RoundIteratorOfPixels();
		iteratorRound.setImageResource(ir);

		allClusterAroundNode = new HashSet<Pixel>();
		colorsToCheck = new LinkedList<PixColors>();
		colorsToCheck.add(PixColors.RED);
		colorsToCheck.add(PixColors.YELLOW);
	}

	/**
	 * loads recursively set of (colorScheme) pixels into neighbors and then
	 * allClusterAroundNode. Maybe called from outside a few times (thus building
	 * allClusterAroundNode gradually)
	 */
	public void buildPartialCluster(Pixel p) {

		if (!redClusterSearch && allClusterAroundNode != null && allClusterAroundNode.size() > maxClusterSize)
			return;

		if (!redClusterSearch)
			allClusterAroundNode.add(p);

		if (redClusterSearch && (checkColors(p)))
			allClusterAroundNode.add(p);

		if (visualize && allClusterAroundNode.size() == 1)
			vis.displayInitial();
		if (visualize && allClusterAroundNode.size() % 50 == 0)
			vis.displayProgres();

		iteratorRound.setPixelToCheckAround(p);

		neighbours = new HashSet<Pixel>();

		for (Pixel px : iteratorRound) {
			if ((checkColors(px)) && !neighbours.contains(px)) {
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

	private boolean isRed(Pixel p) {
		return (p.getRed() == redScheme[0] && p.getGreen() == redScheme[1] && p.getBlue() == redScheme[2]);
	}

	private boolean isYellow(Pixel p) {
		return (p.getRed() == yellowScheme[0] && p.getGreen() == yellowScheme[1] && p.getBlue() == yellowScheme[2]);
	}

	private boolean isWhite(Pixel p) {
		return (p.getRed() == whiteScheme[0] && p.getGreen() == whiteScheme[1] && p.getBlue() == whiteScheme[2]);
	}

	private boolean checkColors(Pixel p) {
		for (PixColors pc : colorsToCheck) {
			switch (pc) {
			case WHITE:
				if (isWhite(p))
					return true;
				break;
			case RED:
				if (isRed(p))
					return true;
				break;
			case YELLOW:
				if (isYellow(p))
					return true;
				break;
			default:
				throw new RuntimeException("switch mess");
			}
		}
		return false;
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

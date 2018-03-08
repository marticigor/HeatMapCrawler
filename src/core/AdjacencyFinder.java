package core;

import java.util.*;

import core.image_morpho_transform.LocalyDisconnectTest;
import core.image_morpho_transform.Skeleton;
import core.utils.RoundIteratorOfPixels;
import ifaces.I_ColorScheme;
import lib_duke.ImageResource;
import lib_duke.LineMaker;
import lib_duke.Pixel;

public class AdjacencyFinder implements I_ColorScheme {

	private ImageResource noded;
	private List<Node> nodes;
	private RoundIteratorOfPixels riop;
	private HashSet<Pixel> redCluster;
	private RecursiveClusterFinder rcf;
	private ImageResource visualizeIR;
	private boolean visual;
	private boolean debug;

	private Map<Pixel, Node> mapPixToNode;
	private Set<Node> adjacentNodes;

	private int bottleneckSize, passableSize;
	private LocalyDisconnectTest ldt;
	private HashSet<Pixel> branch;
	private int borderInSharpenStage;

	// boolean investigative = false; //hardcore problem analysis approach.. sad

	public AdjacencyFinder(int borderInSharpenStage, ImageResource noded, List<Node> nodes, boolean visual,
			boolean debug, int bottleneckSize, int passableSize) {

		this.noded = noded;
		this.nodes = nodes;
		this.visual = visual;
		this.debug = debug;
		this.bottleneckSize = bottleneckSize;
		this.passableSize = passableSize;

		this.borderInSharpenStage = borderInSharpenStage;

		riop = new RoundIteratorOfPixels();
		riop.setImageResource(noded);

		mapPixToNode = new HashMap<Pixel, Node>();
		adjacentNodes = new HashSet<Node>();

		// public RecursiveClusterFinder(ImageResource ir, int red, int green,
		// int blue, boolean visualize)
		rcf = new RecursiveClusterFinder(noded, this.debug);

		if (this.visual) {
			visualizeIR = new ImageResource(noded.getWidth(), noded.getHeight());
		}

		Skeleton skeletonMock = new Skeleton(noded, borderInSharpenStage, true, false, -1, -1, -1, -1,
				borderInSharpenStage);

		// OuterClass.InnerClass innerObject = outerObject.new InnerClass();
		Skeleton.SkeletonUtils utils = skeletonMock.new SkeletonUtils(skeletonMock.getThresholdForeBack());
		ldt = new LocalyDisconnectTest(utils);

	}

	/**
	 * build all adjacency lists into nodes
	 */
	public void buildAdjacencyLists() {

		int nmbOfBottlenecks = 0;
		Pixel p;

		for (Node n : nodes) {
			if (isBottleNeck(n)) {
				n.setBottleneck(true);
				nmbOfBottlenecks++;
			}
		}

		System.out.println("Number of all nodes: " + nodes.size());
		System.out.println("Number of bottleneck nodes: " + nmbOfBottlenecks);

		for (Node n : nodes) {

			if (n.getBottleneck()) {
				p = noded.getPixel(n.getX(), n.getY());
				p.setRed(yellowScheme[0]);
				p.setGreen(yellowScheme[1]);
				p.setBlue(yellowScheme[2]);
			}
		}

		{ // scope for adjacency lists builder

			Pixel currP;
			int currX, currY;

			// this is first run - map of pixels gets created, later we can
			// demask green squares
			maskAllNodes(true); // boolean firstRun

			if (visual) {
				noded.draw();
				Pause.pause(2000);
			}

			if (visual && debug) {
				demaskAllNodes();
				noded.draw();
				Pause.pause(5000);
				maskAllNodes(false);
				noded.draw();
			}

			for (Node buildForThis : nodes) {

				// if(investigative) System.out.println("buildForThis: " +
				// buildForThis);

				maskOrDemaskNode(buildForThis, false); // demask this node, not
														// first run
				if (visual & debug) {
					noded.draw();
					Pause.pause(1000);
				}

				redCluster = new HashSet<Pixel>();

				currX = buildForThis.getX();
				currY = buildForThis.getY();
				currP = noded.getPixel(currX, currY);

				riop.setPixelToCheckAround(currP);

				// >>>>>>>>>>>>>>>>>>>>>>>>> around Node buildForThis
				for (Pixel iterP : riop) {

					if (!isRoutable(iterP))
						continue;
					// if(investigative) System.out.println("iterating around
					// this node " + iterP);
					putAllSurrReds(iterP);
				}

				// now redCluster is build for Node buildForThis
				adjacentNodes = getAdjacents(buildForThis);
				for (Node singleAdjacent : adjacentNodes) {
					buildForThis.addAdjacentNode(singleAdjacent);
				}

				adjacentNodes = new HashSet<Node>();// clear

				if (visual && debug) {
					for (Pixel pDebug : redCluster) {
						Pixel pIr = visualizeIR.getPixel(pDebug.getX(), pDebug.getY());
						pIr.setRed(blueischScheme[0]);
						pIr.setGreen(blueischScheme[1]);
						pIr.setBlue(blueischScheme[2]);
					}
					visualizeIR.draw();
					Pause.pause(50);
					for (Pixel pDebug : redCluster) {
						Pixel pIr = visualizeIR.getPixel(pDebug.getX(), pDebug.getY());
						pIr.setRed(redischScheme[0]);
						pIr.setGreen(redischScheme[1]);
						pIr.setBlue(redischScheme[2]);
					}
				} // visual && debug

				maskOrDemaskNode(buildForThis, true); // doMask

				if (visual & debug) {
					noded.draw();
					Pause.pause(50);
				}
			} // for buildForThis

			demaskAllNodes();

			if (visual) {
				noded.draw();
				Pause.pause(500);
			}

			RoundIteratorOfPixels highlightByColor = null;
			if (visual) {
				highlightByColor = new RoundIteratorOfPixels();
				highlightByColor.setImageResource(noded);
			}

			for (Node n : nodes) {
				Pixel nodePix = noded.getPixel(n.getX(), n.getY());
				if (n.getBottleneck()) {
					nodePix.setRed(yellowScheme[0]);
					nodePix.setGreen(yellowScheme[1]);
					nodePix.setBlue(yellowScheme[2]);
				} else {
					nodePix.setRed(whiteScheme[0]);
					nodePix.setGreen(whiteScheme[1]);
					nodePix.setBlue(whiteScheme[2]);
				}

				if (visual) {
					highlightByColor.setPixelToCheckAround(nodePix);
					for (Pixel pix : highlightByColor) {
						if (n.getBottleneck()) {
							pix.setRed(yellowScheme[0]);
							pix.setGreen(yellowScheme[1]);
							pix.setBlue(yellowScheme[2]);
						} else {
							pix.setRed(whiteScheme[0]);
							pix.setGreen(whiteScheme[1]);
							pix.setBlue(whiteScheme[2]);
						}
					}
				}

			}
			if (visual) {
				noded.draw();
				Pause.pause(5000);
			}
		} // scope for adjacency lists builder
	}

	/**
	 *
	 */
	private void putAllSurrReds(Pixel currP) {

		rcf.resetAllCluster();
		rcf.buildPartialCluster(currP);
		branch = rcf.getAllCluster();
		if (branch.size() == 0 && (visual || debug)) {
			System.err.print(" ZERO size branch.");
		}
		copyBranchIntoRedCluster(branch);
	}

	/**
	 *
	 */
	private void copyBranchIntoRedCluster(HashSet<Pixel> branch) {
		for (Pixel p : branch) {
			redCluster.add(p);
		}
	}

	/**
	 * 
	 */
	private void mapBackgrounds() {

		int maskSize = 0;
		int toSizes = 0;
		int counter = 0;
		Pixel original, copy;

		for (Node n : nodes) {
			counter = 0;
			maskSize = (n.getBottleneck()) ? bottleneckSize : passableSize;

			toSizes = (maskSize - 1) / 2;

			assert (toSizes <= borderInSharpenStage);

			for (int x = n.getX() - toSizes; x < n.getX() + toSizes + 1; x++) {
				for (int y = n.getY() - toSizes; y < n.getY() + toSizes + 1; y++) {

					original = noded.getPixel(x, y);
					copy = new Pixel(original);
					mapPixToNode.put(original, n);
					n.addPixelToMask(copy);
					counter++;

				}
			}
			if (counter != (toSizes * 2 + 1) * (toSizes * 2 + 1))
				throw new RuntimeException("retrievedMaskSize.");
		}
	}

	/**
	 *
	 */
	private void maskOrDemaskNode(Node n, boolean doMask) {

		int maskSize = 0;
		int toSizes = 0;
		int counter = 0;
		Pixel original, copy;
		ArrayList<Pixel> retrievedMask = null;

		maskSize = (n.getBottleneck()) ? bottleneckSize : passableSize;

		toSizes = (maskSize - 1) / 2;

		if (!doMask && (maskSize != (int) Math.sqrt(n.getMask().size()))) {
			throw new RuntimeException("Mask size problem." + n.getMask().size());
		}
		if (!doMask)
			retrievedMask = n.getMask();

		for (int x = n.getX() - toSizes; x < n.getX() + toSizes + 1; x++) {
			for (int y = n.getY() - toSizes; y < n.getY() + toSizes + 1; y++) {

				if (doMask) { // mask
					original = noded.getPixel(x, y);
					original.setRed(greenScheme[0]);
					original.setGreen(greenScheme[1]);
					original.setBlue(greenScheme[2]);
				} else { // demask
					if (retrievedMask.size() != (toSizes * 2 + 1) * (toSizes * 2 + 1))
						throw new RuntimeException("retrievedMaskSize");
					original = noded.getPixel(x, y);
					copy = retrievedMask.get(counter);
					original.setRed(copy.getRed());
					original.setGreen(copy.getGreen());
					original.setBlue(copy.getBlue());
					counter++;
				}
			}
		}
	}

	/**
	 *  
	 */
	private Set<Node> getAdjacents(Node buildForThis) {
		Set<Node> adjacentNodes = new HashSet<Node>();
		Node adj = null;
		for (Pixel p : redCluster) {
			riop.setPixelToCheckAround(p);
			for (Pixel pRound : riop) {
				if (isNode(pRound)) {
					adj = mapPixToNode.get(pRound);
					if (adj != buildForThis)
						adjacentNodes.add(adj);
				}
			}
		}
		return adjacentNodes;
	}

	/**
	 * 
	 * @param p
	 * @return
	 */
	private boolean isNode(Pixel p) {
		return (p.getRed() == greenScheme[0] && p.getGreen() == greenScheme[1] && p.getBlue() == greenScheme[2]);
	}

	/**
	 * 
	 * @param p
	 * @return
	 */
	private boolean isRoutable(Pixel p) {
		return (
				(p.getRed() == redScheme[0] &&
				p.getGreen() == redScheme[1] &&
				p.getBlue() == redScheme[2])
				||
				(p.getRed() == yellowScheme[0] &&
				p.getGreen() == yellowScheme[1] &&
				p.getBlue() == yellowScheme[2])
				
				);
	}

	/**
	 *
	 */
	private void maskAllNodes(boolean firstRun) {
		if (firstRun)
			mapBackgrounds();
		for (Node node : nodes) {
			maskOrDemaskNode(node, true);
		}
	}

	/**
	 *
	 */
	private void demaskAllNodes() {
		for (Node node : nodes) {
			maskOrDemaskNode(node, false);
		}
	}

	/**
	 *
	 */
	private boolean isBottleNeck(Node n) {
		return ldt.locallyDisconnects(noded.getPixel(n.getX(), n.getY()));
	}

	/**
	 *
	 */
	public void drawAdjacencyEdges(List<Node> nodesToDraw) {
		ImageResource edges = new ImageResource(noded.getWidth(), noded.getHeight());
		LineMaker lm = new LineMaker(edges);
		int x1, y1, x2, y2;
		int r, g, b;
		Random rndm = new Random();
		for (Node n : nodesToDraw) {
			Set<Node> adjacents = n.getAdjacentNodes();
			x1 = n.getX();
			y1 = n.getY();

			r = rndm.nextInt(256);
			g = rndm.nextInt(256);
			b = rndm.nextInt(256);

			if (r < 50)
				r = 50;
			if (g < 50)
				g = 50;
			if (b < 50)
				b = 50;

			for (Node a : adjacents) {
				x2 = a.getX();
				y2 = a.getY();
				lm.drawLine(x1, y1, x2, y2, r, g, b);
			}
		}
		edges.draw();
	}

	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private void testPrintRedCluster() {
		System.out.println("TEST PRINT RED CLUSTER");
		for (Pixel p : redCluster) {
			System.out.println("---- " + p);
		}
	}
}

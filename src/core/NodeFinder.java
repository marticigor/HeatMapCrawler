package core;

import java.util.*;

import core.image_filters.JustCopy;
import core.image_morpho_transform.LocalyDisconnectTest;
import core.image_morpho_transform.Skeleton;
import core.node_finder_utils.TopLeftOfClosest;
import core.node_finder_utils.CenterOfGravity;
import core.node_finder_utils.MaximusPixels;
import core.node_finder_utils.SurrPixels;
import core.salient_areas_detectors.ForegroundCountingSalientDetector;
import core.salient_areas_detectors.SimilaritySalientDetector;
import ifaces.I_ColorScheme;
import ifaces.I_SalientDetector;
import ifaces.node_finder.I_PixelExam;
import ifaces.node_finder.I_PixelSelector;
import lib_duke.ImageResource;
import lib_duke.Pixel;

public class NodeFinder implements I_ColorScheme {

	private ImageResource thresholded;
	// the android app uses this hardcoded constant
	private final static int COMPUTE_WEIGHT_OUTLOOK = 5;
	// private final static int MULTIPLICATE_LOOKAHEADANDBACK = 5;
	// max weight 122
	private ImageResource skeletonized;
	private List<Node> nodes = new ArrayList<Node>();
	private int width;
	private int height;
	private ImageResource noded;

	private int lookAheadAndBack;
	private int surfaceConstantInterval1_MinExcl;
	private int surfaceConstantInterval1_MaxIncl;
	private int surfaceConstantInterval2_MinExcl;
	private int surfaceConstantInterval2_MaxIncl;
	private int routableNeighbourghsConstant;

	@SuppressWarnings("unused")
	private int thresholded_lookAheadAndBack;
	@SuppressWarnings("unused")
	private int thresholded_surfaceConstantInterval1_MinExcl;
	@SuppressWarnings("unused")
	private int thresholded_surfaceConstantInterval1_MaxIncl;
	@SuppressWarnings("unused")
	private int thresholded_surfaceConstantInterval2_MinExcl;
	@SuppressWarnings("unused")
	private int thresholded_surfaceConstantInterval2_MaxIncl;
	@SuppressWarnings("unused")
	private int thresholded_routableNeighbourghsConstant;

	private int surfaceConstant1_1;
	private int surfaceConstant1_2;
	private int surfaceConstant2_1;
	private int surfaceConstant2_2;
	private int neighbourghsConstant;

	private final long shotId;

	// represents white pixels clustered around future node
	private Set<Pixel> allClusterSetResource = new HashSet<Pixel>();

	int borderSharpenStage;
	private double[] bounds;
	private boolean debug;
	private boolean visual;

	private static double spanLon = -1.0;
	private static double spanLat = -1.0;

	private I_PixelExam surrPixels;
	private final Runner myHandler;
	private RecursiveClusterFinder rcf;
	private int maxClusterSize;
	private LocalyDisconnectTest ldt;

	/**
	 *
	 */
	public NodeFinder(ImageResource thresholded, ImageResource skeletonized,

			int look, int surface1, int surface2, int surface3, int surface4, int neighbours,

			int thresholded_look, int thresholded_surface1, int thresholded_surface2, int thresholded_surface3,
			int thresholded_surface4, int thresholded_neighbours,

			Runner myHandler, double[] bounds, long shotId, int maxClusterSize, boolean debug, boolean visual) {

		this.lookAheadAndBack = look;
		this.surfaceConstantInterval1_MinExcl = surface1;
		this.surfaceConstantInterval1_MaxIncl = surface2;
		this.surfaceConstantInterval2_MinExcl = surface3;
		this.surfaceConstantInterval2_MaxIncl = surface4;
		this.routableNeighbourghsConstant = neighbours;

		this.thresholded_lookAheadAndBack = thresholded_look;
		this.thresholded_surfaceConstantInterval1_MinExcl = thresholded_surface1;
		this.thresholded_surfaceConstantInterval1_MaxIncl = thresholded_surface2;
		this.thresholded_surfaceConstantInterval2_MinExcl = thresholded_surface3;
		this.thresholded_surfaceConstantInterval2_MaxIncl = thresholded_surface4;
		this.thresholded_routableNeighbourghsConstant = thresholded_neighbours;

		this.skeletonized = skeletonized;
		this.thresholded = thresholded;
		width = skeletonized.getWidth();
		height = skeletonized.getHeight();
		noded = new ImageResource(width, height);
		this.myHandler = myHandler;
		this.borderSharpenStage = myHandler.getBorderInSharpenStage();
		this.bounds = bounds;
		this.shotId = shotId;
		this.maxClusterSize = maxClusterSize;
		this.debug = debug;
		this.visual = visual;

		// 0 lon east
		// 1 lat north
		// 2 lat south
		// 3 lon west
		// 4 lat center
		// 5 lon center

		// TODO code duplicity
		double spanLonNow = bounds[0] - bounds[3];
		if (spanLon == -1.0)
			spanLon = spanLonNow;
		else if (spanLon != spanLonNow) {
			System.err.println("------------------------------span discrepancy Lon");
			System.err.println("spanLon " + spanLon + " now " + spanLonNow);
			System.err.println("dif " + (spanLon - spanLonNow));
			spanLon = spanLonNow;
		}
		double spanLatNow = bounds[1] - bounds[2];
		if (spanLat == -1.0)
			spanLat = spanLatNow;
		else if (spanLat != spanLatNow) {
			System.err.println("------------------------------span discrepancy Lat");
			System.err.println("spanLat " + spanLat + " now " + spanLatNow);
			System.err.println("dif " + (spanLat - spanLatNow));
			spanLat = spanLatNow;
		}

		if (bounds.length != 6)
			throw new RuntimeException("Node finder bounds length.");

		JustCopy copy = new JustCopy(skeletonized, noded, myHandler.getBorderInSharpenStage(), true, debug, -1, -1, -1,
				-1, myHandler.getBorderInSharpenStage());
		copy.doYourThing();

		if (debug) {
			System.out.println("NodeFinder");
			System.out.println("______________________________");
			for (double d : this.bounds)
				System.out.println(d);
			System.out.println("______________________________");
		}

		Skeleton skeletonMock = new Skeleton(noded, borderSharpenStage, true, false, -1, -1, -1, -1,
				borderSharpenStage);
		Skeleton.SkeletonUtils utils = skeletonMock.new SkeletonUtils(skeletonMock.getThresholdForeBack());
		ldt = new LocalyDisconnectTest(utils);

	}

	/**
	 *
	 */
	public void findNodes() {

		/*
		 * surfaceConstant1_1 = thresholded_surfaceConstantInterval1_MinExcl;
		 * surfaceConstant1_2 = thresholded_surfaceConstantInterval1_MaxIncl;
		 * surfaceConstant2_1 = thresholded_surfaceConstantInterval2_MinExcl;
		 * surfaceConstant2_2 = thresholded_surfaceConstantInterval2_MaxIncl;
		 * neighbourghsConstant = thresholded_routableNeighbourghsConstant;
		 * 
		 * System.out.println("surfaceConstant1_1 " + surfaceConstant1_1);
		 * System.out.println("surfaceConstant1_2 " + surfaceConstant1_2);
		 * System.out.println("surfaceConstant2_1 " + surfaceConstant2_1);
		 * System.out.println("surfaceConstant2_2 " + surfaceConstant2_2);
		 * System.out.println("thresholded_lookAheadAndBack " +
		 * thresholded_lookAheadAndBack);
		 * 
		 * detectSalientAreas(thresholded, thresholded_lookAheadAndBack, false);
		 */

		surfaceConstant1_1 = surfaceConstantInterval1_MinExcl;
		surfaceConstant1_2 = surfaceConstantInterval1_MaxIncl;
		surfaceConstant2_1 = surfaceConstantInterval2_MinExcl;
		surfaceConstant2_2 = surfaceConstantInterval2_MaxIncl;
		neighbourghsConstant = routableNeighbourghsConstant;

		System.out.println("surfaceConstant1_1 " + surfaceConstant1_1);
		System.out.println("surfaceConstant1_2 " + surfaceConstant1_2);
		System.out.println("surfaceConstant2_1 " + surfaceConstant2_1);
		System.out.println("surfaceConstant2_2 " + surfaceConstant2_2);
		System.out.println("lookAheadAndBack " + lookAheadAndBack);

		// detectSalientAreas

		// TODO memory wasting...
		ImageResource nodedCopySimilarity = new ImageResource(noded);// returns blank image
		ImageResource nodedCopyForeground = new ImageResource(noded);

		I_SalientDetector detectorSimilarity = new SimilaritySalientDetector(skeletonized, nodedCopySimilarity,
				thresholded, borderSharpenStage, lookAheadAndBack, surfaceConstant1_1, surfaceConstant1_2,
				surfaceConstant2_1, surfaceConstant2_2, neighbourghsConstant, visual, debug);

		detectorSimilarity.detectSalientAreas(false);

		I_SalientDetector detectorForeground = new ForegroundCountingSalientDetector(skeletonized, nodedCopyForeground,
				thresholded, borderSharpenStage, lookAheadAndBack, surfaceConstant1_1, surfaceConstant1_2,
				surfaceConstant2_1, surfaceConstant2_2, neighbourghsConstant, visual, debug);

		detectorForeground.detectSalientAreas(false);

		// merge white pixels from both detectors ( OR )

		for (Pixel p : nodedCopySimilarity.pixels()) {
			mergeIntoNoded(p);
		}
		for (Pixel p : nodedCopyForeground.pixels()) {
			mergeIntoNoded(p);
		}

		// nodedCopySimilarity.draw();
		// nodedCopyForeground.draw();

		if (visual) {
			noded.draw();
			Pause.pause(5000);
		}

		rcf = new RecursiveClusterFinder(noded, maxClusterSize);

		int x, y;
		double lon, lat;
		short weight;
		Pixel theOneP = null;

		final double dLon = Math.abs(bounds[0] - bounds[3]);
		final double lonShiftToPixCenter = (dLon / width) / 2;
		final double dLat = Math.abs(bounds[1] - bounds[2]);
		final double latShiftToPixCenter = (dLat / height) / 2;

		if (debug || visual) {
			System.out.println("______________________________________");
			System.out.println("dLon " + dLon);
			System.out.println("dLat " + dLat);
			System.out.println("lonShiftToPixCenter " + lonShiftToPixCenter);
			System.out.println("latShiftToPixCenter " + latShiftToPixCenter);
			System.out.println("______________________________________");
		}

		// int helperCountOne = 0, helperCountTwo = 0;
		for (Pixel pOfNoded : noded.pixels()) { // 1

			if (isSetToBeClustered(pOfNoded)) { // 2 is the pixel white?

				allClusterSetResource = new HashSet<Pixel>();
				rcf.resetAllCluster();

				// now define recursively (in clusterFinder) a future node
				// object as a small bitmap

				buildBranch(pOfNoded); // as this emerges from recursion (in
										// clusterFinder) allClusterAroundNode
										// is build

				if (debug && nodes.size() % 20 == 0)
					printAllClustered(allClusterSetResource); // size of print REDUCED

				// find the Pixel - future Node queue start

				surrPixels = new SurrPixels(noded, whiteScheme);
				int maxSurr = getMaxNumberOfSurroundingWhites(allClusterSetResource);
				I_PixelSelector maximusesSurr = new MaximusPixels();
				maximusesSurr.proces(allClusterSetResource, surrPixels, maxSurr);
				Set<Pixel> maximusesSetResource = maximusesSurr.getSet();
				I_PixelSelector center = new CenterOfGravity();
				center.proces(maximusesSetResource, null, 0);
				Set<Pixel> closestToCenter = center.getSet();

				/*
				 * if (closestToCenter.size() == 0) { throw new
				 * RuntimeException("closestToCenter.size() = 0"); } else if
				 * (closestToCenter.size() == 1) helperCountOne++; else if
				 * (closestToCenter.size() == 2) helperCountTwo++; else if
				 * (closestToCenter.size() > 2 && (debug || visual))
				 * System.err.println("ClosestToCenter.size() > 2 :" + closestToCenter.size());
				 */

				I_PixelSelector topLeft = new TopLeftOfClosest();
				topLeft.proces(closestToCenter, null, 0);
				Set<Pixel> theWinnerSet = topLeft.getSet(); // guaranteed to have only one member
				for (Pixel pixel : theWinnerSet)
					theOneP = pixel;

				// bounds

				// 0 lon east
				// 1 lat north
				// 2 lat south
				// 3 lon west
				// 4 lat center
				// 5 lon center

				x = theOneP.getX();
				y = theOneP.getY();

				lon = bounds[3] + ((((double) x / ((double) (width))) * dLon) + lonShiftToPixCenter);
				lat = bounds[1] - ((((double) y / ((double) (height))) * dLat) + latShiftToPixCenter);

				weight = computeWeight(x, y);

				Node node = new Node(x, y, weight, lon, lat, myHandler.incrAndGetId(), shotId);

				// TODO think how to move node a bit so it really is bottleneck

				if (isBottleNeck(node))
					node.setBottleneck(true);
				nodes.add(node);

				// we are done with white pixels in allClusterAroundNode
				for (Pixel toRed : allClusterSetResource) {
					setRed(toRed);
				}
			} // 2
		} // 1

		// now set pixels white
		Pixel px;
		for (Node n : nodes) {
			px = noded.getPixel(n.getX(), n.getY());
			setWhite(px);
		}

		if (visual) {
			noded.draw();
			Pause.pause(5000);
		}
	}

	/**
	 * Loads recursively set of white pixels into allClusterAroundNode. No locally
	 * disconnecting nodes (and their Pixels) yet, a branch is always full cluster.
	 */
	private void buildBranch(Pixel p) {
		rcf.buildPartialCluster(p);
		this.allClusterSetResource = rcf.getAllCluster();
		assert (allClusterSetResource.size() != 0);
	}

	/**
	 * 
	 * @param cluster
	 * @return
	 */
	private int getMaxNumberOfSurroundingWhites(Set<Pixel> cluster) {
		assert (cluster.size() > 0);
		int max = 0;
		int curr = 0;
		for (Pixel p : cluster) {
			curr = surrPixels.exam(p);
			if (curr > max)
				max = curr;
		}
		return max;
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private short computeWeight(int x, int y) {
		// ImageResource thresholded;
		// int borderSharpenStage;
		short weight = 0;// weird, on purpose
		Pixel pix;
		for (int xNow = (x - COMPUTE_WEIGHT_OUTLOOK); xNow <= x + COMPUTE_WEIGHT_OUTLOOK; xNow++) {
			for (int yNow = (y - COMPUTE_WEIGHT_OUTLOOK); yNow <= y + COMPUTE_WEIGHT_OUTLOOK; yNow++) {
				// this will get corrected when concatenating graph later;
				if (xNow < 0 || xNow > thresholded.getWidth() - 1 || yNow < 0 || yNow > thresholded.getHeight() - 1)
					continue;
				pix = thresholded.getPixel(xNow, yNow);
				if (isRoutable(pix))
					weight++;
			}
		}
		return weight;
	}

	/**
	 * checks if p is set to white scheme
	 */
	private boolean isSetToBeClustered(Pixel p) {
		if (p.getRed() == whiteScheme[0] && p.getGreen() == whiteScheme[1] && p.getBlue() == whiteScheme[2])
			return true;
		return false;
	}

	/**
	 * @param pix
	 * @return
	 */
	private boolean isRoutable(Pixel pix) {
		return pix.getRed() == redScheme[0] && pix.getGreen() == redScheme[1] && pix.getBlue() == redScheme[2];
	}

	/**
	 * prints simplistic visualization of clusters
	 */
	private void printAllClustered(Set<Pixel> cluster) {
		assert (cluster.size() > 0);
		System.out.println("--------------------------");

		boolean[][] clusterA = new boolean[(lookAheadAndBack * 2 + 1)][(lookAheadAndBack * 2 + 1)];

		StringBuilder sb = new StringBuilder();

		int minX = Integer.MAX_VALUE;
		int minY = Integer.MAX_VALUE;

		for (Pixel p : cluster) {
			if (p.getX() < minX)
				minX = p.getX();
			if (p.getY() < minY)
				minY = p.getY();
		}

		int xToVis = 0;
		int yToVis = 0;

		for (Pixel p : cluster) {

			xToVis = p.getX() - minX;
			xToVis = xToVis >= (lookAheadAndBack * 2 + 1) ? (lookAheadAndBack * 2) : xToVis;
			yToVis = p.getY() - minY;
			yToVis = yToVis >= (lookAheadAndBack * 2 + 1) ? (lookAheadAndBack * 2) : yToVis;

			clusterA[xToVis][yToVis] = true;
		}
		for (int x = 0; x < clusterA.length; x++) {
			for (int y = 0; y < clusterA.length; y++) {

				if (clusterA[x][y])
					sb.append("X");
				else
					sb.append("_");
			}
			System.out.println(sb.toString());
			sb = new StringBuilder();
		}
	}

	// no need to optimize
	Pixel toBeWhite = null;
	Pixel nodedBackGround = null;

	private void mergeIntoNoded(Pixel p) {
		if (isWhite(p)) {
			toBeWhite = noded.getPixel(p.getX(), p.getY());
			setWhite(toBeWhite);
		} else if (isRed(p)) {
			nodedBackGround = noded.getPixel(p.getX(), p.getY());
			if (!isWhite(nodedBackGround))
				setRed(nodedBackGround);
		}
	}

	private boolean isBottleNeck(Node n) {
		return ldt.locallyDisconnects(noded.getPixel(n.getX(), n.getY()));
	}

	public ImageResource getNodedImage() {
		return noded;
	}

	public List<Node> getNodes() {
		return nodes;
	}

	private void setRed(Pixel p) {
		p.setRed(redScheme[0]);
		p.setGreen(redScheme[1]);
		p.setBlue(redScheme[2]);
	}

	private void setWhite(Pixel p) {
		p.setRed(whiteScheme[0]);
		p.setGreen(whiteScheme[1]);
		p.setBlue(whiteScheme[2]);
	}

	private boolean isWhite(Pixel p) {
		return (p.getRed() == whiteScheme[0] && p.getGreen() == whiteScheme[1] && p.getBlue() == whiteScheme[2]);
	}

	private boolean isRed(Pixel p) {
		return (p.getRed() == redScheme[0] && p.getGreen() == redScheme[1] && p.getBlue() == redScheme[2]);
	}

	@SuppressWarnings("unused")
	private void setYellow(Pixel p) {
		p.setRed(yellowScheme[0]);
		p.setGreen(yellowScheme[1]);
		p.setBlue(yellowScheme[2]);
	}
}

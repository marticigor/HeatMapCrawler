package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import javax.swing.SwingUtilities;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import core.nodes_filters.ZeroAdjacentsFilter;
import core.tasks.TaskCanny;
import core.tasks.TaskGaussian;
import core.tasks.TaskHighlight;
import core.tasks.TaskJustCopy;
import core.tasks.TaskSharpen;
import core.tasks.TaskSkeleton;
import database.ManageNodeEntity;
import database.NmbShotsEntity;
import database.NodeEntity;
import ifaces.I_NodeFilter;
import lib_duke.DirectoryResource;
import lib_duke.ImageResource;
import lib_duke.Pixel;
import mockery.NodeGraphMocks;
import output.OutputXml;
import output.Trackpoint;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class Runner implements Runnable {

	static {
		boolean assertsEnabled = false;
		assert assertsEnabled = true; // Intentional side effect!!!
		if (!assertsEnabled)
			System.err.println("Asserts not enabled!!!");
	}

	// config in aplicationContext.xml
	private final int devi;
	private final int look, surface1, surface2, surface3, surface4, neighbours;
	private final int thresholded_look, thresholded_surface1, thresholded_surface2, thresholded_surface3,
			thresholded_surface4, thresholded_neighbours;

	// config NOT in aplicationContext.xml
	private final int bottleneckSize = 3; // 3
	private final int passableSize = 3; // 3

	private final int sizeDivKonq = 4;

	// config in aplicationContext.xml
	boolean visual; // also pauses execution now and then, is purism (e.g
					// boolean pauseVisual;) really needed here?
	boolean debug;

	private ImageChunks chunks;
	private int nmbOfShots; // not necesearilly all shots appear in nodes (blank
							// images)

	private int nodeCount = 0;
	private long id = -1;

	private final int borderInSharpenStage; // ((Math.max(bottleneckSize,
											// passableSize)) - 1) / 2;

	/**
	 *
	 */
	public Runner(int devi,

			int look, int v3, int v4, int v5, int v6, int nei,

			int thresholded_look, int thresholded_v3, int thresholded_v4, int thresholded_v5, int thresholded_v6,
			int thresholded_nei,

			boolean visual, boolean debug) {

		this.devi = devi;

		this.look = look;
		this.surface1 = v3;
		this.surface2 = v4;
		this.surface3 = v5;
		this.surface4 = v6;
		this.neighbours = nei;

		this.thresholded_look = thresholded_look;
		this.thresholded_surface1 = thresholded_v3;
		this.thresholded_surface2 = thresholded_v4;
		this.thresholded_surface3 = thresholded_v5;
		this.thresholded_surface4 = thresholded_v6;
		this.thresholded_neighbours = thresholded_nei;

		this.borderInSharpenStage = Math.max(thresholded_look, look);

		this.visual = visual;
		this.debug = debug;
	}

	/**
	 *
	 */
	public void run() {

		DirectoryResource dirRPng = new DirectoryResource();// opens dialog
															// window
		DirectoryResource dirRTxt = new DirectoryResource();

		List<File> listFilesPng = new ArrayList<File>();
		for (File f : dirRPng.selectedFiles())
			listFilesPng.add(f);

		List<File> listFilesTxt = new ArrayList<File>();
		for (File f : dirRTxt.selectedFiles())
			listFilesTxt.add(f);

		long shotId = 0;
		nmbOfShots = listFilesPng.size();

		for (int i = 0; i < 1; i++) { // stress test - out of memory, leak...

			System.out.println("---------------------------------------------------------" + "--------- iter " + i);

			for (File iteratedFile : listFilesPng) {

				System.out.println("\n\n\n\n\nPROCESING " + iteratedFile.toString());
				String fileName = iteratedFile.getName();
				fileName = fileName.substring(0, fileName.indexOf('.'));
				String fileNameTxt = fileName + ".txt";

				// get the description file
				File description = null;

				try {
					description = getDesriptionFile(listFilesTxt, fileNameTxt);
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
					throw new RuntimeException("Text description file not found.");
				}

				// read description into string
				String readLine = null;
				BufferedReader br = null;
				try {
					br = new BufferedReader(new FileReader(description));
					System.out.println("Reading file " + description);
					while ((readLine = br.readLine()) != null) {
						System.out.println(readLine);
						break;
					}
				} catch (IOException e) {
					e.printStackTrace();
					throw new RuntimeException("IO Runner1");
				} finally {
					if (br != null)
						try {
							br.close();
						} catch (IOException e) {
							e.printStackTrace();
							throw new RuntimeException("IO Runner2");
						}
				}

				// convert string into double []
				double[] bounds = new double[6];
				// 0 lon east
				// 1 lat north
				// 2 lat south
				// 3 lon west
				// 4 lat center
				// 5 lon center

				if (readLine == null)
					throw new RuntimeException("No description in file.");

				// there are 12 characters with special meanings: the backslash
				// \,
				// the caret ^, the dollar sign $,
				// the period or dot ., the vertical bar or pipe symbol |,
				// the question mark ?, the asterisk or star *, the plus sign +,
				// the opening parenthesis (, the closing parenthesis ),
				// and the opening square bracket [, the opening curly brace
				// {, These special characters are often called
				// "metacharacters".

				String[] values = readLine.split(Pattern.quote("|"));
				if (values.length != 7)
					throw new RuntimeException("Length.");// beginning "|"

				System.out.println("___________________________________");
				for (int j = 1; j < 7; j++) {
					System.out.println(values[j]);
					bounds[j - 1] = Double.parseDouble(values[j]);
				}
				System.out.println("___________________________________");

				ImageResource image = new ImageResource(iteratedFile);
				ImagePreprocesor ip = new ImagePreprocesor(devi, borderInSharpenStage, visual, debug, image);

				chunks = new ImageChunks(ip.getX(), ip.getY(), sizeDivKonq);

				perManyTasksProces(ip);
				ip.invokeSequencialQueue();

				if (visual) {
					image.draw();
					Pause.pause(2000);
					final ImageResource procesedMapStage = ip.getProcesedStage();
					procesedMapStage.draw();
					Pause.pause(2000);
				}

				final ImageResource skeletonized = ip.getProcesed();
				System.out.println("----" + skeletonized);
				final ImageResource thresholded = ip.getProcesedStage();
				System.out.println("----" + thresholded);

				if (visual) {
					skeletonized.draw();
					Pause.pause(2000);
				}

				NodeFinder nf = new NodeFinder(thresholded, skeletonized,

						look, surface1, surface2, surface3, surface4, neighbours,

						thresholded_look, thresholded_surface1, thresholded_surface2, thresholded_surface3,
						thresholded_surface4, thresholded_neighbours,

						this, bounds, shotId, debug, visual);

				nf.findNodes();

				ImageResource noded = nf.getNodedImage();
				if (visual)
					noded.draw();

				List<Node> nodes = nf.getNodes();

				AdjacencyFinder af = new AdjacencyFinder(noded, nodes, visual, debug, bottleneckSize, passableSize);
				af.buildAdjacencyLists();

				// test output gpx
				if (debug) {
					// 0 lon east
					// 1 lat north
					// 2 lat south
					// 3 lon west
					// 4 lat center
					// 5 lon center
					List<Trackpoint> points = new LinkedList<Trackpoint>();
					Trackpoint tr = new Trackpoint(bounds[3], bounds[1]);// top
																			// left
																			// corner
					points.add(tr);
					tr = new Trackpoint(bounds[0], bounds[1]);// envelope _
					points.add(tr);
					tr = new Trackpoint(bounds[0], bounds[2]);// envelope |
					points.add(tr);
					for (Node n : nodes) {
						tr = new Trackpoint(n.getLon(), n.getLat());
						points.add(tr);// inside envelope should be
					}
					OutputXml out = new OutputXml(points, fileName + ".gpx");
					try {
						out.composeOutputDoc();
						out.writeOutputFile();
					} catch (ParserConfigurationException e) {
						e.printStackTrace();
					} catch (TransformerException e) {
						e.printStackTrace();
					}
				} // debug

				if (visual) {
					af.drawAdjacencyEdges();
					Pause.pause(2000);
				} else {
				}

				addNodeCount(nodes.size());
				System.out.println("CURRENT Number of nodes: " + getNodeCount());

				I_NodeFilter filter = new ZeroAdjacentsFilter();
				List<Node> filtered = filter.procesChunk(nodes);
				System.out.println("FILTERED OUT NODES (zero adjacents): " + (nodes.size() - filtered.size()));

				
				
				
				
				System.out.println("Do we have reference TO zero adjacency list nodes?");

				ImageResource test = new ImageResource(noded.getWidth(), noded.getHeight());
				Set<Node> adj = null;
				for (Node zeroAdj : filter.getFilteredOut()) {
					for (Node noZeroAdjNode : filtered) {
						adj = noZeroAdjNode.getAdjacentNodes();
						if (adj.contains(zeroAdj)) {
							
							noZeroAdjNode.removeAdjacentNode(zeroAdj);
							
							
							System.err.println("PROBLEM: " + noZeroAdjNode + "\n POINTS to zeroAdj:\n" + zeroAdj);
							Pixel zero = noded.getPixel(zeroAdj.getX(), zeroAdj.getY());
							Pixel pointingToZero = noded.getPixel(noZeroAdjNode.getX(), noZeroAdjNode.getY());

							Pixel zeroTest = test.getPixel(zeroAdj.getX(), zeroAdj.getY());
							Pixel pointingToZeroTest = test.getPixel(noZeroAdjNode.getX(), noZeroAdjNode.getY());

							pointingToZero.setRed(255);
							pointingToZero.setGreen(255);
							pointingToZero.setBlue(255);

							pointingToZeroTest.setRed(255);
							pointingToZeroTest.setGreen(255);
							pointingToZeroTest.setBlue(255);

							zero.setRed(255);
							zero.setBlue(255);
							zero.setGreen(0);

							zeroTest.setRed(255);
							zeroTest.setBlue(255);
							zeroTest.setGreen(0);
						}
					}
				}

				noded.draw();
				test.draw();

				Set<Node> rightSet = null;
				Set<Node> rightAdj = null;
				
				for (Node left : filtered){
					rightSet  = left.getAdjacentNodes();
					for(Node right : rightSet){
						rightAdj = right.getAdjacentNodes();
						if( ! rightAdj.contains(left)){
							//System.out.println("----PROBLEM, left " + left );
							Pixel problem = test.getPixel(left.getX(), left.getY());
							problem.setGreen(255);
						}
					}
				}
				test.draw();

					
					
					
					
					
					
					
					
				//if (true)
					//throw new RuntimeException("HALT");

				if (filtered != null)
					persist(filtered);
				else
					throw new RuntimeException("nodes = null");
				//
				//
				// OR inject MOCKS
				// NodeGraphMocks mocks = new NodeGraphMocks();
				// List <Node> mockNodes = mocks.getMocks1();
				// persist(mockNodes);

				shotId++;
			}

			System.out.println("FINAL Number of nodes: " + getNodeCount());

		} // stress test - out of memory, leak...
	}// run

	/**
	 *
	 * @param ip
	 */
	private void perManyTasksProces(final ImagePreprocesor ip) {

		System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "8");

		List<RecursiveAction[]> stages = new ArrayList<RecursiveAction[]>();

		// FILTERS QUEUE FIFO START
		// --------------------------------------------------

		// thresholding simple
		TaskSharpen[] sharpenTask = new TaskSharpen[sizeDivKonq * sizeDivKonq];
		decorateFactory(sharpenTask, TaskSharpen.class, ip);
		stages.add(sharpenTask);

		TaskJustCopy[] justCopyTask = new TaskJustCopy[sizeDivKonq * sizeDivKonq];
		decorateFactory(justCopyTask, TaskJustCopy.class, ip);
		stages.add(justCopyTask);

		// --------------------------------------------------
		// FILTERS QUEUE FIFO END

		ForkJoinPool forkJoinPool = new ForkJoinPool(8);

		for (RecursiveAction[] stage : stages) {
			for (RecursiveAction segment : stage) {
				// debug print1 at bottom
				forkJoinPool.invoke(segment); // commonPool?
			}
			if (debug)
				System.out.println("RETURNED IN LOOP  " + System.currentTimeMillis());
		}

	}

	/**
	 *
	 * @param task
	 * @param ref
	 * @param ip
	 */
	private <T extends RecursiveAction> void decorateFactory(T[] task, Class<T> ref, ImagePreprocesor ip) {

		Constructor<T> constructor = null;
		try {
			constructor = ref.getConstructor(core.ImagePreprocesor.class, java.lang.Integer.class,
					java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class);
		} catch (NoSuchMethodException | SecurityException e1) {
			e1.printStackTrace();
			System.exit(1);
		}
		int i = 0;
		for (int y = 0; y < sizeDivKonq; y++) {
			for (int x = 0; x < sizeDivKonq; x++) {
				try {
					task[i] = constructor.newInstance(ip, chunks.fromX[x], chunks.toX[x], chunks.fromY[y],
							chunks.toY[y]);
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					e.printStackTrace();
					System.exit(1);
				}
				i++;
			}
		}
	}

	/**
	 *
	 * @param nodes
	 */
	private void persist(List<Node> nodes) {

		List<NodeEntity> list = new LinkedList<NodeEntity>();
		for (Node n : nodes) {
			if (n.getEntity() != null)
				list.add(n.getEntity());
			int size1 = n.getAdjacentNodes().size();
			int size2 = n.getEntity().getAdjacents().size();
			if (size1 != size2)
				throw new RuntimeException("sizes do not match - persist in Runner");
		}

		NmbShotsEntity nmb = new NmbShotsEntity(nmbOfShots);

		ManageNodeEntity man = ManageNodeEntity.getInstance();
		man.persist(list, nmb, debug);
	}

	/**
	 *
	 * @return
	 * @throws FileNotFoundException
	 */
	private File getDesriptionFile(List<File> listFilesTxt, String fileNameTxt) throws FileNotFoundException {
		for (File f : listFilesTxt) {
			String currName = f.getName();
			if (currName.equals(fileNameTxt))
				return f;
		}
		throw new FileNotFoundException("filename: " + fileNameTxt);
	}

	/**
	 *
	 * @param nodes
	 */
	private void printBuiltNodes(List<Node> nodes) {
		for (Node n : nodes) {
			System.out.println("------------------------------------------------------");
			System.out.println("node " + n.toString());
			Set<Node> adjacents = n.getAdjacentNodes();
			for (Node adjacent : adjacents) {
				System.out.println("\t\t" + "adjacent " + adjacent.toString());
			}
			System.out.println("------------------------------------------------------");
		}
	}

	/**
	 * no thread safe compound action
	 * 
	 * @return
	 */
	public long incrAndGetId() {
		id++;
		return id;
	}

	/**
	 *
	 * @param value
	 */
	private void addNodeCount(int value) {
		nodeCount += value;
	}

	/**
	 *
	 * @return
	 */
	private int getNodeCount() {
		return nodeCount;
	}

	public int getBorderInSharpenStage() {
		return borderInSharpenStage;
	}
}

package core;

import java.util.*;

import core.utils.RoundIteratorOfPixels;
import ifaces.I_ColorScheme;
import lib_duke.ImageResource;
import lib_duke.LineMaker;
import lib_duke.Pixel;
public class AdjacencyFinder implements I_ColorScheme {

    private ImageResource noded;
    private List < Node > nodes;
    private RoundIteratorOfPixels riop;
    private HashSet < Pixel > redCluster;
    private RecursiveClusterFinder rcf;
    private ImageResource visualizeIR;
    private boolean visual;
    private boolean debug;

    private HashMap < Pixel, Node > mapPixToNode;

    private int bottleneckSize, passableSize;

    public AdjacencyFinder(ImageResource noded, List < Node > nodes, boolean visual, boolean debug, int bottleneckSize, int passableSize) {

        this.noded = noded;
        this.nodes = nodes;
        this.visual = visual;
        this.debug = debug;
        this.bottleneckSize = bottleneckSize;
        this.passableSize = passableSize;

        riop = new RoundIteratorOfPixels();
        riop.setImageResource(noded);

        mapPixToNode = new HashMap < Pixel, Node > ();

        rcf = new RecursiveClusterFinder(noded, nodes, mapPixToNode, redScheme[0], redScheme[1], redScheme[2],
            greenScheme[0], greenScheme[1], greenScheme[2],
            this.debug); //visualize "debuger"

        if (this.visual) {
        	visualizeIR = new ImageResource(noded.getWidth(), noded.getHeight());
        }
    }

    /**
     * wrap method to build all lists into nodes
     */
    public void buildAdjacencyLists() {

        int nmbOfBottlenecks = 0;
        Pixel p;

        for (Node n: nodes) {
            if (isBottleNeck(n)) {
                n.setBottleneck(true);
                nmbOfBottlenecks++;
            }
        }

        System.out.println("Number of all nodes: " + nodes.size());
        System.out.println("Number of bottleneck nodes: " + nmbOfBottlenecks);

        for (Node n: nodes) {

            if (n.getBottleneck()) {
                p = noded.getPixel(n.getX(), n.getY());
                p.setRed(yellowScheme[0]);
                p.setGreen(yellowScheme[1]);
                p.setBlue(yellowScheme[2]);
            }
        }

        Collections.sort(nodes);

        { //scope for adjacency lists builder

            redCluster = new HashSet < Pixel > ();

            Pixel currP;
            int currX, currY;

            //this is first run - map of pixels gets created, later we can demask green squares
            maskAllNodes(true); //boolean firstRun

            if(visual){
                noded.draw();
                Pause.pause(2000);
            }

            for (Node buildForThis: nodes) {

                maskOrDemaskNode(buildForThis, false); //demask this node, not first run

                redCluster.clear();

                currX = buildForThis.getX();
                currY = buildForThis.getY();
                currP = noded.getPixel(currX, currY);

                riop.setPixelToCheckAround(currP);
                int count = 0;

                //--------------------------------------------------------------------
                for (Pixel iterP: riop) {
                    if (count == 0 || count == 3 || count == 5) putAllSurrReds(iterP);
                    count++;
                }
                //now redCluster is build for node buildForThis
                HashSet < Node > adjacents = rcf.getAdjacents();
                for (Node singleAdjacent: adjacents) {
                    buildForThis.addAdjacentNode(singleAdjacent);
                }
                rcf.resetToNewAdjacents();

                if (visual && debug) { //visual && debug
                    for (Pixel pDebug: redCluster) {

                        Pixel pIr = visualizeIR.getPixel(pDebug.getX(), pDebug.getY());

                        pIr.setRed(blueischScheme[0]);
                        pIr.setGreen(blueischScheme[1]);
                        pIr.setBlue(blueischScheme[2]);

                    }

                    visualizeIR.draw();
                    Pause.pause(2000);

                    for (Pixel pDebug: redCluster) {

                        Pixel pIr = visualizeIR.getPixel(pDebug.getX(), pDebug.getY());

                        pIr.setRed(redischScheme[0]);
                        pIr.setGreen(redischScheme[1]);
                        pIr.setBlue(redischScheme[2]);

                    }
                } //visual && debug
                maskOrDemaskNode(buildForThis, true); //doMask
            }

            demaskAllNodes();

            if(visual){
                noded.draw();
                Pause.pause(2000);
            }
            System.out.println("actualy highlighting nodes");

            RoundIteratorOfPixels makeYellow = new RoundIteratorOfPixels();
            makeYellow.setImageResource(noded);

            for (Node n: nodes) {
                boolean bottleneck = n.getBottleneck();
                Pixel pY = noded.getPixel(n.getX(), n.getY());
                makeYellow.setPixelToCheckAround(pY);
                for (Pixel pix: makeYellow) {
                    if (bottleneck) {
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

            if(visual){
                noded.draw();
                Pause.pause(2000);
            }
        } //scope for adjacency lists builder

        List <Node> toRemove = new ArrayList<Node>();
        for (Node n : nodes) if (n.getAdjacentNodes().size() == 0) toRemove.add(n);
        for (Node n : toRemove) {
        	System.out.println("Nodes of zero adjacency list size : " + n);
        	//nodes.remove(n);// do not remove them yet, they still may by useful in more
                //broad context after merge of this 'drop' graph into 'ocean' graph
        }
    }

    /**
     *
     */
    public void drawAdjacencyEdges() {
        ImageResource edges = new ImageResource(noded.getWidth(), noded.getHeight());
        LineMaker lm = new LineMaker(edges);
        int x1, y1, x2, y2;
        int r, g, b;
        Random rndm = new Random();
        for (Node n: nodes) {
            Set < Node > adjacents = n.getAdjacentNodes();
            x1 = n.getX();
            y1 = n.getY();

            r = rndm.nextInt(256);
            g = rndm.nextInt(256);
            b = rndm.nextInt(256);

            if (r < 50) r = 50;
            if (g < 50) g = 50;
            if (b < 50) b = 50;

            for (Node a: adjacents) {
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
    private void putAllSurrReds(Pixel currP) {

        rcf.resetAllCluster();
        rcf.setCluster(currP);

        HashSet < Pixel > branch = rcf.getAllCluster();
        copyBranchIntoRedCluster(branch);
    }

    /**
     *
     */
    private void copyBranchIntoRedCluster(HashSet < Pixel > branch) {
        for (Pixel p: branch) {
            redCluster.add(p);
        }
    }

    private void mapBackgrounds() {

        int maskSize = 0;
        int toSizes = 0;
        int counter = 0;
        Pixel original, copy; //TODO do we really need a copy?

        for (Node n: nodes) {
            counter = 0;
            maskSize = (n.getBottleneck()) ? bottleneckSize : passableSize;

            toSizes = (maskSize - 1) / 2;

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
        ArrayList < Pixel > retrievedMask = null;

        maskSize = (n.getBottleneck()) ? bottleneckSize : passableSize;

        toSizes = (maskSize - 1) / 2;

        if (!doMask && (maskSize != (int) Math.sqrt(n.getMask().size()))) {
            throw new RuntimeException("Mask size problem." + n.getMask().size());
        }
        if (!doMask) retrievedMask = n.getMask();

        for (int x = n.getX() - toSizes; x < n.getX() + toSizes + 1; x++) {
            for (int y = n.getY() - toSizes; y < n.getY() + toSizes + 1; y++) {

                if (doMask) { //mask
                    original = noded.getPixel(x, y);
                    original.setRed(greenScheme[0]);
                    original.setGreen(greenScheme[1]);
                    original.setBlue(greenScheme[2]);
                } else { //demask
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
    private void maskAllNodes(boolean firstRun) {

        if (firstRun) mapBackgrounds();

        for (Node node: nodes) {
            maskOrDemaskNode(node, true);
        }
    }

    /**
     *
     */
    private void demaskAllNodes() {
        for (Node node: nodes) {
            maskOrDemaskNode(node, false);
        }
    }

    /**
     *
     */
    private boolean isBottleNeck(Node n) {

        byte counter = 0;
        boolean[] takenSeat = new boolean[8];
        Pixel p;

        p = noded.getPixel(n.getX(), n.getY());

        riop.setPixelToCheckAround(p);

        for (Pixel pRecycled: riop) {
            if (isWhite(pRecycled) || isRed(pRecycled)) takenSeat[counter] = true;
            else takenSeat[counter] = false;
            counter++;
        }
        for (byte i = 0; i < 4; i++) {
            if (!takenSeat[i] && !takenSeat[i + 4]) return true; //rotation of xox mask
        }

        //now rotation of permutation matrix
        //xoo
        //oox
        //oxo
        int firstPoint, secondPoint;
        for (byte i = 0; i < 8; i++) {

            firstPoint = i + 3;
            if (firstPoint > 7) firstPoint -= 8;
            secondPoint = i + 5;
            if (secondPoint > 7) secondPoint -= 8;

            if (!takenSeat[i] && !takenSeat[firstPoint] && !takenSeat[secondPoint]) return true;

        }
        return false;
    }

    private boolean isWhite(Pixel p) {
        if (p.getRed() == whiteScheme[0] && p.getGreen() == whiteScheme[1] && p.getBlue() == whiteScheme[2]) return true;
        return false;
    }
    private boolean isRed(Pixel p) {
        if (p.getRed() == redScheme[0] && p.getGreen() == redScheme[1] && p.getBlue() == redScheme[2]) return true;
        return false;
    }
    @SuppressWarnings("unused")
    private boolean isYellow(Pixel p) {
        if (p.getRed() == yellowScheme[0] && p.getGreen() == yellowScheme[1] && p.getBlue() == yellowScheme[2]) return true;
        return false;
    }
}

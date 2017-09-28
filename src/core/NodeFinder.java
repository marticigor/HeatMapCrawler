package core;

import java.util.*;

import ifaces.IColorScheme;
import lib_duke.ImageResource;
import lib_duke.Pixel;

public class NodeFinder implements IColorScheme {

    private ImageResource sharpened;
    private ArrayList < Node > nodes = new ArrayList < Node > ();
    private int width;
    private int height;
    private ImageResource noded;
    private int nmbOfNodes = 0;
    private int lookAheadAndBack; //3
    private int surfaceLimit; //43

    private RoundIteratorOfPixels iteratorRound = new RoundIteratorOfPixels();
    private RecursiveClusterFinder rcf;

    //represents white pixels clustered around future node
    private HashSet < Pixel > allClusterAroundNode = new HashSet < Pixel > ();

    /**
     *
     */
    public NodeFinder(ImageResource sharpened, int look, int surface) {

        this.lookAheadAndBack = look;
        this.surfaceLimit = surface;
        this.sharpened = sharpened;
        width = sharpened.getWidth();
        height = sharpened.getHeight();
        noded = new ImageResource(width, height);

        for (Pixel p: sharpened.pixels()) {

            Pixel pCopy = noded.getPixel(p.getX(), p.getY());
            pCopy.setRed(p.getRed());
            pCopy.setGreen(p.getGreen());
            pCopy.setBlue(p.getBlue());

        }
    }

    /**
     *  
     */
    public void findNodes() {

        Pixel p = null;
        Pixel pIn = null;

        int surfaceArea = 0;

        iteratorRound.setImageResource(sharpened);
        iteratorRound.resetCount();

        for (int x = lookAheadAndBack + 1; x < width - (lookAheadAndBack + 1); x++) {
            for (int y = lookAheadAndBack + 1; y < height - (lookAheadAndBack + 1); y++) {

                p = sharpened.getPixel(x, y);

                if (p.getRed() == redScheme[0] && p.getGreen() == redScheme[1] && p.getBlue() == redScheme[2]) {

                    for (int xIn = x - lookAheadAndBack; xIn < x + lookAheadAndBack; xIn++) {
                        for (int yIn = y - lookAheadAndBack; yIn < y + lookAheadAndBack; yIn++) {

                            pIn = sharpened.getPixel(xIn, yIn);

                            if (pIn.getRed() == redScheme[0] && pIn.getGreen() == redScheme[1] && pIn.getBlue() == redScheme[2]) {

                                iteratorRound.setPixelToCheckAround(pIn);
                                iteratorRound.resetCount();

                                for (Pixel p1: iteratorRound) {

                                    if (p1.getRed() != redScheme[0] || p1.getGreen() != redScheme[1] || p1.getBlue() != redScheme[2]) surfaceArea++;

                                }
                            }
                        }
                    }
                    if (surfaceArea > surfaceLimit) {
                        Pixel pNoded = noded.getPixel(x, y);
                        setWhite(pNoded);
                    }
                    surfaceArea = 0;
                }
            }
        }

        //
        //noded.draw();
        //noded.saveAs();
        //Pause.pause(8000);
        //
        rcf = new RecursiveClusterFinder(noded, whiteScheme[0], whiteScheme[1], whiteScheme[2]);
        //

        for (Pixel pOfNoded: noded.pixels()) { //1

            if (isSetToClusterAround(pOfNoded)) { //2 white check only

                allClusterAroundNode.clear();
                rcf.resetAllCluster();

                //now define recursively a node object as a small bitmap  

                setClustered(pOfNoded); //as this emerges from recursion we have filled allClusterAroundNode

                if (nmbOfNodes % 200 == 0) printAllClustered(allClusterAroundNode); //size of print REDUCED

                int neighboursToFindMax;
                int maxNeighbours = 0;

                int sumX = 0;
                int sumY = 0;
                int centerGravityX = 0;
                int centerGravityY = 0;

                ArrayList < Node > maximusNodes = new ArrayList < Node > ();

                for (Pixel pChecked: allClusterAroundNode) { //find int max

                    neighboursToFindMax = getNumberOfSurrWhites(pChecked, noded);

                    if (neighboursToFindMax > maxNeighbours) maxNeighbours = neighboursToFindMax;

                }

                for (Pixel maybeMaximus: allClusterAroundNode) {

                    if (getNumberOfSurrWhites(maybeMaximus, noded) == maxNeighbours) {

                        Node node = new Node(maybeMaximus.getX(), maybeMaximus.getY());
                        maximusNodes.add(node);
                        //find average X Y for this cluster of maximuses
                        sumX += node.getX();
                        sumY += node.getY();

                    }
                }

                out: {
                    @SuppressWarnings("unused")
                    int closestXsoFar = Integer.MAX_VALUE;
                    @SuppressWarnings("unused")
                    int closestYsoFar = Integer.MAX_VALUE;
                    @SuppressWarnings("unused")
                    int xToOut = 0;
                    @SuppressWarnings("unused")
                    int yToOut = 0;

                    centerGravityX = sumX / maximusNodes.size();
                    centerGravityY = sumY / maximusNodes.size();

                    double minDist = Double.MAX_VALUE;

                    //select one of maximusNodes and add it to nodes;

                    for (Node maybeClosest: maximusNodes) {

                        //we do have (among maximusNodes) a node with exactly avg x y values 
                        if (maybeClosest.getX() == centerGravityX && maybeClosest.getY() == centerGravityY) {
                            nodes.add(maybeClosest);
                            nmbOfNodes++;

                            break out;
                        }

                        //calculate dist to center of gravity and set it to node POJO

                        int deltaX = Math.abs(maybeClosest.getX() - centerGravityX);
                        int deltaY = Math.abs(maybeClosest.getY() - centerGravityY);
                        double dist = Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));

                        maybeClosest.setDstToCenter(dist);
                        if (dist < minDist) minDist = dist;

                    }

                    for (Node possiblyClosestNode: maximusNodes) {

                        if (possiblyClosestNode.getDstToCenter() == minDist) {
                            nodes.add(possiblyClosestNode);
                            xToOut = possiblyClosestNode.getX();
                            yToOut = possiblyClosestNode.getY();
                            break;
                        }
                    }

                    nmbOfNodes++;
                } //out

                for (Pixel toRed: allClusterAroundNode) {
                    setRed(toRed);
                }
            } //2
        } //1

        //now set pixels white
        Pixel px;
        for (Node n: nodes) {
            px = noded.getPixel(n.getX(), n.getY());
            setWhite(px);
        }
    }

    /**
     * loads recursively set of white pixels into neighbours and then allClusterAroundNode
     */
    private void setClustered(Pixel p) {

        rcf.setCluster(p);
        this.allClusterAroundNode = rcf.getAllCluster();

    }

    /**
     * checks if p is set to white scheme
     */
    private boolean isSetToClusterAround(Pixel p) {

        if (p.getRed() == whiteScheme[0] && p.getGreen() == whiteScheme[1] && p.getBlue() == whiteScheme[2]) return true;
        return false;
    }

    /**
     * 
     */
    private int getNumberOfSurrWhites(Pixel p, ImageResource ir) {

        int neighbours = 0;

        iteratorRound.resetCount();
        iteratorRound.setImageResource(ir);
        iteratorRound.setPixelToCheckAround(p);

        for (Pixel myPx: iteratorRound) {

            if (myPx.getRed() == whiteScheme[0] && myPx.getGreen() == whiteScheme[1] && myPx.getBlue() == whiteScheme[2]) neighbours++;

        }

        return neighbours;
    }

    /**
     *prints simplistic visualisation of clusters  
     */
    private void printAllClustered(HashSet < Pixel > cluster) {

        System.out.println("--------------------------");

        boolean[][] clusterA = new boolean[(lookAheadAndBack * 2 + 1)][(lookAheadAndBack * 2 + 1)];

        StringBuilder sb = new StringBuilder();

        int minX = 100000;
        int minY = 100000;

        for (Pixel p: cluster) {

            if (p.getX() < minX) minX = p.getX();
            if (p.getY() < minY) minY = p.getY();

        }

        int xToVis = 0;
        int yToVis = 0;

        for (Pixel p: cluster) {

            xToVis = p.getX() - minX;
            xToVis = xToVis >= (lookAheadAndBack * 2 + 1) ? (lookAheadAndBack * 2) : xToVis;
            yToVis = p.getY() - minY;
            yToVis = yToVis >= (lookAheadAndBack * 2 + 1) ? (lookAheadAndBack * 2) : yToVis;

            clusterA[xToVis][yToVis] = true;

        }

        for (int x = 0; x < clusterA.length; x++) {
            for (int y = 0; y < clusterA.length; y++) {

                if (clusterA[x][y]) sb.append("X");
                else sb.append("_");

            }
            System.out.println(sb.toString());
            sb = new StringBuilder();
        }
    }

    public void drawNoded() {
        noded.draw();
    }

    public void vizualizeNoded() {
        System.out.println("Number of nodes: " + nmbOfNodes);
        drawNoded();
    }
    public ImageResource getNodedImage() {
        return noded;
    }
    public ArrayList < Node > getNodes() {
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
    @SuppressWarnings("unused")
    private void setYellow(Pixel p) {
        p.setRed(yellowScheme[0]);
        p.setGreen(yellowScheme[1]);
        p.setBlue(yellowScheme[2]);
    }
}
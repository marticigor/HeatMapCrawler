package core;

import java.util.*;
import javax.swing.SwingUtilities;

import lib_duke.ImageResource;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class Runner implements Runnable {

    //TODO DI for defaults

    private final int devi, look, surface;

    private final int bottleneckSize = 3;
    private final int passableSize = 5;

    private final int sizeDivKonq = 8;

    private ImageChunks chunks;

    //this border is necessary for kernel convolution later on, not necessary
    //in sharpen stage though
    private final int borderInSharpenStage = ((Math.max(bottleneckSize, passableSize)) - 1) / 2;

    /**
     * 
     */
    public Runner() {

        //TODO DI for defaults

        this.devi = 56;
        this.look = 3;
        this.surface = 46;
    }

    /**
     * 
     */
    public Runner(int v1, int v2, int v3) {
        this.devi = v1;
        this.look = v2;
        this.surface = v3;
    }

    /**
     * 
     */
    public static void main(String[] args) {

        /*
         *In Swing programs, the initial threads don't have a lot to do. Their most essential job
         *is to create a Runnable object that initializes the GUI
         *and schedule that object for execution on the event dispatch thread.
         *
         *
         *An initial thread schedules the GUI creation task by invoking
         *javax.swing.SwingUtilities.invokeLater or
         *javax.swing.SwingUtilities.invokeAndWait
         *Both of these methods take a single argument:
         *the Runnable that defines the new task.
         *Their only difference is indicated by their names:
         *invokeLater simply schedules the task and returns;
         *invokeAndWait waits for the task to finish before returning. 
         */
        System.out.println("this initial Thread is EDT " + SwingUtilities.isEventDispatchThread());
        ControlWin control = new ControlWin(); //ControlWin implements Runnable
        SwingUtilities.invokeLater(control);
    }

    /**
     *
     */
    public void run() {

        //#################################################################################
        boolean visual = false;
        boolean debug = false;
        //#################################################################################   

        final ImagePreprocesor ip = new ImagePreprocesor(devi, borderInSharpenStage);

        chunks = new ImageChunks(ip.getX(), ip.getY(), sizeDivKonq);
        perManyTasksProces(ip);

        final ImageResource procesedMap = ip.getProcesed();

        NodeFinder nf = new NodeFinder(procesedMap, look, surface);
        nf.findNodes();
        nf.vizualizeNoded(); //DRAW

        final ImageResource noded = nf.getNodedImage();
        final List < Node > nodes = nf.getNodes();

        AdjacencyFinder af = new AdjacencyFinder(noded, nodes, visual, debug, bottleneckSize, passableSize);
        af.buildAdjacencyLists();
        
        //TEST PRINT
        //TEST PRINT
        //TEST PRINT
        //TEST PRINT
        //TEST PRINT
        
        //printBuiltNodes(nodes);
    }

    /**
     * 
     */
    private void perManyTasksProces(final ImagePreprocesor ip) {

        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "4");
        ParallelExecutionImageProcesTask[] procesTasks = new ParallelExecutionImageProcesTask[sizeDivKonq * sizeDivKonq];

        int i = 0;
        for (int y = 0; y < sizeDivKonq; y++) {
            for (int x = 0; x < sizeDivKonq; x++) {
                procesTasks[i] = new ParallelExecutionImageProcesTask(ip, chunks.fromX[x], chunks.toX[x],
                    chunks.fromY[y], chunks.toY[y]);

                //System.out.println("DEBUG chunks.fromX[x] " + chunks.fromX[x]);
                //System.out.println("DEBUG chunks.toX[x] " + chunks.toX[x]);
                //System.out.println("DEBUG chunks.fromY[y] " + chunks.fromY[y]);
                //System.out.println("DEBUG chunks.toY[y] " + chunks.toY[y]);

                ForkJoinPool.commonPool().invoke(procesTasks[i]);
                i++;
            }
        }
    }

    private class ParallelExecutionImageProcesTask extends RecursiveAction {
        //Instances of RecursiveAction represent executions that do not yield a return value.

        //First and foremost, fork/join tasks should operate as “pure” in-memory algorithms in which no I/O operations
        //come into play. Also, communication between tasks through shared state should be avoided as much as possible,
        //because that implies that locking might have to be performed.
        //Ideally, tasks communicate only when one task forks another or when one task joins another.

        //http://www.oracle.com/technetwork/articles/java/fork-join-422606.html

        /**
         * 
         */
        private static final long serialVersionUID = 8116076238536681371L;
        final int xFromIncl;
        final int xToExcl;

        final int yFromIncl;
        final int yToExcl;

        final ImagePreprocesor ip;

        /**
         * 
         */
        private ParallelExecutionImageProcesTask(ImagePreprocesor ip, int xFrom, int xTo, int yFrom, int yTo) {
            this.ip = ip;
            this.xFromIncl = xFrom;
            this.xToExcl = xTo;
            this.yFromIncl = yFrom;
            this.yToExcl = yTo;
        }

        @Override
        protected void compute() {
            System.out.println("COMPUTE");
            ip.proces(xFromIncl, xToExcl, yFromIncl, yToExcl, false); //false for real segments
        }
    }

    /**
     * 
     */
    private void printBuiltNodes(List < Node > nodes) {
        for (Node n: nodes) {
            System.out.println("------------------------------------------------------");
            System.out.println("node " + n.toString());
            HashSet < Node > adjacents = n.getAdjacentNodes();
            for (Node adjacent: adjacents) {
                System.out.println("\t\t" + "adjacent " + adjacent.toString());
            }
            System.out.println("------------------------------------------------------");
        }
    }
}
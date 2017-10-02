package core;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import javax.swing.SwingUtilities;

import core.tasks.TaskCanny;
import core.tasks.TaskGaussian;
import core.tasks.TaskHighlight;
import core.tasks.TaskSharpen;
import lib_duke.ImageResource;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class Runner implements Runnable {

    //TODO DI for defaults

    private final int devi, look, surface;

    private final int bottleneckSize = 3;
    private final int passableSize = 5;

    private final int sizeDivKonq = 4;

    private ImageChunks chunks;

    //this border is necessary for kernel convolution later on, not necessary
    //in sharpen stage though
    private final int borderInSharpenStage = ((Math.max(bottleneckSize, passableSize)) - 1) / 2;

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
         *In Swing programs, the initial threads don't have a lot to do.Their most essential job
         *is to create a Runnable object that initializes the GUI
         *and schedule that object for execution on the event dispatch thread.
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
    
    //#################################################################################
    boolean visual = false;
    boolean debug = false;
    //################################################################################# 
    
    /**
     *
     */
    public void run() {

        final ImagePreprocesor ip = new ImagePreprocesor(devi, borderInSharpenStage, visual, debug);

        chunks = new ImageChunks(ip.getX(), ip.getY(), sizeDivKonq);
        perManyTasksProces(ip);

        final ImageResource procesedMapStage = ip.getProcesedStage();
        procesedMapStage.draw();
        final ImageResource procesedMap = ip.getProcesed();
        procesedMap.draw();

        NodeFinder nf = new NodeFinder(procesedMap, look, surface);
        nf.findNodes();
        //nf.vizualizeNoded(); //DRAW

        final ImageResource noded = nf.getNodedImage();
        final List < Node > nodes = nf.getNodes();

        AdjacencyFinder af = new AdjacencyFinder(noded, nodes, visual, debug, bottleneckSize, passableSize);
        af.buildAdjacencyLists();
        af.drawAdjacencyEdges();

        //TEST PRINT
        //printBuiltNodes(nodes);
    }

    /**
     * 
     */
    private void perManyTasksProces(final ImagePreprocesor ip) {

        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "8");
              
        List<RecursiveAction []> stages = new ArrayList<RecursiveAction []>();
        
        //FILTERS QUEUE FIFO START
          
        TaskSharpen [] sharpenTask = new TaskSharpen[sizeDivKonq * sizeDivKonq];
        decorateFactory(sharpenTask, TaskSharpen.class, ip);
        stages.add(sharpenTask);
        
        TaskGaussian [] gaussianTask = new TaskGaussian[sizeDivKonq * sizeDivKonq];
        decorateFactory(gaussianTask, TaskGaussian.class, ip);
        //stages.add(gaussianTask);
        
        TaskCanny[] cannyTask = new TaskCanny[sizeDivKonq * sizeDivKonq];
        decorateFactory(cannyTask, TaskCanny.class, ip);
        //stages.add(cannyTask);
        
        TaskHighlight [] highlightTask = new TaskHighlight [sizeDivKonq * sizeDivKonq];
        decorateFactory(highlightTask, TaskHighlight.class, ip);
        //stages.add(highlightTask);
        
        //--------------------------------------------------
        //FILTERS QUEUE FIFO END
        
        ForkJoinPool forkJoinPool = new ForkJoinPool(8);
        
		for (RecursiveAction [] stage : stages){
			for (RecursiveAction segment : stage){
					//debug print1 at bottom
					forkJoinPool.invoke(segment);//commonPool?
			}
			if (debug) System.out.println("RETURNED IN LOOP  " + System.currentTimeMillis());
		}
				
    }
    @SuppressWarnings("unchecked")
	private <T extends RecursiveAction> void decorateFactory(T[] task,
			@SuppressWarnings("rawtypes") Class ref,
			ImagePreprocesor ip){
    	@SuppressWarnings("rawtypes")
		Constructor constructor = null;
		try {
			constructor = ref.getConstructor(
					core.ImagePreprocesor.class,
					java.lang.Integer.class,
					java.lang.Integer.class,
					java.lang.Integer.class,
					java.lang.Integer.class
					);
		} catch (NoSuchMethodException | SecurityException e1) {
			e1.printStackTrace();
			System.exit(1);
		}
    	int i = 0;
		for (int y = 0; y < sizeDivKonq; y++){
			for (int x = 0; x < sizeDivKonq; x++){
			    try {
					task [i] = (T) constructor.newInstance
					    (ip, chunks.fromX[x], chunks.toX[x], chunks.fromY[y], chunks.toY[y]);
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
     */
    @SuppressWarnings("unused")
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

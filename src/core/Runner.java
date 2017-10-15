package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import javax.swing.SwingUtilities;

import core.tasks.TaskCanny;
import core.tasks.TaskGaussian;
import core.tasks.TaskHighlight;
import core.tasks.TaskSharpen;
import core.tasks.TaskSkeleton;
import lib_duke.DirectoryResource;
import lib_duke.ImageResource;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class Runner implements Runnable {

    //TODO DI for defaults

    private final int devi, look, surface;

    private final int bottleneckSize = 3; //3
    private final int passableSize = 3; //3

    private final int sizeDivKonq = 4;

    private ImageChunks chunks;

    //this border is necessary for kernel convolution later on, not necessary
    //in sharpen stage
    private final int borderInSharpenStage = 2; //((Math.max(bottleneckSize, passableSize)) - 1) / 2;
    //private final int borderInSharpenStage = ((Math.max(bottleneckSize, passableSize)) - 1) / 2;

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
    boolean visual = false; // also pauses execution now and then
    boolean debug = false;
    //################################################################################# 

    /**
     *
     */
    public void run() {

        DirectoryResource dirRPng = new DirectoryResource();
        
        DirectoryResource dirRTxt = new DirectoryResource();

        List < File > lFPng = new ArrayList < File > ();
        for (File f: dirRPng.selectedFiles()) lFPng.add(f);
        
        List < File > lFTxt = new ArrayList < File > ();
        for (File f: dirRTxt.selectedFiles()) lFTxt.add(f);

        for (int i = 0; i < 100; i++) { //stress tests

            System.out.println("---------------------------------------------------------" +
                "--------- iter " + i);

            for (File iteratedFile: lFPng) {
                
                System.out.println("PROCESING " + iteratedFile.toString());
                String fileName = iteratedFile.getName();
                fileName = fileName.substring(0, fileName.indexOf('.'));
                String fileNameTxt = fileName + ".txt";
                
                //get the description file
                File description = null;
                for (File f : lFTxt){
                	String currName = f.getName();
                	if(currName.equals(fileNameTxt)){
                		description = f;
                		break;
                	}
                }
                if(description == null) throw new RuntimeException("Text description file not found.");
                
                //read description into string
                String readLine = null;
                try {
                	BufferedReader br = new BufferedReader(new FileReader(description));
                    System.out.println("Reading file " + description);
                    while ((readLine = br.readLine()) != null) {
                        System.out.println(readLine);
                        break;
                    }
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {}
                
                //convert string into double []
                double [] bounds = new double [6];
                //0 lon east
                //1 lat north
                //2 lat south
                //3 lon west
                //4 lat center
                //5 lon center
                
                if(readLine == null) throw new RuntimeException("No description in file.");
                
                //there are 12 characters with special meanings: the backslash \,
                //the caret ^, the dollar sign $,
                //the period or dot ., the vertical bar or pipe symbol |,
                //the question mark ?, the asterisk or star *, the plus sign +,
                //the opening parenthesis (, the closing parenthesis ),
                //and the opening square bracket [, the opening curly brace
                //{, These special characters are often called "metacharacters".
                
                
				String [] values = readLine.split(Pattern.quote("|"));
				
				if(values.length != 7) throw new RuntimeException("Length.");//beginning "|"
				
                System.out.println("___________________________________");
				for (int j = 1; j < 7; j++){
					System.out.println(values[j]);
					bounds[j - 1] = Double.parseDouble(values[j]);
				}
                System.out.println("___________________________________");
				
                ImageResource image = new ImageResource(iteratedFile);
                ImagePreprocesor ip = new ImagePreprocesor(devi, borderInSharpenStage, visual, debug, image);

                chunks = new ImageChunks(ip.getX(), ip.getY(), sizeDivKonq);
                perManyTasksProces(ip);

                if (visual) {
                    image.draw();
                    Pause.pause(2000);
                    final ImageResource procesedMapStage = ip.getProcesedStage();
                    procesedMapStage.draw();
                    Pause.pause(2000);
                }

                ImageResource procesedMap = ip.getProcesed();
                if (visual) {
                    procesedMap.draw();
                    Pause.pause(2000);
                }

                NodeFinder nf = new NodeFinder(procesedMap, look, surface, this, bounds);
                nf.findNodes();

                ImageResource noded = nf.getNodedImage();
                if (visual) noded.draw();

                final List < Node > nodes = nf.getNodes();

                AdjacencyFinder af = new AdjacencyFinder(noded, nodes, visual, debug, bottleneckSize, passableSize);
                af.buildAdjacencyLists();

                if (visual) {
                    af.drawAdjacencyEdges();
                    Pause.pause(3000);
                } else {}
            }

        }
    }

    private long id = -1;
    //no thread safe compound action
    public long incrAndGetId() {
        id++;
        return id;
    }

    /**
     * 
     */
    private void perManyTasksProces(final ImagePreprocesor ip) {

        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "8");

        List < RecursiveAction[] > stages = new ArrayList < RecursiveAction[] > ();

        //FILTERS QUEUE FIFO START

        //thresholding simple
        TaskSharpen[] sharpenTask = new TaskSharpen[sizeDivKonq * sizeDivKonq];
        decorateFactory(sharpenTask, TaskSharpen.class, ip);
        stages.add(sharpenTask);

        //TaskGaussian [] gaussianTask = new TaskGaussian[sizeDivKonq * sizeDivKonq];
        //decorateFactory(gaussianTask, TaskGaussian.class, ip);
        //stages.add(gaussianTask);

        TaskSkeleton[] skeletonTask = new TaskSkeleton[sizeDivKonq * sizeDivKonq];
        decorateFactory(skeletonTask, TaskSkeleton.class, ip);
        stages.add(skeletonTask);

        //TaskCanny[] cannyTask = new TaskCanny[sizeDivKonq * sizeDivKonq];
        //decorateFactory(cannyTask, TaskCanny.class, ip);
        //stages.add(cannyTask);

        //TaskHighlight [] highlightTask = new TaskHighlight [sizeDivKonq * sizeDivKonq];
        //decorateFactory(highlightTask, TaskHighlight.class, ip);
        //stages.add(highlightTask);

        //--------------------------------------------------
        //FILTERS QUEUE FIFO END

        ForkJoinPool forkJoinPool = new ForkJoinPool(8);

        for (RecursiveAction[] stage: stages) {
            for (RecursiveAction segment: stage) {
                //debug print1 at bottom
                forkJoinPool.invoke(segment); //commonPool?
            }
            if (debug) System.out.println("RETURNED IN LOOP  " + System.currentTimeMillis());
        }

    }
    @SuppressWarnings("unchecked")
    private < T extends RecursiveAction > void decorateFactory(T[] task,
        @SuppressWarnings("rawtypes") Class ref,
        ImagePreprocesor ip) {
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
        for (int y = 0; y < sizeDivKonq; y++) {
            for (int x = 0; x < sizeDivKonq; x++) {
                try {
                    task[i] = (T) constructor.newInstance(ip, chunks.fromX[x], chunks.toX[x], chunks.fromY[y], chunks.toY[y]);
                } catch (InstantiationException | IllegalAccessException | IllegalArgumentException |
                    InvocationTargetException e) {
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
package core.tasks;

import java.util.concurrent.RecursiveAction;

import core.ImagePreprocesor;

    public class TaskSharpen extends RecursiveAction {
    	
        //Instances of RecursiveAction represent executions that do not yield a return value.

        //First and foremost, fork/join tasks should operate as “pure” in-memory
    	//algorithms in which no I/O operations come into play. Also, communication
    	//between tasks through shared state should be avoided as much as possible,
        //because that implies that locking might have to be performed. Ideally, tasks
    	//communicate only when one task forks another or when one task joins another.

        //http://www.oracle.com/technetwork/articles/java/fork-join-422606.html

        /**
         * 
         */
        private static final long serialVersionUID = 8116076238536681371L;
        
        private final int xFromIncl;
        private final int xToExcl;
        private final int yFromIncl;
        private final int yToExcl;

        private final ImagePreprocesor ip;
        
        /**
         * 
         */
        public TaskSharpen(ImagePreprocesor ip, Integer xFrom, Integer xTo, Integer yFrom, Integer yTo) {
            this.ip = ip;
            //note (int) only, of course
            this.xFromIncl = (int)xFrom;
            this.xToExcl = (int)xTo;
            this.yFromIncl = (int)yFrom;
            this.yToExcl = (int)yTo;
        }

        @Override
        protected void compute() {
            //System.out.println("COMPUTE SHARPEN ON THREAD " + Thread.currentThread());
            ip.procesSharpen(xFromIncl, xToExcl, yFromIncl, yToExcl, false); //false for real segments
        }
        
        /**
         * 
         */
        public String toString(){
        	return this.hashCode() + this.getClass().getName() + " -----| " + this.xFromIncl + " | " +
            this.xToExcl + " | " + this.yFromIncl + " | " + this.yToExcl + " -----| " + ip;
        }
    }


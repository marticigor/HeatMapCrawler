package core.node_finder_utils;

import java.util.*;

import ifaces.node_finder.I_PixelExam;
import ifaces.node_finder.I_PixelSelector;
import lib_duke.Pixel;

public class TopLeftOfClosest implements I_PixelSelector {

	private Set <Pixel> outputSet;
	
	@Override
	public void proces(Set<Pixel> inputSet, I_PixelExam exam, int... args) {
		assert(exam == null);
		assert(args.length == 1 && args [0] == 0);
		CallForDimen xCall = new Xcaller();
		CallForDimen yCall = new Ycaller();
		List<Pixel> survivors = new ArrayList<Pixel>(inputSet);
		List<Pixel> prune = new ArrayList<Pixel>();
		CallForDimen [] stages = new CallForDimen []{xCall::get, yCall::get};
		
		for(CallForDimen stage : stages){
			prune.clear();
			//iterate survivors, find min x y respectively, prune
			int min = Integer.MAX_VALUE, curr;
			for (Pixel p : survivors){
				curr = stage.get(p);
				if(curr < min) min = curr;
			}
			for (Pixel p : survivors) if(stage.get(p) != min) prune.add(p);
			for (Pixel p : prune) survivors.remove(p);
		}
		if(survivors.size() != 1)throw new RuntimeException("survivors.size() != 1");
		outputSet = new HashSet <Pixel>(survivors);
	}

	private interface CallForDimen { int get(Pixel p); }
	private class Xcaller implements CallForDimen{	
		public int get(Pixel p) { return p.getX(); }
	}
	private class Ycaller implements CallForDimen{
		public int get(Pixel p) { return p.getY(); }
	}
	
	@Override
	public Set<Pixel> getSet() {
		return outputSet;
	}
}

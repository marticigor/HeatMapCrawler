package core.nodes_filters;

import java.util.LinkedList;
import java.util.List;

import core.Node;
import ifaces.I_NodeFilter;

public class ZeroAdjacentsFilter implements I_NodeFilter {

	List <Node> zeroAdj = new LinkedList <Node>();
	
	@Override
	public boolean passes(Node node) {
		return (node.getAdjacentNodes().size() != 0);
	}

	@Override
	public List<Node> procesChunk(List<Node> nodes) {
		List <Node> filtered = new LinkedList <Node>();
		for(Node n : nodes)
			if(passes(n)) filtered.add(n);
			else zeroAdj.add(n);

		return filtered;
	}
	
	@Override
	public List<Node> getFilteredOut(){
		return zeroAdj;
	}

}

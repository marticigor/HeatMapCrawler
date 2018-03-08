package core.nodes_filters;

import java.util.LinkedList;
import java.util.List;

import core.Node;
import ifaces.I_NodeFilter;

public class ZeroAdjacencyNodesFilter implements I_NodeFilter {

	@Override
	public boolean passes(Node node) {
		return (node.getAdjacentNodes().size() != 0);
	}

	@Override
	public List<Node> procesChunk(List<Node> nodes) {
		List<Node> noZeroAdjacent = new LinkedList<Node>();
		for (Node n : nodes)
			if (passes(n))
				noZeroAdjacent.add(n);
		return noZeroAdjacent;
	}

	@Override
	public List<Node> getFilteredOut() {
		return null;
	}

}

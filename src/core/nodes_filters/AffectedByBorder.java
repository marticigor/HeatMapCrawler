package core.nodes_filters;

import java.util.ArrayList;
import java.util.List;

import core.Node;
import ifaces.I_NodeFilter;

public class AffectedByBorder implements I_NodeFilter {

	private int border, width, height;

	public AffectedByBorder(int border, int width, int height) {
		this.border = border;
		this.width = width;
		this.height = height;
	}

	@Override
	public boolean passes(Node node) {
		if (node.getX() > border && node.getX() < width - border && node.getY() > border
				&& node.getY() < height - border)
			return true;
		return false;
	}

	@Override
	public List<Node> procesChunk(List<Node> nodes) {
		List<Node> survivors = new ArrayList<Node>();
		for (Node n : nodes) {
			if (passes(n))
				survivors.add(n);
		}
		return survivors;
	}

	@Override
	public List<Node> getFilteredOut() {
		throw new UnsupportedOperationException(this.getClass().toString());
	}

}

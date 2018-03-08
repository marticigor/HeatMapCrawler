package ifaces;

import java.util.List;

import core.Node;

public interface I_NodeFilter {

	public boolean passes(Node node);

	public List<Node> procesChunk(List<Node> nodes);

	public List<Node> getFilteredOut();

}

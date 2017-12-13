package mockery;

import java.util.LinkedList;
import java.util.List;

import core.Node;

public class NodeGraphMocks {

	// https://www.dropbox.com/s/kt86vpg8gihrgjj/2017-10-24%2012.57.22.jpg?dl=0
	public List<Node> getMocks1() {

		Node[] nodes = new Node[5];
		List<Node> list = new LinkedList<Node>();
		// public Node(int x, int y, double lon, double lat, long id, long
		// shotId)
		nodes[0] = new Node(12, 1, (short)-1, 12.0, 1.0, 0, 0);
		nodes[1] = new Node(13, 1, (short)-1, 13.0, 1.0, 1, 1);
		nodes[2] = new Node(14, 1, (short)-1, 14.0, 1.0, 2, 2);
		nodes[3] = new Node(15, 2, (short)-1, 15.0, 2.0, 3, 3);
		nodes[4] = new Node(16, 1, (short)-1, 16.0, 1.0, 4, 4);

		nodes[1].addAdjacentNode(nodes[2]);

		nodes[2].addAdjacentNode(nodes[3]);
		nodes[2].addAdjacentNode(nodes[4]);

		nodes[3].addAdjacentNode(nodes[2]);
		nodes[3].addAdjacentNode(nodes[4]);

		nodes[4].addAdjacentNode(nodes[3]);
		nodes[4].addAdjacentNode(nodes[2]);
		for (Node n : nodes)
			list.add(n);

		return list;
	}

}

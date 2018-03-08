package core.nodes_decorators;

import java.util.List;
import java.util.Set;

import core.Node;

public class TestMutualVisibility {

	private Set<Node> leftAdj;
	private Set<Node> rightAdj;
	// private ImageResource investigate = new ImageResource (1000, 1000);
	// private boolean investigative = false; //sad..
	// private LineMaker lm = new LineMaker(investigate);

	// assure there is no one way edge, this indicates error
	public int proces(List<Node> nodes) {

		int newEdges = 0;
		for (Node left : nodes) {
			leftAdj = left.getAdjacentNodes();
			if (leftAdj.contains(left))
				throw new RuntimeException("reference to itself");
			for (Node right : leftAdj) {
				rightAdj = right.getAdjacentNodes();
				if (!rightAdj.contains(left)) {
					right.addAdjacentNode(left);
					newEdges++;
					// drawLine(right, left);
					// Pixel sink = investigate.getPixel(left.getX(),
					// left.getY());
					// sink.setRed(0);
					// sink.setGreen(255);
					// sink.setBlue(255);
					// if(investigative) System.out.println("adding visibility
					// from: " + right + " to: " + left);
				}
			}
		}
		// investigate.draw();
		return newEdges;
	}

	@SuppressWarnings("unused")
	private void drawLine(Node right, Node left) {
		// lm.drawLine(right.getX(), right.getY(), left.getX(), left.getY(),
		// 255, 255, 255);
	}
}

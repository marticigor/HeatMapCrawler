package core.nodes_decorators;

import java.util.List;
import java.util.Set;

import core.Node;

public class CorrectMutualVisibility {

	Set <Node> leftAdj;
	Set <Node> rightAdj;
	
	//assure there is no one way edge (some corner cases in dense graphs)
	public int proces(List<Node> nodes){
		
		int newEdges = 0;
		for (Node left : nodes){
			leftAdj = left.getAdjacentNodes();
			if(leftAdj.contains(left)) throw new RuntimeException("reference to itself");
			for (Node right : leftAdj){
				rightAdj = right.getAdjacentNodes();
				if(!rightAdj.contains(left)){
					right.addAdjacentNode(left);
					newEdges ++;
				}
			}	
		}
		return newEdges;
	}
}

package apps;

import structures.*;
import java.util.ArrayList;

public class MST {
	
	/**
	 * Initializes the algorithm by building single-vertex partial trees
	 * 
	 * @param graph Graph for which the MST is to be found
	 * @return The initial partial tree list
	 */
	public static PartialTreeList initialize(Graph graph) {
	
		/* COMPLETE THIS METHOD */
		//Step1: create empty tree
		PartialTreeList ptl = new PartialTreeList();

		//Step2:
		for (Vertex v : graph.vertices) {
			PartialTree pt = new PartialTree(v);
			Vertex.Neighbor ptr = v.neighbors;

			while (ptr != null) {
				PartialTree.Arc x = new PartialTree.Arc(v, ptr.vertex, ptr.weight);
				pt.getArcs().insert(x);
				ptr = ptr.next;
			}

			ptl.append(pt);
		}

		return ptl;

	}

	/**
	 * Executes the algorithm on a graph, starting with the initial partial tree list
	 * 
	 * @param ptlist Initial partial tree list
	 * @return Array list of all arcs that are in the MST - sequence of arcs is irrelevant
	 */
	public static ArrayList<PartialTree.Arc> execute(PartialTreeList ptlist) {
		
		/* COMPLETE THIS METHOD */
		ArrayList<PartialTree.Arc> result = new ArrayList<>();

		while (ptlist.size() > 1) {
			PartialTree ptx = null;
			PartialTree pty = null;
			PartialTree.Arc a = null;
			MinHeap<PartialTree.Arc> pqx;
			MinHeap<PartialTree.Arc> pqy;

			//Step3: remove first partial tree
			ptx = ptlist.remove();
			pqx = ptx.getArcs();

			//Step4: extract first arc from pq
			a = pqx.deleteMin();

			//Step5: if roots are same delete and move on
			while (a.v1.getRoot().equals(a.v2.getRoot())) {
				a = pqx.deleteMin();
			}

			//step6: add arc to arraylist of arcs
			result.add(a);

			//step7: remove tree containing second vertex
			pty = ptlist.removeTreeContaining(a.v2);

			//step8: merge PTY and pTX
			ptx.merge(pty);

			//step9:add tree to list
			ptlist.append(ptx);
		}


		return result;

	}
}

package structures;

import javax.swing.tree.TreeNode;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * Encapsulates an interval tree.
 *
 * @author runb-cs112
 */
public class IntervalTree {

	/**
	 * The root of the interval tree
	 */
	IntervalTreeNode root;

	/**
	 * Constructs entire interval tree from set of input intervals. Constructing the tree
	 * means building the interval tree structure and mapping the intervals to the nodes.
	 *
	 * @param intervals Array list of intervals for which the tree is constructed
	 */
	public IntervalTree(ArrayList<Interval> intervals) {

		// make a copy of intervals to use for right sorting
		ArrayList<Interval> intervalsRight = new ArrayList<Interval>(intervals.size());
		for (Interval iv : intervals) {
			intervalsRight.add(iv);
		}

		// rename input intervals for left sorting
		ArrayList<Interval> intervalsLeft = intervals;

		// sort intervals on left and right end points
		sortIntervals(intervalsLeft, 'l');
		sortIntervals(intervalsRight, 'r');

		// get sorted list of end points without duplicates
		ArrayList<Integer> sortedEndPoints =
				getSortedEndPoints(intervalsLeft, intervalsRight);

		// build the tree nodes
		root = buildTreeNodes(sortedEndPoints);

		// map intervals to the tree nodes
		mapIntervalsToTree(intervalsLeft, intervalsRight);
	}

	/**
	 * Returns the root of this interval tree.
	 *
	 * @return Root of interval tree.
	 */
	public IntervalTreeNode getRoot() {
		return root;
	}

	/**
	 * Sorts a set of intervals in place, according to left or right endpoints.
	 * At the end of the method, the parameter array list is a sorted list.
	 *
	 * @param intervals Array list of intervals to be sorted.
	 * @param lr        If 'l', then sort is on left endpoints; if 'r', sort is on right endpoints
	 */
	public static void sortIntervals(ArrayList<Interval> intervals, char lr) {
		// COMPLETE THIS METHOD
		if (intervals == null) {
			return;
		}
		else if (intervals.size() != 0) {
			quicksort(intervals, lr, 0, intervals.size() - 1);
		}
	}

	/**
	 * Given a set of intervals (left sorted and right sorted), extracts the left and right end points,
	 * and returns a sorted list of the combined end points without duplicates.
	 *
	 * @param leftSortedIntervals  Array list of intervals sorted according to left endpoints
	 * @param rightSortedIntervals Array list of intervals sorted according to right endpoints
	 * @return Sorted array list of all endpoints without duplicates
	 */
	public static ArrayList<Integer> getSortedEndPoints(ArrayList<Interval> leftSortedIntervals, ArrayList<Interval> rightSortedIntervals) {
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE PROGRAM COMPILE
		if (leftSortedIntervals == null && rightSortedIntervals == null) {
			return null;
		}

		ArrayList<Integer> endpoints = new ArrayList<>();

		//add left endpoints no duplicates
		for (Interval x : leftSortedIntervals) {
			if (!endpoints.contains(x.leftEndPoint)) {
				endpoints.add(x.leftEndPoint);
			}
		}
		//add right endpoints no duplicates
		for (Interval x : rightSortedIntervals) {
			if (!endpoints.contains((x.rightEndPoint))) {
				endpoints.add(x.rightEndPoint);
			}
		}

		//sort array
		quickSortEndpoints(endpoints, 0, endpoints.size() - 1);
		return endpoints;
	}

	/**
	 * Builds the interval tree structure given a sorted array list of end points
	 * without duplicates.
	 *
	 * @param endPoints Sorted array list of end points
	 * @return Root of the tree structure
	 */
	public static IntervalTreeNode buildTreeNodes(ArrayList<Integer> endPoints) {
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE PROGRAM COMPILE
		if (endPoints == null) {
			return null;
		}

		Queue<IntervalTreeNode> Q = new Queue<>();
		ArrayList<Interval> al = new ArrayList<Interval>(0);
		IntervalTreeNode temp;
		IntervalTreeNode t1;
		IntervalTreeNode t2;
		float v1;
		float v2;
		float x;

		for (Integer i : endPoints) {
			temp = new IntervalTreeNode(i, i, i);
			Q.enqueue(temp);
		}

		while (Q.size() > 1) {
			int tempsize = Q.size();

			while (tempsize > 1) {
				t1 = Q.dequeue();
				t2 = Q.dequeue();
				tempsize = tempsize - 2;

				v1 = t1.maxSplitValue;
				v2 = t2.minSplitValue;

				//x = split value
				x = (v1 + v2) / 2;


				IntervalTreeNode n = new IntervalTreeNode(x, t1.minSplitValue, t2.maxSplitValue);
				n.leftChild = t1;
				n.rightChild = t2;

				Q.enqueue(n);
			}
			if (tempsize == 1) {
				Q.enqueue(Q.dequeue());

			}
		}

		if (Q.size() == 0) {
			return null;
		}

		IntervalTreeNode r = Q.dequeue();
		return r;
	}

	/**
	 * Maps a set of intervals to the nodes of this interval tree.
	 *
	 * @param leftSortedIntervals  Array list of intervals sorted according to left endpoints
	 * @param rightSortedIntervals Array list of intervals sorted according to right endpoints
	 */
	public void mapIntervalsToTree(ArrayList<Interval> leftSortedIntervals, ArrayList<Interval> rightSortedIntervals) {
		// COMPLETE THIS METHOD
		if (leftSortedIntervals == null && rightSortedIntervals == null) {
			return;
		}

		for (Interval i : leftSortedIntervals) {
			IntervalTreeNode n = findHighestNodeInInterval(root, i);
			if(n.leftIntervals == null){
				ArrayList<Interval> al = new ArrayList<Interval>();
				al.add(i);
				n.leftIntervals = al;
			}
			else{
				n.leftIntervals.add(i);
			}

		}
		for (Interval i : rightSortedIntervals) {
			IntervalTreeNode n = findHighestNodeInInterval(root, i);
			if (n.rightIntervals == null) {
				ArrayList<Interval> al = new ArrayList<Interval>();
				al.add(i);
				n.rightIntervals = al;
			} else {
				n.rightIntervals.add(i);
			}
		}
	}

	/**
	 * Gets all intervals in this interval tree that intersect with a given interval.
	 *
	 * @param q The query interval for which intersections are to be found
	 * @return Array list of all intersecting intervals; size is 0 if there are no intersections
	 */
	public ArrayList<Interval> findIntersectingIntervals(Interval q) {
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE PROGRAM COMPILE
		ArrayList<Interval> resultlist = query2(root, q);
		return resultlist;
	}


	private static int partition(ArrayList<Interval> arrlist, char lr, int left, int right) {
		int i = left, j = right;
		Interval temp;
		int pivot;

		if (lr == 'l') {
			pivot = arrlist.get((left + right) / 2).leftEndPoint;

			while (i <= j) {
				while (arrlist.get(i).leftEndPoint < pivot) {
					i++;
				}
				while (arrlist.get(j).leftEndPoint > pivot) {
					j--;
				}
				if (i <= j) {
					temp = arrlist.get(i);
					arrlist.set(i, arrlist.get(j));
					arrlist.set(j, temp);
					i++;
					j--;
				}
			}

		} else {
			pivot = arrlist.get((left + right) / 2).rightEndPoint;

			while (i <= j) {
				while (arrlist.get(i).rightEndPoint < pivot) {
					i++;
				}
				while (arrlist.get(j).rightEndPoint > pivot) {
					j--;
				}
				if (i <= j) {
					temp = arrlist.get(i);
					arrlist.set(i, arrlist.get(j));
					arrlist.set(j, temp);
					i++;
					j--;
				}
			}
		}

		return i;
	}

	private static void quicksort(ArrayList<Interval> arrlist, char lr, int left, int right) {
		if (arrlist == null) {
			return;
		}

		int index = partition(arrlist, lr, left, right);
		if (left < index - 1) {
			quicksort(arrlist, lr, left, index - 1);
		}
		if (index < right) {
			quicksort(arrlist, lr, index, right);
		}
	}

	private static int endpointPartition(ArrayList<Integer> arrayList, int left, int right) {
		if (arrayList == null) {
			return -1;
		}
		int i = left, j = right;
		int temp;
		int pivot;

		pivot = arrayList.get((left + right) / 2);

		while (i <= j) {
			while (arrayList.get(i) < pivot) {
				i++;
			}
			while (arrayList.get(j) > pivot) {
				j--;
			}
			if (i <= j) {
				temp = arrayList.get(i);
				arrayList.set(i, arrayList.get(j));
				arrayList.set(j, temp);
				i++;
				j--;
			}
		}
		return i;
	}

	private static void quickSortEndpoints(ArrayList<Integer> arraylist, int left, int right) {
		if (arraylist == null || arraylist.size() == 0) {
			return;
		}
		int index = endpointPartition(arraylist, left, right);
		if (left < index - 1) {
			quickSortEndpoints(arraylist, left, index - 1);
		}
		if (index < right) {
			quickSortEndpoints(arraylist, index, right);
		}
	}

	private static ArrayList<Interval> query(IntervalTreeNode subRoot, Interval i) {
		if (subRoot == null) {
			return null;
		}

		ArrayList<Interval> resultlist = new ArrayList<Interval>();
		Queue<IntervalTreeNode> queue = new Queue<>();
		queue.enqueue(subRoot);


		while (!queue.isEmpty()) {
			IntervalTreeNode node = queue.dequeue();
			if(node.leftIntervals != null){
				for(Interval in : node.leftIntervals){
					if(in.intersects(i) && !resultlist.contains(i)){
						resultlist.add(in);
					}
				}
			}
			else {
				if (node.leftChild != null) {
					queue.enqueue(node.leftChild);
				}
				if (node.rightChild != null) {
					queue.enqueue(node.rightChild);
				}
			}
		}
		return resultlist;
	}

	private static ArrayList<Interval> query2(IntervalTreeNode subRoot, Interval q){
		if (subRoot == null) {
			return null;
		}
		ArrayList<Interval> resultlist = new ArrayList<>();
		IntervalTreeNode R = subRoot;
		float SplitVal = R.splitValue;
		ArrayList<Interval> Llist = subRoot.leftIntervals;
		ArrayList<Interval> RList = subRoot.rightIntervals;
		float x = q.leftEndPoint;
		float y = q.rightEndPoint;


		//if it is a leaf, return empty list
		if (R.leftChild == null && R.rightChild == null) {
			return resultlist;
		}

		//if split falls within interval, add
		if (SplitVal >= x && SplitVal <= y) {
			if(Llist!= null){
				resultlist.addAll(Llist);
			}
			ArrayList<Interval> temp1 = query2(R.leftChild, q);
			ArrayList<Interval> temp2 = query2(R.rightChild, q);

			for(Interval i : temp1){
				if(!resultlist.contains(i)){
					resultlist.add(i);
				}
			}
			for(Interval i : temp2){
				if(!resultlist.contains(i)){
					resultlist.add(i);
				}
			}

		}

		//else if SplitVal falls to the left of i then
		else if (SplitVal < x) {
			if(RList != null) {
				int i = RList.size() - 1;
				while (i >= 0 && (RList.get(i).intersects(q))) {
					resultlist.add(RList.get(i));
					i--;
				}
			}
			ArrayList<Interval> temp = query2(R.rightChild, q);
			for(Interval in : temp){
				if(in.intersects(q) && !resultlist.contains(in)){
					resultlist.add(in);
				}
			}
		}

		//if Split Val falls to the right of i
		else if (SplitVal > y) {
			int i = 0;
			if(Llist!= null) {
				while (i < Llist.size() && Llist.get(i).intersects(q)) {
					resultlist.add(Llist.get(i));
					i++;
				}
			}
			ArrayList<Interval> temp = query2(R.leftChild, q);
			for(Interval in : temp){
				if(in.intersects(q) && !resultlist.contains(in)){
					resultlist.add(in);
				}
			}
		}

		return resultlist;
	}

	private static IntervalTreeNode findHighestNodeInInterval(IntervalTreeNode R, Interval i) {
		if (R == null) {
			return null;
		}

		IntervalTreeNode node = new IntervalTreeNode(0,0,0);
		int x = i.leftEndPoint;
		int y = i.rightEndPoint;

		Queue<IntervalTreeNode> queue = new Queue<IntervalTreeNode>();
		queue.enqueue(R);

		while (!queue.isEmpty()) {
			node = queue.dequeue();
			if (node.splitValue >= x && node.splitValue <=y) {
				return node;
			}
			else {
				if (node.leftChild != null) {
					queue.enqueue(node.leftChild);
				}
				if (node.rightChild != null) {
					queue.enqueue(node.rightChild);
				}
			}
		}
		return node;
	}

	private static void printIntervalTree(IntervalTreeNode roo){
		if (roo == null) {
			return;
		}

		IntervalTreeNode node = new IntervalTreeNode(0,0,0);

		Queue<IntervalTreeNode> queue = new Queue<IntervalTreeNode>();
		queue.enqueue(roo);

		while (!queue.isEmpty()) {
			node = queue.dequeue();
			System.out.println("Left Intervals: " + node.leftIntervals);
			System.out.println("Split Value: " + node.splitValue);
			System.out.println("Right Intervals: " + node.rightIntervals);
			System.out.print("\n");
			if (node.leftChild != null) {
				queue.enqueue(node.leftChild);
			}
			if (node.rightChild != null) {
				queue.enqueue(node.rightChild);
			}
		}
	}
}


	package searchCustom;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import searchShared.NodeQueue;
import searchShared.Problem;
import searchShared.SearchObject;
import searchShared.SearchNode;

import world.GridPos;

public class CustomGraphSearch implements SearchObject {

	private HashSet<SearchNode> explored;
	private NodeQueue frontier;
	protected ArrayList<SearchNode> path;
	private boolean insertFront;

    public CustomGraphSearch(boolean bInsertFront) {
			insertFront = bInsertFront;
    }


	public ArrayList<SearchNode> search(Problem p) {

		frontier = new NodeQueue();
		explored = new HashSet<SearchNode>();
		GridPos startState = (GridPos) p.getInitialState();
		frontier.addNodeToFront(new SearchNode(startState));

		path = new ArrayList<SearchNode>();

		/*
		* The algorithm loops until path is returned, either with the goalState
		* or an empty path which means it failed
		*/

		while(true){

			/*
			* If the frontier is empty, returns empty path (failure)
			*/

			if (frontier.isEmpty()){
				System.out.println("Frontier is empty");
				return path;
				}

			SearchNode currentNode = frontier.removeFirst(); // pop the shallowest leaf

			//System.out.println("currentNode is " +currentNode.getState().getX()+" " + currentNode.getState().getY()  );

			/*
			* Check the currentNode state is the goal state
			* 	if it is true, return the path
			* 	else the node is added to the explored set
			*/
			if (p.isGoalState(currentNode.getState())) {
				path = currentNode.getPathFromRoot();
				return path;
			}
			else {
				explored.add(currentNode);
			}

			// Getting the childStates from the currentNode state
			ArrayList<GridPos> childStates = p.getReachableStatesFrom(currentNode.getState());

		/*
		* Iterate over the childStates and insert the corresponding nodes to the
		* frontier iff they are not already present in it or have already been
		* explored (contained in the explored hash set)
		*
		* Depending on the implementation (BFS or DFS), nodes are added in front or
		* back of the frontier (see report for more information)
		*/
			Iterator iter = childStates.iterator();
			while(iter.hasNext()) {
				SearchNode childNode = new SearchNode((GridPos)iter.next(), currentNode);

				if ((!explored.contains(childNode)) && (!frontier.contains(childNode))) {
					//	System.out.println(childNode.getState().getX() + " " + childNode.getState().getY());
					if (insertFront) {
						frontier.addNodeToFront(childNode);
					}
					else	{
						frontier.addNodeToBack(childNode);
					}
				}
			}

		}
	}

	public ArrayList<SearchNode> getPath() {
		return path;
	}

	public ArrayList<SearchNode> getFrontierNodes() {
		return new ArrayList<SearchNode>(frontier.toList());
	}
	public ArrayList<SearchNode> getExploredNodes() {
		return new ArrayList<SearchNode>(explored);
	}
	public ArrayList<SearchNode> getAllExpandedNodes() {
		ArrayList<SearchNode> allNodes = new ArrayList<SearchNode>();
		allNodes.addAll(getFrontierNodes());
		allNodes.addAll(getExploredNodes());
		return allNodes;
	}

}

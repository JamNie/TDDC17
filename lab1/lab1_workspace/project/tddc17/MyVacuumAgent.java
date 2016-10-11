package tddc17;


import aima.core.environment.liuvacuum.*;
import aima.core.agent.Action;
import aima.core.agent.AgentProgram;
import aima.core.agent.Percept;
import aima.core.agent.State;
import aima.core.agent.impl.*;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;

class MyAgentState
{
	public int[][] world = new int[30][30];
	public int initialized = 0;
	final int UNKNOWN 	= 0;
	final int WALL 		= 1;
	final int CLEAR 	= 2;
	final int DIRT		= 3;
	final int HOME		= 4;
	final int ACTION_NONE 			= 0;
	final int ACTION_MOVE_FORWARD 	= 1;
	final int ACTION_TURN_RIGHT 	= 2;
	final int ACTION_TURN_LEFT 		= 3;
	final int ACTION_SUCK	 		= 4;
	
	public int agent_x_position = 1;
	public int agent_y_position = 1;
	public int agent_last_action = ACTION_NONE;
	
	public static final int NORTH = 0;
	public static final int EAST = 1;
	public static final int SOUTH = 2;
	public static final int WEST = 3;
	public int agent_direction = EAST;
	
	MyAgentState()
	{
		for (int i=0; i < world.length; i++)
			for (int j=0; j < world[i].length ; j++)
				world[i][j] = UNKNOWN;
		world[1][1] = HOME;
		agent_last_action = ACTION_NONE;
	}
	// Based on the last action and the received percept updates the x & y agent position
	public void updatePosition(DynamicPercept p)
	{
		Boolean bump = (Boolean)p.getAttribute("bump");

		if (agent_last_action==ACTION_MOVE_FORWARD && !bump)
	    {
			switch (agent_direction) {
			case MyAgentState.NORTH:
				agent_y_position--;
				break;
			case MyAgentState.EAST:
				agent_x_position++;
				break;
			case MyAgentState.SOUTH:
				agent_y_position++;
				break;
			case MyAgentState.WEST:
				agent_x_position--;
				break;
			}
	    }
		
	}
	
	public void updateWorld(int x_position, int y_position, int info)
	{
		world[x_position][y_position] = info;
	}
	
	public Boolean isUnknown(int x, int y) {
		return world[x][y] == UNKNOWN;
	}
	
	public Boolean isUnknown(Position pos) {
		return world[pos.getX()][pos.getY()] == UNKNOWN;
	}
	
	public Boolean isSquareOfType(Position pos, int typeOfSquare) {
		return world[pos.getX()][pos.getY()] == typeOfSquare;
	}
	
	public Boolean isHome(Position pos) {
		return world[pos.getX()][pos.getY()] == HOME;
	}
	
	public Boolean isWall(Position pos) {
		return isWall(pos.getX(), pos.getY());
	}
	public Boolean isWall(int x, int y) {
		return world[x][y] == WALL;
	}
	
	public void printWorldDebug()
	{
		for (int i=0; i < world.length; i++)
		{
			for (int j=0; j < world[i].length ; j++)
			{
				if (world[j][i]==UNKNOWN)
					System.out.print(" ? ");
				if (world[j][i]==WALL)
					System.out.print(" # ");
				if (world[j][i]==CLEAR)
					System.out.print(" _ ");
				if (world[j][i]==DIRT)
					System.out.print(" D ");
				if (world[j][i]==HOME)
					System.out.print(" H ");
			}
			System.out.println("");
		}
	}
}

class Path {
	Queue<Move> moves = new LinkedList<Move>();
	Move lastElement;
	
	Path(Path path1, Path path2) {
		for(Move move : path1.getMoves()) {
			addMove(move);
		}
		for(Move move : path2.getMoves()) {
			addMove(move);
		}
	}
	
	Path(Move initalMove) {
		addMove(initalMove);
		lastElement = initalMove;
	}
	
	public void addMove(Move move) {
		moves.add(move);
		lastElement = move;
	}
	
	public Queue<Move> getMoves() {
		return moves;
	}
	
	public int getTotalCost() {
		int cost = 0;
		for(Move move : moves) {
			cost += move.getCost();
		}
		return cost;
	}
	
	public Position getDestination() {
		return lastElement.getPosition();
	}
	
	public int getLastMoveDirection() {
		return lastElement.getDirection();
	}
	
	public Queue<ActionWrapper> getActions() {
		Queue<ActionWrapper> actions = new LinkedList<ActionWrapper>();
		
		for(Move move : moves) {
			actions.addAll(move.getActions());
		}
		
		return actions;
	}
}

class Search {
	MyAgentState state;
	HashMap<String, Boolean> explored = new HashMap<String, Boolean>();
	Comparator<Path> comparator = new MoveComparator();
	PriorityQueue<Path> frontier = new PriorityQueue<Path>(400, comparator);
	HashMap<String, Boolean> frontierPositions = new HashMap<String, Boolean>();
	
	
	public Search(MyAgentState state) {
		this.state = state;
	}
	
	private PriorityQueue<Path> findMovesFromPosition(Position pos, int direction) {
		return findMovesFromPosition(pos.getX(), pos.getY(), direction);
	}
	

	public Queue<ActionWrapper> find(int typeOfSquare) {
		explored.clear();
		frontier.clear();
		frontierPositions.clear();
		explored.put(new Position(state.agent_x_position, state.agent_y_position).toString(), true);
		
		PriorityQueue<Path> paths = findMovesFromPosition(state.agent_x_position, state.agent_y_position, state.agent_direction);
		for(Path path : paths) {
			frontier.add(path);
		}
		
		while(!frontier.isEmpty()) {
			Path path = frontier.poll();
			Position pos = path.getDestination();
			
			if(state.isSquareOfType(pos, typeOfSquare)) {
				return path.getActions();
			}
			
			explored.put(pos.toString(), true);
			exploreNode(path, path.getDestination());
		}
		
		return new LinkedList<ActionWrapper>();
	}

	private void exploreNode(Path previous, Position pos) {
		PriorityQueue<Path> childPaths = findMovesFromPosition(pos, previous.getLastMoveDirection());
		
		for(Path childPath : childPaths) {
			Path totalPath = new Path(previous, childPath);
			
			if(!explored.containsKey(totalPath.getDestination())
					&& !frontier.contains(totalPath)
					&& !frontierPositions.containsKey(totalPath.getDestination().toString())) {
				frontierPositions.put(totalPath.getDestination().toString(), true);
				frontier.add(totalPath);
			}
		}
	}
	

	private PriorityQueue<Path> findMovesFromPosition(int x, int y, int direction) {
		Queue<ActionWrapper> actionSequenceNorth = new LinkedList<ActionWrapper>();
		Queue<ActionWrapper> actionSequenceEast = new LinkedList<ActionWrapper>();
		Queue<ActionWrapper> actionSequenceSouth = new LinkedList<ActionWrapper>();
		Queue<ActionWrapper> actionSequenceWest = new LinkedList<ActionWrapper>();
		
		switch(direction) {	    		
    		case MyAgentState.NORTH:
				actionSequenceNorth.add(new ActionWrapper(LIUVacuumEnvironment.ACTION_MOVE_FORWARD, state.ACTION_MOVE_FORWARD));
				
				actionSequenceEast.add(new ActionWrapper(LIUVacuumEnvironment.ACTION_TURN_RIGHT, state.ACTION_TURN_RIGHT));
				actionSequenceEast.add(new ActionWrapper(LIUVacuumEnvironment.ACTION_MOVE_FORWARD, state.ACTION_MOVE_FORWARD));

				actionSequenceSouth.add(new ActionWrapper(LIUVacuumEnvironment.ACTION_TURN_LEFT, state.ACTION_TURN_LEFT));
				actionSequenceSouth.add(new ActionWrapper(LIUVacuumEnvironment.ACTION_TURN_LEFT, state.ACTION_TURN_LEFT));
				actionSequenceSouth.add(new ActionWrapper(LIUVacuumEnvironment.ACTION_MOVE_FORWARD, state.ACTION_MOVE_FORWARD));

				actionSequenceWest.add(new ActionWrapper(LIUVacuumEnvironment.ACTION_TURN_LEFT, state.ACTION_TURN_LEFT));
				actionSequenceWest.add(new ActionWrapper(LIUVacuumEnvironment.ACTION_MOVE_FORWARD, state.ACTION_MOVE_FORWARD));
			
    			break;
    		case MyAgentState.EAST:
				actionSequenceNorth.add(new ActionWrapper(LIUVacuumEnvironment.ACTION_TURN_LEFT, state.ACTION_TURN_LEFT));
				actionSequenceNorth.add(new ActionWrapper(LIUVacuumEnvironment.ACTION_MOVE_FORWARD, state.ACTION_MOVE_FORWARD));
				
				actionSequenceEast.add(new ActionWrapper(LIUVacuumEnvironment.ACTION_MOVE_FORWARD, state.ACTION_MOVE_FORWARD));
				
				actionSequenceSouth.add(new ActionWrapper(LIUVacuumEnvironment.ACTION_TURN_RIGHT, state.ACTION_TURN_RIGHT));
				actionSequenceSouth.add(new ActionWrapper(LIUVacuumEnvironment.ACTION_MOVE_FORWARD, state.ACTION_MOVE_FORWARD));

				actionSequenceWest.add(new ActionWrapper(LIUVacuumEnvironment.ACTION_TURN_LEFT, state.ACTION_TURN_LEFT));
				actionSequenceWest.add(new ActionWrapper(LIUVacuumEnvironment.ACTION_TURN_LEFT, state.ACTION_TURN_LEFT));
				actionSequenceWest.add(new ActionWrapper(LIUVacuumEnvironment.ACTION_MOVE_FORWARD, state.ACTION_MOVE_FORWARD));
    			
    			break;
    		
	    	case MyAgentState.SOUTH:
				actionSequenceNorth.add(new ActionWrapper(LIUVacuumEnvironment.ACTION_TURN_LEFT, state.ACTION_TURN_LEFT));
				actionSequenceNorth.add(new ActionWrapper(LIUVacuumEnvironment.ACTION_TURN_LEFT, state.ACTION_TURN_LEFT));
				actionSequenceNorth.add(new ActionWrapper(LIUVacuumEnvironment.ACTION_MOVE_FORWARD, state.ACTION_MOVE_FORWARD));
				
				actionSequenceEast.add(new ActionWrapper(LIUVacuumEnvironment.ACTION_TURN_LEFT, state.ACTION_TURN_LEFT));
				actionSequenceEast.add(new ActionWrapper(LIUVacuumEnvironment.ACTION_MOVE_FORWARD, state.ACTION_MOVE_FORWARD));

				actionSequenceSouth.add(new ActionWrapper(LIUVacuumEnvironment.ACTION_MOVE_FORWARD, state.ACTION_MOVE_FORWARD));

				actionSequenceWest.add(new ActionWrapper(LIUVacuumEnvironment.ACTION_TURN_RIGHT, state.ACTION_TURN_RIGHT));
				actionSequenceWest.add(new ActionWrapper(LIUVacuumEnvironment.ACTION_MOVE_FORWARD, state.ACTION_MOVE_FORWARD));
    			
    			break;
    		
		    case MyAgentState.WEST:
				actionSequenceNorth.add(new ActionWrapper(LIUVacuumEnvironment.ACTION_TURN_RIGHT, state.ACTION_TURN_RIGHT));
				actionSequenceNorth.add(new ActionWrapper(LIUVacuumEnvironment.ACTION_MOVE_FORWARD, state.ACTION_MOVE_FORWARD));
				
				actionSequenceEast.add(new ActionWrapper(LIUVacuumEnvironment.ACTION_TURN_LEFT, state.ACTION_TURN_LEFT));
				actionSequenceEast.add(new ActionWrapper(LIUVacuumEnvironment.ACTION_TURN_LEFT, state.ACTION_TURN_LEFT));
				actionSequenceEast.add(new ActionWrapper(LIUVacuumEnvironment.ACTION_MOVE_FORWARD, state.ACTION_MOVE_FORWARD));
				
				actionSequenceSouth.add(new ActionWrapper(LIUVacuumEnvironment.ACTION_TURN_LEFT, state.ACTION_TURN_LEFT));
				actionSequenceSouth.add(new ActionWrapper(LIUVacuumEnvironment.ACTION_MOVE_FORWARD, state.ACTION_MOVE_FORWARD));
				
				actionSequenceWest.add(new ActionWrapper(LIUVacuumEnvironment.ACTION_MOVE_FORWARD, state.ACTION_MOVE_FORWARD));
	
				break;
		}
		
		Comparator<Path> comparator = new MoveComparator();
		PriorityQueue<Path> paths = new PriorityQueue<Path>(4, comparator);

		Path north = new Path(new Move(actionSequenceNorth, new Position(x,y), MyAgentState.NORTH));
		Path east = new Path(new Move(actionSequenceEast, new Position(x,y), MyAgentState.EAST));
		Path south = new Path(new Move(actionSequenceSouth, new Position(x,y), MyAgentState.SOUTH));
		Path west = new Path(new Move(actionSequenceWest, new Position(x,y), MyAgentState.WEST));
		
		if(!state.isWall(north.getDestination())) {
    		//System.out.print("\nADDED NORTH\n");
			paths.add(north);
		}
		
		if(!state.isWall(east.getDestination())) {
    		//System.out.print("\nADDED EAST\n");
    		paths.add(east);
		}
		
		if(!state.isWall(south.getDestination())) {
    		//System.out.print("\nADDED SOUTH\n");
    		paths.add(south);
		}
		
		if(!state.isWall(west.getDestination())) {
    		//System.out.print("\nADDED WEST\n");
    		paths.add(west);
		}
		
		return paths;
	}
}

class ActionWrapper {
	Action action;
	int actionType;
	
	ActionWrapper(Action action, int actionType) {
		this.action = action;
		this.actionType = actionType;		
	}
	
	public Action getAction() { return action; }
	public int getActionType() { return actionType; }
}

class Position{
	int x;
	int y;
	
	Position(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	int getX() { return x;}
	int getY() { return y;}
	
	public String toString() {
		return x + "_" + y;
	}
}

class Move {
	Queue<ActionWrapper> actions;
	int direction;
	Position fromPosition;
	
	Move(Queue<ActionWrapper> actions, Position fromPosition, int direction) {
		this.actions = actions;
		this.fromPosition = fromPosition;
		this.direction = direction;
	}
	
	public int getCost() {
		return actions.size();
	}
	
	public int getDirection() {
		return direction;
	}
	
	public Position getPosition() {
		int currentX = fromPosition.getX();
		int currentY = fromPosition.getY();
		
		switch(direction) {
		case MyAgentState.NORTH:
			currentY -= 1;
			break;

		case MyAgentState.EAST:
			currentX += 1;
			break;

		case MyAgentState.SOUTH:
			currentY += 1;
			break;

		case MyAgentState.WEST:
			currentX -= 1;
			break;
		}
		
		return new Position(currentX, currentY);
	}
	
	public Queue<ActionWrapper> getActions() {
		return this.actions;
	}
}

class MoveComparator implements Comparator<Path>{

	@Override
	public int compare(Path arg0, Path arg1) {
		// TODO Auto-generated method stub
		if (arg0.getTotalCost() < arg1.getTotalCost())
        {
            return -1;
        }
        if (arg0.getTotalCost() > arg1.getTotalCost())
        {
            return 1;
        }
        return 0;
	}
}

class MyAgentProgram implements AgentProgram {

	private int initnialRandomActions = 10;
	private Random random_generator = new Random();
	
	// Here you can define your variables!
	public int iterationCounter = 1000;
	public MyAgentState state = new MyAgentState();
	Search search = new Search(state);
	public Queue<ActionWrapper> actionSequence = new LinkedList<ActionWrapper>();
	
	// moves the Agent to a random start position
	// uses percepts to update the Agent position - only the position, other percepts are ignored
	// returns a random action
	private Action moveToRandomStartPosition(DynamicPercept percept) {
		int action = random_generator.nextInt(6);
		initnialRandomActions--;
		state.updatePosition(percept);
		if(action==0) {
		    state.agent_direction = ((state.agent_direction-1) % 4);
		    if (state.agent_direction<0) 
		    	state.agent_direction +=4;
		    state.agent_last_action = state.ACTION_TURN_LEFT;
			return LIUVacuumEnvironment.ACTION_TURN_LEFT;
		} else if (action==1) {
			state.agent_direction = ((state.agent_direction+1) % 4);
		    state.agent_last_action = state.ACTION_TURN_RIGHT;
		    return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
		} 
		state.agent_last_action=state.ACTION_MOVE_FORWARD;
		return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
	}
	
	
	@Override
	public Action execute(Percept percept) {
		
		// DO NOT REMOVE this if condition!!!
    	if (initnialRandomActions>0) {
    		return moveToRandomStartPosition((DynamicPercept) percept);
    	} else if (initnialRandomActions==0) {
    		// process percept for the last step of the initial random actions
    		initnialRandomActions--;
    		state.updatePosition((DynamicPercept) percept);
			System.out.println("Processing percepts after the last execution of moveToRandomStartPosition()");
			state.agent_last_action=state.ACTION_SUCK;
	    	return LIUVacuumEnvironment.ACTION_SUCK;
    	}
		
    	// This example agent program will update the internal agent state while only moving forward.
    	// START HERE - code below should be modified!
    	    	
    	System.out.println("x=" + state.agent_x_position);
    	System.out.println("y=" + state.agent_y_position);
    	System.out.println("dir=" + state.agent_direction);
    	
		
	    iterationCounter--;
	    
	    if (iterationCounter==0)
	    	return NoOpAction.NO_OP;

	    DynamicPercept p = (DynamicPercept) percept;
	    Boolean bump = (Boolean)p.getAttribute("bump");
	    Boolean dirt = (Boolean)p.getAttribute("dirt");
	    Boolean home = (Boolean)p.getAttribute("home");
	    System.out.println("percept: " + p);
	    
	    // State update based on the percept value and the last action
	    state.updatePosition((DynamicPercept)percept);
	    updateMap(dirt, home, bump);

	    // Next action selection based on the percept value
	    if (dirt)
	    {
	    	System.out.println("DIRT -> choosing SUCK action!");
	    	state.agent_last_action=state.ACTION_SUCK;
	    	return LIUVacuumEnvironment.ACTION_SUCK;
	    } 
	    else
	    {
	    	//Find all unknown squares
	    	if(actionSequence.isEmpty()) {
	    		actionSequence = search.find(state.UNKNOWN);
	    	}
	    	
	    	//Find home
	    	if(actionSequence.isEmpty()) {
	    		if(home) {
	    	    	return NoOpAction.NO_OP;
	    		}
	    		else {
	    		actionSequence = search.find(state.HOME);
	    		}
	    	}
	    	
    		return doAction();
	    }
	}
	
	private void updateMap(Boolean dirt, Boolean home, Boolean bump) {
		if (bump) {
			switch (state.agent_direction) {
			case MyAgentState.NORTH:
				state.updateWorld(state.agent_x_position,state.agent_y_position-1,state.WALL);
				break;
			case MyAgentState.EAST:
				state.updateWorld(state.agent_x_position+1,state.agent_y_position,state.WALL);
				break;
			case MyAgentState.SOUTH:
				state.updateWorld(state.agent_x_position,state.agent_y_position+1,state.WALL);
				break;
			case MyAgentState.WEST:
				state.updateWorld(state.agent_x_position-1,state.agent_y_position,state.WALL);
				break;
			}
	    }
	    if (dirt)
	    	state.updateWorld(state.agent_x_position,state.agent_y_position,state.DIRT);
	    else if(home)
	    	state.updateWorld(state.agent_x_position,state.agent_y_position,state.HOME);
	    else
	    	state.updateWorld(state.agent_x_position,state.agent_y_position,state.CLEAR);
	    	
	    
	    state.printWorldDebug();
	}


	private Action doAction() {
		ActionWrapper action = actionSequence.poll();
		
		switch(action.getActionType()) {
		//Right
		case 2:
			state.agent_direction = ((state.agent_direction+1) % 4);
			break;
		//LEFT
		case 3:
			state.agent_direction = ((state.agent_direction-1) % 4);
		    if (state.agent_direction<0) 
		    	state.agent_direction +=4;
			break;
		}

		state.agent_last_action = action.getActionType();
		return action.getAction();
	}
}

public class MyVacuumAgent extends AbstractAgent {
    public MyVacuumAgent() {
    	super(new MyAgentProgram());
	}
}
package transit;

import java.util.ArrayList;

import javax.xml.stream.Location;

/**
 * This class contains methods which perform various operations on a layered linked
 * list to simulate transit
 */
public class Transit {
	private TNode trainZero; // a reference to the zero node in the train layer

	/* 
	 * Default constructor used by the driver and Autolab. 
	 * DO NOT use in your code.
	 * DO NOT remove from this file
	 */ 
	public Transit() { trainZero = null; }

	/* 
	 * Default constructor used by the driver and Autolab. 
	 * DO NOT use in your code.
	 * DO NOT remove from this file
	 */
	public Transit(TNode tz) { trainZero = tz; }
	
	/*
	 * Getter method for trainZero
	 *
	 * DO NOT remove from this file.
	 */
	public TNode getTrainZero () {
		return trainZero;
	}

	/**
	 * Makes a layered linked list representing the given arrays of train stations, bus
	 * stops, and walking locations. Each layer begins with a location of 0, even though
	 * the arrays don't contain the value 0. Store the zero node in the train layer in
	 * the instance variable trainZero.
	 * 
	 * @param trainStations Int array listing all the train stations
	 * @param busStops Int array listing all the bus stops
	 * @param locations Int array listing all the walking locations (always increments by 1)
	 */
	public void makeList(int[] trainStations, int[] busStops, int[] locations) {
		
		TNode walkHead = new TNode();
		TNode walkZero = walkHead;
		
		for (int i = 0; i < locations.length; i++){
			TNode currentWalk = new TNode(locations[i]);
			walkHead.setNext(currentWalk);
			walkHead = currentWalk;
		}

		TNode busHead = new TNode();
		TNode busZero = busHead;
		
		for (int i = 0; i < busStops.length; i++){
			TNode currentBus = new TNode(busStops[i]);
			busHead.setNext(currentBus);
			busHead = currentBus;

			TNode walkIterate = walkZero.getNext();
			for (int a = 0; a < locations.length; a++){
				if (busStops[i] == walkIterate.getLocation()){
					currentBus.setDown(walkIterate);
				}
				walkIterate = walkIterate.getNext();
			}
		}

		TNode trainHead = new TNode();
		trainZero = trainHead;
		
		for (int i = 0; i < trainStations.length; i++){
			TNode currentTrain = new TNode(trainStations[i]);
			trainHead.setNext(currentTrain);
			trainHead = currentTrain;
			
			TNode busIterate = busZero.getNext();
			for (int a = 0; a < busStops.length; a++){
				if (trainStations[i] == busIterate.getLocation()){
					currentTrain.setDown(busIterate);
				}
				busIterate = busIterate.getNext();
			}
		}
        
	    trainZero.setDown(busZero);
		busZero.setDown(walkZero);



	}
	
	
	
	/**
	 * Modifies the layered list to remove the given train station but NOT its associated
	 * bus stop or walking location. Do nothing if the train station doesn't exist
	 * 
	 * @param station The location of the train station to remove
	 */
	public void removeTrainStation(int station) {
	   
		TNode currentNode = trainZero;
		TNode previousNode = null;

		while (currentNode != null && currentNode.getLocation() != station){
			previousNode = currentNode;
			currentNode = currentNode.getNext();
		}

		if (currentNode == null){
			return;
		}
		else{
		previousNode.setNext(currentNode.getNext());
		}


	}

	/**
	 * Modifies the layered list to add a new bus stop at the specified location. Do nothing
	 * if there is no corresponding walking location.
	 * 
	 * @param busStop The location of the bus stop to add
	 */
	public void addBusStop(int busStop) {
		
		TNode walker = trainZero.getDown().getDown();
		while (walker.getNext() != null){
			walker = walker.getNext();
		}

		int max = walker.getLocation();

		if (busStop > max){
			return;
		}

		TNode currentNode = trainZero.getDown();
		TNode previousNode = null;

		while (currentNode != null && currentNode.getLocation() < busStop){
			if (currentNode.getLocation() == busStop){
				return;
			}
			previousNode = currentNode;
			currentNode = currentNode.getNext();
		}

		TNode newBusStop = new TNode (busStop);;
		
		if (previousNode != null && previousNode.getLocation() == busStop){
			return;
		}
		
		if (currentNode != null && currentNode.getLocation() == busStop){
			return;
		}

		if (currentNode == null){
			if (previousNode.getLocation() == busStop){
				return;
			}
			
			previousNode.setNext(newBusStop);
		}
		
		
		
		
		TNode walkIterate = trainZero.getDown().getDown();

		while (walkIterate != null){
			if (walkIterate.getLocation() == busStop){
				newBusStop.setDown(walkIterate);
				previousNode.setNext(newBusStop);
				if (currentNode != null){
				newBusStop.setNext(currentNode);
				}
				return;
			}
			walkIterate = walkIterate.getNext();
		}

		if (walkIterate == null){
			return;
		}

		


	}
	
	/**
	 * Determines the optimal path to get to a given destination in the walking layer, and 
	 * collects all the nodes which are visited in this path into an arraylist. 
	 * 
	 * @param destination An int representing the destination
	 * @return
	 */
	public ArrayList<TNode> bestPath(int destination) {

		ArrayList <TNode> bestPath = new ArrayList <TNode>();
		TNode trainWalker = trainZero;
		TNode previousTrain = trainWalker;
		
		
		//Finding closest train station
		while (trainWalker != null && trainWalker.getLocation() < destination){
			bestPath.add(trainWalker);
			previousTrain = trainWalker;
			trainWalker = trainWalker.getNext();
		}

		if (trainWalker != null && trainWalker.getLocation() != destination){

			trainWalker = previousTrain;
		}
		
		
		
		if (trainWalker != null && trainWalker.getLocation() == destination){
			bestPath.add(trainWalker);
			bestPath.add(trainWalker.getDown());
			bestPath.add(trainWalker.getDown().getDown());
			return bestPath;
		}

		if (trainWalker == null){
			trainWalker = previousTrain;
		}
		
		TNode busWalker = trainWalker.getDown();
		TNode previousBus = null;

		while(busWalker != null && busWalker.getLocation() < destination){
			bestPath.add(busWalker);
			previousBus = busWalker;
			busWalker = busWalker.getNext();
		}

		if (busWalker != null && busWalker.getLocation() != destination){
			busWalker = previousBus;
		}

		if (busWalker == null){
			busWalker = previousBus;
		}
		

		//bestPath.add(busWalker);

		if (busWalker.getLocation() == destination){
			bestPath.add(busWalker);
			bestPath.add(busWalker.getDown());
			return bestPath;
		}

		TNode walker = busWalker.getDown();
		
		while (walker.getLocation() != destination){
			bestPath.add(walker);
			walker = walker.getNext();
		}
		bestPath.add(walker);

		return bestPath;

		
	}

	/**
	 * Returns a deep copy of the given layered list, which contains exactly the same
	 * locations and connections, but every node is a NEW node.
	 * 
	 * @return A reference to the train zero node of a deep copy
	 */
	public TNode duplicate() {

		TNode trainWalker = trainZero.getNext();

		TNode busWalker = trainZero.getDown().getNext();

		TNode walker = trainZero.getDown().getDown().getNext();

		TNode train0 = new TNode();

		TNode tr0 = train0;

		TNode bus0 = new TNode();

		TNode b0 = bus0;

		TNode walk0 = new TNode();

		TNode w0 = walk0;

		train0.setDown(bus0);

		bus0.setDown(walk0);

		while (trainWalker != null){
			TNode nextTrain = new TNode(trainWalker.getLocation());
			train0.setNext(nextTrain);
			train0 = nextTrain;
			trainWalker = trainWalker.getNext();
		}

		while (busWalker != null){
			TNode nextBus = new TNode(busWalker.getLocation());
			bus0.setNext(nextBus);
			bus0 = nextBus;
			busWalker = busWalker.getNext();
		}

		while (walker != null){
			TNode nextWalk = new TNode(walker.getLocation());
			walk0.setNext(nextWalk);
			walk0 = nextWalk;
			walker = walker.getNext();
		}

		
		
		train0 = tr0.getNext();
		bus0 = b0.getNext();

		while (train0 != null){
			bus0 = b0.getNext();
			while(bus0 != null){
				if (train0.getLocation() == bus0.getLocation()){
					train0.setDown(bus0);
				}
				bus0 = bus0.getNext();
			}
			train0 = train0.getNext();
		}

		bus0 = b0.getNext();
		walk0 = w0.getNext();

		while (bus0 != null){
			walk0 = w0.getNext();
			while(walk0 != null){
				if (bus0.getLocation() == walk0.getLocation()){
					bus0.setDown(walk0);
				}
				walk0 = walk0.getNext();
			}
			bus0 = bus0.getNext();
		}

		return tr0;

	}

	/**
	 * Modifies the given layered list to add a scooter layer in between the bus and
	 * walking layer.
	 * 
	 * @param scooterStops An int array representing where the scooter stops are located
	 */
	public void addScooter(int[] scooterStops) {

	TNode scooterZero = new TNode();

	TNode scooterHead = scooterZero;

	scooterZero.setDown(trainZero.getDown().getDown());

	trainZero.getDown().setDown(scooterZero);

	for (int i = 0; i<scooterStops.length; i++){
		TNode next = new TNode(scooterStops[i]);
		scooterZero.setNext(next);
		scooterZero = next;
	}

	TNode walker = null;

	scooterHead = scooterHead.getNext();

	while (scooterHead != null){
		walker = trainZero.getDown().getDown().getDown().getNext();
		while(walker != null){
			if(scooterHead.getLocation() == walker.getLocation()){
				scooterHead.setDown(walker);
				break;
			}
			walker = walker.getNext();
		}
		scooterHead = scooterHead.getNext();
	}

	TNode busWalker = trainZero.getDown().getNext();

	TNode scooter = null;

	while (busWalker != null){
		scooter = trainZero.getDown().getDown().getNext();
		while (scooter != null){
			if (scooter.getLocation() == busWalker.getLocation()){
				busWalker.setDown(scooter);
				break;
			}
			scooter = scooter.getNext();
		}
		busWalker = busWalker.getNext();
	}



	}

	/**
	 * Used by the driver to display the layered linked list. 
	 * DO NOT edit.
	 */
	public void printList() {
		// Traverse the starts of the layers, then the layers within
		for (TNode vertPtr = trainZero; vertPtr != null; vertPtr = vertPtr.getDown()) {
			for (TNode horizPtr = vertPtr; horizPtr != null; horizPtr = horizPtr.getNext()) {
				// Output the location, then prepare for the arrow to the next
				StdOut.print(horizPtr.getLocation());
				if (horizPtr.getNext() == null) break;
				
				// Spacing is determined by the numbers in the walking layer
				for (int i = horizPtr.getLocation()+1; i < horizPtr.getNext().getLocation(); i++) {
					StdOut.print("--");
					int numLen = String.valueOf(i).length();
					for (int j = 0; j < numLen; j++) StdOut.print("-");
				}
				StdOut.print("->");
			}

			// Prepare for vertical lines
			if (vertPtr.getDown() == null) break;
			StdOut.println();
			
			TNode downPtr = vertPtr.getDown();
			// Reset horizPtr, and output a | under each number
			for (TNode horizPtr = vertPtr; horizPtr != null; horizPtr = horizPtr.getNext()) {
				while (downPtr.getLocation() < horizPtr.getLocation()) downPtr = downPtr.getNext();
				if (downPtr.getLocation() == horizPtr.getLocation() && horizPtr.getDown() == downPtr) StdOut.print("|");
				else StdOut.print(" ");
				int numLen = String.valueOf(horizPtr.getLocation()).length();
				for (int j = 0; j < numLen-1; j++) StdOut.print(" ");
				
				if (horizPtr.getNext() == null) break;
				
				for (int i = horizPtr.getLocation()+1; i <= horizPtr.getNext().getLocation(); i++) {
					StdOut.print("  ");

					if (i != horizPtr.getNext().getLocation()) {
						numLen = String.valueOf(i).length();
						for (int j = 0; j < numLen; j++) StdOut.print(" ");
					}
				}
			}
			StdOut.println();
		}
		StdOut.println();
	}
	
	/**
	 * Used by the driver to display best path. 
	 * DO NOT edit.
	 */
	public void printBestPath(int destination) {
		ArrayList<TNode> path = bestPath(destination);
		for (TNode vertPtr = trainZero; vertPtr != null; vertPtr = vertPtr.getDown()) {
			for (TNode horizPtr = vertPtr; horizPtr != null; horizPtr = horizPtr.getNext()) {
				// ONLY print the number if this node is in the path, otherwise spaces
				if (path.contains(horizPtr)) StdOut.print(horizPtr.getLocation());
				else {
					int numLen = String.valueOf(horizPtr.getLocation()).length();
					for (int i = 0; i < numLen; i++) StdOut.print(" ");
				}
				if (horizPtr.getNext() == null) break;
				
				// ONLY print the edge if both ends are in the path, otherwise spaces
				String separator = (path.contains(horizPtr) && path.contains(horizPtr.getNext())) ? ">" : " ";
				for (int i = horizPtr.getLocation()+1; i < horizPtr.getNext().getLocation(); i++) {
					StdOut.print(separator + separator);
					
					int numLen = String.valueOf(i).length();
					for (int j = 0; j < numLen; j++) StdOut.print(separator);
				}

				StdOut.print(separator + separator);
			}
			
			if (vertPtr.getDown() == null) break;
			StdOut.println();

			for (TNode horizPtr = vertPtr; horizPtr != null; horizPtr = horizPtr.getNext()) {
				// ONLY print the vertical edge if both ends are in the path, otherwise space
				StdOut.print((path.contains(horizPtr) && path.contains(horizPtr.getDown())) ? "V" : " ");
				int numLen = String.valueOf(horizPtr.getLocation()).length();
				for (int j = 0; j < numLen-1; j++) StdOut.print(" ");
				
				if (horizPtr.getNext() == null) break;
				
				for (int i = horizPtr.getLocation()+1; i <= horizPtr.getNext().getLocation(); i++) {
					StdOut.print("  ");

					if (i != horizPtr.getNext().getLocation()) {
						numLen = String.valueOf(i).length();
						for (int j = 0; j < numLen; j++) StdOut.print(" ");
					}
				}
			}
			StdOut.println();
		}
		StdOut.println();
	}
}

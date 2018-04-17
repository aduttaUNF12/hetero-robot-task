import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;
import lpsolve.*;

public class hetero_task_alloc{


	static ArrayList< ArrayList<Integer>> costPartitions;// used to hold the initial partitions calculated using LPSolve
	//Vector< Vector<Integer>> finalPartitions;// used to hold the final clusters calculated using LPSolve
	static ArrayList< ArrayList<Integer>> intPartitions;// used to hold the integer partitions of number of agents
	static int Coordinates[][];
	static int[][] currentAssignmentStat;// = new int[tasks + agents][2];
	static double optimalValue=0;
	private static double initialCost=0;
	private static double finalCost=0;
	private static double initialVal=0;
	private static double finalValue=0;
	private static int width;

	/*integer partitioning code goes here*/
	static void storepartitions(int[] p, int n, int t)
	{
		//for (int i = 0; i < p.length; i++)
		//System.out.println(p[i]);


		if (n == t) {
			ArrayList<Integer> onePart = new ArrayList<Integer>();// (p);//, p + n);
			for (int i = 0; i < t; i++){
				//System.out.println(p[i]);
				onePart.add(p[i]);
			}
			//onePart =  onePart.subList(0, t-1);
			//System.out.println(onePart.get(0));
			intPartitions.add(onePart);
			//onePart.clear();
		}
	}

	static void FindAllUniqueParts(int a, int t)
	{
		final int n = a;
		int[] p = new int[n]; // An array to store a partition
		int k = 0;  // Index of last element in a partition
		p[k] = n;  // Initialize first partition as number itself

		// This loop first prints current partition, then generates next
		// partition. The loop stops when the current partition has all 1s
		while (true)
		{
			// store the current partition
			if (t == k + 1) { storepartitions(p, k + 1, t); }

			// Generate next partition

			// Find the rightmost non-one value in p[]. Also, update the
			// rem_val so that we know how much value can be accommodated
			int rem_val = 0;
			while (k >= 0 && p[k] == 1)
			{
				rem_val += p[k];
				k--;
			}

			// if k < 0, all the values are 1 so there are no more partitions
			if (k < 0)  return;

			// Decrease the p[k] found above and adjust the rem_val
			p[k]--;
			rem_val++;


			// If rem_val is more, then the sorted order is violated.  Divide
			// rem_val in different values of size p[k] and copy these values at
			// different positions after p[k]
			while (rem_val > p[k])
			{
				p[k + 1] = p[k];
				rem_val = rem_val - p[k];
				k++;
			}

			// Copy rem_val to next position and increment position
			p[k + 1] = rem_val;
			k++;
		}
	}

	static void displayAssignment(int[][] assign, int k) {
		for (int p = 0; p < (assign.length); p++) {
			System.out.println("Robot ID " + assign[p][0]+" is assigned to Task ID " + assign[p][1]);
		}
	}

	static void displayCurrentPartition() {
		for (int p = 0; p < costPartitions.size(); p++) {
			System.out.println("Cluster number" + (p + 1));
			for (int a = 0; a < costPartitions.get(p).size(); a++) {
				System.out.println(" Agent ID " + costPartitions.get(p).get(a) + ".");
			}
		}
	}


	//extract LP output partition from the text/csv file
	@SuppressWarnings("unchecked")
	static void extractLPPartitions(int agent, int task, String Filename) {
		int i, j, n, count;
		n = agent + task;
		double[][] AgentRelationship = new double[n][n];//(n, vector<int>(n));
		double[] AgentPartition = new double[n];

		for (i = 0; i < n; i++)
		{
			AgentPartition[i] = i;
		}

		try
		{
			FileInputStream fs= new FileInputStream(Filename);
			BufferedReader read = new BufferedReader(new InputStreamReader(fs));
			for (int lineno = 0; lineno < 2; lineno++)
			{
				//read.hasNext();
				read.readLine();
				// do nothing here -- ignore first 2 lines
			}
			
			for (i = 0; i < n; i++)
			{
				for (j = i + 1; j < n; j++)
				{
					String line = read.readLine();
					line = line.substring(line.length()-1);
					//System.out.println("a01 is --> "+line);
					AgentRelationship[i][j] = Double.parseDouble(line);//(a01);
					if (AgentRelationship[i][j] == 0)
					{
						AgentPartition[j] = AgentPartition[i];
					}
					/*
					else if (AgentRelationship[i][j] > 0 && AgentRelationship[i][j] < 1)
					{
						AgentPartition[j] = 0.5;
					}
					*/
				}

			}
			read.close();
		}
		catch (Exception e)
		{
			System.err.format("Exception occurred trying to read '%s'.", Filename);
			e.printStackTrace();
			return;
		}

		count = 0;
		
		ArrayList<Integer> cluster = new ArrayList<Integer>();
		//ArrayList<Integer> notAssigned_cluster = new ArrayList<Integer>();
		
		for (i = 0; i < n; i++)
		{
			for (j = 0; j < n; j++)
			{
				if (AgentPartition[j] == i)
				{
					cluster.add(j);
					count = 1;
				}
				//if(AgentRelationship[i][j] > 0 && AgentRelationship[i][j] < 1 && AgentPartition[j]!= i)
					//notAssigned_cluster.add(j);
				
				if (j == n - 1 && count == 1) {
					//System.out.println(cluster.clone());
					costPartitions.add((ArrayList<Integer>) cluster.clone());
					cluster.clear();
				}
			}
			count = 0;
		}
		//costPartitions.add((ArrayList<Integer>) notAssigned_cluster.clone());
		//notAssigned_cluster.clear();
		/*
		int k;
		for(k=costPartitions.size()-1; k>=0; k--) {
			if(costPartitions.get(k).get(0)>=task) {
				notAssigned_cluster.addAll(costPartitions.get(k));
			}			
			else
				break;
		}
		k++;
		while(k<costPartitions.size()) {
			costPartitions.remove(k);
		}
		*/
		/*
		for (i = 0; i < n; i++)
		{
			if (AgentPartition[i] == 0.5)
			{
				notAssigned_cluster
			}
			
		}
		*/

		//displayCurrentPartition();
		return;// do ctrl+F5 to see your result.

	}
	
	//extract LP output partition from the text/csv file
		@SuppressWarnings("unchecked")
		static void extractLPPartitions_bipart(int agent, int task, String Filename) {
			int i, j, n, count;
			n = agent + task;
			double[][] AgentRelationship = new double[n][n];//(n, vector<int>(n));
			double[] AgentPartition = new double[n];

			for (i = 0; i < n; i++)
			{
				AgentPartition[i] = i;
			}

			try
			{
				FileInputStream fs= new FileInputStream(Filename);
				BufferedReader read = new BufferedReader(new InputStreamReader(fs));
				for (int lineno = 0; lineno < 2; lineno++)
				{
					//read.hasNext();
					read.readLine();
					// do nothing here -- ignore first 2 lines
				}
				
				for (i = 0; i < task; i++)
				{
					for (j = task; j < n; j++)
					{
						String line = read.readLine();
						line = line.substring(line.length()-1);
						//System.out.println("a01 is --> "+line);
						AgentRelationship[i][j] = Double.parseDouble(line);//(a01);
						if (AgentRelationship[i][j] == 0)
						{
							AgentPartition[j] = AgentPartition[i];
						}
						/*
						else if (AgentRelationship[i][j] > 0 && AgentRelationship[i][j] < 1)
						{
							AgentPartition[j] = 0.5;
						}
						*/
					}

				}
				read.close();
			}
			catch (Exception e)
			{
				System.err.format("Exception occurred trying to read '%s'.", Filename);
				e.printStackTrace();
				return;
			}

			count = 0;
			
			ArrayList<Integer> cluster = new ArrayList<Integer>();
			//ArrayList<Integer> notAssigned_cluster = new ArrayList<Integer>();
			
			for (i = 0; i < n; i++)
			{
				for (j = 0; j < n; j++)
				{
					if (AgentPartition[j] == i)
					{
						cluster.add(j);
						count = 1;
					}
					//if(AgentRelationship[i][j] > 0 && AgentRelationship[i][j] < 1 && AgentPartition[j]!= i)
						//notAssigned_cluster.add(j);
					
					if (j == n - 1 && count == 1) {
						//System.out.println(cluster.clone());
						costPartitions.add((ArrayList<Integer>) cluster.clone());
						cluster.clear();
					}
				}
				count = 0;
			}
			//costPartitions.add((ArrayList<Integer>) notAssigned_cluster.clone());
			//notAssigned_cluster.clear();
			/*
			int k;
			for(k=costPartitions.size()-1; k>=0; k--) {
				if(costPartitions.get(k).get(0)>=task) {
					notAssigned_cluster.addAll(costPartitions.get(k));
				}			
				else
					break;
			}
			k++;
			while(k<costPartitions.size()) {
				costPartitions.remove(k);
			}
			*/
			/*
			for (i = 0; i < n; i++)
			{
				if (AgentPartition[i] == 0.5)
				{
					notAssigned_cluster
				}
				
			}
			*/

			//displayCurrentPartition();
			return;// do ctrl+F5 to see your result.

		}

	//calculate the OPTIMAL value for the current distribution of O_i based on the integer partitioning of #agents
	static double calculateOptimalValue(ArrayList<Integer> optimalMemberSize) {
		double val = 0;
		for (int i = 0; i < optimalMemberSize.size(); i++)
		{
			val = val + Math.pow(optimalMemberSize.get(i), 2);
		}
		return val;
	}

	//calculate the current value of a CS based on its member coalition' sizes
	double calculateCurrentValue(ArrayList< ArrayList<Integer>> currentPartition) {
		double val = 0;
		for (int i = 0; i < currentPartition.size(); i++)
		{
			val = val + Math.pow(currentPartition.get(i).size(),2);
		}
		return val;
	}


	// clear all allocated memories
	void clearAll() {
		costPartitions.clear();
		intPartitions.clear();
		//finalPartitions.clear();
	}

	//calculate manhattan distance
	static float calculateDistance(float x1, float y1, float x2, float y2) {
		float dist = Math.abs(x1 - x2) + Math.abs(y1 - y2);
		return dist;
	}

	// Compares two agents' coordinates according to distances from current task.
	boolean compareDistance(AgentCoordinates a1, AgentCoordinates a2)
	{
		//a1[3] = calculateDistance(a1[1], a1[2], t[1], t[2]);
		//a2[3] = calculateDistance(a2[1], a2[2], t[1], t[2]);
		return (a1.dist > a2.dist);
	}

	static void calculateAllDistances(AgentCoordinates[] AgentCoordinatesSorted, int currentTask) {
		for (int i = 0; i < AgentCoordinatesSorted.length; i++)
		{
			AgentCoordinatesSorted[i].dist = calculateDistance(AgentCoordinatesSorted[i].x, AgentCoordinatesSorted[i].y, AgentCoordinatesSorted[currentTask].x, AgentCoordinatesSorted[currentTask].y);

		}
	}

	static boolean unassignExtraRobots(int extraSize, int taskID, AgentCoordinates AgentCoordinatesSorted[], int[][] currentCoalSizes) {
		//System.out.println("In unassignExtraRobots function");
		calculateAllDistances(AgentCoordinatesSorted, taskID);
		Arrays.sort(AgentCoordinatesSorted, new sortByDist());
		int unassignCount = 0;
		for (int i = AgentCoordinatesSorted.length - 1; i >= 0; i--)
		{
			if (currentAssignmentStat[AgentCoordinatesSorted[i].id][1] == taskID && unassignCount < extraSize) {
				currentAssignmentStat[AgentCoordinatesSorted[i].id][1] = -1;
				currentCoalSizes[taskID][1] = currentCoalSizes[taskID][1] - 1;
				unassignCount++;
				if (unassignCount == extraSize) return true;
			}
		}

		return false;
	}

	//add robots to a particular task's already assigned coalition to reach the max value
	static boolean addRobotsToCoal(int shortage, int taskID, AgentCoordinates AgentCoordinatesSorted[], int[][] currentCoalSizes) {

		calculateAllDistances(AgentCoordinatesSorted, taskID);
		Arrays.sort(AgentCoordinatesSorted, new sortByDist());
		int newAssign = 0;
		for (int i = 0; i < AgentCoordinatesSorted.length; i++)
		{	
			//System.out.println("i =: "+i);
			int id = AgentCoordinatesSorted[i].id;
			if (currentAssignmentStat[id][1] == -1 && newAssign < shortage) {
				currentAssignmentStat[id][1] = taskID;
				currentCoalSizes[taskID][1] = currentCoalSizes[taskID][1] + 1;
				newAssign++;
				//System.out.println("In addRobotsToCoal function with size: "+AgentCoordinatesSorted.length);
				if (newAssign == shortage) return true;
			}
		}
		return false;
	}

	static AgentCoordinates[] readCoordinates(int agents, int tasks) {
		int n = agents + tasks;
		AgentCoordinates[] arr = new AgentCoordinates[n];

		for (int k = 0; k < n; k++)
		{
			//AgentCoordinates thisAgent;
			arr[k] = new AgentCoordinates();
			arr[k].id = k;
			arr[k].x = Coordinates[k][0];
			arr[k].y = Coordinates[k][1];
			arr[k].dist = 0;//used as a distance keeper
			//arr[k] = thisAgent;
			//cout + "Value of K is: " + k + " and ID is: " + arr[k].id + endl;
		}
		//int size = sizeof(arr) / sizeof(arr[0]);
		//cout + "Size of ARR is: " + size + endl;
		return arr;
	}
	//execute the region growing algorithm on the costPartitions to find the finalPartitions -- this is used to incorporate our value function
	static void regionGrowing(int agents, int tasks, ArrayList<Integer> optMem, int[][] currentCoalSizes, task[] sortedT) {
		AgentCoordinates[] agentCoordArr = readCoordinates(agents, tasks);  //new AgentCoordinates[agents + tasks];
		//int[][] currentAssignmentStat = new int[tasks + agents][2];// holds two columns, first column holds agent ids and the second column holds the task id to which the agent is allocated to,
		// -1 will indicate that the agent is unassigned.
		for (int i = 0; i < tasks + agents; i++) {
			currentAssignmentStat[i][0] = i;
			if (i < tasks) {
				currentAssignmentStat[i][1] = -2;
			}
			else {
				currentAssignmentStat[i][1] = -1;
			}
		}

		for (int i = 0; i < costPartitions.size(); i++) {
			for (int j = 1; j < costPartitions.get(i).size(); j++) {
				//currentAssignmentStat[costPartitions[i][j]][0] = costPartitions[i][j];
				if(costPartitions.get(i).get(0) < tasks)
					currentAssignmentStat[costPartitions.get(i).get(j)][1] = costPartitions.get(i).get(0);
				//System.out.println("Agent ID " + currentAssignmentStat[costPartitions.get(i).get(j)][0] + " and assigned to task " + currentAssignmentStat[costPartitions.get(i).get(j)][1]);
			}
		}
		initialCost = calculateCost(tasks + agents);
		initialVal = calculateValue(sortedT);
		int assignedAgentToCurrTask = 0;
		for (int i = 0; i < sortedT.length; i++) {
			assignedAgentToCurrTask = sortedT[i].coalSize;
			//int rad = 1;// for encompassing all the agents which are 1-hop away from the task.
			if (assignedAgentToCurrTask < sortedT[i].optimal) {//encompass unassigned robots
				int shortage = sortedT[i].optimal - assignedAgentToCurrTask;
				if(addRobotsToCoal(shortage, sortedT[i].taskID, agentCoordArr, currentCoalSizes))
					sortedT[i].coalSize = sortedT[i].optimal;
				//rad++;//no use
			}
			if (assignedAgentToCurrTask == sortedT[i].optimal) {// you are done with this task
				continue;
			}
			if (assignedAgentToCurrTask > sortedT[i].optimal) {//detach farthest assigned robots so that assignedAgentToCurrTask == optMem[i]
				int extra = assignedAgentToCurrTask - sortedT[i].optimal;
				if(unassignExtraRobots(extra, sortedT[i].taskID, agentCoordArr, currentCoalSizes))
					sortedT[i].coalSize = sortedT[i].optimal;
			}
			//delete[] agentCoordSorted;
		}
		//displayAssignment(currentAssignmentStat, 1);
		return;
	}



	static double calculateEdgeCost(double dist) {
		double w;
		double fval = 1 - (double)(dist / (Math.sqrt(2)*((double)(width))));
		w = Math.log(fval / (double)(1 - fval));
		//System.out.println(dist + " and " + fval + " and " + w);
		return w;
	}

	static void agentCoordinateGeneration(int robots, int tasks) {

		//Generation of random points (agent positions) for varying numbers of agents (grid size depends on number of agents):
		int n, i, j, check;
		n = robots + tasks;
		Random rand = new Random();

		for (i = 0; i < n; i++)
		{
			check = 0;
			Coordinates[i][0]=rand.nextInt(width);
			Coordinates[i][1]=rand.nextInt(width);

			while (check == 0 && i>0)
			{
				check = 1;
				for (j = 0; j<i; j++)
				{
					if (Coordinates[i][0] == Coordinates[j][0] && Coordinates[i][1] == Coordinates[j][1])
					{
						Coordinates[i][0]=rand.nextInt(width);
						Coordinates[i][1]=rand.nextInt(width);
						check = 0;
					}
				}
			}
			//System.out.println("coordinates are  + " + Coordinates[i][0] + " and " + Coordinates[i][1]);
		}
		return;
	}

	static String LPSetUp(int agents, int tasks, int run) {
		int k, i, j;
		double NegativeWeightSum = 0;
		int n = agents + tasks;
		//int width = n;


		//clock_t start, end;
		//float time = 0.0;
		//start = clock();

		//vector< vector<float> > AgentCoordinates(n, vector<float>(2));
		double AgentDistances[][] = new double[n][n];//(n, vector<float>(n));
		double EdgeUtility[][] = new double[n][n];//(n, vector<float>(n));


		String Filename = "LPinput_Tasks" + Integer.toString(tasks) + "Agents" + Integer.toString(agents) + "Run" + Integer.toString(run) + ".lp";

		PrintWriter writer = null;
		try {
			writer = new PrintWriter(Filename, "UTF-8");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		String LPModelName = "LPresults_Tasks" + Integer.toString(tasks) + "Agents" + Integer.toString(agents) + "Run" + Integer.toString(run);


		writer.println("/* Objective function */");
		writer.println("min: ");

		for (i = 0; i < n; i++)
		{
			for (j = i + 1; j < n; j++)
			{
				//AgentDistances[i][j]=Math.sqrt(Math.pow((Coordinates[i][0] - Coordinates[j][0]), 2) + Math.pow((Coordinates[i][1] - Coordinates[j][1]), 2));
				AgentDistances[i][j]=Math.sqrt(Math.pow((Coordinates[i][0] - Coordinates[j][0]), 2) + Math.pow((Coordinates[i][1] - Coordinates[j][1]), 2));
				double dist = AgentDistances[i][j];

				if (i < tasks && j < tasks) {
					//AgentDistances[i][j] = 0;
					EdgeUtility[i][j] = -n * tasks * dist;
				}
				else {
					//AgentDistances[i][j] = 0;
					EdgeUtility[i][j] = calculateEdgeCost(dist);
				}


				if (EdgeUtility[i][j] >= 0)
				{
					if (i == 0 && j == 1) { writer.println(EdgeUtility[i][j] + " x" + (i + 1) + (j + 1));}//myfile + EdgeUtility[i][j] + " x" + i + 1 + j + 1; }
					else { writer.println(" + " + EdgeUtility[i][j] + " x" + (i + 1) + (j + 1));}//myfile + " + " + EdgeUtility[i][j] + " x" + i + 1 + j + 1; }
				}
				else
				{
					//myfile + " - " + fabs(EdgeUtility[i][j]) + " x" + i + 1 + j + 1;
					writer.println(" - " + Math.abs(EdgeUtility[i][j]) + " x" + (i + 1) + (j + 1));
					NegativeWeightSum = NegativeWeightSum + Math.abs(EdgeUtility[i][j]);
				}
			}
		}
		writer.println(" + " + NegativeWeightSum + ";" );
		writer.println("");
		//myfile + " + " + NegativeWeightSum + ";" + endl + endl;

		//Agent edges, their utilities, and the objective function have now been stored. Constraints are now set.
		writer.println("/* Variable Bounds */");
		//myfile + "/* Variable Bounds */" + endl;

		for (i = 0; i < n; i++)
		{
			for (j = i + 1; j < n; j++)
			{
				//if ((i >= tasks && j < tasks) || (i < tasks && j >= tasks)) {
				writer.println("0 <= " + "x" + (i + 1) + (j + 1) + " <= 1;");// + endl;
				//}
				//else
				//{
				//myfile + "0 <= " + "x" + i + 1 + j + 1 + " <= 1;" + endl;
				//}
			}
		}

		for (i = 0; i < n; i++)
		{
			for (j = i + 1; j < n; j++)
			{
				for (k = j + 1; k < n; k++)
				{
					writer.println("x" + (i + 1) + (j + 1) + " + x" + (i + 1) + (k + 1) + " - x" + (j + 1) + (k + 1) + " >= 0;");// + endl;
				}
				for (k = i + 1; k < j; k++)
				{
					writer.println("x" + (i + 1) + (j + 1) + " + x" + (k + 1) + (j + 1) + " - x" + (i + 1) + (k + 1) + " >= 0;");// + endl;
				}
				for (k = j + 1; k < n; k++)
				{
					writer.println("x" + (i + 1) + (j + 1) + " + x" + (j + 1) + (k + 1) + " - x" + (i + 1) + (k + 1) + " >= 0;");// + endl;
				}
			}
		}

		writer.close();
		String File = solveLP(Filename, LPModelName);
		//delete[] name2; delete[] name1;
		return File;
	}

	static String LPSetUp_bipart(int agents, int tasks, int run) {
		int k, i, j;
		double NegativeWeightSum = 0;
		int n = agents + tasks;
		//int width = n;


		//clock_t start, end;
		//float time = 0.0;
		//start = clock();

		//vector< vector<float> > AgentCoordinates(n, vector<float>(2));
		double AgentDistances[][] = new double[n][n];//(n, vector<float>(n));
		double EdgeUtility[][] = new double[n][n];//(n, vector<float>(n));


		String Filename = "LPinput_Tasks" + Integer.toString(tasks) + "Agents" + Integer.toString(agents) + "Run" + Integer.toString(run) + ".lp";

		PrintWriter writer = null;
		try {
			writer = new PrintWriter(Filename, "UTF-8");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		String LPModelName = "LPresults_Tasks" + Integer.toString(tasks) + "Agents" + Integer.toString(agents) + "Run" + Integer.toString(run);


		writer.println("/* Objective function */");
		writer.println("min: ");
		//for bipartite graph formulation
		for (i = 0; i < tasks; i++)// loop through all the tasks
		{
			for (j = tasks; j < n; j++)// loop though all the robots
			{
				//AgentDistances[i][j]=Math.sqrt(Math.pow((Coordinates[i][0] - Coordinates[j][0]), 2) + Math.pow((Coordinates[i][1] - Coordinates[j][1]), 2));
				AgentDistances[i][j]=Math.sqrt(Math.pow((Coordinates[i][0] - Coordinates[j][0]), 2) + Math.pow((Coordinates[i][1] - Coordinates[j][1]), 2));
				double dist = AgentDistances[i][j];
				/*
				if (i < tasks && j < tasks) {
					//AgentDistances[i][j] = 0;
					EdgeUtility[i][j] = -n * tasks * dist;
				}
				else {
					//AgentDistances[i][j] = 0;
					EdgeUtility[i][j] = calculateEdgeCost(dist);
				}
				*/
				EdgeUtility[i][j] = calculateEdgeCost(dist);
				if (EdgeUtility[i][j] >= 0)
				{
					if (i == 0 && j == 1) { writer.println(EdgeUtility[i][j] + " x" + (i + 1) + (j + 1));}//myfile + EdgeUtility[i][j] + " x" + i + 1 + j + 1; }
					else { writer.println(" + " + EdgeUtility[i][j] + " x" + (i + 1) + (j + 1));}//myfile + " + " + EdgeUtility[i][j] + " x" + i + 1 + j + 1; }
				}
				else
				{
					//myfile + " - " + fabs(EdgeUtility[i][j]) + " x" + i + 1 + j + 1;
					writer.println(" - " + Math.abs(EdgeUtility[i][j]) + " x" + (i + 1) + (j + 1));
					NegativeWeightSum = NegativeWeightSum + Math.abs(EdgeUtility[i][j]);
				}
			}
		}
		writer.println(" + " + NegativeWeightSum + ";" );
		writer.println("");
		//myfile + " + " + NegativeWeightSum + ";" + endl + endl;

		//Agent edges, their utilities, and the objective function have now been stored. Constraints are now set.
		writer.println("/* Variable Bounds */");
		//myfile + "/* Variable Bounds */" + endl;

		//for bipartite graph formulation
		for (i = 0; i < tasks; i++)// loop through all the tasks
		{
			for (j = tasks; j < n; j++)// loop though all the robots
			{
				//if ((i >= tasks && j < tasks) || (i < tasks && j >= tasks)) {
				writer.println("0 <= " + "x" + (i + 1) + (j + 1) + " <= 1;");// + endl;
				//}
				//else
				//{
				//myfile + "0 <= " + "x" + i + 1 + j + 1 + " <= 1;" + endl;
				//}
			}
		}
		
		/*
		for (i = 0; i < n; i++)
		{
			for (j = i + 1; j < n; j++)
			{
				for (k = j + 1; k < n; k++)
				{
					writer.println("x" + (i + 1) + (j + 1) + " + x" + (i + 1) + (k + 1) + " - x" + (j + 1) + (k + 1) + " >= 0;");// + endl;
				}
				for (k = i + 1; k < j; k++)
				{
					writer.println("x" + (i + 1) + (j + 1) + " + x" + (k + 1) + (j + 1) + " - x" + (i + 1) + (k + 1) + " >= 0;");// + endl;
				}
				for (k = j + 1; k < n; k++)
				{
					writer.println("x" + (i + 1) + (j + 1) + " + x" + (j + 1) + (k + 1) + " - x" + (i + 1) + (k + 1) + " >= 0;");// + endl;
				}
			}
		}
		*/
		writer.close();
		String File = solveLP(Filename, LPModelName);
		//delete[] name2; delete[] name1;
		return File;
	}

	static String solveLP(String Filename2, String LPModelName) {
		String str = LPModelName+".csv";
		try {
			// LP model creation here
			LpSolve lp;
			/*read the LP model*/
			//cout + Filename2 + " and " + LPModelName + endl;
			/*
	char *readFileName = new char[Filename2.size() + 1];
	std::copy(Filename2.begin(), Filename2.end(), readFileName);
	readFileName[Filename2.size()] = '\0';

	char *LPModel = new char[LPModelName.size() + 1];
	std::copy(LPModelName.begin(), LPModelName.end(), LPModel);
	LPModel[LPModelName.size()] = '\0';
			 */
			//cout + readFileName + " and " + LPModel + endl;
			lp = LpSolve.readLp(Filename2, 3, LPModelName);
			lp.solve();
			lp.setOutputfile(str);
			lp.printSolution(1);
			//}
			lp.deleteLp();
			//System.out.println("LP Solve Done!");
		}
		catch (LpSolveException e) {
			e.printStackTrace();
		}
		return str;
	}
	
	static double find_distance (int task, int robot) {
        double x_diff, y_diff;
        x_diff = Coordinates[task][0] - Coordinates[robot][0];
        y_diff = Coordinates[task][1] - Coordinates[robot][1];
        return Math.sqrt(x_diff*x_diff + y_diff*y_diff);
    }
	
	static double calculateCost(int n){
		double cost_dist=0;
		for(int all=0;all<n;all++) {
			if(currentAssignmentStat[all][1]>=0) {
				//cost_dist += Math.sqrt(Math.pow((Coordinates[currentAssignmentStat[all][0]][0] - Coordinates[currentAssignmentStat[all][1]][0]), 2) + Math.pow((Coordinates[currentAssignmentStat[all][0]][1] - Coordinates[currentAssignmentStat[all][1]][1]), 2));
				int robot = currentAssignmentStat[all][0];
				int task = currentAssignmentStat[all][1];
				cost_dist= cost_dist+find_distance(task, robot);
			}//if(currentAssignmentStat[all][1]==-1)
				//System.out.println("WHY -1 Here??");
		}
		return cost_dist;
	}
	
	// run brute-force method here and calculate the minimum cost for the highest valued CS. Print the result to a file.
	public static void run_brute_Force(int[][] coordinates, PrintWriter writer, int agents, int tasks, ArrayList<Integer> optimalMemberSize, int run, int maxCoalSize){
		Integer[] opt_member_array = optimalMemberSize.toArray(new Integer[optimalMemberSize.size()]);
		optimalValue = calculateOptimalValue(optimalMemberSize);
		
		brute_force bfp = new brute_force(Coordinates, agents, tasks, opt_member_array);
		double startTime = System.currentTimeMillis();
        
		double minCost = bfp.generate (tasks, optimalValue);
        /*
        ArrayList <int[]> cs;
        double val;
        double cost;
        double maxVal = 0;
        double minCost = 1000000;
        
        for (int i = 0; i < bfp.Valid_CS.size(); i++) {
            //System.out.print(i+1 + " ");
            cs = bfp.Valid_CS.get(i);
            val = bfp.value(cs);
            cost = bfp.cost_coalStruct(cs);
            if (val == optimalValue) {
            	//brute_force.maxVal = val;
            	maxVal = val;
                if (cost < minCost) {
                	minCost = cost;
                	//System.out.println(cost);
                }
            }
        }
        */
        double currTime = System.currentTimeMillis();
        double timePassed = currTime - startTime;
        //print this data to the file
        writer.println(agents + "\t" + tasks + "\t" + run + "\t" + timePassed + "\t" + optimalValue + "\t" + optimalValue + "\t" + -1 + "\t" + minCost + "\t" + -1 + "\t" + maxCoalSize);
		System.out.println("Brute-Force method --> Done with this set: Agents= " +agents + " Tasks= "+tasks+ " Run= "+run + " and O_i set: " + optimalMemberSize.toString() );
	}
	
	//main() method
	public static void main(String args[])
	{
		//srand(NULL);
		width = 100;
		for(int agents = 10; agents <=100; agents+=10) {// number of robots are varied here
			for(int tasks = 2; tasks <=0.5*agents && tasks <= 10 ; tasks+=2) {// number of tasks are varied here
				//int tasks = 2;
				int n = agents + tasks;
				for (int run=1;run<=1;run++) {// number of runs for each setting
					int maxCoalSize = -1;
					String outputFile = "region_Task" + Integer.toString(tasks) + "Agents" + Integer.toString(agents) + "Run" + Integer.toString(run) + ".txt";
					PrintWriter writer = null;
					try {
						writer = new PrintWriter(outputFile, "UTF-8");
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}

					Coordinates = new int[n][2];
					intPartitions = new ArrayList<ArrayList<Integer>>();
					costPartitions = new ArrayList<ArrayList<Integer>>();
					currentAssignmentStat = new int[n][2];
					optimalValue=0;
					initialCost=0;
					finalCost=0;
					initialVal=0;
					finalValue=0;

					//writer.println("agents\ttasks\trun\ttimePassed\toptimalValue\tfinalValue\tinitialVal\tfinalCost\tinitialCost");
					agentCoordinateGeneration(agents, tasks);
					double startTime = System.currentTimeMillis();

					String Filename = LPSetUp_bipart(agents, tasks, run);// calling the methods for the bipartite graph
					extractLPPartitions_bipart(agents, tasks, Filename);// data is put in a vector called costPartitions

					double currTime = System.currentTimeMillis();
					double timePassed = currTime - startTime;
					//write format: agent# task# run# timeTaken opt_val final_val initial_val final_cost initial_cost
					writer.println(agents + "\t" + tasks + "\t" + run + "\t" + timePassed + "\t" + optimalValue + "\t" + finalValue + "\t" + initialVal + "\t" + finalCost + "\t" + initialCost+ "\t" + maxCoalSize);

					FindAllUniqueParts(agents, tasks);

					task sortedT[] = new task[tasks];
					int[][] currentCoalSizes = new int[tasks][2];

					for (int p = 0; p < intPartitions.size(); p++) {
						ArrayList<Integer> optimalMemberSize = intPartitions.get(p);//create an array containing the optimal member sizes required for each task
						//Collections.sort(optimalMemberSize);
						maxCoalSize = -1;
						for (int c = 0; c < tasks; c++) {
							//System.out.println(sortedT[c].taskID);
							currentCoalSizes[c][0] = costPartitions.get(c).get(0);
							currentCoalSizes[c][1] = costPartitions.get(c).size() - 1;// -1 is there because there is also 1 task in the partition, but only the count of agents is stored as the size.
							sortedT[c] = new task();
							sortedT[c].taskID = costPartitions.get(c).get(0);
							sortedT[c].coalSize = costPartitions.get(c).size() - 1;
							sortedT[c].optimal = optimalMemberSize.get(c);
							if(maxCoalSize < sortedT[c].optimal) maxCoalSize = sortedT[c].optimal;
						}
						optimalValue = calculateOptimalValue(optimalMemberSize);
						startTime = System.currentTimeMillis();

						Arrays.sort(sortedT, new sortByCoalSize());//tasks are sorted at this point
						regionGrowing(agents, tasks, optimalMemberSize, currentCoalSizes, sortedT);//the region growing algorithm

						currTime = System.currentTimeMillis();
						timePassed = currTime - startTime;
						//Arrays.sort(sortedT, new sortByCoalSize());
						//System.out.println("done with RG.. ");
						finalCost = calculateCost(n);
						finalValue= calculateValue(sortedT);
						//write format: agent# task# run# timeTaken opt_val final_val initial_val final_cost initial_cost
						writer.println(agents + "\t" + tasks + "\t" + run + "\t" + timePassed + "\t" + optimalValue + "\t" + finalValue + "\t" + initialVal + "\t" + finalCost + "\t" + initialCost+ "\t" + maxCoalSize);
						System.out.println("Region_Growing --> Done with this set: Agents= " +agents + " Tasks= "+tasks+ " Run= "+run + " and O_i set: " + optimalMemberSize.toString() );
						//displayAssignment(currentAssignmentStat, 0);
					}
					writer.close();
					/*
					System.out.println("Brute-Force method --> STARTING with this: Agents= " +agents + " Tasks= "+tasks+ " Run= "+run + " for a list of O_i sets");
					//run Brute-Force method and print the result to a new file
					String bfp_outputFile = "BFP_Task" + Integer.toString(tasks) + "Agents" + Integer.toString(agents) + "Run" + Integer.toString(run) + ".txt";
					PrintWriter bfp_writer = null;
					try {
						bfp_writer = new PrintWriter(bfp_outputFile, "UTF-8");
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					for (int p = 0; p < intPartitions.size(); p++) {
						maxCoalSize = -1;
						ArrayList<Integer> optimalMemberSize = intPartitions.get(p);//create an array containing the optimal member sizes required for each task
						//Collections.sort(optimalMemberSize);
						maxCoalSize = Collections.max(optimalMemberSize);
						run_brute_Force(Coordinates, bfp_writer, agents, tasks, optimalMemberSize, run, maxCoalSize);
					}
					bfp_writer.close();
					*/
				}
			}
		}
		System.out.println("We are DONE with the code including the brute-force methods!");
		return;

	}


	private static double calculateValue(task[] sortedT) {
		double val=0;
		//task[] TlistCopy = sortedT.clone();
		for(int i=0; i<sortedT.length; i++) {
			val = val + (Math.pow(sortedT[i].optimal,2) - Math.pow((sortedT[i].optimal - sortedT[i].coalSize),2));
			//System.out.print("This coal size is : "+TlistCopy[i].coalSize+" and val is: "+ val);
		}
		return val;
	}
}

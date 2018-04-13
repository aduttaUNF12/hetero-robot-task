/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.*;
/**
 *
 * @author n00823146
 */
public class brute_force {
    
    public static int n;
    public static int robots;
    public static int tasks;
    public static int level;
    public static int valid_count;
    public int n_max;
    
    public static double maxUtil;
    public static double maxVal;
    public static double minCost;
    public static ArrayList <int[]> OPT_CS;
    public ArrayList <ArrayList <int[]>> Valid_CS = new ArrayList <> ();
    
    public static int position[][];//={{263,196},{335,183},{381,123},{300,48},{212,128}};
    public static Integer optimal[];
    
    Scanner reader = new Scanner(System.in);  // Reading from System.in
    public static task[] bfp_t;// = new task[];
    
    public brute_force (int[][] pos, int agents, int task, Integer[] opt) {
    	robots = agents;
        tasks = task;
        maxUtil = -1000000;
        maxVal = 1;
        minCost = 1000000;
        level = task;
        position = pos.clone();
        optimal = opt.clone();
        n= robots + tasks;
        bfp_t = new task[task];
        initialize ();
    }
    
    final void initialize () {
    	//Arrays.sort(optimal);
    	for(int i=0;i<tasks;i++) {
    		bfp_t[i] = new task();
    		bfp_t[i].taskID=i;
    		bfp_t[i].coalSize = 0;
    		bfp_t[i].optimal = optimal[i];
    	}
    }
    
    static int find_task_number (int[] coal) {
        int result = 0;
        for (int i = 0; i < coal.length; i++) {
            if (coal[i] <= tasks) {
            	result = coal[i] - 1;
                break;
            }
        }
        return result;
    }
    
    double value(ArrayList<int[]> cs) {
        double val_cs = 0;
        int task_number;
        int assigned;
        
        for (int i = 0; i < cs.size(); i++) {
            task_number = find_task_number(cs.get(i));
            assigned = cs.get(i).length - 1;
            bfp_t[task_number].coalSize = assigned;
            val_cs  = val_cs + (double)assigned * (2.0 * bfp_t[task_number].optimal - (double)assigned);
        }
        
        return val_cs;
    }
    
   
    double find_distance (int task, int robot) {
        double x_diff, y_diff;
        x_diff = position[task][0] - position[robot][0];
        y_diff = position[task][1] - position[robot][1];
        return Math.sqrt(x_diff*x_diff + y_diff*y_diff);
    }
    
    double cost_coal(int[] coal) {
        double cost=0;
        double dist;
        int task_number;
        task_number = find_task_number(coal);
        for (int i = 0; i < coal.length; i++) {
            dist = find_distance(task_number, coal[i] - 1);
            cost  = cost + dist;
        }
        return cost;
    }
    
    double cost_coalStruct(ArrayList< int[]> cs) {
        double cost=0;
        for (int i = 0; i < cs.size(); i++) {
            cost = cost + cost_coal(cs.get(i));
        }
        return cost;
    }
    
    double utility(ArrayList< int[]> cs) {
        double u = value(cs) - cost_coalStruct(cs);
        return u;
    }
    
    
    boolean isValid(ArrayList<int[]> cs) {
        boolean result;
        int r, t;
        
        result = true;
        for (int i = 0; i < cs.size(); i++) {
            r = 0;
            t = 0;
            for (int j = 0; j < cs.get(i).length; j++) {
                if (cs.get(i)[j] <= tasks) {
                    t++;
                }
                else {
                    r++;
                }
            }
            if ((t != 1) || (r == 0)) {
                result = false;
                break;
            }
        }
        
        return result;
    }
    
    void show (ArrayList<int[]> cs) {
        for (int i = 0; i < cs.size(); i++) {
            System.out.print("\t"+(i+1)+"th coalition is --> ");
            for (int j = 0; j < cs.get(i).length-1; j++) {
                System.out.print(cs.get(i)[j]+", ");
            }
            System.out.print(cs.get(i)[cs.get(i).length-1]);
        }
    }
    
    
    ArrayList<int[]> coalition_structure (int[] kappa, int p) {
        ArrayList<int[]> result = new ArrayList<>();
        int count;
        
        for (int i = 0; i < p; i++) {
            count = 0;
            for (int j = 1; j <= n; j++) {
                if (kappa[j-1] == i) {
                    count++;
                }
            }
            int[] tempArr = new int[count];
            int index = 0;
            for (int j = 1; j <= n; j++) {
                if (kappa[j-1] == i) {
                    tempArr[index] = j;
                    index++;
                }
            }
            result.add(tempArr);
            //System.out.print("} ");
        }
        return result;
    }
    
    static void  print_coalition (ArrayList<int []> cs) {
        for (int i = 0; i < cs.size(); i++) {
            System.out.print("{");
            for (int j = 0; j < cs.get(i).length; j++) {
                if (j == 0) {
                    System.out.print (cs.get(i)[j]);
                } else {
                    System.out.print("," + cs.get(i)[j]);
                }
            }
            System.out.print("} ");
            
        }
    }
    
    /*
     void print_coalition (int[] kappa, int p) {
     int count;
     for (int i = 0; i < p; i++) {
     System.out.print("{");
     count = 0;
     for (int j = 1; j <= n; j++) {
     if (kappa[j-1] == i) {
     if (count > 0) {
     System.out.print("," + j);
     }
     else {
     System.out.print (j);
     }
     count++;
     }
     }
     System.out.print("} ");
     }
     }
     */
    
    int next_coalition (int[] kappa, int[] M, int lev, double optVal) {
        ArrayList<int[]> cs;
        int flag = 1;
        
        //for (int i = 0; i < n; i++) {
        //    System.out.print (kappa[i]);
        //}
        //System.out.print(" ");
        //print_coalition (kappa,lev);
        cs = coalition_structure(kappa,lev);
        if (isValid(cs)) {
        	if(value(cs) == optVal) {
        		if(cost_coalStruct(cs)<minCost)
        			minCost = cost_coalStruct(cs);
        	}
            //Valid_CS.add(cs);
            //print_coalition (cs);
            //print_coalition (kappa,lev);
            //System.out.println(" Valid");
            valid_count++;
        }
        //show(cs);
        for (int i = n-1; i > 0; i--) {
            flag = 1;
            if ((kappa[i] < lev-1) && (kappa[i] <= M[i-1])) {
                kappa[i] = kappa[i]+1;
                if (kappa[i] > M[i]) {
                    M[i] = kappa[i];
                }
                for (int j = i+1; j <= n-(lev-M[i]); j++) {
                    kappa[j] = 0;
                    M[j] = M[i];
                }
                for (int j = n-(lev-M[i])+1; j <= n-1; j++) {
                    kappa[j] = M[j] = lev - (n-j);
                }
                return flag;
            }
            flag = 0;
        }
        //    System.out.println();
        return flag;
    }
    
    
    int[] first_coalition_setup (int lev, double optVal) {
        int[] result = new int[n];
        for (int i = 0; i <= n-lev; i++) {
            result[i] = 0;
        }
        for (int i = n-lev+1; i <= n-1; i++) {
            result[i] = i - (n-lev);
        }
        return result;
    }
    
    
    double generate (int level, double optVal) {
        int[] kappa, M;
        int flag, count;
        flag = 1;
        kappa = first_coalition_setup (level, optVal);
        M = first_coalition_setup (level, optVal);
        count = 1;
        //System.out.print(count + " ");
        while (flag != 0) {
            flag = next_coalition (kappa, M, level, optVal);
            if (flag != 0) {
                count++;
            }
            //System.out.println();
            //if (flag != 0) {
            //    System.out.print(count + " ");
            //}
        }
        //System.out.println("Total coalitions: " + count);
        //System.out.println("Valid coalitions: " + valid_count);
        return minCost;
    }
    
    //iterative implementation of the CSG
    /*
    public static void main(String[] args) {
        // TODO code application logic here
        double val;
        double cost;
        double ut;
        
        robots = 6;
        tasks = 3;
        n = robots + tasks;
        // for (int a = 6; a <= 6; a+=2) {//implement the above algorithm on 4 to 12 agents (inclusive, interval of 2)
        TaskAllocation_bruteForce bfp = new TaskAllocation_bruteForce();
        //System.out.println("Agent count --> "+a);
        //n = a;
        double startTime = System.currentTimeMillis();
        //level = tasks;
        bfp.generate (tasks);
        ArrayList <int[]> cs;
        for (int i = 0; i < Valid_CS.size(); i++) {
            System.out.print(i+1 + " ");
            cs = Valid_CS.get(i);
            print_coalition (cs);
            val = value(cs);
            cost = cost_coalStruct(cs);
            ut = utility(cs);
            System.out.print (" " + val + " " + cost + " " + ut);
            if (val > maxVal) {
                maxVal = val;
                if (cost < minCost) {
                    minCost = cost;
                }
            }
            
            System.out.println();
        }
        System.out.print ("Optimal Coalition Structure: ");
        print_coalition(OPT_CS);
        System.out.println();
        double currTime = System.currentTimeMillis();
        double timePassed = currTime - startTime;
        System.out.println("For #"+n+" number of agents, it took " + timePassed + " milliseconds");
        //}
    }
    */
}


public class task {
	int taskID;
	int coalSize;
	int optimal;
	Integer distribution[];
	
	public task() {
		taskID = -1;
		coalSize = 0;
		optimal = 0;
		distribution = new Integer[hetero_task_alloc.types];
	}
}

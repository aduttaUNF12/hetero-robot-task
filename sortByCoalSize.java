import java.util.Comparator;

public class sortByCoalSize implements Comparator<task>{
	public int compare(task a, task b)
    {
        return (b.coalSize - a.coalSize);//descending order
    }
}


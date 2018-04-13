import java.util.Comparator;

public class sortByDist implements Comparator<AgentCoordinates>{
	public int compare(AgentCoordinates a, AgentCoordinates b)
    {
        return (int) (a.dist - b.dist);//ascending order
    }
}

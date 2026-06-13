import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
public class GraphScheduler
{
    private final List<List<Integer>> graph;
    private final int[] indegree;
    public GraphScheduler(int vertices)
    {
        graph = new ArrayList<>();
        indegree = new int[vertices];
        for(int i = 0; i < vertices; i++)
        {
            graph.add(new ArrayList<>());
        }
    }
    public void addDependency(int before, int after)
    {
        check(before);
        check(after);
        graph.get(before).add(after);
        indegree[after]++;
    }
    public List<Integer> buildOrder()
    {
        ArrayDeque<Integer> ready = new ArrayDeque<>();
        int[] work = Arrays.copyOf(indegree, indegree.length);
        for(int i = 0; i < work.length; i++)
        {
            if(work[i] == 0)
            {
                ready.add(i);
            }
        }
        List<Integer> order = new ArrayList<>();
        while(!ready.isEmpty())
        {
            int current = ready.remove();
            order.add(current);
            List<Integer> nextList = new ArrayList<>(graph.get(current));
            Collections.sort(nextList);
            for(int next : nextList)
            {
                work[next]--;
                if(work[next] == 0)
                {
                    ready.add(next);
                }
            }
        }
        if(order.size() != graph.size())
        {
            throw new IllegalStateException("cycle detected");
        }
        return order;
    }
    public int longestChain()
    {
        List<Integer> order = buildOrder();
        int[] distance = new int[graph.size()];
        for(int node : order)
        {
            for(int next : graph.get(node))
            {
                distance[next] = Math.max(distance[next], distance[node] + 1);
            }
        }
        int best = 0;
        for(int value : distance)
        {
            best = Math.max(best, value);
        }
        return best;
    }
    private void check(int vertex)
    {
        if(vertex < 0 || vertex >= graph.size())
        {
            throw new IllegalArgumentException("bad vertex");
        }
    }
    public static void main(String[] args)
    {
        GraphScheduler scheduler = new GraphScheduler(8);
        scheduler.addDependency(0, 2);
        scheduler.addDependency(0, 3);
        scheduler.addDependency(1, 3);
        scheduler.addDependency(2, 5);
        scheduler.addDependency(3, 6);
        scheduler.addDependency(4, 6);
        scheduler.addDependency(5, 7);
        scheduler.addDependency(6, 7);
        System.out.println(scheduler.buildOrder());
        System.out.println("Longest chain: " + scheduler.longestChain());
    }
}



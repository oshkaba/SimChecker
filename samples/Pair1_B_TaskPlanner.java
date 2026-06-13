import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
public class TaskPlanner
{
    private final List<List<Integer>> edges;
    private final int[] incoming;
    public TaskPlanner(int count)
    {
        edges = new ArrayList<>();
        incoming = new int[count];
        for(int i = 0; i < count; i++)
        {
            edges.add(new ArrayList<>());
        }
    }
    public void requireBefore(int first, int second)
    {
        validate(first);
        validate(second);
        edges.get(first).add(second);
        incoming[second]++;
    }
    public List<Integer> makePlan()
    {
        ArrayDeque<Integer> queue = new ArrayDeque<>();
        int[] left = Arrays.copyOf(incoming, incoming.length);
        for(int i = 0; i < left.length; i++)
        {
            if(left[i] == 0)
            {
                queue.add(i);
            }
        }
        List<Integer> result = new ArrayList<>();
        while(!queue.isEmpty())
        {
            int task = queue.remove();
            result.add(task);
            List<Integer> followers = new ArrayList<>(edges.get(task));
            Collections.sort(followers);
            for(int next : followers)
            {
                left[next]--;
                if(left[next] == 0)
                {
                    queue.add(next);
                }
            }
        }
        if(result.size() != edges.size())
        {
            throw new IllegalStateException("loop found");
        }
        return result;
    }
    public int maxDepth()
    {
        List<Integer> result = makePlan();
        int[] depth = new int[edges.size()];
        for(int task : result)
        {
            for(int next : edges.get(task))
            {
                depth[next] = Math.max(depth[next], depth[task] + 1);
            }
        }
        int answer = 0;
        for(int value : depth)
        {
            answer = Math.max(answer, value);
        }
        return answer;
    }
    private void validate(int task)
    {
        if(task < 0 || task >= edges.size())
        {
            throw new IllegalArgumentException("bad task");
        }
    }
    public static void main(String[] args)
    {
        TaskPlanner planner = new TaskPlanner(8);
        planner.requireBefore(0, 2);
        planner.requireBefore(0, 3);
        planner.requireBefore(1, 3);
        planner.requireBefore(2, 5);
        planner.requireBefore(3, 6);
        planner.requireBefore(4, 6);
        planner.requireBefore(5, 7);
        planner.requireBefore(6, 7);
        System.out.println(planner.makePlan());
        System.out.println("Max depth: " + planner.maxDepth());
    }
}



import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
public class RouteFinder
{
    private final int[][] grid;
    private final int rows;
    private final int cols;
    public RouteFinder(int[][] grid)
    {
        this.grid = grid;
        rows = grid.length;
        cols = grid[0].length;
    }
    public List<String> shortestRoute(int sr, int sc, int er, int ec)
    {
        int[][] parentRow = new int[rows][cols];
        int[][] parentCol = new int[rows][cols];
        for(int r = 0; r < rows; r++)
        {
            Arrays.fill(parentRow[r], -1);
            Arrays.fill(parentCol[r], -1);
        }
        ArrayDeque<int[]> queue = new ArrayDeque<>();
        queue.add(new int[] {sr, sc});
        parentRow[sr][sc] = sr;
        parentCol[sr][sc] = sc;
        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};
        while(!queue.isEmpty())
        {
            int[] cell = queue.remove();
            int row = cell[0];
            int col = cell[1];
            if(row == er && col == ec)
            {
                break;
            }
            for(int i = 0; i < dr.length; i++)
            {
                int nr = row + dr[i];
                int nc = col + dc[i];
                if(canEnter(nr, nc, parentRow))
                {
                    parentRow[nr][nc] = row;
                    parentCol[nr][nc] = col;
                    queue.add(new int[] {nr, nc});
                }
            }
        }
        if(parentRow[er][ec] == -1)
        {
            return Collections.emptyList();
        }
        List<String> path = new ArrayList<>();
        for(int row = er, col = ec; row != sr || col != sc;)
        {
            path.add("(" + row + "," + col + ")");
            int oldRow = parentRow[row][col];
            int oldCol = parentCol[row][col];
            row = oldRow;
            col = oldCol;
        }
        path.add("(" + sr + "," + sc + ")");
        Collections.reverse(path);
        return path;
    }
    private boolean canEnter(int row, int col, int[][] parentRow)
    {
        if(row < 0 || row >= rows || col < 0 || col >= cols)
        {
            return false;
        }
        return grid[row][col] == 0 && parentRow[row][col] == -1;
    }
    public static void main(String[] args)
    {
        int[][] map = {{0,0,0,1,0},{1,1,0,1,0},{0,0,0,0,0},{0,1,1,1,0},{0,0,0,0,0}};
        RouteFinder finder = new RouteFinder(map);
        List<String> route = finder.shortestRoute(0, 0, 4, 4);
        System.out.println(route);
        System.out.println("Steps: " + Math.max(0, route.size() - 1));
    }
}















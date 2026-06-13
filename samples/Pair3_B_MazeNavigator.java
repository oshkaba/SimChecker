import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
public class MazeNavigator
{
    private final int[][] maze;
    private final int height;
    private final int width;
    public MazeNavigator(int[][] maze)
    {
        this.maze = maze;
        height = maze.length;
        width = maze[0].length;
    }
    public List<String> findPath(int startRow, int startCol, int goalRow, int goalCol)
    {
        int[][] prevRow = new int[height][width];
        int[][] prevCol = new int[height][width];
        for(int r = 0; r < height; r++)
        {
            Arrays.fill(prevRow[r], -1);
            Arrays.fill(prevCol[r], -1);
        }
        ArrayDeque<int[]> open = new ArrayDeque<>();
        open.add(new int[] {startRow, startCol});
        prevRow[startRow][startCol] = startRow;
        prevCol[startRow][startCol] = startCol;
        int[] rowStep = {-1, 1, 0, 0};
        int[] colStep = {0, 0, -1, 1};
        while(!open.isEmpty())
        {
            int[] point = open.remove();
            int row = point[0];
            int col = point[1];
            if(row == goalRow && col == goalCol)
            {
                break;
            }
            for(int i = 0; i < rowStep.length; i++)
            {
                int nr = row + rowStep[i];
                int nc = col + colStep[i];
                if(isFree(nr, nc, prevRow))
                {
                    prevRow[nr][nc] = row;
                    prevCol[nr][nc] = col;
                    open.add(new int[] {nr, nc});
                }
            }
        }
        if(prevRow[goalRow][goalCol] == -1)
        {
            return Collections.emptyList();
        }
        List<String> answer = new ArrayList<>();
        for(int row = goalRow, col = goalCol; row != startRow || col != startCol;)
        {
            answer.add("(" + row + "," + col + ")");
            int oldRow = prevRow[row][col];
            int oldCol = prevCol[row][col];
            row = oldRow;
            col = oldCol;
        }
        answer.add("(" + startRow + "," + startCol + ")");
        Collections.reverse(answer);
        return answer;
    }
    private boolean isFree(int row, int col, int[][] prevRow)
    {
        if(row < 0 || row >= height || col < 0 || col >= width)
        {
            return false;
        }
        return maze[row][col] == 0 && prevRow[row][col] == -1;
    }
    public static void main(String[] args)
    {
        int[][] data = {{0,0,0,1,0},{1,1,0,1,0},{0,0,0,0,0},{0,1,1,1,0},{0,0,0,0,0}};
        MazeNavigator nav = new MazeNavigator(data);
        List<String> path = nav.findPath(0, 0, 4, 4);
        System.out.println(path);
        System.out.println("Length: " + Math.max(0, path.size() - 1));
    }
}















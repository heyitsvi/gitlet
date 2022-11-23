package gitlet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Paths {
    /** Array containing info about whether the vertices have been visited **/
    private boolean[] marked;
    /** All the paths from a vertex to some other vertex **/
    private List<List<Integer>> allPaths;
    /** Current path from the source to destination **/
    private List<Integer> localPathList;
    /** Distance of nodes from the given source **/
    private int[] distTo;

    /**
     * Paths constructor initialises the marked and distTo arrays with
     * and calls the allPaths function to find the paths from a source
     * to some destination.
     * @param G Graph Obj
     * @param s source vertex number
     * @param d destination vertex number
     */
    public Paths(gitlet.GraphObj G, int s, int d) {
        marked = new boolean[G.getV()];
        Arrays.fill(marked, false);
        distTo = new int[G.getV()];
        Arrays.fill(distTo, -1);
        distTo[s] = 0;

        allPaths = new ArrayList<>();
        localPathList = new ArrayList<>();
        localPathList.add(s);

        allPaths(G, s, d, marked, localPathList);
    }

    /** Get the all paths array list **/
    public List<List<Integer>> allPaths() {
        return this.allPaths;
    }

    /** Get the distTo array **/
    public int[] getDistances() {
        return this.distTo;
    }

    /**
     * Calculate all the paths from some source to a destination.
     * @param G Graph Obj
     * @param u Source Vertex
     * @param w Destination Vertex
     * @param visited Visited Array contains marked(already visited) vertices
     * @param list List to store the path
     */
    private void allPaths(gitlet.GraphObj G, int u, int w, boolean[] visited, List<Integer> list) {
        if (u == w) {
            List<Integer> res = new ArrayList<>(list);
            allPaths.add(res);
        } else {
            visited[u] = true;
            for (int i : G.adj(u)) {
                if (!visited[i]) {
                    list.add(i);
                    distTo[i] = distTo[u] + 1;
                    allPaths(G, i, w, visited, list);
                    list.remove(list.size() - 1);
                }
            }
            visited[u] = false;
        }
    }
}

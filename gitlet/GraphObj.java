package gitlet;
import java.util.*;

import static gitlet.Repository.COMMIT_DIR;
import static gitlet.Utils.plainFilenamesIn;

public class GraphObj {
    /** Number of vertices in the graph **/
    private int V;
    /** Adjacency List containing all the neighbours of a node **/
    private List<Integer>[] adj;
    /** Graph object constructor **/
    public GraphObj(int V) {
        this.V = V;
        adj = new ArrayList[V];
        for (int i = 0; i < V; i++) {
            adj[i] = new ArrayList<>();
        }
    }

    /** Add an edge from v to w. **/
    public void addEdge(int v, int w) {
        adj[v].add(w);
    }
    /** Get the Iterator to iter over all the neighbours of a node. **/
    public Iterable<Integer> adj(int v) {
        return adj[v];
    }
    /** Get the total number of vertices in the Graph. **/
    public int getV() {
        return this.V;
    }

    /**
     * Create a mapping of random Node Integer Vals with their corresponding file
     * names.
     * @return Map of FileNames and their Node Vals in the graph.
     */
    public static HashMap<String, Integer> createGraphMap() {
        HashMap<String, Integer> graphMap = new HashMap<>();
        List<String> commits = plainFilenamesIn(COMMIT_DIR);

        int count  = 0;

        for (String commit : commits) {
            graphMap.put(commit, count++);
        }
        return graphMap;
    }

    /**
     * Find the closest vertex common between source 1 and source 2 in the DAG.
     * @param distArr1 Distance Array with distances of various nodes from source 1 (HEAD)
     * @param distArr2 Distance Array with distances of various nodes from source 2 (Given Branch)
     * @param set Set containing the common nodes in the paths from source 1 and source 2
     *            to the very first commit.
     * @return Closest node to source 1 and source 2 from the set of all common nodes.
     */
    public static int closestVertexToNodes(int[] distArr1, int[] distArr2, Set<Integer> set) {
        TreeMap<Integer, Integer> nodeDistances = new TreeMap<>();
        for (int i : set) {
            nodeDistances.put(i, distArr1[i] + distArr2[i]);
        }
        int minDistance = Collections.min(nodeDistances.values());
        for (int key : nodeDistances.keySet()) {
            if (nodeDistances.get(key) == minDistance) {
                return key;
            }
        }
        return -1;
    }
}

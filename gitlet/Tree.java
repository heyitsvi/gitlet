package gitlet;
import java.io.Serializable;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class Tree implements Serializable {
    /** Map contains all the files that are staged or tracked**/
    private TreeMap<String, String> map;
    /** Set contains all the files that need to untracked or removed **/
    private Set<String> removeSet;
    /** Create a tree object. **/
    Tree() {
        map = new TreeMap<>();
        removeSet = new TreeSet<>();
    }
    /** Get the created Tree object. **/
    public static Tree createTree() {
        return new Tree();
    }
    /** Get the Map of the given Tree Object. **/
    public TreeMap<String, String> getMap() {
        return this.map;
    }
    /** Get the Remove Set of the given Tree Object. **/
    public Set<String> getRemoveSet() {
        return this.removeSet;
    }

}

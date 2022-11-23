package gitlet;


import java.io.File;
import java.io.Serializable;
import java.util.*;

import static gitlet.Repository.*;
import static gitlet.Utils.*;


/** Represents a gitlet commit object.
 *
 *  @author Vivek Singh
 */
public class Commit implements Serializable {
    /**
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    //private String author;
    private Date date;
    private String message;
    private String parent;
    private String parent2;
    private String tree;

    //private TreeMap<String, String> map = new TreeMap<>();


    Commit(String message, String parent, Date date, String tree, String parent2) {
        this.message = message;
        this.parent = parent;
        this.date = date;
        this.tree = tree;
        this.parent2 = parent2;
    }

    String getParent() {
        return this.parent;
    }

    String getMsg() {
        return this.message;
    }

    Date getDate() {
        return this.date;
    }

    String getTree() {
        return this.tree;
    }
    String getParent2() {
        return this.parent2;
    }

    static Commit initialCommit() {
        return new Commit("initial commit", null, new Date(0), null, null);
    }

    static Commit createCommit(String msg, String parent, Date d, String tree) {
        return new Commit(msg, parent, d, tree, null);
    }

    static Commit createMergeCommit(String msg, String parent, Date d, String tree, String p2) {
        return new Commit(msg, parent, d, tree, p2);
    }

    /** Get the list of all commit files in lexicographical order and print the commits
     * that contain the specified msg.
     * @param msg The msg to search for
     * */
    public static void findMsgInCommits(String msg) {
        List<String> files = plainFilenamesIn(COMMIT_DIR);
        boolean flag = false;
        for (String file : files) {
            File f = join(COMMIT_DIR, file);
            gitlet.Commit commitObj = readObject(f, gitlet.Commit.class);

            if (commitObj.getMsg().equals(msg)) {
                System.out.println(file);
                flag = true;
            }
        }

        if (!flag) {
            System.out.println("Found no commit with that message.");
        }

    }

    /** Get the list of all commit files in lexicographical order and print the commits */
    public static void printAllCommits() {
        List<String> files = plainFilenamesIn(COMMIT_DIR);

        for (String file : files) {
            File f = join(COMMIT_DIR, file);
            gitlet.Commit commitObj = readObject(f, gitlet.Commit.class);
            System.out.println(generateLogMsg(commitObj));
        }
    }

    /** Generate the log msg from the commit object
     * @param c Commit object that contains info to display.
     * @return Log Message
     * */
    public static String generateLogMsg(gitlet.Commit c) {
        String commit = sha1(serialize(c));
        String msg = c.getMsg();
        Date date = c.getDate();
        String formattedDate = formatDate(date);
        String parent = c.getParent();
        String parent2 = c.getParent2();

        if (parent2 == null) {
            return "=== \n"
                    + "commit " + commit + "\n"
                    + "Date: " + formattedDate + "\n"
                    + msg + "\n";
        } else {
            return "=== \n"
                    + "commit " + commit + "\n"
                    + "Merge: " + parent.substring(0, 7) + " " + parent2.substring(0, 7)
                    + "\n" + "Date: " + formattedDate + "\n"
                    + msg + "\n";
        }
    }

    /** Traverse commits starting from the HEAD commit to the initial commit
     * and display their log msg */
    public static void printLog() {
        String latestCommitID = getLatestIDInHEAD();

        gitlet.Commit prevCommitObj = getLatestCommitObj(latestCommitID);

        while (prevCommitObj.getParent() != null) {
            System.out.println(generateLogMsg(prevCommitObj));
            prevCommitObj = getLatestCommitObj(prevCommitObj.getParent());
        }
        System.out.println(generateLogMsg(prevCommitObj));
    }

    /** Get the commit object from a directory */
    public static gitlet.Commit getCommitObj(String fileName, File dir) {
        File filePath = join(dir, fileName);
        return readObject(filePath, gitlet.Commit.class);
    }

    /** Get the tree object from a commit */
    public static gitlet.Tree getCommitTreeObj(gitlet.Commit c) {
        String treeSHA = c.getTree();

        if (treeSHA == null) {
            return null;
        }
        File filePath = join(TREE_DIR, treeSHA);
        return readObject(filePath, gitlet.Tree.class);
    }

    /** Get the latest commit obj */
    public static gitlet.Commit getLatestCommitObj(String commitID) {
        return getCommitObj(commitID, COMMIT_DIR);
    }

    /** Get the tree object from the latest commit */
    public static gitlet.Tree getLatestCommitTreeObj(String commitID) {
        return getCommitTreeObj(getLatestCommitObj(commitID));
    }

    /** Create a new commit with the given message, can be a merge or regular
     * commit. */
    public static void createANewCommit(String msg, String type, String branch) {
        gitlet.Tree newTreeObj = getNewCombinedCommitObj();
        byte[] serialiseTreeObj = serialize(newTreeObj);
        String newObjSHA = sha1(serialiseTreeObj);

        String parent = getLatestIDInHEAD();

        File path = createTreeObj(serialiseTreeObj);
        writeObject(path, newTreeObj);

        gitlet.Commit newCommit;

        if (type.equals("regular")) {
            newCommit = gitlet.Commit.createCommit(msg, parent, new Date(), newObjSHA);
        } else {
            newCommit = gitlet.Commit.createMergeCommit(msg, parent, new Date(), newObjSHA, branch);
        }
        byte[] serialisedCommit = serialize(newCommit);
        File newCommitFile = createCommitObj(serialisedCommit);
        writeObject(newCommitFile, newCommit);

        updateActiveBranch(serialisedCommit);
        clearStagingArea();
    }

    /** Check if a commit with the given id exits **/
    public static String checkIfCommitExists(String commitID) {
        return findFileInDir(commitID, COMMIT_DIR);
    }

    /** Check if the same file (i.e.with the same SHA val) exists in the latest commit. */
    public static boolean sameFileInLatestCommit(String fileName, String sha) {
        String shaInMaster = getLatestIDInHEAD();
        gitlet.Tree latestCommitTreeObj = getLatestCommitTreeObj(shaInMaster);

        if (latestCommitTreeObj == null || !latestCommitTreeObj.getMap().containsKey(fileName)) {
            return false;
        }

        return latestCommitTreeObj.getMap().get(fileName).equals(sha);
    }

    /** Create a merge commit with the given branch and the head branch **/
    public static void createMergeCommit(String branch) {
        String msg  = "Merged " + branch + " into " + readContentsAsString(HEAD) + ".";
        String branchID = readContentsAsString(join(HEADS_DIR, branch));
        createANewCommit(msg, "merge", branchID);
    }

    /** Remove the files from the commit if they are staged for removal
     * @param removeFiles Set containing the names of the files that need to be removed.
     * @param commitTree The Tree Object that
     * */
    public static void removeFilesFromCommit(Set<String> removeFiles, gitlet.Tree commitTree) {
        for (String file : removeFiles) {
            commitTree.getMap().remove(file);
        }
    }

    /** Check if the latest commit in the current branch tracks this file */
    public static boolean fileInHEADCommit(String fileName) {
        gitlet.Tree latestCommitTreeObj = getLatestCommitTreeObj(getLatestIDInHEAD());
        if (latestCommitTreeObj == null) {
            return false;
        }
        return latestCommitTreeObj.getMap().containsKey(fileName);
    }

    /** Merge index and the latest commit to create the new commit obj
     * Remove the files staged for removal
     * Add the files staged for addition to the new commit obj.
     */
    public static gitlet.Tree getNewCombinedCommitObj() {
        String prevCommitSHA = getLatestIDInHEAD();
        gitlet.Tree prevCommitTreeObj = getLatestCommitTreeObj(prevCommitSHA);
        gitlet.Tree indexTreeObj = readObject(INDEX, gitlet.Tree.class);
        removeFilesFromCommit(indexTreeObj.getRemoveSet(), prevCommitTreeObj);
        return mergeObjs(prevCommitTreeObj, indexTreeObj);
    }

    /** Reset the CWD to the given Commit **/
    public static void resetToCommit(String commitID) {
        gitlet.Tree t2 = getCommitTreeObj(getCommitObj(commitID, COMMIT_DIR));
        Set<String> filesToRemove = new HashSet<>(plainFilenamesIn(CWD));
        Set<String> filesOtherCommit;

        if (t2 != null) {
            filesOtherCommit = t2.getMap().keySet();
            for (String file : filesOtherCommit) {
                if (join(CWD, file).exists()) {
                    overwriteFile(file, t2.getMap().get(file), CWD);
                } else {
                    createFileWithContents(join(CWD, file), join(BLOB_DIR, t2.getMap().get(file)));
                }
            }
            filesToRemove.removeAll(filesOtherCommit);
        }

        for (String file : filesToRemove) {
            restrictedDelete(file);
        }

        if (INDEX.exists()) {
            clearStagingArea();
        }
        String branch = getActiveBranch();
        writeContents(join(HEADS_DIR, branch), commitID);
    }

    public static String findInitCommitSHA() {
        List<String> allCommits = plainFilenamesIn(COMMIT_DIR);
        for (String commit : allCommits) {
            Commit c = getCommitObj(commit, COMMIT_DIR);
            if (c.getParent() == null) {
                return commit;
            }
        }
        return "";
    }

}

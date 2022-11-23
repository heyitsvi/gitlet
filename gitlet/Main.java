package gitlet;
import java.util.Set;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Vivek Singh
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch (firstArg) {
            case "init":
                validateNumArgs(args, 1);

                if (gitlet.Repository.checkGitDirExists()) {
                    System.out.println("A Gitlet version-control system already exists in the current directory.");
                    System.exit(0);
                }

                gitlet.Repository.setupGitlet();
                break;

            case "add":
                validateNumArgs(args, 2);

                if (!gitlet.Repository.checkGitDirExists()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                    System.exit(0);
                }

                if (!gitlet.Repository.checkFileExists(args[1])) {
                    System.out.println("File does not exist.");
                    System.exit(0);
                }

                if (!gitlet.Repository.indexExists()) {
                    gitlet.Repository.setupStagingArea(args[1]);
                } else {
                    gitlet.Repository.addToIndex(args[1]);
                }
                break;

            case "commit":
                validateNumArgs(args, 2);

                if (!gitlet.Repository.checkGitDirExists()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                    System.exit(0);
                }

                if (!gitlet.Repository.indexExists() || !gitlet.Repository.newFilesTracked()) {
                    System.out.println("No changes added to the commit.");
                    System.exit(0);
                }

                if (args[1].isBlank()) {
                    System.out.println("Please enter a commit message.");
                    System.exit(0);
                }

                gitlet.Commit.createANewCommit(args[1], "regular", null);
                break;

            case "rm" :
                validateNumArgs(args, 2);

                if (!gitlet.Repository.checkGitDirExists()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                    System.exit(0);
                }

                gitlet.Repository.removeFile(args[1]);
                break;

            case "log" :
                validateNumArgs(args, 1);
                if (!gitlet.Repository.checkGitDirExists()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                    System.exit(0);
                }

                gitlet.Commit.printLog();
                break;

            case "global-log" :
                validateNumArgs(args, 1);
                if (!gitlet.Repository.checkGitDirExists()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                    System.exit(0);
                }
                gitlet.Commit.printAllCommits();
                break;

            case "find" :
                validateNumArgs(args, 2);
                if (!gitlet.Repository.checkGitDirExists()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                    System.exit(0);
                }
                gitlet.Commit.findMsgInCommits(args[1]);
                break;

            case "status" :
                validateNumArgs(args, 1);
                if (!gitlet.Repository.checkGitDirExists()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                    System.exit(0);
                }

                gitlet.Repository.printStatus();
                break;

            case "branch" :
                validateNumArgs(args, 2);
                if (!gitlet.Repository.checkGitDirExists()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                    System.exit(0);
                }

                if (gitlet.Repository.branchExists(args[1])) {
                    System.out.println("A branch with that name already exists.");
                }

                gitlet.Repository.createBranch(args[1]);
                break;

            case "checkout" :
                if (!gitlet.Repository.checkGitDirExists()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                    System.exit(0);
                }

                if (args.length == 2) {
                    if (!gitlet.Repository.branchExists(args[1])) {
                        System.out.println("No such branch exists.");
                        System.exit(0);
                    }

                    if (gitlet.Repository.anyUntrackedFiles()) {
                        System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                        System.exit(0);
                    }

                    if (gitlet.Repository.isCurrentBranch(args[1])) {
                        System.out.println("No need to checkout the current branch.");
                        System.exit(0);
                    }
                    gitlet.Repository.checkoutBranch(args[1]);
                } else if (args.length == 3 && args[1].equals("--")) {
                    gitlet.Repository.checkoutFile(gitlet.Repository.getLatestIDInHEAD(), args[2]);
                } else if (args[2].equals("--") && args.length == 4) {
                    String commitInDir = gitlet.Commit.checkIfCommitExists(args[1]);
                    if (commitInDir.length() == 0) {
                        System.out.println("No commit with that id exists.");
                        System.exit(0);
                    }
                    gitlet.Repository.checkoutFile(commitInDir, args[3]);
                } else {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                break;

            case "rm-branch" :
                validateNumArgs(args, 2);

                if (!gitlet.Repository.checkGitDirExists()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                    System.exit(0);
                }

                if (!gitlet.Repository.branchExists(args[1])) {
                    System.out.println("A branch with that name does not exist.");
                    System.exit(0);
                }

                if (gitlet.Repository.isCurrentBranch(args[1])) {
                    System.out.println("Cannot remove the current branch.");
                    System.exit(0);
                }

                gitlet.Repository.removeBranch(args[1]);
                break;

            case "reset" :
                validateNumArgs(args, 2);

                if (!gitlet.Repository.checkGitDirExists()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                    System.exit(0);
                }

                if (gitlet.Repository.anyUntrackedFiles()) {
                    System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                    System.exit(0);
                }

                String commitInDir = gitlet.Commit.checkIfCommitExists(args[1]);
                if (commitInDir.length() == 0) {
                    System.out.println("No commit with that id exists.");
                    System.exit(0);
                }
                gitlet.Commit.resetToCommit(commitInDir);
                break;

            case "merge" :
                validateNumArgs(args, 2);
                if (!gitlet.Repository.checkGitDirExists()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                    System.exit(0);
                }

                if (gitlet.Repository.newFilesTracked()) {
                    System.out.println("You have uncommitted changes.");
                    System.exit(0);
                }

                if (!gitlet.Repository.branchExists(args[1])) {
                    System.out.println("A branch with that name does not exist.");
                    System.exit(0);
                }
                if (gitlet.Repository.isCurrentBranch(args[1])) {
                    System.out.println("Cannot merge a branch with itself.");
                    System.exit(0);
                }

                if (gitlet.Repository.anyUntrackedFiles()) {
                    System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                    System.exit(0);
                }

                String splitCommit = gitlet.Repository.findSplitPoint(args[1]);
                String otherBranch = gitlet.Repository.latestCommitIn(args[1]);
                String currentBranch = gitlet.Repository.getLatestIDInHEAD();
                if (splitCommit.equals(otherBranch)) {
                    System.out.println("Given branch is an ancestor of the current branch.");
                    System.exit(0);
                }

                if (splitCommit.equals(currentBranch)) {
                    gitlet.Repository.checkoutBranch(args[1]);
                    System.out.println("Current branch fast-forwarded.");
                    System.exit(0);
                }

                gitlet.Repository.merge(splitCommit, args[1]);

                break;
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
        }
    }

    /**
     * Checks the number of arguments versus the expected number,
     *
     * throws a RuntimeException if they do not match.
     *
     * @param args Argument array from command line
     * @param n Number of expected arguments
     */
    public static void validateNumArgs(String[] args, int n) {
        if (args.length != n) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }
}

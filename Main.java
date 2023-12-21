
package gitlet;
import java.io.File;
import java.io.IOException;


/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ...
     */
    public static void main(String[] args) throws IOException {
        Repository repo= new Repository();
        // TODO: what if args is empty?
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                repo.init();
                // TODO: handle the `init` command
                break;
            case "add":
                String textfilename = args[1];
                repo.add(textfilename);
                // TODO: handle the `add [filename]` command
                break;
            // TODO: FILL THE REST IN
            case "commit":
                String message = args[1];
                if(message.equals("")){
                    System.out.println("Please enter a commit message.");
                    break;
                }
                repo.commit(message);
                // TODO: handle the `add [filename]` command
                break;

            // TODO: FILL THE REST IN
            case "restore":
                if (args[1].equals("--")) {
                    String filename = args[2];
                    repo.restore("", filename);
                } else {
                    String commitID = args[1];
                    String filename = args[3];
                    repo.restore(commitID, filename);
                }
                break;

            case "log":
                repo.log();
                break;

            case "global-log":
                repo.globalLog();
                break;

            case "rm":
                repo.rm(args[1]);
                break;

            case "status":
                repo.status();
                break;

            case "find":
                String commitMessage = args[1];
                repo.find(commitMessage);
                break;
            case "branch":
                String branchName = args[1];
                repo.branch(branchName);
                break;
            case "switch":
                String switchBranchName = args[1];
                repo.switchBranch(switchBranchName);
                break;
            case "rm-branch":
                String rmBranchName = args[1];
                repo.rmBranch(rmBranchName);
                break;

            case "reset":
                String commitID=args[1];
                repo.reset(commitID);
                break;

            default:
                System.out.println("Please enter a command.");

        }

    }

}
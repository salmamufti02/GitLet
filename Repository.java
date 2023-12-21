

package gitlet;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Formatter;


import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Soumya Agarwal and Salma Mufti
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    //String DateFormat = "EEE, d MMM yyyy HH:mm:ss Z";
    public static final  File BlobDir=join(GITLET_DIR,"BLobDir");
    public static final File CommitDir=join(GITLET_DIR,"CommitDir");
    public static final  File StageDir=join(GITLET_DIR,"StageDir");
    public static final File BranchesDir=join(GITLET_DIR, "BranchesDir");
    public static final  File StageAdd = Utils.join(StageDir,"stageAdd");
    public static final  File StageRem = Utils.join(StageDir,"stageRem");
    private HashMap<String, String> stageForAdd = new HashMap<>();
    private HashMap<String, String> stageForRem = new HashMap<>();
    //private static final String currentBranchName = null;
    public static final File currentBranchFile=Utils.join(GITLET_DIR,"currentBranchFile");

    public static final File Head= Utils.join(GITLET_DIR,"Head");


    /* TODO: fill in the rest of this class. */
    public void inititialized() {
        if (!(GITLET_DIR.exists())) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }

    public void init() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");//edge case
            System.exit(0);
        }
        GITLET_DIR.mkdir();
        BlobDir.mkdir();
        CommitDir.mkdir();
        StageDir.mkdir();
        BranchesDir.mkdir();
        //setUpPersistance();

        writeObject(StageAdd,this.stageForAdd);
        Branch branchObj= new Branch("main");//branch obj gives us the name of the current branch

        Utils.writeObject(currentBranchFile,branchObj);
        commit("initial commit");
    }
    //I CHECK IF IT HASN'T BEEN MADE PREVIOUSLY+ HOW DO I CHECK CHANGE IN CONTENTS + WHAT IF WE CHANGE FILENAME
    //want to put this up in persistance


    //Branches? HEre we need to initialize  a master branch
    // and have it point to the initial commit
    //what is UID?
    public void add(String filename) throws IOException {

        File addFile= Utils.join(CWD,filename);
        File BlobFile;


        if(!addFile.exists()){
            System.out.println("File does not exist.");
            System.exit(0);
        }
        String contents= Utils.readContentsAsString(addFile);

        if(!(StageAdd.length()==0)){
            stageForAdd=Utils.readObject(StageAdd, HashMap.class);
        }
        if(!(StageRem.length()==0)){
            stageForRem=Utils.readObject(StageRem, HashMap.class);
        }

        if(stageForAdd.containsKey(filename)) //if names are same
        {
            //overwrites the contents

            Blob BlobObj = new Blob(contents);
            String blobID = BlobObj.getBlobid();
            BlobFile = join(BlobDir, blobID);
            writeObject(BlobFile, BlobObj);
            //create a blob

            stageForAdd.put(filename, blobID); //override
            System.exit(0);
        }

        Commit headCommit= Utils.readObject(Head,Commit.class);
        HashMap<String, String> HeadBlobMap = headCommit.getblobid();
        String Blob_id=HeadBlobMap.get(filename);
        if(HeadBlobMap.containsKey(filename)){
            BlobFile=Utils.join(BlobDir,Blob_id);
            Blob blobobj=Utils.readObject(BlobFile,Blob.class);
            String blob_contents = blobobj.getContents();

            if (contents.equals(blob_contents)) {
                stageForAdd.remove(filename);
                Utils.writeObject(StageAdd,stageForAdd);
                stageForRem.remove(filename);//dont want to do that
                Utils.writeObject(StageRem,stageForRem);
                System.exit(0);
            }
        }


        Blob BlobObj= new Blob(contents);//creates an object blob and saves contents of the file
        String blobID=BlobObj.getBlobid();
        //BlobObj.SerialNumber(blobID);//give serial number to the blob obj
        BlobFile=join(BlobDir,blobID);
        writeObject(BlobFile,BlobObj);
        stageForAdd = Utils.readObject(StageAdd, HashMap.class);
        stageForAdd.put(filename, blobID);//adding to add hash map
        Utils.writeObject(StageAdd, stageForAdd);

    }

    public void commit(String message){

        String parentHash;
        if(message.equals("initial commit")){
            // what if they are evil, they are
            parentHash=null;

        }
        else{
            stageForAdd=readObject(StageAdd, HashMap.class);
            stageForRem=readObject(StageRem, HashMap.class);
            if(stageForAdd.isEmpty()&& stageForRem.isEmpty()){
                System.out.println("No changes added to the commit.");
                System.exit(0);
            }
            //check for changes
            Commit headCommit= Utils.readObject(Head,Commit.class); //error
            parentHash=headCommit.getOwnHash();

        }
        Commit commitObj=new Commit(message,parentHash);
        //Branch branchObj=readObject(currentBranchFile, Branch.class);

        File commitFile=Utils.join(CommitDir, commitObj.getOwnHash());
        //commitFile=new File(commitObj.getOwnHash());
        writeObject(commitFile,commitObj);
        //commitFile=//created a file with name of SHa1
        writeObject(Head,commitObj);

        //serial number of commit object should be the same

        //write to the current branch

        Branch branchObj=readObject(currentBranchFile, Branch.class);
        File currentBranch= Utils.join(BranchesDir,branchObj.getCurrBranchName());
        writeObject(currentBranch,commitObj);

        /**
         //clone parent commit
         //change meta data aka: message and timestamp
         //use staging area to modify the file we're tracking**/

        //HOW DO WE ACCESS OLD PARENT FILES
    }
    public void log(){
        Commit headCommit= Utils.readObject(Head,Commit.class);
        String currentCommitId = headCommit.getOwnHash();
        while (currentCommitId != null) {
            System.out.println("===");
            File commitFile = join(CommitDir, currentCommitId);
            Commit commitObj = readObject(commitFile, Commit.class);
            System.out.println("commit " + currentCommitId);
            System.out.println("Date: " + commitObj.getTimestamp());
            System.out.println(commitObj.getMessage());
            System.out.println();
            currentCommitId = commitObj.getParentHash();
        }
    }

    public void restore(String Commitid, String filename) {
        File filetemp = Utils.join(CWD,filename);
        Boolean exists=false;


        if(Commitid.equals("")){
            Commit headCommit= Utils.readObject(Head,Commit.class);
            HashMap<String, String> HeadBlobMap = headCommit.getblobid();
            for(String i: HeadBlobMap.keySet()){
                if(i==filename){
                    exists= true;
                }
            }

            if(exists){
                System.out.println("File does not exist in that commit.");
                System.exit(0);
            }

            String Blob_id=HeadBlobMap.get(filename);
            File BlobFile=Utils.join(BlobDir,Blob_id);
            Blob blobobj=Utils.readObject(BlobFile,Blob.class);
            String blob_contents = blobobj.getContents();
            filetemp=Utils.join(CWD,filename);
            Utils.writeContents(filetemp,blob_contents);


            /**Takes the version of the file as it exists in the head commit and puts it in the working
             // directory, overwriting the version of the file that’s already there if there is one.
             // The new version of the file is not staged.**/
        }
        else{
            File givenCommitFile=Utils.join(CommitDir,Commitid);
            if(!givenCommitFile.exists()){
                System.out.println("No commit with that id exists.");
                System.exit(0);

            }
            Commit givenCommit= Utils.readObject(givenCommitFile,Commit.class);
            HashMap<String, String> CommitBlobMap = givenCommit.getblobid();

            for(String i: CommitBlobMap.keySet()){
                if(i==filename){
                    exists= true;
                }
            }
            if(exists){
                System.out.println("File does not exist in that commit.");
                System.exit(0);
            }

            String Blob_id=CommitBlobMap.get(filename);
            File BlobFile=Utils.join(BlobDir,Blob_id);
            Blob blobobj=Utils.readObject(BlobFile,Blob.class);
            String blob_contents = blobobj.getContents();
            filetemp=Utils.join(CWD,filename);
            Utils.writeContents(filetemp,blob_contents);

            Commit headCommit = Utils.readObject(Head, Commit.class); //moves current branchs head to commit specified by commit id.
            headCommit.setParentHash(Commitid);
            // Update the current branch's head to the reset commit
            Utils.writeObject(Head, headCommit);

            /**Takes the version of the file as it exists in
             * the head commit and puts it in the working directory, overwriting the version of the file
             * that’s already there if there is one. The new version of the file is not staged.**/
        }
        Commit headCommit = Utils.readObject(Head, Commit.class); //moves current branchs head to commit specified by commit id.
        headCommit.setParentHash(Commitid);
        // Update the current branch's head to the reset commit
        Utils.writeObject(Head, headCommit);

        //CHANGE THE HEAD
    }

    public void globalLog() {
        File[] commitFiles = CommitDir.listFiles();
        if (commitFiles == null) {
            return;
        }
        for (File commitFile : commitFiles) {
            if (!commitFile.isFile()) {
                continue;
            }
            Commit commitObj = Utils.readObject(commitFile, Commit.class);
            printCommitInfo(commitObj);
        }
    }

    public void printCommitInfo(Commit commit) {
        System.out.println("===");
        System.out.println("commit " + commit.getOwnHash());
        System.out.println("Date: " + commit.getTimestamp());
        System.out.println(commit.getMessage());
        System.out.println();
    }

    public void rm(String filename) {
        stageForAdd = Utils.readObject(StageAdd, HashMap.class); //get the map
        Commit headCommit = Utils.readObject(Head, Commit.class);
        HashMap<String, String> HeadBlobMap = headCommit.getblobid();

        if (stageForAdd.containsKey(filename)) //if file is currently staged for Addition
        {
            stageForAdd.remove(filename);
            writeObject(StageAdd, stageForAdd);
        }
        else if (HeadBlobMap.containsKey(filename)) {
            if(!(StageRem.length()==0)){
                stageForRem = Utils.readObject(StageRem, HashMap.class);
            }
            //stageForRem = Utils.readObject(StageRem, HashMap.class);
            stageForRem.put(filename, stageForAdd.get(filename));
            Utils.writeObject(StageRem, stageForRem);
            //stage for removal
            Utils.restrictedDelete(filename);

        }
        else{
            System.out.println("No reason to remove the file.");
    }
       //Unstage the file if it is currently staged for addition. If the file is
        // tracked in the current commit, stage it for removal and remove the file from
        // the working directory if the user has not already done so (do not remove it unless it is tracked
        // in the current commit)

    }
    public void status() {

        /**for (String filename : stageForAdd.keySet()) {
         System.out.println(filename);
         }**/

        //String currentBranch = "main";
        System.out.println("=== Branches ===");
        //String currentBranch = "main";
        System.out.println("*main"); // Mark the current branch with *
        System.out.println();
        System.out.println("=== Staged Files ===");
        stageForAdd = Utils.readObject(StageAdd, HashMap.class); //get the map
        for (String i : Utils.plainFilenamesIn(CWD)) { //lexicographic order
            if (stageForAdd.containsKey(i)) {
                System.out.println(i);
            }
        }
        // Print removed files
        System.out.println("\n=== Removed Files ===");
        if(!(StageRem.length()==0)){
            stageForRem = Utils.readObject(StageRem, HashMap.class); //get the map
            for (String filename : stageForRem.keySet()) {
                System.out.println(filename);
            }
        }
            // Print modifications not staged for commit
        System.out.println("\n=== Modifications Not Staged For Commit ===");
            // Print untracked files
        System.out.println("\n=== Untracked Files ===");

        }

    public void find(String message) {
        boolean found = false;
        for (String commitId : Utils.plainFilenamesIn(CommitDir)) {
            File commitFile = new File(CommitDir, commitId);
            Commit commitObj = readObject(commitFile, Commit.class);
            if (commitObj.getMessage().equals(message)) {
                System.out.println(commitObj.getOwnHash());
                found = true;
            }
        }
        if (!found) {
            System.out.println("Found no commit with that message.");
        }
    }

    public void branch(String branchName) {
        //File branchFile=Utils.join(BranchesDir,branchName);
        File branchFile = Utils.join(BranchesDir, branchName);
        if (branchFile.exists()) {
            System.out.println("A branch with that name already exists.");
        } else {
            Commit headCommitobj = Utils.readObject(Head, Commit.class);
            //String currentCommitId= headCommitobj.getOwnHash();
            Utils.writeObject(branchFile, headCommitobj);

            //String currentCommitId = readContentsAsString(Head);
            //writeContents(branchFile, currentCommitId);
        }
    }

    public void switchBranch (String branchName) {

        File branchFile=Utils.join(BranchesDir,branchName);
        if (!branchFile.exists()) {
            System.out.println("No such branch exists.");
            return;
        }
        Commit branchCommit=Utils.readObject(branchFile,Commit.class); //cannot cast class
        Commit headCommit = Utils.readObject(Head, Commit.class);
        String currentCommitId= headCommit.getOwnHash();
        String branchCommitId= branchCommit.getOwnHash();

        Branch branchobj=Utils.readObject(currentBranchFile,Branch.class);
        branchobj.getCurrBranchName();
        if(branchobj.getCurrBranchName().equals(branchName)){
            System.out.println("No need to switch to the current branch.");
            System.exit(0);
        }

        //if (currentCommitId != null && currentCommitId.equals(branchCommitId)) {
            //System.out.println("No need to switch to the current branch.");
        //}

        //NOT SURE OF WHAT UNTRACKED MEANS
        //Commit headCommit = Utils.readObject(Head, Commit.class);
        HashMap<String, String> HeadMap= headCommit.getblobid();
        HashMap<String, String> BranchMap= branchCommit.getblobid();
        for(String filename: Utils.plainFilenamesIn(CWD)) {
            if (!HeadMap.containsKey(filename)) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                return;
            }
        }
        //restore all files in the given branch
        for(String filename: BranchMap.keySet())
        {
            restore(branchCommitId,filename);
        }

        // all the files in the head of the given branch
        for( String filename: Utils.plainFilenamesIn(CWD)){

            //if there are untracked files in the CWD, print the given line^^ and return.
            if(!BranchMap.containsKey(filename)){
                Utils.restrictedDelete(filename);

            } else {
                String Blob_id = BranchMap.get(filename);
                File BlobFile = Utils.join(BlobDir, Blob_id);
                Blob blobobj = Utils.readObject(BlobFile, Blob.class);
                String blob_contents = blobobj.getContents();
                File filetemp = Utils.join(CWD, filename);
                Utils.writeContents(filetemp, blob_contents);

            }
        }

        //Takes all files in the commit at the head of the given branch,
        // and puts them in the working directory,
        // overwriting the versions of the files that are already there if they exist.
        //changing current branch name
        Branch branchObj=new Branch(branchName);
        writeObject(currentBranchFile,branchObj);

        writeObject(Head,branchCommit);
        //make head the current branch

        stageForAdd.clear();
        Utils.writeObject(StageAdd, stageForAdd);
        // Clear the staging area

    }

    public void rmBranch(String branchName) {
        File branchFile=Utils.join(BranchesDir,branchName);
        if (!branchFile.exists()) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        //Commit branchCommit=Utils.readObject(branchFile,Commit.class);
        //Commit headCommit = Utils.readObject(Head, Commit.class);
        //String currentCommitId= headCommit.getOwnHash();
        //String branchCommitId= branchCommit.getOwnHash();

        Branch branchobj=Utils.readObject(currentBranchFile,Branch.class);
        branchobj.getCurrBranchName();
        if(branchobj.getCurrBranchName().equals(branchName)){
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }

        //Utils.restrictedDelete(branchName);
        branchFile.delete();
    }

    public void reset(String commitId) {
        File commitFile = Utils.join(CommitDir, commitId); //checks if a commit with the given commit id exists. if it doesnt it prints error msg
        if(!commitFile.exists()) {
            System.out.println("No commit with that id exists."); //bc nothing to reset
            return;
        }
        Commit commitObj = Utils.readObject(commitFile, Commit.class); //reads commit obj corresponding to the commit id from the commit dir
        HashMap <String, String > commitBlobMap = commitObj.getblobid(); //gets blob map from commit obj.


        Commit headCommit = Utils.readObject(Head, Commit.class);
        HashMap<String, String> HeadMap= headCommit.getblobid();

        for(String filename: Utils.plainFilenamesIn(CWD)) {
            if (!(HeadMap.containsKey(filename))) {

                String Blob_id=commitBlobMap.get(filename);
                File BlobFile=Utils.join(BlobDir,Blob_id);
                Blob blobobj=Utils.readObject(BlobFile,Blob.class);
                String blob_contents = blobobj.getContents();

                String head_Blob_id=HeadMap.get(filename);
                if(!(head_Blob_id==null)){
                    File head_BlobFile=Utils.join(BlobDir,head_Blob_id);
                    Blob headblobobj=Utils.readObject(head_BlobFile,Blob.class);
                    String head_blob_contents = headblobobj.getContents();

                    if(!(head_blob_contents.equals(blob_contents))){
                        System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                        return;
                    }
                }

            }
        }

        for (String filename : commitBlobMap.keySet()) {   //if there are no untracked files, we restore all files in the given commit
            restore(commitId, filename); //call restore on each filename in the blob map of the commit. restore will replace the files in the wd with the version present in commit
        }

        for (String filename : Utils.plainFilenamesIn(CWD)) { //after restoring all the files we remove tracked files not present in the commit.
            if (!commitBlobMap.containsKey(filename)) { //iterate thro all files in wd & check if theyre present in blob map of the commit.
                Utils.restrictedDelete(filename); // if they arent, means that those files were in prev commit but not in current one so we remove them
            }
        }
        //read current head commit from head file & update its parent hash to given commit id & then write it back to head file
        //Commit headCommit = Utils.readObject(Head, Commit.class); //moves current branchs head to commit specified by commit id.
        //headCommit.setParentHash(commitId);
        // Update the current branch's head to the reset commit
        //Utils.writeObject(Head, headCommit);

        // Clear the staging area
        stageForAdd.clear();
        Utils.writeObject(StageAdd, stageForAdd); //write it back to staging area
    }
}
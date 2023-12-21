package gitlet;


import org.apache.commons.collections.map.HashedMap;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
public class Commit implements Serializable {
    private String message;
    private String ownHash; //stores hash of the commit
    private String parentHash;
    private String timestamp;
    private DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");
    private Map<String, String> blobIDs;
    public Commit(String message, String headHash ) //or parent? as in previous commit
    {
        this.message = message;
        this.parentHash = headHash ;
        Date date = new Date();
        this.timestamp=(dateFormat.format(date));
        this.timestamp = getTimestamp();
        if ("initial commit".equals(this.message)) {
            this.timestamp = "Wed Dec 31 16:00:00 1969 -0800";
        }
        this.blobIDs = new HashMap<>();
        this.blobIDs=Utils.readObject(Repository.StageAdd,HashMap.class);
        Utils.writeObject(Repository.StageAdd,new HashMap<>());
        Utils.writeObject(Repository.StageRem,new HashMap<>());
        // sha1 of the file: sha1 of the blob; file ID: blobID, fileID
        this.ownHash = Utils.sha1(this.timestamp+this.message);

    }

    // generate a unique hash for the commit using its contents
    // and Commit commitObj = new Commit(message);
    //get serial number and returns it //
    //should return sha1 //return sha1(commitObj)
    public String getMessage(){
        return message;
    }
    public String getTimestamp(){
        return this.timestamp;
    }
    public String getParentHash() {
        return parentHash;
    }

    public String getOwnHash() {
        return ownHash;
    }
    public void setParentHash(String parentHash) {
        this.parentHash = parentHash;
    }
    public void addFile(String filename, String blobID) {
        blobIDs.put(filename, blobID);

    }
    public HashMap<String,String> getblobid(){
        return (HashMap<String, String>) blobIDs;
    }
}
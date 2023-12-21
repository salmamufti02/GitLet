package gitlet;

import java.io.Serializable;

public class Branch implements Serializable {
    private String name;

    public Branch(String currname) {
        this.name=currname;
    }


    public String getCurrBranchName(){
        return name;
    }
}

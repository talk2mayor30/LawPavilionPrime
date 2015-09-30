package git.lawpavilionprime.SetUp;

import java.util.ArrayList;

/**
 * Created by don_mayor on 6/24/2015.
 */
public class ModuleItem {



    private String mTitle;
    private String mDate;
    private String mSize;
    private String mStatus;
    private String mUrl;
    private String mName;
    private String mID;

    public ModuleItem(){

    }

    public String getTitle(){ return mTitle; }

    public String getDate(){ return mDate; }

    public String getStatus(){ return mStatus; }

    public String getSize(){ return mSize; }

    public String getUrl(){ return mUrl; }

    public String getID() {
        return mID;
    }


    public String getName() {
        return mName;
    }


    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public void setDate(String mDate) {
        this.mDate = mDate;
    }

    public void setSize(String mSize) {
        this.mSize = mSize;
    }


    public void setStatus(String mStatus) {
        this.mStatus = mStatus;
    }


    public void setUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public void setID(String mID) {
        this.mID = mID;
    }
}

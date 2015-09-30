package notused;

/**
 * Created by don_mayor on 6/24/2015.
 */
public class ModuleItem {



    private String mTitle;
    private String mDate;
    private int mSize;
    private String mStatus;
    private String mUrl;
    private String mName;
    private String mID;

    public ModuleItem(String title, int size, String date, String status, String url){

        this.mTitle = title;
        this.mDate = date;
        this.mSize = size;
        this.mStatus = status;
        this.mUrl = url;
        //this.mID = id;
        //this.mName = name;
    }

    public String getTitle(){ return mTitle; }

    public String getDate(){ return mDate; }

    public String getStatus(){ return mStatus; }

    public int getSize(){ return mSize; }

    public String getUrl(){ return mUrl; }

}

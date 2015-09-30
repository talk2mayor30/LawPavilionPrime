package git.lawpavilionprime.SupremeCourt;

public class SupremeGridItem {

    private String title;
    private String suitNumber;
    private Boolean show;
    private String type;
    //added
    //private int header;


    public SupremeGridItem(String title, String suitNumber, String type, Boolean show) {
        super();
        this.title = title;
        this.suitNumber = suitNumber;
        this.show = show;
        this.type = type;

    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getShow() {
        return show;
    }

    public void setShow(Boolean show) {
        this.show = show;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSuitNumber() {
        return suitNumber;
    }

    public void setSuitNumber(String suitNumber) {
        this.suitNumber = suitNumber;
    }


}

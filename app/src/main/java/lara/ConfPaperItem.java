package lara;

/**
 * Created by don_mayor on 8/21/2015.
 */
public class ConfPaperItem {

    private String speaker;
    private String document_url;
    private String title;

    public ConfPaperItem(String speaker, String title, String document_url){

        this.speaker = speaker;
        this.document_url = document_url;
        this.title = title;
    }


    public String getSpeaker() {
        return speaker;
    }

    public void setSpeaker(String speaker) {
        this.speaker = speaker;
    }

    public String getDocument_url() {
        return document_url;
    }

    public void setDocument_url(String document_url) {
        this.document_url = document_url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

package git.lawpavilionprime.bookflip;

/**
 * Created by don_mayor on 7/23/2015.
 */
public class QueryResult {


    public String title ;
    public String subjectMatter;
    public String year;
    public String description;
    public String issues;

    public QueryResult(int i){

        this.title = i+ " Omolara Adejuwon v. nigerian Navy filed in supreme court of Nigeria on June 2014 " +
                "Omolara Adejuwon v. nigerian Navy filed in supreme court of Nigeria on June 2014 ";

        this.subjectMatter = "Civil Matter Bla Bla Bla"+
                "Whether Omolara is fit to join the Nigerian Navy AND ALSO TO SEE WHETHER " +
                "THIS TEXT FITS TO A LINE OR NOT IN ORDER TO IMPROVE ON USER EXPERIENCE";

        this.description = "In trial order to manage memoryesource and for speed optimization in the app,\" +\n" +
                "                        \" it's better to stage-load cases when the need arises.\" +\n" +
                "                        \" This method returns the requested content and respective page numbers.\\n\" +\n" +
                "                        \"Input params: String court (sc|ca|fhc), \" +\n" +
                "                        \"String suitno, Integer pages (if null, request all pages in ascending order,\" +\n" +
                "                        \" else fetch requested pages which are supplied via an array)--THE END.";

        this.year = "2015";
        this.issues =  "TO FIX ISSUES";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIssue() {
        return issues;
    }

    public void setIssues(String issues) {
        this.issues = issues;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getSubjectMatter() {
        return subjectMatter;
    }

    public void setSubjectMatter(String subjectMatter) {
        this.subjectMatter = subjectMatter;
    }
}

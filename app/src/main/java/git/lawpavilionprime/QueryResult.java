package git.lawpavilionprime;

import java.util.ArrayList;

/**
 * Created by don_mayor on 7/23/2015.
 */
public class QueryResult {


    public String title;
    public String subjectMatter;
    public String year;
    public String description;
    public String issues;

    public QueryResult(String title, String subjectMatter, String Issues,  String description, String year){

        this.title = title;
        this.subjectMatter = subjectMatter;
        this.issues = issues;
        this.description = description;
        this.year = year;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIssues() {
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

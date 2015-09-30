package git.lawpavilionprime.bookflip;

/**
 * Created by don_mayor on 7/17/2015.
 */
public class ListItems {

    String mCaseTitle;
    String mSubjectMatter;
    String mIssues;
    String mBody;
    String mYear;

    public ListItems(String caseTitle, String subjectMatter, String issues, String body, String year) {
        this.mCaseTitle = caseTitle;
        this.mSubjectMatter = subjectMatter;
        this.mIssues = issues;
        this.mBody = body;
        this.mYear = year;
    }

    public String getCaseTitle() {
        return mCaseTitle;
    }

    public void setCaseTitle(String mCaseTitle) {
        this.mCaseTitle = mCaseTitle;
    }

    public String getSubjectMatter() {
        return mSubjectMatter;
    }

    public void setSubjectMatter(String mSubjectMatter) {
        this.mSubjectMatter = mSubjectMatter;
    }


    public String getIssues() {
        return mIssues;
    }

    public void setIssues(String mIssues) {
        this.mIssues = mIssues;
    }

    public String getBody() {
        return mBody;
    }

    public void setBody(String mBody) {
        this.mBody = mBody;
    }

    public String getYear() {
        return mYear;
    }

    public void setYear(String mYear) {
        this.mYear = mYear;
    }
}

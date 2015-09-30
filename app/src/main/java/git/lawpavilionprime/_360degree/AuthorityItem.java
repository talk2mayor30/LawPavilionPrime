package git.lawpavilionprime._360degree;

/**
 * Created by don_mayor on 9/9/2015.
 */
public class AuthorityItem {

    private String authority;
    private boolean isLA;
    private boolean isLC;

    public AuthorityItem(String auth, boolean isLA, boolean isLC){

        this.isLA = isLA;
        this.isLC = isLC;
        this.authority = auth;
    }

    public String getAuthority() {
        return authority;
    }

    public boolean isLA() {
        return isLA;
    }

    public boolean isLC() {
        return isLC;
    }


}

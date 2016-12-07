package net.hockeyapp.android.objects;

public class CrashMetaData {
    private String mUserDescription;
    private String mUserEmail;
    private String mUserID;

    public String getUserDescription() {
        return this.mUserDescription;
    }

    public void setUserDescription(String userDescription) {
        this.mUserDescription = userDescription;
    }

    public String getUserEmail() {
        return this.mUserEmail;
    }

    public void setUserEmail(String userEmail) {
        this.mUserEmail = userEmail;
    }

    public String getUserID() {
        return this.mUserID;
    }

    public void setUserID(String userID) {
        this.mUserID = userID;
    }

    public String toString() {
        return "\n" + CrashMetaData.class.getSimpleName() + "\n" + "userDescription " + this.mUserDescription + "\n" + "userEmail       " + this.mUserEmail + "\n" + "userID          " + this.mUserID;
    }
}

package net.hockeyapp.android.objects;

public class CrashMetaData
{
  private String mUserDescription;
  private String mUserEmail;
  private String mUserID;
  
  public String getUserDescription()
  {
    return this.mUserDescription;
  }
  
  public String getUserEmail()
  {
    return this.mUserEmail;
  }
  
  public String getUserID()
  {
    return this.mUserID;
  }
  
  public void setUserDescription(String paramString)
  {
    this.mUserDescription = paramString;
  }
  
  public void setUserEmail(String paramString)
  {
    this.mUserEmail = paramString;
  }
  
  public void setUserID(String paramString)
  {
    this.mUserID = paramString;
  }
  
  public String toString()
  {
    return "\n" + CrashMetaData.class.getSimpleName() + "\n" + "userDescription " + this.mUserDescription + "\n" + "userEmail       " + this.mUserEmail + "\n" + "userID          " + this.mUserID;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/objects/CrashMetaData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
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
  
  public String toString()
  {
    return "\n" + CrashMetaData.class.getSimpleName() + "\nuserDescription " + this.mUserDescription + "\nuserEmail       " + this.mUserEmail + "\nuserID          " + this.mUserID;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/objects/CrashMetaData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
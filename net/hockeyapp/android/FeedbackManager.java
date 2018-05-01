package net.hockeyapp.android;

import android.content.BroadcastReceiver;
import net.hockeyapp.android.objects.FeedbackUserDataElement;

public class FeedbackManager
{
  private static String identifier;
  private static FeedbackManagerListener lastListener = null;
  private static boolean notificationActive;
  private static BroadcastReceiver receiver = null;
  private static FeedbackUserDataElement requireUserEmail;
  private static FeedbackUserDataElement requireUserName;
  private static String urlString;
  
  static
  {
    notificationActive = false;
    identifier = null;
    urlString = null;
    requireUserName = FeedbackUserDataElement.REQUIRED;
    requireUserEmail = FeedbackUserDataElement.REQUIRED;
  }
  
  public static FeedbackManagerListener getLastListener()
  {
    return lastListener;
  }
  
  public static FeedbackUserDataElement getRequireUserEmail()
  {
    return requireUserEmail;
  }
  
  public static FeedbackUserDataElement getRequireUserName()
  {
    return requireUserName;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/FeedbackManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
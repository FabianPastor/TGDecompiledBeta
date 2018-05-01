package org.telegram.messenger.support.customtabs;

import android.os.Bundle;

public class CustomTabsCallback
{
  public static final int NAVIGATION_ABORTED = 4;
  public static final int NAVIGATION_FAILED = 3;
  public static final int NAVIGATION_FINISHED = 2;
  public static final int NAVIGATION_STARTED = 1;
  public static final int TAB_HIDDEN = 6;
  public static final int TAB_SHOWN = 5;
  
  public void extraCallback(String paramString, Bundle paramBundle) {}
  
  public void onMessageChannelReady(Bundle paramBundle) {}
  
  public void onNavigationEvent(int paramInt, Bundle paramBundle) {}
  
  public void onPostMessage(String paramString, Bundle paramBundle) {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/support/customtabs/CustomTabsCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
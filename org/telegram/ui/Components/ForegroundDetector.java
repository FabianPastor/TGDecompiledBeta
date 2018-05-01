package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.os.Build.VERSION;
import android.os.Bundle;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;

@SuppressLint({"NewApi"})
public class ForegroundDetector
  implements Application.ActivityLifecycleCallbacks
{
  private static ForegroundDetector Instance = null;
  private long enterBackgroundTime = 0L;
  private CopyOnWriteArrayList<Listener> listeners = new CopyOnWriteArrayList();
  private int refs;
  private boolean wasInBackground = true;
  
  public ForegroundDetector(Application paramApplication)
  {
    Instance = this;
    paramApplication.registerActivityLifecycleCallbacks(this);
  }
  
  public static ForegroundDetector getInstance()
  {
    return Instance;
  }
  
  public void addListener(Listener paramListener)
  {
    this.listeners.add(paramListener);
  }
  
  public boolean isBackground()
  {
    if (this.refs == 0) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean isForeground()
  {
    if (this.refs > 0) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean isWasInBackground(boolean paramBoolean)
  {
    if ((paramBoolean) && (Build.VERSION.SDK_INT >= 21) && (System.currentTimeMillis() - this.enterBackgroundTime < 200L)) {
      this.wasInBackground = false;
    }
    return this.wasInBackground;
  }
  
  public void onActivityCreated(Activity paramActivity, Bundle paramBundle) {}
  
  public void onActivityDestroyed(Activity paramActivity) {}
  
  public void onActivityPaused(Activity paramActivity) {}
  
  public void onActivityResumed(Activity paramActivity) {}
  
  public void onActivitySaveInstanceState(Activity paramActivity, Bundle paramBundle) {}
  
  public void onActivityStarted(Activity paramActivity)
  {
    int i = this.refs + 1;
    this.refs = i;
    if (i == 1)
    {
      if (System.currentTimeMillis() - this.enterBackgroundTime < 200L) {
        this.wasInBackground = false;
      }
      if (BuildVars.LOGS_ENABLED) {
        FileLog.d("switch to foreground");
      }
      paramActivity = this.listeners.iterator();
      while (paramActivity.hasNext())
      {
        Listener localListener = (Listener)paramActivity.next();
        try
        {
          localListener.onBecameForeground();
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
      }
    }
  }
  
  public void onActivityStopped(Activity paramActivity)
  {
    int i = this.refs - 1;
    this.refs = i;
    if (i == 0)
    {
      this.enterBackgroundTime = System.currentTimeMillis();
      this.wasInBackground = true;
      if (BuildVars.LOGS_ENABLED) {
        FileLog.d("switch to background");
      }
      paramActivity = this.listeners.iterator();
      while (paramActivity.hasNext())
      {
        Listener localListener = (Listener)paramActivity.next();
        try
        {
          localListener.onBecameBackground();
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
      }
    }
  }
  
  public void removeListener(Listener paramListener)
  {
    this.listeners.remove(paramListener);
  }
  
  public void resetBackgroundVar()
  {
    this.wasInBackground = false;
  }
  
  public static abstract interface Listener
  {
    public abstract void onBecameBackground();
    
    public abstract void onBecameForeground();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/ForegroundDetector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
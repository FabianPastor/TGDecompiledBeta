package org.telegram.messenger.support.customtabs;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.RemoteException;
import android.support.v4.util.ArrayMap;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.Map;

public abstract class CustomTabsService
  extends Service
{
  public static final String ACTION_CUSTOM_TABS_CONNECTION = "android.support.customtabs.action.CustomTabsService";
  public static final String KEY_URL = "android.support.customtabs.otherurls.URL";
  public static final int RESULT_FAILURE_DISALLOWED = -1;
  public static final int RESULT_FAILURE_MESSAGING_ERROR = -3;
  public static final int RESULT_FAILURE_REMOTE_ERROR = -2;
  public static final int RESULT_SUCCESS = 0;
  private ICustomTabsService.Stub mBinder = new ICustomTabsService.Stub()
  {
    public Bundle extraCommand(String paramAnonymousString, Bundle paramAnonymousBundle)
    {
      return CustomTabsService.this.extraCommand(paramAnonymousString, paramAnonymousBundle);
    }
    
    public boolean mayLaunchUrl(ICustomTabsCallback paramAnonymousICustomTabsCallback, Uri paramAnonymousUri, Bundle paramAnonymousBundle, List<Bundle> paramAnonymousList)
    {
      return CustomTabsService.this.mayLaunchUrl(new CustomTabsSessionToken(paramAnonymousICustomTabsCallback), paramAnonymousUri, paramAnonymousBundle, paramAnonymousList);
    }
    
    public boolean newSession(ICustomTabsCallback paramAnonymousICustomTabsCallback)
    {
      bool1 = false;
      CustomTabsSessionToken localCustomTabsSessionToken = new CustomTabsSessionToken(paramAnonymousICustomTabsCallback);
      for (;;)
      {
        try
        {
          local1 = new org/telegram/messenger/support/customtabs/CustomTabsService$1$1;
          local1.<init>(this, localCustomTabsSessionToken);
        }
        catch (RemoteException paramAnonymousICustomTabsCallback)
        {
          IBinder.DeathRecipient local1;
          boolean bool2 = bool1;
          continue;
        }
        synchronized (CustomTabsService.this.mDeathRecipientMap)
        {
          paramAnonymousICustomTabsCallback.asBinder().linkToDeath(local1, 0);
          CustomTabsService.this.mDeathRecipientMap.put(paramAnonymousICustomTabsCallback.asBinder(), local1);
          bool2 = CustomTabsService.this.newSession(localCustomTabsSessionToken);
          return bool2;
        }
      }
    }
    
    public int postMessage(ICustomTabsCallback paramAnonymousICustomTabsCallback, String paramAnonymousString, Bundle paramAnonymousBundle)
    {
      return CustomTabsService.this.postMessage(new CustomTabsSessionToken(paramAnonymousICustomTabsCallback), paramAnonymousString, paramAnonymousBundle);
    }
    
    public boolean requestPostMessageChannel(ICustomTabsCallback paramAnonymousICustomTabsCallback, Uri paramAnonymousUri)
    {
      return CustomTabsService.this.requestPostMessageChannel(new CustomTabsSessionToken(paramAnonymousICustomTabsCallback), paramAnonymousUri);
    }
    
    public boolean updateVisuals(ICustomTabsCallback paramAnonymousICustomTabsCallback, Bundle paramAnonymousBundle)
    {
      return CustomTabsService.this.updateVisuals(new CustomTabsSessionToken(paramAnonymousICustomTabsCallback), paramAnonymousBundle);
    }
    
    public boolean warmup(long paramAnonymousLong)
    {
      return CustomTabsService.this.warmup(paramAnonymousLong);
    }
  };
  private final Map<IBinder, IBinder.DeathRecipient> mDeathRecipientMap = new ArrayMap();
  
  /* Error */
  protected boolean cleanUpSession(CustomTabsSessionToken paramCustomTabsSessionToken)
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore_2
    //   2: aload_0
    //   3: getfield 41	org/telegram/messenger/support/customtabs/CustomTabsService:mDeathRecipientMap	Ljava/util/Map;
    //   6: astore_3
    //   7: aload_3
    //   8: monitorenter
    //   9: aload_1
    //   10: invokevirtual 59	org/telegram/messenger/support/customtabs/CustomTabsSessionToken:getCallbackBinder	()Landroid/os/IBinder;
    //   13: astore_1
    //   14: aload_1
    //   15: aload_0
    //   16: getfield 41	org/telegram/messenger/support/customtabs/CustomTabsService:mDeathRecipientMap	Ljava/util/Map;
    //   19: aload_1
    //   20: invokeinterface 65 2 0
    //   25: checkcast 67	android/os/IBinder$DeathRecipient
    //   28: iconst_0
    //   29: invokeinterface 73 3 0
    //   34: pop
    //   35: aload_0
    //   36: getfield 41	org/telegram/messenger/support/customtabs/CustomTabsService:mDeathRecipientMap	Ljava/util/Map;
    //   39: aload_1
    //   40: invokeinterface 76 2 0
    //   45: pop
    //   46: aload_3
    //   47: monitorexit
    //   48: iconst_1
    //   49: istore_2
    //   50: iload_2
    //   51: ireturn
    //   52: astore_1
    //   53: aload_3
    //   54: monitorexit
    //   55: aload_1
    //   56: athrow
    //   57: astore_1
    //   58: goto -8 -> 50
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	61	0	this	CustomTabsService
    //   0	61	1	paramCustomTabsSessionToken	CustomTabsSessionToken
    //   1	50	2	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   9	48	52	finally
    //   53	55	52	finally
    //   2	9	57	java/util/NoSuchElementException
    //   55	57	57	java/util/NoSuchElementException
  }
  
  protected abstract Bundle extraCommand(String paramString, Bundle paramBundle);
  
  protected abstract boolean mayLaunchUrl(CustomTabsSessionToken paramCustomTabsSessionToken, Uri paramUri, Bundle paramBundle, List<Bundle> paramList);
  
  protected abstract boolean newSession(CustomTabsSessionToken paramCustomTabsSessionToken);
  
  public IBinder onBind(Intent paramIntent)
  {
    return this.mBinder;
  }
  
  protected abstract int postMessage(CustomTabsSessionToken paramCustomTabsSessionToken, String paramString, Bundle paramBundle);
  
  protected abstract boolean requestPostMessageChannel(CustomTabsSessionToken paramCustomTabsSessionToken, Uri paramUri);
  
  protected abstract boolean updateVisuals(CustomTabsSessionToken paramCustomTabsSessionToken, Bundle paramBundle);
  
  protected abstract boolean warmup(long paramLong);
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface Result {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/support/customtabs/CustomTabsService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
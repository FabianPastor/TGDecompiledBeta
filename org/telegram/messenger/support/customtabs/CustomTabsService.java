package org.telegram.messenger.support.customtabs;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.RemoteException;
import android.support.v4.util.ArrayMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public abstract class CustomTabsService
  extends Service
{
  public static final String ACTION_CUSTOM_TABS_CONNECTION = "android.support.customtabs.action.CustomTabsService";
  public static final String KEY_URL = "android.support.customtabs.otherurls.URL";
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
      final CustomTabsSessionToken localCustomTabsSessionToken = new CustomTabsSessionToken(paramAnonymousICustomTabsCallback);
      try
      {
        IBinder.DeathRecipient local1 = new IBinder.DeathRecipient()
        {
          public void binderDied()
          {
            CustomTabsService.this.cleanUpSession(localCustomTabsSessionToken);
          }
        };
        synchronized (CustomTabsService.this.mDeathRecipientMap)
        {
          paramAnonymousICustomTabsCallback.asBinder().linkToDeath(local1, 0);
          CustomTabsService.this.mDeathRecipientMap.put(paramAnonymousICustomTabsCallback.asBinder(), local1);
          boolean bool = CustomTabsService.this.newSession(localCustomTabsSessionToken);
          return bool;
        }
        return false;
      }
      catch (RemoteException paramAnonymousICustomTabsCallback) {}
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
  
  protected boolean cleanUpSession(CustomTabsSessionToken paramCustomTabsSessionToken)
  {
    try
    {
      ??? = this.mDeathRecipientMap;
      synchronized (this.mDeathRecipientMap)
      {
        paramCustomTabsSessionToken = paramCustomTabsSessionToken.getCallbackBinder();
        paramCustomTabsSessionToken.unlinkToDeath((IBinder.DeathRecipient)this.mDeathRecipientMap.get(paramCustomTabsSessionToken), 0);
        this.mDeathRecipientMap.remove(paramCustomTabsSessionToken);
        return true;
      }
      return false;
    }
    catch (NoSuchElementException paramCustomTabsSessionToken) {}
  }
  
  protected abstract Bundle extraCommand(String paramString, Bundle paramBundle);
  
  protected abstract boolean mayLaunchUrl(CustomTabsSessionToken paramCustomTabsSessionToken, Uri paramUri, Bundle paramBundle, List<Bundle> paramList);
  
  protected abstract boolean newSession(CustomTabsSessionToken paramCustomTabsSessionToken);
  
  public IBinder onBind(Intent paramIntent)
  {
    return this.mBinder;
  }
  
  protected abstract boolean updateVisuals(CustomTabsSessionToken paramCustomTabsSessionToken, Bundle paramBundle);
  
  protected abstract boolean warmup(long paramLong);
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/support/customtabs/CustomTabsService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
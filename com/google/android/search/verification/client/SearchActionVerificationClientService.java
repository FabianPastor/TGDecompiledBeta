package com.google.android.search.verification.client;

import android.app.IntentService;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.search.verification.api.ISearchActionVerificationService;
import com.google.android.search.verification.api.ISearchActionVerificationService.Stub;
import java.util.Iterator;
import java.util.Set;

public abstract class SearchActionVerificationClientService
  extends IntentService
{
  private static final int CONNECTION_TIMEOUT_IN_MS = 1000;
  public static final String EXTRA_INTENT = "SearchActionVerificationClientExtraIntent";
  private static final long MS_TO_NS = 1000000L;
  private static final String REMOTE_SERVICE_ACTION = "com.google.android.googlequicksearchbox.SEARCH_ACTION_VERIFICATION_SERVICE";
  private static final String SEARCH_APP_PACKAGE = "com.google.android.googlequicksearchbox";
  private static final String TAG = "SAVerificationClientS";
  private static final String TESTING_APP_PACKAGE = "com.google.verificationdemo.fakeverification";
  private static final int TIME_TO_SLEEP_IN_MS = 50;
  private final boolean DBG = isTestingMode();
  private final long mConnectionTimeout;
  private ISearchActionVerificationService mIRemoteService = null;
  private SearchActionVerificationServiceConnection mSearchActionVerificationServiceConnection;
  private final Intent mServiceIntent = new Intent("com.google.android.googlequicksearchbox.SEARCH_ACTION_VERIFICATION_SERVICE").setPackage("com.google.android.googlequicksearchbox");
  
  public SearchActionVerificationClientService()
  {
    super("SearchActionVerificationClientService");
    if (isTestingMode()) {
      this.mServiceIntent.setPackage("com.google.verificationdemo.fakeverification");
    }
    this.mConnectionTimeout = getConnectionTimeout();
  }
  
  private boolean isConnected()
  {
    if (this.mIRemoteService != null) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private static void logIntentWithExtras(Intent paramIntent)
  {
    Log.d("SAVerificationClientS", "Intent:");
    Log.d("SAVerificationClientS", "\t" + paramIntent);
    Bundle localBundle = paramIntent.getExtras();
    if (localBundle != null)
    {
      Log.d("SAVerificationClientS", "Extras:");
      paramIntent = localBundle.keySet().iterator();
      while (paramIntent.hasNext())
      {
        String str = (String)paramIntent.next();
        Log.d("SAVerificationClientS", String.format("\t%s: %s", new Object[] { str, localBundle.get(str) }));
      }
    }
  }
  
  public long getConnectionTimeout()
  {
    return 1000L;
  }
  
  public boolean isTestingMode()
  {
    return false;
  }
  
  public final void onCreate()
  {
    if (this.DBG) {
      Log.d("SAVerificationClientS", "onCreate");
    }
    super.onCreate();
    this.mSearchActionVerificationServiceConnection = new SearchActionVerificationServiceConnection();
    bindService(this.mServiceIntent, this.mSearchActionVerificationServiceConnection, 1);
  }
  
  public final void onDestroy()
  {
    if (this.DBG) {
      Log.d("SAVerificationClientS", "onDestroy");
    }
    super.onDestroy();
    unbindService(this.mSearchActionVerificationServiceConnection);
  }
  
  protected final void onHandleIntent(Intent paramIntent)
  {
    if (paramIntent == null) {
      if (this.DBG) {
        Log.d("SAVerificationClientS", "Unable to verify null intent");
      }
    }
    for (;;)
    {
      return;
      long l = System.nanoTime();
      while ((!isConnected()) && (System.nanoTime() - l < this.mConnectionTimeout * 1000000L))
      {
        try
        {
          Thread.sleep(50L);
        }
        catch (InterruptedException localInterruptedException) {}
        if (this.DBG) {
          Log.d("SAVerificationClientS", "Unexpected InterruptedException: " + localInterruptedException);
        }
      }
      if (isConnected())
      {
        if (paramIntent.hasExtra("SearchActionVerificationClientExtraIntent"))
        {
          paramIntent = (Intent)paramIntent.getParcelableExtra("SearchActionVerificationClientExtraIntent");
          if (this.DBG) {
            logIntentWithExtras(paramIntent);
          }
          try
          {
            Object localObject = new java/lang/StringBuilder;
            ((StringBuilder)localObject).<init>();
            Log.i("SAVerificationClientS", "API version: " + this.mIRemoteService.getVersion());
            localObject = new android/os/Bundle;
            ((Bundle)localObject).<init>();
            performAction(paramIntent, this.mIRemoteService.isSearchAction(paramIntent, (Bundle)localObject), (Bundle)localObject);
          }
          catch (RemoteException paramIntent)
          {
            Log.e("SAVerificationClientS", "Remote exception: " + paramIntent.getMessage());
          }
        }
        else if (this.DBG)
        {
          Log.d("SAVerificationClientS", "No extra, nothing to check: " + paramIntent);
        }
      }
      else {
        Log.e("SAVerificationClientS", "VerificationService is not connected, unable to check intent: " + paramIntent);
      }
    }
  }
  
  public abstract boolean performAction(Intent paramIntent, boolean paramBoolean, Bundle paramBundle);
  
  class SearchActionVerificationServiceConnection
    implements ServiceConnection
  {
    SearchActionVerificationServiceConnection() {}
    
    public void onServiceConnected(ComponentName paramComponentName, IBinder paramIBinder)
    {
      if (SearchActionVerificationClientService.this.DBG) {
        Log.d("SAVerificationClientS", "onServiceConnected");
      }
      SearchActionVerificationClientService.access$102(SearchActionVerificationClientService.this, ISearchActionVerificationService.Stub.asInterface(paramIBinder));
    }
    
    public void onServiceDisconnected(ComponentName paramComponentName)
    {
      SearchActionVerificationClientService.access$102(SearchActionVerificationClientService.this, null);
      if (SearchActionVerificationClientService.this.DBG) {
        Log.d("SAVerificationClientS", "onServiceDisconnected");
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/search/verification/client/SearchActionVerificationClientService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
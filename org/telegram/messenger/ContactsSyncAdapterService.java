package org.telegram.messenger;

import android.accounts.Account;
import android.accounts.OperationCanceledException;
import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.IBinder;

public class ContactsSyncAdapterService
  extends Service
{
  private static SyncAdapterImpl sSyncAdapter = null;
  
  private SyncAdapterImpl getSyncAdapter()
  {
    if (sSyncAdapter == null) {
      sSyncAdapter = new SyncAdapterImpl(this);
    }
    return sSyncAdapter;
  }
  
  private static void performSync(Context paramContext, Account paramAccount, Bundle paramBundle, String paramString, ContentProviderClient paramContentProviderClient, SyncResult paramSyncResult)
    throws OperationCanceledException
  {
    FileLog.d("telegram", "performSync: " + paramAccount.toString());
  }
  
  public IBinder onBind(Intent paramIntent)
  {
    return getSyncAdapter().getSyncAdapterBinder();
  }
  
  private static class SyncAdapterImpl
    extends AbstractThreadedSyncAdapter
  {
    private Context mContext;
    
    public SyncAdapterImpl(Context paramContext)
    {
      super(true);
      this.mContext = paramContext;
    }
    
    public void onPerformSync(Account paramAccount, Bundle paramBundle, String paramString, ContentProviderClient paramContentProviderClient, SyncResult paramSyncResult)
    {
      try
      {
        ContactsSyncAdapterService.performSync(this.mContext, paramAccount, paramBundle, paramString, paramContentProviderClient, paramSyncResult);
        return;
      }
      catch (OperationCanceledException paramAccount)
      {
        FileLog.e("tmessages", paramAccount);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/ContactsSyncAdapterService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
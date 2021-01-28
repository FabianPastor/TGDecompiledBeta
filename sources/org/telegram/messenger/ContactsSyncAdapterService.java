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

public class ContactsSyncAdapterService extends Service {
    private static SyncAdapterImpl sSyncAdapter;

    private static class SyncAdapterImpl extends AbstractThreadedSyncAdapter {
        private Context mContext;

        public SyncAdapterImpl(Context context) {
            super(context, true);
            this.mContext = context;
        }

        public void onPerformSync(Account account, Bundle bundle, String str, ContentProviderClient contentProviderClient, SyncResult syncResult) {
            try {
                ContactsSyncAdapterService.performSync(this.mContext, account, bundle, str, contentProviderClient, syncResult);
            } catch (OperationCanceledException e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public IBinder onBind(Intent intent) {
        return getSyncAdapter().getSyncAdapterBinder();
    }

    private SyncAdapterImpl getSyncAdapter() {
        if (sSyncAdapter == null) {
            sSyncAdapter = new SyncAdapterImpl(this);
        }
        return sSyncAdapter;
    }

    /* access modifiers changed from: private */
    public static void performSync(Context context, Account account, Bundle bundle, String str, ContentProviderClient contentProviderClient, SyncResult syncResult) throws OperationCanceledException {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("performSync: " + account.toString());
        }
    }
}

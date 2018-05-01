package org.telegram.messenger.support.customtabs;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import org.telegram.messenger.support.customtabs.IPostMessageService.Stub;

public class PostMessageService extends Service {
    private Stub mBinder = new C23211();

    /* renamed from: org.telegram.messenger.support.customtabs.PostMessageService$1 */
    class C23211 extends Stub {
        C23211() {
        }

        public void onMessageChannelReady(ICustomTabsCallback iCustomTabsCallback, Bundle bundle) throws RemoteException {
            iCustomTabsCallback.onMessageChannelReady(bundle);
        }

        public void onPostMessage(ICustomTabsCallback iCustomTabsCallback, String str, Bundle bundle) throws RemoteException {
            iCustomTabsCallback.onPostMessage(str, bundle);
        }
    }

    public IBinder onBind(Intent intent) {
        return this.mBinder;
    }
}

package org.telegram.messenger.support.customtabs;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import org.telegram.messenger.support.customtabs.IPostMessageService.Stub;

public class PostMessageService extends Service {
    private Stub mBinder = new Stub() {
        public void onMessageChannelReady(ICustomTabsCallback callback, Bundle extras) throws RemoteException {
            callback.onMessageChannelReady(extras);
        }

        public void onPostMessage(ICustomTabsCallback callback, String message, Bundle extras) throws RemoteException {
            callback.onPostMessage(message, extras);
        }
    };

    public IBinder onBind(Intent intent) {
        return this.mBinder;
    }
}

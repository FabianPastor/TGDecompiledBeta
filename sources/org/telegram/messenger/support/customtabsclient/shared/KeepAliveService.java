package org.telegram.messenger.support.customtabsclient.shared;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class KeepAliveService extends Service {
    private static final Binder sBinder = new Binder();

    public IBinder onBind(Intent intent) {
        return sBinder;
    }
}

package com.google.firebase.iid;

import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.iid.MessengerCompat;

final class zzp {
    private final Messenger zzifn;
    private final MessengerCompat zznzi;

    zzp(IBinder iBinder) throws RemoteException {
        String interfaceDescriptor = iBinder.getInterfaceDescriptor();
        if ("android.os.IMessenger".equals(interfaceDescriptor)) {
            this.zzifn = new Messenger(iBinder);
            this.zznzi = null;
        } else if ("com.google.android.gms.iid.IMessengerCompat".equals(interfaceDescriptor)) {
            this.zznzi = new MessengerCompat(iBinder);
            this.zzifn = null;
        } else {
            String str = "MessengerIpcClient";
            String str2 = "Invalid interface descriptor: ";
            interfaceDescriptor = String.valueOf(interfaceDescriptor);
            Log.w(str, interfaceDescriptor.length() != 0 ? str2.concat(interfaceDescriptor) : new String(str2));
            throw new RemoteException();
        }
    }

    final void send(Message message) throws RemoteException {
        if (this.zzifn != null) {
            this.zzifn.send(message);
        } else if (this.zznzi != null) {
            this.zznzi.send(message);
        } else {
            throw new IllegalStateException("Both messengers are null");
        }
    }
}

package com.google.android.gms.common.api.internal;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;

final class zzdi extends Handler {
    private /* synthetic */ zzdg zzfvf;

    public final void handleMessage(Message message) {
        switch (message.what) {
            case 0:
                PendingResult pendingResult = (PendingResult) message.obj;
                synchronized (this.zzfvf.zzfou) {
                    if (pendingResult == null) {
                        this.zzfvf.zzfuy.zzd(new Status(13, "Transform returned null"));
                    } else if (pendingResult instanceof zzct) {
                        this.zzfvf.zzfuy.zzd(((zzct) pendingResult).getStatus());
                    } else {
                        this.zzfvf.zzfuy.zza(pendingResult);
                    }
                }
                return;
            case 1:
                RuntimeException runtimeException = (RuntimeException) message.obj;
                String str = "TransformedResultImpl";
                String str2 = "Runtime exception on the transformation worker thread: ";
                String valueOf = String.valueOf(runtimeException.getMessage());
                Log.e(str, valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2));
                throw runtimeException;
            default:
                Log.e("TransformedResultImpl", "TransformationResultHandler received unknown message type: " + message.what);
                return;
        }
    }
}

package com.google.android.gms.internal;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;

final class zzbeu extends Handler {
    private /* synthetic */ zzbes zzaFi;

    public zzbeu(zzbes com_google_android_gms_internal_zzbes, Looper looper) {
        this.zzaFi = com_google_android_gms_internal_zzbes;
        super(looper);
    }

    public final void handleMessage(Message message) {
        switch (message.what) {
            case 0:
                PendingResult pendingResult = (PendingResult) message.obj;
                synchronized (this.zzaFi.zzaBW) {
                    if (pendingResult == null) {
                        this.zzaFi.zzaFb.zzv(new Status(13, "Transform returned null"));
                    } else if (pendingResult instanceof zzbeh) {
                        this.zzaFi.zzaFb.zzv(((zzbeh) pendingResult).getStatus());
                    } else {
                        this.zzaFi.zzaFb.zza(pendingResult);
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

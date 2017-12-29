package com.google.android.gms.common.internal;

import android.app.PendingIntent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;

final class zzh extends Handler {
    private /* synthetic */ zzd zzfza;

    public zzh(zzd com_google_android_gms_common_internal_zzd, Looper looper) {
        this.zzfza = com_google_android_gms_common_internal_zzd;
        super(looper);
    }

    private static void zza(Message message) {
        ((zzi) message.obj).unregister();
    }

    private static boolean zzb(Message message) {
        return message.what == 2 || message.what == 1 || message.what == 7;
    }

    public final void handleMessage(Message message) {
        PendingIntent pendingIntent = null;
        if (this.zzfza.zzfyx.get() != message.arg1) {
            if (zzb(message)) {
                zza(message);
            }
        } else if ((message.what == 1 || message.what == 7 || message.what == 4 || message.what == 5) && !this.zzfza.isConnecting()) {
            zza(message);
        } else if (message.what == 4) {
            this.zzfza.zzfyv = new ConnectionResult(message.arg2);
            if (!this.zzfza.zzakq() || this.zzfza.zzfyw) {
                r0 = this.zzfza.zzfyv != null ? this.zzfza.zzfyv : new ConnectionResult(8);
                this.zzfza.zzfym.zzf(r0);
                this.zzfza.onConnectionFailed(r0);
                return;
            }
            this.zzfza.zza(3, null);
        } else if (message.what == 5) {
            r0 = this.zzfza.zzfyv != null ? this.zzfza.zzfyv : new ConnectionResult(8);
            this.zzfza.zzfym.zzf(r0);
            this.zzfza.onConnectionFailed(r0);
        } else if (message.what == 3) {
            if (message.obj instanceof PendingIntent) {
                pendingIntent = (PendingIntent) message.obj;
            }
            ConnectionResult connectionResult = new ConnectionResult(message.arg2, pendingIntent);
            this.zzfza.zzfym.zzf(connectionResult);
            this.zzfza.onConnectionFailed(connectionResult);
        } else if (message.what == 6) {
            this.zzfza.zza(5, null);
            if (this.zzfza.zzfyr != null) {
                this.zzfza.zzfyr.onConnectionSuspended(message.arg2);
            }
            this.zzfza.onConnectionSuspended(message.arg2);
            this.zzfza.zza(5, 1, null);
        } else if (message.what == 2 && !this.zzfza.isConnected()) {
            zza(message);
        } else if (zzb(message)) {
            ((zzi) message.obj).zzaks();
        } else {
            Log.wtf("GmsClient", "Don't know how to handle message: " + message.what, new Exception());
        }
    }
}

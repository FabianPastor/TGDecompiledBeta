package com.google.android.gms.common.internal;

import android.app.PendingIntent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;

final class zzh extends Handler {
    private /* synthetic */ zzd zzaHe;

    public zzh(zzd com_google_android_gms_common_internal_zzd, Looper looper) {
        this.zzaHe = com_google_android_gms_common_internal_zzd;
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
        if (this.zzaHe.zzaHb.get() != message.arg1) {
            if (zzb(message)) {
                zza(message);
            }
        } else if ((message.what == 1 || message.what == 7 || message.what == 4 || message.what == 5) && !this.zzaHe.isConnecting()) {
            zza(message);
        } else if (message.what == 4) {
            this.zzaHe.zzaGZ = new ConnectionResult(message.arg2);
            if (!this.zzaHe.zzri() || this.zzaHe.zzaHa) {
                r0 = this.zzaHe.zzaGZ != null ? this.zzaHe.zzaGZ : new ConnectionResult(8);
                this.zzaHe.zzaGQ.zzf(r0);
                this.zzaHe.onConnectionFailed(r0);
                return;
            }
            this.zzaHe.zza(3, null);
        } else if (message.what == 5) {
            r0 = this.zzaHe.zzaGZ != null ? this.zzaHe.zzaGZ : new ConnectionResult(8);
            this.zzaHe.zzaGQ.zzf(r0);
            this.zzaHe.onConnectionFailed(r0);
        } else if (message.what == 3) {
            if (message.obj instanceof PendingIntent) {
                pendingIntent = (PendingIntent) message.obj;
            }
            ConnectionResult connectionResult = new ConnectionResult(message.arg2, pendingIntent);
            this.zzaHe.zzaGQ.zzf(connectionResult);
            this.zzaHe.onConnectionFailed(connectionResult);
        } else if (message.what == 6) {
            this.zzaHe.zza(5, null);
            if (this.zzaHe.zzaGV != null) {
                this.zzaHe.zzaGV.onConnectionSuspended(message.arg2);
            }
            this.zzaHe.onConnectionSuspended(message.arg2);
            this.zzaHe.zza(5, 1, null);
        } else if (message.what == 2 && !this.zzaHe.isConnected()) {
            zza(message);
        } else if (zzb(message)) {
            ((zzi) message.obj).zzrk();
        } else {
            Log.wtf("GmsClient", "Don't know how to handle message: " + message.what, new Exception());
        }
    }
}

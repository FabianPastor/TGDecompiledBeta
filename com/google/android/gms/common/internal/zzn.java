package com.google.android.gms.common.internal;

import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;
import android.support.annotation.BinderThread;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;

public final class zzn extends zze {
    private /* synthetic */ zzd zzaHe;
    private IBinder zzaHi;

    @BinderThread
    public zzn(zzd com_google_android_gms_common_internal_zzd, int i, IBinder iBinder, Bundle bundle) {
        this.zzaHe = com_google_android_gms_common_internal_zzd;
        super(com_google_android_gms_common_internal_zzd, i, bundle);
        this.zzaHi = iBinder;
    }

    protected final void zzj(ConnectionResult connectionResult) {
        if (this.zzaHe.zzaGW != null) {
            this.zzaHe.zzaGW.onConnectionFailed(connectionResult);
        }
        this.zzaHe.onConnectionFailed(connectionResult);
    }

    protected final boolean zzrj() {
        try {
            String interfaceDescriptor = this.zzaHi.getInterfaceDescriptor();
            if (this.zzaHe.zzdc().equals(interfaceDescriptor)) {
                IInterface zzd = this.zzaHe.zzd(this.zzaHi);
                if (zzd == null) {
                    return false;
                }
                if (!this.zzaHe.zza(2, 4, zzd) && !this.zzaHe.zza(3, 4, zzd)) {
                    return false;
                }
                this.zzaHe.zzaGZ = null;
                Bundle zzoC = this.zzaHe.zzoC();
                if (this.zzaHe.zzaGV != null) {
                    this.zzaHe.zzaGV.onConnected(zzoC);
                }
                return true;
            }
            String valueOf = String.valueOf(this.zzaHe.zzdc());
            Log.e("GmsClient", new StringBuilder((String.valueOf(valueOf).length() + 34) + String.valueOf(interfaceDescriptor).length()).append("service descriptor mismatch: ").append(valueOf).append(" vs. ").append(interfaceDescriptor).toString());
            return false;
        } catch (RemoteException e) {
            Log.w("GmsClient", "service probably died");
            return false;
        }
    }
}

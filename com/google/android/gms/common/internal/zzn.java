package com.google.android.gms.common.internal;

import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;

public final class zzn extends zze {
    private /* synthetic */ zzd zzfza;
    private IBinder zzfze;

    public zzn(zzd com_google_android_gms_common_internal_zzd, int i, IBinder iBinder, Bundle bundle) {
        this.zzfza = com_google_android_gms_common_internal_zzd;
        super(com_google_android_gms_common_internal_zzd, i, bundle);
        this.zzfze = iBinder;
    }

    protected final boolean zzakr() {
        try {
            String interfaceDescriptor = this.zzfze.getInterfaceDescriptor();
            if (this.zzfza.zzhj().equals(interfaceDescriptor)) {
                IInterface zzd = this.zzfza.zzd(this.zzfze);
                if (zzd == null) {
                    return false;
                }
                if (!this.zzfza.zza(2, 4, zzd) && !this.zzfza.zza(3, 4, zzd)) {
                    return false;
                }
                this.zzfza.zzfyv = null;
                Bundle zzafi = this.zzfza.zzafi();
                if (this.zzfza.zzfyr != null) {
                    this.zzfza.zzfyr.onConnected(zzafi);
                }
                return true;
            }
            String zzhj = this.zzfza.zzhj();
            Log.e("GmsClient", new StringBuilder((String.valueOf(zzhj).length() + 34) + String.valueOf(interfaceDescriptor).length()).append("service descriptor mismatch: ").append(zzhj).append(" vs. ").append(interfaceDescriptor).toString());
            return false;
        } catch (RemoteException e) {
            Log.w("GmsClient", "service probably died");
            return false;
        }
    }

    protected final void zzj(ConnectionResult connectionResult) {
        if (this.zzfza.zzfys != null) {
            this.zzfza.zzfys.onConnectionFailed(connectionResult);
        }
        this.zzfza.onConnectionFailed(connectionResult);
    }
}

package com.google.android.gms.internal;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.dynamic.zze;
import com.google.android.gms.dynamite.DynamiteModule;
import com.google.android.gms.dynamite.descriptors.com.google.android.gms.flags.ModuleDescriptor;
import com.google.android.gms.internal.zzapq.zza;

public class zzapp {
    private zzapq zzaWI = null;
    private boolean zztW = false;

    public void initialize(Context context) {
        Throwable e;
        synchronized (this) {
            if (this.zztW) {
                return;
            }
            try {
                this.zzaWI = zza.asInterface(DynamiteModule.zza(context, DynamiteModule.zzaQw, ModuleDescriptor.MODULE_ID).zzdX("com.google.android.gms.flags.impl.FlagProviderImpl"));
                this.zzaWI.init(zze.zzA(context));
                this.zztW = true;
            } catch (DynamiteModule.zza e2) {
                e = e2;
                Log.w("FlagValueProvider", "Failed to initialize flags module.", e);
            } catch (RemoteException e3) {
                e = e3;
                Log.w("FlagValueProvider", "Failed to initialize flags module.", e);
            }
        }
    }

    public <T> T zzb(zzapn<T> com_google_android_gms_internal_zzapn_T) {
        synchronized (this) {
            if (this.zztW) {
                return com_google_android_gms_internal_zzapn_T.zza(this.zzaWI);
            }
            T zzfm = com_google_android_gms_internal_zzapn_T.zzfm();
            return zzfm;
        }
    }
}

package com.google.android.gms.internal;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.dynamic.zzn;
import com.google.android.gms.dynamite.DynamiteModule;
import com.google.android.gms.dynamite.DynamiteModule.zzc;
import com.google.android.gms.dynamite.descriptors.com.google.android.gms.flags.ModuleDescriptor;

public final class zzcaa {
    private zzcab zzaXG = null;
    private boolean zzuJ = false;

    public final void initialize(Context context) {
        Throwable e;
        synchronized (this) {
            if (this.zzuJ) {
                return;
            }
            try {
                this.zzaXG = zzcac.asInterface(DynamiteModule.zza(context, DynamiteModule.zzaSP, ModuleDescriptor.MODULE_ID).zzcV("com.google.android.gms.flags.impl.FlagProviderImpl"));
                this.zzaXG.init(zzn.zzw(context));
                this.zzuJ = true;
            } catch (zzc e2) {
                e = e2;
                Log.w("FlagValueProvider", "Failed to initialize flags module.", e);
            } catch (RemoteException e3) {
                e = e3;
                Log.w("FlagValueProvider", "Failed to initialize flags module.", e);
            }
        }
    }

    public final <T> T zzb(zzbzt<T> com_google_android_gms_internal_zzbzt_T) {
        synchronized (this) {
            if (this.zzuJ) {
                return com_google_android_gms_internal_zzbzt_T.zza(this.zzaXG);
            }
            T zzdI = com_google_android_gms_internal_zzbzt_T.zzdI();
            return zzdI;
        }
    }
}

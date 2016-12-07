package com.google.android.gms.internal;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.dynamic.zze;
import com.google.android.gms.dynamite.descriptors.com.google.android.gms.flags.ModuleDescriptor;
import com.google.android.gms.internal.zzuz.zza;

public class zzuy {
    private zzuz Uv = null;
    private boolean zzaom = false;

    public void initialize(Context context) {
        Throwable e;
        synchronized (this) {
            if (this.zzaom) {
                return;
            }
            try {
                this.Uv = zza.asInterface(zzsu.zza(context, zzsu.Oy, ModuleDescriptor.MODULE_ID).zzjd("com.google.android.gms.flags.impl.FlagProviderImpl"));
                this.Uv.init(zze.zzac(context));
                this.zzaom = true;
            } catch (zzsu.zza e2) {
                e = e2;
                Log.w("FlagValueProvider", "Failed to initialize flags module.", e);
            } catch (RemoteException e3) {
                e = e3;
                Log.w("FlagValueProvider", "Failed to initialize flags module.", e);
            }
        }
    }

    public <T> T zzb(zzuw<T> com_google_android_gms_internal_zzuw_T) {
        synchronized (this) {
            if (this.zzaom) {
                return com_google_android_gms_internal_zzuw_T.zza(this.Uv);
            }
            T zzkq = com_google_android_gms_internal_zzuw_T.zzkq();
            return zzkq;
        }
    }
}

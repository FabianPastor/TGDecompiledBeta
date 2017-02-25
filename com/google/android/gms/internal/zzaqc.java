package com.google.android.gms.internal;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.dynamic.zzd;
import com.google.android.gms.dynamite.DynamiteModule;
import com.google.android.gms.dynamite.descriptors.com.google.android.gms.flags.ModuleDescriptor;
import com.google.android.gms.internal.zzaqd.zza;

public class zzaqc {
    private zzaqd zzaXk = null;
    private boolean zztZ = false;

    public void initialize(Context context) {
        Throwable e;
        synchronized (this) {
            if (this.zztZ) {
                return;
            }
            try {
                this.zzaXk = zza.asInterface(DynamiteModule.zza(context, DynamiteModule.zzaRY, ModuleDescriptor.MODULE_ID).zzdT("com.google.android.gms.flags.impl.FlagProviderImpl"));
                this.zzaXk.init(zzd.zzA(context));
                this.zztZ = true;
            } catch (DynamiteModule.zza e2) {
                e = e2;
                Log.w("FlagValueProvider", "Failed to initialize flags module.", e);
            } catch (RemoteException e3) {
                e = e3;
                Log.w("FlagValueProvider", "Failed to initialize flags module.", e);
            }
        }
    }

    public <T> T zzb(zzaqa<T> com_google_android_gms_internal_zzaqa_T) {
        synchronized (this) {
            if (this.zztZ) {
                return com_google_android_gms_internal_zzaqa_T.zza(this.zzaXk);
            }
            T zzfr = com_google_android_gms_internal_zzaqa_T.zzfr();
            return zzfr;
        }
    }
}

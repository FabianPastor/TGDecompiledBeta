package com.google.android.gms.internal;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.dynamic.zze;
import com.google.android.gms.dynamite.descriptors.com.google.android.gms.flags.ModuleDescriptor;
import com.google.android.gms.internal.zzvt.zza;

public class zzvs {
    private zzvt WC = null;
    private boolean zzaoz = false;

    public void initialize(Context context) {
        Throwable e;
        synchronized (this) {
            if (this.zzaoz) {
                return;
            }
            try {
                this.WC = zza.asInterface(zztl.zza(context, zztl.Qm, ModuleDescriptor.MODULE_ID).zzjd("com.google.android.gms.flags.impl.FlagProviderImpl"));
                this.WC.init(zze.zzac(context));
                this.zzaoz = true;
            } catch (zztl.zza e2) {
                e = e2;
                Log.w("FlagValueProvider", "Failed to initialize flags module.", e);
            } catch (RemoteException e3) {
                e = e3;
                Log.w("FlagValueProvider", "Failed to initialize flags module.", e);
            }
        }
    }

    public <T> T zzb(zzvq<T> com_google_android_gms_internal_zzvq_T) {
        synchronized (this) {
            if (this.zzaoz) {
                return com_google_android_gms_internal_zzvq_T.zza(this.WC);
            }
            T zzlp = com_google_android_gms_internal_zzvq_T.zzlp();
            return zzlp;
        }
    }
}

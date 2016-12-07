package com.google.android.gms.common.internal;

import android.content.Context;
import android.os.IBinder;
import android.view.View;
import com.google.android.gms.dynamic.zze;
import com.google.android.gms.dynamic.zzg;
import com.google.android.gms.dynamic.zzg.zza;

public final class zzaj extends zzg<zzy> {
    private static final zzaj zzaFl = new zzaj();

    private zzaj() {
        super("com.google.android.gms.common.ui.SignInButtonCreatorImpl");
    }

    public static View zzd(Context context, int i, int i2) throws zza {
        return zzaFl.zze(context, i, i2);
    }

    private View zze(Context context, int i, int i2) throws zza {
        try {
            zzah com_google_android_gms_common_internal_zzah = new zzah(i, i2, null);
            return (View) zze.zzE(((zzy) zzaT(context)).zza(zze.zzA(context), com_google_android_gms_common_internal_zzah));
        } catch (Throwable e) {
            throw new zza("Could not get button with size " + i + " and color " + i2, e);
        }
    }

    public zzy zzby(IBinder iBinder) {
        return zzy.zza.zzbx(iBinder);
    }

    public /* synthetic */ Object zzc(IBinder iBinder) {
        return zzby(iBinder);
    }
}

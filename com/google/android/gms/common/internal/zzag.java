package com.google.android.gms.common.internal;

import android.content.Context;
import android.os.IBinder;
import android.view.View;
import com.google.android.gms.dynamic.zze;
import com.google.android.gms.dynamic.zzg;
import com.google.android.gms.dynamic.zzg.zza;

public final class zzag extends zzg<zzy> {
    private static final zzag Da = new zzag();

    private zzag() {
        super("com.google.android.gms.common.ui.SignInButtonCreatorImpl");
    }

    public static View zzb(Context context, int i, int i2) throws zza {
        return Da.zzc(context, i, i2);
    }

    private View zzc(Context context, int i, int i2) throws zza {
        try {
            SignInButtonConfig signInButtonConfig = new SignInButtonConfig(i, i2, null);
            return (View) zze.zzae(((zzy) zzcu(context)).zza(zze.zzac(context), signInButtonConfig));
        } catch (Throwable e) {
            throw new zza("Could not get button with size " + i + " and color " + i2, e);
        }
    }

    public /* synthetic */ Object zzc(IBinder iBinder) {
        return zzdz(iBinder);
    }

    public zzy zzdz(IBinder iBinder) {
        return zzy.zza.zzdy(iBinder);
    }
}

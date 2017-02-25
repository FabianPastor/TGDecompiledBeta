package com.google.android.gms.flags.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import com.google.android.gms.common.util.DynamiteApi;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.flags.impl.zza.zzb;
import com.google.android.gms.flags.impl.zza.zzc;
import com.google.android.gms.flags.impl.zza.zzd;
import com.google.android.gms.internal.zzaqd.zza;

@DynamiteApi
public class FlagProviderImpl extends zza {
    private SharedPreferences zzBd;
    private boolean zztZ = false;

    public boolean getBooleanFlagValue(String str, boolean z, int i) {
        return !this.zztZ ? z : zza.zza.zza(this.zzBd, str, Boolean.valueOf(z)).booleanValue();
    }

    public int getIntFlagValue(String str, int i, int i2) {
        return !this.zztZ ? i : zzb.zza(this.zzBd, str, Integer.valueOf(i)).intValue();
    }

    public long getLongFlagValue(String str, long j, int i) {
        return !this.zztZ ? j : zzc.zza(this.zzBd, str, Long.valueOf(j)).longValue();
    }

    public String getStringFlagValue(String str, String str2, int i) {
        return !this.zztZ ? str2 : zzd.zza(this.zzBd, str, str2);
    }

    public void init(IObjectWrapper iObjectWrapper) {
        Context context = (Context) com.google.android.gms.dynamic.zzd.zzF(iObjectWrapper);
        if (!this.zztZ) {
            try {
                this.zzBd = zzb.zzn(context.createPackageContext("com.google.android.gms", 0));
                this.zztZ = true;
            } catch (NameNotFoundException e) {
            }
        }
    }
}

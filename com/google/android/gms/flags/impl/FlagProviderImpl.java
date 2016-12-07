package com.google.android.gms.flags.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import com.google.android.gms.common.util.DynamiteApi;
import com.google.android.gms.dynamic.zze;
import com.google.android.gms.flags.impl.zza.zzb;
import com.google.android.gms.flags.impl.zza.zzc;
import com.google.android.gms.flags.impl.zza.zzd;
import com.google.android.gms.internal.zzvt.zza;

@DynamiteApi
public class FlagProviderImpl extends zza {
    private boolean zzaoz = false;
    private SharedPreferences zzbct;

    public boolean getBooleanFlagValue(String str, boolean z, int i) {
        return !this.zzaoz ? z : zza.zza.zza(this.zzbct, str, Boolean.valueOf(z)).booleanValue();
    }

    public int getIntFlagValue(String str, int i, int i2) {
        return !this.zzaoz ? i : zzb.zza(this.zzbct, str, Integer.valueOf(i)).intValue();
    }

    public long getLongFlagValue(String str, long j, int i) {
        return !this.zzaoz ? j : zzc.zza(this.zzbct, str, Long.valueOf(j)).longValue();
    }

    public String getStringFlagValue(String str, String str2, int i) {
        return !this.zzaoz ? str2 : zzd.zza(this.zzbct, str, str2);
    }

    public void init(com.google.android.gms.dynamic.zzd com_google_android_gms_dynamic_zzd) {
        Context context = (Context) zze.zzae(com_google_android_gms_dynamic_zzd);
        if (!this.zzaoz) {
            try {
                this.zzbct = zzb.zzm(context.createPackageContext("com.google.android.gms", 0));
                this.zzaoz = true;
            } catch (NameNotFoundException e) {
            }
        }
    }
}

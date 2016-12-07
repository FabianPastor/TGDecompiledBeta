package com.google.android.gms.flags.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import com.google.android.gms.common.util.DynamiteApi;
import com.google.android.gms.dynamic.zze;
import com.google.android.gms.flags.impl.zza.zzb;
import com.google.android.gms.flags.impl.zza.zzc;
import com.google.android.gms.flags.impl.zza.zzd;
import com.google.android.gms.internal.zzuz.zza;

@DynamiteApi
public class FlagProviderImpl extends zza {
    private boolean zzaom = false;
    private SharedPreferences zzbak;

    public boolean getBooleanFlagValue(String str, boolean z, int i) {
        return !this.zzaom ? z : zza.zza.zza(this.zzbak, str, Boolean.valueOf(z)).booleanValue();
    }

    public int getIntFlagValue(String str, int i, int i2) {
        return !this.zzaom ? i : zzb.zza(this.zzbak, str, Integer.valueOf(i)).intValue();
    }

    public long getLongFlagValue(String str, long j, int i) {
        return !this.zzaom ? j : zzc.zza(this.zzbak, str, Long.valueOf(j)).longValue();
    }

    public String getStringFlagValue(String str, String str2, int i) {
        return !this.zzaom ? str2 : zzd.zza(this.zzbak, str, str2);
    }

    public void init(com.google.android.gms.dynamic.zzd com_google_android_gms_dynamic_zzd) {
        Context context = (Context) zze.zzae(com_google_android_gms_dynamic_zzd);
        if (!this.zzaom) {
            try {
                this.zzbak = zzb.zzn(context.createPackageContext("com.google.android.gms", 0));
                this.zzaom = true;
            } catch (NameNotFoundException e) {
            }
        }
    }
}

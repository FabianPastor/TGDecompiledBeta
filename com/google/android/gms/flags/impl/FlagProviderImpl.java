package com.google.android.gms.flags.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import com.google.android.gms.common.util.DynamiteApi;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.dynamic.zzn;
import com.google.android.gms.internal.zzcad;

@DynamiteApi
public class FlagProviderImpl extends zzcad {
    private SharedPreferences zzBT;
    private boolean zzuH = false;

    public boolean getBooleanFlagValue(String str, boolean z, int i) {
        return !this.zzuH ? z : zzb.zza(this.zzBT, str, Boolean.valueOf(z)).booleanValue();
    }

    public int getIntFlagValue(String str, int i, int i2) {
        return !this.zzuH ? i : zzd.zza(this.zzBT, str, Integer.valueOf(i)).intValue();
    }

    public long getLongFlagValue(String str, long j, int i) {
        return !this.zzuH ? j : zzf.zza(this.zzBT, str, Long.valueOf(j)).longValue();
    }

    public String getStringFlagValue(String str, String str2, int i) {
        return !this.zzuH ? str2 : zzh.zza(this.zzBT, str, str2);
    }

    public void init(IObjectWrapper iObjectWrapper) {
        Context context = (Context) zzn.zzE(iObjectWrapper);
        if (!this.zzuH) {
            try {
                this.zzBT = zzj.zzaW(context.createPackageContext("com.google.android.gms", 0));
                this.zzuH = true;
            } catch (NameNotFoundException e) {
            } catch (Exception e2) {
                String str = "FlagProviderImpl";
                String str2 = "Could not retrieve sdk flags, continuing with defaults: ";
                String valueOf = String.valueOf(e2.getMessage());
                Log.w(str, valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2));
            }
        }
    }
}

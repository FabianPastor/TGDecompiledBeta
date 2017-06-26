package com.google.android.gms.flags.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import com.google.android.gms.common.util.DynamiteApi;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.dynamic.zzn;
import com.google.android.gms.internal.zzcac;

@DynamiteApi
public class FlagProviderImpl extends zzcac {
    private SharedPreferences zzBV;
    private boolean zzuJ = false;

    public boolean getBooleanFlagValue(String str, boolean z, int i) {
        return !this.zzuJ ? z : zzb.zza(this.zzBV, str, Boolean.valueOf(z)).booleanValue();
    }

    public int getIntFlagValue(String str, int i, int i2) {
        return !this.zzuJ ? i : zzd.zza(this.zzBV, str, Integer.valueOf(i)).intValue();
    }

    public long getLongFlagValue(String str, long j, int i) {
        return !this.zzuJ ? j : zzf.zza(this.zzBV, str, Long.valueOf(j)).longValue();
    }

    public String getStringFlagValue(String str, String str2, int i) {
        return !this.zzuJ ? str2 : zzh.zza(this.zzBV, str, str2);
    }

    public void init(IObjectWrapper iObjectWrapper) {
        Context context = (Context) zzn.zzE(iObjectWrapper);
        if (!this.zzuJ) {
            try {
                this.zzBV = zzj.zzaW(context.createPackageContext("com.google.android.gms", 0));
                this.zzuJ = true;
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

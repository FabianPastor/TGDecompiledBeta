package com.google.android.gms.internal;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Binder;
import android.os.Process;
import com.google.android.gms.common.util.zzq;

public final class zzbhe {
    private Context mContext;

    public zzbhe(Context context) {
        this.mContext = context;
    }

    public final int checkCallingOrSelfPermission(String str) {
        return this.mContext.checkCallingOrSelfPermission(str);
    }

    public final ApplicationInfo getApplicationInfo(String str, int i) throws NameNotFoundException {
        return this.mContext.getPackageManager().getApplicationInfo(str, i);
    }

    public final PackageInfo getPackageInfo(String str, int i) throws NameNotFoundException {
        return this.mContext.getPackageManager().getPackageInfo(str, i);
    }

    public final String[] getPackagesForUid(int i) {
        return this.mContext.getPackageManager().getPackagesForUid(i);
    }

    public final boolean zzamu() {
        if (Binder.getCallingUid() == Process.myUid()) {
            return zzbhd.zzcz(this.mContext);
        }
        if (zzq.isAtLeastO()) {
            String nameForUid = this.mContext.getPackageManager().getNameForUid(Binder.getCallingUid());
            if (nameForUid != null) {
                return this.mContext.getPackageManager().isInstantApp(nameForUid);
            }
        }
        return false;
    }

    @TargetApi(19)
    public final boolean zzf(int i, String str) {
        if (zzq.zzaml()) {
            try {
                ((AppOpsManager) this.mContext.getSystemService("appops")).checkPackage(i, str);
                return true;
            } catch (SecurityException e) {
                return false;
            }
        }
        String[] packagesForUid = this.mContext.getPackageManager().getPackagesForUid(i);
        if (str == null || packagesForUid == null) {
            return false;
        }
        for (Object equals : packagesForUid) {
            if (str.equals(equals)) {
                return true;
            }
        }
        return false;
    }

    public final CharSequence zzgt(String str) throws NameNotFoundException {
        return this.mContext.getPackageManager().getApplicationLabel(this.mContext.getPackageManager().getApplicationInfo(str, 0));
    }
}

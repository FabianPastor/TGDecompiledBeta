package com.google.android.gms.common.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.WorkSource;
import android.util.Log;
import com.google.android.gms.internal.zzsz;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class zzy {
    private static final Method GH = zzaza();
    private static final Method GI = zzazb();
    private static final Method GJ = zzazc();
    private static final Method GK = zzazd();
    private static final Method GL = zzaze();

    public static int zza(WorkSource workSource) {
        if (GJ != null) {
            try {
                return ((Integer) GJ.invoke(workSource, new Object[0])).intValue();
            } catch (Throwable e) {
                Log.wtf("WorkSourceUtil", "Unable to assign blame through WorkSource", e);
            }
        }
        return 0;
    }

    public static String zza(WorkSource workSource, int i) {
        if (GL != null) {
            try {
                return (String) GL.invoke(workSource, new Object[]{Integer.valueOf(i)});
            } catch (Throwable e) {
                Log.wtf("WorkSourceUtil", "Unable to assign blame through WorkSource", e);
            }
        }
        return null;
    }

    public static void zza(WorkSource workSource, int i, String str) {
        if (GI != null) {
            if (str == null) {
                str = "";
            }
            try {
                GI.invoke(workSource, new Object[]{Integer.valueOf(i), str});
            } catch (Throwable e) {
                Log.wtf("WorkSourceUtil", "Unable to assign blame through WorkSource", e);
            }
        } else if (GH != null) {
            try {
                GH.invoke(workSource, new Object[]{Integer.valueOf(i)});
            } catch (Throwable e2) {
                Log.wtf("WorkSourceUtil", "Unable to assign blame through WorkSource", e2);
            }
        }
    }

    private static Method zzaza() {
        Method method = null;
        try {
            method = WorkSource.class.getMethod("add", new Class[]{Integer.TYPE});
        } catch (Exception e) {
        }
        return method;
    }

    private static Method zzazb() {
        Method method = null;
        if (zzs.zzayt()) {
            try {
                method = WorkSource.class.getMethod("add", new Class[]{Integer.TYPE, String.class});
            } catch (Exception e) {
            }
        }
        return method;
    }

    private static Method zzazc() {
        Method method = null;
        try {
            method = WorkSource.class.getMethod("size", new Class[0]);
        } catch (Exception e) {
        }
        return method;
    }

    private static Method zzazd() {
        Method method = null;
        try {
            method = WorkSource.class.getMethod("get", new Class[]{Integer.TYPE});
        } catch (Exception e) {
        }
        return method;
    }

    private static Method zzaze() {
        Method method = null;
        if (zzs.zzayt()) {
            try {
                method = WorkSource.class.getMethod("getName", new Class[]{Integer.TYPE});
            } catch (Exception e) {
            }
        }
        return method;
    }

    public static List<String> zzb(WorkSource workSource) {
        int i = 0;
        int zza = workSource == null ? 0 : zza(workSource);
        if (zza == 0) {
            return Collections.EMPTY_LIST;
        }
        List<String> arrayList = new ArrayList();
        while (i < zza) {
            String zza2 = zza(workSource, i);
            if (!zzv.zzij(zza2)) {
                arrayList.add(zza2);
            }
            i++;
        }
        return arrayList;
    }

    public static boolean zzcm(Context context) {
        return (context == null || context.getPackageManager() == null || zzsz.zzco(context).checkPermission("android.permission.UPDATE_DEVICE_STATS", context.getPackageName()) != 0) ? false : true;
    }

    public static WorkSource zzf(int i, String str) {
        WorkSource workSource = new WorkSource();
        zza(workSource, i, str);
        return workSource;
    }

    public static WorkSource zzy(Context context, String str) {
        String str2;
        String str3;
        String valueOf;
        if (context == null || context.getPackageManager() == null) {
            return null;
        }
        try {
            ApplicationInfo applicationInfo = zzsz.zzco(context).getApplicationInfo(str, 0);
            if (applicationInfo != null) {
                return zzf(applicationInfo.uid, str);
            }
            str2 = "WorkSourceUtil";
            str3 = "Could not get applicationInfo from package: ";
            valueOf = String.valueOf(str);
            Log.e(str2, valueOf.length() != 0 ? str3.concat(valueOf) : new String(str3));
            return null;
        } catch (NameNotFoundException e) {
            str2 = "WorkSourceUtil";
            str3 = "Could not find package: ";
            valueOf = String.valueOf(str);
            Log.e(str2, valueOf.length() != 0 ? str3.concat(valueOf) : new String(str3));
            return null;
        }
    }
}

package com.google.android.gms.common.internal;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.internal.zzbha;

public final class zzbd {
    private static boolean zzRk;
    private static String zzaIf;
    private static int zzaIg;
    private static Object zzuF = new Object();

    public static String zzaD(Context context) {
        zzaF(context);
        return zzaIf;
    }

    public static int zzaE(Context context) {
        zzaF(context);
        return zzaIg;
    }

    private static void zzaF(Context context) {
        synchronized (zzuF) {
            if (zzRk) {
                return;
            }
            zzRk = true;
            try {
                Bundle bundle = zzbha.zzaP(context).getApplicationInfo(context.getPackageName(), 128).metaData;
                if (bundle == null) {
                    return;
                }
                zzaIf = bundle.getString("com.google.app.id");
                zzaIg = bundle.getInt("com.google.android.gms.version");
            } catch (Throwable e) {
                Log.wtf("MetadataValueReader", "This should never happen.", e);
            }
        }
    }
}

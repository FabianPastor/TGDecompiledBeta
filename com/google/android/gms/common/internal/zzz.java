package com.google.android.gms.common.internal;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.internal.zzadg;

public class zzz {
    private static boolean zzQm;
    private static String zzaGt;
    private static int zzaGu;
    private static Object zztX = new Object();

    public static String zzaV(Context context) {
        zzaX(context);
        return zzaGt;
    }

    public static int zzaW(Context context) {
        zzaX(context);
        return zzaGu;
    }

    private static void zzaX(Context context) {
        synchronized (zztX) {
            if (zzQm) {
                return;
            }
            zzQm = true;
            try {
                Bundle bundle = zzadg.zzbi(context).getApplicationInfo(context.getPackageName(), 128).metaData;
                if (bundle == null) {
                    return;
                }
                zzaGt = bundle.getString("com.google.app.id");
                zzaGu = bundle.getInt("com.google.android.gms.version");
            } catch (Throwable e) {
                Log.wtf("MetadataValueReader", "This should never happen.", e);
            }
        }
    }
}

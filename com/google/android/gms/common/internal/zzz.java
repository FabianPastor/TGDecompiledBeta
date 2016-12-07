package com.google.android.gms.common.internal;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.internal.zzacx;

public class zzz {
    private static boolean zzPT;
    private static String zzaEW;
    private static int zzaEX;
    private static Object zztU = new Object();

    public static String zzaD(Context context) {
        zzaF(context);
        return zzaEW;
    }

    public static int zzaE(Context context) {
        zzaF(context);
        return zzaEX;
    }

    private static void zzaF(Context context) {
        synchronized (zztU) {
            if (zzPT) {
                return;
            }
            zzPT = true;
            try {
                Bundle bundle = zzacx.zzaQ(context).getApplicationInfo(context.getPackageName(), 128).metaData;
                if (bundle == null) {
                    return;
                }
                zzaEW = bundle.getString("com.google.app.id");
                zzaEX = bundle.getInt("com.google.android.gms.version");
            } catch (Throwable e) {
                Log.wtf("MetadataValueReader", "This should never happen.", e);
            }
        }
    }
}

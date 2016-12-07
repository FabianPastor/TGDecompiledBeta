package com.google.android.gms.common.internal;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.internal.zzsi;

public class zzaa {
    private static String CS;
    private static int CT;
    private static Object zzaok = new Object();
    private static boolean zzcdp;

    public static String zzcg(Context context) {
        zzci(context);
        return CS;
    }

    public static int zzch(Context context) {
        zzci(context);
        return CT;
    }

    private static void zzci(Context context) {
        synchronized (zzaok) {
            if (zzcdp) {
                return;
            }
            zzcdp = true;
            try {
                Bundle bundle = zzsi.zzcr(context).getApplicationInfo(context.getPackageName(), 128).metaData;
                if (bundle == null) {
                    return;
                }
                CS = bundle.getString("com.google.app.id");
                CT = bundle.getInt("com.google.android.gms.version");
            } catch (Throwable e) {
                Log.wtf("MetadataValueReader", "This should never happen.", e);
            }
        }
    }
}

package com.google.android.gms.flags.impl;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.android.gms.internal.zzvv;
import java.util.concurrent.Callable;

public class zzb {
    private static SharedPreferences WM = null;

    class AnonymousClass1 implements Callable<SharedPreferences> {
        final /* synthetic */ Context zzang;

        AnonymousClass1(Context context) {
            this.zzang = context;
        }

        public /* synthetic */ Object call() throws Exception {
            return zzbhi();
        }

        public SharedPreferences zzbhi() {
            return this.zzang.getSharedPreferences("google_sdk_flags", 1);
        }
    }

    public static SharedPreferences zzm(Context context) {
        SharedPreferences sharedPreferences;
        synchronized (SharedPreferences.class) {
            if (WM == null) {
                WM = (SharedPreferences) zzvv.zzb(new AnonymousClass1(context));
            }
            sharedPreferences = WM;
        }
        return sharedPreferences;
    }
}

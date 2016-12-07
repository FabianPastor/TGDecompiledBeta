package com.google.android.gms.flags.impl;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.android.gms.internal.zzvb;
import java.util.concurrent.Callable;

public class zzb {
    private static SharedPreferences UF = null;

    class AnonymousClass1 implements Callable<SharedPreferences> {
        final /* synthetic */ Context zzamt;

        AnonymousClass1(Context context) {
            this.zzamt = context;
        }

        public /* synthetic */ Object call() throws Exception {
            return zzbhq();
        }

        public SharedPreferences zzbhq() {
            return this.zzamt.getSharedPreferences("google_sdk_flags", 1);
        }
    }

    public static SharedPreferences zzn(Context context) {
        SharedPreferences sharedPreferences;
        synchronized (SharedPreferences.class) {
            if (UF == null) {
                UF = (SharedPreferences) zzvb.zzb(new AnonymousClass1(context));
            }
            sharedPreferences = UF;
        }
        return sharedPreferences;
    }
}

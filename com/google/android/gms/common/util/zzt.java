package com.google.android.gms.common.util;

import android.os.Binder;
import android.os.Process;
import android.util.Log;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class zzt {
    private static String EY = null;

    public static String zzaxx() {
        return zzhl(Binder.getCallingPid());
    }

    public static String zzaxy() {
        if (EY == null) {
            EY = zzhl(Process.myPid());
        }
        return EY;
    }

    private static String zzhl(int i) {
        BufferedReader bufferedReader;
        Throwable e;
        Throwable th;
        String str = null;
        try {
            bufferedReader = new BufferedReader(new FileReader("/proc/" + i + "/cmdline"));
            try {
                str = bufferedReader.readLine().trim();
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (Throwable e2) {
                        Log.w("ProcessUtils", e2.getMessage(), e2);
                    }
                }
            } catch (IOException e3) {
                e2 = e3;
                try {
                    Log.e("ProcessUtils", e2.getMessage(), e2);
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        } catch (Throwable e22) {
                            Log.w("ProcessUtils", e22.getMessage(), e22);
                        }
                    }
                    return str;
                } catch (Throwable th2) {
                    th = th2;
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        } catch (Throwable e222) {
                            Log.w("ProcessUtils", e222.getMessage(), e222);
                        }
                    }
                    throw th;
                }
            }
        } catch (IOException e4) {
            e222 = e4;
            bufferedReader = str;
            Log.e("ProcessUtils", e222.getMessage(), e222);
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            return str;
        } catch (Throwable e2222) {
            bufferedReader = str;
            th = e2222;
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            throw th;
        }
        return str;
    }
}

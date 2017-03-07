package com.google.android.gms.common.util;

import android.os.Process;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileReader;
import java.io.IOException;

public class zzu {
    private static String zzaIn = null;
    private static final int zzaIo = Process.myPid();

    static String zzdq(int i) {
        Closeable bufferedReader;
        Throwable th;
        String str = null;
        if (i > 0) {
            ThreadPolicy allowThreadDiskReads;
            try {
                allowThreadDiskReads = StrictMode.allowThreadDiskReads();
                bufferedReader = new BufferedReader(new FileReader("/proc/" + i + "/cmdline"));
                try {
                    StrictMode.setThreadPolicy(allowThreadDiskReads);
                    str = bufferedReader.readLine().trim();
                    zzp.zzb(bufferedReader);
                } catch (IOException e) {
                    zzp.zzb(bufferedReader);
                    return str;
                } catch (Throwable th2) {
                    th = th2;
                    zzp.zzb(bufferedReader);
                    throw th;
                }
            } catch (IOException e2) {
                bufferedReader = str;
                zzp.zzb(bufferedReader);
                return str;
            } catch (Throwable th3) {
                Throwable th4 = th3;
                bufferedReader = str;
                th = th4;
                zzp.zzb(bufferedReader);
                throw th;
            }
        }
        return str;
    }

    public static String zzzq() {
        if (zzaIn == null) {
            zzaIn = zzdq(zzaIo);
        }
        return zzaIn;
    }
}

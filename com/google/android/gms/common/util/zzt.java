package com.google.android.gms.common.util;

import android.os.Process;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileReader;
import java.io.IOException;

public class zzt {
    private static String zzaGZ = null;
    private static final int zzaHa = Process.myPid();

    static String zzdk(int i) {
        ThreadPolicy allowThreadDiskReads;
        Closeable bufferedReader;
        Throwable th;
        String str = null;
        if (i > 0) {
            try {
                allowThreadDiskReads = StrictMode.allowThreadDiskReads();
                bufferedReader = new BufferedReader(new FileReader("/proc/" + i + "/cmdline"));
                try {
                    StrictMode.setThreadPolicy(allowThreadDiskReads);
                    str = bufferedReader.readLine().trim();
                    zzo.zzb(bufferedReader);
                } catch (IOException e) {
                    zzo.zzb(bufferedReader);
                    return str;
                } catch (Throwable th2) {
                    th = th2;
                    zzo.zzb(bufferedReader);
                    throw th;
                }
            } catch (IOException e2) {
                bufferedReader = str;
                zzo.zzb(bufferedReader);
                return str;
            } catch (Throwable th3) {
                Throwable th4 = th3;
                bufferedReader = str;
                th = th4;
                zzo.zzb(bufferedReader);
                throw th;
            }
        }
        return str;
    }

    public static String zzyK() {
        if (zzaGZ == null) {
            zzaGZ = zzdk(zzaHa);
        }
        return zzaGZ;
    }
}

package com.google.android.gms.common.util;

import android.os.Process;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileReader;
import java.io.IOException;

public class zzt {
    private static String GE = null;
    private static final int GF = Process.myPid();

    public static String zzayz() {
        if (GE == null) {
            GE = zzhi(GF);
        }
        return GE;
    }

    static String zzhi(int i) {
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
}

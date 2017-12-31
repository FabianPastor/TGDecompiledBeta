package com.google.android.gms.common.util;

import android.os.Process;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileReader;
import java.io.IOException;

public final class zzs {
    private static String zzget = null;
    private static final int zzgeu = Process.myPid();

    public static String zzamo() {
        if (zzget == null) {
            zzget = zzcj(zzgeu);
        }
        return zzget;
    }

    private static String zzcj(int i) {
        Throwable th;
        String str = null;
        if (i > 0) {
            ThreadPolicy allowThreadDiskReads;
            Closeable bufferedReader;
            try {
                allowThreadDiskReads = StrictMode.allowThreadDiskReads();
                bufferedReader = new BufferedReader(new FileReader("/proc/" + i + "/cmdline"));
                try {
                    StrictMode.setThreadPolicy(allowThreadDiskReads);
                    str = bufferedReader.readLine().trim();
                    zzn.closeQuietly(bufferedReader);
                } catch (IOException e) {
                    zzn.closeQuietly(bufferedReader);
                    return str;
                } catch (Throwable th2) {
                    th = th2;
                    zzn.closeQuietly(bufferedReader);
                    throw th;
                }
            } catch (IOException e2) {
                bufferedReader = str;
                zzn.closeQuietly(bufferedReader);
                return str;
            } catch (Throwable th3) {
                Throwable th4 = th3;
                bufferedReader = str;
                th = th4;
                zzn.closeQuietly(bufferedReader);
                throw th;
            }
        }
        return str;
    }
}

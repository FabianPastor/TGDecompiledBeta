package com.google.android.gms.common.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.Log;
import java.io.File;

public class zzw {
    @TargetApi(21)
    public static File getNoBackupFilesDir(Context context) {
        return zzs.zzyI() ? context.getNoBackupFilesDir() : zze(new File(context.getApplicationInfo().dataDir, "no_backup"));
    }

    private static synchronized File zze(File file) {
        synchronized (zzw.class) {
            if (!(file.exists() || file.mkdirs() || file.exists())) {
                String str = "SupportV4Utils";
                String str2 = "Unable to create no-backup dir ";
                String valueOf = String.valueOf(file.getPath());
                Log.w(str, valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2));
                file = null;
            }
        }
        return file;
    }
}

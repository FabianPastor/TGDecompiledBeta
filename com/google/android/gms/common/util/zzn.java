package com.google.android.gms.common.util;

import java.io.Closeable;
import java.io.IOException;

public final class zzn {
    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
            }
        }
    }
}

package com.google.android.gms.common.util;

public final class zzu {
    private static int zza(StackTraceElement[] stackTraceElementArr, StackTraceElement[] stackTraceElementArr2) {
        int i = 0;
        int length = stackTraceElementArr2.length;
        int length2 = stackTraceElementArr.length;
        while (true) {
            length2--;
            if (length2 < 0) {
                break;
            }
            length--;
            if (length < 0 || !stackTraceElementArr2[length].equals(stackTraceElementArr[length2])) {
                break;
            }
            i++;
        }
        return i;
    }

    public static String zzaxz() {
        int i;
        int zza;
        StringBuilder stringBuilder = new StringBuilder();
        Throwable th = new Throwable();
        StackTraceElement[] stackTrace = th.getStackTrace();
        stringBuilder.append("Async stack trace:");
        for (Object append : stackTrace) {
            stringBuilder.append("\n\tat ").append(append);
        }
        StackTraceElement[] stackTraceElementArr = stackTrace;
        Throwable cause = th.getCause();
        while (cause != null) {
            stringBuilder.append("\nCaused by: ");
            stringBuilder.append(cause);
            StackTraceElement[] stackTrace2 = cause.getStackTrace();
            zza = zza(stackTrace2, stackTraceElementArr);
            for (i = 0; i < stackTrace2.length - zza; i++) {
                String valueOf = String.valueOf(stackTrace2[i]);
                stringBuilder.append(new StringBuilder(String.valueOf(valueOf).length() + 5).append("\n\tat ").append(valueOf).toString());
            }
            if (zza > 0) {
                stringBuilder.append("\n\t... " + zza + " more");
            }
            cause = cause.getCause();
            stackTraceElementArr = stackTrace2;
        }
        return stringBuilder.toString();
    }
}

package org.telegram.messenger;

import android.content.Context;
import android.os.Build;

public class NativeLoader {
    private static final String LIB_NAME = "tmessages.30";
    private static final int LIB_VERSION = 30;
    private static final String SHARED_LIB_NAME = "c++_shared";
    private static volatile boolean nativeLoaded = false;

    private NativeLoader() {
    }

    public static synchronized void initNativeLibs(Context context) {
        synchronized (NativeLoader.class) {
            if (!nativeLoaded) {
                if (Build.VERSION.SDK_INT < 18) {
                    if (!loadNativeLib(context, "c++_shared")) {
                        throw new IllegalStateException("unable to load shared c++ library: c++_shared");
                    }
                }
                if (loadNativeLib(context, "tmessages.30")) {
                    nativeLoaded = true;
                } else {
                    throw new IllegalStateException("unable to load native library: tmessages.30");
                }
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:55:0x00fb A[Catch:{ Error -> 0x002b, all -> 0x0028 }] */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x0113 A[RETURN] */
    @android.annotation.SuppressLint({"UnsafeDynamicallyLoadedCode"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static boolean loadNativeLib(android.content.Context r10, java.lang.String r11) {
        /*
            java.lang.String r0 = "lib"
            java.lang.String r1 = "x86_64"
            java.lang.String r2 = "x86"
            java.lang.String r3 = "armeabi"
            r4 = 0
            r5 = 1
            java.lang.System.loadLibrary(r11)     // Catch:{ Error -> 0x002b }
            boolean r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Error -> 0x002b }
            if (r6 == 0) goto L_0x0027
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Error -> 0x002b }
            r6.<init>()     // Catch:{ Error -> 0x002b }
            java.lang.String r7 = "loaded normal lib: "
            r6.append(r7)     // Catch:{ Error -> 0x002b }
            r6.append(r11)     // Catch:{ Error -> 0x002b }
            java.lang.String r6 = r6.toString()     // Catch:{ Error -> 0x002b }
            org.telegram.messenger.FileLog.d(r6)     // Catch:{ Error -> 0x002b }
        L_0x0027:
            return r5
        L_0x0028:
            r10 = move-exception
            goto L_0x0114
        L_0x002b:
            r6 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r6)     // Catch:{ all -> 0x0028 }
            java.lang.String r6 = android.os.Build.CPU_ABI     // Catch:{ Exception -> 0x008f }
            java.lang.String r6 = android.os.Build.CPU_ABI     // Catch:{ Exception -> 0x008f }
            boolean r6 = r6.equalsIgnoreCase(r1)     // Catch:{ Exception -> 0x008f }
            java.lang.String r7 = "mips"
            java.lang.String r8 = "armeabi-v7a"
            java.lang.String r9 = "arm64-v8a"
            if (r6 == 0) goto L_0x0040
            goto L_0x008d
        L_0x0040:
            java.lang.String r1 = android.os.Build.CPU_ABI     // Catch:{ Exception -> 0x008f }
            boolean r1 = r1.equalsIgnoreCase(r9)     // Catch:{ Exception -> 0x008f }
            if (r1 == 0) goto L_0x004a
            r1 = r9
            goto L_0x008d
        L_0x004a:
            java.lang.String r1 = android.os.Build.CPU_ABI     // Catch:{ Exception -> 0x008f }
            boolean r1 = r1.equalsIgnoreCase(r8)     // Catch:{ Exception -> 0x008f }
            if (r1 == 0) goto L_0x0054
            r1 = r8
            goto L_0x008d
        L_0x0054:
            java.lang.String r1 = android.os.Build.CPU_ABI     // Catch:{ Exception -> 0x008f }
            boolean r1 = r1.equalsIgnoreCase(r3)     // Catch:{ Exception -> 0x008f }
            if (r1 == 0) goto L_0x005e
        L_0x005c:
            r1 = r3
            goto L_0x008d
        L_0x005e:
            java.lang.String r1 = android.os.Build.CPU_ABI     // Catch:{ Exception -> 0x008f }
            boolean r1 = r1.equalsIgnoreCase(r2)     // Catch:{ Exception -> 0x008f }
            if (r1 == 0) goto L_0x0068
            r1 = r2
            goto L_0x008d
        L_0x0068:
            java.lang.String r1 = android.os.Build.CPU_ABI     // Catch:{ Exception -> 0x008f }
            boolean r1 = r1.equalsIgnoreCase(r7)     // Catch:{ Exception -> 0x008f }
            if (r1 == 0) goto L_0x0072
            r1 = r7
            goto L_0x008d
        L_0x0072:
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x008f }
            if (r1 == 0) goto L_0x005c
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x008f }
            r1.<init>()     // Catch:{ Exception -> 0x008f }
            java.lang.String r6 = "Unsupported arch: "
            r1.append(r6)     // Catch:{ Exception -> 0x008f }
            java.lang.String r6 = android.os.Build.CPU_ABI     // Catch:{ Exception -> 0x008f }
            r1.append(r6)     // Catch:{ Exception -> 0x008f }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x008f }
            org.telegram.messenger.FileLog.e((java.lang.String) r1)     // Catch:{ Exception -> 0x008f }
            goto L_0x005c
        L_0x008d:
            r3 = r1
            goto L_0x0093
        L_0x008f:
            r1 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ all -> 0x0028 }
        L_0x0093:
            java.lang.String r1 = "os.arch"
            java.lang.String r1 = java.lang.System.getProperty(r1)     // Catch:{ all -> 0x0028 }
            if (r1 == 0) goto L_0x00a4
            java.lang.String r6 = "686"
            boolean r1 = r1.contains(r6)     // Catch:{ all -> 0x0028 }
            if (r1 == 0) goto L_0x00a4
            goto L_0x00a5
        L_0x00a4:
            r2 = r3
        L_0x00a5:
            java.io.File r1 = new java.io.File     // Catch:{ all -> 0x0028 }
            java.io.File r3 = r10.getFilesDir()     // Catch:{ all -> 0x0028 }
            r1.<init>(r3, r0)     // Catch:{ all -> 0x0028 }
            r1.mkdirs()     // Catch:{ all -> 0x0028 }
            java.io.File r3 = new java.io.File     // Catch:{ all -> 0x0028 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x0028 }
            r6.<init>()     // Catch:{ all -> 0x0028 }
            r6.append(r0)     // Catch:{ all -> 0x0028 }
            r6.append(r11)     // Catch:{ all -> 0x0028 }
            java.lang.String r0 = "loc.so"
            r6.append(r0)     // Catch:{ all -> 0x0028 }
            java.lang.String r0 = r6.toString()     // Catch:{ all -> 0x0028 }
            r3.<init>(r1, r0)     // Catch:{ all -> 0x0028 }
            boolean r0 = r3.exists()     // Catch:{ all -> 0x0028 }
            if (r0 == 0) goto L_0x00f7
            java.lang.String r0 = r3.getAbsolutePath()     // Catch:{ Error -> 0x00f0 }
            java.lang.System.load(r0)     // Catch:{ Error -> 0x00f0 }
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Error -> 0x00f0 }
            if (r0 == 0) goto L_0x00ef
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Error -> 0x00f0 }
            r0.<init>()     // Catch:{ Error -> 0x00f0 }
            java.lang.String r6 = "loaded local lib: "
            r0.append(r6)     // Catch:{ Error -> 0x00f0 }
            r0.append(r11)     // Catch:{ Error -> 0x00f0 }
            java.lang.String r0 = r0.toString()     // Catch:{ Error -> 0x00f0 }
            org.telegram.messenger.FileLog.d(r0)     // Catch:{ Error -> 0x00f0 }
        L_0x00ef:
            return r5
        L_0x00f0:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x0028 }
            r3.delete()     // Catch:{ all -> 0x0028 }
        L_0x00f7:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x0028 }
            if (r0 == 0) goto L_0x010d
            java.util.Locale r0 = java.util.Locale.US     // Catch:{ all -> 0x0028 }
            java.lang.String r6 = "library %s not found, arch = %s"
            r7 = 2
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ all -> 0x0028 }
            r7[r4] = r11     // Catch:{ all -> 0x0028 }
            r7[r5] = r2     // Catch:{ all -> 0x0028 }
            java.lang.String r0 = java.lang.String.format(r0, r6, r7)     // Catch:{ all -> 0x0028 }
            org.telegram.messenger.FileLog.e((java.lang.String) r0)     // Catch:{ all -> 0x0028 }
        L_0x010d:
            boolean r10 = loadFromZip(r10, r1, r3, r2, r11)     // Catch:{ all -> 0x0028 }
            if (r10 == 0) goto L_0x0117
            return r5
        L_0x0114:
            r10.printStackTrace()
        L_0x0117:
            java.lang.System.loadLibrary(r11)     // Catch:{ Error -> 0x0133 }
            boolean r10 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Error -> 0x0133 }
            if (r10 == 0) goto L_0x0132
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Error -> 0x0133 }
            r10.<init>()     // Catch:{ Error -> 0x0133 }
            java.lang.String r0 = "loaded lib: "
            r10.append(r0)     // Catch:{ Error -> 0x0133 }
            r10.append(r11)     // Catch:{ Error -> 0x0133 }
            java.lang.String r10 = r10.toString()     // Catch:{ Error -> 0x0133 }
            org.telegram.messenger.FileLog.d(r10)     // Catch:{ Error -> 0x0133 }
        L_0x0132:
            return r5
        L_0x0133:
            r10 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r10)
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NativeLoader.loadNativeLib(android.content.Context, java.lang.String):boolean");
    }

    /* JADX WARNING: Removed duplicated region for block: B:54:0x00db A[SYNTHETIC, Splitter:B:54:0x00db] */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x00e5 A[SYNTHETIC, Splitter:B:59:0x00e5] */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x00f1 A[SYNTHETIC, Splitter:B:66:0x00f1] */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x00fb A[SYNTHETIC, Splitter:B:71:0x00fb] */
    @android.annotation.SuppressLint({"UnsafeDynamicallyLoadedCode", "SetWorldReadable"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static boolean loadFromZip(android.content.Context r4, java.io.File r5, java.io.File r6, java.lang.String r7, java.lang.String r8) {
        /*
            r0 = 0
            java.io.File[] r5 = r5.listFiles()     // Catch:{ Exception -> 0x0011 }
            int r1 = r5.length     // Catch:{ Exception -> 0x0011 }
            r2 = 0
        L_0x0007:
            if (r2 >= r1) goto L_0x0015
            r3 = r5[r2]     // Catch:{ Exception -> 0x0011 }
            r3.delete()     // Catch:{ Exception -> 0x0011 }
            int r2 = r2 + 1
            goto L_0x0007
        L_0x0011:
            r5 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r5)
        L_0x0015:
            r5 = 0
            java.util.zip.ZipFile r1 = new java.util.zip.ZipFile     // Catch:{ Exception -> 0x00d4, all -> 0x00d1 }
            android.content.pm.ApplicationInfo r4 = r4.getApplicationInfo()     // Catch:{ Exception -> 0x00d4, all -> 0x00d1 }
            java.lang.String r4 = r4.sourceDir     // Catch:{ Exception -> 0x00d4, all -> 0x00d1 }
            r1.<init>(r4)     // Catch:{ Exception -> 0x00d4, all -> 0x00d1 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00cf }
            r4.<init>()     // Catch:{ Exception -> 0x00cf }
            java.lang.String r2 = "lib/"
            r4.append(r2)     // Catch:{ Exception -> 0x00cf }
            r4.append(r7)     // Catch:{ Exception -> 0x00cf }
            java.lang.String r2 = "/lib"
            r4.append(r2)     // Catch:{ Exception -> 0x00cf }
            r4.append(r8)     // Catch:{ Exception -> 0x00cf }
            java.lang.String r2 = ".so"
            r4.append(r2)     // Catch:{ Exception -> 0x00cf }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x00cf }
            java.util.zip.ZipEntry r4 = r1.getEntry(r4)     // Catch:{ Exception -> 0x00cf }
            if (r4 == 0) goto L_0x00b0
            java.io.InputStream r5 = r1.getInputStream(r4)     // Catch:{ Exception -> 0x00cf }
            java.io.FileOutputStream r4 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x00cf }
            r4.<init>(r6)     // Catch:{ Exception -> 0x00cf }
            r7 = 4096(0x1000, float:5.74E-42)
            byte[] r7 = new byte[r7]     // Catch:{ Exception -> 0x00cf }
        L_0x0052:
            int r2 = r5.read(r7)     // Catch:{ Exception -> 0x00cf }
            if (r2 <= 0) goto L_0x005f
            java.lang.Thread.yield()     // Catch:{ Exception -> 0x00cf }
            r4.write(r7, r0, r2)     // Catch:{ Exception -> 0x00cf }
            goto L_0x0052
        L_0x005f:
            r4.close()     // Catch:{ Exception -> 0x00cf }
            r4 = 1
            r6.setReadable(r4, r0)     // Catch:{ Exception -> 0x00cf }
            r6.setExecutable(r4, r0)     // Catch:{ Exception -> 0x00cf }
            r6.setWritable(r4)     // Catch:{ Exception -> 0x00cf }
            java.lang.String r6 = r6.getAbsolutePath()     // Catch:{ Error -> 0x009e }
            java.lang.System.load(r6)     // Catch:{ Error -> 0x009e }
            boolean r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Error -> 0x009e }
            if (r6 == 0) goto L_0x008b
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Error -> 0x009e }
            r6.<init>()     // Catch:{ Error -> 0x009e }
            java.lang.String r7 = "loaded lib from zip: "
            r6.append(r7)     // Catch:{ Error -> 0x009e }
            r6.append(r8)     // Catch:{ Error -> 0x009e }
            java.lang.String r6 = r6.toString()     // Catch:{ Error -> 0x009e }
            org.telegram.messenger.FileLog.d(r6)     // Catch:{ Error -> 0x009e }
        L_0x008b:
            if (r5 == 0) goto L_0x0095
            r5.close()     // Catch:{ Exception -> 0x0091 }
            goto L_0x0095
        L_0x0091:
            r5 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r5)
        L_0x0095:
            r1.close()     // Catch:{ Exception -> 0x0099 }
            goto L_0x009d
        L_0x0099:
            r5 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r5)
        L_0x009d:
            return r4
        L_0x009e:
            r4 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r4)     // Catch:{ Exception -> 0x00cf }
            if (r5 == 0) goto L_0x00ac
            r5.close()     // Catch:{ Exception -> 0x00a8 }
            goto L_0x00ac
        L_0x00a8:
            r4 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r4)
        L_0x00ac:
            r1.close()     // Catch:{ Exception -> 0x00e9 }
            goto L_0x00ed
        L_0x00b0:
            java.lang.Exception r4 = new java.lang.Exception     // Catch:{ Exception -> 0x00cf }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00cf }
            r6.<init>()     // Catch:{ Exception -> 0x00cf }
            java.lang.String r2 = "Unable to find file in apk:lib/"
            r6.append(r2)     // Catch:{ Exception -> 0x00cf }
            r6.append(r7)     // Catch:{ Exception -> 0x00cf }
            java.lang.String r7 = "/"
            r6.append(r7)     // Catch:{ Exception -> 0x00cf }
            r6.append(r8)     // Catch:{ Exception -> 0x00cf }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x00cf }
            r4.<init>(r6)     // Catch:{ Exception -> 0x00cf }
            throw r4     // Catch:{ Exception -> 0x00cf }
        L_0x00cf:
            r4 = move-exception
            goto L_0x00d6
        L_0x00d1:
            r4 = move-exception
            r1 = r5
            goto L_0x00ef
        L_0x00d4:
            r4 = move-exception
            r1 = r5
        L_0x00d6:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r4)     // Catch:{ all -> 0x00ee }
            if (r5 == 0) goto L_0x00e3
            r5.close()     // Catch:{ Exception -> 0x00df }
            goto L_0x00e3
        L_0x00df:
            r4 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r4)
        L_0x00e3:
            if (r1 == 0) goto L_0x00ed
            r1.close()     // Catch:{ Exception -> 0x00e9 }
            goto L_0x00ed
        L_0x00e9:
            r4 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r4)
        L_0x00ed:
            return r0
        L_0x00ee:
            r4 = move-exception
        L_0x00ef:
            if (r5 == 0) goto L_0x00f9
            r5.close()     // Catch:{ Exception -> 0x00f5 }
            goto L_0x00f9
        L_0x00f5:
            r5 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r5)
        L_0x00f9:
            if (r1 == 0) goto L_0x0103
            r1.close()     // Catch:{ Exception -> 0x00ff }
            goto L_0x0103
        L_0x00ff:
            r5 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r5)
        L_0x0103:
            goto L_0x0105
        L_0x0104:
            throw r4
        L_0x0105:
            goto L_0x0104
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NativeLoader.loadFromZip(android.content.Context, java.io.File, java.io.File, java.lang.String, java.lang.String):boolean");
    }
}

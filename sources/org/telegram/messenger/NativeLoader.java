package org.telegram.messenger;

public class NativeLoader {
    private static final String LIB_NAME = "tmessages.30";
    private static final String LIB_SO_NAME = "libtmessages.30.so";
    private static final int LIB_VERSION = 30;
    private static final String LOCALE_LIB_SO_NAME = "libtmessages.30loc.so";
    private static volatile boolean nativeLoaded = false;
    private String crashPath = "";

    private static native void init(String str, boolean z);

    /* JADX WARNING: Removed duplicated region for block: B:11:0x0035 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x0036 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x0022  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.io.File getNativeLibraryDir(android.content.Context r4) {
        /*
            r0 = 0
            if (r4 == 0) goto L_0x001f
            java.io.File r1 = new java.io.File     // Catch:{ all -> 0x001b }
            java.lang.Class<android.content.pm.ApplicationInfo> r2 = android.content.pm.ApplicationInfo.class
            java.lang.String r3 = "nativeLibraryDir"
            java.lang.reflect.Field r2 = r2.getField(r3)     // Catch:{ all -> 0x001b }
            android.content.pm.ApplicationInfo r3 = r4.getApplicationInfo()     // Catch:{ all -> 0x001b }
            java.lang.Object r2 = r2.get(r3)     // Catch:{ all -> 0x001b }
            java.lang.String r2 = (java.lang.String) r2     // Catch:{ all -> 0x001b }
            r1.<init>(r2)     // Catch:{ all -> 0x001b }
            goto L_0x0020
        L_0x001b:
            r1 = move-exception
            r1.printStackTrace()
        L_0x001f:
            r1 = r0
        L_0x0020:
            if (r1 != 0) goto L_0x002f
            java.io.File r1 = new java.io.File
            android.content.pm.ApplicationInfo r4 = r4.getApplicationInfo()
            java.lang.String r4 = r4.dataDir
            java.lang.String r2 = "lib"
            r1.<init>(r4, r2)
        L_0x002f:
            boolean r4 = r1.isDirectory()
            if (r4 == 0) goto L_0x0036
            return r1
        L_0x0036:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NativeLoader.getNativeLibraryDir(android.content.Context):java.io.File");
    }

    /* JADX WARNING: Removed duplicated region for block: B:45:0x00b5 A[SYNTHETIC, Splitter:B:45:0x00b5] */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x00bf A[SYNTHETIC, Splitter:B:50:0x00bf] */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x00cb A[SYNTHETIC, Splitter:B:57:0x00cb] */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x00d5 A[SYNTHETIC, Splitter:B:62:0x00d5] */
    @android.annotation.SuppressLint({"UnsafeDynamicallyLoadedCode", "SetWorldReadable"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static boolean loadFromZip(android.content.Context r5, java.io.File r6, java.io.File r7, java.lang.String r8) {
        /*
            java.lang.String r0 = "/"
            r1 = 0
            java.io.File[] r6 = r6.listFiles()     // Catch:{ Exception -> 0x0013 }
            int r2 = r6.length     // Catch:{ Exception -> 0x0013 }
            r3 = 0
        L_0x0009:
            if (r3 >= r2) goto L_0x0017
            r4 = r6[r3]     // Catch:{ Exception -> 0x0013 }
            r4.delete()     // Catch:{ Exception -> 0x0013 }
            int r3 = r3 + 1
            goto L_0x0009
        L_0x0013:
            r6 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r6)
        L_0x0017:
            r6 = 0
            java.util.zip.ZipFile r2 = new java.util.zip.ZipFile     // Catch:{ Exception -> 0x00ae, all -> 0x00ab }
            android.content.pm.ApplicationInfo r5 = r5.getApplicationInfo()     // Catch:{ Exception -> 0x00ae, all -> 0x00ab }
            java.lang.String r5 = r5.sourceDir     // Catch:{ Exception -> 0x00ae, all -> 0x00ab }
            r2.<init>(r5)     // Catch:{ Exception -> 0x00ae, all -> 0x00ab }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00a9 }
            r5.<init>()     // Catch:{ Exception -> 0x00a9 }
            java.lang.String r3 = "lib/"
            r5.append(r3)     // Catch:{ Exception -> 0x00a9 }
            r5.append(r8)     // Catch:{ Exception -> 0x00a9 }
            r5.append(r0)     // Catch:{ Exception -> 0x00a9 }
            java.lang.String r3 = "libtmessages.30.so"
            r5.append(r3)     // Catch:{ Exception -> 0x00a9 }
            java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x00a9 }
            java.util.zip.ZipEntry r5 = r2.getEntry(r5)     // Catch:{ Exception -> 0x00a9 }
            if (r5 == 0) goto L_0x008a
            java.io.InputStream r6 = r2.getInputStream(r5)     // Catch:{ Exception -> 0x00a9 }
            java.io.FileOutputStream r5 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x00a9 }
            r5.<init>(r7)     // Catch:{ Exception -> 0x00a9 }
            r8 = 4096(0x1000, float:5.74E-42)
            byte[] r8 = new byte[r8]     // Catch:{ Exception -> 0x00a9 }
        L_0x004f:
            int r0 = r6.read(r8)     // Catch:{ Exception -> 0x00a9 }
            if (r0 <= 0) goto L_0x005c
            java.lang.Thread.yield()     // Catch:{ Exception -> 0x00a9 }
            r5.write(r8, r1, r0)     // Catch:{ Exception -> 0x00a9 }
            goto L_0x004f
        L_0x005c:
            r5.close()     // Catch:{ Exception -> 0x00a9 }
            r5 = 1
            r7.setReadable(r5, r1)     // Catch:{ Exception -> 0x00a9 }
            r7.setExecutable(r5, r1)     // Catch:{ Exception -> 0x00a9 }
            r7.setWritable(r5)     // Catch:{ Exception -> 0x00a9 }
            java.lang.String r7 = r7.getAbsolutePath()     // Catch:{ Error -> 0x0073 }
            java.lang.System.load(r7)     // Catch:{ Error -> 0x0073 }
            nativeLoaded = r5     // Catch:{ Error -> 0x0073 }
            goto L_0x0077
        L_0x0073:
            r7 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r7)     // Catch:{ Exception -> 0x00a9 }
        L_0x0077:
            if (r6 == 0) goto L_0x0081
            r6.close()     // Catch:{ Exception -> 0x007d }
            goto L_0x0081
        L_0x007d:
            r6 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r6)
        L_0x0081:
            r2.close()     // Catch:{ Exception -> 0x0085 }
            goto L_0x0089
        L_0x0085:
            r6 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r6)
        L_0x0089:
            return r5
        L_0x008a:
            java.lang.Exception r5 = new java.lang.Exception     // Catch:{ Exception -> 0x00a9 }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00a9 }
            r7.<init>()     // Catch:{ Exception -> 0x00a9 }
            java.lang.String r3 = "Unable to find file in apk:lib/"
            r7.append(r3)     // Catch:{ Exception -> 0x00a9 }
            r7.append(r8)     // Catch:{ Exception -> 0x00a9 }
            r7.append(r0)     // Catch:{ Exception -> 0x00a9 }
            java.lang.String r8 = "tmessages.30"
            r7.append(r8)     // Catch:{ Exception -> 0x00a9 }
            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x00a9 }
            r5.<init>(r7)     // Catch:{ Exception -> 0x00a9 }
            throw r5     // Catch:{ Exception -> 0x00a9 }
        L_0x00a9:
            r5 = move-exception
            goto L_0x00b0
        L_0x00ab:
            r5 = move-exception
            r2 = r6
            goto L_0x00c9
        L_0x00ae:
            r5 = move-exception
            r2 = r6
        L_0x00b0:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r5)     // Catch:{ all -> 0x00c8 }
            if (r6 == 0) goto L_0x00bd
            r6.close()     // Catch:{ Exception -> 0x00b9 }
            goto L_0x00bd
        L_0x00b9:
            r5 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r5)
        L_0x00bd:
            if (r2 == 0) goto L_0x00c7
            r2.close()     // Catch:{ Exception -> 0x00c3 }
            goto L_0x00c7
        L_0x00c3:
            r5 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r5)
        L_0x00c7:
            return r1
        L_0x00c8:
            r5 = move-exception
        L_0x00c9:
            if (r6 == 0) goto L_0x00d3
            r6.close()     // Catch:{ Exception -> 0x00cf }
            goto L_0x00d3
        L_0x00cf:
            r6 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r6)
        L_0x00d3:
            if (r2 == 0) goto L_0x00dd
            r2.close()     // Catch:{ Exception -> 0x00d9 }
            goto L_0x00dd
        L_0x00d9:
            r6 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r6)
        L_0x00dd:
            goto L_0x00df
        L_0x00de:
            throw r5
        L_0x00df:
            goto L_0x00de
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NativeLoader.loadFromZip(android.content.Context, java.io.File, java.io.File, java.lang.String):boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x001b, code lost:
        return;
     */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x00e2 A[Catch:{ all -> 0x001c }] */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x00fc  */
    @android.annotation.SuppressLint({"UnsafeDynamicallyLoadedCode"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static synchronized void initNativeLibs(android.content.Context r7) {
        /*
            java.lang.Class<org.telegram.messenger.NativeLoader> r0 = org.telegram.messenger.NativeLoader.class
            monitor-enter(r0)
            boolean r1 = nativeLoaded     // Catch:{ all -> 0x010f }
            if (r1 == 0) goto L_0x0009
            monitor-exit(r0)
            return
        L_0x0009:
            r1 = 1
            java.lang.String r2 = "tmessages.30"
            java.lang.System.loadLibrary(r2)     // Catch:{ Error -> 0x001f }
            nativeLoaded = r1     // Catch:{ Error -> 0x001f }
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Error -> 0x001f }
            if (r2 == 0) goto L_0x001a
            java.lang.String r2 = "loaded normal lib"
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Error -> 0x001f }
        L_0x001a:
            monitor-exit(r0)
            return
        L_0x001c:
            r7 = move-exception
            goto L_0x00fe
        L_0x001f:
            r2 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ all -> 0x001c }
            java.lang.String r2 = android.os.Build.CPU_ABI     // Catch:{ Exception -> 0x0090 }
            java.lang.String r2 = android.os.Build.CPU_ABI     // Catch:{ Exception -> 0x0090 }
            java.lang.String r3 = "x86_64"
            boolean r2 = r2.equalsIgnoreCase(r3)     // Catch:{ Exception -> 0x0090 }
            if (r2 == 0) goto L_0x0032
            java.lang.String r2 = "x86_64"
            goto L_0x0096
        L_0x0032:
            java.lang.String r2 = android.os.Build.CPU_ABI     // Catch:{ Exception -> 0x0090 }
            java.lang.String r3 = "arm64-v8a"
            boolean r2 = r2.equalsIgnoreCase(r3)     // Catch:{ Exception -> 0x0090 }
            if (r2 == 0) goto L_0x003f
            java.lang.String r2 = "arm64-v8a"
            goto L_0x0096
        L_0x003f:
            java.lang.String r2 = android.os.Build.CPU_ABI     // Catch:{ Exception -> 0x0090 }
            java.lang.String r3 = "armeabi-v7a"
            boolean r2 = r2.equalsIgnoreCase(r3)     // Catch:{ Exception -> 0x0090 }
            if (r2 == 0) goto L_0x004c
            java.lang.String r2 = "armeabi-v7a"
            goto L_0x0096
        L_0x004c:
            java.lang.String r2 = android.os.Build.CPU_ABI     // Catch:{ Exception -> 0x0090 }
            java.lang.String r3 = "armeabi"
            boolean r2 = r2.equalsIgnoreCase(r3)     // Catch:{ Exception -> 0x0090 }
            if (r2 == 0) goto L_0x0059
            java.lang.String r2 = "armeabi"
            goto L_0x0096
        L_0x0059:
            java.lang.String r2 = android.os.Build.CPU_ABI     // Catch:{ Exception -> 0x0090 }
            java.lang.String r3 = "x86"
            boolean r2 = r2.equalsIgnoreCase(r3)     // Catch:{ Exception -> 0x0090 }
            if (r2 == 0) goto L_0x0066
            java.lang.String r2 = "x86"
            goto L_0x0096
        L_0x0066:
            java.lang.String r2 = android.os.Build.CPU_ABI     // Catch:{ Exception -> 0x0090 }
            java.lang.String r3 = "mips"
            boolean r2 = r2.equalsIgnoreCase(r3)     // Catch:{ Exception -> 0x0090 }
            if (r2 == 0) goto L_0x0073
            java.lang.String r2 = "mips"
            goto L_0x0096
        L_0x0073:
            java.lang.String r2 = "armeabi"
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0090 }
            if (r3 == 0) goto L_0x0096
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0090 }
            r3.<init>()     // Catch:{ Exception -> 0x0090 }
            java.lang.String r4 = "Unsupported arch: "
            r3.append(r4)     // Catch:{ Exception -> 0x0090 }
            java.lang.String r4 = android.os.Build.CPU_ABI     // Catch:{ Exception -> 0x0090 }
            r3.append(r4)     // Catch:{ Exception -> 0x0090 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0090 }
            org.telegram.messenger.FileLog.e((java.lang.String) r3)     // Catch:{ Exception -> 0x0090 }
            goto L_0x0096
        L_0x0090:
            r2 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ all -> 0x001c }
            java.lang.String r2 = "armeabi"
        L_0x0096:
            java.lang.String r3 = "os.arch"
            java.lang.String r3 = java.lang.System.getProperty(r3)     // Catch:{ all -> 0x001c }
            if (r3 == 0) goto L_0x00a8
            java.lang.String r4 = "686"
            boolean r3 = r3.contains(r4)     // Catch:{ all -> 0x001c }
            if (r3 == 0) goto L_0x00a8
            java.lang.String r2 = "x86"
        L_0x00a8:
            java.io.File r3 = new java.io.File     // Catch:{ all -> 0x001c }
            java.io.File r4 = r7.getFilesDir()     // Catch:{ all -> 0x001c }
            java.lang.String r5 = "lib"
            r3.<init>(r4, r5)     // Catch:{ all -> 0x001c }
            r3.mkdirs()     // Catch:{ all -> 0x001c }
            java.io.File r4 = new java.io.File     // Catch:{ all -> 0x001c }
            java.lang.String r5 = "libtmessages.30loc.so"
            r4.<init>(r3, r5)     // Catch:{ all -> 0x001c }
            boolean r5 = r4.exists()     // Catch:{ all -> 0x001c }
            if (r5 == 0) goto L_0x00de
            boolean r5 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Error -> 0x00d7 }
            if (r5 == 0) goto L_0x00cc
            java.lang.String r5 = "Load local lib"
            org.telegram.messenger.FileLog.d(r5)     // Catch:{ Error -> 0x00d7 }
        L_0x00cc:
            java.lang.String r5 = r4.getAbsolutePath()     // Catch:{ Error -> 0x00d7 }
            java.lang.System.load(r5)     // Catch:{ Error -> 0x00d7 }
            nativeLoaded = r1     // Catch:{ Error -> 0x00d7 }
            monitor-exit(r0)
            return
        L_0x00d7:
            r5 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r5)     // Catch:{ all -> 0x001c }
            r4.delete()     // Catch:{ all -> 0x001c }
        L_0x00de:
            boolean r5 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x001c }
            if (r5 == 0) goto L_0x00f6
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x001c }
            r5.<init>()     // Catch:{ all -> 0x001c }
            java.lang.String r6 = "Library not found, arch = "
            r5.append(r6)     // Catch:{ all -> 0x001c }
            r5.append(r2)     // Catch:{ all -> 0x001c }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x001c }
            org.telegram.messenger.FileLog.e((java.lang.String) r5)     // Catch:{ all -> 0x001c }
        L_0x00f6:
            boolean r7 = loadFromZip(r7, r3, r4, r2)     // Catch:{ all -> 0x001c }
            if (r7 == 0) goto L_0x0101
            monitor-exit(r0)
            return
        L_0x00fe:
            r7.printStackTrace()     // Catch:{ all -> 0x010f }
        L_0x0101:
            java.lang.String r7 = "tmessages.30"
            java.lang.System.loadLibrary(r7)     // Catch:{ Error -> 0x0109 }
            nativeLoaded = r1     // Catch:{ Error -> 0x0109 }
            goto L_0x010d
        L_0x0109:
            r7 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r7)     // Catch:{ all -> 0x010f }
        L_0x010d:
            monitor-exit(r0)
            return
        L_0x010f:
            r7 = move-exception
            monitor-exit(r0)
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NativeLoader.initNativeLibs(android.content.Context):void");
    }
}

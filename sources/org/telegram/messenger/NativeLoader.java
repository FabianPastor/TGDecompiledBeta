package org.telegram.messenger;

public class NativeLoader {
    private static final String LIB_NAME = "tmessages.40";
    private static final String LIB_SO_NAME = "libtmessages.40.so";
    private static final int LIB_VERSION = 40;
    private static final String LOCALE_LIB_SO_NAME = "libtmessages.40loc.so";
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

    /* JADX WARNING: Removed duplicated region for block: B:44:0x00b4 A[SYNTHETIC, Splitter:B:44:0x00b4] */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x00be A[SYNTHETIC, Splitter:B:49:0x00be] */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x00ca A[SYNTHETIC, Splitter:B:56:0x00ca] */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x00d4 A[SYNTHETIC, Splitter:B:61:0x00d4] */
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
            java.util.zip.ZipFile r2 = new java.util.zip.ZipFile     // Catch:{ Exception -> 0x00ad, all -> 0x00aa }
            android.content.pm.ApplicationInfo r5 = r5.getApplicationInfo()     // Catch:{ Exception -> 0x00ad, all -> 0x00aa }
            java.lang.String r5 = r5.sourceDir     // Catch:{ Exception -> 0x00ad, all -> 0x00aa }
            r2.<init>(r5)     // Catch:{ Exception -> 0x00ad, all -> 0x00aa }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00a8 }
            r5.<init>()     // Catch:{ Exception -> 0x00a8 }
            java.lang.String r3 = "lib/"
            r5.append(r3)     // Catch:{ Exception -> 0x00a8 }
            r5.append(r8)     // Catch:{ Exception -> 0x00a8 }
            r5.append(r0)     // Catch:{ Exception -> 0x00a8 }
            java.lang.String r3 = "libtmessages.40.so"
            r5.append(r3)     // Catch:{ Exception -> 0x00a8 }
            java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x00a8 }
            java.util.zip.ZipEntry r5 = r2.getEntry(r5)     // Catch:{ Exception -> 0x00a8 }
            if (r5 == 0) goto L_0x0088
            java.io.InputStream r6 = r2.getInputStream(r5)     // Catch:{ Exception -> 0x00a8 }
            java.io.FileOutputStream r5 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x00a8 }
            r5.<init>(r7)     // Catch:{ Exception -> 0x00a8 }
            r8 = 4096(0x1000, float:5.74E-42)
            byte[] r8 = new byte[r8]     // Catch:{ Exception -> 0x00a8 }
        L_0x004f:
            int r0 = r6.read(r8)     // Catch:{ Exception -> 0x00a8 }
            if (r0 <= 0) goto L_0x005c
            java.lang.Thread.yield()     // Catch:{ Exception -> 0x00a8 }
            r5.write(r8, r1, r0)     // Catch:{ Exception -> 0x00a8 }
            goto L_0x004f
        L_0x005c:
            r5.close()     // Catch:{ Exception -> 0x00a8 }
            r5 = 1
            r7.setReadable(r5, r1)     // Catch:{ Exception -> 0x00a8 }
            r7.setExecutable(r5, r1)     // Catch:{ Exception -> 0x00a8 }
            r7.setWritable(r5)     // Catch:{ Exception -> 0x00a8 }
            java.lang.String r7 = r7.getAbsolutePath()     // Catch:{ Error -> 0x0073 }
            java.lang.System.load(r7)     // Catch:{ Error -> 0x0073 }
            nativeLoaded = r5     // Catch:{ Error -> 0x0073 }
            goto L_0x0077
        L_0x0073:
            r7 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r7)     // Catch:{ Exception -> 0x00a8 }
        L_0x0077:
            r6.close()     // Catch:{ Exception -> 0x007b }
            goto L_0x007f
        L_0x007b:
            r6 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r6)
        L_0x007f:
            r2.close()     // Catch:{ Exception -> 0x0083 }
            goto L_0x0087
        L_0x0083:
            r6 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r6)
        L_0x0087:
            return r5
        L_0x0088:
            java.lang.Exception r5 = new java.lang.Exception     // Catch:{ Exception -> 0x00a8 }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00a8 }
            r7.<init>()     // Catch:{ Exception -> 0x00a8 }
            java.lang.String r3 = "Unable to find file in apk:lib/"
            r7.append(r3)     // Catch:{ Exception -> 0x00a8 }
            r7.append(r8)     // Catch:{ Exception -> 0x00a8 }
            r7.append(r0)     // Catch:{ Exception -> 0x00a8 }
            java.lang.String r8 = "tmessages.40"
            r7.append(r8)     // Catch:{ Exception -> 0x00a8 }
            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x00a8 }
            r5.<init>(r7)     // Catch:{ Exception -> 0x00a8 }
            throw r5     // Catch:{ Exception -> 0x00a8 }
        L_0x00a8:
            r5 = move-exception
            goto L_0x00af
        L_0x00aa:
            r5 = move-exception
            r2 = r6
            goto L_0x00c8
        L_0x00ad:
            r5 = move-exception
            r2 = r6
        L_0x00af:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r5)     // Catch:{ all -> 0x00c7 }
            if (r6 == 0) goto L_0x00bc
            r6.close()     // Catch:{ Exception -> 0x00b8 }
            goto L_0x00bc
        L_0x00b8:
            r5 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r5)
        L_0x00bc:
            if (r2 == 0) goto L_0x00c6
            r2.close()     // Catch:{ Exception -> 0x00c2 }
            goto L_0x00c6
        L_0x00c2:
            r5 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r5)
        L_0x00c6:
            return r1
        L_0x00c7:
            r5 = move-exception
        L_0x00c8:
            if (r6 == 0) goto L_0x00d2
            r6.close()     // Catch:{ Exception -> 0x00ce }
            goto L_0x00d2
        L_0x00ce:
            r6 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r6)
        L_0x00d2:
            if (r2 == 0) goto L_0x00dc
            r2.close()     // Catch:{ Exception -> 0x00d8 }
            goto L_0x00dc
        L_0x00d8:
            r6 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r6)
        L_0x00dc:
            goto L_0x00de
        L_0x00dd:
            throw r5
        L_0x00de:
            goto L_0x00dd
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NativeLoader.loadFromZip(android.content.Context, java.io.File, java.io.File, java.lang.String):boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x001c, code lost:
        return;
     */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x00db A[Catch:{ all -> 0x001d }] */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x00f5  */
    @android.annotation.SuppressLint({"UnsafeDynamicallyLoadedCode"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static synchronized void initNativeLibs(android.content.Context r7) {
        /*
            java.lang.Class<org.telegram.messenger.NativeLoader> r0 = org.telegram.messenger.NativeLoader.class
            monitor-enter(r0)
            boolean r1 = nativeLoaded     // Catch:{ all -> 0x0109 }
            if (r1 == 0) goto L_0x0009
            monitor-exit(r0)
            return
        L_0x0009:
            r1 = 1
            java.lang.String r2 = "tmessages.40"
            java.lang.System.loadLibrary(r2)     // Catch:{ Error -> 0x0020 }
            nativeLoaded = r1     // Catch:{ Error -> 0x0020 }
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Error -> 0x0020 }
            if (r2 == 0) goto L_0x001b
            java.lang.String r2 = "loaded normal lib"
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Error -> 0x0020 }
        L_0x001b:
            monitor-exit(r0)
            return
        L_0x001d:
            r7 = move-exception
            goto L_0x00f7
        L_0x0020:
            r2 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ all -> 0x001d }
            java.lang.String r2 = android.os.Build.CPU_ABI     // Catch:{ Exception -> 0x0088 }
            java.lang.String r3 = "x86_64"
            boolean r3 = r2.equalsIgnoreCase(r3)     // Catch:{ Exception -> 0x0088 }
            if (r3 == 0) goto L_0x0033
            java.lang.String r2 = "x86_64"
            goto L_0x008e
        L_0x0033:
            java.lang.String r3 = "arm64-v8a"
            boolean r3 = r2.equalsIgnoreCase(r3)     // Catch:{ Exception -> 0x0088 }
            if (r3 == 0) goto L_0x003e
            java.lang.String r2 = "arm64-v8a"
            goto L_0x008e
        L_0x003e:
            java.lang.String r3 = "armeabi-v7a"
            boolean r3 = r2.equalsIgnoreCase(r3)     // Catch:{ Exception -> 0x0088 }
            if (r3 == 0) goto L_0x0049
            java.lang.String r2 = "armeabi-v7a"
            goto L_0x008e
        L_0x0049:
            java.lang.String r3 = "armeabi"
            boolean r3 = r2.equalsIgnoreCase(r3)     // Catch:{ Exception -> 0x0088 }
            if (r3 == 0) goto L_0x0054
            java.lang.String r2 = "armeabi"
            goto L_0x008e
        L_0x0054:
            java.lang.String r3 = "x86"
            boolean r3 = r2.equalsIgnoreCase(r3)     // Catch:{ Exception -> 0x0088 }
            if (r3 == 0) goto L_0x0061
            java.lang.String r2 = "x86"
            goto L_0x008e
        L_0x0061:
            java.lang.String r3 = "mips"
            boolean r3 = r2.equalsIgnoreCase(r3)     // Catch:{ Exception -> 0x0088 }
            if (r3 == 0) goto L_0x006c
            java.lang.String r2 = "mips"
            goto L_0x008e
        L_0x006c:
            java.lang.String r3 = "armeabi"
            boolean r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0088 }
            if (r4 == 0) goto L_0x0086
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0088 }
            r4.<init>()     // Catch:{ Exception -> 0x0088 }
            java.lang.String r5 = "Unsupported arch: "
            r4.append(r5)     // Catch:{ Exception -> 0x0088 }
            r4.append(r2)     // Catch:{ Exception -> 0x0088 }
            java.lang.String r2 = r4.toString()     // Catch:{ Exception -> 0x0088 }
            org.telegram.messenger.FileLog.e((java.lang.String) r2)     // Catch:{ Exception -> 0x0088 }
        L_0x0086:
            r2 = r3
            goto L_0x008e
        L_0x0088:
            r2 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ all -> 0x001d }
            java.lang.String r2 = "armeabi"
        L_0x008e:
            java.lang.String r3 = "os.arch"
            java.lang.String r3 = java.lang.System.getProperty(r3)     // Catch:{ all -> 0x001d }
            if (r3 == 0) goto L_0x00a1
            java.lang.String r4 = "686"
            boolean r3 = r3.contains(r4)     // Catch:{ all -> 0x001d }
            if (r3 == 0) goto L_0x00a1
            java.lang.String r2 = "x86"
        L_0x00a1:
            java.io.File r3 = new java.io.File     // Catch:{ all -> 0x001d }
            java.io.File r4 = r7.getFilesDir()     // Catch:{ all -> 0x001d }
            java.lang.String r5 = "lib"
            r3.<init>(r4, r5)     // Catch:{ all -> 0x001d }
            r3.mkdirs()     // Catch:{ all -> 0x001d }
            java.io.File r4 = new java.io.File     // Catch:{ all -> 0x001d }
            java.lang.String r5 = "libtmessages.40loc.so"
            r4.<init>(r3, r5)     // Catch:{ all -> 0x001d }
            boolean r5 = r4.exists()     // Catch:{ all -> 0x001d }
            if (r5 == 0) goto L_0x00d7
            boolean r5 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Error -> 0x00d0 }
            if (r5 == 0) goto L_0x00c5
            java.lang.String r5 = "Load local lib"
            org.telegram.messenger.FileLog.d(r5)     // Catch:{ Error -> 0x00d0 }
        L_0x00c5:
            java.lang.String r5 = r4.getAbsolutePath()     // Catch:{ Error -> 0x00d0 }
            java.lang.System.load(r5)     // Catch:{ Error -> 0x00d0 }
            nativeLoaded = r1     // Catch:{ Error -> 0x00d0 }
            monitor-exit(r0)
            return
        L_0x00d0:
            r5 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r5)     // Catch:{ all -> 0x001d }
            r4.delete()     // Catch:{ all -> 0x001d }
        L_0x00d7:
            boolean r5 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x001d }
            if (r5 == 0) goto L_0x00ef
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x001d }
            r5.<init>()     // Catch:{ all -> 0x001d }
            java.lang.String r6 = "Library not found, arch = "
            r5.append(r6)     // Catch:{ all -> 0x001d }
            r5.append(r2)     // Catch:{ all -> 0x001d }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x001d }
            org.telegram.messenger.FileLog.e((java.lang.String) r5)     // Catch:{ all -> 0x001d }
        L_0x00ef:
            boolean r7 = loadFromZip(r7, r3, r4, r2)     // Catch:{ all -> 0x001d }
            if (r7 == 0) goto L_0x00fa
            monitor-exit(r0)
            return
        L_0x00f7:
            r7.printStackTrace()     // Catch:{ all -> 0x0109 }
        L_0x00fa:
            java.lang.String r7 = "tmessages.40"
            java.lang.System.loadLibrary(r7)     // Catch:{ Error -> 0x0103 }
            nativeLoaded = r1     // Catch:{ Error -> 0x0103 }
            goto L_0x0107
        L_0x0103:
            r7 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r7)     // Catch:{ all -> 0x0109 }
        L_0x0107:
            monitor-exit(r0)
            return
        L_0x0109:
            r7 = move-exception
            monitor-exit(r0)
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NativeLoader.initNativeLibs(android.content.Context):void");
    }
}

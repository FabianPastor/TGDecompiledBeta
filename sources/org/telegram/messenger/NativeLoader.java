package org.telegram.messenger;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class NativeLoader {
    private static final String LIB_NAME = "tmessages.42";
    private static final String LIB_SO_NAME = "libtmessages.42.so";
    private static final int LIB_VERSION = 42;
    private static final String LOCALE_LIB_SO_NAME = "libtmessages.42loc.so";
    private static volatile boolean nativeLoaded = false;
    private String crashPath = "";

    private static native void init(String str, boolean z);

    private static File getNativeLibraryDir(Context context) {
        File f = null;
        if (context != null) {
            try {
                f = new File((String) ApplicationInfo.class.getField("nativeLibraryDir").get(context.getApplicationInfo()));
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
        if (f == null) {
            f = new File(context.getApplicationInfo().dataDir, "lib");
        }
        if (f.isDirectory()) {
            return f;
        }
        return null;
    }

    private static boolean loadFromZip(Context context, File destDir, File destLocalFile, String folder) {
        try {
            for (File file : destDir.listFiles()) {
                file.delete();
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        ZipFile zipFile = null;
        InputStream stream = null;
        try {
            zipFile = new ZipFile(context.getApplicationInfo().sourceDir);
            ZipEntry entry = zipFile.getEntry("lib/" + folder + "/" + "libtmessages.42.so");
            if (entry != null) {
                stream = zipFile.getInputStream(entry);
                OutputStream out = new FileOutputStream(destLocalFile);
                byte[] buf = new byte[4096];
                while (true) {
                    int read = stream.read(buf);
                    int len = read;
                    if (read <= 0) {
                        break;
                    }
                    Thread.yield();
                    out.write(buf, 0, len);
                }
                out.close();
                destLocalFile.setReadable(true, false);
                destLocalFile.setExecutable(true, false);
                destLocalFile.setWritable(true);
                try {
                    System.load(destLocalFile.getAbsolutePath());
                    nativeLoaded = true;
                } catch (Error e2) {
                    FileLog.e((Throwable) e2);
                }
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (Exception e3) {
                        FileLog.e((Throwable) e3);
                    }
                }
                try {
                    zipFile.close();
                } catch (Exception e4) {
                    FileLog.e((Throwable) e4);
                }
                return true;
            }
            throw new Exception("Unable to find file in apk:lib/" + folder + "/" + "tmessages.42");
        } catch (Exception e5) {
            FileLog.e((Throwable) e5);
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception e6) {
                    FileLog.e((Throwable) e6);
                }
            }
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (Exception e7) {
                    FileLog.e((Throwable) e7);
                }
            }
            return false;
        } catch (Throwable th) {
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception e8) {
                    FileLog.e((Throwable) e8);
                }
            }
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (Exception e9) {
                    FileLog.e((Throwable) e9);
                }
            }
            throw th;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x001b, code lost:
        return;
     */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x00e3 A[Catch:{ all -> 0x001c }] */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x00fd  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static synchronized void initNativeLibs(android.content.Context r8) {
        /*
            java.lang.Class<org.telegram.messenger.NativeLoader> r0 = org.telegram.messenger.NativeLoader.class
            monitor-enter(r0)
            boolean r1 = nativeLoaded     // Catch:{ all -> 0x0111 }
            if (r1 == 0) goto L_0x0009
            monitor-exit(r0)
            return
        L_0x0009:
            r1 = 1
            java.lang.String r2 = "tmessages.42"
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
            r2 = move-exception
            goto L_0x0100
        L_0x001f:
            r2 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ all -> 0x001c }
            java.lang.String r2 = android.os.Build.CPU_ABI     // Catch:{ Exception -> 0x0090 }
            java.lang.String r3 = android.os.Build.CPU_ABI     // Catch:{ Exception -> 0x0090 }
            java.lang.String r4 = "x86_64"
            boolean r3 = r3.equalsIgnoreCase(r4)     // Catch:{ Exception -> 0x0090 }
            if (r3 == 0) goto L_0x0032
            java.lang.String r3 = "x86_64"
            goto L_0x008f
        L_0x0032:
            java.lang.String r3 = android.os.Build.CPU_ABI     // Catch:{ Exception -> 0x0090 }
            java.lang.String r4 = "arm64-v8a"
            boolean r3 = r3.equalsIgnoreCase(r4)     // Catch:{ Exception -> 0x0090 }
            if (r3 == 0) goto L_0x003f
            java.lang.String r3 = "arm64-v8a"
            goto L_0x008f
        L_0x003f:
            java.lang.String r3 = android.os.Build.CPU_ABI     // Catch:{ Exception -> 0x0090 }
            java.lang.String r4 = "armeabi-v7a"
            boolean r3 = r3.equalsIgnoreCase(r4)     // Catch:{ Exception -> 0x0090 }
            if (r3 == 0) goto L_0x004c
            java.lang.String r3 = "armeabi-v7a"
            goto L_0x008f
        L_0x004c:
            java.lang.String r3 = android.os.Build.CPU_ABI     // Catch:{ Exception -> 0x0090 }
            java.lang.String r4 = "armeabi"
            boolean r3 = r3.equalsIgnoreCase(r4)     // Catch:{ Exception -> 0x0090 }
            if (r3 == 0) goto L_0x0059
            java.lang.String r3 = "armeabi"
            goto L_0x008f
        L_0x0059:
            java.lang.String r3 = android.os.Build.CPU_ABI     // Catch:{ Exception -> 0x0090 }
            java.lang.String r4 = "x86"
            boolean r3 = r3.equalsIgnoreCase(r4)     // Catch:{ Exception -> 0x0090 }
            if (r3 == 0) goto L_0x0066
            java.lang.String r3 = "x86"
            goto L_0x008f
        L_0x0066:
            java.lang.String r3 = android.os.Build.CPU_ABI     // Catch:{ Exception -> 0x0090 }
            java.lang.String r4 = "mips"
            boolean r3 = r3.equalsIgnoreCase(r4)     // Catch:{ Exception -> 0x0090 }
            if (r3 == 0) goto L_0x0073
            java.lang.String r3 = "mips"
            goto L_0x008f
        L_0x0073:
            java.lang.String r3 = "armeabi"
            boolean r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0090 }
            if (r4 == 0) goto L_0x008f
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0090 }
            r4.<init>()     // Catch:{ Exception -> 0x0090 }
            java.lang.String r5 = "Unsupported arch: "
            r4.append(r5)     // Catch:{ Exception -> 0x0090 }
            java.lang.String r5 = android.os.Build.CPU_ABI     // Catch:{ Exception -> 0x0090 }
            r4.append(r5)     // Catch:{ Exception -> 0x0090 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0090 }
            org.telegram.messenger.FileLog.e((java.lang.String) r4)     // Catch:{ Exception -> 0x0090 }
        L_0x008f:
            goto L_0x0096
        L_0x0090:
            r2 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ all -> 0x001c }
            java.lang.String r3 = "armeabi"
        L_0x0096:
            java.lang.String r2 = "os.arch"
            java.lang.String r2 = java.lang.System.getProperty(r2)     // Catch:{ all -> 0x001c }
            if (r2 == 0) goto L_0x00a9
            java.lang.String r4 = "686"
            boolean r4 = r2.contains(r4)     // Catch:{ all -> 0x001c }
            if (r4 == 0) goto L_0x00a9
            java.lang.String r4 = "x86"
            r3 = r4
        L_0x00a9:
            java.io.File r4 = new java.io.File     // Catch:{ all -> 0x001c }
            java.io.File r5 = r8.getFilesDir()     // Catch:{ all -> 0x001c }
            java.lang.String r6 = "lib"
            r4.<init>(r5, r6)     // Catch:{ all -> 0x001c }
            r4.mkdirs()     // Catch:{ all -> 0x001c }
            java.io.File r5 = new java.io.File     // Catch:{ all -> 0x001c }
            java.lang.String r6 = "libtmessages.42loc.so"
            r5.<init>(r4, r6)     // Catch:{ all -> 0x001c }
            boolean r6 = r5.exists()     // Catch:{ all -> 0x001c }
            if (r6 == 0) goto L_0x00df
            boolean r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Error -> 0x00d8 }
            if (r6 == 0) goto L_0x00cd
            java.lang.String r6 = "Load local lib"
            org.telegram.messenger.FileLog.d(r6)     // Catch:{ Error -> 0x00d8 }
        L_0x00cd:
            java.lang.String r6 = r5.getAbsolutePath()     // Catch:{ Error -> 0x00d8 }
            java.lang.System.load(r6)     // Catch:{ Error -> 0x00d8 }
            nativeLoaded = r1     // Catch:{ Error -> 0x00d8 }
            monitor-exit(r0)
            return
        L_0x00d8:
            r6 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r6)     // Catch:{ all -> 0x001c }
            r5.delete()     // Catch:{ all -> 0x001c }
        L_0x00df:
            boolean r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x001c }
            if (r6 == 0) goto L_0x00f7
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x001c }
            r6.<init>()     // Catch:{ all -> 0x001c }
            java.lang.String r7 = "Library not found, arch = "
            r6.append(r7)     // Catch:{ all -> 0x001c }
            r6.append(r3)     // Catch:{ all -> 0x001c }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x001c }
            org.telegram.messenger.FileLog.e((java.lang.String) r6)     // Catch:{ all -> 0x001c }
        L_0x00f7:
            boolean r6 = loadFromZip(r8, r4, r5, r3)     // Catch:{ all -> 0x001c }
            if (r6 == 0) goto L_0x00ff
            monitor-exit(r0)
            return
        L_0x00ff:
            goto L_0x0103
        L_0x0100:
            r2.printStackTrace()     // Catch:{ all -> 0x0111 }
        L_0x0103:
            java.lang.String r2 = "tmessages.42"
            java.lang.System.loadLibrary(r2)     // Catch:{ Error -> 0x010b }
            nativeLoaded = r1     // Catch:{ Error -> 0x010b }
            goto L_0x010f
        L_0x010b:
            r1 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ all -> 0x0111 }
        L_0x010f:
            monitor-exit(r0)
            return
        L_0x0111:
            r8 = move-exception
            monitor-exit(r0)
            throw r8
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NativeLoader.initNativeLibs(android.content.Context):void");
    }
}

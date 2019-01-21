package org.telegram.messenger;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import com.google.devtools.build.android.desugar.runtime.ThrowableExtension;
import java.io.File;

public class NativeLoader {
    private static final String LIB_NAME = "tmessages.29";
    private static final String LIB_SO_NAME = "libtmessages.29.so";
    private static final int LIB_VERSION = 29;
    private static final String LOCALE_LIB_SO_NAME = "libtmessages.29loc.so";
    private static volatile boolean nativeLoaded = false;
    private String crashPath = "";

    private static native void init(String str, boolean z);

    private static File getNativeLibraryDir(Context context) {
        File f = null;
        if (context != null) {
            try {
                f = new File((String) ApplicationInfo.class.getField("nativeLibraryDir").get(context.getApplicationInfo()));
            } catch (Throwable th) {
                ThrowableExtension.printStackTrace(th);
            }
        }
        if (f == null) {
            f = new File(context.getApplicationInfo().dataDir, "lib");
        }
        return f.isDirectory() ? f : null;
    }

    /* JADX WARNING: Removed duplicated region for block: B:35:0x00a1 A:{SYNTHETIC, Splitter: B:35:0x00a1} */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x00a6 A:{SYNTHETIC, Splitter: B:38:0x00a6} */
    @android.annotation.SuppressLint({"UnsafeDynamicallyLoadedCode", "SetWorldReadable"})
    private static boolean loadFromZip(android.content.Context r12, java.io.File r13, java.io.File r14, java.lang.String r15) {
        /*
        r10 = r13.listFiles();	 Catch:{ Exception -> 0x0010 }
        r11 = r10.length;	 Catch:{ Exception -> 0x0010 }
        r9 = 0;
    L_0x0006:
        if (r9 >= r11) goto L_0x0014;
    L_0x0008:
        r3 = r10[r9];	 Catch:{ Exception -> 0x0010 }
        r3.delete();	 Catch:{ Exception -> 0x0010 }
        r9 = r9 + 1;
        goto L_0x0006;
    L_0x0010:
        r1 = move-exception;
        org.telegram.messenger.FileLog.e(r1);
    L_0x0014:
        r7 = 0;
        r6 = 0;
        r8 = new java.util.zip.ZipFile;	 Catch:{ Exception -> 0x00f7 }
        r9 = r12.getApplicationInfo();	 Catch:{ Exception -> 0x00f7 }
        r9 = r9.sourceDir;	 Catch:{ Exception -> 0x00f7 }
        r8.<init>(r9);	 Catch:{ Exception -> 0x00f7 }
        r9 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0071, all -> 0x009d }
        r9.<init>();	 Catch:{ Exception -> 0x0071, all -> 0x009d }
        r10 = "lib/";
        r9 = r9.append(r10);	 Catch:{ Exception -> 0x0071, all -> 0x009d }
        r9 = r9.append(r15);	 Catch:{ Exception -> 0x0071, all -> 0x009d }
        r10 = "/";
        r9 = r9.append(r10);	 Catch:{ Exception -> 0x0071, all -> 0x009d }
        r10 = "libtmessages.29.so";
        r9 = r9.append(r10);	 Catch:{ Exception -> 0x0071, all -> 0x009d }
        r9 = r9.toString();	 Catch:{ Exception -> 0x0071, all -> 0x009d }
        r2 = r8.getEntry(r9);	 Catch:{ Exception -> 0x0071, all -> 0x009d }
        if (r2 != 0) goto L_0x0082;
    L_0x0049:
        r9 = new java.lang.Exception;	 Catch:{ Exception -> 0x0071, all -> 0x009d }
        r10 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0071, all -> 0x009d }
        r10.<init>();	 Catch:{ Exception -> 0x0071, all -> 0x009d }
        r11 = "Unable to find file in apk:lib/";
        r10 = r10.append(r11);	 Catch:{ Exception -> 0x0071, all -> 0x009d }
        r10 = r10.append(r15);	 Catch:{ Exception -> 0x0071, all -> 0x009d }
        r11 = "/";
        r10 = r10.append(r11);	 Catch:{ Exception -> 0x0071, all -> 0x009d }
        r11 = "tmessages.29";
        r10 = r10.append(r11);	 Catch:{ Exception -> 0x0071, all -> 0x009d }
        r10 = r10.toString();	 Catch:{ Exception -> 0x0071, all -> 0x009d }
        r9.<init>(r10);	 Catch:{ Exception -> 0x0071, all -> 0x009d }
        throw r9;	 Catch:{ Exception -> 0x0071, all -> 0x009d }
    L_0x0071:
        r1 = move-exception;
        r7 = r8;
    L_0x0073:
        org.telegram.messenger.FileLog.e(r1);	 Catch:{ all -> 0x00f5 }
        if (r6 == 0) goto L_0x007b;
    L_0x0078:
        r6.close();	 Catch:{ Exception -> 0x00e1 }
    L_0x007b:
        if (r7 == 0) goto L_0x0080;
    L_0x007d:
        r7.close();	 Catch:{ Exception -> 0x00e6 }
    L_0x0080:
        r9 = 0;
    L_0x0081:
        return r9;
    L_0x0082:
        r6 = r8.getInputStream(r2);	 Catch:{ Exception -> 0x0071, all -> 0x009d }
        r5 = new java.io.FileOutputStream;	 Catch:{ Exception -> 0x0071, all -> 0x009d }
        r5.<init>(r14);	 Catch:{ Exception -> 0x0071, all -> 0x009d }
        r9 = 4096; // 0x1000 float:5.74E-42 double:2.0237E-320;
        r0 = new byte[r9];	 Catch:{ Exception -> 0x0071, all -> 0x009d }
    L_0x008f:
        r4 = r6.read(r0);	 Catch:{ Exception -> 0x0071, all -> 0x009d }
        if (r4 <= 0) goto L_0x00aa;
    L_0x0095:
        java.lang.Thread.yield();	 Catch:{ Exception -> 0x0071, all -> 0x009d }
        r9 = 0;
        r5.write(r0, r9, r4);	 Catch:{ Exception -> 0x0071, all -> 0x009d }
        goto L_0x008f;
    L_0x009d:
        r9 = move-exception;
        r7 = r8;
    L_0x009f:
        if (r6 == 0) goto L_0x00a4;
    L_0x00a1:
        r6.close();	 Catch:{ Exception -> 0x00eb }
    L_0x00a4:
        if (r7 == 0) goto L_0x00a9;
    L_0x00a6:
        r7.close();	 Catch:{ Exception -> 0x00f0 }
    L_0x00a9:
        throw r9;
    L_0x00aa:
        r5.close();	 Catch:{ Exception -> 0x0071, all -> 0x009d }
        r9 = 1;
        r10 = 0;
        r14.setReadable(r9, r10);	 Catch:{ Exception -> 0x0071, all -> 0x009d }
        r9 = 1;
        r10 = 0;
        r14.setExecutable(r9, r10);	 Catch:{ Exception -> 0x0071, all -> 0x009d }
        r9 = 1;
        r14.setWritable(r9);	 Catch:{ Exception -> 0x0071, all -> 0x009d }
        r9 = r14.getAbsolutePath();	 Catch:{ Error -> 0x00d2 }
        java.lang.System.load(r9);	 Catch:{ Error -> 0x00d2 }
        r9 = 1;
        nativeLoaded = r9;	 Catch:{ Error -> 0x00d2 }
    L_0x00c5:
        r9 = 1;
        if (r6 == 0) goto L_0x00cb;
    L_0x00c8:
        r6.close();	 Catch:{ Exception -> 0x00d7 }
    L_0x00cb:
        if (r8 == 0) goto L_0x00d0;
    L_0x00cd:
        r8.close();	 Catch:{ Exception -> 0x00dc }
    L_0x00d0:
        r7 = r8;
        goto L_0x0081;
    L_0x00d2:
        r1 = move-exception;
        org.telegram.messenger.FileLog.e(r1);	 Catch:{ Exception -> 0x0071, all -> 0x009d }
        goto L_0x00c5;
    L_0x00d7:
        r1 = move-exception;
        org.telegram.messenger.FileLog.e(r1);
        goto L_0x00cb;
    L_0x00dc:
        r1 = move-exception;
        org.telegram.messenger.FileLog.e(r1);
        goto L_0x00d0;
    L_0x00e1:
        r1 = move-exception;
        org.telegram.messenger.FileLog.e(r1);
        goto L_0x007b;
    L_0x00e6:
        r1 = move-exception;
        org.telegram.messenger.FileLog.e(r1);
        goto L_0x0080;
    L_0x00eb:
        r1 = move-exception;
        org.telegram.messenger.FileLog.e(r1);
        goto L_0x00a4;
    L_0x00f0:
        r1 = move-exception;
        org.telegram.messenger.FileLog.e(r1);
        goto L_0x00a9;
    L_0x00f5:
        r9 = move-exception;
        goto L_0x009f;
    L_0x00f7:
        r1 = move-exception;
        goto L_0x0073;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NativeLoader.loadFromZip(android.content.Context, java.io.File, java.io.File, java.lang.String):boolean");
    }

    /* JADX WARNING: Removed duplicated region for block: B:38:0x0086 A:{Catch:{ Error -> 0x0020, Throwable -> 0x0132 }} */
    /* JADX WARNING: Missing block: B:40:0x00a1, code:
            if (loadFromZip(r9, r0, r1, r3) == false) goto L_0x00a3;
     */
    @android.annotation.SuppressLint({"UnsafeDynamicallyLoadedCode"})
    public static synchronized void initNativeLibs(android.content.Context r9) {
        /*
        r7 = org.telegram.messenger.NativeLoader.class;
        monitor-enter(r7);
        r6 = nativeLoaded;	 Catch:{ all -> 0x00b4 }
        if (r6 == 0) goto L_0x0009;
    L_0x0007:
        monitor-exit(r7);
        return;
    L_0x0009:
        net.hockeyapp.android.Constants.loadFromContext(r9);	 Catch:{ all -> 0x00b4 }
        r6 = "tmessages.29";
        java.lang.System.loadLibrary(r6);	 Catch:{ Error -> 0x0020 }
        r6 = 1;
        nativeLoaded = r6;	 Catch:{ Error -> 0x0020 }
        r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Error -> 0x0020 }
        if (r6 == 0) goto L_0x0007;
    L_0x0019:
        r6 = "loaded normal lib";
        org.telegram.messenger.FileLog.d(r6);	 Catch:{ Error -> 0x0020 }
        goto L_0x0007;
    L_0x0020:
        r2 = move-exception;
        org.telegram.messenger.FileLog.e(r2);	 Catch:{ Throwable -> 0x0132 }
        r5 = android.os.Build.CPU_ABI;	 Catch:{ Exception -> 0x0129 }
        r6 = android.os.Build.CPU_ABI;	 Catch:{ Exception -> 0x0129 }
        r8 = "x86_64";
        r6 = r6.equalsIgnoreCase(r8);	 Catch:{ Exception -> 0x0129 }
        if (r6 == 0) goto L_0x00b7;
    L_0x0031:
        r3 = "x86_64";
    L_0x0034:
        r6 = "os.arch";
        r4 = java.lang.System.getProperty(r6);	 Catch:{ Throwable -> 0x0132 }
        if (r4 == 0) goto L_0x0049;
    L_0x003d:
        r6 = "686";
        r6 = r4.contains(r6);	 Catch:{ Throwable -> 0x0132 }
        if (r6 == 0) goto L_0x0049;
    L_0x0046:
        r3 = "x86";
    L_0x0049:
        r0 = new java.io.File;	 Catch:{ Throwable -> 0x0132 }
        r6 = r9.getFilesDir();	 Catch:{ Throwable -> 0x0132 }
        r8 = "lib";
        r0.<init>(r6, r8);	 Catch:{ Throwable -> 0x0132 }
        r0.mkdirs();	 Catch:{ Throwable -> 0x0132 }
        r1 = new java.io.File;	 Catch:{ Throwable -> 0x0132 }
        r6 = "libtmessages.29loc.so";
        r1.<init>(r0, r6);	 Catch:{ Throwable -> 0x0132 }
        r6 = r1.exists();	 Catch:{ Throwable -> 0x0132 }
        if (r6 == 0) goto L_0x0082;
    L_0x0066:
        r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Error -> 0x007b }
        if (r6 == 0) goto L_0x0070;
    L_0x006a:
        r6 = "Load local lib";
        org.telegram.messenger.FileLog.d(r6);	 Catch:{ Error -> 0x007b }
    L_0x0070:
        r6 = r1.getAbsolutePath();	 Catch:{ Error -> 0x007b }
        java.lang.System.load(r6);	 Catch:{ Error -> 0x007b }
        r6 = 1;
        nativeLoaded = r6;	 Catch:{ Error -> 0x007b }
        goto L_0x0007;
    L_0x007b:
        r2 = move-exception;
        org.telegram.messenger.FileLog.e(r2);	 Catch:{ Throwable -> 0x0132 }
        r1.delete();	 Catch:{ Throwable -> 0x0132 }
    L_0x0082:
        r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Throwable -> 0x0132 }
        if (r6 == 0) goto L_0x009d;
    L_0x0086:
        r6 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x0132 }
        r6.<init>();	 Catch:{ Throwable -> 0x0132 }
        r8 = "Library not found, arch = ";
        r6 = r6.append(r8);	 Catch:{ Throwable -> 0x0132 }
        r6 = r6.append(r3);	 Catch:{ Throwable -> 0x0132 }
        r6 = r6.toString();	 Catch:{ Throwable -> 0x0132 }
        org.telegram.messenger.FileLog.e(r6);	 Catch:{ Throwable -> 0x0132 }
    L_0x009d:
        r6 = loadFromZip(r9, r0, r1, r3);	 Catch:{ Throwable -> 0x0132 }
        if (r6 != 0) goto L_0x0007;
    L_0x00a3:
        r6 = "tmessages.29";
        java.lang.System.loadLibrary(r6);	 Catch:{ Error -> 0x00ae }
        r6 = 1;
        nativeLoaded = r6;	 Catch:{ Error -> 0x00ae }
        goto L_0x0007;
    L_0x00ae:
        r2 = move-exception;
        org.telegram.messenger.FileLog.e(r2);	 Catch:{ all -> 0x00b4 }
        goto L_0x0007;
    L_0x00b4:
        r6 = move-exception;
        monitor-exit(r7);
        throw r6;
    L_0x00b7:
        r6 = android.os.Build.CPU_ABI;	 Catch:{ Exception -> 0x0129 }
        r8 = "arm64-v8a";
        r6 = r6.equalsIgnoreCase(r8);	 Catch:{ Exception -> 0x0129 }
        if (r6 == 0) goto L_0x00c7;
    L_0x00c2:
        r3 = "arm64-v8a";
        goto L_0x0034;
    L_0x00c7:
        r6 = android.os.Build.CPU_ABI;	 Catch:{ Exception -> 0x0129 }
        r8 = "armeabi-v7a";
        r6 = r6.equalsIgnoreCase(r8);	 Catch:{ Exception -> 0x0129 }
        if (r6 == 0) goto L_0x00d7;
    L_0x00d2:
        r3 = "armeabi-v7a";
        goto L_0x0034;
    L_0x00d7:
        r6 = android.os.Build.CPU_ABI;	 Catch:{ Exception -> 0x0129 }
        r8 = "armeabi";
        r6 = r6.equalsIgnoreCase(r8);	 Catch:{ Exception -> 0x0129 }
        if (r6 == 0) goto L_0x00e7;
    L_0x00e2:
        r3 = "armeabi";
        goto L_0x0034;
    L_0x00e7:
        r6 = android.os.Build.CPU_ABI;	 Catch:{ Exception -> 0x0129 }
        r8 = "x86";
        r6 = r6.equalsIgnoreCase(r8);	 Catch:{ Exception -> 0x0129 }
        if (r6 == 0) goto L_0x00f7;
    L_0x00f2:
        r3 = "x86";
        goto L_0x0034;
    L_0x00f7:
        r6 = android.os.Build.CPU_ABI;	 Catch:{ Exception -> 0x0129 }
        r8 = "mips";
        r6 = r6.equalsIgnoreCase(r8);	 Catch:{ Exception -> 0x0129 }
        if (r6 == 0) goto L_0x0107;
    L_0x0102:
        r3 = "mips";
        goto L_0x0034;
    L_0x0107:
        r3 = "armeabi";
        r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x0129 }
        if (r6 == 0) goto L_0x0034;
    L_0x010e:
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0129 }
        r6.<init>();	 Catch:{ Exception -> 0x0129 }
        r8 = "Unsupported arch: ";
        r6 = r6.append(r8);	 Catch:{ Exception -> 0x0129 }
        r8 = android.os.Build.CPU_ABI;	 Catch:{ Exception -> 0x0129 }
        r6 = r6.append(r8);	 Catch:{ Exception -> 0x0129 }
        r6 = r6.toString();	 Catch:{ Exception -> 0x0129 }
        org.telegram.messenger.FileLog.e(r6);	 Catch:{ Exception -> 0x0129 }
        goto L_0x0034;
    L_0x0129:
        r2 = move-exception;
        org.telegram.messenger.FileLog.e(r2);	 Catch:{ Throwable -> 0x0132 }
        r3 = "armeabi";
        goto L_0x0034;
    L_0x0132:
        r2 = move-exception;
        com.google.devtools.build.android.desugar.runtime.ThrowableExtension.printStackTrace(r2);	 Catch:{ all -> 0x00b4 }
        goto L_0x00a3;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NativeLoader.initNativeLibs(android.content.Context):void");
    }
}

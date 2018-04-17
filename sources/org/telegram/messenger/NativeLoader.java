package org.telegram.messenger;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.hockeyapp.android.Constants;

public class NativeLoader {
    private static final String LIB_NAME = "tmessages.28";
    private static final String LIB_SO_NAME = "libtmessages.28.so";
    private static final int LIB_VERSION = 28;
    private static final String LOCALE_LIB_SO_NAME = "libtmessages.28loc.so";
    private static volatile boolean nativeLoaded = false;
    private String crashPath = TtmlNode.ANONYMOUS_REGION_ID;

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

    @SuppressLint({"UnsafeDynamicallyLoadedCode", "SetWorldReadable"})
    private static boolean loadFromZip(Context context, File destDir, File destLocalFile, String folder) {
        try {
            for (File file : destDir.listFiles()) {
                file.delete();
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
        ZipFile zipFile = null;
        InputStream stream = null;
        try {
            zipFile = new ZipFile(context.getApplicationInfo().sourceDir);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("lib/");
            stringBuilder.append(folder);
            stringBuilder.append("/");
            stringBuilder.append(LIB_SO_NAME);
            ZipEntry entry = zipFile.getEntry(stringBuilder.toString());
            if (entry == null) {
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("Unable to find file in apk:lib/");
                stringBuilder2.append(folder);
                stringBuilder2.append("/");
                stringBuilder2.append(LIB_NAME);
                throw new Exception(stringBuilder2.toString());
            }
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
            } catch (Throwable e2) {
                FileLog.m3e(e2);
            }
            if (stream != null) {
                try {
                    stream.close();
                } catch (Throwable e3) {
                    FileLog.m3e(e3);
                }
            }
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (Throwable e32) {
                    FileLog.m3e(e32);
                }
            }
            return true;
        } catch (Throwable e4) {
            FileLog.m3e(e4);
            if (stream != null) {
                try {
                    stream.close();
                } catch (Throwable e42) {
                    FileLog.m3e(e42);
                }
            }
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (Throwable e422) {
                    FileLog.m3e(e422);
                }
            }
            return false;
        } catch (Throwable th) {
            if (stream != null) {
                try {
                    stream.close();
                } catch (Throwable e4222) {
                    FileLog.m3e(e4222);
                }
            }
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (Throwable e42222) {
                    FileLog.m3e(e42222);
                }
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @SuppressLint({"UnsafeDynamicallyLoadedCode"})
    public static synchronized void initNativeLibs(Context context) {
        StringBuilder stringBuilder;
        synchronized (NativeLoader.class) {
            if (nativeLoaded) {
                return;
            }
            Constants.loadFromContext(context);
            try {
                System.loadLibrary(LIB_NAME);
                nativeLoaded = true;
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m0d("loaded normal lib");
                }
            } catch (Throwable e) {
                Throwable e2;
                try {
                    String folder;
                    File destDir;
                    File destLocalFile;
                    FileLog.m3e(e2);
                    try {
                        e2 = Build.CPU_ABI;
                        if (Build.CPU_ABI.equalsIgnoreCase("x86_64")) {
                            folder = "x86_64";
                        } else if (Build.CPU_ABI.equalsIgnoreCase("arm64-v8a")) {
                            folder = "arm64-v8a";
                        } else if (Build.CPU_ABI.equalsIgnoreCase("armeabi-v7a")) {
                            folder = "armeabi-v7a";
                        } else if (Build.CPU_ABI.equalsIgnoreCase("armeabi")) {
                            folder = "armeabi";
                        } else if (Build.CPU_ABI.equalsIgnoreCase("x86")) {
                            folder = "x86";
                        } else if (Build.CPU_ABI.equalsIgnoreCase("mips")) {
                            folder = "mips";
                        } else {
                            folder = "armeabi";
                            if (BuildVars.LOGS_ENABLED) {
                                StringBuilder stringBuilder2 = new StringBuilder();
                                stringBuilder2.append("Unsupported arch: ");
                                stringBuilder2.append(Build.CPU_ABI);
                                FileLog.m1e(stringBuilder2.toString());
                            }
                            e2 = folder;
                            folder = System.getProperty("os.arch");
                            if (folder != null && folder.contains("686")) {
                                e2 = "x86";
                            }
                            destDir = new File(context.getFilesDir(), "lib");
                            destDir.mkdirs();
                            destLocalFile = new File(destDir, LOCALE_LIB_SO_NAME);
                            if (destLocalFile.exists()) {
                                try {
                                    if (BuildVars.LOGS_ENABLED) {
                                        FileLog.m0d("Load local lib");
                                    }
                                    System.load(destLocalFile.getAbsolutePath());
                                    nativeLoaded = true;
                                    return;
                                } catch (Throwable e3) {
                                    FileLog.m3e(e3);
                                    destLocalFile.delete();
                                    if (BuildVars.LOGS_ENABLED) {
                                        stringBuilder = new StringBuilder();
                                        stringBuilder.append("Library not found, arch = ");
                                        stringBuilder.append(e2);
                                        FileLog.m1e(stringBuilder.toString());
                                    }
                                    if (loadFromZip(context, destDir, destLocalFile, e2)) {
                                        try {
                                            System.loadLibrary(LIB_NAME);
                                            nativeLoaded = true;
                                        } catch (Throwable e4) {
                                            FileLog.m3e(e4);
                                        }
                                        return;
                                    }
                                    return;
                                }
                            }
                            if (BuildVars.LOGS_ENABLED) {
                                stringBuilder = new StringBuilder();
                                stringBuilder.append("Library not found, arch = ");
                                stringBuilder.append(e2);
                                FileLog.m1e(stringBuilder.toString());
                            }
                            if (loadFromZip(context, destDir, destLocalFile, e2)) {
                                System.loadLibrary(LIB_NAME);
                                nativeLoaded = true;
                                return;
                            }
                            return;
                        }
                    } catch (Throwable e22) {
                        FileLog.m3e(e22);
                        folder = "armeabi";
                    }
                    e22 = folder;
                    folder = System.getProperty("os.arch");
                    e22 = "x86";
                    destDir = new File(context.getFilesDir(), "lib");
                    destDir.mkdirs();
                    destLocalFile = new File(destDir, LOCALE_LIB_SO_NAME);
                    if (destLocalFile.exists()) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.m0d("Load local lib");
                        }
                        System.load(destLocalFile.getAbsolutePath());
                        nativeLoaded = true;
                        return;
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("Library not found, arch = ");
                        stringBuilder.append(e22);
                        FileLog.m1e(stringBuilder.toString());
                    }
                    if (loadFromZip(context, destDir, destLocalFile, e22)) {
                        System.loadLibrary(LIB_NAME);
                        nativeLoaded = true;
                        return;
                    }
                    return;
                } catch (Throwable e222) {
                    e222.printStackTrace();
                }
            }
        }
    }
}

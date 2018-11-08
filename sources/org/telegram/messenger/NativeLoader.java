package org.telegram.messenger;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import com.google.devtools.build.android.desugar.runtime.ThrowableExtension;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.hockeyapp.android.Constants;

public class NativeLoader {
    private static final String LIB_NAME = "tmessages.29";
    private static final String LIB_SO_NAME = "libtmessages.29.so";
    private static final int LIB_VERSION = 29;
    private static final String LOCALE_LIB_SO_NAME = "libtmessages.29loc.so";
    private static volatile boolean nativeLoaded = false;
    private String crashPath = TtmlNode.ANONYMOUS_REGION_ID;

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
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @SuppressLint({"UnsafeDynamicallyLoadedCode", "SetWorldReadable"})
    private static boolean loadFromZip(Context context, File destDir, File destLocalFile, String folder) {
        Throwable e;
        Throwable th;
        try {
            for (File file : destDir.listFiles()) {
                file.delete();
            }
        } catch (Throwable e2) {
            FileLog.m14e(e2);
        }
        ZipFile zipFile = null;
        InputStream stream = null;
        try {
            ZipFile zipFile2 = new ZipFile(context.getApplicationInfo().sourceDir);
            try {
                ZipEntry entry = zipFile2.getEntry("lib/" + folder + "/" + LIB_SO_NAME);
                if (entry == null) {
                    throw new Exception("Unable to find file in apk:lib/" + folder + "/" + LIB_NAME);
                }
                stream = zipFile2.getInputStream(entry);
                OutputStream out = new FileOutputStream(destLocalFile);
                byte[] buf = new byte[4096];
                while (true) {
                    int len = stream.read(buf);
                    if (len <= 0) {
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
                } catch (Throwable e22) {
                    FileLog.m14e(e22);
                }
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (Throwable e222) {
                        FileLog.m14e(e222);
                    }
                }
                if (zipFile2 != null) {
                    try {
                        zipFile2.close();
                    } catch (Throwable e2222) {
                        FileLog.m14e(e2222);
                    }
                }
                zipFile = zipFile2;
                return true;
            } catch (Exception e3) {
                e2222 = e3;
                zipFile = zipFile2;
            } catch (Throwable th2) {
                th = th2;
                zipFile = zipFile2;
                if (stream != null) {
                }
                if (zipFile != null) {
                }
                throw th;
            }
        } catch (Exception e4) {
            e2222 = e4;
            try {
                FileLog.m14e(e2222);
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (Throwable e22222) {
                        FileLog.m14e(e22222);
                    }
                }
                if (zipFile != null) {
                    try {
                        zipFile.close();
                    } catch (Throwable e222222) {
                        FileLog.m14e(e222222);
                    }
                }
                return false;
            } catch (Throwable th3) {
                th = th3;
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (Throwable e2222222) {
                        FileLog.m14e(e2222222);
                    }
                }
                if (zipFile != null) {
                    try {
                        zipFile.close();
                    } catch (Throwable e22222222) {
                        FileLog.m14e(e22222222);
                    }
                }
                throw th;
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:38:0x0086 A:{Catch:{ Error -> 0x0020, Throwable -> 0x0132 }} */
    /* JADX WARNING: Missing block: B:40:0x00a1, code:
            if (loadFromZip(r9, r0, r1, r3) == false) goto L_0x00a3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @SuppressLint({"UnsafeDynamicallyLoadedCode"})
    public static synchronized void initNativeLibs(Context context) {
        String folder;
        synchronized (NativeLoader.class) {
            if (!nativeLoaded) {
                Constants.loadFromContext(context);
                try {
                    System.loadLibrary(LIB_NAME);
                    nativeLoaded = true;
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m11d("loaded normal lib");
                    }
                } catch (Throwable e) {
                    FileLog.m14e(e);
                    try {
                        String str = Build.CPU_ABI;
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
                                FileLog.m12e("Unsupported arch: " + Build.CPU_ABI);
                            }
                        }
                    } catch (Throwable e2) {
                        FileLog.m14e(e2);
                        folder = "armeabi";
                    }
                    String javaArch = System.getProperty("os.arch");
                    if (javaArch != null && javaArch.contains("686")) {
                        folder = "x86";
                    }
                    File destDir = new File(context.getFilesDir(), "lib");
                    destDir.mkdirs();
                    File destLocalFile = new File(destDir, LOCALE_LIB_SO_NAME);
                    if (destLocalFile.exists()) {
                        try {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.m11d("Load local lib");
                            }
                            System.load(destLocalFile.getAbsolutePath());
                            nativeLoaded = true;
                        } catch (Throwable e22) {
                            FileLog.m14e(e22);
                            destLocalFile.delete();
                            if (BuildVars.LOGS_ENABLED) {
                            }
                        }
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m12e("Library not found, arch = " + folder);
                    }
                } catch (Throwable e222) {
                    ThrowableExtension.printStackTrace(e222);
                }
            }
        }
        return;
        try {
            System.loadLibrary(LIB_NAME);
            nativeLoaded = true;
        } catch (Throwable e2222) {
            FileLog.m14e(e2222);
        }
        return;
    }
}

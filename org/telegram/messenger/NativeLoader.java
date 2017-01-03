package org.telegram.messenger;

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
    private static final String LIB_NAME = "tmessages.25";
    private static final String LIB_SO_NAME = "libtmessages.25.so";
    private static final int LIB_VERSION = 25;
    private static final String LOCALE_LIB_SO_NAME = "libtmessages.25loc.so";
    private static volatile boolean nativeLoaded = false;
    private String crashPath = "";

    private static native void init(String str, boolean z);

    private static File getNativeLibraryDir(Context context) {
        File file = null;
        if (context != null) {
            try {
                file = new File((String) ApplicationInfo.class.getField("nativeLibraryDir").get(context.getApplicationInfo()));
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
        if (file == null) {
            file = new File(context.getApplicationInfo().dataDir, "lib");
        }
        return file.isDirectory() ? file : null;
    }

    private static boolean loadFromZip(Context context, File destDir, File destLocalFile, String folder) {
        Throwable th;
        try {
            for (File file : destDir.listFiles()) {
                file.delete();
            }
        } catch (Throwable e) {
            Throwable e2;
            FileLog.e("tmessages", e2);
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
                    init(Constants.FILES_PATH, BuildVars.DEBUG_VERSION);
                    nativeLoaded = true;
                } catch (Throwable e22) {
                    FileLog.e("tmessages", e22);
                }
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (Throwable e222) {
                        FileLog.e("tmessages", e222);
                    }
                }
                if (zipFile2 != null) {
                    try {
                        zipFile2.close();
                    } catch (Throwable e2222) {
                        FileLog.e("tmessages", e2222);
                    }
                }
                zipFile = zipFile2;
                return true;
            } catch (Exception e3) {
                e2222 = e3;
                zipFile = zipFile2;
                try {
                    FileLog.e("tmessages", e2222);
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (Throwable e22222) {
                            FileLog.e("tmessages", e22222);
                        }
                    }
                    if (zipFile != null) {
                        try {
                            zipFile.close();
                        } catch (Throwable e222222) {
                            FileLog.e("tmessages", e222222);
                        }
                    }
                    return false;
                } catch (Throwable th2) {
                    th = th2;
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (Throwable e2222222) {
                            FileLog.e("tmessages", e2222222);
                        }
                    }
                    if (zipFile != null) {
                        try {
                            zipFile.close();
                        } catch (Throwable e22222222) {
                            FileLog.e("tmessages", e22222222);
                        }
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                zipFile = zipFile2;
                if (stream != null) {
                    stream.close();
                }
                if (zipFile != null) {
                    zipFile.close();
                }
                throw th;
            }
        } catch (Exception e4) {
            e22222222 = e4;
            FileLog.e("tmessages", e22222222);
            if (stream != null) {
                stream.close();
            }
            if (zipFile != null) {
                zipFile.close();
            }
            return false;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static synchronized void initNativeLibs(Context context) {
        synchronized (NativeLoader.class) {
            if (!nativeLoaded) {
                String folder;
                Constants.loadFromContext(context);
                try {
                    if (Build.CPU_ABI.equalsIgnoreCase("armeabi-v7a")) {
                        folder = "armeabi-v7a";
                    } else if (Build.CPU_ABI.equalsIgnoreCase("armeabi")) {
                        folder = "armeabi";
                    } else if (Build.CPU_ABI.equalsIgnoreCase("x86")) {
                        folder = "x86";
                    } else if (Build.CPU_ABI.equalsIgnoreCase("mips")) {
                        folder = "mips";
                    } else {
                        folder = "armeabi";
                        FileLog.e("tmessages", "Unsupported arch: " + Build.CPU_ABI);
                    }
                    try {
                        String javaArch = System.getProperty("os.arch");
                        if (javaArch != null && javaArch.contains("686")) {
                            folder = "x86";
                        }
                        File destFile = getNativeLibraryDir(context);
                        if (destFile != null) {
                            File destFile2 = new File(destFile, LIB_SO_NAME);
                            if (destFile2.exists()) {
                                FileLog.d("tmessages", "load normal lib");
                                System.loadLibrary(LIB_NAME);
                                init(Constants.FILES_PATH, BuildVars.DEBUG_VERSION);
                                nativeLoaded = true;
                            }
                            destFile = destFile2;
                        }
                    } catch (Throwable e) {
                        FileLog.e("tmessages", e);
                    } catch (Throwable e2) {
                        e2.printStackTrace();
                    }
                } catch (Throwable e22) {
                    FileLog.e("tmessages", e22);
                    folder = "armeabi";
                }
                File destDir = new File(context.getFilesDir(), "lib");
                destDir.mkdirs();
                File destLocalFile = new File(destDir, LOCALE_LIB_SO_NAME);
                if (destLocalFile.exists()) {
                    try {
                        FileLog.d("tmessages", "Load local lib");
                        System.load(destLocalFile.getAbsolutePath());
                        init(Constants.FILES_PATH, BuildVars.DEBUG_VERSION);
                        nativeLoaded = true;
                    } catch (Throwable e222) {
                        FileLog.e("tmessages", e222);
                        destLocalFile.delete();
                    }
                }
                FileLog.e("tmessages", "Library not found, arch = " + folder);
            }
        }
        try {
            System.loadLibrary(LIB_NAME);
            init(Constants.FILES_PATH, BuildVars.DEBUG_VERSION);
            nativeLoaded = true;
        } catch (Throwable e2222) {
            FileLog.e("tmessages", e2222);
        }
    }
}

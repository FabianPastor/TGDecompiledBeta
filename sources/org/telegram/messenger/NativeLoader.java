package org.telegram.messenger;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import java.io.File;
import java.io.FileOutputStream;
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
        File file;
        if (context != null) {
            try {
                file = new File((String) ApplicationInfo.class.getField("nativeLibraryDir").get(context.getApplicationInfo()));
            } catch (Throwable th) {
                th.printStackTrace();
            }
            if (file == null) {
                file = new File(context.getApplicationInfo().dataDir, "lib");
            }
            if (file.isDirectory() == null) {
                return file;
            }
            return null;
        }
        file = null;
        if (file == null) {
            file = new File(context.getApplicationInfo().dataDir, "lib");
        }
        if (file.isDirectory() == null) {
            return null;
        }
        return file;
    }

    @SuppressLint({"UnsafeDynamicallyLoadedCode", "SetWorldReadable"})
    private static boolean loadFromZip(Context context, File file, File file2, String str) {
        int read;
        Throwable e;
        File file3;
        try {
            for (File delete : file.listFiles()) {
                delete.delete();
            }
        } catch (Throwable e2) {
            FileLog.m3e(e2);
        }
        file = null;
        ZipFile zipFile;
        try {
            zipFile = new ZipFile(context.getApplicationInfo().sourceDir);
            try {
                context = new StringBuilder();
                context.append("lib/");
                context.append(str);
                context.append("/");
                context.append(LIB_SO_NAME);
                context = zipFile.getEntry(context.toString());
                if (context == null) {
                    file2 = new StringBuilder();
                    file2.append("Unable to find file in apk:lib/");
                    file2.append(str);
                    file2.append("/");
                    file2.append(LIB_NAME);
                    throw new Exception(file2.toString());
                }
                context = zipFile.getInputStream(context);
                try {
                    file = new FileOutputStream(file2);
                    str = new byte[4096];
                    while (true) {
                        read = context.read(str);
                        if (read <= 0) {
                            break;
                        }
                        Thread.yield();
                        file.write(str, 0, read);
                    }
                    file.close();
                    file2.setReadable(true, false);
                    file2.setExecutable(true, false);
                    file2.setWritable(true);
                    try {
                        System.load(file2.getAbsolutePath());
                        nativeLoaded = true;
                    } catch (Throwable e3) {
                        FileLog.m3e(e3);
                    }
                    if (context != null) {
                        try {
                            context.close();
                        } catch (Throwable e4) {
                            FileLog.m3e(e4);
                        }
                    }
                    if (zipFile != null) {
                        try {
                            zipFile.close();
                        } catch (Throwable e42) {
                            FileLog.m3e(e42);
                        }
                    }
                    return true;
                } catch (File file4) {
                    file3 = file4;
                    file4 = context;
                    e42 = file3;
                    try {
                        FileLog.m3e(e42);
                        if (file4 != null) {
                            try {
                                file4.close();
                            } catch (Throwable e422) {
                                FileLog.m3e(e422);
                            }
                        }
                        if (zipFile != null) {
                            try {
                                zipFile.close();
                            } catch (Throwable e4222) {
                                FileLog.m3e(e4222);
                            }
                        }
                        return false;
                    } catch (Throwable th) {
                        context = th;
                        if (file4 != null) {
                            try {
                                file4.close();
                            } catch (Throwable e22) {
                                FileLog.m3e(e22);
                            }
                        }
                        if (zipFile != null) {
                            try {
                                zipFile.close();
                            } catch (Throwable e222) {
                                FileLog.m3e(e222);
                            }
                        }
                        throw context;
                    }
                } catch (File file42) {
                    file3 = file42;
                    file42 = context;
                    context = file3;
                    if (file42 != null) {
                        file42.close();
                    }
                    if (zipFile != null) {
                        zipFile.close();
                    }
                    throw context;
                }
            } catch (Exception e5) {
                e4222 = e5;
                FileLog.m3e(e4222);
                if (file42 != null) {
                    file42.close();
                }
                if (zipFile != null) {
                    zipFile.close();
                }
                return false;
            }
        } catch (Exception e6) {
            e4222 = e6;
            zipFile = null;
            FileLog.m3e(e4222);
            if (file42 != null) {
                file42.close();
            }
            if (zipFile != null) {
                zipFile.close();
            }
            return false;
        } catch (Throwable th2) {
            context = th2;
            zipFile = null;
            if (file42 != null) {
                file42.close();
            }
            if (zipFile != null) {
                zipFile.close();
            }
            throw context;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @SuppressLint({"UnsafeDynamicallyLoadedCode"})
    public static synchronized void initNativeLibs(Context context) {
        String str;
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
                try {
                    FileLog.m3e(e);
                    try {
                        str = Build.CPU_ABI;
                        if (Build.CPU_ABI.equalsIgnoreCase("x86_64")) {
                            str = "x86_64";
                        } else if (Build.CPU_ABI.equalsIgnoreCase("arm64-v8a")) {
                            str = "arm64-v8a";
                        } else if (Build.CPU_ABI.equalsIgnoreCase("armeabi-v7a")) {
                            str = "armeabi-v7a";
                        } else if (Build.CPU_ABI.equalsIgnoreCase("armeabi")) {
                            str = "armeabi";
                        } else if (Build.CPU_ABI.equalsIgnoreCase("x86")) {
                            str = "x86";
                        } else if (Build.CPU_ABI.equalsIgnoreCase("mips")) {
                            str = "mips";
                        } else {
                            str = "armeabi";
                            if (BuildVars.LOGS_ENABLED) {
                                StringBuilder stringBuilder2 = new StringBuilder();
                                stringBuilder2.append("Unsupported arch: ");
                                stringBuilder2.append(Build.CPU_ABI);
                                FileLog.m1e(stringBuilder2.toString());
                            }
                        }
                    } catch (Throwable e2) {
                        FileLog.m3e(e2);
                        str = "armeabi";
                    }
                    String property = System.getProperty("os.arch");
                    if (property != null && property.contains("686")) {
                        str = "x86";
                    }
                    File file = new File(context.getFilesDir(), "lib");
                    file.mkdirs();
                    File file2 = new File(file, LOCALE_LIB_SO_NAME);
                    if (file2.exists()) {
                        try {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.m0d("Load local lib");
                            }
                            System.load(file2.getAbsolutePath());
                            nativeLoaded = true;
                            return;
                        } catch (Throwable e3) {
                            FileLog.m3e(e3);
                            file2.delete();
                            if (BuildVars.LOGS_ENABLED) {
                                stringBuilder = new StringBuilder();
                                stringBuilder.append("Library not found, arch = ");
                                stringBuilder.append(str);
                                FileLog.m1e(stringBuilder.toString());
                            }
                            if (loadFromZip(context, file, file2, str) != null) {
                                return;
                            }
                            System.loadLibrary(LIB_NAME);
                            nativeLoaded = true;
                            return;
                        }
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("Library not found, arch = ");
                        stringBuilder.append(str);
                        FileLog.m1e(stringBuilder.toString());
                    }
                    if (loadFromZip(context, file, file2, str) != null) {
                        return;
                    }
                } catch (Context context2) {
                    context2.printStackTrace();
                }
                try {
                    System.loadLibrary(LIB_NAME);
                    nativeLoaded = true;
                } catch (Throwable e4) {
                    FileLog.m3e(e4);
                }
                return;
            }
        }
    }
}

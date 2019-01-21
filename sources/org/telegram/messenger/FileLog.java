package org.telegram.messenger;

import android.util.Log;
import com.google.devtools.build.android.desugar.runtime.ThrowableExtension;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Locale;
import org.telegram.messenger.time.FastDateFormat;

public class FileLog {
    private static volatile FileLog Instance = null;
    private static final String tag = "tmessages";
    private File currentFile = null;
    private FastDateFormat dateFormat = null;
    private boolean initied;
    private DispatchQueue logQueue = null;
    private File networkFile = null;
    private OutputStreamWriter streamWriter = null;

    public static FileLog getInstance() {
        Throwable th;
        FileLog localInstance = Instance;
        if (localInstance == null) {
            synchronized (FileLog.class) {
                try {
                    localInstance = Instance;
                    if (localInstance == null) {
                        FileLog localInstance2 = new FileLog();
                        try {
                            Instance = localInstance2;
                            localInstance = localInstance2;
                        } catch (Throwable th2) {
                            th = th2;
                            localInstance = localInstance2;
                            throw th;
                        }
                    }
                } catch (Throwable th3) {
                    th = th3;
                    throw th;
                }
            }
        }
        return localInstance;
    }

    public FileLog() {
        if (BuildVars.LOGS_ENABLED) {
            init();
        }
    }

    public void init() {
        if (!this.initied) {
            this.dateFormat = FastDateFormat.getInstance("dd_MM_yyyy_HH_mm_ss", Locale.US);
            try {
                File sdCard = ApplicationLoader.applicationContext.getExternalFilesDir(null);
                if (sdCard != null) {
                    File dir = new File(sdCard.getAbsolutePath() + "/logs");
                    dir.mkdirs();
                    this.currentFile = new File(dir, this.dateFormat.format(System.currentTimeMillis()) + ".txt");
                    try {
                        this.logQueue = new DispatchQueue("logQueue");
                        this.currentFile.createNewFile();
                        this.streamWriter = new OutputStreamWriter(new FileOutputStream(this.currentFile));
                        this.streamWriter.write("-----start log " + this.dateFormat.format(System.currentTimeMillis()) + "-----\n");
                        this.streamWriter.flush();
                    } catch (Exception e) {
                        ThrowableExtension.printStackTrace(e);
                    }
                    this.initied = true;
                }
            } catch (Exception e2) {
                ThrowableExtension.printStackTrace(e2);
            }
        }
    }

    public static void ensureInitied() {
        getInstance().init();
    }

    public static String getNetworkLogPath() {
        if (!BuildVars.LOGS_ENABLED) {
            return "";
        }
        try {
            File sdCard = ApplicationLoader.applicationContext.getExternalFilesDir(null);
            if (sdCard == null) {
                return "";
            }
            File dir = new File(sdCard.getAbsolutePath() + "/logs");
            dir.mkdirs();
            getInstance().networkFile = new File(dir, getInstance().dateFormat.format(System.currentTimeMillis()) + "_net.txt");
            return getInstance().networkFile.getAbsolutePath();
        } catch (Throwable e) {
            ThrowableExtension.printStackTrace(e);
            return "";
        }
    }

    public static void e(String message, Throwable exception) {
        if (BuildVars.LOGS_ENABLED) {
            ensureInitied();
            Log.e("tmessages", message, exception);
            if (getInstance().streamWriter != null) {
                getInstance().logQueue.postRunnable(new FileLog$$Lambda$0(message, exception));
            }
        }
    }

    static final /* synthetic */ void lambda$e$0$FileLog(String message, Throwable exception) {
        try {
            getInstance().streamWriter.write(getInstance().dateFormat.format(System.currentTimeMillis()) + " E/tmessages: " + message + "\n");
            getInstance().streamWriter.write(exception.toString());
            getInstance().streamWriter.flush();
        } catch (Exception e) {
            ThrowableExtension.printStackTrace(e);
        }
    }

    public static void e(String message) {
        if (BuildVars.LOGS_ENABLED) {
            ensureInitied();
            Log.e("tmessages", message);
            if (getInstance().streamWriter != null) {
                getInstance().logQueue.postRunnable(new FileLog$$Lambda$1(message));
            }
        }
    }

    static final /* synthetic */ void lambda$e$1$FileLog(String message) {
        try {
            getInstance().streamWriter.write(getInstance().dateFormat.format(System.currentTimeMillis()) + " E/tmessages: " + message + "\n");
            getInstance().streamWriter.flush();
        } catch (Exception e) {
            ThrowableExtension.printStackTrace(e);
        }
    }

    public static void e(Throwable e) {
        if (BuildVars.LOGS_ENABLED) {
            ensureInitied();
            ThrowableExtension.printStackTrace(e);
            if (getInstance().streamWriter != null) {
                getInstance().logQueue.postRunnable(new FileLog$$Lambda$2(e));
            } else {
                ThrowableExtension.printStackTrace(e);
            }
        }
    }

    static final /* synthetic */ void lambda$e$2$FileLog(Throwable e) {
        try {
            getInstance().streamWriter.write(getInstance().dateFormat.format(System.currentTimeMillis()) + " E/tmessages: " + e + "\n");
            StackTraceElement[] stack = e.getStackTrace();
            for (Object obj : stack) {
                getInstance().streamWriter.write(getInstance().dateFormat.format(System.currentTimeMillis()) + " E/tmessages: " + obj + "\n");
            }
            getInstance().streamWriter.flush();
        } catch (Exception e1) {
            ThrowableExtension.printStackTrace(e1);
        }
    }

    public static void d(String message) {
        if (BuildVars.LOGS_ENABLED) {
            ensureInitied();
            Log.d("tmessages", message);
            if (getInstance().streamWriter != null) {
                getInstance().logQueue.postRunnable(new FileLog$$Lambda$3(message));
            }
        }
    }

    static final /* synthetic */ void lambda$d$3$FileLog(String message) {
        try {
            getInstance().streamWriter.write(getInstance().dateFormat.format(System.currentTimeMillis()) + " D/tmessages: " + message + "\n");
            getInstance().streamWriter.flush();
        } catch (Exception e) {
            ThrowableExtension.printStackTrace(e);
        }
    }

    public static void w(String message) {
        if (BuildVars.LOGS_ENABLED) {
            ensureInitied();
            Log.w("tmessages", message);
            if (getInstance().streamWriter != null) {
                getInstance().logQueue.postRunnable(new FileLog$$Lambda$4(message));
            }
        }
    }

    static final /* synthetic */ void lambda$w$4$FileLog(String message) {
        try {
            getInstance().streamWriter.write(getInstance().dateFormat.format(System.currentTimeMillis()) + " W/tmessages: " + message + "\n");
            getInstance().streamWriter.flush();
        } catch (Exception e) {
            ThrowableExtension.printStackTrace(e);
        }
    }

    public static void cleanupLogs() {
        ensureInitied();
        File sdCard = ApplicationLoader.applicationContext.getExternalFilesDir(null);
        if (sdCard != null) {
            File[] files = new File(sdCard.getAbsolutePath() + "/logs").listFiles();
            if (files != null) {
                for (File file : files) {
                    if ((getInstance().currentFile == null || !file.getAbsolutePath().equals(getInstance().currentFile.getAbsolutePath())) && (getInstance().networkFile == null || !file.getAbsolutePath().equals(getInstance().networkFile.getAbsolutePath()))) {
                        file.delete();
                    }
                }
            }
        }
    }
}

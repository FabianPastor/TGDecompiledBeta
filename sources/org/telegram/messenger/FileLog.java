package org.telegram.messenger;

import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Locale;
import org.telegram.messenger.time.FastDateFormat;
import org.telegram.messenger.video.MediaCodecVideoConvertor;

public class FileLog {
    private static volatile FileLog Instance = null;
    private static final String tag = "tmessages";
    private File currentFile = null;
    private FastDateFormat dateFormat = null;
    private boolean initied;
    private DispatchQueue logQueue = null;
    private File networkFile = null;
    private OutputStreamWriter streamWriter = null;
    private File tonlibFile = null;

    public static FileLog getInstance() {
        FileLog localInstance = Instance;
        if (localInstance == null) {
            synchronized (FileLog.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    FileLog fileLog = new FileLog();
                    localInstance = fileLog;
                    Instance = fileLog;
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
                File sdCard = ApplicationLoader.applicationContext.getExternalFilesDir((String) null);
                if (sdCard != null) {
                    File dir = new File(sdCard.getAbsolutePath() + "/logs");
                    dir.mkdirs();
                    this.currentFile = new File(dir, this.dateFormat.format(System.currentTimeMillis()) + ".txt");
                    try {
                        this.logQueue = new DispatchQueue("logQueue");
                        this.currentFile.createNewFile();
                        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(this.currentFile));
                        this.streamWriter = outputStreamWriter;
                        outputStreamWriter.write("-----start log " + this.dateFormat.format(System.currentTimeMillis()) + "-----\n");
                        this.streamWriter.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    this.initied = true;
                }
            } catch (Exception e2) {
                e2.printStackTrace();
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
            File sdCard = ApplicationLoader.applicationContext.getExternalFilesDir((String) null);
            if (sdCard == null) {
                return "";
            }
            File dir = new File(sdCard.getAbsolutePath() + "/logs");
            dir.mkdirs();
            FileLog instance = getInstance();
            instance.networkFile = new File(dir, getInstance().dateFormat.format(System.currentTimeMillis()) + "_net.txt");
            return getInstance().networkFile.getAbsolutePath();
        } catch (Throwable e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getTonlibLogPath() {
        if (!BuildVars.LOGS_ENABLED) {
            return "";
        }
        try {
            File sdCard = ApplicationLoader.applicationContext.getExternalFilesDir((String) null);
            if (sdCard == null) {
                return "";
            }
            File dir = new File(sdCard.getAbsolutePath() + "/logs");
            dir.mkdirs();
            FileLog instance = getInstance();
            instance.tonlibFile = new File(dir, getInstance().dateFormat.format(System.currentTimeMillis()) + "_tonlib.txt");
            return getInstance().tonlibFile.getAbsolutePath();
        } catch (Throwable e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void e(String message, Throwable exception) {
        if (BuildVars.LOGS_ENABLED) {
            ensureInitied();
            Log.e("tmessages", message, exception);
            if (getInstance().streamWriter != null) {
                getInstance().logQueue.postRunnable(new FileLog$$ExternalSyntheticLambda3(message, exception));
            }
        }
    }

    static /* synthetic */ void lambda$e$0(String message, Throwable exception) {
        try {
            OutputStreamWriter outputStreamWriter = getInstance().streamWriter;
            outputStreamWriter.write(getInstance().dateFormat.format(System.currentTimeMillis()) + " E/tmessages: " + message + "\n");
            getInstance().streamWriter.write(exception.toString());
            getInstance().streamWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void e(String message) {
        if (BuildVars.LOGS_ENABLED) {
            ensureInitied();
            Log.e("tmessages", message);
            if (getInstance().streamWriter != null) {
                getInstance().logQueue.postRunnable(new FileLog$$ExternalSyntheticLambda1(message));
            }
        }
    }

    static /* synthetic */ void lambda$e$1(String message) {
        try {
            OutputStreamWriter outputStreamWriter = getInstance().streamWriter;
            outputStreamWriter.write(getInstance().dateFormat.format(System.currentTimeMillis()) + " E/tmessages: " + message + "\n");
            getInstance().streamWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void e(Throwable e) {
        e(e, true);
    }

    public static void e(Throwable e, boolean logToAppCenter) {
        if (BuildVars.LOGS_ENABLED) {
            if (BuildVars.DEBUG_VERSION && needSent(e) && logToAppCenter) {
                AndroidUtilities.appCenterLog(e);
            }
            ensureInitied();
            e.printStackTrace();
            if (getInstance().streamWriter != null) {
                getInstance().logQueue.postRunnable(new FileLog$$ExternalSyntheticLambda4(e));
            } else {
                e.printStackTrace();
            }
        }
    }

    static /* synthetic */ void lambda$e$2(Throwable e) {
        try {
            OutputStreamWriter outputStreamWriter = getInstance().streamWriter;
            outputStreamWriter.write(getInstance().dateFormat.format(System.currentTimeMillis()) + " E/tmessages: " + e + "\n");
            StackTraceElement[] stack = e.getStackTrace();
            for (int a = 0; a < stack.length; a++) {
                OutputStreamWriter outputStreamWriter2 = getInstance().streamWriter;
                outputStreamWriter2.write(getInstance().dateFormat.format(System.currentTimeMillis()) + " E/tmessages: " + stack[a] + "\n");
            }
            getInstance().streamWriter.flush();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    private static boolean needSent(Throwable e) {
        if ((e instanceof InterruptedException) || (e instanceof MediaCodecVideoConvertor.ConversionCanceledException)) {
            return false;
        }
        return true;
    }

    public static void d(String message) {
        if (BuildVars.LOGS_ENABLED) {
            ensureInitied();
            Log.d("tmessages", message);
            if (getInstance().streamWriter != null) {
                getInstance().logQueue.postRunnable(new FileLog$$ExternalSyntheticLambda0(message));
            }
        }
    }

    static /* synthetic */ void lambda$d$3(String message) {
        try {
            OutputStreamWriter outputStreamWriter = getInstance().streamWriter;
            outputStreamWriter.write(getInstance().dateFormat.format(System.currentTimeMillis()) + " D/tmessages: " + message + "\n");
            getInstance().streamWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void w(String message) {
        if (BuildVars.LOGS_ENABLED) {
            ensureInitied();
            Log.w("tmessages", message);
            if (getInstance().streamWriter != null) {
                getInstance().logQueue.postRunnable(new FileLog$$ExternalSyntheticLambda2(message));
            }
        }
    }

    static /* synthetic */ void lambda$w$4(String message) {
        try {
            OutputStreamWriter outputStreamWriter = getInstance().streamWriter;
            outputStreamWriter.write(getInstance().dateFormat.format(System.currentTimeMillis()) + " W/tmessages: " + message + "\n");
            getInstance().streamWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void cleanupLogs() {
        ensureInitied();
        File sdCard = ApplicationLoader.applicationContext.getExternalFilesDir((String) null);
        if (sdCard != null) {
            File[] files = new File(sdCard.getAbsolutePath() + "/logs").listFiles();
            if (files != null) {
                for (File file : files) {
                    if ((getInstance().currentFile == null || !file.getAbsolutePath().equals(getInstance().currentFile.getAbsolutePath())) && ((getInstance().networkFile == null || !file.getAbsolutePath().equals(getInstance().networkFile.getAbsolutePath())) && (getInstance().tonlibFile == null || !file.getAbsolutePath().equals(getInstance().tonlibFile.getAbsolutePath())))) {
                        file.delete();
                    }
                }
            }
        }
    }
}

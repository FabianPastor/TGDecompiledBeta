package org.telegram.messenger;

import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Locale;
import org.telegram.messenger.time.FastDateFormat;
import org.telegram.messenger.video.MediaCodecVideoConvertor;
/* loaded from: classes.dex */
public class FileLog {
    private static volatile FileLog Instance = null;
    private static final String tag = "tmessages";
    private boolean initied;
    private OutputStreamWriter streamWriter = null;
    private FastDateFormat dateFormat = null;
    private DispatchQueue logQueue = null;
    private File currentFile = null;
    private File networkFile = null;
    private File tonlibFile = null;

    public static FileLog getInstance() {
        FileLog fileLog = Instance;
        if (fileLog == null) {
            synchronized (FileLog.class) {
                fileLog = Instance;
                if (fileLog == null) {
                    fileLog = new FileLog();
                    Instance = fileLog;
                }
            }
        }
        return fileLog;
    }

    public FileLog() {
        if (!BuildVars.LOGS_ENABLED) {
            return;
        }
        init();
    }

    public void init() {
        File externalFilesDir;
        if (this.initied) {
            return;
        }
        this.dateFormat = FastDateFormat.getInstance("dd_MM_yyyy_HH_mm_ss", Locale.US);
        try {
            externalFilesDir = ApplicationLoader.applicationContext.getExternalFilesDir(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (externalFilesDir == null) {
            return;
        }
        File file = new File(externalFilesDir.getAbsolutePath() + "/logs");
        file.mkdirs();
        this.currentFile = new File(file, this.dateFormat.format(System.currentTimeMillis()) + ".txt");
        try {
            this.logQueue = new DispatchQueue("logQueue");
            this.currentFile.createNewFile();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(this.currentFile));
            this.streamWriter = outputStreamWriter;
            outputStreamWriter.write("-----start log " + this.dateFormat.format(System.currentTimeMillis()) + "-----\n");
            this.streamWriter.flush();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        this.initied = true;
    }

    public static void ensureInitied() {
        getInstance().init();
    }

    public static String getNetworkLogPath() {
        if (!BuildVars.LOGS_ENABLED) {
            return "";
        }
        try {
            File externalFilesDir = ApplicationLoader.applicationContext.getExternalFilesDir(null);
            if (externalFilesDir == null) {
                return "";
            }
            File file = new File(externalFilesDir.getAbsolutePath() + "/logs");
            file.mkdirs();
            FileLog fileLog = getInstance();
            fileLog.networkFile = new File(file, getInstance().dateFormat.format(System.currentTimeMillis()) + "_net.txt");
            return getInstance().networkFile.getAbsolutePath();
        } catch (Throwable th) {
            th.printStackTrace();
            return "";
        }
    }

    public static String getTonlibLogPath() {
        if (!BuildVars.LOGS_ENABLED) {
            return "";
        }
        try {
            File externalFilesDir = ApplicationLoader.applicationContext.getExternalFilesDir(null);
            if (externalFilesDir == null) {
                return "";
            }
            File file = new File(externalFilesDir.getAbsolutePath() + "/logs");
            file.mkdirs();
            FileLog fileLog = getInstance();
            fileLog.tonlibFile = new File(file, getInstance().dateFormat.format(System.currentTimeMillis()) + "_tonlib.txt");
            return getInstance().tonlibFile.getAbsolutePath();
        } catch (Throwable th) {
            th.printStackTrace();
            return "";
        }
    }

    public static void e(final String str, final Throwable th) {
        if (!BuildVars.LOGS_ENABLED) {
            return;
        }
        ensureInitied();
        Log.e("tmessages", str, th);
        if (getInstance().streamWriter == null) {
            return;
        }
        getInstance().logQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.FileLog$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                FileLog.lambda$e$0(str, th);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$e$0(String str, Throwable th) {
        try {
            OutputStreamWriter outputStreamWriter = getInstance().streamWriter;
            outputStreamWriter.write(getInstance().dateFormat.format(System.currentTimeMillis()) + " E/tmessages: " + str + "\n");
            getInstance().streamWriter.write(th.toString());
            getInstance().streamWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void e(final String str) {
        if (!BuildVars.LOGS_ENABLED) {
            return;
        }
        ensureInitied();
        Log.e("tmessages", str);
        if (getInstance().streamWriter == null) {
            return;
        }
        getInstance().logQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.FileLog$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                FileLog.lambda$e$1(str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$e$1(String str) {
        try {
            OutputStreamWriter outputStreamWriter = getInstance().streamWriter;
            outputStreamWriter.write(getInstance().dateFormat.format(System.currentTimeMillis()) + " E/tmessages: " + str + "\n");
            getInstance().streamWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void e(Throwable th) {
        e(th, true);
    }

    public static void e(final Throwable th, boolean z) {
        if (!BuildVars.LOGS_ENABLED) {
            return;
        }
        if (BuildVars.DEBUG_VERSION && needSent(th) && z) {
            AndroidUtilities.appCenterLog(th);
        }
        ensureInitied();
        th.printStackTrace();
        if (getInstance().streamWriter != null) {
            getInstance().logQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.FileLog$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    FileLog.lambda$e$2(th);
                }
            });
        } else {
            th.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$e$2(Throwable th) {
        try {
            OutputStreamWriter outputStreamWriter = getInstance().streamWriter;
            outputStreamWriter.write(getInstance().dateFormat.format(System.currentTimeMillis()) + " E/tmessages: " + th + "\n");
            StackTraceElement[] stackTrace = th.getStackTrace();
            for (int i = 0; i < stackTrace.length; i++) {
                OutputStreamWriter outputStreamWriter2 = getInstance().streamWriter;
                outputStreamWriter2.write(getInstance().dateFormat.format(System.currentTimeMillis()) + " E/tmessages: " + stackTrace[i] + "\n");
            }
            getInstance().streamWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void fatal(Throwable th) {
        fatal(th, true);
    }

    public static void fatal(final Throwable th, boolean z) {
        if (!BuildVars.LOGS_ENABLED) {
            return;
        }
        if (BuildVars.DEBUG_VERSION && needSent(th) && z) {
            AndroidUtilities.appCenterLog(th);
        }
        ensureInitied();
        th.printStackTrace();
        if (getInstance().streamWriter != null) {
            getInstance().logQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.FileLog$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    FileLog.lambda$fatal$3(th);
                }
            });
            return;
        }
        th.printStackTrace();
        if (!BuildVars.DEBUG_PRIVATE_VERSION) {
            return;
        }
        System.exit(2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$fatal$3(Throwable th) {
        try {
            OutputStreamWriter outputStreamWriter = getInstance().streamWriter;
            outputStreamWriter.write(getInstance().dateFormat.format(System.currentTimeMillis()) + " E/tmessages: " + th + "\n");
            StackTraceElement[] stackTrace = th.getStackTrace();
            for (int i = 0; i < stackTrace.length; i++) {
                OutputStreamWriter outputStreamWriter2 = getInstance().streamWriter;
                outputStreamWriter2.write(getInstance().dateFormat.format(System.currentTimeMillis()) + " E/tmessages: " + stackTrace[i] + "\n");
            }
            getInstance().streamWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (BuildVars.DEBUG_PRIVATE_VERSION) {
            System.exit(2);
        }
    }

    private static boolean needSent(Throwable th) {
        return !(th instanceof InterruptedException) && !(th instanceof MediaCodecVideoConvertor.ConversionCanceledException);
    }

    public static void d(final String str) {
        if (!BuildVars.LOGS_ENABLED) {
            return;
        }
        ensureInitied();
        Log.d("tmessages", str);
        if (getInstance().streamWriter == null) {
            return;
        }
        getInstance().logQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.FileLog$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                FileLog.lambda$d$4(str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$d$4(String str) {
        try {
            OutputStreamWriter outputStreamWriter = getInstance().streamWriter;
            outputStreamWriter.write(getInstance().dateFormat.format(System.currentTimeMillis()) + " D/tmessages: " + str + "\n");
            getInstance().streamWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void w(final String str) {
        if (!BuildVars.LOGS_ENABLED) {
            return;
        }
        ensureInitied();
        Log.w("tmessages", str);
        if (getInstance().streamWriter == null) {
            return;
        }
        getInstance().logQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.FileLog$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                FileLog.lambda$w$5(str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$w$5(String str) {
        try {
            OutputStreamWriter outputStreamWriter = getInstance().streamWriter;
            outputStreamWriter.write(getInstance().dateFormat.format(System.currentTimeMillis()) + " W/tmessages: " + str + "\n");
            getInstance().streamWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void cleanupLogs() {
        File externalFilesDir;
        ensureInitied();
        if (ApplicationLoader.applicationContext.getExternalFilesDir(null) == null) {
            return;
        }
        File[] listFiles = new File(externalFilesDir.getAbsolutePath() + "/logs").listFiles();
        if (listFiles == null) {
            return;
        }
        for (File file : listFiles) {
            if ((getInstance().currentFile == null || !file.getAbsolutePath().equals(getInstance().currentFile.getAbsolutePath())) && ((getInstance().networkFile == null || !file.getAbsolutePath().equals(getInstance().networkFile.getAbsolutePath())) && (getInstance().tonlibFile == null || !file.getAbsolutePath().equals(getInstance().tonlibFile.getAbsolutePath())))) {
                file.delete();
            }
        }
    }
}

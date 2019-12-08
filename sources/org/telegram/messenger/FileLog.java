package org.telegram.messenger;

import android.util.Log;
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
        if (BuildVars.LOGS_ENABLED) {
            init();
        }
    }

    public void init() {
        if (!this.initied) {
            this.dateFormat = FastDateFormat.getInstance("dd_MM_yyyy_HH_mm_ss", Locale.US);
            try {
                File externalFilesDir = ApplicationLoader.applicationContext.getExternalFilesDir(null);
                if (externalFilesDir != null) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(externalFilesDir.getAbsolutePath());
                    stringBuilder.append("/logs");
                    File file = new File(stringBuilder.toString());
                    file.mkdirs();
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(this.dateFormat.format(System.currentTimeMillis()));
                    stringBuilder.append(".txt");
                    this.currentFile = new File(file, stringBuilder.toString());
                    try {
                        this.logQueue = new DispatchQueue("logQueue");
                        this.currentFile.createNewFile();
                        this.streamWriter = new OutputStreamWriter(new FileOutputStream(this.currentFile));
                        OutputStreamWriter outputStreamWriter = this.streamWriter;
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("-----start log ");
                        stringBuilder2.append(this.dateFormat.format(System.currentTimeMillis()));
                        stringBuilder2.append("-----\n");
                        outputStreamWriter.write(stringBuilder2.toString());
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
        String str = "";
        if (!BuildVars.LOGS_ENABLED) {
            return str;
        }
        try {
            File externalFilesDir = ApplicationLoader.applicationContext.getExternalFilesDir(null);
            if (externalFilesDir == null) {
                return str;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(externalFilesDir.getAbsolutePath());
            stringBuilder.append("/logs");
            File file = new File(stringBuilder.toString());
            file.mkdirs();
            FileLog instance = getInstance();
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append(getInstance().dateFormat.format(System.currentTimeMillis()));
            stringBuilder2.append("_net.txt");
            instance.networkFile = new File(file, stringBuilder2.toString());
            return getInstance().networkFile.getAbsolutePath();
        } catch (Throwable th) {
            th.printStackTrace();
            return str;
        }
    }

    public static String getTonlibLogPath() {
        String str = "";
        if (!BuildVars.LOGS_ENABLED) {
            return str;
        }
        try {
            File externalFilesDir = ApplicationLoader.applicationContext.getExternalFilesDir(null);
            if (externalFilesDir == null) {
                return str;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(externalFilesDir.getAbsolutePath());
            stringBuilder.append("/logs");
            File file = new File(stringBuilder.toString());
            file.mkdirs();
            FileLog instance = getInstance();
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append(getInstance().dateFormat.format(System.currentTimeMillis()));
            stringBuilder2.append("_tonlib.txt");
            instance.tonlibFile = new File(file, stringBuilder2.toString());
            return getInstance().tonlibFile.getAbsolutePath();
        } catch (Throwable th) {
            th.printStackTrace();
            return str;
        }
    }

    public static void e(String str, Throwable th) {
        if (BuildVars.LOGS_ENABLED) {
            ensureInitied();
            Log.e("tmessages", str, th);
            if (getInstance().streamWriter != null) {
                getInstance().logQueue.postRunnable(new -$$Lambda$FileLog$U3_7mjWjDO5tBtPnAliQ7P97K6k(str, th));
            }
        }
    }

    static /* synthetic */ void lambda$e$0(String str, Throwable th) {
        try {
            OutputStreamWriter outputStreamWriter = getInstance().streamWriter;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(getInstance().dateFormat.format(System.currentTimeMillis()));
            stringBuilder.append(" E/tmessages: ");
            stringBuilder.append(str);
            stringBuilder.append("\n");
            outputStreamWriter.write(stringBuilder.toString());
            getInstance().streamWriter.write(th.toString());
            getInstance().streamWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void e(String str) {
        if (BuildVars.LOGS_ENABLED) {
            ensureInitied();
            Log.e("tmessages", str);
            if (getInstance().streamWriter != null) {
                getInstance().logQueue.postRunnable(new -$$Lambda$FileLog$i7yDAXBrl68gcx0blG9TP5Nmrmw(str));
            }
        }
    }

    static /* synthetic */ void lambda$e$1(String str) {
        try {
            OutputStreamWriter outputStreamWriter = getInstance().streamWriter;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(getInstance().dateFormat.format(System.currentTimeMillis()));
            stringBuilder.append(" E/tmessages: ");
            stringBuilder.append(str);
            stringBuilder.append("\n");
            outputStreamWriter.write(stringBuilder.toString());
            getInstance().streamWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void e(Throwable th) {
        if (BuildVars.LOGS_ENABLED) {
            ensureInitied();
            th.printStackTrace();
            if (getInstance().streamWriter != null) {
                getInstance().logQueue.postRunnable(new -$$Lambda$FileLog$nIyr75ATOMaf7ha7_MTqnmuRzrQ(th));
            } else {
                th.printStackTrace();
            }
        }
    }

    static /* synthetic */ void lambda$e$2(Throwable th) {
        String str = "\n";
        String str2 = " E/tmessages: ";
        try {
            OutputStreamWriter outputStreamWriter = getInstance().streamWriter;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(getInstance().dateFormat.format(System.currentTimeMillis()));
            stringBuilder.append(str2);
            stringBuilder.append(th);
            stringBuilder.append(str);
            outputStreamWriter.write(stringBuilder.toString());
            StackTraceElement[] stackTrace = th.getStackTrace();
            for (Object append : stackTrace) {
                OutputStreamWriter outputStreamWriter2 = getInstance().streamWriter;
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append(getInstance().dateFormat.format(System.currentTimeMillis()));
                stringBuilder2.append(str2);
                stringBuilder2.append(append);
                stringBuilder2.append(str);
                outputStreamWriter2.write(stringBuilder2.toString());
            }
            getInstance().streamWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void d(String str) {
        if (BuildVars.LOGS_ENABLED) {
            ensureInitied();
            Log.d("tmessages", str);
            if (getInstance().streamWriter != null) {
                getInstance().logQueue.postRunnable(new -$$Lambda$FileLog$eWlFO8runhKiadqnBdmm0EveMAs(str));
            }
        }
    }

    static /* synthetic */ void lambda$d$3(String str) {
        try {
            OutputStreamWriter outputStreamWriter = getInstance().streamWriter;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(getInstance().dateFormat.format(System.currentTimeMillis()));
            stringBuilder.append(" D/tmessages: ");
            stringBuilder.append(str);
            stringBuilder.append("\n");
            outputStreamWriter.write(stringBuilder.toString());
            getInstance().streamWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void w(String str) {
        if (BuildVars.LOGS_ENABLED) {
            ensureInitied();
            Log.w("tmessages", str);
            if (getInstance().streamWriter != null) {
                getInstance().logQueue.postRunnable(new -$$Lambda$FileLog$CtxtnEkTmpoT5uv6cLRigOvvNc8(str));
            }
        }
    }

    static /* synthetic */ void lambda$w$4(String str) {
        try {
            OutputStreamWriter outputStreamWriter = getInstance().streamWriter;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(getInstance().dateFormat.format(System.currentTimeMillis()));
            stringBuilder.append(" W/tmessages: ");
            stringBuilder.append(str);
            stringBuilder.append("\n");
            outputStreamWriter.write(stringBuilder.toString());
            getInstance().streamWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void cleanupLogs() {
        ensureInitied();
        File externalFilesDir = ApplicationLoader.applicationContext.getExternalFilesDir(null);
        if (externalFilesDir != null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(externalFilesDir.getAbsolutePath());
            stringBuilder.append("/logs");
            File[] listFiles = new File(stringBuilder.toString()).listFiles();
            if (listFiles != null) {
                for (File file : listFiles) {
                    if ((getInstance().currentFile == null || !file.getAbsolutePath().equals(getInstance().currentFile.getAbsolutePath())) && ((getInstance().networkFile == null || !file.getAbsolutePath().equals(getInstance().networkFile.getAbsolutePath())) && (getInstance().tonlibFile == null || !file.getAbsolutePath().equals(getInstance().tonlibFile.getAbsolutePath())))) {
                        file.delete();
                    }
                }
            }
        }
    }
}

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
        if (!BuildVars.LOGS_ENABLED) {
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
        try {
            File externalFilesDir = ApplicationLoader.applicationContext.getExternalFilesDir(null);
            if (externalFilesDir == null) {
                return TtmlNode.ANONYMOUS_REGION_ID;
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
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
    }

    /* renamed from: e */
    public static void m2e(final String str, final Throwable th) {
        if (BuildVars.LOGS_ENABLED) {
            ensureInitied();
            Log.e(tag, str, th);
            if (getInstance().streamWriter != null) {
                getInstance().logQueue.postRunnable(new Runnable() {
                    public void run() {
                        try {
                            OutputStreamWriter access$100 = FileLog.getInstance().streamWriter;
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(FileLog.getInstance().dateFormat.format(System.currentTimeMillis()));
                            stringBuilder.append(" E/tmessages: ");
                            stringBuilder.append(str);
                            stringBuilder.append("\n");
                            access$100.write(stringBuilder.toString());
                            FileLog.getInstance().streamWriter.write(th.toString());
                            FileLog.getInstance().streamWriter.flush();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }

    /* renamed from: e */
    public static void m1e(final String str) {
        if (BuildVars.LOGS_ENABLED) {
            ensureInitied();
            Log.e(tag, str);
            if (getInstance().streamWriter != null) {
                getInstance().logQueue.postRunnable(new Runnable() {
                    public void run() {
                        try {
                            OutputStreamWriter access$100 = FileLog.getInstance().streamWriter;
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(FileLog.getInstance().dateFormat.format(System.currentTimeMillis()));
                            stringBuilder.append(" E/tmessages: ");
                            stringBuilder.append(str);
                            stringBuilder.append("\n");
                            access$100.write(stringBuilder.toString());
                            FileLog.getInstance().streamWriter.flush();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }

    /* renamed from: e */
    public static void m3e(final Throwable th) {
        if (BuildVars.LOGS_ENABLED) {
            ensureInitied();
            th.printStackTrace();
            if (getInstance().streamWriter != null) {
                getInstance().logQueue.postRunnable(new Runnable() {
                    public void run() {
                        try {
                            OutputStreamWriter access$100 = FileLog.getInstance().streamWriter;
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(FileLog.getInstance().dateFormat.format(System.currentTimeMillis()));
                            stringBuilder.append(" E/tmessages: ");
                            stringBuilder.append(th);
                            stringBuilder.append("\n");
                            access$100.write(stringBuilder.toString());
                            StackTraceElement[] stackTrace = th.getStackTrace();
                            for (Object append : stackTrace) {
                                OutputStreamWriter access$1002 = FileLog.getInstance().streamWriter;
                                StringBuilder stringBuilder2 = new StringBuilder();
                                stringBuilder2.append(FileLog.getInstance().dateFormat.format(System.currentTimeMillis()));
                                stringBuilder2.append(" E/tmessages: ");
                                stringBuilder2.append(append);
                                stringBuilder2.append("\n");
                                access$1002.write(stringBuilder2.toString());
                            }
                            FileLog.getInstance().streamWriter.flush();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                th.printStackTrace();
            }
        }
    }

    /* renamed from: d */
    public static void m0d(final String str) {
        if (BuildVars.LOGS_ENABLED) {
            ensureInitied();
            Log.d(tag, str);
            if (getInstance().streamWriter != null) {
                getInstance().logQueue.postRunnable(new Runnable() {
                    public void run() {
                        try {
                            OutputStreamWriter access$100 = FileLog.getInstance().streamWriter;
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(FileLog.getInstance().dateFormat.format(System.currentTimeMillis()));
                            stringBuilder.append(" D/tmessages: ");
                            stringBuilder.append(str);
                            stringBuilder.append("\n");
                            access$100.write(stringBuilder.toString());
                            FileLog.getInstance().streamWriter.flush();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }

    /* renamed from: w */
    public static void m4w(final String str) {
        if (BuildVars.LOGS_ENABLED) {
            ensureInitied();
            Log.w(tag, str);
            if (getInstance().streamWriter != null) {
                getInstance().logQueue.postRunnable(new Runnable() {
                    public void run() {
                        try {
                            OutputStreamWriter access$100 = FileLog.getInstance().streamWriter;
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(FileLog.getInstance().dateFormat.format(System.currentTimeMillis()));
                            stringBuilder.append(" W/tmessages: ");
                            stringBuilder.append(str);
                            stringBuilder.append("\n");
                            access$100.write(stringBuilder.toString());
                            FileLog.getInstance().streamWriter.flush();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
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
                    if (getInstance().currentFile == null || !file.getAbsolutePath().equals(getInstance().currentFile.getAbsolutePath())) {
                        if (getInstance().networkFile == null || !file.getAbsolutePath().equals(getInstance().networkFile.getAbsolutePath())) {
                            file.delete();
                        }
                    }
                }
            }
        }
    }
}

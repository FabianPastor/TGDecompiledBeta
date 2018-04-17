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
                File sdCard = ApplicationLoader.applicationContext.getExternalFilesDir(null);
                if (sdCard != null) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(sdCard.getAbsolutePath());
                    stringBuilder.append("/logs");
                    File dir = new File(stringBuilder.toString());
                    dir.mkdirs();
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append(this.dateFormat.format(System.currentTimeMillis()));
                    stringBuilder2.append(".txt");
                    this.currentFile = new File(dir, stringBuilder2.toString());
                    try {
                        this.logQueue = new DispatchQueue("logQueue");
                        this.currentFile.createNewFile();
                        this.streamWriter = new OutputStreamWriter(new FileOutputStream(this.currentFile));
                        OutputStreamWriter outputStreamWriter = this.streamWriter;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("-----start log ");
                        stringBuilder.append(this.dateFormat.format(System.currentTimeMillis()));
                        stringBuilder.append("-----\n");
                        outputStreamWriter.write(stringBuilder.toString());
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
            File sdCard = ApplicationLoader.applicationContext.getExternalFilesDir(null);
            if (sdCard == null) {
                return TtmlNode.ANONYMOUS_REGION_ID;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(sdCard.getAbsolutePath());
            stringBuilder.append("/logs");
            File dir = new File(stringBuilder.toString());
            dir.mkdirs();
            FileLog instance = getInstance();
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append(getInstance().dateFormat.format(System.currentTimeMillis()));
            stringBuilder2.append("_net.txt");
            instance.networkFile = new File(dir, stringBuilder2.toString());
            return getInstance().networkFile.getAbsolutePath();
        } catch (Throwable e) {
            e.printStackTrace();
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
    }

    /* renamed from: e */
    public static void m2e(final String message, final Throwable exception) {
        if (BuildVars.LOGS_ENABLED) {
            ensureInitied();
            Log.e(tag, message, exception);
            if (getInstance().streamWriter != null) {
                getInstance().logQueue.postRunnable(new Runnable() {
                    public void run() {
                        try {
                            OutputStreamWriter access$100 = FileLog.getInstance().streamWriter;
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(FileLog.getInstance().dateFormat.format(System.currentTimeMillis()));
                            stringBuilder.append(" E/tmessages: ");
                            stringBuilder.append(message);
                            stringBuilder.append("\n");
                            access$100.write(stringBuilder.toString());
                            FileLog.getInstance().streamWriter.write(exception.toString());
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
    public static void m1e(final String message) {
        if (BuildVars.LOGS_ENABLED) {
            ensureInitied();
            Log.e(tag, message);
            if (getInstance().streamWriter != null) {
                getInstance().logQueue.postRunnable(new Runnable() {
                    public void run() {
                        try {
                            OutputStreamWriter access$100 = FileLog.getInstance().streamWriter;
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(FileLog.getInstance().dateFormat.format(System.currentTimeMillis()));
                            stringBuilder.append(" E/tmessages: ");
                            stringBuilder.append(message);
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
    public static void m3e(final Throwable e) {
        if (BuildVars.LOGS_ENABLED) {
            ensureInitied();
            e.printStackTrace();
            if (getInstance().streamWriter != null) {
                getInstance().logQueue.postRunnable(new Runnable() {
                    public void run() {
                        try {
                            OutputStreamWriter access$100 = FileLog.getInstance().streamWriter;
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(FileLog.getInstance().dateFormat.format(System.currentTimeMillis()));
                            stringBuilder.append(" E/tmessages: ");
                            stringBuilder.append(e);
                            stringBuilder.append("\n");
                            access$100.write(stringBuilder.toString());
                            StackTraceElement[] stack = e.getStackTrace();
                            for (Object append : stack) {
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
                e.printStackTrace();
            }
        }
    }

    /* renamed from: d */
    public static void m0d(final String message) {
        if (BuildVars.LOGS_ENABLED) {
            ensureInitied();
            Log.d(tag, message);
            if (getInstance().streamWriter != null) {
                getInstance().logQueue.postRunnable(new Runnable() {
                    public void run() {
                        try {
                            OutputStreamWriter access$100 = FileLog.getInstance().streamWriter;
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(FileLog.getInstance().dateFormat.format(System.currentTimeMillis()));
                            stringBuilder.append(" D/tmessages: ");
                            stringBuilder.append(message);
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
    public static void m4w(final String message) {
        if (BuildVars.LOGS_ENABLED) {
            ensureInitied();
            Log.w(tag, message);
            if (getInstance().streamWriter != null) {
                getInstance().logQueue.postRunnable(new Runnable() {
                    public void run() {
                        try {
                            OutputStreamWriter access$100 = FileLog.getInstance().streamWriter;
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(FileLog.getInstance().dateFormat.format(System.currentTimeMillis()));
                            stringBuilder.append(" W/tmessages: ");
                            stringBuilder.append(message);
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
        File sdCard = ApplicationLoader.applicationContext.getExternalFilesDir(null);
        if (sdCard != null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(sdCard.getAbsolutePath());
            stringBuilder.append("/logs");
            File[] files = new File(stringBuilder.toString()).listFiles();
            if (files != null) {
                for (File file : files) {
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

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
                try {
                    localInstance = Instance;
                    if (localInstance == null) {
                        FileLog localInstance2 = new FileLog();
                        try {
                            Instance = localInstance2;
                            localInstance = localInstance2;
                        } catch (Throwable th) {
                            Throwable th2 = th;
                            localInstance = localInstance2;
                            throw th2;
                        }
                    }
                } catch (Throwable th3) {
                    th2 = th3;
                    throw th2;
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
            File dir = new File(sdCard.getAbsolutePath() + "/logs");
            dir.mkdirs();
            getInstance().networkFile = new File(dir, getInstance().dateFormat.format(System.currentTimeMillis()) + "_net.txt");
            return getInstance().networkFile.getAbsolutePath();
        } catch (Throwable e) {
            e.printStackTrace();
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
    }

    public static void e(final String message, final Throwable exception) {
        if (BuildVars.LOGS_ENABLED) {
            ensureInitied();
            Log.e(tag, message, exception);
            if (getInstance().streamWriter != null) {
                getInstance().logQueue.postRunnable(new Runnable() {
                    public void run() {
                        try {
                            FileLog.getInstance().streamWriter.write(FileLog.getInstance().dateFormat.format(System.currentTimeMillis()) + " E/tmessages: " + message + "\n");
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

    public static void e(final String message) {
        if (BuildVars.LOGS_ENABLED) {
            ensureInitied();
            Log.e(tag, message);
            if (getInstance().streamWriter != null) {
                getInstance().logQueue.postRunnable(new Runnable() {
                    public void run() {
                        try {
                            FileLog.getInstance().streamWriter.write(FileLog.getInstance().dateFormat.format(System.currentTimeMillis()) + " E/tmessages: " + message + "\n");
                            FileLog.getInstance().streamWriter.flush();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }

    public static void e(final Throwable e) {
        if (BuildVars.LOGS_ENABLED) {
            ensureInitied();
            e.printStackTrace();
            if (getInstance().streamWriter != null) {
                getInstance().logQueue.postRunnable(new Runnable() {
                    public void run() {
                        try {
                            FileLog.getInstance().streamWriter.write(FileLog.getInstance().dateFormat.format(System.currentTimeMillis()) + " E/tmessages: " + e + "\n");
                            StackTraceElement[] stack = e.getStackTrace();
                            for (Object obj : stack) {
                                FileLog.getInstance().streamWriter.write(FileLog.getInstance().dateFormat.format(System.currentTimeMillis()) + " E/tmessages: " + obj + "\n");
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

    public static void d(final String message) {
        if (BuildVars.LOGS_ENABLED) {
            ensureInitied();
            Log.d(tag, message);
            if (getInstance().streamWriter != null) {
                getInstance().logQueue.postRunnable(new Runnable() {
                    public void run() {
                        try {
                            FileLog.getInstance().streamWriter.write(FileLog.getInstance().dateFormat.format(System.currentTimeMillis()) + " D/tmessages: " + message + "\n");
                            FileLog.getInstance().streamWriter.flush();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }

    public static void w(final String message) {
        if (BuildVars.LOGS_ENABLED) {
            ensureInitied();
            Log.w(tag, message);
            if (getInstance().streamWriter != null) {
                getInstance().logQueue.postRunnable(new Runnable() {
                    public void run() {
                        try {
                            FileLog.getInstance().streamWriter.write(FileLog.getInstance().dateFormat.format(System.currentTimeMillis()) + " W/tmessages: " + message + "\n");
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

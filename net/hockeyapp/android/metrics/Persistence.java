package net.hockeyapp.android.metrics;

import android.content.Context;
import android.text.TextUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.UUID;
import net.hockeyapp.android.utils.HockeyLog;

class Persistence {
    private static final String BIT_TELEMETRY_DIRECTORY = "/net.hockeyapp.android/telemetry/";
    private static final Object LOCK = new Object();
    private static final Integer MAX_FILE_COUNT = Integer.valueOf(50);
    private static final String TAG = "HA-MetricsPersistence";
    protected ArrayList<File> mServedFiles;
    protected final File mTelemetryDirectory;
    private final WeakReference<Context> mWeakContext;
    protected WeakReference<Sender> mWeakSender;

    protected Persistence(Context context, File telemetryDirectory, Sender sender) {
        this.mWeakContext = new WeakReference(context);
        this.mServedFiles = new ArrayList(51);
        this.mTelemetryDirectory = telemetryDirectory;
        this.mWeakSender = new WeakReference(sender);
        createDirectoriesIfNecessary();
    }

    protected Persistence(Context context, Sender sender) {
        this(context, new File(context.getFilesDir().getAbsolutePath() + BIT_TELEMETRY_DIRECTORY), null);
        setSender(sender);
    }

    protected void persist(String[] data) {
        if (isFreeSpaceAvailable()) {
            StringBuilder buffer = new StringBuilder();
            for (String aData : data) {
                if (buffer.length() > 0) {
                    buffer.append('\n');
                }
                buffer.append(aData);
            }
            if (writeToDisk(buffer.toString())) {
                getSender().triggerSending();
                return;
            }
            return;
        }
        HockeyLog.warn(TAG, "Failed to persist file: Too many files on disk.");
        getSender().triggerSending();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected boolean writeToDisk(String data) {
        Throwable th;
        String uuid = UUID.randomUUID().toString();
        Boolean isSuccess = Boolean.valueOf(false);
        FileOutputStream outputStream = null;
        try {
            synchronized (LOCK) {
                try {
                    File filesDir = new File(this.mTelemetryDirectory + "/" + uuid);
                    FileOutputStream outputStream2 = new FileOutputStream(filesDir, true);
                    try {
                        outputStream2.write(data.getBytes());
                        HockeyLog.warn(TAG, "Saving data to: " + filesDir.toString());
                    } catch (Throwable th2) {
                        th = th2;
                        outputStream = outputStream2;
                        throw th;
                    }
                } catch (Throwable th3) {
                    th = th3;
                    throw th;
                }
            }
        } catch (Exception e) {
            Exception e2 = e;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected String load(File file) {
        Throwable th;
        StringBuilder buffer = new StringBuilder();
        if (file != null) {
            BufferedReader reader = null;
            try {
                synchronized (LOCK) {
                    try {
                        BufferedReader reader2 = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                        while (true) {
                            try {
                                int c = reader2.read();
                                if (c == -1) {
                                    break;
                                }
                                buffer.append((char) c);
                            } catch (Throwable th2) {
                                th = th2;
                                reader = reader2;
                            }
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        throw th;
                    }
                }
            } catch (Exception e) {
                HockeyLog.warn(TAG, "Error reading telemetry data from file with exception message " + e.getMessage());
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e2) {
                        HockeyLog.warn(TAG, "Error closing stream." + e2.getMessage());
                    }
                }
            } catch (Throwable th4) {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e22) {
                        HockeyLog.warn(TAG, "Error closing stream." + e22.getMessage());
                    }
                }
            }
        }
        return buffer.toString();
    }

    protected boolean hasFilesAvailable() {
        return nextAvailableFileInDirectory() != null;
    }

    protected File nextAvailableFileInDirectory() {
        File file;
        synchronized (LOCK) {
            if (this.mTelemetryDirectory != null) {
                File[] files = this.mTelemetryDirectory.listFiles();
                if (files != null && files.length > 0) {
                    for (int i = 0; i <= files.length - 1; i++) {
                        file = files[i];
                        if (!this.mServedFiles.contains(file)) {
                            HockeyLog.info(TAG, "The directory " + file.toString() + " (ADDING TO SERVED AND RETURN)");
                            this.mServedFiles.add(file);
                            break;
                        }
                        HockeyLog.info(TAG, "The directory " + file.toString() + " (WAS ALREADY SERVED)");
                    }
                }
            }
            if (this.mTelemetryDirectory != null) {
                HockeyLog.info(TAG, "The directory " + this.mTelemetryDirectory.toString() + " did not contain any " + "unserved files");
            }
            file = null;
        }
        return file;
    }

    protected void deleteFile(File file) {
        if (file != null) {
            synchronized (LOCK) {
                if (file.delete()) {
                    HockeyLog.warn(TAG, "Successfully deleted telemetry file at: " + file.toString());
                    this.mServedFiles.remove(file);
                } else {
                    HockeyLog.warn(TAG, "Error deleting telemetry file " + file.toString());
                }
            }
            return;
        }
        HockeyLog.warn(TAG, "Couldn't delete file, the reference to the file was null");
    }

    protected void makeAvailable(File file) {
        synchronized (LOCK) {
            if (file != null) {
                this.mServedFiles.remove(file);
            }
        }
    }

    protected boolean isFreeSpaceAvailable() {
        boolean z = false;
        synchronized (LOCK) {
            Context context = getContext();
            if (context.getFilesDir() != null) {
                String path = context.getFilesDir().getAbsolutePath() + BIT_TELEMETRY_DIRECTORY;
                if (!TextUtils.isEmpty(path)) {
                    File[] files = new File(path).listFiles();
                    if (files != null && files.length < MAX_FILE_COUNT.intValue()) {
                        z = true;
                    }
                }
            }
        }
        return z;
    }

    protected void createDirectoriesIfNecessary() {
        String successMessage = "Successfully created directory";
        String errorMessage = "Error creating directory";
        if (this.mTelemetryDirectory != null && !this.mTelemetryDirectory.exists()) {
            if (this.mTelemetryDirectory.mkdirs()) {
                HockeyLog.info(TAG, successMessage);
            } else {
                HockeyLog.info(TAG, errorMessage);
            }
        }
    }

    private Context getContext() {
        if (this.mWeakContext != null) {
            return (Context) this.mWeakContext.get();
        }
        return null;
    }

    protected Sender getSender() {
        if (this.mWeakSender != null) {
            return (Sender) this.mWeakSender.get();
        }
        return null;
    }

    protected void setSender(Sender sender) {
        this.mWeakSender = new WeakReference(sender);
    }
}

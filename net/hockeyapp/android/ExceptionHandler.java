package net.hockeyapp.android;

import android.content.Context;
import android.os.Process;
import android.text.TextUtils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Date;
import java.util.UUID;
import net.hockeyapp.android.objects.CrashDetails;
import net.hockeyapp.android.utils.HockeyLog;

public class ExceptionHandler implements UncaughtExceptionHandler {
    private CrashManagerListener mCrashManagerListener;
    private UncaughtExceptionHandler mDefaultExceptionHandler;
    private boolean mIgnoreDefaultHandler = false;

    public ExceptionHandler(UncaughtExceptionHandler defaultExceptionHandler, CrashManagerListener listener, boolean ignoreDefaultHandler) {
        this.mDefaultExceptionHandler = defaultExceptionHandler;
        this.mIgnoreDefaultHandler = ignoreDefaultHandler;
        this.mCrashManagerListener = listener;
    }

    public void setListener(CrashManagerListener listener) {
        this.mCrashManagerListener = listener;
    }

    public static void saveException(Throwable exception, Thread thread, CrashManagerListener listener) {
        Date now = new Date();
        Date startDate = new Date(CrashManager.getInitializeTimestamp());
        exception.printStackTrace(new PrintWriter(new StringWriter()));
        Context context = CrashManager.weakContext != null ? (Context) CrashManager.weakContext.get() : null;
        if (context == null) {
            HockeyLog.error("Failed to save exception: context in CrashManager is null");
        } else if (CrashManager.stackTracesCount >= 100) {
            HockeyLog.warn("ExceptionHandler: HockeyApp will not save this exception as there are already 100 or more unsent exceptions on disk");
        } else {
            String filename = UUID.randomUUID().toString();
            CrashDetails crashDetails = new CrashDetails(filename, exception);
            crashDetails.setAppPackage(Constants.APP_PACKAGE);
            crashDetails.setAppVersionCode(Constants.APP_VERSION);
            crashDetails.setAppVersionName(Constants.APP_VERSION_NAME);
            crashDetails.setAppStartDate(startDate);
            crashDetails.setAppCrashDate(now);
            if (listener == null || listener.includeDeviceData()) {
                crashDetails.setOsVersion(Constants.ANDROID_VERSION);
                crashDetails.setOsBuild(Constants.ANDROID_BUILD);
                crashDetails.setDeviceManufacturer(Constants.PHONE_MANUFACTURER);
                crashDetails.setDeviceModel(Constants.PHONE_MODEL);
            }
            if (thread != null && (listener == null || listener.includeThreadDetails())) {
                crashDetails.setThreadName(thread.getName() + "-" + thread.getId());
            }
            String deviceIdentifier = Constants.DEVICE_IDENTIFIER;
            if (deviceIdentifier != null && (listener == null || listener.includeDeviceIdentifier())) {
                crashDetails.setReporterKey(deviceIdentifier);
            }
            crashDetails.writeCrashReport(context);
            if (listener != null) {
                try {
                    writeValueToFile(context, limitedString(listener.getUserID()), filename + ".user");
                    writeValueToFile(context, limitedString(listener.getContact()), filename + ".contact");
                    writeValueToFile(context, listener.getDescription(), filename + ".description");
                } catch (Throwable e) {
                    HockeyLog.error("Error saving crash meta data!", e);
                }
            }
        }
    }

    public void uncaughtException(Thread thread, Throwable exception) {
        Context context = CrashManager.weakContext != null ? (Context) CrashManager.weakContext.get() : null;
        if (context == null || context.getFilesDir() == null) {
            this.mDefaultExceptionHandler.uncaughtException(thread, exception);
            return;
        }
        saveException(exception, thread, this.mCrashManagerListener);
        if (this.mIgnoreDefaultHandler) {
            Process.killProcess(Process.myPid());
            System.exit(10);
            return;
        }
        this.mDefaultExceptionHandler.uncaughtException(thread, exception);
    }

    private static void writeValueToFile(Context context, String value, String filename) throws IOException {
        Throwable e;
        Throwable th;
        if (!TextUtils.isEmpty(value)) {
            BufferedWriter writer = null;
            try {
                File file = new File(context.getFilesDir(), filename);
                if (!TextUtils.isEmpty(value) && TextUtils.getTrimmedLength(value) > 0) {
                    BufferedWriter writer2 = new BufferedWriter(new FileWriter(file));
                    try {
                        writer2.write(value);
                        writer2.flush();
                        writer = writer2;
                    } catch (IOException e2) {
                        e = e2;
                        writer = writer2;
                        try {
                            HockeyLog.error("Failed to write value to " + filename, e);
                            if (writer != null) {
                                writer.close();
                            }
                        } catch (Throwable th2) {
                            th = th2;
                            if (writer != null) {
                                writer.close();
                            }
                            throw th;
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        writer = writer2;
                        if (writer != null) {
                            writer.close();
                        }
                        throw th;
                    }
                }
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e3) {
                e = e3;
                HockeyLog.error("Failed to write value to " + filename, e);
                if (writer != null) {
                    writer.close();
                }
            }
        }
    }

    private static String limitedString(String string) {
        if (TextUtils.isEmpty(string) || string.length() <= 255) {
            return string;
        }
        return string.substring(0, 255);
    }
}

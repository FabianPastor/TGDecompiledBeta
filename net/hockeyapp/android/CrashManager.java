package net.hockeyapp.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import net.hockeyapp.android.objects.CrashManagerUserInput;
import net.hockeyapp.android.objects.CrashMetaData;
import net.hockeyapp.android.utils.AsyncTaskUtils;
import net.hockeyapp.android.utils.HockeyLog;
import net.hockeyapp.android.utils.HttpURLConnectionBuilder;
import net.hockeyapp.android.utils.Util;

public class CrashManager {
    private static final FilenameFilter STACK_TRACES_FILTER = new FilenameFilter() {
        public boolean accept(File dir, String filename) {
            return filename.endsWith(".stacktrace");
        }
    };
    private static boolean didCrashInLastSession = false;
    private static String identifier = null;
    private static long initializeTimestamp;
    static CountDownLatch latch = new CountDownLatch(1);
    static int stackTracesCount = 0;
    private static String urlString = null;
    static WeakReference<Context> weakContext;

    public static void register(Context context, String appIdentifier, CrashManagerListener listener) {
        register(context, "https://sdk.hockeyapp.net/", appIdentifier, listener);
    }

    public static void register(Context context, String urlString, String appIdentifier, CrashManagerListener listener) {
        initialize(context, urlString, appIdentifier, listener, false);
        execute(context, listener);
    }

    @SuppressLint({"StaticFieldLeak"})
    public static void execute(Context context, final CrashManagerListener listener) {
        final WeakReference<Context> weakContext = new WeakReference(context);
        AsyncTaskUtils.execute(new AsyncTask<Void, Object, Integer>() {
            private boolean autoSend = false;

            protected Integer doInBackground(Void... voids) {
                boolean z = true;
                Context context = (Context) weakContext.get();
                if (context != null) {
                    this.autoSend |= PreferenceManager.getDefaultSharedPreferences(context).getBoolean("always_send_crash_reports", false);
                }
                int foundOrSend = CrashManager.hasStackTraces(weakContext);
                if (foundOrSend != 1) {
                    z = false;
                }
                CrashManager.didCrashInLastSession = z;
                CrashManager.latch.countDown();
                return Integer.valueOf(foundOrSend);
            }

            protected void onPostExecute(Integer foundOrSend) {
                boolean autoSend = this.autoSend;
                boolean ignoreDefaultHandler = listener != null && listener.ignoreDefaultHandler();
                if (foundOrSend.intValue() == 1) {
                    if (listener != null) {
                        autoSend |= listener.shouldAutoUploadCrashes();
                        listener.onNewCrashesFound();
                    }
                    if (autoSend || !CrashManager.showDialog(weakContext, listener, ignoreDefaultHandler)) {
                        CrashManager.sendCrashes(weakContext, listener, ignoreDefaultHandler, null);
                    }
                } else if (foundOrSend.intValue() == 2) {
                    if (listener != null) {
                        listener.onConfirmedCrashesFound();
                    }
                    CrashManager.sendCrashes(weakContext, listener, ignoreDefaultHandler, null);
                } else if (foundOrSend.intValue() == 0) {
                    if (listener != null) {
                        listener.onNoCrashesFound();
                    }
                    CrashManager.registerHandler(listener, ignoreDefaultHandler);
                }
            }
        });
    }

    public static int hasStackTraces(WeakReference<Context> weakContext) {
        String[] filenames = searchForStackTraces(weakContext);
        List<String> confirmedFilenames = null;
        if (filenames == null || filenames.length <= 0) {
            return 0;
        }
        Context context;
        if (weakContext != null) {
            try {
                context = (Context) weakContext.get();
            } catch (Exception e) {
            }
        } else {
            context = null;
        }
        if (context != null) {
            confirmedFilenames = Arrays.asList(context.getSharedPreferences("HockeySDK", 0).getString("ConfirmedFilenames", TtmlNode.ANONYMOUS_REGION_ID).split("\\|"));
        }
        if (confirmedFilenames == null) {
            return 1;
        }
        for (String filename : filenames) {
            if (!confirmedFilenames.contains(filename)) {
                return 1;
            }
        }
        return 2;
    }

    private static void submitStackTrace(WeakReference<Context> weakContext, String filename, CrashManagerListener listener, CrashMetaData crashMetaData) {
        Boolean successful = Boolean.valueOf(false);
        HttpURLConnection urlConnection = null;
        try {
            String stacktrace = contentsOfFile(weakContext, filename);
            if (stacktrace.length() > 0) {
                HockeyLog.debug("Transmitting crash data: \n" + stacktrace);
                String userID = contentsOfFile(weakContext, filename.replace(".stacktrace", ".user"));
                String contact = contentsOfFile(weakContext, filename.replace(".stacktrace", ".contact"));
                if (crashMetaData != null) {
                    String crashMetaDataUserID = crashMetaData.getUserID();
                    if (!TextUtils.isEmpty(crashMetaDataUserID)) {
                        userID = crashMetaDataUserID;
                    }
                    String crashMetaDataContact = crashMetaData.getUserEmail();
                    if (!TextUtils.isEmpty(crashMetaDataContact)) {
                        contact = crashMetaDataContact;
                    }
                }
                String applicationLog = contentsOfFile(weakContext, filename.replace(".stacktrace", ".description"));
                String description = crashMetaData != null ? crashMetaData.getUserDescription() : TtmlNode.ANONYMOUS_REGION_ID;
                if (!TextUtils.isEmpty(applicationLog)) {
                    if (TextUtils.isEmpty(description)) {
                        description = String.format("Log:\n%s", new Object[]{applicationLog});
                    } else {
                        description = String.format("%s\n\nLog:\n%s", new Object[]{description, applicationLog});
                    }
                }
                Map<String, String> parameters = new HashMap();
                parameters.put("raw", stacktrace);
                parameters.put("userID", userID);
                parameters.put("contact", contact);
                parameters.put("description", description);
                parameters.put("sdk", "HockeySDK");
                parameters.put("sdk_version", "5.0.4");
                urlConnection = new HttpURLConnectionBuilder(getURLString()).setRequestMethod("POST").writeFormFields(parameters).build();
                int responseCode = urlConnection.getResponseCode();
                boolean z = responseCode == 202 || responseCode == 201;
                successful = Boolean.valueOf(z);
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (successful.booleanValue()) {
                HockeyLog.debug("Transmission succeeded");
                deleteStackTrace(weakContext, filename);
                if (listener != null) {
                    listener.onCrashesSent();
                    deleteRetryCounter(weakContext, filename);
                    return;
                }
                return;
            }
            HockeyLog.debug("Transmission failed, will retry on next register() call");
            if (listener != null) {
                listener.onCrashesNotSent();
                updateRetryCounter(weakContext, filename, listener.getMaxRetryAttempts());
            }
        } catch (Throwable e) {
            HockeyLog.error("Failed to transmit crash data", e);
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (successful.booleanValue()) {
                HockeyLog.debug("Transmission succeeded");
                deleteStackTrace(weakContext, filename);
                if (listener != null) {
                    listener.onCrashesSent();
                    deleteRetryCounter(weakContext, filename);
                    return;
                }
                return;
            }
            HockeyLog.debug("Transmission failed, will retry on next register() call");
            if (listener != null) {
                listener.onCrashesNotSent();
                updateRetryCounter(weakContext, filename, listener.getMaxRetryAttempts());
            }
        } catch (Throwable th) {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (successful.booleanValue()) {
                HockeyLog.debug("Transmission succeeded");
                deleteStackTrace(weakContext, filename);
                if (listener != null) {
                    listener.onCrashesSent();
                    deleteRetryCounter(weakContext, filename);
                }
            } else {
                HockeyLog.debug("Transmission failed, will retry on next register() call");
                if (listener != null) {
                    listener.onCrashesNotSent();
                    updateRetryCounter(weakContext, filename, listener.getMaxRetryAttempts());
                }
            }
        }
    }

    public static void deleteStackTraces(WeakReference<Context> weakContext) {
        String[] list = searchForStackTraces(weakContext);
        if (list != null && list.length > 0) {
            HockeyLog.debug("Found " + list.length + " stacktrace(s).");
            for (String file : list) {
                if (weakContext != null) {
                    try {
                        HockeyLog.debug("Delete stacktrace " + file + ".");
                        deleteStackTrace(weakContext, file);
                    } catch (Throwable e) {
                        HockeyLog.error("Failed to delete stacktrace", e);
                    }
                }
            }
        }
    }

    @SuppressLint({"StaticFieldLeak"})
    public static boolean handleUserInput(CrashManagerUserInput userInput, CrashMetaData userProvidedMetaData, CrashManagerListener listener, final WeakReference<Context> weakContext, boolean ignoreDefaultHandler) {
        switch (userInput) {
            case CrashManagerUserInputDontSend:
                if (listener != null) {
                    listener.onUserDeniedCrashes();
                }
                registerHandler(listener, ignoreDefaultHandler);
                AsyncTaskUtils.execute(new AsyncTask<Void, Object, Object>() {
                    protected Object doInBackground(Void... voids) {
                        CrashManager.deleteStackTraces(weakContext);
                        return null;
                    }
                });
                return true;
            case CrashManagerUserInputAlwaysSend:
                Context context = weakContext != null ? (Context) weakContext.get() : null;
                if (context == null) {
                    return false;
                }
                PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("always_send_crash_reports", true).apply();
                sendCrashes(weakContext, listener, ignoreDefaultHandler, userProvidedMetaData);
                return true;
            case CrashManagerUserInputSend:
                sendCrashes(weakContext, listener, ignoreDefaultHandler, userProvidedMetaData);
                return true;
            default:
                return false;
        }
    }

    private static void initialize(Context context, String urlString, String appIdentifier, CrashManagerListener listener, boolean registerHandler) {
        boolean ignoreDefaultHandler = false;
        if (context != null) {
            if (initializeTimestamp == 0) {
                initializeTimestamp = System.currentTimeMillis();
            }
            urlString = urlString;
            identifier = Util.sanitizeAppIdentifier(appIdentifier);
            didCrashInLastSession = false;
            weakContext = new WeakReference(context);
            Constants.loadFromContext(context);
            if (identifier == null) {
                identifier = Constants.APP_PACKAGE;
            }
            if (registerHandler) {
                if (listener != null && listener.ignoreDefaultHandler()) {
                    ignoreDefaultHandler = true;
                }
                registerHandler(listener, ignoreDefaultHandler);
            }
        }
    }

    private static boolean showDialog(final WeakReference<Context> weakContext, final CrashManagerListener listener, final boolean ignoreDefaultHandler) {
        if (listener != null && listener.onHandleAlertView()) {
            return true;
        }
        Context context = weakContext != null ? (Context) weakContext.get() : null;
        if (context == null || !(context instanceof Activity)) {
            return false;
        }
        Builder builder = new Builder(context);
        builder.setTitle(getAlertTitle(context));
        builder.setMessage(R.string.hockeyapp_crash_dialog_message);
        builder.setNegativeButton(R.string.hockeyapp_crash_dialog_negative_button, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                CrashManager.handleUserInput(CrashManagerUserInput.CrashManagerUserInputDontSend, null, listener, weakContext, ignoreDefaultHandler);
            }
        });
        builder.setNeutralButton(R.string.hockeyapp_crash_dialog_neutral_button, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                CrashManager.handleUserInput(CrashManagerUserInput.CrashManagerUserInputAlwaysSend, null, listener, weakContext, ignoreDefaultHandler);
            }
        });
        builder.setPositiveButton(R.string.hockeyapp_crash_dialog_positive_button, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                CrashManager.handleUserInput(CrashManagerUserInput.CrashManagerUserInputSend, null, listener, weakContext, ignoreDefaultHandler);
            }
        });
        builder.create().show();
        return true;
    }

    private static String getAlertTitle(Context context) {
        return context.getString(R.string.hockeyapp_crash_dialog_title, new Object[]{Util.getAppName(context)});
    }

    @SuppressLint({"StaticFieldLeak"})
    private static void sendCrashes(final WeakReference<Context> weakContext, final CrashManagerListener listener, boolean ignoreDefaultHandler, final CrashMetaData crashMetaData) {
        registerHandler(listener, ignoreDefaultHandler);
        Context context = weakContext != null ? (Context) weakContext.get() : null;
        final boolean isConnectedToNetwork = context != null && Util.isConnectedToNetwork(context);
        if (!(isConnectedToNetwork || listener == null)) {
            listener.onCrashesNotSent();
        }
        AsyncTaskUtils.execute(new AsyncTask<Void, Object, Object>() {
            /* JADX WARNING: inconsistent code. */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            protected Object doInBackground(Void... voids) {
                String[] list = CrashManager.searchForStackTraces(weakContext);
                if (list != null && list.length > 0) {
                    HockeyLog.debug("Found " + list.length + " stacktrace(s).");
                    if (list.length > 100) {
                        CrashManager.deleteRedundantStackTraces(weakContext);
                        list = CrashManager.searchForStackTraces(weakContext);
                    }
                    CrashManager.saveConfirmedStackTraces(weakContext, list);
                    if (isConnectedToNetwork) {
                        for (String file : list) {
                            CrashManager.submitStackTrace(weakContext, file, listener, crashMetaData);
                        }
                    }
                }
                return null;
            }
        });
    }

    private static void registerHandler(CrashManagerListener listener, boolean ignoreDefaultHandler) {
        if (TextUtils.isEmpty(Constants.APP_VERSION) || TextUtils.isEmpty(Constants.APP_PACKAGE)) {
            HockeyLog.debug("Exception handler not set because version or package is null.");
            return;
        }
        UncaughtExceptionHandler currentHandler = Thread.getDefaultUncaughtExceptionHandler();
        if (currentHandler != null) {
            HockeyLog.debug("Current handler class = " + currentHandler.getClass().getName());
        }
        if (currentHandler instanceof ExceptionHandler) {
            ((ExceptionHandler) currentHandler).setListener(listener);
        } else {
            Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(currentHandler, listener, ignoreDefaultHandler));
        }
    }

    private static String getURLString() {
        return urlString + "api/2/apps/" + identifier + "/crashes/";
    }

    private static void updateRetryCounter(WeakReference<Context> weakContext, String filename, int maxRetryAttempts) {
        if (maxRetryAttempts != -1) {
            Context context = weakContext != null ? (Context) weakContext.get() : null;
            if (context != null) {
                SharedPreferences preferences = context.getSharedPreferences("HockeySDK", 0);
                Editor editor = preferences.edit();
                int retryCounter = preferences.getInt("RETRY_COUNT: " + filename, 0);
                if (retryCounter >= maxRetryAttempts) {
                    deleteStackTrace(weakContext, filename);
                    deleteRetryCounter(weakContext, filename);
                    return;
                }
                editor.putInt("RETRY_COUNT: " + filename, retryCounter + 1);
                editor.apply();
            }
        }
    }

    private static void deleteRetryCounter(WeakReference<Context> weakContext, String filename) {
        Context context = weakContext != null ? (Context) weakContext.get() : null;
        if (context != null) {
            Editor editor = context.getSharedPreferences("HockeySDK", 0).edit();
            editor.remove("RETRY_COUNT: " + filename);
            editor.apply();
        }
    }

    private static void deleteStackTrace(WeakReference<Context> weakContext, String filename) {
        Context context = weakContext != null ? (Context) weakContext.get() : null;
        if (context != null) {
            context.deleteFile(filename);
            context.deleteFile(filename.replace(".stacktrace", ".user"));
            context.deleteFile(filename.replace(".stacktrace", ".contact"));
            context.deleteFile(filename.replace(".stacktrace", ".description"));
            stackTracesCount--;
        }
    }

    private static String contentsOfFile(WeakReference<Context> weakContext, String filename) {
        Throwable e;
        Throwable th;
        Context context = weakContext != null ? (Context) weakContext.get() : null;
        if (context == null) {
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
        File file = context.getFileStreamPath(filename);
        if (file == null || !file.exists()) {
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
        StringBuilder contents = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(context.openFileInput(filename)));
            while (true) {
                try {
                    String line = reader.readLine();
                    if (line == null) {
                        break;
                    }
                    contents.append(line);
                    contents.append(System.getProperty("line.separator"));
                } catch (IOException e2) {
                    e = e2;
                    bufferedReader = reader;
                } catch (Throwable th2) {
                    th = th2;
                    bufferedReader = reader;
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                    bufferedReader = reader;
                } catch (IOException e3) {
                    bufferedReader = reader;
                }
            }
        } catch (IOException e4) {
            e = e4;
            try {
                HockeyLog.error("Failed to read content of " + filename, e);
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e5) {
                    }
                }
                return contents.toString();
            } catch (Throwable th3) {
                th = th3;
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e6) {
                    }
                }
                throw th;
            }
        }
        return contents.toString();
    }

    private static void saveConfirmedStackTraces(WeakReference<Context> weakContext, String[] stackTraces) {
        Context context = weakContext != null ? (Context) weakContext.get() : null;
        if (context != null) {
            try {
                Editor editor = context.getSharedPreferences("HockeySDK", 0).edit();
                editor.putString("ConfirmedFilenames", TextUtils.join(",", stackTraces));
                editor.apply();
            } catch (Exception e) {
            }
        }
    }

    static String[] searchForStackTraces(WeakReference<Context> weakContext) {
        Context context;
        if (weakContext != null) {
            context = (Context) weakContext.get();
        } else {
            context = null;
        }
        if (context == null) {
            return null;
        }
        File dir = context.getFilesDir();
        if (dir != null) {
            HockeyLog.debug("Looking for exceptions in: " + dir.getAbsolutePath());
            if (!dir.exists() && !dir.mkdir()) {
                return new String[0];
            }
            String[] list = dir.list(STACK_TRACES_FILTER);
            stackTracesCount = list != null ? list.length : 0;
            return list;
        }
        HockeyLog.debug("Can't search for exception as file path is null.");
        return null;
    }

    private static void deleteRedundantStackTraces(WeakReference<Context> weakContext) {
        Context context = weakContext != null ? (Context) weakContext.get() : null;
        if (context != null) {
            File dir = context.getFilesDir();
            if (dir != null && dir.exists()) {
                File[] files = dir.listFiles(STACK_TRACES_FILTER);
                if (files.length > 100) {
                    HockeyLog.debug("Delete " + (files.length - 100) + " redundant stacktrace(s).");
                    Arrays.sort(files, new Comparator<File>() {
                        public int compare(File file1, File file2) {
                            return Long.valueOf(file1.lastModified()).compareTo(Long.valueOf(file2.lastModified()));
                        }
                    });
                    for (int i = 0; i < files.length - 100; i++) {
                        deleteStackTrace(weakContext, files[i].getName());
                    }
                }
            }
        }
    }

    public static long getInitializeTimestamp() {
        return initializeTimestamp;
    }
}

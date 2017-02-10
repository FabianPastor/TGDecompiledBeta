package org.telegram.messenger;

import android.app.Activity;
import android.net.Uri;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.UUID;
import net.hockeyapp.android.Constants;
import net.hockeyapp.android.utils.SimpleMultipartEntity;

public class NativeCrashManager {
    public static void handleDumpFiles(Activity activity) {
        for (String dumpFilename : searchForDumpFiles()) {
            String logFilename = createLogFile();
            if (logFilename != null) {
                uploadDumpAndLog(activity, BuildVars.DEBUG_VERSION ? BuildVars.HOCKEY_APP_HASH_DEBUG : BuildVars.HOCKEY_APP_HASH, dumpFilename, logFilename);
            }
        }
    }

    public static String createLogFile() {
        Date now = new Date();
        try {
            String filename = UUID.randomUUID().toString();
            BufferedWriter write = new BufferedWriter(new FileWriter(Constants.FILES_PATH + "/" + filename + ".faketrace"));
            write.write("Package: " + Constants.APP_PACKAGE + "\n");
            write.write("Version Code: " + Constants.APP_VERSION + "\n");
            write.write("Version Name: " + Constants.APP_VERSION_NAME + "\n");
            write.write("Android: " + Constants.ANDROID_VERSION + "\n");
            write.write("Manufacturer: " + Constants.PHONE_MANUFACTURER + "\n");
            write.write("Model: " + Constants.PHONE_MODEL + "\n");
            write.write("Date: " + now + "\n");
            write.write("\n");
            write.write("MinidumpContainer");
            write.flush();
            write.close();
            return filename + ".faketrace";
        } catch (Throwable e) {
            FileLog.e(e);
            return null;
        }
    }

    public static void uploadDumpAndLog(final Activity activity, final String identifier, final String dumpFilename, final String logFilename) {
        new Thread() {
            public void run() {
                try {
                    SimpleMultipartEntity entity = new SimpleMultipartEntity();
                    entity.writeFirstBoundaryIfNeeds();
                    Uri attachmentUri = Uri.fromFile(new File(Constants.FILES_PATH, dumpFilename));
                    entity.addPart("attachment0", attachmentUri.getLastPathSegment(), activity.getContentResolver().openInputStream(attachmentUri), false);
                    attachmentUri = Uri.fromFile(new File(Constants.FILES_PATH, logFilename));
                    entity.addPart("log", attachmentUri.getLastPathSegment(), activity.getContentResolver().openInputStream(attachmentUri), true);
                    entity.writeLastBoundaryIfNeeds();
                    HttpURLConnection urlConnection = (HttpURLConnection) new URL("https://rink.hockeyapp.net/api/2/apps/" + identifier + "/crashes/upload").openConnection();
                    urlConnection.setDoOutput(true);
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", entity.getContentType());
                    urlConnection.setRequestProperty("Content-Length", String.valueOf(entity.getContentLength()));
                    BufferedOutputStream outputStream = new BufferedOutputStream(urlConnection.getOutputStream());
                    outputStream.write(entity.getOutputStream().toByteArray());
                    outputStream.flush();
                    outputStream.close();
                    urlConnection.connect();
                    FileLog.e("response code = " + urlConnection.getResponseCode() + " message = " + urlConnection.getResponseMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    activity.deleteFile(logFilename);
                    activity.deleteFile(dumpFilename);
                }
            }
        }.start();
    }

    private static String[] searchForDumpFiles() {
        if (Constants.FILES_PATH == null) {
            return new String[0];
        }
        File dir = new File(Constants.FILES_PATH + "/");
        if (dir.mkdir() || dir.exists()) {
            return dir.list(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.endsWith(".dmp");
                }
            });
        }
        return new String[0];
    }
}

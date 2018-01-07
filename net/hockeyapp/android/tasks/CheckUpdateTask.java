package net.hockeyapp.android.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.text.TextUtils;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import net.hockeyapp.android.Constants;
import net.hockeyapp.android.Tracking;
import net.hockeyapp.android.UpdateManagerListener;
import net.hockeyapp.android.utils.HockeyLog;
import net.hockeyapp.android.utils.Util;
import net.hockeyapp.android.utils.VersionHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.exoplayer2.C;

public class CheckUpdateTask extends AsyncTask<Void, String, JSONArray> {
    protected String apkUrlString = null;
    protected String appIdentifier = null;
    protected UpdateManagerListener listener;
    protected Boolean mandatory = Boolean.valueOf(false);
    protected String urlString = null;
    private long usageTime = 0;
    private WeakReference<Context> weakContext = null;

    public CheckUpdateTask(WeakReference<? extends Context> weakContext, String urlString, String appIdentifier, UpdateManagerListener listener) {
        this.appIdentifier = appIdentifier;
        this.urlString = urlString;
        this.listener = listener;
        Context ctx = null;
        if (weakContext != null) {
            ctx = (Context) weakContext.get();
        }
        if (ctx != null) {
            this.weakContext = new WeakReference(ctx.getApplicationContext());
            this.usageTime = Tracking.getUsageTime(ctx);
            Constants.loadFromContext(ctx);
        }
    }

    public void attach(WeakReference<? extends Context> weakContext) {
        Context ctx = null;
        if (weakContext != null) {
            ctx = (Context) weakContext.get();
        }
        if (ctx != null) {
            this.weakContext = new WeakReference(ctx.getApplicationContext());
            Constants.loadFromContext(ctx);
        }
    }

    public void detach() {
        this.weakContext = null;
    }

    protected int getVersionCode() {
        return Integer.parseInt(Constants.APP_VERSION);
    }

    protected JSONArray doInBackground(Void... args) {
        Context context;
        Exception e;
        if (this.weakContext != null) {
            context = (Context) this.weakContext.get();
        } else {
            context = null;
        }
        if (context == null) {
            return null;
        }
        this.apkUrlString = getURLString(context, "apk");
        try {
            int versionCode = getVersionCode();
            URLConnection connection = createConnection(new URL(getURLString(context, "json")));
            connection.connect();
            InputStream inputStream = new BufferedInputStream(connection.getInputStream());
            String jsonString = Util.convertStreamToString(inputStream);
            inputStream.close();
            JSONArray json = new JSONArray(jsonString);
            if (findNewVersion(context, json, versionCode)) {
                return limitResponseSize(json);
            }
        } catch (IOException e2) {
            e = e2;
            if (Util.isConnectedToNetwork(context)) {
                HockeyLog.error("HockeyUpdate", "Could not fetch updates although connected to internet", e);
            }
            return null;
        } catch (JSONException e3) {
            e = e3;
            if (Util.isConnectedToNetwork(context)) {
                HockeyLog.error("HockeyUpdate", "Could not fetch updates although connected to internet", e);
            }
            return null;
        }
        return null;
    }

    protected URLConnection createConnection(URL url) throws IOException {
        URLConnection connection = url.openConnection();
        connection.addRequestProperty("User-Agent", "HockeySDK/Android 5.0.4");
        return connection;
    }

    private boolean findNewVersion(Context context, JSONArray json, int versionCode) {
        boolean newerVersionFound = false;
        int index = 0;
        while (index < json.length()) {
            try {
                boolean largerVersionCode;
                JSONObject entry = json.getJSONObject(index);
                if (entry.getInt("version") > versionCode) {
                    largerVersionCode = true;
                } else {
                    largerVersionCode = false;
                }
                boolean newerApkFile;
                if (entry.getInt("version") == versionCode && VersionHelper.isNewerThanLastUpdateTime(context, entry.getLong("timestamp"))) {
                    newerApkFile = true;
                } else {
                    newerApkFile = false;
                }
                boolean minRequirementsMet;
                if (VersionHelper.compareVersionStrings(entry.getString("minimum_os_version"), VersionHelper.mapGoogleVersion(VERSION.RELEASE)) <= 0) {
                    minRequirementsMet = true;
                } else {
                    minRequirementsMet = false;
                }
                if ((largerVersionCode || newerApkFile) && minRequirementsMet) {
                    if (entry.has("mandatory")) {
                        this.mandatory = Boolean.valueOf(this.mandatory.booleanValue() | entry.getBoolean("mandatory"));
                    }
                    newerVersionFound = true;
                }
                index++;
            } catch (JSONException e) {
                return false;
            }
        }
        return newerVersionFound;
    }

    private JSONArray limitResponseSize(JSONArray json) {
        JSONArray result = new JSONArray();
        for (int index = 0; index < Math.min(json.length(), 25); index++) {
            try {
                result.put(json.get(index));
            } catch (JSONException e) {
            }
        }
        return result;
    }

    protected void onPostExecute(JSONArray updateInfo) {
        if (updateInfo != null) {
            HockeyLog.verbose("HockeyUpdate", "Received Update Info");
            if (this.listener != null) {
                this.listener.onUpdateAvailable(updateInfo, this.apkUrlString);
                return;
            }
            return;
        }
        HockeyLog.verbose("HockeyUpdate", "No Update Info available");
        if (this.listener != null) {
            this.listener.onNoUpdateAvailable();
        }
    }

    protected void cleanUp() {
        this.urlString = null;
        this.appIdentifier = null;
    }

    private String getURLString(Context context, String format) {
        Throwable e;
        SharedPreferences prefs;
        String auid;
        String iuid;
        StringBuilder builder = new StringBuilder();
        builder.append(this.urlString);
        builder.append("api/2/apps/");
        builder.append(this.appIdentifier != null ? this.appIdentifier : context.getPackageName());
        builder.append("?format=").append(format);
        String deviceIdentifier = null;
        try {
            deviceIdentifier = (String) Constants.getDeviceIdentifier().get();
        } catch (InterruptedException e2) {
            e = e2;
            HockeyLog.debug("Error get device identifier", e);
            if (!TextUtils.isEmpty(deviceIdentifier)) {
                builder.append("&udid=").append(encodeParam(deviceIdentifier));
            }
            prefs = context.getSharedPreferences("net.hockeyapp.android.login", 0);
            auid = prefs.getString("auid", null);
            if (!TextUtils.isEmpty(auid)) {
                builder.append("&auid=").append(encodeParam(auid));
            }
            iuid = prefs.getString("iuid", null);
            if (!TextUtils.isEmpty(iuid)) {
                builder.append("&iuid=").append(encodeParam(iuid));
            }
            builder.append("&os=Android");
            builder.append("&os_version=").append(encodeParam(Constants.ANDROID_VERSION));
            builder.append("&device=").append(encodeParam(Constants.PHONE_MODEL));
            builder.append("&oem=").append(encodeParam(Constants.PHONE_MANUFACTURER));
            builder.append("&app_version=").append(encodeParam(Constants.APP_VERSION));
            builder.append("&sdk=").append(encodeParam("HockeySDK"));
            builder.append("&sdk_version=").append(encodeParam("5.0.4"));
            builder.append("&lang=").append(encodeParam(Locale.getDefault().getLanguage()));
            builder.append("&usage_time=").append(this.usageTime);
            return builder.toString();
        } catch (ExecutionException e3) {
            e = e3;
            HockeyLog.debug("Error get device identifier", e);
            if (TextUtils.isEmpty(deviceIdentifier)) {
                builder.append("&udid=").append(encodeParam(deviceIdentifier));
            }
            prefs = context.getSharedPreferences("net.hockeyapp.android.login", 0);
            auid = prefs.getString("auid", null);
            if (TextUtils.isEmpty(auid)) {
                builder.append("&auid=").append(encodeParam(auid));
            }
            iuid = prefs.getString("iuid", null);
            if (TextUtils.isEmpty(iuid)) {
                builder.append("&iuid=").append(encodeParam(iuid));
            }
            builder.append("&os=Android");
            builder.append("&os_version=").append(encodeParam(Constants.ANDROID_VERSION));
            builder.append("&device=").append(encodeParam(Constants.PHONE_MODEL));
            builder.append("&oem=").append(encodeParam(Constants.PHONE_MANUFACTURER));
            builder.append("&app_version=").append(encodeParam(Constants.APP_VERSION));
            builder.append("&sdk=").append(encodeParam("HockeySDK"));
            builder.append("&sdk_version=").append(encodeParam("5.0.4"));
            builder.append("&lang=").append(encodeParam(Locale.getDefault().getLanguage()));
            builder.append("&usage_time=").append(this.usageTime);
            return builder.toString();
        }
        if (TextUtils.isEmpty(deviceIdentifier)) {
            builder.append("&udid=").append(encodeParam(deviceIdentifier));
        }
        prefs = context.getSharedPreferences("net.hockeyapp.android.login", 0);
        auid = prefs.getString("auid", null);
        if (TextUtils.isEmpty(auid)) {
            builder.append("&auid=").append(encodeParam(auid));
        }
        iuid = prefs.getString("iuid", null);
        if (TextUtils.isEmpty(iuid)) {
            builder.append("&iuid=").append(encodeParam(iuid));
        }
        builder.append("&os=Android");
        builder.append("&os_version=").append(encodeParam(Constants.ANDROID_VERSION));
        builder.append("&device=").append(encodeParam(Constants.PHONE_MODEL));
        builder.append("&oem=").append(encodeParam(Constants.PHONE_MANUFACTURER));
        builder.append("&app_version=").append(encodeParam(Constants.APP_VERSION));
        builder.append("&sdk=").append(encodeParam("HockeySDK"));
        builder.append("&sdk_version=").append(encodeParam("5.0.4"));
        builder.append("&lang=").append(encodeParam(Locale.getDefault().getLanguage()));
        builder.append("&usage_time=").append(this.usageTime);
        return builder.toString();
    }

    private String encodeParam(String param) {
        try {
            return URLEncoder.encode(param, C.UTF8_NAME);
        } catch (UnsupportedEncodingException e) {
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
    }
}

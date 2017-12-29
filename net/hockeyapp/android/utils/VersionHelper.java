package net.hockeyapp.android.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.Pattern;
import net.hockeyapp.android.UpdateInfoListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class VersionHelper {
    private int mCurrentVersionCode;
    private UpdateInfoListener mListener;
    private JSONObject mNewest;
    private ArrayList<JSONObject> mSortedVersions;

    public VersionHelper(Context context, String infoJSON, UpdateInfoListener listener) {
        this.mListener = listener;
        loadVersions(context, infoJSON);
        sortVersions();
    }

    private void loadVersions(Context context, String infoJSON) {
        this.mNewest = new JSONObject();
        this.mSortedVersions = new ArrayList();
        this.mCurrentVersionCode = this.mListener.getCurrentVersionCode();
        try {
            JSONArray versions = new JSONArray(infoJSON);
            int versionCode = this.mListener.getCurrentVersionCode();
            for (int index = 0; index < versions.length(); index++) {
                boolean largerVersionCode;
                JSONObject entry = versions.getJSONObject(index);
                if (entry.getInt("version") > versionCode) {
                    largerVersionCode = true;
                } else {
                    largerVersionCode = false;
                }
                boolean newerApkFile;
                if (entry.getInt("version") == versionCode && isNewerThanLastUpdateTime(context, entry.getLong("timestamp"))) {
                    newerApkFile = true;
                } else {
                    newerApkFile = false;
                }
                if (largerVersionCode || newerApkFile) {
                    this.mNewest = entry;
                    versionCode = entry.getInt("version");
                }
                this.mSortedVersions.add(entry);
            }
        } catch (JSONException e) {
        } catch (NullPointerException e2) {
        }
    }

    private void sortVersions() {
        Collections.sort(this.mSortedVersions, new Comparator<JSONObject>() {
            public int compare(JSONObject object1, JSONObject object2) {
                try {
                    return object1.getInt("version") > object2.getInt("version") ? 0 : 0;
                } catch (JSONException e) {
                } catch (NullPointerException e2) {
                }
            }
        });
    }

    public String getVersionString() {
        return failSafeGetStringFromJSON(this.mNewest, "shortversion", TtmlNode.ANONYMOUS_REGION_ID) + " (" + failSafeGetStringFromJSON(this.mNewest, "version", TtmlNode.ANONYMOUS_REGION_ID) + ")";
    }

    @SuppressLint({"SimpleDateFormat"})
    public String getFileDateString() {
        return new SimpleDateFormat("dd.MM.yyyy").format(new Date(1000 * failSafeGetLongFromJSON(this.mNewest, "timestamp", 0)));
    }

    public long getFileSizeBytes() {
        boolean external = Boolean.valueOf(failSafeGetStringFromJSON(this.mNewest, "external", "false")).booleanValue();
        long appSize = failSafeGetLongFromJSON(this.mNewest, "appsize", 0);
        return (external && appSize == 0) ? -1 : appSize;
    }

    private static String failSafeGetStringFromJSON(JSONObject json, String name, String defaultValue) {
        try {
            defaultValue = json.getString(name);
        } catch (JSONException e) {
        }
        return defaultValue;
    }

    private static long failSafeGetLongFromJSON(JSONObject json, String name, long defaultValue) {
        try {
            defaultValue = json.getLong(name);
        } catch (JSONException e) {
        }
        return defaultValue;
    }

    public String getReleaseNotes(boolean showRestore) {
        StringBuilder result = new StringBuilder();
        result.append("<html>");
        result.append("<body style='padding: 0px 0px 20px 0px'>");
        int count = 0;
        Iterator it = this.mSortedVersions.iterator();
        while (it.hasNext()) {
            JSONObject version = (JSONObject) it.next();
            if (count > 0) {
                result.append(getSeparator());
                if (showRestore) {
                    result.append(getRestoreButton(count, version));
                }
            }
            result.append(getVersionLine(count, version));
            result.append(getVersionNotes(count, version));
            count++;
        }
        result.append("</body>");
        result.append("</html>");
        return result.toString();
    }

    private Object getSeparator() {
        return "<hr style='border-top: 1px solid #c8c8c8; border-bottom: 0px; margin: 40px 10px 0px 10px;' />";
    }

    private String getRestoreButton(int count, JSONObject version) {
        StringBuilder result = new StringBuilder();
        String versionID = getVersionID(version);
        if (!TextUtils.isEmpty(versionID)) {
            result.append("<a href='restore:" + versionID + "'  style='background: #c8c8c8; color: #000; display: block; float: right; padding: 7px; margin: 0px 10px 10px; text-decoration: none;'>Restore</a>");
        }
        return result.toString();
    }

    private String getVersionID(JSONObject version) {
        String versionID = TtmlNode.ANONYMOUS_REGION_ID;
        try {
            versionID = version.getString(TtmlNode.ATTR_ID);
        } catch (JSONException e) {
        }
        return versionID;
    }

    private String getVersionLine(int count, JSONObject version) {
        StringBuilder result = new StringBuilder();
        int newestCode = getVersionCode(this.mNewest);
        int versionCode = getVersionCode(version);
        String versionName = getVersionName(version);
        result.append("<div style='padding: 20px 10px 10px;'><strong>");
        if (count == 0) {
            result.append("Newest version:");
        } else {
            result.append("Version " + versionName + " (" + versionCode + "): ");
            if (versionCode != newestCode && versionCode == this.mCurrentVersionCode) {
                this.mCurrentVersionCode = -1;
                result.append("[INSTALLED]");
            }
        }
        result.append("</strong></div>");
        return result.toString();
    }

    private int getVersionCode(JSONObject version) {
        int versionCode = 0;
        try {
            versionCode = version.getInt("version");
        } catch (JSONException e) {
        }
        return versionCode;
    }

    private String getVersionName(JSONObject version) {
        String versionName = TtmlNode.ANONYMOUS_REGION_ID;
        try {
            versionName = version.getString("shortversion");
        } catch (JSONException e) {
        }
        return versionName;
    }

    private String getVersionNotes(int count, JSONObject version) {
        StringBuilder result = new StringBuilder();
        String notes = failSafeGetStringFromJSON(version, "notes", TtmlNode.ANONYMOUS_REGION_ID);
        result.append("<div style='padding: 0px 10px;'>");
        if (notes.trim().length() == 0) {
            result.append("<em>No information.</em>");
        } else {
            result.append(notes);
        }
        result.append("</div>");
        return result.toString();
    }

    public static int compareVersionStrings(String left, String right) {
        if (left == null || right == null) {
            return 0;
        }
        try {
            Scanner leftScanner = new Scanner(left.replaceAll("\\-.*", TtmlNode.ANONYMOUS_REGION_ID));
            Scanner rightScanner = new Scanner(right.replaceAll("\\-.*", TtmlNode.ANONYMOUS_REGION_ID));
            leftScanner.useDelimiter("\\.");
            rightScanner.useDelimiter("\\.");
            while (leftScanner.hasNextInt() && rightScanner.hasNextInt()) {
                int leftValue = leftScanner.nextInt();
                int rightValue = rightScanner.nextInt();
                if (leftValue < rightValue) {
                    return -1;
                }
                if (leftValue > rightValue) {
                    return 1;
                }
            }
            if (leftScanner.hasNextInt()) {
                return 1;
            }
            if (rightScanner.hasNextInt()) {
                return -1;
            }
            return 0;
        } catch (Exception e) {
            return 0;
        }
    }

    public static boolean isNewerThanLastUpdateTime(Context context, long timestamp) {
        if (context == null) {
            return false;
        }
        try {
            if (timestamp > (new File(context.getPackageManager().getApplicationInfo(context.getPackageName(), 0).sourceDir).lastModified() / 1000) + 1800) {
                return true;
            }
            return false;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String mapGoogleVersion(String version) {
        if (version == null || version.equalsIgnoreCase("L")) {
            return "5.0";
        }
        if (version.equalsIgnoreCase("M")) {
            return "6.0";
        }
        if (version.equalsIgnoreCase("N")) {
            return "7.0";
        }
        if (Pattern.matches("^[a-zA-Z]+", version)) {
            return "99.0";
        }
        return version;
    }
}

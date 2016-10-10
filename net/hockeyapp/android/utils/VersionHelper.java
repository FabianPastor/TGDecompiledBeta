package net.hockeyapp.android.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
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

public class VersionHelper
{
  public static final String VERSION_MAX = "99.0";
  private int mCurrentVersionCode;
  private UpdateInfoListener mListener;
  private JSONObject mNewest;
  private ArrayList<JSONObject> mSortedVersions;
  
  public VersionHelper(Context paramContext, String paramString, UpdateInfoListener paramUpdateInfoListener)
  {
    this.mListener = paramUpdateInfoListener;
    loadVersions(paramContext, paramString);
    sortVersions();
  }
  
  public static int compareVersionStrings(String paramString1, String paramString2)
  {
    int j = -1;
    int i;
    if ((paramString1 == null) || (paramString2 == null)) {
      i = 0;
    }
    for (;;)
    {
      return i;
      try
      {
        paramString1 = new Scanner(paramString1.replaceAll("\\-.*", ""));
        paramString2 = new Scanner(paramString2.replaceAll("\\-.*", ""));
        paramString1.useDelimiter("\\.");
        paramString2.useDelimiter("\\.");
        for (;;)
        {
          if ((paramString1.hasNextInt()) && (paramString2.hasNextInt()))
          {
            int k = paramString1.nextInt();
            int m = paramString2.nextInt();
            i = j;
            if (k < m) {
              break;
            }
            if (k > m) {
              return 1;
            }
          }
        }
        if (paramString1.hasNextInt()) {
          return 1;
        }
        boolean bool = paramString2.hasNextInt();
        i = j;
        if (!bool) {
          return 0;
        }
      }
      catch (Exception paramString1) {}
    }
    return 0;
  }
  
  private static long failSafeGetLongFromJSON(JSONObject paramJSONObject, String paramString, long paramLong)
  {
    try
    {
      long l = paramJSONObject.getLong(paramString);
      return l;
    }
    catch (JSONException paramJSONObject) {}
    return paramLong;
  }
  
  private static String failSafeGetStringFromJSON(JSONObject paramJSONObject, String paramString1, String paramString2)
  {
    try
    {
      paramJSONObject = paramJSONObject.getString(paramString1);
      return paramJSONObject;
    }
    catch (JSONException paramJSONObject) {}
    return paramString2;
  }
  
  private String getRestoreButton(int paramInt, JSONObject paramJSONObject)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    paramJSONObject = getVersionID(paramJSONObject);
    if (!TextUtils.isEmpty(paramJSONObject)) {
      localStringBuilder.append("<a href='restore:" + paramJSONObject + "'  style='background: #c8c8c8; color: #000; display: block; float: right; padding: 7px; margin: 0px 10px 10px; text-decoration: none;'>Restore</a>");
    }
    return localStringBuilder.toString();
  }
  
  private Object getSeparator()
  {
    return "<hr style='border-top: 1px solid #c8c8c8; border-bottom: 0px; margin: 40px 10px 0px 10px;' />";
  }
  
  private int getVersionCode(JSONObject paramJSONObject)
  {
    try
    {
      int i = paramJSONObject.getInt("version");
      return i;
    }
    catch (JSONException paramJSONObject) {}
    return 0;
  }
  
  private String getVersionID(JSONObject paramJSONObject)
  {
    try
    {
      paramJSONObject = paramJSONObject.getString("id");
      return paramJSONObject;
    }
    catch (JSONException paramJSONObject) {}
    return "";
  }
  
  private String getVersionLine(int paramInt, JSONObject paramJSONObject)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    int i = getVersionCode(this.mNewest);
    int j = getVersionCode(paramJSONObject);
    paramJSONObject = getVersionName(paramJSONObject);
    localStringBuilder.append("<div style='padding: 20px 10px 10px;'><strong>");
    if (paramInt == 0) {
      localStringBuilder.append("Newest version:");
    }
    for (;;)
    {
      localStringBuilder.append("</strong></div>");
      return localStringBuilder.toString();
      localStringBuilder.append("Version " + paramJSONObject + " (" + j + "): ");
      if ((j != i) && (j == this.mCurrentVersionCode))
      {
        this.mCurrentVersionCode = -1;
        localStringBuilder.append("[INSTALLED]");
      }
    }
  }
  
  private String getVersionName(JSONObject paramJSONObject)
  {
    try
    {
      paramJSONObject = paramJSONObject.getString("shortversion");
      return paramJSONObject;
    }
    catch (JSONException paramJSONObject) {}
    return "";
  }
  
  private String getVersionNotes(int paramInt, JSONObject paramJSONObject)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    paramJSONObject = failSafeGetStringFromJSON(paramJSONObject, "notes", "");
    localStringBuilder.append("<div style='padding: 0px 10px;'>");
    if (paramJSONObject.trim().length() == 0) {
      localStringBuilder.append("<em>No information.</em>");
    }
    for (;;)
    {
      localStringBuilder.append("</div>");
      return localStringBuilder.toString();
      localStringBuilder.append(paramJSONObject);
    }
  }
  
  public static boolean isNewerThanLastUpdateTime(Context paramContext, long paramLong)
  {
    if (paramContext == null) {}
    for (;;)
    {
      return false;
      try
      {
        long l = new File(paramContext.getPackageManager().getApplicationInfo(paramContext.getPackageName(), 0).sourceDir).lastModified() / 1000L;
        if (paramLong > l + 1800L) {
          return true;
        }
      }
      catch (PackageManager.NameNotFoundException paramContext)
      {
        paramContext.printStackTrace();
      }
    }
    return false;
  }
  
  private void loadVersions(Context paramContext, String paramString)
  {
    this.mNewest = new JSONObject();
    this.mSortedVersions = new ArrayList();
    this.mCurrentVersionCode = this.mListener.getCurrentVersionCode();
    for (;;)
    {
      int k;
      int m;
      try
      {
        paramString = new JSONArray(paramString);
        int j = this.mListener.getCurrentVersionCode();
        int i = 0;
        if (i < paramString.length())
        {
          JSONObject localJSONObject = paramString.getJSONObject(i);
          if (localJSONObject.getInt("version") > j)
          {
            k = 1;
            if ((localJSONObject.getInt("version") == j) && (isNewerThanLastUpdateTime(paramContext, localJSONObject.getLong("timestamp"))))
            {
              m = 1;
              break label167;
              this.mNewest = localJSONObject;
              j = localJSONObject.getInt("version");
              this.mSortedVersions.add(localJSONObject);
              i += 1;
            }
          }
          else
          {
            k = 0;
            continue;
          }
          m = 0;
        }
      }
      catch (NullPointerException paramContext)
      {
        return;
      }
      catch (JSONException paramContext)
      {
        return;
      }
      label167:
      if (k == 0) {
        if (m == 0) {}
      }
    }
  }
  
  public static String mapGoogleVersion(String paramString)
  {
    String str;
    if ((paramString == null) || (paramString.equalsIgnoreCase("L"))) {
      str = "5.0";
    }
    do
    {
      return str;
      if (paramString.equalsIgnoreCase("M")) {
        return "6.0";
      }
      if (paramString.equalsIgnoreCase("N")) {
        return "7.0";
      }
      str = paramString;
    } while (!Pattern.matches("^[a-zA-Z]+", paramString));
    return "99.0";
  }
  
  private void sortVersions()
  {
    Collections.sort(this.mSortedVersions, new Comparator()
    {
      public int compare(JSONObject paramAnonymousJSONObject1, JSONObject paramAnonymousJSONObject2)
      {
        try
        {
          int i = paramAnonymousJSONObject1.getInt("version");
          int j = paramAnonymousJSONObject2.getInt("version");
          if (i > j) {}
          return 0;
        }
        catch (NullPointerException paramAnonymousJSONObject1)
        {
          return 0;
        }
        catch (JSONException paramAnonymousJSONObject1) {}
        return 0;
      }
    });
  }
  
  @SuppressLint({"SimpleDateFormat"})
  public String getFileDateString()
  {
    Date localDate = new Date(1000L * failSafeGetLongFromJSON(this.mNewest, "timestamp", 0L));
    return new SimpleDateFormat("dd.MM.yyyy").format(localDate);
  }
  
  public long getFileSizeBytes()
  {
    boolean bool = Boolean.valueOf(failSafeGetStringFromJSON(this.mNewest, "external", "false")).booleanValue();
    long l2 = failSafeGetLongFromJSON(this.mNewest, "appsize", 0L);
    long l1 = l2;
    if (bool)
    {
      l1 = l2;
      if (l2 == 0L) {
        l1 = -1L;
      }
    }
    return l1;
  }
  
  public String getReleaseNotes(boolean paramBoolean)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("<html>");
    localStringBuilder.append("<body style='padding: 0px 0px 20px 0px'>");
    int i = 0;
    Iterator localIterator = this.mSortedVersions.iterator();
    while (localIterator.hasNext())
    {
      JSONObject localJSONObject = (JSONObject)localIterator.next();
      if (i > 0)
      {
        localStringBuilder.append(getSeparator());
        if (paramBoolean) {
          localStringBuilder.append(getRestoreButton(i, localJSONObject));
        }
      }
      localStringBuilder.append(getVersionLine(i, localJSONObject));
      localStringBuilder.append(getVersionNotes(i, localJSONObject));
      i += 1;
    }
    localStringBuilder.append("</body>");
    localStringBuilder.append("</html>");
    return localStringBuilder.toString();
  }
  
  public String getVersionString()
  {
    return failSafeGetStringFromJSON(this.mNewest, "shortversion", "") + " (" + failSafeGetStringFromJSON(this.mNewest, "version", "") + ")";
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/utils/VersionHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
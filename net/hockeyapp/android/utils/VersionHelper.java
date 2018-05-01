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
import net.hockeyapp.android.R.string;
import net.hockeyapp.android.UpdateInfoListener;
import org.json.JSONException;
import org.json.JSONObject;

public class VersionHelper
{
  private Context mContext;
  private int mCurrentVersionCode;
  private UpdateInfoListener mListener;
  private JSONObject mNewest;
  private ArrayList<JSONObject> mSortedVersions;
  
  public VersionHelper(Context paramContext, String paramString, UpdateInfoListener paramUpdateInfoListener)
  {
    this.mContext = paramContext;
    this.mListener = paramUpdateInfoListener;
    loadVersions(paramString);
    sortVersions();
  }
  
  public static int compareVersionStrings(String paramString1, String paramString2)
  {
    int i = -1;
    int j;
    if ((paramString1 == null) || (paramString2 == null)) {
      j = 0;
    }
    for (;;)
    {
      return j;
      try
      {
        Scanner localScanner = new java/util/Scanner;
        localScanner.<init>(paramString1.replaceAll("\\-.*", ""));
        paramString1 = new java/util/Scanner;
        paramString1.<init>(paramString2.replaceAll("\\-.*", ""));
        localScanner.useDelimiter("\\.");
        paramString1.useDelimiter("\\.");
        for (;;)
        {
          if ((localScanner.hasNextInt()) && (paramString1.hasNextInt()))
          {
            int k = localScanner.nextInt();
            int m = paramString1.nextInt();
            j = i;
            if (k < m) {
              break;
            }
            if (k > m)
            {
              j = 1;
              break;
            }
          }
        }
        if (localScanner.hasNextInt())
        {
          j = 1;
        }
        else
        {
          boolean bool = paramString1.hasNextInt();
          j = i;
          if (!bool) {
            j = 0;
          }
        }
      }
      catch (Exception paramString1)
      {
        j = 0;
      }
    }
  }
  
  private static long failSafeGetLongFromJSON(JSONObject paramJSONObject, String paramString, long paramLong)
  {
    try
    {
      long l = paramJSONObject.getLong(paramString);
      paramLong = l;
    }
    catch (JSONException paramJSONObject)
    {
      for (;;) {}
    }
    return paramLong;
  }
  
  private static String failSafeGetStringFromJSON(JSONObject paramJSONObject, String paramString1, String paramString2)
  {
    try
    {
      paramJSONObject = paramJSONObject.getString(paramString1);
      paramString2 = paramJSONObject;
    }
    catch (JSONException paramJSONObject)
    {
      for (;;) {}
    }
    return paramString2;
  }
  
  private String getRestoreButton(JSONObject paramJSONObject)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    paramJSONObject = getVersionID(paramJSONObject);
    if (!TextUtils.isEmpty(paramJSONObject)) {
      localStringBuilder.append(String.format("<a href='restore:%s' style='%s'>%s</a>", new Object[] { paramJSONObject, "background: #c8c8c8; color: #000; display: block; float: right; padding: 7px; margin: 0px 10px 10px; text-decoration: none;", this.mContext.getString(R.string.hockeyapp_update_restore) }));
    }
    return localStringBuilder.toString();
  }
  
  private Object getSeparator()
  {
    return "<hr style='border-top: 1px solid #c8c8c8; border-bottom: 0px; margin: 40px 10px 0px 10px;' />";
  }
  
  private int getVersionCode(JSONObject paramJSONObject)
  {
    int i = 0;
    try
    {
      j = paramJSONObject.getInt("version");
      return j;
    }
    catch (JSONException paramJSONObject)
    {
      for (;;)
      {
        int j = i;
      }
    }
  }
  
  private String getVersionID(JSONObject paramJSONObject)
  {
    String str = "";
    try
    {
      paramJSONObject = paramJSONObject.getString("id");
      return paramJSONObject;
    }
    catch (JSONException paramJSONObject)
    {
      for (;;)
      {
        paramJSONObject = str;
      }
    }
  }
  
  private String getVersionLine(int paramInt, JSONObject paramJSONObject)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    int i = getVersionCode(this.mNewest);
    int j = getVersionCode(paramJSONObject);
    paramJSONObject = getVersionName(paramJSONObject);
    localStringBuilder.append("<div style='padding: 20px 10px 10px;'><strong>");
    if (paramInt == 0) {
      localStringBuilder.append(this.mContext.getString(R.string.hockeyapp_update_newest_version)).append(':');
    }
    for (;;)
    {
      localStringBuilder.append("</strong></div>");
      return localStringBuilder.toString();
      localStringBuilder.append(String.format("%s (%s): ", new Object[] { String.format(this.mContext.getString(R.string.hockeyapp_update_version), new Object[] { paramJSONObject }), Integer.valueOf(j) }));
      if ((j != i) && (j == this.mCurrentVersionCode))
      {
        this.mCurrentVersionCode = -1;
        localStringBuilder.append(String.format("[%s]", new Object[] { this.mContext.getString(R.string.hockeyapp_update_already_installed) }));
      }
    }
  }
  
  private String getVersionName(JSONObject paramJSONObject)
  {
    String str = "";
    try
    {
      paramJSONObject = paramJSONObject.getString("shortversion");
      return paramJSONObject;
    }
    catch (JSONException paramJSONObject)
    {
      for (;;)
      {
        paramJSONObject = str;
      }
    }
  }
  
  private String getVersionNotes(JSONObject paramJSONObject)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    paramJSONObject = failSafeGetStringFromJSON(paramJSONObject, "notes", "");
    localStringBuilder.append("<div style='padding: 0px 10px;'>");
    if (paramJSONObject.trim().length() == 0) {
      localStringBuilder.append(String.format("<em>%s</em>", new Object[] { this.mContext.getString(R.string.hockeyapp_update_no_info) }));
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
    boolean bool = false;
    if (paramContext == null) {}
    for (;;)
    {
      return bool;
      try
      {
        paramContext = paramContext.getPackageManager().getApplicationInfo(paramContext.getPackageName(), 0).sourceDir;
        File localFile = new java/io/File;
        localFile.<init>(paramContext);
        long l = localFile.lastModified() / 1000L;
        if (paramLong > l + 1800L) {
          bool = true;
        }
      }
      catch (PackageManager.NameNotFoundException paramContext)
      {
        HockeyLog.error("Failed to get application info", paramContext);
      }
    }
  }
  
  /* Error */
  private void loadVersions(String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: new 72	org/json/JSONObject
    //   4: dup
    //   5: invokespecial 240	org/json/JSONObject:<init>	()V
    //   8: putfield 140	net/hockeyapp/android/utils/VersionHelper:mNewest	Lorg/json/JSONObject;
    //   11: aload_0
    //   12: new 242	java/util/ArrayList
    //   15: dup
    //   16: invokespecial 243	java/util/ArrayList:<init>	()V
    //   19: putfield 245	net/hockeyapp/android/utils/VersionHelper:mSortedVersions	Ljava/util/ArrayList;
    //   22: aload_0
    //   23: aload_0
    //   24: getfield 26	net/hockeyapp/android/utils/VersionHelper:mListener	Lnet/hockeyapp/android/UpdateInfoListener;
    //   27: invokeinterface 250 1 0
    //   32: putfield 168	net/hockeyapp/android/utils/VersionHelper:mCurrentVersionCode	I
    //   35: new 252	org/json/JSONArray
    //   38: astore_2
    //   39: aload_2
    //   40: aload_1
    //   41: invokespecial 253	org/json/JSONArray:<init>	(Ljava/lang/String;)V
    //   44: aload_0
    //   45: getfield 168	net/hockeyapp/android/utils/VersionHelper:mCurrentVersionCode	I
    //   48: istore_3
    //   49: iconst_0
    //   50: istore 4
    //   52: iload 4
    //   54: aload_2
    //   55: invokevirtual 254	org/json/JSONArray:length	()I
    //   58: if_icmpge +103 -> 161
    //   61: aload_2
    //   62: iload 4
    //   64: invokevirtual 258	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
    //   67: astore_1
    //   68: aload_1
    //   69: ldc -126
    //   71: invokevirtual 134	org/json/JSONObject:getInt	(Ljava/lang/String;)I
    //   74: iload_3
    //   75: if_icmple +73 -> 148
    //   78: iconst_1
    //   79: istore 5
    //   81: aload_1
    //   82: ldc -126
    //   84: invokevirtual 134	org/json/JSONObject:getInt	(Ljava/lang/String;)I
    //   87: iload_3
    //   88: if_icmpne +66 -> 154
    //   91: aload_0
    //   92: getfield 24	net/hockeyapp/android/utils/VersionHelper:mContext	Landroid/content/Context;
    //   95: aload_1
    //   96: ldc_w 260
    //   99: invokevirtual 76	org/json/JSONObject:getLong	(Ljava/lang/String;)J
    //   102: invokestatic 262	net/hockeyapp/android/utils/VersionHelper:isNewerThanLastUpdateTime	(Landroid/content/Context;J)Z
    //   105: ifeq +49 -> 154
    //   108: iconst_1
    //   109: istore 6
    //   111: iload 5
    //   113: ifne +8 -> 121
    //   116: iload 6
    //   118: ifeq +15 -> 133
    //   121: aload_0
    //   122: aload_1
    //   123: putfield 140	net/hockeyapp/android/utils/VersionHelper:mNewest	Lorg/json/JSONObject;
    //   126: aload_1
    //   127: ldc -126
    //   129: invokevirtual 134	org/json/JSONObject:getInt	(Ljava/lang/String;)I
    //   132: istore_3
    //   133: aload_0
    //   134: getfield 245	net/hockeyapp/android/utils/VersionHelper:mSortedVersions	Ljava/util/ArrayList;
    //   137: aload_1
    //   138: invokevirtual 266	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   141: pop
    //   142: iinc 4 1
    //   145: goto -93 -> 52
    //   148: iconst_0
    //   149: istore 5
    //   151: goto -70 -> 81
    //   154: iconst_0
    //   155: istore 6
    //   157: goto -46 -> 111
    //   160: astore_1
    //   161: return
    //   162: astore_1
    //   163: goto -2 -> 161
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	166	0	this	VersionHelper
    //   0	166	1	paramString	String
    //   38	24	2	localJSONArray	org.json.JSONArray
    //   48	85	3	i	int
    //   50	93	4	j	int
    //   79	71	5	k	int
    //   109	47	6	m	int
    // Exception table:
    //   from	to	target	type
    //   35	49	160	java/lang/NullPointerException
    //   52	78	160	java/lang/NullPointerException
    //   81	108	160	java/lang/NullPointerException
    //   121	133	160	java/lang/NullPointerException
    //   133	142	160	java/lang/NullPointerException
    //   35	49	162	org/json/JSONException
    //   52	78	162	org/json/JSONException
    //   81	108	162	org/json/JSONException
    //   121	133	162	org/json/JSONException
    //   133	142	162	org/json/JSONException
  }
  
  public static String mapGoogleVersion(String paramString)
  {
    String str;
    if ((paramString == null) || (paramString.equalsIgnoreCase("L"))) {
      str = "5.0";
    }
    for (;;)
    {
      return str;
      if (paramString.equalsIgnoreCase("M"))
      {
        str = "6.0";
      }
      else if (paramString.equalsIgnoreCase("N"))
      {
        str = "7.0";
      }
      else if (paramString.equalsIgnoreCase("O"))
      {
        str = "8.0";
      }
      else
      {
        str = paramString;
        if (Pattern.matches("^[a-zA-Z]+", paramString)) {
          str = "99.0";
        }
      }
    }
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
          if (i <= j) {}
        }
        catch (NullPointerException paramAnonymousJSONObject1)
        {
          for (;;) {}
        }
        catch (JSONException paramAnonymousJSONObject1)
        {
          for (;;) {}
        }
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
    long l1 = failSafeGetLongFromJSON(this.mNewest, "appsize", 0L);
    long l2 = l1;
    if (bool)
    {
      l2 = l1;
      if (l1 == 0L) {
        l2 = -1L;
      }
    }
    return l2;
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
          localStringBuilder.append(getRestoreButton(localJSONObject));
        }
      }
      localStringBuilder.append(getVersionLine(i, localJSONObject));
      localStringBuilder.append(getVersionNotes(localJSONObject));
      i++;
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
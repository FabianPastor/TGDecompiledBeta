package net.hockeyapp.android.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Locale;
import net.hockeyapp.android.Constants;
import net.hockeyapp.android.Tracking;
import net.hockeyapp.android.UpdateManagerListener;
import net.hockeyapp.android.utils.HockeyLog;
import net.hockeyapp.android.utils.VersionCache;
import net.hockeyapp.android.utils.VersionHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CheckUpdateTask
  extends AsyncTask<Void, String, JSONArray>
{
  protected static final String APK = "apk";
  private static final int MAX_NUMBER_OF_VERSIONS = 25;
  protected String appIdentifier = null;
  private Context context = null;
  protected UpdateManagerListener listener;
  protected Boolean mandatory = Boolean.valueOf(false);
  protected String urlString = null;
  private long usageTime = 0L;
  
  public CheckUpdateTask(WeakReference<? extends Context> paramWeakReference, String paramString)
  {
    this(paramWeakReference, paramString, null);
  }
  
  public CheckUpdateTask(WeakReference<? extends Context> paramWeakReference, String paramString1, String paramString2)
  {
    this(paramWeakReference, paramString1, paramString2, null);
  }
  
  public CheckUpdateTask(WeakReference<? extends Context> paramWeakReference, String paramString1, String paramString2, UpdateManagerListener paramUpdateManagerListener)
  {
    this.appIdentifier = paramString2;
    this.urlString = paramString1;
    this.listener = paramUpdateManagerListener;
    paramString1 = null;
    if (paramWeakReference != null) {
      paramString1 = (Context)paramWeakReference.get();
    }
    if (paramString1 != null)
    {
      this.context = paramString1.getApplicationContext();
      this.usageTime = Tracking.getUsageTime(paramString1);
      Constants.loadFromContext(paramString1);
    }
  }
  
  private static String convertStreamToString(InputStream paramInputStream)
  {
    BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(paramInputStream), 1024);
    StringBuilder localStringBuilder = new StringBuilder();
    try
    {
      for (;;)
      {
        String str = localBufferedReader.readLine();
        if (str == null) {
          break;
        }
        localStringBuilder.append(str + "\n");
      }
      try
      {
        paramInputStream.close();
        throw ((Throwable)localObject);
      }
      catch (IOException paramInputStream)
      {
        for (;;)
        {
          paramInputStream.printStackTrace();
        }
      }
    }
    catch (IOException localIOException)
    {
      localIOException = localIOException;
      localIOException.printStackTrace();
      try
      {
        paramInputStream.close();
        for (;;)
        {
          return localStringBuilder.toString();
          try
          {
            paramInputStream.close();
          }
          catch (IOException paramInputStream)
          {
            paramInputStream.printStackTrace();
          }
        }
      }
      catch (IOException paramInputStream)
      {
        for (;;)
        {
          paramInputStream.printStackTrace();
        }
      }
    }
    finally {}
  }
  
  private String encodeParam(String paramString)
  {
    try
    {
      paramString = URLEncoder.encode(paramString, "UTF-8");
      return paramString;
    }
    catch (UnsupportedEncodingException paramString) {}
    return "";
  }
  
  private boolean findNewVersion(JSONArray paramJSONArray, int paramInt)
  {
    boolean bool1 = false;
    int i = 0;
    boolean bool2 = bool1;
    for (;;)
    {
      int j;
      int k;
      try
      {
        if (i < paramJSONArray.length())
        {
          JSONObject localJSONObject = paramJSONArray.getJSONObject(i);
          if (localJSONObject.getInt("version") > paramInt)
          {
            j = 1;
            if ((localJSONObject.getInt("version") != paramInt) || (!VersionHelper.isNewerThanLastUpdateTime(this.context, localJSONObject.getLong("timestamp")))) {
              continue;
            }
            k = 1;
            if (VersionHelper.compareVersionStrings(localJSONObject.getString("minimum_os_version"), VersionHelper.mapGoogleVersion(Build.VERSION.RELEASE)) > 0) {
              continue;
            }
            m = 1;
            break label174;
            bool2 = bool1;
            if (m != 0)
            {
              if (localJSONObject.has("mandatory")) {
                this.mandatory = Boolean.valueOf(this.mandatory.booleanValue() | localJSONObject.getBoolean("mandatory"));
              }
              bool2 = true;
            }
            i += 1;
            bool1 = bool2;
            break;
          }
          j = 0;
          continue;
          k = 0;
          continue;
          int m = 0;
        }
      }
      catch (JSONException paramJSONArray)
      {
        bool2 = false;
      }
      return bool2;
      label174:
      if (j == 0)
      {
        bool2 = bool1;
        if (k == 0) {}
      }
    }
  }
  
  private JSONArray limitResponseSize(JSONArray paramJSONArray)
  {
    JSONArray localJSONArray = new JSONArray();
    int i = 0;
    for (;;)
    {
      if (i < Math.min(paramJSONArray.length(), 25)) {}
      try
      {
        localJSONArray.put(paramJSONArray.get(i));
        i += 1;
        continue;
        return localJSONArray;
      }
      catch (JSONException localJSONException)
      {
        for (;;) {}
      }
    }
  }
  
  public void attach(WeakReference<? extends Context> paramWeakReference)
  {
    Context localContext = null;
    if (paramWeakReference != null) {
      localContext = (Context)paramWeakReference.get();
    }
    if (localContext != null)
    {
      this.context = localContext.getApplicationContext();
      Constants.loadFromContext(localContext);
    }
  }
  
  protected void cleanUp()
  {
    this.urlString = null;
    this.appIdentifier = null;
  }
  
  protected URLConnection createConnection(URL paramURL)
    throws IOException
  {
    paramURL = paramURL.openConnection();
    paramURL.addRequestProperty("User-Agent", "HockeySDK/Android");
    if (Build.VERSION.SDK_INT <= 9) {
      paramURL.setRequestProperty("connection", "close");
    }
    return paramURL;
  }
  
  public void detach()
  {
    this.context = null;
  }
  
  protected JSONArray doInBackground(Void... paramVarArgs)
  {
    try
    {
      int i = getVersionCode();
      paramVarArgs = new JSONArray(VersionCache.getVersionInfo(this.context));
      if ((getCachingEnabled()) && (findNewVersion(paramVarArgs, i)))
      {
        HockeyLog.verbose("HockeyUpdate", "Returning cached JSON");
        return paramVarArgs;
      }
      paramVarArgs = createConnection(new URL(getURLString("json")));
      paramVarArgs.connect();
      paramVarArgs = new BufferedInputStream(paramVarArgs.getInputStream());
      String str = convertStreamToString(paramVarArgs);
      paramVarArgs.close();
      paramVarArgs = new JSONArray(str);
      if (findNewVersion(paramVarArgs, i))
      {
        paramVarArgs = limitResponseSize(paramVarArgs);
        return paramVarArgs;
      }
    }
    catch (JSONException paramVarArgs)
    {
      paramVarArgs.printStackTrace();
      return null;
    }
    catch (IOException paramVarArgs)
    {
      for (;;) {}
    }
  }
  
  protected boolean getCachingEnabled()
  {
    return true;
  }
  
  protected String getURLString(String paramString)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(this.urlString);
    localStringBuilder.append("api/2/apps/");
    if (this.appIdentifier != null) {}
    for (String str = this.appIdentifier;; str = this.context.getPackageName())
    {
      localStringBuilder.append(str);
      localStringBuilder.append("?format=" + paramString);
      if (!TextUtils.isEmpty(Settings.Secure.getString(this.context.getContentResolver(), "android_id"))) {
        localStringBuilder.append("&udid=" + encodeParam(Settings.Secure.getString(this.context.getContentResolver(), "android_id")));
      }
      paramString = this.context.getSharedPreferences("net.hockeyapp.android.login", 0);
      str = paramString.getString("auid", null);
      if (!TextUtils.isEmpty(str)) {
        localStringBuilder.append("&auid=" + encodeParam(str));
      }
      paramString = paramString.getString("iuid", null);
      if (!TextUtils.isEmpty(paramString)) {
        localStringBuilder.append("&iuid=" + encodeParam(paramString));
      }
      localStringBuilder.append("&os=Android");
      localStringBuilder.append("&os_version=" + encodeParam(Constants.ANDROID_VERSION));
      localStringBuilder.append("&device=" + encodeParam(Constants.PHONE_MODEL));
      localStringBuilder.append("&oem=" + encodeParam(Constants.PHONE_MANUFACTURER));
      localStringBuilder.append("&app_version=" + encodeParam(Constants.APP_VERSION));
      localStringBuilder.append("&sdk=" + encodeParam("HockeySDK"));
      localStringBuilder.append("&sdk_version=" + encodeParam("4.0.1"));
      localStringBuilder.append("&lang=" + encodeParam(Locale.getDefault().getLanguage()));
      localStringBuilder.append("&usage_time=" + this.usageTime);
      return localStringBuilder.toString();
    }
  }
  
  protected int getVersionCode()
  {
    return Integer.parseInt(Constants.APP_VERSION);
  }
  
  protected void onPostExecute(JSONArray paramJSONArray)
  {
    if (paramJSONArray != null)
    {
      HockeyLog.verbose("HockeyUpdate", "Received Update Info");
      if (this.listener != null) {
        this.listener.onUpdateAvailable(paramJSONArray, getURLString("apk"));
      }
    }
    do
    {
      return;
      HockeyLog.verbose("HockeyUpdate", "No Update Info available");
    } while (this.listener == null);
    this.listener.onNoUpdateAvailable();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/tasks/CheckUpdateTask.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
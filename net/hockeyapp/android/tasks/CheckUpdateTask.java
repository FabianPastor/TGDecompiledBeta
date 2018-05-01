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
import java.util.concurrent.Future;
import net.hockeyapp.android.Constants;
import net.hockeyapp.android.Tracking;
import net.hockeyapp.android.UpdateManagerListener;
import net.hockeyapp.android.utils.HockeyLog;
import net.hockeyapp.android.utils.Util;
import net.hockeyapp.android.utils.VersionHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CheckUpdateTask
  extends AsyncTask<Void, String, JSONArray>
{
  protected String apkUrlString = null;
  protected String appIdentifier = null;
  protected UpdateManagerListener listener;
  protected Boolean mandatory = Boolean.valueOf(false);
  protected String urlString = null;
  private long usageTime = 0L;
  private WeakReference<Context> weakContext = null;
  
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
      this.weakContext = new WeakReference(paramString1.getApplicationContext());
      this.usageTime = Tracking.getUsageTime(paramString1);
      Constants.loadFromContext(paramString1);
    }
  }
  
  private String encodeParam(String paramString)
  {
    try
    {
      paramString = URLEncoder.encode(paramString, "UTF-8");
      return paramString;
    }
    catch (UnsupportedEncodingException paramString)
    {
      for (;;)
      {
        paramString = "";
      }
    }
  }
  
  private boolean findNewVersion(Context paramContext, JSONArray paramJSONArray, int paramInt)
  {
    boolean bool1 = false;
    int i = 0;
    for (;;)
    {
      bool2 = bool1;
      try
      {
        if (i < paramJSONArray.length())
        {
          JSONObject localJSONObject = paramJSONArray.getJSONObject(i);
          int j;
          label41:
          int k;
          if (localJSONObject.getInt("version") > paramInt)
          {
            j = 1;
            if ((localJSONObject.getInt("version") != paramInt) || (!VersionHelper.isNewerThanLastUpdateTime(paramContext, localJSONObject.getLong("timestamp")))) {
              break label165;
            }
            k = 1;
            label69:
            if (VersionHelper.compareVersionStrings(localJSONObject.getString("minimum_os_version"), VersionHelper.mapGoogleVersion(Build.VERSION.RELEASE)) > 0) {
              break label171;
            }
          }
          label165:
          label171:
          for (int m = 1;; m = 0)
          {
            if (j == 0)
            {
              bool2 = bool1;
              if (k == 0) {}
            }
            else
            {
              bool2 = bool1;
              if (m != 0)
              {
                if (localJSONObject.has("mandatory")) {
                  this.mandatory = Boolean.valueOf(this.mandatory.booleanValue() | localJSONObject.getBoolean("mandatory"));
                }
                bool2 = true;
              }
            }
            i++;
            bool1 = bool2;
            break;
            j = 0;
            break label41;
            k = 0;
            break label69;
          }
        }
        return bool2;
      }
      catch (JSONException paramContext)
      {
        bool2 = false;
      }
    }
  }
  
  private String getURLString(Context paramContext, String paramString)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(this.urlString);
    localStringBuilder.append("api/2/apps/");
    String str;
    if (this.appIdentifier != null) {
      str = this.appIdentifier;
    }
    for (;;)
    {
      localStringBuilder.append(str);
      localStringBuilder.append("?format=").append(paramString);
      str = null;
      try
      {
        paramString = (String)Constants.getDeviceIdentifier().get();
        if (!TextUtils.isEmpty(paramString)) {
          localStringBuilder.append("&udid=").append(encodeParam(paramString));
        }
        paramContext = paramContext.getSharedPreferences("net.hockeyapp.android.login", 0);
        paramString = paramContext.getString("auid", null);
        if (!TextUtils.isEmpty(paramString)) {
          localStringBuilder.append("&auid=").append(encodeParam(paramString));
        }
        paramContext = paramContext.getString("iuid", null);
        if (!TextUtils.isEmpty(paramContext)) {
          localStringBuilder.append("&iuid=").append(encodeParam(paramContext));
        }
        localStringBuilder.append("&os=Android");
        localStringBuilder.append("&os_version=").append(encodeParam(Constants.ANDROID_VERSION));
        localStringBuilder.append("&device=").append(encodeParam(Constants.PHONE_MODEL));
        localStringBuilder.append("&oem=").append(encodeParam(Constants.PHONE_MANUFACTURER));
        localStringBuilder.append("&app_version=").append(encodeParam(Constants.APP_VERSION));
        localStringBuilder.append("&sdk=").append(encodeParam("HockeySDK"));
        localStringBuilder.append("&sdk_version=").append(encodeParam("5.0.4"));
        localStringBuilder.append("&lang=").append(encodeParam(Locale.getDefault().getLanguage()));
        localStringBuilder.append("&usage_time=").append(this.usageTime);
        return localStringBuilder.toString();
        str = paramContext.getPackageName();
      }
      catch (InterruptedException paramString)
      {
        for (;;)
        {
          HockeyLog.debug("Error get device identifier", paramString);
          paramString = str;
        }
      }
      catch (ExecutionException paramString)
      {
        for (;;) {}
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
        i++;
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
      this.weakContext = new WeakReference(localContext.getApplicationContext());
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
    paramURL.addRequestProperty("User-Agent", "HockeySDK/Android 5.0.4");
    return paramURL;
  }
  
  public void detach()
  {
    this.weakContext = null;
  }
  
  protected JSONArray doInBackground(Void... paramVarArgs)
  {
    if (this.weakContext != null)
    {
      paramVarArgs = (Context)this.weakContext.get();
      if (paramVarArgs != null) {
        break label31;
      }
      paramVarArgs = null;
    }
    for (;;)
    {
      return paramVarArgs;
      paramVarArgs = null;
      break;
      label31:
      this.apkUrlString = getURLString(paramVarArgs, "apk");
      try
      {
        int i = getVersionCode();
        Object localObject1 = new java/net/URL;
        ((URL)localObject1).<init>(getURLString(paramVarArgs, "json"));
        localObject1 = createConnection((URL)localObject1);
        ((URLConnection)localObject1).connect();
        Object localObject2 = new java/io/BufferedInputStream;
        ((BufferedInputStream)localObject2).<init>(((URLConnection)localObject1).getInputStream());
        localObject1 = Util.convertStreamToString((InputStream)localObject2);
        ((InputStream)localObject2).close();
        localObject2 = new org/json/JSONArray;
        ((JSONArray)localObject2).<init>((String)localObject1);
        if (findNewVersion(paramVarArgs, (JSONArray)localObject2, i))
        {
          localObject1 = limitResponseSize((JSONArray)localObject2);
          paramVarArgs = (Void[])localObject1;
        }
      }
      catch (JSONException localJSONException)
      {
        if (Util.isConnectedToNetwork(paramVarArgs)) {
          HockeyLog.error("HockeyUpdate", "Could not fetch updates although connected to internet", localJSONException);
        }
        paramVarArgs = null;
      }
      catch (IOException localIOException)
      {
        for (;;) {}
      }
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
        this.listener.onUpdateAvailable(paramJSONArray, this.apkUrlString);
      }
    }
    for (;;)
    {
      return;
      HockeyLog.verbose("HockeyUpdate", "No Update Info available");
      if (this.listener != null) {
        this.listener.onNoUpdateAvailable();
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/tasks/CheckUpdateTask.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
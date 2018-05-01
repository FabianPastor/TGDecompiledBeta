package net.hockeyapp.android.tasks;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;
import net.hockeyapp.android.Constants;
import net.hockeyapp.android.utils.HockeyLog;
import net.hockeyapp.android.utils.HttpURLConnectionBuilder;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressLint({"StaticFieldLeak"})
public class LoginTask
  extends ConnectionTask<Void, Void, Boolean>
{
  private Context mContext;
  private Handler mHandler;
  private final int mMode;
  private final Map<String, String> mParams;
  private ProgressDialog mProgressDialog;
  private boolean mShowProgressDialog;
  private final String mUrlString;
  
  public LoginTask(Context paramContext, Handler paramHandler, String paramString, int paramInt, Map<String, String> paramMap)
  {
    this.mContext = paramContext;
    this.mHandler = paramHandler;
    this.mUrlString = paramString;
    this.mMode = paramInt;
    this.mParams = paramMap;
    this.mShowProgressDialog = true;
    if (paramContext != null) {
      Constants.loadFromContext(paramContext);
    }
  }
  
  private boolean handleResponse(String paramString)
  {
    Object localObject = this.mContext.getSharedPreferences("net.hockeyapp.android.login", 0);
    for (;;)
    {
      try
      {
        localJSONObject = new org/json/JSONObject;
        localJSONObject.<init>(paramString);
        paramString = localJSONObject.getString("status");
        if (!TextUtils.isEmpty(paramString)) {
          continue;
        }
        bool = false;
      }
      catch (JSONException paramString)
      {
        JSONObject localJSONObject;
        HockeyLog.error("Failed to parse login response", paramString);
        boolean bool = false;
        continue;
      }
      return bool;
      if (this.mMode == 1)
      {
        if (paramString.equals("identified"))
        {
          paramString = localJSONObject.getString("iuid");
          if (!TextUtils.isEmpty(paramString))
          {
            ((SharedPreferences)localObject).edit().putString("iuid", paramString).putString("email", (String)this.mParams.get("email")).apply();
            bool = true;
          }
        }
      }
      else if (this.mMode == 2)
      {
        if (paramString.equals("authorized"))
        {
          paramString = localJSONObject.getString("auid");
          if (!TextUtils.isEmpty(paramString))
          {
            ((SharedPreferences)localObject).edit().putString("auid", paramString).putString("email", (String)this.mParams.get("email")).apply();
            bool = true;
          }
        }
      }
      else
      {
        if (this.mMode != 3) {
          continue;
        }
        if (paramString.equals("validated"))
        {
          bool = true;
          continue;
        }
        ((SharedPreferences)localObject).edit().remove("iuid").remove("auid").remove("email").apply();
      }
      bool = false;
    }
    localObject = new java/lang/IllegalArgumentException;
    paramString = new java/lang/StringBuilder;
    paramString.<init>();
    ((IllegalArgumentException)localObject).<init>("Login mode " + this.mMode + " not supported.");
    throw ((Throwable)localObject);
  }
  
  private HttpURLConnection makeRequest(int paramInt, Map<String, String> paramMap)
    throws IOException
  {
    if (paramInt == 1) {
      paramMap = new HttpURLConnectionBuilder(this.mUrlString).setRequestMethod("POST").writeFormFields(paramMap).build();
    }
    for (;;)
    {
      return paramMap;
      if (paramInt == 2)
      {
        paramMap = new HttpURLConnectionBuilder(this.mUrlString).setRequestMethod("POST").setBasicAuthorization((String)paramMap.get("email"), (String)paramMap.get("password")).build();
      }
      else
      {
        if (paramInt != 3) {
          break;
        }
        String str = (String)paramMap.get("type");
        paramMap = (String)paramMap.get("id");
        paramMap = new HttpURLConnectionBuilder(this.mUrlString + "?" + str + "=" + paramMap).build();
      }
    }
    throw new IllegalArgumentException("Login mode " + paramInt + " not supported.");
  }
  
  public void attach(Context paramContext, Handler paramHandler)
  {
    this.mContext = paramContext;
    this.mHandler = paramHandler;
  }
  
  public void detach()
  {
    this.mContext = null;
    this.mHandler = null;
    this.mProgressDialog = null;
  }
  
  protected Boolean doInBackground(Void... paramVarArgs)
  {
    localObject = null;
    for (paramVarArgs = null;; paramVarArgs = Boolean.valueOf(false)) {
      try
      {
        HttpURLConnection localHttpURLConnection = makeRequest(this.mMode, this.mParams);
        paramVarArgs = localHttpURLConnection;
        localObject = localHttpURLConnection;
        localHttpURLConnection.connect();
        paramVarArgs = localHttpURLConnection;
        localObject = localHttpURLConnection;
        if (localHttpURLConnection.getResponseCode() == 200)
        {
          paramVarArgs = localHttpURLConnection;
          localObject = localHttpURLConnection;
          String str = getStringFromConnection(localHttpURLConnection);
          paramVarArgs = localHttpURLConnection;
          localObject = localHttpURLConnection;
          if (!TextUtils.isEmpty(str))
          {
            paramVarArgs = localHttpURLConnection;
            localObject = localHttpURLConnection;
            boolean bool = handleResponse(str);
            localObject = Boolean.valueOf(bool);
            paramVarArgs = (Void[])localObject;
            if (localHttpURLConnection != null)
            {
              localHttpURLConnection.disconnect();
              paramVarArgs = (Void[])localObject;
            }
            return paramVarArgs;
          }
        }
        if (localHttpURLConnection != null) {
          localHttpURLConnection.disconnect();
        }
      }
      catch (IOException localIOException)
      {
        for (;;)
        {
          localObject = paramVarArgs;
          HockeyLog.error("Failed to login", localIOException);
          if (paramVarArgs != null) {
            paramVarArgs.disconnect();
          }
        }
      }
      finally
      {
        if (localObject == null) {
          break;
        }
        ((HttpURLConnection)localObject).disconnect();
      }
    }
  }
  
  protected void onPostExecute(Boolean paramBoolean)
  {
    if (this.mProgressDialog != null) {}
    try
    {
      this.mProgressDialog.dismiss();
      if (this.mHandler != null)
      {
        Message localMessage = new Message();
        Bundle localBundle = new Bundle();
        localBundle.putBoolean("success", paramBoolean.booleanValue());
        localMessage.setData(localBundle);
        this.mHandler.sendMessage(localMessage);
      }
      return;
    }
    catch (Exception localException)
    {
      for (;;) {}
    }
  }
  
  protected void onPreExecute()
  {
    if (((this.mProgressDialog == null) || (!this.mProgressDialog.isShowing())) && (this.mShowProgressDialog)) {
      this.mProgressDialog = ProgressDialog.show(this.mContext, "", "Please wait...", true, false);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/tasks/LoginTask.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
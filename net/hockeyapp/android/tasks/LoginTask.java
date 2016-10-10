package net.hockeyapp.android.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.Map;
import net.hockeyapp.android.Constants;
import net.hockeyapp.android.utils.HttpURLConnectionBuilder;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginTask
  extends ConnectionTask<Void, Void, Boolean>
{
  public static final String BUNDLE_SUCCESS = "success";
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
    SharedPreferences localSharedPreferences = this.mContext.getSharedPreferences("net.hockeyapp.android.login", 0);
    try
    {
      paramString = new JSONObject(paramString);
      String str = paramString.getString("status");
      if (TextUtils.isEmpty(str)) {
        return false;
      }
      if (this.mMode == 1)
      {
        if (!str.equals("identified")) {
          break label228;
        }
        paramString = paramString.getString("iuid");
        if (TextUtils.isEmpty(paramString)) {
          break label228;
        }
        localSharedPreferences.edit().putString("iuid", paramString).apply();
        return true;
      }
      if (this.mMode == 2)
      {
        if (!str.equals("authorized")) {
          break label228;
        }
        paramString = paramString.getString("auid");
        if (TextUtils.isEmpty(paramString)) {
          break label228;
        }
        localSharedPreferences.edit().putString("auid", paramString).apply();
        return true;
      }
      if (this.mMode == 3)
      {
        if (str.equals("validated")) {
          return true;
        }
        localSharedPreferences.edit().remove("iuid").remove("auid").apply();
        return false;
      }
    }
    catch (JSONException paramString)
    {
      paramString.printStackTrace();
      return false;
    }
    throw new IllegalArgumentException("Login mode " + this.mMode + " not supported.");
    label228:
    return false;
  }
  
  private HttpURLConnection makeRequest(int paramInt, Map<String, String> paramMap)
    throws IOException
  {
    if (paramInt == 1) {
      return new HttpURLConnectionBuilder(this.mUrlString).setRequestMethod("POST").writeFormFields(paramMap).build();
    }
    if (paramInt == 2) {
      return new HttpURLConnectionBuilder(this.mUrlString).setRequestMethod("POST").setBasicAuthorization((String)paramMap.get("email"), (String)paramMap.get("password")).build();
    }
    if (paramInt == 3)
    {
      String str = (String)paramMap.get("type");
      paramMap = (String)paramMap.get("id");
      return new HttpURLConnectionBuilder(this.mUrlString + "?" + str + "=" + paramMap).build();
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
    Object localObject3 = null;
    paramVarArgs = null;
    localObject1 = null;
    try
    {
      localHttpURLConnection = makeRequest(this.mMode, this.mParams);
      localObject1 = localHttpURLConnection;
      localObject3 = localHttpURLConnection;
      paramVarArgs = localHttpURLConnection;
      localHttpURLConnection.connect();
      localObject1 = localHttpURLConnection;
      localObject3 = localHttpURLConnection;
      paramVarArgs = localHttpURLConnection;
      if (localHttpURLConnection.getResponseCode() == 200)
      {
        localObject1 = localHttpURLConnection;
        localObject3 = localHttpURLConnection;
        paramVarArgs = localHttpURLConnection;
        String str = getStringFromConnection(localHttpURLConnection);
        localObject1 = localHttpURLConnection;
        localObject3 = localHttpURLConnection;
        paramVarArgs = localHttpURLConnection;
        if (!TextUtils.isEmpty(str))
        {
          localObject1 = localHttpURLConnection;
          localObject3 = localHttpURLConnection;
          paramVarArgs = localHttpURLConnection;
          boolean bool = handleResponse(str);
          return Boolean.valueOf(bool);
        }
      }
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      for (;;)
      {
        HttpURLConnection localHttpURLConnection;
        paramVarArgs = (Void[])localObject1;
        localUnsupportedEncodingException.printStackTrace();
        if (localObject1 != null) {
          ((HttpURLConnection)localObject1).disconnect();
        }
      }
    }
    catch (IOException localIOException)
    {
      for (;;)
      {
        paramVarArgs = localUnsupportedEncodingException;
        localIOException.printStackTrace();
        if (localUnsupportedEncodingException != null) {
          localUnsupportedEncodingException.disconnect();
        }
      }
    }
    finally
    {
      if (paramVarArgs == null) {
        break label189;
      }
      paramVarArgs.disconnect();
    }
    return Boolean.valueOf(false);
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
      for (;;)
      {
        localException.printStackTrace();
      }
    }
  }
  
  protected void onPreExecute()
  {
    if (((this.mProgressDialog == null) || (!this.mProgressDialog.isShowing())) && (this.mShowProgressDialog)) {
      this.mProgressDialog = ProgressDialog.show(this.mContext, "", "Please wait...", true, false);
    }
  }
  
  public void setShowProgressDialog(boolean paramBoolean)
  {
    this.mShowProgressDialog = paramBoolean;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/tasks/LoginTask.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
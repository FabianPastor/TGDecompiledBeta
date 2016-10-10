package net.hockeyapp.android.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.hockeyapp.android.Constants;
import net.hockeyapp.android.R.string;
import net.hockeyapp.android.utils.HockeyLog;
import net.hockeyapp.android.utils.HttpURLConnectionBuilder;
import net.hockeyapp.android.utils.Util;

public class SendFeedbackTask
  extends ConnectionTask<Void, Void, HashMap<String, String>>
{
  public static final String BUNDLE_FEEDBACK_RESPONSE = "feedback_response";
  public static final String BUNDLE_FEEDBACK_STATUS = "feedback_status";
  public static final String BUNDLE_REQUEST_TYPE = "request_type";
  private static final String FILE_TAG = "HockeyApp";
  private static final String TAG = "SendFeedbackTask";
  private List<Uri> mAttachmentUris;
  private Context mContext;
  private String mEmail;
  private Handler mHandler;
  private boolean mIsFetchMessages;
  private int mLastMessageId;
  private String mName;
  private ProgressDialog mProgressDialog;
  private boolean mShowProgressDialog;
  private String mSubject;
  private String mText;
  private String mToken;
  private String mUrlString;
  
  public SendFeedbackTask(Context paramContext, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, List<Uri> paramList, String paramString6, Handler paramHandler, boolean paramBoolean)
  {
    this.mContext = paramContext;
    this.mUrlString = paramString1;
    this.mName = paramString2;
    this.mEmail = paramString3;
    this.mSubject = paramString4;
    this.mText = paramString5;
    this.mAttachmentUris = paramList;
    this.mToken = paramString6;
    this.mHandler = paramHandler;
    this.mIsFetchMessages = paramBoolean;
    this.mShowProgressDialog = true;
    this.mLastMessageId = -1;
    if (paramContext != null) {
      Constants.loadFromContext(paramContext);
    }
  }
  
  private void clearTemporaryFolder(HashMap<String, String> paramHashMap)
  {
    paramHashMap = (String)paramHashMap.get("status");
    if ((paramHashMap != null) && (paramHashMap.startsWith("2")) && (this.mContext != null))
    {
      paramHashMap = new File(this.mContext.getCacheDir(), "HockeyApp");
      if ((paramHashMap != null) && (paramHashMap.exists()))
      {
        paramHashMap = paramHashMap.listFiles();
        int j = paramHashMap.length;
        int i = 0;
        while (i < j)
        {
          Object localObject = paramHashMap[i];
          if ((localObject != null) && (!Boolean.valueOf(((File)localObject).delete()).booleanValue())) {
            HockeyLog.debug("SendFeedbackTask", "Error deleting file from temporary folder");
          }
          i += 1;
        }
      }
    }
  }
  
  private HashMap<String, String> doGet()
  {
    Object localObject4 = new StringBuilder();
    ((StringBuilder)localObject4).append(this.mUrlString + Util.encodeParam(this.mToken));
    if (this.mLastMessageId != -1) {
      ((StringBuilder)localObject4).append("?last_message_id=" + this.mLastMessageId);
    }
    localHashMap = new HashMap();
    localObject3 = null;
    localObject1 = null;
    try
    {
      localObject4 = new HttpURLConnectionBuilder(((StringBuilder)localObject4).toString()).build();
      localObject1 = localObject4;
      localObject3 = localObject4;
      localHashMap.put("type", "fetch");
      localObject1 = localObject4;
      localObject3 = localObject4;
      ((HttpURLConnection)localObject4).connect();
      localObject1 = localObject4;
      localObject3 = localObject4;
      localHashMap.put("status", String.valueOf(((HttpURLConnection)localObject4).getResponseCode()));
      localObject1 = localObject4;
      localObject3 = localObject4;
      localHashMap.put("response", getStringFromConnection((HttpURLConnection)localObject4));
    }
    catch (IOException localIOException)
    {
      localObject3 = localObject1;
      localIOException.printStackTrace();
      return localHashMap;
    }
    finally
    {
      if (localObject3 == null) {
        break label198;
      }
      ((HttpURLConnection)localObject3).disconnect();
    }
    return localHashMap;
  }
  
  private HashMap<String, String> doPostPut()
  {
    localHashMap1 = new HashMap();
    localHashMap1.put("type", "send");
    Object localObject6 = null;
    Object localObject5 = null;
    localObject2 = localObject5;
    localObject1 = localObject6;
    for (;;)
    {
      try
      {
        HashMap localHashMap2 = new HashMap();
        localObject2 = localObject5;
        localObject1 = localObject6;
        localHashMap2.put("name", this.mName);
        localObject2 = localObject5;
        localObject1 = localObject6;
        localHashMap2.put("email", this.mEmail);
        localObject2 = localObject5;
        localObject1 = localObject6;
        localHashMap2.put("subject", this.mSubject);
        localObject2 = localObject5;
        localObject1 = localObject6;
        localHashMap2.put("text", this.mText);
        localObject2 = localObject5;
        localObject1 = localObject6;
        localHashMap2.put("bundle_identifier", Constants.APP_PACKAGE);
        localObject2 = localObject5;
        localObject1 = localObject6;
        localHashMap2.put("bundle_short_version", Constants.APP_VERSION_NAME);
        localObject2 = localObject5;
        localObject1 = localObject6;
        localHashMap2.put("bundle_version", Constants.APP_VERSION);
        localObject2 = localObject5;
        localObject1 = localObject6;
        localHashMap2.put("os_version", Constants.ANDROID_VERSION);
        localObject2 = localObject5;
        localObject1 = localObject6;
        localHashMap2.put("oem", Constants.PHONE_MANUFACTURER);
        localObject2 = localObject5;
        localObject1 = localObject6;
        localHashMap2.put("model", Constants.PHONE_MODEL);
        localObject2 = localObject5;
        localObject1 = localObject6;
        if (this.mToken != null)
        {
          localObject2 = localObject5;
          localObject1 = localObject6;
          this.mUrlString = (this.mUrlString + this.mToken + "/");
        }
        localObject2 = localObject5;
        localObject1 = localObject6;
        HttpURLConnectionBuilder localHttpURLConnectionBuilder = new HttpURLConnectionBuilder(this.mUrlString);
        localObject2 = localObject5;
        localObject1 = localObject6;
        if (this.mToken == null) {
          continue;
        }
        localObject4 = "PUT";
        localObject2 = localObject5;
        localObject1 = localObject6;
        localObject4 = localHttpURLConnectionBuilder.setRequestMethod((String)localObject4).writeFormFields(localHashMap2).build();
        localObject2 = localObject4;
        localObject1 = localObject4;
        ((HttpURLConnection)localObject4).connect();
        localObject2 = localObject4;
        localObject1 = localObject4;
        localHashMap1.put("status", String.valueOf(((HttpURLConnection)localObject4).getResponseCode()));
        localObject2 = localObject4;
        localObject1 = localObject4;
        localHashMap1.put("response", getStringFromConnection((HttpURLConnection)localObject4));
      }
      catch (IOException localIOException)
      {
        Object localObject4;
        localObject1 = localObject2;
        localIOException.printStackTrace();
        return localHashMap1;
      }
      finally
      {
        if (localObject1 == null) {
          continue;
        }
        ((HttpURLConnection)localObject1).disconnect();
      }
      return localHashMap1;
      localObject4 = "POST";
    }
  }
  
  private HashMap<String, String> doPostPutWithAttachments()
  {
    localHashMap1 = new HashMap();
    localHashMap1.put("type", "send");
    Object localObject6 = null;
    Object localObject5 = null;
    localObject2 = localObject5;
    localObject1 = localObject6;
    for (;;)
    {
      try
      {
        HashMap localHashMap2 = new HashMap();
        localObject2 = localObject5;
        localObject1 = localObject6;
        localHashMap2.put("name", this.mName);
        localObject2 = localObject5;
        localObject1 = localObject6;
        localHashMap2.put("email", this.mEmail);
        localObject2 = localObject5;
        localObject1 = localObject6;
        localHashMap2.put("subject", this.mSubject);
        localObject2 = localObject5;
        localObject1 = localObject6;
        localHashMap2.put("text", this.mText);
        localObject2 = localObject5;
        localObject1 = localObject6;
        localHashMap2.put("bundle_identifier", Constants.APP_PACKAGE);
        localObject2 = localObject5;
        localObject1 = localObject6;
        localHashMap2.put("bundle_short_version", Constants.APP_VERSION_NAME);
        localObject2 = localObject5;
        localObject1 = localObject6;
        localHashMap2.put("bundle_version", Constants.APP_VERSION);
        localObject2 = localObject5;
        localObject1 = localObject6;
        localHashMap2.put("os_version", Constants.ANDROID_VERSION);
        localObject2 = localObject5;
        localObject1 = localObject6;
        localHashMap2.put("oem", Constants.PHONE_MANUFACTURER);
        localObject2 = localObject5;
        localObject1 = localObject6;
        localHashMap2.put("model", Constants.PHONE_MODEL);
        localObject2 = localObject5;
        localObject1 = localObject6;
        if (this.mToken != null)
        {
          localObject2 = localObject5;
          localObject1 = localObject6;
          this.mUrlString = (this.mUrlString + this.mToken + "/");
        }
        localObject2 = localObject5;
        localObject1 = localObject6;
        HttpURLConnectionBuilder localHttpURLConnectionBuilder = new HttpURLConnectionBuilder(this.mUrlString);
        localObject2 = localObject5;
        localObject1 = localObject6;
        if (this.mToken == null) {
          continue;
        }
        localObject4 = "PUT";
        localObject2 = localObject5;
        localObject1 = localObject6;
        localObject4 = localHttpURLConnectionBuilder.setRequestMethod((String)localObject4).writeMultipartData(localHashMap2, this.mContext, this.mAttachmentUris).build();
        localObject2 = localObject4;
        localObject1 = localObject4;
        ((HttpURLConnection)localObject4).connect();
        localObject2 = localObject4;
        localObject1 = localObject4;
        localHashMap1.put("status", String.valueOf(((HttpURLConnection)localObject4).getResponseCode()));
        localObject2 = localObject4;
        localObject1 = localObject4;
        localHashMap1.put("response", getStringFromConnection((HttpURLConnection)localObject4));
      }
      catch (IOException localIOException)
      {
        Object localObject4;
        localObject1 = localObject2;
        localIOException.printStackTrace();
        return localHashMap1;
      }
      finally
      {
        if (localObject1 == null) {
          continue;
        }
        ((HttpURLConnection)localObject1).disconnect();
      }
      return localHashMap1;
      localObject4 = "POST";
    }
  }
  
  public void attach(Context paramContext)
  {
    this.mContext = paramContext;
  }
  
  public void detach()
  {
    this.mContext = null;
    this.mProgressDialog = null;
  }
  
  protected HashMap<String, String> doInBackground(Void... paramVarArgs)
  {
    if ((this.mIsFetchMessages) && (this.mToken != null)) {
      paramVarArgs = doGet();
    }
    HashMap localHashMap;
    do
    {
      return paramVarArgs;
      if (this.mIsFetchMessages) {
        break;
      }
      if (this.mAttachmentUris.isEmpty()) {
        return doPostPut();
      }
      localHashMap = doPostPutWithAttachments();
      paramVarArgs = localHashMap;
    } while (localHashMap == null);
    clearTemporaryFolder(localHashMap);
    return localHashMap;
    return null;
  }
  
  protected void onPostExecute(HashMap<String, String> paramHashMap)
  {
    if (this.mProgressDialog != null) {}
    try
    {
      this.mProgressDialog.dismiss();
      if (this.mHandler != null)
      {
        Message localMessage = new Message();
        localBundle = new Bundle();
        if (paramHashMap != null)
        {
          localBundle.putString("request_type", (String)paramHashMap.get("type"));
          localBundle.putString("feedback_response", (String)paramHashMap.get("response"));
          localBundle.putString("feedback_status", (String)paramHashMap.get("status"));
          localMessage.setData(localBundle);
          this.mHandler.sendMessage(localMessage);
        }
      }
      else
      {
        return;
      }
    }
    catch (Exception localException)
    {
      for (;;)
      {
        Bundle localBundle;
        localException.printStackTrace();
        continue;
        localBundle.putString("request_type", "unknown");
      }
    }
  }
  
  protected void onPreExecute()
  {
    String str = this.mContext.getString(R.string.hockeyapp_feedback_sending_feedback_text);
    if (this.mIsFetchMessages) {
      str = this.mContext.getString(R.string.hockeyapp_feedback_fetching_feedback_text);
    }
    if (((this.mProgressDialog == null) || (!this.mProgressDialog.isShowing())) && (this.mShowProgressDialog)) {
      this.mProgressDialog = ProgressDialog.show(this.mContext, "", str, true, false);
    }
  }
  
  public void setLastMessageId(int paramInt)
  {
    this.mLastMessageId = paramInt;
  }
  
  public void setShowProgressDialog(boolean paramBoolean)
  {
    this.mShowProgressDialog = paramBoolean;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/tasks/SendFeedbackTask.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
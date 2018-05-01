package net.hockeyapp.android.tasks;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import java.io.File;
import java.io.FilenameFilter;
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

@SuppressLint({"StaticFieldLeak"})
public class SendFeedbackTask
  extends ConnectionTask<Void, Void, HashMap<String, String>>
{
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
    int i = 0;
    paramHashMap = (String)paramHashMap.get("status");
    if ((paramHashMap != null) && (paramHashMap.startsWith("2")) && (this.mContext != null))
    {
      paramHashMap = new File(this.mContext.getCacheDir(), "HockeyApp");
      if (paramHashMap.exists()) {
        for (paramHashMap : paramHashMap.listFiles()) {
          if ((paramHashMap != null) && (!Boolean.valueOf(paramHashMap.delete()).booleanValue())) {
            HockeyLog.debug("SendFeedbackTask", "Error deleting file from temporary folder");
          }
        }
      }
      paramHashMap = Constants.getHockeyAppStorageDir(this.mContext).listFiles(new FilenameFilter()
      {
        public boolean accept(File paramAnonymousFile, String paramAnonymousString)
        {
          return paramAnonymousString.endsWith(".jpg");
        }
      });
      ??? = paramHashMap.length;
      ??? = i;
      if (??? < ???)
      {
        ??? = paramHashMap[???];
        if (this.mAttachmentUris.contains(Uri.fromFile(???)))
        {
          if (!???.delete()) {
            break label209;
          }
          HockeyLog.debug("SendFeedbackTask", "Screenshot '" + ???.getName() + "' has been deleted");
        }
        for (;;)
        {
          ???++;
          break;
          label209:
          HockeyLog.error("SendFeedbackTask", "Error deleting screenshot");
        }
      }
    }
  }
  
  private HashMap<String, String> doGet()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(this.mUrlString).append(Util.encodeParam(this.mToken));
    if (this.mLastMessageId != -1) {
      localStringBuilder.append("?last_message_id=").append(this.mLastMessageId);
    }
    HashMap localHashMap = new HashMap();
    Object localObject1 = null;
    HttpURLConnection localHttpURLConnection1 = null;
    localHttpURLConnection2 = localHttpURLConnection1;
    localObject3 = localObject1;
    try
    {
      HttpURLConnectionBuilder localHttpURLConnectionBuilder = new net/hockeyapp/android/utils/HttpURLConnectionBuilder;
      localHttpURLConnection2 = localHttpURLConnection1;
      localObject3 = localObject1;
      localHttpURLConnectionBuilder.<init>(localStringBuilder.toString());
      localHttpURLConnection2 = localHttpURLConnection1;
      localObject3 = localObject1;
      localHttpURLConnection1 = localHttpURLConnectionBuilder.build();
      localHttpURLConnection2 = localHttpURLConnection1;
      localObject3 = localHttpURLConnection1;
      localHashMap.put("type", "fetch");
      localHttpURLConnection2 = localHttpURLConnection1;
      localObject3 = localHttpURLConnection1;
      localHttpURLConnection1.connect();
      localHttpURLConnection2 = localHttpURLConnection1;
      localObject3 = localHttpURLConnection1;
      localHashMap.put("status", String.valueOf(localHttpURLConnection1.getResponseCode()));
      localHttpURLConnection2 = localHttpURLConnection1;
      localObject3 = localHttpURLConnection1;
      localHashMap.put("response", getStringFromConnection(localHttpURLConnection1));
      if (localHttpURLConnection1 != null) {
        localHttpURLConnection1.disconnect();
      }
    }
    catch (IOException localIOException)
    {
      for (;;)
      {
        localObject3 = localHttpURLConnection2;
        HockeyLog.error("Failed to fetching feedback messages", localIOException);
        if (localHttpURLConnection2 != null) {
          localHttpURLConnection2.disconnect();
        }
      }
    }
    finally
    {
      if (localObject3 == null) {
        break label227;
      }
      ((HttpURLConnection)localObject3).disconnect();
    }
    return localHashMap;
  }
  
  private HashMap<String, String> doPostPut()
  {
    HashMap localHashMap1 = new HashMap();
    localHashMap1.put("type", "send");
    Object localObject1 = null;
    Object localObject2 = null;
    localObject3 = localObject2;
    localObject5 = localObject1;
    for (;;)
    {
      try
      {
        HashMap localHashMap2 = new java/util/HashMap;
        localObject3 = localObject2;
        localObject5 = localObject1;
        localHashMap2.<init>();
        localObject3 = localObject2;
        localObject5 = localObject1;
        localHashMap2.put("name", this.mName);
        localObject3 = localObject2;
        localObject5 = localObject1;
        localHashMap2.put("email", this.mEmail);
        localObject3 = localObject2;
        localObject5 = localObject1;
        localHashMap2.put("subject", this.mSubject);
        localObject3 = localObject2;
        localObject5 = localObject1;
        localHashMap2.put("text", this.mText);
        localObject3 = localObject2;
        localObject5 = localObject1;
        localHashMap2.put("bundle_identifier", Constants.APP_PACKAGE);
        localObject3 = localObject2;
        localObject5 = localObject1;
        localHashMap2.put("bundle_short_version", Constants.APP_VERSION_NAME);
        localObject3 = localObject2;
        localObject5 = localObject1;
        localHashMap2.put("bundle_version", Constants.APP_VERSION);
        localObject3 = localObject2;
        localObject5 = localObject1;
        localHashMap2.put("os_version", Constants.ANDROID_VERSION);
        localObject3 = localObject2;
        localObject5 = localObject1;
        localHashMap2.put("oem", Constants.PHONE_MANUFACTURER);
        localObject3 = localObject2;
        localObject5 = localObject1;
        localHashMap2.put("model", Constants.PHONE_MODEL);
        localObject3 = localObject2;
        localObject5 = localObject1;
        localHashMap2.put("sdk_version", "5.0.4");
        localObject3 = localObject2;
        localObject5 = localObject1;
        if (this.mToken != null)
        {
          localObject3 = localObject2;
          localObject5 = localObject1;
          localObject6 = new java/lang/StringBuilder;
          localObject3 = localObject2;
          localObject5 = localObject1;
          ((StringBuilder)localObject6).<init>();
          localObject3 = localObject2;
          localObject5 = localObject1;
          this.mUrlString = (this.mUrlString + this.mToken + "/");
        }
        localObject3 = localObject2;
        localObject5 = localObject1;
        HttpURLConnectionBuilder localHttpURLConnectionBuilder = new net/hockeyapp/android/utils/HttpURLConnectionBuilder;
        localObject3 = localObject2;
        localObject5 = localObject1;
        localHttpURLConnectionBuilder.<init>(this.mUrlString);
        localObject3 = localObject2;
        localObject5 = localObject1;
        if (this.mToken == null) {
          continue;
        }
        localObject6 = "PUT";
        localObject3 = localObject2;
        localObject5 = localObject1;
        localObject6 = localHttpURLConnectionBuilder.setRequestMethod((String)localObject6).writeFormFields(localHashMap2).build();
        localObject3 = localObject6;
        localObject5 = localObject6;
        ((HttpURLConnection)localObject6).connect();
        localObject3 = localObject6;
        localObject5 = localObject6;
        localHashMap1.put("status", String.valueOf(((HttpURLConnection)localObject6).getResponseCode()));
        localObject3 = localObject6;
        localObject5 = localObject6;
        localHashMap1.put("response", getStringFromConnection((HttpURLConnection)localObject6));
      }
      catch (IOException localIOException)
      {
        Object localObject6;
        localObject5 = localObject3;
        HockeyLog.error("Failed to send feedback message", localIOException);
        if (localObject3 == null) {
          continue;
        }
        ((HttpURLConnection)localObject3).disconnect();
        continue;
      }
      finally
      {
        if (localObject5 == null) {
          continue;
        }
        ((HttpURLConnection)localObject5).disconnect();
      }
      return localHashMap1;
      localObject6 = "POST";
    }
  }
  
  private HashMap<String, String> doPostPutWithAttachments()
  {
    HashMap localHashMap1 = new HashMap();
    localHashMap1.put("type", "send");
    Object localObject1 = null;
    Object localObject2 = null;
    localObject3 = localObject2;
    localObject5 = localObject1;
    for (;;)
    {
      try
      {
        HashMap localHashMap2 = new java/util/HashMap;
        localObject3 = localObject2;
        localObject5 = localObject1;
        localHashMap2.<init>();
        localObject3 = localObject2;
        localObject5 = localObject1;
        localHashMap2.put("name", this.mName);
        localObject3 = localObject2;
        localObject5 = localObject1;
        localHashMap2.put("email", this.mEmail);
        localObject3 = localObject2;
        localObject5 = localObject1;
        localHashMap2.put("subject", this.mSubject);
        localObject3 = localObject2;
        localObject5 = localObject1;
        localHashMap2.put("text", this.mText);
        localObject3 = localObject2;
        localObject5 = localObject1;
        localHashMap2.put("bundle_identifier", Constants.APP_PACKAGE);
        localObject3 = localObject2;
        localObject5 = localObject1;
        localHashMap2.put("bundle_short_version", Constants.APP_VERSION_NAME);
        localObject3 = localObject2;
        localObject5 = localObject1;
        localHashMap2.put("bundle_version", Constants.APP_VERSION);
        localObject3 = localObject2;
        localObject5 = localObject1;
        localHashMap2.put("os_version", Constants.ANDROID_VERSION);
        localObject3 = localObject2;
        localObject5 = localObject1;
        localHashMap2.put("oem", Constants.PHONE_MANUFACTURER);
        localObject3 = localObject2;
        localObject5 = localObject1;
        localHashMap2.put("model", Constants.PHONE_MODEL);
        localObject3 = localObject2;
        localObject5 = localObject1;
        localHashMap2.put("sdk_version", "5.0.4");
        localObject3 = localObject2;
        localObject5 = localObject1;
        if (this.mToken != null)
        {
          localObject3 = localObject2;
          localObject5 = localObject1;
          localObject6 = new java/lang/StringBuilder;
          localObject3 = localObject2;
          localObject5 = localObject1;
          ((StringBuilder)localObject6).<init>();
          localObject3 = localObject2;
          localObject5 = localObject1;
          this.mUrlString = (this.mUrlString + this.mToken + "/");
        }
        localObject3 = localObject2;
        localObject5 = localObject1;
        HttpURLConnectionBuilder localHttpURLConnectionBuilder = new net/hockeyapp/android/utils/HttpURLConnectionBuilder;
        localObject3 = localObject2;
        localObject5 = localObject1;
        localHttpURLConnectionBuilder.<init>(this.mUrlString);
        localObject3 = localObject2;
        localObject5 = localObject1;
        if (this.mToken == null) {
          continue;
        }
        localObject6 = "PUT";
        localObject3 = localObject2;
        localObject5 = localObject1;
        localObject6 = localHttpURLConnectionBuilder.setRequestMethod((String)localObject6).writeMultipartData(localHashMap2, this.mContext, this.mAttachmentUris).build();
        localObject3 = localObject6;
        localObject5 = localObject6;
        ((HttpURLConnection)localObject6).connect();
        localObject3 = localObject6;
        localObject5 = localObject6;
        localHashMap1.put("status", String.valueOf(((HttpURLConnection)localObject6).getResponseCode()));
        localObject3 = localObject6;
        localObject5 = localObject6;
        localHashMap1.put("response", getStringFromConnection((HttpURLConnection)localObject6));
      }
      catch (IOException localIOException)
      {
        Object localObject6;
        localObject5 = localObject3;
        HockeyLog.error("Failed to send feedback message", localIOException);
        if (localObject3 == null) {
          continue;
        }
        ((HttpURLConnection)localObject3).disconnect();
        continue;
      }
      finally
      {
        if (localObject5 == null) {
          continue;
        }
        ((HttpURLConnection)localObject5).disconnect();
      }
      return localHashMap1;
      localObject6 = "POST";
    }
  }
  
  private String getLoadingMessage()
  {
    String str = this.mContext.getString(R.string.hockeyapp_feedback_sending_feedback_text);
    if (this.mIsFetchMessages) {
      str = this.mContext.getString(R.string.hockeyapp_feedback_fetching_feedback_text);
    }
    return str;
  }
  
  public void attach(Context paramContext)
  {
    this.mContext = paramContext;
    if ((getStatus() == AsyncTask.Status.RUNNING) && ((this.mProgressDialog == null) || (!this.mProgressDialog.isShowing())) && (this.mShowProgressDialog)) {
      this.mProgressDialog = ProgressDialog.show(this.mContext, "", getLoadingMessage(), true, false);
    }
  }
  
  public void detach()
  {
    this.mContext = null;
    if (this.mProgressDialog != null)
    {
      this.mProgressDialog.dismiss();
      this.mProgressDialog = null;
    }
  }
  
  protected HashMap<String, String> doInBackground(Void... paramVarArgs)
  {
    if ((this.mIsFetchMessages) && (this.mToken != null)) {
      paramVarArgs = doGet();
    }
    for (;;)
    {
      return paramVarArgs;
      if (!this.mIsFetchMessages)
      {
        if (this.mAttachmentUris.isEmpty())
        {
          paramVarArgs = doPostPut();
        }
        else
        {
          HashMap localHashMap = doPostPutWithAttachments();
          paramVarArgs = localHashMap;
          if (localHashMap != null)
          {
            clearTemporaryFolder(localHashMap);
            paramVarArgs = localHashMap;
          }
        }
      }
      else {
        paramVarArgs = null;
      }
    }
  }
  
  protected void onPostExecute(HashMap<String, String> paramHashMap)
  {
    if (this.mProgressDialog != null) {}
    try
    {
      this.mProgressDialog.dismiss();
      Message localMessage;
      Bundle localBundle;
      if (this.mHandler != null)
      {
        localMessage = new Message();
        localBundle = new Bundle();
        if (paramHashMap == null) {
          break label104;
        }
        localBundle.putString("request_type", (String)paramHashMap.get("type"));
        localBundle.putString("feedback_response", (String)paramHashMap.get("response"));
        localBundle.putString("feedback_status", (String)paramHashMap.get("status"));
      }
      for (;;)
      {
        localMessage.setData(localBundle);
        this.mHandler.sendMessage(localMessage);
        return;
        label104:
        localBundle.putString("request_type", "unknown");
      }
    }
    catch (Exception localException)
    {
      for (;;) {}
    }
  }
  
  protected void onPreExecute()
  {
    if (((this.mProgressDialog == null) || (!this.mProgressDialog.isShowing())) && (this.mShowProgressDialog)) {
      this.mProgressDialog = ProgressDialog.show(this.mContext, "", getLoadingMessage(), true, false);
    }
  }
  
  public void setHandler(Handler paramHandler)
  {
    this.mHandler = paramHandler;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/tasks/SendFeedbackTask.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
package net.hockeyapp.android.tasks;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import java.util.ArrayList;
import net.hockeyapp.android.FeedbackActivity;
import net.hockeyapp.android.FeedbackManager;
import net.hockeyapp.android.FeedbackManagerListener;
import net.hockeyapp.android.objects.Feedback;
import net.hockeyapp.android.objects.FeedbackMessage;
import net.hockeyapp.android.objects.FeedbackResponse;
import net.hockeyapp.android.utils.FeedbackParser;
import net.hockeyapp.android.utils.Util;

public class ParseFeedbackTask
  extends AsyncTask<Void, Void, FeedbackResponse>
{
  public static final String BUNDLE_PARSE_FEEDBACK_RESPONSE = "parse_feedback_response";
  public static final String ID_LAST_MESSAGE_PROCESSED = "idLastMessageProcessed";
  public static final String ID_LAST_MESSAGE_SEND = "idLastMessageSend";
  public static final int NEW_ANSWER_NOTIFICATION_ID = 2;
  public static final String PREFERENCES_NAME = "net.hockeyapp.android.feedback";
  private Context mContext;
  private String mFeedbackResponse;
  private Handler mHandler;
  private String mRequestType;
  private String mUrlString;
  
  public ParseFeedbackTask(Context paramContext, String paramString1, Handler paramHandler, String paramString2)
  {
    this.mContext = paramContext;
    this.mFeedbackResponse = paramString1;
    this.mHandler = paramHandler;
    this.mRequestType = paramString2;
    this.mUrlString = null;
  }
  
  private void checkForNewAnswers(ArrayList<FeedbackMessage> paramArrayList)
  {
    paramArrayList = (FeedbackMessage)paramArrayList.get(paramArrayList.size() - 1);
    int i = paramArrayList.getId();
    Object localObject = this.mContext.getSharedPreferences("net.hockeyapp.android.feedback", 0);
    if (this.mRequestType.equals("send")) {
      ((SharedPreferences)localObject).edit().putInt("idLastMessageSend", i).putInt("idLastMessageProcessed", i).apply();
    }
    boolean bool;
    do
    {
      int j;
      int k;
      do
      {
        do
        {
          return;
        } while (!this.mRequestType.equals("fetch"));
        j = ((SharedPreferences)localObject).getInt("idLastMessageSend", -1);
        k = ((SharedPreferences)localObject).getInt("idLastMessageProcessed", -1);
      } while ((i == j) || (i == k));
      ((SharedPreferences)localObject).edit().putInt("idLastMessageProcessed", i).apply();
      bool = false;
      localObject = FeedbackManager.getLastListener();
      if (localObject != null) {
        bool = ((FeedbackManagerListener)localObject).feedbackAnswered(paramArrayList);
      }
    } while (bool);
    startNotification(this.mContext);
  }
  
  private void startNotification(Context paramContext)
  {
    if (this.mUrlString == null) {}
    NotificationManager localNotificationManager;
    do
    {
      return;
      localNotificationManager = (NotificationManager)paramContext.getSystemService("notification");
      int i = paramContext.getResources().getIdentifier("ic_menu_refresh", "drawable", "android");
      Object localObject1 = null;
      if (FeedbackManager.getLastListener() != null) {
        localObject1 = FeedbackManager.getLastListener().getFeedbackActivityClass();
      }
      Object localObject2 = localObject1;
      if (localObject1 == null) {
        localObject2 = FeedbackActivity.class;
      }
      localObject1 = new Intent();
      ((Intent)localObject1).setFlags(805306368);
      ((Intent)localObject1).setClass(paramContext, (Class)localObject2);
      ((Intent)localObject1).putExtra("url", this.mUrlString);
      paramContext = Util.createNotification(paramContext, PendingIntent.getActivity(paramContext, 0, (Intent)localObject1, 1073741824), "HockeyApp Feedback", "A new answer to your feedback is available.", i);
    } while (paramContext == null);
    localNotificationManager.notify(2, paramContext);
  }
  
  protected FeedbackResponse doInBackground(Void... paramVarArgs)
  {
    if ((this.mContext != null) && (this.mFeedbackResponse != null))
    {
      paramVarArgs = FeedbackParser.getInstance().parseFeedbackResponse(this.mFeedbackResponse);
      if ((paramVarArgs != null) && (paramVarArgs.getFeedback() != null))
      {
        ArrayList localArrayList = paramVarArgs.getFeedback().getMessages();
        if ((localArrayList != null) && (!localArrayList.isEmpty())) {
          checkForNewAnswers(localArrayList);
        }
      }
      return paramVarArgs;
    }
    return null;
  }
  
  protected void onPostExecute(FeedbackResponse paramFeedbackResponse)
  {
    if ((paramFeedbackResponse != null) && (this.mHandler != null))
    {
      Message localMessage = new Message();
      Bundle localBundle = new Bundle();
      localBundle.putSerializable("parse_feedback_response", paramFeedbackResponse);
      localMessage.setData(localBundle);
      this.mHandler.sendMessage(localMessage);
    }
  }
  
  public void setUrlString(String paramString)
  {
    this.mUrlString = paramString;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/tasks/ParseFeedbackTask.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
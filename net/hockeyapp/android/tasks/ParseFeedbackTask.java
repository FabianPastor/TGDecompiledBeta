package net.hockeyapp.android.tasks;

import android.annotation.SuppressLint;
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
import net.hockeyapp.android.R.string;
import net.hockeyapp.android.objects.Feedback;
import net.hockeyapp.android.objects.FeedbackMessage;
import net.hockeyapp.android.objects.FeedbackResponse;
import net.hockeyapp.android.utils.FeedbackParser;
import net.hockeyapp.android.utils.Util;

@SuppressLint({"StaticFieldLeak"})
public class ParseFeedbackTask
  extends AsyncTask<Void, Void, FeedbackResponse>
{
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
    for (;;)
    {
      return;
      if (this.mRequestType.equals("fetch"))
      {
        int j = ((SharedPreferences)localObject).getInt("idLastMessageSend", -1);
        int k = ((SharedPreferences)localObject).getInt("idLastMessageProcessed", -1);
        if ((i != j) && (i != k))
        {
          ((SharedPreferences)localObject).edit().putInt("idLastMessageProcessed", i).apply();
          boolean bool = false;
          localObject = FeedbackManager.getLastListener();
          if (localObject != null) {
            bool = ((FeedbackManagerListener)localObject).feedbackAnswered(paramArrayList);
          }
          if ((!bool) && (this.mUrlString != null)) {
            showNewAnswerNotification(this.mContext, this.mUrlString);
          }
        }
      }
    }
  }
  
  private static void showNewAnswerNotification(Context paramContext, String paramString)
  {
    Object localObject1 = null;
    if (FeedbackManager.getLastListener() != null) {
      localObject1 = FeedbackManager.getLastListener().getFeedbackActivityClass();
    }
    Object localObject2 = localObject1;
    if (localObject1 == null) {
      localObject2 = FeedbackActivity.class;
    }
    int i = paramContext.getResources().getIdentifier("ic_menu_refresh", "drawable", "android");
    localObject1 = new Intent();
    ((Intent)localObject1).setFlags(805306368);
    ((Intent)localObject1).setClass(paramContext, (Class)localObject2);
    ((Intent)localObject1).putExtra("url", paramString);
    Util.sendNotification(paramContext, 2, Util.createNotification(paramContext, PendingIntent.getActivity(paramContext, 0, (Intent)localObject1, NUM), paramContext.getString(R.string.hockeyapp_feedback_notification_title), paramContext.getString(R.string.hockeyapp_feedback_new_answer_notification_message), i, "net.hockeyapp.android.NOTIFICATION"), "net.hockeyapp.android.NOTIFICATION", paramContext.getString(R.string.hockeyapp_feedback_notification_channel));
  }
  
  protected FeedbackResponse doInBackground(Void... paramVarArgs)
  {
    FeedbackResponse localFeedbackResponse;
    if ((this.mContext != null) && (this.mFeedbackResponse != null))
    {
      localFeedbackResponse = FeedbackParser.getInstance().parseFeedbackResponse(this.mFeedbackResponse);
      paramVarArgs = localFeedbackResponse;
      if (localFeedbackResponse != null)
      {
        paramVarArgs = localFeedbackResponse;
        if (localFeedbackResponse.getFeedback() != null)
        {
          ArrayList localArrayList = localFeedbackResponse.getFeedback().getMessages();
          paramVarArgs = localFeedbackResponse;
          if (localArrayList != null)
          {
            paramVarArgs = localFeedbackResponse;
            if (!localArrayList.isEmpty()) {
              checkForNewAnswers(localArrayList);
            }
          }
        }
      }
    }
    for (paramVarArgs = localFeedbackResponse;; paramVarArgs = null) {
      return paramVarArgs;
    }
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
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/tasks/ParseFeedbackTask.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
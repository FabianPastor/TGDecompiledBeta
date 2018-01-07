package net.hockeyapp.android.tasks;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import java.util.ArrayList;
import net.hockeyapp.android.FeedbackActivity;
import net.hockeyapp.android.FeedbackManager;
import net.hockeyapp.android.FeedbackManagerListener;
import net.hockeyapp.android.R;
import net.hockeyapp.android.UpdateFragment;
import net.hockeyapp.android.objects.FeedbackMessage;
import net.hockeyapp.android.objects.FeedbackResponse;
import net.hockeyapp.android.utils.FeedbackParser;
import net.hockeyapp.android.utils.Util;

@SuppressLint({"StaticFieldLeak"})
public class ParseFeedbackTask extends AsyncTask<Void, Void, FeedbackResponse> {
    private Context mContext;
    private String mFeedbackResponse;
    private Handler mHandler;
    private String mRequestType;
    private String mUrlString = null;

    public ParseFeedbackTask(Context context, String feedbackResponse, Handler handler, String requestType) {
        this.mContext = context;
        this.mFeedbackResponse = feedbackResponse;
        this.mHandler = handler;
        this.mRequestType = requestType;
    }

    protected FeedbackResponse doInBackground(Void... params) {
        if (this.mContext == null || this.mFeedbackResponse == null) {
            return null;
        }
        FeedbackResponse response = FeedbackParser.getInstance().parseFeedbackResponse(this.mFeedbackResponse);
        if (response == null || response.getFeedback() == null) {
            return response;
        }
        ArrayList<FeedbackMessage> messages = response.getFeedback().getMessages();
        if (messages == null || messages.isEmpty()) {
            return response;
        }
        checkForNewAnswers(messages);
        return response;
    }

    protected void onPostExecute(FeedbackResponse result) {
        if (result != null && this.mHandler != null) {
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putSerializable("parse_feedback_response", result);
            msg.setData(bundle);
            this.mHandler.sendMessage(msg);
        }
    }

    private void checkForNewAnswers(ArrayList<FeedbackMessage> messages) {
        FeedbackMessage latestMessage = (FeedbackMessage) messages.get(messages.size() - 1);
        int idLatestMessage = latestMessage.getId();
        SharedPreferences preferences = this.mContext.getSharedPreferences("net.hockeyapp.android.feedback", 0);
        if (this.mRequestType.equals("send")) {
            preferences.edit().putInt("idLastMessageSend", idLatestMessage).putInt("idLastMessageProcessed", idLatestMessage).apply();
        } else if (this.mRequestType.equals("fetch")) {
            int idLastMessageSend = preferences.getInt("idLastMessageSend", -1);
            int idLastMessageProcessed = preferences.getInt("idLastMessageProcessed", -1);
            if (idLatestMessage != idLastMessageSend && idLatestMessage != idLastMessageProcessed) {
                preferences.edit().putInt("idLastMessageProcessed", idLatestMessage).apply();
                boolean eventHandled = false;
                FeedbackManagerListener listener = FeedbackManager.getLastListener();
                if (listener != null) {
                    eventHandled = listener.feedbackAnswered(latestMessage);
                }
                if (!eventHandled && this.mUrlString != null) {
                    showNewAnswerNotification(this.mContext, this.mUrlString);
                }
            }
        }
    }

    private static void showNewAnswerNotification(Context context, String urlString) {
        Class<?> activityClass = null;
        if (FeedbackManager.getLastListener() != null) {
            activityClass = FeedbackManager.getLastListener().getFeedbackActivityClass();
        }
        if (activityClass == null) {
            activityClass = FeedbackActivity.class;
        }
        int iconId = context.getResources().getIdentifier("ic_menu_refresh", "drawable", "android");
        Intent intent = new Intent();
        intent.setFlags(805306368);
        intent.setClass(context, activityClass);
        intent.putExtra(UpdateFragment.FRAGMENT_URL, urlString);
        Util.sendNotification(context, 2, Util.createNotification(context, PendingIntent.getActivity(context, 0, intent, NUM), context.getString(R.string.hockeyapp_feedback_notification_title), context.getString(R.string.hockeyapp_feedback_new_answer_notification_message), iconId, "net.hockeyapp.android.NOTIFICATION"), "net.hockeyapp.android.NOTIFICATION", context.getString(R.string.hockeyapp_feedback_notification_channel));
    }
}

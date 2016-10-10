package net.hockeyapp.android;

import net.hockeyapp.android.objects.FeedbackMessage;

public abstract class FeedbackManagerListener
{
  public abstract boolean feedbackAnswered(FeedbackMessage paramFeedbackMessage);
  
  public Class<? extends FeedbackActivity> getFeedbackActivityClass()
  {
    return FeedbackActivity.class;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/FeedbackManagerListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
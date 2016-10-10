package net.hockeyapp.android.listeners;

import net.hockeyapp.android.tasks.SendFeedbackTask;

public abstract class SendFeedbackListener
{
  public void feedbackFailed(SendFeedbackTask paramSendFeedbackTask, Boolean paramBoolean) {}
  
  public void feedbackSuccessful(SendFeedbackTask paramSendFeedbackTask) {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/listeners/SendFeedbackListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
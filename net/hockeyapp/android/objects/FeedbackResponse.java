package net.hockeyapp.android.objects;

import java.io.Serializable;

public class FeedbackResponse
  implements Serializable
{
  private Feedback mFeedback;
  private String mStatus;
  private String mToken;
  
  public Feedback getFeedback()
  {
    return this.mFeedback;
  }
  
  public String getStatus()
  {
    return this.mStatus;
  }
  
  public String getToken()
  {
    return this.mToken;
  }
  
  public void setFeedback(Feedback paramFeedback)
  {
    this.mFeedback = paramFeedback;
  }
  
  public void setStatus(String paramString)
  {
    this.mStatus = paramString;
  }
  
  public void setToken(String paramString)
  {
    this.mToken = paramString;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/objects/FeedbackResponse.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
package net.hockeyapp.android.objects;

import java.io.Serializable;
import java.util.ArrayList;

public class Feedback
  implements Serializable
{
  private String mCreatedAt;
  private String mEmail;
  private int mId;
  private ArrayList<FeedbackMessage> mMessages;
  private String mName;
  
  public ArrayList<FeedbackMessage> getMessages()
  {
    return this.mMessages;
  }
  
  public void setCreatedAt(String paramString)
  {
    this.mCreatedAt = paramString;
  }
  
  public void setEmail(String paramString)
  {
    this.mEmail = paramString;
  }
  
  public void setId(int paramInt)
  {
    this.mId = paramInt;
  }
  
  public void setMessages(ArrayList<FeedbackMessage> paramArrayList)
  {
    this.mMessages = paramArrayList;
  }
  
  public void setName(String paramString)
  {
    this.mName = paramString;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/objects/Feedback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
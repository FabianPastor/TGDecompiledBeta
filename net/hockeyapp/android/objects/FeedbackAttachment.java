package net.hockeyapp.android.objects;

import java.io.Serializable;

public class FeedbackAttachment
  implements Serializable
{
  private String mCreatedAt;
  private String mFilename;
  private int mId;
  private int mMessageId;
  private String mUpdatedAt;
  private String mUrl;
  
  public String getCacheId()
  {
    return "" + this.mMessageId + this.mId;
  }
  
  public String getFilename()
  {
    return this.mFilename;
  }
  
  public String getUrl()
  {
    return this.mUrl;
  }
  
  public void setCreatedAt(String paramString)
  {
    this.mCreatedAt = paramString;
  }
  
  public void setFilename(String paramString)
  {
    this.mFilename = paramString;
  }
  
  public void setId(int paramInt)
  {
    this.mId = paramInt;
  }
  
  public void setMessageId(int paramInt)
  {
    this.mMessageId = paramInt;
  }
  
  public void setUpdatedAt(String paramString)
  {
    this.mUpdatedAt = paramString;
  }
  
  public void setUrl(String paramString)
  {
    this.mUrl = paramString;
  }
  
  public String toString()
  {
    return "\n" + FeedbackAttachment.class.getSimpleName() + "\nid         " + this.mId + "\nmessage id " + this.mMessageId + "\nfilename   " + this.mFilename + "\nurl        " + this.mUrl + "\ncreatedAt  " + this.mCreatedAt + "\nupdatedAt  " + this.mUpdatedAt;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/objects/FeedbackAttachment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
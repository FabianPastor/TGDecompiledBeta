package net.hockeyapp.android.objects;

import java.io.Serializable;
import java.util.List;

public class FeedbackMessage
  implements Serializable
{
  private String mAppId;
  private String mCleanText;
  private String mCreatedAt;
  private String mDeviceModel;
  private String mDeviceOem;
  private String mDeviceOsVersion;
  private List<FeedbackAttachment> mFeedbackAttachments;
  private int mId;
  private String mName;
  private String mSubject;
  private String mText;
  private String mToken;
  private String mUserString;
  private int mVia;
  
  public String getCreatedAt()
  {
    return this.mCreatedAt;
  }
  
  public List<FeedbackAttachment> getFeedbackAttachments()
  {
    return this.mFeedbackAttachments;
  }
  
  public int getId()
  {
    return this.mId;
  }
  
  public String getName()
  {
    return this.mName;
  }
  
  public String getText()
  {
    return this.mText;
  }
  
  public void setAppId(String paramString)
  {
    this.mAppId = paramString;
  }
  
  public void setCleanText(String paramString)
  {
    this.mCleanText = paramString;
  }
  
  public void setCreatedAt(String paramString)
  {
    this.mCreatedAt = paramString;
  }
  
  public void setFeedbackAttachments(List<FeedbackAttachment> paramList)
  {
    this.mFeedbackAttachments = paramList;
  }
  
  public void setId(int paramInt)
  {
    this.mId = paramInt;
  }
  
  public void setModel(String paramString)
  {
    this.mDeviceModel = paramString;
  }
  
  public void setName(String paramString)
  {
    this.mName = paramString;
  }
  
  public void setOem(String paramString)
  {
    this.mDeviceOem = paramString;
  }
  
  public void setOsVersion(String paramString)
  {
    this.mDeviceOsVersion = paramString;
  }
  
  public void setSubject(String paramString)
  {
    this.mSubject = paramString;
  }
  
  public void setText(String paramString)
  {
    this.mText = paramString;
  }
  
  public void setToken(String paramString)
  {
    this.mToken = paramString;
  }
  
  public void setUserString(String paramString)
  {
    this.mUserString = paramString;
  }
  
  public void setVia(int paramInt)
  {
    this.mVia = paramInt;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/objects/FeedbackMessage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
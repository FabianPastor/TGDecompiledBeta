package net.hockeyapp.android.objects;

import java.io.File;
import java.io.FilenameFilter;
import java.io.Serializable;
import net.hockeyapp.android.Constants;

public class FeedbackAttachment
  implements Serializable
{
  private static final long serialVersionUID = 5059651319640956830L;
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
  
  public String getCreatedAt()
  {
    return this.mCreatedAt;
  }
  
  public String getFilename()
  {
    return this.mFilename;
  }
  
  public int getId()
  {
    return this.mId;
  }
  
  public int getMessageId()
  {
    return this.mMessageId;
  }
  
  public String getUpdatedAt()
  {
    return this.mUpdatedAt;
  }
  
  public String getUrl()
  {
    return this.mUrl;
  }
  
  public boolean isAvailableInCache()
  {
    Object localObject = Constants.getHockeyAppStorageDir();
    if ((((File)localObject).exists()) && (((File)localObject).isDirectory()))
    {
      localObject = ((File)localObject).listFiles(new FilenameFilter()
      {
        public boolean accept(File paramAnonymousFile, String paramAnonymousString)
        {
          return paramAnonymousString.equals(FeedbackAttachment.this.getCacheId());
        }
      });
      return (localObject != null) && (localObject.length == 1);
    }
    return false;
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
    return "\n" + FeedbackAttachment.class.getSimpleName() + "\n" + "id         " + this.mId + "\n" + "message id " + this.mMessageId + "\n" + "filename   " + this.mFilename + "\n" + "url        " + this.mUrl + "\n" + "createdAt  " + this.mCreatedAt + "\n" + "updatedAt  " + this.mUpdatedAt;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/objects/FeedbackAttachment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
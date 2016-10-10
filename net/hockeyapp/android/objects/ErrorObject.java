package net.hockeyapp.android.objects;

import java.io.Serializable;

public class ErrorObject
  implements Serializable
{
  private static final long serialVersionUID = 1508110658372169868L;
  private int mCode;
  private String mMessage;
  
  public int getCode()
  {
    return this.mCode;
  }
  
  public String getMessage()
  {
    return this.mMessage;
  }
  
  public void setCode(int paramInt)
  {
    this.mCode = paramInt;
  }
  
  public void setMessage(String paramString)
  {
    this.mMessage = paramString;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/objects/ErrorObject.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
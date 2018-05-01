package net.hockeyapp.android.objects;

import java.io.Serializable;

public class ErrorObject
  implements Serializable
{
  private String mMessage;
  
  public String getMessage()
  {
    return this.mMessage;
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
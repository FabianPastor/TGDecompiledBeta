package org.telegram.messenger.volley;

import android.content.Intent;

public class AuthFailureError
  extends VolleyError
{
  private Intent mResolutionIntent;
  
  public AuthFailureError() {}
  
  public AuthFailureError(Intent paramIntent)
  {
    this.mResolutionIntent = paramIntent;
  }
  
  public AuthFailureError(String paramString)
  {
    super(paramString);
  }
  
  public AuthFailureError(String paramString, Exception paramException)
  {
    super(paramString, paramException);
  }
  
  public AuthFailureError(NetworkResponse paramNetworkResponse)
  {
    super(paramNetworkResponse);
  }
  
  public String getMessage()
  {
    if (this.mResolutionIntent != null) {
      return "User needs to (re)enter credentials.";
    }
    return super.getMessage();
  }
  
  public Intent getResolutionIntent()
  {
    return this.mResolutionIntent;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/volley/AuthFailureError.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
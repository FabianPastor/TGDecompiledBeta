package org.telegram.messenger.volley;

public class NetworkError
  extends VolleyError
{
  public NetworkError() {}
  
  public NetworkError(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
  
  public NetworkError(NetworkResponse paramNetworkResponse)
  {
    super(paramNetworkResponse);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/volley/NetworkError.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
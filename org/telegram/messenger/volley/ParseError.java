package org.telegram.messenger.volley;

public class ParseError
  extends VolleyError
{
  public ParseError() {}
  
  public ParseError(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
  
  public ParseError(NetworkResponse paramNetworkResponse)
  {
    super(paramNetworkResponse);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/volley/ParseError.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
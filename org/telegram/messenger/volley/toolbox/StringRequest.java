package org.telegram.messenger.volley.toolbox;

import java.io.UnsupportedEncodingException;
import org.telegram.messenger.volley.NetworkResponse;
import org.telegram.messenger.volley.Request;
import org.telegram.messenger.volley.Response;
import org.telegram.messenger.volley.Response.ErrorListener;
import org.telegram.messenger.volley.Response.Listener;

public class StringRequest
  extends Request<String>
{
  private final Response.Listener<String> mListener;
  
  public StringRequest(int paramInt, String paramString, Response.Listener<String> paramListener, Response.ErrorListener paramErrorListener)
  {
    super(paramInt, paramString, paramErrorListener);
    this.mListener = paramListener;
  }
  
  public StringRequest(String paramString, Response.Listener<String> paramListener, Response.ErrorListener paramErrorListener)
  {
    this(0, paramString, paramListener, paramErrorListener);
  }
  
  protected void deliverResponse(String paramString)
  {
    this.mListener.onResponse(paramString);
  }
  
  protected Response<String> parseNetworkResponse(NetworkResponse paramNetworkResponse)
  {
    try
    {
      String str1 = new String(paramNetworkResponse.data, HttpHeaderParser.parseCharset(paramNetworkResponse.headers));
      return Response.success(str1, HttpHeaderParser.parseCacheHeaders(paramNetworkResponse));
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      for (;;)
      {
        String str2 = new String(paramNetworkResponse.data);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/volley/toolbox/StringRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
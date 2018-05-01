package org.telegram.messenger.volley.toolbox;

import java.io.UnsupportedEncodingException;
import org.telegram.messenger.volley.NetworkResponse;
import org.telegram.messenger.volley.Request;
import org.telegram.messenger.volley.Response;
import org.telegram.messenger.volley.Response.ErrorListener;
import org.telegram.messenger.volley.Response.Listener;
import org.telegram.messenger.volley.VolleyLog;

public abstract class JsonRequest<T>
  extends Request<T>
{
  protected static final String PROTOCOL_CHARSET = "utf-8";
  private static final String PROTOCOL_CONTENT_TYPE = String.format("application/json; charset=%s", new Object[] { "utf-8" });
  private final Response.Listener<T> mListener;
  private final String mRequestBody;
  
  public JsonRequest(int paramInt, String paramString1, String paramString2, Response.Listener<T> paramListener, Response.ErrorListener paramErrorListener)
  {
    super(paramInt, paramString1, paramErrorListener);
    this.mListener = paramListener;
    this.mRequestBody = paramString2;
  }
  
  public JsonRequest(String paramString1, String paramString2, Response.Listener<T> paramListener, Response.ErrorListener paramErrorListener)
  {
    this(-1, paramString1, paramString2, paramListener, paramErrorListener);
  }
  
  protected void deliverResponse(T paramT)
  {
    this.mListener.onResponse(paramT);
  }
  
  public byte[] getBody()
  {
    try
    {
      if (this.mRequestBody == null) {
        return null;
      }
      byte[] arrayOfByte = this.mRequestBody.getBytes("utf-8");
      return arrayOfByte;
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", new Object[] { this.mRequestBody, "utf-8" });
    }
    return null;
  }
  
  public String getBodyContentType()
  {
    return PROTOCOL_CONTENT_TYPE;
  }
  
  public byte[] getPostBody()
  {
    return getBody();
  }
  
  public String getPostBodyContentType()
  {
    return getBodyContentType();
  }
  
  protected abstract Response<T> parseNetworkResponse(NetworkResponse paramNetworkResponse);
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/volley/toolbox/JsonRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
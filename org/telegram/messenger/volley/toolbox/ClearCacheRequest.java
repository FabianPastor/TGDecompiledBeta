package org.telegram.messenger.volley.toolbox;

import android.os.Handler;
import android.os.Looper;
import org.telegram.messenger.volley.Cache;
import org.telegram.messenger.volley.NetworkResponse;
import org.telegram.messenger.volley.Request;
import org.telegram.messenger.volley.Request.Priority;
import org.telegram.messenger.volley.Response;

public class ClearCacheRequest
  extends Request<Object>
{
  private final Cache mCache;
  private final Runnable mCallback;
  
  public ClearCacheRequest(Cache paramCache, Runnable paramRunnable)
  {
    super(0, null, null);
    this.mCache = paramCache;
    this.mCallback = paramRunnable;
  }
  
  protected void deliverResponse(Object paramObject) {}
  
  public Request.Priority getPriority()
  {
    return Request.Priority.IMMEDIATE;
  }
  
  public boolean isCanceled()
  {
    this.mCache.clear();
    if (this.mCallback != null) {
      new Handler(Looper.getMainLooper()).postAtFrontOfQueue(this.mCallback);
    }
    return true;
  }
  
  protected Response<Object> parseNetworkResponse(NetworkResponse paramNetworkResponse)
  {
    return null;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/volley/toolbox/ClearCacheRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
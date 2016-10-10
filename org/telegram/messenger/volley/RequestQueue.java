package org.telegram.messenger.volley;

import android.os.Handler;
import android.os.Looper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class RequestQueue
{
  private static final int DEFAULT_NETWORK_THREAD_POOL_SIZE = 4;
  private final Cache mCache;
  private CacheDispatcher mCacheDispatcher;
  private final PriorityBlockingQueue<Request<?>> mCacheQueue = new PriorityBlockingQueue();
  private final Set<Request<?>> mCurrentRequests = new HashSet();
  private final ResponseDelivery mDelivery;
  private NetworkDispatcher[] mDispatchers;
  private List<RequestFinishedListener> mFinishedListeners = new ArrayList();
  private final Network mNetwork;
  private final PriorityBlockingQueue<Request<?>> mNetworkQueue = new PriorityBlockingQueue();
  private AtomicInteger mSequenceGenerator = new AtomicInteger();
  private final Map<String, Queue<Request<?>>> mWaitingRequests = new HashMap();
  
  public RequestQueue(Cache paramCache, Network paramNetwork)
  {
    this(paramCache, paramNetwork, 4);
  }
  
  public RequestQueue(Cache paramCache, Network paramNetwork, int paramInt)
  {
    this(paramCache, paramNetwork, paramInt, new ExecutorDelivery(new Handler(Looper.getMainLooper())));
  }
  
  public RequestQueue(Cache paramCache, Network paramNetwork, int paramInt, ResponseDelivery paramResponseDelivery)
  {
    this.mCache = paramCache;
    this.mNetwork = paramNetwork;
    this.mDispatchers = new NetworkDispatcher[paramInt];
    this.mDelivery = paramResponseDelivery;
  }
  
  public <T> Request<T> add(Request<T> paramRequest)
  {
    paramRequest.setRequestQueue(this);
    synchronized (this.mCurrentRequests)
    {
      this.mCurrentRequests.add(paramRequest);
      paramRequest.setSequence(getSequenceNumber());
      paramRequest.addMarker("add-to-queue");
      if (!paramRequest.shouldCache())
      {
        this.mNetworkQueue.add(paramRequest);
        return paramRequest;
      }
    }
    for (;;)
    {
      String str;
      synchronized (this.mWaitingRequests)
      {
        str = paramRequest.getCacheKey();
        if (this.mWaitingRequests.containsKey(str))
        {
          Queue localQueue = (Queue)this.mWaitingRequests.get(str);
          ??? = localQueue;
          if (localQueue == null) {
            ??? = new LinkedList();
          }
          ((Queue)???).add(paramRequest);
          this.mWaitingRequests.put(str, ???);
          if (VolleyLog.DEBUG) {
            VolleyLog.v("Request for cacheKey=%s is in flight, putting on hold.", new Object[] { str });
          }
          return paramRequest;
        }
      }
      this.mWaitingRequests.put(str, null);
      this.mCacheQueue.add(paramRequest);
    }
  }
  
  public <T> void addRequestFinishedListener(RequestFinishedListener<T> paramRequestFinishedListener)
  {
    synchronized (this.mFinishedListeners)
    {
      this.mFinishedListeners.add(paramRequestFinishedListener);
      return;
    }
  }
  
  public void cancelAll(final Object paramObject)
  {
    if (paramObject == null) {
      throw new IllegalArgumentException("Cannot cancelAll with a null tag");
    }
    cancelAll(new RequestFilter()
    {
      public boolean apply(Request<?> paramAnonymousRequest)
      {
        return paramAnonymousRequest.getTag() == paramObject;
      }
    });
  }
  
  public void cancelAll(RequestFilter paramRequestFilter)
  {
    synchronized (this.mCurrentRequests)
    {
      Iterator localIterator = this.mCurrentRequests.iterator();
      while (localIterator.hasNext())
      {
        Request localRequest = (Request)localIterator.next();
        if (paramRequestFilter.apply(localRequest)) {
          localRequest.cancel();
        }
      }
    }
  }
  
  <T> void finish(Request<T> paramRequest)
  {
    Object localObject2;
    synchronized (this.mCurrentRequests)
    {
      this.mCurrentRequests.remove(paramRequest);
      synchronized (this.mFinishedListeners)
      {
        localObject2 = this.mFinishedListeners.iterator();
        if (((Iterator)localObject2).hasNext()) {
          ((RequestFinishedListener)((Iterator)localObject2).next()).onRequestFinished(paramRequest);
        }
      }
    }
    if (paramRequest.shouldCache()) {
      synchronized (this.mWaitingRequests)
      {
        paramRequest = paramRequest.getCacheKey();
        localObject2 = (Queue)this.mWaitingRequests.remove(paramRequest);
        if (localObject2 != null)
        {
          if (VolleyLog.DEBUG) {
            VolleyLog.v("Releasing %d waiting requests for cacheKey=%s.", new Object[] { Integer.valueOf(((Queue)localObject2).size()), paramRequest });
          }
          this.mCacheQueue.addAll((Collection)localObject2);
        }
        return;
      }
    }
  }
  
  public Cache getCache()
  {
    return this.mCache;
  }
  
  public int getSequenceNumber()
  {
    return this.mSequenceGenerator.incrementAndGet();
  }
  
  public <T> void removeRequestFinishedListener(RequestFinishedListener<T> paramRequestFinishedListener)
  {
    synchronized (this.mFinishedListeners)
    {
      this.mFinishedListeners.remove(paramRequestFinishedListener);
      return;
    }
  }
  
  public void start()
  {
    stop();
    this.mCacheDispatcher = new CacheDispatcher(this.mCacheQueue, this.mNetworkQueue, this.mCache, this.mDelivery);
    this.mCacheDispatcher.start();
    int i = 0;
    while (i < this.mDispatchers.length)
    {
      NetworkDispatcher localNetworkDispatcher = new NetworkDispatcher(this.mNetworkQueue, this.mNetwork, this.mCache, this.mDelivery);
      this.mDispatchers[i] = localNetworkDispatcher;
      localNetworkDispatcher.start();
      i += 1;
    }
  }
  
  public void stop()
  {
    if (this.mCacheDispatcher != null) {
      this.mCacheDispatcher.quit();
    }
    int i = 0;
    while (i < this.mDispatchers.length)
    {
      if (this.mDispatchers[i] != null) {
        this.mDispatchers[i].quit();
      }
      i += 1;
    }
  }
  
  public static abstract interface RequestFilter
  {
    public abstract boolean apply(Request<?> paramRequest);
  }
  
  public static abstract interface RequestFinishedListener<T>
  {
    public abstract void onRequestFinished(Request<T> paramRequest);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/volley/RequestQueue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
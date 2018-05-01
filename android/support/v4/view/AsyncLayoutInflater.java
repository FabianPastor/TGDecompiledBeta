package android.support.v4.view;

import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.util.Pools.SynchronizedPool;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.concurrent.ArrayBlockingQueue;

public final class AsyncLayoutInflater
{
  private static final String TAG = "AsyncLayoutInflater";
  Handler mHandler;
  private Handler.Callback mHandlerCallback = new Handler.Callback()
  {
    public boolean handleMessage(Message paramAnonymousMessage)
    {
      paramAnonymousMessage = (AsyncLayoutInflater.InflateRequest)paramAnonymousMessage.obj;
      if (paramAnonymousMessage.view == null) {
        paramAnonymousMessage.view = AsyncLayoutInflater.this.mInflater.inflate(paramAnonymousMessage.resid, paramAnonymousMessage.parent, false);
      }
      paramAnonymousMessage.callback.onInflateFinished(paramAnonymousMessage.view, paramAnonymousMessage.resid, paramAnonymousMessage.parent);
      AsyncLayoutInflater.this.mInflateThread.releaseRequest(paramAnonymousMessage);
      return true;
    }
  };
  InflateThread mInflateThread;
  LayoutInflater mInflater;
  
  public AsyncLayoutInflater(@NonNull Context paramContext)
  {
    this.mInflater = new BasicInflater(paramContext);
    this.mHandler = new Handler(this.mHandlerCallback);
    this.mInflateThread = InflateThread.getInstance();
  }
  
  @UiThread
  public void inflate(@LayoutRes int paramInt, @Nullable ViewGroup paramViewGroup, @NonNull OnInflateFinishedListener paramOnInflateFinishedListener)
  {
    if (paramOnInflateFinishedListener == null) {
      throw new NullPointerException("callback argument may not be null!");
    }
    InflateRequest localInflateRequest = this.mInflateThread.obtainRequest();
    localInflateRequest.inflater = this;
    localInflateRequest.resid = paramInt;
    localInflateRequest.parent = paramViewGroup;
    localInflateRequest.callback = paramOnInflateFinishedListener;
    this.mInflateThread.enqueue(localInflateRequest);
  }
  
  private static class BasicInflater
    extends LayoutInflater
  {
    private static final String[] sClassPrefixList = { "android.widget.", "android.webkit.", "android.app." };
    
    BasicInflater(Context paramContext)
    {
      super();
    }
    
    public LayoutInflater cloneInContext(Context paramContext)
    {
      return new BasicInflater(paramContext);
    }
    
    protected View onCreateView(String paramString, AttributeSet paramAttributeSet)
      throws ClassNotFoundException
    {
      String[] arrayOfString = sClassPrefixList;
      int j = arrayOfString.length;
      int i = 0;
      while (i < j)
      {
        Object localObject = arrayOfString[i];
        try
        {
          localObject = createView(paramString, (String)localObject, paramAttributeSet);
          if (localObject != null) {
            return (View)localObject;
          }
        }
        catch (ClassNotFoundException localClassNotFoundException)
        {
          i += 1;
        }
      }
      return super.onCreateView(paramString, paramAttributeSet);
    }
  }
  
  private static class InflateRequest
  {
    AsyncLayoutInflater.OnInflateFinishedListener callback;
    AsyncLayoutInflater inflater;
    ViewGroup parent;
    int resid;
    View view;
  }
  
  private static class InflateThread
    extends Thread
  {
    private static final InflateThread sInstance = new InflateThread();
    private ArrayBlockingQueue<AsyncLayoutInflater.InflateRequest> mQueue = new ArrayBlockingQueue(10);
    private Pools.SynchronizedPool<AsyncLayoutInflater.InflateRequest> mRequestPool = new Pools.SynchronizedPool(10);
    
    static
    {
      sInstance.start();
    }
    
    public static InflateThread getInstance()
    {
      return sInstance;
    }
    
    public void enqueue(AsyncLayoutInflater.InflateRequest paramInflateRequest)
    {
      try
      {
        this.mQueue.put(paramInflateRequest);
        return;
      }
      catch (InterruptedException paramInflateRequest)
      {
        throw new RuntimeException("Failed to enqueue async inflate request", paramInflateRequest);
      }
    }
    
    public AsyncLayoutInflater.InflateRequest obtainRequest()
    {
      AsyncLayoutInflater.InflateRequest localInflateRequest2 = (AsyncLayoutInflater.InflateRequest)this.mRequestPool.acquire();
      AsyncLayoutInflater.InflateRequest localInflateRequest1 = localInflateRequest2;
      if (localInflateRequest2 == null) {
        localInflateRequest1 = new AsyncLayoutInflater.InflateRequest();
      }
      return localInflateRequest1;
    }
    
    public void releaseRequest(AsyncLayoutInflater.InflateRequest paramInflateRequest)
    {
      paramInflateRequest.callback = null;
      paramInflateRequest.inflater = null;
      paramInflateRequest.parent = null;
      paramInflateRequest.resid = 0;
      paramInflateRequest.view = null;
      this.mRequestPool.release(paramInflateRequest);
    }
    
    /* Error */
    public void run()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 34	android/support/v4/view/AsyncLayoutInflater$InflateThread:mQueue	Ljava/util/concurrent/ArrayBlockingQueue;
      //   4: invokevirtual 94	java/util/concurrent/ArrayBlockingQueue:take	()Ljava/lang/Object;
      //   7: checkcast 64	android/support/v4/view/AsyncLayoutInflater$InflateRequest
      //   10: astore_1
      //   11: aload_1
      //   12: aload_1
      //   13: getfield 74	android/support/v4/view/AsyncLayoutInflater$InflateRequest:inflater	Landroid/support/v4/view/AsyncLayoutInflater;
      //   16: getfield 98	android/support/v4/view/AsyncLayoutInflater:mInflater	Landroid/view/LayoutInflater;
      //   19: aload_1
      //   20: getfield 82	android/support/v4/view/AsyncLayoutInflater$InflateRequest:resid	I
      //   23: aload_1
      //   24: getfield 78	android/support/v4/view/AsyncLayoutInflater$InflateRequest:parent	Landroid/view/ViewGroup;
      //   27: iconst_0
      //   28: invokevirtual 104	android/view/LayoutInflater:inflate	(ILandroid/view/ViewGroup;Z)Landroid/view/View;
      //   31: putfield 86	android/support/v4/view/AsyncLayoutInflater$InflateRequest:view	Landroid/view/View;
      //   34: aload_1
      //   35: getfield 74	android/support/v4/view/AsyncLayoutInflater$InflateRequest:inflater	Landroid/support/v4/view/AsyncLayoutInflater;
      //   38: getfield 108	android/support/v4/view/AsyncLayoutInflater:mHandler	Landroid/os/Handler;
      //   41: iconst_0
      //   42: aload_1
      //   43: invokestatic 114	android/os/Message:obtain	(Landroid/os/Handler;ILjava/lang/Object;)Landroid/os/Message;
      //   46: invokevirtual 117	android/os/Message:sendToTarget	()V
      //   49: goto -49 -> 0
      //   52: astore_1
      //   53: ldc 119
      //   55: aload_1
      //   56: invokestatic 125	android/util/Log:w	(Ljava/lang/String;Ljava/lang/Throwable;)I
      //   59: pop
      //   60: goto -60 -> 0
      //   63: astore_2
      //   64: ldc 119
      //   66: ldc 127
      //   68: aload_2
      //   69: invokestatic 130	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   72: pop
      //   73: goto -39 -> 34
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	76	0	this	InflateThread
      //   10	33	1	localInflateRequest	AsyncLayoutInflater.InflateRequest
      //   52	4	1	localInterruptedException	InterruptedException
      //   63	6	2	localRuntimeException	RuntimeException
      // Exception table:
      //   from	to	target	type
      //   0	11	52	java/lang/InterruptedException
      //   11	34	63	java/lang/RuntimeException
    }
  }
  
  public static abstract interface OnInflateFinishedListener
  {
    public abstract void onInflateFinished(View paramView, int paramInt, ViewGroup paramViewGroup);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/view/AsyncLayoutInflater.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
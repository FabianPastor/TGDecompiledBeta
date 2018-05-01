package org.telegram.messenger.exoplayer;

import android.os.Handler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.telegram.messenger.exoplayer.upstream.Allocator;
import org.telegram.messenger.exoplayer.upstream.NetworkLock;

public final class DefaultLoadControl
  implements LoadControl
{
  private static final int ABOVE_HIGH_WATERMARK = 0;
  private static final int BELOW_LOW_WATERMARK = 2;
  private static final int BETWEEN_WATERMARKS = 1;
  public static final float DEFAULT_HIGH_BUFFER_LOAD = 0.8F;
  public static final int DEFAULT_HIGH_WATERMARK_MS = 30000;
  public static final float DEFAULT_LOW_BUFFER_LOAD = 0.2F;
  public static final int DEFAULT_LOW_WATERMARK_MS = 15000;
  private final Allocator allocator;
  private int bufferState;
  private final Handler eventHandler;
  private final EventListener eventListener;
  private boolean fillingBuffers;
  private final float highBufferLoad;
  private final long highWatermarkUs;
  private final HashMap<Object, LoaderState> loaderStates;
  private final List<Object> loaders;
  private final float lowBufferLoad;
  private final long lowWatermarkUs;
  private long maxLoadStartPositionUs;
  private boolean streamingPrioritySet;
  private int targetBufferSize;
  
  public DefaultLoadControl(Allocator paramAllocator)
  {
    this(paramAllocator, null, null);
  }
  
  public DefaultLoadControl(Allocator paramAllocator, Handler paramHandler, EventListener paramEventListener)
  {
    this(paramAllocator, paramHandler, paramEventListener, 15000, 30000, 0.2F, 0.8F);
  }
  
  public DefaultLoadControl(Allocator paramAllocator, Handler paramHandler, EventListener paramEventListener, int paramInt1, int paramInt2, float paramFloat1, float paramFloat2)
  {
    this.allocator = paramAllocator;
    this.eventHandler = paramHandler;
    this.eventListener = paramEventListener;
    this.loaders = new ArrayList();
    this.loaderStates = new HashMap();
    this.lowWatermarkUs = (paramInt1 * 1000L);
    this.highWatermarkUs = (paramInt2 * 1000L);
    this.lowBufferLoad = paramFloat1;
    this.highBufferLoad = paramFloat2;
  }
  
  private int getBufferState(int paramInt)
  {
    float f = paramInt / this.targetBufferSize;
    if (f > this.highBufferLoad) {
      return 0;
    }
    if (f < this.lowBufferLoad) {
      return 2;
    }
    return 1;
  }
  
  private int getLoaderBufferState(long paramLong1, long paramLong2)
  {
    if (paramLong2 == -1L) {}
    do
    {
      return 0;
      paramLong1 = paramLong2 - paramLong1;
    } while (paramLong1 > this.highWatermarkUs);
    if (paramLong1 < this.lowWatermarkUs) {
      return 2;
    }
    return 1;
  }
  
  private void notifyLoadingChanged(final boolean paramBoolean)
  {
    if ((this.eventHandler != null) && (this.eventListener != null)) {
      this.eventHandler.post(new Runnable()
      {
        public void run()
        {
          DefaultLoadControl.this.eventListener.onLoadingChanged(paramBoolean);
        }
      });
    }
  }
  
  private void updateControlState()
  {
    int m = 0;
    int k = 0;
    int j = this.bufferState;
    int i = 0;
    Object localObject;
    if (i < this.loaders.size())
    {
      localObject = (LoaderState)this.loaderStates.get(this.loaders.get(i));
      boolean bool1 = m | ((LoaderState)localObject).loading;
      if (((LoaderState)localObject).nextLoadPositionUs != -1L) {}
      for (m = 1;; m = 0)
      {
        k |= m;
        j = Math.max(j, ((LoaderState)localObject).bufferState);
        i += 1;
        m = bool1;
        break;
      }
    }
    boolean bool2;
    if ((!this.loaders.isEmpty()) && ((m != 0) || (k != 0)) && ((j == 2) || ((j == 1) && (this.fillingBuffers))))
    {
      bool2 = true;
      this.fillingBuffers = bool2;
      if ((!this.fillingBuffers) || (this.streamingPrioritySet)) {
        break label289;
      }
      NetworkLock.instance.add(0);
      this.streamingPrioritySet = true;
      notifyLoadingChanged(true);
    }
    for (;;)
    {
      this.maxLoadStartPositionUs = -1L;
      if (!this.fillingBuffers) {
        return;
      }
      i = 0;
      while (i < this.loaders.size())
      {
        localObject = this.loaders.get(i);
        long l = ((LoaderState)this.loaderStates.get(localObject)).nextLoadPositionUs;
        if ((l != -1L) && ((this.maxLoadStartPositionUs == -1L) || (l < this.maxLoadStartPositionUs))) {
          this.maxLoadStartPositionUs = l;
        }
        i += 1;
      }
      bool2 = false;
      break;
      label289:
      if ((!this.fillingBuffers) && (this.streamingPrioritySet) && (m == 0))
      {
        NetworkLock.instance.remove(0);
        this.streamingPrioritySet = false;
        notifyLoadingChanged(false);
      }
    }
  }
  
  public Allocator getAllocator()
  {
    return this.allocator;
  }
  
  public void register(Object paramObject, int paramInt)
  {
    this.loaders.add(paramObject);
    this.loaderStates.put(paramObject, new LoaderState(paramInt));
    this.targetBufferSize += paramInt;
  }
  
  public void trimAllocator()
  {
    this.allocator.trim(this.targetBufferSize);
  }
  
  public void unregister(Object paramObject)
  {
    this.loaders.remove(paramObject);
    paramObject = (LoaderState)this.loaderStates.remove(paramObject);
    this.targetBufferSize -= ((LoaderState)paramObject).bufferSizeContribution;
    updateControlState();
  }
  
  public boolean update(Object paramObject, long paramLong1, long paramLong2, boolean paramBoolean)
  {
    int j = getLoaderBufferState(paramLong1, paramLong2);
    paramObject = (LoaderState)this.loaderStates.get(paramObject);
    int i;
    int k;
    int m;
    if ((((LoaderState)paramObject).bufferState != j) || (((LoaderState)paramObject).nextLoadPositionUs != paramLong2) || (((LoaderState)paramObject).loading != paramBoolean))
    {
      i = 1;
      if (i != 0)
      {
        ((LoaderState)paramObject).bufferState = j;
        ((LoaderState)paramObject).nextLoadPositionUs = paramLong2;
        ((LoaderState)paramObject).loading = paramBoolean;
      }
      k = this.allocator.getTotalBytesAllocated();
      m = getBufferState(k);
      if (this.bufferState == m) {
        break label167;
      }
    }
    label167:
    for (j = 1;; j = 0)
    {
      if (j != 0) {
        this.bufferState = m;
      }
      if ((i != 0) || (j != 0)) {
        updateControlState();
      }
      if ((k >= this.targetBufferSize) || (paramLong2 == -1L) || (paramLong2 > this.maxLoadStartPositionUs)) {
        break label173;
      }
      return true;
      i = 0;
      break;
    }
    label173:
    return false;
  }
  
  public static abstract interface EventListener
  {
    public abstract void onLoadingChanged(boolean paramBoolean);
  }
  
  private static class LoaderState
  {
    public final int bufferSizeContribution;
    public int bufferState;
    public boolean loading;
    public long nextLoadPositionUs;
    
    public LoaderState(int paramInt)
    {
      this.bufferSizeContribution = paramInt;
      this.bufferState = 0;
      this.loading = false;
      this.nextLoadPositionUs = -1L;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/DefaultLoadControl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
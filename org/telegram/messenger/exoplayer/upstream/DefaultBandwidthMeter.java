package org.telegram.messenger.exoplayer.upstream;

import android.os.Handler;
import org.telegram.messenger.exoplayer.util.Clock;
import org.telegram.messenger.exoplayer.util.SlidingPercentile;
import org.telegram.messenger.exoplayer.util.SystemClock;

public final class DefaultBandwidthMeter
  implements BandwidthMeter
{
  public static final int DEFAULT_MAX_WEIGHT = 2000;
  private long bitrateEstimate;
  private long bytesAccumulator;
  private final Clock clock;
  private final Handler eventHandler;
  private final BandwidthMeter.EventListener eventListener;
  private final SlidingPercentile slidingPercentile;
  private long startTimeMs;
  private int streamCount;
  
  public DefaultBandwidthMeter()
  {
    this(null, null);
  }
  
  public DefaultBandwidthMeter(Handler paramHandler, BandwidthMeter.EventListener paramEventListener)
  {
    this(paramHandler, paramEventListener, new SystemClock());
  }
  
  public DefaultBandwidthMeter(Handler paramHandler, BandwidthMeter.EventListener paramEventListener, int paramInt)
  {
    this(paramHandler, paramEventListener, new SystemClock(), paramInt);
  }
  
  public DefaultBandwidthMeter(Handler paramHandler, BandwidthMeter.EventListener paramEventListener, Clock paramClock)
  {
    this(paramHandler, paramEventListener, paramClock, 2000);
  }
  
  public DefaultBandwidthMeter(Handler paramHandler, BandwidthMeter.EventListener paramEventListener, Clock paramClock, int paramInt)
  {
    this.eventHandler = paramHandler;
    this.eventListener = paramEventListener;
    this.clock = paramClock;
    this.slidingPercentile = new SlidingPercentile(paramInt);
    this.bitrateEstimate = -1L;
  }
  
  private void notifyBandwidthSample(final int paramInt, final long paramLong1, long paramLong2)
  {
    if ((this.eventHandler != null) && (this.eventListener != null)) {
      this.eventHandler.post(new Runnable()
      {
        public void run()
        {
          DefaultBandwidthMeter.this.eventListener.onBandwidthSample(paramInt, paramLong1, this.val$bitrate);
        }
      });
    }
  }
  
  public long getBitrateEstimate()
  {
    try
    {
      long l = this.bitrateEstimate;
      return l;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public void onBytesTransferred(int paramInt)
  {
    try
    {
      this.bytesAccumulator += paramInt;
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  /* Error */
  public void onTransferEnd()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 80	org/telegram/messenger/exoplayer/upstream/DefaultBandwidthMeter:streamCount	I
    //   6: ifle +135 -> 141
    //   9: iconst_1
    //   10: istore_3
    //   11: iload_3
    //   12: invokestatic 86	org/telegram/messenger/exoplayer/util/Assertions:checkState	(Z)V
    //   15: aload_0
    //   16: getfield 48	org/telegram/messenger/exoplayer/upstream/DefaultBandwidthMeter:clock	Lorg/telegram/messenger/exoplayer/util/Clock;
    //   19: invokeinterface 91 1 0
    //   24: lstore 6
    //   26: lload 6
    //   28: aload_0
    //   29: getfield 93	org/telegram/messenger/exoplayer/upstream/DefaultBandwidthMeter:startTimeMs	J
    //   32: lsub
    //   33: l2i
    //   34: istore_2
    //   35: iload_2
    //   36: ifle +74 -> 110
    //   39: aload_0
    //   40: getfield 77	org/telegram/messenger/exoplayer/upstream/DefaultBandwidthMeter:bytesAccumulator	J
    //   43: ldc2_w 94
    //   46: lmul
    //   47: iload_2
    //   48: i2l
    //   49: ldiv
    //   50: l2f
    //   51: fstore_1
    //   52: aload_0
    //   53: getfield 55	org/telegram/messenger/exoplayer/upstream/DefaultBandwidthMeter:slidingPercentile	Lorg/telegram/messenger/exoplayer/util/SlidingPercentile;
    //   56: aload_0
    //   57: getfield 77	org/telegram/messenger/exoplayer/upstream/DefaultBandwidthMeter:bytesAccumulator	J
    //   60: l2d
    //   61: invokestatic 101	java/lang/Math:sqrt	(D)D
    //   64: d2i
    //   65: fload_1
    //   66: invokevirtual 105	org/telegram/messenger/exoplayer/util/SlidingPercentile:addSample	(IF)V
    //   69: aload_0
    //   70: getfield 55	org/telegram/messenger/exoplayer/upstream/DefaultBandwidthMeter:slidingPercentile	Lorg/telegram/messenger/exoplayer/util/SlidingPercentile;
    //   73: ldc 106
    //   75: invokevirtual 110	org/telegram/messenger/exoplayer/util/SlidingPercentile:getPercentile	(F)F
    //   78: fstore_1
    //   79: fload_1
    //   80: invokestatic 116	java/lang/Float:isNaN	(F)Z
    //   83: ifeq +63 -> 146
    //   86: ldc2_w 56
    //   89: lstore 4
    //   91: aload_0
    //   92: lload 4
    //   94: putfield 59	org/telegram/messenger/exoplayer/upstream/DefaultBandwidthMeter:bitrateEstimate	J
    //   97: aload_0
    //   98: iload_2
    //   99: aload_0
    //   100: getfield 77	org/telegram/messenger/exoplayer/upstream/DefaultBandwidthMeter:bytesAccumulator	J
    //   103: aload_0
    //   104: getfield 59	org/telegram/messenger/exoplayer/upstream/DefaultBandwidthMeter:bitrateEstimate	J
    //   107: invokespecial 118	org/telegram/messenger/exoplayer/upstream/DefaultBandwidthMeter:notifyBandwidthSample	(IJJ)V
    //   110: aload_0
    //   111: aload_0
    //   112: getfield 80	org/telegram/messenger/exoplayer/upstream/DefaultBandwidthMeter:streamCount	I
    //   115: iconst_1
    //   116: isub
    //   117: putfield 80	org/telegram/messenger/exoplayer/upstream/DefaultBandwidthMeter:streamCount	I
    //   120: aload_0
    //   121: getfield 80	org/telegram/messenger/exoplayer/upstream/DefaultBandwidthMeter:streamCount	I
    //   124: ifle +9 -> 133
    //   127: aload_0
    //   128: lload 6
    //   130: putfield 93	org/telegram/messenger/exoplayer/upstream/DefaultBandwidthMeter:startTimeMs	J
    //   133: aload_0
    //   134: lconst_0
    //   135: putfield 77	org/telegram/messenger/exoplayer/upstream/DefaultBandwidthMeter:bytesAccumulator	J
    //   138: aload_0
    //   139: monitorexit
    //   140: return
    //   141: iconst_0
    //   142: istore_3
    //   143: goto -132 -> 11
    //   146: fload_1
    //   147: f2l
    //   148: lstore 4
    //   150: goto -59 -> 91
    //   153: astore 8
    //   155: aload_0
    //   156: monitorexit
    //   157: aload 8
    //   159: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	160	0	this	DefaultBandwidthMeter
    //   51	96	1	f	float
    //   34	65	2	i	int
    //   10	133	3	bool	boolean
    //   89	60	4	l1	long
    //   24	105	6	l2	long
    //   153	5	8	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	9	153	finally
    //   11	35	153	finally
    //   39	86	153	finally
    //   91	110	153	finally
    //   110	133	153	finally
    //   133	138	153	finally
  }
  
  public void onTransferStart()
  {
    try
    {
      if (this.streamCount == 0) {
        this.startTimeMs = this.clock.elapsedRealtime();
      }
      this.streamCount += 1;
      return;
    }
    finally {}
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/upstream/DefaultBandwidthMeter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
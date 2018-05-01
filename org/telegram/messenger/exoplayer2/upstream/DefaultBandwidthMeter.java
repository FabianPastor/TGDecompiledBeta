package org.telegram.messenger.exoplayer2.upstream;

import android.os.Handler;
import org.telegram.messenger.exoplayer2.util.Clock;
import org.telegram.messenger.exoplayer2.util.SlidingPercentile;

public final class DefaultBandwidthMeter
  implements BandwidthMeter, TransferListener<Object>
{
  private static final int BYTES_TRANSFERRED_FOR_ESTIMATE = 524288;
  public static final int DEFAULT_MAX_WEIGHT = 2000;
  private static final int ELAPSED_MILLIS_FOR_ESTIMATE = 2000;
  private long bitrateEstimate;
  private final Clock clock;
  private final Handler eventHandler;
  private final BandwidthMeter.EventListener eventListener;
  private long sampleBytesTransferred;
  private long sampleStartTimeMs;
  private final SlidingPercentile slidingPercentile;
  private int streamCount;
  private long totalBytesTransferred;
  private long totalElapsedTimeMs;
  
  public DefaultBandwidthMeter()
  {
    this(null, null);
  }
  
  public DefaultBandwidthMeter(Handler paramHandler, BandwidthMeter.EventListener paramEventListener)
  {
    this(paramHandler, paramEventListener, 2000);
  }
  
  public DefaultBandwidthMeter(Handler paramHandler, BandwidthMeter.EventListener paramEventListener, int paramInt)
  {
    this(paramHandler, paramEventListener, paramInt, Clock.DEFAULT);
  }
  
  public DefaultBandwidthMeter(Handler paramHandler, BandwidthMeter.EventListener paramEventListener, int paramInt, Clock paramClock)
  {
    this.eventHandler = paramHandler;
    this.eventListener = paramEventListener;
    this.slidingPercentile = new SlidingPercentile(paramInt);
    this.clock = paramClock;
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
  
  public void onBytesTransferred(Object paramObject, int paramInt)
  {
    try
    {
      this.sampleBytesTransferred += paramInt;
      return;
    }
    finally
    {
      paramObject = finally;
      throw ((Throwable)paramObject);
    }
  }
  
  /* Error */
  public void onTransferEnd(Object paramObject)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 91	org/telegram/messenger/exoplayer2/upstream/DefaultBandwidthMeter:streamCount	I
    //   6: ifle +189 -> 195
    //   9: iconst_1
    //   10: istore_2
    //   11: iload_2
    //   12: invokestatic 97	org/telegram/messenger/exoplayer2/util/Assertions:checkState	(Z)V
    //   15: aload_0
    //   16: getfield 64	org/telegram/messenger/exoplayer2/upstream/DefaultBandwidthMeter:clock	Lorg/telegram/messenger/exoplayer2/util/Clock;
    //   19: invokeinterface 100 1 0
    //   24: lstore_3
    //   25: lload_3
    //   26: aload_0
    //   27: getfield 102	org/telegram/messenger/exoplayer2/upstream/DefaultBandwidthMeter:sampleStartTimeMs	J
    //   30: lsub
    //   31: l2i
    //   32: istore 5
    //   34: aload_0
    //   35: aload_0
    //   36: getfield 104	org/telegram/messenger/exoplayer2/upstream/DefaultBandwidthMeter:totalElapsedTimeMs	J
    //   39: iload 5
    //   41: i2l
    //   42: ladd
    //   43: putfield 104	org/telegram/messenger/exoplayer2/upstream/DefaultBandwidthMeter:totalElapsedTimeMs	J
    //   46: aload_0
    //   47: aload_0
    //   48: getfield 106	org/telegram/messenger/exoplayer2/upstream/DefaultBandwidthMeter:totalBytesTransferred	J
    //   51: aload_0
    //   52: getfield 87	org/telegram/messenger/exoplayer2/upstream/DefaultBandwidthMeter:sampleBytesTransferred	J
    //   55: ladd
    //   56: putfield 106	org/telegram/messenger/exoplayer2/upstream/DefaultBandwidthMeter:totalBytesTransferred	J
    //   59: iload 5
    //   61: ifle +88 -> 149
    //   64: aload_0
    //   65: getfield 87	org/telegram/messenger/exoplayer2/upstream/DefaultBandwidthMeter:sampleBytesTransferred	J
    //   68: ldc2_w 107
    //   71: lmul
    //   72: iload 5
    //   74: i2l
    //   75: ldiv
    //   76: l2f
    //   77: fstore 6
    //   79: aload_0
    //   80: getfield 62	org/telegram/messenger/exoplayer2/upstream/DefaultBandwidthMeter:slidingPercentile	Lorg/telegram/messenger/exoplayer2/util/SlidingPercentile;
    //   83: aload_0
    //   84: getfield 87	org/telegram/messenger/exoplayer2/upstream/DefaultBandwidthMeter:sampleBytesTransferred	J
    //   87: l2d
    //   88: invokestatic 114	java/lang/Math:sqrt	(D)D
    //   91: d2i
    //   92: fload 6
    //   94: invokevirtual 118	org/telegram/messenger/exoplayer2/util/SlidingPercentile:addSample	(IF)V
    //   97: aload_0
    //   98: getfield 104	org/telegram/messenger/exoplayer2/upstream/DefaultBandwidthMeter:totalElapsedTimeMs	J
    //   101: ldc2_w 119
    //   104: lcmp
    //   105: ifge +14 -> 119
    //   108: aload_0
    //   109: getfield 106	org/telegram/messenger/exoplayer2/upstream/DefaultBandwidthMeter:totalBytesTransferred	J
    //   112: ldc2_w 121
    //   115: lcmp
    //   116: iflt +33 -> 149
    //   119: aload_0
    //   120: getfield 62	org/telegram/messenger/exoplayer2/upstream/DefaultBandwidthMeter:slidingPercentile	Lorg/telegram/messenger/exoplayer2/util/SlidingPercentile;
    //   123: ldc 123
    //   125: invokevirtual 127	org/telegram/messenger/exoplayer2/util/SlidingPercentile:getPercentile	(F)F
    //   128: fstore 6
    //   130: fload 6
    //   132: invokestatic 133	java/lang/Float:isNaN	(F)Z
    //   135: ifeq +65 -> 200
    //   138: ldc2_w 65
    //   141: lstore 7
    //   143: aload_0
    //   144: lload 7
    //   146: putfield 68	org/telegram/messenger/exoplayer2/upstream/DefaultBandwidthMeter:bitrateEstimate	J
    //   149: aload_0
    //   150: iload 5
    //   152: aload_0
    //   153: getfield 87	org/telegram/messenger/exoplayer2/upstream/DefaultBandwidthMeter:sampleBytesTransferred	J
    //   156: aload_0
    //   157: getfield 68	org/telegram/messenger/exoplayer2/upstream/DefaultBandwidthMeter:bitrateEstimate	J
    //   160: invokespecial 135	org/telegram/messenger/exoplayer2/upstream/DefaultBandwidthMeter:notifyBandwidthSample	(IJJ)V
    //   163: aload_0
    //   164: getfield 91	org/telegram/messenger/exoplayer2/upstream/DefaultBandwidthMeter:streamCount	I
    //   167: iconst_1
    //   168: isub
    //   169: istore 5
    //   171: aload_0
    //   172: iload 5
    //   174: putfield 91	org/telegram/messenger/exoplayer2/upstream/DefaultBandwidthMeter:streamCount	I
    //   177: iload 5
    //   179: ifle +8 -> 187
    //   182: aload_0
    //   183: lload_3
    //   184: putfield 102	org/telegram/messenger/exoplayer2/upstream/DefaultBandwidthMeter:sampleStartTimeMs	J
    //   187: aload_0
    //   188: lconst_0
    //   189: putfield 87	org/telegram/messenger/exoplayer2/upstream/DefaultBandwidthMeter:sampleBytesTransferred	J
    //   192: aload_0
    //   193: monitorexit
    //   194: return
    //   195: iconst_0
    //   196: istore_2
    //   197: goto -186 -> 11
    //   200: fload 6
    //   202: f2l
    //   203: lstore 7
    //   205: goto -62 -> 143
    //   208: astore_1
    //   209: aload_0
    //   210: monitorexit
    //   211: aload_1
    //   212: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	213	0	this	DefaultBandwidthMeter
    //   0	213	1	paramObject	Object
    //   10	187	2	bool	boolean
    //   24	160	3	l1	long
    //   32	146	5	i	int
    //   77	124	6	f	float
    //   141	63	7	l2	long
    // Exception table:
    //   from	to	target	type
    //   2	9	208	finally
    //   11	59	208	finally
    //   64	119	208	finally
    //   119	138	208	finally
    //   143	149	208	finally
    //   149	177	208	finally
    //   182	187	208	finally
    //   187	192	208	finally
  }
  
  public void onTransferStart(Object paramObject, DataSpec paramDataSpec)
  {
    try
    {
      if (this.streamCount == 0) {
        this.sampleStartTimeMs = this.clock.elapsedRealtime();
      }
      this.streamCount += 1;
      return;
    }
    finally {}
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/upstream/DefaultBandwidthMeter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
package org.telegram.messenger.exoplayer2.source;

import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput.CryptoData;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.Util;

final class SampleMetadataQueue
{
  private static final int SAMPLE_CAPACITY_INCREMENT = 1000;
  private int absoluteFirstIndex;
  private int capacity = 1000;
  private TrackOutput.CryptoData[] cryptoDatas = new TrackOutput.CryptoData[this.capacity];
  private int[] flags = new int[this.capacity];
  private Format[] formats = new Format[this.capacity];
  private long largestDiscardedTimestampUs = Long.MIN_VALUE;
  private long largestQueuedTimestampUs = Long.MIN_VALUE;
  private int length;
  private long[] offsets = new long[this.capacity];
  private int readPosition;
  private int relativeFirstIndex;
  private int[] sizes = new int[this.capacity];
  private int[] sourceIds = new int[this.capacity];
  private long[] timesUs = new long[this.capacity];
  private Format upstreamFormat;
  private boolean upstreamFormatRequired = true;
  private boolean upstreamKeyframeRequired = true;
  private int upstreamSourceId;
  
  private long discardSamples(int paramInt)
  {
    this.largestDiscardedTimestampUs = Math.max(this.largestDiscardedTimestampUs, getLargestTimestamp(paramInt));
    this.length -= paramInt;
    this.absoluteFirstIndex += paramInt;
    this.relativeFirstIndex += paramInt;
    if (this.relativeFirstIndex >= this.capacity) {
      this.relativeFirstIndex -= this.capacity;
    }
    this.readPosition -= paramInt;
    if (this.readPosition < 0) {
      this.readPosition = 0;
    }
    if (this.length == 0) {
      if (this.relativeFirstIndex == 0)
      {
        paramInt = this.capacity;
        paramInt--;
      }
    }
    for (long l = this.offsets[paramInt] + this.sizes[paramInt];; l = this.offsets[this.relativeFirstIndex])
    {
      return l;
      paramInt = this.relativeFirstIndex;
      break;
    }
  }
  
  private int findSampleBefore(int paramInt1, int paramInt2, long paramLong, boolean paramBoolean)
  {
    int i = -1;
    int j = 0;
    int k = paramInt1;
    for (paramInt1 = j; (paramInt1 < paramInt2) && (this.timesUs[k] <= paramLong); paramInt1++)
    {
      if ((!paramBoolean) || ((this.flags[k] & 0x1) != 0)) {
        i = paramInt1;
      }
      j = k + 1;
      k = j;
      if (j == this.capacity) {
        k = 0;
      }
    }
    return i;
  }
  
  private long getLargestTimestamp(int paramInt)
  {
    long l1;
    if (paramInt == 0)
    {
      l1 = Long.MIN_VALUE;
      return l1;
    }
    long l2 = Long.MIN_VALUE;
    int i = getRelativeIndex(paramInt - 1);
    for (int j = 0;; j++)
    {
      l1 = l2;
      if (j >= paramInt) {
        break;
      }
      l2 = Math.max(l2, this.timesUs[i]);
      l1 = l2;
      if ((this.flags[i] & 0x1) != 0) {
        break;
      }
      int k = i - 1;
      i = k;
      if (k == -1) {
        i = this.capacity - 1;
      }
    }
  }
  
  private int getRelativeIndex(int paramInt)
  {
    paramInt = this.relativeFirstIndex + paramInt;
    if (paramInt < this.capacity) {}
    for (;;)
    {
      return paramInt;
      paramInt -= this.capacity;
    }
  }
  
  /* Error */
  public int advanceTo(long paramLong, boolean paramBoolean1, boolean paramBoolean2)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: aload_0
    //   4: getfield 89	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:readPosition	I
    //   7: invokespecial 95	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:getRelativeIndex	(I)I
    //   10: istore 5
    //   12: aload_0
    //   13: invokevirtual 101	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:hasNextSample	()Z
    //   16: ifeq +33 -> 49
    //   19: lload_1
    //   20: aload_0
    //   21: getfield 47	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:timesUs	[J
    //   24: iload 5
    //   26: laload
    //   27: lcmp
    //   28: iflt +21 -> 49
    //   31: aload_0
    //   32: getfield 65	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:largestQueuedTimestampUs	J
    //   35: lstore 6
    //   37: lload_1
    //   38: lload 6
    //   40: lcmp
    //   41: ifle +16 -> 57
    //   44: iload 4
    //   46: ifne +11 -> 57
    //   49: iconst_m1
    //   50: istore 5
    //   52: aload_0
    //   53: monitorexit
    //   54: iload 5
    //   56: ireturn
    //   57: aload_0
    //   58: iload 5
    //   60: aload_0
    //   61: getfield 83	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:length	I
    //   64: aload_0
    //   65: getfield 89	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:readPosition	I
    //   68: isub
    //   69: lload_1
    //   70: iload_3
    //   71: invokespecial 103	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:findSampleBefore	(IIJZ)I
    //   74: istore 5
    //   76: iload 5
    //   78: iconst_m1
    //   79: if_icmpne +9 -> 88
    //   82: iconst_m1
    //   83: istore 5
    //   85: goto -33 -> 52
    //   88: aload_0
    //   89: aload_0
    //   90: getfield 89	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:readPosition	I
    //   93: iload 5
    //   95: iadd
    //   96: putfield 89	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:readPosition	I
    //   99: goto -47 -> 52
    //   102: astore 8
    //   104: aload_0
    //   105: monitorexit
    //   106: aload 8
    //   108: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	109	0	this	SampleMetadataQueue
    //   0	109	1	paramLong	long
    //   0	109	3	paramBoolean1	boolean
    //   0	109	4	paramBoolean2	boolean
    //   10	86	5	i	int
    //   35	4	6	l	long
    //   102	5	8	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	37	102	finally
    //   57	76	102	finally
    //   88	99	102	finally
  }
  
  public int advanceToEnd()
  {
    try
    {
      int i = this.length;
      int j = this.readPosition;
      this.readPosition = this.length;
      return i - j;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  /* Error */
  public boolean attemptSplice(long paramLong)
  {
    // Byte code:
    //   0: iconst_1
    //   1: istore_3
    //   2: aload_0
    //   3: monitorenter
    //   4: aload_0
    //   5: getfield 83	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:length	I
    //   8: ifne +25 -> 33
    //   11: aload_0
    //   12: getfield 63	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:largestDiscardedTimestampUs	J
    //   15: lstore 4
    //   17: lload_1
    //   18: lload 4
    //   20: lcmp
    //   21: ifle +7 -> 28
    //   24: aload_0
    //   25: monitorexit
    //   26: iload_3
    //   27: ireturn
    //   28: iconst_0
    //   29: istore_3
    //   30: goto -6 -> 24
    //   33: aload_0
    //   34: getfield 63	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:largestDiscardedTimestampUs	J
    //   37: aload_0
    //   38: aload_0
    //   39: getfield 89	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:readPosition	I
    //   42: invokespecial 75	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:getLargestTimestamp	(I)J
    //   45: invokestatic 81	java/lang/Math:max	(JJ)J
    //   48: lload_1
    //   49: lcmp
    //   50: iflt +8 -> 58
    //   53: iconst_0
    //   54: istore_3
    //   55: goto -31 -> 24
    //   58: aload_0
    //   59: getfield 83	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:length	I
    //   62: istore 6
    //   64: aload_0
    //   65: aload_0
    //   66: getfield 83	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:length	I
    //   69: iconst_1
    //   70: isub
    //   71: invokespecial 95	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:getRelativeIndex	(I)I
    //   74: istore 7
    //   76: iload 6
    //   78: aload_0
    //   79: getfield 89	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:readPosition	I
    //   82: if_icmple +56 -> 138
    //   85: aload_0
    //   86: getfield 47	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:timesUs	[J
    //   89: iload 7
    //   91: laload
    //   92: lload_1
    //   93: lcmp
    //   94: iflt +44 -> 138
    //   97: iload 6
    //   99: iconst_1
    //   100: isub
    //   101: istore 8
    //   103: iload 7
    //   105: iconst_1
    //   106: isub
    //   107: istore 9
    //   109: iload 9
    //   111: istore 7
    //   113: iload 8
    //   115: istore 6
    //   117: iload 9
    //   119: iconst_m1
    //   120: if_icmpne -44 -> 76
    //   123: aload_0
    //   124: getfield 41	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:capacity	I
    //   127: iconst_1
    //   128: isub
    //   129: istore 7
    //   131: iload 8
    //   133: istore 6
    //   135: goto -59 -> 76
    //   138: aload_0
    //   139: aload_0
    //   140: getfield 85	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:absoluteFirstIndex	I
    //   143: iload 6
    //   145: iadd
    //   146: invokevirtual 110	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:discardUpstreamSamples	(I)J
    //   149: pop2
    //   150: goto -126 -> 24
    //   153: astore 10
    //   155: aload_0
    //   156: monitorexit
    //   157: aload 10
    //   159: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	160	0	this	SampleMetadataQueue
    //   0	160	1	paramLong	long
    //   1	54	3	bool	boolean
    //   15	4	4	l	long
    //   62	84	6	i	int
    //   74	56	7	j	int
    //   101	31	8	k	int
    //   107	14	9	m	int
    //   153	5	10	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   4	17	153	finally
    //   33	53	153	finally
    //   58	76	153	finally
    //   76	97	153	finally
    //   123	131	153	finally
    //   138	150	153	finally
  }
  
  public void commitSample(long paramLong1, int paramInt1, long paramLong2, int paramInt2, TrackOutput.CryptoData paramCryptoData)
  {
    for (;;)
    {
      try
      {
        boolean bool = this.upstreamKeyframeRequired;
        if (bool)
        {
          if ((paramInt1 & 0x1) == 0) {
            return;
          }
          this.upstreamKeyframeRequired = false;
        }
        if (!this.upstreamFormatRequired)
        {
          bool = true;
          Assertions.checkState(bool);
          commitSampleTimestamp(paramLong1);
          int i = getRelativeIndex(this.length);
          this.timesUs[i] = paramLong1;
          this.offsets[i] = paramLong2;
          this.sizes[i] = paramInt2;
          this.flags[i] = paramInt1;
          this.cryptoDatas[i] = paramCryptoData;
          this.formats[i] = this.upstreamFormat;
          this.sourceIds[i] = this.upstreamSourceId;
          this.length += 1;
          if (this.length == this.capacity)
          {
            i = this.capacity + 1000;
            paramCryptoData = new int[i];
            long[] arrayOfLong1 = new long[i];
            long[] arrayOfLong2 = new long[i];
            int[] arrayOfInt1 = new int[i];
            int[] arrayOfInt2 = new int[i];
            TrackOutput.CryptoData[] arrayOfCryptoData = new TrackOutput.CryptoData[i];
            Format[] arrayOfFormat = new Format[i];
            paramInt2 = this.capacity - this.relativeFirstIndex;
            System.arraycopy(this.offsets, this.relativeFirstIndex, arrayOfLong1, 0, paramInt2);
            System.arraycopy(this.timesUs, this.relativeFirstIndex, arrayOfLong2, 0, paramInt2);
            System.arraycopy(this.flags, this.relativeFirstIndex, arrayOfInt1, 0, paramInt2);
            System.arraycopy(this.sizes, this.relativeFirstIndex, arrayOfInt2, 0, paramInt2);
            System.arraycopy(this.cryptoDatas, this.relativeFirstIndex, arrayOfCryptoData, 0, paramInt2);
            System.arraycopy(this.formats, this.relativeFirstIndex, arrayOfFormat, 0, paramInt2);
            System.arraycopy(this.sourceIds, this.relativeFirstIndex, paramCryptoData, 0, paramInt2);
            paramInt1 = this.relativeFirstIndex;
            System.arraycopy(this.offsets, 0, arrayOfLong1, paramInt2, paramInt1);
            System.arraycopy(this.timesUs, 0, arrayOfLong2, paramInt2, paramInt1);
            System.arraycopy(this.flags, 0, arrayOfInt1, paramInt2, paramInt1);
            System.arraycopy(this.sizes, 0, arrayOfInt2, paramInt2, paramInt1);
            System.arraycopy(this.cryptoDatas, 0, arrayOfCryptoData, paramInt2, paramInt1);
            System.arraycopy(this.formats, 0, arrayOfFormat, paramInt2, paramInt1);
            System.arraycopy(this.sourceIds, 0, paramCryptoData, paramInt2, paramInt1);
            this.offsets = arrayOfLong1;
            this.timesUs = arrayOfLong2;
            this.flags = arrayOfInt1;
            this.sizes = arrayOfInt2;
            this.cryptoDatas = arrayOfCryptoData;
            this.formats = arrayOfFormat;
            this.sourceIds = paramCryptoData;
            this.relativeFirstIndex = 0;
            this.length = this.capacity;
            this.capacity = i;
          }
        }
        else
        {
          bool = false;
        }
      }
      finally {}
    }
  }
  
  public void commitSampleTimestamp(long paramLong)
  {
    try
    {
      this.largestQueuedTimestampUs = Math.max(this.largestQueuedTimestampUs, paramLong);
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  /* Error */
  public long discardTo(long paramLong, boolean paramBoolean1, boolean paramBoolean2)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 83	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:length	I
    //   6: ifeq +21 -> 27
    //   9: aload_0
    //   10: getfield 47	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:timesUs	[J
    //   13: aload_0
    //   14: getfield 87	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:relativeFirstIndex	I
    //   17: laload
    //   18: lstore 5
    //   20: lload_1
    //   21: lload 5
    //   23: lcmp
    //   24: ifge +11 -> 35
    //   27: ldc2_w 135
    //   30: lstore_1
    //   31: aload_0
    //   32: monitorexit
    //   33: lload_1
    //   34: lreturn
    //   35: iload 4
    //   37: ifeq +49 -> 86
    //   40: aload_0
    //   41: getfield 89	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:readPosition	I
    //   44: aload_0
    //   45: getfield 83	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:length	I
    //   48: if_icmpeq +38 -> 86
    //   51: aload_0
    //   52: getfield 89	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:readPosition	I
    //   55: iconst_1
    //   56: iadd
    //   57: istore 7
    //   59: aload_0
    //   60: aload_0
    //   61: getfield 87	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:relativeFirstIndex	I
    //   64: iload 7
    //   66: lload_1
    //   67: iload_3
    //   68: invokespecial 103	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:findSampleBefore	(IIJZ)I
    //   71: istore 7
    //   73: iload 7
    //   75: iconst_m1
    //   76: if_icmpne +19 -> 95
    //   79: ldc2_w 135
    //   82: lstore_1
    //   83: goto -52 -> 31
    //   86: aload_0
    //   87: getfield 83	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:length	I
    //   90: istore 7
    //   92: goto -33 -> 59
    //   95: aload_0
    //   96: iload 7
    //   98: invokespecial 138	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:discardSamples	(I)J
    //   101: lstore_1
    //   102: goto -71 -> 31
    //   105: astore 8
    //   107: aload_0
    //   108: monitorexit
    //   109: aload 8
    //   111: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	112	0	this	SampleMetadataQueue
    //   0	112	1	paramLong	long
    //   0	112	3	paramBoolean1	boolean
    //   0	112	4	paramBoolean2	boolean
    //   18	4	5	l	long
    //   57	40	7	i	int
    //   105	5	8	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	20	105	finally
    //   40	59	105	finally
    //   59	73	105	finally
    //   86	92	105	finally
    //   95	102	105	finally
  }
  
  /* Error */
  public long discardToEnd()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 83	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:length	I
    //   6: istore_1
    //   7: iload_1
    //   8: ifne +11 -> 19
    //   11: ldc2_w 135
    //   14: lstore_2
    //   15: aload_0
    //   16: monitorexit
    //   17: lload_2
    //   18: lreturn
    //   19: aload_0
    //   20: aload_0
    //   21: getfield 83	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:length	I
    //   24: invokespecial 138	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:discardSamples	(I)J
    //   27: lstore_2
    //   28: goto -13 -> 15
    //   31: astore 4
    //   33: aload_0
    //   34: monitorexit
    //   35: aload 4
    //   37: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	38	0	this	SampleMetadataQueue
    //   6	2	1	i	int
    //   14	14	2	l	long
    //   31	5	4	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	7	31	finally
    //   19	28	31	finally
  }
  
  /* Error */
  public long discardToRead()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 89	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:readPosition	I
    //   6: istore_1
    //   7: iload_1
    //   8: ifne +11 -> 19
    //   11: ldc2_w 135
    //   14: lstore_2
    //   15: aload_0
    //   16: monitorexit
    //   17: lload_2
    //   18: lreturn
    //   19: aload_0
    //   20: aload_0
    //   21: getfield 89	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:readPosition	I
    //   24: invokespecial 138	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:discardSamples	(I)J
    //   27: lstore_2
    //   28: goto -13 -> 15
    //   31: astore 4
    //   33: aload_0
    //   34: monitorexit
    //   35: aload 4
    //   37: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	38	0	this	SampleMetadataQueue
    //   6	2	1	i	int
    //   14	14	2	l	long
    //   31	5	4	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	7	31	finally
    //   19	28	31	finally
  }
  
  public long discardUpstreamSamples(int paramInt)
  {
    paramInt = getWriteIndex() - paramInt;
    boolean bool;
    if ((paramInt >= 0) && (paramInt <= this.length - this.readPosition))
    {
      bool = true;
      Assertions.checkArgument(bool);
      this.length -= paramInt;
      this.largestQueuedTimestampUs = Math.max(this.largestDiscardedTimestampUs, getLargestTimestamp(this.length));
      if (this.length != 0) {
        break label75;
      }
    }
    for (long l = 0L;; l = this.offsets[paramInt] + this.sizes[paramInt])
    {
      return l;
      bool = false;
      break;
      label75:
      paramInt = getRelativeIndex(this.length - 1);
    }
  }
  
  public boolean format(Format paramFormat)
  {
    boolean bool = false;
    if (paramFormat == null) {}
    for (;;)
    {
      try
      {
        this.upstreamFormatRequired = true;
        return bool;
      }
      finally {}
      this.upstreamFormatRequired = false;
      if (!Util.areEqual(paramFormat, this.upstreamFormat))
      {
        this.upstreamFormat = paramFormat;
        bool = true;
      }
    }
  }
  
  public int getFirstIndex()
  {
    return this.absoluteFirstIndex;
  }
  
  /* Error */
  public long getFirstTimestampUs()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 83	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:length	I
    //   6: istore_1
    //   7: iload_1
    //   8: ifne +11 -> 19
    //   11: ldc2_w 60
    //   14: lstore_2
    //   15: aload_0
    //   16: monitorexit
    //   17: lload_2
    //   18: lreturn
    //   19: aload_0
    //   20: getfield 47	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:timesUs	[J
    //   23: aload_0
    //   24: getfield 87	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:relativeFirstIndex	I
    //   27: laload
    //   28: lstore_2
    //   29: goto -14 -> 15
    //   32: astore 4
    //   34: aload_0
    //   35: monitorexit
    //   36: aload 4
    //   38: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	39	0	this	SampleMetadataQueue
    //   6	2	1	i	int
    //   14	15	2	l	long
    //   32	5	4	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	7	32	finally
    //   19	29	32	finally
  }
  
  public long getLargestQueuedTimestampUs()
  {
    try
    {
      long l = this.largestQueuedTimestampUs;
      return l;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public int getReadIndex()
  {
    return this.absoluteFirstIndex + this.readPosition;
  }
  
  /* Error */
  public Format getUpstreamFormat()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 67	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:upstreamFormatRequired	Z
    //   6: istore_1
    //   7: iload_1
    //   8: ifeq +9 -> 17
    //   11: aconst_null
    //   12: astore_2
    //   13: aload_0
    //   14: monitorexit
    //   15: aload_2
    //   16: areturn
    //   17: aload_0
    //   18: getfield 124	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:upstreamFormat	Lorg/telegram/messenger/exoplayer2/Format;
    //   21: astore_2
    //   22: goto -9 -> 13
    //   25: astore_2
    //   26: aload_0
    //   27: monitorexit
    //   28: aload_2
    //   29: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	30	0	this	SampleMetadataQueue
    //   6	2	1	bool	boolean
    //   12	10	2	localFormat	Format
    //   25	4	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	7	25	finally
    //   17	22	25	finally
  }
  
  public int getWriteIndex()
  {
    return this.absoluteFirstIndex + this.length;
  }
  
  /* Error */
  public boolean hasNextSample()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 89	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:readPosition	I
    //   6: istore_1
    //   7: aload_0
    //   8: getfield 83	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:length	I
    //   11: istore_2
    //   12: iload_1
    //   13: iload_2
    //   14: if_icmpeq +9 -> 23
    //   17: iconst_1
    //   18: istore_3
    //   19: aload_0
    //   20: monitorexit
    //   21: iload_3
    //   22: ireturn
    //   23: iconst_0
    //   24: istore_3
    //   25: goto -6 -> 19
    //   28: astore 4
    //   30: aload_0
    //   31: monitorexit
    //   32: aload 4
    //   34: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	35	0	this	SampleMetadataQueue
    //   6	9	1	i	int
    //   11	4	2	j	int
    //   18	7	3	bool	boolean
    //   28	5	4	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	12	28	finally
  }
  
  public int peekSourceId()
  {
    int i = getRelativeIndex(this.readPosition);
    if (hasNextSample()) {}
    for (i = this.sourceIds[i];; i = this.upstreamSourceId) {
      return i;
    }
  }
  
  /* Error */
  public int read(org.telegram.messenger.exoplayer2.FormatHolder paramFormatHolder, org.telegram.messenger.exoplayer2.decoder.DecoderInputBuffer paramDecoderInputBuffer, boolean paramBoolean1, boolean paramBoolean2, Format paramFormat, SampleExtrasHolder paramSampleExtrasHolder)
  {
    // Byte code:
    //   0: bipush -4
    //   2: istore 7
    //   4: aload_0
    //   5: monitorenter
    //   6: aload_0
    //   7: invokevirtual 101	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:hasNextSample	()Z
    //   10: ifne +60 -> 70
    //   13: iload 4
    //   15: ifeq +13 -> 28
    //   18: aload_2
    //   19: iconst_4
    //   20: invokevirtual 170	org/telegram/messenger/exoplayer2/decoder/DecoderInputBuffer:setFlags	(I)V
    //   23: aload_0
    //   24: monitorexit
    //   25: iload 7
    //   27: ireturn
    //   28: aload_0
    //   29: getfield 124	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:upstreamFormat	Lorg/telegram/messenger/exoplayer2/Format;
    //   32: ifnull +31 -> 63
    //   35: iload_3
    //   36: ifne +12 -> 48
    //   39: aload_0
    //   40: getfield 124	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:upstreamFormat	Lorg/telegram/messenger/exoplayer2/Format;
    //   43: aload 5
    //   45: if_acmpeq +18 -> 63
    //   48: aload_1
    //   49: aload_0
    //   50: getfield 124	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:upstreamFormat	Lorg/telegram/messenger/exoplayer2/Format;
    //   53: putfield 174	org/telegram/messenger/exoplayer2/FormatHolder:format	Lorg/telegram/messenger/exoplayer2/Format;
    //   56: bipush -5
    //   58: istore 7
    //   60: goto -37 -> 23
    //   63: bipush -3
    //   65: istore 7
    //   67: goto -44 -> 23
    //   70: aload_0
    //   71: aload_0
    //   72: getfield 89	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:readPosition	I
    //   75: invokespecial 95	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:getRelativeIndex	(I)I
    //   78: istore 8
    //   80: iload_3
    //   81: ifne +15 -> 96
    //   84: aload_0
    //   85: getfield 59	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:formats	[Lorg/telegram/messenger/exoplayer2/Format;
    //   88: iload 8
    //   90: aaload
    //   91: aload 5
    //   93: if_acmpeq +21 -> 114
    //   96: aload_1
    //   97: aload_0
    //   98: getfield 59	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:formats	[Lorg/telegram/messenger/exoplayer2/Format;
    //   101: iload 8
    //   103: aaload
    //   104: putfield 174	org/telegram/messenger/exoplayer2/FormatHolder:format	Lorg/telegram/messenger/exoplayer2/Format;
    //   107: bipush -5
    //   109: istore 7
    //   111: goto -88 -> 23
    //   114: aload_2
    //   115: invokevirtual 177	org/telegram/messenger/exoplayer2/decoder/DecoderInputBuffer:isFlagsOnly	()Z
    //   118: ifeq +10 -> 128
    //   121: bipush -3
    //   123: istore 7
    //   125: goto -102 -> 23
    //   128: aload_2
    //   129: aload_0
    //   130: getfield 47	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:timesUs	[J
    //   133: iload 8
    //   135: laload
    //   136: putfield 180	org/telegram/messenger/exoplayer2/decoder/DecoderInputBuffer:timeUs	J
    //   139: aload_2
    //   140: aload_0
    //   141: getfield 49	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:flags	[I
    //   144: iload 8
    //   146: iaload
    //   147: invokevirtual 170	org/telegram/messenger/exoplayer2/decoder/DecoderInputBuffer:setFlags	(I)V
    //   150: aload 6
    //   152: aload_0
    //   153: getfield 51	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:sizes	[I
    //   156: iload 8
    //   158: iaload
    //   159: putfield 183	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue$SampleExtrasHolder:size	I
    //   162: aload 6
    //   164: aload_0
    //   165: getfield 45	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:offsets	[J
    //   168: iload 8
    //   170: laload
    //   171: putfield 186	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue$SampleExtrasHolder:offset	J
    //   174: aload 6
    //   176: aload_0
    //   177: getfield 55	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:cryptoDatas	[Lorg/telegram/messenger/exoplayer2/extractor/TrackOutput$CryptoData;
    //   180: iload 8
    //   182: aaload
    //   183: putfield 190	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue$SampleExtrasHolder:cryptoData	Lorg/telegram/messenger/exoplayer2/extractor/TrackOutput$CryptoData;
    //   186: aload_0
    //   187: aload_0
    //   188: getfield 89	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:readPosition	I
    //   191: iconst_1
    //   192: iadd
    //   193: putfield 89	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:readPosition	I
    //   196: goto -173 -> 23
    //   199: astore_1
    //   200: aload_0
    //   201: monitorexit
    //   202: aload_1
    //   203: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	204	0	this	SampleMetadataQueue
    //   0	204	1	paramFormatHolder	org.telegram.messenger.exoplayer2.FormatHolder
    //   0	204	2	paramDecoderInputBuffer	org.telegram.messenger.exoplayer2.decoder.DecoderInputBuffer
    //   0	204	3	paramBoolean1	boolean
    //   0	204	4	paramBoolean2	boolean
    //   0	204	5	paramFormat	Format
    //   0	204	6	paramSampleExtrasHolder	SampleExtrasHolder
    //   2	122	7	i	int
    //   78	103	8	j	int
    // Exception table:
    //   from	to	target	type
    //   6	13	199	finally
    //   18	23	199	finally
    //   28	35	199	finally
    //   39	48	199	finally
    //   48	56	199	finally
    //   70	80	199	finally
    //   84	96	199	finally
    //   96	107	199	finally
    //   114	121	199	finally
    //   128	196	199	finally
  }
  
  public void reset(boolean paramBoolean)
  {
    this.length = 0;
    this.absoluteFirstIndex = 0;
    this.relativeFirstIndex = 0;
    this.readPosition = 0;
    this.upstreamKeyframeRequired = true;
    this.largestDiscardedTimestampUs = Long.MIN_VALUE;
    this.largestQueuedTimestampUs = Long.MIN_VALUE;
    if (paramBoolean)
    {
      this.upstreamFormat = null;
      this.upstreamFormatRequired = true;
    }
  }
  
  public void rewind()
  {
    try
    {
      this.readPosition = 0;
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  /* Error */
  public boolean setReadPosition(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 85	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:absoluteFirstIndex	I
    //   6: iload_1
    //   7: if_icmpgt +32 -> 39
    //   10: iload_1
    //   11: aload_0
    //   12: getfield 85	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:absoluteFirstIndex	I
    //   15: aload_0
    //   16: getfield 83	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:length	I
    //   19: iadd
    //   20: if_icmpgt +19 -> 39
    //   23: aload_0
    //   24: iload_1
    //   25: aload_0
    //   26: getfield 85	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:absoluteFirstIndex	I
    //   29: isub
    //   30: putfield 89	org/telegram/messenger/exoplayer2/source/SampleMetadataQueue:readPosition	I
    //   33: iconst_1
    //   34: istore_2
    //   35: aload_0
    //   36: monitorexit
    //   37: iload_2
    //   38: ireturn
    //   39: iconst_0
    //   40: istore_2
    //   41: goto -6 -> 35
    //   44: astore_3
    //   45: aload_0
    //   46: monitorexit
    //   47: aload_3
    //   48: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	49	0	this	SampleMetadataQueue
    //   0	49	1	paramInt	int
    //   34	7	2	bool	boolean
    //   44	4	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	33	44	finally
  }
  
  public void sourceId(int paramInt)
  {
    this.upstreamSourceId = paramInt;
  }
  
  public static final class SampleExtrasHolder
  {
    public TrackOutput.CryptoData cryptoData;
    public long offset;
    public int size;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/SampleMetadataQueue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
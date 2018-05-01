package org.telegram.messenger.exoplayer2.extractor;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.FormatHolder;
import org.telegram.messenger.exoplayer2.decoder.CryptoInfo;
import org.telegram.messenger.exoplayer2.decoder.DecoderInputBuffer;
import org.telegram.messenger.exoplayer2.upstream.Allocation;
import org.telegram.messenger.exoplayer2.upstream.Allocator;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.Util;

public final class DefaultTrackOutput
  implements TrackOutput
{
  private static final int INITIAL_SCRATCH_SIZE = 32;
  private static final int STATE_DISABLED = 2;
  private static final int STATE_ENABLED = 0;
  private static final int STATE_ENABLED_WRITING = 1;
  private final int allocationLength;
  private final Allocator allocator;
  private final LinkedBlockingDeque<Allocation> dataQueue;
  private Format downstreamFormat;
  private final BufferExtrasHolder extrasHolder;
  private final InfoQueue infoQueue;
  private Allocation lastAllocation;
  private int lastAllocationOffset;
  private Format lastUnadjustedFormat;
  private boolean pendingFormatAdjustment;
  private boolean pendingSplice;
  private long sampleOffsetUs;
  private final ParsableByteArray scratch;
  private final AtomicInteger state;
  private long totalBytesDropped;
  private long totalBytesWritten;
  private UpstreamFormatChangedListener upstreamFormatChangeListener;
  
  public DefaultTrackOutput(Allocator paramAllocator)
  {
    this.allocator = paramAllocator;
    this.allocationLength = paramAllocator.getIndividualAllocationLength();
    this.infoQueue = new InfoQueue();
    this.dataQueue = new LinkedBlockingDeque();
    this.extrasHolder = new BufferExtrasHolder(null);
    this.scratch = new ParsableByteArray(32);
    this.state = new AtomicInteger();
    this.lastAllocationOffset = this.allocationLength;
  }
  
  private void clearSampleData()
  {
    this.infoQueue.clearSampleData();
    this.allocator.release((Allocation[])this.dataQueue.toArray(new Allocation[this.dataQueue.size()]));
    this.dataQueue.clear();
    this.allocator.trim();
    this.totalBytesDropped = 0L;
    this.totalBytesWritten = 0L;
    this.lastAllocation = null;
    this.lastAllocationOffset = this.allocationLength;
  }
  
  private void dropDownstreamTo(long paramLong)
  {
    int j = (int)(paramLong - this.totalBytesDropped) / this.allocationLength;
    int i = 0;
    while (i < j)
    {
      this.allocator.release((Allocation)this.dataQueue.remove());
      this.totalBytesDropped += this.allocationLength;
      i += 1;
    }
  }
  
  private void dropUpstreamFrom(long paramLong)
  {
    int j = (int)(paramLong - this.totalBytesDropped);
    int i = j / this.allocationLength;
    int k = j % this.allocationLength;
    j = this.dataQueue.size() - i - 1;
    i = j;
    if (k == 0) {
      i = j + 1;
    }
    j = 0;
    while (j < i)
    {
      this.allocator.release((Allocation)this.dataQueue.removeLast());
      j += 1;
    }
    this.lastAllocation = ((Allocation)this.dataQueue.peekLast());
    i = k;
    if (k == 0) {
      i = this.allocationLength;
    }
    this.lastAllocationOffset = i;
  }
  
  private void endWriteOperation()
  {
    if (!this.state.compareAndSet(1, 0)) {
      clearSampleData();
    }
  }
  
  private static Format getAdjustedSampleFormat(Format paramFormat, long paramLong)
  {
    Format localFormat;
    if (paramFormat == null) {
      localFormat = null;
    }
    do
    {
      do
      {
        return localFormat;
        localFormat = paramFormat;
      } while (paramLong == 0L);
      localFormat = paramFormat;
    } while (paramFormat.subsampleOffsetUs == Long.MAX_VALUE);
    return paramFormat.copyWithSubsampleOffsetUs(paramFormat.subsampleOffsetUs + paramLong);
  }
  
  private int prepareForAppend(int paramInt)
  {
    if (this.lastAllocationOffset == this.allocationLength)
    {
      this.lastAllocationOffset = 0;
      this.lastAllocation = this.allocator.allocate();
      this.dataQueue.add(this.lastAllocation);
    }
    return Math.min(paramInt, this.allocationLength - this.lastAllocationOffset);
  }
  
  private void readData(long paramLong, ByteBuffer paramByteBuffer, int paramInt)
  {
    while (paramInt > 0)
    {
      dropDownstreamTo(paramLong);
      int i = (int)(paramLong - this.totalBytesDropped);
      int j = Math.min(paramInt, this.allocationLength - i);
      Allocation localAllocation = (Allocation)this.dataQueue.peek();
      paramByteBuffer.put(localAllocation.data, localAllocation.translateOffset(i), j);
      paramLong += j;
      paramInt -= j;
    }
  }
  
  private void readData(long paramLong, byte[] paramArrayOfByte, int paramInt)
  {
    int i = 0;
    while (i < paramInt)
    {
      dropDownstreamTo(paramLong);
      int j = (int)(paramLong - this.totalBytesDropped);
      int k = Math.min(paramInt - i, this.allocationLength - j);
      Allocation localAllocation = (Allocation)this.dataQueue.peek();
      System.arraycopy(localAllocation.data, localAllocation.translateOffset(j), paramArrayOfByte, i, k);
      paramLong += k;
      i += k;
    }
  }
  
  private void readEncryptionData(DecoderInputBuffer paramDecoderInputBuffer, BufferExtrasHolder paramBufferExtrasHolder)
  {
    long l1 = paramBufferExtrasHolder.offset;
    this.scratch.reset(1);
    readData(l1, this.scratch.data, 1);
    l1 += 1L;
    int j = this.scratch.data[0];
    if ((j & 0x80) != 0)
    {
      i = 1;
      j &= 0x7F;
      if (paramDecoderInputBuffer.cryptoInfo.iv == null) {
        paramDecoderInputBuffer.cryptoInfo.iv = new byte[16];
      }
      readData(l1, paramDecoderInputBuffer.cryptoInfo.iv, j);
      l1 += j;
      if (i == 0) {
        break label307;
      }
      this.scratch.reset(2);
      readData(l1, this.scratch.data, 2);
      l1 += 2L;
    }
    Object localObject2;
    Object localObject1;
    label307:
    for (j = this.scratch.readUnsignedShort();; j = 1)
    {
      localObject2 = paramDecoderInputBuffer.cryptoInfo.numBytesOfClearData;
      if (localObject2 != null)
      {
        localObject1 = localObject2;
        if (localObject2.length >= j) {}
      }
      else
      {
        localObject1 = new int[j];
      }
      int[] arrayOfInt = paramDecoderInputBuffer.cryptoInfo.numBytesOfEncryptedData;
      if (arrayOfInt != null)
      {
        localObject2 = arrayOfInt;
        if (arrayOfInt.length >= j) {}
      }
      else
      {
        localObject2 = new int[j];
      }
      if (i == 0) {
        break label313;
      }
      i = j * 6;
      this.scratch.reset(i);
      readData(l1, this.scratch.data, i);
      long l2 = l1 + i;
      this.scratch.setPosition(0);
      i = 0;
      for (;;)
      {
        l1 = l2;
        if (i >= j) {
          break;
        }
        localObject1[i] = this.scratch.readUnsignedShort();
        localObject2[i] = this.scratch.readUnsignedIntToInt();
        i += 1;
      }
      i = 0;
      break;
    }
    label313:
    localObject1[0] = 0;
    localObject2[0] = (paramBufferExtrasHolder.size - (int)(l1 - paramBufferExtrasHolder.offset));
    paramDecoderInputBuffer.cryptoInfo.set(j, (int[])localObject1, (int[])localObject2, paramBufferExtrasHolder.encryptionKeyId, paramDecoderInputBuffer.cryptoInfo.iv, 1);
    int i = (int)(l1 - paramBufferExtrasHolder.offset);
    paramBufferExtrasHolder.offset += i;
    paramBufferExtrasHolder.size -= i;
  }
  
  private boolean startWriteOperation()
  {
    return this.state.compareAndSet(0, 1);
  }
  
  public void disable()
  {
    if (this.state.getAndSet(2) == 0) {
      clearSampleData();
    }
  }
  
  public void discardUpstreamSamples(int paramInt)
  {
    this.totalBytesWritten = this.infoQueue.discardUpstreamSamples(paramInt);
    dropUpstreamFrom(this.totalBytesWritten);
  }
  
  public void format(Format paramFormat)
  {
    Format localFormat = getAdjustedSampleFormat(paramFormat, this.sampleOffsetUs);
    boolean bool = this.infoQueue.format(localFormat);
    this.lastUnadjustedFormat = paramFormat;
    this.pendingFormatAdjustment = false;
    if ((this.upstreamFormatChangeListener != null) && (bool)) {
      this.upstreamFormatChangeListener.onUpstreamFormatChanged(localFormat);
    }
  }
  
  public long getLargestQueuedTimestampUs()
  {
    return this.infoQueue.getLargestQueuedTimestampUs();
  }
  
  public int getReadIndex()
  {
    return this.infoQueue.getReadIndex();
  }
  
  public Format getUpstreamFormat()
  {
    return this.infoQueue.getUpstreamFormat();
  }
  
  public int getWriteIndex()
  {
    return this.infoQueue.getWriteIndex();
  }
  
  public boolean isEmpty()
  {
    return this.infoQueue.isEmpty();
  }
  
  public int peekSourceId()
  {
    return this.infoQueue.peekSourceId();
  }
  
  public int readData(FormatHolder paramFormatHolder, DecoderInputBuffer paramDecoderInputBuffer, boolean paramBoolean1, boolean paramBoolean2, long paramLong)
  {
    switch (this.infoQueue.readData(paramFormatHolder, paramDecoderInputBuffer, paramBoolean1, paramBoolean2, this.downstreamFormat, this.extrasHolder))
    {
    default: 
      throw new IllegalStateException();
    case -5: 
      this.downstreamFormat = paramFormatHolder.format;
      return -5;
    case -4: 
      if (!paramDecoderInputBuffer.isEndOfStream())
      {
        if (paramDecoderInputBuffer.timeUs < paramLong) {
          paramDecoderInputBuffer.addFlag(Integer.MIN_VALUE);
        }
        if (paramDecoderInputBuffer.isEncrypted()) {
          readEncryptionData(paramDecoderInputBuffer, this.extrasHolder);
        }
        paramDecoderInputBuffer.ensureSpaceForWrite(this.extrasHolder.size);
        readData(this.extrasHolder.offset, paramDecoderInputBuffer.data, this.extrasHolder.size);
        dropDownstreamTo(this.extrasHolder.nextOffset);
      }
      return -4;
    }
    return -3;
  }
  
  public void reset(boolean paramBoolean)
  {
    AtomicInteger localAtomicInteger = this.state;
    if (paramBoolean) {}
    for (int i = 0;; i = 2)
    {
      i = localAtomicInteger.getAndSet(i);
      clearSampleData();
      this.infoQueue.resetLargestParsedTimestamps();
      if (i == 2) {
        this.downstreamFormat = null;
      }
      return;
    }
  }
  
  public int sampleData(ExtractorInput paramExtractorInput, int paramInt, boolean paramBoolean)
    throws IOException, InterruptedException
  {
    if (!startWriteOperation())
    {
      int i = paramExtractorInput.skip(paramInt);
      paramInt = i;
      if (i == -1)
      {
        if (paramBoolean) {
          paramInt = -1;
        }
      }
      else {
        return paramInt;
      }
      throw new EOFException();
    }
    try
    {
      paramInt = prepareForAppend(paramInt);
      paramInt = paramExtractorInput.read(this.lastAllocation.data, this.lastAllocation.translateOffset(this.lastAllocationOffset), paramInt);
      if (paramInt == -1)
      {
        if (paramBoolean) {
          return -1;
        }
        throw new EOFException();
      }
    }
    finally
    {
      endWriteOperation();
    }
    this.lastAllocationOffset += paramInt;
    this.totalBytesWritten += paramInt;
    endWriteOperation();
    return paramInt;
  }
  
  public void sampleData(ParsableByteArray paramParsableByteArray, int paramInt)
  {
    int i = paramInt;
    if (!startWriteOperation())
    {
      paramParsableByteArray.skipBytes(paramInt);
      return;
    }
    while (i > 0)
    {
      paramInt = prepareForAppend(i);
      paramParsableByteArray.readBytes(this.lastAllocation.data, this.lastAllocation.translateOffset(this.lastAllocationOffset), paramInt);
      this.lastAllocationOffset += paramInt;
      this.totalBytesWritten += paramInt;
      i -= paramInt;
    }
    endWriteOperation();
  }
  
  public void sampleMetadata(long paramLong, int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte)
  {
    if (this.pendingFormatAdjustment) {
      format(this.lastUnadjustedFormat);
    }
    if (!startWriteOperation())
    {
      this.infoQueue.commitSampleTimestamp(paramLong);
      return;
    }
    try
    {
      if (this.pendingSplice)
      {
        if ((paramInt1 & 0x1) != 0)
        {
          boolean bool = this.infoQueue.attemptSplice(paramLong);
          if (bool) {}
        }
        else
        {
          return;
        }
        this.pendingSplice = false;
      }
      long l1 = this.sampleOffsetUs;
      long l2 = this.totalBytesWritten;
      long l3 = paramInt2;
      long l4 = paramInt3;
      this.infoQueue.commitSample(paramLong + l1, paramInt1, l2 - l3 - l4, paramInt2, paramArrayOfByte);
      return;
    }
    finally
    {
      endWriteOperation();
    }
  }
  
  public void setSampleOffsetUs(long paramLong)
  {
    if (this.sampleOffsetUs != paramLong)
    {
      this.sampleOffsetUs = paramLong;
      this.pendingFormatAdjustment = true;
    }
  }
  
  public void setUpstreamFormatChangeListener(UpstreamFormatChangedListener paramUpstreamFormatChangedListener)
  {
    this.upstreamFormatChangeListener = paramUpstreamFormatChangedListener;
  }
  
  public void skipAll()
  {
    long l = this.infoQueue.skipAll();
    if (l != -1L) {
      dropDownstreamTo(l);
    }
  }
  
  public boolean skipToKeyframeBefore(long paramLong, boolean paramBoolean)
  {
    paramLong = this.infoQueue.skipToKeyframeBefore(paramLong, paramBoolean);
    if (paramLong == -1L) {
      return false;
    }
    dropDownstreamTo(paramLong);
    return true;
  }
  
  public void sourceId(int paramInt)
  {
    this.infoQueue.sourceId(paramInt);
  }
  
  public void splice()
  {
    this.pendingSplice = true;
  }
  
  private static final class BufferExtrasHolder
  {
    public byte[] encryptionKeyId;
    public long nextOffset;
    public long offset;
    public int size;
  }
  
  private static final class InfoQueue
  {
    private static final int SAMPLE_CAPACITY_INCREMENT = 1000;
    private int absoluteReadIndex;
    private int capacity = 1000;
    private byte[][] encryptionKeys = new byte[this.capacity][];
    private int[] flags = new int[this.capacity];
    private Format[] formats = new Format[this.capacity];
    private long largestDequeuedTimestampUs = Long.MIN_VALUE;
    private long largestQueuedTimestampUs = Long.MIN_VALUE;
    private long[] offsets = new long[this.capacity];
    private int queueSize;
    private int relativeReadIndex;
    private int relativeWriteIndex;
    private int[] sizes = new int[this.capacity];
    private int[] sourceIds = new int[this.capacity];
    private long[] timesUs = new long[this.capacity];
    private Format upstreamFormat;
    private boolean upstreamFormatRequired = true;
    private boolean upstreamKeyframeRequired = true;
    private int upstreamSourceId;
    
    /* Error */
    public boolean attemptSplice(long paramLong)
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: getfield 63	org/telegram/messenger/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:largestDequeuedTimestampUs	J
      //   6: lstore 4
      //   8: lload 4
      //   10: lload_1
      //   11: lcmp
      //   12: iflt +11 -> 23
      //   15: iconst_0
      //   16: istore 6
      //   18: aload_0
      //   19: monitorexit
      //   20: iload 6
      //   22: ireturn
      //   23: aload_0
      //   24: getfield 74	org/telegram/messenger/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:queueSize	I
      //   27: istore_3
      //   28: iload_3
      //   29: ifle +33 -> 62
      //   32: aload_0
      //   33: getfield 47	org/telegram/messenger/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:timesUs	[J
      //   36: aload_0
      //   37: getfield 76	org/telegram/messenger/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:relativeReadIndex	I
      //   40: iload_3
      //   41: iadd
      //   42: iconst_1
      //   43: isub
      //   44: aload_0
      //   45: getfield 41	org/telegram/messenger/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:capacity	I
      //   48: irem
      //   49: laload
      //   50: lload_1
      //   51: lcmp
      //   52: iflt +10 -> 62
      //   55: iload_3
      //   56: iconst_1
      //   57: isub
      //   58: istore_3
      //   59: goto -31 -> 28
      //   62: aload_0
      //   63: aload_0
      //   64: getfield 78	org/telegram/messenger/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:absoluteReadIndex	I
      //   67: iload_3
      //   68: iadd
      //   69: invokevirtual 82	org/telegram/messenger/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:discardUpstreamSamples	(I)J
      //   72: pop2
      //   73: iconst_1
      //   74: istore 6
      //   76: goto -58 -> 18
      //   79: astore 7
      //   81: aload_0
      //   82: monitorexit
      //   83: aload 7
      //   85: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	86	0	this	InfoQueue
      //   0	86	1	paramLong	long
      //   27	42	3	i	int
      //   6	3	4	l	long
      //   16	59	6	bool	boolean
      //   79	5	7	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   2	8	79	finally
      //   23	28	79	finally
      //   32	55	79	finally
      //   62	73	79	finally
    }
    
    public void clearSampleData()
    {
      this.absoluteReadIndex = 0;
      this.relativeReadIndex = 0;
      this.relativeWriteIndex = 0;
      this.queueSize = 0;
      this.upstreamKeyframeRequired = true;
    }
    
    public void commitSample(long paramLong1, int paramInt1, long paramLong2, int paramInt2, byte[] paramArrayOfByte)
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
            this.timesUs[this.relativeWriteIndex] = paramLong1;
            this.offsets[this.relativeWriteIndex] = paramLong2;
            this.sizes[this.relativeWriteIndex] = paramInt2;
            this.flags[this.relativeWriteIndex] = paramInt1;
            this.encryptionKeys[this.relativeWriteIndex] = paramArrayOfByte;
            this.formats[this.relativeWriteIndex] = this.upstreamFormat;
            this.sourceIds[this.relativeWriteIndex] = this.upstreamSourceId;
            this.queueSize += 1;
            if (this.queueSize != this.capacity) {
              break label504;
            }
            paramInt1 = this.capacity + 1000;
            paramArrayOfByte = new int[paramInt1];
            long[] arrayOfLong1 = new long[paramInt1];
            long[] arrayOfLong2 = new long[paramInt1];
            int[] arrayOfInt1 = new int[paramInt1];
            int[] arrayOfInt2 = new int[paramInt1];
            byte[][] arrayOfByte = new byte[paramInt1][];
            Format[] arrayOfFormat = new Format[paramInt1];
            paramInt2 = this.capacity - this.relativeReadIndex;
            System.arraycopy(this.offsets, this.relativeReadIndex, arrayOfLong1, 0, paramInt2);
            System.arraycopy(this.timesUs, this.relativeReadIndex, arrayOfLong2, 0, paramInt2);
            System.arraycopy(this.flags, this.relativeReadIndex, arrayOfInt1, 0, paramInt2);
            System.arraycopy(this.sizes, this.relativeReadIndex, arrayOfInt2, 0, paramInt2);
            System.arraycopy(this.encryptionKeys, this.relativeReadIndex, arrayOfByte, 0, paramInt2);
            System.arraycopy(this.formats, this.relativeReadIndex, arrayOfFormat, 0, paramInt2);
            System.arraycopy(this.sourceIds, this.relativeReadIndex, paramArrayOfByte, 0, paramInt2);
            int i = this.relativeReadIndex;
            System.arraycopy(this.offsets, 0, arrayOfLong1, paramInt2, i);
            System.arraycopy(this.timesUs, 0, arrayOfLong2, paramInt2, i);
            System.arraycopy(this.flags, 0, arrayOfInt1, paramInt2, i);
            System.arraycopy(this.sizes, 0, arrayOfInt2, paramInt2, i);
            System.arraycopy(this.encryptionKeys, 0, arrayOfByte, paramInt2, i);
            System.arraycopy(this.formats, 0, arrayOfFormat, paramInt2, i);
            System.arraycopy(this.sourceIds, 0, paramArrayOfByte, paramInt2, i);
            this.offsets = arrayOfLong1;
            this.timesUs = arrayOfLong2;
            this.flags = arrayOfInt1;
            this.sizes = arrayOfInt2;
            this.encryptionKeys = arrayOfByte;
            this.formats = arrayOfFormat;
            this.sourceIds = paramArrayOfByte;
            this.relativeReadIndex = 0;
            this.relativeWriteIndex = this.capacity;
            this.queueSize = this.capacity;
            this.capacity = paramInt1;
            continue;
          }
          bool = false;
        }
        finally {}
        continue;
        label504:
        this.relativeWriteIndex += 1;
        if (this.relativeWriteIndex == this.capacity) {
          this.relativeWriteIndex = 0;
        }
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
    
    public long discardUpstreamSamples(int paramInt)
    {
      paramInt = getWriteIndex() - paramInt;
      if ((paramInt >= 0) && (paramInt <= this.queueSize)) {}
      for (boolean bool = true;; bool = false)
      {
        Assertions.checkArgument(bool);
        if (paramInt != 0) {
          break label82;
        }
        if (this.absoluteReadIndex != 0) {
          break;
        }
        return 0L;
      }
      if (this.relativeWriteIndex == 0) {}
      for (paramInt = this.capacity;; paramInt = this.relativeWriteIndex)
      {
        paramInt -= 1;
        return this.offsets[paramInt] + this.sizes[paramInt];
      }
      label82:
      this.queueSize -= paramInt;
      this.relativeWriteIndex = ((this.relativeWriteIndex + this.capacity - paramInt) % this.capacity);
      this.largestQueuedTimestampUs = Long.MIN_VALUE;
      paramInt = this.queueSize - 1;
      for (;;)
      {
        if (paramInt >= 0)
        {
          int i = (this.relativeReadIndex + paramInt) % this.capacity;
          this.largestQueuedTimestampUs = Math.max(this.largestQueuedTimestampUs, this.timesUs[i]);
          if ((this.flags[i] & 0x1) == 0) {}
        }
        else
        {
          return this.offsets[this.relativeWriteIndex];
        }
        paramInt -= 1;
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
    
    public long getLargestQueuedTimestampUs()
    {
      try
      {
        long l = Math.max(this.largestDequeuedTimestampUs, this.largestQueuedTimestampUs);
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
      return this.absoluteReadIndex;
    }
    
    /* Error */
    public Format getUpstreamFormat()
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: getfield 67	org/telegram/messenger/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:upstreamFormatRequired	Z
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
      //   18: getfield 99	org/telegram/messenger/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:upstreamFormat	Lorg/telegram/messenger/exoplayer2/Format;
      //   21: astore_2
      //   22: goto -9 -> 13
      //   25: astore_2
      //   26: aload_0
      //   27: monitorexit
      //   28: aload_2
      //   29: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	30	0	this	InfoQueue
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
      return this.absoluteReadIndex + this.queueSize;
    }
    
    /* Error */
    public boolean isEmpty()
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: getfield 74	org/telegram/messenger/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:queueSize	I
      //   6: istore_1
      //   7: iload_1
      //   8: ifne +9 -> 17
      //   11: iconst_1
      //   12: istore_2
      //   13: aload_0
      //   14: monitorexit
      //   15: iload_2
      //   16: ireturn
      //   17: iconst_0
      //   18: istore_2
      //   19: goto -6 -> 13
      //   22: astore_3
      //   23: aload_0
      //   24: monitorexit
      //   25: aload_3
      //   26: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	27	0	this	InfoQueue
      //   6	2	1	i	int
      //   12	7	2	bool	boolean
      //   22	4	3	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   2	7	22	finally
    }
    
    public int peekSourceId()
    {
      if (this.queueSize == 0) {
        return this.upstreamSourceId;
      }
      return this.sourceIds[this.relativeReadIndex];
    }
    
    public int readData(FormatHolder paramFormatHolder, DecoderInputBuffer paramDecoderInputBuffer, boolean paramBoolean1, boolean paramBoolean2, Format paramFormat, DefaultTrackOutput.BufferExtrasHolder paramBufferExtrasHolder)
    {
      int i = -4;
      for (;;)
      {
        long l;
        try
        {
          if (this.queueSize == 0)
          {
            if (paramBoolean2)
            {
              paramDecoderInputBuffer.setFlags(4);
              return i;
            }
            if ((this.upstreamFormat == null) || ((!paramBoolean1) && (this.upstreamFormat == paramFormat))) {
              break label302;
            }
            paramFormatHolder.format = this.upstreamFormat;
            i = -5;
            continue;
          }
          if ((paramBoolean1) || (this.formats[this.relativeReadIndex] != paramFormat))
          {
            paramFormatHolder.format = this.formats[this.relativeReadIndex];
            i = -5;
            continue;
          }
          if (paramDecoderInputBuffer.isFlagsOnly())
          {
            i = -3;
            continue;
          }
          paramDecoderInputBuffer.timeUs = this.timesUs[this.relativeReadIndex];
          paramDecoderInputBuffer.setFlags(this.flags[this.relativeReadIndex]);
          paramBufferExtrasHolder.size = this.sizes[this.relativeReadIndex];
          paramBufferExtrasHolder.offset = this.offsets[this.relativeReadIndex];
          paramBufferExtrasHolder.encryptionKeyId = this.encryptionKeys[this.relativeReadIndex];
          this.largestDequeuedTimestampUs = Math.max(this.largestDequeuedTimestampUs, paramDecoderInputBuffer.timeUs);
          this.queueSize -= 1;
          this.relativeReadIndex += 1;
          this.absoluteReadIndex += 1;
          if (this.relativeReadIndex == this.capacity) {
            this.relativeReadIndex = 0;
          }
          if (this.queueSize > 0)
          {
            l = this.offsets[this.relativeReadIndex];
            paramBufferExtrasHolder.nextOffset = l;
            continue;
          }
          l = paramBufferExtrasHolder.offset;
        }
        finally {}
        int j = paramBufferExtrasHolder.size;
        l += j;
        continue;
        label302:
        i = -3;
      }
    }
    
    public void resetLargestParsedTimestamps()
    {
      this.largestDequeuedTimestampUs = Long.MIN_VALUE;
      this.largestQueuedTimestampUs = Long.MIN_VALUE;
    }
    
    /* Error */
    public long skipAll()
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: getfield 74	org/telegram/messenger/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:queueSize	I
      //   6: istore_1
      //   7: iload_1
      //   8: ifne +11 -> 19
      //   11: ldc2_w 171
      //   14: lstore_2
      //   15: aload_0
      //   16: monitorexit
      //   17: lload_2
      //   18: lreturn
      //   19: aload_0
      //   20: getfield 76	org/telegram/messenger/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:relativeReadIndex	I
      //   23: aload_0
      //   24: getfield 74	org/telegram/messenger/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:queueSize	I
      //   27: iadd
      //   28: iconst_1
      //   29: isub
      //   30: aload_0
      //   31: getfield 41	org/telegram/messenger/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:capacity	I
      //   34: irem
      //   35: istore_1
      //   36: aload_0
      //   37: aload_0
      //   38: getfield 76	org/telegram/messenger/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:relativeReadIndex	I
      //   41: aload_0
      //   42: getfield 74	org/telegram/messenger/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:queueSize	I
      //   45: iadd
      //   46: aload_0
      //   47: getfield 41	org/telegram/messenger/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:capacity	I
      //   50: irem
      //   51: putfield 76	org/telegram/messenger/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:relativeReadIndex	I
      //   54: aload_0
      //   55: aload_0
      //   56: getfield 78	org/telegram/messenger/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:absoluteReadIndex	I
      //   59: aload_0
      //   60: getfield 74	org/telegram/messenger/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:queueSize	I
      //   63: iadd
      //   64: putfield 78	org/telegram/messenger/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:absoluteReadIndex	I
      //   67: aload_0
      //   68: iconst_0
      //   69: putfield 74	org/telegram/messenger/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:queueSize	I
      //   72: aload_0
      //   73: getfield 45	org/telegram/messenger/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:offsets	[J
      //   76: iload_1
      //   77: laload
      //   78: lstore_2
      //   79: aload_0
      //   80: getfield 51	org/telegram/messenger/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:sizes	[I
      //   83: iload_1
      //   84: iaload
      //   85: istore_1
      //   86: lload_2
      //   87: iload_1
      //   88: i2l
      //   89: ladd
      //   90: lstore_2
      //   91: goto -76 -> 15
      //   94: astore 4
      //   96: aload_0
      //   97: monitorexit
      //   98: aload 4
      //   100: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	101	0	this	InfoQueue
      //   6	82	1	i	int
      //   14	77	2	l	long
      //   94	5	4	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   2	7	94	finally
      //   19	86	94	finally
    }
    
    /* Error */
    public long skipToKeyframeBefore(long paramLong, boolean paramBoolean)
    {
      // Byte code:
      //   0: ldc2_w 171
      //   3: lstore 9
      //   5: aload_0
      //   6: monitorenter
      //   7: lload 9
      //   9: lstore 7
      //   11: aload_0
      //   12: getfield 74	org/telegram/messenger/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:queueSize	I
      //   15: ifeq +25 -> 40
      //   18: aload_0
      //   19: getfield 47	org/telegram/messenger/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:timesUs	[J
      //   22: aload_0
      //   23: getfield 76	org/telegram/messenger/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:relativeReadIndex	I
      //   26: laload
      //   27: lstore 7
      //   29: lload_1
      //   30: lload 7
      //   32: lcmp
      //   33: ifge +12 -> 45
      //   36: lload 9
      //   38: lstore 7
      //   40: aload_0
      //   41: monitorexit
      //   42: lload 7
      //   44: lreturn
      //   45: lload_1
      //   46: aload_0
      //   47: getfield 65	org/telegram/messenger/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:largestQueuedTimestampUs	J
      //   50: lcmp
      //   51: ifle +11 -> 62
      //   54: lload 9
      //   56: lstore 7
      //   58: iload_3
      //   59: ifeq -19 -> 40
      //   62: iconst_0
      //   63: istore 4
      //   65: iconst_m1
      //   66: istore 6
      //   68: aload_0
      //   69: getfield 76	org/telegram/messenger/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:relativeReadIndex	I
      //   72: istore 5
      //   74: iload 5
      //   76: aload_0
      //   77: getfield 85	org/telegram/messenger/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:relativeWriteIndex	I
      //   80: if_icmpeq +15 -> 95
      //   83: aload_0
      //   84: getfield 47	org/telegram/messenger/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:timesUs	[J
      //   87: iload 5
      //   89: laload
      //   90: lload_1
      //   91: lcmp
      //   92: ifle +65 -> 157
      //   95: lload 9
      //   97: lstore 7
      //   99: iload 6
      //   101: iconst_m1
      //   102: if_icmpeq -62 -> 40
      //   105: aload_0
      //   106: aload_0
      //   107: getfield 76	org/telegram/messenger/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:relativeReadIndex	I
      //   110: iload 6
      //   112: iadd
      //   113: aload_0
      //   114: getfield 41	org/telegram/messenger/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:capacity	I
      //   117: irem
      //   118: putfield 76	org/telegram/messenger/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:relativeReadIndex	I
      //   121: aload_0
      //   122: aload_0
      //   123: getfield 78	org/telegram/messenger/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:absoluteReadIndex	I
      //   126: iload 6
      //   128: iadd
      //   129: putfield 78	org/telegram/messenger/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:absoluteReadIndex	I
      //   132: aload_0
      //   133: aload_0
      //   134: getfield 74	org/telegram/messenger/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:queueSize	I
      //   137: iload 6
      //   139: isub
      //   140: putfield 74	org/telegram/messenger/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:queueSize	I
      //   143: aload_0
      //   144: getfield 45	org/telegram/messenger/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:offsets	[J
      //   147: aload_0
      //   148: getfield 76	org/telegram/messenger/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:relativeReadIndex	I
      //   151: laload
      //   152: lstore 7
      //   154: goto -114 -> 40
      //   157: aload_0
      //   158: getfield 49	org/telegram/messenger/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:flags	[I
      //   161: iload 5
      //   163: iaload
      //   164: iconst_1
      //   165: iand
      //   166: ifeq +7 -> 173
      //   169: iload 4
      //   171: istore 6
      //   173: iload 5
      //   175: iconst_1
      //   176: iadd
      //   177: aload_0
      //   178: getfield 41	org/telegram/messenger/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:capacity	I
      //   181: irem
      //   182: istore 5
      //   184: iload 4
      //   186: iconst_1
      //   187: iadd
      //   188: istore 4
      //   190: goto -116 -> 74
      //   193: astore 11
      //   195: aload_0
      //   196: monitorexit
      //   197: aload 11
      //   199: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	200	0	this	InfoQueue
      //   0	200	1	paramLong	long
      //   0	200	3	paramBoolean	boolean
      //   63	126	4	i	int
      //   72	111	5	j	int
      //   66	106	6	k	int
      //   9	144	7	l1	long
      //   3	93	9	l2	long
      //   193	5	11	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   11	29	193	finally
      //   45	54	193	finally
      //   68	74	193	finally
      //   74	95	193	finally
      //   105	154	193	finally
      //   157	169	193	finally
      //   173	184	193	finally
    }
    
    public void sourceId(int paramInt)
    {
      this.upstreamSourceId = paramInt;
    }
  }
  
  public static abstract interface UpstreamFormatChangedListener
  {
    public abstract void onUpstreamFormatChanged(Format paramFormat);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/extractor/DefaultTrackOutput.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
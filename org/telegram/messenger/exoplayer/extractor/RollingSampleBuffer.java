package org.telegram.messenger.exoplayer.extractor;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingDeque;
import org.telegram.messenger.exoplayer.CryptoInfo;
import org.telegram.messenger.exoplayer.SampleHolder;
import org.telegram.messenger.exoplayer.upstream.Allocation;
import org.telegram.messenger.exoplayer.upstream.Allocator;
import org.telegram.messenger.exoplayer.upstream.DataSource;
import org.telegram.messenger.exoplayer.util.Assertions;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;

final class RollingSampleBuffer
{
  private static final int INITIAL_SCRATCH_SIZE = 32;
  private final int allocationLength;
  private final Allocator allocator;
  private final LinkedBlockingDeque<Allocation> dataQueue;
  private final SampleExtrasHolder extrasHolder;
  private final InfoQueue infoQueue;
  private Allocation lastAllocation;
  private int lastAllocationOffset;
  private final ParsableByteArray scratch;
  private long totalBytesDropped;
  private long totalBytesWritten;
  
  public RollingSampleBuffer(Allocator paramAllocator)
  {
    this.allocator = paramAllocator;
    this.allocationLength = paramAllocator.getIndividualAllocationLength();
    this.infoQueue = new InfoQueue();
    this.dataQueue = new LinkedBlockingDeque();
    this.extrasHolder = new SampleExtrasHolder(null);
    this.scratch = new ParsableByteArray(32);
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
  
  private static void ensureCapacity(ParsableByteArray paramParsableByteArray, int paramInt)
  {
    if (paramParsableByteArray.limit() < paramInt) {
      paramParsableByteArray.reset(new byte[paramInt], paramInt);
    }
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
  
  private void readEncryptionData(SampleHolder paramSampleHolder, SampleExtrasHolder paramSampleExtrasHolder)
  {
    long l1 = paramSampleExtrasHolder.offset;
    readData(l1, this.scratch.data, 1);
    l1 += 1L;
    int j = this.scratch.data[0];
    if ((j & 0x80) != 0)
    {
      i = 1;
      j &= 0x7F;
      if (paramSampleHolder.cryptoInfo.iv == null) {
        paramSampleHolder.cryptoInfo.iv = new byte[16];
      }
      readData(l1, paramSampleHolder.cryptoInfo.iv, j);
      l1 += j;
      if (i == 0) {
        break label299;
      }
      readData(l1, this.scratch.data, 2);
      l1 += 2L;
      this.scratch.setPosition(0);
    }
    Object localObject2;
    Object localObject1;
    label299:
    for (j = this.scratch.readUnsignedShort();; j = 1)
    {
      localObject2 = paramSampleHolder.cryptoInfo.numBytesOfClearData;
      if (localObject2 != null)
      {
        localObject1 = localObject2;
        if (localObject2.length >= j) {}
      }
      else
      {
        localObject1 = new int[j];
      }
      int[] arrayOfInt = paramSampleHolder.cryptoInfo.numBytesOfEncryptedData;
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
        break label305;
      }
      i = j * 6;
      ensureCapacity(this.scratch, i);
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
    label305:
    localObject1[0] = 0;
    localObject2[0] = (paramSampleHolder.size - (int)(l1 - paramSampleExtrasHolder.offset));
    paramSampleHolder.cryptoInfo.set(j, (int[])localObject1, (int[])localObject2, paramSampleExtrasHolder.encryptionKeyId, paramSampleHolder.cryptoInfo.iv, 1);
    int i = (int)(l1 - paramSampleExtrasHolder.offset);
    paramSampleExtrasHolder.offset += i;
    paramSampleHolder.size -= i;
  }
  
  public int appendData(ExtractorInput paramExtractorInput, int paramInt, boolean paramBoolean)
    throws IOException, InterruptedException
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
    this.lastAllocationOffset += paramInt;
    this.totalBytesWritten += paramInt;
    return paramInt;
  }
  
  public int appendData(DataSource paramDataSource, int paramInt, boolean paramBoolean)
    throws IOException
  {
    paramInt = prepareForAppend(paramInt);
    paramInt = paramDataSource.read(this.lastAllocation.data, this.lastAllocation.translateOffset(this.lastAllocationOffset), paramInt);
    if (paramInt == -1)
    {
      if (paramBoolean) {
        return -1;
      }
      throw new EOFException();
    }
    this.lastAllocationOffset += paramInt;
    this.totalBytesWritten += paramInt;
    return paramInt;
  }
  
  public void appendData(ParsableByteArray paramParsableByteArray, int paramInt)
  {
    while (paramInt > 0)
    {
      int i = prepareForAppend(paramInt);
      paramParsableByteArray.readBytes(this.lastAllocation.data, this.lastAllocation.translateOffset(this.lastAllocationOffset), i);
      this.lastAllocationOffset += i;
      this.totalBytesWritten += i;
      paramInt -= i;
    }
  }
  
  public void clear()
  {
    this.infoQueue.clear();
    this.allocator.release((Allocation[])this.dataQueue.toArray(new Allocation[this.dataQueue.size()]));
    this.dataQueue.clear();
    this.totalBytesDropped = 0L;
    this.totalBytesWritten = 0L;
    this.lastAllocation = null;
    this.lastAllocationOffset = this.allocationLength;
  }
  
  public void commitSample(long paramLong1, int paramInt1, long paramLong2, int paramInt2, byte[] paramArrayOfByte)
  {
    this.infoQueue.commitSample(paramLong1, paramInt1, paramLong2, paramInt2, paramArrayOfByte);
  }
  
  public void discardUpstreamSamples(int paramInt)
  {
    this.totalBytesWritten = this.infoQueue.discardUpstreamSamples(paramInt);
    dropUpstreamFrom(this.totalBytesWritten);
  }
  
  public int getReadIndex()
  {
    return this.infoQueue.getReadIndex();
  }
  
  public int getWriteIndex()
  {
    return this.infoQueue.getWriteIndex();
  }
  
  public long getWritePosition()
  {
    return this.totalBytesWritten;
  }
  
  public boolean peekSample(SampleHolder paramSampleHolder)
  {
    return this.infoQueue.peekSample(paramSampleHolder, this.extrasHolder);
  }
  
  public boolean readSample(SampleHolder paramSampleHolder)
  {
    if (!this.infoQueue.peekSample(paramSampleHolder, this.extrasHolder)) {
      return false;
    }
    if (paramSampleHolder.isEncrypted()) {
      readEncryptionData(paramSampleHolder, this.extrasHolder);
    }
    paramSampleHolder.ensureSpaceForWrite(paramSampleHolder.size);
    readData(this.extrasHolder.offset, paramSampleHolder.data, paramSampleHolder.size);
    dropDownstreamTo(this.infoQueue.moveToNextSample());
    return true;
  }
  
  public void skipSample()
  {
    dropDownstreamTo(this.infoQueue.moveToNextSample());
  }
  
  public boolean skipToKeyframeBefore(long paramLong)
  {
    paramLong = this.infoQueue.skipToKeyframeBefore(paramLong);
    if (paramLong == -1L) {
      return false;
    }
    dropDownstreamTo(paramLong);
    return true;
  }
  
  private static final class InfoQueue
  {
    private static final int SAMPLE_CAPACITY_INCREMENT = 1000;
    private int absoluteReadIndex;
    private int capacity = 1000;
    private byte[][] encryptionKeys = new byte[this.capacity][];
    private int[] flags = new int[this.capacity];
    private long[] offsets = new long[this.capacity];
    private int queueSize;
    private int relativeReadIndex;
    private int relativeWriteIndex;
    private int[] sizes = new int[this.capacity];
    private long[] timesUs = new long[this.capacity];
    
    public void clear()
    {
      this.absoluteReadIndex = 0;
      this.relativeReadIndex = 0;
      this.relativeWriteIndex = 0;
      this.queueSize = 0;
    }
    
    /* Error */
    public void commitSample(long paramLong1, int paramInt1, long paramLong2, int paramInt2, byte[] paramArrayOfByte)
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: getfield 33	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:timesUs	[J
      //   6: aload_0
      //   7: getfield 49	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:relativeWriteIndex	I
      //   10: lload_1
      //   11: lastore
      //   12: aload_0
      //   13: getfield 31	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:offsets	[J
      //   16: aload_0
      //   17: getfield 49	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:relativeWriteIndex	I
      //   20: lload 4
      //   22: lastore
      //   23: aload_0
      //   24: getfield 37	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:sizes	[I
      //   27: aload_0
      //   28: getfield 49	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:relativeWriteIndex	I
      //   31: iload 6
      //   33: iastore
      //   34: aload_0
      //   35: getfield 35	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:flags	[I
      //   38: aload_0
      //   39: getfield 49	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:relativeWriteIndex	I
      //   42: iload_3
      //   43: iastore
      //   44: aload_0
      //   45: getfield 41	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:encryptionKeys	[[B
      //   48: aload_0
      //   49: getfield 49	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:relativeWriteIndex	I
      //   52: aload 7
      //   54: aastore
      //   55: aload_0
      //   56: aload_0
      //   57: getfield 51	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:queueSize	I
      //   60: iconst_1
      //   61: iadd
      //   62: putfield 51	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:queueSize	I
      //   65: aload_0
      //   66: getfield 51	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:queueSize	I
      //   69: aload_0
      //   70: getfield 29	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:capacity	I
      //   73: if_icmpne +264 -> 337
      //   76: aload_0
      //   77: getfield 29	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:capacity	I
      //   80: sipush 1000
      //   83: iadd
      //   84: istore_3
      //   85: iload_3
      //   86: newarray <illegal type>
      //   88: astore 7
      //   90: iload_3
      //   91: newarray <illegal type>
      //   93: astore 9
      //   95: iload_3
      //   96: newarray <illegal type>
      //   98: astore 10
      //   100: iload_3
      //   101: newarray <illegal type>
      //   103: astore 11
      //   105: iload_3
      //   106: anewarray 39	[B
      //   109: astore 12
      //   111: aload_0
      //   112: getfield 29	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:capacity	I
      //   115: aload_0
      //   116: getfield 47	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:relativeReadIndex	I
      //   119: isub
      //   120: istore 6
      //   122: aload_0
      //   123: getfield 31	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:offsets	[J
      //   126: aload_0
      //   127: getfield 47	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:relativeReadIndex	I
      //   130: aload 7
      //   132: iconst_0
      //   133: iload 6
      //   135: invokestatic 59	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
      //   138: aload_0
      //   139: getfield 33	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:timesUs	[J
      //   142: aload_0
      //   143: getfield 47	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:relativeReadIndex	I
      //   146: aload 9
      //   148: iconst_0
      //   149: iload 6
      //   151: invokestatic 59	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
      //   154: aload_0
      //   155: getfield 35	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:flags	[I
      //   158: aload_0
      //   159: getfield 47	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:relativeReadIndex	I
      //   162: aload 10
      //   164: iconst_0
      //   165: iload 6
      //   167: invokestatic 59	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
      //   170: aload_0
      //   171: getfield 37	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:sizes	[I
      //   174: aload_0
      //   175: getfield 47	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:relativeReadIndex	I
      //   178: aload 11
      //   180: iconst_0
      //   181: iload 6
      //   183: invokestatic 59	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
      //   186: aload_0
      //   187: getfield 41	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:encryptionKeys	[[B
      //   190: aload_0
      //   191: getfield 47	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:relativeReadIndex	I
      //   194: aload 12
      //   196: iconst_0
      //   197: iload 6
      //   199: invokestatic 59	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
      //   202: aload_0
      //   203: getfield 47	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:relativeReadIndex	I
      //   206: istore 8
      //   208: aload_0
      //   209: getfield 31	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:offsets	[J
      //   212: iconst_0
      //   213: aload 7
      //   215: iload 6
      //   217: iload 8
      //   219: invokestatic 59	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
      //   222: aload_0
      //   223: getfield 33	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:timesUs	[J
      //   226: iconst_0
      //   227: aload 9
      //   229: iload 6
      //   231: iload 8
      //   233: invokestatic 59	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
      //   236: aload_0
      //   237: getfield 35	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:flags	[I
      //   240: iconst_0
      //   241: aload 10
      //   243: iload 6
      //   245: iload 8
      //   247: invokestatic 59	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
      //   250: aload_0
      //   251: getfield 37	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:sizes	[I
      //   254: iconst_0
      //   255: aload 11
      //   257: iload 6
      //   259: iload 8
      //   261: invokestatic 59	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
      //   264: aload_0
      //   265: getfield 41	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:encryptionKeys	[[B
      //   268: iconst_0
      //   269: aload 12
      //   271: iload 6
      //   273: iload 8
      //   275: invokestatic 59	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
      //   278: aload_0
      //   279: aload 7
      //   281: putfield 31	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:offsets	[J
      //   284: aload_0
      //   285: aload 9
      //   287: putfield 33	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:timesUs	[J
      //   290: aload_0
      //   291: aload 10
      //   293: putfield 35	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:flags	[I
      //   296: aload_0
      //   297: aload 11
      //   299: putfield 37	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:sizes	[I
      //   302: aload_0
      //   303: aload 12
      //   305: putfield 41	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:encryptionKeys	[[B
      //   308: aload_0
      //   309: iconst_0
      //   310: putfield 47	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:relativeReadIndex	I
      //   313: aload_0
      //   314: aload_0
      //   315: getfield 29	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:capacity	I
      //   318: putfield 49	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:relativeWriteIndex	I
      //   321: aload_0
      //   322: aload_0
      //   323: getfield 29	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:capacity	I
      //   326: putfield 51	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:queueSize	I
      //   329: aload_0
      //   330: iload_3
      //   331: putfield 29	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:capacity	I
      //   334: aload_0
      //   335: monitorexit
      //   336: return
      //   337: aload_0
      //   338: aload_0
      //   339: getfield 49	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:relativeWriteIndex	I
      //   342: iconst_1
      //   343: iadd
      //   344: putfield 49	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:relativeWriteIndex	I
      //   347: aload_0
      //   348: getfield 49	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:relativeWriteIndex	I
      //   351: aload_0
      //   352: getfield 29	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:capacity	I
      //   355: if_icmpne -21 -> 334
      //   358: aload_0
      //   359: iconst_0
      //   360: putfield 49	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:relativeWriteIndex	I
      //   363: goto -29 -> 334
      //   366: astore 7
      //   368: aload_0
      //   369: monitorexit
      //   370: aload 7
      //   372: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	373	0	this	InfoQueue
      //   0	373	1	paramLong1	long
      //   0	373	3	paramInt1	int
      //   0	373	4	paramLong2	long
      //   0	373	6	paramInt2	int
      //   0	373	7	paramArrayOfByte	byte[]
      //   206	68	8	i	int
      //   93	193	9	arrayOfLong	long[]
      //   98	194	10	arrayOfInt1	int[]
      //   103	195	11	arrayOfInt2	int[]
      //   109	195	12	arrayOfByte	byte[][]
      // Exception table:
      //   from	to	target	type
      //   2	334	366	finally
      //   337	363	366	finally
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
      return this.offsets[this.relativeWriteIndex];
    }
    
    public int getReadIndex()
    {
      return this.absoluteReadIndex;
    }
    
    public int getWriteIndex()
    {
      return this.absoluteReadIndex + this.queueSize;
    }
    
    /* Error */
    public long moveToNextSample()
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: aload_0
      //   4: getfield 51	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:queueSize	I
      //   7: iconst_1
      //   8: isub
      //   9: putfield 51	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:queueSize	I
      //   12: aload_0
      //   13: getfield 47	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:relativeReadIndex	I
      //   16: istore_1
      //   17: aload_0
      //   18: iload_1
      //   19: iconst_1
      //   20: iadd
      //   21: putfield 47	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:relativeReadIndex	I
      //   24: aload_0
      //   25: aload_0
      //   26: getfield 45	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:absoluteReadIndex	I
      //   29: iconst_1
      //   30: iadd
      //   31: putfield 45	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:absoluteReadIndex	I
      //   34: aload_0
      //   35: getfield 47	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:relativeReadIndex	I
      //   38: aload_0
      //   39: getfield 29	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:capacity	I
      //   42: if_icmpne +8 -> 50
      //   45: aload_0
      //   46: iconst_0
      //   47: putfield 47	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:relativeReadIndex	I
      //   50: aload_0
      //   51: getfield 51	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:queueSize	I
      //   54: ifle +17 -> 71
      //   57: aload_0
      //   58: getfield 31	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:offsets	[J
      //   61: aload_0
      //   62: getfield 47	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:relativeReadIndex	I
      //   65: laload
      //   66: lstore_2
      //   67: aload_0
      //   68: monitorexit
      //   69: lload_2
      //   70: lreturn
      //   71: aload_0
      //   72: getfield 37	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:sizes	[I
      //   75: iload_1
      //   76: iaload
      //   77: i2l
      //   78: lstore_2
      //   79: aload_0
      //   80: getfield 31	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:offsets	[J
      //   83: iload_1
      //   84: laload
      //   85: lstore 4
      //   87: lload_2
      //   88: lload 4
      //   90: ladd
      //   91: lstore_2
      //   92: goto -25 -> 67
      //   95: astore 6
      //   97: aload_0
      //   98: monitorexit
      //   99: aload 6
      //   101: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	102	0	this	InfoQueue
      //   16	68	1	i	int
      //   66	26	2	l1	long
      //   85	4	4	l2	long
      //   95	5	6	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   2	50	95	finally
      //   50	67	95	finally
      //   71	87	95	finally
    }
    
    /* Error */
    public boolean peekSample(SampleHolder paramSampleHolder, RollingSampleBuffer.SampleExtrasHolder paramSampleExtrasHolder)
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: getfield 51	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:queueSize	I
      //   6: istore_3
      //   7: iload_3
      //   8: ifne +11 -> 19
      //   11: iconst_0
      //   12: istore 4
      //   14: aload_0
      //   15: monitorexit
      //   16: iload 4
      //   18: ireturn
      //   19: aload_1
      //   20: aload_0
      //   21: getfield 33	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:timesUs	[J
      //   24: aload_0
      //   25: getfield 47	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:relativeReadIndex	I
      //   28: laload
      //   29: putfield 82	org/telegram/messenger/exoplayer/SampleHolder:timeUs	J
      //   32: aload_1
      //   33: aload_0
      //   34: getfield 37	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:sizes	[I
      //   37: aload_0
      //   38: getfield 47	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:relativeReadIndex	I
      //   41: iaload
      //   42: putfield 85	org/telegram/messenger/exoplayer/SampleHolder:size	I
      //   45: aload_1
      //   46: aload_0
      //   47: getfield 35	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:flags	[I
      //   50: aload_0
      //   51: getfield 47	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:relativeReadIndex	I
      //   54: iaload
      //   55: putfield 87	org/telegram/messenger/exoplayer/SampleHolder:flags	I
      //   58: aload_2
      //   59: aload_0
      //   60: getfield 31	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:offsets	[J
      //   63: aload_0
      //   64: getfield 47	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:relativeReadIndex	I
      //   67: laload
      //   68: putfield 92	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$SampleExtrasHolder:offset	J
      //   71: aload_2
      //   72: aload_0
      //   73: getfield 41	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:encryptionKeys	[[B
      //   76: aload_0
      //   77: getfield 47	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:relativeReadIndex	I
      //   80: aaload
      //   81: putfield 95	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$SampleExtrasHolder:encryptionKeyId	[B
      //   84: iconst_1
      //   85: istore 4
      //   87: goto -73 -> 14
      //   90: astore_1
      //   91: aload_0
      //   92: monitorexit
      //   93: aload_1
      //   94: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	95	0	this	InfoQueue
      //   0	95	1	paramSampleHolder	SampleHolder
      //   0	95	2	paramSampleExtrasHolder	RollingSampleBuffer.SampleExtrasHolder
      //   6	2	3	i	int
      //   12	74	4	bool	boolean
      // Exception table:
      //   from	to	target	type
      //   2	7	90	finally
      //   19	84	90	finally
    }
    
    /* Error */
    public long skipToKeyframeBefore(long paramLong)
    {
      // Byte code:
      //   0: ldc2_w 98
      //   3: lstore 8
      //   5: aload_0
      //   6: monitorenter
      //   7: lload 8
      //   9: lstore 6
      //   11: aload_0
      //   12: getfield 51	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:queueSize	I
      //   15: ifeq +25 -> 40
      //   18: aload_0
      //   19: getfield 33	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:timesUs	[J
      //   22: aload_0
      //   23: getfield 47	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:relativeReadIndex	I
      //   26: laload
      //   27: lstore 6
      //   29: lload_1
      //   30: lload 6
      //   32: lcmp
      //   33: ifge +12 -> 45
      //   36: lload 8
      //   38: lstore 6
      //   40: aload_0
      //   41: monitorexit
      //   42: lload 6
      //   44: lreturn
      //   45: aload_0
      //   46: getfield 49	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:relativeWriteIndex	I
      //   49: ifne +119 -> 168
      //   52: aload_0
      //   53: getfield 29	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:capacity	I
      //   56: istore_3
      //   57: lload 8
      //   59: lstore 6
      //   61: lload_1
      //   62: aload_0
      //   63: getfield 33	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:timesUs	[J
      //   66: iload_3
      //   67: iconst_1
      //   68: isub
      //   69: laload
      //   70: lcmp
      //   71: ifgt -31 -> 40
      //   74: iconst_0
      //   75: istore_3
      //   76: iconst_m1
      //   77: istore 5
      //   79: aload_0
      //   80: getfield 47	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:relativeReadIndex	I
      //   83: istore 4
      //   85: iload 4
      //   87: aload_0
      //   88: getfield 49	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:relativeWriteIndex	I
      //   91: if_icmpeq +15 -> 106
      //   94: aload_0
      //   95: getfield 33	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:timesUs	[J
      //   98: iload 4
      //   100: laload
      //   101: lload_1
      //   102: lcmp
      //   103: ifle +73 -> 176
      //   106: lload 8
      //   108: lstore 6
      //   110: iload 5
      //   112: iconst_m1
      //   113: if_icmpeq -73 -> 40
      //   116: aload_0
      //   117: aload_0
      //   118: getfield 51	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:queueSize	I
      //   121: iload 5
      //   123: isub
      //   124: putfield 51	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:queueSize	I
      //   127: aload_0
      //   128: aload_0
      //   129: getfield 47	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:relativeReadIndex	I
      //   132: iload 5
      //   134: iadd
      //   135: aload_0
      //   136: getfield 29	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:capacity	I
      //   139: irem
      //   140: putfield 47	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:relativeReadIndex	I
      //   143: aload_0
      //   144: aload_0
      //   145: getfield 45	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:absoluteReadIndex	I
      //   148: iload 5
      //   150: iadd
      //   151: putfield 45	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:absoluteReadIndex	I
      //   154: aload_0
      //   155: getfield 31	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:offsets	[J
      //   158: aload_0
      //   159: getfield 47	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:relativeReadIndex	I
      //   162: laload
      //   163: lstore 6
      //   165: goto -125 -> 40
      //   168: aload_0
      //   169: getfield 49	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:relativeWriteIndex	I
      //   172: istore_3
      //   173: goto -116 -> 57
      //   176: aload_0
      //   177: getfield 35	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:flags	[I
      //   180: iload 4
      //   182: iaload
      //   183: iconst_1
      //   184: iand
      //   185: ifeq +6 -> 191
      //   188: iload_3
      //   189: istore 5
      //   191: iload 4
      //   193: iconst_1
      //   194: iadd
      //   195: aload_0
      //   196: getfield 29	org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer$InfoQueue:capacity	I
      //   199: irem
      //   200: istore 4
      //   202: iload_3
      //   203: iconst_1
      //   204: iadd
      //   205: istore_3
      //   206: goto -121 -> 85
      //   209: astore 10
      //   211: aload_0
      //   212: monitorexit
      //   213: aload 10
      //   215: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	216	0	this	InfoQueue
      //   0	216	1	paramLong	long
      //   56	150	3	i	int
      //   83	118	4	j	int
      //   77	113	5	k	int
      //   9	155	6	l1	long
      //   3	104	8	l2	long
      //   209	5	10	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   11	29	209	finally
      //   45	57	209	finally
      //   61	74	209	finally
      //   79	85	209	finally
      //   85	106	209	finally
      //   116	165	209	finally
      //   168	173	209	finally
      //   176	188	209	finally
      //   191	202	209	finally
    }
  }
  
  private static final class SampleExtrasHolder
  {
    public byte[] encryptionKeyId;
    public long offset;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/extractor/RollingSampleBuffer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
package org.telegram.messenger.exoplayer2.source;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.FormatHolder;
import org.telegram.messenger.exoplayer2.decoder.CryptoInfo;
import org.telegram.messenger.exoplayer2.decoder.DecoderInputBuffer;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput.CryptoData;
import org.telegram.messenger.exoplayer2.upstream.Allocation;
import org.telegram.messenger.exoplayer2.upstream.Allocator;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

public final class SampleQueue
  implements TrackOutput
{
  public static final int ADVANCE_FAILED = -1;
  private static final int INITIAL_SCRATCH_SIZE = 32;
  private final int allocationLength;
  private final Allocator allocator;
  private Format downstreamFormat;
  private final SampleMetadataQueue.SampleExtrasHolder extrasHolder;
  private AllocationNode firstAllocationNode;
  private Format lastUnadjustedFormat;
  private final SampleMetadataQueue metadataQueue;
  private boolean pendingFormatAdjustment;
  private boolean pendingSplice;
  private AllocationNode readAllocationNode;
  private long sampleOffsetUs;
  private final ParsableByteArray scratch;
  private long totalBytesWritten;
  private UpstreamFormatChangedListener upstreamFormatChangeListener;
  private AllocationNode writeAllocationNode;
  
  public SampleQueue(Allocator paramAllocator)
  {
    this.allocator = paramAllocator;
    this.allocationLength = paramAllocator.getIndividualAllocationLength();
    this.metadataQueue = new SampleMetadataQueue();
    this.extrasHolder = new SampleMetadataQueue.SampleExtrasHolder();
    this.scratch = new ParsableByteArray(32);
    this.firstAllocationNode = new AllocationNode(0L, this.allocationLength);
    this.readAllocationNode = this.firstAllocationNode;
    this.writeAllocationNode = this.firstAllocationNode;
  }
  
  private void advanceReadTo(long paramLong)
  {
    while (paramLong >= this.readAllocationNode.endPosition) {
      this.readAllocationNode = this.readAllocationNode.next;
    }
  }
  
  private void clearAllocationNodes(AllocationNode paramAllocationNode)
  {
    if (!paramAllocationNode.wasInitialized) {}
    for (;;)
    {
      return;
      if (this.writeAllocationNode.wasInitialized) {}
      Allocation[] arrayOfAllocation;
      for (int i = 1;; i = 0)
      {
        arrayOfAllocation = new Allocation[i + (int)(this.writeAllocationNode.startPosition - paramAllocationNode.startPosition) / this.allocationLength];
        for (i = 0; i < arrayOfAllocation.length; i++)
        {
          arrayOfAllocation[i] = paramAllocationNode.allocation;
          paramAllocationNode = paramAllocationNode.clear();
        }
      }
      this.allocator.release(arrayOfAllocation);
    }
  }
  
  private void discardDownstreamTo(long paramLong)
  {
    if (paramLong == -1L) {}
    for (;;)
    {
      return;
      while (paramLong >= this.firstAllocationNode.endPosition)
      {
        this.allocator.release(this.firstAllocationNode.allocation);
        this.firstAllocationNode = this.firstAllocationNode.clear();
      }
      if (this.readAllocationNode.startPosition < this.firstAllocationNode.startPosition) {
        this.readAllocationNode = this.firstAllocationNode;
      }
    }
  }
  
  private static Format getAdjustedSampleFormat(Format paramFormat, long paramLong)
  {
    Format localFormat;
    if (paramFormat == null) {
      localFormat = null;
    }
    for (;;)
    {
      return localFormat;
      localFormat = paramFormat;
      if (paramLong != 0L)
      {
        localFormat = paramFormat;
        if (paramFormat.subsampleOffsetUs != Long.MAX_VALUE) {
          localFormat = paramFormat.copyWithSubsampleOffsetUs(paramFormat.subsampleOffsetUs + paramLong);
        }
      }
    }
  }
  
  private void postAppend(int paramInt)
  {
    this.totalBytesWritten += paramInt;
    if (this.totalBytesWritten == this.writeAllocationNode.endPosition) {
      this.writeAllocationNode = this.writeAllocationNode.next;
    }
  }
  
  private int preAppend(int paramInt)
  {
    if (!this.writeAllocationNode.wasInitialized) {
      this.writeAllocationNode.initialize(this.allocator.allocate(), new AllocationNode(this.writeAllocationNode.endPosition, this.allocationLength));
    }
    return Math.min(paramInt, (int)(this.writeAllocationNode.endPosition - this.totalBytesWritten));
  }
  
  private void readData(long paramLong, ByteBuffer paramByteBuffer, int paramInt)
  {
    advanceReadTo(paramLong);
    while (paramInt > 0)
    {
      int i = Math.min(paramInt, (int)(this.readAllocationNode.endPosition - paramLong));
      paramByteBuffer.put(this.readAllocationNode.allocation.data, this.readAllocationNode.translateOffset(paramLong), i);
      int j = paramInt - i;
      long l = paramLong + i;
      paramInt = j;
      paramLong = l;
      if (l == this.readAllocationNode.endPosition)
      {
        this.readAllocationNode = this.readAllocationNode.next;
        paramInt = j;
        paramLong = l;
      }
    }
  }
  
  private void readData(long paramLong, byte[] paramArrayOfByte, int paramInt)
  {
    advanceReadTo(paramLong);
    int i = paramInt;
    while (i > 0)
    {
      int j = Math.min(i, (int)(this.readAllocationNode.endPosition - paramLong));
      System.arraycopy(this.readAllocationNode.allocation.data, this.readAllocationNode.translateOffset(paramLong), paramArrayOfByte, paramInt - i, j);
      int k = i - j;
      long l = paramLong + j;
      i = k;
      paramLong = l;
      if (l == this.readAllocationNode.endPosition)
      {
        this.readAllocationNode = this.readAllocationNode.next;
        i = k;
        paramLong = l;
      }
    }
  }
  
  private void readEncryptionData(DecoderInputBuffer paramDecoderInputBuffer, SampleMetadataQueue.SampleExtrasHolder paramSampleExtrasHolder)
  {
    long l1 = paramSampleExtrasHolder.offset;
    this.scratch.reset(1);
    readData(l1, this.scratch.data, 1);
    l1 += 1L;
    int i = this.scratch.data[0];
    int j;
    if ((i & 0x80) != 0)
    {
      j = 1;
      i &= 0x7F;
      if (paramDecoderInputBuffer.cryptoInfo.iv == null) {
        paramDecoderInputBuffer.cryptoInfo.iv = new byte[16];
      }
      readData(l1, paramDecoderInputBuffer.cryptoInfo.iv, i);
      l1 += i;
      if (j == 0) {
        break label305;
      }
      this.scratch.reset(2);
      readData(l1, this.scratch.data, 2);
      l1 += 2L;
    }
    Object localObject1;
    Object localObject2;
    label305:
    for (i = this.scratch.readUnsignedShort();; i = 1)
    {
      localObject1 = paramDecoderInputBuffer.cryptoInfo.numBytesOfClearData;
      if (localObject1 != null)
      {
        localObject2 = localObject1;
        if (localObject1.length >= i) {}
      }
      else
      {
        localObject2 = new int[i];
      }
      localObject3 = paramDecoderInputBuffer.cryptoInfo.numBytesOfEncryptedData;
      if (localObject3 != null)
      {
        localObject1 = localObject3;
        if (localObject3.length >= i) {}
      }
      else
      {
        localObject1 = new int[i];
      }
      if (j == 0) {
        break label311;
      }
      j = i * 6;
      this.scratch.reset(j);
      readData(l1, this.scratch.data, j);
      long l2 = l1 + j;
      this.scratch.setPosition(0);
      for (j = 0;; j++)
      {
        l1 = l2;
        if (j >= i) {
          break;
        }
        localObject2[j] = this.scratch.readUnsignedShort();
        localObject1[j] = this.scratch.readUnsignedIntToInt();
      }
      j = 0;
      break;
    }
    label311:
    localObject2[0] = 0;
    localObject1[0] = (paramSampleExtrasHolder.size - (int)(l1 - paramSampleExtrasHolder.offset));
    Object localObject3 = paramSampleExtrasHolder.cryptoData;
    paramDecoderInputBuffer.cryptoInfo.set(i, (int[])localObject2, (int[])localObject1, ((TrackOutput.CryptoData)localObject3).encryptionKey, paramDecoderInputBuffer.cryptoInfo.iv, ((TrackOutput.CryptoData)localObject3).cryptoMode, ((TrackOutput.CryptoData)localObject3).encryptedBlocks, ((TrackOutput.CryptoData)localObject3).clearBlocks);
    i = (int)(l1 - paramSampleExtrasHolder.offset);
    paramSampleExtrasHolder.offset += i;
    paramSampleExtrasHolder.size -= i;
  }
  
  public int advanceTo(long paramLong, boolean paramBoolean1, boolean paramBoolean2)
  {
    return this.metadataQueue.advanceTo(paramLong, paramBoolean1, paramBoolean2);
  }
  
  public int advanceToEnd()
  {
    return this.metadataQueue.advanceToEnd();
  }
  
  public void discardTo(long paramLong, boolean paramBoolean1, boolean paramBoolean2)
  {
    discardDownstreamTo(this.metadataQueue.discardTo(paramLong, paramBoolean1, paramBoolean2));
  }
  
  public void discardToEnd()
  {
    discardDownstreamTo(this.metadataQueue.discardToEnd());
  }
  
  public void discardToRead()
  {
    discardDownstreamTo(this.metadataQueue.discardToRead());
  }
  
  public void discardUpstreamSamples(int paramInt)
  {
    this.totalBytesWritten = this.metadataQueue.discardUpstreamSamples(paramInt);
    if ((this.totalBytesWritten == 0L) || (this.totalBytesWritten == this.firstAllocationNode.startPosition))
    {
      clearAllocationNodes(this.firstAllocationNode);
      this.firstAllocationNode = new AllocationNode(this.totalBytesWritten, this.allocationLength);
      this.readAllocationNode = this.firstAllocationNode;
      this.writeAllocationNode = this.firstAllocationNode;
      return;
    }
    for (AllocationNode localAllocationNode1 = this.firstAllocationNode; this.totalBytesWritten > localAllocationNode1.endPosition; localAllocationNode1 = localAllocationNode1.next) {}
    AllocationNode localAllocationNode2 = localAllocationNode1.next;
    clearAllocationNodes(localAllocationNode2);
    localAllocationNode1.next = new AllocationNode(localAllocationNode1.endPosition, this.allocationLength);
    if (this.totalBytesWritten == localAllocationNode1.endPosition) {}
    for (AllocationNode localAllocationNode3 = localAllocationNode1.next;; localAllocationNode3 = localAllocationNode1)
    {
      this.writeAllocationNode = localAllocationNode3;
      if (this.readAllocationNode != localAllocationNode2) {
        break;
      }
      this.readAllocationNode = localAllocationNode1.next;
      break;
    }
  }
  
  public void format(Format paramFormat)
  {
    Format localFormat = getAdjustedSampleFormat(paramFormat, this.sampleOffsetUs);
    boolean bool = this.metadataQueue.format(localFormat);
    this.lastUnadjustedFormat = paramFormat;
    this.pendingFormatAdjustment = false;
    if ((this.upstreamFormatChangeListener != null) && (bool)) {
      this.upstreamFormatChangeListener.onUpstreamFormatChanged(localFormat);
    }
  }
  
  public int getFirstIndex()
  {
    return this.metadataQueue.getFirstIndex();
  }
  
  public long getFirstTimestampUs()
  {
    return this.metadataQueue.getFirstTimestampUs();
  }
  
  public long getLargestQueuedTimestampUs()
  {
    return this.metadataQueue.getLargestQueuedTimestampUs();
  }
  
  public int getReadIndex()
  {
    return this.metadataQueue.getReadIndex();
  }
  
  public Format getUpstreamFormat()
  {
    return this.metadataQueue.getUpstreamFormat();
  }
  
  public int getWriteIndex()
  {
    return this.metadataQueue.getWriteIndex();
  }
  
  public boolean hasNextSample()
  {
    return this.metadataQueue.hasNextSample();
  }
  
  public int peekSourceId()
  {
    return this.metadataQueue.peekSourceId();
  }
  
  public int read(FormatHolder paramFormatHolder, DecoderInputBuffer paramDecoderInputBuffer, boolean paramBoolean1, boolean paramBoolean2, long paramLong)
  {
    int i;
    switch (this.metadataQueue.read(paramFormatHolder, paramDecoderInputBuffer, paramBoolean1, paramBoolean2, this.downstreamFormat, this.extrasHolder))
    {
    default: 
      throw new IllegalStateException();
    case -5: 
      this.downstreamFormat = paramFormatHolder.format;
      i = -5;
    }
    for (;;)
    {
      return i;
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
      }
      i = -4;
      continue;
      i = -3;
    }
  }
  
  public void reset()
  {
    reset(false);
  }
  
  public void reset(boolean paramBoolean)
  {
    this.metadataQueue.reset(paramBoolean);
    clearAllocationNodes(this.firstAllocationNode);
    this.firstAllocationNode = new AllocationNode(0L, this.allocationLength);
    this.readAllocationNode = this.firstAllocationNode;
    this.writeAllocationNode = this.firstAllocationNode;
    this.totalBytesWritten = 0L;
    this.allocator.trim();
  }
  
  public void rewind()
  {
    this.metadataQueue.rewind();
    this.readAllocationNode = this.firstAllocationNode;
  }
  
  public int sampleData(ExtractorInput paramExtractorInput, int paramInt, boolean paramBoolean)
    throws IOException, InterruptedException
  {
    paramInt = preAppend(paramInt);
    paramInt = paramExtractorInput.read(this.writeAllocationNode.allocation.data, this.writeAllocationNode.translateOffset(this.totalBytesWritten), paramInt);
    if (paramInt == -1) {
      if (paramBoolean) {
        paramInt = -1;
      }
    }
    for (;;)
    {
      return paramInt;
      throw new EOFException();
      postAppend(paramInt);
    }
  }
  
  public void sampleData(ParsableByteArray paramParsableByteArray, int paramInt)
  {
    while (paramInt > 0)
    {
      int i = preAppend(paramInt);
      paramParsableByteArray.readBytes(this.writeAllocationNode.allocation.data, this.writeAllocationNode.translateOffset(this.totalBytesWritten), i);
      paramInt -= i;
      postAppend(i);
    }
  }
  
  public void sampleMetadata(long paramLong, int paramInt1, int paramInt2, int paramInt3, TrackOutput.CryptoData paramCryptoData)
  {
    if (this.pendingFormatAdjustment) {
      format(this.lastUnadjustedFormat);
    }
    if (this.pendingSplice) {
      if (((paramInt1 & 0x1) != 0) && (this.metadataQueue.attemptSplice(paramLong))) {}
    }
    for (;;)
    {
      return;
      this.pendingSplice = false;
      long l1 = this.sampleOffsetUs;
      long l2 = this.totalBytesWritten;
      long l3 = paramInt2;
      long l4 = paramInt3;
      this.metadataQueue.commitSample(paramLong + l1, paramInt1, l2 - l3 - l4, paramInt2, paramCryptoData);
    }
  }
  
  public boolean setReadPosition(int paramInt)
  {
    return this.metadataQueue.setReadPosition(paramInt);
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
  
  public void sourceId(int paramInt)
  {
    this.metadataQueue.sourceId(paramInt);
  }
  
  public void splice()
  {
    this.pendingSplice = true;
  }
  
  private static final class AllocationNode
  {
    public Allocation allocation;
    public final long endPosition;
    public AllocationNode next;
    public final long startPosition;
    public boolean wasInitialized;
    
    public AllocationNode(long paramLong, int paramInt)
    {
      this.startPosition = paramLong;
      this.endPosition = (paramInt + paramLong);
    }
    
    public AllocationNode clear()
    {
      this.allocation = null;
      AllocationNode localAllocationNode = this.next;
      this.next = null;
      return localAllocationNode;
    }
    
    public void initialize(Allocation paramAllocation, AllocationNode paramAllocationNode)
    {
      this.allocation = paramAllocation;
      this.next = paramAllocationNode;
      this.wasInitialized = true;
    }
    
    public int translateOffset(long paramLong)
    {
      return (int)(paramLong - this.startPosition) + this.allocation.offset;
    }
  }
  
  public static abstract interface UpstreamFormatChangedListener
  {
    public abstract void onUpstreamFormatChanged(Format paramFormat);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/SampleQueue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
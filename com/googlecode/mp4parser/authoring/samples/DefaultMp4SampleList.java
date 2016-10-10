package com.googlecode.mp4parser.authoring.samples;

import com.coremedia.iso.boxes.ChunkOffsetBox;
import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.MovieBox;
import com.coremedia.iso.boxes.SampleSizeBox;
import com.coremedia.iso.boxes.SampleTableBox;
import com.coremedia.iso.boxes.SampleToChunkBox;
import com.coremedia.iso.boxes.SampleToChunkBox.Entry;
import com.coremedia.iso.boxes.TrackBox;
import com.coremedia.iso.boxes.TrackHeaderBox;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.util.CastUtils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DefaultMp4SampleList
  extends AbstractList<Sample>
{
  private static final long MAX_MAP_SIZE = 268435456L;
  ByteBuffer[][] cache = null;
  int[] chunkNumsStartSampleNum;
  long[] chunkOffsets;
  long[] chunkSizes;
  int lastChunk = 0;
  long[][] sampleOffsetsWithinChunks;
  SampleSizeBox ssb;
  Container topLevel;
  TrackBox trackBox = null;
  
  public DefaultMp4SampleList(long paramLong, Container paramContainer)
  {
    this.topLevel = paramContainer;
    paramContainer = ((MovieBox)paramContainer.getBoxes(MovieBox.class).get(0)).getBoxes(TrackBox.class).iterator();
    for (;;)
    {
      if (!paramContainer.hasNext())
      {
        if (this.trackBox != null) {
          break;
        }
        throw new RuntimeException("This MP4 does not contain track " + paramLong);
      }
      localObject = (TrackBox)paramContainer.next();
      if (((TrackBox)localObject).getTrackHeaderBox().getTrackId() == paramLong) {
        this.trackBox = ((TrackBox)localObject);
      }
    }
    this.chunkOffsets = this.trackBox.getSampleTableBox().getChunkOffsetBox().getChunkOffsets();
    this.chunkSizes = new long[this.chunkOffsets.length];
    this.cache = new ByteBuffer[this.chunkOffsets.length][];
    this.sampleOffsetsWithinChunks = new long[this.chunkOffsets.length][];
    this.ssb = this.trackBox.getSampleTableBox().getSampleSizeBox();
    paramContainer = this.trackBox.getSampleTableBox().getSampleToChunkBox().getEntries();
    paramContainer = (SampleToChunkBox.Entry[])paramContainer.toArray(new SampleToChunkBox.Entry[paramContainer.size()]);
    int i = 0 + 1;
    Object localObject = paramContainer[0];
    int n = 0;
    int m = 0;
    paramLong = ((SampleToChunkBox.Entry)localObject).getFirstChunk();
    int j = CastUtils.l2i(((SampleToChunkBox.Entry)localObject).getSamplesPerChunk());
    int k = 1;
    int i4 = size();
    n += 1;
    int i1;
    if (n == paramLong)
    {
      m = j;
      if (paramContainer.length > i)
      {
        j = i + 1;
        localObject = paramContainer[i];
        i1 = CastUtils.l2i(((SampleToChunkBox.Entry)localObject).getSamplesPerChunk());
        paramLong = ((SampleToChunkBox.Entry)localObject).getFirstChunk();
        i = j;
        j = i1;
      }
    }
    for (;;)
    {
      this.sampleOffsetsWithinChunks[(n - 1)] = new long[m];
      k += m;
      if (k > i4)
      {
        this.chunkNumsStartSampleNum = new int[n + 1];
        localObject = paramContainer[0];
        int i2 = 0;
        i1 = 0;
        long l = ((SampleToChunkBox.Entry)localObject).getFirstChunk();
        i = CastUtils.l2i(((SampleToChunkBox.Entry)localObject).getSamplesPerChunk());
        m = 1;
        for (k = 0 + 1;; k = n)
        {
          localObject = this.chunkNumsStartSampleNum;
          int i3 = i2 + 1;
          localObject[i2] = m;
          paramLong = l;
          j = i;
          n = k;
          if (i3 == l)
          {
            if (paramContainer.length > k)
            {
              localObject = paramContainer[k];
              j = CastUtils.l2i(((SampleToChunkBox.Entry)localObject).getSamplesPerChunk());
              paramLong = ((SampleToChunkBox.Entry)localObject).getFirstChunk();
              n = k + 1;
              i1 = i;
            }
          }
          else
          {
            label477:
            m += i1;
            if (m <= i4) {
              break label648;
            }
            this.chunkNumsStartSampleNum[i3] = Integer.MAX_VALUE;
            j = 0;
            paramLong = 0L;
            i = 1;
          }
          for (;;)
          {
            if (i > this.ssb.getSampleCount())
            {
              return;
              j = -1;
              paramLong = Long.MAX_VALUE;
              break;
              j = -1;
              paramLong = Long.MAX_VALUE;
              i1 = i;
              n = k;
              break label477;
            }
            while (i == this.chunkNumsStartSampleNum[j])
            {
              j += 1;
              paramLong = 0L;
            }
            paramContainer = this.chunkSizes;
            k = j - 1;
            paramContainer[k] += this.ssb.getSampleSizeAtIndex(i - 1);
            this.sampleOffsetsWithinChunks[(j - 1)][(i - this.chunkNumsStartSampleNum[(j - 1)])] = paramLong;
            paramLong += this.ssb.getSampleSizeAtIndex(i - 1);
            i += 1;
          }
          label648:
          i2 = i3;
          l = paramLong;
          i = j;
        }
      }
      break;
    }
  }
  
  public Sample get(int paramInt)
  {
    if (paramInt >= this.ssb.getSampleCount()) {
      throw new IndexOutOfBoundsException();
    }
    int j = getChunkForSample(paramInt);
    int k = this.chunkNumsStartSampleNum[j] - 1;
    long l4 = this.chunkOffsets[CastUtils.l2i(j)];
    long[] arrayOfLong2 = this.sampleOffsetsWithinChunks[CastUtils.l2i(j)];
    long l3 = arrayOfLong2[(paramInt - k)];
    ByteBuffer[] arrayOfByteBuffer = this.cache[CastUtils.l2i(j)];
    Object localObject = arrayOfByteBuffer;
    final long l1;
    int i;
    if (arrayOfByteBuffer == null)
    {
      localObject = new ArrayList();
      l1 = 0L;
      i = 0;
    }
    for (;;)
    {
      try
      {
        if (i >= arrayOfLong2.length)
        {
          ((List)localObject).add(this.topLevel.getByteBuffer(l4 + l1, -l1 + arrayOfLong2[(arrayOfLong2.length - 1)] + this.ssb.getSampleSizeAtIndex(arrayOfLong2.length + k - 1)));
          localObject = (ByteBuffer[])((List)localObject).toArray(new ByteBuffer[((List)localObject).size()]);
          this.cache[CastUtils.l2i(j)] = localObject;
          arrayOfByteBuffer = null;
          j = localObject.length;
          i = 0;
          l1 = l3;
          if (i >= j)
          {
            localObject = arrayOfByteBuffer;
            new Sample()
            {
              public ByteBuffer asByteBuffer()
              {
                return (ByteBuffer)((ByteBuffer)l1.position(CastUtils.l2i(this.val$finalOffsetWithInChunk))).slice().limit(CastUtils.l2i(this.val$sampleSize));
              }
              
              public long getSize()
              {
                return this.val$sampleSize;
              }
              
              public String toString()
              {
                return "DefaultMp4Sample(size:" + this.val$sampleSize + ")";
              }
              
              public void writeTo(WritableByteChannel paramAnonymousWritableByteChannel)
                throws IOException
              {
                paramAnonymousWritableByteChannel.write(asByteBuffer());
              }
            };
          }
        }
        else
        {
          long l2 = l1;
          if (arrayOfLong2[i] + this.ssb.getSampleSizeAtIndex(i + k) - l1 > 268435456L)
          {
            ((List)localObject).add(this.topLevel.getByteBuffer(l4 + l1, arrayOfLong2[i] - l1));
            l2 = arrayOfLong2[i];
          }
          i += 1;
          l1 = l2;
          continue;
        }
        arrayOfLong2 = localIOException[i];
      }
      catch (IOException localIOException)
      {
        throw new IndexOutOfBoundsException(localIOException.getMessage());
      }
      if (l1 < arrayOfLong2.limit())
      {
        long[] arrayOfLong1 = arrayOfLong2;
      }
      else
      {
        l1 -= arrayOfLong2.limit();
        i += 1;
      }
    }
  }
  
  int getChunkForSample(int paramInt)
  {
    paramInt += 1;
    try
    {
      if ((paramInt >= this.chunkNumsStartSampleNum[this.lastChunk]) && (paramInt < this.chunkNumsStartSampleNum[(this.lastChunk + 1)]))
      {
        paramInt = this.lastChunk;
        return paramInt;
      }
      if (paramInt < this.chunkNumsStartSampleNum[this.lastChunk]) {
        for (this.lastChunk = 0;; this.lastChunk += 1) {
          if (this.chunkNumsStartSampleNum[(this.lastChunk + 1)] > paramInt)
          {
            paramInt = this.lastChunk;
            break;
          }
        }
      }
      this.lastChunk += 1;
    }
    finally {}
    for (;;)
    {
      if (this.chunkNumsStartSampleNum[(this.lastChunk + 1)] > paramInt)
      {
        paramInt = this.lastChunk;
        break;
      }
      this.lastChunk += 1;
    }
  }
  
  public int size()
  {
    return CastUtils.l2i(this.trackBox.getSampleTableBox().getSampleSizeBox().getSampleCount());
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/authoring/samples/DefaultMp4SampleList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
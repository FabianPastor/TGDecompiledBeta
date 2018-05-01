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
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DefaultMp4SampleList
  extends AbstractList<Sample>
{
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
    Object localObject = ((MovieBox)paramContainer.getBoxes(MovieBox.class).get(0)).getBoxes(TrackBox.class).iterator();
    for (;;)
    {
      if (!((Iterator)localObject).hasNext())
      {
        if (this.trackBox != null) {
          break;
        }
        throw new RuntimeException("This MP4 does not contain track " + paramLong);
      }
      paramContainer = (TrackBox)((Iterator)localObject).next();
      if (paramContainer.getTrackHeaderBox().getTrackId() == paramLong) {
        this.trackBox = paramContainer;
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
    localObject = paramContainer[0];
    int j = 0;
    int k = 0;
    paramLong = ((SampleToChunkBox.Entry)localObject).getFirstChunk();
    int m = CastUtils.l2i(((SampleToChunkBox.Entry)localObject).getSamplesPerChunk());
    int n = 1;
    int i1 = size();
    j++;
    int i2;
    if (j == paramLong)
    {
      k = m;
      if (paramContainer.length > i)
      {
        m = i + 1;
        localObject = paramContainer[i];
        i2 = CastUtils.l2i(((SampleToChunkBox.Entry)localObject).getSamplesPerChunk());
        paramLong = ((SampleToChunkBox.Entry)localObject).getFirstChunk();
        i = m;
        m = i2;
      }
    }
    for (;;)
    {
      this.sampleOffsetsWithinChunks[(j - 1)] = new long[k];
      n += k;
      if (n > i1)
      {
        this.chunkNumsStartSampleNum = new int[j + 1];
        localObject = paramContainer[0];
        int i3 = 0;
        i2 = 0;
        long l = ((SampleToChunkBox.Entry)localObject).getFirstChunk();
        m = CastUtils.l2i(((SampleToChunkBox.Entry)localObject).getSamplesPerChunk());
        k = 1;
        for (n = 0 + 1;; n = j)
        {
          localObject = this.chunkNumsStartSampleNum;
          int i4 = i3 + 1;
          localObject[i3] = k;
          paramLong = l;
          i = m;
          j = n;
          if (i4 == l)
          {
            if (paramContainer.length > n)
            {
              localObject = paramContainer[n];
              i = CastUtils.l2i(((SampleToChunkBox.Entry)localObject).getSamplesPerChunk());
              paramLong = ((SampleToChunkBox.Entry)localObject).getFirstChunk();
              j = n + 1;
              i2 = m;
            }
          }
          else
          {
            label474:
            k += i2;
            if (k <= i1) {
              break label639;
            }
            this.chunkNumsStartSampleNum[i4] = Integer.MAX_VALUE;
            m = 0;
            paramLong = 0L;
          }
          for (i = 1;; i++)
          {
            if (i > this.ssb.getSampleCount())
            {
              return;
              m = -1;
              paramLong = Long.MAX_VALUE;
              break;
              i = -1;
              paramLong = Long.MAX_VALUE;
              i2 = m;
              j = n;
              break label474;
            }
            while (i == this.chunkNumsStartSampleNum[m])
            {
              m++;
              paramLong = 0L;
            }
            paramContainer = this.chunkSizes;
            n = m - 1;
            paramContainer[n] += this.ssb.getSampleSizeAtIndex(i - 1);
            this.sampleOffsetsWithinChunks[(m - 1)][(i - this.chunkNumsStartSampleNum[(m - 1)])] = paramLong;
            paramLong += this.ssb.getSampleSizeAtIndex(i - 1);
          }
          label639:
          i3 = i4;
          l = paramLong;
          m = i;
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
    int i = getChunkForSample(paramInt);
    int j = this.chunkNumsStartSampleNum[i] - 1;
    long l1 = this.chunkOffsets[CastUtils.l2i(i)];
    long[] arrayOfLong1 = this.sampleOffsetsWithinChunks[CastUtils.l2i(i)];
    long l2 = arrayOfLong1[(paramInt - j)];
    ByteBuffer[] arrayOfByteBuffer = this.cache[CastUtils.l2i(i)];
    Object localObject = arrayOfByteBuffer;
    final long l3;
    if (arrayOfByteBuffer == null)
    {
      localObject = new ArrayList();
      l3 = 0L;
    }
    for (int k = 0;; k++)
    {
      for (;;)
      {
        try
        {
          if (k >= arrayOfLong1.length)
          {
            ((List)localObject).add(this.topLevel.getByteBuffer(l1 + l3, -l3 + arrayOfLong1[(arrayOfLong1.length - 1)] + this.ssb.getSampleSizeAtIndex(arrayOfLong1.length + j - 1)));
            localObject = (ByteBuffer[])((List)localObject).toArray(new ByteBuffer[((List)localObject).size()]);
            this.cache[CastUtils.l2i(i)] = localObject;
            arrayOfByteBuffer = null;
            j = localObject.length;
            k = 0;
            l3 = l2;
            if (k >= j)
            {
              localObject = arrayOfByteBuffer;
              new Sample()
              {
                public String toString()
                {
                  return "DefaultMp4Sample(size:" + this.val$sampleSize + ")";
                }
              };
            }
          }
          else
          {
            long l4 = l3;
            if (arrayOfLong1[k] + this.ssb.getSampleSizeAtIndex(k + j) - l3 > 268435456L)
            {
              ((List)localObject).add(this.topLevel.getByteBuffer(l1 + l3, arrayOfLong1[k] - l3));
              l4 = arrayOfLong1[k];
            }
            k++;
            l3 = l4;
            continue;
          }
          arrayOfLong1 = localIOException[k];
        }
        catch (IOException localIOException)
        {
          throw new IndexOutOfBoundsException(localIOException.getMessage());
        }
        if (l3 >= arrayOfLong1.limit()) {
          break;
        }
        long[] arrayOfLong2 = arrayOfLong1;
      }
      l3 -= arrayOfLong1.limit();
    }
  }
  
  int getChunkForSample(int paramInt)
  {
    paramInt++;
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
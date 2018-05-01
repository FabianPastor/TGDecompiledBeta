package org.telegram.messenger.exoplayer2.source.chunk;

import java.io.IOException;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.extractor.DefaultExtractorInput;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.util.Util;

public final class SingleSampleMediaChunk
  extends BaseMediaChunk
{
  private volatile int bytesLoaded;
  private volatile boolean loadCanceled;
  private volatile boolean loadCompleted;
  private final Format sampleFormat;
  private final int trackType;
  
  public SingleSampleMediaChunk(DataSource paramDataSource, DataSpec paramDataSpec, Format paramFormat1, int paramInt1, Object paramObject, long paramLong1, long paramLong2, int paramInt2, int paramInt3, Format paramFormat2)
  {
    super(paramDataSource, paramDataSpec, paramFormat1, paramInt1, paramObject, paramLong1, paramLong2, paramInt2);
    this.trackType = paramInt3;
    this.sampleFormat = paramFormat2;
  }
  
  public long bytesLoaded()
  {
    return this.bytesLoaded;
  }
  
  public void cancelLoad()
  {
    this.loadCanceled = true;
  }
  
  public boolean isLoadCanceled()
  {
    return this.loadCanceled;
  }
  
  public boolean isLoadCompleted()
  {
    return this.loadCompleted;
  }
  
  public void load()
    throws IOException, InterruptedException
  {
    Object localObject1 = this.dataSpec.subrange(this.bytesLoaded);
    try
    {
      long l1 = this.dataSource.open((DataSpec)localObject1);
      long l2 = l1;
      if (l1 != -1L) {
        l2 = l1 + this.bytesLoaded;
      }
      localObject1 = new org/telegram/messenger/exoplayer2/extractor/DefaultExtractorInput;
      ((DefaultExtractorInput)localObject1).<init>(this.dataSource, this.bytesLoaded, l2);
      Object localObject3 = getOutput();
      ((BaseMediaChunkOutput)localObject3).setSampleOffsetUs(0L);
      localObject3 = ((BaseMediaChunkOutput)localObject3).track(0, this.trackType);
      ((TrackOutput)localObject3).format(this.sampleFormat);
      for (int i = 0; i != -1; i = ((TrackOutput)localObject3).sampleData((ExtractorInput)localObject1, Integer.MAX_VALUE, true)) {
        this.bytesLoaded += i;
      }
      i = this.bytesLoaded;
      ((TrackOutput)localObject3).sampleMetadata(this.startTimeUs, 1, i, 0, null);
      Util.closeQuietly(this.dataSource);
      this.loadCompleted = true;
      return;
    }
    finally
    {
      Util.closeQuietly(this.dataSource);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/chunk/SingleSampleMediaChunk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
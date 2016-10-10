package org.telegram.messenger.exoplayer.hls;

import android.util.SparseArray;
import java.io.IOException;
import org.telegram.messenger.exoplayer.MediaFormat;
import org.telegram.messenger.exoplayer.SampleHolder;
import org.telegram.messenger.exoplayer.chunk.Format;
import org.telegram.messenger.exoplayer.drm.DrmInitData;
import org.telegram.messenger.exoplayer.extractor.DefaultTrackOutput;
import org.telegram.messenger.exoplayer.extractor.Extractor;
import org.telegram.messenger.exoplayer.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer.extractor.SeekMap;
import org.telegram.messenger.exoplayer.extractor.TrackOutput;
import org.telegram.messenger.exoplayer.upstream.Allocator;
import org.telegram.messenger.exoplayer.util.Assertions;
import org.telegram.messenger.exoplayer.util.MimeTypes;

public final class HlsExtractorWrapper
  implements ExtractorOutput
{
  private final int adaptiveMaxHeight;
  private final int adaptiveMaxWidth;
  private Allocator allocator;
  private final Extractor extractor;
  public final Format format;
  private boolean prepared;
  private MediaFormat[] sampleQueueFormats;
  private final SparseArray<DefaultTrackOutput> sampleQueues;
  private final boolean shouldSpliceIn;
  private boolean spliceConfigured;
  public final long startTimeUs;
  private volatile boolean tracksBuilt;
  public final int trigger;
  
  public HlsExtractorWrapper(int paramInt1, Format paramFormat, long paramLong, Extractor paramExtractor, boolean paramBoolean, int paramInt2, int paramInt3)
  {
    this.trigger = paramInt1;
    this.format = paramFormat;
    this.startTimeUs = paramLong;
    this.extractor = paramExtractor;
    this.shouldSpliceIn = paramBoolean;
    this.adaptiveMaxWidth = paramInt2;
    this.adaptiveMaxHeight = paramInt3;
    this.sampleQueues = new SparseArray();
  }
  
  public void clear()
  {
    int i = 0;
    while (i < this.sampleQueues.size())
    {
      ((DefaultTrackOutput)this.sampleQueues.valueAt(i)).clear();
      i += 1;
    }
  }
  
  public final void configureSpliceTo(HlsExtractorWrapper paramHlsExtractorWrapper)
  {
    Assertions.checkState(isPrepared());
    if ((this.spliceConfigured) || (!paramHlsExtractorWrapper.shouldSpliceIn) || (!paramHlsExtractorWrapper.isPrepared())) {
      return;
    }
    boolean bool = true;
    int j = getTrackCount();
    int i = 0;
    while (i < j)
    {
      bool &= ((DefaultTrackOutput)this.sampleQueues.valueAt(i)).configureSpliceTo((DefaultTrackOutput)paramHlsExtractorWrapper.sampleQueues.valueAt(i));
      i += 1;
    }
    this.spliceConfigured = bool;
  }
  
  public void discardUntil(int paramInt, long paramLong)
  {
    Assertions.checkState(isPrepared());
    ((DefaultTrackOutput)this.sampleQueues.valueAt(paramInt)).discardUntil(paramLong);
  }
  
  public void drmInitData(DrmInitData paramDrmInitData) {}
  
  public void endTracks()
  {
    this.tracksBuilt = true;
  }
  
  public long getAdjustedEndTimeUs()
  {
    long l = Long.MIN_VALUE;
    int i = 0;
    while (i < this.sampleQueues.size())
    {
      l = Math.max(l, ((DefaultTrackOutput)this.sampleQueues.valueAt(i)).getLargestParsedTimestampUs());
      i += 1;
    }
    return l;
  }
  
  public long getLargestParsedTimestampUs()
  {
    long l = Long.MIN_VALUE;
    int i = 0;
    while (i < this.sampleQueues.size())
    {
      l = Math.max(l, ((DefaultTrackOutput)this.sampleQueues.valueAt(i)).getLargestParsedTimestampUs());
      i += 1;
    }
    return l;
  }
  
  public MediaFormat getMediaFormat(int paramInt)
  {
    Assertions.checkState(isPrepared());
    return this.sampleQueueFormats[paramInt];
  }
  
  public boolean getSample(int paramInt, SampleHolder paramSampleHolder)
  {
    Assertions.checkState(isPrepared());
    return ((DefaultTrackOutput)this.sampleQueues.valueAt(paramInt)).getSample(paramSampleHolder);
  }
  
  public int getTrackCount()
  {
    Assertions.checkState(isPrepared());
    return this.sampleQueues.size();
  }
  
  public boolean hasSamples(int paramInt)
  {
    Assertions.checkState(isPrepared());
    return !((DefaultTrackOutput)this.sampleQueues.valueAt(paramInt)).isEmpty();
  }
  
  public void init(Allocator paramAllocator)
  {
    this.allocator = paramAllocator;
    this.extractor.init(this);
  }
  
  public boolean isPrepared()
  {
    if ((!this.prepared) && (this.tracksBuilt))
    {
      int i = 0;
      while (i < this.sampleQueues.size())
      {
        if (!((DefaultTrackOutput)this.sampleQueues.valueAt(i)).hasFormat()) {
          return false;
        }
        i += 1;
      }
      this.prepared = true;
      this.sampleQueueFormats = new MediaFormat[this.sampleQueues.size()];
      i = 0;
      while (i < this.sampleQueueFormats.length)
      {
        MediaFormat localMediaFormat2 = ((DefaultTrackOutput)this.sampleQueues.valueAt(i)).getFormat();
        MediaFormat localMediaFormat1 = localMediaFormat2;
        if (MimeTypes.isVideo(localMediaFormat2.mimeType)) {
          if (this.adaptiveMaxWidth == -1)
          {
            localMediaFormat1 = localMediaFormat2;
            if (this.adaptiveMaxHeight == -1) {}
          }
          else
          {
            localMediaFormat1 = localMediaFormat2.copyWithMaxVideoDimensions(this.adaptiveMaxWidth, this.adaptiveMaxHeight);
          }
        }
        this.sampleQueueFormats[i] = localMediaFormat1;
        i += 1;
      }
    }
    return this.prepared;
  }
  
  public int read(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    boolean bool = true;
    int i = this.extractor.read(paramExtractorInput, null);
    if (i != 1) {}
    for (;;)
    {
      Assertions.checkState(bool);
      return i;
      bool = false;
    }
  }
  
  public void seekMap(SeekMap paramSeekMap) {}
  
  public TrackOutput track(int paramInt)
  {
    DefaultTrackOutput localDefaultTrackOutput = new DefaultTrackOutput(this.allocator);
    this.sampleQueues.put(paramInt, localDefaultTrackOutput);
    return localDefaultTrackOutput;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/hls/HlsExtractorWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
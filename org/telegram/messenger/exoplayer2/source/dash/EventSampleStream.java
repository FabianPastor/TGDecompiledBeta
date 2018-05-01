package org.telegram.messenger.exoplayer2.source.dash;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.FormatHolder;
import org.telegram.messenger.exoplayer2.decoder.DecoderInputBuffer;
import org.telegram.messenger.exoplayer2.metadata.emsg.EventMessageEncoder;
import org.telegram.messenger.exoplayer2.source.SampleStream;
import org.telegram.messenger.exoplayer2.source.dash.manifest.EventStream;
import org.telegram.messenger.exoplayer2.util.Util;

final class EventSampleStream
  implements SampleStream
{
  private int currentIndex;
  private final EventMessageEncoder eventMessageEncoder;
  private EventStream eventStream;
  private boolean eventStreamUpdatable;
  private long[] eventTimesUs;
  private boolean isFormatSentDownstream;
  private long pendingSeekPositionUs;
  private final Format upstreamFormat;
  
  EventSampleStream(EventStream paramEventStream, Format paramFormat, boolean paramBoolean)
  {
    this.upstreamFormat = paramFormat;
    this.eventMessageEncoder = new EventMessageEncoder();
    this.pendingSeekPositionUs = -9223372036854775807L;
    updateEventStream(paramEventStream, paramBoolean);
  }
  
  String eventStreamId()
  {
    return this.eventStream.id();
  }
  
  public boolean isReady()
  {
    return true;
  }
  
  public void maybeThrowError()
    throws IOException
  {}
  
  public int readData(FormatHolder paramFormatHolder, DecoderInputBuffer paramDecoderInputBuffer, boolean paramBoolean)
  {
    int i = -4;
    if ((paramBoolean) || (!this.isFormatSentDownstream))
    {
      paramFormatHolder.format = this.upstreamFormat;
      this.isFormatSentDownstream = true;
      i = -5;
    }
    for (;;)
    {
      return i;
      if (this.currentIndex == this.eventTimesUs.length)
      {
        if (!this.eventStreamUpdatable) {
          paramDecoderInputBuffer.setFlags(4);
        } else {
          i = -3;
        }
      }
      else
      {
        int j = this.currentIndex;
        this.currentIndex = (j + 1);
        paramFormatHolder = this.eventMessageEncoder.encode(this.eventStream.events[j], this.eventStream.timescale);
        if (paramFormatHolder != null)
        {
          paramDecoderInputBuffer.ensureSpaceForWrite(paramFormatHolder.length);
          paramDecoderInputBuffer.setFlags(1);
          paramDecoderInputBuffer.data.put(paramFormatHolder);
          paramDecoderInputBuffer.timeUs = this.eventTimesUs[j];
        }
        else
        {
          i = -3;
        }
      }
    }
  }
  
  public void seekToUs(long paramLong)
  {
    int i = 1;
    this.currentIndex = Util.binarySearchCeil(this.eventTimesUs, paramLong, true, false);
    if ((this.eventStreamUpdatable) && (this.currentIndex == this.eventTimesUs.length)) {
      if (i == 0) {
        break label50;
      }
    }
    for (;;)
    {
      this.pendingSeekPositionUs = paramLong;
      return;
      i = 0;
      break;
      label50:
      paramLong = -9223372036854775807L;
    }
  }
  
  public int skipData(long paramLong)
  {
    int i = Math.max(this.currentIndex, Util.binarySearchCeil(this.eventTimesUs, paramLong, true, false));
    int j = this.currentIndex;
    this.currentIndex = i;
    return i - j;
  }
  
  void updateEventStream(EventStream paramEventStream, boolean paramBoolean)
  {
    long l;
    if (this.currentIndex == 0)
    {
      l = -9223372036854775807L;
      this.eventStreamUpdatable = paramBoolean;
      this.eventStream = paramEventStream;
      this.eventTimesUs = paramEventStream.presentationTimesUs;
      if (this.pendingSeekPositionUs == -9223372036854775807L) {
        break label64;
      }
      seekToUs(this.pendingSeekPositionUs);
    }
    for (;;)
    {
      return;
      l = this.eventTimesUs[(this.currentIndex - 1)];
      break;
      label64:
      if (l != -9223372036854775807L) {
        this.currentIndex = Util.binarySearchCeil(this.eventTimesUs, l, false, false);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/dash/EventSampleStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
package org.telegram.messenger.exoplayer2.source;

import java.io.IOException;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.FormatHolder;
import org.telegram.messenger.exoplayer2.SeekParameters;
import org.telegram.messenger.exoplayer2.decoder.DecoderInputBuffer;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelection;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.MimeTypes;

public final class ClippingMediaPeriod
  implements MediaPeriod, MediaPeriod.Callback
{
  private MediaPeriod.Callback callback;
  long endUs;
  public final MediaPeriod mediaPeriod;
  private long pendingInitialDiscontinuityPositionUs;
  private ClippingSampleStream[] sampleStreams;
  long startUs;
  
  public ClippingMediaPeriod(MediaPeriod paramMediaPeriod, boolean paramBoolean)
  {
    this.mediaPeriod = paramMediaPeriod;
    this.sampleStreams = new ClippingSampleStream[0];
    if (paramBoolean) {}
    for (long l = 0L;; l = -9223372036854775807L)
    {
      this.pendingInitialDiscontinuityPositionUs = l;
      this.startUs = -9223372036854775807L;
      this.endUs = -9223372036854775807L;
      return;
    }
  }
  
  private SeekParameters clipSeekParameters(long paramLong, SeekParameters paramSeekParameters)
  {
    long l = Math.min(paramLong - this.startUs, paramSeekParameters.toleranceBeforeUs);
    if (this.endUs == Long.MIN_VALUE)
    {
      paramLong = paramSeekParameters.toleranceAfterUs;
      if ((l != paramSeekParameters.toleranceBeforeUs) || (paramLong != paramSeekParameters.toleranceAfterUs)) {
        break label69;
      }
    }
    for (;;)
    {
      return paramSeekParameters;
      paramLong = Math.min(this.endUs - paramLong, paramSeekParameters.toleranceAfterUs);
      break;
      label69:
      paramSeekParameters = new SeekParameters(l, paramLong);
    }
  }
  
  private static boolean shouldKeepInitialDiscontinuity(long paramLong, TrackSelection[] paramArrayOfTrackSelection)
  {
    boolean bool1 = false;
    boolean bool2 = bool1;
    int i;
    if (paramLong != 0L) {
      i = paramArrayOfTrackSelection.length;
    }
    for (int j = 0;; j++)
    {
      bool2 = bool1;
      if (j < i)
      {
        TrackSelection localTrackSelection = paramArrayOfTrackSelection[j];
        if ((localTrackSelection != null) && (!MimeTypes.isAudio(localTrackSelection.getSelectedFormat().sampleMimeType))) {
          bool2 = true;
        }
      }
      else
      {
        return bool2;
      }
    }
  }
  
  public boolean continueLoading(long paramLong)
  {
    return this.mediaPeriod.continueLoading(this.startUs + paramLong);
  }
  
  public void discardBuffer(long paramLong, boolean paramBoolean)
  {
    this.mediaPeriod.discardBuffer(this.startUs + paramLong, paramBoolean);
  }
  
  public long getAdjustedSeekPositionUs(long paramLong, SeekParameters paramSeekParameters)
  {
    if (paramLong == this.startUs) {}
    for (paramLong = 0L;; paramLong = this.mediaPeriod.getAdjustedSeekPositionUs(paramLong, paramSeekParameters) - this.startUs)
    {
      return paramLong;
      paramLong += this.startUs;
      paramSeekParameters = clipSeekParameters(paramLong, paramSeekParameters);
    }
  }
  
  public long getBufferedPositionUs()
  {
    long l1 = Long.MIN_VALUE;
    long l2 = this.mediaPeriod.getBufferedPositionUs();
    long l3 = l1;
    if (l2 != Long.MIN_VALUE) {
      if ((this.endUs == Long.MIN_VALUE) || (l2 < this.endUs)) {
        break label51;
      }
    }
    label51:
    for (l3 = l1;; l3 = Math.max(0L, l2 - this.startUs)) {
      return l3;
    }
  }
  
  public long getNextLoadPositionUs()
  {
    long l1 = Long.MIN_VALUE;
    long l2 = this.mediaPeriod.getNextLoadPositionUs();
    long l3 = l1;
    if (l2 != Long.MIN_VALUE) {
      if ((this.endUs == Long.MIN_VALUE) || (l2 < this.endUs)) {
        break label51;
      }
    }
    label51:
    for (l3 = l1;; l3 = l2 - this.startUs) {
      return l3;
    }
  }
  
  public TrackGroupArray getTrackGroups()
  {
    return this.mediaPeriod.getTrackGroups();
  }
  
  boolean isPendingInitialDiscontinuity()
  {
    if (this.pendingInitialDiscontinuityPositionUs != -9223372036854775807L) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public void maybeThrowPrepareError()
    throws IOException
  {
    this.mediaPeriod.maybeThrowPrepareError();
  }
  
  public void onContinueLoadingRequested(MediaPeriod paramMediaPeriod)
  {
    this.callback.onContinueLoadingRequested(this);
  }
  
  public void onPrepared(MediaPeriod paramMediaPeriod)
  {
    if ((this.startUs != -9223372036854775807L) && (this.endUs != -9223372036854775807L)) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      this.callback.onPrepared(this);
      return;
    }
  }
  
  public void prepare(MediaPeriod.Callback paramCallback, long paramLong)
  {
    this.callback = paramCallback;
    this.mediaPeriod.prepare(this, this.startUs + paramLong);
  }
  
  public long readDiscontinuity()
  {
    boolean bool1 = false;
    long l1;
    if (isPendingInitialDiscontinuity())
    {
      l1 = this.pendingInitialDiscontinuityPositionUs;
      this.pendingInitialDiscontinuityPositionUs = -9223372036854775807L;
      long l2 = readDiscontinuity();
      if (l2 != -9223372036854775807L) {
        l1 = l2;
      }
    }
    for (;;)
    {
      return l1;
      continue;
      l1 = this.mediaPeriod.readDiscontinuity();
      if (l1 != -9223372036854775807L) {
        break;
      }
      l1 = -9223372036854775807L;
    }
    if (l1 >= this.startUs) {}
    for (boolean bool2 = true;; bool2 = false)
    {
      Assertions.checkState(bool2);
      if (this.endUs != Long.MIN_VALUE)
      {
        bool2 = bool1;
        if (l1 > this.endUs) {}
      }
      else
      {
        bool2 = true;
      }
      Assertions.checkState(bool2);
      l1 -= this.startUs;
      break;
    }
  }
  
  public void reevaluateBuffer(long paramLong)
  {
    this.mediaPeriod.reevaluateBuffer(this.startUs + paramLong);
  }
  
  public long seekToUs(long paramLong)
  {
    boolean bool1 = false;
    this.pendingInitialDiscontinuityPositionUs = -9223372036854775807L;
    for (ClippingSampleStream localClippingSampleStream : this.sampleStreams) {
      if (localClippingSampleStream != null) {
        localClippingSampleStream.clearSentEos();
      }
    }
    paramLong += this.startUs;
    long l = this.mediaPeriod.seekToUs(paramLong);
    if (l != paramLong)
    {
      bool2 = bool1;
      if (l < this.startUs) {
        break label119;
      }
      if (this.endUs != Long.MIN_VALUE)
      {
        bool2 = bool1;
        if (l > this.endUs) {
          break label119;
        }
      }
    }
    boolean bool2 = true;
    label119:
    Assertions.checkState(bool2);
    return l - this.startUs;
  }
  
  public long selectTracks(TrackSelection[] paramArrayOfTrackSelection, boolean[] paramArrayOfBoolean1, SampleStream[] paramArrayOfSampleStream, boolean[] paramArrayOfBoolean2, long paramLong)
  {
    this.sampleStreams = new ClippingSampleStream[paramArrayOfSampleStream.length];
    SampleStream[] arrayOfSampleStream = new SampleStream[paramArrayOfSampleStream.length];
    int i = 0;
    if (i < paramArrayOfSampleStream.length)
    {
      this.sampleStreams[i] = ((ClippingSampleStream)paramArrayOfSampleStream[i]);
      if (this.sampleStreams[i] != null) {}
      for (SampleStream localSampleStream = this.sampleStreams[i].childStream;; localSampleStream = null)
      {
        arrayOfSampleStream[i] = localSampleStream;
        i++;
        break;
      }
    }
    long l1 = this.mediaPeriod.selectTracks(paramArrayOfTrackSelection, paramArrayOfBoolean1, arrayOfSampleStream, paramArrayOfBoolean2, paramLong + this.startUs) - this.startUs;
    long l2;
    boolean bool;
    if ((isPendingInitialDiscontinuity()) && (paramLong == 0L) && (shouldKeepInitialDiscontinuity(this.startUs, paramArrayOfTrackSelection)))
    {
      l2 = l1;
      this.pendingInitialDiscontinuityPositionUs = l2;
      if ((l1 != paramLong) && ((l1 < 0L) || ((this.endUs != Long.MIN_VALUE) && (this.startUs + l1 > this.endUs)))) {
        break label245;
      }
      bool = true;
      label189:
      Assertions.checkState(bool);
      i = 0;
      label197:
      if (i >= paramArrayOfSampleStream.length) {
        break label299;
      }
      if (arrayOfSampleStream[i] != null) {
        break label251;
      }
      this.sampleStreams[i] = null;
    }
    for (;;)
    {
      paramArrayOfSampleStream[i] = this.sampleStreams[i];
      i++;
      break label197;
      l2 = -9223372036854775807L;
      break;
      label245:
      bool = false;
      break label189;
      label251:
      if ((paramArrayOfSampleStream[i] == null) || (this.sampleStreams[i].childStream != arrayOfSampleStream[i])) {
        this.sampleStreams[i] = new ClippingSampleStream(arrayOfSampleStream[i]);
      }
    }
    label299:
    return l1;
  }
  
  public void setClipping(long paramLong1, long paramLong2)
  {
    this.startUs = paramLong1;
    this.endUs = paramLong2;
  }
  
  private final class ClippingSampleStream
    implements SampleStream
  {
    public final SampleStream childStream;
    private boolean sentEos;
    
    public ClippingSampleStream(SampleStream paramSampleStream)
    {
      this.childStream = paramSampleStream;
    }
    
    public void clearSentEos()
    {
      this.sentEos = false;
    }
    
    public boolean isReady()
    {
      if ((!ClippingMediaPeriod.this.isPendingInitialDiscontinuity()) && (this.childStream.isReady())) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public void maybeThrowError()
      throws IOException
    {
      this.childStream.maybeThrowError();
    }
    
    public int readData(FormatHolder paramFormatHolder, DecoderInputBuffer paramDecoderInputBuffer, boolean paramBoolean)
    {
      int i;
      if (ClippingMediaPeriod.this.isPendingInitialDiscontinuity()) {
        i = -3;
      }
      for (;;)
      {
        return i;
        if (this.sentEos)
        {
          paramDecoderInputBuffer.setFlags(4);
          i = -4;
        }
        else
        {
          int j = this.childStream.readData(paramFormatHolder, paramDecoderInputBuffer, paramBoolean);
          if (j == -5)
          {
            paramDecoderInputBuffer = paramFormatHolder.format;
            if (ClippingMediaPeriod.this.startUs != 0L)
            {
              i = 0;
              label77:
              if (ClippingMediaPeriod.this.endUs == Long.MIN_VALUE) {
                break label122;
              }
            }
            label122:
            for (j = 0;; j = paramDecoderInputBuffer.encoderPadding)
            {
              paramFormatHolder.format = paramDecoderInputBuffer.copyWithGaplessInfo(i, j);
              i = -5;
              break;
              i = paramDecoderInputBuffer.encoderDelay;
              break label77;
            }
          }
          if ((ClippingMediaPeriod.this.endUs != Long.MIN_VALUE) && (((j == -4) && (paramDecoderInputBuffer.timeUs >= ClippingMediaPeriod.this.endUs)) || ((j == -3) && (ClippingMediaPeriod.this.getBufferedPositionUs() == Long.MIN_VALUE))))
          {
            paramDecoderInputBuffer.clear();
            paramDecoderInputBuffer.setFlags(4);
            this.sentEos = true;
            i = -4;
          }
          else
          {
            i = j;
            if (j == -4)
            {
              i = j;
              if (!paramDecoderInputBuffer.isEndOfStream())
              {
                paramDecoderInputBuffer.timeUs -= ClippingMediaPeriod.this.startUs;
                i = j;
              }
            }
          }
        }
      }
    }
    
    public int skipData(long paramLong)
    {
      if (ClippingMediaPeriod.this.isPendingInitialDiscontinuity()) {}
      for (int i = -3;; i = this.childStream.skipData(ClippingMediaPeriod.this.startUs + paramLong)) {
        return i;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/ClippingMediaPeriod.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
package org.telegram.messenger.exoplayer2.extractor.ogg;

import java.io.IOException;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.extractor.PositionHolder;
import org.telegram.messenger.exoplayer2.extractor.SeekMap;
import org.telegram.messenger.exoplayer2.extractor.SeekMap.Unseekable;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

abstract class StreamReader
{
  private static final int STATE_END_OF_INPUT = 3;
  private static final int STATE_READ_HEADERS = 0;
  private static final int STATE_READ_PAYLOAD = 2;
  private static final int STATE_SKIP_HEADERS = 1;
  private long currentGranule;
  private ExtractorOutput extractorOutput;
  private boolean formatSet;
  private long lengthOfReadPacket;
  private final OggPacket oggPacket = new OggPacket();
  private OggSeeker oggSeeker;
  private long payloadStartPosition;
  private int sampleRate;
  private boolean seekMapSet;
  private SetupData setupData;
  private int state;
  private long targetGranule;
  private TrackOutput trackOutput;
  
  private int readHeaders(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    int i = 1;
    int j;
    while (i != 0)
    {
      if (!this.oggPacket.populate(paramExtractorInput))
      {
        this.state = 3;
        j = -1;
        return j;
      }
      this.lengthOfReadPacket = (paramExtractorInput.getPosition() - this.payloadStartPosition);
      boolean bool = readHeaders(this.oggPacket.getPayload(), this.payloadStartPosition, this.setupData);
      i = bool;
      if (bool)
      {
        this.payloadStartPosition = paramExtractorInput.getPosition();
        i = bool;
      }
    }
    this.sampleRate = this.setupData.format.sampleRate;
    if (!this.formatSet)
    {
      this.trackOutput.format(this.setupData.format);
      this.formatSet = true;
    }
    if (this.setupData.oggSeeker != null) {
      this.oggSeeker = this.setupData.oggSeeker;
    }
    for (;;)
    {
      this.setupData = null;
      this.state = 2;
      this.oggPacket.trimPayload();
      j = 0;
      break;
      if (paramExtractorInput.getLength() == -1L)
      {
        this.oggSeeker = new UnseekableOggSeeker(null);
      }
      else
      {
        OggPageHeader localOggPageHeader = this.oggPacket.getPageHeader();
        long l1 = this.payloadStartPosition;
        long l2 = paramExtractorInput.getLength();
        j = localOggPageHeader.headerSize;
        this.oggSeeker = new DefaultOggSeeker(l1, l2, this, localOggPageHeader.bodySize + j, localOggPageHeader.granulePosition);
      }
    }
  }
  
  private int readPayload(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
    throws IOException, InterruptedException
  {
    long l1 = this.oggSeeker.read(paramExtractorInput);
    int i;
    if (l1 >= 0L)
    {
      paramPositionHolder.position = l1;
      i = 1;
    }
    for (;;)
    {
      return i;
      if (l1 < -1L) {
        onSeekEnd(-(2L + l1));
      }
      if (!this.seekMapSet)
      {
        paramPositionHolder = this.oggSeeker.createSeekMap();
        this.extractorOutput.seekMap(paramPositionHolder);
        this.seekMapSet = true;
      }
      if ((this.lengthOfReadPacket > 0L) || (this.oggPacket.populate(paramExtractorInput)))
      {
        this.lengthOfReadPacket = 0L;
        paramExtractorInput = this.oggPacket.getPayload();
        long l2 = preparePayload(paramExtractorInput);
        if ((l2 >= 0L) && (this.currentGranule + l2 >= this.targetGranule))
        {
          l1 = convertGranuleToTime(this.currentGranule);
          this.trackOutput.sampleData(paramExtractorInput, paramExtractorInput.limit());
          this.trackOutput.sampleMetadata(l1, 1, paramExtractorInput.limit(), 0, null);
          this.targetGranule = -1L;
        }
        this.currentGranule += l2;
        i = 0;
      }
      else
      {
        this.state = 3;
        i = -1;
      }
    }
  }
  
  protected long convertGranuleToTime(long paramLong)
  {
    return 1000000L * paramLong / this.sampleRate;
  }
  
  protected long convertTimeToGranule(long paramLong)
  {
    return this.sampleRate * paramLong / 1000000L;
  }
  
  void init(ExtractorOutput paramExtractorOutput, TrackOutput paramTrackOutput)
  {
    this.extractorOutput = paramExtractorOutput;
    this.trackOutput = paramTrackOutput;
    reset(true);
  }
  
  protected void onSeekEnd(long paramLong)
  {
    this.currentGranule = paramLong;
  }
  
  protected abstract long preparePayload(ParsableByteArray paramParsableByteArray);
  
  final int read(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
    throws IOException, InterruptedException
  {
    int i;
    switch (this.state)
    {
    default: 
      throw new IllegalStateException();
    case 0: 
      i = readHeaders(paramExtractorInput);
    }
    for (;;)
    {
      return i;
      paramExtractorInput.skipFully((int)this.payloadStartPosition);
      this.state = 2;
      i = 0;
      continue;
      i = readPayload(paramExtractorInput, paramPositionHolder);
    }
  }
  
  protected abstract boolean readHeaders(ParsableByteArray paramParsableByteArray, long paramLong, SetupData paramSetupData)
    throws IOException, InterruptedException;
  
  protected void reset(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.setupData = new SetupData();
      this.payloadStartPosition = 0L;
    }
    for (this.state = 0;; this.state = 1)
    {
      this.targetGranule = -1L;
      this.currentGranule = 0L;
      return;
    }
  }
  
  final void seek(long paramLong1, long paramLong2)
  {
    this.oggPacket.reset();
    boolean bool;
    if (paramLong1 == 0L) {
      if (!this.seekMapSet)
      {
        bool = true;
        reset(bool);
      }
    }
    for (;;)
    {
      return;
      bool = false;
      break;
      if (this.state != 0)
      {
        this.targetGranule = this.oggSeeker.startSeek(paramLong2);
        this.state = 2;
      }
    }
  }
  
  static class SetupData
  {
    Format format;
    OggSeeker oggSeeker;
  }
  
  private static final class UnseekableOggSeeker
    implements OggSeeker
  {
    public SeekMap createSeekMap()
    {
      return new SeekMap.Unseekable(-9223372036854775807L);
    }
    
    public long read(ExtractorInput paramExtractorInput)
      throws IOException, InterruptedException
    {
      return -1L;
    }
    
    public long startSeek(long paramLong)
    {
      return 0L;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/extractor/ogg/StreamReader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
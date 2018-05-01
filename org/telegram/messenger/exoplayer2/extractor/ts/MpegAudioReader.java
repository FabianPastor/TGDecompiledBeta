package org.telegram.messenger.exoplayer2.extractor.ts;

import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.extractor.MpegAudioHeader;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

public final class MpegAudioReader
  implements ElementaryStreamReader
{
  private static final int HEADER_SIZE = 4;
  private static final int STATE_FINDING_HEADER = 0;
  private static final int STATE_READING_FRAME = 2;
  private static final int STATE_READING_HEADER = 1;
  private String formatId;
  private int frameBytesRead;
  private long frameDurationUs;
  private int frameSize;
  private boolean hasOutputFormat;
  private final MpegAudioHeader header;
  private final ParsableByteArray headerScratch = new ParsableByteArray(4);
  private final String language;
  private boolean lastByteWasFF;
  private TrackOutput output;
  private int state = 0;
  private long timeUs;
  
  public MpegAudioReader()
  {
    this(null);
  }
  
  public MpegAudioReader(String paramString)
  {
    this.headerScratch.data[0] = ((byte)-1);
    this.header = new MpegAudioHeader();
    this.language = paramString;
  }
  
  private void findHeader(ParsableByteArray paramParsableByteArray)
  {
    byte[] arrayOfByte = paramParsableByteArray.data;
    int i = paramParsableByteArray.getPosition();
    int j = paramParsableByteArray.limit();
    boolean bool;
    label38:
    int k;
    if (i < j) {
      if ((arrayOfByte[i] & 0xFF) == 255)
      {
        bool = true;
        if ((!this.lastByteWasFF) || ((arrayOfByte[i] & 0xE0) != 224)) {
          break label114;
        }
        k = 1;
        label61:
        this.lastByteWasFF = bool;
        if (k == 0) {
          break label120;
        }
        paramParsableByteArray.setPosition(i + 1);
        this.lastByteWasFF = false;
        this.headerScratch.data[1] = ((byte)arrayOfByte[i]);
        this.frameBytesRead = 2;
        this.state = 1;
      }
    }
    for (;;)
    {
      return;
      bool = false;
      break label38;
      label114:
      k = 0;
      break label61;
      label120:
      i++;
      break;
      paramParsableByteArray.setPosition(j);
    }
  }
  
  private void readFrameRemainder(ParsableByteArray paramParsableByteArray)
  {
    int i = Math.min(paramParsableByteArray.bytesLeft(), this.frameSize - this.frameBytesRead);
    this.output.sampleData(paramParsableByteArray, i);
    this.frameBytesRead += i;
    if (this.frameBytesRead < this.frameSize) {}
    for (;;)
    {
      return;
      this.output.sampleMetadata(this.timeUs, 1, this.frameSize, 0, null);
      this.timeUs += this.frameDurationUs;
      this.frameBytesRead = 0;
      this.state = 0;
    }
  }
  
  private void readHeaderRemainder(ParsableByteArray paramParsableByteArray)
  {
    int i = Math.min(paramParsableByteArray.bytesLeft(), 4 - this.frameBytesRead);
    paramParsableByteArray.readBytes(this.headerScratch.data, this.frameBytesRead, i);
    this.frameBytesRead += i;
    if (this.frameBytesRead < 4) {}
    for (;;)
    {
      return;
      this.headerScratch.setPosition(0);
      if (!MpegAudioHeader.populateHeader(this.headerScratch.readInt(), this.header))
      {
        this.frameBytesRead = 0;
        this.state = 1;
      }
      else
      {
        this.frameSize = this.header.frameSize;
        if (!this.hasOutputFormat)
        {
          this.frameDurationUs = (1000000L * this.header.samplesPerFrame / this.header.sampleRate);
          paramParsableByteArray = Format.createAudioSampleFormat(this.formatId, this.header.mimeType, null, -1, 4096, this.header.channels, this.header.sampleRate, null, null, 0, this.language);
          this.output.format(paramParsableByteArray);
          this.hasOutputFormat = true;
        }
        this.headerScratch.setPosition(0);
        this.output.sampleData(this.headerScratch, 4);
        this.state = 2;
      }
    }
  }
  
  public void consume(ParsableByteArray paramParsableByteArray)
  {
    while (paramParsableByteArray.bytesLeft() > 0) {
      switch (this.state)
      {
      default: 
        break;
      case 0: 
        findHeader(paramParsableByteArray);
        break;
      case 1: 
        readHeaderRemainder(paramParsableByteArray);
        break;
      case 2: 
        readFrameRemainder(paramParsableByteArray);
      }
    }
  }
  
  public void createTracks(ExtractorOutput paramExtractorOutput, TsPayloadReader.TrackIdGenerator paramTrackIdGenerator)
  {
    paramTrackIdGenerator.generateNewId();
    this.formatId = paramTrackIdGenerator.getFormatId();
    this.output = paramExtractorOutput.track(paramTrackIdGenerator.getTrackId(), 1);
  }
  
  public void packetFinished() {}
  
  public void packetStarted(long paramLong, boolean paramBoolean)
  {
    this.timeUs = paramLong;
  }
  
  public void seek()
  {
    this.state = 0;
    this.frameBytesRead = 0;
    this.lastByteWasFF = false;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/extractor/ts/MpegAudioReader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
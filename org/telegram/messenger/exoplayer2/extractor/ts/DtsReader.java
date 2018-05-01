package org.telegram.messenger.exoplayer2.extractor.ts;

import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.audio.DtsUtil;
import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

public final class DtsReader
  implements ElementaryStreamReader
{
  private static final int HEADER_SIZE = 15;
  private static final int STATE_FINDING_SYNC = 0;
  private static final int STATE_READING_HEADER = 1;
  private static final int STATE_READING_SAMPLE = 2;
  private static final int SYNC_VALUE = NUM;
  private static final int SYNC_VALUE_SIZE = 4;
  private int bytesRead;
  private Format format;
  private String formatId;
  private final ParsableByteArray headerScratchBytes = new ParsableByteArray(new byte[15]);
  private final String language;
  private TrackOutput output;
  private long sampleDurationUs;
  private int sampleSize;
  private int state;
  private int syncBytes;
  private long timeUs;
  
  public DtsReader(String paramString)
  {
    this.headerScratchBytes.data[0] = ((byte)127);
    this.headerScratchBytes.data[1] = ((byte)-2);
    this.headerScratchBytes.data[2] = ((byte)Byte.MIN_VALUE);
    this.headerScratchBytes.data[3] = ((byte)1);
    this.state = 0;
    this.language = paramString;
  }
  
  private boolean continueRead(ParsableByteArray paramParsableByteArray, byte[] paramArrayOfByte, int paramInt)
  {
    int i = Math.min(paramParsableByteArray.bytesLeft(), paramInt - this.bytesRead);
    paramParsableByteArray.readBytes(paramArrayOfByte, this.bytesRead, i);
    this.bytesRead += i;
    if (this.bytesRead == paramInt) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private void parseHeader()
  {
    byte[] arrayOfByte = this.headerScratchBytes.data;
    if (this.format == null)
    {
      this.format = DtsUtil.parseDtsFormat(arrayOfByte, this.formatId, this.language, null);
      this.output.format(this.format);
    }
    this.sampleSize = DtsUtil.getDtsFrameSize(arrayOfByte);
    this.sampleDurationUs = ((int)(1000000L * DtsUtil.parseDtsAudioSampleCount(arrayOfByte) / this.format.sampleRate));
  }
  
  private boolean skipToNextSync(ParsableByteArray paramParsableByteArray)
  {
    boolean bool1 = false;
    do
    {
      bool2 = bool1;
      if (paramParsableByteArray.bytesLeft() <= 0) {
        break;
      }
      this.syncBytes <<= 8;
      this.syncBytes |= paramParsableByteArray.readUnsignedByte();
    } while (this.syncBytes != NUM);
    this.syncBytes = 0;
    boolean bool2 = true;
    return bool2;
  }
  
  public void consume(ParsableByteArray paramParsableByteArray)
  {
    while (paramParsableByteArray.bytesLeft() > 0) {
      switch (this.state)
      {
      default: 
        break;
      case 0: 
        if (skipToNextSync(paramParsableByteArray))
        {
          this.bytesRead = 4;
          this.state = 1;
        }
        break;
      case 1: 
        if (continueRead(paramParsableByteArray, this.headerScratchBytes.data, 15))
        {
          parseHeader();
          this.headerScratchBytes.setPosition(0);
          this.output.sampleData(this.headerScratchBytes, 15);
          this.state = 2;
        }
        break;
      case 2: 
        int i = Math.min(paramParsableByteArray.bytesLeft(), this.sampleSize - this.bytesRead);
        this.output.sampleData(paramParsableByteArray, i);
        this.bytesRead += i;
        if (this.bytesRead == this.sampleSize)
        {
          this.output.sampleMetadata(this.timeUs, 1, this.sampleSize, 0, null);
          this.timeUs += this.sampleDurationUs;
          this.state = 0;
        }
        break;
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
    this.bytesRead = 0;
    this.syncBytes = 0;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/extractor/ts/DtsReader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
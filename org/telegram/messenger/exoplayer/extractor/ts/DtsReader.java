package org.telegram.messenger.exoplayer.extractor.ts;

import org.telegram.messenger.exoplayer.MediaFormat;
import org.telegram.messenger.exoplayer.extractor.TrackOutput;
import org.telegram.messenger.exoplayer.util.DtsUtil;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;

final class DtsReader
  extends ElementaryStreamReader
{
  private static final int HEADER_SIZE = 15;
  private static final int STATE_FINDING_SYNC = 0;
  private static final int STATE_READING_HEADER = 1;
  private static final int STATE_READING_SAMPLE = 2;
  private static final int SYNC_VALUE = NUM;
  private static final int SYNC_VALUE_SIZE = 4;
  private int bytesRead;
  private final ParsableByteArray headerScratchBytes = new ParsableByteArray(new byte[15]);
  private MediaFormat mediaFormat;
  private long sampleDurationUs;
  private int sampleSize;
  private int state;
  private int syncBytes;
  private long timeUs;
  
  public DtsReader(TrackOutput paramTrackOutput)
  {
    super(paramTrackOutput);
    this.headerScratchBytes.data[0] = Byte.MAX_VALUE;
    this.headerScratchBytes.data[1] = -2;
    this.headerScratchBytes.data[2] = Byte.MIN_VALUE;
    this.headerScratchBytes.data[3] = 1;
    this.state = 0;
  }
  
  private boolean continueRead(ParsableByteArray paramParsableByteArray, byte[] paramArrayOfByte, int paramInt)
  {
    int i = Math.min(paramParsableByteArray.bytesLeft(), paramInt - this.bytesRead);
    paramParsableByteArray.readBytes(paramArrayOfByte, this.bytesRead, i);
    this.bytesRead += i;
    return this.bytesRead == paramInt;
  }
  
  private void parseHeader()
  {
    byte[] arrayOfByte = this.headerScratchBytes.data;
    if (this.mediaFormat == null)
    {
      this.mediaFormat = DtsUtil.parseDtsFormat(arrayOfByte, null, -1L, null);
      this.output.format(this.mediaFormat);
    }
    this.sampleSize = DtsUtil.getDtsFrameSize(arrayOfByte);
    this.sampleDurationUs = ((int)(1000000L * DtsUtil.parseDtsAudioSampleCount(arrayOfByte) / this.mediaFormat.sampleRate));
  }
  
  private boolean skipToNextSync(ParsableByteArray paramParsableByteArray)
  {
    boolean bool2 = false;
    do
    {
      bool1 = bool2;
      if (paramParsableByteArray.bytesLeft() <= 0) {
        break;
      }
      this.syncBytes <<= 8;
      this.syncBytes |= paramParsableByteArray.readUnsignedByte();
    } while (this.syncBytes != NUM);
    this.syncBytes = 0;
    boolean bool1 = true;
    return bool1;
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/extractor/ts/DtsReader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
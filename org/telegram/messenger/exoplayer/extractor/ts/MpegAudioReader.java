package org.telegram.messenger.exoplayer.extractor.ts;

import org.telegram.messenger.exoplayer.MediaFormat;
import org.telegram.messenger.exoplayer.extractor.TrackOutput;
import org.telegram.messenger.exoplayer.util.MpegAudioHeader;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;

final class MpegAudioReader
  extends ElementaryStreamReader
{
  private static final int HEADER_SIZE = 4;
  private static final int STATE_FINDING_HEADER = 0;
  private static final int STATE_READING_FRAME = 2;
  private static final int STATE_READING_HEADER = 1;
  private int frameBytesRead;
  private long frameDurationUs;
  private int frameSize;
  private boolean hasOutputFormat;
  private final MpegAudioHeader header;
  private final ParsableByteArray headerScratch = new ParsableByteArray(4);
  private boolean lastByteWasFF;
  private int state = 0;
  private long timeUs;
  
  public MpegAudioReader(TrackOutput paramTrackOutput)
  {
    super(paramTrackOutput);
    this.headerScratch.data[0] = -1;
    this.header = new MpegAudioHeader();
  }
  
  private void findHeader(ParsableByteArray paramParsableByteArray)
  {
    byte[] arrayOfByte = paramParsableByteArray.data;
    int i = paramParsableByteArray.getPosition();
    int k = paramParsableByteArray.limit();
    while (i < k)
    {
      boolean bool;
      if ((arrayOfByte[i] & 0xFF) == 255)
      {
        bool = true;
        if ((!this.lastByteWasFF) || ((arrayOfByte[i] & 0xE0) != 224)) {
          break label115;
        }
      }
      label115:
      for (int j = 1;; j = 0)
      {
        this.lastByteWasFF = bool;
        if (j == 0) {
          break label120;
        }
        paramParsableByteArray.setPosition(i + 1);
        this.lastByteWasFF = false;
        this.headerScratch.data[1] = arrayOfByte[i];
        this.frameBytesRead = 2;
        this.state = 1;
        return;
        bool = false;
        break;
      }
      label120:
      i += 1;
    }
    paramParsableByteArray.setPosition(k);
  }
  
  private void readFrameRemainder(ParsableByteArray paramParsableByteArray)
  {
    int i = Math.min(paramParsableByteArray.bytesLeft(), this.frameSize - this.frameBytesRead);
    this.output.sampleData(paramParsableByteArray, i);
    this.frameBytesRead += i;
    if (this.frameBytesRead < this.frameSize) {
      return;
    }
    this.output.sampleMetadata(this.timeUs, 1, this.frameSize, 0, null);
    this.timeUs += this.frameDurationUs;
    this.frameBytesRead = 0;
    this.state = 0;
  }
  
  private void readHeaderRemainder(ParsableByteArray paramParsableByteArray)
  {
    int i = Math.min(paramParsableByteArray.bytesLeft(), 4 - this.frameBytesRead);
    paramParsableByteArray.readBytes(this.headerScratch.data, this.frameBytesRead, i);
    this.frameBytesRead += i;
    if (this.frameBytesRead < 4) {
      return;
    }
    this.headerScratch.setPosition(0);
    if (!MpegAudioHeader.populateHeader(this.headerScratch.readInt(), this.header))
    {
      this.frameBytesRead = 0;
      this.state = 1;
      return;
    }
    this.frameSize = this.header.frameSize;
    if (!this.hasOutputFormat)
    {
      this.frameDurationUs = (1000000L * this.header.samplesPerFrame / this.header.sampleRate);
      paramParsableByteArray = MediaFormat.createAudioFormat(null, this.header.mimeType, -1, 4096, -1L, this.header.channels, this.header.sampleRate, null, null);
      this.output.format(paramParsableByteArray);
      this.hasOutputFormat = true;
    }
    this.headerScratch.setPosition(0);
    this.output.sampleData(this.headerScratch, 4);
    this.state = 2;
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/extractor/ts/MpegAudioReader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
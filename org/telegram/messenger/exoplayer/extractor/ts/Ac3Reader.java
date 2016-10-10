package org.telegram.messenger.exoplayer.extractor.ts;

import org.telegram.messenger.exoplayer.MediaFormat;
import org.telegram.messenger.exoplayer.extractor.TrackOutput;
import org.telegram.messenger.exoplayer.util.Ac3Util;
import org.telegram.messenger.exoplayer.util.ParsableBitArray;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;

final class Ac3Reader
  extends ElementaryStreamReader
{
  private static final int HEADER_SIZE = 8;
  private static final int STATE_FINDING_SYNC = 0;
  private static final int STATE_READING_HEADER = 1;
  private static final int STATE_READING_SAMPLE = 2;
  private int bytesRead;
  private final ParsableBitArray headerScratchBits;
  private final ParsableByteArray headerScratchBytes;
  private final boolean isEac3;
  private boolean lastByteWas0B;
  private MediaFormat mediaFormat;
  private long sampleDurationUs;
  private int sampleSize;
  private int state;
  private long timeUs;
  
  public Ac3Reader(TrackOutput paramTrackOutput, boolean paramBoolean)
  {
    super(paramTrackOutput);
    this.isEac3 = paramBoolean;
    this.headerScratchBits = new ParsableBitArray(new byte[8]);
    this.headerScratchBytes = new ParsableByteArray(this.headerScratchBits.data);
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
    MediaFormat localMediaFormat;
    if (this.mediaFormat == null)
    {
      if (this.isEac3)
      {
        localMediaFormat = Ac3Util.parseEac3SyncframeFormat(this.headerScratchBits, null, -1L, null);
        this.mediaFormat = localMediaFormat;
        this.output.format(this.mediaFormat);
      }
    }
    else
    {
      if (!this.isEac3) {
        break label124;
      }
      i = Ac3Util.parseEAc3SyncframeSize(this.headerScratchBits.data);
      label63:
      this.sampleSize = i;
      if (!this.isEac3) {
        break label138;
      }
    }
    label124:
    label138:
    for (int i = Ac3Util.parseEAc3SyncframeAudioSampleCount(this.headerScratchBits.data);; i = Ac3Util.getAc3SyncframeAudioSampleCount())
    {
      this.sampleDurationUs = ((int)(1000000L * i / this.mediaFormat.sampleRate));
      return;
      localMediaFormat = Ac3Util.parseAc3SyncframeFormat(this.headerScratchBits, null, -1L, null);
      break;
      i = Ac3Util.parseAc3SyncframeSize(this.headerScratchBits.data);
      break label63;
    }
  }
  
  private boolean skipToNextSync(ParsableByteArray paramParsableByteArray)
  {
    if (paramParsableByteArray.bytesLeft() > 0)
    {
      if (!this.lastByteWas0B)
      {
        if (paramParsableByteArray.readUnsignedByte() == 11) {}
        for (bool = true;; bool = false)
        {
          this.lastByteWas0B = bool;
          break;
        }
      }
      int i = paramParsableByteArray.readUnsignedByte();
      if (i == 119)
      {
        this.lastByteWas0B = false;
        return true;
      }
      if (i == 11) {}
      for (boolean bool = true;; bool = false)
      {
        this.lastByteWas0B = bool;
        break;
      }
    }
    return false;
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
          this.state = 1;
          this.headerScratchBytes.data[0] = 11;
          this.headerScratchBytes.data[1] = 119;
          this.bytesRead = 2;
        }
        break;
      case 1: 
        if (continueRead(paramParsableByteArray, this.headerScratchBytes.data, 8))
        {
          parseHeader();
          this.headerScratchBytes.setPosition(0);
          this.output.sampleData(this.headerScratchBytes, 8);
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
    this.lastByteWas0B = false;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/extractor/ts/Ac3Reader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
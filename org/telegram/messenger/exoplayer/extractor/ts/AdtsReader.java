package org.telegram.messenger.exoplayer.extractor.ts;

import android.util.Log;
import android.util.Pair;
import java.util.Arrays;
import java.util.Collections;
import org.telegram.messenger.exoplayer.MediaFormat;
import org.telegram.messenger.exoplayer.extractor.TrackOutput;
import org.telegram.messenger.exoplayer.util.CodecSpecificDataUtil;
import org.telegram.messenger.exoplayer.util.ParsableBitArray;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;

final class AdtsReader
  extends ElementaryStreamReader
{
  private static final int CRC_SIZE = 2;
  private static final int HEADER_SIZE = 5;
  private static final int ID3_HEADER_SIZE = 10;
  private static final byte[] ID3_IDENTIFIER = { 73, 68, 51 };
  private static final int ID3_SIZE_OFFSET = 6;
  private static final int MATCH_STATE_FF = 512;
  private static final int MATCH_STATE_I = 768;
  private static final int MATCH_STATE_ID = 1024;
  private static final int MATCH_STATE_START = 256;
  private static final int MATCH_STATE_VALUE_SHIFT = 8;
  private static final int STATE_FINDING_SAMPLE = 0;
  private static final int STATE_READING_ADTS_HEADER = 2;
  private static final int STATE_READING_ID3_HEADER = 1;
  private static final int STATE_READING_SAMPLE = 3;
  private static final String TAG = "AdtsReader";
  private final ParsableBitArray adtsScratch;
  private int bytesRead;
  private TrackOutput currentOutput;
  private long currentSampleDuration;
  private boolean hasCrc;
  private boolean hasOutputFormat;
  private final ParsableByteArray id3HeaderBuffer;
  private final TrackOutput id3Output;
  private int matchState;
  private long sampleDurationUs;
  private int sampleSize;
  private int state;
  private long timeUs;
  
  public AdtsReader(TrackOutput paramTrackOutput1, TrackOutput paramTrackOutput2)
  {
    super(paramTrackOutput1);
    this.id3Output = paramTrackOutput2;
    paramTrackOutput2.format(MediaFormat.createId3Format());
    this.adtsScratch = new ParsableBitArray(new byte[7]);
    this.id3HeaderBuffer = new ParsableByteArray(Arrays.copyOf(ID3_IDENTIFIER, 10));
    setFindingSampleState();
  }
  
  private boolean continueRead(ParsableByteArray paramParsableByteArray, byte[] paramArrayOfByte, int paramInt)
  {
    int i = Math.min(paramParsableByteArray.bytesLeft(), paramInt - this.bytesRead);
    paramParsableByteArray.readBytes(paramArrayOfByte, this.bytesRead, i);
    this.bytesRead += i;
    return this.bytesRead == paramInt;
  }
  
  private void findNextSample(ParsableByteArray paramParsableByteArray)
  {
    byte[] arrayOfByte = paramParsableByteArray.data;
    int i = paramParsableByteArray.getPosition();
    int k = paramParsableByteArray.limit();
    if (i < k)
    {
      int j = i + 1;
      i = arrayOfByte[i] & 0xFF;
      if ((this.matchState == 512) && (i >= 240) && (i != 255))
      {
        if ((i & 0x1) == 0) {}
        for (boolean bool = true;; bool = false)
        {
          this.hasCrc = bool;
          setReadingAdtsHeaderState();
          paramParsableByteArray.setPosition(j);
          return;
        }
      }
      switch (this.matchState | i)
      {
      default: 
        i = j;
        if (this.matchState != 256)
        {
          this.matchState = 256;
          i = j - 1;
        }
      case 511: 
      case 329: 
      case 836: 
        for (;;)
        {
          break;
          this.matchState = 512;
          i = j;
          continue;
          this.matchState = 768;
          i = j;
          continue;
          this.matchState = 1024;
          i = j;
        }
      }
      setReadingId3HeaderState();
      paramParsableByteArray.setPosition(j);
      return;
    }
    paramParsableByteArray.setPosition(i);
  }
  
  private void parseAdtsHeader()
  {
    this.adtsScratch.setPosition(0);
    int j;
    int i;
    if (!this.hasOutputFormat)
    {
      j = this.adtsScratch.readBits(2) + 1;
      i = j;
      if (j != 2)
      {
        Log.w("AdtsReader", "Detected audio object type: " + j + ", but assuming AAC LC.");
        i = 2;
      }
      j = this.adtsScratch.readBits(4);
      this.adtsScratch.skipBits(1);
      Object localObject = CodecSpecificDataUtil.buildAacAudioSpecificConfig(i, j, this.adtsScratch.readBits(3));
      Pair localPair = CodecSpecificDataUtil.parseAacAudioSpecificConfig((byte[])localObject);
      localObject = MediaFormat.createAudioFormat(null, "audio/mp4a-latm", -1, -1, -1L, ((Integer)localPair.second).intValue(), ((Integer)localPair.first).intValue(), Collections.singletonList(localObject), null);
      this.sampleDurationUs = (1024000000L / ((MediaFormat)localObject).sampleRate);
      this.output.format((MediaFormat)localObject);
      this.hasOutputFormat = true;
    }
    for (;;)
    {
      this.adtsScratch.skipBits(4);
      j = this.adtsScratch.readBits(13) - 2 - 5;
      i = j;
      if (this.hasCrc) {
        i = j - 2;
      }
      setReadingSampleState(this.output, this.sampleDurationUs, 0, i);
      return;
      this.adtsScratch.skipBits(10);
    }
  }
  
  private void parseId3Header()
  {
    this.id3Output.sampleData(this.id3HeaderBuffer, 10);
    this.id3HeaderBuffer.setPosition(6);
    setReadingSampleState(this.id3Output, 0L, 10, this.id3HeaderBuffer.readSynchSafeInt() + 10);
  }
  
  private void readSample(ParsableByteArray paramParsableByteArray)
  {
    int i = Math.min(paramParsableByteArray.bytesLeft(), this.sampleSize - this.bytesRead);
    this.currentOutput.sampleData(paramParsableByteArray, i);
    this.bytesRead += i;
    if (this.bytesRead == this.sampleSize)
    {
      this.currentOutput.sampleMetadata(this.timeUs, 1, this.sampleSize, 0, null);
      this.timeUs += this.currentSampleDuration;
      setFindingSampleState();
    }
  }
  
  private void setFindingSampleState()
  {
    this.state = 0;
    this.bytesRead = 0;
    this.matchState = 256;
  }
  
  private void setReadingAdtsHeaderState()
  {
    this.state = 2;
    this.bytesRead = 0;
  }
  
  private void setReadingId3HeaderState()
  {
    this.state = 1;
    this.bytesRead = ID3_IDENTIFIER.length;
    this.sampleSize = 0;
    this.id3HeaderBuffer.setPosition(0);
  }
  
  private void setReadingSampleState(TrackOutput paramTrackOutput, long paramLong, int paramInt1, int paramInt2)
  {
    this.state = 3;
    this.bytesRead = paramInt1;
    this.currentOutput = paramTrackOutput;
    this.currentSampleDuration = paramLong;
    this.sampleSize = paramInt2;
  }
  
  public void consume(ParsableByteArray paramParsableByteArray)
  {
    while (paramParsableByteArray.bytesLeft() > 0) {
      switch (this.state)
      {
      default: 
        break;
      case 0: 
        findNextSample(paramParsableByteArray);
        break;
      case 1: 
        if (continueRead(paramParsableByteArray, this.id3HeaderBuffer.data, 10)) {
          parseId3Header();
        }
        break;
      case 2: 
        if (this.hasCrc) {}
        for (int i = 7; continueRead(paramParsableByteArray, this.adtsScratch.data, i); i = 5)
        {
          parseAdtsHeader();
          break;
        }
      case 3: 
        readSample(paramParsableByteArray);
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
    setFindingSampleState();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/extractor/ts/AdtsReader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
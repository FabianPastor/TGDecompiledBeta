package org.telegram.messenger.exoplayer2.extractor.ts;

import android.util.Pair;
import java.util.Collections;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput;
import org.telegram.messenger.exoplayer2.util.CodecSpecificDataUtil;
import org.telegram.messenger.exoplayer2.util.ParsableBitArray;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

public final class LatmReader
  implements ElementaryStreamReader
{
  private static final int INITIAL_BUFFER_SIZE = 1024;
  private static final int STATE_FINDING_SYNC_1 = 0;
  private static final int STATE_FINDING_SYNC_2 = 1;
  private static final int STATE_READING_HEADER = 2;
  private static final int STATE_READING_SAMPLE = 3;
  private static final int SYNC_BYTE_FIRST = 86;
  private static final int SYNC_BYTE_SECOND = 224;
  private int audioMuxVersionA;
  private int bytesRead;
  private int channelCount;
  private Format format;
  private String formatId;
  private int frameLengthType;
  private final String language;
  private int numSubframes;
  private long otherDataLenBits;
  private boolean otherDataPresent;
  private TrackOutput output;
  private final ParsableBitArray sampleBitArray;
  private final ParsableByteArray sampleDataBuffer;
  private long sampleDurationUs;
  private int sampleRateHz;
  private int sampleSize;
  private int secondHeaderByte;
  private int state;
  private boolean streamMuxRead;
  private long timeUs;
  
  public LatmReader(String paramString)
  {
    this.language = paramString;
    this.sampleDataBuffer = new ParsableByteArray(1024);
    this.sampleBitArray = new ParsableBitArray(this.sampleDataBuffer.data);
  }
  
  private static long latmGetValue(ParsableBitArray paramParsableBitArray)
  {
    return paramParsableBitArray.readBits((paramParsableBitArray.readBits(2) + 1) * 8);
  }
  
  private void parseAudioMuxElement(ParsableBitArray paramParsableBitArray)
    throws ParserException
  {
    if (!paramParsableBitArray.readBit())
    {
      this.streamMuxRead = true;
      parseStreamMuxConfig(paramParsableBitArray);
    }
    while (this.audioMuxVersionA == 0) {
      if (this.numSubframes != 0)
      {
        throw new ParserException();
        if (this.streamMuxRead) {
          break;
        }
      }
      else
      {
        for (;;)
        {
          return;
          parsePayloadMux(paramParsableBitArray, parsePayloadLengthInfo(paramParsableBitArray));
          if (this.otherDataPresent) {
            paramParsableBitArray.skipBits((int)this.otherDataLenBits);
          }
        }
      }
    }
    throw new ParserException();
  }
  
  private int parseAudioSpecificConfig(ParsableBitArray paramParsableBitArray)
    throws ParserException
  {
    int i = paramParsableBitArray.bitsLeft();
    Pair localPair = CodecSpecificDataUtil.parseAacAudioSpecificConfig(paramParsableBitArray, true);
    this.sampleRateHz = ((Integer)localPair.first).intValue();
    this.channelCount = ((Integer)localPair.second).intValue();
    return i - paramParsableBitArray.bitsLeft();
  }
  
  private void parseFrameLength(ParsableBitArray paramParsableBitArray)
  {
    this.frameLengthType = paramParsableBitArray.readBits(3);
    switch (this.frameLengthType)
    {
    }
    for (;;)
    {
      return;
      paramParsableBitArray.skipBits(8);
      continue;
      paramParsableBitArray.skipBits(9);
      continue;
      paramParsableBitArray.skipBits(6);
      continue;
      paramParsableBitArray.skipBits(1);
    }
  }
  
  private int parsePayloadLengthInfo(ParsableBitArray paramParsableBitArray)
    throws ParserException
  {
    int i = 0;
    if (this.frameLengthType == 0)
    {
      int j;
      int k;
      do
      {
        j = paramParsableBitArray.readBits(8);
        k = i + j;
        i = k;
      } while (j == 255);
      return k;
    }
    throw new ParserException();
  }
  
  private void parsePayloadMux(ParsableBitArray paramParsableBitArray, int paramInt)
  {
    int i = paramParsableBitArray.getPosition();
    if ((i & 0x7) == 0) {
      this.sampleDataBuffer.setPosition(i >> 3);
    }
    for (;;)
    {
      this.output.sampleData(this.sampleDataBuffer, paramInt);
      this.output.sampleMetadata(this.timeUs, 1, paramInt, 0, null);
      this.timeUs += this.sampleDurationUs;
      return;
      paramParsableBitArray.readBits(this.sampleDataBuffer.data, 0, paramInt * 8);
      this.sampleDataBuffer.setPosition(0);
    }
  }
  
  private void parseStreamMuxConfig(ParsableBitArray paramParsableBitArray)
    throws ParserException
  {
    int i = paramParsableBitArray.readBits(1);
    if (i == 1) {}
    for (int j = paramParsableBitArray.readBits(1);; j = 0)
    {
      this.audioMuxVersionA = j;
      if (this.audioMuxVersionA != 0) {
        break label320;
      }
      if (i == 1) {
        latmGetValue(paramParsableBitArray);
      }
      if (paramParsableBitArray.readBit()) {
        break;
      }
      throw new ParserException();
    }
    this.numSubframes = paramParsableBitArray.readBits(6);
    int k = paramParsableBitArray.readBits(4);
    j = paramParsableBitArray.readBits(3);
    if ((k != 0) || (j != 0)) {
      throw new ParserException();
    }
    if (i == 0)
    {
      j = paramParsableBitArray.getPosition();
      k = parseAudioSpecificConfig(paramParsableBitArray);
      paramParsableBitArray.setPosition(j);
      Object localObject = new byte[(k + 7) / 8];
      paramParsableBitArray.readBits((byte[])localObject, 0, k);
      localObject = Format.createAudioSampleFormat(this.formatId, "audio/mp4a-latm", null, -1, -1, this.channelCount, this.sampleRateHz, Collections.singletonList(localObject), null, 0, this.language);
      if (!((Format)localObject).equals(this.format))
      {
        this.format = ((Format)localObject);
        this.sampleDurationUs = (1024000000L / ((Format)localObject).sampleRate);
        this.output.format((Format)localObject);
      }
      parseFrameLength(paramParsableBitArray);
      this.otherDataPresent = paramParsableBitArray.readBit();
      this.otherDataLenBits = 0L;
      if (this.otherDataPresent)
      {
        if (i != 1) {
          break label287;
        }
        this.otherDataLenBits = latmGetValue(paramParsableBitArray);
      }
    }
    for (;;)
    {
      if (paramParsableBitArray.readBit()) {
        paramParsableBitArray.skipBits(8);
      }
      return;
      paramParsableBitArray.skipBits((int)latmGetValue(paramParsableBitArray) - parseAudioSpecificConfig(paramParsableBitArray));
      break;
      label287:
      boolean bool;
      do
      {
        bool = paramParsableBitArray.readBit();
        this.otherDataLenBits = ((this.otherDataLenBits << 8) + paramParsableBitArray.readBits(8));
      } while (bool);
    }
    label320:
    throw new ParserException();
  }
  
  private void resetBufferForSize(int paramInt)
  {
    this.sampleDataBuffer.reset(paramInt);
    this.sampleBitArray.reset(this.sampleDataBuffer.data);
  }
  
  public void consume(ParsableByteArray paramParsableByteArray)
    throws ParserException
  {
    while (paramParsableByteArray.bytesLeft() > 0)
    {
      int i;
      switch (this.state)
      {
      default: 
        break;
      case 0: 
        if (paramParsableByteArray.readUnsignedByte() == 86) {
          this.state = 1;
        }
        break;
      case 1: 
        i = paramParsableByteArray.readUnsignedByte();
        if ((i & 0xE0) == 224)
        {
          this.secondHeaderByte = i;
          this.state = 2;
        }
        else if (i != 86)
        {
          this.state = 0;
        }
        break;
      case 2: 
        this.sampleSize = ((this.secondHeaderByte & 0xFF1F) << 8 | paramParsableByteArray.readUnsignedByte());
        if (this.sampleSize > this.sampleDataBuffer.data.length) {
          resetBufferForSize(this.sampleSize);
        }
        this.bytesRead = 0;
        this.state = 3;
        break;
      case 3: 
        i = Math.min(paramParsableByteArray.bytesLeft(), this.sampleSize - this.bytesRead);
        paramParsableByteArray.readBytes(this.sampleBitArray.data, this.bytesRead, i);
        this.bytesRead += i;
        if (this.bytesRead == this.sampleSize)
        {
          this.sampleBitArray.setPosition(0);
          parseAudioMuxElement(this.sampleBitArray);
          this.state = 0;
        }
        break;
      }
    }
  }
  
  public void createTracks(ExtractorOutput paramExtractorOutput, TsPayloadReader.TrackIdGenerator paramTrackIdGenerator)
  {
    paramTrackIdGenerator.generateNewId();
    this.output = paramExtractorOutput.track(paramTrackIdGenerator.getTrackId(), 1);
    this.formatId = paramTrackIdGenerator.getFormatId();
  }
  
  public void packetFinished() {}
  
  public void packetStarted(long paramLong, boolean paramBoolean)
  {
    this.timeUs = paramLong;
  }
  
  public void seek()
  {
    this.state = 0;
    this.streamMuxRead = false;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/extractor/ts/LatmReader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
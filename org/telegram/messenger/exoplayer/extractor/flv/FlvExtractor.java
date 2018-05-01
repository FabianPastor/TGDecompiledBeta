package org.telegram.messenger.exoplayer.extractor.flv;

import java.io.IOException;
import org.telegram.messenger.exoplayer.extractor.Extractor;
import org.telegram.messenger.exoplayer.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer.extractor.PositionHolder;
import org.telegram.messenger.exoplayer.extractor.SeekMap;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;
import org.telegram.messenger.exoplayer.util.Util;

public final class FlvExtractor
  implements Extractor, SeekMap
{
  private static final int FLV_HEADER_SIZE = 9;
  private static final int FLV_TAG = Util.getIntegerCodeForString("FLV");
  private static final int FLV_TAG_HEADER_SIZE = 11;
  private static final int STATE_READING_FLV_HEADER = 1;
  private static final int STATE_READING_TAG_DATA = 4;
  private static final int STATE_READING_TAG_HEADER = 3;
  private static final int STATE_SKIPPING_TO_TAG_HEADER = 2;
  private static final int TAG_TYPE_AUDIO = 8;
  private static final int TAG_TYPE_SCRIPT_DATA = 18;
  private static final int TAG_TYPE_VIDEO = 9;
  private AudioTagPayloadReader audioReader;
  private int bytesToNextTagHeader;
  private ExtractorOutput extractorOutput;
  private final ParsableByteArray headerBuffer = new ParsableByteArray(9);
  private ScriptTagPayloadReader metadataReader;
  private int parserState = 1;
  private final ParsableByteArray scratch = new ParsableByteArray(4);
  private final ParsableByteArray tagData = new ParsableByteArray();
  public int tagDataSize;
  private final ParsableByteArray tagHeaderBuffer = new ParsableByteArray(11);
  public long tagTimestampUs;
  public int tagType;
  private VideoTagPayloadReader videoReader;
  
  private ParsableByteArray prepareTagData(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    if (this.tagDataSize > this.tagData.capacity()) {
      this.tagData.reset(new byte[Math.max(this.tagData.capacity() * 2, this.tagDataSize)], 0);
    }
    for (;;)
    {
      this.tagData.setLimit(this.tagDataSize);
      paramExtractorInput.readFully(this.tagData.data, 0, this.tagDataSize);
      return this.tagData;
      this.tagData.setPosition(0);
    }
  }
  
  private boolean readFlvHeader(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    if (!paramExtractorInput.readFully(this.headerBuffer.data, 0, 9, true)) {
      return false;
    }
    this.headerBuffer.setPosition(0);
    this.headerBuffer.skipBytes(4);
    int j = this.headerBuffer.readUnsignedByte();
    int i;
    if ((j & 0x4) != 0)
    {
      i = 1;
      if ((j & 0x1) == 0) {
        break label194;
      }
    }
    label194:
    for (j = 1;; j = 0)
    {
      if ((i != 0) && (this.audioReader == null)) {
        this.audioReader = new AudioTagPayloadReader(this.extractorOutput.track(8));
      }
      if ((j != 0) && (this.videoReader == null)) {
        this.videoReader = new VideoTagPayloadReader(this.extractorOutput.track(9));
      }
      if (this.metadataReader == null) {
        this.metadataReader = new ScriptTagPayloadReader(null);
      }
      this.extractorOutput.endTracks();
      this.extractorOutput.seekMap(this);
      this.bytesToNextTagHeader = (this.headerBuffer.readInt() - 9 + 4);
      this.parserState = 2;
      return true;
      i = 0;
      break;
    }
  }
  
  private boolean readTagData(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    boolean bool2 = true;
    boolean bool1;
    if ((this.tagType == 8) && (this.audioReader != null))
    {
      this.audioReader.consume(prepareTagData(paramExtractorInput), this.tagTimestampUs);
      bool1 = bool2;
    }
    for (;;)
    {
      this.bytesToNextTagHeader = 4;
      this.parserState = 2;
      return bool1;
      if ((this.tagType == 9) && (this.videoReader != null))
      {
        this.videoReader.consume(prepareTagData(paramExtractorInput), this.tagTimestampUs);
        bool1 = bool2;
      }
      else if ((this.tagType == 18) && (this.metadataReader != null))
      {
        this.metadataReader.consume(prepareTagData(paramExtractorInput), this.tagTimestampUs);
        bool1 = bool2;
        if (this.metadataReader.getDurationUs() != -1L)
        {
          if (this.audioReader != null) {
            this.audioReader.setDurationUs(this.metadataReader.getDurationUs());
          }
          bool1 = bool2;
          if (this.videoReader != null)
          {
            this.videoReader.setDurationUs(this.metadataReader.getDurationUs());
            bool1 = bool2;
          }
        }
      }
      else
      {
        paramExtractorInput.skipFully(this.tagDataSize);
        bool1 = false;
      }
    }
  }
  
  private boolean readTagHeader(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    if (!paramExtractorInput.readFully(this.tagHeaderBuffer.data, 0, 11, true)) {
      return false;
    }
    this.tagHeaderBuffer.setPosition(0);
    this.tagType = this.tagHeaderBuffer.readUnsignedByte();
    this.tagDataSize = this.tagHeaderBuffer.readUnsignedInt24();
    this.tagTimestampUs = this.tagHeaderBuffer.readUnsignedInt24();
    this.tagTimestampUs = ((this.tagHeaderBuffer.readUnsignedByte() << 24 | this.tagTimestampUs) * 1000L);
    this.tagHeaderBuffer.skipBytes(3);
    this.parserState = 4;
    return true;
  }
  
  private void skipToTagHeader(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    paramExtractorInput.skipFully(this.bytesToNextTagHeader);
    this.bytesToNextTagHeader = 0;
    this.parserState = 3;
  }
  
  public long getPosition(long paramLong)
  {
    return 0L;
  }
  
  public void init(ExtractorOutput paramExtractorOutput)
  {
    this.extractorOutput = paramExtractorOutput;
  }
  
  public boolean isSeekable()
  {
    return false;
  }
  
  public int read(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
    throws IOException, InterruptedException
  {
    do
    {
      do
      {
        for (;;)
        {
          switch (this.parserState)
          {
          default: 
            break;
          case 1: 
            if (!readFlvHeader(paramExtractorInput)) {
              return -1;
            }
            break;
          case 2: 
            skipToTagHeader(paramExtractorInput);
          }
        }
      } while (readTagHeader(paramExtractorInput));
      return -1;
    } while (!readTagData(paramExtractorInput));
    return 0;
  }
  
  public void release() {}
  
  public void seek()
  {
    this.parserState = 1;
    this.bytesToNextTagHeader = 0;
  }
  
  public boolean sniff(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    paramExtractorInput.peekFully(this.scratch.data, 0, 3);
    this.scratch.setPosition(0);
    if (this.scratch.readUnsignedInt24() != FLV_TAG) {}
    do
    {
      do
      {
        return false;
        paramExtractorInput.peekFully(this.scratch.data, 0, 2);
        this.scratch.setPosition(0);
      } while ((this.scratch.readUnsignedShort() & 0xFA) != 0);
      paramExtractorInput.peekFully(this.scratch.data, 0, 4);
      this.scratch.setPosition(0);
      int i = this.scratch.readInt();
      paramExtractorInput.resetPeekPosition();
      paramExtractorInput.advancePeekPosition(i);
      paramExtractorInput.peekFully(this.scratch.data, 0, 4);
      this.scratch.setPosition(0);
    } while (this.scratch.readInt() != 0);
    return true;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/extractor/flv/FlvExtractor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
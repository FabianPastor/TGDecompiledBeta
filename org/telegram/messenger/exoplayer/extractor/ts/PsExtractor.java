package org.telegram.messenger.exoplayer.extractor.ts;

import android.util.SparseArray;
import java.io.IOException;
import org.telegram.messenger.exoplayer.extractor.Extractor;
import org.telegram.messenger.exoplayer.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer.extractor.PositionHolder;
import org.telegram.messenger.exoplayer.extractor.SeekMap;
import org.telegram.messenger.exoplayer.util.ParsableBitArray;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;

public final class PsExtractor
  implements Extractor
{
  public static final int AUDIO_STREAM = 192;
  public static final int AUDIO_STREAM_MASK = 224;
  private static final long MAX_SEARCH_LENGTH = 1048576L;
  private static final int MPEG_PROGRAM_END_CODE = 441;
  private static final int PACKET_START_CODE_PREFIX = 1;
  private static final int PACK_START_CODE = 442;
  public static final int PRIVATE_STREAM_1 = 189;
  private static final int SYSTEM_HEADER_START_CODE = 443;
  public static final int VIDEO_STREAM = 224;
  public static final int VIDEO_STREAM_MASK = 240;
  private boolean foundAllTracks;
  private boolean foundAudioTrack;
  private boolean foundVideoTrack;
  private ExtractorOutput output;
  private final ParsableByteArray psPacketBuffer;
  private final SparseArray<PesReader> psPayloadReaders;
  private final PtsTimestampAdjuster ptsTimestampAdjuster;
  
  public PsExtractor()
  {
    this(new PtsTimestampAdjuster(0L));
  }
  
  public PsExtractor(PtsTimestampAdjuster paramPtsTimestampAdjuster)
  {
    this.ptsTimestampAdjuster = paramPtsTimestampAdjuster;
    this.psPacketBuffer = new ParsableByteArray(4096);
    this.psPayloadReaders = new SparseArray();
  }
  
  public void init(ExtractorOutput paramExtractorOutput)
  {
    this.output = paramExtractorOutput;
    paramExtractorOutput.seekMap(SeekMap.UNSEEKABLE);
  }
  
  public int read(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
    throws IOException, InterruptedException
  {
    if (!paramExtractorInput.peekFully(this.psPacketBuffer.data, 0, 4, true)) {
      return -1;
    }
    this.psPacketBuffer.setPosition(0);
    int i = this.psPacketBuffer.readInt();
    if (i == 441) {
      return -1;
    }
    if (i == 442)
    {
      paramExtractorInput.peekFully(this.psPacketBuffer.data, 0, 10);
      this.psPacketBuffer.setPosition(0);
      this.psPacketBuffer.skipBytes(9);
      paramExtractorInput.skipFully((this.psPacketBuffer.readUnsignedByte() & 0x7) + 14);
      return 0;
    }
    if (i == 443)
    {
      paramExtractorInput.peekFully(this.psPacketBuffer.data, 0, 2);
      this.psPacketBuffer.setPosition(0);
      paramExtractorInput.skipFully(this.psPacketBuffer.readUnsignedShort() + 6);
      return 0;
    }
    if ((i & 0xFF00) >> 8 != 1)
    {
      paramExtractorInput.skipFully(1);
      return 0;
    }
    i &= 0xFF;
    PesReader localPesReader2 = (PesReader)this.psPayloadReaders.get(i);
    paramPositionHolder = localPesReader2;
    PesReader localPesReader1;
    if (!this.foundAllTracks)
    {
      localPesReader1 = localPesReader2;
      if (localPesReader2 == null)
      {
        localPesReader1 = null;
        if ((this.foundAudioTrack) || (i != 189)) {
          break label381;
        }
        paramPositionHolder = new Ac3Reader(this.output.track(i), false);
        this.foundAudioTrack = true;
        localPesReader1 = localPesReader2;
        if (paramPositionHolder != null)
        {
          localPesReader1 = new PesReader(paramPositionHolder, this.ptsTimestampAdjuster);
          this.psPayloadReaders.put(i, localPesReader1);
        }
      }
      if ((!this.foundAudioTrack) || (!this.foundVideoTrack))
      {
        paramPositionHolder = localPesReader1;
        if (paramExtractorInput.getPosition() <= 1048576L) {}
      }
      else
      {
        this.foundAllTracks = true;
        this.output.endTracks();
        paramPositionHolder = localPesReader1;
      }
    }
    paramExtractorInput.peekFully(this.psPacketBuffer.data, 0, 2);
    this.psPacketBuffer.setPosition(0);
    i = this.psPacketBuffer.readUnsignedShort() + 6;
    if (paramPositionHolder == null) {
      paramExtractorInput.skipFully(i);
    }
    for (;;)
    {
      return 0;
      label381:
      if ((!this.foundAudioTrack) && ((i & 0xE0) == 192))
      {
        paramPositionHolder = new MpegAudioReader(this.output.track(i));
        this.foundAudioTrack = true;
        break;
      }
      paramPositionHolder = localPesReader1;
      if (this.foundVideoTrack) {
        break;
      }
      paramPositionHolder = localPesReader1;
      if ((i & 0xF0) != 224) {
        break;
      }
      paramPositionHolder = new H262Reader(this.output.track(i));
      this.foundVideoTrack = true;
      break;
      if (this.psPacketBuffer.capacity() < i) {
        this.psPacketBuffer.reset(new byte[i], i);
      }
      paramExtractorInput.readFully(this.psPacketBuffer.data, 0, i);
      this.psPacketBuffer.setPosition(6);
      this.psPacketBuffer.setLimit(i);
      paramPositionHolder.consume(this.psPacketBuffer, this.output);
      this.psPacketBuffer.setLimit(this.psPacketBuffer.capacity());
    }
  }
  
  public void release() {}
  
  public void seek()
  {
    this.ptsTimestampAdjuster.reset();
    int i = 0;
    while (i < this.psPayloadReaders.size())
    {
      ((PesReader)this.psPayloadReaders.valueAt(i)).seek();
      i += 1;
    }
  }
  
  public boolean sniff(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    boolean bool = true;
    byte[] arrayOfByte = new byte[14];
    paramExtractorInput.peekFully(arrayOfByte, 0, 14);
    if (442 != ((arrayOfByte[0] & 0xFF) << 24 | (arrayOfByte[1] & 0xFF) << 16 | (arrayOfByte[2] & 0xFF) << 8 | arrayOfByte[3] & 0xFF)) {}
    while (((arrayOfByte[4] & 0xC4) != 68) || ((arrayOfByte[6] & 0x4) != 4) || ((arrayOfByte[8] & 0x4) != 4) || ((arrayOfByte[9] & 0x1) != 1) || ((arrayOfByte[12] & 0x3) != 3)) {
      return false;
    }
    paramExtractorInput.advancePeekPosition(arrayOfByte[13] & 0x7);
    paramExtractorInput.peekFully(arrayOfByte, 0, 3);
    if (1 == ((arrayOfByte[0] & 0xFF) << 16 | (arrayOfByte[1] & 0xFF) << 8 | arrayOfByte[2] & 0xFF)) {}
    for (;;)
    {
      return bool;
      bool = false;
    }
  }
  
  private static final class PesReader
  {
    private static final int PES_SCRATCH_SIZE = 64;
    private boolean dtsFlag;
    private int extendedHeaderLength;
    private final ElementaryStreamReader pesPayloadReader;
    private final ParsableBitArray pesScratch;
    private boolean ptsFlag;
    private final PtsTimestampAdjuster ptsTimestampAdjuster;
    private boolean seenFirstDts;
    private long timeUs;
    
    public PesReader(ElementaryStreamReader paramElementaryStreamReader, PtsTimestampAdjuster paramPtsTimestampAdjuster)
    {
      this.pesPayloadReader = paramElementaryStreamReader;
      this.ptsTimestampAdjuster = paramPtsTimestampAdjuster;
      this.pesScratch = new ParsableBitArray(new byte[64]);
    }
    
    private void parseHeader()
    {
      this.pesScratch.skipBits(8);
      this.ptsFlag = this.pesScratch.readBit();
      this.dtsFlag = this.pesScratch.readBit();
      this.pesScratch.skipBits(6);
      this.extendedHeaderLength = this.pesScratch.readBits(8);
    }
    
    private void parseHeaderExtension()
    {
      this.timeUs = 0L;
      if (this.ptsFlag)
      {
        this.pesScratch.skipBits(4);
        long l1 = this.pesScratch.readBits(3);
        this.pesScratch.skipBits(1);
        long l2 = this.pesScratch.readBits(15) << 15;
        this.pesScratch.skipBits(1);
        long l3 = this.pesScratch.readBits(15);
        this.pesScratch.skipBits(1);
        if ((!this.seenFirstDts) && (this.dtsFlag))
        {
          this.pesScratch.skipBits(4);
          long l4 = this.pesScratch.readBits(3);
          this.pesScratch.skipBits(1);
          long l5 = this.pesScratch.readBits(15) << 15;
          this.pesScratch.skipBits(1);
          long l6 = this.pesScratch.readBits(15);
          this.pesScratch.skipBits(1);
          this.ptsTimestampAdjuster.adjustTimestamp(l4 << 30 | l5 | l6);
          this.seenFirstDts = true;
        }
        this.timeUs = this.ptsTimestampAdjuster.adjustTimestamp(l1 << 30 | l2 | l3);
      }
    }
    
    public void consume(ParsableByteArray paramParsableByteArray, ExtractorOutput paramExtractorOutput)
    {
      paramParsableByteArray.readBytes(this.pesScratch.data, 0, 3);
      this.pesScratch.setPosition(0);
      parseHeader();
      paramParsableByteArray.readBytes(this.pesScratch.data, 0, this.extendedHeaderLength);
      this.pesScratch.setPosition(0);
      parseHeaderExtension();
      this.pesPayloadReader.packetStarted(this.timeUs, true);
      this.pesPayloadReader.consume(paramParsableByteArray);
      this.pesPayloadReader.packetFinished();
    }
    
    public void seek()
    {
      this.seenFirstDts = false;
      this.pesPayloadReader.seek();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/extractor/ts/PsExtractor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
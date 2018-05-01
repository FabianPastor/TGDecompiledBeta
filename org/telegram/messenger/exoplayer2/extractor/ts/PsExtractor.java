package org.telegram.messenger.exoplayer2.extractor.ts;

import android.util.SparseArray;
import java.io.IOException;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.extractor.Extractor;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.extractor.ExtractorsFactory;
import org.telegram.messenger.exoplayer2.extractor.PositionHolder;
import org.telegram.messenger.exoplayer2.extractor.SeekMap.Unseekable;
import org.telegram.messenger.exoplayer2.util.ParsableBitArray;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.TimestampAdjuster;

public final class PsExtractor
  implements Extractor
{
  public static final int AUDIO_STREAM = 192;
  public static final int AUDIO_STREAM_MASK = 224;
  public static final ExtractorsFactory FACTORY = new ExtractorsFactory()
  {
    public Extractor[] createExtractors()
    {
      return new Extractor[] { new PsExtractor() };
    }
  };
  private static final long MAX_SEARCH_LENGTH = 1048576L;
  private static final int MAX_STREAM_ID_PLUS_ONE = 256;
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
  private final TimestampAdjuster timestampAdjuster;
  
  public PsExtractor()
  {
    this(new TimestampAdjuster(0L));
  }
  
  public PsExtractor(TimestampAdjuster paramTimestampAdjuster)
  {
    this.timestampAdjuster = paramTimestampAdjuster;
    this.psPacketBuffer = new ParsableByteArray(4096);
    this.psPayloadReaders = new SparseArray();
  }
  
  public void init(ExtractorOutput paramExtractorOutput)
  {
    this.output = paramExtractorOutput;
    paramExtractorOutput.seekMap(new SeekMap.Unseekable(-9223372036854775807L));
  }
  
  public int read(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
    throws IOException, InterruptedException
  {
    if (!paramExtractorInput.peekFully(this.psPacketBuffer.data, 0, 4, true)) {
      i = -1;
    }
    for (;;)
    {
      return i;
      this.psPacketBuffer.setPosition(0);
      i = this.psPacketBuffer.readInt();
      if (i == 441)
      {
        i = -1;
      }
      else if (i == 442)
      {
        paramExtractorInput.peekFully(this.psPacketBuffer.data, 0, 10);
        this.psPacketBuffer.setPosition(9);
        paramExtractorInput.skipFully((this.psPacketBuffer.readUnsignedByte() & 0x7) + 14);
        i = 0;
      }
      else if (i == 443)
      {
        paramExtractorInput.peekFully(this.psPacketBuffer.data, 0, 2);
        this.psPacketBuffer.setPosition(0);
        paramExtractorInput.skipFully(this.psPacketBuffer.readUnsignedShort() + 6);
        i = 0;
      }
      else
      {
        if ((i & 0xFF00) >> 8 == 1) {
          break;
        }
        paramExtractorInput.skipFully(1);
        i = 0;
      }
    }
    i &= 0xFF;
    PesReader localPesReader = (PesReader)this.psPayloadReaders.get(i);
    paramPositionHolder = localPesReader;
    Object localObject;
    if (!this.foundAllTracks)
    {
      localObject = localPesReader;
      if (localPesReader == null)
      {
        localObject = null;
        if ((this.foundAudioTrack) || (i != 189)) {
          break label404;
        }
        paramPositionHolder = new Ac3Reader();
        this.foundAudioTrack = true;
        label250:
        localObject = localPesReader;
        if (paramPositionHolder != null)
        {
          localObject = new TsPayloadReader.TrackIdGenerator(i, 256);
          paramPositionHolder.createTracks(this.output, (TsPayloadReader.TrackIdGenerator)localObject);
          localObject = new PesReader(paramPositionHolder, this.timestampAdjuster);
          this.psPayloadReaders.put(i, localObject);
        }
      }
      if ((!this.foundAudioTrack) || (!this.foundVideoTrack))
      {
        paramPositionHolder = (PositionHolder)localObject;
        if (paramExtractorInput.getPosition() <= 1048576L) {}
      }
      else
      {
        this.foundAllTracks = true;
        this.output.endTracks();
        paramPositionHolder = (PositionHolder)localObject;
      }
    }
    paramExtractorInput.peekFully(this.psPacketBuffer.data, 0, 2);
    this.psPacketBuffer.setPosition(0);
    int i = this.psPacketBuffer.readUnsignedShort() + 6;
    if (paramPositionHolder == null) {
      paramExtractorInput.skipFully(i);
    }
    for (;;)
    {
      i = 0;
      break;
      label404:
      if ((!this.foundAudioTrack) && ((i & 0xE0) == 192))
      {
        paramPositionHolder = new MpegAudioReader();
        this.foundAudioTrack = true;
        break label250;
      }
      paramPositionHolder = (PositionHolder)localObject;
      if (this.foundVideoTrack) {
        break label250;
      }
      paramPositionHolder = (PositionHolder)localObject;
      if ((i & 0xF0) != 224) {
        break label250;
      }
      paramPositionHolder = new H262Reader();
      this.foundVideoTrack = true;
      break label250;
      this.psPacketBuffer.reset(i);
      paramExtractorInput.readFully(this.psPacketBuffer.data, 0, i);
      this.psPacketBuffer.setPosition(6);
      paramPositionHolder.consume(this.psPacketBuffer);
      this.psPacketBuffer.setLimit(this.psPacketBuffer.capacity());
    }
  }
  
  public void release() {}
  
  public void seek(long paramLong1, long paramLong2)
  {
    this.timestampAdjuster.reset();
    for (int i = 0; i < this.psPayloadReaders.size(); i++) {
      ((PesReader)this.psPayloadReaders.valueAt(i)).seek();
    }
  }
  
  public boolean sniff(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    boolean bool1 = true;
    boolean bool2 = false;
    byte[] arrayOfByte = new byte[14];
    paramExtractorInput.peekFully(arrayOfByte, 0, 14);
    if (442 != ((arrayOfByte[0] & 0xFF) << 24 | (arrayOfByte[1] & 0xFF) << 16 | (arrayOfByte[2] & 0xFF) << 8 | arrayOfByte[3] & 0xFF)) {
      bool3 = bool2;
    }
    do
    {
      do
      {
        do
        {
          do
          {
            do
            {
              return bool3;
              bool3 = bool2;
            } while ((arrayOfByte[4] & 0xC4) != 68);
            bool3 = bool2;
          } while ((arrayOfByte[6] & 0x4) != 4);
          bool3 = bool2;
        } while ((arrayOfByte[8] & 0x4) != 4);
        bool3 = bool2;
      } while ((arrayOfByte[9] & 0x1) != 1);
      bool3 = bool2;
    } while ((arrayOfByte[12] & 0x3) != 3);
    paramExtractorInput.advancePeekPosition(arrayOfByte[13] & 0x7);
    paramExtractorInput.peekFully(arrayOfByte, 0, 3);
    if (1 == ((arrayOfByte[0] & 0xFF) << 16 | (arrayOfByte[1] & 0xFF) << 8 | arrayOfByte[2] & 0xFF)) {}
    for (boolean bool3 = bool1;; bool3 = false) {
      break;
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
    private boolean seenFirstDts;
    private long timeUs;
    private final TimestampAdjuster timestampAdjuster;
    
    public PesReader(ElementaryStreamReader paramElementaryStreamReader, TimestampAdjuster paramTimestampAdjuster)
    {
      this.pesPayloadReader = paramElementaryStreamReader;
      this.timestampAdjuster = paramTimestampAdjuster;
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
          this.timestampAdjuster.adjustTsTimestamp(l4 << 30 | l5 | l6);
          this.seenFirstDts = true;
        }
        this.timeUs = this.timestampAdjuster.adjustTsTimestamp(l1 << 30 | l2 | l3);
      }
    }
    
    public void consume(ParsableByteArray paramParsableByteArray)
      throws ParserException
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/extractor/ts/PsExtractor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
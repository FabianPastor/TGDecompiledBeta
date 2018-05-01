package org.telegram.messenger.exoplayer2.extractor.mp3;

import java.io.EOFException;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.extractor.Extractor;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.extractor.ExtractorsFactory;
import org.telegram.messenger.exoplayer2.extractor.GaplessInfoHolder;
import org.telegram.messenger.exoplayer2.extractor.MpegAudioHeader;
import org.telegram.messenger.exoplayer2.extractor.PositionHolder;
import org.telegram.messenger.exoplayer2.extractor.SeekMap;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput;
import org.telegram.messenger.exoplayer2.metadata.Metadata;
import org.telegram.messenger.exoplayer2.metadata.id3.Id3Decoder;
import org.telegram.messenger.exoplayer2.metadata.id3.Id3Decoder.FramePredicate;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.Util;

public final class Mp3Extractor
  implements Extractor
{
  public static final ExtractorsFactory FACTORY = new ExtractorsFactory()
  {
    public Extractor[] createExtractors()
    {
      return new Extractor[] { new Mp3Extractor() };
    }
  };
  public static final int FLAG_DISABLE_ID3_METADATA = 2;
  public static final int FLAG_ENABLE_CONSTANT_BITRATE_SEEKING = 1;
  private static final int MAX_SNIFF_BYTES = 16384;
  private static final int MAX_SYNC_BYTES = 131072;
  private static final int MPEG_AUDIO_HEADER_MASK = -128000;
  private static final int SCRATCH_LENGTH = 10;
  private static final int SEEK_HEADER_INFO = Util.getIntegerCodeForString("Info");
  private static final int SEEK_HEADER_UNSET = 0;
  private static final int SEEK_HEADER_VBRI = Util.getIntegerCodeForString("VBRI");
  private static final int SEEK_HEADER_XING = Util.getIntegerCodeForString("Xing");
  private long basisTimeUs;
  private ExtractorOutput extractorOutput;
  private final int flags;
  private final long forcedFirstSampleTimestampUs;
  private final GaplessInfoHolder gaplessInfoHolder;
  private Metadata metadata;
  private int sampleBytesRemaining;
  private long samplesRead;
  private final ParsableByteArray scratch;
  private Seeker seeker;
  private final MpegAudioHeader synchronizedHeader;
  private int synchronizedHeaderData;
  private TrackOutput trackOutput;
  
  public Mp3Extractor()
  {
    this(0);
  }
  
  public Mp3Extractor(int paramInt)
  {
    this(paramInt, -9223372036854775807L);
  }
  
  public Mp3Extractor(int paramInt, long paramLong)
  {
    this.flags = paramInt;
    this.forcedFirstSampleTimestampUs = paramLong;
    this.scratch = new ParsableByteArray(10);
    this.synchronizedHeader = new MpegAudioHeader();
    this.gaplessInfoHolder = new GaplessInfoHolder();
    this.basisTimeUs = -9223372036854775807L;
  }
  
  private Seeker getConstantBitrateSeeker(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    paramExtractorInput.peekFully(this.scratch.data, 0, 4);
    this.scratch.setPosition(0);
    MpegAudioHeader.populateHeader(this.scratch.readInt(), this.synchronizedHeader);
    return new ConstantBitrateSeeker(paramExtractorInput.getLength(), paramExtractorInput.getPosition(), this.synchronizedHeader);
  }
  
  private static int getSeekFrameHeader(ParsableByteArray paramParsableByteArray, int paramInt)
  {
    if (paramParsableByteArray.limit() >= paramInt + 4)
    {
      paramParsableByteArray.setPosition(paramInt);
      int i = paramParsableByteArray.readInt();
      paramInt = i;
      if (i != SEEK_HEADER_XING)
      {
        if (i != SEEK_HEADER_INFO) {
          break label40;
        }
        paramInt = i;
      }
    }
    for (;;)
    {
      return paramInt;
      label40:
      if (paramParsableByteArray.limit() >= 40)
      {
        paramParsableByteArray.setPosition(36);
        if (paramParsableByteArray.readInt() == SEEK_HEADER_VBRI)
        {
          paramInt = SEEK_HEADER_VBRI;
          continue;
        }
      }
      paramInt = 0;
    }
  }
  
  private static boolean headersMatch(int paramInt, long paramLong)
  {
    if ((0xFFFE0C00 & paramInt) == (0xFFFFFFFFFFFE0C00 & paramLong)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private Seeker maybeReadSeekFrame(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    int i = 21;
    Object localObject = new ParsableByteArray(this.synchronizedHeader.frameSize);
    paramExtractorInput.peekFully(((ParsableByteArray)localObject).data, 0, this.synchronizedHeader.frameSize);
    if ((this.synchronizedHeader.version & 0x1) != 0) {
      if (this.synchronizedHeader.channels != 1) {
        i = 36;
      }
    }
    int j;
    for (;;)
    {
      j = getSeekFrameHeader((ParsableByteArray)localObject, i);
      if ((j != SEEK_HEADER_XING) && (j != SEEK_HEADER_INFO)) {
        break;
      }
      XingSeeker localXingSeeker = XingSeeker.create(paramExtractorInput.getLength(), paramExtractorInput.getPosition(), this.synchronizedHeader, (ParsableByteArray)localObject);
      if ((localXingSeeker != null) && (!this.gaplessInfoHolder.hasGaplessInfo()))
      {
        paramExtractorInput.resetPeekPosition();
        paramExtractorInput.advancePeekPosition(i + 141);
        paramExtractorInput.peekFully(this.scratch.data, 0, 3);
        this.scratch.setPosition(0);
        this.gaplessInfoHolder.setFromXingHeaderValue(this.scratch.readUnsignedInt24());
      }
      paramExtractorInput.skipFully(this.synchronizedHeader.frameSize);
      localObject = localXingSeeker;
      if (localXingSeeker == null) {
        break label289;
      }
      localObject = localXingSeeker;
      if (localXingSeeker.isSeekable()) {
        break label289;
      }
      localObject = localXingSeeker;
      if (j != SEEK_HEADER_INFO) {
        break label289;
      }
      paramExtractorInput = getConstantBitrateSeeker(paramExtractorInput);
      return paramExtractorInput;
      if (this.synchronizedHeader.channels == 1) {
        i = 13;
      }
    }
    if (j == SEEK_HEADER_VBRI)
    {
      localObject = VbriSeeker.create(paramExtractorInput.getLength(), paramExtractorInput.getPosition(), this.synchronizedHeader, (ParsableByteArray)localObject);
      paramExtractorInput.skipFully(this.synchronizedHeader.frameSize);
    }
    for (;;)
    {
      label289:
      paramExtractorInput = (ExtractorInput)localObject;
      break;
      localObject = null;
      paramExtractorInput.resetPeekPosition();
    }
  }
  
  private void peekId3Data(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    int i = 0;
    paramExtractorInput.peekFully(this.scratch.data, 0, 10);
    this.scratch.setPosition(0);
    if (this.scratch.readUnsignedInt24() != Id3Decoder.ID3_TAG)
    {
      paramExtractorInput.resetPeekPosition();
      paramExtractorInput.advancePeekPosition(i);
      return;
    }
    this.scratch.skipBytes(3);
    int j = this.scratch.readSynchSafeInt();
    int k = j + 10;
    Id3Decoder.FramePredicate localFramePredicate;
    if (this.metadata == null)
    {
      byte[] arrayOfByte = new byte[k];
      System.arraycopy(this.scratch.data, 0, arrayOfByte, 0, 10);
      paramExtractorInput.peekFully(arrayOfByte, 10, j);
      if ((this.flags & 0x2) != 0)
      {
        localFramePredicate = GaplessInfoHolder.GAPLESS_INFO_ID3_FRAME_PREDICATE;
        label129:
        this.metadata = new Id3Decoder(localFramePredicate).decode(arrayOfByte, k);
        if (this.metadata != null) {
          this.gaplessInfoHolder.setFromMetadata(this.metadata);
        }
      }
    }
    for (;;)
    {
      i += k;
      break;
      localFramePredicate = null;
      break label129;
      paramExtractorInput.advancePeekPosition(j);
    }
  }
  
  private int readSample(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    int i;
    if (this.sampleBytesRemaining == 0)
    {
      paramExtractorInput.resetPeekPosition();
      if (!paramExtractorInput.peekFully(this.scratch.data, 0, 4, true)) {
        i = -1;
      }
    }
    for (;;)
    {
      return i;
      this.scratch.setPosition(0);
      i = this.scratch.readInt();
      if ((!headersMatch(i, this.synchronizedHeaderData)) || (MpegAudioHeader.getFrameSize(i) == -1))
      {
        paramExtractorInput.skipFully(1);
        this.synchronizedHeaderData = 0;
        i = 0;
      }
      else
      {
        MpegAudioHeader.populateHeader(i, this.synchronizedHeader);
        long l1;
        if (this.basisTimeUs == -9223372036854775807L)
        {
          this.basisTimeUs = this.seeker.getTimeUs(paramExtractorInput.getPosition());
          if (this.forcedFirstSampleTimestampUs != -9223372036854775807L)
          {
            l1 = this.seeker.getTimeUs(0L);
            this.basisTimeUs += this.forcedFirstSampleTimestampUs - l1;
          }
        }
        this.sampleBytesRemaining = this.synchronizedHeader.frameSize;
        i = this.trackOutput.sampleData(paramExtractorInput, this.sampleBytesRemaining, true);
        if (i == -1)
        {
          i = -1;
        }
        else
        {
          this.sampleBytesRemaining -= i;
          if (this.sampleBytesRemaining > 0)
          {
            i = 0;
          }
          else
          {
            long l2 = this.basisTimeUs;
            l1 = this.samplesRead * 1000000L / this.synchronizedHeader.sampleRate;
            this.trackOutput.sampleMetadata(l2 + l1, 1, this.synchronizedHeader.frameSize, 0, null);
            this.samplesRead += this.synchronizedHeader.samplesPerFrame;
            this.sampleBytesRemaining = 0;
            i = 0;
          }
        }
      }
    }
  }
  
  private boolean synchronize(ExtractorInput paramExtractorInput, boolean paramBoolean)
    throws IOException, InterruptedException
  {
    int i = 0;
    int j = 0;
    int k = 0;
    int m = 0;
    int n;
    int i1;
    int i2;
    int i3;
    label104:
    boolean bool;
    if (paramBoolean)
    {
      n = 16384;
      paramExtractorInput.resetPeekPosition();
      i1 = j;
      i2 = m;
      i3 = i;
      if (paramExtractorInput.getPosition() == 0L)
      {
        peekId3Data(paramExtractorInput);
        int i4 = (int)paramExtractorInput.getPeekPosition();
        i1 = j;
        k = i4;
        i2 = m;
        i3 = i;
        if (!paramBoolean)
        {
          paramExtractorInput.skipFully(i4);
          i3 = i;
          i2 = m;
          k = i4;
          i1 = j;
        }
      }
      byte[] arrayOfByte = this.scratch.data;
      if (i3 <= 0) {
        break label168;
      }
      bool = true;
      label121:
      if (paramExtractorInput.peekFully(arrayOfByte, 0, 4, bool)) {
        break label174;
      }
      label136:
      if (!paramBoolean) {
        break label362;
      }
      paramExtractorInput.skipFully(k + i2);
    }
    for (;;)
    {
      this.synchronizedHeaderData = i1;
      for (paramBoolean = true;; paramBoolean = false)
      {
        return paramBoolean;
        n = 131072;
        break;
        label168:
        bool = false;
        break label121;
        label174:
        this.scratch.setPosition(0);
        m = this.scratch.readInt();
        if ((i1 == 0) || (headersMatch(m, i1)))
        {
          j = MpegAudioHeader.getFrameSize(m);
          if (j != -1) {
            break label306;
          }
        }
        i1 = i2 + 1;
        if (i2 != n) {
          break label253;
        }
        if (!paramBoolean) {
          throw new ParserException("Searched too many bytes.");
        }
      }
      label253:
      i3 = 0;
      i = 0;
      if (paramBoolean)
      {
        paramExtractorInput.resetPeekPosition();
        paramExtractorInput.advancePeekPosition(k + i1);
        i2 = i1;
        i1 = i;
        break label104;
      }
      paramExtractorInput.skipFully(1);
      i2 = i1;
      i1 = i;
      break label104;
      label306:
      i = i3 + 1;
      if (i == 1)
      {
        MpegAudioHeader.populateHeader(m, this.synchronizedHeader);
        i3 = m;
      }
      do
      {
        paramExtractorInput.advancePeekPosition(j - 4);
        i1 = i3;
        i3 = i;
        break;
        i3 = i1;
      } while (i != 4);
      break label136;
      label362:
      paramExtractorInput.resetPeekPosition();
    }
  }
  
  public void init(ExtractorOutput paramExtractorOutput)
  {
    this.extractorOutput = paramExtractorOutput;
    this.trackOutput = this.extractorOutput.track(0, 1);
    this.extractorOutput.endTracks();
  }
  
  public int read(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
    throws IOException, InterruptedException
  {
    if (this.synchronizedHeaderData == 0) {}
    try
    {
      synchronize(paramExtractorInput, false);
      if (this.seeker != null) {
        break label172;
      }
      this.seeker = maybeReadSeekFrame(paramExtractorInput);
      if ((this.seeker == null) || ((!this.seeker.isSeekable()) && ((this.flags & 0x1) != 0))) {
        this.seeker = getConstantBitrateSeeker(paramExtractorInput);
      }
      this.extractorOutput.seekMap(this.seeker);
      localTrackOutput = this.trackOutput;
      str = this.synchronizedHeader.mimeType;
      i = this.synchronizedHeader.channels;
      j = this.synchronizedHeader.sampleRate;
      k = this.gaplessInfoHolder.encoderDelay;
      m = this.gaplessInfoHolder.encoderPadding;
      if ((this.flags & 0x2) == 0) {
        break label189;
      }
      paramPositionHolder = null;
    }
    catch (EOFException paramExtractorInput)
    {
      for (;;)
      {
        TrackOutput localTrackOutput;
        String str;
        int i;
        int k;
        int m;
        int j = -1;
        continue;
        paramPositionHolder = this.metadata;
      }
    }
    localTrackOutput.format(Format.createAudioSampleFormat(null, str, null, -1, 4096, i, j, -1, k, m, null, null, 0, null, paramPositionHolder));
    label172:
    j = readSample(paramExtractorInput);
    return j;
  }
  
  public void release() {}
  
  public void seek(long paramLong1, long paramLong2)
  {
    this.synchronizedHeaderData = 0;
    this.basisTimeUs = -9223372036854775807L;
    this.samplesRead = 0L;
    this.sampleBytesRemaining = 0;
  }
  
  public boolean sniff(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    return synchronize(paramExtractorInput, true);
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface Flags {}
  
  static abstract interface Seeker
    extends SeekMap
  {
    public abstract long getTimeUs(long paramLong);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/extractor/mp3/Mp3Extractor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
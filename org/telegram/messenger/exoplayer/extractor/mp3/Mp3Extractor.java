package org.telegram.messenger.exoplayer.extractor.mp3;

import java.io.EOFException;
import java.io.IOException;
import org.telegram.messenger.exoplayer.MediaFormat;
import org.telegram.messenger.exoplayer.ParserException;
import org.telegram.messenger.exoplayer.extractor.Extractor;
import org.telegram.messenger.exoplayer.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer.extractor.GaplessInfo;
import org.telegram.messenger.exoplayer.extractor.PositionHolder;
import org.telegram.messenger.exoplayer.extractor.SeekMap;
import org.telegram.messenger.exoplayer.extractor.TrackOutput;
import org.telegram.messenger.exoplayer.util.MpegAudioHeader;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;
import org.telegram.messenger.exoplayer.util.Util;

public final class Mp3Extractor
  implements Extractor
{
  private static final int HEADER_MASK = -128000;
  private static final int INFO_HEADER = Util.getIntegerCodeForString("Info");
  private static final int MAX_SNIFF_BYTES = 4096;
  private static final int MAX_SYNC_BYTES = 131072;
  private static final int VBRI_HEADER = Util.getIntegerCodeForString("VBRI");
  private static final int XING_HEADER = Util.getIntegerCodeForString("Xing");
  private long basisTimeUs;
  private ExtractorOutput extractorOutput;
  private final long forcedFirstSampleTimestampUs;
  private GaplessInfo gaplessInfo;
  private int sampleBytesRemaining;
  private long samplesRead;
  private final ParsableByteArray scratch;
  private Seeker seeker;
  private final MpegAudioHeader synchronizedHeader;
  private int synchronizedHeaderData;
  private TrackOutput trackOutput;
  
  public Mp3Extractor()
  {
    this(-1L);
  }
  
  public Mp3Extractor(long paramLong)
  {
    this.forcedFirstSampleTimestampUs = paramLong;
    this.scratch = new ParsableByteArray(4);
    this.synchronizedHeader = new MpegAudioHeader();
    this.basisTimeUs = -1L;
  }
  
  private boolean maybeResynchronize(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    paramExtractorInput.resetPeekPosition();
    if (!paramExtractorInput.peekFully(this.scratch.data, 0, 4, true)) {
      return false;
    }
    this.scratch.setPosition(0);
    int i = this.scratch.readInt();
    if (((i & 0xFFFE0C00) == (this.synchronizedHeaderData & 0xFFFE0C00)) && (MpegAudioHeader.getFrameSize(i) != -1))
    {
      MpegAudioHeader.populateHeader(i, this.synchronizedHeader);
      return true;
    }
    this.synchronizedHeaderData = 0;
    paramExtractorInput.skipFully(1);
    return synchronizeCatchingEndOfInput(paramExtractorInput);
  }
  
  private int readSample(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    if (this.sampleBytesRemaining == 0)
    {
      if (!maybeResynchronize(paramExtractorInput)) {
        return -1;
      }
      if (this.basisTimeUs == -1L)
      {
        this.basisTimeUs = this.seeker.getTimeUs(paramExtractorInput.getPosition());
        if (this.forcedFirstSampleTimestampUs != -1L)
        {
          l1 = this.seeker.getTimeUs(0L);
          this.basisTimeUs += this.forcedFirstSampleTimestampUs - l1;
        }
      }
      this.sampleBytesRemaining = this.synchronizedHeader.frameSize;
    }
    int i = this.trackOutput.sampleData(paramExtractorInput, this.sampleBytesRemaining, true);
    if (i == -1) {
      return -1;
    }
    this.sampleBytesRemaining -= i;
    if (this.sampleBytesRemaining > 0) {
      return 0;
    }
    long l1 = this.basisTimeUs;
    long l2 = this.samplesRead * 1000000L / this.synchronizedHeader.sampleRate;
    this.trackOutput.sampleMetadata(l1 + l2, 1, this.synchronizedHeader.frameSize, 0, null);
    this.samplesRead += this.synchronizedHeader.samplesPerFrame;
    this.sampleBytesRemaining = 0;
    return 0;
  }
  
  private void setupSeeker(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    int i = 21;
    ParsableByteArray localParsableByteArray = new ParsableByteArray(this.synchronizedHeader.frameSize);
    paramExtractorInput.peekFully(localParsableByteArray.data, 0, this.synchronizedHeader.frameSize);
    long l1 = paramExtractorInput.getPosition();
    long l2 = paramExtractorInput.getLength();
    if ((this.synchronizedHeader.version & 0x1) != 0)
    {
      if (this.synchronizedHeader.channels != 1) {
        i = 36;
      }
      localParsableByteArray.setPosition(i);
      int j = localParsableByteArray.readInt();
      if ((j != XING_HEADER) && (j != INFO_HEADER)) {
        break label299;
      }
      this.seeker = XingSeeker.create(this.synchronizedHeader, localParsableByteArray, l1, l2);
      if ((this.seeker != null) && (this.gaplessInfo == null))
      {
        paramExtractorInput.resetPeekPosition();
        paramExtractorInput.advancePeekPosition(i + 141);
        paramExtractorInput.peekFully(this.scratch.data, 0, 3);
        this.scratch.setPosition(0);
        this.gaplessInfo = GaplessInfo.createFromXingHeaderValue(this.scratch.readUnsignedInt24());
      }
      paramExtractorInput.skipFully(this.synchronizedHeader.frameSize);
    }
    for (;;)
    {
      if (this.seeker == null)
      {
        paramExtractorInput.resetPeekPosition();
        paramExtractorInput.peekFully(this.scratch.data, 0, 4);
        this.scratch.setPosition(0);
        MpegAudioHeader.populateHeader(this.scratch.readInt(), this.synchronizedHeader);
        this.seeker = new ConstantBitrateSeeker(paramExtractorInput.getPosition(), this.synchronizedHeader.bitrate, l2);
      }
      return;
      if (this.synchronizedHeader.channels != 1) {
        break;
      }
      i = 13;
      break;
      label299:
      localParsableByteArray.setPosition(36);
      if (localParsableByteArray.readInt() == VBRI_HEADER)
      {
        this.seeker = VbriSeeker.create(this.synchronizedHeader, localParsableByteArray, l1, l2);
        paramExtractorInput.skipFully(this.synchronizedHeader.frameSize);
      }
    }
  }
  
  private boolean synchronize(ExtractorInput paramExtractorInput, boolean paramBoolean)
    throws IOException, InterruptedException
  {
    int n = 0;
    int i1 = 0;
    int i2 = 0;
    int k = 0;
    paramExtractorInput.resetPeekPosition();
    int i = i2;
    int m = n;
    int j = i1;
    if (paramExtractorInput.getPosition() == 0L)
    {
      this.gaplessInfo = Id3Util.parseId3(paramExtractorInput);
      int i3 = (int)paramExtractorInput.getPeekPosition();
      i = i2;
      k = i3;
      m = n;
      j = i1;
      if (!paramBoolean)
      {
        paramExtractorInput.skipFully(i3);
        j = i1;
        m = n;
        k = i3;
        i = i2;
      }
    }
    for (;;)
    {
      if ((paramBoolean) && (m == 4096)) {
        return false;
      }
      if ((!paramBoolean) && (m == 131072)) {
        throw new ParserException("Searched too many bytes.");
      }
      if (!paramExtractorInput.peekFully(this.scratch.data, 0, 4, true)) {
        return false;
      }
      this.scratch.setPosition(0);
      i1 = this.scratch.readInt();
      if ((i == 0) || ((0xFFFE0C00 & i1) == (0xFFFE0C00 & i)))
      {
        i2 = MpegAudioHeader.getFrameSize(i1);
        if (i2 != -1) {
          break;
        }
      }
      j = 0;
      i = 0;
      m += 1;
      if (paramBoolean)
      {
        paramExtractorInput.resetPeekPosition();
        paramExtractorInput.advancePeekPosition(k + m);
      }
      else
      {
        paramExtractorInput.skipFully(1);
      }
    }
    n = j + 1;
    if (n == 1)
    {
      MpegAudioHeader.populateHeader(i1, this.synchronizedHeader);
      j = i1;
    }
    do
    {
      paramExtractorInput.advancePeekPosition(i2 - 4);
      i = j;
      j = n;
      break;
      j = i;
    } while (n != 4);
    if (paramBoolean) {
      paramExtractorInput.skipFully(k + m);
    }
    for (;;)
    {
      this.synchronizedHeaderData = i;
      return true;
      paramExtractorInput.resetPeekPosition();
    }
  }
  
  private boolean synchronizeCatchingEndOfInput(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    try
    {
      boolean bool = synchronize(paramExtractorInput, false);
      return bool;
    }
    catch (EOFException paramExtractorInput) {}
    return false;
  }
  
  public void init(ExtractorOutput paramExtractorOutput)
  {
    this.extractorOutput = paramExtractorOutput;
    this.trackOutput = paramExtractorOutput.track(0);
    paramExtractorOutput.endTracks();
  }
  
  public int read(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
    throws IOException, InterruptedException
  {
    if ((this.synchronizedHeaderData == 0) && (!synchronizeCatchingEndOfInput(paramExtractorInput))) {
      return -1;
    }
    if (this.seeker == null)
    {
      setupSeeker(paramExtractorInput);
      this.extractorOutput.seekMap(this.seeker);
      MediaFormat localMediaFormat = MediaFormat.createAudioFormat(null, this.synchronizedHeader.mimeType, -1, 4096, this.seeker.getDurationUs(), this.synchronizedHeader.channels, this.synchronizedHeader.sampleRate, null, null);
      paramPositionHolder = localMediaFormat;
      if (this.gaplessInfo != null) {
        paramPositionHolder = localMediaFormat.copyWithGaplessInfo(this.gaplessInfo.encoderDelay, this.gaplessInfo.encoderPadding);
      }
      this.trackOutput.format(paramPositionHolder);
    }
    return readSample(paramExtractorInput);
  }
  
  public void release() {}
  
  public void seek()
  {
    this.synchronizedHeaderData = 0;
    this.samplesRead = 0L;
    this.basisTimeUs = -1L;
    this.sampleBytesRemaining = 0;
  }
  
  public boolean sniff(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    return synchronize(paramExtractorInput, true);
  }
  
  static abstract interface Seeker
    extends SeekMap
  {
    public abstract long getDurationUs();
    
    public abstract long getTimeUs(long paramLong);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/extractor/mp3/Mp3Extractor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
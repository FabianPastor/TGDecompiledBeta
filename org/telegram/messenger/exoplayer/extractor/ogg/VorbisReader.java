package org.telegram.messenger.exoplayer.extractor.ogg;

import java.io.IOException;
import java.util.ArrayList;
import org.telegram.messenger.exoplayer.MediaFormat;
import org.telegram.messenger.exoplayer.ParserException;
import org.telegram.messenger.exoplayer.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer.extractor.PositionHolder;
import org.telegram.messenger.exoplayer.extractor.SeekMap;
import org.telegram.messenger.exoplayer.extractor.TrackOutput;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;

final class VorbisReader
  extends StreamReader
  implements SeekMap
{
  private static final long LARGEST_EXPECTED_PAGE_SIZE = 8000L;
  private long audioStartPosition;
  private VorbisUtil.CommentHeader commentHeader;
  private long duration;
  private long elapsedSamples;
  private long inputLength;
  private final OggSeeker oggSeeker = new OggSeeker();
  private int previousPacketBlockSize;
  private boolean seenFirstAudioPacket;
  private long targetGranule = -1L;
  private long totalSamples;
  private VorbisUtil.VorbisIdHeader vorbisIdHeader;
  private VorbisSetup vorbisSetup;
  
  static void appendNumberOfSamples(ParsableByteArray paramParsableByteArray, long paramLong)
  {
    paramParsableByteArray.setLimit(paramParsableByteArray.limit() + 4);
    paramParsableByteArray.data[(paramParsableByteArray.limit() - 4)] = ((byte)(int)(paramLong & 0xFF));
    paramParsableByteArray.data[(paramParsableByteArray.limit() - 3)] = ((byte)(int)(paramLong >>> 8 & 0xFF));
    paramParsableByteArray.data[(paramParsableByteArray.limit() - 2)] = ((byte)(int)(paramLong >>> 16 & 0xFF));
    paramParsableByteArray.data[(paramParsableByteArray.limit() - 1)] = ((byte)(int)(paramLong >>> 24 & 0xFF));
  }
  
  private static int decodeBlockSize(byte paramByte, VorbisSetup paramVorbisSetup)
  {
    int i = OggUtil.readBits(paramByte, paramVorbisSetup.iLogModes, 1);
    if (!paramVorbisSetup.modes[i].blockFlag) {
      return paramVorbisSetup.idHeader.blockSize0;
    }
    return paramVorbisSetup.idHeader.blockSize1;
  }
  
  static boolean verifyBitstreamType(ParsableByteArray paramParsableByteArray)
  {
    try
    {
      boolean bool = VorbisUtil.verifyVorbisHeaderCapturePattern(1, paramParsableByteArray, true);
      return bool;
    }
    catch (ParserException paramParsableByteArray) {}
    return false;
  }
  
  public long getPosition(long paramLong)
  {
    if (paramLong == 0L)
    {
      this.targetGranule = -1L;
      return this.audioStartPosition;
    }
    this.targetGranule = (this.vorbisSetup.idHeader.sampleRate * paramLong / 1000000L);
    return Math.max(this.audioStartPosition, (this.inputLength - this.audioStartPosition) * paramLong / this.duration - 4000L);
  }
  
  public boolean isSeekable()
  {
    return (this.vorbisSetup != null) && (this.inputLength != -1L);
  }
  
  public int read(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
    throws IOException, InterruptedException
  {
    long l;
    if (this.totalSamples == 0L)
    {
      if (this.vorbisSetup == null)
      {
        this.inputLength = paramExtractorInput.getLength();
        this.vorbisSetup = readSetupHeaders(paramExtractorInput, this.scratch);
        this.audioStartPosition = paramExtractorInput.getPosition();
        this.extractorOutput.seekMap(this);
        if (this.inputLength != -1L)
        {
          paramPositionHolder.position = Math.max(0L, paramExtractorInput.getLength() - 8000L);
          return 1;
        }
      }
      ArrayList localArrayList;
      if (this.inputLength == -1L)
      {
        l = -1L;
        this.totalSamples = l;
        localArrayList = new ArrayList();
        localArrayList.add(this.vorbisSetup.idHeader.data);
        localArrayList.add(this.vorbisSetup.setupHeaderData);
        if (this.inputLength != -1L) {
          break label281;
        }
      }
      label281:
      for (l = -1L;; l = this.totalSamples * 1000000L / this.vorbisSetup.idHeader.sampleRate)
      {
        this.duration = l;
        this.trackOutput.format(MediaFormat.createAudioFormat(null, "audio/vorbis", this.vorbisSetup.idHeader.bitrateNominal, 65025, this.duration, this.vorbisSetup.idHeader.channels, (int)this.vorbisSetup.idHeader.sampleRate, localArrayList, null));
        if (this.inputLength == -1L) {
          break label305;
        }
        this.oggSeeker.setup(this.inputLength - this.audioStartPosition, this.totalSamples);
        paramPositionHolder.position = this.audioStartPosition;
        return 1;
        l = this.oggParser.readGranuleOfLastPage(paramExtractorInput);
        break;
      }
    }
    label305:
    if ((!this.seenFirstAudioPacket) && (this.targetGranule > -1L))
    {
      OggUtil.skipToNextPage(paramExtractorInput);
      l = this.oggSeeker.getNextSeekPosition(this.targetGranule, paramExtractorInput);
      if (l != -1L)
      {
        paramPositionHolder.position = l;
        return 1;
      }
      this.elapsedSamples = this.oggParser.skipToPageOfGranule(paramExtractorInput, this.targetGranule);
      this.previousPacketBlockSize = this.vorbisIdHeader.blockSize0;
      this.seenFirstAudioPacket = true;
    }
    if (this.oggParser.readPacket(paramExtractorInput, this.scratch))
    {
      int j;
      if ((this.scratch.data[0] & 0x1) != 1)
      {
        j = decodeBlockSize(this.scratch.data[0], this.vorbisSetup);
        if (!this.seenFirstAudioPacket) {
          break label579;
        }
      }
      label579:
      for (int i = (this.previousPacketBlockSize + j) / 4;; i = 0)
      {
        if (this.elapsedSamples + i >= this.targetGranule)
        {
          appendNumberOfSamples(this.scratch, i);
          l = this.elapsedSamples * 1000000L / this.vorbisSetup.idHeader.sampleRate;
          this.trackOutput.sampleData(this.scratch, this.scratch.limit());
          this.trackOutput.sampleMetadata(l, 1, this.scratch.limit(), 0, null);
          this.targetGranule = -1L;
        }
        this.seenFirstAudioPacket = true;
        this.elapsedSamples += i;
        this.previousPacketBlockSize = j;
        this.scratch.reset();
        return 0;
      }
    }
    return -1;
  }
  
  VorbisSetup readSetupHeaders(ExtractorInput paramExtractorInput, ParsableByteArray paramParsableByteArray)
    throws IOException, InterruptedException
  {
    if (this.vorbisIdHeader == null)
    {
      this.oggParser.readPacket(paramExtractorInput, paramParsableByteArray);
      this.vorbisIdHeader = VorbisUtil.readVorbisIdentificationHeader(paramParsableByteArray);
      paramParsableByteArray.reset();
    }
    if (this.commentHeader == null)
    {
      this.oggParser.readPacket(paramExtractorInput, paramParsableByteArray);
      this.commentHeader = VorbisUtil.readVorbisCommentHeader(paramParsableByteArray);
      paramParsableByteArray.reset();
    }
    this.oggParser.readPacket(paramExtractorInput, paramParsableByteArray);
    paramExtractorInput = new byte[paramParsableByteArray.limit()];
    System.arraycopy(paramParsableByteArray.data, 0, paramExtractorInput, 0, paramParsableByteArray.limit());
    VorbisUtil.Mode[] arrayOfMode = VorbisUtil.readVorbisModes(paramParsableByteArray, this.vorbisIdHeader.channels);
    int i = VorbisUtil.iLog(arrayOfMode.length - 1);
    paramParsableByteArray.reset();
    return new VorbisSetup(this.vorbisIdHeader, this.commentHeader, paramExtractorInput, arrayOfMode, i);
  }
  
  public void seek()
  {
    super.seek();
    this.previousPacketBlockSize = 0;
    this.elapsedSamples = 0L;
    this.seenFirstAudioPacket = false;
  }
  
  static final class VorbisSetup
  {
    public final VorbisUtil.CommentHeader commentHeader;
    public final int iLogModes;
    public final VorbisUtil.VorbisIdHeader idHeader;
    public final VorbisUtil.Mode[] modes;
    public final byte[] setupHeaderData;
    
    public VorbisSetup(VorbisUtil.VorbisIdHeader paramVorbisIdHeader, VorbisUtil.CommentHeader paramCommentHeader, byte[] paramArrayOfByte, VorbisUtil.Mode[] paramArrayOfMode, int paramInt)
    {
      this.idHeader = paramVorbisIdHeader;
      this.commentHeader = paramCommentHeader;
      this.setupHeaderData = paramArrayOfByte;
      this.modes = paramArrayOfMode;
      this.iLogModes = paramInt;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/extractor/ogg/VorbisReader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
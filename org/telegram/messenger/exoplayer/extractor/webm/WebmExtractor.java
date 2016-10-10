package org.telegram.messenger.exoplayer.extractor.webm;

import android.util.Pair;
import android.util.SparseArray;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.telegram.messenger.exoplayer.MediaFormat;
import org.telegram.messenger.exoplayer.ParserException;
import org.telegram.messenger.exoplayer.drm.DrmInitData.SchemeInitData;
import org.telegram.messenger.exoplayer.drm.DrmInitData.Universal;
import org.telegram.messenger.exoplayer.extractor.ChunkIndex;
import org.telegram.messenger.exoplayer.extractor.Extractor;
import org.telegram.messenger.exoplayer.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer.extractor.PositionHolder;
import org.telegram.messenger.exoplayer.extractor.SeekMap;
import org.telegram.messenger.exoplayer.extractor.TrackOutput;
import org.telegram.messenger.exoplayer.util.LongArray;
import org.telegram.messenger.exoplayer.util.MimeTypes;
import org.telegram.messenger.exoplayer.util.NalUnitUtil;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;
import org.telegram.messenger.exoplayer.util.Util;

public final class WebmExtractor
  implements Extractor
{
  private static final int BLOCK_STATE_DATA = 2;
  private static final int BLOCK_STATE_HEADER = 1;
  private static final int BLOCK_STATE_START = 0;
  private static final String CODEC_ID_AAC = "A_AAC";
  private static final String CODEC_ID_AC3 = "A_AC3";
  private static final String CODEC_ID_ACM = "A_MS/ACM";
  private static final String CODEC_ID_DTS = "A_DTS";
  private static final String CODEC_ID_DTS_EXPRESS = "A_DTS/EXPRESS";
  private static final String CODEC_ID_DTS_LOSSLESS = "A_DTS/LOSSLESS";
  private static final String CODEC_ID_E_AC3 = "A_EAC3";
  private static final String CODEC_ID_FLAC = "A_FLAC";
  private static final String CODEC_ID_FOURCC = "V_MS/VFW/FOURCC";
  private static final String CODEC_ID_H264 = "V_MPEG4/ISO/AVC";
  private static final String CODEC_ID_H265 = "V_MPEGH/ISO/HEVC";
  private static final String CODEC_ID_MP3 = "A_MPEG/L3";
  private static final String CODEC_ID_MPEG2 = "V_MPEG2";
  private static final String CODEC_ID_MPEG4_AP = "V_MPEG4/ISO/AP";
  private static final String CODEC_ID_MPEG4_ASP = "V_MPEG4/ISO/ASP";
  private static final String CODEC_ID_MPEG4_SP = "V_MPEG4/ISO/SP";
  private static final String CODEC_ID_OPUS = "A_OPUS";
  private static final String CODEC_ID_PCM_INT_LIT = "A_PCM/INT/LIT";
  private static final String CODEC_ID_PGS = "S_HDMV/PGS";
  private static final String CODEC_ID_SUBRIP = "S_TEXT/UTF8";
  private static final String CODEC_ID_TRUEHD = "A_TRUEHD";
  private static final String CODEC_ID_VOBSUB = "S_VOBSUB";
  private static final String CODEC_ID_VORBIS = "A_VORBIS";
  private static final String CODEC_ID_VP8 = "V_VP8";
  private static final String CODEC_ID_VP9 = "V_VP9";
  private static final String DOC_TYPE_MATROSKA = "matroska";
  private static final String DOC_TYPE_WEBM = "webm";
  private static final int ENCRYPTION_IV_SIZE = 8;
  private static final int FOURCC_COMPRESSION_VC1 = 826496599;
  private static final int ID_AUDIO = 225;
  private static final int ID_AUDIO_BIT_DEPTH = 25188;
  private static final int ID_BLOCK = 161;
  private static final int ID_BLOCK_DURATION = 155;
  private static final int ID_BLOCK_GROUP = 160;
  private static final int ID_CHANNELS = 159;
  private static final int ID_CLUSTER = 524531317;
  private static final int ID_CODEC_DELAY = 22186;
  private static final int ID_CODEC_ID = 134;
  private static final int ID_CODEC_PRIVATE = 25506;
  private static final int ID_CONTENT_COMPRESSION = 20532;
  private static final int ID_CONTENT_COMPRESSION_ALGORITHM = 16980;
  private static final int ID_CONTENT_COMPRESSION_SETTINGS = 16981;
  private static final int ID_CONTENT_ENCODING = 25152;
  private static final int ID_CONTENT_ENCODINGS = 28032;
  private static final int ID_CONTENT_ENCODING_ORDER = 20529;
  private static final int ID_CONTENT_ENCODING_SCOPE = 20530;
  private static final int ID_CONTENT_ENCRYPTION = 20533;
  private static final int ID_CONTENT_ENCRYPTION_AES_SETTINGS = 18407;
  private static final int ID_CONTENT_ENCRYPTION_AES_SETTINGS_CIPHER_MODE = 18408;
  private static final int ID_CONTENT_ENCRYPTION_ALGORITHM = 18401;
  private static final int ID_CONTENT_ENCRYPTION_KEY_ID = 18402;
  private static final int ID_CUES = 475249515;
  private static final int ID_CUE_CLUSTER_POSITION = 241;
  private static final int ID_CUE_POINT = 187;
  private static final int ID_CUE_TIME = 179;
  private static final int ID_CUE_TRACK_POSITIONS = 183;
  private static final int ID_DEFAULT_DURATION = 2352003;
  private static final int ID_DISPLAY_HEIGHT = 21690;
  private static final int ID_DISPLAY_UNIT = 21682;
  private static final int ID_DISPLAY_WIDTH = 21680;
  private static final int ID_DOC_TYPE = 17026;
  private static final int ID_DOC_TYPE_READ_VERSION = 17029;
  private static final int ID_DURATION = 17545;
  private static final int ID_EBML = 440786851;
  private static final int ID_EBML_READ_VERSION = 17143;
  private static final int ID_INFO = 357149030;
  private static final int ID_LANGUAGE = 2274716;
  private static final int ID_PIXEL_HEIGHT = 186;
  private static final int ID_PIXEL_WIDTH = 176;
  private static final int ID_REFERENCE_BLOCK = 251;
  private static final int ID_SAMPLING_FREQUENCY = 181;
  private static final int ID_SEEK = 19899;
  private static final int ID_SEEK_HEAD = 290298740;
  private static final int ID_SEEK_ID = 21419;
  private static final int ID_SEEK_POSITION = 21420;
  private static final int ID_SEEK_PRE_ROLL = 22203;
  private static final int ID_SEGMENT = 408125543;
  private static final int ID_SEGMENT_INFO = 357149030;
  private static final int ID_SIMPLE_BLOCK = 163;
  private static final int ID_TIMECODE_SCALE = 2807729;
  private static final int ID_TIME_CODE = 231;
  private static final int ID_TRACKS = 374648427;
  private static final int ID_TRACK_ENTRY = 174;
  private static final int ID_TRACK_NUMBER = 215;
  private static final int ID_TRACK_TYPE = 131;
  private static final int ID_VIDEO = 224;
  private static final int LACING_EBML = 3;
  private static final int LACING_FIXED_SIZE = 2;
  private static final int LACING_NONE = 0;
  private static final int LACING_XIPH = 1;
  private static final int MP3_MAX_INPUT_SIZE = 4096;
  private static final int OPUS_MAX_INPUT_SIZE = 5760;
  private static final byte[] SUBRIP_PREFIX = { 49, 10, 48, 48, 58, 48, 48, 58, 48, 48, 44, 48, 48, 48, 32, 45, 45, 62, 32, 48, 48, 58, 48, 48, 58, 48, 48, 44, 48, 48, 48, 10 };
  private static final int SUBRIP_PREFIX_END_TIMECODE_OFFSET = 19;
  private static final byte[] SUBRIP_TIMECODE_EMPTY = { 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32 };
  private static final int SUBRIP_TIMECODE_LENGTH = 12;
  private static final int TRACK_TYPE_AUDIO = 2;
  private static final int UNKNOWN = -1;
  private static final int VORBIS_MAX_INPUT_SIZE = 8192;
  private static final int WAVE_FORMAT_EXTENSIBLE = 65534;
  private static final int WAVE_FORMAT_PCM = 1;
  private static final int WAVE_FORMAT_SIZE = 18;
  private static final UUID WAVE_SUBFORMAT_PCM = new UUID(72057594037932032L, -9223371306706625679L);
  private long blockDurationUs;
  private int blockFlags;
  private int blockLacingSampleCount;
  private int blockLacingSampleIndex;
  private int[] blockLacingSampleSizes;
  private int blockState;
  private long blockTimeUs;
  private int blockTrackNumber;
  private int blockTrackNumberLength;
  private long clusterTimecodeUs = -1L;
  private LongArray cueClusterPositions;
  private LongArray cueTimesUs;
  private long cuesContentPosition = -1L;
  private Track currentTrack;
  private long durationTimecode = -1L;
  private long durationUs = -1L;
  private ExtractorOutput extractorOutput;
  private final ParsableByteArray nalLength;
  private final ParsableByteArray nalStartCode;
  private final EbmlReader reader;
  private int sampleBytesRead;
  private int sampleBytesWritten;
  private int sampleCurrentNalBytesRemaining;
  private boolean sampleEncodingHandled;
  private boolean sampleRead;
  private boolean sampleSeenReferenceBlock;
  private final ParsableByteArray sampleStrippedBytes;
  private final ParsableByteArray scratch;
  private int seekEntryId;
  private final ParsableByteArray seekEntryIdBytes;
  private long seekEntryPosition;
  private boolean seekForCues;
  private long seekPositionAfterBuildingCues = -1L;
  private boolean seenClusterPositionForCurrentCuePoint;
  private long segmentContentPosition = -1L;
  private long segmentContentSize = -1L;
  private boolean sentDrmInitData;
  private boolean sentSeekMap;
  private final ParsableByteArray subripSample;
  private long timecodeScale = -1L;
  private final SparseArray<Track> tracks;
  private final VarintReader varintReader;
  private final ParsableByteArray vorbisNumPageSamples;
  
  public WebmExtractor()
  {
    this(new DefaultEbmlReader());
  }
  
  WebmExtractor(EbmlReader paramEbmlReader)
  {
    this.reader = paramEbmlReader;
    this.reader.init(new InnerEbmlReaderOutput(null));
    this.varintReader = new VarintReader();
    this.tracks = new SparseArray();
    this.scratch = new ParsableByteArray(4);
    this.vorbisNumPageSamples = new ParsableByteArray(ByteBuffer.allocate(4).putInt(-1).array());
    this.seekEntryIdBytes = new ParsableByteArray(4);
    this.nalStartCode = new ParsableByteArray(NalUnitUtil.NAL_START_CODE);
    this.nalLength = new ParsableByteArray(4);
    this.sampleStrippedBytes = new ParsableByteArray();
    this.subripSample = new ParsableByteArray();
  }
  
  private SeekMap buildSeekMap()
  {
    if ((this.segmentContentPosition == -1L) || (this.durationUs == -1L) || (this.cueTimesUs == null) || (this.cueTimesUs.size() == 0) || (this.cueClusterPositions == null) || (this.cueClusterPositions.size() != this.cueTimesUs.size()))
    {
      this.cueTimesUs = null;
      this.cueClusterPositions = null;
      return SeekMap.UNSEEKABLE;
    }
    int j = this.cueTimesUs.size();
    int[] arrayOfInt = new int[j];
    long[] arrayOfLong1 = new long[j];
    long[] arrayOfLong2 = new long[j];
    long[] arrayOfLong3 = new long[j];
    int i = 0;
    while (i < j)
    {
      arrayOfLong3[i] = this.cueTimesUs.get(i);
      arrayOfLong1[i] = (this.segmentContentPosition + this.cueClusterPositions.get(i));
      i += 1;
    }
    i = 0;
    while (i < j - 1)
    {
      arrayOfInt[i] = ((int)(arrayOfLong1[(i + 1)] - arrayOfLong1[i]));
      arrayOfLong2[i] = (arrayOfLong3[(i + 1)] - arrayOfLong3[i]);
      i += 1;
    }
    arrayOfInt[(j - 1)] = ((int)(this.segmentContentPosition + this.segmentContentSize - arrayOfLong1[(j - 1)]));
    arrayOfLong2[(j - 1)] = (this.durationUs - arrayOfLong3[(j - 1)]);
    this.cueTimesUs = null;
    this.cueClusterPositions = null;
    return new ChunkIndex(arrayOfInt, arrayOfLong1, arrayOfLong2, arrayOfLong3);
  }
  
  private void commitSampleToOutput(Track paramTrack, long paramLong)
  {
    if ("S_TEXT/UTF8".equals(paramTrack.codecId)) {
      writeSubripSample(paramTrack);
    }
    paramTrack.output.sampleMetadata(paramLong, this.blockFlags, this.sampleBytesWritten, 0, paramTrack.encryptionKeyId);
    this.sampleRead = true;
    resetSample();
  }
  
  private static int[] ensureArrayCapacity(int[] paramArrayOfInt, int paramInt)
  {
    int[] arrayOfInt;
    if (paramArrayOfInt == null) {
      arrayOfInt = new int[paramInt];
    }
    do
    {
      return arrayOfInt;
      arrayOfInt = paramArrayOfInt;
    } while (paramArrayOfInt.length >= paramInt);
    return new int[Math.max(paramArrayOfInt.length * 2, paramInt)];
  }
  
  private static boolean isCodecSupported(String paramString)
  {
    return ("V_VP8".equals(paramString)) || ("V_VP9".equals(paramString)) || ("V_MPEG2".equals(paramString)) || ("V_MPEG4/ISO/SP".equals(paramString)) || ("V_MPEG4/ISO/ASP".equals(paramString)) || ("V_MPEG4/ISO/AP".equals(paramString)) || ("V_MPEG4/ISO/AVC".equals(paramString)) || ("V_MPEGH/ISO/HEVC".equals(paramString)) || ("V_MS/VFW/FOURCC".equals(paramString)) || ("A_OPUS".equals(paramString)) || ("A_VORBIS".equals(paramString)) || ("A_AAC".equals(paramString)) || ("A_MPEG/L3".equals(paramString)) || ("A_AC3".equals(paramString)) || ("A_EAC3".equals(paramString)) || ("A_TRUEHD".equals(paramString)) || ("A_DTS".equals(paramString)) || ("A_DTS/EXPRESS".equals(paramString)) || ("A_DTS/LOSSLESS".equals(paramString)) || ("A_FLAC".equals(paramString)) || ("A_MS/ACM".equals(paramString)) || ("A_PCM/INT/LIT".equals(paramString)) || ("S_TEXT/UTF8".equals(paramString)) || ("S_VOBSUB".equals(paramString)) || ("S_HDMV/PGS".equals(paramString));
  }
  
  private boolean maybeSeekForCues(PositionHolder paramPositionHolder, long paramLong)
  {
    if (this.seekForCues)
    {
      this.seekPositionAfterBuildingCues = paramLong;
      paramPositionHolder.position = this.cuesContentPosition;
      this.seekForCues = false;
      return true;
    }
    if ((this.sentSeekMap) && (this.seekPositionAfterBuildingCues != -1L))
    {
      paramPositionHolder.position = this.seekPositionAfterBuildingCues;
      this.seekPositionAfterBuildingCues = -1L;
      return true;
    }
    return false;
  }
  
  private void readScratch(ExtractorInput paramExtractorInput, int paramInt)
    throws IOException, InterruptedException
  {
    if (this.scratch.limit() >= paramInt) {
      return;
    }
    if (this.scratch.capacity() < paramInt) {
      this.scratch.reset(Arrays.copyOf(this.scratch.data, Math.max(this.scratch.data.length * 2, paramInt)), this.scratch.limit());
    }
    paramExtractorInput.readFully(this.scratch.data, this.scratch.limit(), paramInt - this.scratch.limit());
    this.scratch.setLimit(paramInt);
  }
  
  private int readToOutput(ExtractorInput paramExtractorInput, TrackOutput paramTrackOutput, int paramInt)
    throws IOException, InterruptedException
  {
    int i = this.sampleStrippedBytes.bytesLeft();
    if (i > 0)
    {
      paramInt = Math.min(paramInt, i);
      paramTrackOutput.sampleData(this.sampleStrippedBytes, paramInt);
    }
    for (;;)
    {
      this.sampleBytesRead += paramInt;
      this.sampleBytesWritten += paramInt;
      return paramInt;
      paramInt = paramTrackOutput.sampleData(paramExtractorInput, paramInt, false);
    }
  }
  
  private void readToTarget(ExtractorInput paramExtractorInput, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException, InterruptedException
  {
    int i = Math.min(paramInt2, this.sampleStrippedBytes.bytesLeft());
    paramExtractorInput.readFully(paramArrayOfByte, paramInt1 + i, paramInt2 - i);
    if (i > 0) {
      this.sampleStrippedBytes.readBytes(paramArrayOfByte, paramInt1, i);
    }
    this.sampleBytesRead += paramInt2;
  }
  
  private void resetSample()
  {
    this.sampleBytesRead = 0;
    this.sampleBytesWritten = 0;
    this.sampleCurrentNalBytesRemaining = 0;
    this.sampleEncodingHandled = false;
    this.sampleStrippedBytes.reset();
  }
  
  private long scaleTimecodeToUs(long paramLong)
    throws ParserException
  {
    if (this.timecodeScale == -1L) {
      throw new ParserException("Can't scale timecode prior to timecodeScale being set.");
    }
    return Util.scaleLargeTimestamp(paramLong, this.timecodeScale, 1000L);
  }
  
  private static void setSubripSampleEndTimecode(byte[] paramArrayOfByte, long paramLong)
  {
    if (paramLong == -1L) {}
    int i;
    int j;
    int k;
    int m;
    for (byte[] arrayOfByte = SUBRIP_TIMECODE_EMPTY;; arrayOfByte = String.format(Locale.US, "%02d:%02d:%02d,%03d", new Object[] { Integer.valueOf(i), Integer.valueOf(j), Integer.valueOf(k), Integer.valueOf(m) }).getBytes())
    {
      System.arraycopy(arrayOfByte, 0, paramArrayOfByte, 19, 12);
      return;
      i = (int)(paramLong / 3600000000L);
      paramLong -= i * 3600000000L;
      j = (int)(paramLong / 60000000L);
      paramLong -= 60000000 * j;
      k = (int)(paramLong / 1000000L);
      m = (int)((paramLong - 1000000 * k) / 1000L);
    }
  }
  
  private void writeSampleData(ExtractorInput paramExtractorInput, Track paramTrack, int paramInt)
    throws IOException, InterruptedException
  {
    int i;
    if ("S_TEXT/UTF8".equals(paramTrack.codecId))
    {
      i = SUBRIP_PREFIX.length + paramInt;
      if (this.subripSample.capacity() < i) {
        this.subripSample.data = Arrays.copyOf(SUBRIP_PREFIX, i + paramInt);
      }
      paramExtractorInput.readFully(this.subripSample.data, SUBRIP_PREFIX.length, paramInt);
      this.subripSample.setPosition(0);
      this.subripSample.setLimit(i);
    }
    TrackOutput localTrackOutput;
    label441:
    do
    {
      return;
      localTrackOutput = paramTrack.output;
      byte[] arrayOfByte;
      int j;
      if (!this.sampleEncodingHandled)
      {
        if (paramTrack.hasContentEncryption)
        {
          this.blockFlags &= 0xFFFFFFFD;
          paramExtractorInput.readFully(this.scratch.data, 0, 1);
          this.sampleBytesRead += 1;
          if ((this.scratch.data[0] & 0x80) == 128) {
            throw new ParserException("Extension bit is set in signal byte");
          }
          if ((this.scratch.data[0] & 0x1) == 1)
          {
            this.scratch.data[0] = 8;
            this.scratch.setPosition(0);
            localTrackOutput.sampleData(this.scratch, 1);
            this.sampleBytesWritten += 1;
            this.blockFlags |= 0x2;
          }
          this.sampleEncodingHandled = true;
        }
      }
      else
      {
        paramInt += this.sampleStrippedBytes.limit();
        if ((!"V_MPEG4/ISO/AVC".equals(paramTrack.codecId)) && (!"V_MPEGH/ISO/HEVC".equals(paramTrack.codecId))) {
          break label441;
        }
        arrayOfByte = this.nalLength.data;
        arrayOfByte[0] = 0;
        arrayOfByte[1] = 0;
        arrayOfByte[2] = 0;
        i = paramTrack.nalUnitLengthFieldLength;
        j = paramTrack.nalUnitLengthFieldLength;
      }
      for (;;)
      {
        if (this.sampleBytesRead < paramInt)
        {
          if (this.sampleCurrentNalBytesRemaining == 0)
          {
            readToTarget(paramExtractorInput, arrayOfByte, 4 - j, i);
            this.nalLength.setPosition(0);
            this.sampleCurrentNalBytesRemaining = this.nalLength.readUnsignedIntToInt();
            this.nalStartCode.setPosition(0);
            localTrackOutput.sampleData(this.nalStartCode, 4);
            this.sampleBytesWritten += 4;
            continue;
            if (paramTrack.sampleStrippedBytes == null) {
              break;
            }
            this.sampleStrippedBytes.reset(paramTrack.sampleStrippedBytes, paramTrack.sampleStrippedBytes.length);
            break;
          }
          this.sampleCurrentNalBytesRemaining -= readToOutput(paramExtractorInput, localTrackOutput, this.sampleCurrentNalBytesRemaining);
          continue;
          while (this.sampleBytesRead < paramInt) {
            readToOutput(paramExtractorInput, localTrackOutput, paramInt - this.sampleBytesRead);
          }
        }
      }
    } while (!"A_VORBIS".equals(paramTrack.codecId));
    this.vorbisNumPageSamples.setPosition(0);
    localTrackOutput.sampleData(this.vorbisNumPageSamples, 4);
    this.sampleBytesWritten += 4;
  }
  
  private void writeSubripSample(Track paramTrack)
  {
    setSubripSampleEndTimecode(this.subripSample.data, this.blockDurationUs);
    paramTrack.output.sampleData(this.subripSample, this.subripSample.limit());
    this.sampleBytesWritten += this.subripSample.limit();
  }
  
  void binaryElement(int paramInt1, int paramInt2, ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    switch (paramInt1)
    {
    default: 
      throw new ParserException("Unexpected id: " + paramInt1);
    case 21419: 
      Arrays.fill(this.seekEntryIdBytes.data, (byte)0);
      paramExtractorInput.readFully(this.seekEntryIdBytes.data, 4 - paramInt2, paramInt2);
      this.seekEntryIdBytes.setPosition(0);
      this.seekEntryId = ((int)this.seekEntryIdBytes.readUnsignedInt());
      return;
    case 25506: 
      this.currentTrack.codecPrivate = new byte[paramInt2];
      paramExtractorInput.readFully(this.currentTrack.codecPrivate, 0, paramInt2);
      return;
    case 16981: 
      this.currentTrack.sampleStrippedBytes = new byte[paramInt2];
      paramExtractorInput.readFully(this.currentTrack.sampleStrippedBytes, 0, paramInt2);
      return;
    case 18402: 
      this.currentTrack.encryptionKeyId = new byte[paramInt2];
      paramExtractorInput.readFully(this.currentTrack.encryptionKeyId, 0, paramInt2);
      return;
    }
    if (this.blockState == 0)
    {
      this.blockTrackNumber = ((int)this.varintReader.readUnsignedVarint(paramExtractorInput, false, true, 8));
      this.blockTrackNumberLength = this.varintReader.getLastLength();
      this.blockDurationUs = -1L;
      this.blockState = 1;
      this.scratch.reset();
    }
    Track localTrack = (Track)this.tracks.get(this.blockTrackNumber);
    if (localTrack == null)
    {
      paramExtractorInput.skipFully(paramInt2 - this.blockTrackNumberLength);
      this.blockState = 0;
      return;
    }
    int i;
    if (this.blockState == 1)
    {
      readScratch(paramExtractorInput, 3);
      i = (this.scratch.data[2] & 0x6) >> 1;
      if (i != 0) {
        break label584;
      }
      this.blockLacingSampleCount = 1;
      this.blockLacingSampleSizes = ensureArrayCapacity(this.blockLacingSampleSizes, 1);
      this.blockLacingSampleSizes[0] = (paramInt2 - this.blockTrackNumberLength - 3);
      paramInt2 = this.scratch.data[0];
      i = this.scratch.data[1];
      this.blockTimeUs = (this.clusterTimecodeUs + scaleTimecodeToUs(paramInt2 << 8 | i & 0xFF));
      if ((this.scratch.data[2] & 0x8) != 8) {
        break label1225;
      }
      paramInt2 = 1;
      label439:
      if ((localTrack.type != 2) && ((paramInt1 != 163) || ((this.scratch.data[2] & 0x80) != 128))) {
        break label1230;
      }
      i = 1;
      label477:
      if (i == 0) {
        break label1236;
      }
      i = 1;
      label485:
      if (paramInt2 == 0) {
        break label1242;
      }
    }
    label584:
    label1100:
    label1225:
    label1230:
    label1236:
    label1242:
    for (paramInt2 = 134217728;; paramInt2 = 0)
    {
      this.blockFlags = (paramInt2 | i);
      this.blockState = 2;
      this.blockLacingSampleIndex = 0;
      if (paramInt1 != 163) {
        break label1253;
      }
      while (this.blockLacingSampleIndex < this.blockLacingSampleCount)
      {
        writeSampleData(paramExtractorInput, localTrack, this.blockLacingSampleSizes[this.blockLacingSampleIndex]);
        commitSampleToOutput(localTrack, this.blockTimeUs + this.blockLacingSampleIndex * localTrack.defaultSampleDurationNs / 1000);
        this.blockLacingSampleIndex += 1;
      }
      if (paramInt1 != 163) {
        throw new ParserException("Lacing only supported in SimpleBlocks.");
      }
      readScratch(paramExtractorInput, 4);
      this.blockLacingSampleCount = ((this.scratch.data[3] & 0xFF) + 1);
      this.blockLacingSampleSizes = ensureArrayCapacity(this.blockLacingSampleSizes, this.blockLacingSampleCount);
      if (i == 2)
      {
        paramInt2 = (paramInt2 - this.blockTrackNumberLength - 4) / this.blockLacingSampleCount;
        Arrays.fill(this.blockLacingSampleSizes, 0, this.blockLacingSampleCount, paramInt2);
        break;
      }
      int k;
      int j;
      int m;
      int n;
      Object localObject;
      if (i == 1)
      {
        k = 0;
        i = 4;
        j = 0;
        while (j < this.blockLacingSampleCount - 1)
        {
          this.blockLacingSampleSizes[j] = 0;
          m = i;
          do
          {
            i = m + 1;
            readScratch(paramExtractorInput, i);
            n = this.scratch.data[(i - 1)] & 0xFF;
            localObject = this.blockLacingSampleSizes;
            localObject[j] += n;
            m = i;
          } while (n == 255);
          k += this.blockLacingSampleSizes[j];
          j += 1;
        }
        this.blockLacingSampleSizes[(this.blockLacingSampleCount - 1)] = (paramInt2 - this.blockTrackNumberLength - i - k);
        break;
      }
      if (i == 3)
      {
        k = 0;
        i = 4;
        j = 0;
        if (j < this.blockLacingSampleCount - 1)
        {
          this.blockLacingSampleSizes[j] = 0;
          n = i + 1;
          readScratch(paramExtractorInput, n);
          if (this.scratch.data[(n - 1)] == 0) {
            throw new ParserException("No valid varint length mask found");
          }
          long l2 = 0L;
          m = 0;
          long l1;
          for (;;)
          {
            i = n;
            l1 = l2;
            if (m < 8)
            {
              int i1 = 1 << 7 - m;
              if ((this.scratch.data[(n - 1)] & i1) == 0) {
                break label1100;
              }
              int i2 = n - 1;
              n += m;
              readScratch(paramExtractorInput, n);
              localObject = this.scratch.data;
              i = i2 + 1;
              l2 = localObject[i2] & 0xFF & (i1 ^ 0xFFFFFFFF);
              while (i < n)
              {
                l2 = l2 << 8 | this.scratch.data[i] & 0xFF;
                i += 1;
              }
              i = n;
              l1 = l2;
              if (j > 0)
              {
                l1 = l2 - ((1L << m * 7 + 6) - 1L);
                i = n;
              }
            }
            if ((l1 >= -2147483648L) && (l1 <= 2147483647L)) {
              break;
            }
            throw new ParserException("EBML lacing sample size out of range.");
            m += 1;
          }
          m = (int)l1;
          localObject = this.blockLacingSampleSizes;
          if (j == 0) {}
          for (;;)
          {
            localObject[j] = m;
            k += this.blockLacingSampleSizes[j];
            j += 1;
            break;
            m += this.blockLacingSampleSizes[(j - 1)];
          }
        }
        this.blockLacingSampleSizes[(this.blockLacingSampleCount - 1)] = (paramInt2 - this.blockTrackNumberLength - i - k);
        break;
      }
      throw new ParserException("Unexpected lacing value: " + i);
      paramInt2 = 0;
      break label439;
      i = 0;
      break label477;
      i = 0;
      break label485;
    }
    this.blockState = 0;
    return;
    label1253:
    writeSampleData(paramExtractorInput, localTrack, this.blockLacingSampleSizes[0]);
  }
  
  void endMasterElement(int paramInt)
    throws ParserException
  {
    switch (paramInt)
    {
    default: 
    case 357149030: 
    case 19899: 
    case 475249515: 
    case 160: 
    case 25152: 
    case 28032: 
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
                do
                {
                  do
                  {
                    return;
                    if (this.timecodeScale == -1L) {
                      this.timecodeScale = 1000000L;
                    }
                  } while (this.durationTimecode == -1L);
                  this.durationUs = scaleTimecodeToUs(this.durationTimecode);
                  return;
                  if ((this.seekEntryId == -1) || (this.seekEntryPosition == -1L)) {
                    throw new ParserException("Mandatory element SeekID or SeekPosition not found");
                  }
                } while (this.seekEntryId != 475249515);
                this.cuesContentPosition = this.seekEntryPosition;
                return;
              } while (this.sentSeekMap);
              this.extractorOutput.seekMap(buildSeekMap());
              this.sentSeekMap = true;
              return;
            } while (this.blockState != 2);
            if (!this.sampleSeenReferenceBlock) {
              this.blockFlags |= 0x1;
            }
            commitSampleToOutput((Track)this.tracks.get(this.blockTrackNumber), this.blockTimeUs);
            this.blockState = 0;
            return;
          } while (!this.currentTrack.hasContentEncryption);
          if (this.currentTrack.encryptionKeyId == null) {
            throw new ParserException("Encrypted Track found but ContentEncKeyID was not found");
          }
        } while (this.sentDrmInitData);
        this.extractorOutput.drmInitData(new DrmInitData.Universal(new DrmInitData.SchemeInitData("video/webm", this.currentTrack.encryptionKeyId)));
        this.sentDrmInitData = true;
        return;
      } while ((!this.currentTrack.hasContentEncryption) || (this.currentTrack.sampleStrippedBytes == null));
      throw new ParserException("Combining encryption and compression is not supported");
    case 174: 
      if ((this.tracks.get(this.currentTrack.number) == null) && (isCodecSupported(this.currentTrack.codecId)))
      {
        this.currentTrack.initializeOutput(this.extractorOutput, this.currentTrack.number, this.durationUs);
        this.tracks.put(this.currentTrack.number, this.currentTrack);
      }
      this.currentTrack = null;
      return;
    }
    if (this.tracks.size() == 0) {
      throw new ParserException("No valid tracks were found");
    }
    this.extractorOutput.endTracks();
  }
  
  void floatElement(int paramInt, double paramDouble)
  {
    switch (paramInt)
    {
    default: 
      return;
    case 17545: 
      this.durationTimecode = (paramDouble);
      return;
    }
    this.currentTrack.sampleRate = ((int)paramDouble);
  }
  
  int getElementType(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return 0;
    case 160: 
    case 174: 
    case 183: 
    case 187: 
    case 224: 
    case 225: 
    case 18407: 
    case 19899: 
    case 20532: 
    case 20533: 
    case 25152: 
    case 28032: 
    case 290298740: 
    case 357149030: 
    case 374648427: 
    case 408125543: 
    case 440786851: 
    case 475249515: 
    case 524531317: 
      return 1;
    case 131: 
    case 155: 
    case 159: 
    case 176: 
    case 179: 
    case 186: 
    case 215: 
    case 231: 
    case 241: 
    case 251: 
    case 16980: 
    case 17029: 
    case 17143: 
    case 18401: 
    case 18408: 
    case 20529: 
    case 20530: 
    case 21420: 
    case 21680: 
    case 21682: 
    case 21690: 
    case 22186: 
    case 22203: 
    case 25188: 
    case 2352003: 
    case 2807729: 
      return 2;
    case 134: 
    case 17026: 
    case 2274716: 
      return 3;
    case 161: 
    case 163: 
    case 16981: 
    case 18402: 
    case 21419: 
    case 25506: 
      return 4;
    }
    return 5;
  }
  
  public void init(ExtractorOutput paramExtractorOutput)
  {
    this.extractorOutput = paramExtractorOutput;
  }
  
  void integerElement(int paramInt, long paramLong)
    throws ParserException
  {
    switch (paramInt)
    {
    default: 
    case 17143: 
    case 17029: 
    case 21420: 
    case 2807729: 
    case 176: 
    case 186: 
    case 21680: 
    case 21690: 
    case 21682: 
    case 215: 
    case 131: 
    case 2352003: 
    case 22186: 
    case 22203: 
    case 159: 
    case 25188: 
    case 251: 
    case 20529: 
    case 20530: 
    case 16980: 
    case 18401: 
    case 18408: 
    case 179: 
    case 241: 
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
                do
                {
                  do
                  {
                    do
                    {
                      return;
                    } while (paramLong == 1L);
                    throw new ParserException("EBMLReadVersion " + paramLong + " not supported");
                  } while ((paramLong >= 1L) && (paramLong <= 2L));
                  throw new ParserException("DocTypeReadVersion " + paramLong + " not supported");
                  this.seekEntryPosition = (this.segmentContentPosition + paramLong);
                  return;
                  this.timecodeScale = paramLong;
                  return;
                  this.currentTrack.width = ((int)paramLong);
                  return;
                  this.currentTrack.height = ((int)paramLong);
                  return;
                  this.currentTrack.displayWidth = ((int)paramLong);
                  return;
                  this.currentTrack.displayHeight = ((int)paramLong);
                  return;
                  this.currentTrack.displayUnit = ((int)paramLong);
                  return;
                  this.currentTrack.number = ((int)paramLong);
                  return;
                  this.currentTrack.type = ((int)paramLong);
                  return;
                  this.currentTrack.defaultSampleDurationNs = ((int)paramLong);
                  return;
                  this.currentTrack.codecDelayNs = paramLong;
                  return;
                  this.currentTrack.seekPreRollNs = paramLong;
                  return;
                  this.currentTrack.channelCount = ((int)paramLong);
                  return;
                  this.currentTrack.audioBitDepth = ((int)paramLong);
                  return;
                  this.sampleSeenReferenceBlock = true;
                  return;
                } while (paramLong == 0L);
                throw new ParserException("ContentEncodingOrder " + paramLong + " not supported");
              } while (paramLong == 1L);
              throw new ParserException("ContentEncodingScope " + paramLong + " not supported");
            } while (paramLong == 3L);
            throw new ParserException("ContentCompAlgo " + paramLong + " not supported");
          } while (paramLong == 5L);
          throw new ParserException("ContentEncAlgo " + paramLong + " not supported");
        } while (paramLong == 1L);
        throw new ParserException("AESSettingsCipherMode " + paramLong + " not supported");
        this.cueTimesUs.add(scaleTimecodeToUs(paramLong));
        return;
      } while (this.seenClusterPositionForCurrentCuePoint);
      this.cueClusterPositions.add(paramLong);
      this.seenClusterPositionForCurrentCuePoint = true;
      return;
    case 231: 
      this.clusterTimecodeUs = scaleTimecodeToUs(paramLong);
      return;
    }
    this.blockDurationUs = scaleTimecodeToUs(paramLong);
  }
  
  boolean isLevel1Element(int paramInt)
  {
    return (paramInt == 357149030) || (paramInt == 524531317) || (paramInt == 475249515) || (paramInt == 374648427);
  }
  
  public int read(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
    throws IOException, InterruptedException
  {
    int i = 0;
    this.sampleRead = false;
    int j = 1;
    while ((j != 0) && (!this.sampleRead))
    {
      bool = this.reader.read(paramExtractorInput);
      j = bool;
      if (bool)
      {
        j = bool;
        if (maybeSeekForCues(paramPositionHolder, paramExtractorInput.getPosition())) {
          i = 1;
        }
      }
    }
    while (j != 0)
    {
      boolean bool;
      return i;
    }
    return -1;
  }
  
  public void release() {}
  
  public void seek()
  {
    this.clusterTimecodeUs = -1L;
    this.blockState = 0;
    this.reader.reset();
    this.varintReader.reset();
    resetSample();
  }
  
  public boolean sniff(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    return new Sniffer().sniff(paramExtractorInput);
  }
  
  void startMasterElement(int paramInt, long paramLong1, long paramLong2)
    throws ParserException
  {
    switch (paramInt)
    {
    case 25152: 
    default: 
    case 408125543: 
    case 19899: 
    case 475249515: 
    case 187: 
    case 524531317: 
      do
      {
        return;
        if ((this.segmentContentPosition != -1L) && (this.segmentContentPosition != paramLong1)) {
          throw new ParserException("Multiple Segment elements not supported");
        }
        this.segmentContentPosition = paramLong1;
        this.segmentContentSize = paramLong2;
        return;
        this.seekEntryId = -1;
        this.seekEntryPosition = -1L;
        return;
        this.cueTimesUs = new LongArray();
        this.cueClusterPositions = new LongArray();
        return;
        this.seenClusterPositionForCurrentCuePoint = false;
        return;
      } while (this.sentSeekMap);
      if (this.cuesContentPosition != -1L)
      {
        this.seekForCues = true;
        return;
      }
      this.extractorOutput.seekMap(SeekMap.UNSEEKABLE);
      this.sentSeekMap = true;
      return;
    case 160: 
      this.sampleSeenReferenceBlock = false;
      return;
    case 20533: 
      this.currentTrack.hasContentEncryption = true;
      return;
    }
    this.currentTrack = new Track(null);
  }
  
  void stringElement(int paramInt, String paramString)
    throws ParserException
  {
    switch (paramInt)
    {
    default: 
    case 17026: 
      do
      {
        return;
      } while (("webm".equals(paramString)) || ("matroska".equals(paramString)));
      throw new ParserException("DocType " + paramString + " not supported");
    case 134: 
      this.currentTrack.codecId = paramString;
      return;
    }
    Track.access$202(this.currentTrack, paramString);
  }
  
  private final class InnerEbmlReaderOutput
    implements EbmlReaderOutput
  {
    private InnerEbmlReaderOutput() {}
    
    public void binaryElement(int paramInt1, int paramInt2, ExtractorInput paramExtractorInput)
      throws IOException, InterruptedException
    {
      WebmExtractor.this.binaryElement(paramInt1, paramInt2, paramExtractorInput);
    }
    
    public void endMasterElement(int paramInt)
      throws ParserException
    {
      WebmExtractor.this.endMasterElement(paramInt);
    }
    
    public void floatElement(int paramInt, double paramDouble)
      throws ParserException
    {
      WebmExtractor.this.floatElement(paramInt, paramDouble);
    }
    
    public int getElementType(int paramInt)
    {
      return WebmExtractor.this.getElementType(paramInt);
    }
    
    public void integerElement(int paramInt, long paramLong)
      throws ParserException
    {
      WebmExtractor.this.integerElement(paramInt, paramLong);
    }
    
    public boolean isLevel1Element(int paramInt)
    {
      return WebmExtractor.this.isLevel1Element(paramInt);
    }
    
    public void startMasterElement(int paramInt, long paramLong1, long paramLong2)
      throws ParserException
    {
      WebmExtractor.this.startMasterElement(paramInt, paramLong1, paramLong2);
    }
    
    public void stringElement(int paramInt, String paramString)
      throws ParserException
    {
      WebmExtractor.this.stringElement(paramInt, paramString);
    }
  }
  
  private static final class Track
  {
    private static final int DISPLAY_UNIT_PIXELS = 0;
    public int audioBitDepth = -1;
    public int channelCount = 1;
    public long codecDelayNs = 0L;
    public String codecId;
    public byte[] codecPrivate;
    public int defaultSampleDurationNs;
    public int displayHeight = -1;
    public int displayUnit = 0;
    public int displayWidth = -1;
    public byte[] encryptionKeyId;
    public boolean hasContentEncryption;
    public int height = -1;
    private String language = "eng";
    public int nalUnitLengthFieldLength;
    public int number;
    public TrackOutput output;
    public int sampleRate = 8000;
    public byte[] sampleStrippedBytes;
    public long seekPreRollNs = 0L;
    public int type;
    public int width = -1;
    
    private static Pair<List<byte[]>, Integer> parseAvcCodecPrivate(ParsableByteArray paramParsableByteArray)
      throws ParserException
    {
      int j;
      try
      {
        paramParsableByteArray.setPosition(4);
        j = (paramParsableByteArray.readUnsignedByte() & 0x3) + 1;
        if (j == 3) {
          throw new ParserException();
        }
      }
      catch (ArrayIndexOutOfBoundsException paramParsableByteArray)
      {
        throw new ParserException("Error parsing AVC codec private");
      }
      ArrayList localArrayList = new ArrayList();
      int k = paramParsableByteArray.readUnsignedByte();
      int i = 0;
      while (i < (k & 0x1F))
      {
        localArrayList.add(NalUnitUtil.parseChildNalUnit(paramParsableByteArray));
        i += 1;
      }
      k = paramParsableByteArray.readUnsignedByte();
      i = 0;
      while (i < k)
      {
        localArrayList.add(NalUnitUtil.parseChildNalUnit(paramParsableByteArray));
        i += 1;
      }
      paramParsableByteArray = Pair.create(localArrayList, Integer.valueOf(j));
      return paramParsableByteArray;
    }
    
    private static List<byte[]> parseFourCcVc1Private(ParsableByteArray paramParsableByteArray)
      throws ParserException
    {
      try
      {
        paramParsableByteArray.skipBytes(16);
        long l = paramParsableByteArray.readLittleEndianUnsignedInt();
        if (l != 826496599L) {
          throw new ParserException("Unsupported FourCC compression type: " + l);
        }
      }
      catch (ArrayIndexOutOfBoundsException paramParsableByteArray)
      {
        throw new ParserException("Error parsing FourCC VC1 codec private");
      }
      int i = paramParsableByteArray.getPosition();
      paramParsableByteArray = paramParsableByteArray.data;
      i += 20;
      for (;;)
      {
        if (i < paramParsableByteArray.length - 4)
        {
          if ((paramParsableByteArray[i] == 0) && (paramParsableByteArray[(i + 1)] == 0) && (paramParsableByteArray[(i + 2)] == 1) && (paramParsableByteArray[(i + 3)] == 15)) {
            return Collections.singletonList(Arrays.copyOfRange(paramParsableByteArray, i, paramParsableByteArray.length));
          }
        }
        else {
          throw new ParserException("Failed to find FourCC VC1 initialization data");
        }
        i += 1;
      }
    }
    
    private static Pair<List<byte[]>, Integer> parseHevcCodecPrivate(ParsableByteArray paramParsableByteArray)
      throws ParserException
    {
      for (;;)
      {
        int i;
        int j;
        try
        {
          paramParsableByteArray.setPosition(21);
          int n = paramParsableByteArray.readUnsignedByte();
          int i1 = paramParsableByteArray.readUnsignedByte();
          i = 0;
          int m = paramParsableByteArray.getPosition();
          j = 0;
          if (j < i1)
          {
            paramParsableByteArray.skipBytes(1);
            i2 = paramParsableByteArray.readUnsignedShort();
            k = 0;
            if (k >= i2) {
              break label221;
            }
            i3 = paramParsableByteArray.readUnsignedShort();
            i += i3 + 4;
            paramParsableByteArray.skipBytes(i3);
            k += 1;
            continue;
          }
          paramParsableByteArray.setPosition(m);
          arrayOfByte = new byte[i];
          m = 0;
          j = 0;
          if (j >= i1) {
            break label235;
          }
          paramParsableByteArray.skipBytes(1);
          int i2 = paramParsableByteArray.readUnsignedShort();
          int k = 0;
          if (k >= i2) {
            break label228;
          }
          int i3 = paramParsableByteArray.readUnsignedShort();
          System.arraycopy(NalUnitUtil.NAL_START_CODE, 0, arrayOfByte, m, NalUnitUtil.NAL_START_CODE.length);
          m += NalUnitUtil.NAL_START_CODE.length;
          System.arraycopy(paramParsableByteArray.data, paramParsableByteArray.getPosition(), arrayOfByte, m, i3);
          m += i3;
          paramParsableByteArray.skipBytes(i3);
          k += 1;
          continue;
          return Pair.create(paramParsableByteArray, Integer.valueOf((n & 0x3) + 1));
        }
        catch (ArrayIndexOutOfBoundsException paramParsableByteArray)
        {
          byte[] arrayOfByte;
          throw new ParserException("Error parsing HEVC codec private");
        }
        paramParsableByteArray = Collections.singletonList(arrayOfByte);
        continue;
        label221:
        j += 1;
        continue;
        label228:
        j += 1;
        continue;
        label235:
        if (i == 0) {
          paramParsableByteArray = null;
        }
      }
    }
    
    private static boolean parseMsAcmCodecPrivate(ParsableByteArray paramParsableByteArray)
      throws ParserException
    {
      try
      {
        int i = paramParsableByteArray.readLittleEndianUnsignedShort();
        if (i == 1) {
          return true;
        }
        if (i == 65534)
        {
          paramParsableByteArray.setPosition(24);
          if (paramParsableByteArray.readLong() == WebmExtractor.WAVE_SUBFORMAT_PCM.getMostSignificantBits())
          {
            long l1 = paramParsableByteArray.readLong();
            long l2 = WebmExtractor.WAVE_SUBFORMAT_PCM.getLeastSignificantBits();
            if (l1 == l2) {}
          }
          else
          {
            return false;
          }
        }
        else
        {
          return false;
        }
      }
      catch (ArrayIndexOutOfBoundsException paramParsableByteArray)
      {
        throw new ParserException("Error parsing MS/ACM codec private");
      }
      return true;
    }
    
    private static List<byte[]> parseVorbisCodecPrivate(byte[] paramArrayOfByte)
      throws ParserException
    {
      if (paramArrayOfByte[0] != 2) {
        try
        {
          throw new ParserException("Error parsing vorbis codec private");
        }
        catch (ArrayIndexOutOfBoundsException paramArrayOfByte)
        {
          throw new ParserException("Error parsing vorbis codec private");
        }
      }
      int j = 0;
      int i = 1;
      while (paramArrayOfByte[i] == -1)
      {
        j += 255;
        i += 1;
      }
      for (;;)
      {
        int k = j + 1;
        j = paramArrayOfByte[j];
        if (paramArrayOfByte[k] != 1) {
          throw new ParserException("Error parsing vorbis codec private");
        }
        byte[] arrayOfByte1 = new byte[m];
        System.arraycopy(paramArrayOfByte, k, arrayOfByte1, 0, m);
        k += m;
        if (paramArrayOfByte[k] != 3) {
          throw new ParserException("Error parsing vorbis codec private");
        }
        i = k + (i + j);
        if (paramArrayOfByte[i] != 5) {
          throw new ParserException("Error parsing vorbis codec private");
        }
        byte[] arrayOfByte2 = new byte[paramArrayOfByte.length - i];
        System.arraycopy(paramArrayOfByte, i, arrayOfByte2, 0, paramArrayOfByte.length - i);
        paramArrayOfByte = new ArrayList(2);
        paramArrayOfByte.add(arrayOfByte1);
        paramArrayOfByte.add(arrayOfByte2);
        return paramArrayOfByte;
        int m = j + paramArrayOfByte[i];
        j = 0;
        k = i + 1;
        i = j;
        j = k;
        while (paramArrayOfByte[j] == -1)
        {
          i += 255;
          j += 1;
        }
      }
    }
    
    public void initializeOutput(ExtractorOutput paramExtractorOutput, int paramInt, long paramLong)
      throws ParserException
    {
      int k = -1;
      int j = -1;
      Object localObject2 = null;
      Object localObject1 = this.codecId;
      int i = -1;
      switch (((String)localObject1).hashCode())
      {
      }
      for (;;)
      {
        switch (i)
        {
        default: 
          throw new ParserException("Unrecognized codec identifier.");
          if (((String)localObject1).equals("V_VP8"))
          {
            i = 0;
            continue;
            if (((String)localObject1).equals("V_VP9"))
            {
              i = 1;
              continue;
              if (((String)localObject1).equals("V_MPEG2"))
              {
                i = 2;
                continue;
                if (((String)localObject1).equals("V_MPEG4/ISO/SP"))
                {
                  i = 3;
                  continue;
                  if (((String)localObject1).equals("V_MPEG4/ISO/ASP"))
                  {
                    i = 4;
                    continue;
                    if (((String)localObject1).equals("V_MPEG4/ISO/AP"))
                    {
                      i = 5;
                      continue;
                      if (((String)localObject1).equals("V_MPEG4/ISO/AVC"))
                      {
                        i = 6;
                        continue;
                        if (((String)localObject1).equals("V_MPEGH/ISO/HEVC"))
                        {
                          i = 7;
                          continue;
                          if (((String)localObject1).equals("V_MS/VFW/FOURCC"))
                          {
                            i = 8;
                            continue;
                            if (((String)localObject1).equals("A_VORBIS"))
                            {
                              i = 9;
                              continue;
                              if (((String)localObject1).equals("A_OPUS"))
                              {
                                i = 10;
                                continue;
                                if (((String)localObject1).equals("A_AAC"))
                                {
                                  i = 11;
                                  continue;
                                  if (((String)localObject1).equals("A_MPEG/L3"))
                                  {
                                    i = 12;
                                    continue;
                                    if (((String)localObject1).equals("A_AC3"))
                                    {
                                      i = 13;
                                      continue;
                                      if (((String)localObject1).equals("A_EAC3"))
                                      {
                                        i = 14;
                                        continue;
                                        if (((String)localObject1).equals("A_TRUEHD"))
                                        {
                                          i = 15;
                                          continue;
                                          if (((String)localObject1).equals("A_DTS"))
                                          {
                                            i = 16;
                                            continue;
                                            if (((String)localObject1).equals("A_DTS/EXPRESS"))
                                            {
                                              i = 17;
                                              continue;
                                              if (((String)localObject1).equals("A_DTS/LOSSLESS"))
                                              {
                                                i = 18;
                                                continue;
                                                if (((String)localObject1).equals("A_FLAC"))
                                                {
                                                  i = 19;
                                                  continue;
                                                  if (((String)localObject1).equals("A_MS/ACM"))
                                                  {
                                                    i = 20;
                                                    continue;
                                                    if (((String)localObject1).equals("A_PCM/INT/LIT"))
                                                    {
                                                      i = 21;
                                                      continue;
                                                      if (((String)localObject1).equals("S_TEXT/UTF8"))
                                                      {
                                                        i = 22;
                                                        continue;
                                                        if (((String)localObject1).equals("S_VOBSUB"))
                                                        {
                                                          i = 23;
                                                          continue;
                                                          if (((String)localObject1).equals("S_HDMV/PGS")) {
                                                            i = 24;
                                                          }
                                                        }
                                                      }
                                                    }
                                                  }
                                                }
                                              }
                                            }
                                          }
                                        }
                                      }
                                    }
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
          break;
        }
      }
      localObject1 = "video/x-vnd.on2.vp8";
      i = k;
      if (MimeTypes.isAudio((String)localObject1)) {
        localObject1 = MediaFormat.createAudioFormat(Integer.toString(paramInt), (String)localObject1, -1, i, paramLong, this.channelCount, this.sampleRate, (List)localObject2, this.language, j);
      }
      for (;;)
      {
        this.output = paramExtractorOutput.track(this.number);
        this.output.format((MediaFormat)localObject1);
        return;
        localObject1 = "video/x-vnd.on2.vp9";
        i = k;
        break;
        localObject1 = "video/mpeg2";
        i = k;
        break;
        localObject1 = "video/mp4v-es";
        if (this.codecPrivate == null) {}
        for (localObject2 = null;; localObject2 = Collections.singletonList(this.codecPrivate))
        {
          i = k;
          break;
        }
        localObject1 = "video/avc";
        Pair localPair = parseAvcCodecPrivate(new ParsableByteArray(this.codecPrivate));
        localObject2 = (List)localPair.first;
        this.nalUnitLengthFieldLength = ((Integer)localPair.second).intValue();
        i = k;
        break;
        localObject1 = "video/hevc";
        localPair = parseHevcCodecPrivate(new ParsableByteArray(this.codecPrivate));
        localObject2 = (List)localPair.first;
        this.nalUnitLengthFieldLength = ((Integer)localPair.second).intValue();
        i = k;
        break;
        localObject1 = "video/wvc1";
        localObject2 = parseFourCcVc1Private(new ParsableByteArray(this.codecPrivate));
        i = k;
        break;
        localObject1 = "audio/vorbis";
        i = 8192;
        localObject2 = parseVorbisCodecPrivate(this.codecPrivate);
        break;
        localObject1 = "audio/opus";
        i = 5760;
        localObject2 = new ArrayList(3);
        ((List)localObject2).add(this.codecPrivate);
        ((List)localObject2).add(ByteBuffer.allocate(8).order(ByteOrder.nativeOrder()).putLong(this.codecDelayNs).array());
        ((List)localObject2).add(ByteBuffer.allocate(8).order(ByteOrder.nativeOrder()).putLong(this.seekPreRollNs).array());
        break;
        localObject1 = "audio/mp4a-latm";
        localObject2 = Collections.singletonList(this.codecPrivate);
        i = k;
        break;
        localObject1 = "audio/mpeg";
        i = 4096;
        break;
        localObject1 = "audio/ac3";
        i = k;
        break;
        localObject1 = "audio/eac3";
        i = k;
        break;
        localObject1 = "audio/true-hd";
        i = k;
        break;
        localObject1 = "audio/vnd.dts";
        i = k;
        break;
        localObject1 = "audio/vnd.dts.hd";
        i = k;
        break;
        localObject1 = "audio/x-flac";
        localObject2 = Collections.singletonList(this.codecPrivate);
        i = k;
        break;
        localObject1 = "audio/raw";
        if (!parseMsAcmCodecPrivate(new ParsableByteArray(this.codecPrivate))) {
          throw new ParserException("Non-PCM MS/ACM is unsupported");
        }
        int m = Util.getPcmEncoding(this.audioBitDepth);
        i = k;
        j = m;
        if (m != 0) {
          break;
        }
        throw new ParserException("Unsupported PCM bit depth: " + this.audioBitDepth);
        localObject1 = "audio/raw";
        m = Util.getPcmEncoding(this.audioBitDepth);
        i = k;
        j = m;
        if (m != 0) {
          break;
        }
        throw new ParserException("Unsupported PCM bit depth: " + this.audioBitDepth);
        localObject1 = "application/x-subrip";
        i = k;
        break;
        localObject1 = "application/vobsub";
        localObject2 = Collections.singletonList(this.codecPrivate);
        i = k;
        break;
        localObject1 = "application/pgs";
        i = k;
        break;
        if (MimeTypes.isVideo((String)localObject1))
        {
          if (this.displayUnit == 0)
          {
            if (this.displayWidth != -1) {
              break label1613;
            }
            j = this.width;
            label1504:
            this.displayWidth = j;
            if (this.displayHeight != -1) {
              break label1622;
            }
          }
          label1613:
          label1622:
          for (j = this.height;; j = this.displayHeight)
          {
            this.displayHeight = j;
            float f2 = -1.0F;
            float f1 = f2;
            if (this.displayWidth != -1)
            {
              f1 = f2;
              if (this.displayHeight != -1) {
                f1 = this.height * this.displayWidth / (this.width * this.displayHeight);
              }
            }
            localObject1 = MediaFormat.createVideoFormat(Integer.toString(paramInt), (String)localObject1, -1, i, paramLong, this.width, this.height, (List)localObject2, -1, f1);
            break;
            j = this.displayWidth;
            break label1504;
          }
        }
        if ("application/x-subrip".equals(localObject1))
        {
          localObject1 = MediaFormat.createTextFormat(Integer.toString(paramInt), (String)localObject1, -1, paramLong, this.language);
        }
        else
        {
          if ((!"application/vobsub".equals(localObject1)) && (!"application/pgs".equals(localObject1))) {
            break label1706;
          }
          localObject1 = MediaFormat.createImageFormat(Integer.toString(paramInt), (String)localObject1, -1, paramLong, (List)localObject2, this.language);
        }
      }
      label1706:
      throw new ParserException("Unexpected MIME type.");
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/extractor/webm/WebmExtractor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
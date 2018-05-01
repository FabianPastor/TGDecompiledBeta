package org.telegram.messenger.exoplayer2.extractor.mkv;

import android.util.Log;
import android.util.SparseArray;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.audio.Ac3Util;
import org.telegram.messenger.exoplayer2.drm.DrmInitData;
import org.telegram.messenger.exoplayer2.drm.DrmInitData.SchemeData;
import org.telegram.messenger.exoplayer2.extractor.ChunkIndex;
import org.telegram.messenger.exoplayer2.extractor.Extractor;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.extractor.ExtractorsFactory;
import org.telegram.messenger.exoplayer2.extractor.PositionHolder;
import org.telegram.messenger.exoplayer2.extractor.SeekMap;
import org.telegram.messenger.exoplayer2.extractor.SeekMap.Unseekable;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput.CryptoData;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.LongArray;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.exoplayer2.util.NalUnitUtil;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.Util;
import org.telegram.messenger.exoplayer2.video.AvcConfig;
import org.telegram.messenger.exoplayer2.video.ColorInfo;
import org.telegram.messenger.exoplayer2.video.HevcConfig;

public final class MatroskaExtractor
  implements Extractor
{
  private static final int BLOCK_STATE_DATA = 2;
  private static final int BLOCK_STATE_HEADER = 1;
  private static final int BLOCK_STATE_START = 0;
  private static final String CODEC_ID_AAC = "A_AAC";
  private static final String CODEC_ID_AC3 = "A_AC3";
  private static final String CODEC_ID_ACM = "A_MS/ACM";
  private static final String CODEC_ID_ASS = "S_TEXT/ASS";
  private static final String CODEC_ID_DTS = "A_DTS";
  private static final String CODEC_ID_DTS_EXPRESS = "A_DTS/EXPRESS";
  private static final String CODEC_ID_DTS_LOSSLESS = "A_DTS/LOSSLESS";
  private static final String CODEC_ID_DVBSUB = "S_DVBSUB";
  private static final String CODEC_ID_E_AC3 = "A_EAC3";
  private static final String CODEC_ID_FLAC = "A_FLAC";
  private static final String CODEC_ID_FOURCC = "V_MS/VFW/FOURCC";
  private static final String CODEC_ID_H264 = "V_MPEG4/ISO/AVC";
  private static final String CODEC_ID_H265 = "V_MPEGH/ISO/HEVC";
  private static final String CODEC_ID_MP2 = "A_MPEG/L2";
  private static final String CODEC_ID_MP3 = "A_MPEG/L3";
  private static final String CODEC_ID_MPEG2 = "V_MPEG2";
  private static final String CODEC_ID_MPEG4_AP = "V_MPEG4/ISO/AP";
  private static final String CODEC_ID_MPEG4_ASP = "V_MPEG4/ISO/ASP";
  private static final String CODEC_ID_MPEG4_SP = "V_MPEG4/ISO/SP";
  private static final String CODEC_ID_OPUS = "A_OPUS";
  private static final String CODEC_ID_PCM_INT_LIT = "A_PCM/INT/LIT";
  private static final String CODEC_ID_PGS = "S_HDMV/PGS";
  private static final String CODEC_ID_SUBRIP = "S_TEXT/UTF8";
  private static final String CODEC_ID_THEORA = "V_THEORA";
  private static final String CODEC_ID_TRUEHD = "A_TRUEHD";
  private static final String CODEC_ID_VOBSUB = "S_VOBSUB";
  private static final String CODEC_ID_VORBIS = "A_VORBIS";
  private static final String CODEC_ID_VP8 = "V_VP8";
  private static final String CODEC_ID_VP9 = "V_VP9";
  private static final String DOC_TYPE_MATROSKA = "matroska";
  private static final String DOC_TYPE_WEBM = "webm";
  private static final int ENCRYPTION_IV_SIZE = 8;
  public static final ExtractorsFactory FACTORY = new ExtractorsFactory()
  {
    public Extractor[] createExtractors()
    {
      return new Extractor[] { new MatroskaExtractor() };
    }
  };
  public static final int FLAG_DISABLE_SEEK_FOR_CUES = 1;
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
  private static final int ID_COLOUR = 21936;
  private static final int ID_COLOUR_PRIMARIES = 21947;
  private static final int ID_COLOUR_RANGE = 21945;
  private static final int ID_COLOUR_TRANSFER = 21946;
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
  private static final int ID_FLAG_DEFAULT = 136;
  private static final int ID_FLAG_FORCED = 21930;
  private static final int ID_INFO = 357149030;
  private static final int ID_LANGUAGE = 2274716;
  private static final int ID_LUMNINANCE_MAX = 21977;
  private static final int ID_LUMNINANCE_MIN = 21978;
  private static final int ID_MASTERING_METADATA = 21968;
  private static final int ID_MAX_CLL = 21948;
  private static final int ID_MAX_FALL = 21949;
  private static final int ID_PIXEL_HEIGHT = 186;
  private static final int ID_PIXEL_WIDTH = 176;
  private static final int ID_PRIMARY_B_CHROMATICITY_X = 21973;
  private static final int ID_PRIMARY_B_CHROMATICITY_Y = 21974;
  private static final int ID_PRIMARY_G_CHROMATICITY_X = 21971;
  private static final int ID_PRIMARY_G_CHROMATICITY_Y = 21972;
  private static final int ID_PRIMARY_R_CHROMATICITY_X = 21969;
  private static final int ID_PRIMARY_R_CHROMATICITY_Y = 21970;
  private static final int ID_PROJECTION = 30320;
  private static final int ID_PROJECTION_PRIVATE = 30322;
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
  private static final int ID_STEREO_MODE = 21432;
  private static final int ID_TIMECODE_SCALE = 2807729;
  private static final int ID_TIME_CODE = 231;
  private static final int ID_TRACKS = 374648427;
  private static final int ID_TRACK_ENTRY = 174;
  private static final int ID_TRACK_NUMBER = 215;
  private static final int ID_TRACK_TYPE = 131;
  private static final int ID_VIDEO = 224;
  private static final int ID_WHITE_POINT_CHROMATICITY_X = 21975;
  private static final int ID_WHITE_POINT_CHROMATICITY_Y = 21976;
  private static final int LACING_EBML = 3;
  private static final int LACING_FIXED_SIZE = 2;
  private static final int LACING_NONE = 0;
  private static final int LACING_XIPH = 1;
  private static final int OPUS_MAX_INPUT_SIZE = 5760;
  private static final byte[] SSA_DIALOGUE_FORMAT = Util.getUtf8Bytes("Format: Start, End, ReadOrder, Layer, Style, Name, MarginL, MarginR, MarginV, Effect, Text");
  private static final byte[] SSA_PREFIX = { 68, 105, 97, 108, 111, 103, 117, 101, 58, 32, 48, 58, 48, 48, 58, 48, 48, 58, 48, 48, 44, 48, 58, 48, 48, 58, 48, 48, 58, 48, 48, 44 };
  private static final int SSA_PREFIX_END_TIMECODE_OFFSET = 21;
  private static final byte[] SSA_TIMECODE_EMPTY = { 32, 32, 32, 32, 32, 32, 32, 32, 32, 32 };
  private static final String SSA_TIMECODE_FORMAT = "%01d:%02d:%02d:%02d";
  private static final long SSA_TIMECODE_LAST_VALUE_SCALING_FACTOR = 10000L;
  private static final byte[] SUBRIP_PREFIX = { 49, 10, 48, 48, 58, 48, 48, 58, 48, 48, 44, 48, 48, 48, 32, 45, 45, 62, 32, 48, 48, 58, 48, 48, 58, 48, 48, 44, 48, 48, 48, 10 };
  private static final int SUBRIP_PREFIX_END_TIMECODE_OFFSET = 19;
  private static final byte[] SUBRIP_TIMECODE_EMPTY = { 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32 };
  private static final String SUBRIP_TIMECODE_FORMAT = "%02d:%02d:%02d,%03d";
  private static final long SUBRIP_TIMECODE_LAST_VALUE_SCALING_FACTOR = 1000L;
  private static final String TAG = "MatroskaExtractor";
  private static final int TRACK_TYPE_AUDIO = 2;
  private static final int UNSET_ENTRY_ID = -1;
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
  private long clusterTimecodeUs = -9223372036854775807L;
  private LongArray cueClusterPositions;
  private LongArray cueTimesUs;
  private long cuesContentPosition = -1L;
  private Track currentTrack;
  private long durationTimecode = -9223372036854775807L;
  private long durationUs = -9223372036854775807L;
  private final ParsableByteArray encryptionInitializationVector;
  private final ParsableByteArray encryptionSubsampleData;
  private ByteBuffer encryptionSubsampleDataBuffer;
  private ExtractorOutput extractorOutput;
  private final ParsableByteArray nalLength;
  private final ParsableByteArray nalStartCode;
  private final EbmlReader reader;
  private int sampleBytesRead;
  private int sampleBytesWritten;
  private int sampleCurrentNalBytesRemaining;
  private boolean sampleEncodingHandled;
  private boolean sampleInitializationVectorRead;
  private int samplePartitionCount;
  private boolean samplePartitionCountRead;
  private boolean sampleRead;
  private boolean sampleSeenReferenceBlock;
  private byte sampleSignalByte;
  private boolean sampleSignalByteRead;
  private final ParsableByteArray sampleStrippedBytes;
  private final ParsableByteArray scratch;
  private int seekEntryId;
  private final ParsableByteArray seekEntryIdBytes;
  private long seekEntryPosition;
  private boolean seekForCues;
  private final boolean seekForCuesEnabled;
  private long seekPositionAfterBuildingCues = -1L;
  private boolean seenClusterPositionForCurrentCuePoint;
  private long segmentContentPosition = -1L;
  private long segmentContentSize;
  private boolean sentSeekMap;
  private final ParsableByteArray subtitleSample;
  private long timecodeScale = -9223372036854775807L;
  private final SparseArray<Track> tracks;
  private final VarintReader varintReader;
  private final ParsableByteArray vorbisNumPageSamples;
  
  public MatroskaExtractor()
  {
    this(0);
  }
  
  public MatroskaExtractor(int paramInt)
  {
    this(new DefaultEbmlReader(), paramInt);
  }
  
  MatroskaExtractor(EbmlReader paramEbmlReader, int paramInt)
  {
    this.reader = paramEbmlReader;
    this.reader.init(new InnerEbmlReaderOutput(null));
    if ((paramInt & 0x1) == 0) {}
    for (boolean bool = true;; bool = false)
    {
      this.seekForCuesEnabled = bool;
      this.varintReader = new VarintReader();
      this.tracks = new SparseArray();
      this.scratch = new ParsableByteArray(4);
      this.vorbisNumPageSamples = new ParsableByteArray(ByteBuffer.allocate(4).putInt(-1).array());
      this.seekEntryIdBytes = new ParsableByteArray(4);
      this.nalStartCode = new ParsableByteArray(NalUnitUtil.NAL_START_CODE);
      this.nalLength = new ParsableByteArray(4);
      this.sampleStrippedBytes = new ParsableByteArray();
      this.subtitleSample = new ParsableByteArray();
      this.encryptionInitializationVector = new ParsableByteArray(8);
      this.encryptionSubsampleData = new ParsableByteArray();
      return;
    }
  }
  
  private SeekMap buildSeekMap()
  {
    if ((this.segmentContentPosition == -1L) || (this.durationUs == -9223372036854775807L) || (this.cueTimesUs == null) || (this.cueTimesUs.size() == 0) || (this.cueClusterPositions == null) || (this.cueClusterPositions.size() != this.cueTimesUs.size()))
    {
      this.cueTimesUs = null;
      this.cueClusterPositions = null;
    }
    long[] arrayOfLong1;
    long[] arrayOfLong2;
    long[] arrayOfLong3;
    for (Object localObject = new SeekMap.Unseekable(this.durationUs);; localObject = new ChunkIndex((int[])localObject, arrayOfLong1, arrayOfLong2, arrayOfLong3))
    {
      return (SeekMap)localObject;
      int i = this.cueTimesUs.size();
      localObject = new int[i];
      arrayOfLong1 = new long[i];
      arrayOfLong2 = new long[i];
      arrayOfLong3 = new long[i];
      for (int j = 0; j < i; j++)
      {
        arrayOfLong3[j] = this.cueTimesUs.get(j);
        arrayOfLong1[j] = (this.segmentContentPosition + this.cueClusterPositions.get(j));
      }
      for (j = 0; j < i - 1; j++)
      {
        localObject[j] = ((int)(arrayOfLong1[(j + 1)] - arrayOfLong1[j]));
        arrayOfLong2[j] = (arrayOfLong3[(j + 1)] - arrayOfLong3[j]);
      }
      localObject[(i - 1)] = ((int)(this.segmentContentPosition + this.segmentContentSize - arrayOfLong1[(i - 1)]));
      arrayOfLong2[(i - 1)] = (this.durationUs - arrayOfLong3[(i - 1)]);
      this.cueTimesUs = null;
      this.cueClusterPositions = null;
    }
  }
  
  private void commitSampleToOutput(Track paramTrack, long paramLong)
  {
    if (paramTrack.trueHdSampleRechunker != null)
    {
      paramTrack.trueHdSampleRechunker.sampleMetadata(paramTrack, paramLong);
      this.sampleRead = true;
      resetSample();
      return;
    }
    if ("S_TEXT/UTF8".equals(paramTrack.codecId)) {
      commitSubtitleSample(paramTrack, "%02d:%02d:%02d,%03d", 19, 1000L, SUBRIP_TIMECODE_EMPTY);
    }
    for (;;)
    {
      paramTrack.output.sampleMetadata(paramLong, this.blockFlags, this.sampleBytesWritten, 0, paramTrack.cryptoData);
      break;
      if ("S_TEXT/ASS".equals(paramTrack.codecId)) {
        commitSubtitleSample(paramTrack, "%01d:%02d:%02d:%02d", 21, 10000L, SSA_TIMECODE_EMPTY);
      }
    }
  }
  
  private void commitSubtitleSample(Track paramTrack, String paramString, int paramInt, long paramLong, byte[] paramArrayOfByte)
  {
    setSampleDuration(this.subtitleSample.data, this.blockDurationUs, paramString, paramInt, paramLong, paramArrayOfByte);
    paramTrack.output.sampleData(this.subtitleSample, this.subtitleSample.limit());
    this.sampleBytesWritten += this.subtitleSample.limit();
  }
  
  private static int[] ensureArrayCapacity(int[] paramArrayOfInt, int paramInt)
  {
    int[] arrayOfInt;
    if (paramArrayOfInt == null) {
      arrayOfInt = new int[paramInt];
    }
    for (;;)
    {
      return arrayOfInt;
      arrayOfInt = paramArrayOfInt;
      if (paramArrayOfInt.length < paramInt) {
        arrayOfInt = new int[Math.max(paramArrayOfInt.length * 2, paramInt)];
      }
    }
  }
  
  private static boolean isCodecSupported(String paramString)
  {
    if (("V_VP8".equals(paramString)) || ("V_VP9".equals(paramString)) || ("V_MPEG2".equals(paramString)) || ("V_MPEG4/ISO/SP".equals(paramString)) || ("V_MPEG4/ISO/ASP".equals(paramString)) || ("V_MPEG4/ISO/AP".equals(paramString)) || ("V_MPEG4/ISO/AVC".equals(paramString)) || ("V_MPEGH/ISO/HEVC".equals(paramString)) || ("V_MS/VFW/FOURCC".equals(paramString)) || ("V_THEORA".equals(paramString)) || ("A_OPUS".equals(paramString)) || ("A_VORBIS".equals(paramString)) || ("A_AAC".equals(paramString)) || ("A_MPEG/L2".equals(paramString)) || ("A_MPEG/L3".equals(paramString)) || ("A_AC3".equals(paramString)) || ("A_EAC3".equals(paramString)) || ("A_TRUEHD".equals(paramString)) || ("A_DTS".equals(paramString)) || ("A_DTS/EXPRESS".equals(paramString)) || ("A_DTS/LOSSLESS".equals(paramString)) || ("A_FLAC".equals(paramString)) || ("A_MS/ACM".equals(paramString)) || ("A_PCM/INT/LIT".equals(paramString)) || ("S_TEXT/UTF8".equals(paramString)) || ("S_TEXT/ASS".equals(paramString)) || ("S_VOBSUB".equals(paramString)) || ("S_HDMV/PGS".equals(paramString)) || ("S_DVBSUB".equals(paramString))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private boolean maybeSeekForCues(PositionHolder paramPositionHolder, long paramLong)
  {
    boolean bool = true;
    if (this.seekForCues)
    {
      this.seekPositionAfterBuildingCues = paramLong;
      paramPositionHolder.position = this.cuesContentPosition;
      this.seekForCues = false;
    }
    for (;;)
    {
      return bool;
      if ((this.sentSeekMap) && (this.seekPositionAfterBuildingCues != -1L))
      {
        paramPositionHolder.position = this.seekPositionAfterBuildingCues;
        this.seekPositionAfterBuildingCues = -1L;
      }
      else
      {
        bool = false;
      }
    }
  }
  
  private void readScratch(ExtractorInput paramExtractorInput, int paramInt)
    throws IOException, InterruptedException
  {
    if (this.scratch.limit() >= paramInt) {}
    for (;;)
    {
      return;
      if (this.scratch.capacity() < paramInt) {
        this.scratch.reset(Arrays.copyOf(this.scratch.data, Math.max(this.scratch.data.length * 2, paramInt)), this.scratch.limit());
      }
      paramExtractorInput.readFully(this.scratch.data, this.scratch.limit(), paramInt - this.scratch.limit());
      this.scratch.setLimit(paramInt);
    }
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
    this.sampleSignalByteRead = false;
    this.samplePartitionCountRead = false;
    this.samplePartitionCount = 0;
    this.sampleSignalByte = ((byte)0);
    this.sampleInitializationVectorRead = false;
    this.sampleStrippedBytes.reset();
  }
  
  private long scaleTimecodeToUs(long paramLong)
    throws ParserException
  {
    if (this.timecodeScale == -9223372036854775807L) {
      throw new ParserException("Can't scale timecode prior to timecodeScale being set.");
    }
    return Util.scaleLargeTimestamp(paramLong, this.timecodeScale, 1000L);
  }
  
  private static void setSampleDuration(byte[] paramArrayOfByte1, long paramLong1, String paramString, int paramInt, long paramLong2, byte[] paramArrayOfByte2)
  {
    if (paramLong1 == -9223372036854775807L) {}
    int i;
    int j;
    int k;
    int m;
    for (paramString = paramArrayOfByte2;; paramString = Util.getUtf8Bytes(String.format(Locale.US, paramString, new Object[] { Integer.valueOf(i), Integer.valueOf(j), Integer.valueOf(k), Integer.valueOf(m) })))
    {
      System.arraycopy(paramString, 0, paramArrayOfByte1, paramInt, paramArrayOfByte2.length);
      return;
      i = (int)(paramLong1 / 3600000000L);
      paramLong1 -= i * 3600 * 1000000L;
      j = (int)(paramLong1 / 60000000L);
      paramLong1 -= j * 60 * 1000000L;
      k = (int)(paramLong1 / 1000000L);
      m = (int)((paramLong1 - k * 1000000L) / paramLong2);
    }
  }
  
  private void writeSampleData(ExtractorInput paramExtractorInput, Track paramTrack, int paramInt)
    throws IOException, InterruptedException
  {
    if ("S_TEXT/UTF8".equals(paramTrack.codecId)) {
      writeSubtitleSampleData(paramExtractorInput, SUBRIP_PREFIX, paramInt);
    }
    for (;;)
    {
      return;
      if ("S_TEXT/ASS".equals(paramTrack.codecId))
      {
        writeSubtitleSampleData(paramExtractorInput, SSA_PREFIX, paramInt);
      }
      else
      {
        TrackOutput localTrackOutput = paramTrack.output;
        int i;
        label190:
        byte[] arrayOfByte;
        int j;
        if (!this.sampleEncodingHandled)
        {
          if (!paramTrack.hasContentEncryption) {
            break label846;
          }
          this.blockFlags &= 0xBFFFFFFF;
          if (!this.sampleSignalByteRead)
          {
            paramExtractorInput.readFully(this.scratch.data, 0, 1);
            this.sampleBytesRead += 1;
            if ((this.scratch.data[0] & 0x80) == 128) {
              throw new ParserException("Extension bit is set in signal byte");
            }
            this.sampleSignalByte = ((byte)this.scratch.data[0]);
            this.sampleSignalByteRead = true;
          }
          label260:
          int k;
          label511:
          int m;
          if ((this.sampleSignalByte & 0x1) == 1)
          {
            i = 1;
            if (i == 0) {
              break label665;
            }
            if ((this.sampleSignalByte & 0x2) != 2) {
              break label566;
            }
            i = 1;
            this.blockFlags |= 0x40000000;
            if (!this.sampleInitializationVectorRead)
            {
              paramExtractorInput.readFully(this.encryptionInitializationVector.data, 0, 8);
              this.sampleBytesRead += 8;
              this.sampleInitializationVectorRead = true;
              arrayOfByte = this.scratch.data;
              if (i == 0) {
                break label572;
              }
              j = 128;
              arrayOfByte[0] = ((byte)(byte)(j | 0x8));
              this.scratch.setPosition(0);
              localTrackOutput.sampleData(this.scratch, 1);
              this.sampleBytesWritten += 1;
              this.encryptionInitializationVector.setPosition(0);
              localTrackOutput.sampleData(this.encryptionInitializationVector, 8);
              this.sampleBytesWritten += 8;
            }
            if (i == 0) {
              break label665;
            }
            if (!this.samplePartitionCountRead)
            {
              paramExtractorInput.readFully(this.scratch.data, 0, 1);
              this.sampleBytesRead += 1;
              this.scratch.setPosition(0);
              this.samplePartitionCount = this.scratch.readUnsignedByte();
              this.samplePartitionCountRead = true;
            }
            i = this.samplePartitionCount * 4;
            this.scratch.reset(i);
            paramExtractorInput.readFully(this.scratch.data, 0, i);
            this.sampleBytesRead += i;
            short s = (short)(this.samplePartitionCount / 2 + 1);
            k = s * 6 + 2;
            if ((this.encryptionSubsampleDataBuffer == null) || (this.encryptionSubsampleDataBuffer.capacity() < k)) {
              this.encryptionSubsampleDataBuffer = ByteBuffer.allocate(k);
            }
            this.encryptionSubsampleDataBuffer.position(0);
            this.encryptionSubsampleDataBuffer.putShort(s);
            i = 0;
            j = 0;
            m = i;
            if (j >= this.samplePartitionCount) {
              break label594;
            }
            i = this.scratch.readUnsignedIntToInt();
            if (j % 2 != 0) {
              break label578;
            }
            this.encryptionSubsampleDataBuffer.putShort((short)(i - m));
          }
          for (;;)
          {
            j++;
            break label511;
            i = 0;
            break;
            label566:
            i = 0;
            break label190;
            label572:
            j = 0;
            break label260;
            label578:
            this.encryptionSubsampleDataBuffer.putInt(i - m);
          }
          label594:
          i = paramInt - this.sampleBytesRead - m;
          if (this.samplePartitionCount % 2 == 1)
          {
            this.encryptionSubsampleDataBuffer.putInt(i);
            this.encryptionSubsampleData.reset(this.encryptionSubsampleDataBuffer.array(), k);
            localTrackOutput.sampleData(this.encryptionSubsampleData, k);
            this.sampleBytesWritten += k;
            label665:
            this.sampleEncodingHandled = true;
          }
        }
        else
        {
          paramInt += this.sampleStrippedBytes.limit();
          if ((!"V_MPEG4/ISO/AVC".equals(paramTrack.codecId)) && (!"V_MPEGH/ISO/HEVC".equals(paramTrack.codecId))) {
            break label895;
          }
          arrayOfByte = this.nalLength.data;
          arrayOfByte[0] = ((byte)0);
          arrayOfByte[1] = ((byte)0);
          arrayOfByte[2] = ((byte)0);
          j = paramTrack.nalUnitLengthFieldLength;
          i = paramTrack.nalUnitLengthFieldLength;
        }
        for (;;)
        {
          if (this.sampleBytesRead < paramInt)
          {
            if (this.sampleCurrentNalBytesRemaining == 0)
            {
              readToTarget(paramExtractorInput, arrayOfByte, 4 - i, j);
              this.nalLength.setPosition(0);
              this.sampleCurrentNalBytesRemaining = this.nalLength.readUnsignedIntToInt();
              this.nalStartCode.setPosition(0);
              localTrackOutput.sampleData(this.nalStartCode, 4);
              this.sampleBytesWritten += 4;
              continue;
              this.encryptionSubsampleDataBuffer.putShort((short)i);
              this.encryptionSubsampleDataBuffer.putInt(0);
              break;
              label846:
              if (paramTrack.sampleStrippedBytes == null) {
                break label665;
              }
              this.sampleStrippedBytes.reset(paramTrack.sampleStrippedBytes, paramTrack.sampleStrippedBytes.length);
              break label665;
            }
            this.sampleCurrentNalBytesRemaining -= readToOutput(paramExtractorInput, localTrackOutput, this.sampleCurrentNalBytesRemaining);
            continue;
            label895:
            if (paramTrack.trueHdSampleRechunker != null) {
              if (this.sampleStrippedBytes.limit() != 0) {
                break label958;
              }
            }
            label958:
            for (boolean bool = true;; bool = false)
            {
              Assertions.checkState(bool);
              paramTrack.trueHdSampleRechunker.startSample(paramExtractorInput, this.blockFlags, paramInt);
              while (this.sampleBytesRead < paramInt) {
                readToOutput(paramExtractorInput, localTrackOutput, paramInt - this.sampleBytesRead);
              }
            }
          }
        }
        if ("A_VORBIS".equals(paramTrack.codecId))
        {
          this.vorbisNumPageSamples.setPosition(0);
          localTrackOutput.sampleData(this.vorbisNumPageSamples, 4);
          this.sampleBytesWritten += 4;
        }
      }
    }
  }
  
  private void writeSubtitleSampleData(ExtractorInput paramExtractorInput, byte[] paramArrayOfByte, int paramInt)
    throws IOException, InterruptedException
  {
    int i = paramArrayOfByte.length + paramInt;
    if (this.subtitleSample.capacity() < i) {
      this.subtitleSample.data = Arrays.copyOf(paramArrayOfByte, i + paramInt);
    }
    for (;;)
    {
      paramExtractorInput.readFully(this.subtitleSample.data, paramArrayOfByte.length, paramInt);
      this.subtitleSample.reset(i);
      return;
      System.arraycopy(paramArrayOfByte, 0, this.subtitleSample.data, 0, paramArrayOfByte.length);
    }
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
    }
    for (;;)
    {
      return;
      this.currentTrack.codecPrivate = new byte[paramInt2];
      paramExtractorInput.readFully(this.currentTrack.codecPrivate, 0, paramInt2);
      continue;
      this.currentTrack.projectionData = new byte[paramInt2];
      paramExtractorInput.readFully(this.currentTrack.projectionData, 0, paramInt2);
      continue;
      this.currentTrack.sampleStrippedBytes = new byte[paramInt2];
      paramExtractorInput.readFully(this.currentTrack.sampleStrippedBytes, 0, paramInt2);
      continue;
      Object localObject1 = new byte[paramInt2];
      paramExtractorInput.readFully((byte[])localObject1, 0, paramInt2);
      this.currentTrack.cryptoData = new TrackOutput.CryptoData(1, (byte[])localObject1, 0, 0);
      continue;
      if (this.blockState == 0)
      {
        this.blockTrackNumber = ((int)this.varintReader.readUnsignedVarint(paramExtractorInput, false, true, 8));
        this.blockTrackNumberLength = this.varintReader.getLastLength();
        this.blockDurationUs = -9223372036854775807L;
        this.blockState = 1;
        this.scratch.reset();
      }
      localObject1 = (Track)this.tracks.get(this.blockTrackNumber);
      if (localObject1 == null)
      {
        paramExtractorInput.skipFully(paramInt2 - this.blockTrackNumberLength);
        this.blockState = 0;
      }
      else
      {
        int i;
        if (this.blockState == 1)
        {
          readScratch(paramExtractorInput, 3);
          i = (this.scratch.data[2] & 0x6) >> 1;
          if (i != 0) {
            break label637;
          }
          this.blockLacingSampleCount = 1;
          this.blockLacingSampleSizes = ensureArrayCapacity(this.blockLacingSampleSizes, 1);
          this.blockLacingSampleSizes[0] = (paramInt2 - this.blockTrackNumberLength - 3);
          i = this.scratch.data[0];
          paramInt2 = this.scratch.data[1];
          this.blockTimeUs = (this.clusterTimecodeUs + scaleTimecodeToUs(i << 8 | paramInt2 & 0xFF));
          if ((this.scratch.data[2] & 0x8) != 8) {
            break label1266;
          }
          paramInt2 = 1;
          label492:
          if ((((Track)localObject1).type != 2) && ((paramInt1 != 163) || ((this.scratch.data[2] & 0x80) != 128))) {
            break label1271;
          }
          i = 1;
          label530:
          if (i == 0) {
            break label1277;
          }
          i = 1;
          label538:
          if (paramInt2 == 0) {
            break label1283;
          }
        }
        label637:
        label1266:
        label1271:
        label1277:
        label1283:
        for (paramInt2 = Integer.MIN_VALUE;; paramInt2 = 0)
        {
          this.blockFlags = (paramInt2 | i);
          this.blockState = 2;
          this.blockLacingSampleIndex = 0;
          if (paramInt1 != 163) {
            break label1296;
          }
          while (this.blockLacingSampleIndex < this.blockLacingSampleCount)
          {
            writeSampleData(paramExtractorInput, (Track)localObject1, this.blockLacingSampleSizes[this.blockLacingSampleIndex]);
            commitSampleToOutput((Track)localObject1, this.blockTimeUs + this.blockLacingSampleIndex * ((Track)localObject1).defaultSampleDurationNs / 1000);
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
          int j;
          int k;
          int m;
          int n;
          Object localObject2;
          if (i == 1)
          {
            j = 0;
            i = 4;
            for (k = 0; k < this.blockLacingSampleCount - 1; k++)
            {
              this.blockLacingSampleSizes[k] = 0;
              m = i;
              do
              {
                i = m + 1;
                readScratch(paramExtractorInput, i);
                n = this.scratch.data[(i - 1)] & 0xFF;
                localObject2 = this.blockLacingSampleSizes;
                localObject2[k] += n;
                m = i;
              } while (n == 255);
              j += this.blockLacingSampleSizes[k];
            }
            this.blockLacingSampleSizes[(this.blockLacingSampleCount - 1)] = (paramInt2 - this.blockTrackNumberLength - i - j);
            break;
          }
          if (i == 3)
          {
            j = 0;
            i = 4;
            k = 0;
            if (k < this.blockLacingSampleCount - 1)
            {
              this.blockLacingSampleSizes[k] = 0;
              n = i + 1;
              readScratch(paramExtractorInput, n);
              if (this.scratch.data[(n - 1)] == 0) {
                throw new ParserException("No valid varint length mask found");
              }
              long l1 = 0L;
              long l2;
              for (m = 0;; m++)
              {
                i = n;
                l2 = l1;
                if (m < 8)
                {
                  int i1 = 1 << 7 - m;
                  if ((this.scratch.data[(n - 1)] & i1) == 0) {
                    continue;
                  }
                  int i2 = n - 1;
                  n += m;
                  readScratch(paramExtractorInput, n);
                  localObject2 = this.scratch.data;
                  i = i2 + 1;
                  l1 = localObject2[i2] & 0xFF & (i1 ^ 0xFFFFFFFF);
                  while (i < n)
                  {
                    l1 = l1 << 8 | this.scratch.data[i] & 0xFF;
                    i++;
                  }
                  i = n;
                  l2 = l1;
                  if (k > 0)
                  {
                    l2 = l1 - ((1L << m * 7 + 6) - 1L);
                    i = n;
                  }
                }
                if ((l2 >= -2147483648L) && (l2 <= 2147483647L)) {
                  break;
                }
                throw new ParserException("EBML lacing sample size out of range.");
              }
              m = (int)l2;
              localObject2 = this.blockLacingSampleSizes;
              if (k == 0) {}
              for (;;)
              {
                localObject2[k] = m;
                j += this.blockLacingSampleSizes[k];
                k++;
                break;
                m += this.blockLacingSampleSizes[(k - 1)];
              }
            }
            this.blockLacingSampleSizes[(this.blockLacingSampleCount - 1)] = (paramInt2 - this.blockTrackNumberLength - i - j);
            break;
          }
          throw new ParserException("Unexpected lacing value: " + i);
          paramInt2 = 0;
          break label492;
          i = 0;
          break label530;
          i = 0;
          break label538;
        }
        this.blockState = 0;
        continue;
        label1296:
        writeSampleData(paramExtractorInput, (Track)localObject1, this.blockLacingSampleSizes[0]);
      }
    }
  }
  
  void endMasterElement(int paramInt)
    throws ParserException
  {
    switch (paramInt)
    {
    }
    for (;;)
    {
      return;
      if (this.timecodeScale == -9223372036854775807L) {
        this.timecodeScale = 1000000L;
      }
      if (this.durationTimecode != -9223372036854775807L)
      {
        this.durationUs = scaleTimecodeToUs(this.durationTimecode);
        continue;
        if ((this.seekEntryId == -1) || (this.seekEntryPosition == -1L)) {
          throw new ParserException("Mandatory element SeekID or SeekPosition not found");
        }
        if (this.seekEntryId == 475249515)
        {
          this.cuesContentPosition = this.seekEntryPosition;
          continue;
          if (!this.sentSeekMap)
          {
            this.extractorOutput.seekMap(buildSeekMap());
            this.sentSeekMap = true;
            continue;
            if (this.blockState == 2)
            {
              if (!this.sampleSeenReferenceBlock) {
                this.blockFlags |= 0x1;
              }
              commitSampleToOutput((Track)this.tracks.get(this.blockTrackNumber), this.blockTimeUs);
              this.blockState = 0;
              continue;
              if (this.currentTrack.hasContentEncryption)
              {
                if (this.currentTrack.cryptoData == null) {
                  throw new ParserException("Encrypted Track found but ContentEncKeyID was not found");
                }
                this.currentTrack.drmInitData = new DrmInitData(new DrmInitData.SchemeData[] { new DrmInitData.SchemeData(C.UUID_NIL, "video/webm", this.currentTrack.cryptoData.encryptionKey) });
                continue;
                if ((this.currentTrack.hasContentEncryption) && (this.currentTrack.sampleStrippedBytes != null))
                {
                  throw new ParserException("Combining encryption and compression is not supported");
                  if (isCodecSupported(this.currentTrack.codecId))
                  {
                    this.currentTrack.initializeOutput(this.extractorOutput, this.currentTrack.number);
                    this.tracks.put(this.currentTrack.number, this.currentTrack);
                  }
                  this.currentTrack = null;
                  continue;
                  if (this.tracks.size() == 0) {
                    throw new ParserException("No valid tracks were found");
                  }
                  this.extractorOutput.endTracks();
                }
              }
            }
          }
        }
      }
    }
  }
  
  void floatElement(int paramInt, double paramDouble)
  {
    switch (paramInt)
    {
    }
    for (;;)
    {
      return;
      this.durationTimecode = (paramDouble);
      continue;
      this.currentTrack.sampleRate = ((int)paramDouble);
      continue;
      this.currentTrack.primaryRChromaticityX = ((float)paramDouble);
      continue;
      this.currentTrack.primaryRChromaticityY = ((float)paramDouble);
      continue;
      this.currentTrack.primaryGChromaticityX = ((float)paramDouble);
      continue;
      this.currentTrack.primaryGChromaticityY = ((float)paramDouble);
      continue;
      this.currentTrack.primaryBChromaticityX = ((float)paramDouble);
      continue;
      this.currentTrack.primaryBChromaticityY = ((float)paramDouble);
      continue;
      this.currentTrack.whitePointChromaticityX = ((float)paramDouble);
      continue;
      this.currentTrack.whitePointChromaticityY = ((float)paramDouble);
      continue;
      this.currentTrack.maxMasteringLuminance = ((float)paramDouble);
      continue;
      this.currentTrack.minMasteringLuminance = ((float)paramDouble);
    }
  }
  
  int getElementType(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      paramInt = 0;
    }
    for (;;)
    {
      return paramInt;
      paramInt = 1;
      continue;
      paramInt = 2;
      continue;
      paramInt = 3;
      continue;
      paramInt = 4;
      continue;
      paramInt = 5;
    }
  }
  
  public void init(ExtractorOutput paramExtractorOutput)
  {
    this.extractorOutput = paramExtractorOutput;
  }
  
  void integerElement(int paramInt, long paramLong)
    throws ParserException
  {
    boolean bool1 = true;
    boolean bool2 = true;
    switch (paramInt)
    {
    }
    for (;;)
    {
      return;
      if (paramLong != 1L)
      {
        throw new ParserException("EBMLReadVersion " + paramLong + " not supported");
        if ((paramLong < 1L) || (paramLong > 2L))
        {
          throw new ParserException("DocTypeReadVersion " + paramLong + " not supported");
          this.seekEntryPosition = (this.segmentContentPosition + paramLong);
          continue;
          this.timecodeScale = paramLong;
          continue;
          this.currentTrack.width = ((int)paramLong);
          continue;
          this.currentTrack.height = ((int)paramLong);
          continue;
          this.currentTrack.displayWidth = ((int)paramLong);
          continue;
          this.currentTrack.displayHeight = ((int)paramLong);
          continue;
          this.currentTrack.displayUnit = ((int)paramLong);
          continue;
          this.currentTrack.number = ((int)paramLong);
          continue;
          Track localTrack = this.currentTrack;
          if (paramLong == 1L) {}
          for (;;)
          {
            localTrack.flagForced = bool2;
            break;
            bool2 = false;
          }
          localTrack = this.currentTrack;
          if (paramLong == 1L) {}
          for (bool2 = bool1;; bool2 = false)
          {
            localTrack.flagDefault = bool2;
            break;
          }
          this.currentTrack.type = ((int)paramLong);
          continue;
          this.currentTrack.defaultSampleDurationNs = ((int)paramLong);
          continue;
          this.currentTrack.codecDelayNs = paramLong;
          continue;
          this.currentTrack.seekPreRollNs = paramLong;
          continue;
          this.currentTrack.channelCount = ((int)paramLong);
          continue;
          this.currentTrack.audioBitDepth = ((int)paramLong);
          continue;
          this.sampleSeenReferenceBlock = true;
          continue;
          if (paramLong != 0L)
          {
            throw new ParserException("ContentEncodingOrder " + paramLong + " not supported");
            if (paramLong != 1L)
            {
              throw new ParserException("ContentEncodingScope " + paramLong + " not supported");
              if (paramLong != 3L)
              {
                throw new ParserException("ContentCompAlgo " + paramLong + " not supported");
                if (paramLong != 5L)
                {
                  throw new ParserException("ContentEncAlgo " + paramLong + " not supported");
                  if (paramLong != 1L)
                  {
                    throw new ParserException("AESSettingsCipherMode " + paramLong + " not supported");
                    this.cueTimesUs.add(scaleTimecodeToUs(paramLong));
                    continue;
                    if (!this.seenClusterPositionForCurrentCuePoint)
                    {
                      this.cueClusterPositions.add(paramLong);
                      this.seenClusterPositionForCurrentCuePoint = true;
                      continue;
                      this.clusterTimecodeUs = scaleTimecodeToUs(paramLong);
                      continue;
                      this.blockDurationUs = scaleTimecodeToUs(paramLong);
                      continue;
                      switch ((int)paramLong)
                      {
                      default: 
                        break;
                      case 0: 
                        this.currentTrack.stereoMode = 0;
                        break;
                      case 1: 
                        this.currentTrack.stereoMode = 2;
                        break;
                      case 3: 
                        this.currentTrack.stereoMode = 1;
                        break;
                      case 15: 
                        this.currentTrack.stereoMode = 3;
                        continue;
                        this.currentTrack.hasColorInfo = true;
                        switch ((int)paramLong)
                        {
                        case 2: 
                        case 3: 
                        case 8: 
                        default: 
                          break;
                        case 1: 
                          this.currentTrack.colorSpace = 1;
                          break;
                        case 4: 
                        case 5: 
                        case 6: 
                        case 7: 
                          this.currentTrack.colorSpace = 2;
                          break;
                        case 9: 
                          this.currentTrack.colorSpace = 6;
                          continue;
                          switch ((int)paramLong)
                          {
                          default: 
                            break;
                          case 1: 
                          case 6: 
                          case 7: 
                            this.currentTrack.colorTransfer = 3;
                            break;
                          case 16: 
                            this.currentTrack.colorTransfer = 6;
                            break;
                          case 18: 
                            this.currentTrack.colorTransfer = 7;
                            continue;
                            switch ((int)paramLong)
                            {
                            default: 
                              break;
                            case 1: 
                              this.currentTrack.colorRange = 2;
                              break;
                            case 2: 
                              this.currentTrack.colorRange = 1;
                              continue;
                              this.currentTrack.maxContentLuminance = ((int)paramLong);
                              continue;
                              this.currentTrack.maxFrameAverageLuminance = ((int)paramLong);
                            }
                            break;
                          }
                          break;
                        }
                        break;
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
  
  boolean isLevel1Element(int paramInt)
  {
    if ((paramInt == 357149030) || (paramInt == 524531317) || (paramInt == 475249515) || (paramInt == 374648427)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public int read(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
    throws IOException, InterruptedException
  {
    int i = 0;
    this.sampleRead = false;
    int j = 1;
    while ((j != 0) && (!this.sampleRead))
    {
      boolean bool = this.reader.read(paramExtractorInput);
      j = bool;
      if (bool)
      {
        j = bool;
        if (maybeSeekForCues(paramPositionHolder, paramExtractorInput.getPosition())) {
          i = 1;
        }
      }
    }
    for (;;)
    {
      return i;
      if (j == 0)
      {
        for (i = 0; i < this.tracks.size(); i++) {
          ((Track)this.tracks.valueAt(i)).outputPendingSampleMetadata();
        }
        i = -1;
      }
    }
  }
  
  public void release() {}
  
  public void seek(long paramLong1, long paramLong2)
  {
    this.clusterTimecodeUs = -9223372036854775807L;
    this.blockState = 0;
    this.reader.reset();
    this.varintReader.reset();
    resetSample();
    for (int i = 0; i < this.tracks.size(); i++) {
      ((Track)this.tracks.valueAt(i)).reset();
    }
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
    }
    for (;;)
    {
      return;
      if ((this.segmentContentPosition != -1L) && (this.segmentContentPosition != paramLong1)) {
        throw new ParserException("Multiple Segment elements not supported");
      }
      this.segmentContentPosition = paramLong1;
      this.segmentContentSize = paramLong2;
      continue;
      this.seekEntryId = -1;
      this.seekEntryPosition = -1L;
      continue;
      this.cueTimesUs = new LongArray();
      this.cueClusterPositions = new LongArray();
      continue;
      this.seenClusterPositionForCurrentCuePoint = false;
      continue;
      if (!this.sentSeekMap) {
        if ((this.seekForCuesEnabled) && (this.cuesContentPosition != -1L))
        {
          this.seekForCues = true;
        }
        else
        {
          this.extractorOutput.seekMap(new SeekMap.Unseekable(this.durationUs));
          this.sentSeekMap = true;
          continue;
          this.sampleSeenReferenceBlock = false;
          continue;
          this.currentTrack.hasContentEncryption = true;
          continue;
          this.currentTrack = new Track(null);
          continue;
          this.currentTrack.hasColorInfo = true;
        }
      }
    }
  }
  
  void stringElement(int paramInt, String paramString)
    throws ParserException
  {
    switch (paramInt)
    {
    }
    for (;;)
    {
      return;
      if ((!"webm".equals(paramString)) && (!"matroska".equals(paramString)))
      {
        throw new ParserException("DocType " + paramString + " not supported");
        this.currentTrack.codecId = paramString;
        continue;
        Track.access$202(this.currentTrack, paramString);
      }
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface Flags {}
  
  private final class InnerEbmlReaderOutput
    implements EbmlReaderOutput
  {
    private InnerEbmlReaderOutput() {}
    
    public void binaryElement(int paramInt1, int paramInt2, ExtractorInput paramExtractorInput)
      throws IOException, InterruptedException
    {
      MatroskaExtractor.this.binaryElement(paramInt1, paramInt2, paramExtractorInput);
    }
    
    public void endMasterElement(int paramInt)
      throws ParserException
    {
      MatroskaExtractor.this.endMasterElement(paramInt);
    }
    
    public void floatElement(int paramInt, double paramDouble)
      throws ParserException
    {
      MatroskaExtractor.this.floatElement(paramInt, paramDouble);
    }
    
    public int getElementType(int paramInt)
    {
      return MatroskaExtractor.this.getElementType(paramInt);
    }
    
    public void integerElement(int paramInt, long paramLong)
      throws ParserException
    {
      MatroskaExtractor.this.integerElement(paramInt, paramLong);
    }
    
    public boolean isLevel1Element(int paramInt)
    {
      return MatroskaExtractor.this.isLevel1Element(paramInt);
    }
    
    public void startMasterElement(int paramInt, long paramLong1, long paramLong2)
      throws ParserException
    {
      MatroskaExtractor.this.startMasterElement(paramInt, paramLong1, paramLong2);
    }
    
    public void stringElement(int paramInt, String paramString)
      throws ParserException
    {
      MatroskaExtractor.this.stringElement(paramInt, paramString);
    }
  }
  
  private static final class Track
  {
    private static final int DEFAULT_MAX_CLL = 1000;
    private static final int DEFAULT_MAX_FALL = 200;
    private static final int DISPLAY_UNIT_PIXELS = 0;
    private static final int MAX_CHROMATICITY = 50000;
    public int audioBitDepth = -1;
    public int channelCount = 1;
    public long codecDelayNs = 0L;
    public String codecId;
    public byte[] codecPrivate;
    public int colorRange = -1;
    public int colorSpace = -1;
    public int colorTransfer = -1;
    public TrackOutput.CryptoData cryptoData;
    public int defaultSampleDurationNs;
    public int displayHeight = -1;
    public int displayUnit = 0;
    public int displayWidth = -1;
    public DrmInitData drmInitData;
    public boolean flagDefault = true;
    public boolean flagForced;
    public boolean hasColorInfo = false;
    public boolean hasContentEncryption;
    public int height = -1;
    private String language = "eng";
    public int maxContentLuminance = 1000;
    public int maxFrameAverageLuminance = 200;
    public float maxMasteringLuminance = -1.0F;
    public float minMasteringLuminance = -1.0F;
    public int nalUnitLengthFieldLength;
    public int number;
    public TrackOutput output;
    public float primaryBChromaticityX = -1.0F;
    public float primaryBChromaticityY = -1.0F;
    public float primaryGChromaticityX = -1.0F;
    public float primaryGChromaticityY = -1.0F;
    public float primaryRChromaticityX = -1.0F;
    public float primaryRChromaticityY = -1.0F;
    public byte[] projectionData = null;
    public int sampleRate = 8000;
    public byte[] sampleStrippedBytes;
    public long seekPreRollNs = 0L;
    public int stereoMode = -1;
    public MatroskaExtractor.TrueHdSampleRechunker trueHdSampleRechunker;
    public int type;
    public float whitePointChromaticityX = -1.0F;
    public float whitePointChromaticityY = -1.0F;
    public int width = -1;
    
    private byte[] getHdrStaticInfo()
    {
      byte[] arrayOfByte;
      if ((this.primaryRChromaticityX == -1.0F) || (this.primaryRChromaticityY == -1.0F) || (this.primaryGChromaticityX == -1.0F) || (this.primaryGChromaticityY == -1.0F) || (this.primaryBChromaticityX == -1.0F) || (this.primaryBChromaticityY == -1.0F) || (this.whitePointChromaticityX == -1.0F) || (this.whitePointChromaticityY == -1.0F) || (this.maxMasteringLuminance == -1.0F) || (this.minMasteringLuminance == -1.0F)) {
        arrayOfByte = null;
      }
      for (;;)
      {
        return arrayOfByte;
        arrayOfByte = new byte[25];
        ByteBuffer localByteBuffer = ByteBuffer.wrap(arrayOfByte);
        localByteBuffer.put((byte)0);
        localByteBuffer.putShort((short)(int)(this.primaryRChromaticityX * 50000.0F + 0.5F));
        localByteBuffer.putShort((short)(int)(this.primaryRChromaticityY * 50000.0F + 0.5F));
        localByteBuffer.putShort((short)(int)(this.primaryGChromaticityX * 50000.0F + 0.5F));
        localByteBuffer.putShort((short)(int)(this.primaryGChromaticityY * 50000.0F + 0.5F));
        localByteBuffer.putShort((short)(int)(this.primaryBChromaticityX * 50000.0F + 0.5F));
        localByteBuffer.putShort((short)(int)(this.primaryBChromaticityY * 50000.0F + 0.5F));
        localByteBuffer.putShort((short)(int)(this.whitePointChromaticityX * 50000.0F + 0.5F));
        localByteBuffer.putShort((short)(int)(this.whitePointChromaticityY * 50000.0F + 0.5F));
        localByteBuffer.putShort((short)(int)(this.maxMasteringLuminance + 0.5F));
        localByteBuffer.putShort((short)(int)(this.minMasteringLuminance + 0.5F));
        localByteBuffer.putShort((short)this.maxContentLuminance);
        localByteBuffer.putShort((short)this.maxFrameAverageLuminance);
      }
    }
    
    private static List<byte[]> parseFourCcVc1Private(ParsableByteArray paramParsableByteArray)
      throws ParserException
    {
      try
      {
        paramParsableByteArray.skipBytes(16);
        if (paramParsableByteArray.readLittleEndianUnsignedInt() != 826496599L)
        {
          paramParsableByteArray = null;
          return paramParsableByteArray;
        }
        int i = paramParsableByteArray.getPosition();
        paramParsableByteArray = paramParsableByteArray.data;
        i += 20;
        for (;;)
        {
          if (i >= paramParsableByteArray.length - 4) {
            break label95;
          }
          if ((paramParsableByteArray[i] == 0) && (paramParsableByteArray[(i + 1)] == 0) && (paramParsableByteArray[(i + 2)] == 1) && (paramParsableByteArray[(i + 3)] == 15))
          {
            paramParsableByteArray = Collections.singletonList(Arrays.copyOfRange(paramParsableByteArray, i, paramParsableByteArray.length));
            break;
          }
          i++;
        }
        label95:
        paramParsableByteArray = new org/telegram/messenger/exoplayer2/ParserException;
        paramParsableByteArray.<init>("Failed to find FourCC VC1 initialization data");
        throw paramParsableByteArray;
      }
      catch (ArrayIndexOutOfBoundsException paramParsableByteArray)
      {
        throw new ParserException("Error parsing FourCC VC1 codec private");
      }
    }
    
    /* Error */
    private static boolean parseMsAcmCodecPrivate(ParsableByteArray paramParsableByteArray)
      throws ParserException
    {
      // Byte code:
      //   0: iconst_1
      //   1: istore_1
      //   2: aload_0
      //   3: invokevirtual 210	org/telegram/messenger/exoplayer2/util/ParsableByteArray:readLittleEndianUnsignedShort	()I
      //   6: istore_2
      //   7: iload_2
      //   8: iconst_1
      //   9: if_icmpne +5 -> 14
      //   12: iload_1
      //   13: ireturn
      //   14: iload_2
      //   15: ldc -45
      //   17: if_icmpne +48 -> 65
      //   20: aload_0
      //   21: bipush 24
      //   23: invokevirtual 214	org/telegram/messenger/exoplayer2/util/ParsableByteArray:setPosition	(I)V
      //   26: aload_0
      //   27: invokevirtual 217	org/telegram/messenger/exoplayer2/util/ParsableByteArray:readLong	()J
      //   30: invokestatic 221	org/telegram/messenger/exoplayer2/extractor/mkv/MatroskaExtractor:access$400	()Ljava/util/UUID;
      //   33: invokevirtual 226	java/util/UUID:getMostSignificantBits	()J
      //   36: lcmp
      //   37: ifne +23 -> 60
      //   40: aload_0
      //   41: invokevirtual 217	org/telegram/messenger/exoplayer2/util/ParsableByteArray:readLong	()J
      //   44: lstore_3
      //   45: invokestatic 221	org/telegram/messenger/exoplayer2/extractor/mkv/MatroskaExtractor:access$400	()Ljava/util/UUID;
      //   48: invokevirtual 229	java/util/UUID:getLeastSignificantBits	()J
      //   51: lstore 5
      //   53: lload_3
      //   54: lload 5
      //   56: lcmp
      //   57: ifeq -45 -> 12
      //   60: iconst_0
      //   61: istore_1
      //   62: goto -50 -> 12
      //   65: iconst_0
      //   66: istore_1
      //   67: goto -55 -> 12
      //   70: astore_0
      //   71: new 162	org/telegram/messenger/exoplayer2/ParserException
      //   74: dup
      //   75: ldc -25
      //   77: invokespecial 200	org/telegram/messenger/exoplayer2/ParserException:<init>	(Ljava/lang/String;)V
      //   80: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	81	0	paramParsableByteArray	ParsableByteArray
      //   1	66	1	bool	boolean
      //   6	12	2	i	int
      //   44	10	3	l1	long
      //   51	4	5	l2	long
      // Exception table:
      //   from	to	target	type
      //   2	7	70	java/lang/ArrayIndexOutOfBoundsException
      //   20	53	70	java/lang/ArrayIndexOutOfBoundsException
    }
    
    private static List<byte[]> parseVorbisCodecPrivate(byte[] paramArrayOfByte)
      throws ParserException
    {
      if (paramArrayOfByte[0] != 2) {
        try
        {
          paramArrayOfByte = new org/telegram/messenger/exoplayer2/ParserException;
          paramArrayOfByte.<init>("Error parsing vorbis codec private");
          throw paramArrayOfByte;
        }
        catch (ArrayIndexOutOfBoundsException paramArrayOfByte)
        {
          throw new ParserException("Error parsing vorbis codec private");
        }
      }
      int i = 0;
      for (int j = 1; paramArrayOfByte[j] == -1; j++) {
        i += 255;
      }
      int k = i + paramArrayOfByte[j];
      i = 0;
      int m = j + 1;
      j = i;
      for (i = m; paramArrayOfByte[i] == -1; i++) {
        j += 255;
      }
      m = i + 1;
      i = paramArrayOfByte[i];
      if (paramArrayOfByte[m] != 1)
      {
        paramArrayOfByte = new org/telegram/messenger/exoplayer2/ParserException;
        paramArrayOfByte.<init>("Error parsing vorbis codec private");
        throw paramArrayOfByte;
      }
      byte[] arrayOfByte1 = new byte[k];
      System.arraycopy(paramArrayOfByte, m, arrayOfByte1, 0, k);
      m += k;
      if (paramArrayOfByte[m] != 3)
      {
        paramArrayOfByte = new org/telegram/messenger/exoplayer2/ParserException;
        paramArrayOfByte.<init>("Error parsing vorbis codec private");
        throw paramArrayOfByte;
      }
      j = m + (j + i);
      if (paramArrayOfByte[j] != 5)
      {
        paramArrayOfByte = new org/telegram/messenger/exoplayer2/ParserException;
        paramArrayOfByte.<init>("Error parsing vorbis codec private");
        throw paramArrayOfByte;
      }
      byte[] arrayOfByte2 = new byte[paramArrayOfByte.length - j];
      System.arraycopy(paramArrayOfByte, j, arrayOfByte2, 0, paramArrayOfByte.length - j);
      paramArrayOfByte = new java/util/ArrayList;
      paramArrayOfByte.<init>(2);
      paramArrayOfByte.add(arrayOfByte1);
      paramArrayOfByte.add(arrayOfByte2);
      return paramArrayOfByte;
    }
    
    public void initializeOutput(ExtractorOutput paramExtractorOutput, int paramInt)
      throws ParserException
    {
      int i = -1;
      int j = -1;
      Object localObject1 = null;
      Object localObject2 = this.codecId;
      int k = -1;
      switch (((String)localObject2).hashCode())
      {
      }
      for (;;)
      {
        switch (k)
        {
        default: 
          throw new ParserException("Unrecognized codec identifier.");
          if (((String)localObject2).equals("V_VP8"))
          {
            k = 0;
            continue;
            if (((String)localObject2).equals("V_VP9"))
            {
              k = 1;
              continue;
              if (((String)localObject2).equals("V_MPEG2"))
              {
                k = 2;
                continue;
                if (((String)localObject2).equals("V_MPEG4/ISO/SP"))
                {
                  k = 3;
                  continue;
                  if (((String)localObject2).equals("V_MPEG4/ISO/ASP"))
                  {
                    k = 4;
                    continue;
                    if (((String)localObject2).equals("V_MPEG4/ISO/AP"))
                    {
                      k = 5;
                      continue;
                      if (((String)localObject2).equals("V_MPEG4/ISO/AVC"))
                      {
                        k = 6;
                        continue;
                        if (((String)localObject2).equals("V_MPEGH/ISO/HEVC"))
                        {
                          k = 7;
                          continue;
                          if (((String)localObject2).equals("V_MS/VFW/FOURCC"))
                          {
                            k = 8;
                            continue;
                            if (((String)localObject2).equals("V_THEORA"))
                            {
                              k = 9;
                              continue;
                              if (((String)localObject2).equals("A_VORBIS"))
                              {
                                k = 10;
                                continue;
                                if (((String)localObject2).equals("A_OPUS"))
                                {
                                  k = 11;
                                  continue;
                                  if (((String)localObject2).equals("A_AAC"))
                                  {
                                    k = 12;
                                    continue;
                                    if (((String)localObject2).equals("A_MPEG/L2"))
                                    {
                                      k = 13;
                                      continue;
                                      if (((String)localObject2).equals("A_MPEG/L3"))
                                      {
                                        k = 14;
                                        continue;
                                        if (((String)localObject2).equals("A_AC3"))
                                        {
                                          k = 15;
                                          continue;
                                          if (((String)localObject2).equals("A_EAC3"))
                                          {
                                            k = 16;
                                            continue;
                                            if (((String)localObject2).equals("A_TRUEHD"))
                                            {
                                              k = 17;
                                              continue;
                                              if (((String)localObject2).equals("A_DTS"))
                                              {
                                                k = 18;
                                                continue;
                                                if (((String)localObject2).equals("A_DTS/EXPRESS"))
                                                {
                                                  k = 19;
                                                  continue;
                                                  if (((String)localObject2).equals("A_DTS/LOSSLESS"))
                                                  {
                                                    k = 20;
                                                    continue;
                                                    if (((String)localObject2).equals("A_FLAC"))
                                                    {
                                                      k = 21;
                                                      continue;
                                                      if (((String)localObject2).equals("A_MS/ACM"))
                                                      {
                                                        k = 22;
                                                        continue;
                                                        if (((String)localObject2).equals("A_PCM/INT/LIT"))
                                                        {
                                                          k = 23;
                                                          continue;
                                                          if (((String)localObject2).equals("S_TEXT/UTF8"))
                                                          {
                                                            k = 24;
                                                            continue;
                                                            if (((String)localObject2).equals("S_TEXT/ASS"))
                                                            {
                                                              k = 25;
                                                              continue;
                                                              if (((String)localObject2).equals("S_VOBSUB"))
                                                              {
                                                                k = 26;
                                                                continue;
                                                                if (((String)localObject2).equals("S_HDMV/PGS"))
                                                                {
                                                                  k = 27;
                                                                  continue;
                                                                  if (((String)localObject2).equals("S_DVBSUB")) {
                                                                    k = 28;
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
                }
              }
            }
          }
          break;
        }
      }
      localObject2 = "video/x-vnd.on2.vp8";
      Object localObject3 = localObject1;
      k = i;
      label944:
      int m;
      if (this.flagDefault)
      {
        i = 1;
        if (!this.flagForced) {
          break label1896;
        }
        m = 2;
        label954:
        m = 0x0 | i | m;
        if (!MimeTypes.isAudio((String)localObject2)) {
          break label1902;
        }
        i = 1;
        localObject2 = Format.createAudioSampleFormat(Integer.toString(paramInt), (String)localObject2, null, -1, k, this.channelCount, this.sampleRate, j, (List)localObject3, this.drmInitData, m, this.language);
        paramInt = i;
      }
      for (;;)
      {
        this.output = paramExtractorOutput.track(this.number, paramInt);
        this.output.format((Format)localObject2);
        return;
        localObject2 = "video/x-vnd.on2.vp9";
        k = i;
        localObject3 = localObject1;
        break;
        localObject2 = "video/mpeg2";
        k = i;
        localObject3 = localObject1;
        break;
        localObject2 = "video/mp4v-es";
        if (this.codecPrivate == null) {}
        for (localObject3 = null;; localObject3 = Collections.singletonList(this.codecPrivate))
        {
          k = i;
          break;
        }
        localObject2 = "video/avc";
        localObject1 = AvcConfig.parse(new ParsableByteArray(this.codecPrivate));
        localObject3 = ((AvcConfig)localObject1).initializationData;
        this.nalUnitLengthFieldLength = ((AvcConfig)localObject1).nalUnitLengthFieldLength;
        k = i;
        break;
        localObject2 = "video/hevc";
        localObject1 = HevcConfig.parse(new ParsableByteArray(this.codecPrivate));
        localObject3 = ((HevcConfig)localObject1).initializationData;
        this.nalUnitLengthFieldLength = ((HevcConfig)localObject1).nalUnitLengthFieldLength;
        k = i;
        break;
        localObject3 = parseFourCcVc1Private(new ParsableByteArray(this.codecPrivate));
        if (localObject3 != null)
        {
          localObject2 = "video/wvc1";
          k = i;
          break;
        }
        Log.w("MatroskaExtractor", "Unsupported FourCC. Setting mimeType to video/x-unknown");
        localObject2 = "video/x-unknown";
        k = i;
        break;
        localObject2 = "video/x-unknown";
        k = i;
        localObject3 = localObject1;
        break;
        localObject2 = "audio/vorbis";
        k = 8192;
        localObject3 = parseVorbisCodecPrivate(this.codecPrivate);
        break;
        localObject2 = "audio/opus";
        k = 5760;
        localObject3 = new ArrayList(3);
        ((List)localObject3).add(this.codecPrivate);
        ((List)localObject3).add(ByteBuffer.allocate(8).order(ByteOrder.nativeOrder()).putLong(this.codecDelayNs).array());
        ((List)localObject3).add(ByteBuffer.allocate(8).order(ByteOrder.nativeOrder()).putLong(this.seekPreRollNs).array());
        break;
        localObject2 = "audio/mp4a-latm";
        localObject3 = Collections.singletonList(this.codecPrivate);
        k = i;
        break;
        localObject2 = "audio/mpeg-L2";
        k = 4096;
        localObject3 = localObject1;
        break;
        localObject2 = "audio/mpeg";
        k = 4096;
        localObject3 = localObject1;
        break;
        localObject2 = "audio/ac3";
        k = i;
        localObject3 = localObject1;
        break;
        localObject2 = "audio/eac3";
        k = i;
        localObject3 = localObject1;
        break;
        localObject2 = "audio/true-hd";
        this.trueHdSampleRechunker = new MatroskaExtractor.TrueHdSampleRechunker();
        k = i;
        localObject3 = localObject1;
        break;
        localObject2 = "audio/vnd.dts";
        k = i;
        localObject3 = localObject1;
        break;
        localObject2 = "audio/vnd.dts.hd";
        k = i;
        localObject3 = localObject1;
        break;
        localObject2 = "audio/flac";
        localObject3 = Collections.singletonList(this.codecPrivate);
        k = i;
        break;
        localObject2 = "audio/raw";
        if (parseMsAcmCodecPrivate(new ParsableByteArray(this.codecPrivate)))
        {
          m = Util.getPcmEncoding(this.audioBitDepth);
          k = i;
          j = m;
          localObject3 = localObject1;
          if (m != 0) {
            break;
          }
          j = -1;
          localObject2 = "audio/x-unknown";
          Log.w("MatroskaExtractor", "Unsupported PCM bit depth: " + this.audioBitDepth + ". Setting mimeType to " + "audio/x-unknown");
          k = i;
          localObject3 = localObject1;
          break;
        }
        localObject2 = "audio/x-unknown";
        Log.w("MatroskaExtractor", "Non-PCM MS/ACM is unsupported. Setting mimeType to " + "audio/x-unknown");
        k = i;
        localObject3 = localObject1;
        break;
        localObject2 = "audio/raw";
        m = Util.getPcmEncoding(this.audioBitDepth);
        k = i;
        j = m;
        localObject3 = localObject1;
        if (m != 0) {
          break;
        }
        j = -1;
        localObject2 = "audio/x-unknown";
        Log.w("MatroskaExtractor", "Unsupported PCM bit depth: " + this.audioBitDepth + ". Setting mimeType to " + "audio/x-unknown");
        k = i;
        localObject3 = localObject1;
        break;
        localObject2 = "application/x-subrip";
        k = i;
        localObject3 = localObject1;
        break;
        localObject2 = "text/x-ssa";
        k = i;
        localObject3 = localObject1;
        break;
        localObject2 = "application/vobsub";
        localObject3 = Collections.singletonList(this.codecPrivate);
        k = i;
        break;
        localObject2 = "application/pgs";
        k = i;
        localObject3 = localObject1;
        break;
        localObject2 = "application/dvbsubs";
        localObject3 = Collections.singletonList(new byte[] { this.codecPrivate[0], this.codecPrivate[1], this.codecPrivate[2], this.codecPrivate[3] });
        k = i;
        break;
        i = 0;
        break label944;
        label1896:
        m = 0;
        break label954;
        label1902:
        if (MimeTypes.isVideo((String)localObject2))
        {
          i = 2;
          if (this.displayUnit == 0)
          {
            if (this.displayWidth != -1) {
              break label2098;
            }
            j = this.width;
            label1933:
            this.displayWidth = j;
            if (this.displayHeight != -1) {
              break label2107;
            }
          }
          label2098:
          label2107:
          for (j = this.height;; j = this.displayHeight)
          {
            this.displayHeight = j;
            float f1 = -1.0F;
            float f2 = f1;
            if (this.displayWidth != -1)
            {
              f2 = f1;
              if (this.displayHeight != -1) {
                f2 = this.height * this.displayWidth / (this.width * this.displayHeight);
              }
            }
            localObject1 = null;
            if (this.hasColorInfo)
            {
              localObject1 = getHdrStaticInfo();
              localObject1 = new ColorInfo(this.colorSpace, this.colorRange, this.colorTransfer, (byte[])localObject1);
            }
            localObject2 = Format.createVideoSampleFormat(Integer.toString(paramInt), (String)localObject2, null, -1, k, this.width, this.height, -1.0F, (List)localObject3, -1, f2, this.projectionData, this.stereoMode, (ColorInfo)localObject1, this.drmInitData);
            paramInt = i;
            break;
            j = this.displayWidth;
            break label1933;
          }
        }
        if ("application/x-subrip".equals(localObject2))
        {
          k = 3;
          localObject2 = Format.createTextSampleFormat(Integer.toString(paramInt), (String)localObject2, m, this.language, this.drmInitData);
          paramInt = k;
        }
        else if ("text/x-ssa".equals(localObject2))
        {
          k = 3;
          localObject3 = new ArrayList(2);
          ((List)localObject3).add(MatroskaExtractor.SSA_DIALOGUE_FORMAT);
          ((List)localObject3).add(this.codecPrivate);
          localObject2 = Format.createTextSampleFormat(Integer.toString(paramInt), (String)localObject2, null, -1, m, this.language, -1, this.drmInitData, Long.MAX_VALUE, (List)localObject3);
          paramInt = k;
        }
        else
        {
          if ((!"application/vobsub".equals(localObject2)) && (!"application/pgs".equals(localObject2)) && (!"application/dvbsubs".equals(localObject2))) {
            break label2306;
          }
          k = 3;
          localObject2 = Format.createImageSampleFormat(Integer.toString(paramInt), (String)localObject2, null, -1, m, (List)localObject3, this.language, this.drmInitData);
          paramInt = k;
        }
      }
      label2306:
      throw new ParserException("Unexpected MIME type.");
    }
    
    public void outputPendingSampleMetadata()
    {
      if (this.trueHdSampleRechunker != null) {
        this.trueHdSampleRechunker.outputPendingSampleMetadata(this);
      }
    }
    
    public void reset()
    {
      if (this.trueHdSampleRechunker != null) {
        this.trueHdSampleRechunker.reset();
      }
    }
  }
  
  private static final class TrueHdSampleRechunker
  {
    private int blockFlags;
    private int chunkSize;
    private boolean foundSyncframe;
    private int sampleCount;
    private final byte[] syncframePrefix = new byte[12];
    private long timeUs;
    
    public void outputPendingSampleMetadata(MatroskaExtractor.Track paramTrack)
    {
      if ((this.foundSyncframe) && (this.sampleCount > 0))
      {
        paramTrack.output.sampleMetadata(this.timeUs, this.blockFlags, this.chunkSize, 0, paramTrack.cryptoData);
        this.sampleCount = 0;
      }
    }
    
    public void reset()
    {
      this.foundSyncframe = false;
    }
    
    public void sampleMetadata(MatroskaExtractor.Track paramTrack, long paramLong)
    {
      if (!this.foundSyncframe) {}
      for (;;)
      {
        return;
        int i = this.sampleCount;
        this.sampleCount = (i + 1);
        if (i == 0) {
          this.timeUs = paramLong;
        }
        if (this.sampleCount >= 8)
        {
          paramTrack.output.sampleMetadata(this.timeUs, this.blockFlags, this.chunkSize, 0, paramTrack.cryptoData);
          this.sampleCount = 0;
        }
      }
    }
    
    public void startSample(ExtractorInput paramExtractorInput, int paramInt1, int paramInt2)
      throws IOException, InterruptedException
    {
      if (!this.foundSyncframe)
      {
        paramExtractorInput.peekFully(this.syncframePrefix, 0, 12);
        paramExtractorInput.resetPeekPosition();
        if (Ac3Util.parseTrueHdSyncframeAudioSampleCount(this.syncframePrefix) != -1) {}
      }
      for (;;)
      {
        return;
        this.foundSyncframe = true;
        this.sampleCount = 0;
        if (this.sampleCount == 0)
        {
          this.blockFlags = paramInt1;
          this.chunkSize = 0;
        }
        this.chunkSize += paramInt2;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/extractor/mkv/MatroskaExtractor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
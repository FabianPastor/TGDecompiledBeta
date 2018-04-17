package org.telegram.messenger.exoplayer2.extractor.mkv;

import android.util.Log;
import android.util.SparseArray;
import java.io.IOException;
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
import org.telegram.messenger.exoplayer2.C0539C;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.RendererCapabilities;
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

public final class MatroskaExtractor implements Extractor {
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
    public static final ExtractorsFactory FACTORY = new C18321();
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
    private static final byte[] SSA_PREFIX = new byte[]{(byte) 68, (byte) 105, (byte) 97, (byte) 108, (byte) 111, (byte) 103, (byte) 117, (byte) 101, (byte) 58, (byte) 32, (byte) 48, (byte) 58, (byte) 48, (byte) 48, (byte) 58, (byte) 48, (byte) 48, (byte) 58, (byte) 48, (byte) 48, (byte) 44, (byte) 48, (byte) 58, (byte) 48, (byte) 48, (byte) 58, (byte) 48, (byte) 48, (byte) 58, (byte) 48, (byte) 48, (byte) 44};
    private static final int SSA_PREFIX_END_TIMECODE_OFFSET = 21;
    private static final byte[] SSA_TIMECODE_EMPTY = new byte[]{(byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32};
    private static final String SSA_TIMECODE_FORMAT = "%01d:%02d:%02d:%02d";
    private static final long SSA_TIMECODE_LAST_VALUE_SCALING_FACTOR = 10000;
    private static final byte[] SUBRIP_PREFIX = new byte[]{(byte) 49, (byte) 10, (byte) 48, (byte) 48, (byte) 58, (byte) 48, (byte) 48, (byte) 58, (byte) 48, (byte) 48, (byte) 44, (byte) 48, (byte) 48, (byte) 48, (byte) 32, (byte) 45, (byte) 45, (byte) 62, (byte) 32, (byte) 48, (byte) 48, (byte) 58, (byte) 48, (byte) 48, (byte) 58, (byte) 48, (byte) 48, (byte) 44, (byte) 48, (byte) 48, (byte) 48, (byte) 10};
    private static final int SUBRIP_PREFIX_END_TIMECODE_OFFSET = 19;
    private static final byte[] SUBRIP_TIMECODE_EMPTY = new byte[]{(byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32};
    private static final String SUBRIP_TIMECODE_FORMAT = "%02d:%02d:%02d,%03d";
    private static final long SUBRIP_TIMECODE_LAST_VALUE_SCALING_FACTOR = 1000;
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
    private long clusterTimecodeUs;
    private LongArray cueClusterPositions;
    private LongArray cueTimesUs;
    private long cuesContentPosition;
    private Track currentTrack;
    private long durationTimecode;
    private long durationUs;
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
    private long seekPositionAfterBuildingCues;
    private boolean seenClusterPositionForCurrentCuePoint;
    private long segmentContentPosition;
    private long segmentContentSize;
    private boolean sentSeekMap;
    private final ParsableByteArray subtitleSample;
    private long timecodeScale;
    private final SparseArray<Track> tracks;
    private final VarintReader varintReader;
    private final ParsableByteArray vorbisNumPageSamples;

    @Retention(RetentionPolicy.SOURCE)
    public @interface Flags {
    }

    private static final class Track {
        private static final int DEFAULT_MAX_CLL = 1000;
        private static final int DEFAULT_MAX_FALL = 200;
        private static final int DISPLAY_UNIT_PIXELS = 0;
        private static final int MAX_CHROMATICITY = 50000;
        public int audioBitDepth;
        public int channelCount;
        public long codecDelayNs;
        public String codecId;
        public byte[] codecPrivate;
        public int colorRange;
        public int colorSpace;
        public int colorTransfer;
        public CryptoData cryptoData;
        public int defaultSampleDurationNs;
        public int displayHeight;
        public int displayUnit;
        public int displayWidth;
        public DrmInitData drmInitData;
        public boolean flagDefault;
        public boolean flagForced;
        public boolean hasColorInfo;
        public boolean hasContentEncryption;
        public int height;
        private String language;
        public int maxContentLuminance;
        public int maxFrameAverageLuminance;
        public float maxMasteringLuminance;
        public float minMasteringLuminance;
        public int nalUnitLengthFieldLength;
        public int number;
        public TrackOutput output;
        public float primaryBChromaticityX;
        public float primaryBChromaticityY;
        public float primaryGChromaticityX;
        public float primaryGChromaticityY;
        public float primaryRChromaticityX;
        public float primaryRChromaticityY;
        public byte[] projectionData;
        public int sampleRate;
        public byte[] sampleStrippedBytes;
        public long seekPreRollNs;
        public int stereoMode;
        public TrueHdSampleRechunker trueHdSampleRechunker;
        public int type;
        public float whitePointChromaticityX;
        public float whitePointChromaticityY;
        public int width;

        private Track() {
            this.width = -1;
            this.height = -1;
            this.displayWidth = -1;
            this.displayHeight = -1;
            this.displayUnit = 0;
            this.projectionData = null;
            this.stereoMode = -1;
            this.hasColorInfo = false;
            this.colorSpace = -1;
            this.colorTransfer = -1;
            this.colorRange = -1;
            this.maxContentLuminance = DEFAULT_MAX_CLL;
            this.maxFrameAverageLuminance = 200;
            this.primaryRChromaticityX = -1.0f;
            this.primaryRChromaticityY = -1.0f;
            this.primaryGChromaticityX = -1.0f;
            this.primaryGChromaticityY = -1.0f;
            this.primaryBChromaticityX = -1.0f;
            this.primaryBChromaticityY = -1.0f;
            this.whitePointChromaticityX = -1.0f;
            this.whitePointChromaticityY = -1.0f;
            this.maxMasteringLuminance = -1.0f;
            this.minMasteringLuminance = -1.0f;
            this.channelCount = 1;
            this.audioBitDepth = -1;
            this.sampleRate = 8000;
            this.codecDelayNs = 0;
            this.seekPreRollNs = 0;
            this.flagDefault = true;
            this.language = "eng";
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void initializeOutput(ExtractorOutput output, int trackId) throws ParserException {
            int i;
            int type;
            Format format;
            int maxInputSize = -1;
            int pcmEncoding = -1;
            List<byte[]> initializationData = null;
            String str = this.codecId;
            int i2 = 0;
            switch (str.hashCode()) {
                case -2095576542:
                    if (str.equals(MatroskaExtractor.CODEC_ID_MPEG4_AP)) {
                        i = 5;
                        break;
                    }
                case -2095575984:
                    if (str.equals(MatroskaExtractor.CODEC_ID_MPEG4_SP)) {
                        i = 3;
                        break;
                    }
                case -1985379776:
                    if (str.equals(MatroskaExtractor.CODEC_ID_ACM)) {
                        i = 22;
                        break;
                    }
                case -1784763192:
                    if (str.equals(MatroskaExtractor.CODEC_ID_TRUEHD)) {
                        i = 17;
                        break;
                    }
                case -1730367663:
                    if (str.equals(MatroskaExtractor.CODEC_ID_VORBIS)) {
                        i = 10;
                        break;
                    }
                case -1482641358:
                    if (str.equals(MatroskaExtractor.CODEC_ID_MP2)) {
                        i = 13;
                        break;
                    }
                case -1482641357:
                    if (str.equals(MatroskaExtractor.CODEC_ID_MP3)) {
                        i = 14;
                        break;
                    }
                case -1373388978:
                    if (str.equals(MatroskaExtractor.CODEC_ID_FOURCC)) {
                        i = 8;
                        break;
                    }
                case -933872740:
                    if (str.equals(MatroskaExtractor.CODEC_ID_DVBSUB)) {
                        i = 28;
                        break;
                    }
                case -538363189:
                    if (str.equals(MatroskaExtractor.CODEC_ID_MPEG4_ASP)) {
                        i = 4;
                        break;
                    }
                case -538363109:
                    if (str.equals(MatroskaExtractor.CODEC_ID_H264)) {
                        i = 6;
                        break;
                    }
                case -425012669:
                    if (str.equals(MatroskaExtractor.CODEC_ID_VOBSUB)) {
                        i = 26;
                        break;
                    }
                case -356037306:
                    if (str.equals(MatroskaExtractor.CODEC_ID_DTS_LOSSLESS)) {
                        i = 20;
                        break;
                    }
                case 62923557:
                    if (str.equals(MatroskaExtractor.CODEC_ID_AAC)) {
                        i = 12;
                        break;
                    }
                case 62923603:
                    if (str.equals(MatroskaExtractor.CODEC_ID_AC3)) {
                        i = 15;
                        break;
                    }
                case 62927045:
                    if (str.equals(MatroskaExtractor.CODEC_ID_DTS)) {
                        i = 18;
                        break;
                    }
                case 82338133:
                    if (str.equals(MatroskaExtractor.CODEC_ID_VP8)) {
                        i = 0;
                        break;
                    }
                case 82338134:
                    if (str.equals(MatroskaExtractor.CODEC_ID_VP9)) {
                        i = 1;
                        break;
                    }
                case 99146302:
                    if (str.equals(MatroskaExtractor.CODEC_ID_PGS)) {
                        i = 27;
                        break;
                    }
                case 444813526:
                    if (str.equals(MatroskaExtractor.CODEC_ID_THEORA)) {
                        i = 9;
                        break;
                    }
                case 542569478:
                    if (str.equals(MatroskaExtractor.CODEC_ID_DTS_EXPRESS)) {
                        i = 19;
                        break;
                    }
                case 725957860:
                    if (str.equals(MatroskaExtractor.CODEC_ID_PCM_INT_LIT)) {
                        i = 23;
                        break;
                    }
                case 738597099:
                    if (str.equals(MatroskaExtractor.CODEC_ID_ASS)) {
                        i = 25;
                        break;
                    }
                case 855502857:
                    if (str.equals(MatroskaExtractor.CODEC_ID_H265)) {
                        i = 7;
                        break;
                    }
                case 1422270023:
                    if (str.equals(MatroskaExtractor.CODEC_ID_SUBRIP)) {
                        i = 24;
                        break;
                    }
                case 1809237540:
                    if (str.equals(MatroskaExtractor.CODEC_ID_MPEG2)) {
                        i = 2;
                        break;
                    }
                case 1950749482:
                    if (str.equals(MatroskaExtractor.CODEC_ID_E_AC3)) {
                        i = 16;
                        break;
                    }
                case 1950789798:
                    if (str.equals(MatroskaExtractor.CODEC_ID_FLAC)) {
                        i = 21;
                        break;
                    }
                case 1951062397:
                    if (str.equals(MatroskaExtractor.CODEC_ID_OPUS)) {
                        i = 11;
                        break;
                    }
                default:
            }
            i = -1;
            String str2;
            StringBuilder stringBuilder;
            switch (i) {
                case 0:
                    str = MimeTypes.VIDEO_VP8;
                    break;
                case 1:
                    str = MimeTypes.VIDEO_VP9;
                    break;
                case 2:
                    str = MimeTypes.VIDEO_MPEG2;
                    break;
                case 3:
                case 4:
                case 5:
                    List<byte[]> list;
                    str = MimeTypes.VIDEO_MP4V;
                    if (r0.codecPrivate == null) {
                        list = null;
                    } else {
                        list = Collections.singletonList(r0.codecPrivate);
                    }
                    initializationData = list;
                    break;
                case 6:
                    str = "video/avc";
                    AvcConfig avcConfig = AvcConfig.parse(new ParsableByteArray(r0.codecPrivate));
                    initializationData = avcConfig.initializationData;
                    r0.nalUnitLengthFieldLength = avcConfig.nalUnitLengthFieldLength;
                    break;
                case 7:
                    str = MimeTypes.VIDEO_H265;
                    HevcConfig hevcConfig = HevcConfig.parse(new ParsableByteArray(r0.codecPrivate));
                    initializationData = hevcConfig.initializationData;
                    r0.nalUnitLengthFieldLength = hevcConfig.nalUnitLengthFieldLength;
                    break;
                case 8:
                    initializationData = parseFourCcVc1Private(new ParsableByteArray(r0.codecPrivate));
                    if (initializationData == null) {
                        Log.w(MatroskaExtractor.TAG, "Unsupported FourCC. Setting mimeType to video/x-unknown");
                        str = MimeTypes.VIDEO_UNKNOWN;
                        break;
                    }
                    str = MimeTypes.VIDEO_VC1;
                    break;
                case 9:
                    str = MimeTypes.VIDEO_UNKNOWN;
                    break;
                case 10:
                    str = MimeTypes.AUDIO_VORBIS;
                    maxInputSize = 8192;
                    initializationData = parseVorbisCodecPrivate(r0.codecPrivate);
                    break;
                case 11:
                    str = MimeTypes.AUDIO_OPUS;
                    maxInputSize = MatroskaExtractor.OPUS_MAX_INPUT_SIZE;
                    initializationData = new ArrayList(3);
                    initializationData.add(r0.codecPrivate);
                    initializationData.add(ByteBuffer.allocate(8).order(ByteOrder.nativeOrder()).putLong(r0.codecDelayNs).array());
                    initializationData.add(ByteBuffer.allocate(8).order(ByteOrder.nativeOrder()).putLong(r0.seekPreRollNs).array());
                    break;
                case 12:
                    str = MimeTypes.AUDIO_AAC;
                    initializationData = Collections.singletonList(r0.codecPrivate);
                    break;
                case 13:
                    str = MimeTypes.AUDIO_MPEG_L2;
                    maxInputSize = 4096;
                    break;
                case 14:
                    str = MimeTypes.AUDIO_MPEG;
                    maxInputSize = 4096;
                    break;
                case 15:
                    str = MimeTypes.AUDIO_AC3;
                    break;
                case 16:
                    str = MimeTypes.AUDIO_E_AC3;
                    break;
                case 17:
                    str = MimeTypes.AUDIO_TRUEHD;
                    r0.trueHdSampleRechunker = new TrueHdSampleRechunker();
                    break;
                case 18:
                case 19:
                    str = MimeTypes.AUDIO_DTS;
                    break;
                case 20:
                    str = MimeTypes.AUDIO_DTS_HD;
                    break;
                case 21:
                    str = MimeTypes.AUDIO_FLAC;
                    initializationData = Collections.singletonList(r0.codecPrivate);
                    break;
                case 22:
                    str = MimeTypes.AUDIO_RAW;
                    if (!parseMsAcmCodecPrivate(new ParsableByteArray(r0.codecPrivate))) {
                        str = MimeTypes.AUDIO_UNKNOWN;
                        str2 = MatroskaExtractor.TAG;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("Non-PCM MS/ACM is unsupported. Setting mimeType to ");
                        stringBuilder.append(str);
                        Log.w(str2, stringBuilder.toString());
                        break;
                    }
                    pcmEncoding = Util.getPcmEncoding(r0.audioBitDepth);
                    if (pcmEncoding == 0) {
                        pcmEncoding = -1;
                        str = MimeTypes.AUDIO_UNKNOWN;
                        str2 = MatroskaExtractor.TAG;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("Unsupported PCM bit depth: ");
                        stringBuilder.append(r0.audioBitDepth);
                        stringBuilder.append(". Setting mimeType to ");
                        stringBuilder.append(str);
                        Log.w(str2, stringBuilder.toString());
                        break;
                    }
                    break;
                case 23:
                    str = MimeTypes.AUDIO_RAW;
                    pcmEncoding = Util.getPcmEncoding(r0.audioBitDepth);
                    if (pcmEncoding == 0) {
                        pcmEncoding = -1;
                        str = MimeTypes.AUDIO_UNKNOWN;
                        str2 = MatroskaExtractor.TAG;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("Unsupported PCM bit depth: ");
                        stringBuilder.append(r0.audioBitDepth);
                        stringBuilder.append(". Setting mimeType to ");
                        stringBuilder.append(str);
                        Log.w(str2, stringBuilder.toString());
                        break;
                    }
                    break;
                case RendererCapabilities.ADAPTIVE_SUPPORT_MASK /*24*/:
                    str = MimeTypes.APPLICATION_SUBRIP;
                    break;
                case 25:
                    str = MimeTypes.TEXT_SSA;
                    break;
                case 26:
                    str = MimeTypes.APPLICATION_VOBSUB;
                    initializationData = Collections.singletonList(r0.codecPrivate);
                    break;
                case 27:
                    str = MimeTypes.APPLICATION_PGS;
                    break;
                case 28:
                    str = MimeTypes.APPLICATION_DVBSUBS;
                    initializationData = Collections.singletonList(new byte[]{r0.codecPrivate[0], r0.codecPrivate[1], r0.codecPrivate[2], r0.codecPrivate[3]});
                    break;
                default:
                    ExtractorOutput extractorOutput = output;
                    throw new ParserException("Unrecognized codec identifier.");
            }
            int selectionFlags = 0 | r0.flagDefault;
            if (r0.flagForced) {
                i2 = 2;
            }
            selectionFlags |= i2;
            if (MimeTypes.isAudio(str)) {
                type = 1;
                format = Format.createAudioSampleFormat(Integer.toString(trackId), str, null, -1, maxInputSize, r0.channelCount, r0.sampleRate, pcmEncoding, initializationData, r0.drmInitData, selectionFlags, r0.language);
            } else if (MimeTypes.isVideo(str)) {
                type = 2;
                if (r0.displayUnit == 0) {
                    r0.displayWidth = r0.displayWidth == -1 ? r0.width : r0.displayWidth;
                    r0.displayHeight = r0.displayHeight == -1 ? r0.height : r0.displayHeight;
                }
                float pixelWidthHeightRatio = -1.0f;
                if (!(r0.displayWidth == -1 || r0.displayHeight == -1)) {
                    pixelWidthHeightRatio = ((float) (r0.height * r0.displayWidth)) / ((float) (r0.width * r0.displayHeight));
                }
                ColorInfo colorInfo = null;
                if (r0.hasColorInfo) {
                    colorInfo = new ColorInfo(r0.colorSpace, r0.colorRange, r0.colorTransfer, getHdrStaticInfo());
                }
                format = Format.createVideoSampleFormat(Integer.toString(trackId), str, null, -1, maxInputSize, r0.width, r0.height, -1.0f, initializationData, -1, pixelWidthHeightRatio, r0.projectionData, r0.stereoMode, colorInfo, r0.drmInitData);
                r0.output = output.track(r0.number, type);
                r0.output.format(format);
            } else if (MimeTypes.APPLICATION_SUBRIP.equals(str)) {
                type = 3;
                format = Format.createTextSampleFormat(Integer.toString(trackId), str, selectionFlags, r0.language, r0.drmInitData);
            } else if (MimeTypes.TEXT_SSA.equals(str)) {
                type = 3;
                List initializationData2 = new ArrayList(2);
                initializationData2.add(MatroskaExtractor.SSA_DIALOGUE_FORMAT);
                initializationData2.add(r0.codecPrivate);
                format = Format.createTextSampleFormat(Integer.toString(trackId), str, null, -1, selectionFlags, r0.language, -1, r0.drmInitData, Long.MAX_VALUE, initializationData2);
            } else {
                if (!(MimeTypes.APPLICATION_VOBSUB.equals(str) || MimeTypes.APPLICATION_PGS.equals(str))) {
                    if (!MimeTypes.APPLICATION_DVBSUBS.equals(str)) {
                        throw new ParserException("Unexpected MIME type.");
                    }
                }
                type = 3;
                format = Format.createImageSampleFormat(Integer.toString(trackId), str, null, -1, selectionFlags, initializationData, r0.language, r0.drmInitData);
            }
            r0.output = output.track(r0.number, type);
            r0.output.format(format);
        }

        public void outputPendingSampleMetadata() {
            if (this.trueHdSampleRechunker != null) {
                this.trueHdSampleRechunker.outputPendingSampleMetadata(this);
            }
        }

        public void reset() {
            if (this.trueHdSampleRechunker != null) {
                this.trueHdSampleRechunker.reset();
            }
        }

        private byte[] getHdrStaticInfo() {
            if (!(this.primaryRChromaticityX == -1.0f || this.primaryRChromaticityY == -1.0f || this.primaryGChromaticityX == -1.0f || this.primaryGChromaticityY == -1.0f || this.primaryBChromaticityX == -1.0f || this.primaryBChromaticityY == -1.0f || this.whitePointChromaticityX == -1.0f || this.whitePointChromaticityY == -1.0f || this.maxMasteringLuminance == -1.0f)) {
                if (this.minMasteringLuminance != -1.0f) {
                    byte[] hdrStaticInfoData = new byte[25];
                    ByteBuffer hdrStaticInfo = ByteBuffer.wrap(hdrStaticInfoData);
                    hdrStaticInfo.put((byte) 0);
                    hdrStaticInfo.putShort((short) ((int) ((this.primaryRChromaticityX * 50000.0f) + 0.5f)));
                    hdrStaticInfo.putShort((short) ((int) ((this.primaryRChromaticityY * 50000.0f) + 0.5f)));
                    hdrStaticInfo.putShort((short) ((int) ((this.primaryGChromaticityX * 50000.0f) + 0.5f)));
                    hdrStaticInfo.putShort((short) ((int) ((this.primaryGChromaticityY * 50000.0f) + 0.5f)));
                    hdrStaticInfo.putShort((short) ((int) ((this.primaryBChromaticityX * 50000.0f) + 0.5f)));
                    hdrStaticInfo.putShort((short) ((int) ((this.primaryBChromaticityY * 50000.0f) + 0.5f)));
                    hdrStaticInfo.putShort((short) ((int) ((this.whitePointChromaticityX * 50000.0f) + 0.5f)));
                    hdrStaticInfo.putShort((short) ((int) ((this.whitePointChromaticityY * 50000.0f) + 0.5f)));
                    hdrStaticInfo.putShort((short) ((int) (this.maxMasteringLuminance + 0.5f)));
                    hdrStaticInfo.putShort((short) ((int) (this.minMasteringLuminance + 0.5f)));
                    hdrStaticInfo.putShort((short) this.maxContentLuminance);
                    hdrStaticInfo.putShort((short) this.maxFrameAverageLuminance);
                    return hdrStaticInfoData;
                }
            }
            return null;
        }

        private static List<byte[]> parseFourCcVc1Private(ParsableByteArray buffer) throws ParserException {
            try {
                buffer.skipBytes(16);
                if (buffer.readLittleEndianUnsignedInt() != 826496599) {
                    return null;
                }
                int startOffset = buffer.getPosition() + 20;
                byte[] bufferData = buffer.data;
                int offset = startOffset;
                while (offset < bufferData.length - 4) {
                    if (bufferData[offset] == (byte) 0 && bufferData[offset + 1] == (byte) 0 && bufferData[offset + 2] == (byte) 1 && bufferData[offset + 3] == (byte) 15) {
                        return Collections.singletonList(Arrays.copyOfRange(bufferData, offset, bufferData.length));
                    }
                    offset++;
                }
                throw new ParserException("Failed to find FourCC VC1 initialization data");
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new ParserException("Error parsing FourCC VC1 codec private");
            }
        }

        private static List<byte[]> parseVorbisCodecPrivate(byte[] codecPrivate) throws ParserException {
            try {
                if (codecPrivate[0] != (byte) 2) {
                    throw new ParserException("Error parsing vorbis codec private");
                }
                int offset = 1;
                int vorbisInfoLength = 0;
                while (codecPrivate[offset] == (byte) -1) {
                    vorbisInfoLength += 255;
                    offset++;
                }
                int offset2 = offset + 1;
                vorbisInfoLength += codecPrivate[offset];
                offset = 0;
                while (codecPrivate[offset2] == (byte) -1) {
                    offset += 255;
                    offset2++;
                }
                int offset3 = offset2 + 1;
                offset += codecPrivate[offset2];
                if (codecPrivate[offset3] != (byte) 1) {
                    throw new ParserException("Error parsing vorbis codec private");
                }
                byte[] vorbisInfo = new byte[vorbisInfoLength];
                System.arraycopy(codecPrivate, offset3, vorbisInfo, 0, vorbisInfoLength);
                offset3 += vorbisInfoLength;
                if (codecPrivate[offset3] != (byte) 3) {
                    throw new ParserException("Error parsing vorbis codec private");
                }
                offset3 += offset;
                if (codecPrivate[offset3] != (byte) 5) {
                    throw new ParserException("Error parsing vorbis codec private");
                }
                byte[] vorbisBooks = new byte[(codecPrivate.length - offset3)];
                System.arraycopy(codecPrivate, offset3, vorbisBooks, 0, codecPrivate.length - offset3);
                List<byte[]> initializationData = new ArrayList(2);
                initializationData.add(vorbisInfo);
                initializationData.add(vorbisBooks);
                return initializationData;
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new ParserException("Error parsing vorbis codec private");
            }
        }

        private static boolean parseMsAcmCodecPrivate(ParsableByteArray buffer) throws ParserException {
            try {
                int formatTag = buffer.readLittleEndianUnsignedShort();
                boolean z = true;
                if (formatTag == 1) {
                    return true;
                }
                if (formatTag != MatroskaExtractor.WAVE_FORMAT_EXTENSIBLE) {
                    return false;
                }
                buffer.setPosition(24);
                if (buffer.readLong() != MatroskaExtractor.WAVE_SUBFORMAT_PCM.getMostSignificantBits() || buffer.readLong() != MatroskaExtractor.WAVE_SUBFORMAT_PCM.getLeastSignificantBits()) {
                    z = false;
                }
                return z;
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new ParserException("Error parsing MS/ACM codec private");
            }
        }
    }

    private static final class TrueHdSampleRechunker {
        private int blockFlags;
        private int chunkSize;
        private boolean foundSyncframe;
        private int sampleCount;
        private final byte[] syncframePrefix = new byte[12];
        private long timeUs;

        public void reset() {
            this.foundSyncframe = false;
        }

        public void startSample(ExtractorInput input, int blockFlags, int size) throws IOException, InterruptedException {
            if (!this.foundSyncframe) {
                input.peekFully(this.syncframePrefix, 0, 12);
                input.resetPeekPosition();
                if (Ac3Util.parseTrueHdSyncframeAudioSampleCount(this.syncframePrefix) != -1) {
                    this.foundSyncframe = true;
                    this.sampleCount = 0;
                } else {
                    return;
                }
            }
            if (this.sampleCount == 0) {
                this.blockFlags = blockFlags;
                this.chunkSize = 0;
            }
            this.chunkSize += size;
        }

        public void sampleMetadata(Track track, long timeUs) {
            if (this.foundSyncframe) {
                int i = this.sampleCount;
                this.sampleCount = i + 1;
                if (i == 0) {
                    this.timeUs = timeUs;
                }
                if (this.sampleCount >= 8) {
                    track.output.sampleMetadata(this.timeUs, this.blockFlags, this.chunkSize, 0, track.cryptoData);
                    this.sampleCount = 0;
                }
            }
        }

        public void outputPendingSampleMetadata(Track track) {
            if (this.foundSyncframe && this.sampleCount > 0) {
                track.output.sampleMetadata(this.timeUs, this.blockFlags, this.chunkSize, 0, track.cryptoData);
                this.sampleCount = 0;
            }
        }
    }

    /* renamed from: org.telegram.messenger.exoplayer2.extractor.mkv.MatroskaExtractor$1 */
    static class C18321 implements ExtractorsFactory {
        C18321() {
        }

        public Extractor[] createExtractors() {
            return new Extractor[]{new MatroskaExtractor()};
        }
    }

    private final class InnerEbmlReaderOutput implements EbmlReaderOutput {
        private InnerEbmlReaderOutput() {
        }

        public int getElementType(int id) {
            return MatroskaExtractor.this.getElementType(id);
        }

        public boolean isLevel1Element(int id) {
            return MatroskaExtractor.this.isLevel1Element(id);
        }

        public void startMasterElement(int id, long contentPosition, long contentSize) throws ParserException {
            MatroskaExtractor.this.startMasterElement(id, contentPosition, contentSize);
        }

        public void endMasterElement(int id) throws ParserException {
            MatroskaExtractor.this.endMasterElement(id);
        }

        public void integerElement(int id, long value) throws ParserException {
            MatroskaExtractor.this.integerElement(id, value);
        }

        public void floatElement(int id, double value) throws ParserException {
            MatroskaExtractor.this.floatElement(id, value);
        }

        public void stringElement(int id, String value) throws ParserException {
            MatroskaExtractor.this.stringElement(id, value);
        }

        public void binaryElement(int id, int contentsSize, ExtractorInput input) throws IOException, InterruptedException {
            MatroskaExtractor.this.binaryElement(id, contentsSize, input);
        }
    }

    public MatroskaExtractor() {
        this(0);
    }

    public MatroskaExtractor(int flags) {
        this(new DefaultEbmlReader(), flags);
    }

    MatroskaExtractor(EbmlReader reader, int flags) {
        this.segmentContentPosition = -1;
        this.timecodeScale = C0539C.TIME_UNSET;
        this.durationTimecode = C0539C.TIME_UNSET;
        this.durationUs = C0539C.TIME_UNSET;
        this.cuesContentPosition = -1;
        this.seekPositionAfterBuildingCues = -1;
        this.clusterTimecodeUs = C0539C.TIME_UNSET;
        this.reader = reader;
        this.reader.init(new InnerEbmlReaderOutput());
        this.seekForCuesEnabled = (flags & 1) == 0;
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
    }

    public boolean sniff(ExtractorInput input) throws IOException, InterruptedException {
        return new Sniffer().sniff(input);
    }

    public void init(ExtractorOutput output) {
        this.extractorOutput = output;
    }

    public void seek(long position, long timeUs) {
        this.clusterTimecodeUs = C0539C.TIME_UNSET;
        int i = 0;
        this.blockState = 0;
        this.reader.reset();
        this.varintReader.reset();
        resetSample();
        while (i < this.tracks.size()) {
            ((Track) this.tracks.valueAt(i)).reset();
            i++;
        }
    }

    public void release() {
    }

    public int read(ExtractorInput input, PositionHolder seekPosition) throws IOException, InterruptedException {
        int i = 0;
        this.sampleRead = false;
        boolean continueReading = true;
        while (continueReading && !this.sampleRead) {
            continueReading = this.reader.read(input);
            if (continueReading && maybeSeekForCues(seekPosition, input.getPosition())) {
                return 1;
            }
        }
        if (continueReading) {
            return 0;
        }
        while (i < this.tracks.size()) {
            ((Track) this.tracks.valueAt(i)).outputPendingSampleMetadata();
            i++;
        }
        return -1;
    }

    int getElementType(int id) {
        switch (id) {
            case ID_TRACK_TYPE /*131*/:
            case ID_FLAG_DEFAULT /*136*/:
            case ID_BLOCK_DURATION /*155*/:
            case ID_CHANNELS /*159*/:
            case ID_PIXEL_WIDTH /*176*/:
            case ID_CUE_TIME /*179*/:
            case ID_PIXEL_HEIGHT /*186*/:
            case ID_TRACK_NUMBER /*215*/:
            case ID_TIME_CODE /*231*/:
            case ID_CUE_CLUSTER_POSITION /*241*/:
            case ID_REFERENCE_BLOCK /*251*/:
            case ID_CONTENT_COMPRESSION_ALGORITHM /*16980*/:
            case ID_DOC_TYPE_READ_VERSION /*17029*/:
            case ID_EBML_READ_VERSION /*17143*/:
            case ID_CONTENT_ENCRYPTION_ALGORITHM /*18401*/:
            case ID_CONTENT_ENCRYPTION_AES_SETTINGS_CIPHER_MODE /*18408*/:
            case ID_CONTENT_ENCODING_ORDER /*20529*/:
            case ID_CONTENT_ENCODING_SCOPE /*20530*/:
            case ID_SEEK_POSITION /*21420*/:
            case ID_STEREO_MODE /*21432*/:
            case ID_DISPLAY_WIDTH /*21680*/:
            case ID_DISPLAY_UNIT /*21682*/:
            case ID_DISPLAY_HEIGHT /*21690*/:
            case ID_FLAG_FORCED /*21930*/:
            case ID_COLOUR_RANGE /*21945*/:
            case ID_COLOUR_TRANSFER /*21946*/:
            case ID_COLOUR_PRIMARIES /*21947*/:
            case ID_MAX_CLL /*21948*/:
            case ID_MAX_FALL /*21949*/:
            case ID_CODEC_DELAY /*22186*/:
            case ID_SEEK_PRE_ROLL /*22203*/:
            case ID_AUDIO_BIT_DEPTH /*25188*/:
            case ID_DEFAULT_DURATION /*2352003*/:
            case ID_TIMECODE_SCALE /*2807729*/:
                return 2;
            case 134:
            case ID_DOC_TYPE /*17026*/:
            case ID_LANGUAGE /*2274716*/:
                return 3;
            case ID_BLOCK_GROUP /*160*/:
            case ID_TRACK_ENTRY /*174*/:
            case ID_CUE_TRACK_POSITIONS /*183*/:
            case ID_CUE_POINT /*187*/:
            case 224:
            case ID_AUDIO /*225*/:
            case ID_CONTENT_ENCRYPTION_AES_SETTINGS /*18407*/:
            case ID_SEEK /*19899*/:
            case ID_CONTENT_COMPRESSION /*20532*/:
            case ID_CONTENT_ENCRYPTION /*20533*/:
            case ID_COLOUR /*21936*/:
            case ID_MASTERING_METADATA /*21968*/:
            case ID_CONTENT_ENCODING /*25152*/:
            case ID_CONTENT_ENCODINGS /*28032*/:
            case ID_PROJECTION /*30320*/:
            case ID_SEEK_HEAD /*290298740*/:
            case 357149030:
            case ID_TRACKS /*374648427*/:
            case ID_SEGMENT /*408125543*/:
            case ID_EBML /*440786851*/:
            case ID_CUES /*475249515*/:
            case ID_CLUSTER /*524531317*/:
                return 1;
            case ID_BLOCK /*161*/:
            case ID_SIMPLE_BLOCK /*163*/:
            case ID_CONTENT_COMPRESSION_SETTINGS /*16981*/:
            case ID_CONTENT_ENCRYPTION_KEY_ID /*18402*/:
            case ID_SEEK_ID /*21419*/:
            case ID_CODEC_PRIVATE /*25506*/:
            case ID_PROJECTION_PRIVATE /*30322*/:
                return 4;
            case ID_SAMPLING_FREQUENCY /*181*/:
            case ID_DURATION /*17545*/:
            case ID_PRIMARY_R_CHROMATICITY_X /*21969*/:
            case ID_PRIMARY_R_CHROMATICITY_Y /*21970*/:
            case ID_PRIMARY_G_CHROMATICITY_X /*21971*/:
            case ID_PRIMARY_G_CHROMATICITY_Y /*21972*/:
            case ID_PRIMARY_B_CHROMATICITY_X /*21973*/:
            case ID_PRIMARY_B_CHROMATICITY_Y /*21974*/:
            case ID_WHITE_POINT_CHROMATICITY_X /*21975*/:
            case ID_WHITE_POINT_CHROMATICITY_Y /*21976*/:
            case ID_LUMNINANCE_MAX /*21977*/:
            case ID_LUMNINANCE_MIN /*21978*/:
                return 5;
            default:
                return 0;
        }
    }

    boolean isLevel1Element(int id) {
        if (!(id == 357149030 || id == ID_CLUSTER || id == ID_CUES)) {
            if (id != ID_TRACKS) {
                return false;
            }
        }
        return true;
    }

    void startMasterElement(int id, long contentPosition, long contentSize) throws ParserException {
        if (id == ID_BLOCK_GROUP) {
            this.sampleSeenReferenceBlock = false;
        } else if (id == ID_TRACK_ENTRY) {
            this.currentTrack = new Track();
        } else if (id == ID_CUE_POINT) {
            this.seenClusterPositionForCurrentCuePoint = false;
        } else if (id == ID_SEEK) {
            this.seekEntryId = -1;
            this.seekEntryPosition = -1;
        } else if (id == ID_CONTENT_ENCRYPTION) {
            this.currentTrack.hasContentEncryption = true;
        } else if (id == ID_MASTERING_METADATA) {
            this.currentTrack.hasColorInfo = true;
        } else if (id == ID_CONTENT_ENCODING) {
        } else {
            if (id != ID_SEGMENT) {
                if (id == ID_CUES) {
                    this.cueTimesUs = new LongArray();
                    this.cueClusterPositions = new LongArray();
                } else if (id == ID_CLUSTER) {
                    if (!this.sentSeekMap) {
                        if (!this.seekForCuesEnabled || this.cuesContentPosition == -1) {
                            this.extractorOutput.seekMap(new Unseekable(this.durationUs));
                            this.sentSeekMap = true;
                            return;
                        }
                        this.seekForCues = true;
                    }
                }
            } else if (this.segmentContentPosition == -1 || this.segmentContentPosition == contentPosition) {
                this.segmentContentPosition = contentPosition;
                this.segmentContentSize = contentSize;
            } else {
                throw new ParserException("Multiple Segment elements not supported");
            }
        }
    }

    void endMasterElement(int id) throws ParserException {
        if (id != ID_BLOCK_GROUP) {
            if (id == ID_TRACK_ENTRY) {
                if (isCodecSupported(this.currentTrack.codecId)) {
                    this.currentTrack.initializeOutput(this.extractorOutput, this.currentTrack.number);
                    this.tracks.put(this.currentTrack.number, this.currentTrack);
                }
                this.currentTrack = null;
            } else if (id == ID_SEEK) {
                if (this.seekEntryId != -1) {
                    if (this.seekEntryPosition != -1) {
                        if (this.seekEntryId == ID_CUES) {
                            this.cuesContentPosition = this.seekEntryPosition;
                        }
                    }
                }
                throw new ParserException("Mandatory element SeekID or SeekPosition not found");
            } else if (id != ID_CONTENT_ENCODING) {
                if (id != ID_CONTENT_ENCODINGS) {
                    if (id == 357149030) {
                        if (this.timecodeScale == C0539C.TIME_UNSET) {
                            this.timecodeScale = C0539C.MICROS_PER_SECOND;
                        }
                        if (this.durationTimecode != C0539C.TIME_UNSET) {
                            this.durationUs = scaleTimecodeToUs(this.durationTimecode);
                        }
                    } else if (id != ID_TRACKS) {
                        if (id == ID_CUES) {
                            if (!this.sentSeekMap) {
                                this.extractorOutput.seekMap(buildSeekMap());
                                this.sentSeekMap = true;
                            }
                        }
                    } else if (this.tracks.size() == 0) {
                        throw new ParserException("No valid tracks were found");
                    } else {
                        this.extractorOutput.endTracks();
                    }
                } else if (this.currentTrack.hasContentEncryption && this.currentTrack.sampleStrippedBytes != null) {
                    throw new ParserException("Combining encryption and compression is not supported");
                }
            } else if (this.currentTrack.hasContentEncryption) {
                if (this.currentTrack.cryptoData == null) {
                    throw new ParserException("Encrypted Track found but ContentEncKeyID was not found");
                }
                this.currentTrack.drmInitData = new DrmInitData(new SchemeData(C0539C.UUID_NIL, MimeTypes.VIDEO_WEBM, this.currentTrack.cryptoData.encryptionKey));
            }
        } else if (this.blockState == 2) {
            if (!this.sampleSeenReferenceBlock) {
                this.blockFlags |= 1;
            }
            commitSampleToOutput((Track) this.tracks.get(this.blockTrackNumber), this.blockTimeUs);
            this.blockState = 0;
        }
    }

    void integerElement(int id, long value) throws ParserException {
        boolean z = false;
        Track track;
        StringBuilder stringBuilder;
        int i;
        switch (id) {
            case ID_TRACK_TYPE /*131*/:
                this.currentTrack.type = (int) value;
                return;
            case ID_FLAG_DEFAULT /*136*/:
                track = this.currentTrack;
                if (value == 1) {
                    z = true;
                }
                track.flagForced = z;
                return;
            case ID_BLOCK_DURATION /*155*/:
                this.blockDurationUs = scaleTimecodeToUs(value);
                return;
            case ID_CHANNELS /*159*/:
                this.currentTrack.channelCount = (int) value;
                return;
            case ID_PIXEL_WIDTH /*176*/:
                this.currentTrack.width = (int) value;
                return;
            case ID_CUE_TIME /*179*/:
                this.cueTimesUs.add(scaleTimecodeToUs(value));
                return;
            case ID_PIXEL_HEIGHT /*186*/:
                this.currentTrack.height = (int) value;
                return;
            case ID_TRACK_NUMBER /*215*/:
                this.currentTrack.number = (int) value;
                return;
            case ID_TIME_CODE /*231*/:
                this.clusterTimecodeUs = scaleTimecodeToUs(value);
                return;
            case ID_CUE_CLUSTER_POSITION /*241*/:
                if (!this.seenClusterPositionForCurrentCuePoint) {
                    this.cueClusterPositions.add(value);
                    this.seenClusterPositionForCurrentCuePoint = true;
                    return;
                }
                return;
            case ID_REFERENCE_BLOCK /*251*/:
                this.sampleSeenReferenceBlock = true;
                return;
            case ID_CONTENT_COMPRESSION_ALGORITHM /*16980*/:
                if (value != 3) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("ContentCompAlgo ");
                    stringBuilder.append(value);
                    stringBuilder.append(" not supported");
                    throw new ParserException(stringBuilder.toString());
                }
                return;
            case ID_DOC_TYPE_READ_VERSION /*17029*/:
                if (value < 1 || value > 2) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("DocTypeReadVersion ");
                    stringBuilder.append(value);
                    stringBuilder.append(" not supported");
                    throw new ParserException(stringBuilder.toString());
                }
                return;
            case ID_EBML_READ_VERSION /*17143*/:
                if (value != 1) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("EBMLReadVersion ");
                    stringBuilder.append(value);
                    stringBuilder.append(" not supported");
                    throw new ParserException(stringBuilder.toString());
                }
                return;
            case ID_CONTENT_ENCRYPTION_ALGORITHM /*18401*/:
                if (value != 5) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("ContentEncAlgo ");
                    stringBuilder.append(value);
                    stringBuilder.append(" not supported");
                    throw new ParserException(stringBuilder.toString());
                }
                return;
            case ID_CONTENT_ENCRYPTION_AES_SETTINGS_CIPHER_MODE /*18408*/:
                if (value != 1) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("AESSettingsCipherMode ");
                    stringBuilder.append(value);
                    stringBuilder.append(" not supported");
                    throw new ParserException(stringBuilder.toString());
                }
                return;
            case ID_CONTENT_ENCODING_ORDER /*20529*/:
                if (value != 0) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("ContentEncodingOrder ");
                    stringBuilder.append(value);
                    stringBuilder.append(" not supported");
                    throw new ParserException(stringBuilder.toString());
                }
                return;
            case ID_CONTENT_ENCODING_SCOPE /*20530*/:
                if (value != 1) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("ContentEncodingScope ");
                    stringBuilder.append(value);
                    stringBuilder.append(" not supported");
                    throw new ParserException(stringBuilder.toString());
                }
                return;
            case ID_SEEK_POSITION /*21420*/:
                this.seekEntryPosition = value + this.segmentContentPosition;
                return;
            case ID_STEREO_MODE /*21432*/:
                int layout = (int) value;
                if (layout == 3) {
                    this.currentTrack.stereoMode = 1;
                    return;
                } else if (layout != 15) {
                    switch (layout) {
                        case 0:
                            this.currentTrack.stereoMode = 0;
                            return;
                        case 1:
                            this.currentTrack.stereoMode = 2;
                            return;
                        default:
                            return;
                    }
                } else {
                    this.currentTrack.stereoMode = 3;
                    return;
                }
            case ID_DISPLAY_WIDTH /*21680*/:
                this.currentTrack.displayWidth = (int) value;
                return;
            case ID_DISPLAY_UNIT /*21682*/:
                this.currentTrack.displayUnit = (int) value;
                return;
            case ID_DISPLAY_HEIGHT /*21690*/:
                this.currentTrack.displayHeight = (int) value;
                return;
            case ID_FLAG_FORCED /*21930*/:
                track = this.currentTrack;
                if (value == 1) {
                    z = true;
                }
                track.flagDefault = z;
                return;
            case ID_COLOUR_RANGE /*21945*/:
                switch ((int) value) {
                    case 1:
                        this.currentTrack.colorRange = 2;
                        return;
                    case 2:
                        this.currentTrack.colorRange = 1;
                        return;
                    default:
                        return;
                }
            case ID_COLOUR_TRANSFER /*21946*/:
                i = (int) value;
                if (i != 1) {
                    if (i == 16) {
                        this.currentTrack.colorTransfer = 6;
                        return;
                    } else if (i != 18) {
                        switch (i) {
                            case 6:
                            case 7:
                                break;
                            default:
                                return;
                        }
                    } else {
                        this.currentTrack.colorTransfer = 7;
                        return;
                    }
                }
                this.currentTrack.colorTransfer = 3;
                return;
            case ID_COLOUR_PRIMARIES /*21947*/:
                this.currentTrack.hasColorInfo = true;
                i = (int) value;
                if (i == 1) {
                    this.currentTrack.colorSpace = 1;
                    return;
                } else if (i != 9) {
                    switch (i) {
                        case 4:
                        case 5:
                        case 6:
                        case 7:
                            this.currentTrack.colorSpace = 2;
                            return;
                        default:
                            return;
                    }
                } else {
                    this.currentTrack.colorSpace = 6;
                    return;
                }
            case ID_MAX_CLL /*21948*/:
                this.currentTrack.maxContentLuminance = (int) value;
                return;
            case ID_MAX_FALL /*21949*/:
                this.currentTrack.maxFrameAverageLuminance = (int) value;
                return;
            case ID_CODEC_DELAY /*22186*/:
                this.currentTrack.codecDelayNs = value;
                return;
            case ID_SEEK_PRE_ROLL /*22203*/:
                this.currentTrack.seekPreRollNs = value;
                return;
            case ID_AUDIO_BIT_DEPTH /*25188*/:
                this.currentTrack.audioBitDepth = (int) value;
                return;
            case ID_DEFAULT_DURATION /*2352003*/:
                this.currentTrack.defaultSampleDurationNs = (int) value;
                return;
            case ID_TIMECODE_SCALE /*2807729*/:
                this.timecodeScale = value;
                return;
            default:
                return;
        }
    }

    void floatElement(int id, double value) {
        if (id == ID_SAMPLING_FREQUENCY) {
            this.currentTrack.sampleRate = (int) value;
        } else if (id != ID_DURATION) {
            switch (id) {
                case ID_PRIMARY_R_CHROMATICITY_X /*21969*/:
                    this.currentTrack.primaryRChromaticityX = (float) value;
                    return;
                case ID_PRIMARY_R_CHROMATICITY_Y /*21970*/:
                    this.currentTrack.primaryRChromaticityY = (float) value;
                    return;
                case ID_PRIMARY_G_CHROMATICITY_X /*21971*/:
                    this.currentTrack.primaryGChromaticityX = (float) value;
                    return;
                case ID_PRIMARY_G_CHROMATICITY_Y /*21972*/:
                    this.currentTrack.primaryGChromaticityY = (float) value;
                    return;
                case ID_PRIMARY_B_CHROMATICITY_X /*21973*/:
                    this.currentTrack.primaryBChromaticityX = (float) value;
                    return;
                case ID_PRIMARY_B_CHROMATICITY_Y /*21974*/:
                    this.currentTrack.primaryBChromaticityY = (float) value;
                    return;
                case ID_WHITE_POINT_CHROMATICITY_X /*21975*/:
                    this.currentTrack.whitePointChromaticityX = (float) value;
                    return;
                case ID_WHITE_POINT_CHROMATICITY_Y /*21976*/:
                    this.currentTrack.whitePointChromaticityY = (float) value;
                    return;
                case ID_LUMNINANCE_MAX /*21977*/:
                    this.currentTrack.maxMasteringLuminance = (float) value;
                    return;
                case ID_LUMNINANCE_MIN /*21978*/:
                    this.currentTrack.minMasteringLuminance = (float) value;
                    return;
                default:
                    return;
            }
        } else {
            this.durationTimecode = (long) value;
        }
    }

    void stringElement(int id, String value) throws ParserException {
        if (id == 134) {
            this.currentTrack.codecId = value;
        } else if (id != ID_DOC_TYPE) {
            if (id == ID_LANGUAGE) {
                this.currentTrack.language = value;
            }
        } else if (!DOC_TYPE_WEBM.equals(value) && !DOC_TYPE_MATROSKA.equals(value)) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("DocType ");
            stringBuilder.append(value);
            stringBuilder.append(" not supported");
            throw new ParserException(stringBuilder.toString());
        }
    }

    void binaryElement(int id, int contentSize, ExtractorInput input) throws IOException, InterruptedException {
        MatroskaExtractor matroskaExtractor = this;
        int i = id;
        int i2 = contentSize;
        ExtractorInput extractorInput = input;
        boolean z = false;
        int i3 = 1;
        if (i == ID_BLOCK || i == ID_SIMPLE_BLOCK) {
            long j = 8;
            if (matroskaExtractor.blockState == 0) {
                matroskaExtractor.blockTrackNumber = (int) matroskaExtractor.varintReader.readUnsignedVarint(extractorInput, false, true, 8);
                matroskaExtractor.blockTrackNumberLength = matroskaExtractor.varintReader.getLastLength();
                matroskaExtractor.blockDurationUs = C0539C.TIME_UNSET;
                matroskaExtractor.blockState = 1;
                matroskaExtractor.scratch.reset();
            }
            Track track = (Track) matroskaExtractor.tracks.get(matroskaExtractor.blockTrackNumber);
            if (track == null) {
                extractorInput.skipFully(i2 - matroskaExtractor.blockTrackNumberLength);
                matroskaExtractor.blockState = 0;
                return;
            }
            if (matroskaExtractor.blockState == 1) {
                readScratch(extractorInput, 3);
                int i4 = 6;
                int lacing = (matroskaExtractor.scratch.data[2] & 6) >> 1;
                int i5 = 255;
                if (lacing == 0) {
                    matroskaExtractor.blockLacingSampleCount = 1;
                    matroskaExtractor.blockLacingSampleSizes = ensureArrayCapacity(matroskaExtractor.blockLacingSampleSizes, 1);
                    matroskaExtractor.blockLacingSampleSizes[0] = (i2 - matroskaExtractor.blockTrackNumberLength) - 3;
                } else if (i != ID_SIMPLE_BLOCK) {
                    throw new ParserException("Lacing only supported in SimpleBlocks.");
                } else {
                    readScratch(extractorInput, 4);
                    matroskaExtractor.blockLacingSampleCount = (matroskaExtractor.scratch.data[3] & 255) + 1;
                    matroskaExtractor.blockLacingSampleSizes = ensureArrayCapacity(matroskaExtractor.blockLacingSampleSizes, matroskaExtractor.blockLacingSampleCount);
                    if (lacing == 2) {
                        Arrays.fill(matroskaExtractor.blockLacingSampleSizes, 0, matroskaExtractor.blockLacingSampleCount, ((i2 - matroskaExtractor.blockTrackNumberLength) - 4) / matroskaExtractor.blockLacingSampleCount);
                    } else if (lacing == 1) {
                        headerSize = 4;
                        totalSamplesSize = 0;
                        for (sampleIndex = 0; sampleIndex < matroskaExtractor.blockLacingSampleCount - 1; sampleIndex++) {
                            matroskaExtractor.blockLacingSampleSizes[sampleIndex] = 0;
                            do {
                                headerSize++;
                                readScratch(extractorInput, headerSize);
                                i4 = matroskaExtractor.scratch.data[headerSize - 1] & 255;
                                int[] iArr = matroskaExtractor.blockLacingSampleSizes;
                                iArr[sampleIndex] = iArr[sampleIndex] + i4;
                            } while (i4 == 255);
                            totalSamplesSize += matroskaExtractor.blockLacingSampleSizes[sampleIndex];
                        }
                        matroskaExtractor.blockLacingSampleSizes[matroskaExtractor.blockLacingSampleCount - 1] = ((i2 - matroskaExtractor.blockTrackNumberLength) - headerSize) - totalSamplesSize;
                    } else if (lacing == 3) {
                        headerSize = 4;
                        totalSamplesSize = 0;
                        sampleIndex = 0;
                        while (sampleIndex < matroskaExtractor.blockLacingSampleCount - i3) {
                            matroskaExtractor.blockLacingSampleSizes[sampleIndex] = z;
                            headerSize++;
                            readScratch(extractorInput, headerSize);
                            if (matroskaExtractor.scratch.data[headerSize - 1] == (byte) 0) {
                                throw new ParserException("No valid varint length mask found");
                            }
                            int readPosition;
                            int i6;
                            long readValue;
                            long readValue2 = 0;
                            int i7 = z;
                            while (i7 < j) {
                                int lengthMask = i3 << (7 - i7);
                                if ((matroskaExtractor.scratch.data[headerSize - 1] & lengthMask) != 0) {
                                    int readPosition2 = headerSize - 1;
                                    headerSize += i7;
                                    readScratch(extractorInput, headerSize);
                                    readValue2 = (long) ((matroskaExtractor.scratch.data[readPosition2] & i5) & (lengthMask ^ -1));
                                    readPosition = readPosition2 + 1;
                                    while (readPosition < headerSize) {
                                        readPosition++;
                                        readValue2 = (readValue2 << j) | ((long) (matroskaExtractor.scratch.data[readPosition] & 255));
                                        j = 8;
                                    }
                                    if (sampleIndex > 0) {
                                        i6 = 6;
                                        readValue = readValue2 - ((1 << (6 + (i7 * 7))) - 1);
                                        if (readValue >= -2147483648L) {
                                            if (readValue > 2147483647L) {
                                                readPosition = (int) readValue;
                                                matroskaExtractor.blockLacingSampleSizes[sampleIndex] = sampleIndex == 0 ? readPosition : matroskaExtractor.blockLacingSampleSizes[sampleIndex - 1] + readPosition;
                                                totalSamplesSize += matroskaExtractor.blockLacingSampleSizes[sampleIndex];
                                                sampleIndex++;
                                                i4 = i6;
                                                z = false;
                                                i3 = 1;
                                                j = 8;
                                                i5 = 255;
                                            }
                                        }
                                        throw new ParserException("EBML lacing sample size out of range.");
                                    }
                                    i6 = 6;
                                    readValue = readValue2;
                                    if (readValue >= -2147483648L) {
                                        if (readValue > 2147483647L) {
                                            readPosition = (int) readValue;
                                            if (sampleIndex == 0) {
                                            }
                                            matroskaExtractor.blockLacingSampleSizes[sampleIndex] = sampleIndex == 0 ? readPosition : matroskaExtractor.blockLacingSampleSizes[sampleIndex - 1] + readPosition;
                                            totalSamplesSize += matroskaExtractor.blockLacingSampleSizes[sampleIndex];
                                            sampleIndex++;
                                            i4 = i6;
                                            z = false;
                                            i3 = 1;
                                            j = 8;
                                            i5 = 255;
                                        }
                                    }
                                    throw new ParserException("EBML lacing sample size out of range.");
                                }
                                i6 = i4;
                                i7++;
                                i3 = 1;
                                j = 8;
                                i5 = 255;
                            }
                            i6 = i4;
                            readValue = readValue2;
                            if (readValue >= -2147483648L) {
                                if (readValue > 2147483647L) {
                                    readPosition = (int) readValue;
                                    if (sampleIndex == 0) {
                                    }
                                    matroskaExtractor.blockLacingSampleSizes[sampleIndex] = sampleIndex == 0 ? readPosition : matroskaExtractor.blockLacingSampleSizes[sampleIndex - 1] + readPosition;
                                    totalSamplesSize += matroskaExtractor.blockLacingSampleSizes[sampleIndex];
                                    sampleIndex++;
                                    i4 = i6;
                                    z = false;
                                    i3 = 1;
                                    j = 8;
                                    i5 = 255;
                                }
                            }
                            throw new ParserException("EBML lacing sample size out of range.");
                        }
                        matroskaExtractor.blockLacingSampleSizes[matroskaExtractor.blockLacingSampleCount - 1] = ((i2 - matroskaExtractor.blockTrackNumberLength) - headerSize) - totalSamplesSize;
                    } else {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("Unexpected lacing value: ");
                        stringBuilder.append(lacing);
                        throw new ParserException(stringBuilder.toString());
                    }
                }
                matroskaExtractor.blockTimeUs = matroskaExtractor.clusterTimecodeUs + scaleTimecodeToUs((long) ((matroskaExtractor.scratch.data[0] << 8) | (matroskaExtractor.scratch.data[1] & 255)));
                boolean isInvisible = (matroskaExtractor.scratch.data[2] & 8) == 8;
                if (track.type != 2) {
                    if (i != ID_SIMPLE_BLOCK || (matroskaExtractor.scratch.data[2] & 128) != 128) {
                        z = false;
                        matroskaExtractor.blockFlags = (z ? 1 : 0) | (isInvisible ? Integer.MIN_VALUE : 0);
                        matroskaExtractor.blockState = 2;
                        matroskaExtractor.blockLacingSampleIndex = 0;
                    }
                }
                z = true;
                if (z) {
                }
                if (isInvisible) {
                }
                matroskaExtractor.blockFlags = (z ? 1 : 0) | (isInvisible ? Integer.MIN_VALUE : 0);
                matroskaExtractor.blockState = 2;
                matroskaExtractor.blockLacingSampleIndex = 0;
            }
            if (i == ID_SIMPLE_BLOCK) {
                while (matroskaExtractor.blockLacingSampleIndex < matroskaExtractor.blockLacingSampleCount) {
                    writeSampleData(extractorInput, track, matroskaExtractor.blockLacingSampleSizes[matroskaExtractor.blockLacingSampleIndex]);
                    commitSampleToOutput(track, matroskaExtractor.blockTimeUs + ((long) ((matroskaExtractor.blockLacingSampleIndex * track.defaultSampleDurationNs) / 1000)));
                    matroskaExtractor.blockLacingSampleIndex++;
                }
                matroskaExtractor.blockState = 0;
            } else {
                writeSampleData(extractorInput, track, matroskaExtractor.blockLacingSampleSizes[0]);
            }
        } else if (i == ID_CONTENT_COMPRESSION_SETTINGS) {
            matroskaExtractor.currentTrack.sampleStrippedBytes = new byte[i2];
            extractorInput.readFully(matroskaExtractor.currentTrack.sampleStrippedBytes, 0, i2);
        } else if (i == ID_CONTENT_ENCRYPTION_KEY_ID) {
            byte[] encryptionKey = new byte[i2];
            extractorInput.readFully(encryptionKey, 0, i2);
            matroskaExtractor.currentTrack.cryptoData = new CryptoData(1, encryptionKey, 0, 0);
        } else if (i == ID_SEEK_ID) {
            Arrays.fill(matroskaExtractor.seekEntryIdBytes.data, (byte) 0);
            extractorInput.readFully(matroskaExtractor.seekEntryIdBytes.data, 4 - i2, i2);
            matroskaExtractor.seekEntryIdBytes.setPosition(0);
            matroskaExtractor.seekEntryId = (int) matroskaExtractor.seekEntryIdBytes.readUnsignedInt();
        } else if (i == ID_CODEC_PRIVATE) {
            matroskaExtractor.currentTrack.codecPrivate = new byte[i2];
            extractorInput.readFully(matroskaExtractor.currentTrack.codecPrivate, 0, i2);
        } else if (i != ID_PROJECTION_PRIVATE) {
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("Unexpected id: ");
            stringBuilder2.append(i);
            throw new ParserException(stringBuilder2.toString());
        } else {
            matroskaExtractor.currentTrack.projectionData = new byte[i2];
            extractorInput.readFully(matroskaExtractor.currentTrack.projectionData, 0, i2);
        }
    }

    private void commitSampleToOutput(Track track, long timeUs) {
        MatroskaExtractor matroskaExtractor = this;
        Track track2 = track;
        if (track2.trueHdSampleRechunker != null) {
            track2.trueHdSampleRechunker.sampleMetadata(track2, timeUs);
        } else {
            long j = timeUs;
            if (CODEC_ID_SUBRIP.equals(track2.codecId)) {
                commitSubtitleSample(track2, SUBRIP_TIMECODE_FORMAT, 19, SUBRIP_TIMECODE_LAST_VALUE_SCALING_FACTOR, SUBRIP_TIMECODE_EMPTY);
            } else if (CODEC_ID_ASS.equals(track2.codecId)) {
                commitSubtitleSample(track2, SSA_TIMECODE_FORMAT, 21, SSA_TIMECODE_LAST_VALUE_SCALING_FACTOR, SSA_TIMECODE_EMPTY);
            }
            track2.output.sampleMetadata(j, matroskaExtractor.blockFlags, matroskaExtractor.sampleBytesWritten, 0, track2.cryptoData);
        }
        matroskaExtractor.sampleRead = true;
        resetSample();
    }

    private void resetSample() {
        this.sampleBytesRead = 0;
        this.sampleBytesWritten = 0;
        this.sampleCurrentNalBytesRemaining = 0;
        this.sampleEncodingHandled = false;
        this.sampleSignalByteRead = false;
        this.samplePartitionCountRead = false;
        this.samplePartitionCount = 0;
        this.sampleSignalByte = (byte) 0;
        this.sampleInitializationVectorRead = false;
        this.sampleStrippedBytes.reset();
    }

    private void readScratch(ExtractorInput input, int requiredLength) throws IOException, InterruptedException {
        if (this.scratch.limit() < requiredLength) {
            if (this.scratch.capacity() < requiredLength) {
                this.scratch.reset(Arrays.copyOf(this.scratch.data, Math.max(this.scratch.data.length * 2, requiredLength)), this.scratch.limit());
            }
            input.readFully(this.scratch.data, this.scratch.limit(), requiredLength - this.scratch.limit());
            this.scratch.setLimit(requiredLength);
        }
    }

    private void writeSampleData(ExtractorInput input, Track track, int size) throws IOException, InterruptedException {
        MatroskaExtractor matroskaExtractor = this;
        ExtractorInput extractorInput = input;
        Track track2 = track;
        int size2 = size;
        if (CODEC_ID_SUBRIP.equals(track2.codecId)) {
            writeSubtitleSampleData(extractorInput, SUBRIP_PREFIX, size2);
        } else if (CODEC_ID_ASS.equals(track2.codecId)) {
            writeSubtitleSampleData(extractorInput, SSA_PREFIX, size2);
        } else {
            int previousPartitionOffset;
            TrackOutput output = track2.output;
            if (!matroskaExtractor.sampleEncodingHandled) {
                if (track2.hasContentEncryption) {
                    matroskaExtractor.blockFlags &= -NUM;
                    int i = 128;
                    if (!matroskaExtractor.sampleSignalByteRead) {
                        extractorInput.readFully(matroskaExtractor.scratch.data, 0, 1);
                        matroskaExtractor.sampleBytesRead++;
                        if ((matroskaExtractor.scratch.data[0] & 128) == 128) {
                            throw new ParserException("Extension bit is set in signal byte");
                        }
                        matroskaExtractor.sampleSignalByte = matroskaExtractor.scratch.data[0];
                        matroskaExtractor.sampleSignalByteRead = true;
                    }
                    if ((matroskaExtractor.sampleSignalByte & 1) == 1) {
                        boolean hasSubsampleEncryption = (matroskaExtractor.sampleSignalByte & 2) == 2;
                        matroskaExtractor.blockFlags |= NUM;
                        if (!matroskaExtractor.sampleInitializationVectorRead) {
                            extractorInput.readFully(matroskaExtractor.encryptionInitializationVector.data, 0, 8);
                            matroskaExtractor.sampleBytesRead += 8;
                            matroskaExtractor.sampleInitializationVectorRead = true;
                            byte[] bArr = matroskaExtractor.scratch.data;
                            if (!hasSubsampleEncryption) {
                                i = 0;
                            }
                            bArr[0] = (byte) (i | 8);
                            matroskaExtractor.scratch.setPosition(0);
                            output.sampleData(matroskaExtractor.scratch, 1);
                            matroskaExtractor.sampleBytesWritten++;
                            matroskaExtractor.encryptionInitializationVector.setPosition(0);
                            output.sampleData(matroskaExtractor.encryptionInitializationVector, 8);
                            matroskaExtractor.sampleBytesWritten += 8;
                        }
                        if (hasSubsampleEncryption) {
                            if (!matroskaExtractor.samplePartitionCountRead) {
                                extractorInput.readFully(matroskaExtractor.scratch.data, 0, 1);
                                matroskaExtractor.sampleBytesRead++;
                                matroskaExtractor.scratch.setPosition(0);
                                matroskaExtractor.samplePartitionCount = matroskaExtractor.scratch.readUnsignedByte();
                                matroskaExtractor.samplePartitionCountRead = true;
                            }
                            i = matroskaExtractor.samplePartitionCount * 4;
                            matroskaExtractor.scratch.reset(i);
                            extractorInput.readFully(matroskaExtractor.scratch.data, 0, i);
                            matroskaExtractor.sampleBytesRead += i;
                            short subsampleCount = (short) ((matroskaExtractor.samplePartitionCount / 2) + (short) 1);
                            int subsampleDataSize = (6 * subsampleCount) + 2;
                            if (matroskaExtractor.encryptionSubsampleDataBuffer == null || matroskaExtractor.encryptionSubsampleDataBuffer.capacity() < subsampleDataSize) {
                                matroskaExtractor.encryptionSubsampleDataBuffer = ByteBuffer.allocate(subsampleDataSize);
                            }
                            matroskaExtractor.encryptionSubsampleDataBuffer.position(0);
                            matroskaExtractor.encryptionSubsampleDataBuffer.putShort(subsampleCount);
                            int partitionOffset = 0;
                            for (int i2 = 0; i2 < matroskaExtractor.samplePartitionCount; i2++) {
                                previousPartitionOffset = partitionOffset;
                                partitionOffset = matroskaExtractor.scratch.readUnsignedIntToInt();
                                if (i2 % 2 == 0) {
                                    matroskaExtractor.encryptionSubsampleDataBuffer.putShort((short) (partitionOffset - previousPartitionOffset));
                                } else {
                                    matroskaExtractor.encryptionSubsampleDataBuffer.putInt(partitionOffset - previousPartitionOffset);
                                }
                            }
                            previousPartitionOffset = (size2 - matroskaExtractor.sampleBytesRead) - partitionOffset;
                            if (matroskaExtractor.samplePartitionCount % 2 == 1) {
                                matroskaExtractor.encryptionSubsampleDataBuffer.putInt(previousPartitionOffset);
                            } else {
                                matroskaExtractor.encryptionSubsampleDataBuffer.putShort((short) previousPartitionOffset);
                                matroskaExtractor.encryptionSubsampleDataBuffer.putInt(0);
                            }
                            matroskaExtractor.encryptionSubsampleData.reset(matroskaExtractor.encryptionSubsampleDataBuffer.array(), subsampleDataSize);
                            output.sampleData(matroskaExtractor.encryptionSubsampleData, subsampleDataSize);
                            matroskaExtractor.sampleBytesWritten += subsampleDataSize;
                        }
                    }
                } else if (track2.sampleStrippedBytes != null) {
                    matroskaExtractor.sampleStrippedBytes.reset(track2.sampleStrippedBytes, track2.sampleStrippedBytes.length);
                }
                matroskaExtractor.sampleEncodingHandled = true;
            }
            size2 += matroskaExtractor.sampleStrippedBytes.limit();
            if (!CODEC_ID_H264.equals(track2.codecId)) {
                if (!CODEC_ID_H265.equals(track2.codecId)) {
                    if (track2.trueHdSampleRechunker != null) {
                        Assertions.checkState(matroskaExtractor.sampleStrippedBytes.limit() == 0);
                        track2.trueHdSampleRechunker.startSample(extractorInput, matroskaExtractor.blockFlags, size2);
                    }
                    while (matroskaExtractor.sampleBytesRead < size2) {
                        readToOutput(extractorInput, output, size2 - matroskaExtractor.sampleBytesRead);
                    }
                    if (CODEC_ID_VORBIS.equals(track2.codecId)) {
                        matroskaExtractor.vorbisNumPageSamples.setPosition(0);
                        output.sampleData(matroskaExtractor.vorbisNumPageSamples, 4);
                        matroskaExtractor.sampleBytesWritten += 4;
                    }
                }
            }
            byte[] nalLengthData = matroskaExtractor.nalLength.data;
            nalLengthData[0] = (byte) 0;
            nalLengthData[1] = (byte) 0;
            nalLengthData[2] = (byte) 0;
            previousPartitionOffset = track2.nalUnitLengthFieldLength;
            int nalUnitLengthFieldLengthDiff = 4 - track2.nalUnitLengthFieldLength;
            while (matroskaExtractor.sampleBytesRead < size2) {
                if (matroskaExtractor.sampleCurrentNalBytesRemaining == 0) {
                    readToTarget(extractorInput, nalLengthData, nalUnitLengthFieldLengthDiff, previousPartitionOffset);
                    matroskaExtractor.nalLength.setPosition(0);
                    matroskaExtractor.sampleCurrentNalBytesRemaining = matroskaExtractor.nalLength.readUnsignedIntToInt();
                    matroskaExtractor.nalStartCode.setPosition(0);
                    output.sampleData(matroskaExtractor.nalStartCode, 4);
                    matroskaExtractor.sampleBytesWritten += 4;
                } else {
                    matroskaExtractor.sampleCurrentNalBytesRemaining -= readToOutput(extractorInput, output, matroskaExtractor.sampleCurrentNalBytesRemaining);
                }
            }
            if (CODEC_ID_VORBIS.equals(track2.codecId)) {
                matroskaExtractor.vorbisNumPageSamples.setPosition(0);
                output.sampleData(matroskaExtractor.vorbisNumPageSamples, 4);
                matroskaExtractor.sampleBytesWritten += 4;
            }
        }
    }

    private void writeSubtitleSampleData(ExtractorInput input, byte[] samplePrefix, int size) throws IOException, InterruptedException {
        int sizeWithPrefix = samplePrefix.length + size;
        if (this.subtitleSample.capacity() < sizeWithPrefix) {
            this.subtitleSample.data = Arrays.copyOf(samplePrefix, sizeWithPrefix + size);
        } else {
            System.arraycopy(samplePrefix, 0, this.subtitleSample.data, 0, samplePrefix.length);
        }
        input.readFully(this.subtitleSample.data, samplePrefix.length, size);
        this.subtitleSample.reset(sizeWithPrefix);
    }

    private void commitSubtitleSample(Track track, String timecodeFormat, int endTimecodeOffset, long lastTimecodeValueScalingFactor, byte[] emptyTimecode) {
        setSampleDuration(this.subtitleSample.data, this.blockDurationUs, timecodeFormat, endTimecodeOffset, lastTimecodeValueScalingFactor, emptyTimecode);
        track.output.sampleData(this.subtitleSample, this.subtitleSample.limit());
        this.sampleBytesWritten += this.subtitleSample.limit();
    }

    private static void setSampleDuration(byte[] subripSampleData, long durationUs, String timecodeFormat, int endTimecodeOffset, long lastTimecodeValueScalingFactor, byte[] emptyTimecode) {
        byte[] timeCodeData;
        if (durationUs == C0539C.TIME_UNSET) {
            timeCodeData = emptyTimecode;
            long j = durationUs;
            String str = timecodeFormat;
        } else {
            long durationUs2 = durationUs - (((long) (((int) (durationUs / 3600000000L)) * 3600)) * C0539C.MICROS_PER_SECOND);
            long durationUs3 = durationUs2 - (((long) (((int) (durationUs2 / 60000000)) * 60)) * C0539C.MICROS_PER_SECOND);
            int lastValue = (int) ((durationUs3 - (((long) ((int) (durationUs3 / C0539C.MICROS_PER_SECOND))) * C0539C.MICROS_PER_SECOND)) / lastTimecodeValueScalingFactor);
            timeCodeData = Util.getUtf8Bytes(String.format(Locale.US, timecodeFormat, new Object[]{Integer.valueOf(hours), Integer.valueOf(minutes), Integer.valueOf(seconds), Integer.valueOf(lastValue)}));
        }
        System.arraycopy(timeCodeData, 0, subripSampleData, endTimecodeOffset, emptyTimecode.length);
    }

    private void readToTarget(ExtractorInput input, byte[] target, int offset, int length) throws IOException, InterruptedException {
        int pendingStrippedBytes = Math.min(length, this.sampleStrippedBytes.bytesLeft());
        input.readFully(target, offset + pendingStrippedBytes, length - pendingStrippedBytes);
        if (pendingStrippedBytes > 0) {
            this.sampleStrippedBytes.readBytes(target, offset, pendingStrippedBytes);
        }
        this.sampleBytesRead += length;
    }

    private int readToOutput(ExtractorInput input, TrackOutput output, int length) throws IOException, InterruptedException {
        int bytesRead;
        int strippedBytesLeft = this.sampleStrippedBytes.bytesLeft();
        if (strippedBytesLeft > 0) {
            bytesRead = Math.min(length, strippedBytesLeft);
            output.sampleData(this.sampleStrippedBytes, bytesRead);
        } else {
            bytesRead = output.sampleData(input, length, false);
        }
        this.sampleBytesRead += bytesRead;
        this.sampleBytesWritten += bytesRead;
        return bytesRead;
    }

    private SeekMap buildSeekMap() {
        if (!(this.segmentContentPosition == -1 || this.durationUs == C0539C.TIME_UNSET || this.cueTimesUs == null || this.cueTimesUs.size() == 0 || this.cueClusterPositions == null)) {
            if (this.cueClusterPositions.size() == this.cueTimesUs.size()) {
                int cuePointsSize = this.cueTimesUs.size();
                int[] sizes = new int[cuePointsSize];
                long[] offsets = new long[cuePointsSize];
                long[] durationsUs = new long[cuePointsSize];
                long[] timesUs = new long[cuePointsSize];
                int i = 0;
                for (int i2 = 0; i2 < cuePointsSize; i2++) {
                    timesUs[i2] = this.cueTimesUs.get(i2);
                    offsets[i2] = this.segmentContentPosition + this.cueClusterPositions.get(i2);
                }
                while (i < cuePointsSize - 1) {
                    sizes[i] = (int) (offsets[i + 1] - offsets[i]);
                    durationsUs[i] = timesUs[i + 1] - timesUs[i];
                    i++;
                }
                sizes[cuePointsSize - 1] = (int) ((this.segmentContentPosition + this.segmentContentSize) - offsets[cuePointsSize - 1]);
                durationsUs[cuePointsSize - 1] = this.durationUs - timesUs[cuePointsSize - 1];
                this.cueTimesUs = null;
                this.cueClusterPositions = null;
                return new ChunkIndex(sizes, offsets, durationsUs, timesUs);
            }
        }
        this.cueTimesUs = null;
        this.cueClusterPositions = null;
        return new Unseekable(this.durationUs);
    }

    private boolean maybeSeekForCues(PositionHolder seekPosition, long currentPosition) {
        if (this.seekForCues) {
            this.seekPositionAfterBuildingCues = currentPosition;
            seekPosition.position = this.cuesContentPosition;
            this.seekForCues = false;
            return true;
        } else if (!this.sentSeekMap || this.seekPositionAfterBuildingCues == -1) {
            return false;
        } else {
            seekPosition.position = this.seekPositionAfterBuildingCues;
            this.seekPositionAfterBuildingCues = -1;
            return true;
        }
    }

    private long scaleTimecodeToUs(long unscaledTimecode) throws ParserException {
        if (this.timecodeScale == C0539C.TIME_UNSET) {
            throw new ParserException("Can't scale timecode prior to timecodeScale being set.");
        }
        return Util.scaleLargeTimestamp(unscaledTimecode, this.timecodeScale, SUBRIP_TIMECODE_LAST_VALUE_SCALING_FACTOR);
    }

    private static boolean isCodecSupported(String codecId) {
        if (!(CODEC_ID_VP8.equals(codecId) || CODEC_ID_VP9.equals(codecId) || CODEC_ID_MPEG2.equals(codecId) || CODEC_ID_MPEG4_SP.equals(codecId) || CODEC_ID_MPEG4_ASP.equals(codecId) || CODEC_ID_MPEG4_AP.equals(codecId) || CODEC_ID_H264.equals(codecId) || CODEC_ID_H265.equals(codecId) || CODEC_ID_FOURCC.equals(codecId) || CODEC_ID_THEORA.equals(codecId) || CODEC_ID_OPUS.equals(codecId) || CODEC_ID_VORBIS.equals(codecId) || CODEC_ID_AAC.equals(codecId) || CODEC_ID_MP2.equals(codecId) || CODEC_ID_MP3.equals(codecId) || CODEC_ID_AC3.equals(codecId) || CODEC_ID_E_AC3.equals(codecId) || CODEC_ID_TRUEHD.equals(codecId) || CODEC_ID_DTS.equals(codecId) || CODEC_ID_DTS_EXPRESS.equals(codecId) || CODEC_ID_DTS_LOSSLESS.equals(codecId) || CODEC_ID_FLAC.equals(codecId) || CODEC_ID_ACM.equals(codecId) || CODEC_ID_PCM_INT_LIT.equals(codecId) || CODEC_ID_SUBRIP.equals(codecId) || CODEC_ID_ASS.equals(codecId) || CODEC_ID_VOBSUB.equals(codecId) || CODEC_ID_PGS.equals(codecId))) {
            if (!CODEC_ID_DVBSUB.equals(codecId)) {
                return false;
            }
        }
        return true;
    }

    private static int[] ensureArrayCapacity(int[] array, int length) {
        if (array == null) {
            return new int[length];
        }
        if (array.length >= length) {
            return array;
        }
        return new int[Math.max(array.length * 2, length)];
    }
}

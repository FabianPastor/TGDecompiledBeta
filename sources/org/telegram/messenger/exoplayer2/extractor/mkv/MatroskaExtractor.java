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
import org.telegram.messenger.exoplayer2.C0542C;
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
    public static final ExtractorsFactory FACTORY = new C18381();
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
        public void initializeOutput(ExtractorOutput extractorOutput, int i) throws ParserException {
            int i2;
            String str = this.codecId;
            int i3 = 0;
            int i4 = 3;
            switch (str.hashCode()) {
                case -2095576542:
                    if (str.equals(MatroskaExtractor.CODEC_ID_MPEG4_AP)) {
                        i2 = 5;
                        break;
                    }
                case -2095575984:
                    if (str.equals(MatroskaExtractor.CODEC_ID_MPEG4_SP)) {
                        i2 = 3;
                        break;
                    }
                case -1985379776:
                    if (str.equals(MatroskaExtractor.CODEC_ID_ACM)) {
                        i2 = 22;
                        break;
                    }
                case -1784763192:
                    if (str.equals(MatroskaExtractor.CODEC_ID_TRUEHD)) {
                        i2 = 17;
                        break;
                    }
                case -1730367663:
                    if (str.equals(MatroskaExtractor.CODEC_ID_VORBIS)) {
                        i2 = 10;
                        break;
                    }
                case -1482641358:
                    if (str.equals(MatroskaExtractor.CODEC_ID_MP2)) {
                        i2 = 13;
                        break;
                    }
                case -1482641357:
                    if (str.equals(MatroskaExtractor.CODEC_ID_MP3)) {
                        i2 = 14;
                        break;
                    }
                case -1373388978:
                    if (str.equals(MatroskaExtractor.CODEC_ID_FOURCC)) {
                        i2 = 8;
                        break;
                    }
                case -933872740:
                    if (str.equals(MatroskaExtractor.CODEC_ID_DVBSUB)) {
                        i2 = 28;
                        break;
                    }
                case -538363189:
                    if (str.equals(MatroskaExtractor.CODEC_ID_MPEG4_ASP)) {
                        i2 = 4;
                        break;
                    }
                case -538363109:
                    if (str.equals(MatroskaExtractor.CODEC_ID_H264)) {
                        i2 = 6;
                        break;
                    }
                case -425012669:
                    if (str.equals(MatroskaExtractor.CODEC_ID_VOBSUB)) {
                        i2 = 26;
                        break;
                    }
                case -356037306:
                    if (str.equals(MatroskaExtractor.CODEC_ID_DTS_LOSSLESS)) {
                        i2 = 20;
                        break;
                    }
                case 62923557:
                    if (str.equals(MatroskaExtractor.CODEC_ID_AAC)) {
                        i2 = 12;
                        break;
                    }
                case 62923603:
                    if (str.equals(MatroskaExtractor.CODEC_ID_AC3)) {
                        i2 = 15;
                        break;
                    }
                case 62927045:
                    if (str.equals(MatroskaExtractor.CODEC_ID_DTS)) {
                        i2 = 18;
                        break;
                    }
                case 82338133:
                    if (str.equals(MatroskaExtractor.CODEC_ID_VP8)) {
                        i2 = 0;
                        break;
                    }
                case 82338134:
                    if (str.equals(MatroskaExtractor.CODEC_ID_VP9)) {
                        i2 = 1;
                        break;
                    }
                case 99146302:
                    if (str.equals(MatroskaExtractor.CODEC_ID_PGS)) {
                        i2 = 27;
                        break;
                    }
                case 444813526:
                    if (str.equals(MatroskaExtractor.CODEC_ID_THEORA)) {
                        i2 = 9;
                        break;
                    }
                case 542569478:
                    if (str.equals(MatroskaExtractor.CODEC_ID_DTS_EXPRESS)) {
                        i2 = 19;
                        break;
                    }
                case 725957860:
                    if (str.equals(MatroskaExtractor.CODEC_ID_PCM_INT_LIT)) {
                        i2 = 23;
                        break;
                    }
                case 738597099:
                    if (str.equals(MatroskaExtractor.CODEC_ID_ASS)) {
                        i2 = 25;
                        break;
                    }
                case 855502857:
                    if (str.equals(MatroskaExtractor.CODEC_ID_H265)) {
                        i2 = 7;
                        break;
                    }
                case 1422270023:
                    if (str.equals(MatroskaExtractor.CODEC_ID_SUBRIP)) {
                        i2 = 24;
                        break;
                    }
                case 1809237540:
                    if (str.equals(MatroskaExtractor.CODEC_ID_MPEG2)) {
                        i2 = 2;
                        break;
                    }
                case 1950749482:
                    if (str.equals(MatroskaExtractor.CODEC_ID_E_AC3)) {
                        i2 = 16;
                        break;
                    }
                case 1950789798:
                    if (str.equals(MatroskaExtractor.CODEC_ID_FLAC)) {
                        i2 = 21;
                        break;
                    }
                case 1951062397:
                    if (str.equals(MatroskaExtractor.CODEC_ID_OPUS)) {
                        i2 = 11;
                        break;
                    }
                default:
            }
            i2 = -1;
            ColorInfo colorInfo = null;
            List singletonList;
            List list;
            String str2;
            int i5;
            StringBuilder stringBuilder;
            switch (i2) {
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
                    str = MimeTypes.VIDEO_MP4V;
                    if (r0.codecPrivate != null) {
                        singletonList = Collections.singletonList(r0.codecPrivate);
                        break;
                    } else {
                        singletonList = null;
                        break;
                    }
                case 6:
                    str = "video/avc";
                    AvcConfig parse = AvcConfig.parse(new ParsableByteArray(r0.codecPrivate));
                    list = parse.initializationData;
                    r0.nalUnitLengthFieldLength = parse.nalUnitLengthFieldLength;
                    break;
                case 7:
                    str = MimeTypes.VIDEO_H265;
                    HevcConfig parse2 = HevcConfig.parse(new ParsableByteArray(r0.codecPrivate));
                    list = parse2.initializationData;
                    r0.nalUnitLengthFieldLength = parse2.nalUnitLengthFieldLength;
                    break;
                case 8:
                    List parseFourCcVc1Private = parseFourCcVc1Private(new ParsableByteArray(r0.codecPrivate));
                    if (parseFourCcVc1Private != null) {
                        str2 = MimeTypes.VIDEO_VC1;
                    } else {
                        Log.w(MatroskaExtractor.TAG, "Unsupported FourCC. Setting mimeType to video/x-unknown");
                        str2 = MimeTypes.VIDEO_UNKNOWN;
                    }
                    String str3 = str2;
                    int i6 = -1;
                    int i7 = i6;
                    singletonList = parseFourCcVc1Private;
                    break;
                case 9:
                    str = MimeTypes.VIDEO_UNKNOWN;
                    break;
                case 10:
                    str = MimeTypes.AUDIO_VORBIS;
                    i5 = 8192;
                    list = parseVorbisCodecPrivate(r0.codecPrivate);
                    break;
                case 11:
                    str = MimeTypes.AUDIO_OPUS;
                    i5 = MatroskaExtractor.OPUS_MAX_INPUT_SIZE;
                    list = new ArrayList(3);
                    list.add(r0.codecPrivate);
                    list.add(ByteBuffer.allocate(8).order(ByteOrder.nativeOrder()).putLong(r0.codecDelayNs).array());
                    list.add(ByteBuffer.allocate(8).order(ByteOrder.nativeOrder()).putLong(r0.seekPreRollNs).array());
                    break;
                case 12:
                    str = MimeTypes.AUDIO_AAC;
                    singletonList = Collections.singletonList(r0.codecPrivate);
                    break;
                case 13:
                    str = MimeTypes.AUDIO_MPEG_L2;
                    break;
                case 14:
                    str = MimeTypes.AUDIO_MPEG;
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
                    singletonList = Collections.singletonList(r0.codecPrivate);
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
                    i5 = Util.getPcmEncoding(r0.audioBitDepth);
                    if (i5 == 0) {
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
                    i5 = Util.getPcmEncoding(r0.audioBitDepth);
                    if (i5 == 0) {
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
                    singletonList = Collections.singletonList(r0.codecPrivate);
                    break;
                case 27:
                    str = MimeTypes.APPLICATION_PGS;
                    break;
                case 28:
                    str = MimeTypes.APPLICATION_DVBSUBS;
                    singletonList = Collections.singletonList(new byte[]{r0.codecPrivate[0], r0.codecPrivate[1], r0.codecPrivate[2], r0.codecPrivate[3]});
                    break;
                default:
                    throw new ParserException("Unrecognized codec identifier.");
            }
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
                    byte[] bArr = new byte[25];
                    ByteBuffer wrap = ByteBuffer.wrap(bArr);
                    wrap.put((byte) 0);
                    wrap.putShort((short) ((int) ((this.primaryRChromaticityX * 50000.0f) + 0.5f)));
                    wrap.putShort((short) ((int) ((this.primaryRChromaticityY * 50000.0f) + 0.5f)));
                    wrap.putShort((short) ((int) ((this.primaryGChromaticityX * 50000.0f) + 0.5f)));
                    wrap.putShort((short) ((int) ((this.primaryGChromaticityY * 50000.0f) + 0.5f)));
                    wrap.putShort((short) ((int) ((this.primaryBChromaticityX * 50000.0f) + 0.5f)));
                    wrap.putShort((short) ((int) ((this.primaryBChromaticityY * 50000.0f) + 0.5f)));
                    wrap.putShort((short) ((int) ((this.whitePointChromaticityX * 50000.0f) + 0.5f)));
                    wrap.putShort((short) ((int) ((this.whitePointChromaticityY * 50000.0f) + 0.5f)));
                    wrap.putShort((short) ((int) (this.maxMasteringLuminance + 0.5f)));
                    wrap.putShort((short) ((int) (this.minMasteringLuminance + 0.5f)));
                    wrap.putShort((short) this.maxContentLuminance);
                    wrap.putShort((short) this.maxFrameAverageLuminance);
                    return bArr;
                }
            }
            return null;
        }

        private static java.util.List<byte[]> parseFourCcVc1Private(org.telegram.messenger.exoplayer2.util.ParsableByteArray r5) throws org.telegram.messenger.exoplayer2.ParserException {
            /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
            /*
            r0 = 16;
            r5.skipBytes(r0);	 Catch:{ ArrayIndexOutOfBoundsException -> 0x004d }
            r0 = r5.readLittleEndianUnsignedInt();	 Catch:{ ArrayIndexOutOfBoundsException -> 0x004d }
            r2 = 826496599; // 0x31435657 float:2.8425313E-9 double:4.08343576E-315;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x004d }
            r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));	 Catch:{ ArrayIndexOutOfBoundsException -> 0x004d }
            if (r4 == 0) goto L_0x0012;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x004d }
        L_0x0010:
            r5 = 0;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x004d }
            return r5;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x004d }
        L_0x0012:
            r0 = r5.getPosition();	 Catch:{ ArrayIndexOutOfBoundsException -> 0x004d }
            r0 = r0 + 20;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x004d }
            r5 = r5.data;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x004d }
        L_0x001a:
            r1 = r5.length;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x004d }
            r1 = r1 + -4;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x004d }
            if (r0 >= r1) goto L_0x0045;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x004d }
        L_0x001f:
            r1 = r5[r0];	 Catch:{ ArrayIndexOutOfBoundsException -> 0x004d }
            if (r1 != 0) goto L_0x0042;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x004d }
        L_0x0023:
            r1 = r0 + 1;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x004d }
            r1 = r5[r1];	 Catch:{ ArrayIndexOutOfBoundsException -> 0x004d }
            if (r1 != 0) goto L_0x0042;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x004d }
        L_0x0029:
            r1 = r0 + 2;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x004d }
            r1 = r5[r1];	 Catch:{ ArrayIndexOutOfBoundsException -> 0x004d }
            r2 = 1;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x004d }
            if (r1 != r2) goto L_0x0042;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x004d }
        L_0x0030:
            r1 = r0 + 3;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x004d }
            r1 = r5[r1];	 Catch:{ ArrayIndexOutOfBoundsException -> 0x004d }
            r2 = 15;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x004d }
            if (r1 != r2) goto L_0x0042;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x004d }
        L_0x0038:
            r1 = r5.length;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x004d }
            r5 = java.util.Arrays.copyOfRange(r5, r0, r1);	 Catch:{ ArrayIndexOutOfBoundsException -> 0x004d }
            r5 = java.util.Collections.singletonList(r5);	 Catch:{ ArrayIndexOutOfBoundsException -> 0x004d }
            return r5;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x004d }
        L_0x0042:
            r0 = r0 + 1;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x004d }
            goto L_0x001a;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x004d }
        L_0x0045:
            r5 = new org.telegram.messenger.exoplayer2.ParserException;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x004d }
            r0 = "Failed to find FourCC VC1 initialization data";	 Catch:{ ArrayIndexOutOfBoundsException -> 0x004d }
            r5.<init>(r0);	 Catch:{ ArrayIndexOutOfBoundsException -> 0x004d }
            throw r5;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x004d }
        L_0x004d:
            r5 = new org.telegram.messenger.exoplayer2.ParserException;
            r0 = "Error parsing FourCC VC1 codec private";
            r5.<init>(r0);
            throw r5;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.extractor.mkv.MatroskaExtractor.Track.parseFourCcVc1Private(org.telegram.messenger.exoplayer2.util.ParsableByteArray):java.util.List<byte[]>");
        }

        private static java.util.List<byte[]> parseVorbisCodecPrivate(byte[] r8) throws org.telegram.messenger.exoplayer2.ParserException {
            /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
            /*
            r0 = 0;
            r1 = r8[r0];	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
            r2 = 2;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
            if (r1 == r2) goto L_0x000e;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
        L_0x0006:
            r8 = new org.telegram.messenger.exoplayer2.ParserException;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
            r0 = "Error parsing vorbis codec private";	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
            r8.<init>(r0);	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
            throw r8;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
        L_0x000e:
            r1 = 1;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
            r4 = r0;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
            r3 = r1;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
        L_0x0011:
            r5 = r8[r3];	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
            r6 = -1;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
            if (r5 != r6) goto L_0x001b;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
        L_0x0016:
            r4 = r4 + 255;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
            r3 = r3 + 1;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
            goto L_0x0011;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
        L_0x001b:
            r5 = r3 + 1;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
            r3 = r8[r3];	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
            r4 = r4 + r3;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
            r3 = r0;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
        L_0x0021:
            r7 = r8[r5];	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
            if (r7 != r6) goto L_0x002a;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
        L_0x0025:
            r3 = r3 + 255;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
            r5 = r5 + 1;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
            goto L_0x0021;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
        L_0x002a:
            r6 = r5 + 1;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
            r5 = r8[r5];	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
            r3 = r3 + r5;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
            r5 = r8[r6];	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
            if (r5 == r1) goto L_0x003b;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
        L_0x0033:
            r8 = new org.telegram.messenger.exoplayer2.ParserException;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
            r0 = "Error parsing vorbis codec private";	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
            r8.<init>(r0);	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
            throw r8;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
        L_0x003b:
            r1 = new byte[r4];	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
            java.lang.System.arraycopy(r8, r6, r1, r0, r4);	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
            r6 = r6 + r4;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
            r4 = r8[r6];	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
            r5 = 3;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
            if (r4 == r5) goto L_0x004e;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
        L_0x0046:
            r8 = new org.telegram.messenger.exoplayer2.ParserException;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
            r0 = "Error parsing vorbis codec private";	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
            r8.<init>(r0);	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
            throw r8;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
        L_0x004e:
            r6 = r6 + r3;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
            r3 = r8[r6];	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
            r4 = 5;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
            if (r3 == r4) goto L_0x005c;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
        L_0x0054:
            r8 = new org.telegram.messenger.exoplayer2.ParserException;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
            r0 = "Error parsing vorbis codec private";	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
            r8.<init>(r0);	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
            throw r8;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
        L_0x005c:
            r3 = r8.length;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
            r3 = r3 - r6;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
            r3 = new byte[r3];	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
            r4 = r8.length;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
            r4 = r4 - r6;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
            java.lang.System.arraycopy(r8, r6, r3, r0, r4);	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
            r8 = new java.util.ArrayList;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
            r8.<init>(r2);	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
            r8.add(r1);	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
            r8.add(r3);	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0071 }
            return r8;
        L_0x0071:
            r8 = new org.telegram.messenger.exoplayer2.ParserException;
            r0 = "Error parsing vorbis codec private";
            r8.<init>(r0);
            throw r8;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.extractor.mkv.MatroskaExtractor.Track.parseVorbisCodecPrivate(byte[]):java.util.List<byte[]>");
        }

        private static boolean parseMsAcmCodecPrivate(org.telegram.messenger.exoplayer2.util.ParsableByteArray r8) throws org.telegram.messenger.exoplayer2.ParserException {
            /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
            /*
            r0 = r8.readLittleEndianUnsignedShort();	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0037 }
            r1 = 1;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0037 }
            if (r0 != r1) goto L_0x0008;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0037 }
        L_0x0007:
            return r1;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0037 }
        L_0x0008:
            r2 = 65534; // 0xfffe float:9.1833E-41 double:3.2378E-319;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0037 }
            r3 = 0;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0037 }
            if (r0 != r2) goto L_0x0036;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0037 }
        L_0x000e:
            r0 = 24;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0037 }
            r8.setPosition(r0);	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0037 }
            r4 = r8.readLong();	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0037 }
            r0 = org.telegram.messenger.exoplayer2.extractor.mkv.MatroskaExtractor.WAVE_SUBFORMAT_PCM;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0037 }
            r6 = r0.getMostSignificantBits();	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0037 }
            r0 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0037 }
            if (r0 != 0) goto L_0x0034;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0037 }
        L_0x0023:
            r4 = r8.readLong();	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0037 }
            r8 = org.telegram.messenger.exoplayer2.extractor.mkv.MatroskaExtractor.WAVE_SUBFORMAT_PCM;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0037 }
            r6 = r8.getLeastSignificantBits();	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0037 }
            r8 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
            if (r8 != 0) goto L_0x0034;
        L_0x0033:
            goto L_0x0035;
        L_0x0034:
            r1 = r3;
        L_0x0035:
            return r1;
        L_0x0036:
            return r3;
        L_0x0037:
            r8 = new org.telegram.messenger.exoplayer2.ParserException;
            r0 = "Error parsing MS/ACM codec private";
            r8.<init>(r0);
            throw r8;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.extractor.mkv.MatroskaExtractor.Track.parseMsAcmCodecPrivate(org.telegram.messenger.exoplayer2.util.ParsableByteArray):boolean");
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

        public void startSample(ExtractorInput extractorInput, int i, int i2) throws IOException, InterruptedException {
            if (!this.foundSyncframe) {
                extractorInput.peekFully(this.syncframePrefix, 0, 12);
                extractorInput.resetPeekPosition();
                if (Ac3Util.parseTrueHdSyncframeAudioSampleCount(this.syncframePrefix) != -1) {
                    this.foundSyncframe = true;
                    this.sampleCount = 0;
                } else {
                    return;
                }
            }
            if (this.sampleCount == null) {
                this.blockFlags = i;
                this.chunkSize = 0;
            }
            this.chunkSize += i2;
        }

        public void sampleMetadata(Track track, long j) {
            if (this.foundSyncframe) {
                int i = this.sampleCount;
                this.sampleCount = i + 1;
                if (i == 0) {
                    this.timeUs = j;
                }
                if (this.sampleCount >= 8) {
                    track.output.sampleMetadata(this.timeUs, this.blockFlags, this.chunkSize, 0, track.cryptoData);
                    this.sampleCount = null;
                }
            }
        }

        public void outputPendingSampleMetadata(Track track) {
            if (this.foundSyncframe && this.sampleCount > 0) {
                track.output.sampleMetadata(this.timeUs, this.blockFlags, this.chunkSize, 0, track.cryptoData);
                this.sampleCount = null;
            }
        }
    }

    /* renamed from: org.telegram.messenger.exoplayer2.extractor.mkv.MatroskaExtractor$1 */
    static class C18381 implements ExtractorsFactory {
        C18381() {
        }

        public Extractor[] createExtractors() {
            return new Extractor[]{new MatroskaExtractor()};
        }
    }

    private final class InnerEbmlReaderOutput implements EbmlReaderOutput {
        private InnerEbmlReaderOutput() {
        }

        public int getElementType(int i) {
            return MatroskaExtractor.this.getElementType(i);
        }

        public boolean isLevel1Element(int i) {
            return MatroskaExtractor.this.isLevel1Element(i);
        }

        public void startMasterElement(int i, long j, long j2) throws ParserException {
            MatroskaExtractor.this.startMasterElement(i, j, j2);
        }

        public void endMasterElement(int i) throws ParserException {
            MatroskaExtractor.this.endMasterElement(i);
        }

        public void integerElement(int i, long j) throws ParserException {
            MatroskaExtractor.this.integerElement(i, j);
        }

        public void floatElement(int i, double d) throws ParserException {
            MatroskaExtractor.this.floatElement(i, d);
        }

        public void stringElement(int i, String str) throws ParserException {
            MatroskaExtractor.this.stringElement(i, str);
        }

        public void binaryElement(int i, int i2, ExtractorInput extractorInput) throws IOException, InterruptedException {
            MatroskaExtractor.this.binaryElement(i, i2, extractorInput);
        }
    }

    int getElementType(int i) {
        switch (i) {
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

    boolean isLevel1Element(int i) {
        if (!(i == 357149030 || i == ID_CLUSTER || i == ID_CUES)) {
            if (i != ID_TRACKS) {
                return false;
            }
        }
        return true;
    }

    public void release() {
    }

    public MatroskaExtractor() {
        this(0);
    }

    public MatroskaExtractor(int i) {
        this(new DefaultEbmlReader(), i);
    }

    MatroskaExtractor(EbmlReader ebmlReader, int i) {
        this.segmentContentPosition = -1;
        this.timecodeScale = C0542C.TIME_UNSET;
        this.durationTimecode = C0542C.TIME_UNSET;
        this.durationUs = C0542C.TIME_UNSET;
        this.cuesContentPosition = -1;
        this.seekPositionAfterBuildingCues = -1;
        this.clusterTimecodeUs = C0542C.TIME_UNSET;
        this.reader = ebmlReader;
        this.reader.init(new InnerEbmlReaderOutput());
        ebmlReader = true;
        if ((i & 1) != 0) {
            ebmlReader = null;
        }
        this.seekForCuesEnabled = ebmlReader;
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

    public boolean sniff(ExtractorInput extractorInput) throws IOException, InterruptedException {
        return new Sniffer().sniff(extractorInput);
    }

    public void init(ExtractorOutput extractorOutput) {
        this.extractorOutput = extractorOutput;
    }

    public void seek(long j, long j2) {
        this.clusterTimecodeUs = C0542C.TIME_UNSET;
        j = null;
        this.blockState = 0;
        this.reader.reset();
        this.varintReader.reset();
        resetSample();
        while (j < this.tracks.size()) {
            ((Track) this.tracks.valueAt(j)).reset();
            j++;
        }
    }

    public int read(ExtractorInput extractorInput, PositionHolder positionHolder) throws IOException, InterruptedException {
        int i = 0;
        this.sampleRead = false;
        boolean z = true;
        while (z && !this.sampleRead) {
            z = this.reader.read(extractorInput);
            if (z && maybeSeekForCues(positionHolder, extractorInput.getPosition())) {
                return 1;
            }
        }
        if (z) {
            return 0;
        }
        while (i < this.tracks.size()) {
            ((Track) this.tracks.valueAt(i)).outputPendingSampleMetadata();
            i++;
        }
        return -1;
    }

    void startMasterElement(int i, long j, long j2) throws ParserException {
        if (i == ID_BLOCK_GROUP) {
            this.sampleSeenReferenceBlock = false;
        } else if (i == ID_TRACK_ENTRY) {
            this.currentTrack = new Track();
        } else if (i == ID_CUE_POINT) {
            this.seenClusterPositionForCurrentCuePoint = false;
        } else if (i == ID_SEEK) {
            this.seekEntryId = -1;
            this.seekEntryPosition = -1;
        } else if (i == ID_CONTENT_ENCRYPTION) {
            this.currentTrack.hasContentEncryption = true;
        } else if (i == ID_MASTERING_METADATA) {
            this.currentTrack.hasColorInfo = true;
        } else if (i == ID_CONTENT_ENCODING) {
        } else {
            if (i != ID_SEGMENT) {
                if (i == 475249515) {
                    this.cueTimesUs = new LongArray();
                    this.cueClusterPositions = new LongArray();
                } else if (i == 524531317) {
                    if (this.sentSeekMap != 0) {
                        return;
                    }
                    if (this.seekForCuesEnabled == 0 || this.cuesContentPosition == -1) {
                        this.extractorOutput.seekMap(new Unseekable(this.durationUs));
                        this.sentSeekMap = true;
                        return;
                    }
                    this.seekForCues = true;
                }
            } else if (this.segmentContentPosition == -1 || this.segmentContentPosition == j) {
                this.segmentContentPosition = j;
                this.segmentContentSize = j2;
            } else {
                throw new ParserException("Multiple Segment elements not supported");
            }
        }
    }

    void endMasterElement(int i) throws ParserException {
        if (i != ID_BLOCK_GROUP) {
            if (i == ID_TRACK_ENTRY) {
                if (isCodecSupported(this.currentTrack.codecId) != 0) {
                    this.currentTrack.initializeOutput(this.extractorOutput, this.currentTrack.number);
                    this.tracks.put(this.currentTrack.number, this.currentTrack);
                }
                this.currentTrack = 0;
            } else if (i == ID_SEEK) {
                if (this.seekEntryId != -1) {
                    if (this.seekEntryPosition != -1) {
                        if (this.seekEntryId == ID_CUES) {
                            this.cuesContentPosition = this.seekEntryPosition;
                        }
                    }
                }
                throw new ParserException("Mandatory element SeekID or SeekPosition not found");
            } else if (i != ID_CONTENT_ENCODING) {
                if (i != ID_CONTENT_ENCODINGS) {
                    if (i == 357149030) {
                        if (this.timecodeScale == C0542C.TIME_UNSET) {
                            this.timecodeScale = C0542C.MICROS_PER_SECOND;
                        }
                        if (this.durationTimecode != C0542C.TIME_UNSET) {
                            this.durationUs = scaleTimecodeToUs(this.durationTimecode);
                        }
                    } else if (i != ID_TRACKS) {
                        if (i == ID_CUES) {
                            if (this.sentSeekMap == 0) {
                                this.extractorOutput.seekMap(buildSeekMap());
                                this.sentSeekMap = true;
                            }
                        }
                    } else if (this.tracks.size() == 0) {
                        throw new ParserException("No valid tracks were found");
                    } else {
                        this.extractorOutput.endTracks();
                    }
                } else if (!(this.currentTrack.hasContentEncryption == 0 || this.currentTrack.sampleStrippedBytes == 0)) {
                    throw new ParserException("Combining encryption and compression is not supported");
                }
            } else if (this.currentTrack.hasContentEncryption != 0) {
                if (this.currentTrack.cryptoData == 0) {
                    throw new ParserException("Encrypted Track found but ContentEncKeyID was not found");
                }
                this.currentTrack.drmInitData = new DrmInitData(new SchemeData(C0542C.UUID_NIL, MimeTypes.VIDEO_WEBM, this.currentTrack.cryptoData.encryptionKey));
            }
        } else if (this.blockState == 2) {
            if (this.sampleSeenReferenceBlock == 0) {
                this.blockFlags |= 1;
            }
            commitSampleToOutput((Track) this.tracks.get(this.blockTrackNumber), this.blockTimeUs);
            this.blockState = 0;
        }
    }

    void integerElement(int i, long j) throws ParserException {
        boolean z = false;
        StringBuilder stringBuilder;
        switch (i) {
            case ID_TRACK_TYPE /*131*/:
                this.currentTrack.type = (int) j;
                return;
            case ID_FLAG_DEFAULT /*136*/:
                i = this.currentTrack;
                if (j == 1) {
                    z = true;
                }
                i.flagForced = z;
                return;
            case ID_BLOCK_DURATION /*155*/:
                this.blockDurationUs = scaleTimecodeToUs(j);
                return;
            case ID_CHANNELS /*159*/:
                this.currentTrack.channelCount = (int) j;
                return;
            case ID_PIXEL_WIDTH /*176*/:
                this.currentTrack.width = (int) j;
                return;
            case ID_CUE_TIME /*179*/:
                this.cueTimesUs.add(scaleTimecodeToUs(j));
                return;
            case ID_PIXEL_HEIGHT /*186*/:
                this.currentTrack.height = (int) j;
                return;
            case ID_TRACK_NUMBER /*215*/:
                this.currentTrack.number = (int) j;
                return;
            case ID_TIME_CODE /*231*/:
                this.clusterTimecodeUs = scaleTimecodeToUs(j);
                return;
            case ID_CUE_CLUSTER_POSITION /*241*/:
                if (this.seenClusterPositionForCurrentCuePoint == 0) {
                    this.cueClusterPositions.add(j);
                    this.seenClusterPositionForCurrentCuePoint = true;
                    return;
                }
                return;
            case ID_REFERENCE_BLOCK /*251*/:
                this.sampleSeenReferenceBlock = true;
                return;
            case ID_CONTENT_COMPRESSION_ALGORITHM /*16980*/:
                if (j != 3) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("ContentCompAlgo ");
                    stringBuilder.append(j);
                    stringBuilder.append(" not supported");
                    throw new ParserException(stringBuilder.toString());
                }
                return;
            case ID_DOC_TYPE_READ_VERSION /*17029*/:
                if (j < 1 || j > 2) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("DocTypeReadVersion ");
                    stringBuilder.append(j);
                    stringBuilder.append(" not supported");
                    throw new ParserException(stringBuilder.toString());
                }
                return;
            case ID_EBML_READ_VERSION /*17143*/:
                if (j != 1) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("EBMLReadVersion ");
                    stringBuilder.append(j);
                    stringBuilder.append(" not supported");
                    throw new ParserException(stringBuilder.toString());
                }
                return;
            case ID_CONTENT_ENCRYPTION_ALGORITHM /*18401*/:
                if (j != 5) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("ContentEncAlgo ");
                    stringBuilder.append(j);
                    stringBuilder.append(" not supported");
                    throw new ParserException(stringBuilder.toString());
                }
                return;
            case ID_CONTENT_ENCRYPTION_AES_SETTINGS_CIPHER_MODE /*18408*/:
                if (j != 1) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("AESSettingsCipherMode ");
                    stringBuilder.append(j);
                    stringBuilder.append(" not supported");
                    throw new ParserException(stringBuilder.toString());
                }
                return;
            case ID_CONTENT_ENCODING_ORDER /*20529*/:
                if (j != 0) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("ContentEncodingOrder ");
                    stringBuilder.append(j);
                    stringBuilder.append(" not supported");
                    throw new ParserException(stringBuilder.toString());
                }
                return;
            case ID_CONTENT_ENCODING_SCOPE /*20530*/:
                if (j != 1) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("ContentEncodingScope ");
                    stringBuilder.append(j);
                    stringBuilder.append(" not supported");
                    throw new ParserException(stringBuilder.toString());
                }
                return;
            case ID_SEEK_POSITION /*21420*/:
                this.seekEntryPosition = j + this.segmentContentPosition;
                return;
            case ID_STEREO_MODE /*21432*/:
                i = (int) j;
                if (i == 3) {
                    this.currentTrack.stereoMode = 1;
                    return;
                } else if (i != 15) {
                    switch (i) {
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
                this.currentTrack.displayWidth = (int) j;
                return;
            case ID_DISPLAY_UNIT /*21682*/:
                this.currentTrack.displayUnit = (int) j;
                return;
            case ID_DISPLAY_HEIGHT /*21690*/:
                this.currentTrack.displayHeight = (int) j;
                return;
            case ID_FLAG_FORCED /*21930*/:
                i = this.currentTrack;
                if (j == 1) {
                    z = true;
                }
                i.flagDefault = z;
                return;
            case ID_COLOUR_RANGE /*21945*/:
                switch ((int) j) {
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
                i = (int) j;
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
                i = (int) j;
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
                this.currentTrack.maxContentLuminance = (int) j;
                return;
            case ID_MAX_FALL /*21949*/:
                this.currentTrack.maxFrameAverageLuminance = (int) j;
                return;
            case ID_CODEC_DELAY /*22186*/:
                this.currentTrack.codecDelayNs = j;
                return;
            case ID_SEEK_PRE_ROLL /*22203*/:
                this.currentTrack.seekPreRollNs = j;
                return;
            case ID_AUDIO_BIT_DEPTH /*25188*/:
                this.currentTrack.audioBitDepth = (int) j;
                return;
            case ID_DEFAULT_DURATION /*2352003*/:
                this.currentTrack.defaultSampleDurationNs = (int) j;
                return;
            case ID_TIMECODE_SCALE /*2807729*/:
                this.timecodeScale = j;
                return;
            default:
                return;
        }
    }

    void floatElement(int i, double d) {
        if (i == ID_SAMPLING_FREQUENCY) {
            this.currentTrack.sampleRate = (int) d;
        } else if (i != ID_DURATION) {
            switch (i) {
                case ID_PRIMARY_R_CHROMATICITY_X /*21969*/:
                    this.currentTrack.primaryRChromaticityX = (float) d;
                    return;
                case ID_PRIMARY_R_CHROMATICITY_Y /*21970*/:
                    this.currentTrack.primaryRChromaticityY = (float) d;
                    return;
                case ID_PRIMARY_G_CHROMATICITY_X /*21971*/:
                    this.currentTrack.primaryGChromaticityX = (float) d;
                    return;
                case ID_PRIMARY_G_CHROMATICITY_Y /*21972*/:
                    this.currentTrack.primaryGChromaticityY = (float) d;
                    return;
                case ID_PRIMARY_B_CHROMATICITY_X /*21973*/:
                    this.currentTrack.primaryBChromaticityX = (float) d;
                    return;
                case ID_PRIMARY_B_CHROMATICITY_Y /*21974*/:
                    this.currentTrack.primaryBChromaticityY = (float) d;
                    return;
                case ID_WHITE_POINT_CHROMATICITY_X /*21975*/:
                    this.currentTrack.whitePointChromaticityX = (float) d;
                    return;
                case ID_WHITE_POINT_CHROMATICITY_Y /*21976*/:
                    this.currentTrack.whitePointChromaticityY = (float) d;
                    return;
                case ID_LUMNINANCE_MAX /*21977*/:
                    this.currentTrack.maxMasteringLuminance = (float) d;
                    return;
                case ID_LUMNINANCE_MIN /*21978*/:
                    this.currentTrack.minMasteringLuminance = (float) d;
                    return;
                default:
                    return;
            }
        } else {
            this.durationTimecode = (long) d;
        }
    }

    void stringElement(int i, String str) throws ParserException {
        if (i == 134) {
            this.currentTrack.codecId = str;
        } else if (i != ID_DOC_TYPE) {
            if (i == ID_LANGUAGE) {
                this.currentTrack.language = str;
            }
        } else if (DOC_TYPE_WEBM.equals(str) == 0 && DOC_TYPE_MATROSKA.equals(str) == 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("DocType ");
            stringBuilder.append(str);
            stringBuilder.append(" not supported");
            throw new ParserException(stringBuilder.toString());
        }
    }

    void binaryElement(int i, int i2, ExtractorInput extractorInput) throws IOException, InterruptedException {
        MatroskaExtractor matroskaExtractor = this;
        int i3 = i;
        int i4 = i2;
        ExtractorInput extractorInput2 = extractorInput;
        int i5 = 4;
        boolean z = false;
        int i6 = 1;
        if (i3 == ID_BLOCK || i3 == ID_SIMPLE_BLOCK) {
            if (matroskaExtractor.blockState == 0) {
                matroskaExtractor.blockTrackNumber = (int) matroskaExtractor.varintReader.readUnsignedVarint(extractorInput2, false, true, 8);
                matroskaExtractor.blockTrackNumberLength = matroskaExtractor.varintReader.getLastLength();
                matroskaExtractor.blockDurationUs = C0542C.TIME_UNSET;
                matroskaExtractor.blockState = 1;
                matroskaExtractor.scratch.reset();
            }
            Track track = (Track) matroskaExtractor.tracks.get(matroskaExtractor.blockTrackNumber);
            if (track == null) {
                extractorInput2.skipFully(i4 - matroskaExtractor.blockTrackNumberLength);
                matroskaExtractor.blockState = 0;
                return;
            }
            if (matroskaExtractor.blockState == 1) {
                int i7;
                readScratch(extractorInput2, 3);
                int i8 = (matroskaExtractor.scratch.data[2] & 6) >> 1;
                if (i8 == 0) {
                    matroskaExtractor.blockLacingSampleCount = 1;
                    matroskaExtractor.blockLacingSampleSizes = ensureArrayCapacity(matroskaExtractor.blockLacingSampleSizes, 1);
                    matroskaExtractor.blockLacingSampleSizes[0] = (i4 - matroskaExtractor.blockTrackNumberLength) - 3;
                } else if (i3 != ID_SIMPLE_BLOCK) {
                    throw new ParserException("Lacing only supported in SimpleBlocks.");
                } else {
                    readScratch(extractorInput2, 4);
                    matroskaExtractor.blockLacingSampleCount = (matroskaExtractor.scratch.data[3] & 255) + 1;
                    matroskaExtractor.blockLacingSampleSizes = ensureArrayCapacity(matroskaExtractor.blockLacingSampleSizes, matroskaExtractor.blockLacingSampleCount);
                    if (i8 == 2) {
                        Arrays.fill(matroskaExtractor.blockLacingSampleSizes, 0, matroskaExtractor.blockLacingSampleCount, ((i4 - matroskaExtractor.blockTrackNumberLength) - 4) / matroskaExtractor.blockLacingSampleCount);
                    } else if (i8 == 1) {
                        i7 = 0;
                        r10 = i7;
                        while (i7 < matroskaExtractor.blockLacingSampleCount - 1) {
                            matroskaExtractor.blockLacingSampleSizes[i7] = 0;
                            do {
                                i5++;
                                readScratch(extractorInput2, i5);
                                i8 = matroskaExtractor.scratch.data[i5 - 1] & 255;
                                int[] iArr = matroskaExtractor.blockLacingSampleSizes;
                                iArr[i7] = iArr[i7] + i8;
                            } while (i8 == 255);
                            r10 += matroskaExtractor.blockLacingSampleSizes[i7];
                            i7++;
                        }
                        matroskaExtractor.blockLacingSampleSizes[matroskaExtractor.blockLacingSampleCount - 1] = ((i4 - matroskaExtractor.blockTrackNumberLength) - i5) - r10;
                    } else if (i8 == 3) {
                        i7 = 0;
                        r10 = i7;
                        while (i7 < matroskaExtractor.blockLacingSampleCount - i6) {
                            matroskaExtractor.blockLacingSampleSizes[i7] = z;
                            i5++;
                            readScratch(extractorInput2, i5);
                            int i9 = i5 - 1;
                            if (matroskaExtractor.scratch.data[i9] == (byte) 0) {
                                throw new ParserException("No valid varint length mask found");
                            }
                            int i10;
                            long j;
                            int[] iArr2;
                            long j2 = 0;
                            i8 = z;
                            while (i8 < 8) {
                                int i11 = i6 << (7 - i8);
                                if ((matroskaExtractor.scratch.data[i9] & i11) != 0) {
                                    i5 += i8;
                                    readScratch(extractorInput2, i5);
                                    i10 = i9 + 1;
                                    j2 = (long) ((matroskaExtractor.scratch.data[i9] & 255) & (i11 ^ -1));
                                    while (i10 < i5) {
                                        long j3 = (j2 << 8) | ((long) (matroskaExtractor.scratch.data[i10] & 255));
                                        i10++;
                                        j2 = j3;
                                    }
                                    if (i7 > 0) {
                                        j = j2 - ((1 << ((i8 * 7) + 6)) - 1);
                                        if (j >= -2147483648L) {
                                            if (j > 2147483647L) {
                                                i10 = (int) j;
                                                iArr2 = matroskaExtractor.blockLacingSampleSizes;
                                                if (i7 == 0) {
                                                    i10 += matroskaExtractor.blockLacingSampleSizes[i7 - 1];
                                                }
                                                iArr2[i7] = i10;
                                                r10 += matroskaExtractor.blockLacingSampleSizes[i7];
                                                i7++;
                                                z = false;
                                                i6 = 1;
                                            }
                                        }
                                        throw new ParserException("EBML lacing sample size out of range.");
                                    }
                                    j = j2;
                                    if (j >= -2147483648L) {
                                        if (j > 2147483647L) {
                                            i10 = (int) j;
                                            iArr2 = matroskaExtractor.blockLacingSampleSizes;
                                            if (i7 == 0) {
                                                i10 += matroskaExtractor.blockLacingSampleSizes[i7 - 1];
                                            }
                                            iArr2[i7] = i10;
                                            r10 += matroskaExtractor.blockLacingSampleSizes[i7];
                                            i7++;
                                            z = false;
                                            i6 = 1;
                                        }
                                    }
                                    throw new ParserException("EBML lacing sample size out of range.");
                                }
                                i8++;
                                i6 = 1;
                            }
                            j = j2;
                            if (j >= -2147483648L) {
                                if (j > 2147483647L) {
                                    i10 = (int) j;
                                    iArr2 = matroskaExtractor.blockLacingSampleSizes;
                                    if (i7 == 0) {
                                        i10 += matroskaExtractor.blockLacingSampleSizes[i7 - 1];
                                    }
                                    iArr2[i7] = i10;
                                    r10 += matroskaExtractor.blockLacingSampleSizes[i7];
                                    i7++;
                                    z = false;
                                    i6 = 1;
                                }
                            }
                            throw new ParserException("EBML lacing sample size out of range.");
                        }
                        matroskaExtractor.blockLacingSampleSizes[matroskaExtractor.blockLacingSampleCount - 1] = ((i4 - matroskaExtractor.blockTrackNumberLength) - i5) - r10;
                    } else {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("Unexpected lacing value: ");
                        stringBuilder.append(i8);
                        throw new ParserException(stringBuilder.toString());
                    }
                }
                matroskaExtractor.blockTimeUs = matroskaExtractor.clusterTimecodeUs + scaleTimecodeToUs((long) ((matroskaExtractor.scratch.data[0] << 8) | (matroskaExtractor.scratch.data[1] & 255)));
                Object obj = (matroskaExtractor.scratch.data[2] & 8) == 8 ? 1 : null;
                if (track.type != 2) {
                    if (i3 != ID_SIMPLE_BLOCK || (matroskaExtractor.scratch.data[2] & 128) != 128) {
                        i7 = 0;
                        matroskaExtractor.blockFlags = i7 | (obj == null ? Integer.MIN_VALUE : 0);
                        matroskaExtractor.blockState = 2;
                        matroskaExtractor.blockLacingSampleIndex = 0;
                        i4 = ID_SIMPLE_BLOCK;
                    }
                }
                i7 = 1;
                if (obj == null) {
                }
                matroskaExtractor.blockFlags = i7 | (obj == null ? Integer.MIN_VALUE : 0);
                matroskaExtractor.blockState = 2;
                matroskaExtractor.blockLacingSampleIndex = 0;
                i4 = ID_SIMPLE_BLOCK;
            } else {
                i4 = ID_SIMPLE_BLOCK;
            }
            if (i3 == i4) {
                while (matroskaExtractor.blockLacingSampleIndex < matroskaExtractor.blockLacingSampleCount) {
                    writeSampleData(extractorInput2, track, matroskaExtractor.blockLacingSampleSizes[matroskaExtractor.blockLacingSampleIndex]);
                    commitSampleToOutput(track, matroskaExtractor.blockTimeUs + ((long) ((matroskaExtractor.blockLacingSampleIndex * track.defaultSampleDurationNs) / 1000)));
                    matroskaExtractor.blockLacingSampleIndex++;
                }
                matroskaExtractor.blockState = 0;
            } else {
                writeSampleData(extractorInput2, track, matroskaExtractor.blockLacingSampleSizes[0]);
            }
        } else if (i3 == ID_CONTENT_COMPRESSION_SETTINGS) {
            matroskaExtractor.currentTrack.sampleStrippedBytes = new byte[i4];
            extractorInput2.readFully(matroskaExtractor.currentTrack.sampleStrippedBytes, 0, i4);
        } else if (i3 == ID_CONTENT_ENCRYPTION_KEY_ID) {
            byte[] bArr = new byte[i4];
            extractorInput2.readFully(bArr, 0, i4);
            matroskaExtractor.currentTrack.cryptoData = new CryptoData(1, bArr, 0, 0);
        } else if (i3 == ID_SEEK_ID) {
            Arrays.fill(matroskaExtractor.seekEntryIdBytes.data, (byte) 0);
            extractorInput2.readFully(matroskaExtractor.seekEntryIdBytes.data, 4 - i4, i4);
            matroskaExtractor.seekEntryIdBytes.setPosition(0);
            matroskaExtractor.seekEntryId = (int) matroskaExtractor.seekEntryIdBytes.readUnsignedInt();
        } else if (i3 == ID_CODEC_PRIVATE) {
            matroskaExtractor.currentTrack.codecPrivate = new byte[i4];
            extractorInput2.readFully(matroskaExtractor.currentTrack.codecPrivate, 0, i4);
        } else if (i3 != ID_PROJECTION_PRIVATE) {
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("Unexpected id: ");
            stringBuilder2.append(i3);
            throw new ParserException(stringBuilder2.toString());
        } else {
            matroskaExtractor.currentTrack.projectionData = new byte[i4];
            extractorInput2.readFully(matroskaExtractor.currentTrack.projectionData, 0, i4);
        }
    }

    private void commitSampleToOutput(Track track, long j) {
        MatroskaExtractor matroskaExtractor = this;
        Track track2 = track;
        if (track2.trueHdSampleRechunker != null) {
            track2.trueHdSampleRechunker.sampleMetadata(track2, j);
        } else {
            long j2 = j;
            if (CODEC_ID_SUBRIP.equals(track2.codecId)) {
                commitSubtitleSample(track2, SUBRIP_TIMECODE_FORMAT, 19, SUBRIP_TIMECODE_LAST_VALUE_SCALING_FACTOR, SUBRIP_TIMECODE_EMPTY);
            } else if (CODEC_ID_ASS.equals(track2.codecId)) {
                commitSubtitleSample(track2, SSA_TIMECODE_FORMAT, 21, SSA_TIMECODE_LAST_VALUE_SCALING_FACTOR, SSA_TIMECODE_EMPTY);
            }
            track2.output.sampleMetadata(j2, matroskaExtractor.blockFlags, matroskaExtractor.sampleBytesWritten, 0, track2.cryptoData);
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

    private void readScratch(ExtractorInput extractorInput, int i) throws IOException, InterruptedException {
        if (this.scratch.limit() < i) {
            if (this.scratch.capacity() < i) {
                this.scratch.reset(Arrays.copyOf(this.scratch.data, Math.max(this.scratch.data.length * 2, i)), this.scratch.limit());
            }
            extractorInput.readFully(this.scratch.data, this.scratch.limit(), i - this.scratch.limit());
            this.scratch.setLimit(i);
        }
    }

    private void writeSampleData(ExtractorInput extractorInput, Track track, int i) throws IOException, InterruptedException {
        if (CODEC_ID_SUBRIP.equals(track.codecId)) {
            writeSubtitleSampleData(extractorInput, SUBRIP_PREFIX, i);
        } else if (CODEC_ID_ASS.equals(track.codecId)) {
            writeSubtitleSampleData(extractorInput, SSA_PREFIX, i);
        } else {
            TrackOutput trackOutput = track.output;
            boolean z = true;
            if (!this.sampleEncodingHandled) {
                if (track.hasContentEncryption) {
                    this.blockFlags &= -NUM;
                    int i2 = 128;
                    if (!this.sampleSignalByteRead) {
                        extractorInput.readFully(this.scratch.data, 0, 1);
                        this.sampleBytesRead++;
                        if ((this.scratch.data[0] & 128) == 128) {
                            throw new ParserException("Extension bit is set in signal byte");
                        }
                        this.sampleSignalByte = this.scratch.data[0];
                        this.sampleSignalByteRead = true;
                    }
                    if ((this.sampleSignalByte & 1) == 1) {
                        boolean z2 = (this.sampleSignalByte & 2) == 2;
                        this.blockFlags |= NUM;
                        if (!this.sampleInitializationVectorRead) {
                            extractorInput.readFully(this.encryptionInitializationVector.data, 0, 8);
                            this.sampleBytesRead += 8;
                            this.sampleInitializationVectorRead = true;
                            byte[] bArr = this.scratch.data;
                            if (!z2) {
                                i2 = 0;
                            }
                            bArr[0] = (byte) (i2 | 8);
                            this.scratch.setPosition(0);
                            trackOutput.sampleData(this.scratch, 1);
                            this.sampleBytesWritten++;
                            this.encryptionInitializationVector.setPosition(0);
                            trackOutput.sampleData(this.encryptionInitializationVector, 8);
                            this.sampleBytesWritten += 8;
                        }
                        if (z2) {
                            if (!this.samplePartitionCountRead) {
                                extractorInput.readFully(this.scratch.data, 0, 1);
                                this.sampleBytesRead++;
                                this.scratch.setPosition(0);
                                this.samplePartitionCount = this.scratch.readUnsignedByte();
                                this.samplePartitionCountRead = true;
                            }
                            int i3 = this.samplePartitionCount * 4;
                            this.scratch.reset(i3);
                            extractorInput.readFully(this.scratch.data, 0, i3);
                            this.sampleBytesRead += i3;
                            short s = (short) ((this.samplePartitionCount / 2) + 1);
                            i2 = (6 * s) + 2;
                            if (this.encryptionSubsampleDataBuffer == null || this.encryptionSubsampleDataBuffer.capacity() < i2) {
                                this.encryptionSubsampleDataBuffer = ByteBuffer.allocate(i2);
                            }
                            this.encryptionSubsampleDataBuffer.position(0);
                            this.encryptionSubsampleDataBuffer.putShort(s);
                            i3 = 0;
                            int i4 = i3;
                            while (i3 < this.samplePartitionCount) {
                                int readUnsignedIntToInt = this.scratch.readUnsignedIntToInt();
                                if (i3 % 2 == 0) {
                                    this.encryptionSubsampleDataBuffer.putShort((short) (readUnsignedIntToInt - i4));
                                } else {
                                    this.encryptionSubsampleDataBuffer.putInt(readUnsignedIntToInt - i4);
                                }
                                i3++;
                                i4 = readUnsignedIntToInt;
                            }
                            i3 = (i - this.sampleBytesRead) - i4;
                            if (this.samplePartitionCount % 2 == 1) {
                                this.encryptionSubsampleDataBuffer.putInt(i3);
                            } else {
                                this.encryptionSubsampleDataBuffer.putShort((short) i3);
                                this.encryptionSubsampleDataBuffer.putInt(0);
                            }
                            this.encryptionSubsampleData.reset(this.encryptionSubsampleDataBuffer.array(), i2);
                            trackOutput.sampleData(this.encryptionSubsampleData, i2);
                            this.sampleBytesWritten += i2;
                        }
                    }
                } else if (track.sampleStrippedBytes != null) {
                    this.sampleStrippedBytes.reset(track.sampleStrippedBytes, track.sampleStrippedBytes.length);
                }
                this.sampleEncodingHandled = true;
            }
            i += this.sampleStrippedBytes.limit();
            if (!CODEC_ID_H264.equals(track.codecId)) {
                if (!CODEC_ID_H265.equals(track.codecId)) {
                    if (track.trueHdSampleRechunker != null) {
                        if (this.sampleStrippedBytes.limit() != 0) {
                            z = false;
                        }
                        Assertions.checkState(z);
                        track.trueHdSampleRechunker.startSample(extractorInput, this.blockFlags, i);
                    }
                    while (this.sampleBytesRead < i) {
                        readToOutput(extractorInput, trackOutput, i - this.sampleBytesRead);
                    }
                    if (CODEC_ID_VORBIS.equals(track.codecId) != null) {
                        this.vorbisNumPageSamples.setPosition(0);
                        trackOutput.sampleData(this.vorbisNumPageSamples, 4);
                        this.sampleBytesWritten += 4;
                    }
                }
            }
            byte[] bArr2 = this.nalLength.data;
            bArr2[0] = (byte) 0;
            bArr2[1] = (byte) 0;
            bArr2[2] = (byte) 0;
            int i5 = track.nalUnitLengthFieldLength;
            int i6 = 4 - track.nalUnitLengthFieldLength;
            while (this.sampleBytesRead < i) {
                if (this.sampleCurrentNalBytesRemaining == 0) {
                    readToTarget(extractorInput, bArr2, i6, i5);
                    this.nalLength.setPosition(0);
                    this.sampleCurrentNalBytesRemaining = this.nalLength.readUnsignedIntToInt();
                    this.nalStartCode.setPosition(0);
                    trackOutput.sampleData(this.nalStartCode, 4);
                    this.sampleBytesWritten += 4;
                } else {
                    this.sampleCurrentNalBytesRemaining -= readToOutput(extractorInput, trackOutput, this.sampleCurrentNalBytesRemaining);
                }
            }
            if (CODEC_ID_VORBIS.equals(track.codecId) != null) {
                this.vorbisNumPageSamples.setPosition(0);
                trackOutput.sampleData(this.vorbisNumPageSamples, 4);
                this.sampleBytesWritten += 4;
            }
        }
    }

    private void writeSubtitleSampleData(ExtractorInput extractorInput, byte[] bArr, int i) throws IOException, InterruptedException {
        int length = bArr.length + i;
        if (this.subtitleSample.capacity() < length) {
            this.subtitleSample.data = Arrays.copyOf(bArr, length + i);
        } else {
            System.arraycopy(bArr, 0, this.subtitleSample.data, 0, bArr.length);
        }
        extractorInput.readFully(this.subtitleSample.data, bArr.length, i);
        this.subtitleSample.reset(length);
    }

    private void commitSubtitleSample(Track track, String str, int i, long j, byte[] bArr) {
        setSampleDuration(this.subtitleSample.data, this.blockDurationUs, str, i, j, bArr);
        track.output.sampleData(this.subtitleSample, this.subtitleSample.limit());
        this.sampleBytesWritten += this.subtitleSample.limit();
    }

    private static void setSampleDuration(byte[] bArr, long j, String str, int i, long j2, byte[] bArr2) {
        Object obj;
        Object obj2;
        if (j == C0542C.TIME_UNSET) {
            obj = bArr2;
            obj2 = obj;
        } else {
            long j3 = j - (((long) (((int) (j / 3600000000L)) * 3600)) * C0542C.MICROS_PER_SECOND);
            long j4 = j3 - (((long) (((int) (j3 / 60000000)) * 60)) * C0542C.MICROS_PER_SECOND);
            int i2 = (int) ((j4 - (((long) ((int) (j4 / C0542C.MICROS_PER_SECOND))) * C0542C.MICROS_PER_SECOND)) / j2);
            obj2 = Util.getUtf8Bytes(String.format(Locale.US, str, new Object[]{Integer.valueOf(r3), Integer.valueOf(r0), Integer.valueOf(r1), Integer.valueOf(i2)}));
            obj = bArr2;
        }
        System.arraycopy(obj2, 0, bArr, i, obj.length);
    }

    private void readToTarget(ExtractorInput extractorInput, byte[] bArr, int i, int i2) throws IOException, InterruptedException {
        int min = Math.min(i2, this.sampleStrippedBytes.bytesLeft());
        extractorInput.readFully(bArr, i + min, i2 - min);
        if (min > 0) {
            this.sampleStrippedBytes.readBytes(bArr, i, min);
        }
        this.sampleBytesRead += i2;
    }

    private int readToOutput(ExtractorInput extractorInput, TrackOutput trackOutput, int i) throws IOException, InterruptedException {
        int bytesLeft = this.sampleStrippedBytes.bytesLeft();
        if (bytesLeft > 0) {
            extractorInput = Math.min(i, bytesLeft);
            trackOutput.sampleData(this.sampleStrippedBytes, extractorInput);
        } else {
            extractorInput = trackOutput.sampleData(extractorInput, i, false);
        }
        this.sampleBytesRead += extractorInput;
        this.sampleBytesWritten += extractorInput;
        return extractorInput;
    }

    private SeekMap buildSeekMap() {
        if (!(this.segmentContentPosition == -1 || this.durationUs == C0542C.TIME_UNSET || this.cueTimesUs == null || this.cueTimesUs.size() == 0 || this.cueClusterPositions == null)) {
            if (this.cueClusterPositions.size() == this.cueTimesUs.size()) {
                int i;
                int size = this.cueTimesUs.size();
                int[] iArr = new int[size];
                long[] jArr = new long[size];
                long[] jArr2 = new long[size];
                long[] jArr3 = new long[size];
                int i2 = 0;
                for (i = 0; i < size; i++) {
                    jArr3[i] = this.cueTimesUs.get(i);
                    jArr[i] = this.segmentContentPosition + this.cueClusterPositions.get(i);
                }
                while (true) {
                    i = size - 1;
                    if (i2 < i) {
                        i = i2 + 1;
                        iArr[i2] = (int) (jArr[i] - jArr[i2]);
                        jArr2[i2] = jArr3[i] - jArr3[i2];
                        i2 = i;
                    } else {
                        iArr[i] = (int) ((this.segmentContentPosition + this.segmentContentSize) - jArr[i]);
                        jArr2[i] = this.durationUs - jArr3[i];
                        this.cueTimesUs = null;
                        this.cueClusterPositions = null;
                        return new ChunkIndex(iArr, jArr, jArr2, jArr3);
                    }
                }
            }
        }
        this.cueTimesUs = null;
        this.cueClusterPositions = null;
        return new Unseekable(this.durationUs);
    }

    private boolean maybeSeekForCues(PositionHolder positionHolder, long j) {
        if (this.seekForCues) {
            this.seekPositionAfterBuildingCues = j;
            positionHolder.position = this.cuesContentPosition;
            this.seekForCues = false;
            return true;
        } else if (this.sentSeekMap == null || this.seekPositionAfterBuildingCues == -1) {
            return false;
        } else {
            positionHolder.position = this.seekPositionAfterBuildingCues;
            this.seekPositionAfterBuildingCues = -1;
            return true;
        }
    }

    private long scaleTimecodeToUs(long j) throws ParserException {
        if (this.timecodeScale == C0542C.TIME_UNSET) {
            throw new ParserException("Can't scale timecode prior to timecodeScale being set.");
        }
        return Util.scaleLargeTimestamp(j, this.timecodeScale, SUBRIP_TIMECODE_LAST_VALUE_SCALING_FACTOR);
    }

    private static boolean isCodecSupported(String str) {
        if (!(CODEC_ID_VP8.equals(str) || CODEC_ID_VP9.equals(str) || CODEC_ID_MPEG2.equals(str) || CODEC_ID_MPEG4_SP.equals(str) || CODEC_ID_MPEG4_ASP.equals(str) || CODEC_ID_MPEG4_AP.equals(str) || CODEC_ID_H264.equals(str) || CODEC_ID_H265.equals(str) || CODEC_ID_FOURCC.equals(str) || CODEC_ID_THEORA.equals(str) || CODEC_ID_OPUS.equals(str) || CODEC_ID_VORBIS.equals(str) || CODEC_ID_AAC.equals(str) || CODEC_ID_MP2.equals(str) || CODEC_ID_MP3.equals(str) || CODEC_ID_AC3.equals(str) || CODEC_ID_E_AC3.equals(str) || CODEC_ID_TRUEHD.equals(str) || CODEC_ID_DTS.equals(str) || CODEC_ID_DTS_EXPRESS.equals(str) || CODEC_ID_DTS_LOSSLESS.equals(str) || CODEC_ID_FLAC.equals(str) || CODEC_ID_ACM.equals(str) || CODEC_ID_PCM_INT_LIT.equals(str) || CODEC_ID_SUBRIP.equals(str) || CODEC_ID_ASS.equals(str) || CODEC_ID_VOBSUB.equals(str) || CODEC_ID_PGS.equals(str))) {
            if (CODEC_ID_DVBSUB.equals(str) == null) {
                return null;
            }
        }
        return true;
    }

    private static int[] ensureArrayCapacity(int[] iArr, int i) {
        if (iArr == null) {
            return new int[i];
        }
        if (iArr.length >= i) {
            return iArr;
        }
        return new int[Math.max(iArr.length * 2, i)];
    }
}

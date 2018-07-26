package org.telegram.messenger.exoplayer2.extractor.mkv;

import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.telegram.messenger.exoplayer2.C0559C;
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
    public static final ExtractorsFactory FACTORY = new C20011();
    public static final int FLAG_DISABLE_SEEK_FOR_CUES = 1;
    private static final int FOURCC_COMPRESSION_DIVX = NUM;
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

        public void initializeOutput(org.telegram.messenger.exoplayer2.extractor.ExtractorOutput r46, int r47) throws org.telegram.messenger.exoplayer2.ParserException {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unknown predecessor block by arg (r26_1 'colorInfo' org.telegram.messenger.exoplayer2.video.ColorInfo) in PHI: PHI: (r26_2 'colorInfo' org.telegram.messenger.exoplayer2.video.ColorInfo) = (r26_0 'colorInfo' org.telegram.messenger.exoplayer2.video.ColorInfo), (r26_1 'colorInfo' org.telegram.messenger.exoplayer2.video.ColorInfo) binds: {(r26_0 'colorInfo' org.telegram.messenger.exoplayer2.video.ColorInfo)=B:159:0x0445, (r26_1 'colorInfo' org.telegram.messenger.exoplayer2.video.ColorInfo)=B:160:0x0447}
	at jadx.core.dex.instructions.PhiInsn.replaceArg(PhiInsn.java:79)
	at jadx.core.dex.visitors.ModVisitor.processInvoke(ModVisitor.java:222)
	at jadx.core.dex.visitors.ModVisitor.replaceStep(ModVisitor.java:83)
	at jadx.core.dex.visitors.ModVisitor.visit(ModVisitor.java:68)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
            /*
            r45 = this;
            r6 = -1;
            r9 = -1;
            r10 = 0;
            r0 = r45;
            r4 = r0.codecId;
            r2 = -1;
            r5 = r4.hashCode();
            switch(r5) {
                case -2095576542: goto L_0x0052;
                case -2095575984: goto L_0x003c;
                case -1985379776: goto L_0x0127;
                case -1784763192: goto L_0x00e6;
                case -1730367663: goto L_0x008b;
                case -1482641358: goto L_0x00b2;
                case -1482641357: goto L_0x00bf;
                case -1373388978: goto L_0x0073;
                case -933872740: goto L_0x0175;
                case -538363189: goto L_0x0047;
                case -538363109: goto L_0x005d;
                case -425012669: goto L_0x015b;
                case -356037306: goto L_0x010d;
                case 62923557: goto L_0x00a5;
                case 62923603: goto L_0x00cc;
                case 62927045: goto L_0x00f3;
                case 82338133: goto L_0x001b;
                case 82338134: goto L_0x0026;
                case 99146302: goto L_0x0168;
                case 444813526: goto L_0x007f;
                case 542569478: goto L_0x0100;
                case 725957860: goto L_0x0134;
                case 738597099: goto L_0x014e;
                case 855502857: goto L_0x0068;
                case 1422270023: goto L_0x0141;
                case 1809237540: goto L_0x0031;
                case 1950749482: goto L_0x00d9;
                case 1950789798: goto L_0x011a;
                case 1951062397: goto L_0x0098;
                default: goto L_0x000f;
            };
        L_0x000f:
            switch(r2) {
                case 0: goto L_0x0182;
                case 1: goto L_0x01d2;
                case 2: goto L_0x01d6;
                case 3: goto L_0x01da;
                case 4: goto L_0x01da;
                case 5: goto L_0x01da;
                case 6: goto L_0x01ee;
                case 7: goto L_0x020c;
                case 8: goto L_0x022a;
                case 9: goto L_0x0245;
                case 10: goto L_0x024a;
                case 11: goto L_0x0259;
                case 12: goto L_0x02a7;
                case 13: goto L_0x02b4;
                case 14: goto L_0x02bb;
                case 15: goto L_0x02c2;
                case 16: goto L_0x02c7;
                case 17: goto L_0x02cc;
                case 18: goto L_0x02da;
                case 19: goto L_0x02da;
                case 20: goto L_0x02df;
                case 21: goto L_0x02e4;
                case 22: goto L_0x02f1;
                case 23: goto L_0x035b;
                case 24: goto L_0x0397;
                case 25: goto L_0x039c;
                case 26: goto L_0x03a1;
                case 27: goto L_0x03ae;
                case 28: goto L_0x03b3;
                default: goto L_0x0012;
            };
        L_0x0012:
            r2 = new org.telegram.messenger.exoplayer2.ParserException;
            r4 = "Unrecognized codec identifier.";
            r2.<init>(r4);
            throw r2;
        L_0x001b:
            r5 = "V_VP8";
            r4 = r4.equals(r5);
            if (r4 == 0) goto L_0x000f;
        L_0x0024:
            r2 = 0;
            goto L_0x000f;
        L_0x0026:
            r5 = "V_VP9";
            r4 = r4.equals(r5);
            if (r4 == 0) goto L_0x000f;
        L_0x002f:
            r2 = 1;
            goto L_0x000f;
        L_0x0031:
            r5 = "V_MPEG2";
            r4 = r4.equals(r5);
            if (r4 == 0) goto L_0x000f;
        L_0x003a:
            r2 = 2;
            goto L_0x000f;
        L_0x003c:
            r5 = "V_MPEG4/ISO/SP";
            r4 = r4.equals(r5);
            if (r4 == 0) goto L_0x000f;
        L_0x0045:
            r2 = 3;
            goto L_0x000f;
        L_0x0047:
            r5 = "V_MPEG4/ISO/ASP";
            r4 = r4.equals(r5);
            if (r4 == 0) goto L_0x000f;
        L_0x0050:
            r2 = 4;
            goto L_0x000f;
        L_0x0052:
            r5 = "V_MPEG4/ISO/AP";
            r4 = r4.equals(r5);
            if (r4 == 0) goto L_0x000f;
        L_0x005b:
            r2 = 5;
            goto L_0x000f;
        L_0x005d:
            r5 = "V_MPEG4/ISO/AVC";
            r4 = r4.equals(r5);
            if (r4 == 0) goto L_0x000f;
        L_0x0066:
            r2 = 6;
            goto L_0x000f;
        L_0x0068:
            r5 = "V_MPEGH/ISO/HEVC";
            r4 = r4.equals(r5);
            if (r4 == 0) goto L_0x000f;
        L_0x0071:
            r2 = 7;
            goto L_0x000f;
        L_0x0073:
            r5 = "V_MS/VFW/FOURCC";
            r4 = r4.equals(r5);
            if (r4 == 0) goto L_0x000f;
        L_0x007c:
            r2 = 8;
            goto L_0x000f;
        L_0x007f:
            r5 = "V_THEORA";
            r4 = r4.equals(r5);
            if (r4 == 0) goto L_0x000f;
        L_0x0088:
            r2 = 9;
            goto L_0x000f;
        L_0x008b:
            r5 = "A_VORBIS";
            r4 = r4.equals(r5);
            if (r4 == 0) goto L_0x000f;
        L_0x0094:
            r2 = 10;
            goto L_0x000f;
        L_0x0098:
            r5 = "A_OPUS";
            r4 = r4.equals(r5);
            if (r4 == 0) goto L_0x000f;
        L_0x00a1:
            r2 = 11;
            goto L_0x000f;
        L_0x00a5:
            r5 = "A_AAC";
            r4 = r4.equals(r5);
            if (r4 == 0) goto L_0x000f;
        L_0x00ae:
            r2 = 12;
            goto L_0x000f;
        L_0x00b2:
            r5 = "A_MPEG/L2";
            r4 = r4.equals(r5);
            if (r4 == 0) goto L_0x000f;
        L_0x00bb:
            r2 = 13;
            goto L_0x000f;
        L_0x00bf:
            r5 = "A_MPEG/L3";
            r4 = r4.equals(r5);
            if (r4 == 0) goto L_0x000f;
        L_0x00c8:
            r2 = 14;
            goto L_0x000f;
        L_0x00cc:
            r5 = "A_AC3";
            r4 = r4.equals(r5);
            if (r4 == 0) goto L_0x000f;
        L_0x00d5:
            r2 = 15;
            goto L_0x000f;
        L_0x00d9:
            r5 = "A_EAC3";
            r4 = r4.equals(r5);
            if (r4 == 0) goto L_0x000f;
        L_0x00e2:
            r2 = 16;
            goto L_0x000f;
        L_0x00e6:
            r5 = "A_TRUEHD";
            r4 = r4.equals(r5);
            if (r4 == 0) goto L_0x000f;
        L_0x00ef:
            r2 = 17;
            goto L_0x000f;
        L_0x00f3:
            r5 = "A_DTS";
            r4 = r4.equals(r5);
            if (r4 == 0) goto L_0x000f;
        L_0x00fc:
            r2 = 18;
            goto L_0x000f;
        L_0x0100:
            r5 = "A_DTS/EXPRESS";
            r4 = r4.equals(r5);
            if (r4 == 0) goto L_0x000f;
        L_0x0109:
            r2 = 19;
            goto L_0x000f;
        L_0x010d:
            r5 = "A_DTS/LOSSLESS";
            r4 = r4.equals(r5);
            if (r4 == 0) goto L_0x000f;
        L_0x0116:
            r2 = 20;
            goto L_0x000f;
        L_0x011a:
            r5 = "A_FLAC";
            r4 = r4.equals(r5);
            if (r4 == 0) goto L_0x000f;
        L_0x0123:
            r2 = 21;
            goto L_0x000f;
        L_0x0127:
            r5 = "A_MS/ACM";
            r4 = r4.equals(r5);
            if (r4 == 0) goto L_0x000f;
        L_0x0130:
            r2 = 22;
            goto L_0x000f;
        L_0x0134:
            r5 = "A_PCM/INT/LIT";
            r4 = r4.equals(r5);
            if (r4 == 0) goto L_0x000f;
        L_0x013d:
            r2 = 23;
            goto L_0x000f;
        L_0x0141:
            r5 = "S_TEXT/UTF8";
            r4 = r4.equals(r5);
            if (r4 == 0) goto L_0x000f;
        L_0x014a:
            r2 = 24;
            goto L_0x000f;
        L_0x014e:
            r5 = "S_TEXT/ASS";
            r4 = r4.equals(r5);
            if (r4 == 0) goto L_0x000f;
        L_0x0157:
            r2 = 25;
            goto L_0x000f;
        L_0x015b:
            r5 = "S_VOBSUB";
            r4 = r4.equals(r5);
            if (r4 == 0) goto L_0x000f;
        L_0x0164:
            r2 = 26;
            goto L_0x000f;
        L_0x0168:
            r5 = "S_HDMV/PGS";
            r4 = r4.equals(r5);
            if (r4 == 0) goto L_0x000f;
        L_0x0171:
            r2 = 27;
            goto L_0x000f;
        L_0x0175:
            r5 = "S_DVBSUB";
            r4 = r4.equals(r5);
            if (r4 == 0) goto L_0x000f;
        L_0x017e:
            r2 = 28;
            goto L_0x000f;
        L_0x0182:
            r3 = "video/x-vnd.on2.vp8";
        L_0x0185:
            r12 = 0;
            r0 = r45;
            r2 = r0.flagDefault;
            if (r2 == 0) goto L_0x03e7;
        L_0x018c:
            r2 = 1;
        L_0x018d:
            r12 = r12 | r2;
            r0 = r45;
            r2 = r0.flagForced;
            if (r2 == 0) goto L_0x03ea;
        L_0x0194:
            r2 = 2;
        L_0x0195:
            r12 = r12 | r2;
            r2 = org.telegram.messenger.exoplayer2.util.MimeTypes.isAudio(r3);
            if (r2 == 0) goto L_0x03ed;
        L_0x019c:
            r44 = 1;
            r2 = java.lang.Integer.toString(r47);
            r4 = 0;
            r5 = -1;
            r0 = r45;
            r7 = r0.channelCount;
            r0 = r45;
            r8 = r0.sampleRate;
            r0 = r45;
            r11 = r0.drmInitData;
            r0 = r45;
            r13 = r0.language;
            r40 = org.telegram.messenger.exoplayer2.Format.createAudioSampleFormat(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13);
        L_0x01b8:
            r0 = r45;
            r2 = r0.number;
            r0 = r46;
            r1 = r44;
            r2 = r0.track(r2, r1);
            r0 = r45;
            r0.output = r2;
            r0 = r45;
            r2 = r0.output;
            r0 = r40;
            r2.format(r0);
            return;
        L_0x01d2:
            r3 = "video/x-vnd.on2.vp9";
            goto L_0x0185;
        L_0x01d6:
            r3 = "video/mpeg2";
            goto L_0x0185;
        L_0x01da:
            r3 = "video/mp4v-es";
            r0 = r45;
            r2 = r0.codecPrivate;
            if (r2 != 0) goto L_0x01e5;
        L_0x01e3:
            r10 = 0;
        L_0x01e4:
            goto L_0x0185;
        L_0x01e5:
            r0 = r45;
            r2 = r0.codecPrivate;
            r10 = java.util.Collections.singletonList(r2);
            goto L_0x01e4;
        L_0x01ee:
            r3 = "video/avc";
            r2 = new org.telegram.messenger.exoplayer2.util.ParsableByteArray;
            r0 = r45;
            r4 = r0.codecPrivate;
            r2.<init>(r4);
            r39 = org.telegram.messenger.exoplayer2.video.AvcConfig.parse(r2);
            r0 = r39;
            r10 = r0.initializationData;
            r0 = r39;
            r2 = r0.nalUnitLengthFieldLength;
            r0 = r45;
            r0.nalUnitLengthFieldLength = r2;
            goto L_0x0185;
        L_0x020c:
            r3 = "video/hevc";
            r2 = new org.telegram.messenger.exoplayer2.util.ParsableByteArray;
            r0 = r45;
            r4 = r0.codecPrivate;
            r2.<init>(r4);
            r42 = org.telegram.messenger.exoplayer2.video.HevcConfig.parse(r2);
            r0 = r42;
            r10 = r0.initializationData;
            r0 = r42;
            r2 = r0.nalUnitLengthFieldLength;
            r0 = r45;
            r0.nalUnitLengthFieldLength = r2;
            goto L_0x0185;
        L_0x022a:
            r2 = new org.telegram.messenger.exoplayer2.util.ParsableByteArray;
            r0 = r45;
            r4 = r0.codecPrivate;
            r2.<init>(r4);
            r43 = parseFourCcPrivate(r2);
            r0 = r43;
            r3 = r0.first;
            r3 = (java.lang.String) r3;
            r0 = r43;
            r10 = r0.second;
            r10 = (java.util.List) r10;
            goto L_0x0185;
        L_0x0245:
            r3 = "video/x-unknown";
            goto L_0x0185;
        L_0x024a:
            r3 = "audio/vorbis";
            r6 = 8192; // 0x2000 float:1.14794E-41 double:4.0474E-320;
            r0 = r45;
            r2 = r0.codecPrivate;
            r10 = parseVorbisCodecPrivate(r2);
            goto L_0x0185;
        L_0x0259:
            r3 = "audio/opus";
            r6 = 5760; // 0x1680 float:8.071E-42 double:2.846E-320;
            r10 = new java.util.ArrayList;
            r2 = 3;
            r10.<init>(r2);
            r0 = r45;
            r2 = r0.codecPrivate;
            r10.add(r2);
            r2 = 8;
            r2 = java.nio.ByteBuffer.allocate(r2);
            r4 = java.nio.ByteOrder.nativeOrder();
            r2 = r2.order(r4);
            r0 = r45;
            r4 = r0.codecDelayNs;
            r2 = r2.putLong(r4);
            r2 = r2.array();
            r10.add(r2);
            r2 = 8;
            r2 = java.nio.ByteBuffer.allocate(r2);
            r4 = java.nio.ByteOrder.nativeOrder();
            r2 = r2.order(r4);
            r0 = r45;
            r4 = r0.seekPreRollNs;
            r2 = r2.putLong(r4);
            r2 = r2.array();
            r10.add(r2);
            goto L_0x0185;
        L_0x02a7:
            r3 = "audio/mp4a-latm";
            r0 = r45;
            r2 = r0.codecPrivate;
            r10 = java.util.Collections.singletonList(r2);
            goto L_0x0185;
        L_0x02b4:
            r3 = "audio/mpeg-L2";
            r6 = 4096; // 0x1000 float:5.74E-42 double:2.0237E-320;
            goto L_0x0185;
        L_0x02bb:
            r3 = "audio/mpeg";
            r6 = 4096; // 0x1000 float:5.74E-42 double:2.0237E-320;
            goto L_0x0185;
        L_0x02c2:
            r3 = "audio/ac3";
            goto L_0x0185;
        L_0x02c7:
            r3 = "audio/eac3";
            goto L_0x0185;
        L_0x02cc:
            r3 = "audio/true-hd";
            r2 = new org.telegram.messenger.exoplayer2.extractor.mkv.MatroskaExtractor$TrueHdSampleRechunker;
            r2.<init>();
            r0 = r45;
            r0.trueHdSampleRechunker = r2;
            goto L_0x0185;
        L_0x02da:
            r3 = "audio/vnd.dts";
            goto L_0x0185;
        L_0x02df:
            r3 = "audio/vnd.dts.hd";
            goto L_0x0185;
        L_0x02e4:
            r3 = "audio/flac";
            r0 = r45;
            r2 = r0.codecPrivate;
            r10 = java.util.Collections.singletonList(r2);
            goto L_0x0185;
        L_0x02f1:
            r3 = "audio/raw";
            r2 = new org.telegram.messenger.exoplayer2.util.ParsableByteArray;
            r0 = r45;
            r4 = r0.codecPrivate;
            r2.<init>(r4);
            r2 = parseMsAcmCodecPrivate(r2);
            if (r2 == 0) goto L_0x033c;
        L_0x0303:
            r0 = r45;
            r2 = r0.audioBitDepth;
            r9 = org.telegram.messenger.exoplayer2.util.Util.getPcmEncoding(r2);
            if (r9 != 0) goto L_0x0185;
        L_0x030d:
            r9 = -1;
            r3 = "audio/x-unknown";
            r2 = "MatroskaExtractor";
            r4 = new java.lang.StringBuilder;
            r4.<init>();
            r5 = "Unsupported PCM bit depth: ";
            r4 = r4.append(r5);
            r0 = r45;
            r5 = r0.audioBitDepth;
            r4 = r4.append(r5);
            r5 = ". Setting mimeType to ";
            r4 = r4.append(r5);
            r4 = r4.append(r3);
            r4 = r4.toString();
            android.util.Log.w(r2, r4);
            goto L_0x0185;
        L_0x033c:
            r3 = "audio/x-unknown";
            r2 = "MatroskaExtractor";
            r4 = new java.lang.StringBuilder;
            r4.<init>();
            r5 = "Non-PCM MS/ACM is unsupported. Setting mimeType to ";
            r4 = r4.append(r5);
            r4 = r4.append(r3);
            r4 = r4.toString();
            android.util.Log.w(r2, r4);
            goto L_0x0185;
        L_0x035b:
            r3 = "audio/raw";
            r0 = r45;
            r2 = r0.audioBitDepth;
            r9 = org.telegram.messenger.exoplayer2.util.Util.getPcmEncoding(r2);
            if (r9 != 0) goto L_0x0185;
        L_0x0368:
            r9 = -1;
            r3 = "audio/x-unknown";
            r2 = "MatroskaExtractor";
            r4 = new java.lang.StringBuilder;
            r4.<init>();
            r5 = "Unsupported PCM bit depth: ";
            r4 = r4.append(r5);
            r0 = r45;
            r5 = r0.audioBitDepth;
            r4 = r4.append(r5);
            r5 = ". Setting mimeType to ";
            r4 = r4.append(r5);
            r4 = r4.append(r3);
            r4 = r4.toString();
            android.util.Log.w(r2, r4);
            goto L_0x0185;
        L_0x0397:
            r3 = "application/x-subrip";
            goto L_0x0185;
        L_0x039c:
            r3 = "text/x-ssa";
            goto L_0x0185;
        L_0x03a1:
            r3 = "application/vobsub";
            r0 = r45;
            r2 = r0.codecPrivate;
            r10 = java.util.Collections.singletonList(r2);
            goto L_0x0185;
        L_0x03ae:
            r3 = "application/pgs";
            goto L_0x0185;
        L_0x03b3:
            r3 = "application/dvbsubs";
            r2 = 4;
            r2 = new byte[r2];
            r4 = 0;
            r0 = r45;
            r5 = r0.codecPrivate;
            r7 = 0;
            r5 = r5[r7];
            r2[r4] = r5;
            r4 = 1;
            r0 = r45;
            r5 = r0.codecPrivate;
            r7 = 1;
            r5 = r5[r7];
            r2[r4] = r5;
            r4 = 2;
            r0 = r45;
            r5 = r0.codecPrivate;
            r7 = 2;
            r5 = r5[r7];
            r2[r4] = r5;
            r4 = 3;
            r0 = r45;
            r5 = r0.codecPrivate;
            r7 = 3;
            r5 = r5[r7];
            r2[r4] = r5;
            r10 = java.util.Collections.singletonList(r2);
            goto L_0x0185;
        L_0x03e7:
            r2 = 0;
            goto L_0x018d;
        L_0x03ea:
            r2 = 0;
            goto L_0x0195;
        L_0x03ed:
            r2 = org.telegram.messenger.exoplayer2.util.MimeTypes.isVideo(r3);
            if (r2 == 0) goto L_0x04a0;
        L_0x03f3:
            r44 = 2;
            r0 = r45;
            r2 = r0.displayUnit;
            if (r2 != 0) goto L_0x0419;
        L_0x03fb:
            r0 = r45;
            r2 = r0.displayWidth;
            r4 = -1;
            if (r2 != r4) goto L_0x0494;
        L_0x0402:
            r0 = r45;
            r2 = r0.width;
        L_0x0406:
            r0 = r45;
            r0.displayWidth = r2;
            r0 = r45;
            r2 = r0.displayHeight;
            r4 = -1;
            if (r2 != r4) goto L_0x049a;
        L_0x0411:
            r0 = r45;
            r2 = r0.height;
        L_0x0415:
            r0 = r45;
            r0.displayHeight = r2;
        L_0x0419:
            r23 = -NUM; // 0xffffffffbf800000 float:-1.0 double:NaN;
            r0 = r45;
            r2 = r0.displayWidth;
            r4 = -1;
            if (r2 == r4) goto L_0x043f;
        L_0x0422:
            r0 = r45;
            r2 = r0.displayHeight;
            r4 = -1;
            if (r2 == r4) goto L_0x043f;
        L_0x0429:
            r0 = r45;
            r2 = r0.height;
            r0 = r45;
            r4 = r0.displayWidth;
            r2 = r2 * r4;
            r2 = (float) r2;
            r0 = r45;
            r4 = r0.width;
            r0 = r45;
            r5 = r0.displayHeight;
            r4 = r4 * r5;
            r4 = (float) r4;
            r23 = r2 / r4;
        L_0x043f:
            r26 = 0;
            r0 = r45;
            r2 = r0.hasColorInfo;
            if (r2 == 0) goto L_0x0460;
        L_0x0447:
            r41 = r45.getHdrStaticInfo();
            r26 = new org.telegram.messenger.exoplayer2.video.ColorInfo;
            r0 = r45;
            r2 = r0.colorSpace;
            r0 = r45;
            r4 = r0.colorRange;
            r0 = r45;
            r5 = r0.colorTransfer;
            r0 = r26;
            r1 = r41;
            r0.<init>(r2, r4, r5, r1);
        L_0x0460:
            r13 = java.lang.Integer.toString(r47);
            r15 = 0;
            r16 = -1;
            r0 = r45;
            r0 = r0.width;
            r18 = r0;
            r0 = r45;
            r0 = r0.height;
            r19 = r0;
            r20 = -NUM; // 0xffffffffbf800000 float:-1.0 double:NaN;
            r22 = -1;
            r0 = r45;
            r0 = r0.projectionData;
            r24 = r0;
            r0 = r45;
            r0 = r0.stereoMode;
            r25 = r0;
            r0 = r45;
            r0 = r0.drmInitData;
            r27 = r0;
            r14 = r3;
            r17 = r6;
            r21 = r10;
            r40 = org.telegram.messenger.exoplayer2.Format.createVideoSampleFormat(r13, r14, r15, r16, r17, r18, r19, r20, r21, r22, r23, r24, r25, r26, r27);
            goto L_0x01b8;
        L_0x0494:
            r0 = r45;
            r2 = r0.displayWidth;
            goto L_0x0406;
        L_0x049a:
            r0 = r45;
            r2 = r0.displayHeight;
            goto L_0x0415;
        L_0x04a0:
            r2 = "application/x-subrip";
            r2 = r2.equals(r3);
            if (r2 == 0) goto L_0x04bd;
        L_0x04a9:
            r44 = 3;
            r2 = java.lang.Integer.toString(r47);
            r0 = r45;
            r4 = r0.language;
            r0 = r45;
            r5 = r0.drmInitData;
            r40 = org.telegram.messenger.exoplayer2.Format.createTextSampleFormat(r2, r3, r12, r4, r5);
            goto L_0x01b8;
        L_0x04bd:
            r2 = "text/x-ssa";
            r2 = r2.equals(r3);
            if (r2 == 0) goto L_0x0503;
        L_0x04c6:
            r44 = 3;
            r10 = new java.util.ArrayList;
            r2 = 2;
            r10.<init>(r2);
            r2 = org.telegram.messenger.exoplayer2.extractor.mkv.MatroskaExtractor.SSA_DIALOGUE_FORMAT;
            r10.add(r2);
            r0 = r45;
            r2 = r0.codecPrivate;
            r10.add(r2);
            r28 = java.lang.Integer.toString(r47);
            r30 = 0;
            r31 = -1;
            r0 = r45;
            r0 = r0.language;
            r33 = r0;
            r34 = -1;
            r0 = r45;
            r0 = r0.drmInitData;
            r35 = r0;
            r36 = 922337203NUM; // 0x7fffffffffffffff float:NaN double:NaN;
            r29 = r3;
            r32 = r12;
            r38 = r10;
            r40 = org.telegram.messenger.exoplayer2.Format.createTextSampleFormat(r28, r29, r30, r31, r32, r33, r34, r35, r36, r38);
            goto L_0x01b8;
        L_0x0503:
            r2 = "application/vobsub";
            r2 = r2.equals(r3);
            if (r2 != 0) goto L_0x051e;
        L_0x050c:
            r2 = "application/pgs";
            r2 = r2.equals(r3);
            if (r2 != 0) goto L_0x051e;
        L_0x0515:
            r2 = "application/dvbsubs";
            r2 = r2.equals(r3);
            if (r2 == 0) goto L_0x053e;
        L_0x051e:
            r44 = 3;
            r13 = java.lang.Integer.toString(r47);
            r15 = 0;
            r16 = -1;
            r0 = r45;
            r0 = r0.language;
            r19 = r0;
            r0 = r45;
            r0 = r0.drmInitData;
            r20 = r0;
            r14 = r3;
            r17 = r12;
            r18 = r10;
            r40 = org.telegram.messenger.exoplayer2.Format.createImageSampleFormat(r13, r14, r15, r16, r17, r18, r19, r20);
            goto L_0x01b8;
        L_0x053e:
            r2 = new org.telegram.messenger.exoplayer2.ParserException;
            r4 = "Unexpected MIME type.";
            r2.<init>(r4);
            throw r2;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.extractor.mkv.MatroskaExtractor.Track.initializeOutput(org.telegram.messenger.exoplayer2.extractor.ExtractorOutput, int):void");
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
            if (this.primaryRChromaticityX == -1.0f || this.primaryRChromaticityY == -1.0f || this.primaryGChromaticityX == -1.0f || this.primaryGChromaticityY == -1.0f || this.primaryBChromaticityX == -1.0f || this.primaryBChromaticityY == -1.0f || this.whitePointChromaticityX == -1.0f || this.whitePointChromaticityY == -1.0f || this.maxMasteringLuminance == -1.0f || this.minMasteringLuminance == -1.0f) {
                return null;
            }
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

        private static Pair<String, List<byte[]>> parseFourCcPrivate(ParsableByteArray buffer) throws ParserException {
            try {
                buffer.skipBytes(16);
                long compression = buffer.readLittleEndianUnsignedInt();
                if (compression == NUM) {
                    return new Pair(MimeTypes.VIDEO_H263, null);
                }
                if (compression == 826496599) {
                    int startOffset = buffer.getPosition() + 20;
                    byte[] bufferData = buffer.data;
                    int offset = startOffset;
                    while (offset < bufferData.length - 4) {
                        if (bufferData[offset] == (byte) 0 && bufferData[offset + 1] == (byte) 0 && bufferData[offset + 2] == (byte) 1 && bufferData[offset + 3] == (byte) 15) {
                            return new Pair(MimeTypes.VIDEO_VC1, Collections.singletonList(Arrays.copyOfRange(bufferData, offset, bufferData.length)));
                        }
                        offset++;
                    }
                    throw new ParserException("Failed to find FourCC VC1 initialization data");
                }
                Log.w(MatroskaExtractor.TAG, "Unknown FourCC. Setting mimeType to video/x-unknown");
                return new Pair(MimeTypes.VIDEO_UNKNOWN, null);
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new ParserException("Error parsing FourCC private data");
            }
        }

        private static List<byte[]> parseVorbisCodecPrivate(byte[] codecPrivate) throws ParserException {
            try {
                if (codecPrivate[0] != (byte) 2) {
                    throw new ParserException("Error parsing vorbis codec private");
                }
                int vorbisInfoLength = 0;
                int offset = 1;
                while (codecPrivate[offset] == (byte) -1) {
                    vorbisInfoLength += 255;
                    offset++;
                }
                vorbisInfoLength += codecPrivate[offset];
                int vorbisSkipLength = 0;
                offset++;
                while (codecPrivate[offset] == (byte) -1) {
                    vorbisSkipLength += 255;
                    offset++;
                }
                int offset2 = offset + 1;
                vorbisSkipLength += codecPrivate[offset];
                if (codecPrivate[offset2] != (byte) 1) {
                    throw new ParserException("Error parsing vorbis codec private");
                }
                byte[] vorbisInfo = new byte[vorbisInfoLength];
                System.arraycopy(codecPrivate, offset2, vorbisInfo, 0, vorbisInfoLength);
                offset2 += vorbisInfoLength;
                if (codecPrivate[offset2] != (byte) 3) {
                    throw new ParserException("Error parsing vorbis codec private");
                }
                offset2 += vorbisSkipLength;
                if (codecPrivate[offset2] != (byte) 5) {
                    throw new ParserException("Error parsing vorbis codec private");
                }
                byte[] vorbisBooks = new byte[(codecPrivate.length - offset2)];
                System.arraycopy(codecPrivate, offset2, vorbisBooks, 0, codecPrivate.length - offset2);
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
                if (formatTag == 1) {
                    return true;
                }
                if (formatTag != MatroskaExtractor.WAVE_FORMAT_EXTENSIBLE) {
                    return false;
                }
                buffer.setPosition(24);
                if (buffer.readLong() == MatroskaExtractor.WAVE_SUBFORMAT_PCM.getMostSignificantBits() && buffer.readLong() == MatroskaExtractor.WAVE_SUBFORMAT_PCM.getLeastSignificantBits()) {
                    return true;
                }
                return false;
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
        private final byte[] syncframePrefix = new byte[10];
        private long timeUs;

        public void reset() {
            this.foundSyncframe = false;
        }

        public void startSample(ExtractorInput input, int blockFlags, int size) throws IOException, InterruptedException {
            if (!this.foundSyncframe) {
                input.peekFully(this.syncframePrefix, 0, 10);
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
                if (this.sampleCount >= 16) {
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
    static class C20011 implements ExtractorsFactory {
        C20011() {
        }

        public Extractor[] createExtractors() {
            return new Extractor[]{new MatroskaExtractor()};
        }
    }

    private final class InnerEbmlReaderOutput implements EbmlReaderOutput {
        private InnerEbmlReaderOutput() {
        }

        public int getElementType(int id) {
            switch (id) {
                case MatroskaExtractor.ID_TRACK_TYPE /*131*/:
                case MatroskaExtractor.ID_FLAG_DEFAULT /*136*/:
                case MatroskaExtractor.ID_BLOCK_DURATION /*155*/:
                case MatroskaExtractor.ID_CHANNELS /*159*/:
                case MatroskaExtractor.ID_PIXEL_WIDTH /*176*/:
                case MatroskaExtractor.ID_CUE_TIME /*179*/:
                case MatroskaExtractor.ID_PIXEL_HEIGHT /*186*/:
                case MatroskaExtractor.ID_TRACK_NUMBER /*215*/:
                case MatroskaExtractor.ID_TIME_CODE /*231*/:
                case MatroskaExtractor.ID_CUE_CLUSTER_POSITION /*241*/:
                case MatroskaExtractor.ID_REFERENCE_BLOCK /*251*/:
                case MatroskaExtractor.ID_CONTENT_COMPRESSION_ALGORITHM /*16980*/:
                case MatroskaExtractor.ID_DOC_TYPE_READ_VERSION /*17029*/:
                case MatroskaExtractor.ID_EBML_READ_VERSION /*17143*/:
                case MatroskaExtractor.ID_CONTENT_ENCRYPTION_ALGORITHM /*18401*/:
                case MatroskaExtractor.ID_CONTENT_ENCRYPTION_AES_SETTINGS_CIPHER_MODE /*18408*/:
                case MatroskaExtractor.ID_CONTENT_ENCODING_ORDER /*20529*/:
                case MatroskaExtractor.ID_CONTENT_ENCODING_SCOPE /*20530*/:
                case MatroskaExtractor.ID_SEEK_POSITION /*21420*/:
                case MatroskaExtractor.ID_STEREO_MODE /*21432*/:
                case MatroskaExtractor.ID_DISPLAY_WIDTH /*21680*/:
                case MatroskaExtractor.ID_DISPLAY_UNIT /*21682*/:
                case MatroskaExtractor.ID_DISPLAY_HEIGHT /*21690*/:
                case MatroskaExtractor.ID_FLAG_FORCED /*21930*/:
                case MatroskaExtractor.ID_COLOUR_RANGE /*21945*/:
                case MatroskaExtractor.ID_COLOUR_TRANSFER /*21946*/:
                case MatroskaExtractor.ID_COLOUR_PRIMARIES /*21947*/:
                case MatroskaExtractor.ID_MAX_CLL /*21948*/:
                case MatroskaExtractor.ID_MAX_FALL /*21949*/:
                case MatroskaExtractor.ID_CODEC_DELAY /*22186*/:
                case MatroskaExtractor.ID_SEEK_PRE_ROLL /*22203*/:
                case MatroskaExtractor.ID_AUDIO_BIT_DEPTH /*25188*/:
                case MatroskaExtractor.ID_DEFAULT_DURATION /*2352003*/:
                case MatroskaExtractor.ID_TIMECODE_SCALE /*2807729*/:
                    return 2;
                case 134:
                case MatroskaExtractor.ID_DOC_TYPE /*17026*/:
                case MatroskaExtractor.ID_LANGUAGE /*2274716*/:
                    return 3;
                case MatroskaExtractor.ID_BLOCK_GROUP /*160*/:
                case MatroskaExtractor.ID_TRACK_ENTRY /*174*/:
                case MatroskaExtractor.ID_CUE_TRACK_POSITIONS /*183*/:
                case MatroskaExtractor.ID_CUE_POINT /*187*/:
                case 224:
                case MatroskaExtractor.ID_AUDIO /*225*/:
                case MatroskaExtractor.ID_CONTENT_ENCRYPTION_AES_SETTINGS /*18407*/:
                case MatroskaExtractor.ID_SEEK /*19899*/:
                case MatroskaExtractor.ID_CONTENT_COMPRESSION /*20532*/:
                case MatroskaExtractor.ID_CONTENT_ENCRYPTION /*20533*/:
                case MatroskaExtractor.ID_COLOUR /*21936*/:
                case MatroskaExtractor.ID_MASTERING_METADATA /*21968*/:
                case MatroskaExtractor.ID_CONTENT_ENCODING /*25152*/:
                case MatroskaExtractor.ID_CONTENT_ENCODINGS /*28032*/:
                case MatroskaExtractor.ID_PROJECTION /*30320*/:
                case MatroskaExtractor.ID_SEEK_HEAD /*290298740*/:
                case 357149030:
                case MatroskaExtractor.ID_TRACKS /*374648427*/:
                case MatroskaExtractor.ID_SEGMENT /*408125543*/:
                case MatroskaExtractor.ID_EBML /*440786851*/:
                case MatroskaExtractor.ID_CUES /*475249515*/:
                case MatroskaExtractor.ID_CLUSTER /*524531317*/:
                    return 1;
                case MatroskaExtractor.ID_BLOCK /*161*/:
                case MatroskaExtractor.ID_SIMPLE_BLOCK /*163*/:
                case MatroskaExtractor.ID_CONTENT_COMPRESSION_SETTINGS /*16981*/:
                case MatroskaExtractor.ID_CONTENT_ENCRYPTION_KEY_ID /*18402*/:
                case MatroskaExtractor.ID_SEEK_ID /*21419*/:
                case MatroskaExtractor.ID_CODEC_PRIVATE /*25506*/:
                case MatroskaExtractor.ID_PROJECTION_PRIVATE /*30322*/:
                    return 4;
                case MatroskaExtractor.ID_SAMPLING_FREQUENCY /*181*/:
                case MatroskaExtractor.ID_DURATION /*17545*/:
                case MatroskaExtractor.ID_PRIMARY_R_CHROMATICITY_X /*21969*/:
                case MatroskaExtractor.ID_PRIMARY_R_CHROMATICITY_Y /*21970*/:
                case MatroskaExtractor.ID_PRIMARY_G_CHROMATICITY_X /*21971*/:
                case MatroskaExtractor.ID_PRIMARY_G_CHROMATICITY_Y /*21972*/:
                case MatroskaExtractor.ID_PRIMARY_B_CHROMATICITY_X /*21973*/:
                case MatroskaExtractor.ID_PRIMARY_B_CHROMATICITY_Y /*21974*/:
                case MatroskaExtractor.ID_WHITE_POINT_CHROMATICITY_X /*21975*/:
                case MatroskaExtractor.ID_WHITE_POINT_CHROMATICITY_Y /*21976*/:
                case MatroskaExtractor.ID_LUMNINANCE_MAX /*21977*/:
                case MatroskaExtractor.ID_LUMNINANCE_MIN /*21978*/:
                    return 5;
                default:
                    return 0;
            }
        }

        public boolean isLevel1Element(int id) {
            return id == 357149030 || id == MatroskaExtractor.ID_CLUSTER || id == MatroskaExtractor.ID_CUES || id == MatroskaExtractor.ID_TRACKS;
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
        this.timecodeScale = C0559C.TIME_UNSET;
        this.durationTimecode = C0559C.TIME_UNSET;
        this.durationUs = C0559C.TIME_UNSET;
        this.cuesContentPosition = -1;
        this.seekPositionAfterBuildingCues = -1;
        this.clusterTimecodeUs = C0559C.TIME_UNSET;
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
        this.clusterTimecodeUs = C0559C.TIME_UNSET;
        this.blockState = 0;
        this.reader.reset();
        this.varintReader.reset();
        resetSample();
        for (int i = 0; i < this.tracks.size(); i++) {
            ((Track) this.tracks.valueAt(i)).reset();
        }
    }

    public void release() {
    }

    public int read(ExtractorInput input, PositionHolder seekPosition) throws IOException, InterruptedException {
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
        for (int i = 0; i < this.tracks.size(); i++) {
            ((Track) this.tracks.valueAt(i)).outputPendingSampleMetadata();
        }
        return -1;
    }

    void startMasterElement(int id, long contentPosition, long contentSize) throws ParserException {
        switch (id) {
            case ID_BLOCK_GROUP /*160*/:
                this.sampleSeenReferenceBlock = false;
                return;
            case ID_TRACK_ENTRY /*174*/:
                this.currentTrack = new Track();
                return;
            case ID_CUE_POINT /*187*/:
                this.seenClusterPositionForCurrentCuePoint = false;
                return;
            case ID_SEEK /*19899*/:
                this.seekEntryId = -1;
                this.seekEntryPosition = -1;
                return;
            case ID_CONTENT_ENCRYPTION /*20533*/:
                this.currentTrack.hasContentEncryption = true;
                return;
            case ID_MASTERING_METADATA /*21968*/:
                this.currentTrack.hasColorInfo = true;
                return;
            case ID_SEGMENT /*408125543*/:
                if (this.segmentContentPosition == -1 || this.segmentContentPosition == contentPosition) {
                    this.segmentContentPosition = contentPosition;
                    this.segmentContentSize = contentSize;
                    return;
                }
                throw new ParserException("Multiple Segment elements not supported");
            case ID_CUES /*475249515*/:
                this.cueTimesUs = new LongArray();
                this.cueClusterPositions = new LongArray();
                return;
            case ID_CLUSTER /*524531317*/:
                if (!this.sentSeekMap) {
                    if (!this.seekForCuesEnabled || this.cuesContentPosition == -1) {
                        this.extractorOutput.seekMap(new Unseekable(this.durationUs));
                        this.sentSeekMap = true;
                        return;
                    }
                    this.seekForCues = true;
                    return;
                }
                return;
            default:
                return;
        }
    }

    void endMasterElement(int id) throws ParserException {
        switch (id) {
            case ID_BLOCK_GROUP /*160*/:
                if (this.blockState == 2) {
                    if (!this.sampleSeenReferenceBlock) {
                        this.blockFlags |= 1;
                    }
                    commitSampleToOutput((Track) this.tracks.get(this.blockTrackNumber), this.blockTimeUs);
                    this.blockState = 0;
                    return;
                }
                return;
            case ID_TRACK_ENTRY /*174*/:
                if (isCodecSupported(this.currentTrack.codecId)) {
                    this.currentTrack.initializeOutput(this.extractorOutput, this.currentTrack.number);
                    this.tracks.put(this.currentTrack.number, this.currentTrack);
                }
                this.currentTrack = null;
                return;
            case ID_SEEK /*19899*/:
                if (this.seekEntryId == -1 || this.seekEntryPosition == -1) {
                    throw new ParserException("Mandatory element SeekID or SeekPosition not found");
                } else if (this.seekEntryId == ID_CUES) {
                    this.cuesContentPosition = this.seekEntryPosition;
                    return;
                } else {
                    return;
                }
            case ID_CONTENT_ENCODING /*25152*/:
                if (!this.currentTrack.hasContentEncryption) {
                    return;
                }
                if (this.currentTrack.cryptoData == null) {
                    throw new ParserException("Encrypted Track found but ContentEncKeyID was not found");
                }
                this.currentTrack.drmInitData = new DrmInitData(new SchemeData(C0559C.UUID_NIL, MimeTypes.VIDEO_WEBM, this.currentTrack.cryptoData.encryptionKey));
                return;
            case ID_CONTENT_ENCODINGS /*28032*/:
                if (this.currentTrack.hasContentEncryption && this.currentTrack.sampleStrippedBytes != null) {
                    throw new ParserException("Combining encryption and compression is not supported");
                }
                return;
            case 357149030:
                if (this.timecodeScale == C0559C.TIME_UNSET) {
                    this.timecodeScale = 1000000;
                }
                if (this.durationTimecode != C0559C.TIME_UNSET) {
                    this.durationUs = scaleTimecodeToUs(this.durationTimecode);
                    return;
                }
                return;
            case ID_TRACKS /*374648427*/:
                if (this.tracks.size() == 0) {
                    throw new ParserException("No valid tracks were found");
                }
                this.extractorOutput.endTracks();
                return;
            case ID_CUES /*475249515*/:
                if (!this.sentSeekMap) {
                    this.extractorOutput.seekMap(buildSeekMap());
                    this.sentSeekMap = true;
                    return;
                }
                return;
            default:
                return;
        }
    }

    void integerElement(int id, long value) throws ParserException {
        boolean z = true;
        Track track;
        switch (id) {
            case ID_TRACK_TYPE /*131*/:
                this.currentTrack.type = (int) value;
                return;
            case ID_FLAG_DEFAULT /*136*/:
                track = this.currentTrack;
                if (value != 1) {
                    z = false;
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
                    throw new ParserException("ContentCompAlgo " + value + " not supported");
                }
                return;
            case ID_DOC_TYPE_READ_VERSION /*17029*/:
                if (value < 1 || value > 2) {
                    throw new ParserException("DocTypeReadVersion " + value + " not supported");
                }
                return;
            case ID_EBML_READ_VERSION /*17143*/:
                if (value != 1) {
                    throw new ParserException("EBMLReadVersion " + value + " not supported");
                }
                return;
            case ID_CONTENT_ENCRYPTION_ALGORITHM /*18401*/:
                if (value != 5) {
                    throw new ParserException("ContentEncAlgo " + value + " not supported");
                }
                return;
            case ID_CONTENT_ENCRYPTION_AES_SETTINGS_CIPHER_MODE /*18408*/:
                if (value != 1) {
                    throw new ParserException("AESSettingsCipherMode " + value + " not supported");
                }
                return;
            case ID_CONTENT_ENCODING_ORDER /*20529*/:
                if (value != 0) {
                    throw new ParserException("ContentEncodingOrder " + value + " not supported");
                }
                return;
            case ID_CONTENT_ENCODING_SCOPE /*20530*/:
                if (value != 1) {
                    throw new ParserException("ContentEncodingScope " + value + " not supported");
                }
                return;
            case ID_SEEK_POSITION /*21420*/:
                this.seekEntryPosition = this.segmentContentPosition + value;
                return;
            case ID_STEREO_MODE /*21432*/:
                switch ((int) value) {
                    case 0:
                        this.currentTrack.stereoMode = 0;
                        return;
                    case 1:
                        this.currentTrack.stereoMode = 2;
                        return;
                    case 3:
                        this.currentTrack.stereoMode = 1;
                        return;
                    case 15:
                        this.currentTrack.stereoMode = 3;
                        return;
                    default:
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
                if (value != 1) {
                    z = false;
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
                switch ((int) value) {
                    case 1:
                    case 6:
                    case 7:
                        this.currentTrack.colorTransfer = 3;
                        return;
                    case 16:
                        this.currentTrack.colorTransfer = 6;
                        return;
                    case 18:
                        this.currentTrack.colorTransfer = 7;
                        return;
                    default:
                        return;
                }
            case ID_COLOUR_PRIMARIES /*21947*/:
                this.currentTrack.hasColorInfo = true;
                switch ((int) value) {
                    case 1:
                        this.currentTrack.colorSpace = 1;
                        return;
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                        this.currentTrack.colorSpace = 2;
                        return;
                    case 9:
                        this.currentTrack.colorSpace = 6;
                        return;
                    default:
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
        switch (id) {
            case ID_SAMPLING_FREQUENCY /*181*/:
                this.currentTrack.sampleRate = (int) value;
                return;
            case ID_DURATION /*17545*/:
                this.durationTimecode = (long) value;
                return;
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
    }

    void stringElement(int id, String value) throws ParserException {
        switch (id) {
            case 134:
                this.currentTrack.codecId = value;
                return;
            case ID_DOC_TYPE /*17026*/:
                if (!DOC_TYPE_WEBM.equals(value) && !DOC_TYPE_MATROSKA.equals(value)) {
                    throw new ParserException("DocType " + value + " not supported");
                }
                return;
            case ID_LANGUAGE /*2274716*/:
                this.currentTrack.language = value;
                return;
            default:
                return;
        }
    }

    void binaryElement(int id, int contentSize, ExtractorInput input) throws IOException, InterruptedException {
        switch (id) {
            case ID_BLOCK /*161*/:
            case ID_SIMPLE_BLOCK /*163*/:
                if (this.blockState == 0) {
                    this.blockTrackNumber = (int) this.varintReader.readUnsignedVarint(input, false, true, 8);
                    this.blockTrackNumberLength = this.varintReader.getLastLength();
                    this.blockDurationUs = C0559C.TIME_UNSET;
                    this.blockState = 1;
                    this.scratch.reset();
                }
                Track track = (Track) this.tracks.get(this.blockTrackNumber);
                if (track == null) {
                    input.skipFully(contentSize - this.blockTrackNumberLength);
                    this.blockState = 0;
                    return;
                }
                if (this.blockState == 1) {
                    int i;
                    int i2;
                    readScratch(input, 3);
                    int lacing = (this.scratch.data[2] & 6) >> 1;
                    if (lacing == 0) {
                        this.blockLacingSampleCount = 1;
                        this.blockLacingSampleSizes = ensureArrayCapacity(this.blockLacingSampleSizes, 1);
                        this.blockLacingSampleSizes[0] = (contentSize - this.blockTrackNumberLength) - 3;
                    } else if (id != ID_SIMPLE_BLOCK) {
                        throw new ParserException("Lacing only supported in SimpleBlocks.");
                    } else {
                        readScratch(input, 4);
                        this.blockLacingSampleCount = (this.scratch.data[3] & 255) + 1;
                        this.blockLacingSampleSizes = ensureArrayCapacity(this.blockLacingSampleSizes, this.blockLacingSampleCount);
                        if (lacing == 2) {
                            Arrays.fill(this.blockLacingSampleSizes, 0, this.blockLacingSampleCount, ((contentSize - this.blockTrackNumberLength) - 4) / this.blockLacingSampleCount);
                        } else if (lacing == 1) {
                            totalSamplesSize = 0;
                            headerSize = 4;
                            for (sampleIndex = 0; sampleIndex < this.blockLacingSampleCount - 1; sampleIndex++) {
                                this.blockLacingSampleSizes[sampleIndex] = 0;
                                int byteValue;
                                do {
                                    headerSize++;
                                    readScratch(input, headerSize);
                                    byteValue = this.scratch.data[headerSize - 1] & 255;
                                    r26 = this.blockLacingSampleSizes;
                                    r26[sampleIndex] = r26[sampleIndex] + byteValue;
                                } while (byteValue == 255);
                                totalSamplesSize += this.blockLacingSampleSizes[sampleIndex];
                            }
                            this.blockLacingSampleSizes[this.blockLacingSampleCount - 1] = ((contentSize - this.blockTrackNumberLength) - headerSize) - totalSamplesSize;
                        } else if (lacing == 3) {
                            totalSamplesSize = 0;
                            headerSize = 4;
                            sampleIndex = 0;
                            while (sampleIndex < this.blockLacingSampleCount - 1) {
                                this.blockLacingSampleSizes[sampleIndex] = 0;
                                headerSize++;
                                readScratch(input, headerSize);
                                if (this.scratch.data[headerSize - 1] == (byte) 0) {
                                    throw new ParserException("No valid varint length mask found");
                                }
                                long readValue = 0;
                                int i3 = 0;
                                while (i3 < 8) {
                                    int lengthMask = 1 << (7 - i3);
                                    if ((this.scratch.data[headerSize - 1] & lengthMask) != 0) {
                                        int readPosition = headerSize - 1;
                                        headerSize += i3;
                                        readScratch(input, headerSize);
                                        readValue = (long) ((this.scratch.data[readPosition] & 255) & (lengthMask ^ -1));
                                        for (int readPosition2 = readPosition + 1; readPosition2 < headerSize; readPosition2++) {
                                            readValue = (readValue << 8) | ((long) (this.scratch.data[readPosition2] & 255));
                                        }
                                        if (sampleIndex > 0) {
                                            readValue -= (1 << ((i3 * 7) + 6)) - 1;
                                        }
                                        if (readValue >= -2147483648L || readValue > 2147483647L) {
                                            throw new ParserException("EBML lacing sample size out of range.");
                                        }
                                        int intReadValue = (int) readValue;
                                        r26 = this.blockLacingSampleSizes;
                                        if (sampleIndex != 0) {
                                            intReadValue += this.blockLacingSampleSizes[sampleIndex - 1];
                                        }
                                        r26[sampleIndex] = intReadValue;
                                        totalSamplesSize += this.blockLacingSampleSizes[sampleIndex];
                                        sampleIndex++;
                                    } else {
                                        i3++;
                                    }
                                }
                                if (readValue >= -2147483648L) {
                                    break;
                                }
                                throw new ParserException("EBML lacing sample size out of range.");
                            }
                            this.blockLacingSampleSizes[this.blockLacingSampleCount - 1] = ((contentSize - this.blockTrackNumberLength) - headerSize) - totalSamplesSize;
                        } else {
                            throw new ParserException("Unexpected lacing value: " + lacing);
                        }
                    }
                    this.blockTimeUs = this.clusterTimecodeUs + scaleTimecodeToUs((long) ((this.scratch.data[0] << 8) | (this.scratch.data[1] & 255)));
                    boolean isInvisible = (this.scratch.data[2] & 8) == 8;
                    boolean isKeyframe = track.type == 2 || (id == ID_SIMPLE_BLOCK && (this.scratch.data[2] & 128) == 128);
                    if (isKeyframe) {
                        i = 1;
                    } else {
                        i = 0;
                    }
                    if (isInvisible) {
                        i2 = Integer.MIN_VALUE;
                    } else {
                        i2 = 0;
                    }
                    this.blockFlags = i2 | i;
                    this.blockState = 2;
                    this.blockLacingSampleIndex = 0;
                }
                if (id == ID_SIMPLE_BLOCK) {
                    while (this.blockLacingSampleIndex < this.blockLacingSampleCount) {
                        writeSampleData(input, track, this.blockLacingSampleSizes[this.blockLacingSampleIndex]);
                        commitSampleToOutput(track, this.blockTimeUs + ((long) ((this.blockLacingSampleIndex * track.defaultSampleDurationNs) / 1000)));
                        this.blockLacingSampleIndex++;
                    }
                    this.blockState = 0;
                    return;
                }
                writeSampleData(input, track, this.blockLacingSampleSizes[0]);
                return;
            case ID_CONTENT_COMPRESSION_SETTINGS /*16981*/:
                this.currentTrack.sampleStrippedBytes = new byte[contentSize];
                input.readFully(this.currentTrack.sampleStrippedBytes, 0, contentSize);
                return;
            case ID_CONTENT_ENCRYPTION_KEY_ID /*18402*/:
                byte[] encryptionKey = new byte[contentSize];
                input.readFully(encryptionKey, 0, contentSize);
                this.currentTrack.cryptoData = new CryptoData(1, encryptionKey, 0, 0);
                return;
            case ID_SEEK_ID /*21419*/:
                Arrays.fill(this.seekEntryIdBytes.data, (byte) 0);
                input.readFully(this.seekEntryIdBytes.data, 4 - contentSize, contentSize);
                this.seekEntryIdBytes.setPosition(0);
                this.seekEntryId = (int) this.seekEntryIdBytes.readUnsignedInt();
                return;
            case ID_CODEC_PRIVATE /*25506*/:
                this.currentTrack.codecPrivate = new byte[contentSize];
                input.readFully(this.currentTrack.codecPrivate, 0, contentSize);
                return;
            case ID_PROJECTION_PRIVATE /*30322*/:
                this.currentTrack.projectionData = new byte[contentSize];
                input.readFully(this.currentTrack.projectionData, 0, contentSize);
                return;
            default:
                throw new ParserException("Unexpected id: " + id);
        }
    }

    private void commitSampleToOutput(Track track, long timeUs) {
        if (track.trueHdSampleRechunker != null) {
            track.trueHdSampleRechunker.sampleMetadata(track, timeUs);
        } else {
            if (CODEC_ID_SUBRIP.equals(track.codecId)) {
                commitSubtitleSample(track, SUBRIP_TIMECODE_FORMAT, 19, 1000, SUBRIP_TIMECODE_EMPTY);
            } else if (CODEC_ID_ASS.equals(track.codecId)) {
                commitSubtitleSample(track, SSA_TIMECODE_FORMAT, 21, SSA_TIMECODE_LAST_VALUE_SCALING_FACTOR, SSA_TIMECODE_EMPTY);
            }
            track.output.sampleMetadata(timeUs, this.blockFlags, this.sampleBytesWritten, 0, track.cryptoData);
        }
        this.sampleRead = true;
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
        if (CODEC_ID_SUBRIP.equals(track.codecId)) {
            writeSubtitleSampleData(input, SUBRIP_PREFIX, size);
        } else if (CODEC_ID_ASS.equals(track.codecId)) {
            writeSubtitleSampleData(input, SSA_PREFIX, size);
        } else {
            TrackOutput output = track.output;
            if (!this.sampleEncodingHandled) {
                if (track.hasContentEncryption) {
                    this.blockFlags &= -NUM;
                    if (!this.sampleSignalByteRead) {
                        input.readFully(this.scratch.data, 0, 1);
                        this.sampleBytesRead++;
                        if ((this.scratch.data[0] & 128) == 128) {
                            throw new ParserException("Extension bit is set in signal byte");
                        }
                        this.sampleSignalByte = this.scratch.data[0];
                        this.sampleSignalByteRead = true;
                    }
                    if ((this.sampleSignalByte & 1) == 1) {
                        boolean hasSubsampleEncryption = (this.sampleSignalByte & 2) == 2;
                        this.blockFlags |= NUM;
                        if (!this.sampleInitializationVectorRead) {
                            input.readFully(this.encryptionInitializationVector.data, 0, 8);
                            this.sampleBytesRead += 8;
                            this.sampleInitializationVectorRead = true;
                            this.scratch.data[0] = (byte) ((hasSubsampleEncryption ? 128 : 0) | 8);
                            this.scratch.setPosition(0);
                            output.sampleData(this.scratch, 1);
                            this.sampleBytesWritten++;
                            this.encryptionInitializationVector.setPosition(0);
                            output.sampleData(this.encryptionInitializationVector, 8);
                            this.sampleBytesWritten += 8;
                        }
                        if (hasSubsampleEncryption) {
                            if (!this.samplePartitionCountRead) {
                                input.readFully(this.scratch.data, 0, 1);
                                this.sampleBytesRead++;
                                this.scratch.setPosition(0);
                                this.samplePartitionCount = this.scratch.readUnsignedByte();
                                this.samplePartitionCountRead = true;
                            }
                            int samplePartitionDataSize = this.samplePartitionCount * 4;
                            this.scratch.reset(samplePartitionDataSize);
                            input.readFully(this.scratch.data, 0, samplePartitionDataSize);
                            this.sampleBytesRead += samplePartitionDataSize;
                            short subsampleCount = (short) ((this.samplePartitionCount / 2) + 1);
                            int subsampleDataSize = (subsampleCount * 6) + 2;
                            if (this.encryptionSubsampleDataBuffer == null || this.encryptionSubsampleDataBuffer.capacity() < subsampleDataSize) {
                                this.encryptionSubsampleDataBuffer = ByteBuffer.allocate(subsampleDataSize);
                            }
                            this.encryptionSubsampleDataBuffer.position(0);
                            this.encryptionSubsampleDataBuffer.putShort(subsampleCount);
                            int partitionOffset = 0;
                            for (int i = 0; i < this.samplePartitionCount; i++) {
                                int previousPartitionOffset = partitionOffset;
                                partitionOffset = this.scratch.readUnsignedIntToInt();
                                if (i % 2 == 0) {
                                    this.encryptionSubsampleDataBuffer.putShort((short) (partitionOffset - previousPartitionOffset));
                                } else {
                                    this.encryptionSubsampleDataBuffer.putInt(partitionOffset - previousPartitionOffset);
                                }
                            }
                            int finalPartitionSize = (size - this.sampleBytesRead) - partitionOffset;
                            if (this.samplePartitionCount % 2 == 1) {
                                this.encryptionSubsampleDataBuffer.putInt(finalPartitionSize);
                            } else {
                                this.encryptionSubsampleDataBuffer.putShort((short) finalPartitionSize);
                                this.encryptionSubsampleDataBuffer.putInt(0);
                            }
                            this.encryptionSubsampleData.reset(this.encryptionSubsampleDataBuffer.array(), subsampleDataSize);
                            output.sampleData(this.encryptionSubsampleData, subsampleDataSize);
                            this.sampleBytesWritten += subsampleDataSize;
                        }
                    }
                } else if (track.sampleStrippedBytes != null) {
                    this.sampleStrippedBytes.reset(track.sampleStrippedBytes, track.sampleStrippedBytes.length);
                }
                this.sampleEncodingHandled = true;
            }
            size += this.sampleStrippedBytes.limit();
            if (CODEC_ID_H264.equals(track.codecId) || CODEC_ID_H265.equals(track.codecId)) {
                byte[] nalLengthData = this.nalLength.data;
                nalLengthData[0] = (byte) 0;
                nalLengthData[1] = (byte) 0;
                nalLengthData[2] = (byte) 0;
                int nalUnitLengthFieldLength = track.nalUnitLengthFieldLength;
                int nalUnitLengthFieldLengthDiff = 4 - track.nalUnitLengthFieldLength;
                while (this.sampleBytesRead < size) {
                    if (this.sampleCurrentNalBytesRemaining == 0) {
                        readToTarget(input, nalLengthData, nalUnitLengthFieldLengthDiff, nalUnitLengthFieldLength);
                        this.nalLength.setPosition(0);
                        this.sampleCurrentNalBytesRemaining = this.nalLength.readUnsignedIntToInt();
                        this.nalStartCode.setPosition(0);
                        output.sampleData(this.nalStartCode, 4);
                        this.sampleBytesWritten += 4;
                    } else {
                        this.sampleCurrentNalBytesRemaining -= readToOutput(input, output, this.sampleCurrentNalBytesRemaining);
                    }
                }
            } else {
                if (track.trueHdSampleRechunker != null) {
                    Assertions.checkState(this.sampleStrippedBytes.limit() == 0);
                    track.trueHdSampleRechunker.startSample(input, this.blockFlags, size);
                }
                while (this.sampleBytesRead < size) {
                    readToOutput(input, output, size - this.sampleBytesRead);
                }
            }
            if (CODEC_ID_VORBIS.equals(track.codecId)) {
                this.vorbisNumPageSamples.setPosition(0);
                output.sampleData(this.vorbisNumPageSamples, 4);
                this.sampleBytesWritten += 4;
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
        if (durationUs == C0559C.TIME_UNSET) {
            timeCodeData = emptyTimecode;
        } else {
            durationUs -= ((long) (((int) (durationUs / 3600000000L)) * 3600)) * 1000000;
            durationUs -= ((long) (((int) (durationUs / 60000000)) * 60)) * 1000000;
            int lastValue = (int) ((durationUs - (((long) ((int) (durationUs / 1000000))) * 1000000)) / lastTimecodeValueScalingFactor);
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
        if (this.segmentContentPosition == -1 || this.durationUs == C0559C.TIME_UNSET || this.cueTimesUs == null || this.cueTimesUs.size() == 0 || this.cueClusterPositions == null || this.cueClusterPositions.size() != this.cueTimesUs.size()) {
            this.cueTimesUs = null;
            this.cueClusterPositions = null;
            return new Unseekable(this.durationUs);
        }
        int i;
        int cuePointsSize = this.cueTimesUs.size();
        int[] sizes = new int[cuePointsSize];
        long[] offsets = new long[cuePointsSize];
        long[] durationsUs = new long[cuePointsSize];
        long[] timesUs = new long[cuePointsSize];
        for (i = 0; i < cuePointsSize; i++) {
            timesUs[i] = this.cueTimesUs.get(i);
            offsets[i] = this.segmentContentPosition + this.cueClusterPositions.get(i);
        }
        for (i = 0; i < cuePointsSize - 1; i++) {
            sizes[i] = (int) (offsets[i + 1] - offsets[i]);
            durationsUs[i] = timesUs[i + 1] - timesUs[i];
        }
        sizes[cuePointsSize - 1] = (int) ((this.segmentContentPosition + this.segmentContentSize) - offsets[cuePointsSize - 1]);
        durationsUs[cuePointsSize - 1] = this.durationUs - timesUs[cuePointsSize - 1];
        this.cueTimesUs = null;
        this.cueClusterPositions = null;
        return new ChunkIndex(sizes, offsets, durationsUs, timesUs);
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
        if (this.timecodeScale == C0559C.TIME_UNSET) {
            throw new ParserException("Can't scale timecode prior to timecodeScale being set.");
        }
        return Util.scaleLargeTimestamp(unscaledTimecode, this.timecodeScale, 1000);
    }

    private static boolean isCodecSupported(String codecId) {
        if (CODEC_ID_VP8.equals(codecId) || CODEC_ID_VP9.equals(codecId) || CODEC_ID_MPEG2.equals(codecId) || CODEC_ID_MPEG4_SP.equals(codecId) || CODEC_ID_MPEG4_ASP.equals(codecId) || CODEC_ID_MPEG4_AP.equals(codecId) || CODEC_ID_H264.equals(codecId) || CODEC_ID_H265.equals(codecId) || CODEC_ID_FOURCC.equals(codecId) || CODEC_ID_THEORA.equals(codecId) || CODEC_ID_OPUS.equals(codecId) || CODEC_ID_VORBIS.equals(codecId) || CODEC_ID_AAC.equals(codecId) || CODEC_ID_MP2.equals(codecId) || CODEC_ID_MP3.equals(codecId) || CODEC_ID_AC3.equals(codecId) || CODEC_ID_E_AC3.equals(codecId) || CODEC_ID_TRUEHD.equals(codecId) || CODEC_ID_DTS.equals(codecId) || CODEC_ID_DTS_EXPRESS.equals(codecId) || CODEC_ID_DTS_LOSSLESS.equals(codecId) || CODEC_ID_FLAC.equals(codecId) || CODEC_ID_ACM.equals(codecId) || CODEC_ID_PCM_INT_LIT.equals(codecId) || CODEC_ID_SUBRIP.equals(codecId) || CODEC_ID_ASS.equals(codecId) || CODEC_ID_VOBSUB.equals(codecId) || CODEC_ID_PGS.equals(codecId) || CODEC_ID_DVBSUB.equals(codecId)) {
            return true;
        }
        return false;
    }

    private static int[] ensureArrayCapacity(int[] array, int length) {
        if (array == null) {
            return new int[length];
        }
        return array.length < length ? new int[Math.max(array.length * 2, length)] : array;
    }
}

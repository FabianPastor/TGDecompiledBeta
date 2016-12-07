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
import org.telegram.messenger.exoplayer.C;
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

public final class WebmExtractor implements Extractor {
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
    private static final byte[] SUBRIP_PREFIX = new byte[]{(byte) 49, (byte) 10, (byte) 48, (byte) 48, (byte) 58, (byte) 48, (byte) 48, (byte) 58, (byte) 48, (byte) 48, ClosedCaptionCtrl.ERASE_DISPLAYED_MEMORY, (byte) 48, (byte) 48, (byte) 48, ClosedCaptionCtrl.RESUME_CAPTION_LOADING, ClosedCaptionCtrl.CARRIAGE_RETURN, ClosedCaptionCtrl.CARRIAGE_RETURN, (byte) 62, ClosedCaptionCtrl.RESUME_CAPTION_LOADING, (byte) 48, (byte) 48, (byte) 58, (byte) 48, (byte) 48, (byte) 58, (byte) 48, (byte) 48, ClosedCaptionCtrl.ERASE_DISPLAYED_MEMORY, (byte) 48, (byte) 48, (byte) 48, (byte) 10};
    private static final int SUBRIP_PREFIX_END_TIMECODE_OFFSET = 19;
    private static final byte[] SUBRIP_TIMECODE_EMPTY = new byte[]{ClosedCaptionCtrl.RESUME_CAPTION_LOADING, ClosedCaptionCtrl.RESUME_CAPTION_LOADING, ClosedCaptionCtrl.RESUME_CAPTION_LOADING, ClosedCaptionCtrl.RESUME_CAPTION_LOADING, ClosedCaptionCtrl.RESUME_CAPTION_LOADING, ClosedCaptionCtrl.RESUME_CAPTION_LOADING, ClosedCaptionCtrl.RESUME_CAPTION_LOADING, ClosedCaptionCtrl.RESUME_CAPTION_LOADING, ClosedCaptionCtrl.RESUME_CAPTION_LOADING, ClosedCaptionCtrl.RESUME_CAPTION_LOADING, ClosedCaptionCtrl.RESUME_CAPTION_LOADING, ClosedCaptionCtrl.RESUME_CAPTION_LOADING};
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
    private long clusterTimecodeUs;
    private LongArray cueClusterPositions;
    private LongArray cueTimesUs;
    private long cuesContentPosition;
    private Track currentTrack;
    private long durationTimecode;
    private long durationUs;
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
    private long seekPositionAfterBuildingCues;
    private boolean seenClusterPositionForCurrentCuePoint;
    private long segmentContentPosition;
    private long segmentContentSize;
    private boolean sentDrmInitData;
    private boolean sentSeekMap;
    private final ParsableByteArray subripSample;
    private long timecodeScale;
    private final SparseArray<Track> tracks;
    private final VarintReader varintReader;
    private final ParsableByteArray vorbisNumPageSamples;

    private static final class Track {
        private static final int DISPLAY_UNIT_PIXELS = 0;
        public int audioBitDepth;
        public int channelCount;
        public long codecDelayNs;
        public String codecId;
        public byte[] codecPrivate;
        public int defaultSampleDurationNs;
        public int displayHeight;
        public int displayUnit;
        public int displayWidth;
        public byte[] encryptionKeyId;
        public boolean hasContentEncryption;
        public int height;
        private String language;
        public int nalUnitLengthFieldLength;
        public int number;
        public TrackOutput output;
        public int sampleRate;
        public byte[] sampleStrippedBytes;
        public long seekPreRollNs;
        public int type;
        public int width;

        private Track() {
            this.width = -1;
            this.height = -1;
            this.displayWidth = -1;
            this.displayHeight = -1;
            this.displayUnit = 0;
            this.channelCount = 1;
            this.audioBitDepth = -1;
            this.sampleRate = 8000;
            this.codecDelayNs = 0;
            this.seekPreRollNs = 0;
            this.language = "eng";
        }

        public void initializeOutput(ExtractorOutput output, int trackId, long durationUs) throws ParserException {
            String mimeType;
            MediaFormat format;
            int maxInputSize = -1;
            int pcmEncoding = -1;
            List<byte[]> initializationData = null;
            String str = this.codecId;
            Object obj = -1;
            switch (str.hashCode()) {
                case -2095576542:
                    if (str.equals(WebmExtractor.CODEC_ID_MPEG4_AP)) {
                        obj = 5;
                        break;
                    }
                    break;
                case -2095575984:
                    if (str.equals(WebmExtractor.CODEC_ID_MPEG4_SP)) {
                        obj = 3;
                        break;
                    }
                    break;
                case -1985379776:
                    if (str.equals(WebmExtractor.CODEC_ID_ACM)) {
                        obj = 20;
                        break;
                    }
                    break;
                case -1784763192:
                    if (str.equals(WebmExtractor.CODEC_ID_TRUEHD)) {
                        obj = 15;
                        break;
                    }
                    break;
                case -1730367663:
                    if (str.equals(WebmExtractor.CODEC_ID_VORBIS)) {
                        obj = 9;
                        break;
                    }
                    break;
                case -1482641357:
                    if (str.equals(WebmExtractor.CODEC_ID_MP3)) {
                        obj = 12;
                        break;
                    }
                    break;
                case -1373388978:
                    if (str.equals(WebmExtractor.CODEC_ID_FOURCC)) {
                        obj = 8;
                        break;
                    }
                    break;
                case -538363189:
                    if (str.equals(WebmExtractor.CODEC_ID_MPEG4_ASP)) {
                        obj = 4;
                        break;
                    }
                    break;
                case -538363109:
                    if (str.equals(WebmExtractor.CODEC_ID_H264)) {
                        obj = 6;
                        break;
                    }
                    break;
                case -425012669:
                    if (str.equals(WebmExtractor.CODEC_ID_VOBSUB)) {
                        obj = 23;
                        break;
                    }
                    break;
                case -356037306:
                    if (str.equals(WebmExtractor.CODEC_ID_DTS_LOSSLESS)) {
                        obj = 18;
                        break;
                    }
                    break;
                case 62923557:
                    if (str.equals(WebmExtractor.CODEC_ID_AAC)) {
                        obj = 11;
                        break;
                    }
                    break;
                case 62923603:
                    if (str.equals(WebmExtractor.CODEC_ID_AC3)) {
                        obj = 13;
                        break;
                    }
                    break;
                case 62927045:
                    if (str.equals(WebmExtractor.CODEC_ID_DTS)) {
                        obj = 16;
                        break;
                    }
                    break;
                case 82338133:
                    if (str.equals(WebmExtractor.CODEC_ID_VP8)) {
                        obj = null;
                        break;
                    }
                    break;
                case 82338134:
                    if (str.equals(WebmExtractor.CODEC_ID_VP9)) {
                        obj = 1;
                        break;
                    }
                    break;
                case 99146302:
                    if (str.equals(WebmExtractor.CODEC_ID_PGS)) {
                        obj = 24;
                        break;
                    }
                    break;
                case 542569478:
                    if (str.equals(WebmExtractor.CODEC_ID_DTS_EXPRESS)) {
                        obj = 17;
                        break;
                    }
                    break;
                case 725957860:
                    if (str.equals(WebmExtractor.CODEC_ID_PCM_INT_LIT)) {
                        obj = 21;
                        break;
                    }
                    break;
                case 855502857:
                    if (str.equals(WebmExtractor.CODEC_ID_H265)) {
                        obj = 7;
                        break;
                    }
                    break;
                case 1422270023:
                    if (str.equals(WebmExtractor.CODEC_ID_SUBRIP)) {
                        obj = 22;
                        break;
                    }
                    break;
                case 1809237540:
                    if (str.equals(WebmExtractor.CODEC_ID_MPEG2)) {
                        obj = 2;
                        break;
                    }
                    break;
                case 1950749482:
                    if (str.equals(WebmExtractor.CODEC_ID_E_AC3)) {
                        obj = 14;
                        break;
                    }
                    break;
                case 1950789798:
                    if (str.equals(WebmExtractor.CODEC_ID_FLAC)) {
                        obj = 19;
                        break;
                    }
                    break;
                case 1951062397:
                    if (str.equals(WebmExtractor.CODEC_ID_OPUS)) {
                        obj = 10;
                        break;
                    }
                    break;
            }
            switch (obj) {
                case null:
                    mimeType = MimeTypes.VIDEO_VP8;
                    break;
                case 1:
                    mimeType = MimeTypes.VIDEO_VP9;
                    break;
                case 2:
                    mimeType = MimeTypes.VIDEO_MPEG2;
                    break;
                case 3:
                case 4:
                case 5:
                    mimeType = MimeTypes.VIDEO_MP4V;
                    if (this.codecPrivate == null) {
                        initializationData = null;
                    } else {
                        initializationData = Collections.singletonList(this.codecPrivate);
                    }
                    break;
                case 6:
                    mimeType = "video/avc";
                    Pair<List<byte[]>, Integer> h264Data = parseAvcCodecPrivate(new ParsableByteArray(this.codecPrivate));
                    initializationData = h264Data.first;
                    this.nalUnitLengthFieldLength = ((Integer) h264Data.second).intValue();
                    break;
                case 7:
                    mimeType = MimeTypes.VIDEO_H265;
                    Pair<List<byte[]>, Integer> hevcData = parseHevcCodecPrivate(new ParsableByteArray(this.codecPrivate));
                    initializationData = hevcData.first;
                    this.nalUnitLengthFieldLength = ((Integer) hevcData.second).intValue();
                    break;
                case 8:
                    mimeType = MimeTypes.VIDEO_VC1;
                    initializationData = parseFourCcVc1Private(new ParsableByteArray(this.codecPrivate));
                    break;
                case 9:
                    mimeType = MimeTypes.AUDIO_VORBIS;
                    maxInputSize = 8192;
                    initializationData = parseVorbisCodecPrivate(this.codecPrivate);
                    break;
                case 10:
                    mimeType = MimeTypes.AUDIO_OPUS;
                    maxInputSize = WebmExtractor.OPUS_MAX_INPUT_SIZE;
                    initializationData = new ArrayList(3);
                    initializationData.add(this.codecPrivate);
                    initializationData.add(ByteBuffer.allocate(8).order(ByteOrder.nativeOrder()).putLong(this.codecDelayNs).array());
                    initializationData.add(ByteBuffer.allocate(8).order(ByteOrder.nativeOrder()).putLong(this.seekPreRollNs).array());
                    break;
                case 11:
                    mimeType = MimeTypes.AUDIO_AAC;
                    initializationData = Collections.singletonList(this.codecPrivate);
                    break;
                case 12:
                    mimeType = MimeTypes.AUDIO_MPEG;
                    maxInputSize = 4096;
                    break;
                case 13:
                    mimeType = MimeTypes.AUDIO_AC3;
                    break;
                case 14:
                    mimeType = MimeTypes.AUDIO_E_AC3;
                    break;
                case 15:
                    mimeType = MimeTypes.AUDIO_TRUEHD;
                    break;
                case 16:
                case 17:
                    mimeType = MimeTypes.AUDIO_DTS;
                    break;
                case 18:
                    mimeType = MimeTypes.AUDIO_DTS_HD;
                    break;
                case 19:
                    mimeType = MimeTypes.AUDIO_FLAC;
                    initializationData = Collections.singletonList(this.codecPrivate);
                    break;
                case 20:
                    mimeType = MimeTypes.AUDIO_RAW;
                    if (parseMsAcmCodecPrivate(new ParsableByteArray(this.codecPrivate))) {
                        pcmEncoding = Util.getPcmEncoding(this.audioBitDepth);
                        if (pcmEncoding == 0) {
                            throw new ParserException("Unsupported PCM bit depth: " + this.audioBitDepth);
                        }
                    }
                    throw new ParserException("Non-PCM MS/ACM is unsupported");
                    break;
                case 21:
                    mimeType = MimeTypes.AUDIO_RAW;
                    pcmEncoding = Util.getPcmEncoding(this.audioBitDepth);
                    if (pcmEncoding == 0) {
                        throw new ParserException("Unsupported PCM bit depth: " + this.audioBitDepth);
                    }
                    break;
                case 22:
                    mimeType = MimeTypes.APPLICATION_SUBRIP;
                    break;
                case 23:
                    mimeType = MimeTypes.APPLICATION_VOBSUB;
                    initializationData = Collections.singletonList(this.codecPrivate);
                    break;
                case 24:
                    mimeType = MimeTypes.APPLICATION_PGS;
                    break;
                default:
                    throw new ParserException("Unrecognized codec identifier.");
            }
            if (MimeTypes.isAudio(mimeType)) {
                format = MediaFormat.createAudioFormat(Integer.toString(trackId), mimeType, -1, maxInputSize, durationUs, this.channelCount, this.sampleRate, initializationData, this.language, pcmEncoding);
            } else if (MimeTypes.isVideo(mimeType)) {
                if (this.displayUnit == 0) {
                    this.displayWidth = this.displayWidth == -1 ? this.width : this.displayWidth;
                    this.displayHeight = this.displayHeight == -1 ? this.height : this.displayHeight;
                }
                float pixelWidthHeightRatio = -1.0f;
                if (!(this.displayWidth == -1 || this.displayHeight == -1)) {
                    pixelWidthHeightRatio = ((float) (this.height * this.displayWidth)) / ((float) (this.width * this.displayHeight));
                }
                format = MediaFormat.createVideoFormat(Integer.toString(trackId), mimeType, -1, maxInputSize, durationUs, this.width, this.height, initializationData, -1, pixelWidthHeightRatio);
            } else if (MimeTypes.APPLICATION_SUBRIP.equals(mimeType)) {
                format = MediaFormat.createTextFormat(Integer.toString(trackId), mimeType, -1, durationUs, this.language);
            } else if (MimeTypes.APPLICATION_VOBSUB.equals(mimeType) || MimeTypes.APPLICATION_PGS.equals(mimeType)) {
                format = MediaFormat.createImageFormat(Integer.toString(trackId), mimeType, -1, durationUs, initializationData, this.language);
            } else {
                throw new ParserException("Unexpected MIME type.");
            }
            this.output = output.track(this.number);
            this.output.format(format);
        }

        private static List<byte[]> parseFourCcVc1Private(ParsableByteArray buffer) throws ParserException {
            try {
                buffer.skipBytes(16);
                long compression = buffer.readLittleEndianUnsignedInt();
                if (compression != 826496599) {
                    throw new ParserException("Unsupported FourCC compression type: " + compression);
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

        private static Pair<List<byte[]>, Integer> parseAvcCodecPrivate(ParsableByteArray buffer) throws ParserException {
            try {
                buffer.setPosition(4);
                int nalUnitLengthFieldLength = (buffer.readUnsignedByte() & 3) + 1;
                if (nalUnitLengthFieldLength == 3) {
                    throw new ParserException();
                }
                List<byte[]> initializationData = new ArrayList();
                int numSequenceParameterSets = buffer.readUnsignedByte() & 31;
                for (int i = 0; i < numSequenceParameterSets; i++) {
                    initializationData.add(NalUnitUtil.parseChildNalUnit(buffer));
                }
                int numPictureParameterSets = buffer.readUnsignedByte();
                for (int j = 0; j < numPictureParameterSets; j++) {
                    initializationData.add(NalUnitUtil.parseChildNalUnit(buffer));
                }
                return Pair.create(initializationData, Integer.valueOf(nalUnitLengthFieldLength));
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new ParserException("Error parsing AVC codec private");
            }
        }

        private static Pair<List<byte[]>, Integer> parseHevcCodecPrivate(ParsableByteArray parent) throws ParserException {
            try {
                int i;
                int numberOfNalUnits;
                int j;
                int nalUnitLength;
                parent.setPosition(21);
                int lengthSizeMinusOne = parent.readUnsignedByte() & 3;
                int numberOfArrays = parent.readUnsignedByte();
                int csdLength = 0;
                int csdStartPosition = parent.getPosition();
                for (i = 0; i < numberOfArrays; i++) {
                    parent.skipBytes(1);
                    numberOfNalUnits = parent.readUnsignedShort();
                    for (j = 0; j < numberOfNalUnits; j++) {
                        nalUnitLength = parent.readUnsignedShort();
                        csdLength += nalUnitLength + 4;
                        parent.skipBytes(nalUnitLength);
                    }
                }
                parent.setPosition(csdStartPosition);
                byte[] buffer = new byte[csdLength];
                int bufferPosition = 0;
                for (i = 0; i < numberOfArrays; i++) {
                    parent.skipBytes(1);
                    numberOfNalUnits = parent.readUnsignedShort();
                    for (j = 0; j < numberOfNalUnits; j++) {
                        nalUnitLength = parent.readUnsignedShort();
                        System.arraycopy(NalUnitUtil.NAL_START_CODE, 0, buffer, bufferPosition, NalUnitUtil.NAL_START_CODE.length);
                        bufferPosition += NalUnitUtil.NAL_START_CODE.length;
                        System.arraycopy(parent.data, parent.getPosition(), buffer, bufferPosition, nalUnitLength);
                        bufferPosition += nalUnitLength;
                        parent.skipBytes(nalUnitLength);
                    }
                }
                return Pair.create(csdLength == 0 ? null : Collections.singletonList(buffer), Integer.valueOf(lengthSizeMinusOne + 1));
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new ParserException("Error parsing HEVC codec private");
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
                if (formatTag != WebmExtractor.WAVE_FORMAT_EXTENSIBLE) {
                    return false;
                }
                buffer.setPosition(24);
                if (buffer.readLong() == WebmExtractor.WAVE_SUBFORMAT_PCM.getMostSignificantBits() && buffer.readLong() == WebmExtractor.WAVE_SUBFORMAT_PCM.getLeastSignificantBits()) {
                    return true;
                }
                return false;
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new ParserException("Error parsing MS/ACM codec private");
            }
        }
    }

    private final class InnerEbmlReaderOutput implements EbmlReaderOutput {
        private InnerEbmlReaderOutput() {
        }

        public int getElementType(int id) {
            return WebmExtractor.this.getElementType(id);
        }

        public boolean isLevel1Element(int id) {
            return WebmExtractor.this.isLevel1Element(id);
        }

        public void startMasterElement(int id, long contentPosition, long contentSize) throws ParserException {
            WebmExtractor.this.startMasterElement(id, contentPosition, contentSize);
        }

        public void endMasterElement(int id) throws ParserException {
            WebmExtractor.this.endMasterElement(id);
        }

        public void integerElement(int id, long value) throws ParserException {
            WebmExtractor.this.integerElement(id, value);
        }

        public void floatElement(int id, double value) throws ParserException {
            WebmExtractor.this.floatElement(id, value);
        }

        public void stringElement(int id, String value) throws ParserException {
            WebmExtractor.this.stringElement(id, value);
        }

        public void binaryElement(int id, int contentsSize, ExtractorInput input) throws IOException, InterruptedException {
            WebmExtractor.this.binaryElement(id, contentsSize, input);
        }
    }

    public WebmExtractor() {
        this(new DefaultEbmlReader());
    }

    WebmExtractor(EbmlReader reader) {
        this.segmentContentPosition = -1;
        this.segmentContentSize = -1;
        this.timecodeScale = -1;
        this.durationTimecode = -1;
        this.durationUs = -1;
        this.cuesContentPosition = -1;
        this.seekPositionAfterBuildingCues = -1;
        this.clusterTimecodeUs = -1;
        this.reader = reader;
        this.reader.init(new InnerEbmlReaderOutput());
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

    public boolean sniff(ExtractorInput input) throws IOException, InterruptedException {
        return new Sniffer().sniff(input);
    }

    public void init(ExtractorOutput output) {
        this.extractorOutput = output;
    }

    public void seek() {
        this.clusterTimecodeUs = -1;
        this.blockState = 0;
        this.reader.reset();
        this.varintReader.reset();
        resetSample();
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
        return -1;
    }

    int getElementType(int id) {
        switch (id) {
            case ID_TRACK_TYPE /*131*/:
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
            case ID_DISPLAY_WIDTH /*21680*/:
            case ID_DISPLAY_UNIT /*21682*/:
            case ID_DISPLAY_HEIGHT /*21690*/:
            case ID_CODEC_DELAY /*22186*/:
            case ID_SEEK_PRE_ROLL /*22203*/:
            case ID_AUDIO_BIT_DEPTH /*25188*/:
            case ID_DEFAULT_DURATION /*2352003*/:
            case ID_TIMECODE_SCALE /*2807729*/:
                return 2;
            case ID_CODEC_ID /*134*/:
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
            case ID_CONTENT_ENCODING /*25152*/:
            case ID_CONTENT_ENCODINGS /*28032*/:
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
                return 4;
            case ID_SAMPLING_FREQUENCY /*181*/:
            case ID_DURATION /*17545*/:
                return 5;
            default:
                return 0;
        }
    }

    boolean isLevel1Element(int id) {
        return id == 357149030 || id == ID_CLUSTER || id == ID_CUES || id == ID_TRACKS;
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
                    if (this.cuesContentPosition != -1) {
                        this.seekForCues = true;
                        return;
                    }
                    this.extractorOutput.seekMap(SeekMap.UNSEEKABLE);
                    this.sentSeekMap = true;
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
                if (this.tracks.get(this.currentTrack.number) == null && isCodecSupported(this.currentTrack.codecId)) {
                    this.currentTrack.initializeOutput(this.extractorOutput, this.currentTrack.number, this.durationUs);
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
                if (this.currentTrack.encryptionKeyId == null) {
                    throw new ParserException("Encrypted Track found but ContentEncKeyID was not found");
                } else if (!this.sentDrmInitData) {
                    this.extractorOutput.drmInitData(new Universal(new SchemeInitData(MimeTypes.VIDEO_WEBM, this.currentTrack.encryptionKeyId)));
                    this.sentDrmInitData = true;
                    return;
                } else {
                    return;
                }
            case ID_CONTENT_ENCODINGS /*28032*/:
                if (this.currentTrack.hasContentEncryption && this.currentTrack.sampleStrippedBytes != null) {
                    throw new ParserException("Combining encryption and compression is not supported");
                }
                return;
            case 357149030:
                if (this.timecodeScale == -1) {
                    this.timecodeScale = C.MICROS_PER_SECOND;
                }
                if (this.durationTimecode != -1) {
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
        switch (id) {
            case ID_TRACK_TYPE /*131*/:
                this.currentTrack.type = (int) value;
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
            case ID_DISPLAY_WIDTH /*21680*/:
                this.currentTrack.displayWidth = (int) value;
                return;
            case ID_DISPLAY_UNIT /*21682*/:
                this.currentTrack.displayUnit = (int) value;
                return;
            case ID_DISPLAY_HEIGHT /*21690*/:
                this.currentTrack.displayHeight = (int) value;
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
            default:
                return;
        }
    }

    void stringElement(int id, String value) throws ParserException {
        switch (id) {
            case ID_CODEC_ID /*134*/:
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
                    this.blockDurationUs = -1;
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
                                    r25 = this.blockLacingSampleSizes;
                                    r25[sampleIndex] = r25[sampleIndex] + byteValue;
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
                                        r25 = this.blockLacingSampleSizes;
                                        if (sampleIndex != 0) {
                                            intReadValue += this.blockLacingSampleSizes[sampleIndex - 1];
                                        }
                                        r25[sampleIndex] = intReadValue;
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
                        i2 = C.SAMPLE_FLAG_DECODE_ONLY;
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
                this.currentTrack.encryptionKeyId = new byte[contentSize];
                input.readFully(this.currentTrack.encryptionKeyId, 0, contentSize);
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
            default:
                throw new ParserException("Unexpected id: " + id);
        }
    }

    private void commitSampleToOutput(Track track, long timeUs) {
        if (CODEC_ID_SUBRIP.equals(track.codecId)) {
            writeSubripSample(track);
        }
        track.output.sampleMetadata(timeUs, this.blockFlags, this.sampleBytesWritten, 0, track.encryptionKeyId);
        this.sampleRead = true;
        resetSample();
    }

    private void resetSample() {
        this.sampleBytesRead = 0;
        this.sampleBytesWritten = 0;
        this.sampleCurrentNalBytesRemaining = 0;
        this.sampleEncodingHandled = false;
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
            int sizeWithPrefix = SUBRIP_PREFIX.length + size;
            if (this.subripSample.capacity() < sizeWithPrefix) {
                this.subripSample.data = Arrays.copyOf(SUBRIP_PREFIX, sizeWithPrefix + size);
            }
            input.readFully(this.subripSample.data, SUBRIP_PREFIX.length, size);
            this.subripSample.setPosition(0);
            this.subripSample.setLimit(sizeWithPrefix);
            return;
        }
        TrackOutput output = track.output;
        if (!this.sampleEncodingHandled) {
            if (track.hasContentEncryption) {
                this.blockFlags &= -3;
                input.readFully(this.scratch.data, 0, 1);
                this.sampleBytesRead++;
                if ((this.scratch.data[0] & 128) == 128) {
                    throw new ParserException("Extension bit is set in signal byte");
                } else if ((this.scratch.data[0] & 1) == 1) {
                    this.scratch.data[0] = (byte) 8;
                    this.scratch.setPosition(0);
                    output.sampleData(this.scratch, 1);
                    this.sampleBytesWritten++;
                    this.blockFlags |= 2;
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

    private void writeSubripSample(Track track) {
        setSubripSampleEndTimecode(this.subripSample.data, this.blockDurationUs);
        track.output.sampleData(this.subripSample, this.subripSample.limit());
        this.sampleBytesWritten += this.subripSample.limit();
    }

    private static void setSubripSampleEndTimecode(byte[] subripSampleData, long timeUs) {
        byte[] timeCodeData;
        if (timeUs == -1) {
            timeCodeData = SUBRIP_TIMECODE_EMPTY;
        } else {
            timeUs -= ((long) ((int) (timeUs / 3600000000L))) * 3600000000L;
            timeUs -= (long) (60000000 * ((int) (timeUs / 60000000)));
            int milliseconds = (int) ((timeUs - ((long) (1000000 * ((int) (timeUs / C.MICROS_PER_SECOND))))) / 1000);
            timeCodeData = String.format(Locale.US, "%02d:%02d:%02d,%03d", new Object[]{Integer.valueOf(hours), Integer.valueOf(minutes), Integer.valueOf((int) (timeUs / C.MICROS_PER_SECOND)), Integer.valueOf(milliseconds)}).getBytes();
        }
        System.arraycopy(timeCodeData, 0, subripSampleData, 19, 12);
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
        if (this.segmentContentPosition == -1 || this.durationUs == -1 || this.cueTimesUs == null || this.cueTimesUs.size() == 0 || this.cueClusterPositions == null || this.cueClusterPositions.size() != this.cueTimesUs.size()) {
            this.cueTimesUs = null;
            this.cueClusterPositions = null;
            return SeekMap.UNSEEKABLE;
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
        if (this.timecodeScale == -1) {
            throw new ParserException("Can't scale timecode prior to timecodeScale being set.");
        }
        return Util.scaleLargeTimestamp(unscaledTimecode, this.timecodeScale, 1000);
    }

    private static boolean isCodecSupported(String codecId) {
        if (CODEC_ID_VP8.equals(codecId) || CODEC_ID_VP9.equals(codecId) || CODEC_ID_MPEG2.equals(codecId) || CODEC_ID_MPEG4_SP.equals(codecId) || CODEC_ID_MPEG4_ASP.equals(codecId) || CODEC_ID_MPEG4_AP.equals(codecId) || CODEC_ID_H264.equals(codecId) || CODEC_ID_H265.equals(codecId) || CODEC_ID_FOURCC.equals(codecId) || CODEC_ID_OPUS.equals(codecId) || CODEC_ID_VORBIS.equals(codecId) || CODEC_ID_AAC.equals(codecId) || CODEC_ID_MP3.equals(codecId) || CODEC_ID_AC3.equals(codecId) || CODEC_ID_E_AC3.equals(codecId) || CODEC_ID_TRUEHD.equals(codecId) || CODEC_ID_DTS.equals(codecId) || CODEC_ID_DTS_EXPRESS.equals(codecId) || CODEC_ID_DTS_LOSSLESS.equals(codecId) || CODEC_ID_FLAC.equals(codecId) || CODEC_ID_ACM.equals(codecId) || CODEC_ID_PCM_INT_LIT.equals(codecId) || CODEC_ID_SUBRIP.equals(codecId) || CODEC_ID_VOBSUB.equals(codecId) || CODEC_ID_PGS.equals(codecId)) {
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

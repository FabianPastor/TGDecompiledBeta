package org.telegram.messenger.exoplayer2.source.hls.playlist;

import android.net.Uri;
import android.util.Base64;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.exoplayer2.C0539C;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.drm.DrmInitData;
import org.telegram.messenger.exoplayer2.drm.DrmInitData.SchemeData;
import org.telegram.messenger.exoplayer2.source.UnrecognizedInputFormatException;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMasterPlaylist.HlsUrl;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMediaPlaylist.Segment;
import org.telegram.messenger.exoplayer2.upstream.ParsingLoadable.Parser;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.exoplayer2.util.Util;

public final class HlsPlaylistParser implements Parser<HlsPlaylist> {
    private static final String ATTR_CLOSED_CAPTIONS_NONE = "CLOSED-CAPTIONS=NONE";
    private static final String BOOLEAN_FALSE = "NO";
    private static final String BOOLEAN_TRUE = "YES";
    private static final String KEYFORMAT_IDENTITY = "identity";
    private static final String KEYFORMAT_WIDEVINE_PSSH_BINARY = "urn:uuid:edef8ba9-79d6-4ace-a3c8-27dcd51d21ed";
    private static final String KEYFORMAT_WIDEVINE_PSSH_JSON = "com.widevine";
    private static final String METHOD_AES_128 = "AES-128";
    private static final String METHOD_NONE = "NONE";
    private static final String METHOD_SAMPLE_AES = "SAMPLE-AES";
    private static final String METHOD_SAMPLE_AES_CENC = "SAMPLE-AES-CENC";
    private static final String METHOD_SAMPLE_AES_CTR = "SAMPLE-AES-CTR";
    private static final String PLAYLIST_HEADER = "#EXTM3U";
    private static final Pattern REGEX_ATTR_BYTERANGE = Pattern.compile("BYTERANGE=\"(\\d+(?:@\\d+)?)\\b\"");
    private static final Pattern REGEX_AUDIO = Pattern.compile("AUDIO=\"(.+?)\"");
    private static final Pattern REGEX_AUTOSELECT = compileBooleanAttrPattern("AUTOSELECT");
    private static final Pattern REGEX_AVERAGE_BANDWIDTH = Pattern.compile("AVERAGE-BANDWIDTH=(\\d+)\\b");
    private static final Pattern REGEX_BANDWIDTH = Pattern.compile("[^-]BANDWIDTH=(\\d+)\\b");
    private static final Pattern REGEX_BYTERANGE = Pattern.compile("#EXT-X-BYTERANGE:(\\d+(?:@\\d+)?)\\b");
    private static final Pattern REGEX_CODECS = Pattern.compile("CODECS=\"(.+?)\"");
    private static final Pattern REGEX_DEFAULT = compileBooleanAttrPattern("DEFAULT");
    private static final Pattern REGEX_FORCED = compileBooleanAttrPattern("FORCED");
    private static final Pattern REGEX_FRAME_RATE = Pattern.compile("FRAME-RATE=([\\d\\.]+)\\b");
    private static final Pattern REGEX_GROUP_ID = Pattern.compile("GROUP-ID=\"(.+?)\"");
    private static final Pattern REGEX_INSTREAM_ID = Pattern.compile("INSTREAM-ID=\"((?:CC|SERVICE)\\d+)\"");
    private static final Pattern REGEX_IV = Pattern.compile("IV=([^,.*]+)");
    private static final Pattern REGEX_KEYFORMAT = Pattern.compile("KEYFORMAT=\"(.+?)\"");
    private static final Pattern REGEX_LANGUAGE = Pattern.compile("LANGUAGE=\"(.+?)\"");
    private static final Pattern REGEX_MEDIA_DURATION = Pattern.compile("#EXTINF:([\\d\\.]+)\\b");
    private static final Pattern REGEX_MEDIA_SEQUENCE = Pattern.compile("#EXT-X-MEDIA-SEQUENCE:(\\d+)\\b");
    private static final Pattern REGEX_METHOD = Pattern.compile("METHOD=(NONE|AES-128|SAMPLE-AES|SAMPLE-AES-CENC|SAMPLE-AES-CTR)");
    private static final Pattern REGEX_NAME = Pattern.compile("NAME=\"(.+?)\"");
    private static final Pattern REGEX_PLAYLIST_TYPE = Pattern.compile("#EXT-X-PLAYLIST-TYPE:(.+)\\b");
    private static final Pattern REGEX_RESOLUTION = Pattern.compile("RESOLUTION=(\\d+x\\d+)");
    private static final Pattern REGEX_TARGET_DURATION = Pattern.compile("#EXT-X-TARGETDURATION:(\\d+)\\b");
    private static final Pattern REGEX_TIME_OFFSET = Pattern.compile("TIME-OFFSET=(-?[\\d\\.]+)\\b");
    private static final Pattern REGEX_TYPE = Pattern.compile("TYPE=(AUDIO|VIDEO|SUBTITLES|CLOSED-CAPTIONS)");
    private static final Pattern REGEX_URI = Pattern.compile("URI=\"(.+?)\"");
    private static final Pattern REGEX_VERSION = Pattern.compile("#EXT-X-VERSION:(\\d+)\\b");
    private static final String TAG_BYTERANGE = "#EXT-X-BYTERANGE";
    private static final String TAG_DISCONTINUITY = "#EXT-X-DISCONTINUITY";
    private static final String TAG_DISCONTINUITY_SEQUENCE = "#EXT-X-DISCONTINUITY-SEQUENCE";
    private static final String TAG_ENDLIST = "#EXT-X-ENDLIST";
    private static final String TAG_INDEPENDENT_SEGMENTS = "#EXT-X-INDEPENDENT-SEGMENTS";
    private static final String TAG_INIT_SEGMENT = "#EXT-X-MAP";
    private static final String TAG_KEY = "#EXT-X-KEY";
    private static final String TAG_MEDIA = "#EXT-X-MEDIA";
    private static final String TAG_MEDIA_DURATION = "#EXTINF";
    private static final String TAG_MEDIA_SEQUENCE = "#EXT-X-MEDIA-SEQUENCE";
    private static final String TAG_PLAYLIST_TYPE = "#EXT-X-PLAYLIST-TYPE";
    private static final String TAG_PREFIX = "#EXT";
    private static final String TAG_PROGRAM_DATE_TIME = "#EXT-X-PROGRAM-DATE-TIME";
    private static final String TAG_START = "#EXT-X-START";
    private static final String TAG_STREAM_INF = "#EXT-X-STREAM-INF";
    private static final String TAG_TARGET_DURATION = "#EXT-X-TARGETDURATION";
    private static final String TAG_VERSION = "#EXT-X-VERSION";
    private static final String TYPE_AUDIO = "AUDIO";
    private static final String TYPE_CLOSED_CAPTIONS = "CLOSED-CAPTIONS";
    private static final String TYPE_SUBTITLES = "SUBTITLES";
    private static final String TYPE_VIDEO = "VIDEO";

    private static class LineIterator {
        private final Queue<String> extraLines;
        private String next;
        private final BufferedReader reader;

        public LineIterator(Queue<String> extraLines, BufferedReader reader) {
            this.extraLines = extraLines;
            this.reader = reader;
        }

        public boolean hasNext() throws IOException {
            if (this.next != null) {
                return true;
            }
            if (this.extraLines.isEmpty()) {
                do {
                    String readLine = this.reader.readLine();
                    this.next = readLine;
                    if (readLine == null) {
                        return false;
                    }
                    this.next = this.next.trim();
                } while (this.next.isEmpty());
                return true;
            }
            this.next = (String) this.extraLines.poll();
            return true;
        }

        public String next() throws IOException {
            if (!hasNext()) {
                return null;
            }
            String result = this.next;
            this.next = null;
            return result;
        }
    }

    public HlsPlaylist parse(Uri uri, InputStream inputStream) throws IOException {
        Closeable reader = new BufferedReader(new InputStreamReader(inputStream));
        Queue<String> extraLines = new ArrayDeque();
        try {
            if (checkPlaylistHeader(reader)) {
                String readLine;
                HlsPlaylist startsWith;
                while (true) {
                    readLine = reader.readLine();
                    String line = readLine;
                    if (readLine != null) {
                        readLine = line.trim();
                        if (!readLine.isEmpty()) {
                            startsWith = readLine.startsWith(TAG_STREAM_INF);
                            if (startsWith == null) {
                                if (readLine.startsWith(TAG_TARGET_DURATION) || readLine.startsWith(TAG_MEDIA_SEQUENCE) || readLine.startsWith(TAG_MEDIA_DURATION) || readLine.startsWith(TAG_KEY) || readLine.startsWith(TAG_BYTERANGE) || readLine.equals(TAG_DISCONTINUITY) || readLine.equals(TAG_DISCONTINUITY_SEQUENCE)) {
                                    break;
                                } else if (readLine.equals(TAG_ENDLIST)) {
                                    break;
                                } else {
                                    extraLines.add(readLine);
                                }
                            } else {
                                extraLines.add(readLine);
                                startsWith = parseMasterPlaylist(new LineIterator(extraLines, reader), uri.toString());
                                Util.closeQuietly(reader);
                                return startsWith;
                            }
                        }
                    } else {
                        Util.closeQuietly(reader);
                        throw new ParserException("Failed to parse the playlist, could not identify any tags.");
                    }
                }
                extraLines.add(readLine);
                startsWith = parseMediaPlaylist(new LineIterator(extraLines, reader), uri.toString());
                return startsWith;
            }
            throw new UnrecognizedInputFormatException("Input does not start with the #EXTM3U header.", uri);
        } finally {
            Util.closeQuietly(reader);
        }
    }

    private static boolean checkPlaylistHeader(BufferedReader reader) throws IOException {
        int last = reader.read();
        if (last == 239) {
            if (reader.read() == 187) {
                if (reader.read() == 191) {
                    last = reader.read();
                }
            }
            return false;
        }
        char last2 = skipIgnorableWhitespace(reader, true, last);
        int playlistHeaderLength = PLAYLIST_HEADER.length();
        char last3 = last2;
        for (last = 0; last < playlistHeaderLength; last++) {
            if (last3 != PLAYLIST_HEADER.charAt(last)) {
                return false;
            }
            last3 = reader.read();
        }
        return Util.isLinebreak(skipIgnorableWhitespace(reader, false, last3));
    }

    private static int skipIgnorableWhitespace(BufferedReader reader, boolean skipLinebreaks, int c) throws IOException {
        while (c != -1 && Character.isWhitespace(c) && (skipLinebreaks || !Util.isLinebreak(c))) {
            c = reader.read();
        }
        return c;
    }

    private static HlsMasterPlaylist parseMasterPlaylist(LineIterator iterator, String baseUri) throws IOException {
        Format format;
        List<Format> list;
        ArrayList<String> mediaTags;
        ArrayList<HlsUrl> variants;
        HashSet<String> variantUrls = new HashSet();
        HashMap<String, String> audioGroupIdToCodecs = new HashMap();
        ArrayList<HlsUrl> variants2 = new ArrayList();
        ArrayList<HlsUrl> audios = new ArrayList();
        ArrayList<HlsUrl> subtitles = new ArrayList();
        ArrayList<String> mediaTags2 = new ArrayList();
        ArrayList<String> tags = new ArrayList();
        Format muxedAudioFormat = null;
        List<Format> muxedCaptionFormats = null;
        boolean noClosedCaptions = false;
        while (true) {
            boolean noClosedCaptions2 = noClosedCaptions;
            if (!iterator.hasNext()) {
                break;
            }
            HashSet<String> hashSet;
            String codecs;
            String resolutionString;
            String frameRateString;
            String line = iterator.next();
            if (line.startsWith(TAG_PREFIX)) {
                tags.add(line);
            }
            if (line.startsWith(TAG_MEDIA)) {
                mediaTags2.add(line);
                hashSet = variantUrls;
                format = muxedAudioFormat;
                list = muxedCaptionFormats;
            } else if (line.startsWith(TAG_STREAM_INF)) {
                int muxedAudioFormat2;
                int height;
                boolean noClosedCaptions3 = line.contains(ATTR_CLOSED_CAPTIONS_NONE) | noClosedCaptions2;
                int bitrate = parseIntAttr(line, REGEX_BANDWIDTH);
                noClosedCaptions2 = parseOptionalStringAttr(line, REGEX_AVERAGE_BANDWIDTH);
                if (noClosedCaptions2) {
                    bitrate = Integer.parseInt(noClosedCaptions2);
                }
                codecs = parseOptionalStringAttr(line, REGEX_CODECS);
                resolutionString = parseOptionalStringAttr(line, REGEX_RESOLUTION);
                if (resolutionString != null) {
                    String[] widthAndHeight = resolutionString.split("x");
                    format = muxedAudioFormat;
                    muxedAudioFormat2 = Integer.parseInt(widthAndHeight[0]);
                    list = muxedCaptionFormats;
                    muxedCaptionFormats = Integer.parseInt(widthAndHeight[1]);
                    if (muxedAudioFormat2 <= null || muxedCaptionFormats <= null) {
                        muxedAudioFormat2 = -1;
                        muxedCaptionFormats = -1;
                    }
                    height = muxedCaptionFormats;
                } else {
                    format = muxedAudioFormat;
                    list = muxedCaptionFormats;
                    muxedAudioFormat2 = -1;
                    height = -1;
                }
                float frameRate = -1.0f;
                frameRateString = parseOptionalStringAttr(line, REGEX_FRAME_RATE);
                if (frameRateString != null) {
                    frameRate = Float.parseFloat(frameRateString);
                }
                frameRateString = parseOptionalStringAttr(line, REGEX_AUDIO);
                if (frameRateString == null || codecs == null) {
                } else {
                    audioGroupIdToCodecs.put(frameRateString, Util.getCodecsOfType(codecs, 1));
                }
                line = iterator.next();
                if (variantUrls.add(line)) {
                    hashSet = variantUrls;
                    variants2.add(new HlsUrl(line, Format.createVideoContainerFormat(Integer.toString(variants2.size()), MimeTypes.APPLICATION_M3U8, null, codecs, bitrate, muxedAudioFormat2, height, frameRate, null, 0)));
                } else {
                    hashSet = variantUrls;
                }
                noClosedCaptions = noClosedCaptions3;
                muxedAudioFormat = format;
                muxedCaptionFormats = list;
                variantUrls = hashSet;
            } else {
                hashSet = variantUrls;
                format = muxedAudioFormat;
                list = muxedCaptionFormats;
            }
            noClosedCaptions = noClosedCaptions2;
            muxedAudioFormat = format;
            muxedCaptionFormats = list;
            variantUrls = hashSet;
        }
        format = muxedAudioFormat;
        list = muxedCaptionFormats;
        int i = 0;
        while (i < mediaTags2.size()) {
            Object obj;
            String variants3;
            Format format2;
            int accessibilityChannel;
            List<Format> muxedCaptionFormats2;
            String line2 = (String) mediaTags2.get(i);
            int selectionFlags = parseSelectionFlags(line2);
            frameRateString = parseOptionalStringAttr(line2, REGEX_URI);
            codecs = parseStringAttr(line2, REGEX_NAME);
            resolutionString = parseOptionalStringAttr(line2, REGEX_LANGUAGE);
            String groupId = parseOptionalStringAttr(line2, REGEX_GROUP_ID);
            String parseStringAttr = parseStringAttr(line2, REGEX_TYPE);
            int hashCode = parseStringAttr.hashCode();
            mediaTags = mediaTags2;
            variants = variants2;
            if (hashCode != -959297733) {
                if (hashCode != -333210994) {
                    if (hashCode == 62628790) {
                        if (parseStringAttr.equals(TYPE_AUDIO)) {
                            obj = null;
                            switch (obj) {
                                case null:
                                    variants3 = (String) audioGroupIdToCodecs.get(groupId);
                                    format2 = Format.createAudioContainerFormat(codecs, MimeTypes.APPLICATION_M3U8, variants3 == null ? MimeTypes.getMediaMimeType(variants3) : null, variants3, -1, -1, -1, null, selectionFlags, resolutionString);
                                    if (frameRateString != null) {
                                        audios.add(new HlsUrl(frameRateString, format2));
                                        break;
                                    }
                                    format = format2;
                                    break;
                                case 1:
                                    subtitles.add(new HlsUrl(frameRateString, Format.createTextContainerFormat(codecs, MimeTypes.APPLICATION_M3U8, MimeTypes.TEXT_VTT, null, -1, selectionFlags, resolutionString)));
                                    break;
                                case 2:
                                    line = parseStringAttr(line2, REGEX_INSTREAM_ID);
                                    if (line.startsWith("CC")) {
                                        variants2 = MimeTypes.APPLICATION_CEA708;
                                        accessibilityChannel = Integer.parseInt(line.substring(7));
                                    } else {
                                        parseStringAttr = MimeTypes.APPLICATION_CEA608;
                                        accessibilityChannel = Integer.parseInt(line.substring(2));
                                        variants2 = parseStringAttr;
                                    }
                                    if (list != null) {
                                        muxedCaptionFormats2 = new ArrayList();
                                    } else {
                                        muxedCaptionFormats2 = list;
                                    }
                                    muxedCaptionFormats2.add(Format.createTextContainerFormat(codecs, null, variants2, null, -1, selectionFlags, resolutionString, accessibilityChannel));
                                    list = muxedCaptionFormats2;
                                    break;
                                default:
                                    break;
                            }
                            i++;
                            mediaTags2 = mediaTags;
                            variants2 = variants;
                        }
                    }
                } else if (parseStringAttr.equals(TYPE_CLOSED_CAPTIONS)) {
                    obj = 2;
                    switch (obj) {
                        case null:
                            variants3 = (String) audioGroupIdToCodecs.get(groupId);
                            if (variants3 == null) {
                            }
                            format2 = Format.createAudioContainerFormat(codecs, MimeTypes.APPLICATION_M3U8, variants3 == null ? MimeTypes.getMediaMimeType(variants3) : null, variants3, -1, -1, -1, null, selectionFlags, resolutionString);
                            if (frameRateString != null) {
                                format = format2;
                                break;
                            }
                            audios.add(new HlsUrl(frameRateString, format2));
                            break;
                        case 1:
                            subtitles.add(new HlsUrl(frameRateString, Format.createTextContainerFormat(codecs, MimeTypes.APPLICATION_M3U8, MimeTypes.TEXT_VTT, null, -1, selectionFlags, resolutionString)));
                            break;
                        case 2:
                            line = parseStringAttr(line2, REGEX_INSTREAM_ID);
                            if (line.startsWith("CC")) {
                                variants2 = MimeTypes.APPLICATION_CEA708;
                                accessibilityChannel = Integer.parseInt(line.substring(7));
                            } else {
                                parseStringAttr = MimeTypes.APPLICATION_CEA608;
                                accessibilityChannel = Integer.parseInt(line.substring(2));
                                variants2 = parseStringAttr;
                            }
                            if (list != null) {
                                muxedCaptionFormats2 = list;
                            } else {
                                muxedCaptionFormats2 = new ArrayList();
                            }
                            muxedCaptionFormats2.add(Format.createTextContainerFormat(codecs, null, variants2, null, -1, selectionFlags, resolutionString, accessibilityChannel));
                            list = muxedCaptionFormats2;
                            break;
                        default:
                            break;
                    }
                    i++;
                    mediaTags2 = mediaTags;
                    variants2 = variants;
                }
            } else if (parseStringAttr.equals(TYPE_SUBTITLES)) {
                obj = 1;
                switch (obj) {
                    case null:
                        variants3 = (String) audioGroupIdToCodecs.get(groupId);
                        if (variants3 == null) {
                        }
                        format2 = Format.createAudioContainerFormat(codecs, MimeTypes.APPLICATION_M3U8, variants3 == null ? MimeTypes.getMediaMimeType(variants3) : null, variants3, -1, -1, -1, null, selectionFlags, resolutionString);
                        if (frameRateString != null) {
                            audios.add(new HlsUrl(frameRateString, format2));
                            break;
                        }
                        format = format2;
                        break;
                    case 1:
                        subtitles.add(new HlsUrl(frameRateString, Format.createTextContainerFormat(codecs, MimeTypes.APPLICATION_M3U8, MimeTypes.TEXT_VTT, null, -1, selectionFlags, resolutionString)));
                        break;
                    case 2:
                        line = parseStringAttr(line2, REGEX_INSTREAM_ID);
                        if (line.startsWith("CC")) {
                            parseStringAttr = MimeTypes.APPLICATION_CEA608;
                            accessibilityChannel = Integer.parseInt(line.substring(2));
                            variants2 = parseStringAttr;
                        } else {
                            variants2 = MimeTypes.APPLICATION_CEA708;
                            accessibilityChannel = Integer.parseInt(line.substring(7));
                        }
                        if (list != null) {
                            muxedCaptionFormats2 = new ArrayList();
                        } else {
                            muxedCaptionFormats2 = list;
                        }
                        muxedCaptionFormats2.add(Format.createTextContainerFormat(codecs, null, variants2, null, -1, selectionFlags, resolutionString, accessibilityChannel));
                        list = muxedCaptionFormats2;
                        break;
                    default:
                        break;
                }
                i++;
                mediaTags2 = mediaTags;
                variants2 = variants;
            }
            obj = -1;
            switch (obj) {
                case null:
                    variants3 = (String) audioGroupIdToCodecs.get(groupId);
                    if (variants3 == null) {
                    }
                    format2 = Format.createAudioContainerFormat(codecs, MimeTypes.APPLICATION_M3U8, variants3 == null ? MimeTypes.getMediaMimeType(variants3) : null, variants3, -1, -1, -1, null, selectionFlags, resolutionString);
                    if (frameRateString != null) {
                        format = format2;
                        break;
                    }
                    audios.add(new HlsUrl(frameRateString, format2));
                    break;
                case 1:
                    subtitles.add(new HlsUrl(frameRateString, Format.createTextContainerFormat(codecs, MimeTypes.APPLICATION_M3U8, MimeTypes.TEXT_VTT, null, -1, selectionFlags, resolutionString)));
                    break;
                case 2:
                    line = parseStringAttr(line2, REGEX_INSTREAM_ID);
                    if (line.startsWith("CC")) {
                        variants2 = MimeTypes.APPLICATION_CEA708;
                        accessibilityChannel = Integer.parseInt(line.substring(7));
                    } else {
                        parseStringAttr = MimeTypes.APPLICATION_CEA608;
                        accessibilityChannel = Integer.parseInt(line.substring(2));
                        variants2 = parseStringAttr;
                    }
                    if (list != null) {
                        muxedCaptionFormats2 = list;
                    } else {
                        muxedCaptionFormats2 = new ArrayList();
                    }
                    muxedCaptionFormats2.add(Format.createTextContainerFormat(codecs, null, variants2, null, -1, selectionFlags, resolutionString, accessibilityChannel));
                    list = muxedCaptionFormats2;
                    break;
                default:
                    break;
            }
            i++;
            mediaTags2 = mediaTags;
            variants2 = variants;
        }
        variants = variants2;
        mediaTags = mediaTags2;
        if (noClosedCaptions2) {
            list = Collections.emptyList();
        }
        return new HlsMasterPlaylist(baseUri, tags, variants, audios, subtitles, format, list);
    }

    private static int parseSelectionFlags(String line) {
        int i = 0;
        int parseBooleanAttribute = parseBooleanAttribute(line, REGEX_DEFAULT, false) | (parseBooleanAttribute(line, REGEX_FORCED, false) ? 2 : 0);
        if (parseBooleanAttribute(line, REGEX_AUTOSELECT, false)) {
            i = 4;
        }
        return parseBooleanAttribute | i;
    }

    private static HlsMediaPlaylist parseMediaPlaylist(LineIterator iterator, String baseUri) throws IOException {
        boolean z;
        int i;
        int playlistType = 0;
        long startOffsetUs = C0539C.TIME_UNSET;
        List<Segment> segments = new ArrayList();
        List<String> tags = new ArrayList();
        DrmInitData drmInitData = null;
        long targetDurationUs = C0539C.TIME_UNSET;
        boolean hasEndTag = false;
        Segment initializationSegment = null;
        long segmentDurationUs = 0;
        boolean hasDiscontinuitySequence = false;
        int playlistDiscontinuitySequence = 0;
        int relativeDiscontinuitySequence = 0;
        long segmentStartTimeUs = 0;
        long segmentByteRangeOffset = 0;
        long segmentByteRangeLength = -1;
        int segmentMediaSequence = 0;
        String encryptionKeyUri = null;
        String encryptionIV = null;
        int version = 1;
        boolean hasIndependentSegmentsTag = false;
        int mediaSequence = 0;
        long playlistStartTimeUs = 0;
        while (iterator.hasNext()) {
            String line = iterator.next();
            if (line.startsWith(TAG_PREFIX)) {
                tags.add(line);
            }
            String playlistTypeString;
            if (line.startsWith(TAG_PLAYLIST_TYPE)) {
                playlistTypeString = parseStringAttr(line, REGEX_PLAYLIST_TYPE);
                if ("VOD".equals(playlistTypeString)) {
                    playlistType = 1;
                } else if ("EVENT".equals(playlistTypeString)) {
                    playlistType = 2;
                }
            } else if (line.startsWith(TAG_START)) {
                startOffsetUs = (long) (parseDoubleAttr(line, REGEX_TIME_OFFSET) * 1000000.0d);
            } else {
                String uri;
                if (line.startsWith(TAG_INIT_SEGMENT)) {
                    uri = parseStringAttr(line, REGEX_URI);
                    playlistTypeString = parseOptionalStringAttr(line, REGEX_ATTR_BYTERANGE);
                    if (playlistTypeString != null) {
                        z = hasIndependentSegmentsTag;
                        hasIndependentSegmentsTag = playlistTypeString.split("@");
                        segmentByteRangeLength = Long.parseLong(hasIndependentSegmentsTag[0]);
                        i = version;
                        if (hasIndependentSegmentsTag.length > 1) {
                            segmentByteRangeOffset = Long.parseLong(hasIndependentSegmentsTag[1]);
                        }
                    } else {
                        i = version;
                        z = hasIndependentSegmentsTag;
                        String str = playlistTypeString;
                    }
                    initializationSegment = new Segment(uri, segmentByteRangeOffset, segmentByteRangeLength);
                    segmentByteRangeOffset = 0;
                    segmentByteRangeLength = -1;
                } else {
                    i = version;
                    z = hasIndependentSegmentsTag;
                    if (line.startsWith(TAG_TARGET_DURATION)) {
                        targetDurationUs = ((long) parseIntAttr(line, REGEX_TARGET_DURATION)) * C0539C.MICROS_PER_SECOND;
                    } else if (line.startsWith(TAG_MEDIA_SEQUENCE)) {
                        mediaSequence = parseIntAttr(line, REGEX_MEDIA_SEQUENCE);
                        segmentMediaSequence = mediaSequence;
                    } else if (line.startsWith(TAG_VERSION)) {
                        version = parseIntAttr(line, REGEX_VERSION);
                        hasIndependentSegmentsTag = z;
                    } else if (line.startsWith(TAG_MEDIA_DURATION)) {
                        segmentDurationUs = (long) (parseDoubleAttr(line, REGEX_MEDIA_DURATION) * 4696837146684686336L);
                    } else {
                        int i2;
                        if (line.startsWith(TAG_KEY)) {
                            version = parseOptionalStringAttr(line, REGEX_METHOD);
                            String keyFormat = parseOptionalStringAttr(line, REGEX_KEYFORMAT);
                            String encryptionKeyUri2 = null;
                            if (METHOD_NONE.equals(version)) {
                                i2 = mediaSequence;
                                encryptionIV = null;
                                encryptionKeyUri = encryptionKeyUri2;
                            } else {
                                String encryptionIV2;
                                playlistTypeString = parseOptionalStringAttr(line, REGEX_IV);
                                String str2;
                                if (KEYFORMAT_IDENTITY.equals(keyFormat)) {
                                    i2 = mediaSequence;
                                    str2 = keyFormat;
                                    encryptionIV2 = playlistTypeString;
                                } else if (keyFormat == null) {
                                    i2 = mediaSequence;
                                    str2 = keyFormat;
                                    encryptionIV2 = playlistTypeString;
                                } else if (version == 0 || parseWidevineSchemeData(line, keyFormat) == null) {
                                    i2 = mediaSequence;
                                    encryptionIV2 = playlistTypeString;
                                    encryptionKeyUri = encryptionKeyUri2;
                                    encryptionIV = encryptionIV2;
                                } else {
                                    encryptionIV2 = playlistTypeString;
                                    if (!METHOD_SAMPLE_AES_CENC.equals(version)) {
                                        if (!METHOD_SAMPLE_AES_CTR.equals(version)) {
                                            playlistTypeString = C0539C.CENC_TYPE_cbcs;
                                            i2 = mediaSequence;
                                            drmInitData = new DrmInitData(playlistTypeString, uri);
                                            encryptionKeyUri = encryptionKeyUri2;
                                            encryptionIV = encryptionIV2;
                                        }
                                    }
                                    playlistTypeString = C0539C.CENC_TYPE_cenc;
                                    i2 = mediaSequence;
                                    drmInitData = new DrmInitData(playlistTypeString, uri);
                                    encryptionKeyUri = encryptionKeyUri2;
                                    encryptionIV = encryptionIV2;
                                }
                                if (METHOD_AES_128.equals(version) != 0) {
                                    encryptionKeyUri = parseStringAttr(line, REGEX_URI);
                                    encryptionIV = encryptionIV2;
                                }
                                encryptionKeyUri = encryptionKeyUri2;
                                encryptionIV = encryptionIV2;
                            }
                        } else {
                            i2 = mediaSequence;
                            if (line.startsWith(TAG_BYTERANGE) != 0) {
                                String[] splitByteRange = parseStringAttr(line, REGEX_BYTERANGE).split("@");
                                segmentByteRangeLength = Long.parseLong(splitByteRange[0]);
                                if (splitByteRange.length > 1) {
                                    segmentByteRangeOffset = Long.parseLong(splitByteRange[1]);
                                }
                            } else if (line.startsWith(TAG_DISCONTINUITY_SEQUENCE) != 0) {
                                hasDiscontinuitySequence = true;
                                playlistDiscontinuitySequence = Integer.parseInt(line.substring(line.indexOf(58) + 1));
                            } else if (line.equals(TAG_DISCONTINUITY) != 0) {
                                relativeDiscontinuitySequence++;
                            } else if (line.startsWith(TAG_PROGRAM_DATE_TIME) != 0) {
                                if (playlistStartTimeUs == 0) {
                                    playlistStartTimeUs = C0539C.msToUs(Util.parseXsDateTime(line.substring(line.indexOf(58) + 1))) - segmentStartTimeUs;
                                }
                            } else if (line.startsWith("#") == 0) {
                                long segmentByteRangeOffset2;
                                if (encryptionKeyUri == null) {
                                    mediaSequence = 0;
                                } else if (encryptionIV != null) {
                                    mediaSequence = encryptionIV;
                                } else {
                                    mediaSequence = Integer.toHexString(segmentMediaSequence);
                                }
                                String segmentEncryptionIV = mediaSequence;
                                segmentMediaSequence++;
                                if (segmentByteRangeLength == -1) {
                                    segmentByteRangeOffset2 = 0;
                                } else {
                                    segmentByteRangeOffset2 = segmentByteRangeOffset;
                                }
                                segments.add(new Segment(line, segmentDurationUs, relativeDiscontinuitySequence, segmentStartTimeUs, encryptionKeyUri, segmentEncryptionIV, segmentByteRangeOffset2, segmentByteRangeLength));
                                long segmentStartTimeUs2 = segmentStartTimeUs + segmentDurationUs;
                                segmentDurationUs = 0;
                                if (segmentByteRangeLength != -1) {
                                    segmentByteRangeOffset = segmentByteRangeOffset2 + segmentByteRangeLength;
                                } else {
                                    segmentByteRangeOffset = segmentByteRangeOffset2;
                                }
                                segmentByteRangeLength = -1;
                                segmentStartTimeUs = segmentStartTimeUs2;
                            } else if (line.equals(TAG_INDEPENDENT_SEGMENTS) != 0) {
                                hasIndependentSegmentsTag = true;
                                version = i;
                                mediaSequence = i2;
                            } else if (line.equals(TAG_ENDLIST) != 0) {
                                hasEndTag = true;
                            }
                        }
                        hasIndependentSegmentsTag = z;
                        version = i;
                        mediaSequence = i2;
                    }
                }
                hasIndependentSegmentsTag = z;
                version = i;
            }
        }
        i = version;
        z = hasIndependentSegmentsTag;
        return new HlsMediaPlaylist(playlistType, baseUri, tags, startOffsetUs, playlistStartTimeUs, hasDiscontinuitySequence, playlistDiscontinuitySequence, mediaSequence, i, targetDurationUs, z, hasEndTag, playlistStartTimeUs != 0, drmInitData, initializationSegment, segments);
    }

    private static SchemeData parseWidevineSchemeData(String line, String keyFormat) throws ParserException {
        if (KEYFORMAT_WIDEVINE_PSSH_BINARY.equals(keyFormat)) {
            String uriString = parseStringAttr(line, REGEX_URI);
            return new SchemeData(C0539C.WIDEVINE_UUID, MimeTypes.VIDEO_MP4, Base64.decode(uriString.substring(uriString.indexOf(44)), 0));
        } else if (!KEYFORMAT_WIDEVINE_PSSH_JSON.equals(keyFormat)) {
            return null;
        } else {
            try {
                return new SchemeData(C0539C.WIDEVINE_UUID, "hls", line.getBytes(C0539C.UTF8_NAME));
            } catch (Throwable e) {
                throw new ParserException(e);
            }
        }
    }

    private static int parseIntAttr(String line, Pattern pattern) throws ParserException {
        return Integer.parseInt(parseStringAttr(line, pattern));
    }

    private static double parseDoubleAttr(String line, Pattern pattern) throws ParserException {
        return Double.parseDouble(parseStringAttr(line, pattern));
    }

    private static String parseOptionalStringAttr(String line, Pattern pattern) {
        Matcher matcher = pattern.matcher(line);
        return matcher.find() ? matcher.group(1) : null;
    }

    private static String parseStringAttr(String line, Pattern pattern) throws ParserException {
        Matcher matcher = pattern.matcher(line);
        if (matcher.find() && matcher.groupCount() == 1) {
            return matcher.group(1);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Couldn't match ");
        stringBuilder.append(pattern.pattern());
        stringBuilder.append(" in ");
        stringBuilder.append(line);
        throw new ParserException(stringBuilder.toString());
    }

    private static boolean parseBooleanAttribute(String line, Pattern pattern, boolean defaultValue) {
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            return matcher.group(1).equals(BOOLEAN_TRUE);
        }
        return defaultValue;
    }

    private static Pattern compileBooleanAttrPattern(String attribute) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(attribute);
        stringBuilder.append("=(");
        stringBuilder.append(BOOLEAN_FALSE);
        stringBuilder.append("|");
        stringBuilder.append(BOOLEAN_TRUE);
        stringBuilder.append(")");
        return Pattern.compile(stringBuilder.toString());
    }
}

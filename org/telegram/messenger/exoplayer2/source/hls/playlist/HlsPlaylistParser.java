package org.telegram.messenger.exoplayer2.source.hls.playlist;

import android.net.Uri;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.source.UnrecognizedInputFormatException;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMasterPlaylist.HlsUrl;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMediaPlaylist.Segment;
import org.telegram.messenger.exoplayer2.upstream.ParsingLoadable.Parser;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.exoplayer2.util.Util;

public final class HlsPlaylistParser implements Parser<HlsPlaylist> {
    private static final String BOOLEAN_FALSE = "NO";
    private static final String BOOLEAN_TRUE = "YES";
    private static final String METHOD_AES128 = "AES-128";
    private static final String METHOD_NONE = "NONE";
    private static final String PLAYLIST_HEADER = "#EXTM3U";
    private static final Pattern REGEX_ATTR_BYTERANGE = Pattern.compile("BYTERANGE=\"(\\d+(?:@\\d+)?)\\b\"");
    private static final Pattern REGEX_AUTOSELECT = compileBooleanAttrPattern("AUTOSELECT");
    private static final Pattern REGEX_BANDWIDTH = Pattern.compile("BANDWIDTH=(\\d+)\\b");
    private static final Pattern REGEX_BYTERANGE = Pattern.compile("#EXT-X-BYTERANGE:(\\d+(?:@\\d+)?)\\b");
    private static final Pattern REGEX_CODECS = Pattern.compile("CODECS=\"(.+?)\"");
    private static final Pattern REGEX_DEFAULT = compileBooleanAttrPattern("DEFAULT");
    private static final Pattern REGEX_FORCED = compileBooleanAttrPattern("FORCED");
    private static final Pattern REGEX_INSTREAM_ID = Pattern.compile("INSTREAM-ID=\"((?:CC|SERVICE)\\d+)\"");
    private static final Pattern REGEX_IV = Pattern.compile("IV=([^,.*]+)");
    private static final Pattern REGEX_LANGUAGE = Pattern.compile("LANGUAGE=\"(.+?)\"");
    private static final Pattern REGEX_MEDIA_DURATION = Pattern.compile("#EXTINF:([\\d\\.]+)\\b");
    private static final Pattern REGEX_MEDIA_SEQUENCE = Pattern.compile("#EXT-X-MEDIA-SEQUENCE:(\\d+)\\b");
    private static final Pattern REGEX_METHOD = Pattern.compile("METHOD=(NONE|AES-128)");
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
    private static final String TAG_INIT_SEGMENT = "#EXT-X-MAP";
    private static final String TAG_KEY = "#EXT-X-KEY";
    private static final String TAG_MEDIA = "#EXT-X-MEDIA";
    private static final String TAG_MEDIA_DURATION = "#EXTINF";
    private static final String TAG_MEDIA_SEQUENCE = "#EXT-X-MEDIA-SEQUENCE";
    private static final String TAG_PLAYLIST_TYPE = "#EXT-X-PLAYLIST-TYPE";
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

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public HlsPlaylist parse(Uri uri, InputStream inputStream) throws IOException {
        Closeable reader = new BufferedReader(new InputStreamReader(inputStream));
        Queue<String> extraLines = new LinkedList();
        if (checkPlaylistHeader(reader)) {
            String line;
            HlsPlaylist parseMediaPlaylist;
            while (true) {
                line = reader.readLine();
                if (line != null) {
                    line = line.trim();
                    if (!line.isEmpty()) {
                        if (line.startsWith(TAG_STREAM_INF)) {
                            break;
                        }
                        try {
                            if (line.startsWith(TAG_TARGET_DURATION) || line.startsWith(TAG_MEDIA_SEQUENCE) || line.startsWith(TAG_MEDIA_DURATION) || line.startsWith(TAG_KEY) || line.startsWith(TAG_BYTERANGE) || line.equals(TAG_DISCONTINUITY) || line.equals(TAG_DISCONTINUITY_SEQUENCE) || line.equals(TAG_ENDLIST)) {
                                extraLines.add(line);
                                parseMediaPlaylist = parseMediaPlaylist(new LineIterator(extraLines, reader), uri.toString());
                            } else {
                                extraLines.add(line);
                            }
                        } finally {
                            Util.closeQuietly(reader);
                        }
                        return parseMediaPlaylist;
                    }
                } else {
                    Util.closeQuietly(reader);
                    throw new ParserException("Failed to parse the playlist, could not identify any tags.");
                }
            }
            extraLines.add(line);
            parseMediaPlaylist = parseMediaPlaylist(new LineIterator(extraLines, reader), uri.toString());
            return parseMediaPlaylist;
        }
        throw new UnrecognizedInputFormatException("Input does not start with the #EXTM3U header.", uri);
    }

    private static boolean checkPlaylistHeader(BufferedReader reader) throws IOException {
        int last = reader.read();
        if (last == 239) {
            if (reader.read() != 187 || reader.read() != 191) {
                return false;
            }
            last = reader.read();
        }
        char last2 = skipIgnorableWhitespace(reader, true, last);
        int playlistHeaderLength = PLAYLIST_HEADER.length();
        for (int i = 0; i < playlistHeaderLength; i++) {
            if (last2 != PLAYLIST_HEADER.charAt(i)) {
                return false;
            }
            last2 = reader.read();
        }
        return Util.isLinebreak(skipIgnorableWhitespace(reader, false, last2));
    }

    private static int skipIgnorableWhitespace(BufferedReader reader, boolean skipLinebreaks, int c) throws IOException {
        while (c != -1 && Character.isWhitespace(c) && (skipLinebreaks || !Util.isLinebreak(c))) {
            c = reader.read();
        }
        return c;
    }

    private static HlsMasterPlaylist parseMasterPlaylist(LineIterator iterator, String baseUri) throws IOException {
        ArrayList<HlsUrl> variants = new ArrayList();
        ArrayList<HlsUrl> audios = new ArrayList();
        ArrayList<HlsUrl> subtitles = new ArrayList();
        Format muxedAudioFormat = null;
        ArrayList<Format> muxedCaptionFormats = new ArrayList();
        while (iterator.hasNext()) {
            String line = iterator.next();
            if (line.startsWith(TAG_MEDIA)) {
                int selectionFlags = parseSelectionFlags(line);
                String uri = parseOptionalStringAttr(line, REGEX_URI);
                String id = parseStringAttr(line, REGEX_NAME);
                String language = parseOptionalStringAttr(line, REGEX_LANGUAGE);
                String parseStringAttr = parseStringAttr(line, REGEX_TYPE);
                Object obj = -1;
                switch (parseStringAttr.hashCode()) {
                    case -959297733:
                        if (parseStringAttr.equals(TYPE_SUBTITLES)) {
                            obj = 1;
                            break;
                        }
                        break;
                    case -333210994:
                        if (parseStringAttr.equals(TYPE_CLOSED_CAPTIONS)) {
                            obj = 2;
                            break;
                        }
                        break;
                    case 62628790:
                        if (parseStringAttr.equals(TYPE_AUDIO)) {
                            obj = null;
                            break;
                        }
                        break;
                }
                switch (obj) {
                    case null:
                        Format format = Format.createAudioContainerFormat(id, MimeTypes.APPLICATION_M3U8, null, null, -1, -1, -1, null, selectionFlags, language);
                        if (uri != null) {
                            audios.add(new HlsUrl(uri, format));
                            break;
                        }
                        muxedAudioFormat = format;
                        break;
                    case 1:
                        subtitles.add(new HlsUrl(uri, Format.createTextContainerFormat(id, MimeTypes.APPLICATION_M3U8, MimeTypes.TEXT_VTT, null, -1, selectionFlags, language)));
                        break;
                    case 2:
                        String mimeType;
                        int accessibilityChannel;
                        String instreamId = parseStringAttr(line, REGEX_INSTREAM_ID);
                        if (instreamId.startsWith("CC")) {
                            mimeType = MimeTypes.APPLICATION_CEA608;
                            accessibilityChannel = Integer.parseInt(instreamId.substring(2));
                        } else {
                            mimeType = MimeTypes.APPLICATION_CEA708;
                            accessibilityChannel = Integer.parseInt(instreamId.substring(7));
                        }
                        muxedCaptionFormats.add(Format.createTextContainerFormat(id, null, mimeType, null, -1, selectionFlags, language, accessibilityChannel));
                        break;
                    default:
                        break;
                }
            }
            if (line.startsWith(TAG_STREAM_INF)) {
                int width;
                int height;
                int bitrate = parseIntAttr(line, REGEX_BANDWIDTH);
                String codecs = parseOptionalStringAttr(line, REGEX_CODECS);
                String resolutionString = parseOptionalStringAttr(line, REGEX_RESOLUTION);
                if (resolutionString != null) {
                    String[] widthAndHeight = resolutionString.split("x");
                    width = Integer.parseInt(widthAndHeight[0]);
                    height = Integer.parseInt(widthAndHeight[1]);
                    if (width <= 0 || height <= 0) {
                        width = -1;
                        height = -1;
                    }
                } else {
                    width = -1;
                    height = -1;
                }
                variants.add(new HlsUrl(iterator.next(), Format.createVideoContainerFormat(Integer.toString(variants.size()), MimeTypes.APPLICATION_M3U8, null, codecs, bitrate, width, height, -1.0f, null, 0)));
            }
        }
        return new HlsMasterPlaylist(baseUri, variants, audios, subtitles, muxedAudioFormat, muxedCaptionFormats);
    }

    private static int parseSelectionFlags(String line) {
        int i;
        int i2 = 0;
        int i3 = parseBooleanAttribute(line, REGEX_DEFAULT, false) ? 1 : 0;
        if (parseBooleanAttribute(line, REGEX_FORCED, false)) {
            i = 2;
        } else {
            i = 0;
        }
        i3 |= i;
        if (parseBooleanAttribute(line, REGEX_AUTOSELECT, false)) {
            i2 = 4;
        }
        return i3 | i2;
    }

    private static HlsMediaPlaylist parseMediaPlaylist(LineIterator iterator, String baseUri) throws IOException {
        int playlistType = 0;
        long startOffsetUs = C.TIME_UNSET;
        int mediaSequence = 0;
        int version = 1;
        long targetDurationUs = C.TIME_UNSET;
        boolean hasEndTag = false;
        Segment initializationSegment = null;
        List<Segment> segments = new ArrayList();
        long segmentDurationUs = 0;
        boolean hasDiscontinuitySequence = false;
        int playlistDiscontinuitySequence = 0;
        int relativeDiscontinuitySequence = 0;
        long playlistStartTimeUs = 0;
        long segmentStartTimeUs = 0;
        long segmentByteRangeOffset = 0;
        long segmentByteRangeLength = -1;
        int segmentMediaSequence = 0;
        boolean isEncrypted = false;
        String encryptionKeyUri = null;
        String encryptionIV = null;
        while (iterator.hasNext()) {
            String line = iterator.next();
            if (line.startsWith(TAG_PLAYLIST_TYPE)) {
                String playlistTypeString = parseStringAttr(line, REGEX_PLAYLIST_TYPE);
                if ("VOD".equals(playlistTypeString)) {
                    playlistType = 1;
                } else if ("EVENT".equals(playlistTypeString)) {
                    playlistType = 2;
                } else {
                    throw new ParserException("Illegal playlist type: " + playlistTypeString);
                }
            } else if (line.startsWith(TAG_START)) {
                startOffsetUs = (long) (parseDoubleAttr(line, REGEX_TIME_OFFSET) * 1000000.0d);
            } else if (line.startsWith(TAG_INIT_SEGMENT)) {
                String uri = parseStringAttr(line, REGEX_URI);
                String byteRange = parseOptionalStringAttr(line, REGEX_ATTR_BYTERANGE);
                if (byteRange != null) {
                    splitByteRange = byteRange.split("@");
                    segmentByteRangeLength = Long.parseLong(splitByteRange[0]);
                    if (splitByteRange.length > 1) {
                        segmentByteRangeOffset = Long.parseLong(splitByteRange[1]);
                    }
                }
                initializationSegment = new Segment(uri, segmentByteRangeOffset, segmentByteRangeLength);
                segmentByteRangeOffset = 0;
                segmentByteRangeLength = -1;
            } else if (line.startsWith(TAG_TARGET_DURATION)) {
                targetDurationUs = ((long) parseIntAttr(line, REGEX_TARGET_DURATION)) * C.MICROS_PER_SECOND;
            } else if (line.startsWith(TAG_MEDIA_SEQUENCE)) {
                mediaSequence = parseIntAttr(line, REGEX_MEDIA_SEQUENCE);
                segmentMediaSequence = mediaSequence;
            } else if (line.startsWith(TAG_VERSION)) {
                version = parseIntAttr(line, REGEX_VERSION);
            } else if (line.startsWith(TAG_MEDIA_DURATION)) {
                segmentDurationUs = (long) (parseDoubleAttr(line, REGEX_MEDIA_DURATION) * 1000000.0d);
            } else if (line.startsWith(TAG_KEY)) {
                isEncrypted = METHOD_AES128.equals(parseStringAttr(line, REGEX_METHOD));
                if (isEncrypted) {
                    encryptionKeyUri = parseStringAttr(line, REGEX_URI);
                    encryptionIV = parseOptionalStringAttr(line, REGEX_IV);
                } else {
                    encryptionKeyUri = null;
                    encryptionIV = null;
                }
            } else if (line.startsWith(TAG_BYTERANGE)) {
                splitByteRange = parseStringAttr(line, REGEX_BYTERANGE).split("@");
                segmentByteRangeLength = Long.parseLong(splitByteRange[0]);
                if (splitByteRange.length > 1) {
                    segmentByteRangeOffset = Long.parseLong(splitByteRange[1]);
                }
            } else if (line.startsWith(TAG_DISCONTINUITY_SEQUENCE)) {
                hasDiscontinuitySequence = true;
                playlistDiscontinuitySequence = Integer.parseInt(line.substring(line.indexOf(58) + 1));
            } else if (line.equals(TAG_DISCONTINUITY)) {
                relativeDiscontinuitySequence++;
            } else if (line.startsWith(TAG_PROGRAM_DATE_TIME)) {
                if (playlistStartTimeUs == 0) {
                    playlistStartTimeUs = C.msToUs(Util.parseXsDateTime(line.substring(line.indexOf(58) + 1))) - segmentStartTimeUs;
                }
            } else if (!line.startsWith("#")) {
                String segmentEncryptionIV;
                if (!isEncrypted) {
                    segmentEncryptionIV = null;
                } else if (encryptionIV != null) {
                    segmentEncryptionIV = encryptionIV;
                } else {
                    segmentEncryptionIV = Integer.toHexString(segmentMediaSequence);
                }
                segmentMediaSequence++;
                if (segmentByteRangeLength == -1) {
                    segmentByteRangeOffset = 0;
                }
                segments.add(new Segment(line, segmentDurationUs, relativeDiscontinuitySequence, segmentStartTimeUs, isEncrypted, encryptionKeyUri, segmentEncryptionIV, segmentByteRangeOffset, segmentByteRangeLength));
                segmentStartTimeUs += segmentDurationUs;
                segmentDurationUs = 0;
                if (segmentByteRangeLength != -1) {
                    segmentByteRangeOffset += segmentByteRangeLength;
                }
                segmentByteRangeLength = -1;
            } else if (line.equals(TAG_ENDLIST)) {
                hasEndTag = true;
            }
        }
        return new HlsMediaPlaylist(playlistType, baseUri, startOffsetUs, playlistStartTimeUs, hasDiscontinuitySequence, playlistDiscontinuitySequence, mediaSequence, version, targetDurationUs, hasEndTag, playlistStartTimeUs != 0, initializationSegment, segments);
    }

    private static String parseStringAttr(String line, Pattern pattern) throws ParserException {
        Matcher matcher = pattern.matcher(line);
        if (matcher.find() && matcher.groupCount() == 1) {
            return matcher.group(1);
        }
        throw new ParserException("Couldn't match " + pattern.pattern() + " in " + line);
    }

    private static int parseIntAttr(String line, Pattern pattern) throws ParserException {
        return Integer.parseInt(parseStringAttr(line, pattern));
    }

    private static double parseDoubleAttr(String line, Pattern pattern) throws ParserException {
        return Double.parseDouble(parseStringAttr(line, pattern));
    }

    private static String parseOptionalStringAttr(String line, Pattern pattern) {
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private static boolean parseBooleanAttribute(String line, Pattern pattern, boolean defaultValue) {
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            return matcher.group(1).equals(BOOLEAN_TRUE);
        }
        return defaultValue;
    }

    private static Pattern compileBooleanAttrPattern(String attribute) {
        return Pattern.compile(attribute + "=(" + BOOLEAN_FALSE + "|" + BOOLEAN_TRUE + ")");
    }
}

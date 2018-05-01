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
import org.telegram.messenger.exoplayer2.C0542C;
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

        public LineIterator(Queue<String> queue, BufferedReader bufferedReader) {
            this.extraLines = queue;
            this.reader = bufferedReader;
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
            String str = this.next;
            this.next = null;
            return str;
        }
    }

    public HlsPlaylist parse(Uri uri, InputStream inputStream) throws IOException {
        Closeable bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        inputStream = new ArrayDeque();
        try {
            if (checkPlaylistHeader(bufferedReader)) {
                String readLine;
                while (true) {
                    readLine = bufferedReader.readLine();
                    if (readLine != null) {
                        readLine = readLine.trim();
                        if (!readLine.isEmpty()) {
                            if (!readLine.startsWith(TAG_STREAM_INF)) {
                                if (readLine.startsWith(TAG_TARGET_DURATION) || readLine.startsWith(TAG_MEDIA_SEQUENCE) || readLine.startsWith(TAG_MEDIA_DURATION) || readLine.startsWith(TAG_KEY) || readLine.startsWith(TAG_BYTERANGE) || readLine.equals(TAG_DISCONTINUITY) || readLine.equals(TAG_DISCONTINUITY_SEQUENCE)) {
                                    break;
                                } else if (readLine.equals(TAG_ENDLIST)) {
                                    break;
                                } else {
                                    inputStream.add(readLine);
                                }
                            } else {
                                inputStream.add(readLine);
                                uri = parseMasterPlaylist(new LineIterator(inputStream, bufferedReader), uri.toString());
                                Util.closeQuietly(bufferedReader);
                                return uri;
                            }
                        }
                    } else {
                        Util.closeQuietly(bufferedReader);
                        throw new ParserException("Failed to parse the playlist, could not identify any tags.");
                    }
                }
                inputStream.add(readLine);
                uri = parseMediaPlaylist(new LineIterator(inputStream, bufferedReader), uri.toString());
                return uri;
            }
            throw new UnrecognizedInputFormatException("Input does not start with the #EXTM3U header.", uri);
        } finally {
            Util.closeQuietly(bufferedReader);
        }
    }

    private static boolean checkPlaylistHeader(BufferedReader bufferedReader) throws IOException {
        int read = bufferedReader.read();
        if (read == 239) {
            if (bufferedReader.read() == 187) {
                if (bufferedReader.read() == 191) {
                    read = bufferedReader.read();
                }
            }
            return false;
        }
        char skipIgnorableWhitespace = skipIgnorableWhitespace(bufferedReader, true, read);
        int length = PLAYLIST_HEADER.length();
        char c = skipIgnorableWhitespace;
        for (read = 0; read < length; read++) {
            if (c != PLAYLIST_HEADER.charAt(read)) {
                return false;
            }
            c = bufferedReader.read();
        }
        return Util.isLinebreak(skipIgnorableWhitespace(bufferedReader, false, c));
    }

    private static int skipIgnorableWhitespace(BufferedReader bufferedReader, boolean z, int i) throws IOException {
        while (i != -1 && Character.isWhitespace(i) && (z || !Util.isLinebreak(i))) {
            i = bufferedReader.read();
        }
        return i;
    }

    private static HlsMasterPlaylist parseMasterPlaylist(LineIterator lineIterator, String str) throws IOException {
        HashSet hashSet = new HashSet();
        HashMap hashMap = new HashMap();
        List arrayList = new ArrayList();
        List arrayList2 = new ArrayList();
        List arrayList3 = new ArrayList();
        ArrayList arrayList4 = new ArrayList();
        List arrayList5 = new ArrayList();
        int i = 0;
        while (lineIterator.hasNext()) {
            int i2;
            String next = lineIterator.next();
            if (next.startsWith(TAG_PREFIX)) {
                arrayList5.add(next);
            }
            if (next.startsWith(TAG_MEDIA)) {
                arrayList4.add(next);
            } else if (next.startsWith(TAG_STREAM_INF)) {
                int i3;
                int i4;
                i |= next.contains(ATTR_CLOSED_CAPTIONS_NONE);
                int parseIntAttr = parseIntAttr(next, REGEX_BANDWIDTH);
                String parseOptionalStringAttr = parseOptionalStringAttr(next, REGEX_AVERAGE_BANDWIDTH);
                if (parseOptionalStringAttr != null) {
                    parseIntAttr = Integer.parseInt(parseOptionalStringAttr);
                }
                int i5 = parseIntAttr;
                String parseOptionalStringAttr2 = parseOptionalStringAttr(next, REGEX_CODECS);
                parseOptionalStringAttr = parseOptionalStringAttr(next, REGEX_RESOLUTION);
                if (parseOptionalStringAttr != null) {
                    String[] split = parseOptionalStringAttr.split("x");
                    int parseInt = Integer.parseInt(split[0]);
                    int parseInt2 = Integer.parseInt(split[1]);
                    if (parseInt > 0) {
                        if (parseInt2 > 0) {
                            i2 = parseInt;
                            i3 = i2;
                            i4 = parseInt2;
                        }
                    }
                    i2 = -1;
                    parseInt2 = -1;
                    i3 = i2;
                    i4 = parseInt2;
                } else {
                    i3 = -1;
                    i4 = -1;
                }
                float f = -1.0f;
                parseOptionalStringAttr = parseOptionalStringAttr(next, REGEX_FRAME_RATE);
                if (parseOptionalStringAttr != null) {
                    f = Float.parseFloat(parseOptionalStringAttr);
                }
                float f2 = f;
                next = parseOptionalStringAttr(next, REGEX_AUDIO);
                if (!(next == null || parseOptionalStringAttr2 == null)) {
                    hashMap.put(next, Util.getCodecsOfType(parseOptionalStringAttr2, 1));
                }
                next = lineIterator.next();
                if (hashSet.add(next)) {
                    arrayList.add(new HlsUrl(next, Format.createVideoContainerFormat(Integer.toString(arrayList.size()), MimeTypes.APPLICATION_M3U8, null, parseOptionalStringAttr2, i5, i3, i4, f2, null, 0)));
                }
            }
        }
        int i6 = 0;
        Format format = null;
        List list = null;
        while (i6 < arrayList4.size()) {
            int i7;
            String str2;
            Format createAudioContainerFormat;
            int i8;
            String str3;
            String str4 = (String) arrayList4.get(i6);
            int parseSelectionFlags = parseSelectionFlags(str4);
            String parseOptionalStringAttr3 = parseOptionalStringAttr(str4, REGEX_URI);
            String parseStringAttr = parseStringAttr(str4, REGEX_NAME);
            String parseOptionalStringAttr4 = parseOptionalStringAttr(str4, REGEX_LANGUAGE);
            String parseOptionalStringAttr5 = parseOptionalStringAttr(str4, REGEX_GROUP_ID);
            String parseStringAttr2 = parseStringAttr(str4, REGEX_TYPE);
            i2 = parseStringAttr2.hashCode();
            ArrayList arrayList6 = arrayList4;
            String str5 = parseOptionalStringAttr3;
            if (i2 != -959297733) {
                if (i2 != -333210994) {
                    if (i2 == 62628790) {
                        if (parseStringAttr2.equals(TYPE_AUDIO)) {
                            i7 = 0;
                            switch (i7) {
                                case 0:
                                    str2 = str5;
                                    parseOptionalStringAttr5 = (String) hashMap.get(parseOptionalStringAttr5);
                                    createAudioContainerFormat = Format.createAudioContainerFormat(parseStringAttr, MimeTypes.APPLICATION_M3U8, parseOptionalStringAttr5 == null ? MimeTypes.getMediaMimeType(parseOptionalStringAttr5) : null, parseOptionalStringAttr5, -1, -1, -1, null, parseSelectionFlags, parseOptionalStringAttr4);
                                    if (str2 != null) {
                                        arrayList2.add(new HlsUrl(str2, createAudioContainerFormat));
                                        break;
                                    }
                                    format = createAudioContainerFormat;
                                    break;
                                case 1:
                                    arrayList3.add(new HlsUrl(str5, Format.createTextContainerFormat(parseStringAttr, MimeTypes.APPLICATION_M3U8, MimeTypes.TEXT_VTT, null, -1, parseSelectionFlags, parseOptionalStringAttr4)));
                                    break;
                                case 2:
                                    str2 = parseStringAttr(str4, REGEX_INSTREAM_ID);
                                    if (str2.startsWith("CC")) {
                                        parseOptionalStringAttr5 = MimeTypes.APPLICATION_CEA708;
                                        i7 = Integer.parseInt(str2.substring(7));
                                    } else {
                                        parseOptionalStringAttr5 = MimeTypes.APPLICATION_CEA608;
                                        i7 = Integer.parseInt(str2.substring(2));
                                    }
                                    i8 = i7;
                                    str3 = parseOptionalStringAttr5;
                                    if (list == null) {
                                        list = new ArrayList();
                                    }
                                    list.add(Format.createTextContainerFormat(parseStringAttr, null, str3, null, -1, parseSelectionFlags, parseOptionalStringAttr4, i8));
                                    break;
                                default:
                                    break;
                            }
                            i6++;
                            arrayList4 = arrayList6;
                        }
                    }
                } else if (parseStringAttr2.equals(TYPE_CLOSED_CAPTIONS)) {
                    i7 = 2;
                    switch (i7) {
                        case 0:
                            str2 = str5;
                            parseOptionalStringAttr5 = (String) hashMap.get(parseOptionalStringAttr5);
                            if (parseOptionalStringAttr5 == null) {
                            }
                            createAudioContainerFormat = Format.createAudioContainerFormat(parseStringAttr, MimeTypes.APPLICATION_M3U8, parseOptionalStringAttr5 == null ? MimeTypes.getMediaMimeType(parseOptionalStringAttr5) : null, parseOptionalStringAttr5, -1, -1, -1, null, parseSelectionFlags, parseOptionalStringAttr4);
                            if (str2 != null) {
                                format = createAudioContainerFormat;
                                break;
                            }
                            arrayList2.add(new HlsUrl(str2, createAudioContainerFormat));
                            break;
                        case 1:
                            arrayList3.add(new HlsUrl(str5, Format.createTextContainerFormat(parseStringAttr, MimeTypes.APPLICATION_M3U8, MimeTypes.TEXT_VTT, null, -1, parseSelectionFlags, parseOptionalStringAttr4)));
                            break;
                        case 2:
                            str2 = parseStringAttr(str4, REGEX_INSTREAM_ID);
                            if (str2.startsWith("CC")) {
                                parseOptionalStringAttr5 = MimeTypes.APPLICATION_CEA708;
                                i7 = Integer.parseInt(str2.substring(7));
                            } else {
                                parseOptionalStringAttr5 = MimeTypes.APPLICATION_CEA608;
                                i7 = Integer.parseInt(str2.substring(2));
                            }
                            i8 = i7;
                            str3 = parseOptionalStringAttr5;
                            if (list == null) {
                                list = new ArrayList();
                            }
                            list.add(Format.createTextContainerFormat(parseStringAttr, null, str3, null, -1, parseSelectionFlags, parseOptionalStringAttr4, i8));
                            break;
                        default:
                            break;
                    }
                    i6++;
                    arrayList4 = arrayList6;
                }
            } else if (parseStringAttr2.equals(TYPE_SUBTITLES)) {
                i7 = 1;
                switch (i7) {
                    case 0:
                        str2 = str5;
                        parseOptionalStringAttr5 = (String) hashMap.get(parseOptionalStringAttr5);
                        if (parseOptionalStringAttr5 == null) {
                        }
                        createAudioContainerFormat = Format.createAudioContainerFormat(parseStringAttr, MimeTypes.APPLICATION_M3U8, parseOptionalStringAttr5 == null ? MimeTypes.getMediaMimeType(parseOptionalStringAttr5) : null, parseOptionalStringAttr5, -1, -1, -1, null, parseSelectionFlags, parseOptionalStringAttr4);
                        if (str2 != null) {
                            arrayList2.add(new HlsUrl(str2, createAudioContainerFormat));
                            break;
                        }
                        format = createAudioContainerFormat;
                        break;
                    case 1:
                        arrayList3.add(new HlsUrl(str5, Format.createTextContainerFormat(parseStringAttr, MimeTypes.APPLICATION_M3U8, MimeTypes.TEXT_VTT, null, -1, parseSelectionFlags, parseOptionalStringAttr4)));
                        break;
                    case 2:
                        str2 = parseStringAttr(str4, REGEX_INSTREAM_ID);
                        if (str2.startsWith("CC")) {
                            parseOptionalStringAttr5 = MimeTypes.APPLICATION_CEA608;
                            i7 = Integer.parseInt(str2.substring(2));
                        } else {
                            parseOptionalStringAttr5 = MimeTypes.APPLICATION_CEA708;
                            i7 = Integer.parseInt(str2.substring(7));
                        }
                        i8 = i7;
                        str3 = parseOptionalStringAttr5;
                        if (list == null) {
                            list = new ArrayList();
                        }
                        list.add(Format.createTextContainerFormat(parseStringAttr, null, str3, null, -1, parseSelectionFlags, parseOptionalStringAttr4, i8));
                        break;
                    default:
                        break;
                }
                i6++;
                arrayList4 = arrayList6;
            }
            i7 = -1;
            switch (i7) {
                case 0:
                    str2 = str5;
                    parseOptionalStringAttr5 = (String) hashMap.get(parseOptionalStringAttr5);
                    if (parseOptionalStringAttr5 == null) {
                    }
                    createAudioContainerFormat = Format.createAudioContainerFormat(parseStringAttr, MimeTypes.APPLICATION_M3U8, parseOptionalStringAttr5 == null ? MimeTypes.getMediaMimeType(parseOptionalStringAttr5) : null, parseOptionalStringAttr5, -1, -1, -1, null, parseSelectionFlags, parseOptionalStringAttr4);
                    if (str2 != null) {
                        format = createAudioContainerFormat;
                        break;
                    }
                    arrayList2.add(new HlsUrl(str2, createAudioContainerFormat));
                    break;
                case 1:
                    arrayList3.add(new HlsUrl(str5, Format.createTextContainerFormat(parseStringAttr, MimeTypes.APPLICATION_M3U8, MimeTypes.TEXT_VTT, null, -1, parseSelectionFlags, parseOptionalStringAttr4)));
                    break;
                case 2:
                    str2 = parseStringAttr(str4, REGEX_INSTREAM_ID);
                    if (str2.startsWith("CC")) {
                        parseOptionalStringAttr5 = MimeTypes.APPLICATION_CEA708;
                        i7 = Integer.parseInt(str2.substring(7));
                    } else {
                        parseOptionalStringAttr5 = MimeTypes.APPLICATION_CEA608;
                        i7 = Integer.parseInt(str2.substring(2));
                    }
                    i8 = i7;
                    str3 = parseOptionalStringAttr5;
                    if (list == null) {
                        list = new ArrayList();
                    }
                    list.add(Format.createTextContainerFormat(parseStringAttr, null, str3, null, -1, parseSelectionFlags, parseOptionalStringAttr4, i8));
                    break;
                default:
                    break;
            }
            i6++;
            arrayList4 = arrayList6;
        }
        return new HlsMasterPlaylist(str, arrayList5, arrayList, arrayList2, arrayList3, format, i != 0 ? Collections.emptyList() : list);
    }

    private static int parseSelectionFlags(String str) {
        int i = 0;
        int parseBooleanAttribute = parseBooleanAttribute(str, REGEX_DEFAULT, false) | (parseBooleanAttribute(str, REGEX_FORCED, false) ? 2 : 0);
        if (parseBooleanAttribute(str, REGEX_AUTOSELECT, false) != null) {
            i = 4;
        }
        return parseBooleanAttribute | i;
    }

    private static HlsMediaPlaylist parseMediaPlaylist(LineIterator lineIterator, String str) throws IOException {
        List arrayList = new ArrayList();
        List arrayList2 = new ArrayList();
        int i = 0;
        long j = -9223372036854775807L;
        long j2 = j;
        int i2 = 0;
        int i3 = i2;
        int i4 = i3;
        int i5 = i4;
        int i6 = i5;
        int i7 = i6;
        int i8 = i7;
        int i9 = i8;
        int i10 = 1;
        long j3 = 0;
        DrmInitData drmInitData = null;
        Segment segment = null;
        long j4 = 0;
        long j5 = -1;
        long j6 = 0;
        String str2 = null;
        String str3 = null;
        loop0:
        while (true) {
            long j7 = 0;
            while (lineIterator.hasNext()) {
                String next = lineIterator.next();
                if (next.startsWith(TAG_PREFIX)) {
                    arrayList2.add(next);
                }
                if (next.startsWith(TAG_PLAYLIST_TYPE)) {
                    next = parseStringAttr(next, REGEX_PLAYLIST_TYPE);
                    if ("VOD".equals(next)) {
                        i3 = 1;
                    } else if ("EVENT".equals(next)) {
                        i3 = 2;
                    }
                } else if (next.startsWith(TAG_START)) {
                    j = (long) (parseDoubleAttr(next, REGEX_TIME_OFFSET) * 1000000.0d);
                } else if (next.startsWith(TAG_INIT_SEGMENT)) {
                    String parseStringAttr = parseStringAttr(next, REGEX_URI);
                    next = parseOptionalStringAttr(next, REGEX_ATTR_BYTERANGE);
                    if (next != null) {
                        r4 = next.split("@");
                        j5 = Long.parseLong(r4[i]);
                        if (r4.length > 1) {
                            j4 = Long.parseLong(r4[1]);
                        }
                    }
                    segment = new Segment(parseStringAttr, j4, j5);
                    j4 = 0;
                    j5 = -1;
                } else if (next.startsWith(TAG_TARGET_DURATION)) {
                    j2 = C0542C.MICROS_PER_SECOND * ((long) parseIntAttr(next, REGEX_TARGET_DURATION));
                } else if (next.startsWith(TAG_MEDIA_SEQUENCE)) {
                    i4 = parseIntAttr(next, REGEX_MEDIA_SEQUENCE);
                    i7 = i4;
                } else if (next.startsWith(TAG_VERSION)) {
                    i10 = parseIntAttr(next, REGEX_VERSION);
                } else if (next.startsWith(TAG_MEDIA_DURATION)) {
                    j7 = (long) (parseDoubleAttr(next, REGEX_MEDIA_DURATION) * 1000000.0d);
                } else if (next.startsWith(TAG_KEY)) {
                    String parseOptionalStringAttr = parseOptionalStringAttr(next, REGEX_METHOD);
                    String parseOptionalStringAttr2 = parseOptionalStringAttr(next, REGEX_KEYFORMAT);
                    if (METHOD_NONE.equals(parseOptionalStringAttr)) {
                        str2 = null;
                        str3 = null;
                    } else {
                        String parseOptionalStringAttr3 = parseOptionalStringAttr(next, REGEX_IV);
                        if (!KEYFORMAT_IDENTITY.equals(parseOptionalStringAttr2)) {
                            if (parseOptionalStringAttr2 != null) {
                                if (!(parseOptionalStringAttr == null || parseWidevineSchemeData(next, parseOptionalStringAttr2) == null)) {
                                    if (!METHOD_SAMPLE_AES_CENC.equals(parseOptionalStringAttr)) {
                                        if (!METHOD_SAMPLE_AES_CTR.equals(parseOptionalStringAttr)) {
                                            parseOptionalStringAttr = C0542C.CENC_TYPE_cbcs;
                                            drmInitData = new DrmInitData(parseOptionalStringAttr, r4);
                                        }
                                    }
                                    parseOptionalStringAttr = C0542C.CENC_TYPE_cenc;
                                    drmInitData = new DrmInitData(parseOptionalStringAttr, r4);
                                }
                                str3 = parseOptionalStringAttr3;
                                str2 = null;
                            }
                        }
                        if (METHOD_AES_128.equals(parseOptionalStringAttr)) {
                            str2 = parseStringAttr(next, REGEX_URI);
                            str3 = parseOptionalStringAttr3;
                        }
                        str3 = parseOptionalStringAttr3;
                        str2 = null;
                    }
                    i = 0;
                } else {
                    int i11;
                    if (next.startsWith(TAG_BYTERANGE)) {
                        r4 = parseStringAttr(next, REGEX_BYTERANGE).split("@");
                        i11 = 0;
                        j5 = Long.parseLong(r4[0]);
                        if (r4.length > 1) {
                            j4 = Long.parseLong(r4[1]);
                        }
                    } else {
                        i11 = 0;
                        if (next.startsWith(TAG_DISCONTINUITY_SEQUENCE)) {
                            i6 = Integer.parseInt(next.substring(next.indexOf(58) + 1));
                            i = 0;
                            i5 = 1;
                        } else if (next.equals(TAG_DISCONTINUITY)) {
                            i2++;
                        } else if (next.startsWith(TAG_PROGRAM_DATE_TIME)) {
                            if (j3 == 0) {
                                j3 = C0542C.msToUs(Util.parseXsDateTime(next.substring(next.indexOf(58) + 1))) - j6;
                            }
                        } else if (!next.startsWith("#")) {
                            String toHexString = str2 == null ? null : str3 != null ? str3 : Integer.toHexString(i4);
                            i4++;
                            if (j5 == -1) {
                                j4 = 0;
                            }
                            arrayList.add(new Segment(next, j7, i2, j6, str2, toHexString, j4, j5));
                            long j8 = j6 + j7;
                            if (j5 != -1) {
                                j4 += j5;
                            }
                            i = 0;
                            j6 = j8;
                            j5 = -1;
                        } else if (next.equals(TAG_INDEPENDENT_SEGMENTS)) {
                            i = 0;
                            i8 = 1;
                        } else if (next.equals(TAG_ENDLIST)) {
                            i = 0;
                            i9 = 1;
                        }
                    }
                    i = i11;
                }
            }
            break loop0;
        }
        return new HlsMediaPlaylist(i3, str, arrayList2, j, j3, i5, i6, i7, i10, j2, i8, i9, j3 != 0 ? true : i, drmInitData, segment, arrayList);
    }

    private static SchemeData parseWidevineSchemeData(String str, String str2) throws ParserException {
        if (KEYFORMAT_WIDEVINE_PSSH_BINARY.equals(str2)) {
            str = parseStringAttr(str, REGEX_URI);
            return new SchemeData(C0542C.WIDEVINE_UUID, MimeTypes.VIDEO_MP4, Base64.decode(str.substring(str.indexOf(44)), 0));
        } else if (KEYFORMAT_WIDEVINE_PSSH_JSON.equals(str2) == null) {
            return null;
        } else {
            try {
                return new SchemeData(C0542C.WIDEVINE_UUID, "hls", str.getBytes(C0542C.UTF8_NAME));
            } catch (Throwable e) {
                throw new ParserException(e);
            }
        }
    }

    private static int parseIntAttr(String str, Pattern pattern) throws ParserException {
        return Integer.parseInt(parseStringAttr(str, pattern));
    }

    private static double parseDoubleAttr(String str, Pattern pattern) throws ParserException {
        return Double.parseDouble(parseStringAttr(str, pattern));
    }

    private static String parseOptionalStringAttr(String str, Pattern pattern) {
        str = pattern.matcher(str);
        return str.find() != null ? str.group(1) : null;
    }

    private static String parseStringAttr(String str, Pattern pattern) throws ParserException {
        Matcher matcher = pattern.matcher(str);
        if (matcher.find() && matcher.groupCount() == 1) {
            return matcher.group(1);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Couldn't match ");
        stringBuilder.append(pattern.pattern());
        stringBuilder.append(" in ");
        stringBuilder.append(str);
        throw new ParserException(stringBuilder.toString());
    }

    private static boolean parseBooleanAttribute(String str, Pattern pattern, boolean z) {
        str = pattern.matcher(str);
        return str.find() != null ? str.group(1).equals(BOOLEAN_TRUE) : z;
    }

    private static Pattern compileBooleanAttrPattern(String str) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(str);
        stringBuilder.append("=(");
        stringBuilder.append(BOOLEAN_FALSE);
        stringBuilder.append("|");
        stringBuilder.append(BOOLEAN_TRUE);
        stringBuilder.append(")");
        return Pattern.compile(stringBuilder.toString());
    }
}

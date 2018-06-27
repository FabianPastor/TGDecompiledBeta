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
import org.telegram.messenger.exoplayer2.C0554C;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.drm.DrmInitData.SchemeData;
import org.telegram.messenger.exoplayer2.source.UnrecognizedInputFormatException;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMasterPlaylist.HlsUrl;
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
    private static final Pattern REGEX_METHOD = Pattern.compile("METHOD=(NONE|AES-128|SAMPLE-AES|SAMPLE-AES-CENC|SAMPLE-AES-CTR)\\s*(,|$)");
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
    private static final String TAG_GAP = "#EXT-X-GAP";
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

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public HlsPlaylist parse(Uri uri, InputStream inputStream) throws IOException {
        Closeable reader = new BufferedReader(new InputStreamReader(inputStream));
        Queue<String> extraLines = new ArrayDeque();
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
        HashSet<String> variantUrls = new HashSet();
        HashMap<String, String> audioGroupIdToCodecs = new HashMap();
        ArrayList<HlsUrl> variants = new ArrayList();
        ArrayList<HlsUrl> audios = new ArrayList();
        ArrayList<HlsUrl> subtitles = new ArrayList();
        ArrayList<String> mediaTags = new ArrayList();
        ArrayList<String> tags = new ArrayList();
        Format muxedAudioFormat = null;
        List<Format> muxedCaptionFormats = null;
        boolean noClosedCaptions = false;
        while (iterator.hasNext()) {
            String codecs;
            String line = iterator.next();
            if (line.startsWith(TAG_PREFIX)) {
                tags.add(line);
            }
            if (line.startsWith(TAG_MEDIA)) {
                mediaTags.add(line);
            } else {
                if (line.startsWith(TAG_STREAM_INF)) {
                    int width;
                    int height;
                    noClosedCaptions |= line.contains(ATTR_CLOSED_CAPTIONS_NONE);
                    int bitrate = parseIntAttr(line, REGEX_BANDWIDTH);
                    String averageBandwidthString = parseOptionalStringAttr(line, REGEX_AVERAGE_BANDWIDTH);
                    if (averageBandwidthString != null) {
                        bitrate = Integer.parseInt(averageBandwidthString);
                    }
                    codecs = parseOptionalStringAttr(line, REGEX_CODECS);
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
                    float frameRate = -1.0f;
                    String frameRateString = parseOptionalStringAttr(line, REGEX_FRAME_RATE);
                    if (frameRateString != null) {
                        frameRate = Float.parseFloat(frameRateString);
                    }
                    String audioGroupId = parseOptionalStringAttr(line, REGEX_AUDIO);
                    if (!(audioGroupId == null || codecs == null)) {
                        audioGroupIdToCodecs.put(audioGroupId, Util.getCodecsOfType(codecs, 1));
                    }
                    line = iterator.next();
                    if (variantUrls.add(line)) {
                        variants.add(new HlsUrl(line, Format.createVideoContainerFormat(Integer.toString(variants.size()), MimeTypes.APPLICATION_M3U8, null, codecs, bitrate, width, height, frameRate, null, 0)));
                    }
                }
            }
        }
        for (int i = 0; i < mediaTags.size(); i++) {
            line = (String) mediaTags.get(i);
            int selectionFlags = parseSelectionFlags(line);
            String uri = parseOptionalStringAttr(line, REGEX_URI);
            String id = parseStringAttr(line, REGEX_NAME);
            String language = parseOptionalStringAttr(line, REGEX_LANGUAGE);
            String groupId = parseOptionalStringAttr(line, REGEX_GROUP_ID);
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
                    codecs = (String) audioGroupIdToCodecs.get(groupId);
                    Format format = Format.createAudioContainerFormat(id, MimeTypes.APPLICATION_M3U8, codecs != null ? MimeTypes.getMediaMimeType(codecs) : null, codecs, -1, -1, -1, null, selectionFlags, language);
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
                    if (muxedCaptionFormats == null) {
                        muxedCaptionFormats = new ArrayList();
                    }
                    muxedCaptionFormats.add(Format.createTextContainerFormat(id, null, mimeType, null, -1, selectionFlags, language, accessibilityChannel));
                    break;
                default:
                    break;
            }
        }
        if (noClosedCaptions) {
            muxedCaptionFormats = Collections.emptyList();
        }
        return new HlsMasterPlaylist(baseUri, tags, variants, audios, subtitles, muxedAudioFormat, muxedCaptionFormats);
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

    private static org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMediaPlaylist parseMediaPlaylist(org.telegram.messenger.exoplayer2.source.hls.playlist.HlsPlaylistParser.LineIterator r54, java.lang.String r55) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unknown predecessor block by arg (r42_2 'drmInitData' org.telegram.messenger.exoplayer2.drm.DrmInitData) in PHI: PHI: (r42_3 'drmInitData' org.telegram.messenger.exoplayer2.drm.DrmInitData) = (r42_1 'drmInitData' org.telegram.messenger.exoplayer2.drm.DrmInitData), (r42_1 'drmInitData' org.telegram.messenger.exoplayer2.drm.DrmInitData), (r42_1 'drmInitData' org.telegram.messenger.exoplayer2.drm.DrmInitData), (r42_1 'drmInitData' org.telegram.messenger.exoplayer2.drm.DrmInitData), (r42_1 'drmInitData' org.telegram.messenger.exoplayer2.drm.DrmInitData), (r42_1 'drmInitData' org.telegram.messenger.exoplayer2.drm.DrmInitData), (r42_1 'drmInitData' org.telegram.messenger.exoplayer2.drm.DrmInitData), (r42_1 'drmInitData' org.telegram.messenger.exoplayer2.drm.DrmInitData), (r42_1 'drmInitData' org.telegram.messenger.exoplayer2.drm.DrmInitData), (r42_2 'drmInitData' org.telegram.messenger.exoplayer2.drm.DrmInitData), (r42_1 'drmInitData' org.telegram.messenger.exoplayer2.drm.DrmInitData), (r42_1 'drmInitData' org.telegram.messenger.exoplayer2.drm.DrmInitData), (r42_1 'drmInitData' org.telegram.messenger.exoplayer2.drm.DrmInitData), (r42_1 'drmInitData' org.telegram.messenger.exoplayer2.drm.DrmInitData), (r42_1 'drmInitData' org.telegram.messenger.exoplayer2.drm.DrmInitData), (r42_1 'drmInitData' org.telegram.messenger.exoplayer2.drm.DrmInitData), (r42_1 'drmInitData' org.telegram.messenger.exoplayer2.drm.DrmInitData), (r42_1 'drmInitData' org.telegram.messenger.exoplayer2.drm.DrmInitData), (r42_1 'drmInitData' org.telegram.messenger.exoplayer2.drm.DrmInitData), (r42_1 'drmInitData' org.telegram.messenger.exoplayer2.drm.DrmInitData), (r42_1 'drmInitData' org.telegram.messenger.exoplayer2.drm.DrmInitData), (r42_1 'drmInitData' org.telegram.messenger.exoplayer2.drm.DrmInitData), (r42_1 'drmInitData' org.telegram.messenger.exoplayer2.drm.DrmInitData), (r42_1 'drmInitData' org.telegram.messenger.exoplayer2.drm.DrmInitData), (r42_1 'drmInitData' org.telegram.messenger.exoplayer2.drm.DrmInitData), (r42_1 'drmInitData' org.telegram.messenger.exoplayer2.drm.DrmInitData) binds: {(r42_1 'drmInitData' org.telegram.messenger.exoplayer2.drm.DrmInitData)=B:104:0x0038, (r42_1 'drmInitData' org.telegram.messenger.exoplayer2.drm.DrmInitData)=B:106:0x0038, (r42_1 'drmInitData' org.telegram.messenger.exoplayer2.drm.DrmInitData)=B:105:0x0038, (r42_1 'drmInitData' org.telegram.messenger.exoplayer2.drm.DrmInitData)=B:107:0x0038, (r42_1 'drmInitData' org.telegram.messenger.exoplayer2.drm.DrmInitData)=B:108:0x0038, (r42_1 'drmInitData' org.telegram.messenger.exoplayer2.drm.DrmInitData)=B:109:0x0038, (r42_1 'drmInitData' org.telegram.messenger.exoplayer2.drm.DrmInitData)=B:110:0x0038, (r42_1 'drmInitData' org.telegram.messenger.exoplayer2.drm.DrmInitData)=B:111:0x0038, (r42_1 'drmInitData' org.telegram.messenger.exoplayer2.drm.DrmInitData)=B:112:0x0038, (r42_2 'drmInitData' org.telegram.messenger.exoplayer2.drm.DrmInitData)=B:118:0x0038, (r42_1 'drmInitData' org.telegram.messenger.exoplayer2.drm.DrmInitData)=B:117:0x0038, (r42_1 'drmInitData' org.telegram.messenger.exoplayer2.drm.DrmInitData)=B:116:0x0038, (r42_1 'drmInitData' org.telegram.messenger.exoplayer2.drm.DrmInitData)=B:115:0x0038, (r42_1 'drmInitData' org.telegram.messenger.exoplayer2.drm.DrmInitData)=B:114:0x0038, (r42_1 'drmInitData' org.telegram.messenger.exoplayer2.drm.DrmInitData)=B:113:0x0038, (r42_1 'drmInitData' org.telegram.messenger.exoplayer2.drm.DrmInitData)=B:120:0x0038, (r42_1 'drmInitData' org.telegram.messenger.exoplayer2.drm.DrmInitData)=B:119:0x0038, (r42_1 'drmInitData' org.telegram.messenger.exoplayer2.drm.DrmInitData)=B:121:0x0038, (r42_1 'drmInitData' org.telegram.messenger.exoplayer2.drm.DrmInitData)=B:122:0x0038, (r42_1 'drmInitData' org.telegram.messenger.exoplayer2.drm.DrmInitData)=B:124:0x0038, (r42_1 'drmInitData' org.telegram.messenger.exoplayer2.drm.DrmInitData)=B:123:0x0038, (r42_1 'drmInitData' org.telegram.messenger.exoplayer2.drm.DrmInitData)=B:125:0x0038, (r42_1 'drmInitData' org.telegram.messenger.exoplayer2.drm.DrmInitData)=B:126:0x0038, (r42_1 'drmInitData' org.telegram.messenger.exoplayer2.drm.DrmInitData)=B:127:0x0038, (r42_1 'drmInitData' org.telegram.messenger.exoplayer2.drm.DrmInitData)=B:129:0x0038, (r42_1 'drmInitData' org.telegram.messenger.exoplayer2.drm.DrmInitData)=B:128:0x0038}
	at jadx.core.dex.instructions.PhiInsn.replaceArg(PhiInsn.java:79)
	at jadx.core.dex.visitors.ModVisitor.processInvoke(ModVisitor.java:222)
	at jadx.core.dex.visitors.ModVisitor.replaceStep(ModVisitor.java:83)
	at jadx.core.dex.visitors.ModVisitor.visit(ModVisitor.java:68)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r25 = 0;
        r28 = -922337203NUM; // 0x800000NUM float:1.4E-45 double:-4.9E-324;
        r34 = 0;
        r36 = 1;
        r37 = -922337203NUM; // 0x800000NUM float:1.4E-45 double:-4.9E-324;
        r39 = 0;
        r40 = 0;
        r2 = 0;
        r43 = new java.util.ArrayList;
        r43.<init>();
        r27 = new java.util.ArrayList;
        r27.<init>();
        r11 = 0;
        r32 = 0;
        r33 = 0;
        r13 = 0;
        r30 = 0;
        r14 = 0;
        r4 = 0;
        r6 = -1;
        r52 = 0;
        r22 = 0;
        r16 = 0;
        r44 = 0;
        r42 = 0;
    L_0x0038:
        r8 = r54.hasNext();
        if (r8 == 0) goto L_0x0297;
    L_0x003e:
        r9 = r54.next();
        r8 = "#EXT";
        r8 = r9.startsWith(r8);
        if (r8 == 0) goto L_0x0050;
    L_0x004b:
        r0 = r27;
        r0.add(r9);
    L_0x0050:
        r8 = "#EXT-X-PLAYLIST-TYPE";
        r8 = r9.startsWith(r8);
        if (r8 == 0) goto L_0x007b;
    L_0x0059:
        r8 = REGEX_PLAYLIST_TYPE;
        r47 = parseStringAttr(r9, r8);
        r8 = "VOD";
        r0 = r47;
        r8 = r8.equals(r0);
        if (r8 == 0) goto L_0x006d;
    L_0x006a:
        r25 = 1;
        goto L_0x0038;
    L_0x006d:
        r8 = "EVENT";
        r0 = r47;
        r8 = r8.equals(r0);
        if (r8 == 0) goto L_0x0038;
    L_0x0078:
        r25 = 2;
        goto L_0x0038;
    L_0x007b:
        r8 = "#EXT-X-START";
        r8 = r9.startsWith(r8);
        if (r8 == 0) goto L_0x0097;
    L_0x0084:
        r8 = REGEX_TIME_OFFSET;
        r18 = parseDoubleAttr(r9, r8);
        r20 = 469683714NUM; // 0x412e84NUM float:0.0 double:1000000.0;
        r18 = r18 * r20;
        r0 = r18;
        r0 = (long) r0;
        r28 = r0;
        goto L_0x0038;
    L_0x0097:
        r8 = "#EXT-X-MAP";
        r8 = r9.startsWith(r8);
        if (r8 == 0) goto L_0x00d6;
    L_0x00a0:
        r8 = REGEX_URI;
        r3 = parseStringAttr(r9, r8);
        r8 = REGEX_ATTR_BYTERANGE;
        r23 = parseOptionalStringAttr(r9, r8);
        if (r23 == 0) goto L_0x00cb;
    L_0x00ae:
        r8 = "@";
        r0 = r23;
        r51 = r0.split(r8);
        r8 = 0;
        r8 = r51[r8];
        r6 = java.lang.Long.parseLong(r8);
        r0 = r51;
        r8 = r0.length;
        r10 = 1;
        if (r8 <= r10) goto L_0x00cb;
    L_0x00c4:
        r8 = 1;
        r8 = r51[r8];
        r4 = java.lang.Long.parseLong(r8);
    L_0x00cb:
        r2 = new org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMediaPlaylist$Segment;
        r2.<init>(r3, r4, r6);
        r4 = 0;
        r6 = -1;
        goto L_0x0038;
    L_0x00d6:
        r8 = "#EXT-X-TARGETDURATION";
        r8 = r9.startsWith(r8);
        if (r8 == 0) goto L_0x00ef;
    L_0x00df:
        r8 = REGEX_TARGET_DURATION;
        r8 = parseIntAttr(r9, r8);
        r0 = (long) r8;
        r18 = r0;
        r20 = 1000000; // 0xf4240 float:1.401298E-39 double:4.940656E-318;
        r37 = r18 * r20;
        goto L_0x0038;
    L_0x00ef:
        r8 = "#EXT-X-MEDIA-SEQUENCE";
        r8 = r9.startsWith(r8);
        if (r8 == 0) goto L_0x0102;
    L_0x00f8:
        r8 = REGEX_MEDIA_SEQUENCE;
        r34 = parseLongAttr(r9, r8);
        r52 = r34;
        goto L_0x0038;
    L_0x0102:
        r8 = "#EXT-X-VERSION";
        r8 = r9.startsWith(r8);
        if (r8 == 0) goto L_0x0113;
    L_0x010b:
        r8 = REGEX_VERSION;
        r36 = parseIntAttr(r9, r8);
        goto L_0x0038;
    L_0x0113:
        r8 = "#EXTINF";
        r8 = r9.startsWith(r8);
        if (r8 == 0) goto L_0x012e;
    L_0x011c:
        r8 = REGEX_MEDIA_DURATION;
        r18 = parseDoubleAttr(r9, r8);
        r20 = 469683714NUM; // 0x412e84NUM float:0.0 double:1000000.0;
        r18 = r18 * r20;
        r0 = r18;
        r11 = (long) r0;
        goto L_0x0038;
    L_0x012e:
        r8 = "#EXT-X-KEY";
        r8 = r9.startsWith(r8);
        if (r8 == 0) goto L_0x01af;
    L_0x0137:
        r8 = REGEX_METHOD;
        r46 = parseOptionalStringAttr(r9, r8);
        r8 = REGEX_KEYFORMAT;
        r45 = parseOptionalStringAttr(r9, r8);
        r16 = 0;
        r44 = 0;
        r8 = "NONE";
        r0 = r46;
        r8 = r8.equals(r0);
        if (r8 != 0) goto L_0x0038;
    L_0x0152:
        r8 = REGEX_IV;
        r44 = parseOptionalStringAttr(r9, r8);
        r8 = "identity";
        r0 = r45;
        r8 = r8.equals(r0);
        if (r8 != 0) goto L_0x0165;
    L_0x0163:
        if (r45 != 0) goto L_0x0178;
    L_0x0165:
        r8 = "AES-128";
        r0 = r46;
        r8 = r8.equals(r0);
        if (r8 == 0) goto L_0x0038;
    L_0x0170:
        r8 = REGEX_URI;
        r16 = parseStringAttr(r9, r8);
        goto L_0x0038;
    L_0x0178:
        if (r46 == 0) goto L_0x0038;
    L_0x017a:
        r0 = r45;
        r50 = parseWidevineSchemeData(r9, r0);
        if (r50 == 0) goto L_0x0038;
    L_0x0182:
        r42 = new org.telegram.messenger.exoplayer2.drm.DrmInitData;
        r8 = "SAMPLE-AES-CENC";
        r0 = r46;
        r8 = r8.equals(r0);
        if (r8 != 0) goto L_0x019a;
    L_0x018f:
        r8 = "SAMPLE-AES-CTR";
        r0 = r46;
        r8 = r8.equals(r0);
        if (r8 == 0) goto L_0x01ab;
    L_0x019a:
        r8 = "cenc";
    L_0x019d:
        r10 = 1;
        r10 = new org.telegram.messenger.exoplayer2.drm.DrmInitData.SchemeData[r10];
        r18 = 0;
        r10[r18] = r50;
        r0 = r42;
        r0.<init>(r8, r10);
        goto L_0x0038;
    L_0x01ab:
        r8 = "cbcs";
        goto L_0x019d;
    L_0x01af:
        r8 = "#EXT-X-BYTERANGE";
        r8 = r9.startsWith(r8);
        if (r8 == 0) goto L_0x01dd;
    L_0x01b8:
        r8 = REGEX_BYTERANGE;
        r23 = parseStringAttr(r9, r8);
        r8 = "@";
        r0 = r23;
        r51 = r0.split(r8);
        r8 = 0;
        r8 = r51[r8];
        r6 = java.lang.Long.parseLong(r8);
        r0 = r51;
        r8 = r0.length;
        r10 = 1;
        if (r8 <= r10) goto L_0x0038;
    L_0x01d4:
        r8 = 1;
        r8 = r51[r8];
        r4 = java.lang.Long.parseLong(r8);
        goto L_0x0038;
    L_0x01dd:
        r8 = "#EXT-X-DISCONTINUITY-SEQUENCE";
        r8 = r9.startsWith(r8);
        if (r8 == 0) goto L_0x01fa;
    L_0x01e6:
        r32 = 1;
        r8 = 58;
        r8 = r9.indexOf(r8);
        r8 = r8 + 1;
        r8 = r9.substring(r8);
        r33 = java.lang.Integer.parseInt(r8);
        goto L_0x0038;
    L_0x01fa:
        r8 = "#EXT-X-DISCONTINUITY";
        r8 = r9.equals(r8);
        if (r8 == 0) goto L_0x0207;
    L_0x0203:
        r13 = r13 + 1;
        goto L_0x0038;
    L_0x0207:
        r8 = "#EXT-X-PROGRAM-DATE-TIME";
        r8 = r9.startsWith(r8);
        if (r8 == 0) goto L_0x022e;
    L_0x0210:
        r18 = 0;
        r8 = (r30 > r18 ? 1 : (r30 == r18 ? 0 : -1));
        if (r8 != 0) goto L_0x0038;
    L_0x0216:
        r8 = 58;
        r8 = r9.indexOf(r8);
        r8 = r8 + 1;
        r8 = r9.substring(r8);
        r18 = org.telegram.messenger.exoplayer2.util.Util.parseXsDateTime(r8);
        r48 = org.telegram.messenger.exoplayer2.C0554C.msToUs(r18);
        r30 = r48 - r14;
        goto L_0x0038;
    L_0x022e:
        r8 = "#EXT-X-GAP";
        r8 = r9.equals(r8);
        if (r8 == 0) goto L_0x023b;
    L_0x0237:
        r22 = 1;
        goto L_0x0038;
    L_0x023b:
        r8 = "#EXT-X-INDEPENDENT-SEGMENTS";
        r8 = r9.equals(r8);
        if (r8 == 0) goto L_0x0248;
    L_0x0244:
        r39 = 1;
        goto L_0x0038;
    L_0x0248:
        r8 = "#EXT-X-ENDLIST";
        r8 = r9.equals(r8);
        if (r8 == 0) goto L_0x0255;
    L_0x0251:
        r40 = 1;
        goto L_0x0038;
    L_0x0255:
        r8 = "#";
        r8 = r9.startsWith(r8);
        if (r8 != 0) goto L_0x0038;
    L_0x025e:
        if (r16 != 0) goto L_0x028d;
    L_0x0260:
        r17 = 0;
    L_0x0262:
        r18 = 1;
        r52 = r52 + r18;
        r18 = -1;
        r8 = (r6 > r18 ? 1 : (r6 == r18 ? 0 : -1));
        if (r8 != 0) goto L_0x026e;
    L_0x026c:
        r4 = 0;
    L_0x026e:
        r8 = new org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMediaPlaylist$Segment;
        r10 = r2;
        r18 = r4;
        r20 = r6;
        r8.<init>(r9, r10, r11, r13, r14, r16, r17, r18, r20, r22);
        r0 = r43;
        r0.add(r8);
        r14 = r14 + r11;
        r11 = 0;
        r18 = -1;
        r8 = (r6 > r18 ? 1 : (r6 == r18 ? 0 : -1));
        if (r8 == 0) goto L_0x0287;
    L_0x0286:
        r4 = r4 + r6;
    L_0x0287:
        r6 = -1;
        r22 = 0;
        goto L_0x0038;
    L_0x028d:
        if (r44 == 0) goto L_0x0292;
    L_0x028f:
        r17 = r44;
        goto L_0x0262;
    L_0x0292:
        r17 = java.lang.Long.toHexString(r52);
        goto L_0x0262;
    L_0x0297:
        r24 = new org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMediaPlaylist;
        r18 = 0;
        r8 = (r30 > r18 ? 1 : (r30 == r18 ? 0 : -1));
        if (r8 == 0) goto L_0x02a7;
    L_0x029f:
        r41 = 1;
    L_0x02a1:
        r26 = r55;
        r24.<init>(r25, r26, r27, r28, r30, r32, r33, r34, r36, r37, r39, r40, r41, r42, r43);
        return r24;
    L_0x02a7:
        r41 = 0;
        goto L_0x02a1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.source.hls.playlist.HlsPlaylistParser.parseMediaPlaylist(org.telegram.messenger.exoplayer2.source.hls.playlist.HlsPlaylistParser$LineIterator, java.lang.String):org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMediaPlaylist");
    }

    private static SchemeData parseWidevineSchemeData(String line, String keyFormat) throws ParserException {
        if (KEYFORMAT_WIDEVINE_PSSH_BINARY.equals(keyFormat)) {
            String uriString = parseStringAttr(line, REGEX_URI);
            return new SchemeData(C0554C.WIDEVINE_UUID, MimeTypes.VIDEO_MP4, Base64.decode(uriString.substring(uriString.indexOf(44)), 0));
        } else if (!KEYFORMAT_WIDEVINE_PSSH_JSON.equals(keyFormat)) {
            return null;
        } else {
            try {
                return new SchemeData(C0554C.WIDEVINE_UUID, "hls", line.getBytes(C0554C.UTF8_NAME));
            } catch (Throwable e) {
                throw new ParserException(e);
            }
        }
    }

    private static int parseIntAttr(String line, Pattern pattern) throws ParserException {
        return Integer.parseInt(parseStringAttr(line, pattern));
    }

    private static long parseLongAttr(String line, Pattern pattern) throws ParserException {
        return Long.parseLong(parseStringAttr(line, pattern));
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
        throw new ParserException("Couldn't match " + pattern.pattern() + " in " + line);
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
